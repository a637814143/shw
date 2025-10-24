<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1 class="dashboard-title">平台管理员中心</h1>
        <p class="dashboard-subtitle">一站式掌握资金流、服务履约与内容运营</p>
      </div>
      <div class="header-actions">
        <span class="welcome">管理员：{{ username }}</span>
        <span class="wallet-balance">钱包余额：¥{{ adminBalanceText }}</span>
        <button type="button" class="logout-button" @click="logout">退出登录</button>
      </div>
    </header>

    <section class="stats-grid" aria-label="平台概览">
      <article class="stat-card accent">
        <p class="stat-label">平台注册用户</p>
        <p class="stat-value">{{ adminStats.totalUsers }}</p>
        <p class="stat-helper">普通用户 + 家政公司 + 管理员</p>
      </article>
      <article class="stat-card primary">
        <p class="stat-label">家政公司</p>
        <p class="stat-value">{{ adminStats.totalCompanies }}</p>
        <p class="stat-helper">优质服务提供方规模</p>
      </article>
      <article class="stat-card glass">
        <p class="stat-label">累计充值</p>
        <p class="stat-value">¥{{ adminStats.totalRecharge.toFixed(2) }}</p>
        <p class="stat-helper">提现 {{ adminStats.totalWithdraw.toFixed(2) }}</p>
      </article>
      <article class="stat-card warning">
        <p class="stat-label">待审退款</p>
        <p class="stat-value">{{ adminStats.pendingRefunds }}</p>
        <p class="stat-helper">需优先关注的用户诉求</p>
      </article>
    </section>

    <div class="dashboard-main">
      <aside class="sidebar">
        <button
          v-for="item in sections"
          :key="item.key"
          type="button"
          class="sidebar-item"
          :class="{ active: activeSection === item.key }"
          @click="switchSection(item.key)"
        >
          <span class="sidebar-icon">{{ item.icon }}</span>
          {{ item.label }}
        </button>
      </aside>

      <main class="content">
        <section v-if="activeSection === 'overview'" class="panel immersive-panel">
          <header class="panel-header">
            <div>
              <h2>数据总览</h2>
              <p>洞察充值趋势、服务履约与账户构成，打造 Apple 式流畅体验。</p>
            </div>
            <button type="button" class="ghost-button" @click="loadOverview" :disabled="overviewLoading">
              {{ overviewLoading ? '刷新中…' : '刷新数据' }}
            </button>
          </header>
          <div v-if="overviewLoading" class="loading-state">正在同步最新统计…</div>
          <div v-else class="overview-grid">
            <article class="insight-card">
              <header class="insight-header">
                <h3>近 7 日充值趋势</h3>
                <span class="insight-helper">金额单位：元</span>
              </header>
              <div class="sparkline" role="img" aria-label="七日充值趋势柱状图">
                <div
                  v-for="point in weeklySeries"
                  :key="point.label"
                  class="spark-bar"
                  :style="sparkStyle(point.amount)"
                >
                  <span class="spark-amount">¥{{ point.amount.toFixed(2) }}</span>
                  <span class="spark-label">{{ shortLabel(point.label) }}</span>
                </div>
              </div>
            </article>
            <article class="insight-card">
              <header class="insight-header">
                <h3>预约履约对比</h3>
                <span class="insight-helper">反映服务调度效率</span>
              </header>
              <ul class="metric-list">
                <li v-for="metric in appointmentMetrics" :key="metric.status">
                  <span>{{ metric.status }}</span>
                  <span class="metric-bar">
                    <span class="metric-fill" :style="metricStyle(metric.count)"></span>
                    <strong>{{ metric.count }}</strong>
                  </span>
                </li>
              </ul>
            </article>
            <article class="insight-card highlight">
              <header class="insight-header">
                <h3>账户构成与风控</h3>
                <span class="insight-helper">确保平台运维有序</span>
              </header>
              <ul class="stat-lines">
                <li><span>管理员数量</span><strong>{{ adminStats.totalAdmins }}</strong></li>
                <li><span>普通用户</span><strong>{{ normalUserCount }}</strong></li>
                <li><span>累计提现</span><strong>¥{{ adminStats.totalWithdraw.toFixed(2) }}</strong></li>
              </ul>
            </article>
          </div>
        </section>

        <section v-else-if="activeSection === 'users'" class="panel">
          <header class="panel-header">
            <div>
              <h2>用户资产与密码管理</h2>
              <p>精细化调节用户余额、积分与密码，保障账户安全。</p>
            </div>
            <div class="user-actions">
              <label class="visually-hidden" for="user-search">搜索账号</label>
              <input
                id="user-search"
                v-model="userSearch"
                class="search-input"
                type="search"
                placeholder="搜索账号、角色、联系方式"
                :disabled="usersLoading"
              />
              <button
                type="button"
                class="secondary-button danger"
                :disabled="!hasUserSelection || usersLoading"
                @click="handleBulkDeleteUsers"
              >
                删除选中<span v-if="selectedUserCount">（{{ selectedUserCount }}）</span>
              </button>
              <button
                v-if="hasUserFilter"
                type="button"
                class="ghost-button"
                :disabled="usersLoading"
                @click="clearUserFilter"
              >
                清除筛选
              </button>
              <button type="button" class="ghost-button" @click="loadUsers" :disabled="usersLoading">
                {{ usersLoading ? '刷新中…' : '刷新列表' }}
              </button>
            </div>
          </header>
          <div v-if="usersLoading" class="loading-state">正在获取用户信息…</div>
          <div v-else class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="allUsersSelected"
                      :disabled="usersLoading || !users.length"
                      @change="toggleSelectAllUsers(($event.target as HTMLInputElement).checked)"
                      aria-label="全选账号"
                    />
                  </th>
                  <th>账号</th>
                  <th>角色</th>
                  <th>余额（¥）</th>
                  <th>积分</th>
                  <th>设置新余额</th>
                  <th>调整积分</th>
                  <th>重置密码</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in users" :key="user.id">
                  <td class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="selectedUserIds.has(user.id)"
                      :disabled="usersLoading || user.username === username"
                      @change="toggleUserSelection(user.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择账号 ${user.username}`"
                    />
                  </td>
                  <td>{{ user.username }}</td>
                  <td>{{ roleText(user.role) }}</td>
                  <td>{{ user.balance.toFixed(2) }}</td>
                  <td>{{ user.loyaltyPoints }}</td>
                  <td>
                    <div class="inline-form">
                      <input v-model.number="walletEdits[user.id]" type="number" min="0" step="0.01" />
                      <button type="button" class="link-button" @click="saveWallet(user.id)">保存</button>
                    </div>
                  </td>
                  <td>
                    <div class="inline-form">
                      <input v-model.number="loyaltyEdits[user.id]" type="number" min="0" step="1" />
                      <button type="button" class="link-button" @click="saveLoyalty(user.id)">更新</button>
                    </div>
                  </td>
                  <td>
                    <div class="inline-form">
                      <input v-model="passwordEdits[user.id]" type="text" placeholder="请输入新密码" />
                      <button type="button" class="link-button" @click="savePassword(user.id)">重置</button>
                    </div>
                  </td>
                  <td class="table-actions">
                    <button
                      type="button"
                      class="link-button danger"
                      :disabled="usersLoading || user.username === username"
                      @click="handleDeleteUser(user)"
                    >
                      删除
                    </button>
                  </td>
                </tr>
                <tr v-if="!users.length">
                  <td colspan="9" class="empty-row">
                    <span v-if="hasUserFilter">未找到匹配的账号。</span>
                    <span v-else>暂无用户数据。</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="activeSection === 'ledger'" class="panel">
          <header class="panel-header">
            <div>
              <h2>订单管理</h2>
              <p>查看预约履约与结算状态，集中处理平台资金。</p>
            </div>
            <div class="user-actions">
              <button
                type="button"
                class="secondary-button danger"
                :disabled="!hasLedgerSelection || orderLedgerLoading"
                @click="handleBulkDeleteLedgerOrders"
              >
                删除选中<span v-if="selectedLedgerCount">（{{ selectedLedgerCount }}）</span>
              </button>
              <span class="muted helper-note">仅支持删除已结算订单</span>
              <label class="visually-hidden" for="order-ledger-search">搜索订单</label>
              <input
                id="order-ledger-search"
                v-model="orderSearch"
                class="search-input"
                type="search"
                placeholder="搜索服务、用户或家政公司"
                :disabled="orderLedgerLoading"
              />
              <button type="button" class="ghost-button" @click="loadOrderLedger" :disabled="orderLedgerLoading">
                {{ orderLedgerLoading ? '刷新中…' : '刷新列表' }}
              </button>
            </div>
          </header>
          <div v-if="orderLedgerLoading" class="loading-state">正在查询订单记录…</div>
          <div v-else class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="allLedgerSelected"
                      :disabled="orderLedgerLoading || !deletableLedgerOrders.length"
                      @change="toggleSelectAllLedger(($event.target as HTMLInputElement).checked)"
                      aria-label="全选可删除订单"
                    />
                  </th>
                  <th>服务</th>
                  <th>用户 / 公司</th>
                  <th>预约时间</th>
                  <th>状态</th>
                  <th>金额</th>
                  <th class="table-actions">结算</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in orderLedger" :key="order.id">
                  <td class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="selectedLedgerIds.has(order.id)"
                      :disabled="orderLedgerLoading || !canDeleteLedgerOrder(order)"
                      @change="toggleLedgerSelection(order.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择订单 ${order.serviceName}`"
                    />
                  </td>
                  <td>
                    <strong>{{ order.serviceName }}</strong>
                    <div class="order-subtext">家政公司：{{ order.companyName }}</div>
                    <div class="order-subtext muted">指派人员：{{ order.assignedWorker || '—' }}</div>
                  </td>
                  <td>
                    <div>{{ order.username }}</div>
                    <div class="order-subtext muted">{{ order.customerContactPhone || '—' }}</div>
                  </td>
                  <td>{{ formatDateTime(order.scheduledAt) }}</td>
                  <td>
                    <span class="status-badge" :class="`status-${order.status.toLowerCase()}`">
                      {{ statusText(order.status) }}
                    </span>
                    <div class="order-subtext">{{ order.progressNote || '待更新' }}</div>
                  </td>
                  <td>¥{{ order.price.toFixed(2) }}</td>
                  <td class="table-actions actions-inline">
                    <div class="settlement-cell">
                      <span class="settlement-status" :class="{ completed: order.settlementReleased }">
                        {{ order.settlementReleased ? settlementSummary(order) : '待结算' }}
                      </span>
                      <button
                        v-if="!order.settlementReleased"
                        type="button"
                        class="primary-button"
                        :disabled="order.status !== 'COMPLETED' || settlementSaving[order.id]"
                        @click="completeSettlement(order)"
                      >
                        {{ settlementSaving[order.id] ? '结算中…' : '完成交易' }}
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="!orderLedger.length">
                  <td colspan="7" class="empty-row">
                    <span v-if="hasOrderFilter">未找到匹配的订单，请调整搜索条件。</span>
                    <span v-else>暂无订单记录。</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="activeSection === 'transactions'" class="panel">
          <header class="panel-header">
            <div>
              <h2>充值与调整流水</h2>
              <p>记录用户自助充值、积分兑换及管理员调整。</p>
            </div>
            <div class="user-actions">
              <button
                type="button"
                class="secondary-button danger"
                :disabled="!hasTransactionSelection || transactionsLoading"
                @click="handleBulkDeleteTransactions"
              >
                删除选中<span v-if="selectedTransactionCount">（{{ selectedTransactionCount }}）</span>
              </button>
              <label class="visually-hidden" for="transaction-search">搜索流水</label>
              <input
                id="transaction-search"
                v-model="transactionSearch"
                class="search-input"
                type="search"
                placeholder="搜索账号、类型或备注"
                :disabled="transactionsLoading"
              />
              <button type="button" class="ghost-button" @click="loadTransactions" :disabled="transactionsLoading">
                {{ transactionsLoading ? '刷新中…' : '刷新流水' }}
              </button>
            </div>
          </header>
          <div v-if="transactionsLoading" class="loading-state">正在同步流水记录…</div>
          <div v-else class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="allTransactionsSelected"
                      :disabled="transactionsLoading || !transactions.length"
                      @change="toggleSelectAllTransactions(($event.target as HTMLInputElement).checked)"
                      aria-label="全选流水记录"
                    />
                  </th>
                  <th>时间</th>
                  <th>账号</th>
                  <th>类型</th>
                  <th>金额</th>
                  <th>备注</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in transactions" :key="item.id">
                  <td class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="selectedTransactionIds.has(item.id)"
                      :disabled="transactionsLoading"
                      @change="toggleTransactionSelection(item.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择流水 ${item.username}`"
                    />
                  </td>
                  <td>{{ formatDateTime(item.createdAt) }}</td>
                  <td>{{ item.username }}</td>
                  <td>{{ transactionText(item.type) }}</td>
                  <td :class="{ positive: item.amount >= 0, negative: item.amount < 0 }">¥{{ item.amount.toFixed(2) }}</td>
                  <td>{{ item.note || '—' }}</td>
                </tr>
                <tr v-if="!transactions.length">
                  <td colspan="6" class="empty-row">
                    <span v-if="hasTransactionFilter">未找到匹配的流水记录。</span>
                    <span v-else>暂无充值或调整记录。</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="activeSection === 'favorites'" class="panel">
          <header class="panel-header">
            <div>
              <h2>收藏洞察</h2>
              <p>了解用户偏好，为内容与服务运营提供数据依据。</p>
            </div>
            <div class="user-actions">
              <button
                type="button"
                class="secondary-button danger"
                :disabled="!hasFavoriteSelection || favoritesLoading"
                @click="handleBulkDeleteFavorites"
              >
                删除选中<span v-if="selectedFavoriteCount">（{{ selectedFavoriteCount }}）</span>
              </button>
              <label class="visually-hidden" for="favorite-search">搜索收藏</label>
              <input
                id="favorite-search"
                v-model="favoriteSearch"
                class="search-input"
                type="search"
                placeholder="搜索用户、服务或公司"
                :disabled="favoritesLoading"
              />
              <button type="button" class="ghost-button" @click="loadFavorites" :disabled="favoritesLoading">
                {{ favoritesLoading ? '刷新中…' : '刷新数据' }}
              </button>
            </div>
          </header>
          <div v-if="favoritesLoading" class="loading-state">正在加载收藏列表…</div>
          <div v-else class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="allFavoritesSelected"
                      :disabled="favoritesLoading || !favorites.length"
                      @change="toggleSelectAllFavorites(($event.target as HTMLInputElement).checked)"
                      aria-label="全选收藏记录"
                    />
                  </th>
                  <th>用户</th>
                  <th>收藏服务</th>
                  <th>所属公司</th>
                  <th>时间</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in favorites" :key="item.id">
                  <td class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="selectedFavoriteIds.has(item.id)"
                      :disabled="favoritesLoading"
                      @change="toggleFavoriteSelection(item.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择收藏 ${item.serviceName}`"
                    />
                  </td>
                  <td>{{ item.username }}</td>
                  <td>{{ item.serviceName }}</td>
                  <td>{{ item.companyName }}</td>
                  <td>{{ formatDateTime(item.createdAt) }}</td>
                </tr>
                <tr v-if="!favorites.length">
                  <td colspan="5" class="empty-row">
                    <span v-if="hasFavoriteFilter">未找到符合条件的收藏记录。</span>
                    <span v-else>暂未产生收藏数据。</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="activeSection === 'content'" class="panel immersive-panel">
          <header class="panel-header">
            <div>
              <h2>内容运营中心</h2>
              <p>以苹果式高级质感管理轮播、贴士与公告。</p>
            </div>
            <button type="button" class="ghost-button" @click="loadContent" :disabled="contentLoading">
              {{ contentLoading ? '同步中…' : '全部刷新' }}
            </button>
          </header>
          <div v-if="contentLoading" class="loading-state">正在同步内容配置…</div>
          <div v-else class="content-grid">
            <section class="content-card">
              <header class="content-card-header">
                <div>
                  <h3>轮播图</h3>
                  <p>推荐焦点服务，图片支持外链。</p>
                </div>
                <div class="content-toolbar">
                  <label class="visually-hidden" for="carousel-search">搜索轮播图</label>
                  <input
                    id="carousel-search"
                    v-model="carouselSearch"
                    class="search-input"
                    type="search"
                    placeholder="按标题或链接搜索"
                    :disabled="contentLoading"
                  />
                  <button
                    type="button"
                    class="secondary-button danger"
                    :disabled="!hasCarouselSelection || contentLoading"
                    @click="handleBulkDeleteCarousels"
                  >
                    删除选中<span v-if="selectedCarouselCount">（{{ selectedCarouselCount }}）</span>
                  </button>
                  <button
                    v-if="hasCarouselFilter"
                    type="button"
                    class="ghost-button"
                    :disabled="contentLoading"
                    @click="clearCarouselFilter"
                  >
                    清除筛选
                  </button>
                </div>
              </header>
              <form class="content-form" @submit.prevent="submitCarousel">
                <input v-model="carouselForm.title" type="text" placeholder="标题" required />
                <input v-model="carouselForm.imageUrl" type="url" placeholder="图片地址" required />
                <input v-model="carouselForm.serviceLink" type="text" placeholder="关联服务或跳转链接（可选）" />
                <div class="form-actions">
                  <button type="submit" class="primary-button" :disabled="carouselSaving">
                    {{ carouselSaving ? '保存中…' : carouselEditing ? '更新轮播' : '新增轮播' }}
                  </button>
                  <button type="button" class="secondary-button" @click="resetCarousel" :disabled="carouselSaving">
                    重置
                  </button>
                </div>
              </form>
              <ul class="content-list">
                <li v-for="item in carousels" :key="item.id">
                  <div class="content-item-details">
                    <input
                      class="content-checkbox"
                      type="checkbox"
                      :checked="selectedCarouselIds.has(item.id)"
                      :disabled="contentLoading"
                      @change="toggleCarouselSelection(item.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择轮播 ${item.title}`"
                    />
                    <div>
                      <strong>{{ item.title }}</strong>
                      <p class="muted">{{ item.imageUrl }}</p>
                      <p class="muted">{{ item.serviceLink || '无跳转' }}</p>
                    </div>
                  </div>
                  <div class="list-actions">
                    <button type="button" class="link-button" @click="editCarousel(item)">编辑</button>
                    <button type="button" class="link-button danger" @click="removeCarousel(item.id)">删除</button>
                  </div>
                </li>
                <li v-if="!carousels.length" class="empty-row">
                  <span v-if="hasCarouselFilter">未找到符合条件的轮播图。</span>
                  <span v-else>暂无轮播图，立即创建一个吧。</span>
                </li>
              </ul>
            </section>

            <section class="content-card">
              <header class="content-card-header">
                <div>
                  <h3>居家贴士</h3>
                  <p>精选生活指南，提升平台温度。</p>
                </div>
                <div class="content-toolbar">
                  <label class="visually-hidden" for="tip-search">搜索贴士</label>
                  <input
                    id="tip-search"
                    v-model="tipSearch"
                    class="search-input"
                    type="search"
                    placeholder="按标题或内容搜索"
                    :disabled="contentLoading"
                  />
                  <button
                    type="button"
                    class="secondary-button danger"
                    :disabled="!hasTipSelection || contentLoading"
                    @click="handleBulkDeleteTips"
                  >
                    删除选中<span v-if="selectedTipCount">（{{ selectedTipCount }}）</span>
                  </button>
                  <button
                    v-if="hasTipFilter"
                    type="button"
                    class="ghost-button"
                    :disabled="contentLoading"
                    @click="clearTipFilter"
                  >
                    清除筛选
                  </button>
                </div>
              </header>
              <form class="content-form" @submit.prevent="submitTip">
                <input v-model="tipForm.title" type="text" placeholder="贴士标题" required />
                <textarea v-model="tipForm.content" rows="3" placeholder="贴士内容" required></textarea>
                <div class="form-actions">
                  <button type="submit" class="primary-button" :disabled="tipSaving">
                    {{ tipSaving ? '保存中…' : tipEditing ? '更新贴士' : '新增贴士' }}
                  </button>
                  <button type="button" class="secondary-button" @click="resetTip" :disabled="tipSaving">
                    重置
                  </button>
                </div>
              </form>
              <ul class="content-list">
                <li v-for="item in tips" :key="item.id">
                  <div class="content-item-details">
                    <input
                      class="content-checkbox"
                      type="checkbox"
                      :checked="selectedTipIds.has(item.id)"
                      :disabled="contentLoading"
                      @change="toggleTipSelection(item.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择贴士 ${item.title}`"
                    />
                    <div>
                      <strong>{{ item.title }}</strong>
                      <p class="muted">{{ item.content }}</p>
                    </div>
                  </div>
                  <div class="list-actions">
                    <button type="button" class="link-button" @click="editTip(item)">编辑</button>
                    <button type="button" class="link-button danger" @click="removeTip(item.id)">删除</button>
                  </div>
                </li>
                <li v-if="!tips.length" class="empty-row">
                  <span v-if="hasTipFilter">未找到符合条件的贴士。</span>
                  <span v-else>还没有贴士内容。</span>
                </li>
              </ul>
            </section>

            <section class="content-card">
              <header class="content-card-header">
                <div>
                  <h3>系统公告</h3>
                  <p>向所有用户广播平台政策与活动。</p>
                </div>
                <div class="content-toolbar">
                  <label class="visually-hidden" for="announcement-search">搜索公告</label>
                  <input
                    id="announcement-search"
                    v-model="announcementSearch"
                    class="search-input"
                    type="search"
                    placeholder="按标题或内容搜索"
                    :disabled="contentLoading"
                  />
                  <button
                    type="button"
                    class="secondary-button danger"
                    :disabled="!hasAnnouncementSelection || contentLoading"
                    @click="handleBulkDeleteAnnouncements"
                  >
                    删除选中<span v-if="selectedAnnouncementCount">（{{ selectedAnnouncementCount }}）</span>
                  </button>
                  <button
                    v-if="hasAnnouncementFilter"
                    type="button"
                    class="ghost-button"
                    :disabled="contentLoading"
                    @click="clearAnnouncementFilter"
                  >
                    清除筛选
                  </button>
                </div>
              </header>
              <form class="content-form" @submit.prevent="submitAnnouncement">
                <input v-model="announcementForm.title" type="text" placeholder="公告标题" required />
                <textarea v-model="announcementForm.content" rows="3" placeholder="公告内容" required></textarea>
                <div class="form-actions">
                  <button type="submit" class="primary-button" :disabled="announcementSaving">
                    {{ announcementSaving ? '保存中…' : announcementEditing ? '更新公告' : '新增公告' }}
                  </button>
                  <button type="button" class="secondary-button" @click="resetAnnouncement" :disabled="announcementSaving">
                    重置
                  </button>
                </div>
              </form>
              <ul class="content-list">
                <li v-for="item in announcements" :key="item.id">
                  <div class="content-item-details">
                    <input
                      class="content-checkbox"
                      type="checkbox"
                      :checked="selectedAnnouncementIds.has(item.id)"
                      :disabled="contentLoading"
                      @change="toggleAnnouncementSelection(item.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择公告 ${item.title}`"
                    />
                    <div>
                      <strong>{{ item.title }}</strong>
                      <p class="muted">{{ item.content }}</p>
                    </div>
                  </div>
                  <div class="list-actions">
                    <button type="button" class="link-button" @click="editAnnouncement(item)">编辑</button>
                    <button type="button" class="link-button danger" @click="removeAnnouncement(item.id)">删除</button>
                  </div>
                </li>
                <li v-if="!announcements.length" class="empty-row">
                  <span v-if="hasAnnouncementFilter">未找到符合条件的公告。</span>
                  <span v-else>暂无系统公告。</span>
                </li>
              </ul>
            </section>
          </div>
        </section>

        <section v-else class="panel">
          <header class="panel-header">
            <div>
              <h2>全站退款处理</h2>
              <p>当公司未及时处理时，由管理员终审保障体验。</p>
            </div>
            <div class="refund-actions">
              <div class="stage-switch">
                <button
                  type="button"
                  class="chip-button"
                  :class="{ active: refundStage === 'pending' }"
                  @click="changeRefundStage('pending')"
                  :disabled="refundsLoading"
                >
                  待处理
                </button>
                <button
                  type="button"
                  class="chip-button"
                  :class="{ active: refundStage === 'processed' }"
                  @click="changeRefundStage('processed')"
                  :disabled="refundsLoading"
                >
                  已处理
                </button>
                <button
                  type="button"
                  class="chip-button"
                  :class="{ active: refundStage === 'all' }"
                  @click="changeRefundStage('all')"
                  :disabled="refundsLoading"
                >
                  全部
                </button>
              </div>
              <label class="visually-hidden" for="refund-search">搜索退款记录</label>
              <input
                id="refund-search"
                v-model="refundSearch"
                class="search-input"
                type="search"
                placeholder="搜索服务/用户/公司"
                :disabled="refundsLoading"
              />
              <button
                type="button"
                class="secondary-button danger"
                :disabled="refundStage !== 'processed' || !hasRefundSelection || refundsLoading"
                @click="handleBulkDeleteRefunds"
              >
                删除选中<span v-if="selectedRefundIds.size">（{{ selectedRefundIds.size }}）</span>
              </button>
              <button type="button" class="ghost-button" @click="loadRefunds" :disabled="refundsLoading">
                {{ refundsLoading ? '刷新中…' : '刷新列表' }}
              </button>
            </div>
          </header>
          <div v-if="refundsLoading" class="loading-state">正在同步退款请求…</div>
          <div v-else class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="allRefundsSelected"
                      :disabled="refundsLoading || !selectableRefunds.length"
                      @change="toggleSelectAllRefunds(($event.target as HTMLInputElement).checked)"
                      aria-label="全选可删除退款记录"
                    />
                  </th>
                  <th>服务</th>
                  <th>申请人</th>
                  <th>家政公司</th>
                  <th>退款原因</th>
                  <th>申请时间</th>
                  <th>状态</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in refundOrders" :key="order.id">
                  <td class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="selectedRefundIds.has(order.id)"
                      :disabled="refundsLoading || !canSelectRefund(order)"
                      @change="toggleRefundSelection(order.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择退款 ${order.serviceName}`"
                    />
                  </td>
                  <td>
                    <strong>{{ order.serviceName }}</strong>
                    <div class="order-subtext">¥{{ order.price.toFixed(2) }} / {{ order.unit }}</div>
                  </td>
                  <td>{{ order.username }}</td>
                  <td>{{ order.companyName }}</td>
                  <td>{{ order.refundReason }}</td>
                  <td>{{ formatDate(order.updatedAt) }}</td>
                  <td>
                    <span class="status-badge" :class="`status-${order.status.toLowerCase()}`">
                      {{ statusText(order.status) }}
                    </span>
                  </td>
                  <td class="table-actions actions-inline">
                    <template v-if="order.status === 'REFUND_REQUESTED'">
                      <button type="button" class="link-button" @click="handleRefund(order, true)">同意</button>
                      <button type="button" class="link-button danger" @click="handleRefund(order, false)">拒绝</button>
                    </template>
                    <template v-else>
                      <button type="button" class="link-button danger" @click="handleDeleteSingleRefund(order)">删除</button>
                    </template>
                  </td>
                </tr>
                <tr v-if="!refundOrders.length">
                  <td colspan="8" class="empty-row">
                    <span v-if="refundStage === 'pending'">暂无待处理的退款申请。</span>
                    <span v-else-if="refundStage === 'processed'">暂无已处理的退款记录。</span>
                    <span v-else>暂无相关的退款记录。</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

import { AUTH_ACCOUNT_KEY, AUTH_ROLE_KEY, AUTH_TOKEN_KEY, ROLE_LABELS } from '../constants/auth'
import {
  settleAdminOrder,
  createDashboardAnnouncement,
  createDashboardCarousel,
  createDashboardTip,
  deleteDashboardAnnouncement,
  deleteDashboardAnnouncements,
  deleteDashboardCarousel,
  deleteDashboardCarousels,
  deleteDashboardTip,
  deleteDashboardTips,
  deleteAdminFavorites,
  deleteAdminOrders,
  deleteAdminTransactions,
  fetchAdminFavorites,
  fetchAdminOrders,
  fetchAdminOverview,
  fetchAdminRefunds,
  deleteAdminRefunds,
  fetchAdminTransactions,
  fetchCurrentAccount,
  fetchAdminUsers,
  fetchDashboardAnnouncements,
  fetchDashboardCarousels,
  fetchDashboardTips,
  deleteAdminUser,
  deleteAdminUsers,
  handleAdminRefund,
  updateAdminLoyalty,
  updateAdminPassword,
  updateAdminWallet,
  updateDashboardAnnouncement,
  updateDashboardCarousel,
  updateDashboardTip,
  type AccountTransactionItem,
  type AdminOverviewItem,
  type AccountProfileItem,
  type DashboardAnnouncementItem,
  type DashboardCarouselItem,
  type DashboardTipItem,
  type ServiceFavoriteItem,
  type ServiceOrderItem,
  type UpdateLoyaltyPayload,
  type UpdatePasswordPayload,
  type UpdateWalletPayload,
  type UserAccountItem,
} from '../services/dashboard'


type SectionKey = 'overview' | 'users' | 'ledger' | 'transactions' | 'favorites' | 'content' | 'refunds'

interface SectionMeta {
  key: SectionKey
  icon: string
  label: string
}

const router = useRouter()
const username = computed(() => sessionStorage.getItem(AUTH_ACCOUNT_KEY) ?? 'admin')

const sections: SectionMeta[] = [
  { key: 'overview', icon: '✨', label: '数据总览' },
  { key: 'users', icon: '🧾', label: '用户管理' },
  { key: 'ledger', icon: '💼', label: '订单管理' },
  { key: 'transactions', icon: '💳', label: '充值流水' },
  { key: 'favorites', icon: '❤️', label: '收藏洞察' },
  { key: 'content', icon: '🖼️', label: '内容运营' },
  { key: 'refunds', icon: '💰', label: '退款审批' },
]

const activeSection = ref<SectionKey>('overview')

const overview = ref<AdminOverviewItem | null>(null)
const overviewLoading = ref(false)

const adminAccount = ref<AccountProfileItem | null>(null)
const adminBalanceText = computed(() => (adminAccount.value ? adminAccount.value.balance.toFixed(2) : '0.00'))

const users = ref<UserAccountItem[]>([])
const usersLoading = ref(false)
const userSearch = ref('')
const selectedUserIds = ref<Set<number>>(new Set())
let userSearchTimer: ReturnType<typeof setTimeout> | null = null

const orderLedger = ref<ServiceOrderItem[]>([])
const orderLedgerLoading = ref(false)
const orderSearch = ref('')
let orderSearchTimer: ReturnType<typeof setTimeout> | null = null
const selectedLedgerIds = ref<Set<number>>(new Set())
const settlementSaving = reactive<Record<number, boolean>>({})

const transactions = ref<AccountTransactionItem[]>([])
const transactionsLoading = ref(false)
const transactionSearch = ref('')
const selectedTransactionIds = ref<Set<number>>(new Set())
let transactionSearchTimer: ReturnType<typeof setTimeout> | null = null

const favorites = ref<ServiceFavoriteItem[]>([])
const favoritesLoading = ref(false)
const favoriteSearch = ref('')
const selectedFavoriteIds = ref<Set<number>>(new Set())
let favoriteSearchTimer: ReturnType<typeof setTimeout> | null = null

const refundOrders = ref<ServiceOrderItem[]>([])
const refundsLoading = ref(false)
const pendingRefundCount = ref(0)
const refundStage = ref<'pending' | 'processed' | 'all'>('pending')
const refundSearch = ref('')
const selectedRefundIds = ref<Set<number>>(new Set())
let refundSearchTimer: ReturnType<typeof setTimeout> | null = null

const selectedUserCount = computed(() => selectedUserIds.value.size)
const hasUserSelection = computed(() => selectedUserIds.value.size > 0)
const allUsersSelected = computed(
  () => users.value.length > 0 && users.value.every((user) => selectedUserIds.value.has(user.id)),
)
const hasUserFilter = computed(() => userSearch.value.trim().length > 0)
const hasOrderFilter = computed(() => orderSearch.value.trim().length > 0)
const deletableLedgerOrders = computed(() => orderLedger.value.filter((item) => item.settlementReleased))
const allLedgerSelected = computed(
  () =>
    deletableLedgerOrders.value.length > 0 &&
    deletableLedgerOrders.value.every((item) => selectedLedgerIds.value.has(item.id)),
)
const selectedLedgerCount = computed(() => selectedLedgerIds.value.size)
const hasLedgerSelection = computed(() => selectedLedgerIds.value.size > 0)
const hasTransactionFilter = computed(() => transactionSearch.value.trim().length > 0)
const allTransactionsSelected = computed(
  () => transactions.value.length > 0 && transactions.value.every((item) => selectedTransactionIds.value.has(item.id)),
)
const selectedTransactionCount = computed(() => selectedTransactionIds.value.size)
const hasTransactionSelection = computed(() => selectedTransactionIds.value.size > 0)
const hasFavoriteFilter = computed(() => favoriteSearch.value.trim().length > 0)
const allFavoritesSelected = computed(
  () => favorites.value.length > 0 && favorites.value.every((item) => selectedFavoriteIds.value.has(item.id)),
)
const selectedFavoriteCount = computed(() => selectedFavoriteIds.value.size)
const hasFavoriteSelection = computed(() => selectedFavoriteIds.value.size > 0)

const carousels = ref<DashboardCarouselItem[]>([])
const tips = ref<DashboardTipItem[]>([])
const announcements = ref<DashboardAnnouncementItem[]>([])
const contentLoading = ref(false)
const carouselSearch = ref('')
const tipSearch = ref('')
const announcementSearch = ref('')
const selectedCarouselIds = ref<Set<number>>(new Set())
const selectedTipIds = ref<Set<number>>(new Set())
const selectedAnnouncementIds = ref<Set<number>>(new Set())
let carouselSearchTimer: ReturnType<typeof setTimeout> | null = null
let tipSearchTimer: ReturnType<typeof setTimeout> | null = null
let announcementSearchTimer: ReturnType<typeof setTimeout> | null = null

const walletEdits = reactive<Record<number, number>>({})
const loyaltyEdits = reactive<Record<number, number>>({})
const passwordEdits = reactive<Record<number, string>>({})

const canDeleteLedgerOrder = (order: ServiceOrderItem) => order.settlementReleased

const pruneLedgerSelection = () => {
  if (!selectedLedgerIds.value.size) {
    return
  }
  const allowed = new Set(deletableLedgerOrders.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedLedgerIds.value.forEach((id) => {
    if (allowed.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedLedgerIds.value = next
  }
}

const toggleLedgerSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedLedgerIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedLedgerIds.value = next
}

const toggleSelectAllLedger = (checked: boolean) => {
  if (!checked) {
    selectedLedgerIds.value = new Set()
    return
  }
  const next = new Set(selectedLedgerIds.value)
  deletableLedgerOrders.value.forEach((item) => next.add(item.id))
  selectedLedgerIds.value = next
}

const clearLedgerSelection = () => {
  selectedLedgerIds.value = new Set()
}

const pruneTransactionSelection = () => {
  if (!selectedTransactionIds.value.size) {
    return
  }
  const visibleIds = new Set(transactions.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedTransactionIds.value.forEach((id) => {
    if (visibleIds.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedTransactionIds.value = next
  }
}

const toggleTransactionSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedTransactionIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedTransactionIds.value = next
}

const toggleSelectAllTransactions = (checked: boolean) => {
  if (!checked) {
    selectedTransactionIds.value = new Set()
    return
  }
  const next = new Set(selectedTransactionIds.value)
  transactions.value.forEach((item) => next.add(item.id))
  selectedTransactionIds.value = next
}

const clearTransactionSelection = () => {
  selectedTransactionIds.value = new Set()
}

const pruneFavoriteSelection = () => {
  if (!selectedFavoriteIds.value.size) {
    return
  }
  const visibleIds = new Set(favorites.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedFavoriteIds.value.forEach((id) => {
    if (visibleIds.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedFavoriteIds.value = next
  }
}

const toggleFavoriteSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedFavoriteIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedFavoriteIds.value = next
}

const toggleSelectAllFavorites = (checked: boolean) => {
  if (!checked) {
    selectedFavoriteIds.value = new Set()
    return
  }
  const next = new Set(selectedFavoriteIds.value)
  favorites.value.forEach((item) => next.add(item.id))
  selectedFavoriteIds.value = next
}

const clearFavoriteSelection = () => {
  selectedFavoriteIds.value = new Set()
}

const applyOrderUpdate = (updated: ServiceOrderItem) => {
  const ledgerIndex = orderLedger.value.findIndex((item) => item.id === updated.id)
  if (ledgerIndex >= 0) {
    orderLedger.value.splice(ledgerIndex, 1, updated)
    pruneLedgerSelection()
  }
}

const carouselForm = reactive<{ title: string; imageUrl: string; serviceLink: string }>({
  title: '',
  imageUrl: '',
  serviceLink: '',
})
const tipForm = reactive<{ title: string; content: string }>({ title: '', content: '' })
const announcementForm = reactive<{ title: string; content: string }>({ title: '', content: '' })

const carouselEditing = ref<number | null>(null)
const tipEditing = ref<number | null>(null)
const announcementEditing = ref<number | null>(null)
const carouselSaving = ref(false)
const tipSaving = ref(false)
const announcementSaving = ref(false)

const selectedCarouselCount = computed(() => selectedCarouselIds.value.size)
const hasCarouselSelection = computed(() => selectedCarouselIds.value.size > 0)
const allCarouselsSelected = computed(
  () => carousels.value.length > 0 && carousels.value.every((item) => selectedCarouselIds.value.has(item.id)),
)
const hasCarouselFilter = computed(() => carouselSearch.value.trim().length > 0)

const selectedTipCount = computed(() => selectedTipIds.value.size)
const hasTipSelection = computed(() => selectedTipIds.value.size > 0)
const allTipsSelected = computed(
  () => tips.value.length > 0 && tips.value.every((item) => selectedTipIds.value.has(item.id)),
)
const hasTipFilter = computed(() => tipSearch.value.trim().length > 0)

const selectedAnnouncementCount = computed(() => selectedAnnouncementIds.value.size)
const hasAnnouncementSelection = computed(() => selectedAnnouncementIds.value.size > 0)
const allAnnouncementsSelected = computed(
  () =>
    announcements.value.length > 0 &&
    announcements.value.every((item) => selectedAnnouncementIds.value.has(item.id)),
)
const hasAnnouncementFilter = computed(() => announcementSearch.value.trim().length > 0)

const adminStats = computed(() => {
  const base = overview.value
  return {
    totalUsers: base?.totalUsers ?? 0,
    totalCompanies: base?.totalCompanies ?? 0,
    totalAdmins: base?.totalAdmins ?? 0,
    totalRecharge: Number(base?.totalRecharge ?? 0),
    totalWithdraw: Number(base?.totalWithdraw ?? 0),
    pendingRefunds: pendingRefundCount.value,
  }
})

const normalUserCount = computed(
  () => Math.max(0, adminStats.value.totalUsers - adminStats.value.totalCompanies - adminStats.value.totalAdmins),
)

const weeklySeries = computed(() => overview.value?.weeklyRecharge ?? [])
const maxWeeklyAmount = computed(() => {
  const max = weeklySeries.value.reduce((acc, item) => Math.max(acc, item.amount), 0)
  return max <= 0 ? 1 : max
})

const appointmentMetrics = computed(() => overview.value?.appointmentMetrics ?? [])
const maxAppointment = computed(() => {
  const max = appointmentMetrics.value.reduce((acc, item) => Math.max(acc, item.count), 0)
  return max <= 0 ? 1 : max
})

const canSelectRefund = (order: ServiceOrderItem) =>
  order.status === 'REFUND_APPROVED' || order.status === 'REFUND_REJECTED'

const selectableRefunds = computed(() => refundOrders.value.filter((order) => canSelectRefund(order)))
const hasRefundSelection = computed(() => selectedRefundIds.value.size > 0)
const allRefundsSelected = computed(
  () =>
    selectableRefunds.value.length > 0 &&
    selectableRefunds.value.every((order) => selectedRefundIds.value.has(order.id)),
)

const sparkStyle = (amount: number) => ({
  height: `${Math.max(12, Math.round((amount / maxWeeklyAmount.value) * 100))}%`,
})

const metricStyle = (count: number) => ({
  width: `${Math.max(6, Math.round((count / maxAppointment.value) * 100))}%`,
})

const shortLabel = (label: string) => {
  if (!label) return ''
  const parts = label.split('-')
  return parts.length >= 2 ? `${parts[1]}/${parts[2] ?? ''}` : label
}

const formatDateTime = (value: string) => {
  if (!value) return '—'
  return new Date(value).toLocaleString('zh-CN', {
    hour12: false,
  })
}

const settlementSummary = (order: ServiceOrderItem) => {
  if (order.settlementReleasedAt) {
    return `已结算 · ${formatDateTime(order.settlementReleasedAt)}`
  }
  return '已结算'
}

const formatDate = (value: string) => {
  if (!value) return '—'
  return new Date(value).toLocaleDateString('zh-CN')
}

const statusText = (status: ServiceOrderItem['status']) => {
  switch (status) {
    case 'SCHEDULED':
      return '待上门'
    case 'IN_PROGRESS':
      return '服务中'
    case 'COMPLETED':
      return '已完成'
    case 'PENDING':
      return '待确认'
    case 'REFUND_REQUESTED':
      return '退款审核中'
    case 'REFUND_APPROVED':
      return '已退款'
    case 'REFUND_REJECTED':
      return '退款驳回'
    default:
      return status
  }
}

const roleText = (role: string) => ROLE_LABELS[role as keyof typeof ROLE_LABELS] ?? role

const transactionText = (type: string) => {
  switch (type) {
    case 'RECHARGE':
      return '用户充值'
    case 'WITHDRAW':
      return '提现'
    case 'ADJUST':
      return '管理员调整'
    default:
      return type
  }
}

const pruneUserSelection = () => {
  if (!selectedUserIds.value.size) {
    return
  }
  const visibleIds = new Set(users.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedUserIds.value.forEach((id) => {
    if (visibleIds.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedUserIds.value = next
  }
}

const toggleUserSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedUserIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedUserIds.value = next
}

const toggleSelectAllUsers = (checked: boolean) => {
  if (!checked) {
    selectedUserIds.value = new Set()
    return
  }
  const next = new Set(selectedUserIds.value)
  users.value.forEach((item) => {
    if (item.username !== username.value) {
      next.add(item.id)
    }
  })
  selectedUserIds.value = next
}

const clearUserSelection = () => {
  selectedUserIds.value = new Set()
}

const switchSection = (key: SectionKey) => {
  activeSection.value = key
  if (key === 'users') {
    loadUsers()
  } else if (key === 'ledger') {
    loadOrderLedger()
  } else if (key === 'transactions') {
    loadTransactions()
  } else if (key === 'favorites') {
    loadFavorites()
  } else if (key === 'content') {
    loadContent()
  } else if (key === 'refunds') {
    loadRefunds()
  }
}

const logout = () => {
  sessionStorage.removeItem(AUTH_TOKEN_KEY)
  sessionStorage.removeItem(AUTH_ACCOUNT_KEY)
  sessionStorage.removeItem(AUTH_ROLE_KEY)
  router.push({ name: 'login' })
}

const loadOverview = async () => {
  overviewLoading.value = true
  try {
    overview.value = await fetchAdminOverview()
  } catch (error) {
    console.error(error)
  } finally {
    overviewLoading.value = false
  }
}

const loadAdminAccount = async () => {
  try {
    adminAccount.value = await fetchCurrentAccount()
  } catch (error) {
    console.error(error)
  }
}

const loadUsers = async () => {
  usersLoading.value = true
  try {
    const keyword = userSearch.value.trim()
    users.value = await fetchAdminUsers(keyword ? { keyword } : undefined)
    users.value.forEach((user) => {
      if (walletEdits[user.id] === undefined) walletEdits[user.id] = user.balance
      if (loyaltyEdits[user.id] === undefined) loyaltyEdits[user.id] = user.loyaltyPoints
      if (passwordEdits[user.id] === undefined) passwordEdits[user.id] = ''
    })
    pruneUserSelection()
  } catch (error) {
    console.error(error)
    clearUserSelection()
  } finally {
    usersLoading.value = false
  }
}

const loadOrderLedger = async () => {
  orderLedgerLoading.value = true
  try {
    const keyword = orderSearch.value.trim()
    orderLedger.value = await fetchAdminOrders(keyword ? { keyword } : undefined)
    pruneLedgerSelection()
  } catch (error) {
    console.error(error)
  } finally {
    orderLedgerLoading.value = false
  }
}

const loadTransactions = async () => {
  transactionsLoading.value = true
  try {
    const keyword = transactionSearch.value.trim()
    transactions.value = await fetchAdminTransactions(keyword ? { keyword } : undefined)
    pruneTransactionSelection()
  } catch (error) {
    console.error(error)
  } finally {
    transactionsLoading.value = false
  }
}

const loadFavorites = async () => {
  favoritesLoading.value = true
  try {
    const keyword = favoriteSearch.value.trim()
    favorites.value = await fetchAdminFavorites(keyword ? { keyword } : undefined)
    pruneFavoriteSelection()
  } catch (error) {
    console.error(error)
  } finally {
    favoritesLoading.value = false
  }
}

const pruneRefundSelection = () => {
  if (!selectedRefundIds.value.size) {
    return
  }
  const allowed = new Set(selectableRefunds.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedRefundIds.value.forEach((id) => {
    if (allowed.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedRefundIds.value = next
  }
}

const toggleRefundSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedRefundIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedRefundIds.value = next
}

const toggleSelectAllRefunds = (checked: boolean) => {
  if (!checked) {
    clearRefundSelection()
    return
  }
  const next = new Set(selectedRefundIds.value)
  selectableRefunds.value.forEach((order) => next.add(order.id))
  selectedRefundIds.value = next
}

const clearRefundSelection = () => {
  selectedRefundIds.value = new Set()
}

const changeRefundStage = (stage: 'pending' | 'processed' | 'all') => {
  if (refundStage.value === stage) {
    return
  }
  refundStage.value = stage
  clearRefundSelection()
  loadRefunds()
}

const handleBulkDeleteRefunds = async () => {
  if (refundStage.value !== 'processed' || !selectedRefundIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedRefundIds.value.size} 条退款记录？`)) {
    return
  }
  try {
    await deleteAdminRefunds(Array.from(selectedRefundIds.value))
    clearRefundSelection()
    await loadRefunds()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

const handleDeleteSingleRefund = async (order: ServiceOrderItem) => {
  if (!canSelectRefund(order)) {
    return
  }
  if (!window.confirm(`确认删除退款记录“${order.serviceName}”吗？`)) {
    return
  }
  try {
    await deleteAdminRefunds([order.id])
    if (selectedRefundIds.value.has(order.id)) {
      const next = new Set(selectedRefundIds.value)
      next.delete(order.id)
      selectedRefundIds.value = next
    }
    await loadRefunds()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

const handleBulkDeleteLedgerOrders = async () => {
  if (!selectedLedgerIds.value.size) {
    return
  }
  const invalid = Array.from(selectedLedgerIds.value).filter(
    (id) => !deletableLedgerOrders.value.some((order) => order.id === id),
  )
  if (invalid.length) {
    window.alert('仅已结算的订单可以删除，请重新选择。')
    pruneLedgerSelection()
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedLedgerIds.value.size} 条订单记录？`)) {
    return
  }
  try {
    await deleteAdminOrders(Array.from(selectedLedgerIds.value))
    clearLedgerSelection()
    await loadOrderLedger()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

const handleBulkDeleteTransactions = async () => {
  if (!selectedTransactionIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedTransactionIds.value.size} 条流水记录？`)) {
    return
  }
  try {
    await deleteAdminTransactions(Array.from(selectedTransactionIds.value))
    clearTransactionSelection()
    await loadTransactions()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

const handleBulkDeleteFavorites = async () => {
  if (!selectedFavoriteIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedFavoriteIds.value.size} 条收藏记录？`)) {
    return
  }
  try {
    await deleteAdminFavorites(Array.from(selectedFavoriteIds.value))
    clearFavoriteSelection()
    await loadFavorites()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

watch(userSearch, () => {
  if (userSearchTimer) {
    clearTimeout(userSearchTimer)
  }
  userSearchTimer = setTimeout(async () => {
    await loadUsers()
    userSearchTimer = null
  }, 300)
})

watch(orderSearch, () => {
  if (orderSearchTimer) {
    clearTimeout(orderSearchTimer)
  }
  orderSearchTimer = setTimeout(async () => {
    await loadOrderLedger()
    orderSearchTimer = null
  }, 300)
})

watch(transactionSearch, () => {
  if (transactionSearchTimer) {
    clearTimeout(transactionSearchTimer)
  }
  transactionSearchTimer = setTimeout(async () => {
    await loadTransactions()
    transactionSearchTimer = null
  }, 300)
})

watch(favoriteSearch, () => {
  if (favoriteSearchTimer) {
    clearTimeout(favoriteSearchTimer)
  }
  favoriteSearchTimer = setTimeout(async () => {
    await loadFavorites()
    favoriteSearchTimer = null
  }, 300)
})

const clearUserFilter = async () => {
  if (!userSearch.value) {
    return
  }
  userSearch.value = ''
  if (userSearchTimer) {
    clearTimeout(userSearchTimer)
    userSearchTimer = null
  }
  await loadUsers()
}

watch(carouselSearch, () => {
  if (carouselSearchTimer) {
    clearTimeout(carouselSearchTimer)
  }
  carouselSearchTimer = setTimeout(async () => {
    await loadContent()
    carouselSearchTimer = null
  }, 300)
})

watch(tipSearch, () => {
  if (tipSearchTimer) {
    clearTimeout(tipSearchTimer)
  }
  tipSearchTimer = setTimeout(async () => {
    await loadContent()
    tipSearchTimer = null
  }, 300)
})

watch(announcementSearch, () => {
  if (announcementSearchTimer) {
    clearTimeout(announcementSearchTimer)
  }
  announcementSearchTimer = setTimeout(async () => {
    await loadContent()
    announcementSearchTimer = null
  }, 300)
})

const clearCarouselFilter = async () => {
  if (!carouselSearch.value) {
    return
  }
  carouselSearch.value = ''
  if (carouselSearchTimer) {
    clearTimeout(carouselSearchTimer)
    carouselSearchTimer = null
  }
  await loadContent()
}

const clearTipFilter = async () => {
  if (!tipSearch.value) {
    return
  }
  tipSearch.value = ''
  if (tipSearchTimer) {
    clearTimeout(tipSearchTimer)
    tipSearchTimer = null
  }
  await loadContent()
}

const clearAnnouncementFilter = async () => {
  if (!announcementSearch.value) {
    return
  }
  announcementSearch.value = ''
  if (announcementSearchTimer) {
    clearTimeout(announcementSearchTimer)
    announcementSearchTimer = null
  }
  await loadContent()
}

watch(refundSearch, () => {
  if (refundSearchTimer) {
    clearTimeout(refundSearchTimer)
  }
  refundSearchTimer = setTimeout(async () => {
    await loadRefunds()
    refundSearchTimer = null
  }, 300)
})

const loadRefunds = async () => {
  refundsLoading.value = true
  try {
    const keyword = refundSearch.value.trim()
    refundOrders.value = await fetchAdminRefunds({
      stage: refundStage.value,
      keyword: keyword ? keyword : undefined,
    })
    if (refundStage.value === 'pending') {
      pendingRefundCount.value = refundOrders.value.length
    }
    pruneRefundSelection()
  } catch (error) {
    console.error(error)
    refundOrders.value = []
    clearRefundSelection()
  } finally {
    refundsLoading.value = false
  }
}

watch(orderLedger, pruneLedgerSelection)
watch(transactions, pruneTransactionSelection)
watch(favorites, pruneFavoriteSelection)

const pruneCarouselSelection = () => {
  if (!selectedCarouselIds.value.size) return
  const visibleIds = new Set(carousels.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedCarouselIds.value.forEach((id) => {
    if (visibleIds.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedCarouselIds.value = next
  }
}

const toggleCarouselSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedCarouselIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedCarouselIds.value = next
}

const toggleSelectAllCarousels = (checked: boolean) => {
  if (!checked) {
    selectedCarouselIds.value = new Set()
    return
  }
  const next = new Set(selectedCarouselIds.value)
  carousels.value.forEach((item) => next.add(item.id))
  selectedCarouselIds.value = next
}

const clearCarouselSelection = () => {
  selectedCarouselIds.value = new Set()
}

const pruneTipSelection = () => {
  if (!selectedTipIds.value.size) return
  const visibleIds = new Set(tips.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedTipIds.value.forEach((id) => {
    if (visibleIds.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedTipIds.value = next
  }
}

const toggleTipSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedTipIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedTipIds.value = next
}

const toggleSelectAllTips = (checked: boolean) => {
  if (!checked) {
    selectedTipIds.value = new Set()
    return
  }
  const next = new Set(selectedTipIds.value)
  tips.value.forEach((item) => next.add(item.id))
  selectedTipIds.value = next
}

const clearTipSelection = () => {
  selectedTipIds.value = new Set()
}

const pruneAnnouncementSelection = () => {
  if (!selectedAnnouncementIds.value.size) return
  const visibleIds = new Set(announcements.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedAnnouncementIds.value.forEach((id) => {
    if (visibleIds.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedAnnouncementIds.value = next
  }
}

const toggleAnnouncementSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedAnnouncementIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedAnnouncementIds.value = next
}

const toggleSelectAllAnnouncements = (checked: boolean) => {
  if (!checked) {
    selectedAnnouncementIds.value = new Set()
    return
  }
  const next = new Set(selectedAnnouncementIds.value)
  announcements.value.forEach((item) => next.add(item.id))
  selectedAnnouncementIds.value = next
}

const clearAnnouncementSelection = () => {
  selectedAnnouncementIds.value = new Set()
}

const loadContent = async () => {
  contentLoading.value = true
  try {
    const [carouselData, tipData, announcementData] = await Promise.all([
      fetchDashboardCarousels(carouselSearch.value.trim() ? { keyword: carouselSearch.value.trim() } : undefined),
      fetchDashboardTips(tipSearch.value.trim() ? { keyword: tipSearch.value.trim() } : undefined),
      fetchDashboardAnnouncements(
        announcementSearch.value.trim() ? { keyword: announcementSearch.value.trim() } : undefined,
      ),
    ])
    carousels.value = carouselData
    tips.value = tipData
    announcements.value = announcementData
    pruneCarouselSelection()
    pruneTipSelection()
    pruneAnnouncementSelection()
  } catch (error) {
    console.error(error)
  } finally {
    contentLoading.value = false
  }
}

const saveWallet = async (userId: number) => {
  try {
    await updateAdminWallet(userId, { money: walletEdits[userId] } as UpdateWalletPayload)
    await loadUsers()
  } catch (error) {
    console.error(error)
  }
}

const saveLoyalty = async (userId: number) => {
  try {
    await updateAdminLoyalty(userId, { loyaltyPoints: loyaltyEdits[userId] } as UpdateLoyaltyPayload)
    await loadUsers()
  } catch (error) {
    console.error(error)
  }
}

const savePassword = async (userId: number) => {
  if (!passwordEdits[userId]) {
    window.alert('请先输入新密码')
    return
  }
  try {
    await updateAdminPassword(userId, { password: passwordEdits[userId] } as UpdatePasswordPayload)
    passwordEdits[userId] = ''
    window.alert('密码已更新')
  } catch (error) {
    console.error(error)
  }
}

const handleDeleteUser = async (user: UserAccountItem) => {
  if (user.username === username.value) {
    window.alert('不能删除当前登录账号')
    return
  }
  if (!window.confirm(`确认删除账号「${user.username}」吗？`)) {
    return
  }
  try {
    await deleteAdminUser(user.id)
    const next = new Set(selectedUserIds.value)
    next.delete(user.id)
    selectedUserIds.value = next
    await loadUsers()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

const handleBulkDeleteUsers = async () => {
  if (!selectedUserIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedUserIds.value.size} 个账号吗？`)) {
    return
  }
  try {
    await deleteAdminUsers(Array.from(selectedUserIds.value))
    clearUserSelection()
    await loadUsers()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

const completeSettlement = async (order: ServiceOrderItem) => {
  if (order.settlementReleased || order.status !== 'COMPLETED') {
    return
  }
  settlementSaving[order.id] = true
  try {
    const updated = await settleAdminOrder(order.id)
    applyOrderUpdate(updated)
    await loadAdminAccount()
    window.alert('结算完成')
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '结算失败')
  } finally {
    settlementSaving[order.id] = false
  }
}

const handleRefund = async (order: ServiceOrderItem, approve: boolean) => {
  const message = approve ? '确认同意该退款？' : '确认拒绝该退款？'
  if (!window.confirm(message)) return
  try {
    await handleAdminRefund(order.id, { approve })
    if (selectedRefundIds.value.has(order.id)) {
      const next = new Set(selectedRefundIds.value)
      next.delete(order.id)
      selectedRefundIds.value = next
    }
    await loadRefunds()
    await loadOrderLedger()
    await loadOverview()
  } catch (error) {
    console.error(error)
  }
}

const editCarousel = (item: DashboardCarouselItem) => {
  carouselEditing.value = item.id
  carouselForm.title = item.title
  carouselForm.imageUrl = item.imageUrl
  carouselForm.serviceLink = item.serviceLink ?? ''
}

const resetCarousel = () => {
  carouselEditing.value = null
  carouselForm.title = ''
  carouselForm.imageUrl = ''
  carouselForm.serviceLink = ''
}

const submitCarousel = async () => {
  carouselSaving.value = true
  try {
    if (carouselEditing.value) {
      await updateDashboardCarousel(carouselEditing.value, { ...carouselForm, id: carouselEditing.value })
    } else {
      await createDashboardCarousel({ ...carouselForm, id: 0 })
    }
    resetCarousel()
    await loadContent()
  } catch (error) {
    console.error(error)
  } finally {
    carouselSaving.value = false
  }
}

const removeCarousel = async (id: number) => {
  if (!window.confirm('确定删除该轮播图？')) return
  try {
    await deleteDashboardCarousel(id)
    const next = new Set(selectedCarouselIds.value)
    next.delete(id)
    selectedCarouselIds.value = next
    await loadContent()
  } catch (error) {
    console.error(error)
  }
}

const handleBulkDeleteCarousels = async () => {
  if (!selectedCarouselIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedCarouselIds.value.size} 个轮播图吗？`)) {
    return
  }
  try {
    await deleteDashboardCarousels(Array.from(selectedCarouselIds.value))
    clearCarouselSelection()
    await loadContent()
  } catch (error) {
    console.error(error)
  }
}

const editTip = (item: DashboardTipItem) => {
  tipEditing.value = item.id
  tipForm.title = item.title
  tipForm.content = item.content
}

const resetTip = () => {
  tipEditing.value = null
  tipForm.title = ''
  tipForm.content = ''
}

const submitTip = async () => {
  tipSaving.value = true
  try {
    if (tipEditing.value) {
      await updateDashboardTip(tipEditing.value, { ...tipForm, id: tipEditing.value })
    } else {
      await createDashboardTip({ ...tipForm, id: 0 })
    }
    resetTip()
    await loadContent()
  } catch (error) {
    console.error(error)
  } finally {
    tipSaving.value = false
  }
}

const removeTip = async (id: number) => {
  if (!window.confirm('确定删除该贴士？')) return
  try {
    await deleteDashboardTip(id)
    const next = new Set(selectedTipIds.value)
    next.delete(id)
    selectedTipIds.value = next
    await loadContent()
  } catch (error) {
    console.error(error)
  }
}

const handleBulkDeleteTips = async () => {
  if (!selectedTipIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedTipIds.value.size} 条贴士吗？`)) {
    return
  }
  try {
    await deleteDashboardTips(Array.from(selectedTipIds.value))
    clearTipSelection()
    await loadContent()
  } catch (error) {
    console.error(error)
  }
}

const editAnnouncement = (item: DashboardAnnouncementItem) => {
  announcementEditing.value = item.id
  announcementForm.title = item.title
  announcementForm.content = item.content
}

const resetAnnouncement = () => {
  announcementEditing.value = null
  announcementForm.title = ''
  announcementForm.content = ''
}

const submitAnnouncement = async () => {
  announcementSaving.value = true
  try {
    if (announcementEditing.value) {
      await updateDashboardAnnouncement(announcementEditing.value, {
        ...announcementForm,
        id: announcementEditing.value,
      })
    } else {
      await createDashboardAnnouncement({ ...announcementForm, id: 0 })
    }
    resetAnnouncement()
    await loadContent()
  } catch (error) {
    console.error(error)
  } finally {
    announcementSaving.value = false
  }
}

const removeAnnouncement = async (id: number) => {
  if (!window.confirm('确定删除该公告？')) return
  try {
    await deleteDashboardAnnouncement(id)
    const next = new Set(selectedAnnouncementIds.value)
    next.delete(id)
    selectedAnnouncementIds.value = next
    await loadContent()
  } catch (error) {
    console.error(error)
  }
}

const handleBulkDeleteAnnouncements = async () => {
  if (!selectedAnnouncementIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedAnnouncementIds.value.size} 条公告吗？`)) {
    return
  }
  try {
    await deleteDashboardAnnouncements(Array.from(selectedAnnouncementIds.value))
    clearAnnouncementSelection()
    await loadContent()
  } catch (error) {
    console.error(error)
  }
}

onMounted(async () => {
  await Promise.all([loadOverview(), loadUsers(), loadRefunds(), loadAdminAccount(), loadOrderLedger()])
})

onUnmounted(() => {
  if (userSearchTimer) {
    clearTimeout(userSearchTimer)
    userSearchTimer = null
  }
  if (refundSearchTimer) {
    clearTimeout(refundSearchTimer)
    refundSearchTimer = null
  }
  if (carouselSearchTimer) {
    clearTimeout(carouselSearchTimer)
    carouselSearchTimer = null
  }
  if (tipSearchTimer) {
    clearTimeout(tipSearchTimer)
    tipSearchTimer = null
  }
  if (announcementSearchTimer) {
    clearTimeout(announcementSearchTimer)
    announcementSearchTimer = null
  }
  if (orderSearchTimer) {
    clearTimeout(orderSearchTimer)
    orderSearchTimer = null
  }
  if (transactionSearchTimer) {
    clearTimeout(transactionSearchTimer)
    transactionSearchTimer = null
  }
  if (favoriteSearchTimer) {
    clearTimeout(favoriteSearchTimer)
    favoriteSearchTimer = null
  }
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  padding: 2.5rem 2rem 3rem;
  background: radial-gradient(circle at top left, rgba(90, 119, 255, 0.18), transparent 55%),
    radial-gradient(circle at bottom right, rgba(255, 255, 255, 0.18), transparent 50%),
    linear-gradient(135deg, #0f172a, #1f2937 45%, #0b1120 100%);
  color: #f8fafc;
}

.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
  position: relative;
}

.dashboard-header::after {
  content: '';
  position: absolute;
  inset: auto 0 -0.75rem;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(148, 163, 184, 0.4), transparent);
}

.dashboard-title {
  font-size: 2.5rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  margin: 0;
}

.dashboard-subtitle {
  margin-top: 0.75rem;
  color: rgba(226, 232, 240, 0.75);
  font-size: 1.05rem;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.95rem;
  color: rgba(226, 232, 240, 0.9);
}

.wallet-balance {
  font-weight: 600;
  color: #38bdf8;
}

.logout-button {
  padding: 0.5rem 1.25rem;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.2);
  border: 1px solid rgba(148, 163, 184, 0.35);
  color: #f8fafc;
  transition: all 0.2s ease;
}

.logout-button:hover {
  background: rgba(255, 255, 255, 0.18);
  border-color: rgba(255, 255, 255, 0.4);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2.5rem;
}

.stat-card {
  padding: 1.75rem;
  border-radius: 1.25rem;
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(148, 163, 184, 0.15);
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.35);
  position: relative;
  overflow: hidden;
}

.stat-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.35), transparent 55%);
  opacity: 0.35;
  pointer-events: none;
}

.stat-card.accent::after {
  background: radial-gradient(circle at top right, rgba(16, 185, 129, 0.45), transparent 60%);
}

.stat-card.warning::after {
  background: radial-gradient(circle at top right, rgba(248, 113, 113, 0.5), transparent 55%);
}

.stat-card.glass {
  border-color: rgba(125, 211, 252, 0.3);
  background: rgba(8, 47, 73, 0.6);
}

.stat-label {
  font-size: 0.95rem;
  color: rgba(226, 232, 240, 0.7);
  margin: 0 0 0.5rem;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  margin: 0;
  color: #f8fafc;
}

.stat-helper {
  margin-top: 0.75rem;
  font-size: 0.9rem;
  color: rgba(226, 232, 240, 0.6);
}

.dashboard-main {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 2rem;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.85rem 1.1rem;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.35);
  border: 1px solid transparent;
  color: rgba(226, 232, 240, 0.75);
  transition: all 0.2s ease;
}

.sidebar-item:hover {
  color: #f8fafc;
  border-color: rgba(148, 163, 184, 0.35);
}

.sidebar-item.active {
  color: #0f172a;
  background: linear-gradient(120deg, #38bdf8, #6366f1);
  border-color: transparent;
  box-shadow: 0 15px 30px rgba(14, 165, 233, 0.25);
}

.sidebar-icon {
  font-size: 1.2rem;
}

.content {
  display: block;
}

.panel {
  padding: 2rem;
  border-radius: 1.5rem;
  background: rgba(15, 23, 42, 0.55);
  border: 1px solid rgba(148, 163, 184, 0.18);
  backdrop-filter: blur(18px);
  box-shadow: 0 30px 60px rgba(15, 23, 42, 0.35);
}

.immersive-panel {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.18), rgba(14, 165, 233, 0.1));
  border: 1px solid rgba(148, 163, 184, 0.25);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.panel-header h2 {
  margin: 0;
  font-size: 1.6rem;
  font-weight: 600;
}

.panel-header p {
  margin: 0.35rem 0 0;
  color: rgba(226, 232, 240, 0.65);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.helper-note {
  font-size: 0.85rem;
  color: rgba(226, 232, 240, 0.6);
  white-space: nowrap;
}

.search-input {
  min-width: 220px;
  padding: 0.45rem 0.8rem;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: rgba(15, 23, 42, 0.4);
  color: #e2e8f0;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-input:focus {
  outline: none;
  border-color: rgba(96, 165, 250, 0.7);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

.ghost-button {
  padding: 0.55rem 1.3rem;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: rgba(15, 23, 42, 0.3);
  color: #e2e8f0;
  transition: all 0.2s ease;
}

.ghost-button:hover {
  background: rgba(148, 163, 184, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
}

.loading-state {
  padding: 2.5rem;
  text-align: center;
  color: rgba(226, 232, 240, 0.7);
}

.table-wrapper {
  overflow: auto;
  border-radius: 1.1rem;
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.95rem;
}

.data-table th,
.data-table td {
  padding: 1rem 1.2rem;
  text-align: left;
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
}

.data-table thead {
  background: rgba(15, 23, 42, 0.4);
  color: rgba(226, 232, 240, 0.8);
}

.data-table tbody tr:hover {
  background: rgba(148, 163, 184, 0.08);
}

.table-checkbox {
  width: 52px;
  text-align: center;
}

.table-checkbox input[type='checkbox'] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.inline-form {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.inline-form input,
.content-form input,
.content-form textarea {
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(148, 163, 184, 0.25);
  border-radius: 0.75rem;
  padding: 0.55rem 0.75rem;
  color: #f8fafc;
}

.inline-form input:focus,
.content-form input:focus,
.content-form textarea:focus {
  outline: none;
  border-color: rgba(99, 102, 241, 0.55);
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
}

.link-button {
  background: none;
  border: none;
  color: #38bdf8;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  font-size: 0.9rem;
}

.link-button.danger {
  color: #f87171;
}

.primary-button {
  padding: 0.55rem 1.15rem;
  border-radius: 999px;
  border: none;
  background: linear-gradient(120deg, #6366f1, #38bdf8);
  color: #0f172a;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.primary-button:hover {
  transform: translateY(-1px);
}

.secondary-button {
  padding: 0.55rem 1.15rem;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: transparent;
  color: rgba(226, 232, 240, 0.8);
}

.settlement-cell {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 0.5rem;
}

.settlement-status {
  font-size: 0.9rem;
  color: rgba(226, 232, 240, 0.7);
}

.settlement-status.completed {
  color: #4ade80;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}

.status-scheduled {
  background: rgba(96, 165, 250, 0.15);
  color: #93c5fd;
}

.status-in_progress {
  background: rgba(45, 212, 191, 0.2);
  color: #5eead4;
}

.status-completed {
  background: rgba(34, 197, 94, 0.2);
  color: #86efac;
}

.status-refund_requested {
  background: rgba(248, 113, 113, 0.2);
  color: #fca5a5;
}

.order-subtext {
  color: rgba(226, 232, 240, 0.65);
  font-size: 0.85rem;
}

.order-subtext.muted {
  color: rgba(148, 163, 184, 0.6);
}

.table-actions {
  white-space: nowrap;
}

.actions-inline {
  display: flex;
  gap: 0.75rem;
}

.empty-row {
  text-align: center;
  padding: 1.75rem 0;
  color: rgba(226, 232, 240, 0.6);
}

.overview-grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
}

.insight-card {
  padding: 1.6rem;
  border-radius: 1.25rem;
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(148, 163, 184, 0.18);
  min-height: 240px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.insight-card.highlight {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.2), rgba(14, 165, 233, 0.18));
  border-color: rgba(148, 163, 184, 0.28);
}

.insight-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 1rem;
}

.insight-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.insight-helper {
  color: rgba(226, 232, 240, 0.55);
  font-size: 0.85rem;
}

.sparkline {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 0.75rem;
  align-items: end;
  height: 160px;
}

.spark-bar {
  position: relative;
  background: linear-gradient(180deg, rgba(56, 189, 248, 0.85), rgba(99, 102, 241, 0.65));
  border-radius: 0.75rem 0.75rem 0.35rem 0.35rem;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 0.75rem 0.35rem 0.6rem;
  transition: transform 0.2s ease;
}

.spark-bar:hover {
  transform: translateY(-4px);
}

.spark-amount {
  font-size: 0.8rem;
  font-weight: 600;
  color: rgba(15, 23, 42, 0.9);
  margin-bottom: 0.35rem;
}

.spark-label {
  font-size: 0.75rem;
  color: rgba(15, 23, 42, 0.65);
}

.metric-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
}

.metric-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.metric-bar {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 65%;
}

.metric-fill {
  height: 8px;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.85), rgba(14, 165, 233, 0.85));
  box-shadow: 0 6px 12px rgba(14, 165, 233, 0.25);
}

.stat-lines {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.stat-lines li {
  display: flex;
  justify-content: space-between;
  color: rgba(226, 232, 240, 0.85);
}

.positive {
  color: #4ade80;
}

.negative {
  color: #fca5a5;
}

.content-grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.content-card {
  background: rgba(15, 23, 42, 0.55);
  border-radius: 1.2rem;
  border: 1px solid rgba(148, 163, 184, 0.2);
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-height: 100%;
}

.content-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  flex-wrap: wrap;
}

.content-card-header > div:first-child {
  flex: 1 1 180px;
  min-width: 0;
}

.content-toolbar {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.content-card header h3 {
  margin: 0;
  font-size: 1.15rem;
}

.content-card header p {
  margin: 0.4rem 0 0;
  color: rgba(226, 232, 240, 0.6);
  font-size: 0.9rem;
}

.content-form {
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
}

.content-form textarea {
  resize: vertical;
  min-height: 90px;
}

.content-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
}

.content-list li {
  padding: 0.85rem;
  border-radius: 0.9rem;
  background: rgba(15, 23, 42, 0.45);
  border: 1px solid rgba(148, 163, 184, 0.15);
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.content-item-details {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  flex: 1 1 auto;
  min-width: 0;
}

.content-item-details > div {
  flex: 1 1 auto;
  min-width: 0;
}

.content-checkbox {
  width: 1.1rem;
  height: 1.1rem;
  margin-top: 0.25rem;
  accent-color: #6366f1;
  flex-shrink: 0;
}

.content-list li > div:first-child {
  flex: 1 1 auto;
  min-width: 0;
  overflow-wrap: anywhere;
}

.content-list li strong {
  display: block;
  overflow-wrap: anywhere;
}

.list-actions {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  align-items: flex-end;
}

.muted {
  color: rgba(148, 163, 184, 0.7);
  font-size: 0.85rem;
  margin: 0.15rem 0 0;
  overflow-wrap: anywhere;
}

.form-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.visually-hidden {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.refund-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  justify-content: flex-end;
}

.stage-switch {
  display: inline-flex;
  gap: 8px;
  padding: 6px;
  border-radius: 999px;
  background: rgba(30, 41, 59, 0.55);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.25);
}

.chip-button {
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: rgba(15, 23, 42, 0.6);
  padding: 6px 16px;
  border-radius: 999px;
  cursor: pointer;
  color: #f8fafc;
  font-weight: 600;
  font-size: 0.95rem;
  letter-spacing: 0.02em;
  text-shadow: 0 1px 2px rgba(15, 23, 42, 0.5);
  transition: background 0.2s ease, color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

.chip-button.active {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.45), rgba(14, 165, 233, 0.35));
  border-color: rgba(96, 165, 250, 0.65);
  color: #0f172a;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.25);
}

.chip-button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.chip-button:not(:disabled):hover {
  border-color: rgba(148, 163, 184, 0.6);
  background: rgba(59, 130, 246, 0.25);
  color: #bfdbfe;
}

@media (max-width: 1080px) {
  .dashboard-main {
    grid-template-columns: 1fr;
  }

  .sidebar {
    flex-direction: row;
    flex-wrap: wrap;
    margin-bottom: 1.5rem;
  }

  .sidebar-item {
    flex: 1 1 140px;
    justify-content: center;
  }
}
</style>

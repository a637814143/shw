<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1 class="dashboard-title">家政服务体验中心</h1>
        <p class="dashboard-subtitle">精选内容、智能预约、实时沟通，沉浸式高级体验。</p>
      </div>
      <div class="header-actions">
        <img :src="avatarSrc" alt="账号头像" class="account-avatar" />
        <span class="welcome">您好，{{ displayName }}！</span>
        <span class="wallet">钱包余额：¥{{ balanceText }}</span>
        <span class="loyalty">积分：{{ loyaltyText }}</span>
        <button type="button" class="logout-button" @click="logout">退出登录</button>
      </div>
    </header>

    <section class="stats-grid" aria-label="账户概览">
      <article class="stat-card accent">
        <p class="stat-label">积分余额</p>
        <p class="stat-value">{{ loyaltyText }}</p>
        <p class="stat-helper">可在钱包中兑换余额（5:1）</p>
      </article>
      <article class="stat-card primary">
        <p class="stat-label">待服务订单</p>
        <p class="stat-value">{{ orderStats.awaiting }}</p>
        <p class="stat-helper">等待家政人员上门</p>
      </article>
      <article class="stat-card warning">
        <p class="stat-label">系统公告</p>
        <p class="stat-value">{{ announcements.length }}</p>
        <p class="stat-helper">关注最新服务与活动</p>
      </article>
      <article class="stat-card success">
        <p class="stat-label">收藏服务</p>
        <p class="stat-value">{{ favoritesCount }}</p>
        <p class="stat-helper">点击服务卡片右上角可收藏</p>
      </article>
    </section>

    <transition name="fade">
      <div v-if="bookingDialogVisible" class="dialog-backdrop" @click.self="closeBooking">
        <form class="dialog-card" @submit.prevent="submitBooking">
          <header class="dialog-header">
            <h2>预约 {{ bookingForm.service?.name }}</h2>
            <p>请选择上门时间并填写特殊需求，平台会将信息同步给 {{ bookingForm.service?.companyName }}。</p>
            <p class="dialog-subtext">建议服务时长：{{ bookingForm.service?.serviceTime || '按需预约' }}</p>
          </header>
          <div class="dialog-body">
            <label class="dialog-field">
              <span>预约日期</span>
              <input
                v-model="bookingForm.selectedDate"
                type="date"
                class="booking-date-picker"
                :min="bookingDateLimits.min"
                :max="bookingDateLimits.max"
                required
              />
              <p class="dialog-subtext">仅支持预约今天或明天，其他日期不可选</p>
            </label>
            <label class="dialog-field">
              <span>时间段</span>
              <select v-model="bookingForm.timeSlotKey" required class="time-slot-select">
                <option value="" disabled>请选择时间段</option>
                <option
                  v-for="slot in BOOKING_TIME_SLOTS"
                  :key="slot.key"
                  :value="slot.key"
                  :disabled="isSlotUnavailableForSelectedDate(slot)"
                >
                  {{ slotLabelWithAvailability(slot) }}
                </option>
              </select>
            </label>
            <label class="dialog-field">
              <span>服务地址</span>
              <input
                v-model.trim="bookingForm.serviceAddress"
                type="text"
                maxlength="255"
                placeholder="请输入或确认上门地址"
                required
              />
            </label>
            <label class="dialog-field">
              <span>特殊需求（选填）</span>
              <textarea
                v-model="bookingForm.specialRequest"
                rows="3"
                maxlength="500"
                placeholder="例如：提前电话联系、携带清洁工具等"
              ></textarea>
            </label>
          </div>
          <footer class="dialog-footer">
            <button type="button" class="secondary-button" @click="closeBooking">取消</button>
            <button type="submit" class="primary-button">确认预约</button>
          </footer>
    </form>
  </div>
</transition>

<transition name="fade">
  <div v-if="paymentDialogVisible" class="dialog-backdrop" @click.self="closePaymentDialog">
    <form class="dialog-card payment-card" @submit.prevent="confirmPayment">
      <header class="dialog-header">
        <h2>确认支付</h2>
        <p>请填写付款账号并选择支付方式，确认后系统将直接处理订单。</p>
      </header>
      <div class="payment-body">
        <p class="payment-summary">
          服务：{{ paymentServiceName || '—' }}
          <span v-if="paymentCompanyName"> · 提供方：{{ paymentCompanyName }}</span>
        </p>
        <p v-if="paymentAmount !== null" class="payment-summary">金额：¥{{ paymentAmount.toFixed(2) }}</p>

        <label class="dialog-field">
          <span>支付账号</span>
          <input
            v-model.trim="paymentForm.account"
            type="text"
            maxlength="100"
            placeholder="请输入微信或支付宝账号"
            required
          />
        </label>

        <fieldset class="payment-methods">
          <legend>支付方式</legend>
          <label class="method-option">
            <input v-model="paymentForm.method" type="radio" value="wechat" />
            微信支付
          </label>
          <label class="method-option">
            <input v-model="paymentForm.method" type="radio" value="alipay" />
            支付宝
          </label>
        </fieldset>

        <p v-if="paymentMessage" class="payment-status success">{{ paymentMessage }}</p>
        <p v-else-if="paymentError" class="payment-status error">{{ paymentError }}</p>
        <p v-else class="payment-status">确认后将立即处理支付并创建订单。</p>
      </div>
      <footer class="dialog-footer">
        <button type="button" class="secondary-button" :disabled="paymentProcessing" @click="closePaymentDialog">
          取消
        </button>
        <button type="submit" class="primary-button" :disabled="paymentProcessing">
          {{ paymentProcessing ? '支付中…' : '确认支付' }}
        </button>
      </footer>
    </form>
  </div>
</transition>

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
          <span class="sidebar-icon" aria-hidden="true">{{ item.icon }}</span>
          {{ item.label }}
        </button>
      </aside>

      <main class="content">
        <section v-if="activeSection === 'profile'" class="panel profile-panel">
          <header class="panel-header">
            <div>
              <h2>个人资料</h2>
              <p>更新头像与展示名称，让服务更具个性。</p>
            </div>
          </header>
          <AccountProfileEditor :account="account" @updated="handleProfileUpdated" />
        </section>

        <section v-else-if="activeSection === 'discover'" class="panel immersive-panel">
          <header class="panel-header">
            <div>
              <h2>精选推荐</h2>
            </div>
            <button type="button" class="ghost-button" @click="refreshDiscover" :disabled="discoverLoading">
              {{ discoverLoading ? '刷新中…' : '刷新内容' }}
            </button>
          </header>
          <div v-if="discoverLoading" class="loading-state">正在同步精选内容…</div>
          <div v-else class="discover-grid">
            <section class="carousel">
              <header class="section-title">
                <h3>主题轮播</h3>
              </header>
              <div
                v-if="currentCarousel"
                :class="['carousel-frame', { 'has-controls': carousels.length > 1 }]"
                role="region"
                aria-label="精选轮播"
              >
                <button
                  v-if="carousels.length > 1"
                  type="button"
                  class="carousel-button prev"
                  @click="showPreviousCarousel"
                  aria-label="上一张"
                >
                  ‹
                </button>
                <div class="carousel-window">
                  <article :key="currentCarousel.id" class="carousel-card">
                    <div class="carousel-media" :style="{ backgroundImage: `url(${currentCarousel.imageUrl})` }"></div>
                    <div class="carousel-body">
                      <h4>{{ currentCarousel.title }}</h4>
                      <p>{{ currentCarousel.serviceLink ? currentCarousel.serviceLink : '精选家政专题' }}</p>
                    </div>
                  </article>
                </div>
                <button
                  v-if="carousels.length > 1"
                  type="button"
                  class="carousel-button next"
                  @click="showNextCarousel"
                  aria-label="下一张"
                >
                  ›
                </button>
              </div>
              <p v-else class="empty-tip">暂无轮播内容，稍后再来看看。</p>
              <div v-if="carousels.length > 1" class="carousel-indicator">
                {{ carouselIndex + 1 }} / {{ carousels.length }}
              </div>
            </section>

            <section class="tips">
              <header class="section-title">
                <h3>居家贴士</h3>
                <p>精选生活小窍门，守护家庭温度。</p>
              </header>
              <ul class="tip-list">
                <li v-for="item in tips" :key="item.id">
                  <strong>{{ item.title }}</strong>
                  <p>{{ item.content }}</p>
                </li>
                <li v-if="!tips.length" class="empty-tip">暂无贴士内容。</li>
              </ul>
            </section>

            <section class="announcements">
              <header class="section-title">
                <h3>系统公告</h3>
                <p>及时获取平台政策与活动提醒。</p>
              </header>
              <ul class="announcement-list">
                <li v-for="item in announcements" :key="item.id">
                  <strong>{{ item.title }}</strong>
                  <p>{{ item.content }}</p>
                </li>
                <li v-if="!announcements.length" class="empty-tip">暂无公告。</li>
              </ul>
            </section>
          </div>
        </section>

        <section v-else-if="activeSection === 'services'" class="panel">
          <header class="panel-header service-panel-header">
            <div class="service-header-top">
              <h2>可选家政服务</h2>
              <div class="service-search-group">
                <label class="visually-hidden" for="user-service-search">搜索服务</label>
                <input
                  id="user-service-search"
                  v-model="serviceSearch"
                  class="search-input"
                  type="search"
                  placeholder="搜索名称、单位、联系方式或描述"
                />
                <button type="button" class="ghost-button" @click="loadServices">刷新列表</button>
              </div>
            </div>
            <div class="service-category-row" aria-live="polite">
              <nav class="service-category-tabs" aria-label="服务分类筛选">
                <button
                  type="button"
                  class="category-tab"
                  :class="{ active: activeServiceCategoryId === null }"
                  @click="resetServiceFilters"
                >
                  全部
                </button>
                <button
                  v-if="serviceCategoriesLoading"
                  type="button"
                  class="category-tab loading"
                  disabled
                >
                  加载中…
                </button>
                <template v-else>
                  <button
                    v-for="category in serviceCategories"
                    :key="category.id"
                    type="button"
                    class="category-tab"
                    :class="{ active: activeServiceCategoryId === category.id }"
                    @click="handleSelectServiceCategory(category.id)"
                  >
                    {{ category.name }}
                  </button>
                </template>
                <p v-if="!serviceCategoriesLoading && !serviceCategories.length" class="category-empty">暂无分类</p>
              </nav>
            </div>
          </header>
          <div class="service-grid">
            <article v-for="service in services" :key="service.id" class="service-card">
              <div class="service-cover" :style="serviceCoverStyle(service)">
                <img
                  v-if="service.imageBase64"
                  :src="service.imageBase64"
                  alt="服务封面"
                  class="service-cover-img"
                />
                <span v-else class="service-cover-placeholder">服务封面</span>
              </div>
              <button
                type="button"
                class="favorite-toggle"
                :class="{ active: isServiceFavorite(service.id) }"
                :aria-pressed="isServiceFavorite(service.id)"
                :disabled="favoriteLoadingIds.has(service.id) || favoritesLoading"
                @click="toggleFavorite(service)"
              >
                {{ isServiceFavorite(service.id) ? '❤️' : '🤍' }}
              </button>
              <h3 class="service-title">{{ service.name }}</h3>
              <p class="service-company">提供方：{{ service.companyName }}</p>
              <dl class="service-meta">
                <div>
                  <dt>计价单位</dt>
                  <dd>{{ service.unit }}</dd>
                </div>
                <div>
                  <dt>服务价格</dt>
                  <dd>¥{{ service.price.toFixed(2) }}</dd>
                </div>
                <div>
                  <dt>联系方式</dt>
                  <dd>{{ service.contact }}</dd>
                </div>
                <div>
                  <dt>服务时长</dt>
                  <dd>{{ service.serviceTime }}</dd>
                </div>
              </dl>
              <p v-if="service.description" class="service-desc">{{ service.description }}</p>
              <footer class="service-card-footer">
                <span v-if="service.categoryName" class="service-category-chip">{{ service.categoryName }}</span>
                <button
                  type="button"
                  class="primary-button service-book-button"
                  @click="handleSelectService(service)"
                >
                  预约服务
                </button>
              </footer>
            </article>
            <p v-if="!services.length" class="empty-tip">
              <span v-if="hasServiceFilter && allServices.length">没有找到符合条件的服务，换个关键词试试吧。</span>
              <span v-else>暂无家政公司发布服务，稍后再来看看吧。</span>
            </p>
          </div>
        </section>

        <section v-else-if="activeSection === 'favorites'" class="panel">
          <header class="panel-header">
            <div>
              <h2>我的收藏服务</h2>
              <p>集中管理心仪的服务，快速预约或取消收藏。</p>
            </div>
            <div class="service-actions">
              <label class="visually-hidden" for="user-favorite-search">搜索收藏</label>
              <input
                id="user-favorite-search"
                v-model="favoriteSearch"
                class="search-input"
                type="search"
                :disabled="favoritesLoading"
                placeholder="搜索服务或家政公司"
              />
              <button type="button" class="secondary-button" :disabled="favoritesLoading" @click="loadFavorites">
                {{ favoritesLoading ? '刷新中…' : '刷新收藏' }}
              </button>
            </div>
          </header>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>服务</th>
                  <th>收藏时间</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in visibleFavorites" :key="item.id">
                  <td>
                    <strong>{{ item.serviceName }}</strong>
                    <div class="order-subtext">家政公司：{{ item.companyName }}</div>
                  </td>
                  <td>{{ formatDateTime(item.createdAt) }}</td>
                  <td class="table-actions">
                    <button type="button" class="link-button" @click="viewFavoriteService(item)">查看服务</button>
                    <button
                      type="button"
                      class="link-button"
                      :disabled="favoritesLoading"
                      @click="bookFavoriteService(item)"
                    >
                      预约服务
                    </button>
                    <button
                      type="button"
                      class="link-button danger"
                      :disabled="favoritesLoading"
                      @click="handleRemoveFavorite(item)"
                    >
                      取消收藏
                    </button>
                  </td>
                </tr>
                <tr v-if="favoritesLoading">
                  <td colspan="3" class="empty-row">收藏加载中…</td>
                </tr>
                <tr v-else-if="!visibleFavorites.length">
                  <td colspan="3" class="empty-row">
                    <span v-if="hasFavoriteFilter">没有找到匹配的收藏，换个关键词试试。</span>
                    <span v-else>还没有收藏服务，去选择服务页面看看吧。</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="activeSection === 'orders'" class="panel">
          <header class="panel-header">
            <div>
              <h2>我的服务订单</h2>
              <p>共 {{ orderStats.total }} 单 · 已完成 {{ orderStats.completed }} 单。</p>
            </div>
            <div class="service-actions">
              <label class="visually-hidden" for="user-order-search">搜索订单</label>
              <input
                id="user-order-search"
                v-model="orderSearch"
                class="search-input"
                type="search"
                :disabled="ordersLoading"
                placeholder="搜索服务、公司、电话或备注"
              />
              <button
                type="button"
                class="secondary-button danger"
                :disabled="!hasOrderSelection || ordersLoading"
                @click="handleBulkDeleteOrders"
              >
                删除选中<span v-if="selectedOrderCount">（{{ selectedOrderCount }}）</span>
              </button>
              <button type="button" class="secondary-button" :disabled="ordersLoading" @click="loadOrders">
                刷新订单
              </button>
            </div>
          </header>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="allVisibleOrdersSelected"
                      :disabled="ordersLoading || !deletableVisibleOrders.length"
                      @change="toggleSelectAllOrders(($event.target as HTMLInputElement).checked)"
                      aria-label="全选订单"
                    />
                  </th>
                  <th>服务</th>
                  <th>预约时间</th>
                  <th>状态</th>
                  <th>进度与需求</th>
                  <th>退款申请</th>
                  <th>处理信息</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in visibleOrders" :key="order.id">
                  <td class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="selectedOrderIds.has(order.id)"
                      :disabled="ordersLoading || !canDeleteOrder(order)"
                      @change="toggleOrderSelection(order.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择订单 ${order.serviceName}`"
                    />
                  </td>
                  <td>
                    <strong>{{ order.serviceName }}</strong>
                    <div class="order-subtext">{{ order.companyName }} · {{ order.contact }}</div>
                    <div class="order-meta">
                      <span>¥{{ order.price.toFixed(2) }} / {{ order.unit }}</span>
                      <span>积分 +{{ order.loyaltyPoints }}</span>
                    </div>
                    <div class="order-subtext">上门地址：{{ order.serviceAddress || '未填写' }}</div>
                    <div class="order-subtext">服务时间段：{{ formatServiceWindow(order) }}</div>
                  </td>
                  <td>
                    <div class="order-subtext">{{ formatAppointmentStart(order) }}</div>
                  </td>
                  <td>
                    <span class="status-badge" :class="`status-${order.status.toLowerCase()}`">{{ statusText(order.status) }}</span>
                    <div v-if="order.assignedWorker" class="order-subtext">人员：{{ order.assignedWorker }}</div>
                    <div v-if="order.workerContact" class="order-subtext">联系方式：{{ order.workerContact }}</div>
                  </td>
                  <td>
                    <div class="order-subtext">{{ order.progressNote || '等待家政公司更新' }}</div>
                    <div v-if="order.specialRequest" class="order-subtext highlight">需求：{{ order.specialRequest }}</div>
                  </td>
                  <td>
                    <div v-if="order.refundReason" class="order-subtext">{{ order.refundReason }}</div>
                    <div v-else class="order-subtext muted">—</div>
                  </td>
                  <td>
                    <div v-if="order.refundResponse" class="order-subtext">
                      {{ order.refundResponse }}
                      <template v-if="order.handledBy">（{{ order.handledBy }}）</template>
                    </div>
                    <div v-else class="order-subtext muted">—</div>
                  </td>
                  <td class="table-actions">
                    <button
                      type="button"
                      class="link-button"
                      @click="jumpToMessages(order.id)"
                    >
                      去沟通
                    </button>
                    <button
                      type="button"
                      class="link-button"
                      :disabled="ordersLoading || confirmingOrderFlags[order.id] || !canConfirmOrder(order)"
                      @click="handleConfirmOrder(order)"
                    >
                      {{ order.userConfirmed ? '已确认' : confirmingOrderFlags[order.id] ? '确认中…' : '确认订单' }}
                    </button>
                    <button
                      v-if="canRequestRefund(order)"
                      type="button"
                      class="link-button"
                      @click="handleRequestRefund(order)"
                    >
                      申请退款
                    </button>
                    <button
                      type="button"
                      class="link-button danger"
                      :disabled="ordersLoading || !canDeleteOrder(order)"
                      :title="canDeleteOrder(order) ? '' : '仅已完成或已退款的订单可以删除'"
                      @click="handleDeleteOrder(order)"
                    >
                      删除订单
                    </button>
                  </td>
                </tr>
                <tr v-if="ordersLoading">
                  <td colspan="8" class="empty-row">订单加载中…</td>
                </tr>
                <tr v-else-if="!visibleOrders.length">
                  <td colspan="8" class="empty-row">
                    <span v-if="hasOrderFilter">没有找到匹配的订单，请尝试调整关键词。</span>
                    <span v-else>您还没有订单，先去选择一个服务吧。</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="upcomingOrders.length" class="schedule-timeline">
            <h3>我的预约日程</h3>
            <ul>
              <li v-for="item in upcomingOrders" :key="item.id">
                <div>
                  <strong>{{ formatDateTime(item.scheduledAt) }}</strong>
                  <span> · {{ item.serviceName }}（{{ item.companyName }}）</span>
                </div>
                <p>{{ item.progressNote || '等待服务开始' }}</p>
              </li>
            </ul>
          </div>
        </section>

        <section v-else-if="activeSection === 'wallet'" class="panel">
          <header class="panel-header">
            <div>
              <h2>钱包充值与积分兑换</h2>
              <p>支持余额充值与积分兑换，兑换比例 5:1。</p>
            </div>
          </header>
          <div class="wallet-grid">
            <form class="wallet-card" @submit.prevent="submitRecharge">
              <h3>快捷充值</h3>
              <p>充值金额将实时到账，用于预约服务。</p>
              <input v-model.number="walletForm.amount" type="number" min="0.01" step="0.01" placeholder="充值金额" />
              <button type="submit" class="primary-button" :disabled="walletSaving">
                {{ walletSaving ? '充值中…' : '确认充值' }}
              </button>
            </form>
            <form class="wallet-card" @submit.prevent="submitExchange">
              <h3>积分兑换</h3>
              <p>每 5 积分可兑换 1 元余额，积分需为 5 的倍数。</p>
              <input v-model.number="exchangeForm.points" type="number" min="5" step="5" placeholder="兑换积分" />
              <button type="submit" class="primary-button" :disabled="exchangeSaving">
                {{ exchangeSaving ? '兑换中…' : '确认兑换' }}
              </button>
            </form>
          </div>
        </section>

        <UserMessagingPanel
          v-else-if="activeSection === 'messages'"
          class="panel immersive-panel"
          :conversations="conversations"
          :active-conversation-id="activeConversationId"
          :messages="messages"
          :loading-conversations="conversationsLoading"
          :loading-messages="messagesLoading"
          :sending="messageSending"
          @refresh-conversations="loadConversations"
          @refresh-messages="refreshMessages"
          @select-conversation="selectConversation"
          @send-message="handleSendMessage"
        />

        <section v-else class="panel">
          <header class="panel-header">
            <h2>提交服务评价</h2>
            <p>感谢分享真实感受，平台会及时反馈给家政公司。</p>
          </header>
          <div class="review-toolbar">
            <div class="review-search">
              <label class="visually-hidden" for="user-review-search">搜索评价或服务</label>
              <input
                id="user-review-search"
                v-model="reviewSearch"
                class="search-input"
                type="search"
                :disabled="userReviewsLoading"
                placeholder="搜索服务名称、公司或评价内容"
              />
            </div>
            <div class="review-tabs" role="tablist" aria-label="评价筛选">
              <button
                type="button"
                class="tab-button"
                :class="{ active: reviewTab === 'unreviewed' }"
                :aria-pressed="reviewTab === 'unreviewed'"
                @click="setReviewTab('unreviewed')"
              >
                未评价
              </button>
              <button
                type="button"
                class="tab-button"
                :class="{ active: reviewTab === 'reviewed' }"
                :aria-pressed="reviewTab === 'reviewed'"
                @click="setReviewTab('reviewed')"
              >
                已评价
              </button>
            </div>
          </div>

          <div v-if="reviewTab === 'unreviewed'" class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>服务</th>
                  <th>最近预约</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="service in pendingReviewServices" :key="service.id">
                  <td>
                    <strong>{{ service.name }}</strong>
                    <div class="order-subtext muted">服务公司：{{ service.companyName }}</div>
                  </td>
                  <td>{{ service.lastScheduledAt ? formatDateTime(service.lastScheduledAt) : '—' }}</td>
                  <td class="table-actions actions-inline">
                    <button type="button" class="primary-button" @click="openReviewModal(service)">
                      去评价
                    </button>
                  </td>
                </tr>
                <tr v-if="ordersLoading || userReviewsLoading">
                  <td colspan="3" class="empty-row">数据加载中…</td>
                </tr>
                <tr v-else-if="!pendingReviewServices.length">
                  <td colspan="3" class="empty-row">
                    <span v-if="hasReviewSearch">没有匹配的待评价服务。</span>
                    <span v-else>暂无待评价的服务，完成订单后即可发布评价。</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-else class="review-section">
            <div class="review-actions">
              <button
                type="button"
                class="secondary-button danger"
                :disabled="!hasUserReviewSelection || userReviewsLoading"
                @click="handleBulkDeleteUserReviews"
              >
                删除选中<span v-if="selectedUserReviewCount">（{{ selectedUserReviewCount }}）</span>
              </button>
              <button type="button" class="secondary-button" :disabled="userReviewsLoading" @click="loadUserReviews">
                刷新评价
              </button>
            </div>
            <div class="table-wrapper">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="table-checkbox">
                      <input
                        type="checkbox"
                        :checked="allVisibleUserReviewsSelected"
                        :disabled="userReviewsLoading || !visibleUserReviews.length"
                        @change="toggleSelectAllUserReviews(($event.target as HTMLInputElement).checked)"
                        aria-label="全选评价"
                      />
                    </th>
                    <th>服务</th>
                    <th>评分与内容</th>
                    <th class="table-actions">时间与操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in visibleUserReviews" :key="item.id">
                    <td class="table-checkbox">
                      <input
                        type="checkbox"
                        :checked="selectedUserReviewIds.has(item.id)"
                        :disabled="userReviewsLoading"
                        @change="toggleUserReviewSelection(item.id, ($event.target as HTMLInputElement).checked)"
                        :aria-label="`选择评价 ${item.serviceName}`"
                      />
                    </td>
                    <td>
                      <div class="review-service">
                        <strong>{{ item.serviceName }}</strong>
                      </div>
                    </td>
                    <td>
                      <div class="review-rating">评分：{{ item.rating }} 分</div>
                      <p class="review-text">{{ item.content || '暂无评价内容' }}</p>
                    </td>
                    <td class="table-actions actions-inline">
                      <span class="order-subtext">发表于 {{ formatDateTime(item.createdAt) }}</span>
                      <button
                        type="button"
                        class="link-button danger"
                        :disabled="userReviewsLoading"
                        @click="handleDeleteUserReview(item)"
                      >
                        删除
                      </button>
                    </td>
                  </tr>
                  <tr v-if="userReviewsLoading">
                    <td colspan="4" class="empty-row">评价加载中…</td>
                  </tr>
                  <tr v-else-if="!visibleUserReviews.length">
                    <td colspan="4" class="empty-row">
                      <span v-if="hasReviewSearch">没有找到相关评价，尝试调整搜索关键词。</span>
                      <span v-else>暂无评价记录，完成订单后即可发布评价。</span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div
            v-if="reviewModalVisible"
            class="review-modal-backdrop"
            role="dialog"
            aria-modal="true"
            :aria-label="reviewModalService ? `为 ${reviewModalService.name} 提交评价` : '提交评价'"
          >
            <div class="review-modal">
              <header class="review-modal-header">
                <h3>提交评价</h3>
                <p v-if="reviewModalService" class="review-modal-subtitle">
                  {{ reviewModalService.name }} · {{ reviewModalService.companyName }}
                </p>
              </header>
              <form class="review-modal-form" @submit.prevent="submitPendingReview">
                <div class="form-field">
                  <label for="modal-review-rating">评分（1-5分）</label>
                  <input
                    id="modal-review-rating"
                    v-model.number="reviewModalForm.rating"
                    type="number"
                    min="1"
                    max="5"
                    required
                  />
                </div>
                <div class="form-field">
                  <label for="modal-review-content">评价内容</label>
                  <textarea
                    id="modal-review-content"
                    v-model="reviewModalForm.content"
                    rows="4"
                    placeholder="描述您的服务体验"
                    required
                  ></textarea>
                </div>
                <div class="modal-actions">
                  <button type="button" class="secondary-button" @click="closeReviewModal" :disabled="reviewSubmitting">
                    取消
                  </button>
                  <button type="submit" class="primary-button" :disabled="reviewSubmitting">
                    {{ reviewSubmitting ? '提交中…' : '提交评价' }}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

import { AUTH_ACCOUNT_KEY, AUTH_ROLE_KEY, AUTH_TOKEN_KEY } from '../constants/auth'
import {
  addUserFavorite,
  fetchCurrentAccount,
  createUserOrder,
  exchangeUserPoints,
  fetchDashboardAnnouncements,
  fetchDashboardCarousels,
  fetchDashboardTips,
  fetchServiceCategories,
  fetchUserConversations,
  confirmUserOrder,
  fetchUserFavorites,
  fetchUserMessages,
  fetchUserOrders,
  fetchUserReviews,
  fetchUserServices,
  removeUserFavorite,
  markUserConversationRead,
  fetchServiceSlotAvailability,
  rechargeUserWallet,
  requestUserRefund,
  sendUserMessage,
  submitUserReview,
  deleteUserOrder,
  deleteUserOrders,
  deleteUserReview,
  deleteUserReviews,
  type AccountProfileItem,
  type CreateOrderPayload,
  type CompanyMessageItem,
  type CompanyMessagePayload,
  type DashboardAnnouncementItem,
  type DashboardCarouselItem,
  type DashboardTipItem,
  type HousekeepServiceItem,
  type ServiceCategoryItem,
  type ServiceFavoriteItem,
  type TimeSlotAvailabilityItem,
  type ServiceOrderItem,
  type ServiceReviewItem,
  type UserConversationItem,
} from '../services/dashboard'

import UserMessagingPanel from '../pages/user/UserMessagingPanel.vue'
import AccountProfileEditor from '../components/AccountProfileEditor.vue'

interface SectionMeta {
  key: SectionKey
  icon: string
  label: string
}

interface ReviewableServiceSummary {
  id: number
  name: string
  companyName: string
  lastScheduledAt: string | null
}

type SectionKey =
  | 'profile'
  | 'discover'
  | 'services'
  | 'favorites'
  | 'orders'
  | 'wallet'
  | 'messages'
  | 'reviews'

type PaymentMethod = 'wechat' | 'alipay'

type PendingPaymentAction =
  | { kind: 'order'; payload: CreateOrderPayload }
  | { kind: 'recharge'; payload: { amount: number } }

const router = useRouter()
const account = ref<AccountProfileItem | null>(null)

const FALLBACK_AVATAR =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg=='
const allServices = ref<HousekeepServiceItem[]>([])
const services = ref<HousekeepServiceItem[]>([])
const serviceCategories = ref<ServiceCategoryItem[]>([])
const serviceCategoriesLoading = ref(false)
const activeServiceCategoryId = ref<number | null>(null)
const serviceSearch = ref('')
let serviceSearchTimer: ReturnType<typeof setTimeout> | null = null
const orders = ref<ServiceOrderItem[]>([])
const ordersLoading = ref(false)
const orderSearch = ref('')
const selectedOrderIds = ref<Set<number>>(new Set())
const userReviews = ref<ServiceReviewItem[]>([])
const userReviewsLoading = ref(false)
const reviewSearch = ref('')
const selectedUserReviewIds = ref<Set<number>>(new Set())
const carousels = ref<DashboardCarouselItem[]>([])
const carouselIndex = ref(0)
const tips = ref<DashboardTipItem[]>([])
const announcements = ref<DashboardAnnouncementItem[]>([])
const favorites = ref<ServiceFavoriteItem[]>([])
const favoritesLoading = ref(false)
const favoriteSearch = ref('')
const confirmingOrderFlags = reactive<Record<number, boolean>>({})
const conversations = ref<UserConversationItem[]>([])
const messages = ref<CompanyMessageItem[]>([])
const conversationsLoading = ref(false)
const messagesLoading = ref(false)
const messageSending = ref(false)

const activeConversationId = ref<number | null>(null)

const bookingDialogVisible = ref(false)
const timeSlotAvailability = ref<Record<string, TimeSlotAvailabilityItem>>({})
const timeSlotAvailabilityLoading = ref(false)

const formatDateInputValue = (date: Date) => {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

const getTodayInputValue = () => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return formatDateInputValue(today)
}

const getTomorrowInputValue = () => {
  const tomorrow = new Date()
  tomorrow.setHours(0, 0, 0, 0)
  tomorrow.setDate(tomorrow.getDate() + 1)
  return formatDateInputValue(tomorrow)
}

const bookingDateLimits = computed(() => ({
  min: getTodayInputValue(),
  max: getTomorrowInputValue(),
}))

interface BookingTimeSlot {
  key: string
  label: string
  startHour: number
  startMinute: number
  endHour: number
  endMinute: number
}

const BOOKING_TIME_SLOTS = [
  { key: '08-10', label: '08:00-10:00', startHour: 8, startMinute: 0, endHour: 10, endMinute: 0 },
  { key: '11-13', label: '11:00-13:00', startHour: 11, startMinute: 0, endHour: 13, endMinute: 0 },
  { key: '14-16', label: '14:00-16:00', startHour: 14, startMinute: 0, endHour: 16, endMinute: 0 },
  { key: '17-19', label: '17:00-19:00', startHour: 17, startMinute: 0, endHour: 19, endMinute: 0 },
  { key: '20-22', label: '20:00-22:00', startHour: 20, startMinute: 0, endHour: 22, endMinute: 0 },
] as const satisfies readonly BookingTimeSlot[]

type BookingTimeSlotKey = (typeof BOOKING_TIME_SLOTS)[number]['key']

const bookingForm = reactive<{
  service: HousekeepServiceItem | null
  selectedDate: string
  timeSlotKey: BookingTimeSlotKey | ''
  specialRequest: string
  serviceAddress: string
}>({
  service: null,
  selectedDate: getTodayInputValue(),
  timeSlotKey: '',
  specialRequest: '',
  serviceAddress: '',
})

const resolveBookingDate = (value: string) => {
  if (!value) {
    return null
  }
  const parsed = new Date(value)
  if (Number.isNaN(parsed.getTime())) {
    return null
  }
  parsed.setHours(0, 0, 0, 0)
  const min = new Date(bookingDateLimits.value.min)
  const max = new Date(bookingDateLimits.value.max)
  min.setHours(0, 0, 0, 0)
  max.setHours(0, 0, 0, 0)
  if (parsed < min || parsed > max) {
    return null
  }
  return parsed
}

const bookingScheduledAt = computed(() => {
  const slot = BOOKING_TIME_SLOTS.find((item) => item.key === bookingForm.timeSlotKey)
  if (!slot) {
    return ''
  }

  const selectedDate = resolveBookingDate(bookingForm.selectedDate)
  if (!selectedDate) {
    return ''
  }
  const scheduled = new Date(selectedDate)
  scheduled.setHours(slot.startHour, slot.startMinute, 0, 0)
  return scheduled.toISOString()
})

const refreshSlotAvailability = async () => {
  if (!bookingForm.service) {
    timeSlotAvailability.value = {}
    return
  }
  const selectedDate = resolveBookingDate(bookingForm.selectedDate)
  if (!selectedDate) {
    timeSlotAvailability.value = {}
    return
  }
  timeSlotAvailabilityLoading.value = true
  try {
    const items = await fetchServiceSlotAvailability(bookingForm.service.id, bookingForm.selectedDate)
    const next: Record<string, TimeSlotAvailabilityItem> = {}
    items.forEach((item) => {
      if (item?.slotKey) {
        next[item.slotKey] = item
      }
    })
    timeSlotAvailability.value = next
  } catch (error) {
    console.error(error)
    timeSlotAvailability.value = {}
  } finally {
    timeSlotAvailabilityLoading.value = false
  }
}

watch(
  () => bookingForm.selectedDate,
  () => {
    if (bookingDialogVisible.value && bookingForm.service) {
      refreshSlotAvailability()
    }
  },
)

const paymentDialogVisible = ref(false)
const paymentProcessing = ref(false)
const paymentMessage = ref('')
const paymentError = ref('')
const pendingPaymentAction = ref<PendingPaymentAction | null>(null)
const paymentServiceName = ref('')
const paymentCompanyName = ref('')
const paymentAmount = ref<number | null>(null)
const paymentForm = reactive<{ account: string; method: PaymentMethod }>({
  account: '',
  method: 'wechat',
})

const reviewTab = ref<'reviewed' | 'unreviewed'>('unreviewed')
const reviewModalVisible = ref(false)
const reviewModalService = ref<ReviewableServiceSummary | null>(null)
const reviewModalForm = reactive<{ rating: number; content: string }>({ rating: 5, content: '' })
const reviewSubmitting = ref(false)

const walletForm = reactive<{ amount: number | null }>({ amount: null })
const walletSaving = ref(false)
const exchangeForm = reactive<{ points: number | null }>({ points: null })
const exchangeSaving = ref(false)

const discoverLoading = ref(false)

const currentCarousel = computed(() => {
  if (!carousels.value.length) {
    return null
  }
  const length = carousels.value.length
  const index = ((carouselIndex.value % length) + length) % length
  return carousels.value[index]
})

const sections: SectionMeta[] = [
  { key: 'profile', icon: '👤', label: '个人资料' },
  { key: 'discover', icon: '🌟', label: '精选推荐' },
  { key: 'services', icon: '🧹', label: '选择服务' },
  { key: 'favorites', icon: '❤️', label: '我的收藏' },
  { key: 'orders', icon: '📋', label: '我的订单' },
  { key: 'wallet', icon: '💳', label: '充值/兑换' },
  { key: 'messages', icon: '💬', label: '在线沟通' },
  { key: 'reviews', icon: '⭐', label: '评价服务' },
]

const activeSection = ref<SectionKey>('discover')

const displayName = computed(
  () =>
    account.value?.displayName ||
    account.value?.username ||
    sessionStorage.getItem(AUTH_ACCOUNT_KEY) ||
    '用户',
)
const balanceText = computed(() => (account.value ? account.value.balance.toFixed(2) : '0.00'))
const loyaltyText = computed(() => (account.value ? account.value.loyaltyPoints.toString() : '0'))

const avatarSrc = computed(
  () => account.value?.avatarBase64 || FALLBACK_AVATAR,
)

const favoriteOverrides = reactive<Record<number, boolean>>({})
const favoriteLoadingIds = ref<Set<number>>(new Set())

const favoriteIdSet = computed(() => new Set(favorites.value.map((item) => item.serviceId)))
const isServiceFavorite = (serviceId: number) => {
  const override = favoriteOverrides[serviceId]
  if (override !== undefined) {
    return override
  }
  return favoriteIdSet.value.has(serviceId)
}
const hasServiceFilter = computed(
  () => serviceSearch.value.trim().length > 0 || activeServiceCategoryId.value !== null,
)
const activeServiceCategoryName = computed(() => {
  if (activeServiceCategoryId.value === null) {
    return '全部'
  }
  const target = serviceCategories.value.find((item) => item.id === activeServiceCategoryId.value)
  return target ? target.name : '全部'
})
const favoritesCount = computed(() => favorites.value.length)

const visibleFavorites = computed(() => {
  const keyword = favoriteSearch.value.trim()
  if (!keyword) {
    return favorites.value
  }
  return favorites.value.filter((favorite) => matchesFavoriteSearch(favorite, keyword))
})

const hasFavoriteFilter = computed(() => favoriteSearch.value.trim().length > 0)

const normalizeUserSearchValue = (value: unknown) => {
  if (value == null) {
    return ''
  }
  return String(value).toLowerCase()
}

const matchesUserOrderSearch = (order: ServiceOrderItem, keyword: string) => {
  if (!keyword) {
    return true
  }
  const target = keyword.toLowerCase()
  const fields = [
    order.serviceName,
    order.companyName,
    order.contact,
    order.username,
    order.specialRequest,
    order.serviceAddress,
    order.progressNote,
    order.assignedWorker,
    order.workerContact,
    order.refundReason,
  ]
  return fields.some((field) => normalizeUserSearchValue(field).includes(target))
}

const matchesFavoriteSearch = (favorite: ServiceFavoriteItem, keyword: string) => {
  if (!keyword) {
    return true
  }
  const target = keyword.toLowerCase()
  const fields = [favorite.serviceName, favorite.companyName, favorite.username]
  return fields.some((field) => normalizeUserSearchValue(field).includes(target))
}

const canDeleteOrder = (order: ServiceOrderItem) =>
  order.status === 'COMPLETED' || order.status === 'REFUND_APPROVED'

const matchesUserReviewSearch = (review: ServiceReviewItem, keyword: string) => {
  if (!keyword) {
    return true
  }
  const target = keyword.toLowerCase()
  const fields = [review.serviceName, String(review.rating), review.content]
  return fields.some((field) => normalizeUserSearchValue(field).includes(target))
}

const visibleOrders = computed(() => {
  const keyword = orderSearch.value.trim()
  if (!keyword) {
    return orders.value
  }
  return orders.value.filter((order) => matchesUserOrderSearch(order, keyword))
})

const hasOrderFilter = computed(() => orderSearch.value.trim().length > 0)
const deletableOrders = computed(() => orders.value.filter((order) => canDeleteOrder(order)))
const deletableOrderIds = computed(() => new Set(deletableOrders.value.map((item) => item.id)))
const deletableVisibleOrders = computed(() => visibleOrders.value.filter((order) => canDeleteOrder(order)))
const allVisibleOrdersSelected = computed(
  () =>
    deletableVisibleOrders.value.length > 0 &&
    deletableVisibleOrders.value.every((item) => selectedOrderIds.value.has(item.id)),
)

const selectedOrderCount = computed(() => selectedOrderIds.value.size)
const hasOrderSelection = computed(() => selectedOrderIds.value.size > 0)

const visibleUserReviews = computed(() => {
  const keyword = reviewSearch.value.trim()
  if (!keyword) {
    return userReviews.value
  }
  return userReviews.value.filter((item) => matchesUserReviewSearch(item, keyword))
})

const hasReviewSearch = computed(() => reviewSearch.value.trim().length > 0)

const allVisibleUserReviewsSelected = computed(
  () =>
    visibleUserReviews.value.length > 0 &&
    visibleUserReviews.value.every((item) => selectedUserReviewIds.value.has(item.id)),
)

const selectedUserReviewCount = computed(() => selectedUserReviewIds.value.size)
const hasUserReviewSelection = computed(() => selectedUserReviewIds.value.size > 0)

const orderStats = computed(() => {
  const total = visibleOrders.value.length
  const awaiting = visibleOrders.value.filter((order) => order.status === 'SCHEDULED' || order.status === 'PENDING').length
  const inProgress = visibleOrders.value.filter((order) => order.status === 'IN_PROGRESS').length
  const refunding = visibleOrders.value.filter((order) => order.status === 'REFUND_REQUESTED').length
  const completed = visibleOrders.value.filter((order) => order.status === 'COMPLETED').length
  return {
    total,
    awaiting,
    inProgress,
    refunding,
    completed,
  }
})

const upcomingOrders = computed(() => {
  return orders.value
    .filter((order) => ['SCHEDULED', 'PENDING', 'IN_PROGRESS'].includes(order.status))
    .slice()
    .sort((a, b) => new Date(a.scheduledAt).getTime() - new Date(b.scheduledAt).getTime())
    .slice(0, 5)
})

const reviewedServiceIds = computed(() => new Set(userReviews.value.map((item) => item.serviceId)))

const reviewableServices = computed<ReviewableServiceSummary[]>(() => {
  const summaryMap = new Map<number, ReviewableServiceSummary>()
  orders.value.forEach((order) => {
    const lastScheduledAt = order.scheduledAt ?? null
    const existing = summaryMap.get(order.serviceId)
    if (!existing) {
      summaryMap.set(order.serviceId, {
        id: order.serviceId,
        name: order.serviceName,
        companyName: order.companyName,
        lastScheduledAt,
      })
      return
    }
    const existingTime = existing.lastScheduledAt ? new Date(existing.lastScheduledAt).getTime() : -Infinity
    const orderTime = lastScheduledAt ? new Date(lastScheduledAt).getTime() : -Infinity
    if (orderTime > existingTime) {
      existing.lastScheduledAt = lastScheduledAt
    }
  })
  return Array.from(summaryMap.values())
})

const pendingReviewServices = computed(() => {
  const keyword = reviewSearch.value.trim().toLowerCase()
  const reviewedIds = reviewedServiceIds.value
  return reviewableServices.value
    .filter((service) => !reviewedIds.has(service.id))
    .filter((service) => {
      if (!keyword) {
        return true
      }
      return [service.name, service.companyName].some((field) => field.toLowerCase().includes(keyword))
    })
})

const loadAccount = async () => {
  try {
    account.value = await fetchCurrentAccount()
  } catch (error) {
    console.error(error)
  }
}

const handleProfileUpdated = (payload: AccountProfileItem) => {
  account.value = payload
}

const serviceCoverStyle = (service: HousekeepServiceItem) => {
  if (service.imageBase64) {
    return {
      backgroundImage: `url("${service.imageBase64}")`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
    }
  }
  return {
    backgroundImage:
      'linear-gradient(135deg, rgba(59, 130, 246, 0.08), rgba(16, 185, 129, 0.12))',
  }
}

const applyServiceFilter = () => {
  const keyword = serviceSearch.value.trim().toLowerCase()
  const categoryId = activeServiceCategoryId.value

  let next = allServices.value
  if (categoryId !== null) {
    next = next.filter((item) => item.categoryId === categoryId)
  }

  if (!keyword) {
    services.value = next.slice()
    return
  }

  services.value = next.filter((item) => {
    const fields = [
      item.name,
      item.unit,
      item.contact,
      item.serviceTime,
      item.companyName,
      item.description || '',
    ]
    return fields.some((field) => field.toLowerCase().includes(keyword))
  })
}

const pruneOrderSelection = () => {
  if (!selectedOrderIds.value.size) {
    return
  }
  const allowedIds = deletableOrderIds.value
  let changed = false
  const next = new Set<number>()
  selectedOrderIds.value.forEach((id) => {
    if (allowedIds.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedOrderIds.value = next
  }
}

const toggleOrderSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedOrderIds.value)
  if (checked) {
    if (deletableOrderIds.value.has(id)) {
      next.add(id)
    }
  } else {
    next.delete(id)
  }
  selectedOrderIds.value = next
}

const toggleSelectAllOrders = (checked: boolean) => {
  if (!checked) {
    selectedOrderIds.value = new Set()
    return
  }
  const next = new Set(selectedOrderIds.value)
  deletableVisibleOrders.value.forEach((item) => next.add(item.id))
  selectedOrderIds.value = next
}

const clearOrderSelection = () => {
  selectedOrderIds.value = new Set()
}

const pruneUserReviewSelection = () => {
  if (!selectedUserReviewIds.value.size) {
    return
  }
  const visibleIds = new Set(userReviews.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedUserReviewIds.value.forEach((id) => {
    if (visibleIds.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedUserReviewIds.value = next
  }
}

const toggleUserReviewSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedUserReviewIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedUserReviewIds.value = next
}

const toggleSelectAllUserReviews = (checked: boolean) => {
  if (!checked) {
    selectedUserReviewIds.value = new Set()
    return
  }
  const next = new Set(selectedUserReviewIds.value)
  visibleUserReviews.value.forEach((item) => next.add(item.id))
  selectedUserReviewIds.value = next
}

const clearUserReviewSelection = () => {
  selectedUserReviewIds.value = new Set()
}

const loadServices = async () => {
  try {
    const result = await fetchUserServices()
    allServices.value = result.filter((item) => (item.status ?? 'PENDING').toUpperCase() === 'APPROVED')
    applyServiceFilter()
  } catch (error) {
    console.error(error)
    allServices.value = []
    services.value = []
  }
}

const loadServiceCategories = async () => {
  serviceCategoriesLoading.value = true
  try {
    serviceCategories.value = await fetchServiceCategories()
  } catch (error) {
    console.error(error)
    serviceCategories.value = []
  } finally {
    serviceCategoriesLoading.value = false
  }
}

const resetServiceFilters = () => {
  serviceSearch.value = ''
  activeServiceCategoryId.value = null
  applyServiceFilter()
}

const handleSelectServiceCategory = (categoryId: number | null) => {
  if (categoryId === null) {
    activeServiceCategoryId.value = null
    return
  }
  if (activeServiceCategoryId.value === categoryId) {
    return
  }
  activeServiceCategoryId.value = categoryId
}

const loadOrders = async () => {
  ordersLoading.value = true
  try {
    orders.value = await fetchUserOrders()
    pruneOrderSelection()
  } catch (error) {
    console.error(error)
  } finally {
    ordersLoading.value = false
  }
}

const loadFavorites = async () => {
  favoritesLoading.value = true
  try {
    favorites.value = await fetchUserFavorites()
  } catch (error) {
    console.error(error)
  } finally {
    favoritesLoading.value = false
  }
}

const ensureServicesLoaded = async () => {
  if (!allServices.value.length) {
    await loadServices()
  }
}

const showPreviousCarousel = () => {
  if (carousels.value.length <= 1) {
    return
  }
  carouselIndex.value = (carouselIndex.value - 1 + carousels.value.length) % carousels.value.length
}

const showNextCarousel = () => {
  if (carousels.value.length <= 1) {
    return
  }
  carouselIndex.value = (carouselIndex.value + 1) % carousels.value.length
}

watch(carousels, (items) => {
  if (!items.length) {
    carouselIndex.value = 0
    return
  }
  carouselIndex.value = carouselIndex.value % items.length
})

watch(serviceSearch, () => {
  if (serviceSearchTimer) {
    clearTimeout(serviceSearchTimer)
  }
  serviceSearchTimer = setTimeout(() => {
    applyServiceFilter()
    serviceSearchTimer = null
  }, 300)
})

watch(activeServiceCategoryId, () => {
  applyServiceFilter()
})

watch(serviceCategories, (next) => {
  if (activeServiceCategoryId.value === null) {
    return
  }
  const exists = next.some((item) => item.id === activeServiceCategoryId.value)
  if (!exists) {
    activeServiceCategoryId.value = null
  }
})

watch(orders, pruneOrderSelection)
watch(visibleOrders, pruneOrderSelection)
watch(userReviews, pruneUserReviewSelection)
watch(visibleUserReviews, pruneUserReviewSelection)

const loadDiscover = async () => {
  discoverLoading.value = true
  try {
    const [carouselData, tipData, announcementData] = await Promise.all([
      fetchDashboardCarousels(),
      fetchDashboardTips(),
      fetchDashboardAnnouncements(),
    ])
    carousels.value = carouselData
    carouselIndex.value = 0
    tips.value = tipData
    announcements.value = announcementData
  } catch (error) {
    console.error(error)
  } finally {
    discoverLoading.value = false
  }
}

const loadUserReviews = async () => {
  userReviewsLoading.value = true
  try {
    userReviews.value = await fetchUserReviews()
    pruneUserReviewSelection()
  } catch (error) {
    console.error(error)
  } finally {
    userReviewsLoading.value = false
  }
}

const refreshDiscover = () => {
  loadDiscover()
}

const switchSection = (key: SectionKey) => {
  activeSection.value = key
  if (key === 'orders') {
    loadOrders()
    loadAccount()
  } else if (key === 'profile') {
    loadAccount()
  } else if (key === 'messages') {
    loadConversations()
  } else if (key === 'favorites') {
    loadFavorites()
  } else if (key === 'wallet') {
    loadAccount()
  } else if (key === 'reviews') {
    loadUserReviews()
  }
}

const logout = () => {
  sessionStorage.removeItem(AUTH_TOKEN_KEY)
  sessionStorage.removeItem(AUTH_ACCOUNT_KEY)
  sessionStorage.removeItem(AUTH_ROLE_KEY)
  router.push({ name: 'login' })
}

const handleSelectService = (service: HousekeepServiceItem) => {
  bookingForm.service = service
  bookingForm.selectedDate = bookingDateLimits.value.min
  bookingForm.timeSlotKey = ''
  bookingForm.specialRequest = ''
  bookingForm.serviceAddress = account.value?.contactAddress || ''
  bookingDialogVisible.value = true
  refreshSlotAvailability()
}

const closeBooking = () => {
  bookingDialogVisible.value = false
  bookingForm.selectedDate = bookingDateLimits.value.min
  bookingForm.serviceAddress = ''
  bookingForm.timeSlotKey = ''
  bookingForm.specialRequest = ''
  timeSlotAvailability.value = {}
}

const resetPaymentState = () => {
  paymentMessage.value = ''
  paymentError.value = ''
  pendingPaymentAction.value = null
  paymentServiceName.value = ''
  paymentCompanyName.value = ''
  paymentAmount.value = null
  paymentForm.account = account.value?.username || account.value?.displayName || ''
  paymentForm.method = 'wechat'
}

const submitBooking = async () => {
  if (!bookingForm.service || !bookingForm.serviceAddress.trim()) {
    window.alert('请填写完整的预约信息')
    return
  }

  const scheduledAt = bookingScheduledAt.value
  if (!scheduledAt) {
    window.alert('请选择预约日期与时间段')
    return
  }

  const scheduledDate = new Date(scheduledAt)
  if (Number.isNaN(scheduledDate.getTime())) {
    window.alert('预约时间无效，请重新选择')
    return
  }

  const now = new Date()
  if (scheduledDate.getTime() < now.getTime()) {
    window.alert('预约时间需晚于当前时间，请重新选择时间段')
    return
  }

  const selectedSlot = BOOKING_TIME_SLOTS.find((item) => item.key === bookingForm.timeSlotKey)
  if (selectedSlot && isSlotUnavailableForSelectedDate(selectedSlot)) {
    window.alert('当前时间段暂无可用人员，请选择其他时间段或日期')
    return
  }

  resetPaymentState()
  pendingPaymentAction.value = {
    kind: 'order',
    payload: {
      serviceId: bookingForm.service.id,
      scheduledAt,
      specialRequest: bookingForm.specialRequest,
      serviceAddress: bookingForm.serviceAddress.trim(),
    },
  }
  paymentServiceName.value = bookingForm.service.name
  paymentCompanyName.value = bookingForm.service.companyName
  const paymentAmountValue = Number(bookingForm.service.price ?? 0)
  if (!Number.isFinite(paymentAmountValue) || paymentAmountValue <= 0) {
    window.alert('当前服务价格异常，请稍后再试。')
    return
  }
  paymentAmount.value = paymentAmountValue

  bookingDialogVisible.value = false
  paymentDialogVisible.value = true
}

const closePaymentDialog = () => {
  if (paymentProcessing.value) {
    return
  }
  paymentDialogVisible.value = false
  resetPaymentState()
}

const confirmPayment = async () => {
  if (paymentProcessing.value) {
    return
  }

  const accountInput = paymentForm.account.trim()
  if (!accountInput) {
    window.alert('请输入支付账号')
    return
  }

  if (!pendingPaymentAction.value) {
    paymentError.value = '当前没有待支付的事项，请重新操作。'
    return
  }

  paymentProcessing.value = true
  paymentError.value = ''
  paymentMessage.value = ''

  let lastActionKind: PendingPaymentAction['kind'] | null = null
  try {
    const action = pendingPaymentAction.value
    if (!action) {
      paymentError.value = '当前没有待支付的事项，请重新操作。'
      return
    }

    lastActionKind = action.kind

    if (action.kind === 'order') {
      const normalizedDate = new Date(action.payload.scheduledAt)
      if (Number.isNaN(normalizedDate.getTime())) {
        throw new Error('预约时间无效，请重新选择预约时间')
      }
      if (normalizedDate.getTime() < Date.now()) {
        pendingPaymentAction.value = null
        throw new Error('预约时间已过期，请重新选择预约时间')
      }
      const payload = {
        ...action.payload,
        scheduledAt: normalizedDate.toISOString(),
        specialRequest: action.payload.specialRequest?.trim() || undefined,
        serviceAddress: action.payload.serviceAddress?.trim() || undefined,
      }
      await createUserOrder(payload)
      await Promise.all([loadOrders(), loadAccount()])
      bookingForm.service = null
      bookingForm.selectedDate = bookingDateLimits.value.min
      bookingForm.timeSlotKey = ''
      bookingForm.specialRequest = ''
      bookingForm.serviceAddress = ''
      paymentMessage.value = `支付成功，已通过${paymentForm.method === 'wechat' ? '微信' : '支付宝'}创建订单。`
    } else {
      const amount = Number(action.payload.amount)
      if (!Number.isFinite(amount) || amount <= 0) {
        throw new Error('充值金额无效，请重新输入金额')
      }
      account.value = await rechargeUserWallet({ amount })
      walletForm.amount = null
      paymentMessage.value = `支付成功，钱包已通过${paymentForm.method === 'wechat' ? '微信' : '支付宝'}充值。`
    }
    pendingPaymentAction.value = null
    setTimeout(() => {
      closePaymentDialog()
    }, 600)
  } catch (orderError) {
    console.error(orderError)
    const fallbackMessage =
      lastActionKind === 'order' ? '下单失败，请确认预约信息后再试。' : '充值失败，请稍后再试。'
    const rawMessage = orderError instanceof Error ? orderError.message : fallbackMessage
    paymentError.value = rawMessage.includes('参数验证失败')
      ? lastActionKind === 'order'
        ? '预约信息未通过校验，请确保时间未过期且地址填写完整。'
        : '充值金额格式不符合要求，请输入最多两位小数的金额。'
      : rawMessage
  } finally {
    paymentProcessing.value = false
  }
}

const handleDeleteOrder = async (order: ServiceOrderItem) => {
  if (!canDeleteOrder(order)) {
    window.alert('仅已完成或已退款的订单可以删除')
    return
  }
  if (!window.confirm(`确定删除订单“${order.serviceName}”（${order.companyName}）吗？`)) {
    return
  }
  try {
    await deleteUserOrder(order.id)
    const next = new Set(selectedOrderIds.value)
    next.delete(order.id)
    selectedOrderIds.value = next
    await loadOrders()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除订单失败，请稍后再试。')
  }
}

const handleBulkDeleteOrders = async () => {
  if (!selectedOrderIds.value.size) {
    return
  }
  const invalid = Array.from(selectedOrderIds.value).filter((id) => !deletableOrderIds.value.has(id))
  if (invalid.length) {
    window.alert('仅已完成或已退款的订单可以删除，请重新选择。')
    pruneOrderSelection()
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedOrderIds.value.size} 个订单记录吗？`)) {
    return
  }
  try {
    await deleteUserOrders(Array.from(selectedOrderIds.value))
    clearOrderSelection()
    await loadOrders()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '批量删除失败，请稍后再试。')
  }
}

const canConfirmOrder = (order: ServiceOrderItem) =>
  !order.userConfirmed && (order.status === 'COMPLETED' || order.status === 'REFUND_REQUESTED')

const canRequestRefund = (order: ServiceOrderItem) => {
  return (
    order.status === 'IN_PROGRESS' ||
    order.status === 'SCHEDULED' ||
    order.status === 'COMPLETED'
  )
}

const handleRequestRefund = async (order: ServiceOrderItem) => {
  const reason = window.prompt('请输入退款原因：')
  if (!reason) return
  try {
    await requestUserRefund(order.id, { reason })
    await loadOrders()
    window.alert('退款申请已提交，等待管理员处理')
  } catch (error) {
    console.error(error)
  }
}

const handleConfirmOrder = async (order: ServiceOrderItem) => {
  if (order.userConfirmed) {
    return
  }
  if (!canConfirmOrder(order)) {
    window.alert('订单状态未满足确认条件，请等待服务完成或退款处理。')
    return
  }
  if (!window.confirm(`确认“${order.serviceName}”服务已完成并满意吗？确认后平台会通知管理员结算。`)) {
    return
  }
  confirmingOrderFlags[order.id] = true
  try {
    await confirmUserOrder(order.id)
    await loadOrders()
    window.alert('感谢确认，平台将尽快安排结算。')
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '确认失败，请稍后再试。')
  } finally {
    delete confirmingOrderFlags[order.id]
  }
}

const setFavoriteLoading = (serviceId: number, loading: boolean) => {
  const next = new Set(favoriteLoadingIds.value)
  if (loading) {
    next.add(serviceId)
  } else {
    next.delete(serviceId)
  }
  favoriteLoadingIds.value = next
}

const toggleFavorite = async (service: HousekeepServiceItem) => {
  const currentlyFavorite = isServiceFavorite(service.id)
  favoriteOverrides[service.id] = !currentlyFavorite
  setFavoriteLoading(service.id, true)
  try {
    if (currentlyFavorite) {
      await removeUserFavorite(service.id)
    } else {
      await addUserFavorite(service.id)
    }
    await loadFavorites()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '收藏操作失败，请稍后再试。')
  } finally {
    delete favoriteOverrides[service.id]
    setFavoriteLoading(service.id, false)
  }
}

const handleRemoveFavorite = async (favorite: ServiceFavoriteItem) => {
  if (!window.confirm(`确认取消收藏“${favorite.serviceName}”吗？`)) {
    return
  }
  try {
    await removeUserFavorite(favorite.serviceId)
    await loadFavorites()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '取消收藏失败，请稍后再试。')
  }
}

const viewFavoriteService = async (favorite: ServiceFavoriteItem) => {
  await ensureServicesLoaded()
  serviceSearch.value = favorite.serviceName
  activeSection.value = 'services'
}

const bookFavoriteService = async (favorite: ServiceFavoriteItem) => {
  await ensureServicesLoaded()
  let target = allServices.value.find((item) => item.id === favorite.serviceId)
  if (!target) {
    await loadServices()
    target = allServices.value.find((item) => item.id === favorite.serviceId)
  }
  if (!target) {
    window.alert('未找到该服务，可能已下架。')
    return
  }
  handleSelectService(target)
}

const setReviewTab = (tab: 'reviewed' | 'unreviewed') => {
  reviewTab.value = tab
  if (tab !== 'reviewed') {
    selectedUserReviewIds.value = new Set()
  }
}

const openReviewModal = (service: ReviewableServiceSummary) => {
  reviewModalService.value = service
  reviewModalForm.rating = 5
  reviewModalForm.content = ''
  reviewSubmitting.value = false
  reviewModalVisible.value = true
}

const closeReviewModal = () => {
  reviewModalVisible.value = false
  reviewModalService.value = null
  reviewModalForm.rating = 5
  reviewModalForm.content = ''
  reviewSubmitting.value = false
}

const submitPendingReview = async () => {
  if (!reviewModalService.value) {
    return
  }
  const rating = Math.max(1, Math.min(5, Number(reviewModalForm.rating)))
  const content = reviewModalForm.content.trim()
  if (!content) {
    window.alert('请填写评价内容')
    return
  }
  reviewSubmitting.value = true
  try {
    await submitUserReview({
      serviceId: reviewModalService.value.id,
      rating,
      content,
    })
    await loadUserReviews()
    reviewTab.value = 'reviewed'
    closeReviewModal()
    window.alert('评价提交成功')
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '提交评价失败，请稍后重试')
  } finally {
    reviewSubmitting.value = false
  }
}

const handleDeleteUserReview = async (item: ServiceReviewItem) => {
  if (!window.confirm(`确定删除对“${item.serviceName}”的这条评价吗？`)) {
    return
  }
  try {
    await deleteUserReview(item.id)
    const next = new Set(selectedUserReviewIds.value)
    next.delete(item.id)
    selectedUserReviewIds.value = next
    await loadUserReviews()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除评价失败，请稍后再试。')
  }
}

const handleBulkDeleteUserReviews = async () => {
  if (!selectedUserReviewIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedUserReviewIds.value.size} 条评价吗？`)) {
    return
  }
  try {
    await deleteUserReviews(Array.from(selectedUserReviewIds.value))
    clearUserReviewSelection()
    await loadUserReviews()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '批量删除评价失败，请稍后再试。')
  }
}

const submitRecharge = async () => {
  if (!walletForm.amount || walletForm.amount <= 0) {
    window.alert('请输入正确的充值金额')
    return
  }
  walletSaving.value = true
  resetPaymentState()
  const amount = Number(walletForm.amount)
  if (!Number.isFinite(amount) || amount <= 0) {
    window.alert('请输入正确的充值金额')
    walletSaving.value = false
    return
  }
  const normalizedAmount = Math.round(amount * 100) / 100
  if (normalizedAmount <= 0) {
    window.alert('请输入正确的充值金额')
    walletSaving.value = false
    return
  }
  pendingPaymentAction.value = { kind: 'recharge', payload: { amount: normalizedAmount } }
  paymentServiceName.value = '钱包充值'
  paymentCompanyName.value = '账户中心'
  paymentAmount.value = normalizedAmount

  paymentDialogVisible.value = true
  walletSaving.value = false
}

const submitExchange = async () => {
  if (!exchangeForm.points || exchangeForm.points <= 0 || exchangeForm.points % 5 !== 0) {
    window.alert('积分需为 5 的倍数')
    return
  }
  exchangeSaving.value = true
  try {
    account.value = await exchangeUserPoints({ points: exchangeForm.points })
    exchangeForm.points = null
    window.alert('积分兑换成功')
  } catch (error) {
    console.error(error)
  } finally {
    exchangeSaving.value = false
  }
}

const loadConversations = async () => {
  conversationsLoading.value = true
  try {
    conversations.value = await fetchUserConversations()
    const firstConversation = conversations.value[0]
    if (firstConversation && activeConversationId.value === null) {
      activeConversationId.value = firstConversation.orderId
      await refreshMessages()
    }
  } catch (error) {
    console.error(error)
  } finally {
    conversationsLoading.value = false
  }
}

const selectConversation = async (orderId: number) => {
  activeConversationId.value = orderId
  await Promise.all([refreshMessages(), markUserConversationRead(orderId)])
}

const refreshMessages = async () => {
  if (activeConversationId.value === null) return
  messagesLoading.value = true
  try {
    messages.value = await fetchUserMessages(activeConversationId.value)
  } catch (error) {
    console.error(error)
  } finally {
    messagesLoading.value = false
  }
}

const handleSendMessage = async (payload: CompanyMessagePayload) => {
  if (activeConversationId.value === null) return
  messageSending.value = true
  try {
    await sendUserMessage(activeConversationId.value, payload)
    await refreshMessages()
  } catch (error) {
    console.error(error)
  } finally {
    messageSending.value = false
  }
}

const jumpToMessages = (orderId: number) => {
  activeSection.value = 'messages'
  activeConversationId.value = orderId
  loadConversations().then(() => {
    selectConversation(orderId)
  })
}

const formatTimeText = (date: Date) => {
  const hours = `${date.getHours()}`.padStart(2, '0')
  const minutes = `${date.getMinutes()}`.padStart(2, '0')
  return `${hours}:${minutes}`
}

const isSlotPastForSelectedDate = (slot: BookingTimeSlot) => {
  const selectedDate = resolveBookingDate(bookingForm.selectedDate)
  if (!selectedDate) {
    return false
  }

  const today = new Date()
  const normalizedToday = new Date(today)
  normalizedToday.setHours(0, 0, 0, 0)
  if (normalizedToday.getTime() !== selectedDate.getTime()) {
    return false
  }

  const slotEnd = new Date(selectedDate)
  slotEnd.setHours(slot.endHour ?? slot.startHour, slot.endMinute ?? slot.startMinute, 0, 0)
  return slotEnd <= today
}

const isSlotUnavailableForSelectedDate = (slot: BookingTimeSlot) => {
  const selectedDate = resolveBookingDate(bookingForm.selectedDate)
  if (!selectedDate) {
    return false
  }

  if (isSlotPastForSelectedDate(slot)) {
    return true
  }

  const availability = timeSlotAvailability.value[slot.key]
  return Boolean(availability && availability.availableStaff <= 0)
}

const slotLabelWithAvailability = (slot: BookingTimeSlot) => {
  if (isSlotPastForSelectedDate(slot)) {
    return slot.label
  }
  const info = timeSlotAvailability.value[slot.key]
  const countText = info ? info.availableStaff : 0
  return `${slot.label}（空闲人员：${countText}个）`
}

const formatServiceWindow = (order: ServiceOrderItem) => {
  if (!order?.scheduledAt) {
    return '未提供'
  }
  const start = new Date(order.scheduledAt)
  if (Number.isNaN(start.getTime())) {
    return '未提供'
  }
  const matchedSlot = BOOKING_TIME_SLOTS.find(
    (slot) => slot.startHour === start.getHours() && slot.startMinute === start.getMinutes(),
  )
  if (matchedSlot) {
    return matchedSlot.label
  }
  const end = new Date(start.getTime() + 2 * 60 * 60 * 1000)
  return `${formatTimeText(start)}-${formatTimeText(end)}`
}

const formatAppointmentStart = (order: ServiceOrderItem) => {
  const timestamp = order?.createdAt || order?.scheduledAt
  if (!timestamp) {
    return '未提供'
  }
  const start = new Date(timestamp)
  if (Number.isNaN(start.getTime())) {
    return '未提供'
  }
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
  }).format(start)
}

const formatDateTime = (value: string) => {
  if (!value) {
    return '未提供'
  }
  const parsed = new Date(value)
  if (Number.isNaN(parsed.getTime())) {
    return '未提供'
  }
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
  }).format(parsed)
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

onMounted(async () => {
  await Promise.all([
    loadAccount(),
    loadServiceCategories(),
    loadServices(),
    loadOrders(),
    loadDiscover(),
    loadFavorites(),
    loadUserReviews(),
  ])
})

onUnmounted(() => {
  if (serviceSearchTimer) {
    clearTimeout(serviceSearchTimer)
    serviceSearchTimer = null
  }
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 32px clamp(16px, 5vw, 48px) 48px;
  position: relative;
  z-index: 0;
  color: var(--brand-text);
}

.dashboard::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.08) 0%, rgba(16, 185, 129, 0.05) 45%, rgba(59, 130, 246, 0.08) 100%);
  border-radius: 40px 40px 0 0;
  z-index: -1;
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

.dashboard-header {
  position: relative;
  border-radius: calc(var(--brand-radius) + 12px);
  padding: 28px clamp(20px, 4vw, 36px);
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.94) 0%, rgba(16, 185, 129, 0.9) 100%);
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  box-shadow: 0 28px 48px rgba(37, 99, 235, 0.24);
  overflow: hidden;
}

.dashboard-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(600px circle at 90% 10%, rgba(255, 255, 255, 0.18), transparent 60%);
  z-index: 0;
}

.dashboard-header > * {
  position: relative;
  z-index: 1;
}

.dashboard-title {
  margin: 0;
  font-size: clamp(28px, 2.5vw + 12px, 36px);
  font-weight: 700;
}

.dashboard-subtitle {
  margin-top: 8px;
  font-size: 15px;
  opacity: 0.85;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background: rgba(15, 23, 42, 0.25);
  border-radius: 999px;
  backdrop-filter: blur(12px);
}

.account-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.28);
  border: 2px solid rgba(255, 255, 255, 0.4);
}

.logout-button {
  padding: 8px 18px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  background: rgba(15, 23, 42, 0.28);
  color: #fff;
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;
}

.logout-button:hover {
  transform: translateY(-1px);
  background: rgba(15, 23, 42, 0.42);
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.25);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(170px, 1fr));
  gap: 18px;
}

.stat-card {
  padding: 20px;
  border-radius: calc(var(--brand-radius) + 4px);
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(14px);
  position: relative;
}

.stat-card.accent {
  background: linear-gradient(150deg, rgba(37, 99, 235, 0.14), rgba(37, 99, 235, 0.04));
  border-color: rgba(37, 99, 235, 0.22);
}

.stat-card.primary {
  background: linear-gradient(150deg, rgba(16, 185, 129, 0.16), rgba(16, 185, 129, 0.05));
  border-color: rgba(16, 185, 129, 0.24);
}

.stat-card.warning {
  background: linear-gradient(150deg, rgba(245, 158, 11, 0.16), rgba(251, 191, 36, 0.06));
  border-color: rgba(245, 158, 11, 0.24);
}

.stat-card.success {
  background: linear-gradient(150deg, rgba(37, 99, 235, 0.12), rgba(16, 185, 129, 0.06));
  border-color: rgba(37, 99, 235, 0.24);
}

.stat-label {
  font-size: 14px;
  color: var(--brand-text-muted);
  margin: 0 0 6px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
}

.stat-helper {
  margin-top: 10px;
  font-size: 13px;
  color: var(--brand-text-muted);
}

.dashboard-main {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 24px;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 999px;
  background: rgba(248, 250, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.28);
  color: var(--brand-text-muted);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.sidebar-item.active {
  color: var(--brand-success);
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.18), rgba(37, 99, 235, 0.08));
  border-color: rgba(16, 185, 129, 0.28);
  box-shadow: inset 0 0 0 1px rgba(16, 185, 129, 0.2), 0 18px 32px rgba(15, 23, 42, 0.12);
}

.sidebar-icon {
  font-size: 1.2rem;
}

.panel {
  padding: 28px 32px;
  border-radius: calc(var(--brand-radius) + 4px);
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(148, 163, 184, 0.18);
  backdrop-filter: blur(14px);
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.12);
}

.profile-panel {
  background: linear-gradient(145deg, rgba(16, 185, 129, 0.12), rgba(59, 130, 246, 0.08));
  border-color: rgba(16, 185, 129, 0.22);
}

.immersive-panel {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.12), rgba(56, 189, 248, 0.12));
  border-color: rgba(59, 130, 246, 0.22);
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
  font-size: 22px;
  font-weight: 700;
}

.panel-header p {
  margin: 0.35rem 0 0;
  color: var(--brand-text-muted);
  font-size: 14px;
}

.service-panel-header {
  flex-direction: column;
  align-items: stretch;
  gap: 1.25rem;
}

.service-header-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

.service-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
  justify-content: space-between;
}

.service-search-group {
  display: flex;
  gap: 0.75rem;
  align-items: center;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.service-category-row {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.service-category-tabs {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  flex-wrap: wrap;
}

.category-tab {
  padding: 0.4rem 1rem;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.32);
  background: rgba(248, 250, 255, 0.92);
  color: var(--brand-text-muted);
  font-size: 0.95rem;
  transition: background 0.2s ease, border-color 0.2s ease, color 0.2s ease;
}

.category-tab:hover:not(:disabled),
.category-tab:focus-visible:not(:disabled) {
  border-color: rgba(59, 130, 246, 0.45);
  background: rgba(255, 255, 255, 0.98);
  outline: none;
}

.category-tab.active {
  border-color: rgba(37, 99, 235, 0.4);
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.12), rgba(16, 185, 129, 0.12));
  color: var(--brand-primary);
}

.category-tab.loading {
  cursor: default;
  opacity: 0.7;
}

.category-empty {
  margin: 0;
  color: var(--brand-text-muted);
  font-size: 0.9rem;
}

.search-input {
  min-width: 220px;
  padding: 0.55rem 0.9rem;
  border-radius: 0.9rem;
  border: 1px solid rgba(148, 163, 184, 0.32);
  background: rgba(255, 255, 255, 0.92);
  color: var(--brand-text);
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.search-input::placeholder {
  color: var(--brand-text-muted);
}

.search-input:focus {
  outline: none;
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.18);
  background: #fff;
}

.ghost-button {
  padding: 10px 20px;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.32);
  background: rgba(248, 250, 255, 0.9);
  color: var(--brand-primary);
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;
}

.ghost-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.ghost-button:not(:disabled):hover {
  transform: translateY(-1px);
  background: rgba(59, 130, 246, 0.12);
  box-shadow: 0 12px 24px rgba(59, 130, 246, 0.18);
}

.loading-state {
  padding: 2.5rem;
  text-align: center;
  color: var(--brand-text-muted);
}

.discover-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 1.5rem;
}

.carousel {
  grid-column: span 2;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.carousel-frame {
  position: relative;
  display: flex;
  align-items: stretch;
  justify-content: center;
}

.carousel-frame.has-controls {
  padding: 0 2.75rem;
}

.carousel-window {
  width: 100%;
  max-width: 420px;
  display: flex;
  margin: 0 auto;
}

.carousel-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 999px;
  width: 2.5rem;
  height: 2.5rem;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.18);
  color: var(--brand-primary);
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.carousel-button.prev {
  left: 0.75rem;
}

.carousel-button.next {
  right: 0.75rem;
}

.carousel-button:disabled {
  opacity: 0.4;
  cursor: default;
  box-shadow: none;
}

.carousel-button:not(:disabled):hover {
  transform: translateY(-50%) scale(1.05);
  background: rgba(59, 130, 246, 0.12);
}

.carousel-indicator {
  text-align: center;
  color: var(--brand-text-muted);
  font-size: 0.85rem;
}

.carousel-card {
  border-radius: 1.1rem;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.18);
  overflow: hidden;
  min-height: 220px;
  display: flex;
  flex-direction: column;
  flex: 1;
  width: 100%;
}

.carousel-media {
  flex: 1;
  background-size: cover;
  background-position: center;
  min-height: 140px;
}

.carousel-body {
  padding: 0.85rem 1rem;
}

.section-title {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 0.25rem;
}

.section-title h3 {
  margin: 0;
}

.section-title p {
  margin: 0;
  color: var(--brand-text-muted);
  font-size: 0.9rem;
}

.tip-list,
.announcement-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.tip-list li,
.announcement-list li {
  padding: 0.85rem 1rem;
  border-radius: 1rem;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 18px 32px rgba(15, 23, 42, 0.08);
}

.tip-list strong,
.announcement-list strong {
  display: block;
  margin-bottom: 0.35rem;
}

.service-grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
}

.service-card {
  position: relative;
  padding: 1.5rem;
  border-radius: 1.2rem;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 22px 40px rgba(15, 23, 42, 0.14);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.service-cover {
  width: 100%;
  aspect-ratio: 16 / 9;
  border-radius: 1rem;
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  overflow: hidden;
  border: 1px solid rgba(148, 163, 184, 0.18);
  display: grid;
  place-items: center;
  position: relative;
}

.service-cover::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0) 50%, rgba(0, 0, 0, 0.06));
}

.service-cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  position: relative;
  z-index: 1;
}

.service-cover-placeholder {
  position: relative;
  z-index: 1;
  color: rgba(71, 85, 105, 0.9);
  font-size: 0.9rem;
  font-weight: 600;
}

.favorite-toggle {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 999px;
  border: 2px solid #ef4444;
  width: 44px;
  height: 44px;
  display: grid;
  place-items: center;
  font-size: 1.1rem;
  color: #ef4444;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.12);
  transition: all 0.2s ease;
}

.favorite-toggle.active {
  background: #ef4444;
  border-color: #ef4444;
  color: #fff;
  box-shadow: 0 14px 28px rgba(239, 68, 68, 0.26);
}

.favorite-toggle:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.service-title {
  margin: 0;
  font-size: 1.25rem;
}

.service-category-chip {
  padding: 0.15rem 0.6rem;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.12);
  color: var(--brand-primary);
  font-size: 0.8rem;
  white-space: nowrap;
}

.service-card-footer {
  margin-top: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.service-card-footer .service-category-chip {
  margin: 0;
}

.service-book-button {
  margin-left: auto;
}

.service-company {
  margin: 0;
  color: var(--brand-text-muted);
}

.service-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
  margin: 0;
}

.service-meta dt {
  font-size: 0.85rem;
  color: var(--brand-text-muted);
}

.service-desc {
  margin: 0;
  color: var(--brand-text);
}

.primary-button {
  align-self: flex-start;
  padding: 10px 18px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, var(--brand-success) 0%, var(--brand-primary) 100%);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary-button:hover {
  transform: translateY(-1px);
  box-shadow: var(--brand-shadow-soft);
}

.secondary-button {
  padding: 10px 18px;
  border-radius: 12px;
  border: 1px solid rgba(37, 99, 235, 0.35);
  background: rgba(248, 250, 255, 0.92);
  color: var(--brand-primary);
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;
}

.secondary-button:hover {
  transform: translateY(-1px);
  background: rgba(59, 130, 246, 0.12);
  box-shadow: 0 12px 24px rgba(59, 130, 246, 0.18);
}

.secondary-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

.secondary-button.danger {
  border-color: rgba(239, 68, 68, 0.35);
  color: var(--brand-danger);
}

.link-button {
  background: none;
  border: none;
  color: var(--brand-primary);
  cursor: pointer;
  padding: 0;
  font-size: 0.9rem;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s ease;
}

.link-button:hover {
  color: var(--brand-primary-dark);
  text-decoration: underline;
}

.table-wrapper {
  overflow: auto;
  border-radius: calc(var(--brand-radius) + 2px);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 20px 36px rgba(15, 23, 42, 0.1);
  background: rgba(255, 255, 255, 0.95);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 1rem 1.2rem;
  text-align: left;
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
}

.table-checkbox {
  width: 48px;
  text-align: center;
}

.table-checkbox input[type='checkbox'] {
  width: 16px;
  height: 16px;
}

.data-table tbody tr:hover {
  background: rgba(59, 130, 246, 0.08);
}

.order-subtext {
  color: var(--brand-text-muted);
  font-size: 0.9rem;
}

.order-subtext.muted {
  color: rgba(148, 163, 184, 0.6);
}

.order-subtext.highlight {
  color: var(--brand-primary);
  font-weight: 600;
}

.order-meta {
  display: flex;
  gap: 0.75rem;
  font-size: 0.85rem;
  color: var(--brand-text-muted);
}

.review-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.review-search {
  flex: 1 1 260px;
}

.review-tabs {
  display: inline-flex;
  gap: 0.75rem;
  padding: 0.35rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.24);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.08);
}

.tab-button {
  padding: 0.45rem 1.25rem;
  border-radius: 999px;
  border: none;
  background: transparent;
  color: var(--brand-text-muted);
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.tab-button.active {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.18), rgba(16, 185, 129, 0.18));
  color: var(--brand-primary);
  box-shadow: 0 16px 28px rgba(59, 130, 246, 0.18);
}

.tab-button:focus-visible {
  outline: 2px solid rgba(59, 130, 246, 0.4);
  outline-offset: 2px;
}

.review-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.review-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  justify-content: flex-end;
  align-items: center;
}

.review-modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  z-index: 40;
}

.review-modal {
  width: min(440px, 100%);
  background: rgba(255, 255, 255, 0.98);
  border-radius: calc(var(--brand-radius) + 4px);
  border: 1px solid rgba(148, 163, 184, 0.2);
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  box-shadow: 0 26px 48px rgba(15, 23, 42, 0.18);
}

.review-modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  color: var(--brand-text);
}

.review-modal-subtitle {
  margin: 0.35rem 0 0;
  color: var(--brand-text-muted);
  font-size: 0.95rem;
}

.review-modal-form .form-field textarea {
  min-height: 120px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.review-service {
  font-weight: 600;
  color: var(--brand-text);
}

.review-rating {
  margin: 0.35rem 0;
  font-size: 0.85rem;
  color: var(--brand-text-muted);
}

.review-text {
  margin: 0;
  color: var(--brand-text);
  white-space: pre-wrap;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 0.35rem;
}

.status-scheduled,
.status-pending {
  background: linear-gradient(135deg, #6366f1, #4338ca);
}

.status-in_progress {
  background: linear-gradient(135deg, #14b8a6, #0f766e);
}

.status-completed {
  background: linear-gradient(135deg, #10b981, #059669);
}

.status-refund_requested {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.status-refund_approved {
  background: linear-gradient(135deg, #14b8a6, #0f766e);
}

.status-refund_rejected {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.table-actions {
  white-space: nowrap;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.schedule-timeline {
  margin-top: 2rem;
}

.schedule-timeline ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
}

.schedule-timeline li {
  padding: 0.85rem 1rem;
  border-radius: 1rem;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.1);
}

.wallet-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
}

.wallet-card {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding: 1.5rem;
  border-radius: 1.2rem;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 18px 32px rgba(15, 23, 42, 0.1);
}

.wallet-card input,
.form-field select,
.form-field textarea,
.form-field input {
  width: 100%;
  border-radius: calc(var(--brand-radius) / 2);
  border: 1px solid rgba(148, 163, 184, 0.32);
  background: rgba(255, 255, 255, 0.96);
  color: var(--brand-text);
  padding: 0.55rem 0.75rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.wallet-card input:focus,
.form-field select:focus,
.form-field textarea:focus,
.form-field input:focus {
  outline: none;
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.18);
  background: #fff;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.45rem;
}

.form-field-full {
  grid-column: 1 / -1;
}

.form-actions {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
}

.review-list {
  margin-top: 2rem;
}

.review-list ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.review-item {
  padding: 1rem;
  border-radius: 1rem;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.review-rating {
  color: #facc15;
}

.empty-tip {
  text-align: center;
  color: var(--brand-text-muted);
  padding: 1.75rem 0;
}

.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(12px);
  z-index: 30;
}

.dialog-card {
  width: min(560px, 90%);
  background: rgba(255, 255, 255, 0.98);
  border-radius: calc(var(--brand-radius) + 4px);
  border: 1px solid rgba(148, 163, 184, 0.2);
  padding: 28px;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  box-shadow: 0 26px 48px rgba(15, 23, 42, 0.2);
}

.payment-card {
  max-width: 520px;
}

.payment-body {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 1rem;
}

.payment-tip {
  font-size: 0.85rem;
  color: var(--brand-text-muted);
}

.payment-tip a {
  color: var(--brand-primary);
}

.payment-methods {
  border: 1px solid var(--brand-border);
  border-radius: 12px;
  padding: 0.75rem 1rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 0.5rem;
}

.payment-methods legend {
  font-weight: 600;
  color: var(--brand-text-muted);
  padding: 0 0.25rem;
}

.method-option {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  font-weight: 600;
}

.payment-summary {
  margin: 0;
  font-size: 0.95rem;
}

.payment-status {
  margin: 0.25rem 0 0;
  font-size: 0.95rem;
  color: var(--brand-text);
}

.payment-status.success {
  color: var(--brand-success);
}

.payment-status.error {
  color: var(--brand-danger);
}

.payment-status.checking {
  color: var(--brand-warning);
}

.dialog-header h2 {
  margin: 0;
  font-size: 1.5rem;
}

.dialog-subtext {
  margin: 0.25rem 0 0;
  color: var(--brand-text-muted);
  font-size: 0.95rem;
}

.dialog-field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.booking-date-picker {
  width: 100%;
  padding: 0.75rem 0.85rem;
  border-radius: 10px;
  border: 1px solid var(--brand-border);
  background: var(--brand-surface);
  font-size: 1rem;
}

.time-slot-select {
  width: 100%;
  padding: 0.75rem 0.85rem;
  border-radius: 10px;
  border: 1px solid var(--brand-border);
  background: var(--brand-surface);
  font-size: 1rem;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 1080px) {
  .dashboard-main {
    grid-template-columns: 1fr;
  }

  .header-actions {
    flex-wrap: wrap;
    justify-content: flex-end;
  }

  .account-avatar {
    width: 44px;
    height: 44px;
  }

  .sidebar {
    flex-direction: row;
    flex-wrap: wrap;
    margin-bottom: 1.5rem;
  }

  .sidebar-item {
    flex: 1 1 150px;
    justify-content: center;
  }

  .discover-grid {
    grid-template-columns: 1fr;
  }
}
</style>

<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1 class="dashboard-title">家政公司工作台</h1>
        <p class="dashboard-subtitle">维护服务项目、安排预约并高效连接客户</p>
      </div>
      <div class="header-actions">
        <img :src="avatarSrc" alt="账号头像" class="account-avatar" />
        <span class="welcome">您好，{{ displayName }}！</span>
        <span class="wallet">钱包余额：¥{{ balanceText }}</span>
        <button type="button" class="logout-button" @click="logout">退出登录</button>
      </div>
  </header>

    <section class="stats-grid" aria-label="运营数据概览">
      <article class="stat-card accent">
        <p class="stat-label">上架服务</p>
        <p class="stat-value">{{ companyStats.totalServices }}</p>
        <p class="stat-helper">向平台展示您的服务能力</p>
      </article>
      <article class="stat-card primary">
        <p class="stat-label">待执行预约</p>
        <p class="stat-value">{{ companyStats.upcoming }}</p>
        <p class="stat-helper">提前规划人手与物料</p>
      </article>
      <article class="stat-card warning">
        <p class="stat-label">进行中服务</p>
        <p class="stat-value">{{ companyStats.inProgress }}</p>
        <p class="stat-helper">实时掌握执行状态</p>
      </article>
      <article class="stat-card success">
        <p class="stat-label">团队成员</p>
        <p class="stat-value">{{ companyStats.staffCount }}</p>
        <p class="stat-helper">账户余额：¥{{ companyStats.balance.toFixed(2) }}</p>
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
        <section v-if="activeSection === 'profile'" class="panel profile-panel">
          <header class="panel-header">
            <div>
              <h2>企业资料</h2>
              <p>维护公司展示名称与头像，让客户快速识别您的品牌。</p>
            </div>
          </header>
          <AccountProfileEditor :account="account" @updated="handleProfileUpdated" />
        </section>

        <section v-else-if="activeSection === 'services'" class="panel">
          <header class="panel-header">
            <div>
              <h2>服务项目管理</h2>
              <p>完善服务信息让用户更了解您的优势。当前平均定价 ¥{{ companyStats.avgPrice.toFixed(2) }}</p>
            </div>
            <div class="service-actions">
              <label class="visually-hidden" for="service-search">搜索服务</label>
              <input
                id="service-search"
                v-model="serviceSearch"
                class="search-input"
                type="search"
                placeholder="搜索名称、单位、联系方式或描述"
                :disabled="serviceLoading"
              />
              <button
                type="button"
                class="secondary-button danger"
                :disabled="!hasServiceSelection || serviceLoading"
                @click="handleBulkDeleteServices"
              >
                删除选中<span v-if="selectedServiceCount">（{{ selectedServiceCount }}）</span>
              </button>
              <span class="service-hint">共 {{ serviceTotal }} 项，每页显示 {{ serviceSize }} 项</span>
              <button type="button" class="primary-button" @click="openServiceForm()">新增服务</button>
            </div>
          </header>

          <div class="category-menu service-category-menu" role="tablist" aria-label="按服务类别筛选服务">
            <button
              type="button"
              class="category-chip"
              :class="{ active: serviceCategoryFilter === 'all' }"
              @click="handleSelectServiceCategory('all')"
            >
              全部
            </button>
            <button
              v-for="category in serviceCategories"
              :key="category.id"
              type="button"
              class="category-chip"
              :class="{ active: serviceCategoryFilter === category.id }"
              @click="handleSelectServiceCategory(category.id)"
            >
              {{ category.name }}
              <span class="chip-count">{{ category.serviceCount }}</span>
            </button>
            <p v-if="!serviceCategories.length" class="category-empty">暂无服务分类</p>
          </div>

          <div v-if="serviceFormVisible" class="form-card">
            <form class="form-grid" @submit.prevent="submitServiceForm">
              <div class="form-field">
                <label for="service-name">服务名称</label>
                <input id="service-name" v-model="serviceForm.name" type="text" />
              </div>
              <div class="form-field">
                <label for="service-unit">计价单位</label>
                <input id="service-unit" v-model="serviceForm.unit" type="text" placeholder="如：次/小时" />
              </div>
              <div class="form-field">
                <label for="service-price">价格</label>
                <input id="service-price" v-model.number="serviceForm.price" type="number" min="0" step="0.01" />
              </div>
              <div class="form-field">
                <label for="service-contact">联系方式</label>
                <input id="service-contact" v-model="serviceForm.contact" type="text" />
              </div>
              <div class="form-field">
                <label for="service-time">服务时间</label>
                <input
                  id="service-time"
                  v-model="serviceForm.serviceTime"
                  type="text"
                  placeholder="如：工作日 9:00-18:00 / 2 小时"
                />
              </div>
              <div class="form-field">
                <label for="service-category">服务分类</label>
                <select
                  id="service-category"
                  v-model.number="serviceForm.categoryId"
                  :disabled="!serviceCategories.length"
                  required
                >
                  <option disabled value="0">请选择分类</option>
                  <option v-for="category in serviceCategories" :key="category.id" :value="category.id">
                    {{ category.name }}
                  </option>
                </select>
                <p v-if="!serviceCategories.length" class="form-helper">请先在管理员端配置服务分类</p>
              </div>
              <div class="form-field form-field-full">
                <label for="service-description">服务简介</label>
                <textarea id="service-description" v-model="serviceForm.description" rows="3"></textarea>
              </div>
              <div class="form-actions">
                <button type="button" class="secondary-button" @click="closeServiceForm">取消</button>
                <button type="submit" class="primary-button" :disabled="serviceSaving">
                  {{ serviceSaving ? '保存中…' : serviceSubmitText }}
                </button>
              </div>
            </form>
          </div>

          <div class="category-menu" role="tablist" aria-label="按服务类别筛选人员">
            <button
              type="button"
              class="category-chip"
              :class="{ active: staffCategoryFilter === 'all' }"
              @click="staffCategoryFilter = 'all'"
            >
              全部
            </button>
            <button
              v-for="category in serviceCategories"
              :key="category.id"
              type="button"
              class="category-chip"
              :class="{ active: staffCategoryFilter === category.id }"
              @click="staffCategoryFilter = category.id"
            >
              {{ category.name }}
              <span class="chip-count">{{ category.availableStaffCount }}</span>
            </button>
          </div>

          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="allVisibleServicesSelected"
                      :disabled="serviceLoading || !services.length"
                      @change="toggleSelectAllServices(($event.target as HTMLInputElement).checked)"
                      aria-label="全选当前服务"
                    />
                  </th>
                  <th>服务名称</th>
                  <th>服务类别</th>
                  <th>价格</th>
                  <th>联系方式</th>
                  <th>服务时间</th>
                  <th>描述</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in services" :key="item.id">
                  <td class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="selectedServiceIds.has(item.id)"
                      :disabled="serviceLoading"
                      @change="toggleServiceSelection(item.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择服务 ${item.name}`"
                    />
                  </td>
                  <td>
                    <strong>{{ item.name }}</strong>
                    <div class="order-subtext">单位：{{ item.unit }}</div>
                    <div class="order-subtext">空闲人员：{{ item.availableStaffCount }}</div>
                  </td>
                  <td>{{ item.categoryName || '—' }}</td>
                  <td>¥{{ item.price.toFixed(2) }}</td>
                  <td>{{ item.contact }}</td>
                  <td>{{ item.serviceTime }}</td>
                  <td>{{ item.description || '—' }}</td>
                  <td class="table-actions">
                    <button type="button" class="link-button" @click="openServiceForm(item)">编辑</button>
                    <button type="button" class="link-button danger" @click="handleDeleteService(item)">删除</button>
                  </td>
                </tr>
                <tr v-if="serviceLoading">
                  <td colspan="8" class="empty-row">服务加载中…</td>
                </tr>
                <tr v-else-if="!services.length">
                  <td colspan="8" class="empty-row">
                    <span v-if="hasServiceFilter">未找到匹配的服务，请调整搜索关键词。</span>
                    <span v-else>还没有服务内容，请点击上方按钮进行新增。</span>
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-if="serviceTotal > serviceSize" class="table-footer">
              <div class="pagination">
                <button
                  type="button"
                  class="secondary-button"
                  :disabled="serviceLoading || servicePage === 1"
                  @click="changeServicePage(servicePage - 1)"
                >
                  上一页
                </button>
                <span>
                  第 {{ servicePage }} / {{ serviceTotalPages }} 页 · 显示
                  {{ servicePageStart }}-{{ servicePageEnd }} 项，共 {{ serviceTotal }} 项
                </span>
                <button
                  type="button"
                  class="secondary-button"
                  :disabled="serviceLoading || servicePage === serviceTotalPages"
                  @click="changeServicePage(servicePage + 1)"
                >
                  下一页
                </button>
              </div>
            </div>
          </div>
        </section>

        <section v-else-if="activeSection === 'staff'" class="panel">
          <header class="panel-header">
            <div>
              <h2>人员管理</h2>
              <p>维护家政师傅信息，便于快速指派订单。</p>
            </div>
            <div class="staff-actions">
              <label class="visually-hidden" for="staff-search">搜索人员</label>
              <input
                id="staff-search"
                v-model="staffSearch"
                class="search-input"
                type="search"
                placeholder="搜索姓名、联系方式、分类或备注"
                :disabled="staffLoading"
              />
              <button
                type="button"
                class="secondary-button danger"
                :disabled="!hasStaffSelection || staffLoading"
                @click="handleBulkDeleteStaff"
              >
                删除选中<span v-if="selectedStaffCount">（{{ selectedStaffCount }}）</span>
              </button>
              <button
                v-if="hasStaffFilter"
                type="button"
                class="ghost-button"
                :disabled="staffLoading"
                @click="clearStaffFilter"
              >
                清除筛选
              </button>
              <button type="button" class="primary-button" @click="openStaffForm()">新增人员</button>
            </div>
          </header>

          <div v-if="staffFormVisible" class="form-card">
            <form class="form-grid" @submit.prevent="submitStaffForm">
              <div class="form-field">
                <label for="staff-name">姓名</label>
                <input id="staff-name" v-model="staffForm.name" type="text" required />
              </div>
              <div class="form-field">
                <label for="staff-contact">联系方式</label>
                <input id="staff-contact" v-model="staffForm.contact" type="text" required />
              </div>
              <div class="form-field">
                <label for="staff-category">服务类别</label>
                <select
                  id="staff-category"
                  v-model.number="staffForm.categoryId"
                  :disabled="!serviceCategories.length"
                  required
                >
                  <option disabled value="0">请选择分类</option>
                  <option v-for="category in serviceCategories" :key="category.id" :value="category.id">
                    {{ category.name }}
                  </option>
                </select>
                <p v-if="!serviceCategories.length" class="form-helper">请先添加服务分类</p>
              </div>
              <div class="form-field form-field-full">
                <label for="staff-notes">备注</label>
                <textarea id="staff-notes" v-model="staffForm.notes" rows="3"></textarea>
              </div>
              <div class="form-actions">
                <button type="button" class="secondary-button" @click="closeStaffForm">取消</button>
                <button type="submit" class="primary-button" :disabled="staffSaving">
                  {{ staffSaving ? '保存中…' : staffSubmitText }}
                </button>
              </div>
            </form>
          </div>

          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="allStaffSelected"
                      :disabled="staffLoading || !staffList.length"
                      @change="toggleSelectAllStaff(($event.target as HTMLInputElement).checked)"
                      aria-label="全选人员"
                    />
                  </th>
                  <th>姓名</th>
                  <th>联系方式</th>
                  <th>服务类别</th>
                  <th>状态</th>
                  <th>备注</th>
                  <th>创建时间</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in staffList" :key="item.id">
                  <td class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="selectedStaffIds.has(item.id)"
                      :disabled="staffLoading"
                      @change="toggleStaffSelection(item.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择人员 ${item.name}`"
                    />
                  </td>
                  <td>{{ item.name }}</td>
                  <td>{{ item.contact }}</td>
                  <td>{{ item.categoryName || '—' }}</td>
                  <td>
                    <span class="status-badge" :class="item.assigned ? 'status-assigned' : 'status-available'">
                      {{ item.assigned ? '已分配' : '未分配' }}
                    </span>
                  </td>
                  <td>{{ item.notes || '—' }}</td>
                  <td>{{ formatDateTime(item.createdAt) }}</td>
                  <td class="table-actions">
                    <button type="button" class="link-button" @click="openStaffForm(item)">编辑</button>
                    <button type="button" class="link-button danger" @click="handleDeleteStaff(item)">删除</button>
                  </td>
                </tr>
                <tr v-if="!staffList.length">
                  <td colspan="8" class="empty-row">
                    <span v-if="hasStaffFilter">未找到匹配的人员，尝试调整搜索条件。</span>
                    <span v-else>还没有添加人员，点击右上角按钮即可新增。</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="activeSection === 'appointments'" class="panel">
          <header class="panel-header">
            <div>
              <h2>预约排班</h2>
              <p>掌握近期预约并同步上门进度，合理安排师傅日程。</p>
            </div>
            <div class="service-actions">
              <label class="visually-hidden" for="appointment-search">搜索预约</label>
              <input
                id="appointment-search"
                v-model="appointmentSearch"
                class="search-input"
                type="search"
                :disabled="appointmentsLoading"
                placeholder="搜索服务、用户、电话或备注"
              />
              <button
                type="button"
                class="secondary-button danger"
                :disabled="!hasOrderSelection || appointmentsLoading"
                @click="handleBulkDeleteOrders"
              >
                删除选中<span v-if="selectedOrderCount">（{{ selectedOrderCount }}）</span>
              </button>
              <button type="button" class="secondary-button" :disabled="appointmentsLoading" @click="refreshAppointments">
                刷新预约
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
                      :disabled="appointmentsLoading || !visibleCompanyOrders.length"
                      @change="toggleSelectAllOrders(($event.target as HTMLInputElement).checked)"
                      aria-label="全选预约"
                    />
                  </th>
                  <th>服务</th>
                  <th>服务类别</th>
                  <th>预约时间</th>
                  <th>用户</th>
                  <th>状态</th>
                  <th>进度备注</th>
                  <th>指派人员</th>
                  <th class="table-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in visibleCompanyOrders" :key="order.id">
                  <td class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="selectedOrderIds.has(order.id)"
                      :disabled="appointmentsLoading"
                      @change="toggleOrderSelection(order.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择预约 ${order.serviceName}`"
                    />
                  </td>
                  <td>
                    <strong>{{ order.serviceName }}</strong>
                    <div class="order-subtext">价格：¥{{ order.price.toFixed(2) }} / {{ order.unit }}</div>
                    <div class="order-subtext">联系方式：{{ order.contact }}</div>
                    <div class="order-subtext">上门地址：{{ order.serviceAddress || '未填写' }}</div>
                    <div class="order-subtext">用户电话：{{ order.customerContactPhone || '未提供' }}</div>
                    <div class="order-subtext">用户地址：{{ order.customerAddress || '—' }}</div>
                    <div v-if="order.specialRequest" class="order-subtext highlight">
                      用户需求：{{ order.specialRequest }}
                    </div>
                  </td>
                  <td>{{ order.categoryName || '—' }}</td>
                  <td>{{ formatDateTime(order.scheduledAt) }}</td>
                  <td>{{ order.username }}</td>
                  <td>
                    <span class="status-badge" :class="`status-${order.status.toLowerCase()}`">
                      {{ statusText(order.status) }}
                    </span>
                  </td>
                  <td>
                    <input
                      v-model="progressNoteEdits[order.id]"
                      type="text"
                      class="progress-input"
                      placeholder="填写最新进度"
                    />
                  </td>
                  <td>
                    <button type="button" class="secondary-button" @click="openAssignmentModal(order)">
                      指派人员
                    </button>
                    <div v-if="order.assignedWorker" class="order-subtext">
                      当前：{{ order.assignedWorker }}<span v-if="order.workerContact">（{{ order.workerContact }}）</span>
                    </div>
                  </td>
                  <td class="table-actions actions-inline">
                    <button
                      type="button"
                      class="link-button"
                      :disabled="progressSaving[order.id] || appointmentsLoading"
                      @click="saveOrderProgress(order, 'SCHEDULED')"
                    >
                      重置
                    </button>
                    <button
                      type="button"
                      class="link-button"
                      :disabled="progressSaving[order.id] || appointmentsLoading"
                      @click="saveOrderProgress(order, 'IN_PROGRESS')"
                    >
                      服务中
                    </button>
                    <button
                      type="button"
                      class="link-button"
                      :disabled="progressSaving[order.id] || appointmentsLoading"
                      @click="saveOrderProgress(order, 'COMPLETED')"
                    >
                      完成
                    </button>
                    <button
                      type="button"
                      class="link-button danger"
                      :disabled="appointmentsLoading"
                      @click="handleDeleteOrder(order)"
                    >
                      删除
                    </button>
                  </td>
                </tr>
                <tr v-if="appointmentsLoading">
                  <td colspan="9" class="empty-row">预约数据加载中…</td>
                </tr>
                <tr v-else-if="!visibleCompanyOrders.length">
                  <td colspan="9" class="empty-row">
                    <span v-if="hasAppointmentFilter">未找到匹配的预约，请调整搜索条件。</span>
                    <span v-else>暂无预约记录，用户预约后会自动出现在此处。</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-else-if="activeSection === 'reviews'" class="panel">
          <header class="panel-header">
            <div>
              <h2>服务口碑</h2>
              <p>
                平均评分 <strong class="metric-inline">{{ reviewStats.average }}</strong>
                · 好评率 <strong class="metric-inline">{{ reviewStats.positiveRate }}</strong>
                · 精选 {{ reviewStats.pinnedCount }} 条
              </p>
            </div>
            <div class="service-actions">
              <label class="visually-hidden" for="review-search">搜索评价</label>
              <input
                id="review-search"
                v-model="reviewSearch"
                class="search-input"
                type="search"
                :disabled="reviewsLoading"
                placeholder="搜索服务、用户或评价内容"
              />
              <button
                type="button"
                class="secondary-button danger"
                :disabled="!hasReviewSelection || reviewsLoading"
                @click="handleBulkDeleteReviews"
              >
                删除选中<span v-if="selectedReviewCount">（{{ selectedReviewCount }}）</span>
              </button>
              <button type="button" class="secondary-button" :disabled="reviewsLoading" @click="loadCompanyReviews">
                刷新评价
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
                      :checked="allVisibleReviewsSelected"
                      :disabled="reviewsLoading || !visibleCompanyReviews.length"
                      @change="toggleSelectAllReviews(($event.target as HTMLInputElement).checked)"
                      aria-label="全选评价"
                    />
                  </th>
                  <th>服务与评分</th>
                  <th>用户</th>
                  <th>评价内容</th>
                  <th>回复</th>
                  <th class="table-actions">时间与操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="review in visibleCompanyReviews" :key="review.id">
                  <td class="table-checkbox">
                    <input
                      type="checkbox"
                      :checked="selectedReviewIds.has(review.id)"
                      :disabled="reviewsLoading"
                      @change="toggleReviewSelection(review.id, ($event.target as HTMLInputElement).checked)"
                      :aria-label="`选择评价 ${review.serviceName}`"
                    />
                  </td>
                  <td>
                    <div class="review-service">
                      <strong>{{ review.serviceName }}</strong>
                      <span v-if="review.pinned" class="pinned-badge">精选</span>
                    </div>
                    <div class="review-rating">评分：{{ review.rating }} 分</div>
                  </td>
                  <td>
                    <div class="review-author">{{ review.username }}</div>
                    <div class="order-subtext">{{ formatDateTime(review.createdAt) }}</div>
                  </td>
                  <td>
                    <p class="review-text">{{ review.content || '用户未留下文字评价' }}</p>
                  </td>
                  <td>
                    <p v-if="review.companyReply" class="review-text">{{ review.companyReply }}</p>
                    <span v-else class="order-subtext muted">—</span>
                  </td>
                  <td class="table-actions actions-inline">
                    <span class="order-subtext">更新于 {{ formatDateTime(review.updatedAt) }}</span>
                    <button
                      type="button"
                      class="link-button danger"
                      :disabled="reviewsLoading"
                      @click="handleDeleteReview(review)"
                    >
                      删除
                    </button>
                  </td>
                </tr>
                <tr v-if="reviewsLoading">
                  <td colspan="6" class="empty-row">评价数据加载中…</td>
                </tr>
                <tr v-else-if="!visibleCompanyReviews.length">
                  <td colspan="6" class="empty-row">
                    <span v-if="hasReviewFilter">未找到匹配的评价，请尝试更换关键词。</span>
                    <span v-else>暂时还没有客户评价，完成更多订单即可收集口碑。</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <CompanyMessagingPanel
          v-else-if="activeSection === 'messages'"
          class="panel immersive-panel"
          :conversations="conversationSummaries"
          :loading-conversations="conversationsLoading"
          :active-conversation-id="activeConversationId"
          :messages="activeMessages"
          :loading-messages="messagesLoading"
          :sending="messageSending"
          @refresh-conversations="handleRefreshConversations"
          @refresh-messages="refreshActiveMessages"
          @select-conversation="selectConversation"
          @send-message="handleSendMessage"
        />

      </main>
    </div>
    <div
      v-if="assignmentModalVisible"
      class="modal-backdrop"
      role="dialog"
      aria-modal="true"
      aria-labelledby="assignment-modal-title"
    >
      <div class="modal-card">
        <header class="modal-header">
          <h3 id="assignment-modal-title">指派人员</h3>
          <button type="button" class="close-button" @click="closeAssignmentModal" aria-label="关闭">×</button>
        </header>
        <div class="modal-body">
          <p v-if="assignmentModalOrder" class="modal-subtitle">
            服务：{{ assignmentModalOrder.serviceName }} · 分类：{{ assignmentModalOrder.categoryName || '未设置' }}
          </p>
          <div v-if="assignmentModalLoading" class="loading-state">加载人员中…</div>
          <div v-else class="assignment-columns">
            <section>
              <h4>未分配</h4>
              <p v-if="!modalAvailableStaff.length" class="muted-text">暂无未分配人员</p>
              <ul class="staff-list">
                <li v-for="staff in modalAvailableStaff" :key="staff.id">
                  <label class="radio-option">
                    <input type="radio" :value="staff.id" v-model.number="assignmentModalSelection" />
                    <span>{{ staff.name }}（{{ staff.contact }}）</span>
                  </label>
                </li>
              </ul>
            </section>
            <section>
              <h4>已分配</h4>
              <p v-if="!modalAssignedStaff.length" class="muted-text">暂无已分配人员</p>
              <ul class="staff-list">
                <li v-for="staff in modalAssignedStaff" :key="staff.id">{{ staff.name }}（{{ staff.contact }}）</li>
              </ul>
            </section>
          </div>
          <p v-if="assignmentModalError" class="modal-error">{{ assignmentModalError }}</p>
        </div>
        <footer class="modal-footer">
          <button type="button" class="secondary-button" @click="closeAssignmentModal">取消</button>
          <button
            type="button"
            class="primary-button"
            :disabled="assignmentSubmitting"
            @click="confirmAssignment"
          >
            {{ assignmentSubmitting ? '指派中…' : '确认指派' }}
          </button>
        </footer>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

import { AUTH_ACCOUNT_KEY, AUTH_ROLE_KEY, AUTH_TOKEN_KEY } from '../constants/auth'
import {
  fetchCurrentAccount,
  createCompanyService,
  deleteCompanyService,
  deleteCompanyServices,
  fetchCompanyOrders,
  fetchCompanyServices,
  fetchCompanyReviews,
  fetchCompanyConversations,
  fetchCompanyMessages,
  markCompanyConversationRead,
  sendCompanyMessage,
  updateCompanyOrderProgress,
  updateCompanyService,
  fetchCompanyStaff,
  createCompanyStaff,
  updateCompanyStaff,
  deleteCompanyStaff,
  deleteCompanyStaffBatch,
  assignCompanyStaff,
  fetchServiceCategories,
  deleteCompanyOrder,
  deleteCompanyOrders,
  deleteCompanyReview,
  deleteCompanyReviews,
  type AccountProfileItem,
  type CompanyServicePayload,
  type CompanyConversationItem,
  type CompanyMessageItem,
  type HousekeepServiceItem,
  type ServiceReviewItem,
  type ServiceOrderItem,
  type UpdateOrderProgressPayload,
  type CompanyStaffItem,
  type CompanyStaffPayload,
  type ServiceCategoryItem,
} from '../services/dashboard'

import CompanyMessagingPanel from '../pages/company/CompanyMessagingPanel.vue'
import AccountProfileEditor from '../components/AccountProfileEditor.vue'

type SectionKey = 'profile' | 'services' | 'appointments' | 'staff' | 'reviews' | 'messages'

interface SectionMeta {
  key: SectionKey
  icon: string
  label: string
}

const router = useRouter()
const account = ref<AccountProfileItem | null>(null)

const FALLBACK_AVATAR =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg=='

const displayName = computed(
  () =>
    account.value?.displayName ||
    account.value?.username ||
    sessionStorage.getItem(AUTH_ACCOUNT_KEY) ||
    '公司用户',
)
const balanceText = computed(() => (account.value ? account.value.balance.toFixed(2) : '0.00'))
const avatarSrc = computed(() => account.value?.avatarBase64 || FALLBACK_AVATAR)

const sections: SectionMeta[] = [
  { key: 'profile', icon: '👤', label: '企业资料' },
  { key: 'services', icon: '🧹', label: '服务管理' },
  { key: 'appointments', icon: '📅', label: '预约排班' },
  { key: 'staff', icon: '🧑\u200d🤝\u200d🧑', label: '人员管理' },
  { key: 'reviews', icon: '✨', label: '服务口碑' },
  { key: 'messages', icon: '💬', label: '客户沟通' },
]

const activeSection = ref<SectionKey>('services')
const serviceCategories = ref<ServiceCategoryItem[]>([])
const services = ref<HousekeepServiceItem[]>([])
const serviceSearch = ref('')
const serviceCategoryFilter = ref<number | 'all'>('all')
const servicePage = ref(1)
const serviceSize = ref(10)
const serviceTotal = ref(0)
const serviceAveragePrice = ref(0)
const serviceLoading = ref(false)
const selectedServiceIds = ref<Set<number>>(new Set())
let serviceSearchTimer: ReturnType<typeof setTimeout> | null = null
const companyOrders = ref<ServiceOrderItem[]>([])
const appointmentsLoading = ref(false)
const appointmentSearch = ref('')
const selectedOrderIds = ref<Set<number>>(new Set())
const serviceFormVisible = ref(false)
const serviceSaving = ref(false)
const editingServiceId = ref<number | null>(null)
const progressNoteEdits = reactive<Record<number, string>>({})
const progressSaving = reactive<Record<number, boolean>>({})
const serviceForm = reactive<CompanyServicePayload>({
  name: '',
  unit: '',
  price: 0,
  contact: '',
  serviceTime: '按需预约',
  description: '',
  categoryId: 0,
})

const staffList = ref<CompanyStaffItem[]>([])
const staffSearch = ref('')
const staffLoading = ref(false)
const selectedStaffIds = ref<Set<number>>(new Set())
let staffSearchTimer: ReturnType<typeof setTimeout> | null = null
const staffFormVisible = ref(false)
const staffSaving = ref(false)
const editingStaffId = ref<number | null>(null)
const staffCategoryFilter = ref<number | 'all'>('all')
const staffForm = reactive<CompanyStaffPayload>({
  name: '',
  contact: '',
  categoryId: 0,
  notes: '',
})
const assignmentModalVisible = ref(false)
const assignmentModalLoading = ref(false)
const assignmentModalStaff = ref<CompanyStaffItem[]>([])
const assignmentModalOrder = ref<ServiceOrderItem | null>(null)
const assignmentModalSelection = ref<number | null>(null)
const assignmentSubmitting = ref(false)
const assignmentModalError = ref<string | null>(null)
const modalAvailableStaff = computed(() => assignmentModalStaff.value.filter((item) => !item.assigned))
const modalAssignedStaff = computed(() => assignmentModalStaff.value.filter((item) => item.assigned))

const serviceSubmitText = computed(() => (editingServiceId.value ? '保存修改' : '新增服务'))

const staffSubmitText = computed(() => (editingStaffId.value ? '保存人员' : '新增人员'))

const hasStaffSelection = computed(() => selectedStaffIds.value.size > 0)
const allStaffSelected = computed(
  () => staffList.value.length > 0 && staffList.value.every((item) => selectedStaffIds.value.has(item.id)),
)
const hasStaffFilter = computed(() => staffSearch.value.trim().length > 0)
const selectedStaffCount = computed(() => selectedStaffIds.value.size)

const companyReviews = ref<ServiceReviewItem[]>([])
const reviewSearch = ref('')
const selectedReviewIds = ref<Set<number>>(new Set())
const reviewsLoading = ref(false)
const conversationSummaries = ref<CompanyConversationItem[]>([])
const conversationsLoading = ref(false)
const messagesByOrder = reactive<Record<number, CompanyMessageItem[]>>({})
const lastMessageTimestamps = reactive<Record<number, string>>({})
const activeConversationId = ref<number | null>(null)
const messagesLoading = ref(false)
const messageSending = ref(false)
const messagePollHandle = ref<number | null>(null)

const handleProfileUpdated = (payload: AccountProfileItem) => {
  account.value = payload
}

const pruneServiceSelection = () => {
  if (!selectedServiceIds.value.size) {
    return
  }
  const visibleIds = new Set(services.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedServiceIds.value.forEach((id) => {
    if (visibleIds.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedServiceIds.value = next
  }
}

const toggleServiceSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedServiceIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedServiceIds.value = next
}

const toggleSelectAllServices = (checked: boolean) => {
  if (!checked) {
    selectedServiceIds.value = new Set()
    return
  }
  const next = new Set(selectedServiceIds.value)
  services.value.forEach((item) => next.add(item.id))
  selectedServiceIds.value = next
}

const clearServiceSelection = () => {
  selectedServiceIds.value = new Set()
}

const pruneStaffSelection = () => {
  if (!selectedStaffIds.value.size) {
    return
  }
  const visibleIds = new Set(staffList.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedStaffIds.value.forEach((id) => {
    if (visibleIds.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedStaffIds.value = next
  }
}

const toggleStaffSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedStaffIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedStaffIds.value = next
}

const toggleSelectAllStaff = (checked: boolean) => {
  if (!checked) {
    selectedStaffIds.value = new Set()
    return
  }
  const next = new Set(selectedStaffIds.value)
  staffList.value.forEach((item) => next.add(item.id))
  selectedStaffIds.value = next
}

const clearStaffSelection = () => {
  selectedStaffIds.value = new Set()
}

const pruneOrderSelection = () => {
  if (!selectedOrderIds.value.size) {
    return
  }
  const visibleIds = new Set(companyOrders.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedOrderIds.value.forEach((id) => {
    if (visibleIds.has(id)) {
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
    next.add(id)
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
  visibleCompanyOrders.value.forEach((item) => next.add(item.id))
  selectedOrderIds.value = next
}

const clearOrderSelection = () => {
  selectedOrderIds.value = new Set()
}

const pruneReviewSelection = () => {
  if (!selectedReviewIds.value.size) {
    return
  }
  const visibleIds = new Set(companyReviews.value.map((item) => item.id))
  let changed = false
  const next = new Set<number>()
  selectedReviewIds.value.forEach((id) => {
    if (visibleIds.has(id)) {
      next.add(id)
    } else {
      changed = true
    }
  })
  if (changed) {
    selectedReviewIds.value = next
  }
}

const toggleReviewSelection = (id: number, checked: boolean) => {
  const next = new Set(selectedReviewIds.value)
  if (checked) {
    next.add(id)
  } else {
    next.delete(id)
  }
  selectedReviewIds.value = next
}

const toggleSelectAllReviews = (checked: boolean) => {
  if (!checked) {
    selectedReviewIds.value = new Set()
    return
  }
  const next = new Set(selectedReviewIds.value)
  visibleCompanyReviews.value.forEach((item) => next.add(item.id))
  selectedReviewIds.value = next
}

const clearReviewSelection = () => {
  selectedReviewIds.value = new Set()
}

watch(serviceSearch, () => {
  if (serviceSearchTimer) {
    clearTimeout(serviceSearchTimer)
  }
  serviceSearchTimer = setTimeout(async () => {
    servicePage.value = 1
    await loadServices()
    serviceSearchTimer = null
  }, 300)
})

watch(serviceCategoryFilter, async () => {
  servicePage.value = 1
  await loadServices()
})

watch(staffSearch, () => {
  if (staffSearchTimer) {
    clearTimeout(staffSearchTimer)
  }
  staffSearchTimer = setTimeout(async () => {
    await loadStaff()
    staffSearchTimer = null
  }, 300)
})

watch(staffCategoryFilter, async () => {
  await loadStaff()
})

const clearStaffFilter = async () => {
  if (!staffSearch.value) {
    return
  }
  staffSearch.value = ''
  if (staffSearchTimer) {
    clearTimeout(staffSearchTimer)
    staffSearchTimer = null
  }
  await loadStaff()
}

const activeMessages = computed(() =>
  activeConversationId.value != null ? messagesByOrder[activeConversationId.value] ?? [] : [],
)

const companyStats = computed(() => {
  const totalServices = serviceTotal.value
  const avgPrice = totalServices ? serviceAveragePrice.value : 0
  const balance = account.value?.balance ?? 0
  const upcoming = companyOrders.value.length
  const inProgress = companyOrders.value.filter((order) => order.status === 'IN_PROGRESS').length
  const staffCount = staffList.value.length
  return {
    totalServices,
    inProgress,
    avgPrice,
    balance,
    upcoming,
    staffCount,
  }
})

const normalizeSearchValue = (value: unknown) => {
  if (value == null) {
    return ''
  }
  return String(value).toLowerCase()
}

const matchesOrderSearch = (order: ServiceOrderItem, keyword: string) => {
  if (!keyword) {
    return true
  }
  const target = keyword.toLowerCase()
  const fields = [
    order.serviceName,
    order.companyName,
    order.contact,
    order.username,
    order.customerContactPhone,
    order.customerAddress,
    order.specialRequest,
    order.serviceAddress,
    order.progressNote,
    order.refundReason,
    order.assignedWorker,
    order.workerContact,
  ]
  return fields.some((field) => normalizeSearchValue(field).includes(target))
}

const matchesReviewSearch = (review: ServiceReviewItem, keyword: string) => {
  if (!keyword) {
    return true
  }
  const target = keyword.toLowerCase()
  const fields = [review.serviceName, review.username, review.content, review.companyReply]
  return fields.some((field) => normalizeSearchValue(field).includes(target))
}

const visibleCompanyOrders = computed(() => {
  const keyword = appointmentSearch.value.trim()
  if (!keyword) {
    return companyOrders.value
  }
  return companyOrders.value.filter((order) => matchesOrderSearch(order, keyword))
})

const hasAppointmentFilter = computed(() => appointmentSearch.value.trim().length > 0)

const allVisibleOrdersSelected = computed(
  () =>
    visibleCompanyOrders.value.length > 0 &&
    visibleCompanyOrders.value.every((item) => selectedOrderIds.value.has(item.id)),
)

const selectedOrderCount = computed(() => selectedOrderIds.value.size)
const hasOrderSelection = computed(() => selectedOrderIds.value.size > 0)

const visibleCompanyReviews = computed(() => {
  const keyword = reviewSearch.value.trim()
  if (!keyword) {
    return companyReviews.value
  }
  return companyReviews.value.filter((item) => matchesReviewSearch(item, keyword))
})

const hasReviewFilter = computed(() => reviewSearch.value.trim().length > 0)

const allVisibleReviewsSelected = computed(
  () =>
    visibleCompanyReviews.value.length > 0 &&
    visibleCompanyReviews.value.every((item) => selectedReviewIds.value.has(item.id)),
)

const selectedReviewCount = computed(() => selectedReviewIds.value.size)
const hasReviewSelection = computed(() => selectedReviewIds.value.size > 0)

const reviewStats = computed(() => {
  if (!companyReviews.value.length) {
    return { average: '0.0', positiveRate: '0%', pinnedCount: 0 }
  }
  const total = companyReviews.value.reduce((sum, item) => sum + item.rating, 0)
  const average = (total / companyReviews.value.length).toFixed(1)
  const positive = companyReviews.value.filter((item) => item.rating >= 4).length
  const positiveRate = `${Math.round((positive / companyReviews.value.length) * 100)}%`
  const pinnedCount = companyReviews.value.filter((item) => item.pinned).length
  return { average, positiveRate, pinnedCount }
})

watch(companyOrders, pruneOrderSelection)
watch(visibleCompanyOrders, pruneOrderSelection)
watch(companyReviews, pruneReviewSelection)
watch(visibleCompanyReviews, pruneReviewSelection)

const selectedServiceCount = computed(() => selectedServiceIds.value.size)
const hasServiceSelection = computed(() => selectedServiceIds.value.size > 0)
const allVisibleServicesSelected = computed(
  () => services.value.length > 0 && services.value.every((item) => selectedServiceIds.value.has(item.id)),
)
const hasServiceFilter = computed(
  () => serviceSearch.value.trim().length > 0 || serviceCategoryFilter.value !== 'all',
)
const serviceTotalPages = computed(() => Math.max(1, Math.ceil((serviceTotal.value || 0) / serviceSize.value)))
const servicePageStart = computed(() =>
  serviceTotal.value === 0 ? 0 : (servicePage.value - 1) * serviceSize.value + 1,
)
const servicePageEnd = computed(() =>
  serviceTotal.value === 0 ? 0 : Math.min(serviceTotal.value, servicePage.value * serviceSize.value),
)

const handleSelectServiceCategory = (categoryId: number | 'all') => {
  if (serviceCategoryFilter.value === categoryId) {
    return
  }
  serviceCategoryFilter.value = categoryId
}

const changeServicePage = (page: number) => {
  const totalPages = serviceTotalPages.value
  const target = Math.min(Math.max(page, 1), totalPages)
  if (target === servicePage.value) {
    return
  }
  servicePage.value = target
  loadServices()
}

const logout = () => {
  sessionStorage.removeItem(AUTH_TOKEN_KEY)
  sessionStorage.removeItem(AUTH_ACCOUNT_KEY)
  sessionStorage.removeItem(AUTH_ROLE_KEY)
  router.push({ name: 'login' })
}

const resetServiceForm = () => {
  serviceForm.name = ''
  serviceForm.unit = ''
  serviceForm.price = 0
  serviceForm.contact = ''
  serviceForm.serviceTime = '按需预约'
  serviceForm.description = ''
  serviceForm.categoryId = serviceCategories.value[0]?.id ?? 0
  editingServiceId.value = null
}

const openServiceForm = (item?: HousekeepServiceItem) => {
  if (item) {
    serviceForm.name = item.name
    serviceForm.unit = item.unit
    serviceForm.price = item.price
    serviceForm.contact = item.contact
    serviceForm.serviceTime = item.serviceTime || '按需预约'
    serviceForm.description = item.description || ''
    serviceForm.categoryId = item.categoryId ?? serviceCategories.value[0]?.id ?? 0
    editingServiceId.value = item.id
  } else {
    resetServiceForm()
  }
  serviceFormVisible.value = true
}

const closeServiceForm = () => {
  serviceFormVisible.value = false
  resetServiceForm()
}

const submitServiceForm = async () => {
  if (!serviceForm.name.trim() || !serviceForm.unit.trim() || !serviceForm.contact.trim()) {
    window.alert('请完整填写服务信息')
    return
  }
  if (!serviceForm.serviceTime.trim()) {
    window.alert('请填写服务时间')
    return
  }
  if (serviceForm.price <= 0) {
    window.alert('请填写有效的价格')
    return
  }
  if (!serviceForm.categoryId) {
    window.alert('请选择服务分类')
    return
  }
  serviceSaving.value = true
  try {
    const payload: CompanyServicePayload = {
      name: serviceForm.name.trim(),
      unit: serviceForm.unit.trim(),
      price: Number(serviceForm.price),
      contact: serviceForm.contact.trim(),
      serviceTime: serviceForm.serviceTime.trim(),
      description: serviceForm.description?.trim() || '',
      categoryId: serviceForm.categoryId,
    }
    if (editingServiceId.value) {
      await updateCompanyService(editingServiceId.value, payload)
    } else {
      await createCompanyService(payload)
    }
    await loadServices()
    await loadAccount()
    closeServiceForm()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '保存服务失败')
  } finally {
    serviceSaving.value = false
  }
}

const handleDeleteService = async (item: HousekeepServiceItem) => {
  if (!window.confirm(`确认删除服务“${item.name}”？`)) return
  try {
    await deleteCompanyService(item.id)
    if (selectedServiceIds.value.has(item.id)) {
      const next = new Set(selectedServiceIds.value)
      next.delete(item.id)
      selectedServiceIds.value = next
    }
    await loadServices()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

const handleBulkDeleteServices = async () => {
  if (!selectedServiceIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedServiceIds.value.size} 项服务？`)) {
    return
  }
  try {
    const ids = Array.from(selectedServiceIds.value)
    await deleteCompanyServices(ids)
    clearServiceSelection()
    await loadServices()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

const resetStaffForm = () => {
  staffForm.name = ''
  staffForm.contact = ''
  staffForm.categoryId = serviceCategories.value[0]?.id ?? 0
  staffForm.notes = ''
  editingStaffId.value = null
}

const openStaffForm = (item?: CompanyStaffItem) => {
  if (item) {
    staffForm.name = item.name
    staffForm.contact = item.contact
    staffForm.categoryId = item.categoryId ?? serviceCategories.value[0]?.id ?? 0
    staffForm.notes = item.notes || ''
    editingStaffId.value = item.id
  } else {
    resetStaffForm()
  }
  staffFormVisible.value = true
}

const closeStaffForm = () => {
  staffFormVisible.value = false
  resetStaffForm()
}

const submitStaffForm = async () => {
  if (!staffForm.name.trim() || !staffForm.contact.trim()) {
    window.alert('请填写完整的人员姓名和联系方式')
    return
  }
  if (!staffForm.categoryId) {
    window.alert('请选择服务类别')
    return
  }
  staffSaving.value = true
  try {
    const payload: CompanyStaffPayload = {
      name: staffForm.name.trim(),
      contact: staffForm.contact.trim(),
      categoryId: staffForm.categoryId,
      notes: staffForm.notes?.trim() || undefined,
    }
    if (editingStaffId.value) {
      await updateCompanyStaff(editingStaffId.value, payload)
    } else {
      await createCompanyStaff(payload)
    }
    await loadStaff()
    closeStaffForm()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '保存人员信息失败')
  } finally {
    staffSaving.value = false
  }
}

const handleDeleteStaff = async (item: CompanyStaffItem) => {
  if (!window.confirm(`确认移除人员“${item.name}”？`)) return
  try {
    await deleteCompanyStaff(item.id)
    const next = new Set(selectedStaffIds.value)
    next.delete(item.id)
    selectedStaffIds.value = next
    await loadStaff()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

const handleBulkDeleteStaff = async () => {
  if (!selectedStaffIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedStaffIds.value.size} 位人员吗？`)) {
    return
  }
  try {
    await deleteCompanyStaffBatch(Array.from(selectedStaffIds.value))
    clearStaffSelection()
    await loadStaff()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除失败')
  }
}

const openAssignmentModal = async (order: ServiceOrderItem) => {
  if (!order.categoryId) {
    window.alert('该服务未设置分类，无法指派人员')
    return
  }
  assignmentModalVisible.value = true
  assignmentModalOrder.value = order
  assignmentModalSelection.value = order.assignedStaffId ?? null
  assignmentModalError.value = null
  assignmentModalLoading.value = true
  try {
    assignmentModalStaff.value = await fetchCompanyStaff({ categoryId: order.categoryId })
  } catch (error) {
    assignmentModalError.value = error instanceof Error ? error.message : '加载人员列表失败'
    assignmentModalStaff.value = []
  } finally {
    assignmentModalLoading.value = false
  }
}

const closeAssignmentModal = () => {
  assignmentModalVisible.value = false
  assignmentModalOrder.value = null
  assignmentModalSelection.value = null
  assignmentModalStaff.value = []
  assignmentModalError.value = null
}

const confirmAssignment = async () => {
  const order = assignmentModalOrder.value
  if (!order) {
    return
  }
  if (assignmentModalSelection.value == null) {
    window.alert('请选择要指派的人员')
    return
  }
  assignmentSubmitting.value = true
  assignmentModalError.value = null
  try {
    const updated = await assignCompanyStaff(assignmentModalSelection.value, order.id)
    const index = companyOrders.value.findIndex((item) => item.id === updated.id)
    if (index >= 0) {
      companyOrders.value.splice(index, 1, updated)
    }
    progressNoteEdits[updated.id] = updated.progressNote || ''
    await loadStaff()
    closeAssignmentModal()
    window.alert('指派成功')
  } catch (error) {
    assignmentModalError.value = error instanceof Error ? error.message : '指派失败，请稍后再试'
  } finally {
    assignmentSubmitting.value = false
  }
}

const handleDeleteOrder = async (order: ServiceOrderItem) => {
  if (!window.confirm(`确认删除预约“${order.serviceName}”（${order.username}）吗？`)) {
    return
  }
  try {
    await deleteCompanyOrder(order.id)
    const next = new Set(selectedOrderIds.value)
    next.delete(order.id)
    selectedOrderIds.value = next
    await loadCompanyOrders()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除预约失败')
  }
}

const handleBulkDeleteOrders = async () => {
  if (!selectedOrderIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedOrderIds.value.size} 条预约记录吗？`)) {
    return
  }
  try {
    await deleteCompanyOrders(Array.from(selectedOrderIds.value))
    clearOrderSelection()
    await loadCompanyOrders()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '批量删除失败')
  }
}

const refreshAppointments = async () => {
  await Promise.all([loadCompanyOrders(), loadStaff()])
}

const saveOrderProgress = async (order: ServiceOrderItem, status: ServiceOrderItem['status']) => {
  progressSaving[order.id] = true
  try {
    const payload: UpdateOrderProgressPayload = {
      status,
      progressNote: progressNoteEdits[order.id]?.trim() || undefined,
    }
    const updated = await updateCompanyOrderProgress(order.id, payload)
    const index = companyOrders.value.findIndex((item) => item.id === updated.id)
    if (index >= 0) {
      companyOrders.value.splice(index, 1, updated)
    }
    progressNoteEdits[updated.id] = updated.progressNote || ''
    window.alert('预约进度已更新')
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '更新失败，请稍后再试')
  } finally {
    progressSaving[order.id] = false
  }
}

const updateConversationSummary = (
  orderId: number,
  transform: (item: CompanyConversationItem) => CompanyConversationItem,
) => {
  const current = conversationSummaries.value
  const index = current.findIndex((item) => item.orderId === orderId)
  if (index === -1) {
    return
  }
  const next = [...current]
  const target = next[index]
  if (!target) {
    return
  }
  next.splice(index, 1, transform({ ...target }))
  conversationSummaries.value = next
}

const loadCompanyReviews = async () => {
  reviewsLoading.value = true
  try {
    companyReviews.value = await fetchCompanyReviews()
    pruneReviewSelection()
  } catch (error) {
    console.error(error)
  } finally {
    reviewsLoading.value = false
  }
}

const handleDeleteReview = async (item: ServiceReviewItem) => {
  if (!window.confirm(`确认删除服务“${item.serviceName}”的这条评价吗？`)) {
    return
  }
  try {
    await deleteCompanyReview(item.id)
    const next = new Set(selectedReviewIds.value)
    next.delete(item.id)
    selectedReviewIds.value = next
    await loadCompanyReviews()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '删除评价失败')
  }
}

const handleBulkDeleteReviews = async () => {
  if (!selectedReviewIds.value.size) {
    return
  }
  if (!window.confirm(`确认删除选中的 ${selectedReviewIds.value.size} 条评价吗？`)) {
    return
  }
  try {
    await deleteCompanyReviews(Array.from(selectedReviewIds.value))
    clearReviewSelection()
    await loadCompanyReviews()
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '批量删除评价失败')
  }
}

const loadConversationSummaries = async (): Promise<CompanyConversationItem[]> => {
  conversationsLoading.value = true
  try {
    const items = await fetchCompanyConversations()
    conversationSummaries.value = items
    if (!items.length) {
      activeConversationId.value = null
      stopMessagePolling()
    } else if (
      activeConversationId.value == null ||
      !items.some((item) => item.orderId === activeConversationId.value)
    ) {
      const first = items[0]
      if (first) {
        activeConversationId.value = first.orderId
      }
    }
    return items
  } catch (error) {
    console.error(error)
    return []
  } finally {
    conversationsLoading.value = false
  }
}

const loadMessagesForOrder = async (
  orderId: number,
  options: { since?: string | null; silent?: boolean } = {},
) => {
  if (!options.silent) {
    messagesLoading.value = true
  }
  try {
    const params = options.since ? { since: options.since } : undefined
    const fetched = await fetchCompanyMessages(orderId, params)
    if (fetched.length) {
      const existing = [...(messagesByOrder[orderId] ?? [])]
      const existingIds = new Set(existing.map((item) => item.id))
      const combined = options.since
        ? [...existing, ...fetched.filter((item) => !existingIds.has(item.id))]
        : fetched
      combined.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
      if (combined.length) {
        const lastMessage = combined[combined.length - 1]
        if (lastMessage) {
          messagesByOrder[orderId] = combined
          lastMessageTimestamps[orderId] = lastMessage.createdAt
          updateConversationSummary(orderId, (item) => ({
            ...item,
            lastMessage: lastMessage.content,
            lastMessageAt: lastMessage.createdAt,
            unreadCount: 0,
          }))
        }
      }
    } else if (!messagesByOrder[orderId]) {
      messagesByOrder[orderId] = []
    }
    try {
      await markCompanyConversationRead(orderId)
      updateConversationSummary(orderId, (item) => ({ ...item, unreadCount: 0 }))
    } catch (error) {
      console.error(error)
    }
  } catch (error) {
    console.error(error)
  } finally {
    if (!options.silent) {
      messagesLoading.value = false
    }
  }
}

const selectConversation = async (orderId: number) => {
  activeConversationId.value = orderId
  await loadMessagesForOrder(orderId)
  startMessagePolling(orderId)
}

const refreshActiveMessages = async () => {
  if (activeConversationId.value == null) {
    return
  }
  await loadMessagesForOrder(activeConversationId.value)
}

const handleSendMessage = async (payload: { orderId: number; content: string }) => {
  const trimmed = payload.content.trim()
  if (!trimmed) {
    window.alert('请输入消息内容')
    return
  }
  messageSending.value = true
  try {
    const message = await sendCompanyMessage(payload.orderId, { content: trimmed })
    const existing = [...(messagesByOrder[payload.orderId] ?? [])]
    existing.push(message)
    existing.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
    messagesByOrder[payload.orderId] = existing
    lastMessageTimestamps[payload.orderId] = message.createdAt
    updateConversationSummary(payload.orderId, (item) => ({
      ...item,
      lastMessage: message.content,
      lastMessageAt: message.createdAt,
      unreadCount: 0,
    }))
  } catch (error) {
    window.alert(error instanceof Error ? error.message : '发送失败，请稍后再试')
  } finally {
    messageSending.value = false
  }
}

const stopMessagePolling = () => {
  if (messagePollHandle.value != null) {
    window.clearInterval(messagePollHandle.value)
    messagePollHandle.value = null
  }
}

const startMessagePolling = (orderId: number) => {
  stopMessagePolling()
  messagePollHandle.value = window.setInterval(() => {
    const since = lastMessageTimestamps[orderId]
    if (since) {
      loadMessagesForOrder(orderId, { since, silent: true })
    } else {
      loadMessagesForOrder(orderId, { silent: true })
    }
  }, 10000)
}

const switchSection = async (key: SectionKey) => {
  if (activeSection.value === 'messages' && key !== 'messages') {
    stopMessagePolling()
  }
  activeSection.value = key
  if (key === 'profile') {
    await loadAccount()
    return
  }
  if (key === 'services') {
    await loadServices()
  }
  if (key === 'appointments') {
    await refreshAppointments()
  }
  if (key === 'staff') {
    await loadStaff()
  }
  if (key === 'reviews') {
    await loadCompanyReviews()
  }
  if (key === 'messages') {
    const items = await loadConversationSummaries()
    const target = activeConversationId.value ?? items[0]?.orderId ?? null
    if (target != null) {
      await selectConversation(target)
    } else {
      stopMessagePolling()
    }
  }
}

const handleRefreshConversations = async () => {
  const items = await loadConversationSummaries()
  if (activeSection.value === 'messages') {
    const target = activeConversationId.value ?? items[0]?.orderId ?? null
    if (target != null) {
      await selectConversation(target)
    }
  }
}

const assignServiceData = (data: { items: HousekeepServiceItem[]; total: number; averagePrice?: number }) => {
  services.value = data.items
  serviceTotal.value = data.total
  serviceAveragePrice.value = data.averagePrice ?? 0
  pruneServiceSelection()
}

const loadServices = async () => {
  serviceLoading.value = true
  try {
    const keyword = serviceSearch.value.trim()
    const params: { keyword?: string; categoryId?: number; page: number; size: number } = {
      page: servicePage.value,
      size: serviceSize.value,
    }
    if (keyword) {
      params.keyword = keyword
    }
    if (serviceCategoryFilter.value !== 'all') {
      params.categoryId = serviceCategoryFilter.value
    }
    let result = await fetchCompanyServices(params)
    const totalPages = Math.max(1, Math.ceil((result.total || 0) / serviceSize.value))
    if (servicePage.value > totalPages) {
      servicePage.value = totalPages
      result = await fetchCompanyServices({ ...params, page: servicePage.value })
    }
    assignServiceData(result)
  } catch (error) {
    console.error(error)
    services.value = []
    serviceTotal.value = 0
    serviceAveragePrice.value = 0
    clearServiceSelection()
  } finally {
    serviceLoading.value = false
  }
}

const loadStaff = async () => {
  staffLoading.value = true
  try {
    const keyword = staffSearch.value.trim()
    const params: { keyword?: string; categoryId?: number } = {}
    if (keyword) {
      params.keyword = keyword
    }
    if (staffCategoryFilter.value !== 'all') {
      params.categoryId = staffCategoryFilter.value
    }
    const query = Object.keys(params).length ? params : undefined
    staffList.value = await fetchCompanyStaff(query)
    pruneStaffSelection()
  } catch (error) {
    console.error(error)
    staffList.value = []
    clearStaffSelection()
  } finally {
    staffLoading.value = false
  }
}

const loadServiceCategories = async () => {
  try {
    serviceCategories.value = await fetchServiceCategories()
  } catch (error) {
    console.error(error)
    serviceCategories.value = []
  }
  if (serviceCategories.value.length) {
    const firstCategory = serviceCategories.value[0]!
    if (!serviceForm.categoryId) {
      serviceForm.categoryId = firstCategory.id
    }
    if (!staffForm.categoryId) {
      staffForm.categoryId = firstCategory.id
    }
  }
  if (
    serviceCategoryFilter.value !== 'all' &&
    !serviceCategories.value.some((item) => item.id === serviceCategoryFilter.value)
  ) {
    serviceCategoryFilter.value = 'all'
  }
}

const loadCompanyOrders = async () => {
  appointmentsLoading.value = true
  try {
    const result = await fetchCompanyOrders()
    companyOrders.value = result
    result.forEach((item) => {
      progressNoteEdits[item.id] = item.progressNote || ''
    })
    pruneOrderSelection()
  } catch (error) {
    console.error(error)
  } finally {
    appointmentsLoading.value = false
  }
}

const loadAccount = async () => {
  try {
    account.value = await fetchCurrentAccount()
  } catch (error) {
    console.error(error)
  }
}

const statusText = (status: ServiceOrderItem['status']) => {
  switch (status) {
    case 'SCHEDULED':
      return '待上门'
    case 'IN_PROGRESS':
      return '服务中'
    case 'PENDING':
      return '等待用户安排'
    case 'COMPLETED':
      return '已完成'
    case 'REFUND_REQUESTED':
      return '退款审核中'
    case 'REFUND_APPROVED':
      return '已退款'
    case 'REFUND_REJECTED':
      return '退款被拒'
    default:
      return status
  }
}

const formatDateTime = (value: string) => {
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString()
}

onMounted(async () => {
  await loadServiceCategories()
  await Promise.all([loadAccount(), loadServices(), loadStaff()])
  await refreshAppointments()
})

onUnmounted(() => {
  if (serviceSearchTimer) {
    clearTimeout(serviceSearchTimer)
    serviceSearchTimer = null
  }
  if (staffSearchTimer) {
    clearTimeout(staffSearchTimer)
    staffSearchTimer = null
  }
  stopMessagePolling()
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
  background: radial-gradient(600px circle at 90% 10%, rgba(255, 255, 255, 0.2), transparent 60%);
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

.welcome {
  font-weight: 600;
}

.wallet {
  font-weight: 700;
  color: #fef08a;
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
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.16);
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 6px;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(12px);
}

.stat-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(240px circle at 85% 15%, rgba(255, 255, 255, 0.25), transparent 60%);
  z-index: 0;
}

.stat-card > * {
  position: relative;
  z-index: 1;
}

.stat-label {
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--brand-text-muted);
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  color: var(--brand-text);
}

.stat-helper {
  font-size: 13px;
  color: var(--brand-text-muted);
}

.stat-card.accent {
  background: linear-gradient(150deg, rgba(37, 99, 235, 0.12), rgba(37, 99, 235, 0.04));
  border-color: rgba(37, 99, 235, 0.2);
}

.stat-card.primary {
  background: linear-gradient(150deg, rgba(16, 185, 129, 0.12), rgba(16, 185, 129, 0.04));
  border-color: rgba(16, 185, 129, 0.2);
}

.stat-card.success {
  background: linear-gradient(150deg, rgba(37, 99, 235, 0.12), rgba(16, 185, 129, 0.04));
  border-color: rgba(37, 99, 235, 0.2);
}

.stat-card.warning {
  background: linear-gradient(150deg, rgba(245, 158, 11, 0.12), rgba(245, 158, 11, 0.04));
  border-color: rgba(245, 158, 11, 0.2);
}

.dashboard-main {
  display: grid;
  grid-template-columns: minmax(220px, 260px) 1fr;
  gap: 28px;
  align-items: flex-start;
}

.sidebar {
  background: rgba(255, 255, 255, 0.8);
  border-radius: calc(var(--brand-radius) + 2px);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 24px 50px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(16px);
  padding: 28px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.sidebar-item {
  border: none;
  background: transparent;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--brand-text-muted);
  cursor: pointer;
  transition: color 0.2s ease, background-color 0.2s ease, transform 0.2s ease;
}

.sidebar-item .sidebar-icon {
  font-size: 20px;
}

.sidebar-item:hover {
  color: var(--brand-success);
  background: rgba(16, 185, 129, 0.08);
  transform: translateX(4px);
}

.sidebar-item.active {
  color: var(--brand-success);
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.18), rgba(37, 99, 235, 0.08));
  box-shadow: inset 0 0 0 1px rgba(16, 185, 129, 0.2);
}

.content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.panel {
  background: rgba(255, 255, 255, 0.9);
  border-radius: calc(var(--brand-radius) + 4px);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.12);
  padding: 28px 32px;
  backdrop-filter: blur(14px);
}

.profile-panel {
  background: linear-gradient(145deg, rgba(16, 185, 129, 0.1), rgba(59, 130, 246, 0.08));
  border-color: rgba(16, 185, 129, 0.2);
}

.panel.immersive-panel {
  padding: clamp(24px, 3vw, 36px);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.panel-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
}

.panel-header p {
  margin: 8px 0 0;
  color: var(--brand-text-muted);
  font-size: 14px;
}

.service-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.metric-inline {
  font-weight: 700;
  color: var(--brand-primary);
}

.review-service {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pinned-badge {
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(250, 204, 21, 0.2);
  color: #b45309;
  font-size: 12px;
  font-weight: 600;
}

.review-rating {
  margin-top: 6px;
  color: var(--brand-text-muted);
  font-size: 13px;
}

.review-author {
  font-weight: 600;
}

.review-text {
  margin: 0;
  white-space: pre-wrap;
  color: var(--brand-text);
}

.staff-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.search-input {
  min-width: 220px;
  padding: 8px 12px;
  border-radius: 10px;
  border: 1px solid rgba(148, 163, 184, 0.4);
  background-color: rgba(255, 255, 255, 0.9);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-input:focus {
  outline: none;
  border-color: var(--brand-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

.form-card {
  margin-bottom: 24px;
  padding: 24px 28px;
  background: rgba(248, 250, 255, 0.9);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: calc(var(--brand-radius) + 2px);
  box-shadow: 0 20px 38px rgba(37, 99, 235, 0.12);
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
  background: rgba(37, 99, 235, 0.08);
  color: var(--brand-primary);
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.secondary-button:enabled:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.18);
}

.secondary-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.secondary-button.danger {
  border-color: rgba(239, 68, 68, 0.4);
  background: rgba(239, 68, 68, 0.08);
  color: var(--brand-danger);
}

.secondary-button.danger:enabled:hover {
  box-shadow: 0 12px 24px rgba(239, 68, 68, 0.18);
  border-color: var(--brand-danger);
  background: rgba(239, 68, 68, 0.14);
  color: var(--brand-danger);
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.service-card {
  border-radius: calc(var(--brand-radius) + 2px);
  padding: 20px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 20px 36px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.service-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 28px 50px rgba(16, 185, 129, 0.18);
}

.service-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
}

.service-company,
.order-subtext {
  margin: 0;
  font-size: 14px;
  color: var(--brand-text-muted);
}

.order-subtext.highlight {
  color: var(--brand-primary);
  font-weight: 600;
}

.progress-input {
  width: 100%;
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: var(--brand-radius);
  padding: 8px 10px;
  font-size: 13px;
  background: rgba(248, 250, 255, 0.92);
}

.staff-select {
  width: 100%;
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: var(--brand-radius);
  padding: 8px 10px;
  font-size: 13px;
  background: rgba(248, 250, 255, 0.92);
  margin-bottom: 6px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.status-scheduled {
  background: linear-gradient(135deg, #6366f1, #4338ca);
}

.status-in_progress {
  background: linear-gradient(135deg, #14b8a6, #0f766e);
}

.status-pending {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
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

.status-assigned {
  background: linear-gradient(135deg, #f97316, #ea580c);
}

.status-available {
  background: linear-gradient(135deg, #10b981, #059669);
}

.category-menu {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
  padding: 8px 0;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
}

.service-category-menu {
  margin-bottom: 20px;
}

.category-chip {
  border: none;
  background: none;
  font-size: 14px;
  font-weight: 600;
  color: rgba(30, 41, 59, 0.7);
  padding: 4px 0;
  position: relative;
  cursor: pointer;
}

.category-chip.active {
  color: #2563eb;
}

.category-chip.active::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -6px;
  height: 2px;
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  border-radius: 999px;
}

.category-empty {
  margin: 0;
  color: rgba(148, 163, 184, 0.85);
  font-size: 13px;
}

.chip-count {
  margin-left: 6px;
  font-size: 12px;
  color: rgba(148, 163, 184, 0.9);
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  z-index: 2000;
}

.modal-card {
  width: min(720px, 100%);
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.18);
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
}

.modal-body {
  padding: 0 24px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

.close-button {
  border: none;
  background: rgba(148, 163, 184, 0.2);
  border-radius: 999px;
  width: 32px;
  height: 32px;
  font-size: 18px;
  cursor: pointer;
}

.assignment-columns {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
}

.staff-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.radio-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: 12px;
}

.modal-error {
  color: #dc2626;
  font-size: 13px;
}

.modal-subtitle {
  font-size: 14px;
  color: rgba(15, 23, 42, 0.7);
}

.muted-text {
  color: rgba(100, 116, 139, 0.85);
  font-size: 13px;
}

.table-wrapper {
  overflow-x: auto;
  border-radius: calc(var(--brand-radius) + 2px);
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.data-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: rgba(255, 255, 255, 0.95);
  table-layout: fixed;
}

.data-table thead th {
  background: rgba(16, 185, 129, 0.1);
  color: var(--brand-text);
  font-weight: 600;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
  text-align: left;
}

.data-table tbody td {
  padding: 14px 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.15);
  vertical-align: top;
  color: var(--brand-text);
  text-align: left;
}

.data-table tbody tr:last-child td {
  border-bottom: none;
}

.data-table tbody tr:hover td {
  background: rgba(16, 185, 129, 0.06);
}

.table-checkbox {
  width: 44px;
  text-align: center;
  padding-left: 0;
  padding-right: 0;
}

.table-checkbox input[type='checkbox'] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  margin: 0;
}

.table-actions {
  width: 220px;
}

.actions-inline {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.link-button {
  background: none;
  border: none;
  padding: 0;
  color: var(--brand-primary);
  font-weight: 600;
  cursor: pointer;
  transition: color 0.2s ease;
}

.link-button.danger {
  color: var(--brand-danger);
}

.link-button:hover {
  color: var(--brand-primary-dark);
  text-decoration: underline;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-field-full {
  grid-column: 1 / -1;
}

.form-field input,
.form-field select,
.form-field textarea {
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: 12px;
  padding: 10px 12px;
  background: rgba(248, 250, 255, 0.9);
  font-size: 14px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-field input:focus,
.form-field select:focus,
.form-field textarea:focus {
  outline: none;
  border-color: var(--brand-success);
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.18);
}

.empty-row {
  text-align: center;
  color: var(--brand-text-muted);
  padding: 16px 0;
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  padding: 12px 0 0;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
  font-size: 0.95rem;
  color: rgba(30, 41, 59, 0.7);
}

.pagination .secondary-button {
  min-width: 88px;
}

.service-hint {
  color: rgba(15, 23, 42, 0.55);
  font-size: 0.9rem;
}

@media (max-width: 1024px) {
  .dashboard {
    padding: 24px 24px 40px;
  }

  .dashboard-main {
    grid-template-columns: 1fr;
  }

  .sidebar {
    flex-direction: row;
    overflow-x: auto;
  }

  .header-actions {
    flex-wrap: wrap;
  }
}

@media (max-width: 720px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    align-self: stretch;
    justify-content: space-between;
  }

  .account-avatar {
    width: 48px;
    height: 48px;
  }

  .panel,
  .form-card {
    padding: 24px;
  }
}
</style>


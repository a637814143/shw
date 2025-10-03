<template>
  <div class="admin-home-container">
    <!-- 顶部导航栏 -->
    <header class="top-header">
      <div class="header-content">
        <div class="logo-section">
          <h1 class="logo">家政服务平台</h1>
        </div>
        <div class="breadcrumb">
          <span>首页 / 系统首页</span>
        </div>
        <div class="user-section">
          <div class="user-info" @click="toggleUserDropdown">
            <div class="user-avatar">
              <img v-if="userAvatar" :src="userAvatar" alt="头像" class="avatar-image">
              <div v-else class="avatar-placeholder">👤</div>
            </div>
            <span class="username">{{ userInfo.username || '管理员' }}</span>
            <div class="dropdown-arrow" :class="{ active: showUserDropdown }">▼</div>
          </div>
          <!-- 用户下拉菜单 -->
          <div v-if="showUserDropdown" class="user-dropdown" @click.stop>
            <div class="dropdown-item" @click="handlePersonalProfile">
              <span>个人资料</span>
            </div>
            <div class="dropdown-item" @click="handleChangePassword">
              <span>修改密码</span>
            </div>
            <div class="dropdown-item" @click="logout">
              <span>退出登录</span>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- 主要内容区域容器 -->
    <div class="content-wrapper">
      <!-- 侧边导航菜单 -->
      <nav class="sidebar-nav">
        <ul class="nav-menu">
          <li class="nav-item" :class="{ active: activeMenu === 'home' }" @click="setActiveMenu('home')">
            <div class="nav-icon">🏠</div>
            <span class="nav-text">系统首页</span>
          </li>
          
          <li class="nav-item" :class="{ active: activeMenu === 'statistics' }" @click="setActiveMenu('statistics')">
            <div class="nav-icon">📊</div>
            <span class="nav-text">数据统计</span>
          </li>
          
          <li class="nav-item" :class="{ active: isServiceMenuActive }" @click="toggleServiceMenu">
            <div class="nav-icon">📋</div>
            <span class="nav-text">信息管理</span>
            <div class="expand-arrow" :class="{ expanded: isServiceMenuExpanded }">▲</div>
          </li>
          <!-- 信息管理子菜单 -->
          <div v-show="isServiceMenuExpanded" class="sub-menu">
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'appointment' }" @click="setActiveMenu('appointment')">
              <div class="nav-icon">📅</div>
              <span class="nav-text">服务预约</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'services' }" @click="setActiveMenu('services')">
              <div class="nav-icon">🧹</div>
              <span class="nav-text">家政服务</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'withdrawal' }" @click="setActiveMenu('withdrawal')">
              <div class="nav-icon">💰</div>
              <span class="nav-text">提现记录</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'category' }" @click="setActiveMenu('category')">
              <div class="nav-icon">📂</div>
              <span class="nav-text">服务分类</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'notice' }" @click="setActiveMenu('notice')">
              <div class="nav-icon">📢</div>
              <span class="nav-text">系统公告</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'evaluation' }" @click="setActiveMenu('evaluation')">
              <div class="nav-icon">⭐</div>
              <span class="nav-text">服务评价</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'tips' }" @click="setActiveMenu('tips')">
              <div class="nav-icon">💡</div>
              <span class="nav-text">居家贴士</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'carousel' }" @click="setActiveMenu('carousel')">
              <div class="nav-icon">🎠</div>
              <span class="nav-text">轮播图信息</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'favorites' }" @click="setActiveMenu('favorites')">
              <div class="nav-icon">❤️</div>
              <span class="nav-text">收藏信息</span>
            </li>
          </div>
          
          <li class="nav-item" :class="{ active: isUserMenuActive }" @click="toggleUserMenu">
            <div class="nav-icon">👥</div>
            <span class="nav-text">用户管理</span>
            <div class="expand-arrow" :class="{ expanded: isUserMenuExpanded }">▲</div>
          </li>
          <!-- 用户管理子菜单 -->
          <div v-show="isUserMenuExpanded" class="sub-menu">
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'providerCert' }" @click="setActiveMenu('providerCert')">
              <div class="nav-icon">✅</div>
              <span class="nav-text">服务者认证</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'normalUser' }" @click="setActiveMenu('normalUser')">
              <div class="nav-icon">👤</div>
              <span class="nav-text">普通用户</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'housekeepingProvider' }" @click="setActiveMenu('housekeepingProvider')">
              <div class="nav-icon">🧹</div>
              <span class="nav-text">家政服务者</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'adminUser' }" @click="setActiveMenu('adminUser')">
              <div class="nav-icon">⚙️</div>
              <span class="nav-text">管理员</span>
            </li>
          </div>
        </ul>
      </nav>

      <!-- 主内容区域 -->
      <main class="main-content">
        <!-- 系统首页内容 -->
        <div v-if="activeMenu === 'home'" class="content-section">
          <!-- 欢迎信息区域 -->
          <div class="welcome-section">
            <div class="welcome-text">
              <span>您好! {{ userInfo.username || '管理员' }}, 欢迎使用本系统!</span>
            </div>
            <div class="status-badge">
              <div class="status-icon">✓</div>
              <span class="status-text">管理员账号</span>
            </div>
          </div>

          <!-- 系统公告区域 -->
          <div class="announcements-section">
            <h2 class="section-title">系统公告</h2>
            <div class="announcements-list">
              <div class="announcement-item">
                <div class="announcement-bullet"></div>
                <div class="announcement-content">
                  <div class="announcement-title">系统维护通知</div>
                  <div class="announcement-description">
                    系统将于2025年5月20日凌晨2:00-4:00进行例行维护，期间可能会出现短暂的服务不可用。请提前做好准备，感谢您的理解与支持。
                  </div>
                  <div class="announcement-time">2025-05-15 10:30:00</div>
                </div>
              </div>
              
              <div class="announcement-item">
                <div class="announcement-bullet"></div>
                <div class="announcement-content">
                  <div class="announcement-title">新功能上线公告</div>
                  <div class="announcement-description">
                    平台已更新服务评价系统，新增评价标签功能，支持用户对服务进行更精准的评价。同时优化了移动端适配，提升用户体验。
                  </div>
                  <div class="announcement-time">2025-05-10 09:15:23</div>
                </div>
              </div>
              
              <div class="announcement-item">
                <div class="announcement-bullet"></div>
                <div class="announcement-content">
                  <div class="announcement-title">安全提示</div>
                  <div class="announcement-description">
                    近期发现多起钓鱼网站仿冒本平台，请各位用户注意识别官方网址，不要在非官方渠道输入账号密码，定期修改密码，保障账号安全。
                  </div>
                  <div class="announcement-time">2025-05-05 14:20:45</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 数据统计界面 -->
        <div v-if="activeMenu === 'statistics'" class="content-section">
          <div class="statistics-dashboard">
            <!-- 关键指标卡片 -->
            <div class="metrics-cards">
              <div class="metric-card">
                <div class="metric-icon recharge">💰</div>
                <div class="metric-content">
                  <div class="metric-value">13</div>
                  <div class="metric-label">充值次数</div>
                </div>
              </div>
              
              <div class="metric-card">
                <div class="metric-icon withdrawal">💸</div>
                <div class="metric-content">
                  <div class="metric-value">3</div>
                  <div class="metric-label">提现次数</div>
                </div>
              </div>
              
              <div class="metric-card">
                <div class="metric-icon provider">👥</div>
                <div class="metric-content">
                  <div class="metric-value">6</div>
                  <div class="metric-label">服务者数量</div>
                </div>
              </div>
              
              <div class="metric-card">
                <div class="metric-icon user">👤</div>
                <div class="metric-content">
                  <div class="metric-value">6</div>
                  <div class="metric-label">用户数量</div>
                </div>
              </div>
            </div>

            <!-- 图表区域 -->
            <div class="charts-section">
              <!-- 充值趋势图 -->
              <div class="chart-container">
                <div class="chart-header">
                  <h3 class="chart-title">近7天充值趋势图</h3>
                </div>
                <div class="chart-content">
                  <canvas id="rechargeTrendChart" width="600" height="300"></canvas>
                </div>
              </div>

              <!-- 服务预约量统计饼图 -->
              <div class="chart-container">
                <div class="chart-header">
                  <h3 class="chart-title">服务预约量统计</h3>
                  <p class="chart-subtitle">比例图</p>
                </div>
                <div class="chart-content">
                  <canvas id="serviceBookingChart" width="400" height="300"></canvas>
                  <div class="chart-legend">
                    <div class="legend-item">
                      <div class="legend-color" style="background-color: #3b82f6;"></div>
                      <span>沙发保养清洁【包含皮革养护】</span>
                    </div>
                    <div class="legend-item">
                      <div class="legend-color" style="background-color: #10b981;"></div>
                      <span>地板打蜡10平米 (20%)</span>
                    </div>
                    <div class="legend-item">
                      <div class="legend-color" style="background-color: #f59e0b;"></div>
                      <span>深度保洁5小时【全屋大扫除】 (10%)</span>
                    </div>
                    <div class="legend-item">
                      <div class="legend-color" style="background-color: #ef4444;"></div>
                      <span>厨房保养清洁【包含油烟机】 (10%)</span>
                    </div>
                    <div class="legend-item">
                      <div class="legend-color" style="background-color: #06b6d4;"></div>
                      <span>新房精细开荒1平米【包运送垃圾/包验收】 (10%)</span>
                    </div>
                    <div class="legend-item">
                      <div class="legend-color" style="background-color: #059669;"></div>
                      <span>4小时全屋日常保洁【中等户型推荐】快速上门 (20%)</span>
                    </div>
                    <div class="legend-item">
                      <div class="legend-color" style="background-color: #f97316;"></div>
                      <span>全屋清洁【大扫除】 (20%)</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 其他菜单项的内容区域 -->
        <div v-else class="content-section">
          <!-- 服务预约管理 -->
          <div v-if="activeMenu === 'appointment'" class="appointment-management">
            <div class="section-header">
              <h2>服务预约管理</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="searchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入服务名称查询"
                >
                <button class="btn search-btn">查询</button>
                <button class="btn reset-btn" @click="resetSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn batch-delete-btn" @click="batchDelete">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="selectAll"
                        @change="toggleSelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>服务名称</th>
                    <th>数量</th>
                    <th>总金额</th>
                    <th>用户</th>
                    <th>服务者</th>
                    <th>联系电话</th>
                    <th>联系地址</th>
                    <th>服务电话</th>
                    <th>状态</th>
                    <th>开始时间</th>
                    <th>结束时间</th>
                    <th>预约时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in filteredAppointments" :key="index" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="selectedItems[index]"
                        :value="item.id"
                        class="checkbox"
                      >
                    </td>
                    <td>{{ item.serviceName }}</td>
                    <td>{{ item.quantity }}</td>
                    <td class="price-cell">¥{{ item.totalAmount.toFixed(1) }}</td>
                    <td>{{ item.userName }}</td>
                    <td>{{ item.providerName }}</td>
                    <td>{{ item.contactPhone }}</td>
                    <td>{{ item.contactAddress }}</td>
                    <td>{{ item.servicePhone }}</td>
                    <td>
                      <span :class="['status-badge', `status-${item.status}`]">
                        {{ item.statusText }}
                      </span>
                    </td>
                    <td>{{ formatDateTime(item.startTime) }}</td>
                    <td>{{ formatDateTime(item.endTime) }}</td>
                    <td>{{ formatDateTime(item.appointmentTime) }}</td>
                    <td class="operation-cell">
                      <button class="btn assign-btn" @click="assignService(item.id)">
                        分配
                      </button>
                      <button class="btn delete-btn" @click="deleteAppointment(item.id)">
                        🗑️
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ totalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="currentPage === 1"
                  @click="changePage(currentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in pagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: currentPage === page }"
                  @click="changePage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="currentPage === totalPages"
                  @click="changePage(currentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          
     <!-- 提现记录管理 -->
          <div v-if="activeMenu === 'withdrawal'" class="withdrawal-management">
            <div class="section-header">
              <h2>提现记录</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="date" 
                  v-model="withdrawalDate" 
                  class="form-input search-input" 
                >
                <button class="btn search-btn" @click="searchWithdrawals">查询</button>
                <button class="btn reset-btn" @click="resetWithdrawalSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn delete-btn" @click="batchDeleteWithdrawals" :disabled="selectedWithdrawals.length === 0">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="selectAllWithdrawals"
                        @change="toggleSelectAllWithdrawals"
                        class="checkbox"
                      >
                    </th>
                    <th>金额</th>
                    <th>账户类型</th>
                    <th>账号</th>
                    <th>服务者</th>
                    <th>提现时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(record, index) in filteredWithdrawals" :key="record.id" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="withdrawalSelectedItems[index]"
                        class="checkbox"
                      >
                    </td>
                    <td class="price-cell">¥{{ record.amount }}</td>
                    <td>{{ record.accountType === 'wechat' ? '微信' : '支付宝' }}</td>
                    <td>{{ record.accountNumber }}</td>
                    <td>{{ record.providerName }}</td>
                    <td>{{ record.withdrawalTime }}</td>
                    <td class="operation-cell">
                      <button class="btn delete-btn" @click="deleteWithdrawal(record.id)">
                        🗑️
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ withdrawalTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="withdrawalCurrentPage === 1"
                  @click="changeWithdrawalPage(withdrawalCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in withdrawalPagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: withdrawalCurrentPage === page }"
                  @click="changeWithdrawalPage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="withdrawalCurrentPage === withdrawalTotalPages"
                  @click="changeWithdrawalPage(withdrawalCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          
          <!-- 服务分类管理 -->
          <div v-if="activeMenu === 'category'" class="category-management">
            <div class="section-header">
              <h2>服务分类</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="categorySearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入名称搜索"
                >
                <button class="btn search-btn" @click="searchCategories">查询</button>
                <button class="btn reset-btn" @click="resetCategorySearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn add-btn" @click="createNewCategory">新增</button>
                <button class="btn delete-btn" @click="batchDeleteCategories" :disabled="selectedCategories.length === 0">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="categorySelectAll"
                        @change="toggleCategorySelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>名称</th>
                    <th>描述</th>
                    <th>图片</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(category, index) in filteredCategories" :key="category.id" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="categorySelectedItems[index]"
                        :value="category.id"
                        class="checkbox"
                      >
                    </td>
                    <td>{{ category.name }}</td>
                    <td>{{ category.description }}</td>
                    <td class="image-cell">
                      <div class="category-icon">{{ category.icon }}</div>
                    </td>
                    <td class="operation-cell">
                      <button class="btn edit-btn" @click="editCategory(category)">
                        编辑
                      </button>
                      <button class="btn delete-btn" @click="deleteCategory(category.id)">
                        删除
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ categoryTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="categoryCurrentPage === 1"
                  @click="changeCategoryPage(categoryCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in categoryPagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: categoryCurrentPage === page }"
                  @click="changeCategoryPage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="categoryCurrentPage === categoryTotalPages"
                  @click="changeCategoryPage(categoryCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          
          <!-- 系统公告管理 -->
          <div v-if="activeMenu === 'notice'" class="notice-management">
            <div class="section-header">
              <h2>系统公告</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="noticeSearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入公告标题查询"
                >
                <button class="btn search-btn" @click="searchNotices">查询</button>
                <button class="btn reset-btn" @click="resetNoticeSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn add-btn" @click="showAddNoticeDialog = true">新增</button>
                <button class="btn delete-btn" @click="batchDeleteNotices" :disabled="selectedNotices.length === 0">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="noticeSelectAll"
                        @change="toggleNoticeSelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>公告标题</th>
                    <th>公告内容</th>
                    <th>发布时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(notice, index) in filteredNotices" :key="notice.id" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="noticeSelectedItems[index]"
                        :value="notice.id"
                        class="checkbox"
                      >
                    </td>
                    <td>{{ notice.title }}</td>
                    <td>{{ notice.content }}</td>
                    <td>{{ notice.publishTime }}</td>
                    <td class="operation-cell">
                      <button class="btn edit-btn" @click="editNotice(notice)">
                        编辑
                      </button>
                      <button class="btn delete-btn" @click="deleteNotice(notice.id)">
                        删除
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ noticeTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="noticeCurrentPage === 1"
                  @click="changeNoticePage(noticeCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in noticePagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: noticeCurrentPage === page }"
                  @click="changeNoticePage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="noticeCurrentPage === noticeTotalPages"
                  @click="changeNoticePage(noticeCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          
          <!-- 新增系统公告对话框 -->
          <div v-if="showAddNoticeDialog" class="modal-overlay" @click="showAddNoticeDialog = false">
            <div class="modal-dialog notice-dialog" @click.stop>
              <div class="modal-header">
                <h3>新增系统公告</h3>
                <button class="close-btn" @click="showAddNoticeDialog = false">×</button>
              </div>
              <div class="modal-body">
                <div class="form-group">
                  <label>公告标题：</label>
                  <input 
                    type="text" 
                    v-model="newNoticeForm.title" 
                    class="form-input" 
                    placeholder="请输入公告标题"
                    :class="{ 'error': newNoticeFormErrors.title }"
                  >
                  <span v-if="newNoticeFormErrors.title" class="error-message">{{ newNoticeFormErrors.title }}</span>
                </div>
                <div class="form-group">
                  <label>公告内容：</label>
                  <textarea 
                    v-model="newNoticeForm.content" 
                    class="form-input form-textarea" 
                    placeholder="请输入公告内容"
                    rows="6"
                    :class="{ 'error': newNoticeFormErrors.content }"
                  ></textarea>
                  <span v-if="newNoticeFormErrors.content" class="error-message">{{ newNoticeFormErrors.content }}</span>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" @click="cancelAddNotice">取消</button>
                <button class="btn btn-primary" @click="saveNewNotice">保存</button>
              </div>
            </div>
          </div>
          
          <!-- 编辑系统公告对话框 -->
          <div v-if="showEditNoticeDialog" class="modal-overlay" @click="showEditNoticeDialog = false">
            <div class="modal-dialog notice-dialog" @click.stop>
              <div class="modal-header">
                <h3>编辑系统公告</h3>
                <button class="close-btn" @click="showEditNoticeDialog = false">×</button>
              </div>
              <div class="modal-body">
                <div class="form-group">
                  <label>公告标题：</label>
                  <input 
                    type="text" 
                    v-model="editNoticeForm.title" 
                    class="form-input" 
                    placeholder="请输入公告标题"
                    :class="{ 'error': editNoticeFormErrors.title }"
                  >
                  <span v-if="editNoticeFormErrors.title" class="error-message">{{ editNoticeFormErrors.title }}</span>
                </div>
                <div class="form-group">
                  <label>公告内容：</label>
                  <textarea 
                    v-model="editNoticeForm.content" 
                    class="form-input form-textarea" 
                    placeholder="请输入公告内容"
                    rows="6"
                    :class="{ 'error': editNoticeFormErrors.content }"
                  ></textarea>
                  <span v-if="editNoticeFormErrors.content" class="error-message">{{ editNoticeFormErrors.content }}</span>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" @click="cancelEditNotice">取消</button>
                <button class="btn btn-primary" @click="saveEditNotice">保存</button>
              </div>
            </div>
          </div>
          
          <!-- 服务评价管理 -->
          <div v-if="activeMenu === 'evaluation'" class="evaluation-management">
            <div class="section-header">
              <h2>服务评价</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="evaluationSearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入名称筛选"
                >
                <button class="btn search-btn" @click="searchEvaluations">查询</button>
                <button class="btn reset-btn" @click="resetEvaluationSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn delete-btn" @click="batchDeleteEvaluations" :disabled="selectedEvaluations.length === 0">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="evaluationSelectAll"
                        @change="toggleEvaluationSelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>服务者</th>
                    <th>服务名称</th>
                    <th>用户</th>
                    <th>评分</th>
                    <th>内容</th>
                    <th>评价时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(evaluation, index) in filteredEvaluations" :key="evaluation.id" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="evaluationSelectedItems[index]"
                        class="checkbox"
                      >
                    </td>
                    <td>{{ evaluation.providerName }}</td>
                    <td>{{ evaluation.serviceName }}</td>
                    <td>{{ evaluation.userName }}</td>
                    <td class="rating-cell">
                      <div class="star-rating">
                        <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= evaluation.rating }">★</span>
                      </div>
                      <span class="rating-value">{{ evaluation.rating }}</span>
                    </td>
                    <td>{{ evaluation.content }}</td>
                    <td>{{ evaluation.evaluationTime }}</td>
                    <td class="operation-cell">
                      <button class="btn delete-btn" @click="deleteEvaluation(evaluation.id)">
                        删除
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ evaluationTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="evaluationCurrentPage === 1"
                  @click="changeEvaluationPage(evaluationCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in evaluationPagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: evaluationCurrentPage === page }"
                  @click="changeEvaluationPage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="evaluationCurrentPage === evaluationTotalPages"
                  @click="changeEvaluationPage(evaluationCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          
          <!-- 居家贴士管理 -->
          <div v-if="activeMenu === 'tips'" class="tips-management">
            <div class="section-header">
              <h2>居家贴士</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="tipsSearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入贴士标题查询"
                >
                <button class="btn search-btn" @click="searchTips">查询</button>
                <button class="btn reset-btn" @click="resetTipsSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn add-btn" @click="showAddTipDialog = true">新增贴士</button>
                <button class="btn delete-btn" @click="batchDeleteTips" :disabled="selectedTips.length === 0">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="tipsSelectAll"
                        @change="toggleTipsSelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>贴士标题</th>
                    <th>贴士内容</th>
                    <th>发布时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(tip, index) in filteredTips" :key="tip.id" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="tipsSelectedItems[index]"
                        class="checkbox"
                      >
                    </td>
                    <td>{{ tip.title }}</td>
                    <td>{{ tip.content }}</td>
                    <td>{{ tip.publishTime }}</td>
                    <td class="operation-cell">
                      <button class="btn edit-btn" @click="editTip(tip)">
                        编辑
                      </button>
                      <button class="btn delete-btn" @click="deleteTip(tip.id)">
                        删除
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ tipsTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="tipsCurrentPage === 1"
                  @click="changeTipsPage(tipsCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in tipsPagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: tipsCurrentPage === page }"
                  @click="changeTipsPage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="tipsCurrentPage === tipsTotalPages"
                  @click="changeTipsPage(tipsCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          
          <!-- 新增居家贴士对话框 -->
          <div v-if="showAddTipDialog" class="modal-overlay" @click="showAddTipDialog = false">
            <div class="modal-dialog tip-dialog" @click.stop>
              <div class="modal-header">
                <h3>新增居家贴士</h3>
                <button class="close-btn" @click="showAddTipDialog = false">×</button>
              </div>
              <div class="modal-body">
                <div class="form-group">
                  <label>贴士标题：</label>
                  <input 
                    type="text" 
                    v-model="newTipForm.title" 
                    class="form-input" 
                    placeholder="请输入贴士标题"
                    :class="{ 'error': newTipFormErrors.title }"
                  >
                  <span v-if="newTipFormErrors.title" class="error-message">{{ newTipFormErrors.title }}</span>
                </div>
                <div class="form-group">
                  <label>贴士内容：</label>
                  <textarea 
                    v-model="newTipForm.content" 
                    class="form-input form-textarea" 
                    placeholder="请输入贴士内容"
                    rows="6"
                    :class="{ 'error': newTipFormErrors.content }"
                  ></textarea>
                  <span v-if="newTipFormErrors.content" class="error-message">{{ newTipFormErrors.content }}</span>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" @click="cancelAddTip">取消</button>
                <button class="btn btn-primary" @click="saveNewTip">保存</button>
              </div>
            </div>
          </div>
          
          <!-- 轮播图信息管理 -->
          <div v-if="activeMenu === 'carousel'" class="carousel-management">
            <div class="section-header">
              <h2>轮播图信息</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="carouselSearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入家政名称查询"
                >
                <button class="btn search-btn" @click="searchCarousels">查询</button>
                <button class="btn reset-btn" @click="resetCarouselSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn add-btn" @click="showAddCarouselDialog = true">新增</button>
                <button class="btn delete-btn" @click="batchDeleteCarousels" :disabled="selectedCarousels.length === 0">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="carouselSelectAll"
                        @change="toggleCarouselSelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>图片</th>
                    <th>家政服务</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(carousel, index) in filteredCarousels" :key="carousel.id" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="carouselSelectedItems[index]"
                        class="checkbox"
                      >
                    </td>
                    <td class="image-cell">
                      <img :src="carousel.imageUrl" :alt="carousel.serviceName" class="carousel-image">
                    </td>
                    <td>{{ carousel.serviceName }}</td>
                    <td class="operation-cell">
                      <button class="btn edit-btn" @click="editCarousel(carousel)">
                        编辑
                      </button>
                      <button class="btn delete-btn" @click="deleteCarousel(carousel.id)">
                        删除
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ carouselTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="carouselCurrentPage === 1"
                  @click="changeCarouselPage(carouselCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in carouselPagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: carouselCurrentPage === page }"
                  @click="changeCarouselPage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="carouselCurrentPage === carouselTotalPages"
                  @click="changeCarouselPage(carouselCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          
          <!-- 编辑轮播图信息对话框 -->
          <div v-if="showEditCarouselDialog" class="modal-overlay" @click="showEditCarouselDialog = false">
            <div class="modal-dialog carousel-dialog" @click.stop>
              <div class="modal-header">
                <h3>编辑轮播图信息</h3>
                <button class="close-btn" @click="showEditCarouselDialog = false">×</button>
              </div>
              <div class="modal-body">
                <div class="form-group">
                  <label>家政服务名称：</label>
                  <input 
                    type="text" 
                    v-model="editCarouselForm.serviceName" 
                    class="form-input" 
                    placeholder="请输入家政服务名称"
                    :class="{ 'error': editCarouselFormErrors.serviceName }"
                  >
                  <span v-if="editCarouselFormErrors.serviceName" class="error-message">{{ editCarouselFormErrors.serviceName }}</span>
                </div>
                <div class="form-group">
                  <label>轮播图图片：</label>
                  <div class="image-upload-section">
                    <div class="current-image" v-if="editCarouselForm.imageUrl">
                      <img :src="editCarouselForm.imageUrl" alt="当前图片" class="preview-image">
                      <div class="image-overlay">
                        <button type="button" class="btn btn-secondary" @click="changeCarouselImage">更换图片</button>
                      </div>
                    </div>
                    <div v-else class="upload-placeholder">
                      <input 
                        type="file" 
                        ref="carouselImageInput"
                        @change="handleCarouselImageUpload"
                        accept="image/*"
                        style="display: none"
                      >
                      <button type="button" class="btn btn-secondary" @click="$refs.carouselImageInput.click()">选择图片</button>
                    </div>
                  </div>
                  <span v-if="editCarouselFormErrors.imageUrl" class="error-message">{{ editCarouselFormErrors.imageUrl }}</span>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" @click="cancelEditCarousel">取消</button>
                <button class="btn btn-primary" @click="saveEditCarousel">保存</button>
              </div>
            </div>
          </div>
          
          <!-- 新增轮播图信息对话框 -->
          <div v-if="showAddCarouselDialog" class="modal-overlay" @click="showAddCarouselDialog = false">
            <div class="modal-dialog carousel-dialog" @click.stop>
              <div class="modal-header">
                <h3>新增轮播图信息</h3>
                <button class="close-btn" @click="showAddCarouselDialog = false">×</button>
              </div>
              <div class="modal-body">
                <div class="form-group">
                  <label>家政服务名称：</label>
                  <input 
                    type="text" 
                    v-model="newCarouselForm.serviceName" 
                    class="form-input" 
                    placeholder="请输入家政服务名称"
                    :class="{ 'error': newCarouselFormErrors.serviceName }"
                  >
                  <span v-if="newCarouselFormErrors.serviceName" class="error-message">{{ newCarouselFormErrors.serviceName }}</span>
                </div>
                <div class="form-group">
                  <label>轮播图图片：</label>
                  <div class="image-upload-section">
                    <div class="current-image" v-if="newCarouselForm.imageUrl">
                      <img :src="newCarouselForm.imageUrl" alt="当前图片" class="preview-image">
                      <div class="image-overlay">
                        <button type="button" class="btn btn-secondary" @click="changeNewCarouselImage">更换图片</button>
                      </div>
                    </div>
                    <div v-else class="upload-placeholder">
                      <input 
                        type="file" 
                        ref="newCarouselImageInput"
                        @change="handleNewCarouselImageUpload"
                        accept="image/*"
                        style="display: none"
                      >
                      <button type="button" class="btn btn-secondary" @click="$refs.newCarouselImageInput.click()">选择图片</button>
                    </div>
                  </div>
                  <span v-if="newCarouselFormErrors.imageUrl" class="error-message">{{ newCarouselFormErrors.imageUrl }}</span>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" @click="cancelAddCarousel">取消</button>
                <button class="btn btn-primary" @click="saveNewCarousel">保存</button>
              </div>
            </div>
          </div>
          
          <!-- 普通用户管理 -->
          <div v-if="activeMenu === 'normalUser'" class="normal-user-management">
            <div class="section-header">
              <h2>普通用户管理</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="normalUserSearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入用户姓名或手机号查询"
                >
                <button class="btn search-btn" @click="searchNormalUsers">查询</button>
                <button class="btn reset-btn" @click="resetNormalUserSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn add-btn">新增</button>
                <button class="btn delete-btn" @click="batchDeleteNormalUsers" :disabled="selectedNormalUsers.length === 0">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="normalUserSelectAll"
                        @change="toggleNormalUserSelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>用户ID</th>
                    <th>用户名</th>
                    <th>手机号</th>
                    <th>注册时间</th>
                    <th>状态</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(user, index) in filteredNormalUsers" :key="user.id" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="normalUserSelectedItems[index]"
                        class="checkbox"
                      >
                    </td>
                    <td>{{ user.id }}</td>
                    <td>{{ user.userName }}</td>
                    <td>{{ user.phone }}</td>
                    <td>{{ user.registerTime }}</td>
                    <td>
                      <span class="status-badge" :class="user.status === 'active' ? 'status-active' : 'status-inactive'">
                        {{ user.status === 'active' ? '正常' : '禁用' }}
                      </span>
                    </td>
                    <td class="operation-cell">
                      <button class="btn edit-btn" @click="editNormalUser(user)">编辑</button>
                      <button class="btn delete-btn" @click="deleteNormalUser(user.id)">删除</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ normalUserTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="normalUserCurrentPage === 1"
                  @click="changeNormalUserPage(normalUserCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in normalUserPagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: page === normalUserCurrentPage }"
                  @click="changeNormalUserPage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="normalUserCurrentPage === normalUserTotalPages"
                  @click="changeNormalUserPage(normalUserCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          
          <!-- 家政服务者管理 -->
          <div v-if="activeMenu === 'housekeepingProvider'" class="provider-management">
            <div class="section-header">
              <h2>家政服务者管理</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="certProviderSearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入服务者姓名或手机号查询"
                >
                <button class="btn search-btn" @click="searchProviders">查询</button>
                <button class="btn reset-btn" @click="resetProviderSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn add-btn">新增</button>
                <button class="btn delete-btn" @click="batchDeleteProviders" :disabled="selectedProviders.length === 0">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="certProviderSelectAll"
                        @change="toggleProviderSelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>服务者ID</th>
                    <th>姓名</th>
                    <th>手机号</th>
                    <th>认证状态</th>
                    <th>服务评分</th>
                    <th>注册时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(provider, index) in filteredProviders" :key="provider.id" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="certProviderSelectedItems[index]"
                        class="checkbox"
                      >
                    </td>
                    <td>{{ provider.id }}</td>
                    <td>{{ provider.name }}</td>
                    <td>{{ provider.phone }}</td>
                    <td>
                      <span class="status-badge" :class="getProviderStatusClass(provider.certStatus)">
                        {{ getProviderStatusText(provider.certStatus) }}
                      </span>
                    </td>
                    <td>{{ provider.rating }}⭐</td>
                    <td>{{ provider.registerTime }}</td>
                    <td class="operation-cell">
                      <button class="btn edit-btn" @click="editProvider(provider)">编辑</button>
                      <button class="btn delete-btn" @click="deleteProvider(provider.id)">删除</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ providerTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="certProviderCurrentPage === 1"
                  @click="changeProviderPage(certProviderCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in providerPagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: page === certProviderCurrentPage }"
                  @click="changeProviderPage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="certProviderCurrentPage === providerTotalPages"
                  @click="changeProviderPage(certProviderCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          
          <!-- 管理员管理 -->
          <div v-if="activeMenu === 'adminUser'" class="admin-management">
            <div class="section-header">
              <h2>管理员管理</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="adminSearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入管理员姓名或账号查询"
                >
                <button class="btn search-btn" @click="searchAdmins">查询</button>
                <button class="btn reset-btn" @click="resetAdminSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn add-btn">新增</button>
                <button class="btn delete-btn" @click="batchDeleteAdmins" :disabled="selectedAdmins.length === 0">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="adminSelectAll"
                        @change="toggleAdminSelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>管理员ID</th>
                    <th>账号</th>
                    <th>姓名</th>
                    <th>角色</th>
                    <th>创建时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(admin, index) in filteredAdmins" :key="admin.id" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="adminSelectedItems[index]"
                        class="checkbox"
                      >
                    </td>
                    <td>{{ admin.id }}</td>
                    <td>{{ admin.username }}</td>
                    <td>{{ admin.name }}</td>
                    <td>{{ admin.role }}</td>
                    <td>{{ admin.createTime }}</td>
                    <td class="operation-cell">
                      <button class="btn edit-btn" @click="editAdmin(admin)">编辑</button>
                      <button class="btn delete-btn" @click="deleteAdmin(admin.id)">删除</button>
                      <button class="btn reset-pwd-btn" @click="resetAdminPassword(admin.id)">重置密码</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ adminTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="adminCurrentPage === 1"
                  @click="changeAdminPage(adminCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in adminPagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: page === adminCurrentPage }"
                  @click="changeAdminPage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="adminCurrentPage === adminTotalPages"
                  @click="changeAdminPage(adminCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          
          <!-- 收藏信息管理 -->
          <div v-if="activeMenu === 'favorites'" class="favorites-management">
            <div class="section-header">
              <h2>收藏信息</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="favoritesSearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入服务名称查询"
                >
                <button class="btn search-btn" @click="searchFavorites">查询</button>
                <button class="btn reset-btn" @click="resetFavoritesSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn add-btn" @click="showAddFavoriteDialog = true">新增</button>
                <button class="btn delete-btn" @click="batchDeleteFavorites" :disabled="selectedFavorites.length === 0">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="favoritesSelectAll"
                        @change="toggleFavoritesSelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>服务</th>
                    <th>用户</th>
                    <th>收藏时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(favorite, index) in filteredFavorites" :key="favorite.id" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="favoritesSelectedItems[index]"
                        class="checkbox"
                      >
                    </td>
                    <td>{{ favorite.serviceName }}</td>
                    <td>{{ favorite.userName }}</td>
                    <td>{{ favorite.favoriteTime }}</td>
                    <td class="operation-cell">
                      <button class="btn edit-btn" @click="editFavorite(favorite)">
                        编辑
                      </button>
                      <button class="btn delete-btn" @click="deleteFavorite(favorite.id)">
                        删除
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ favoritesTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="favoritesCurrentPage === 1"
                  @click="changeFavoritesPage(favoritesCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in favoritesPagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: favoritesCurrentPage === page }"
                  @click="changeFavoritesPage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="favoritesCurrentPage === favoritesTotalPages"
                  @click="changeFavoritesPage(favoritesCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          
          <!-- 新增收藏信息对话框 -->
          <div v-if="showAddFavoriteDialog" class="modal-overlay" @click="showAddFavoriteDialog = false">
            <div class="modal-dialog favorite-dialog" @click.stop>
              <div class="modal-header">
                <h3>新增收藏信息</h3>
                <button class="close-btn" @click="showAddFavoriteDialog = false">×</button>
              </div>
              <div class="modal-body">
                <div class="form-group">
                  <label>服务名称：</label>
                  <input 
                    type="text" 
                    v-model="newFavoriteForm.serviceName" 
                    class="form-input" 
                    placeholder="请输入服务名称"
                    :class="{ 'error': favoriteFormErrors.serviceName }"
                  >
                  <span v-if="favoriteFormErrors.serviceName" class="error-message">{{ favoriteFormErrors.serviceName }}</span>
                </div>
                <div class="form-group">
                  <label>用户名称：</label>
                  <input 
                    type="text" 
                    v-model="newFavoriteForm.userName" 
                    class="form-input" 
                    placeholder="请输入用户名称"
                    :class="{ 'error': favoriteFormErrors.userName }"
                  >
                  <span v-if="favoriteFormErrors.userName" class="error-message">{{ favoriteFormErrors.userName }}</span>
                </div>
                <div class="form-group">
                  <label>收藏时间：</label>
                  <input 
                    type="datetime-local" 
                    v-model="newFavoriteForm.favoriteTime" 
                    class="form-input"
                    :class="{ 'error': favoriteFormErrors.favoriteTime }"
                  >
                  <span v-if="favoriteFormErrors.favoriteTime" class="error-message">{{ favoriteFormErrors.favoriteTime }}</span>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" @click="cancelAddFavorite">取消</button>
                <button class="btn btn-primary" @click="saveNewFavorite">保存</button>
              </div>
            </div>
          </div>
          
          <!-- 编辑收藏信息对话框 -->
          <div v-if="showEditFavoriteDialog" class="modal-overlay" @click="showEditFavoriteDialog = false">
            <div class="modal-dialog favorite-dialog" @click.stop>
              <div class="modal-header">
                <h3>编辑收藏信息</h3>
                <button class="close-btn" @click="showEditFavoriteDialog = false">×</button>
              </div>
              <div class="modal-body">
                <div class="form-group">
                  <label>服务名称：</label>
                  <input 
                    type="text" 
                    v-model="editFavoriteForm.serviceName" 
                    class="form-input" 
                    placeholder="请输入服务名称"
                    :class="{ 'error': editFavoriteFormErrors.serviceName }"
                  >
                  <span v-if="editFavoriteFormErrors.serviceName" class="error-message">{{ editFavoriteFormErrors.serviceName }}</span>
                </div>
                <div class="form-group">
                  <label>用户名称：</label>
                  <input 
                    type="text" 
                    v-model="editFavoriteForm.userName" 
                    class="form-input" 
                    placeholder="请输入用户名称"
                    :class="{ 'error': editFavoriteFormErrors.userName }"
                  >
                  <span v-if="editFavoriteFormErrors.userName" class="error-message">{{ editFavoriteFormErrors.userName }}</span>
                </div>
                <div class="form-group">
                  <label>收藏时间：</label>
                  <input 
                    type="datetime-local" 
                    v-model="editFavoriteForm.favoriteTime" 
                    class="form-input"
                    :class="{ 'error': editFavoriteFormErrors.favoriteTime }"
                  >
                  <span v-if="editFavoriteFormErrors.favoriteTime" class="error-message">{{ editFavoriteFormErrors.favoriteTime }}</span>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" @click="cancelEditFavorite">取消</button>
                <button class="btn btn-primary" @click="saveEditFavorite">保存</button>
              </div>
            </div>
          </div>
          
          <!-- 家政服务管理 -->
          <div v-if="activeMenu === 'services'" class="service-management">
          </div>
          
          <!-- 服务者认证管理 -->
          <div v-if="activeMenu === 'providerCert'" class="provider-verification">
            <div class="section-header">
              <h2>服务者认证</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="certProviderSearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入家政人员名称查询"
                >
                <button class="btn search-btn">查询</button>
                <button class="btn reset-btn" @click="resetProviderSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn delete-btn" @click="batchDeleteProvider">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="certProviderSelectAll"
                        @change="toggleProviderSelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>家政人员</th>
                    <th>资格证书</th>
                    <th>身份证</th>
                    <th>身份证号码</th>
                    <th>联系方式</th>
                    <th>家庭住址</th>
                    <th>审核状态</th>
                    <th>审核意见</th>
                    <th>审核</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in filteredCertProviders" :key="index" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="certProviderSelectedItems[index]"
                        :value="item.id"
                        class="checkbox"
                      >
                    </td>
                    <td>{{ item.name }}</td>
                    <td class="certificate-cell">
                      <img :src="item.certificateUrl" :alt="item.name + ' 资格证书'" class="certificate-image">
                    </td>
                    <td class="id-card-cell">
                      <img :src="item.idCardUrl" :alt="item.name + ' 身份证'" class="id-card-image">
                    </td>
                    <td>{{ item.idCardNumber }}</td>
                    <td>{{ item.contact }}</td>
                    <td>{{ item.address }}</td>
                    <td class="status-cell">
                      <span class="status-badge" :class="item.status === '通过' ? 'passed-status' : 'pending-status'">{{ item.status }}</span>
                    </td>
                    <td>{{ item.reviewComment || '-' }}</td>
                    <td>
                      <!-- 根据审核状态显示不同的内容 -->
                      <span v-if="item.status === '通过'" class="reviewed-text">已通过</span>
                      <button v-else class="btn review-btn" @click="reviewProvider(item.id)">审核</button>
                    </td>
                    <td class="operation-cell">
                      <button class="btn delete-btn" @click="deleteCertProvider(item.id)">
                        删除
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ providerTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="certProviderCurrentPage === 1"
                  @click="changeProviderPage(certProviderCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in certProviderPagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: certProviderCurrentPage === page }"
                  @click="changeProviderPage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="certProviderCurrentPage === providerTotalPages"
                  @click="changeProviderPage(certProviderCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
          <!-- 家政服务管理 -->
          <div v-if="activeMenu === 'services'" class="service-management">
            <div class="section-header">
              <h2>家政服务管理</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="serviceSearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入名称查询"
                >
                <button class="btn search-btn">查询</button>
                <button class="btn reset-btn" @click="resetServiceSearch">重置</button>
              </div>
              <div class="operation-buttons">
                <button class="btn create-btn" @click="createNewService">新建</button>
                <button class="btn delete-btn" @click="batchDeleteService">批量删除</button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div class="data-table-container">
              <table class="data-table">
                <thead>
                  <tr>
                    <th class="checkbox-header">
                      <input 
                        type="checkbox" 
                        v-model="serviceSelectAll"
                        @change="toggleServiceSelectAll"
                        class="checkbox"
                      >
                    </th>
                    <th>图片</th>
                    <th>名称</th>
                    <th>价格</th>
                    <th>单位</th>
                    <th>详情</th>
                    <th>销量</th>
                    <th>人气</th>
                    <th>分类</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in filteredServices" :key="index" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="serviceSelectedItems[index]"
                        :value="item.id"
                        class="checkbox"
                      >
                    </td>
                    <td class="image-cell">
                      <img :src="item.imageUrl" :alt="item.name" class="service-image">
                    </td>
                    <td>{{ item.name }}</td>
                    <td class="price-cell">¥{{ item.price }}</td>
                    <td>{{ item.unit }}</td>
                    <td>
                      <button class="btn detail-view-btn" @click="viewServiceDetail(item.id)">查看内容</button>
                    </td>
                    <td>{{ item.sales }}</td>
                    <td>{{ item.popularity }}</td>
                    <td>{{ item.category }}</td>
                    <td class="operation-cell">
                      <button class="btn edit-btn" @click="editService(item.id)">
                        编辑
                      </button>
                      <button class="btn delete-btn" @click="deleteService(item.id)">
                        删除
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 分页 -->
            <div class="pagination">
              <div class="page-info">
                共 {{ serviceTotalItems }} 条
              </div>
              <div class="page-controls">
                <button 
                  class="btn page-btn" 
                  :disabled="serviceCurrentPage === 1"
                  @click="changeServicePage(serviceCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in servicePagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: serviceCurrentPage === page }"
                  @click="changeServicePage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="serviceCurrentPage === serviceTotalPages"
                  @click="changeServicePage(serviceCurrentPage + 1)"
                >
                  &gt;
                </button>
              </div>
            </div>
            
            <!-- 服务编辑对话框 -->
            <div v-if="showServiceDialog" class="modal-overlay" @click="cancelServiceDialog">
              <div class="modal-content service-dialog" @click.stop>
                <div class="modal-header">
                  <h3>{{ isEditingService ? '编辑服务' : '新建服务' }}</h3>
                  <button class="close-btn" @click="cancelServiceDialog">×</button>
                </div>
                <div class="modal-body">
                  <form @submit.prevent="saveService">
                    <div class="form-group">
                      <label for="serviceName">服务名称 *</label>
                      <input 
                        type="text" 
                        id="serviceName"
                        v-model="serviceForm.name" 
                        class="form-input" 
                        placeholder="请输入服务名称"
                        required
                      >
                    </div>
                    
                    <div class="form-row">
                      <div class="form-group">
                        <label for="servicePrice">价格 *</label>
                        <input 
                          type="number" 
                          id="servicePrice"
                          v-model.number="serviceForm.price" 
                          class="form-input" 
                          placeholder="请输入价格"
                          min="0"
                          step="0.01"
                          required
                        >
                      </div>
                      
                      <div class="form-group">
                        <label for="serviceUnit">单位 *</label>
                        <select id="serviceUnit" v-model="serviceForm.unit" class="form-input" required>
                          <option value="">请选择单位</option>
                          <option value="次">次</option>
                          <option value="小时">小时</option>
                          <option value="天">天</option>
                          <option value="平米">平米</option>
                          <option value="10平米">10平米</option>
                          <option value="套">套</option>
                          <option value="件">件</option>
                        </select>
                      </div>
                    </div>
                    
                    <div class="form-group">
                      <label for="serviceCategory">服务分类 *</label>
                      <select id="serviceCategory" v-model="serviceForm.category" class="form-input" required>
                        <option value="">请选择分类</option>
                        <option value="皮沙发保养">皮沙发保养</option>
                        <option value="地板打檫">地板打檫</option>
                        <option value="家底长期保洁">家底长期保洁</option>
                        <option value="卫生间保养">卫生间保养</option>
                        <option value="厨房保养">厨房保养</option>
                        <option value="玻璃清洁">玻璃清洁</option>
                        <option value="地毯清洁">地毯清洁</option>
                        <option value="家电清洁">家电清洁</option>
                        <option value="其他">其他</option>
                      </select>
                    </div>
                    
                    <div class="form-group">
                      <label for="serviceImage">服务图片</label>
                      <div class="image-upload-area">
                        <div class="upload-container">
                          <input 
                            type="file" 
                            id="serviceImage"
                            ref="imageInput"
                            accept="image/*"
                            @change="handleImageUpload"
                            class="file-input"
                            style="display: none;"
                          >
                          <div class="upload-button" @click="triggerImageUpload">
                            <div v-if="!serviceForm.imageUrl" class="upload-placeholder">
                              <div class="upload-icon">📷</div>
                              <div class="upload-text">点击添加图片</div>
                              <div class="upload-hint">支持 JPG、PNG、GIF 格式</div>
                            </div>
                            <div v-else class="image-preview-container">
                              <img :src="serviceForm.imageUrl" alt="服务图片预览" class="preview-image">
                              <div class="image-overlay">
                                <button type="button" class="change-image-btn" @click.stop="triggerImageUpload">更换图片</button>
                                <button type="button" class="remove-image-btn" @click.stop="removeImage">删除</button>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    
                    <div class="form-group">
                      <label for="serviceDescription">服务描述</label>
                      <textarea 
                        id="serviceDescription"
                        v-model="serviceForm.description" 
                        class="form-textarea" 
                        placeholder="请输入服务描述"
                        rows="4"
                      ></textarea>
                    </div>
                    
                    <div class="form-actions">
                      <button type="button" class="btn cancel-btn" @click="cancelServiceDialog">取消</button>
                      <button type="submit" class="btn save-btn">{{ isEditingService ? '保存' : '创建' }}</button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
            
            <!-- 服务分类编辑对话框 -->
            <div v-if="showCategoryDialog" class="modal-overlay" @click="cancelCategoryDialog">
              <div class="modal-content category-dialog" @click.stop>
                <div class="modal-header">
                  <h3>{{ isEditingCategory ? '编辑分类' : '新建分类' }}</h3>
                  <button class="close-btn" @click="cancelCategoryDialog">×</button>
                </div>
                <div class="modal-body">
                  <form @submit.prevent="saveCategory">
                    <div class="form-group">
                      <label for="categoryName">分类名称 *</label>
                      <input 
                        type="text" 
                        id="categoryName"
                        v-model="categoryForm.name" 
                        class="form-input" 
                        placeholder="请输入分类名称"
                        required
                      >
                    </div>
                    
                    <div class="form-group">
                      <label for="categoryDescription">分类描述 *</label>
                      <textarea 
                        id="categoryDescription"
                        v-model="categoryForm.description" 
                        class="form-textarea" 
                        placeholder="请输入分类描述"
                        rows="3"
                        required
                      ></textarea>
                    </div>
                    
                    <div class="form-group">
                      <label for="categoryIcon">分类图标</label>
                      <div class="icon-selector">
                        <div class="icon-grid">
                          <div 
                            v-for="icon in availableIcons" 
                            :key="icon"
                            class="icon-option"
                            :class="{ selected: categoryForm.icon === icon }"
                            @click="selectIcon(icon)"
                          >
                            {{ icon }}
                          </div>
                        </div>
                        <div class="selected-icon-preview">
                          <span v-if="categoryForm.icon" class="preview-icon">{{ categoryForm.icon }}</span>
                          <span v-else class="no-icon">未选择图标</span>
                        </div>
                      </div>
                    </div>
                    
                    <div class="form-actions">
                      <button type="button" class="btn cancel-btn" @click="cancelCategoryDialog">取消</button>
                      <button type="submit" class="btn save-btn">{{ isEditingCategory ? '保存' : '创建' }}</button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- 个人资料对话框 -->
    <div v-if="showPersonalProfileDialog" class="modal-overlay" @click="closePersonalProfileDialog">
      <div class="modal-content personal-profile-dialog" @click.stop>
        <div class="modal-header">
          <h3>个人资料</h3>
          <button class="close-btn" @click="closePersonalProfileDialog">×</button>
        </div>
        <div class="modal-body">
          <div class="profile-content">
            <!-- 头像区域 -->
            <div class="avatar-section">
              <div class="avatar-container">
                <div class="avatar-placeholder">
                  <span class="avatar-text">{{ personalProfileForm.name.charAt(0) }}</span>
                </div>
              </div>
              <div class="avatar-info">
                <h4>{{ personalProfileForm.name }}</h4>
                <p>{{ personalProfileForm.role }}</p>
              </div>
            </div>
            
            <!-- 基本信息 -->
            <div class="profile-section">
              <h4>基本信息</h4>
              <div class="form-grid">
                <div class="form-group">
                  <label>用户名：</label>
                  <input 
                    type="text" 
                    v-model="personalProfileForm.username" 
                    class="form-input" 
                    :disabled="!isEditingProfile"
                    readonly
                  >
                  <span class="field-note">用户名不可修改</span>
                </div>
                <div class="form-group">
                  <label>姓名：</label>
                  <input 
                    type="text" 
                    v-model="personalProfileForm.name" 
                    class="form-input" 
                    :disabled="!isEditingProfile"
                    placeholder="请输入姓名"
                  >
                </div>
                <div class="form-group">
                  <label>邮箱：</label>
                  <input 
                    type="email" 
                    v-model="personalProfileForm.email" 
                    class="form-input" 
                    :disabled="!isEditingProfile"
                    placeholder="请输入邮箱"
                  >
                </div>
                <div class="form-group">
                  <label>手机号：</label>
                  <input 
                    type="tel" 
                    v-model="personalProfileForm.phone" 
                    class="form-input" 
                    :disabled="!isEditingProfile"
                    placeholder="请输入手机号"
                  >
                </div>
                <div class="form-group">
                  <label>角色：</label>
                  <input 
                    type="text" 
                    v-model="personalProfileForm.role" 
                    class="form-input" 
                    disabled
                    readonly
                  >
                </div>
                <div class="form-group">
                  <label>部门：</label>
                  <input 
                    type="text" 
                    v-model="personalProfileForm.department" 
                    class="form-input" 
                    :disabled="!isEditingProfile"
                    placeholder="请输入部门"
                  >
                </div>
              </div>
            </div>
            
            <!-- 系统信息 -->
            <div class="profile-section">
              <h4>系统信息</h4>
              <div class="form-grid">
                <div class="form-group">
                  <label>入职日期：</label>
                  <input 
                    type="text" 
                    v-model="personalProfileForm.joinDate" 
                    class="form-input" 
                    disabled
                    readonly
                  >
                </div>
                <div class="form-group">
                  <label>最后登录：</label>
                  <input 
                    type="text" 
                    v-model="personalProfileForm.lastLogin" 
                    class="form-input" 
                    disabled
                    readonly
                  >
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div v-if="!isEditingProfile" class="button-group">
            <button class="btn btn-primary" @click="editPersonalProfile">编辑资料</button>
            <button class="btn btn-secondary" @click="closePersonalProfileDialog">关闭</button>
          </div>
          <div v-else class="button-group">
            <button class="btn btn-primary" @click="savePersonalProfile">保存</button>
            <button class="btn btn-secondary" @click="cancelPersonalProfileEdit">取消</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改密码模态框 -->
    <div v-if="showChangePasswordModal" class="modal-overlay" @click="cancelChangePassword">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>修改密码</h3>
          <button class="close-btn" @click="cancelChangePassword">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>原密码：</label>
            <input 
              type="password" 
              v-model="changePasswordForm.oldPassword" 
              class="form-input" 
              placeholder="请输入原密码"
              :class="{ 'error': passwordErrors.oldPassword }"
            >
            <span v-if="passwordErrors.oldPassword" class="error-message">{{ passwordErrors.oldPassword }}</span>
          </div>
          <div class="form-group">
            <label>新密码：</label>
            <input 
              type="password" 
              v-model="changePasswordForm.newPassword" 
              class="form-input" 
              placeholder="请输入新密码"
              :class="{ 'error': passwordErrors.newPassword }"
            >
            <span v-if="passwordErrors.newPassword" class="error-message">{{ passwordErrors.newPassword }}</span>
            <div class="password-hint">密码至少包含8个字符，包含字母和数字</div>
          </div>
          <div class="form-group">
            <label>确认新密码：</label>
            <input 
              type="password" 
              v-model="changePasswordForm.confirmPassword" 
              class="form-input" 
              placeholder="请再次输入新密码"
              :class="{ 'error': passwordErrors.confirmPassword }"
            >
            <span v-if="passwordErrors.confirmPassword" class="error-message">{{ passwordErrors.confirmPassword }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="cancelChangePassword">取消</button>
          <button class="btn btn-primary" @click="saveChangePassword">保存修改</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 用户信息
const userInfo = ref({
  username: 'admin',
  role: 'administrator'
})
const userAvatar = ref('')
const showUserDropdown = ref(false)

// 菜单状态
const activeMenu = ref('home')
const isServiceMenuExpanded = ref(false)
const isUserMenuExpanded = ref(false)

// 计算属性
const isServiceMenuActive = computed(() => {
  return ['appointment', 'services', 'withdrawal', 'category', 'notice', 'evaluation', 'tips', 'carousel', 'favorites'].includes(activeMenu.value)
})

const isUserMenuActive = computed(() => {
  return ['providerCert', 'normalUser', 'housekeepingProvider', 'adminUser'].includes(activeMenu.value)
})

// 修改密码相关
const showChangePasswordModal = ref(false)
const changePasswordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordErrors = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 切换用户下拉菜单
const toggleUserDropdown = () => {
  showUserDropdown.value = !showUserDropdown.value
}

// 设置激活菜单
const setActiveMenu = (menu: string) => {
  activeMenu.value = menu
  // 移除关闭菜单的代码，这样点击子菜单时就不会收起其他子菜单了
}

// 切换服务管理菜单
const toggleServiceMenu = () => {
  isServiceMenuExpanded.value = !isServiceMenuExpanded.value
  if (isServiceMenuExpanded.value) {
    isUserMenuExpanded.value = false
  }
}

// 切换用户管理菜单
const toggleUserMenu = () => {
  isUserMenuExpanded.value = !isUserMenuExpanded.value
  if (isUserMenuExpanded.value) {
    isServiceMenuExpanded.value = false
  }
}

// 普通用户管理相关状态
const normalUserSearchKeyword = ref('')
const normalUserSelectAll = ref(false)
const normalUserSelectedItems = ref<Record<number, boolean>>({})
const normalUserCurrentPage = ref(1)
const normalUserPageSize = ref(10)
const normalUserTotalItems = ref(5)
const normalUserTotalPages = computed(() => Math.ceil(normalUserTotalItems.value / normalUserPageSize.value))
const selectedNormalUsers = computed(() => {
  return Object.entries(normalUserSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => filteredNormalUsers.value[parseInt(index)]?.id)
    .filter(Boolean)
})

// 模拟普通用户数据
const filteredNormalUsers = ref([
  {
    id: '1',
    userName: '张三',
    phone: '13800138001',
    registerTime: '2024-01-15 09:30:00',
    status: 'active'
  },
  {
    id: '2',
    userName: '李四',
    phone: '13800138002',
    registerTime: '2024-01-20 14:20:00',
    status: 'active'
  },
  {
    id: '3',
    userName: '王五',
    phone: '13800138003',
    registerTime: '2024-02-01 10:15:00',
    status: 'inactive'
  },
  {
    id: '4',
    userName: '赵六',
    phone: '13800138004',
    registerTime: '2024-02-10 16:45:00',
    status: 'active'
  },
  {
    id: '5',
    userName: '钱七',
    phone: '13800138005',
    registerTime: '2024-02-15 11:20:00',
    status: 'active'
  }
])

// 普通用户管理方法
const searchNormalUsers = () => {
  console.log('搜索普通用户:', normalUserSearchKeyword.value)
  // 这里应该根据关键词过滤数据
}

const resetNormalUserSearch = () => {
  normalUserSearchKeyword.value = ''
  // 重置过滤条件
}

const toggleNormalUserSelectAll = () => {
  filteredNormalUsers.value.forEach((_, index) => {
    normalUserSelectedItems.value[index] = normalUserSelectAll.value
  })
}

const editNormalUser = (user: any) => {
  console.log('编辑普通用户:', user)
  // 打开编辑对话框并填充数据
}

const deleteNormalUser = (id: string) => {
  if (confirm('确定要删除这个用户吗？')) {
    console.log('删除普通用户:', id)
    // 删除数据的逻辑
    const index = filteredNormalUsers.value.findIndex(item => item.id === id)
    if (index > -1) {
      filteredNormalUsers.value.splice(index, 1)
      normalUserTotalItems.value--
    }
  }
}

const batchDeleteNormalUsers = () => {
  const selectedIds = selectedNormalUsers.value
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的用户')
    return
  }
  
  // 获取要删除的用户详情用于确认
  const selectedUsers = filteredNormalUsers.value.filter(user => selectedIds.includes(user.id))
  
  const confirmMessage = `确定要删除选中的 ${selectedIds.length} 个用户吗？\n\n删除详情：\n- 用户数量：${selectedIds.length} 个\n- 涉及用户：${selectedUsers.map(u => u.userName).join('、')}\n\n此操作不可撤销！`
  
  if (confirm(confirmMessage)) {
    try {
      // 从用户列表中删除选中的用户
      filteredNormalUsers.value = filteredNormalUsers.value.filter(user => !selectedIds.includes(user.id))
      normalUserTotalItems.value -= selectedIds.length
      
      // 清空选择
      normalUserSelectAll.value = false
      normalUserSelectedItems.value = {}
      
      // 如果当前页没有数据了，回到上一页
      if (filteredNormalUsers.value.length === 0 && normalUserCurrentPage.value > 1) {
        normalUserCurrentPage.value--
      }
      
      // 显示删除成功信息
      alert(`批量删除成功！\n已删除 ${selectedIds.length} 个用户`)
      
      // 记录删除操作日志
      console.log('批量删除普通用户:', {
        deletedIds: selectedIds,
        deletedUsers: selectedUsers,
        timestamp: new Date().toISOString()
      })
      
    } catch (error) {
      console.error('批量删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

const changeNormalUserPage = (page: number) => {
  normalUserCurrentPage.value = page
  // 分页逻辑
}

// 家政服务者管理相关状态
const certProviderSearchKeyword = ref('')
const certProviderSelectAll = ref(false)
const certProviderSelectedItems = ref<Record<number, boolean>>({})
const certProviderCurrentPage = ref(1)
const providerPageSize = ref(10)
const providerTotalItems = ref(4)
const providerTotalPages = computed(() => Math.ceil(providerTotalItems.value / providerPageSize.value))
const selectedProviders = computed(() => {
  return Object.entries(certProviderSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => filteredProviders.value[parseInt(index)]?.id)
    .filter(Boolean)
})

// 模拟家政服务者数据
const providers = ref([
  {
    id: '1',
    name: '刘阿姨',
    phone: '13900139001',
    certStatus: 'approved',
    rating: 4.8,
    registerTime: '2024-01-10 08:30:00'
  },
  {
    id: '2',
    name: '张师傅',
    phone: '13900139002',
    certStatus: 'pending',
    rating: 0,
    registerTime: '2024-01-25 13:45:00'
  },
  {
    id: '3',
    name: '王阿姨',
    phone: '13900139003',
    certStatus: 'approved',
    rating: 4.9,
    registerTime: '2024-02-05 10:20:00'
  },
  {
    id: '4',
    name: '李师傅',
    phone: '13900139004',
    certStatus: 'rejected',
    rating: 0,
    registerTime: '2024-02-18 15:10:00'
  }
])

// 家政服务者管理方法
const searchProviders = () => {
  console.log('搜索家政服务者:', certProviderSearchKeyword.value)
  // 搜索功能通过 filteredProviders 计算属性实现
}

// 家政服务者过滤后的数据
const filteredProviders = computed(() => {
  let filtered = providers.value
  
  // 搜索过滤
  if (certProviderSearchKeyword.value) {
    filtered = filtered.filter(provider => 
      provider.name.toLowerCase().includes(certProviderSearchKeyword.value.toLowerCase()) ||
      provider.phone.includes(certProviderSearchKeyword.value)
    )
  }
  
  return filtered
})

// 重置过滤条件

const toggleProviderSelectAll = () => {
  filteredProviders.value.forEach((_, index) => {
    certProviderSelectedItems.value[index] = certProviderSelectAll.value
  })
}

const getProviderStatusClass = (status: string) => {
  const statusMap: Record<string, string> = {
    approved: 'status-approved',
    pending: 'status-pending',
    rejected: 'status-rejected'
  }
  return statusMap[status] || ''
}

const getProviderStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    approved: '已认证',
    pending: '待审核',
    rejected: '已拒绝'
  }
  return statusMap[status] || '未知'
}

const editProvider = (provider: any) => {
  console.log('编辑家政服务者:', provider)
  // 打开编辑对话框并填充数据
}

const deleteProvider = (id: string) => {
  if (confirm('确定要删除这个家政服务者吗？')) {
    console.log('删除家政服务者:', id)
    // 删除数据的逻辑
    const index = providers.value.findIndex(item => item.id === id)
    if (index > -1) {
      providers.value.splice(index, 1)
      providerTotalItems.value--
    }
  }
}

const batchDeleteProviders = () => {
  const selectedIds = selectedProviders.value
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的家政服务者')
    return
  }
  
  // 获取要删除的服务者详情用于确认
  const selectedProvidersList = filteredProviders.value.filter(provider => selectedIds.includes(provider.id))
  
  const confirmMessage = `确定要删除选中的 ${selectedIds.length} 个家政服务者吗？\n\n删除详情：\n- 服务者数量：${selectedIds.length} 个\n- 涉及服务者：${selectedProvidersList.map(p => p.name).join('、')}\n\n此操作不可撤销！`
  
  if (confirm(confirmMessage)) {
    try {
      // 从服务者列表中删除选中的服务者
      providers.value = providers.value.filter(provider => !selectedIds.includes(provider.id))
      providerTotalItems.value -= selectedIds.length
      
      // 清空选择
      certProviderSelectAll.value = false
      certProviderSelectedItems.value = {}
      
      // 如果当前页没有数据了，回到上一页
      if (filteredProviders.value.length === 0 && certProviderCurrentPage.value > 1) {
        certProviderCurrentPage.value--
      }
      
      // 显示删除成功信息
      alert(`批量删除成功！\n已删除 ${selectedIds.length} 个家政服务者`)
      
      // 记录删除操作日志
      console.log('批量删除家政服务者:', {
        deletedIds: selectedIds,
        deletedProviders: selectedProvidersList,
        timestamp: new Date().toISOString()
      })
      
    } catch (error) {
      console.error('批量删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

// 分页逻辑

// 管理员管理相关状态
const adminSearchKeyword = ref('')
const adminSelectAll = ref(false)
const adminSelectedItems = ref<Record<number, boolean>>({})
const adminCurrentPage = ref(1)
const adminPageSize = ref(10)
const adminTotalItems = ref(3)
const adminTotalPages = computed(() => Math.ceil(adminTotalItems.value / adminPageSize.value))
const selectedAdmins = computed(() => {
  return Object.entries(adminSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => filteredAdmins.value[parseInt(index)]?.id)
    .filter(Boolean)
})

// 模拟管理员数据
const filteredAdmins = ref([
  {
    id: '1',
    username: 'admin',
    name: '超级管理员',
    role: '超级管理员',
    createTime: '2024-01-01 00:00:00'
  },
  {
    id: '2',
    username: 'manager',
    name: '运营经理',
    role: '管理员',
    createTime: '2024-01-05 10:30:00'
  },
  {
    id: '3',
    username: 'editor',
    name: '内容编辑',
    role: '编辑',
    createTime: '2024-01-10 14:20:00'
  }
])

// 管理员管理方法
const searchAdmins = () => {
  console.log('搜索管理员:', adminSearchKeyword.value)
  // 这里应该根据关键词过滤数据
}

const resetAdminSearch = () => {
  adminSearchKeyword.value = ''
  // 重置过滤条件
}

const toggleAdminSelectAll = () => {
  filteredAdmins.value.forEach((_, index) => {
    adminSelectedItems.value[index] = adminSelectAll.value
  })
}

const editAdmin = (admin: any) => {
  console.log('编辑管理员:', admin)
  // 打开编辑对话框并填充数据
}

const deleteAdmin = (id: string) => {
  if (confirm('确定要删除这个管理员吗？')) {
    console.log('删除管理员:', id)
    // 删除数据的逻辑
    const index = filteredAdmins.value.findIndex(item => item.id === id)
    if (index > -1) {
      filteredAdmins.value.splice(index, 1)
      adminTotalItems.value--
    }
  }
}

const resetAdminPassword = (id: string) => {
  if (confirm('确定要重置这个管理员的密码吗？重置后密码将变为默认值。')) {
    console.log('重置管理员密码:', id)
    alert('密码重置成功，新密码为：123456')
  }
}

const batchDeleteAdmins = () => {
  const selectedIds = selectedAdmins.value
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的管理员')
    return
  }
  
  // 获取要删除的管理员详情用于确认
  const selectedAdminsList = filteredAdmins.value.filter(admin => selectedIds.includes(admin.id))
  
  const confirmMessage = `确定要删除选中的 ${selectedIds.length} 个管理员吗？\n\n删除详情：\n- 管理员数量：${selectedIds.length} 个\n- 涉及管理员：${selectedAdminsList.map(a => a.name).join('、')}\n\n此操作不可撤销！`
  
  if (confirm(confirmMessage)) {
    try {
      // 从管理员列表中删除选中的管理员
      filteredAdmins.value = filteredAdmins.value.filter(admin => !selectedIds.includes(admin.id))
      adminTotalItems.value -= selectedIds.length
      
      // 清空选择
      adminSelectAll.value = false
      adminSelectedItems.value = {}
      
      // 如果当前页没有数据了，回到上一页
      if (filteredAdmins.value.length === 0 && adminCurrentPage.value > 1) {
        adminCurrentPage.value--
      }
      
      // 显示删除成功信息
      alert(`批量删除成功！\n已删除 ${selectedIds.length} 个管理员`)
      
      // 记录删除操作日志
      console.log('批量删除管理员:', {
        deletedIds: selectedIds,
        deletedAdmins: selectedAdminsList,
        timestamp: new Date().toISOString()
      })
      
    } catch (error) {
      console.error('批量删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

const changeAdminPage = (page: number) => {
  adminCurrentPage.value = page
  // 分页逻辑
}

// 收藏信息管理相关状态
const favoritesSearchKeyword = ref('')
const favoritesSelectAll = ref(false)
const favoritesSelectedItems = ref<Record<number, boolean>>({})
const showAddFavoriteDialog = ref(false)
const favoritesCurrentPage = ref(1)
const favoritesPageSize = ref(10)
const favoritesTotalItems = ref(3)
const favoritesTotalPages = computed(() => Math.ceil(favoritesTotalItems.value / favoritesPageSize.value))
const selectedFavorites = computed(() => {
  return Object.entries(favoritesSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => filteredFavorites.value[parseInt(index)]?.id)
    .filter(Boolean)
})

// 新增收藏信息表单
const newFavoriteForm = ref({
  serviceName: '',
  userName: '',
  favoriteTime: ''
})

// 表单验证错误
const favoriteFormErrors = ref({
  serviceName: '',
  userName: '',
  favoriteTime: ''
})

// 编辑收藏信息相关状态
const showEditFavoriteDialog = ref(false)
const editingFavoriteId = ref('')

// 编辑收藏信息表单
const editFavoriteForm = ref({
  serviceName: '',
  userName: '',
  favoriteTime: ''
})

// 编辑表单验证错误
const editFavoriteFormErrors = ref({
  serviceName: '',
  userName: '',
  favoriteTime: ''
})

// 轮播图信息管理相关状态
const carouselSearchKeyword = ref('')
const carouselSelectAll = ref(false)
const carouselSelectedItems = ref<Record<number, boolean>>({})
const showAddCarouselDialog = ref(false)
const carouselCurrentPage = ref(1)
const carouselPageSize = ref(10)
const carouselTotalItems = ref(3)
const carouselTotalPages = computed(() => Math.ceil(carouselTotalItems.value / carouselPageSize.value))
const selectedCarousels = computed(() => {
  return Object.entries(carouselSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => filteredCarousels.value[parseInt(index)]?.id)
    .filter(Boolean)
})

// 编辑轮播图信息相关状态
const showEditCarouselDialog = ref(false)
const editingCarouselId = ref('')

// 编辑轮播图信息表单
const editCarouselForm = ref({
  serviceName: '',
  imageUrl: ''
})

// 编辑表单验证错误
const editCarouselFormErrors = ref({
  serviceName: '',
  imageUrl: ''
})

// 新增轮播图信息表单
const newCarouselForm = ref({
  serviceName: '',
  imageUrl: ''
})

// 新增表单验证错误
const newCarouselFormErrors = ref({
  serviceName: '',
  imageUrl: ''
})

// 模拟轮播图数据
const allCarousels = ref([
  { id: '1', serviceName: '全屋清洁 [大扫除]', imageUrl: 'https://picsum.photos/seed/carousel1/200/100' },
  { id: '2', serviceName: '日常保洁 [深度清洁]', imageUrl: 'https://picsum.photos/seed/carousel2/200/100' },
  { id: '3', serviceName: '家电清洗 [空调]', imageUrl: 'https://picsum.photos/seed/carousel3/200/100' }
])

// 过滤后的轮播图数据
const filteredCarousels = computed(() => {
  let filtered = allCarousels.value
  
  // 搜索过滤
  if (carouselSearchKeyword.value) {
    const keyword = carouselSearchKeyword.value.toLowerCase()
    filtered = filtered.filter(carousel => 
      carousel.serviceName.toLowerCase().includes(keyword)
    )
  }
  
  // 更新总条数
  carouselTotalItems.value = filtered.length
  
  // 分页
  const startIndex = (carouselCurrentPage.value - 1) * carouselPageSize.value
  const endIndex = startIndex + carouselPageSize.value
  return filtered.slice(startIndex, endIndex)
})

// 计算要显示的页码
const carouselPagesToShow = computed(() => {
  const pages = []
  const total = carouselTotalPages.value
  const current = carouselCurrentPage.value
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
    } else if (current >= total - 2) {
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      for (let i = current - 2; i <= current + 2; i++) {
        pages.push(i)
      }
    }
  }
  
  return pages
})

// 轮播图信息管理方法
const searchCarousels = () => {
  console.log('搜索轮播图:', carouselSearchKeyword.value)
  carouselCurrentPage.value = 1
}

const resetCarouselSearch = () => {
  carouselSearchKeyword.value = ''
  carouselCurrentPage.value = 1
  console.log('重置轮播图搜索')
}

const toggleCarouselSelectAll = () => {
  filteredCarousels.value.forEach((_, index) => {
    carouselSelectedItems.value[index] = carouselSelectAll.value
  })
}

const editCarousel = (carousel: any) => {
  console.log('编辑轮播图信息:', carousel)
  
  // 设置编辑状态
  editingCarouselId.value = carousel.id
  
  // 填充表单数据
  editCarouselForm.value = {
    serviceName: carousel.serviceName,
    imageUrl: carousel.imageUrl
  }
  
  // 清空错误信息
  editCarouselFormErrors.value = {
    serviceName: '',
    imageUrl: ''
  }
  
  // 打开编辑对话框
  showEditCarouselDialog.value = true
}

const deleteCarousel = (id: string) => {
  if (confirm('确定要删除这条轮播图信息吗？')) {
    try {
      // 从轮播图列表中删除
      const index = allCarousels.value.findIndex(carousel => carousel.id === id)
      if (index > -1) {
        allCarousels.value.splice(index, 1)
        carouselTotalItems.value--
        alert('删除成功')
        console.log('删除轮播图信息:', id)
        
        // 如果当前页没有数据了，回到上一页
        if (filteredCarousels.value.length === 0 && carouselCurrentPage.value > 1) {
          carouselCurrentPage.value--
        }
      }
    } catch (error) {
      console.error('删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

const batchDeleteCarousels = () => {
  const selectedIds = selectedCarousels.value
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的轮播图信息')
    return
  }
  
  // 获取要删除的轮播图详情用于确认
  const selectedRecords = allCarousels.value.filter(carousel => selectedIds.includes(carousel.id))
  
  const confirmMessage = `确定要删除选中的 ${selectedIds.length} 条轮播图信息吗？\n\n删除详情：\n- 记录数量：${selectedIds.length} 条\n- 涉及服务：${selectedRecords.map(r => r.serviceName).join('、')}\n\n此操作不可撤销！`
  
  if (confirm(confirmMessage)) {
    try {
      // 从轮播图列表中删除选中的记录
      allCarousels.value = allCarousels.value.filter(carousel => !selectedIds.includes(carousel.id))
      carouselTotalItems.value -= selectedIds.length
      
      // 清空选择
      carouselSelectAll.value = false
      carouselSelectedItems.value = {}
      
      // 如果当前页没有数据了，回到上一页
      if (filteredCarousels.value.length === 0 && carouselCurrentPage.value > 1) {
        carouselCurrentPage.value--
      }
      
      // 显示删除成功信息
      alert(`批量删除成功！\n已删除 ${selectedIds.length} 条轮播图信息`)
      
      // 记录删除操作日志
      console.log('批量删除轮播图信息:', {
        deletedIds: selectedIds,
        deletedRecords: selectedRecords,
        timestamp: new Date().toISOString()
      })
      
    } catch (error) {
      console.error('批量删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

const changeCarouselPage = (page: number) => {
  if (page >= 1 && page <= carouselTotalPages.value) {
    carouselCurrentPage.value = page
    // 重置选择
    carouselSelectAll.value = false
    carouselSelectedItems.value = {}
  }
}

// 编辑轮播图信息相关函数
const validateEditCarouselForm = () => {
  editCarouselFormErrors.value = {
    serviceName: '',
    imageUrl: ''
  }
  
  let isValid = true
  
  if (!editCarouselForm.value.serviceName.trim()) {
    editCarouselFormErrors.value.serviceName = '请输入家政服务名称'
    isValid = false
  }
  
  if (!editCarouselForm.value.imageUrl.trim()) {
    editCarouselFormErrors.value.imageUrl = '请选择轮播图图片'
    isValid = false
  }
  
  return isValid
}

const saveEditCarousel = () => {
  if (!validateEditCarouselForm()) {
    return
  }
  
  try {
    // 找到要编辑的轮播图信息
    const carouselIndex = allCarousels.value.findIndex(carousel => carousel.id === editingCarouselId.value)
    
    if (carouselIndex === -1) {
      alert('轮播图信息不存在')
      return
    }
    
    // 更新轮播图信息
    allCarousels.value[carouselIndex] = {
      ...allCarousels.value[carouselIndex],
      serviceName: editCarouselForm.value.serviceName.trim(),
      imageUrl: editCarouselForm.value.imageUrl.trim()
    }
    
    // 关闭对话框
    showEditCarouselDialog.value = false
    
    // 重置编辑状态
    editingCarouselId.value = ''
    
    // 重置表单
    resetEditCarouselForm()
    
    // 显示成功消息
    alert('编辑轮播图信息成功！')
    
    console.log('编辑轮播图信息成功:', allCarousels.value[carouselIndex])
    
  } catch (error) {
    console.error('编辑失败:', error)
    alert('编辑失败，请重试')
  }
}

const cancelEditCarousel = () => {
  showEditCarouselDialog.value = false
  editingCarouselId.value = ''
  resetEditCarouselForm()
}

const resetEditCarouselForm = () => {
  editCarouselForm.value = {
    serviceName: '',
    imageUrl: ''
  }
  editCarouselFormErrors.value = {
    serviceName: '',
    imageUrl: ''
  }
}

const changeCarouselImage = () => {
  // 打开文件选择器
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e: Event) => {
    const target = e.target as HTMLInputElement
    const file = target.files?.[0]
    if (file) {
      // 创建文件预览URL
      const reader = new FileReader()
      reader.onload = () => {
        editCarouselForm.value.imageUrl = reader.result as string
      }
      reader.readAsDataURL(file)
    }
  }
  input.click()
}

const handleCarouselImageUpload = (e: Event) => {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    // 创建文件预览URL
    const reader = new FileReader()
    reader.onload = () => {
      editCarouselForm.value.imageUrl = reader.result as string
    }
    reader.readAsDataURL(file)
  }
}

// 新增轮播图信息相关函数
const validateNewCarouselForm = () => {
  newCarouselFormErrors.value = {
    serviceName: '',
    imageUrl: ''
  }
  
  let isValid = true
  
  if (!newCarouselForm.value.serviceName.trim()) {
    newCarouselFormErrors.value.serviceName = '请输入家政服务名称'
    isValid = false
  }
  
  if (!newCarouselForm.value.imageUrl.trim()) {
    newCarouselFormErrors.value.imageUrl = '请选择轮播图图片'
    isValid = false
  }
  
  return isValid
}

const saveNewCarousel = () => {
  if (!validateNewCarouselForm()) {
    return
  }
  
  try {
    // 生成新的ID
    const newId = (allCarousels.value.length + 1).toString()
    
    // 创建新的轮播图信息
    const newCarousel = {
      id: newId,
      serviceName: newCarouselForm.value.serviceName.trim(),
      imageUrl: newCarouselForm.value.imageUrl.trim()
    }
    
    // 添加到轮播图列表
    allCarousels.value.unshift(newCarousel)
    carouselTotalItems.value++
    
    // 关闭对话框
    showAddCarouselDialog.value = false
    
    // 重置表单
    resetNewCarouselForm()
    
    // 显示成功消息
    alert('新增轮播图信息成功！')
    
    console.log('新增轮播图信息:', newCarousel)
    
  } catch (error) {
    console.error('新增失败:', error)
    alert('新增失败，请重试')
  }
}

const cancelAddCarousel = () => {
  showAddCarouselDialog.value = false
  resetNewCarouselForm()
}

const resetNewCarouselForm = () => {
  newCarouselForm.value = {
    serviceName: '',
    imageUrl: ''
  }
  newCarouselFormErrors.value = {
    serviceName: '',
    imageUrl: ''
  }
}

const changeNewCarouselImage = () => {
  // 打开文件选择器
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e: Event) => {
    const target = e.target as HTMLInputElement
    const file = target.files?.[0]
    if (file) {
      // 创建文件预览URL
      const reader = new FileReader()
      reader.onload = () => {
        newCarouselForm.value.imageUrl = reader.result as string
      }
      reader.readAsDataURL(file)
    }
  }
  input.click()
}

const handleNewCarouselImageUpload = (e: Event) => {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    // 创建文件预览URL
    const reader = new FileReader()
    reader.onload = () => {
      newCarouselForm.value.imageUrl = reader.result as string
    }
    reader.readAsDataURL(file)
  }
}

// 居家贴士管理相关状态
const tipsSearchKeyword = ref('')
const tipsSelectAll = ref(false)
const tipsSelectedItems = ref<Record<number, boolean>>({})
const showAddTipDialog = ref(false)
const tipsCurrentPage = ref(1)
const tipsPageSize = ref(10)
const tipsTotalItems = ref(3)
const tipsTotalPages = computed(() => Math.ceil(tipsTotalItems.value / tipsPageSize.value))
const selectedTips = computed(() => {
  return Object.entries(tipsSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => filteredTips.value[parseInt(index)]?.id)
    .filter(Boolean)
})

// 新增居家贴士表单
const newTipForm = ref({
  title: '',
  content: ''
})

// 新增表单验证错误
const newTipFormErrors = ref({
  title: '',
  content: ''
})

// 模拟居家贴士数据
const allTips = ref([
  { id: '1', title: '厨房清洁小窍门', content: '使用白醋和小苏打可以有效清洁厨房油污，既环保又健康。', publishTime: '2025-01-10 10:00:00' },
  { id: '2', title: '衣物保养技巧', content: '深色衣物翻面洗涤可以有效防止褪色，延长衣物使用寿命。', publishTime: '2025-01-15 14:30:00' },
  { id: '3', title: '室内空气净化方法', content: '摆放绿植如吊兰、芦荟等可以有效净化室内空气，改善居住环境。', publishTime: '2025-01-20 09:15:00' }
])

// 过滤后的居家贴士数据
const filteredTips = computed(() => {
  let filtered = allTips.value
  
  // 搜索过滤
  if (tipsSearchKeyword.value) {
    const keyword = tipsSearchKeyword.value.toLowerCase()
    filtered = filtered.filter(tip => 
      tip.title.toLowerCase().includes(keyword) ||
      tip.content.toLowerCase().includes(keyword)
    )
  }
  
  // 更新总条数
  tipsTotalItems.value = filtered.length
  
  // 分页
  const startIndex = (tipsCurrentPage.value - 1) * tipsPageSize.value
  const endIndex = startIndex + tipsPageSize.value
  return filtered.slice(startIndex, endIndex)
})

// 计算要显示的页码
const tipsPagesToShow = computed(() => {
  const pages = []
  const total = tipsTotalPages.value
  const current = tipsCurrentPage.value
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
    } else if (current >= total - 2) {
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      for (let i = current - 2; i <= current + 2; i++) {
        pages.push(i)
      }
    }
  }
  
  return pages
})

// 居家贴士管理方法
const searchTips = () => {
  console.log('搜索居家贴士:', tipsSearchKeyword.value)
  tipsCurrentPage.value = 1
}

const resetTipsSearch = () => {
  tipsSearchKeyword.value = ''
  tipsCurrentPage.value = 1
  console.log('重置居家贴士搜索')
}

const toggleTipsSelectAll = () => {
  filteredTips.value.forEach((_, index) => {
    tipsSelectedItems.value[index] = tipsSelectAll.value
  })
}

const editTip = (tip: any) => {
  console.log('编辑居家贴士:', tip)
  alert('编辑居家贴士功能开发中')
}

const deleteTip = (id: string) => {
  if (confirm('确定要删除这条居家贴士吗？')) {
    try {
      // 从贴士列表中删除
      const index = allTips.value.findIndex(tip => tip.id === id)
      if (index > -1) {
        allTips.value.splice(index, 1)
        tipsTotalItems.value--
        alert('删除成功')
        console.log('删除居家贴士:', id)
        
        // 如果当前页没有数据了，回到上一页
        if (filteredTips.value.length === 0 && tipsCurrentPage.value > 1) {
          tipsCurrentPage.value--
        }
      }
    } catch (error) {
      console.error('删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

const batchDeleteTips = () => {
  const selectedIds = selectedTips.value
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的居家贴士')
    return
  }
  
  // 获取要删除的贴士详情用于确认
  const selectedRecords = allTips.value.filter(tip => selectedIds.includes(tip.id))
  
  const confirmMessage = `确定要删除选中的 ${selectedIds.length} 条居家贴士吗？\n\n删除详情：\n- 记录数量：${selectedIds.length} 条\n- 涉及贴士：${selectedRecords.map(r => r.title).join('、')}\n\n此操作不可撤销！`
  
  if (confirm(confirmMessage)) {
    try {
      // 从贴士列表中删除选中的记录
      allTips.value = allTips.value.filter(tip => !selectedIds.includes(tip.id))
      tipsTotalItems.value -= selectedIds.length
      
      // 清空选择
      tipsSelectAll.value = false
      tipsSelectedItems.value = {}
      
      // 如果当前页没有数据了，回到上一页
      if (filteredTips.value.length === 0 && tipsCurrentPage.value > 1) {
        tipsCurrentPage.value--
      }
      
      // 显示删除成功信息
      alert(`批量删除成功！\n已删除 ${selectedIds.length} 条居家贴士`)
      
      // 记录删除操作日志
      console.log('批量删除居家贴士:', {
        deletedIds: selectedIds,
        deletedRecords: selectedRecords,
        timestamp: new Date().toISOString()
      })
      
    } catch (error) {
      console.error('批量删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

const changeTipsPage = (page: number) => {
  if (page >= 1 && page <= tipsTotalPages.value) {
    tipsCurrentPage.value = page
    // 重置选择
    tipsSelectAll.value = false
    tipsSelectedItems.value = {}
  }
}

// 新增居家贴士相关函数
const validateNewTipForm = () => {
  newTipFormErrors.value = {
    title: '',
    content: ''
  }
  
  let isValid = true
  
  if (!newTipForm.value.title.trim()) {
    newTipFormErrors.value.title = '请输入贴士标题'
    isValid = false
  }
  
  if (!newTipForm.value.content.trim()) {
    newTipFormErrors.value.content = '请输入贴士内容'
    isValid = false
  }
  
  return isValid
}

const saveNewTip = () => {
  if (!validateNewTipForm()) {
    return
  }
  
  try {
    // 生成新的ID
    const newId = (allTips.value.length + 1).toString()
    
    // 创建新的居家贴士
    const newTip = {
      id: newId,
      title: newTipForm.value.title.trim(),
      content: newTipForm.value.content.trim(),
      publishTime: new Date().toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      }).replace(/\//g, '-')
    }
    
    // 添加到贴士列表
    allTips.value.unshift(newTip)
    tipsTotalItems.value++
    
    // 关闭对话框
    showAddTipDialog.value = false
    
    // 重置表单
    resetNewTipForm()
    
    // 显示成功消息
    alert('新增居家贴士成功！')
    
    console.log('新增居家贴士:', newTip)
    
  } catch (error) {
    console.error('新增失败:', error)
    alert('新增失败，请重试')
  }
}

const cancelAddTip = () => {
  showAddTipDialog.value = false
  resetNewTipForm()
}

const resetNewTipForm = () => {
  newTipForm.value = {
    title: '',
    content: ''
  }
  newTipFormErrors.value = {
    title: '',
    content: ''
  }
}

// 模拟收藏数据
const allFavorites = ref([
  {
    id: '1',
    serviceName: '全屋清洁 [大扫除]',
    userName: '小张',
    favoriteTime: '2025-05-23 09:53:42'
  },
  {
    id: '2',
    serviceName: '日常保洁 [深度清洁]',
    userName: '小李',
    favoriteTime: '2025-05-22 14:32:18'
  },
  {
    id: '3',
    serviceName: '家电清洗 [空调]',
    userName: '小王',
    favoriteTime: '2025-05-21 10:15:57'
  },
  {
    id: '4',
    serviceName: '厨房保洁服务',
    userName: '赵六',
    favoriteTime: '2025-05-20 16:45:00'
  },
  {
    id: '5',
    serviceName: '卫生间保洁服务',
    userName: '钱七',
    favoriteTime: '2025-05-19 11:20:00'
  },
  {
    id: '6',
    serviceName: '家具保养服务',
    userName: '周八',
    favoriteTime: '2025-05-18 15:30:00'
  },
  {
    id: '7',
    serviceName: '地面清洁服务',
    userName: '吴九',
    favoriteTime: '2025-05-17 13:40:00'
  },
  {
    id: '8',
    serviceName: '新居开荒服务',
    userName: '郑十',
    favoriteTime: '2025-05-16 10:50:00'
  },
  {
    id: '9',
    serviceName: '沙发清洁服务',
    userName: '孙十一',
    favoriteTime: '2025-05-15 12:10:00'
  },
  {
    id: '10',
    serviceName: '擦玻璃服务',
    userName: '李十二',
    favoriteTime: '2025-05-14 14:30:00'
  }
])

// 过滤后的收藏信息数据
const filteredFavorites = computed(() => {
  let filtered = allFavorites.value
  
  // 搜索过滤
  if (favoritesSearchKeyword.value) {
    const keyword = favoritesSearchKeyword.value.toLowerCase()
    filtered = filtered.filter(favorite => 
      favorite.serviceName.toLowerCase().includes(keyword) ||
      favorite.userName.toLowerCase().includes(keyword)
    )
  }
  
  // 更新总条数
  favoritesTotalItems.value = filtered.length
  
  // 分页
  const startIndex = (favoritesCurrentPage.value - 1) * favoritesPageSize.value
  const endIndex = startIndex + favoritesPageSize.value
  return filtered.slice(startIndex, endIndex)
})

// 计算要显示的页码
const favoritesPagesToShow = computed(() => {
  const pages = []
  const total = favoritesTotalPages.value
  const current = favoritesCurrentPage.value
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
    } else if (current >= total - 2) {
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      for (let i = current - 2; i <= current + 2; i++) {
        pages.push(i)
      }
    }
  }
  
  return pages
})

// 收藏信息管理方法
const searchFavorites = () => {
  console.log('搜索收藏信息:', favoritesSearchKeyword.value)
  favoritesCurrentPage.value = 1
  // 搜索功能通过 filteredFavorites 计算属性实现
}

const resetFavoritesSearch = () => {
  favoritesSearchKeyword.value = ''
  favoritesCurrentPage.value = 1
  console.log('重置收藏信息搜索')
}

const toggleFavoritesSelectAll = () => {
  filteredFavorites.value.forEach((_, index) => {
    favoritesSelectedItems.value[index] = favoritesSelectAll.value
  })
}

const editFavorite = (favorite: any) => {
  console.log('编辑收藏信息:', favorite)
  
  // 设置编辑状态
  editingFavoriteId.value = favorite.id
  
  // 填充表单数据
  editFavoriteForm.value = {
    serviceName: favorite.serviceName,
    userName: favorite.userName,
    favoriteTime: formatDateTimeForInput(favorite.favoriteTime)
  }
  
  // 清空错误信息
  editFavoriteFormErrors.value = {
    serviceName: '',
    userName: '',
    favoriteTime: ''
  }
  
  // 打开编辑对话框
  showEditFavoriteDialog.value = true
}

const deleteFavorite = (id: string) => {
  if (confirm('确定要删除这条收藏信息吗？')) {
    try {
      // 从收藏列表中删除
      const index = allFavorites.value.findIndex(favorite => favorite.id === id)
      if (index > -1) {
        allFavorites.value.splice(index, 1)
        favoritesTotalItems.value--
        alert('删除成功')
        console.log('删除收藏信息:', id)
        
        // 如果当前页没有数据了，回到上一页
        if (filteredFavorites.value.length === 0 && favoritesCurrentPage.value > 1) {
          favoritesCurrentPage.value--
        }
      }
    } catch (error) {
      console.error('删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

const batchDeleteFavorites = () => {
  const selectedIds = selectedFavorites.value
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的收藏信息')
    return
  }
  
  // 获取要删除的收藏详情用于确认
  const selectedRecords = allFavorites.value.filter(favorite => selectedIds.includes(favorite.id))
  
  const confirmMessage = `确定要删除选中的 ${selectedIds.length} 条收藏信息吗？\n\n删除详情：\n- 记录数量：${selectedIds.length} 条\n- 涉及服务：${selectedRecords.map(r => r.serviceName).join('、')}\n\n此操作不可撤销！`
  
  if (confirm(confirmMessage)) {
    try {
      // 从收藏列表中删除选中的记录
      allFavorites.value = allFavorites.value.filter(favorite => !selectedIds.includes(favorite.id))
      favoritesTotalItems.value -= selectedIds.length
      
      // 清空选择
      favoritesSelectAll.value = false
      favoritesSelectedItems.value = {}
      
      // 如果当前页没有数据了，回到上一页
      if (filteredFavorites.value.length === 0 && favoritesCurrentPage.value > 1) {
        favoritesCurrentPage.value--
      }
      
      // 显示删除成功信息
      alert(`批量删除成功！\n已删除 ${selectedIds.length} 条收藏信息`)
      
      // 记录删除操作日志
      console.log('批量删除收藏信息:', {
        deletedIds: selectedIds,
        deletedRecords: selectedRecords,
        timestamp: new Date().toISOString()
      })
      
    } catch (error) {
      console.error('批量删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

const changeFavoritesPage = (page: number) => {
  if (page >= 1 && page <= favoritesTotalPages.value) {
    favoritesCurrentPage.value = page
    // 重置选择
    favoritesSelectAll.value = false
    favoritesSelectedItems.value = {}
  }
}

// 新增收藏信息相关函数
const validateFavoriteForm = () => {
  favoriteFormErrors.value = {
    serviceName: '',
    userName: '',
    favoriteTime: ''
  }
  
  let isValid = true
  
  if (!newFavoriteForm.value.serviceName.trim()) {
    favoriteFormErrors.value.serviceName = '请输入服务名称'
    isValid = false
  }
  
  if (!newFavoriteForm.value.userName.trim()) {
    favoriteFormErrors.value.userName = '请输入用户名称'
    isValid = false
  }
  
  if (!newFavoriteForm.value.favoriteTime) {
    favoriteFormErrors.value.favoriteTime = '请选择收藏时间'
    isValid = false
  }
  
  return isValid
}

const saveNewFavorite = () => {
  if (!validateFavoriteForm()) {
    return
  }
  
  try {
    // 生成新的ID
    const newId = (allFavorites.value.length + 1).toString()
    
    // 格式化时间
    const favoriteTime = new Date(newFavoriteForm.value.favoriteTime).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    }).replace(/\//g, '-')
    
    // 创建新的收藏信息
    const newFavorite = {
      id: newId,
      serviceName: newFavoriteForm.value.serviceName.trim(),
      userName: newFavoriteForm.value.userName.trim(),
      favoriteTime: favoriteTime
    }
    
    // 添加到收藏列表
    allFavorites.value.unshift(newFavorite)
    favoritesTotalItems.value++
    
    // 关闭对话框
    showAddFavoriteDialog.value = false
    
    // 重置表单
    resetFavoriteForm()
    
    // 显示成功消息
    alert('新增收藏信息成功！')
    
    console.log('新增收藏信息:', newFavorite)
    
  } catch (error) {
    console.error('新增失败:', error)
    alert('新增失败，请重试')
  }
}

const cancelAddFavorite = () => {
  showAddFavoriteDialog.value = false
  resetFavoriteForm()
}

const resetFavoriteForm = () => {
  newFavoriteForm.value = {
    serviceName: '',
    userName: '',
    favoriteTime: ''
  }
  favoriteFormErrors.value = {
    serviceName: '',
    userName: '',
    favoriteTime: ''
  }
}

// 编辑收藏信息相关函数
const validateEditFavoriteForm = () => {
  editFavoriteFormErrors.value = {
    serviceName: '',
    userName: '',
    favoriteTime: ''
  }
  
  let isValid = true
  
  if (!editFavoriteForm.value.serviceName.trim()) {
    editFavoriteFormErrors.value.serviceName = '请输入服务名称'
    isValid = false
  }
  
  if (!editFavoriteForm.value.userName.trim()) {
    editFavoriteFormErrors.value.userName = '请输入用户名称'
    isValid = false
  }
  
  if (!editFavoriteForm.value.favoriteTime) {
    editFavoriteFormErrors.value.favoriteTime = '请选择收藏时间'
    isValid = false
  }
  
  return isValid
}

const saveEditFavorite = () => {
  if (!validateEditFavoriteForm()) {
    return
  }
  
  try {
    // 找到要编辑的收藏信息
    const favoriteIndex = allFavorites.value.findIndex(favorite => favorite.id === editingFavoriteId.value)
    
    if (favoriteIndex === -1) {
      alert('收藏信息不存在')
      return
    }
    
    // 格式化时间
    const favoriteTime = new Date(editFavoriteForm.value.favoriteTime).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    }).replace(/\//g, '-')
    
    // 更新收藏信息
    allFavorites.value[favoriteIndex] = {
      ...allFavorites.value[favoriteIndex],
      serviceName: editFavoriteForm.value.serviceName.trim(),
      userName: editFavoriteForm.value.userName.trim(),
      favoriteTime: favoriteTime
    }
    
    // 关闭对话框
    showEditFavoriteDialog.value = false
    
    // 重置编辑状态
    editingFavoriteId.value = ''
    
    // 重置表单
    resetEditFavoriteForm()
    
    // 显示成功消息
    alert('编辑收藏信息成功！')
    
    console.log('编辑收藏信息成功:', allFavorites.value[favoriteIndex])
    
  } catch (error) {
    console.error('编辑失败:', error)
    alert('编辑失败，请重试')
  }
}

const cancelEditFavorite = () => {
  showEditFavoriteDialog.value = false
  editingFavoriteId.value = ''
  resetEditFavoriteForm()
}

const resetEditFavoriteForm = () => {
  editFavoriteForm.value = {
    serviceName: '',
    userName: '',
    favoriteTime: ''
  }
  editFavoriteFormErrors.value = {
    serviceName: '',
    userName: '',
    favoriteTime: ''
  }
}

// 时间格式化辅助函数
const formatDateTimeForInput = (dateTimeString: string) => {
  // 将 "2025-05-23 09:53:42" 格式转换为 datetime-local 输入框需要的格式
  const date = new Date(dateTimeString.replace(/-/g, '/'))
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  
  return `${year}-${month}-${day}T${hours}:${minutes}`
}

// 获取菜单标题
const getMenuTitle = () => {
  const menuTitles: Record<string, string> = {
    home: '系统首页',
    statistics: '数据统计',
    appointment: '服务预约管理',
    services: '家政服务管理',
    withdrawal: '提现记录管理',
    category: '服务分类管理',
    notice: '系统公告管理',
    evaluation: '服务评价管理',
    tips: '居家贴士管理',
    carousel: '轮播图信息管理',
    favorites: '收藏信息管理',
    providerCert: '服务者认证管理',
    normalUser: '普通用户管理',
    housekeepingProvider: '家政服务者管理',
    adminUser: '管理员管理'
  }
  return menuTitles[activeMenu.value] || '功能页面'
}

// 个人资料相关状态
const showPersonalProfileDialog = ref(false)
const isEditingProfile = ref(false)
const personalProfileForm = ref({
  username: 'admin',
  name: '超级管理员',
  email: 'admin@housekeeping.com',
  phone: '13800138000',
  role: '超级管理员',
  department: '技术部',
  joinDate: '2024-01-01',
  lastLogin: '2024-12-19 14:30:00',
  avatar: ''
})

// 处理个人资料
const handlePersonalProfile = () => {
  showUserDropdown.value = false
  showPersonalProfileDialog.value = true
  isEditingProfile.value = false
}

// 编辑个人资料
const editPersonalProfile = () => {
  isEditingProfile.value = true
}

// 保存个人资料
const savePersonalProfile = () => {
  // 验证表单
  if (!personalProfileForm.value.name.trim()) {
    alert('请输入姓名')
    return
  }
  if (!personalProfileForm.value.email.trim()) {
    alert('请输入邮箱')
    return
  }
  if (!personalProfileForm.value.phone.trim()) {
    alert('请输入手机号')
    return
  }
  
  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(personalProfileForm.value.email)) {
    alert('请输入正确的邮箱格式')
    return
  }
  
  // 验证手机号格式
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(personalProfileForm.value.phone)) {
    alert('请输入正确的手机号格式')
    return
  }
  
  try {
    // 这里应该调用API保存数据
    console.log('保存个人资料:', personalProfileForm.value)
    
    // 更新用户信息显示
    userInfo.value = {
      ...userInfo.value,
      username: personalProfileForm.value.username,
      name: personalProfileForm.value.name
    }
    
    isEditingProfile.value = false
    alert('个人资料保存成功！')
    
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败，请重试')
  }
}

// 取消编辑个人资料
const cancelPersonalProfileEdit = () => {
  isEditingProfile.value = false
  // 重置表单数据（这里可以从服务器重新获取）
}

// 关闭个人资料对话框
const closePersonalProfileDialog = () => {
  showPersonalProfileDialog.value = false
  isEditingProfile.value = false
}

// 处理修改密码
const handleChangePassword = () => {
  showUserDropdown.value = false
  // 重置表单和错误信息
  changePasswordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  passwordErrors.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  // 显示模态框
  showChangePasswordModal.value = true
}

// 数据统计图表绘制功能
const drawRechargeTrendChart = () => {
  const canvas = document.getElementById('rechargeTrendChart') as HTMLCanvasElement
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  // 设置画布尺寸
  canvas.width = 600
  canvas.height = 300
  
  // 数据
  const data = [
    { date: '2025-05-15', amount: 1400 },
    { date: '2025-05-16', amount: 1800 },
    { date: '2025-05-17', amount: 2500 },
    { date: '2025-05-18', amount: 1200 },
    { date: '2025-05-19', amount: 0 },
    { date: '2025-05-20', amount: 2000 },
    { date: '2025-05-21', amount: 1375 }
  ]
  
  const maxAmount = Math.max(...data.map(d => d.amount))
  const padding = 40
  const chartWidth = canvas.width - padding * 2
  const chartHeight = canvas.height - padding * 2
  
  // 绘制背景
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, canvas.width, canvas.height)
  
  // 绘制网格线
  ctx.strokeStyle = '#e5e7eb'
  ctx.lineWidth = 1
  
  // 水平网格线
  for (let i = 0; i <= 5; i++) {
    const y = padding + (chartHeight / 5) * i
    ctx.beginPath()
    ctx.moveTo(padding, y)
    ctx.lineTo(canvas.width - padding, y)
    ctx.stroke()
  }
  
  // 垂直网格线
  for (let i = 0; i <= 6; i++) {
    const x = padding + (chartWidth / 6) * i
    ctx.beginPath()
    ctx.moveTo(x, padding)
    ctx.lineTo(x, canvas.height - padding)
    ctx.stroke()
  }
  
  // 绘制Y轴标签
  ctx.fillStyle = '#6b7280'
  ctx.font = '12px Arial'
  ctx.textAlign = 'right'
  for (let i = 0; i <= 5; i++) {
    const value = (maxAmount / 5) * (5 - i)
    const y = padding + (chartHeight / 5) * i + 4
    ctx.fillText(value.toString(), padding - 10, y)
  }
  
  // 绘制X轴标签
  ctx.textAlign = 'center'
  data.forEach((item, index) => {
    const x = padding + (chartWidth / 6) * index
    const y = canvas.height - padding + 20
    ctx.fillText(item.date.split('-')[2], x, y)
  })
  
  // 绘制数据点和连线
  ctx.strokeStyle = '#8b5cf6'
  ctx.lineWidth = 3
  ctx.beginPath()
  
  data.forEach((item, index) => {
    const x = padding + (chartWidth / 6) * index
    const y = padding + chartHeight - (item.amount / maxAmount) * chartHeight
    
    if (index === 0) {
      ctx.moveTo(x, y)
    } else {
      ctx.lineTo(x, y)
    }
  })
  ctx.stroke()
  
  // 绘制数据点
  ctx.fillStyle = '#8b5cf6'
  data.forEach((item, index) => {
    const x = padding + (chartWidth / 6) * index
    const y = padding + chartHeight - (item.amount / maxAmount) * chartHeight
    
    ctx.beginPath()
    ctx.arc(x, y, 4, 0, 2 * Math.PI)
    ctx.fill()
  })
  
  // 绘制填充区域
  const gradient = ctx.createLinearGradient(0, padding, 0, canvas.height - padding)
  gradient.addColorStop(0, 'rgba(139, 92, 246, 0.3)')
  gradient.addColorStop(1, 'rgba(139, 92, 246, 0.05)')
  
  ctx.fillStyle = gradient
  ctx.beginPath()
  ctx.moveTo(padding, canvas.height - padding)
  
  data.forEach((item, index) => {
    const x = padding + (chartWidth / 6) * index
    const y = padding + chartHeight - (item.amount / maxAmount) * chartHeight
    ctx.lineTo(x, y)
  })
  
  ctx.lineTo(canvas.width - padding, canvas.height - padding)
  ctx.closePath()
  ctx.fill()
  
  // 绘制最后一点的虚线
  const lastX = padding + (chartWidth / 6) * 6
  const lastY = padding + chartHeight - (data[6].amount / maxAmount) * chartHeight
  
  ctx.strokeStyle = '#8b5cf6'
  ctx.lineWidth = 1
  ctx.setLineDash([5, 5])
  ctx.beginPath()
  ctx.moveTo(lastX, lastY)
  ctx.lineTo(canvas.width - padding, lastY)
  ctx.stroke()
  ctx.setLineDash([])
}

const drawServiceBookingChart = () => {
  const canvas = document.getElementById('serviceBookingChart') as HTMLCanvasElement
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  // 设置画布尺寸
  canvas.width = 400
  canvas.height = 300
  
  // 数据
  const data = [
    { label: '沙发保养清洁', value: 10, color: '#3b82f6' },
    { label: '地板打蜡10平米', value: 20, color: '#10b981' },
    { label: '深度保洁5小时', value: 10, color: '#f59e0b' },
    { label: '厨房保养清洁', value: 10, color: '#ef4444' },
    { label: '新房精细开荒', value: 10, color: '#06b6d4' },
    { label: '4小时全屋日常保洁', value: 20, color: '#059669' },
    { label: '全屋清洁', value: 20, color: '#f97316' }
  ]
  
  const centerX = canvas.width / 2
  const centerY = canvas.height / 2
  const radius = Math.min(centerX, centerY) - 40
  
  let currentAngle = -Math.PI / 2
  
  data.forEach((item, index) => {
    const sliceAngle = (item.value / 100) * 2 * Math.PI
    
    // 绘制扇形
    ctx.beginPath()
    ctx.moveTo(centerX, centerY)
    ctx.arc(centerX, centerY, radius, currentAngle, currentAngle + sliceAngle)
    ctx.closePath()
    ctx.fillStyle = item.color
    ctx.fill()
    
    // 绘制边框
    ctx.strokeStyle = '#ffffff'
    ctx.lineWidth = 2
    ctx.stroke()
    
    currentAngle += sliceAngle
  })
}

// 监听菜单切换，绘制图表
// watch(activeMenu, (newMenu) => {
//   if (newMenu === 'statistics') {
//     nextTick(() => {
//       try {
//         drawRechargeTrendChart()
//         drawServiceBookingChart()
//       } catch (error) {
//         console.error('图表绘制错误:', error)
//       }
//     })
//   }
// })

// 验证密码表单
const validatePasswordForm = () => {
  let isValid = true
  
  // 重置错误信息
  passwordErrors.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  
  // 验证原密码
  if (!changePasswordForm.value.oldPassword) {
    passwordErrors.value.oldPassword = '请输入原密码'
    isValid = false
  }
  
  // 验证新密码
  if (!changePasswordForm.value.newPassword) {
    passwordErrors.value.newPassword = '请输入新密码'
    isValid = false
  } else if (changePasswordForm.value.newPassword.length < 8) {
    passwordErrors.value.newPassword = '新密码至少需要8个字符'
    isValid = false
  } else if (!/[a-zA-Z]/.test(changePasswordForm.value.newPassword) || !/[0-9]/.test(changePasswordForm.value.newPassword)) {
    passwordErrors.value.newPassword = '新密码必须包含字母和数字'
    isValid = false
  }
  
  // 验证确认密码
  if (!changePasswordForm.value.confirmPassword) {
    passwordErrors.value.confirmPassword = '请确认新密码'
    isValid = false
  } else if (changePasswordForm.value.confirmPassword !== changePasswordForm.value.newPassword) {
    passwordErrors.value.confirmPassword = '两次输入的密码不一致'
    isValid = false
  }
  
  return isValid
}

// 保存修改密码
const saveChangePassword = () => {
  if (!validatePasswordForm()) {
    return
  }
  
  // 模拟API调用
  console.log('修改密码:', changePasswordForm.value)
  
  // 显示成功提示
  alert('密码修改成功！')
  
  // 关闭模态框
  showChangePasswordModal.value = false
  
  // 重置表单
  changePasswordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}


// 取消修改密码
const cancelChangePassword = () => {
  showChangePasswordModal.value = false
  // 重置表单和错误信息
  changePasswordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  passwordErrors.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}
// 服务预约相关状态和方法
const searchKeyword = ref('')
const selectAll = ref(false)
const selectedItems = ref<Record<number, boolean>>({})

// 服务预约分页相关变量
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

// 模拟预约数据
const appointments = ref([
  {
    id: 1,
    serviceName: '新房开荒打扫平米【包运送垃圾/包验收】',
    quantity: 1,
    totalAmount: 9.9,
    userName: '小张',
    providerName: '家政小丁',
    contactPhone: '13989997888',
    contactAddress: '枫叶小区12栋301',
    servicePhone: '13989997888',
    status: 'completed',
    statusText: '已完成',
    startTime: '2025-05-17 10:00:00',
    endTime: '2025-05-17 22:31:29',
    appointmentTime: '2025-05-17 22:31:18',
    assigned: true
  },
  {
    id: 2,
    serviceName: '烟机打檫10平米',
    quantity: 1,
    totalAmount: 66,
    userName: '小张',
    providerName: '家政小冯',
    contactPhone: '13989997888',
    contactAddress: '枫叶小区12栋301',
    servicePhone: '13989997888',
    status: 'completed',
    statusText: '已完成',
    startTime: '2025-05-17 10:00:00',
    endTime: '2025-05-17 22:28:56',
    appointmentTime: '2025-05-17 20:09:20',
    assigned: true
  },
  {
    id: 3,
    serviceName: '沙发保养清洗【包含皮革养护】',
    quantity: 1,
    totalAmount: 50,
    userName: 'nnn',
    providerName: '家政小冯',
    contactPhone: '13989997888',
    contactAddress: '枫叶小区12栋301',
    servicePhone: '13989998999',
    status: 'completed',
    statusText: '已完成',
    startTime: '2025-05-16 10:00:00',
    endTime: '2025-05-16 20:55:05',
    appointmentTime: '2025-05-16 08:05:57',
    assigned: true
  },
  {
    id: 4,
    serviceName: '沙发保养清洗【包含皮革养护】',
    quantity: 1,
    totalAmount: 50,
    userName: 'nnn',
    providerName: '',
    contactPhone: '13989998999',
    contactAddress: '',
    servicePhone: '',
    status: 'pending',
    statusText: '已预约',
    startTime: '2025-05-17 10:00:00',
    endTime: '2025-05-17 20:55:05',
    appointmentTime: '2025-05-16 08:05:56',
    assigned: false
  },
  {
    id: 5,
    serviceName: '烟机打檫10平米',
    quantity: 1,
    totalAmount: 66,
    userName: '小张',
    providerName: '',
    contactPhone: '13989998877',
    contactAddress: '合肥市蜀山区123号',
    servicePhone: '',
    status: 'processing',
    statusText: '待分配',
    startTime: '2025-05-17 16:00:00',
    endTime: '2025-05-17 18:00:00',
    appointmentTime: '2025-05-16 12:03:06',
    assigned: false
  }
])

// 过滤后的预约数据
const filteredAppointments = computed(() => {
  let filtered = appointments.value
  
  // 搜索过滤
  if (searchKeyword.value) {
    filtered = filtered.filter(item => 
      item.serviceName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  
  // 更新总条数
  totalItems.value = filtered.length
  
  // 分页
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  return filtered.slice(startIndex, endIndex)
})

// 计算要显示的页码
const pagesToShow = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
    } else if (current >= total - 2) {
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      for (let i = current - 2; i <= current + 2; i++) {
        pages.push(i)
      }
    }
  }
  
  return pages
})

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  // 简单格式化，实际项目中可能需要更复杂的处理
  return dateTime
}

// 切换全选
const toggleSelectAll = () => {
  filteredAppointments.value.forEach((item, index) => {
    selectedItems.value[index] = selectAll.value
  })
}

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = ''
  currentPage.value = 1
}

// 批量删除
const batchDelete = () => {
  const selectedIds = Object.entries(selectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => appointments.value[index]?.id)
    .filter(Boolean)
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的记录')
    return
  }
  
  if (confirm(`确定要删除选中的 ${selectedIds.length} 条记录吗？`)) {
    // 模拟删除操作
    console.log('批量删除:', selectedIds)
    // 这里应该调用API删除数据
    alert('删除成功')
    // 清空选择
    selectAll.value = false
    selectedItems.value = {}
  }
}

// 分配服务
const assignService = (id: number) => {
  // 模拟分配操作
  console.log('分配服务:', id)
  alert('服务分配成功')
}

// 删除预约
const deleteAppointment = (id: number) => {
  if (confirm('确定要删除这个预约记录吗？')) {
    console.log('删除预约:', id)
    // 模拟删除操作
    const index = appointments.value.findIndex(item => item.id === id)
    if (index > -1) {
      appointments.value.splice(index, 1)
      totalItems.value--
    }
    alert('删除成功')
  }
}

// 家政服务相关状态和方法
const serviceSearchKeyword = ref('')
const serviceSelectAll = ref(false)
const serviceSelectedItems = ref<Record<number, boolean>>({})
const serviceCurrentPage = ref(1)
const servicePageSize = ref(10)
const serviceTotalItems = ref(15)
const serviceTotalPages = computed(() => Math.ceil(serviceTotalItems.value / servicePageSize.value))

// 服务编辑对话框状态
const showServiceDialog = ref(false)
const isEditingService = ref(false)
const editingServiceId = ref<number | null>(null)
const imageInput = ref<HTMLInputElement | null>(null)
const serviceForm = ref({
  name: '',
  price: 0,
  unit: '',
  category: '',
  imageUrl: '',
  description: ''
})

// 模拟家政服务数据
const services = ref([
  {
    id: 1,
    name: '沙发保养清洗【包含皮革养护】',
    price: 50,
    unit: '次',
    sales: 2,
    popularity: 16,
    category: '皮沙发保养',
    imageUrl: 'https://picsum.photos/seed/service1/100/100'
  },
  {
    id: 2,
    name: '地板打檫10平米',
    price: 66,
    unit: '10平米',
    sales: 4,
    popularity: 11,
    category: '地板打檫',
    imageUrl: 'https://picsum.photos/seed/service2/100/100'
  },
  {
    id: 3,
    name: '全屋清洁【大扫除】',
    price: 399,
    unit: '次',
    sales: 0,
    popularity: 1,
    category: '家底长期保洁',
    imageUrl: 'https://picsum.photos/seed/service3/100/100'
  },
  {
    id: 4,
    name: '卫生间保养清洁【深度清洁】',
    price: 88,
    unit: '次',
    sales: 0,
    popularity: 0,
    category: '卫生间保养',
    imageUrl: 'https://picsum.photos/seed/service4/100/100'
  },
  {
    id: 5,
    name: '厨房保养清洁【深度清洁】',
    price: 338,
    unit: '次',
    sales: 0,
    popularity: 9,
    category: '厨房保养',
    imageUrl: 'https://picsum.photos/seed/service5/100/100'
  }
])

// 过滤后的服务数据
const filteredServices = computed(() => {
  let filtered = services.value
  
  // 搜索过滤
  if (serviceSearchKeyword.value) {
    filtered = filtered.filter(item => 
      item.name.toLowerCase().includes(serviceSearchKeyword.value.toLowerCase())
    )
  }
  
  // 分页
  const startIndex = (serviceCurrentPage.value - 1) * servicePageSize.value
  const endIndex = startIndex + servicePageSize.value
  return filtered.slice(startIndex, endIndex)
})

// 计算要显示的页码
const servicePagesToShow = computed(() => {
  const pages = []
  const total = serviceTotalPages.value
  const current = serviceCurrentPage.value
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
    } else if (current >= total - 2) {
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      for (let i = current - 2; i <= current + 2; i++) {
        pages.push(i)
      }
    }
  }
  
  return pages
})

// 切换全选
const toggleServiceSelectAll = () => {
  filteredServices.value.forEach((item, index) => {
    serviceSelectedItems.value[index] = serviceSelectAll.value
  })
}

// 重置搜索
const resetServiceSearch = () => {
  serviceSearchKeyword.value = ''
  serviceCurrentPage.value = 1
}

// 新建服务
const createNewService = () => {
  isEditingService.value = false
  editingServiceId.value = null
  serviceForm.value = {
    name: '',
    price: 0,
    unit: '',
    category: '',
    imageUrl: '',
    description: ''
  }
  showServiceDialog.value = true
}

// 批量删除
const batchDeleteService = () => {
  const selectedIds = Object.entries(serviceSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => filteredServices.value[index]?.id)
    .filter(Boolean)
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的记录')
    return
  }
  
  if (confirm(`确定要删除选中的 ${selectedIds.length} 条记录吗？`)) {
    // 从服务列表中删除选中的项目
    services.value = services.value.filter(service => !selectedIds.includes(service.id))
    serviceTotalItems.value -= selectedIds.length
    
    // 清空选择
    serviceSelectAll.value = false
    serviceSelectedItems.value = {}
    
    // 如果当前页没有数据了，回到上一页
    if (filteredServices.value.length === 0 && serviceCurrentPage.value > 1) {
      serviceCurrentPage.value--
    }
    
    alert('删除成功')
  }
}

// 删除单条记录
const deleteService = (id: number) => {
  if (confirm('确定要删除这条记录吗？')) {
    // 从服务列表中删除指定项目
    const index = services.value.findIndex(service => service.id === id)
    if (index > -1) {
      services.value.splice(index, 1)
      serviceTotalItems.value--
      
      // 如果当前页没有数据了，回到上一页
      if (filteredServices.value.length === 0 && serviceCurrentPage.value > 1) {
        serviceCurrentPage.value--
      }
      
      alert('删除成功')
    }
  }
}

// 编辑服务
const editService = (id: number) => {
  const service = services.value.find(s => s.id === id)
  if (service) {
    isEditingService.value = true
    editingServiceId.value = id
    serviceForm.value = {
      name: service.name,
      price: service.price,
      unit: service.unit,
      category: service.category,
      imageUrl: service.imageUrl,
      description: service.description || ''
    }
    showServiceDialog.value = true
  }
}

// 保存服务
const saveService = () => {
  if (!serviceForm.value.name || !serviceForm.value.price || !serviceForm.value.unit || !serviceForm.value.category) {
    alert('请填写所有必填字段')
    return
  }
  
  if (isEditingService.value && editingServiceId.value) {
    // 编辑现有服务
    const index = services.value.findIndex(s => s.id === editingServiceId.value)
    if (index > -1) {
      services.value[index] = {
        ...services.value[index],
        name: serviceForm.value.name,
        price: serviceForm.value.price,
        unit: serviceForm.value.unit,
        category: serviceForm.value.category,
        imageUrl: serviceForm.value.imageUrl || 'https://picsum.photos/seed/service' + editingServiceId.value + '/100/100',
        description: serviceForm.value.description
      }
      alert('服务更新成功')
    }
  } else {
    // 创建新服务
    const newId = Math.max(...services.value.map(s => s.id)) + 1
    const newService = {
      id: newId,
      name: serviceForm.value.name,
      price: serviceForm.value.price,
      unit: serviceForm.value.unit,
      category: serviceForm.value.category,
      imageUrl: serviceForm.value.imageUrl || 'https://picsum.photos/seed/service' + newId + '/100/100',
      description: serviceForm.value.description,
      sales: 0,
      popularity: 0
    }
    services.value.push(newService)
    serviceTotalItems.value++
    alert('服务创建成功')
  }
  
  cancelServiceDialog()
}

// 取消服务对话框
const cancelServiceDialog = () => {
  showServiceDialog.value = false
  isEditingService.value = false
  editingServiceId.value = null
  serviceForm.value = {
    name: '',
    price: 0,
    unit: '',
    category: '',
    imageUrl: '',
    description: ''
  }
  // 清空文件输入
  if (imageInput.value) {
    imageInput.value.value = ''
  }
}

// 处理图片错误
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = 'https://via.placeholder.com/100x100?text=图片加载失败'
}

// 查看服务详情
const viewServiceDetail = (id: number) => {
  const service = services.value.find(s => s.id === id)
  if (service) {
    const detail = `
服务名称: ${service.name}
价格: ¥${service.price}
单位: ${service.unit}
分类: ${service.category}
销量: ${service.sales}
人气: ${service.popularity}
${service.description ? `描述: ${service.description}` : ''}
    `.trim()
    alert(detail)
  }
}

// 触发图片上传
const triggerImageUpload = () => {
  imageInput.value?.click()
}

// 处理图片上传
const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (file) {
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
      alert('请选择图片文件')
      return
    }
    
    // 检查文件大小 (限制为5MB)
    if (file.size > 5 * 1024 * 1024) {
      alert('图片大小不能超过5MB')
      return
    }
    
    // 创建FileReader来读取文件
    const reader = new FileReader()
    reader.onload = (e) => {
      const result = e.target?.result as string
      serviceForm.value.imageUrl = result
    }
    reader.onerror = () => {
      alert('图片读取失败，请重试')
    }
    reader.readAsDataURL(file)
  }
}

// 删除图片
const removeImage = () => {
  serviceForm.value.imageUrl = ''
  if (imageInput.value) {
    imageInput.value.value = ''
  }
}

// 提现记录相关状态和方法
const withdrawalDate = ref('')
const selectAllWithdrawals = ref(false)
const withdrawalSelectedItems = ref<Record<number, boolean>>({})
const withdrawalCurrentPage = ref(1)
const withdrawalPageSize = ref(10)
const withdrawalTotalItems = ref(4)
const withdrawalTotalPages = computed(() => Math.ceil(withdrawalTotalItems.value / withdrawalPageSize.value))
const selectedWithdrawals = computed(() => {
  return Object.entries(withdrawalSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => filteredWithdrawals.value[parseInt(index)]?.id)
    .filter(Boolean)
})

// 模拟提现记录数据
const allWithdrawals = ref([
  {
    id: 1,
    amount: 100,
    accountType: 'wechat',
    accountNumber: '13989997788',
    providerName: '家政小丁',
    withdrawalTime: '2025-05-23 09:56:26'
  },
  {
    id: 2,
    amount: 1,
    accountType: 'alipay',
    accountNumber: '13990998777',
    providerName: '家政小月',
    withdrawalTime: '2025-05-16 09:04:47'
  },
  {
    id: 3,
    amount: 100,
    accountType: 'alipay',
    accountNumber: '13877956666',
    providerName: '家政小黄',
    withdrawalTime: '2025-05-13 13:31:07'
  },
  {
    id: 4,
    amount: 100,
    accountType: 'wechat',
    accountNumber: '13988776688',
    providerName: '家政小黄',
    withdrawalTime: '2025-05-12 18:28:58'
  }
])

// 过滤后的提现记录数据
const filteredWithdrawals = computed(() => {
  let filtered = allWithdrawals.value
  
  // 搜索过滤
  if (withdrawalDate.value) {
    const searchDate = new Date(withdrawalDate.value)
    filtered = filtered.filter(record => {
      const recordDate = new Date(record.withdrawalTime.split(' ')[0])
      return recordDate.getTime() === searchDate.getTime()
    })
  }
  
  // 分页
  const startIndex = (withdrawalCurrentPage.value - 1) * withdrawalPageSize.value
  const endIndex = startIndex + withdrawalPageSize.value
  return filtered.slice(startIndex, endIndex)
})

// 计算要显示的页码
const withdrawalPagesToShow = computed(() => {
  const pages = []
  const total = withdrawalTotalPages.value
  const current = withdrawalCurrentPage.value
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
    } else if (current >= total - 2) {
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      for (let i = current - 2; i <= current + 2; i++) {
        pages.push(i)
      }
    }
  }
  
  return pages
})

// 切换全选
const toggleSelectAllWithdrawals = () => {
  filteredWithdrawals.value.forEach((_, index) => {
    withdrawalSelectedItems.value[index] = selectAllWithdrawals.value
  })
}

// 重置搜索
const resetWithdrawalSearch = () => {
  withdrawalDate.value = ''
  withdrawalCurrentPage.value = 1
}

// 搜索提现记录
const searchWithdrawals = () => {
  withdrawalCurrentPage.value = 1
}

// 批量删除
const batchDeleteWithdrawals = () => {
  const selectedIds = selectedWithdrawals.value
  
  if (selectedIds.length === 0) {
    alert('请先选择要删除的提现记录')
    return
  }
  
  // 获取要删除的记录详情用于确认
  const selectedRecords = allWithdrawals.value.filter(record => selectedIds.includes(record.id))
  const totalAmount = selectedRecords.reduce((sum, record) => sum + record.amount, 0)
  
  const confirmMessage = `确定要删除选中的 ${selectedIds.length} 条提现记录吗？\n\n删除详情：\n- 记录数量：${selectedIds.length} 条\n- 总金额：¥${totalAmount}\n- 涉及服务者：${[...new Set(selectedRecords.map(r => r.providerName))].join('、')}\n\n此操作不可撤销！`
  
  if (confirm(confirmMessage)) {
    try {
      // 从提现记录列表中删除选中的项目
      allWithdrawals.value = allWithdrawals.value.filter(record => !selectedIds.includes(record.id))
      withdrawalTotalItems.value -= selectedIds.length
      
      // 清空选择
      selectAllWithdrawals.value = false
      withdrawalSelectedItems.value = {}
      
      // 如果当前页没有数据了，回到上一页
      if (filteredWithdrawals.value.length === 0 && withdrawalCurrentPage.value > 1) {
        withdrawalCurrentPage.value--
      }
      
      // 显示删除成功信息
      alert(`批量删除成功！\n已删除 ${selectedIds.length} 条提现记录\n总金额：¥${totalAmount}`)
      
      // 记录删除操作日志
      console.log('批量删除提现记录:', {
        deletedIds: selectedIds,
        deletedRecords: selectedRecords,
        totalAmount: totalAmount,
        timestamp: new Date().toISOString()
      })
      
    } catch (error) {
      console.error('批量删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

// 删除单条记录
const deleteWithdrawal = (id: number) => {
  const record = allWithdrawals.value.find(r => r.id === id)
  if (!record) {
    alert('记录不存在')
    return
  }
  
  const confirmMessage = `确定要删除这条提现记录吗？\n\n删除详情：\n- 服务者：${record.providerName}\n- 金额：¥${record.amount}\n- 账户：${record.accountNumber}\n- 时间：${record.withdrawalTime}\n\n此操作不可撤销！`
  
  if (confirm(confirmMessage)) {
    try {
      // 从提现记录列表中删除指定项目
      const index = allWithdrawals.value.findIndex(record => record.id === id)
      if (index > -1) {
        const deletedRecord = allWithdrawals.value[index]
        allWithdrawals.value.splice(index, 1)
        withdrawalTotalItems.value--
        
        // 如果当前页没有数据了，回到上一页
        if (filteredWithdrawals.value.length === 0 && withdrawalCurrentPage.value > 1) {
          withdrawalCurrentPage.value--
        }
        
        // 显示删除成功信息
        alert(`删除成功！\n已删除 ${deletedRecord.providerName} 的提现记录\n金额：¥${deletedRecord.amount}`)
        
        // 记录删除操作日志
        console.log('删除提现记录:', {
          deletedId: id,
          deletedRecord: deletedRecord,
          timestamp: new Date().toISOString()
        })
      }
    } catch (error) {
      console.error('删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

// 切换页面
const changeWithdrawalPage = (page: number) => {
  if (page >= 1 && page <= withdrawalTotalPages.value) {
    withdrawalCurrentPage.value = page
    // 重置选择
    selectAllWithdrawals.value = false
    withdrawalSelectedItems.value = {}
  }
}

// 服务分类相关状态和方法
const categorySearchKeyword = ref('')
const categorySelectAll = ref(false)
const categorySelectedItems = ref<Record<number, boolean>>({})
const categoryCurrentPage = ref(1)
const categoryPageSize = ref(10)
const showAddCategoryDialog = ref(false)
const categoryTotalItems = ref(9)
const categoryTotalPages = computed(() => Math.ceil(categoryTotalItems.value / categoryPageSize.value))
const selectedCategories = computed(() => {
  return Object.entries(categorySelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => filteredCategories.value[parseInt(index)]?.id)
    .filter(Boolean)
})

// 服务分类编辑对话框状态
const showCategoryDialog = ref(false)
const isEditingCategory = ref(false)
const editingCategoryId = ref<number | null>(null)
const categoryForm = ref({
  name: '',
  description: '',
  icon: ''
})

// 可用的图标列表
const availableIcons = ref([
  '🧹', '🪟', '🧼', '🏠', '🍳', '🚿', '🛋️', '🧹', '📺', '🧽', '🚰', '💡', 
  '🔧', '⚡', '🌿', '🎨', '🔍', '📱', '💻', '🎯', '⭐', '❤️', '🔥', '💎'
])

// 模拟服务分类数据
const allCategories = ref([
  { id: 1, name: '日常保洁', description: '专业工具，掸灰扫拖', icon: '🧹' },
  { id: 2, name: '擦玻璃', description: '专业药剂，洁净玻璃', icon: '🪟' },
  { id: 3, name: '深度保洁', description: '明星服务，洁净超乎想象', icon: '🧼' },
  { id: 4, name: '新居开荒', description: '新房入住开荒，清扫装修痕迹', icon: '🏠' },
  { id: 5, name: '厨房保洁', description: '厨房保养，感受清爽空间', icon: '🍳' },
  { id: 6, name: '卫生间保洁', description: '专业消毒，清新舒爽', icon: '🚿' },
  { id: 7, name: '家具保养', description: '专业护理，延长使用寿命', icon: '🛋️' },
  { id: 8, name: '地面清洁', description: '多种材质，专业处理', icon: '🧹' },
  { id: 9, name: '家电清洗', description: '彻底清洁，健康使用', icon: '📺' }
])

// 过滤后的服务分类数据
const filteredCategories = computed(() => {
  let filtered = allCategories.value
  
  // 搜索过滤
  if (categorySearchKeyword.value) {
    const keyword = categorySearchKeyword.value.toLowerCase()
    filtered = filtered.filter(category => 
      category.name.toLowerCase().includes(keyword) || 
      category.description.toLowerCase().includes(keyword)
    )
  }
  
  // 分页
  const startIndex = (categoryCurrentPage.value - 1) * categoryPageSize.value
  const endIndex = startIndex + categoryPageSize.value
  return filtered.slice(startIndex, endIndex)
})

// 计算要显示的页码
const categoryPagesToShow = computed(() => {
  const pages = []
  const total = categoryTotalPages.value
  const current = categoryCurrentPage.value
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
    } else if (current >= total - 2) {
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      for (let i = current - 2; i <= current + 2; i++) {
        pages.push(i)
      }
    }
  }
  
  return pages
})

// 切换全选
const toggleCategorySelectAll = () => {
  filteredCategories.value.forEach((_, index) => {
    categorySelectedItems.value[index] = categorySelectAll.value
  })
}

// 重置搜索
const resetCategorySearch = () => {
  categorySearchKeyword.value = ''
  categoryCurrentPage.value = 1
}

// 搜索分类
const searchCategories = () => {
  categoryCurrentPage.value = 1
}

// 批量删除
const batchDeleteCategories = () => {
  const selectedIds = Object.entries(categorySelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => allCategories.value[index]?.id)
    .filter(Boolean)
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的分类')
    return
  }
  
  if (confirm(`确定要删除选中的 ${selectedIds.length} 条分类吗？`)) {
    // 模拟删除操作
    console.log('批量删除分类:', selectedIds)
    // 这里应该调用API删除数据
    alert('删除成功')
    // 清空选择
    categorySelectAll.value = false
    categorySelectedItems.value = {}
  }
}

// 新建分类
const createNewCategory = () => {
  // 设置为新建模式
  isEditingCategory.value = false
  editingCategoryId.value = null
  
  // 重置表单数据
  categoryForm.value = {
    name: '',
    description: '',
    icon: ''
  }
  
  // 显示对话框
  showCategoryDialog.value = true
}

// 编辑分类
const editCategory = (category: any) => {
  isEditingCategory.value = true
  editingCategoryId.value = category.id
  categoryForm.value = {
    name: category.name,
    description: category.description,
    icon: category.icon
  }
  showCategoryDialog.value = true
}

// 选择图标
const selectIcon = (icon: string) => {
  categoryForm.value.icon = icon
}

// 保存分类
const saveCategory = () => {
  // 表单验证
  if (!categoryForm.value.name.trim()) {
    alert('请输入分类名称')
    return
  }
  
  if (!categoryForm.value.description.trim()) {
    alert('请输入分类描述')
    return
  }
  
  // 检查分类名称是否重复
  const existingCategory = allCategories.value.find(c => 
    c.name.toLowerCase() === categoryForm.value.name.toLowerCase() && 
    c.id !== editingCategoryId.value
  )
  
  if (existingCategory) {
    alert('分类名称已存在，请使用其他名称')
    return
  }
  
  try {
    if (isEditingCategory.value && editingCategoryId.value) {
      // 编辑现有分类
      const index = allCategories.value.findIndex(c => c.id === editingCategoryId.value)
      if (index > -1) {
        allCategories.value[index] = {
          ...allCategories.value[index],
          name: categoryForm.value.name.trim(),
          description: categoryForm.value.description.trim(),
          icon: categoryForm.value.icon || '📁'
        }
        alert('分类更新成功！')
      }
    } else {
      // 创建新分类
      const newId = allCategories.value.length > 0 ? Math.max(...allCategories.value.map(c => c.id)) + 1 : 1
      const newCategory = {
        id: newId,
        name: categoryForm.value.name.trim(),
        description: categoryForm.value.description.trim(),
        icon: categoryForm.value.icon || '📁'
      }
      allCategories.value.push(newCategory)
      categoryTotalItems.value++
      alert('分类创建成功！')
    }
    
    // 关闭对话框
    cancelCategoryDialog()
    
  } catch (error) {
    console.error('保存分类失败:', error)
    alert('保存失败，请重试')
  }
}

// 取消分类对话框
const cancelCategoryDialog = () => {
  showCategoryDialog.value = false
  isEditingCategory.value = false
  editingCategoryId.value = null
  categoryForm.value = {
    name: '',
    description: '',
    icon: ''
  }
}

// 删除单条分类
const deleteCategory = (id: number) => {
  if (confirm('确定要删除这个分类吗？')) {
    // 模拟删除操作
    console.log('删除分类:', id)
    // 这里应该调用API删除数据
    alert('删除成功')
  }
}

// 切换页面
const changeCategoryPage = (page: number) => {
  if (page >= 1 && page <= categoryTotalPages.value) {
    categoryCurrentPage.value = page
    // 重置选择
    categorySelectAll.value = false
    categorySelectedItems.value = {}
  }
}

// 系统公告相关状态和方法
const noticeSearchKeyword = ref('')
const noticeSelectAll = ref(false)
const noticeSelectedItems = ref<Record<number, boolean>>({})
const noticeCurrentPage = ref(1)
const noticePageSize = ref(10)
const showAddNoticeDialog = ref(false)
const noticeTotalItems = ref(3)

// 新增系统公告表单
const newNoticeForm = ref({
  title: '',
  content: ''
})

// 新增表单验证错误
const newNoticeFormErrors = ref({
  title: '',
  content: ''
})

// 编辑系统公告相关变量
const showEditNoticeDialog = ref(false)
const editingNoticeId = ref<string | null>(null)

// 编辑系统公告表单
const editNoticeForm = ref({
  title: '',
  content: ''
})

// 编辑表单验证错误
const editNoticeFormErrors = ref({
  title: '',
  content: ''
})
const noticeTotalPages = computed(() => Math.ceil(noticeTotalItems.value / noticePageSize.value))
const selectedNotices = computed(() => {
  return Object.entries(noticeSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => parseInt(index))
})

// 模拟系统公告数据
const allNotices = ref([
  {
    id: 1,
    title: '深度保洁特惠',
    content: '即日起，本家政推出深度保洁特惠！专业团队环绕环保清洁剂，彻底清除顽固污渍，让您的家焕然一新！',
    publishTime: '2025-05-11 15:51:17'
  },
  {
    id: 2,
    title: '家电清洗福利',
    content: '家电久未清洗易藏污垢！现开展家电清洗福利，空调、油烟机、洗衣机等多种家电一站式清洁，价格优惠！',
    publishTime: '2025-05-11 15:51:17'
  },
  {
    id: 3,
    title: '新房开荒保洁特惠',
    content: '新房开荒不用愁！专业团队采用环保药剂，高效去除装修残留物，让您轻松入住新家！',
    publishTime: '2025-05-11 15:51:17'
  }
])

// 过滤后的系统公告数据
const filteredNotices = computed(() => {
  let filtered = allNotices.value
  
  // 搜索过滤
  if (noticeSearchKeyword.value) {
    const keyword = noticeSearchKeyword.value.toLowerCase()
    filtered = filtered.filter(notice => 
      notice.title.toLowerCase().includes(keyword)
    )
  }
  
  // 分页
  const startIndex = (noticeCurrentPage.value - 1) * noticePageSize.value
  const endIndex = startIndex + noticePageSize.value
  return filtered.slice(startIndex, endIndex)
})

// 计算要显示的页码
const noticePagesToShow = computed(() => {
  const pages = []
  const total = noticeTotalPages.value
  const current = noticeCurrentPage.value
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
    } else if (current >= total - 2) {
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      for (let i = current - 2; i <= current + 2; i++) {
        pages.push(i)
      }
    }
  }
  
  return pages
})

// 切换全选
const toggleNoticeSelectAll = () => {
  filteredNotices.value.forEach((_, index) => {
    noticeSelectedItems.value[index] = noticeSelectAll.value
  })
}

// 重置搜索
const resetNoticeSearch = () => {
  noticeSearchKeyword.value = ''
  noticeCurrentPage.value = 1
}

// 搜索公告
const searchNotices = () => {
  noticeCurrentPage.value = 1
}

// 批量删除
const batchDeleteNotices = () => {
  const selectedIds = Object.entries(noticeSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => allNotices.value[index]?.id)
    .filter(Boolean)
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的公告')
    return
  }
  
  if (confirm(`确定要删除选中的 ${selectedIds.length} 条公告吗？`)) {
    // 模拟删除操作
    console.log('批量删除公告:', selectedIds)
    // 这里应该调用API删除数据
    alert('删除成功')
    // 清空选择
    noticeSelectAll.value = false
    noticeSelectedItems.value = {}
  }
}

// 编辑公告
const editNotice = (notice: any) => {
  // 填充编辑表单
  editNoticeForm.value = {
    title: notice.title,
    content: notice.content
  }
  
  // 设置正在编辑的公告ID
  editingNoticeId.value = notice.id
  
  // 打开编辑对话框
  showEditNoticeDialog.value = true
  
  console.log('编辑公告:', notice)
}

// 删除单条公告
const deleteNotice = (id: number) => {
  if (confirm('确定要删除这个公告吗？')) {
    // 模拟删除操作
    console.log('删除公告:', id)
    // 这里应该调用API删除数据
    alert('删除成功')
  }
}

// 切换页面
const changeNoticePage = (page: number) => {
  if (page >= 1 && page <= noticeTotalPages.value) {
    noticeCurrentPage.value = page
    // 重置选择
    noticeSelectAll.value = false
    noticeSelectedItems.value = {}
  }
}

// 新增系统公告相关函数
const validateNewNoticeForm = () => {
  newNoticeFormErrors.value = {
    title: '',
    content: ''
  }
  
  let isValid = true
  
  if (!newNoticeForm.value.title.trim()) {
    newNoticeFormErrors.value.title = '请输入公告标题'
    isValid = false
  }
  
  if (!newNoticeForm.value.content.trim()) {
    newNoticeFormErrors.value.content = '请输入公告内容'
    isValid = false
  }
  
  return isValid
}

const saveNewNotice = () => {
  if (!validateNewNoticeForm()) {
    return
  }
  
  try {
    // 生成新的ID
    const newId = (allNotices.value.length + 1).toString()
    
    // 创建新的系统公告
    const newNotice = {
      id: newId,
      title: newNoticeForm.value.title.trim(),
      content: newNoticeForm.value.content.trim(),
      publishTime: new Date().toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      }).replace(/\//g, '-')
    }
    
    // 添加到公告列表
    allNotices.value.unshift(newNotice)
    noticeTotalItems.value++
    
    // 关闭对话框
    showAddNoticeDialog.value = false
    
    // 重置表单
    resetNewNoticeForm()
    
    // 显示成功消息
    alert('新增系统公告成功！')
    
    console.log('新增系统公告:', newNotice)
    
  } catch (error) {
    console.error('新增失败:', error)
    alert('新增失败，请重试')
  }
}

const cancelAddNotice = () => {
  showAddNoticeDialog.value = false
  resetNewNoticeForm()
}

const resetNewNoticeForm = () => {
  newNoticeForm.value = {
    title: '',
    content: ''
  }
  newNoticeFormErrors.value = {
    title: '',
    content: ''
  }
}

// 编辑系统公告相关函数
const validateEditNoticeForm = () => {
  editNoticeFormErrors.value = {
    title: '',
    content: ''
  }
  
  let isValid = true
  
  if (!editNoticeForm.value.title.trim()) {
    editNoticeFormErrors.value.title = '请输入公告标题'
    isValid = false
  }
  
  if (!editNoticeForm.value.content.trim()) {
    editNoticeFormErrors.value.content = '请输入公告内容'
    isValid = false
  }
  
  return isValid
}

const saveEditNotice = () => {
  if (!validateEditNoticeForm()) {
    return
  }
  
  try {
    // 找到要编辑的公告
    const noticeIndex = allNotices.value.findIndex(notice => notice.id === editingNoticeId.value)
    
    if (noticeIndex > -1) {
      // 更新公告信息
      allNotices.value[noticeIndex] = {
        ...allNotices.value[noticeIndex],
        title: editNoticeForm.value.title.trim(),
        content: editNoticeForm.value.content.trim()
      }
      
      // 关闭对话框
      showEditNoticeDialog.value = false
      
      // 重置表单
      resetEditNoticeForm()
      
      // 显示成功消息
      alert('编辑系统公告成功！')
      
      console.log('编辑系统公告成功:', allNotices.value[noticeIndex])
    }
    
  } catch (error) {
    console.error('编辑失败:', error)
    alert('编辑失败，请重试')
  }
}

const cancelEditNotice = () => {
  showEditNoticeDialog.value = false
  resetEditNoticeForm()
}

const resetEditNoticeForm = () => {
  editNoticeForm.value = {
    title: '',
    content: ''
  }
  editNoticeFormErrors.value = {
    title: '',
    content: ''
  }
  editingNoticeId.value = null
}

// 服务评价相关状态和方法
const evaluationSearchKeyword = ref('')
const evaluationSelectAll = ref(false)
const evaluationSelectedItems = ref<Record<number, boolean>>({})
const evaluationCurrentPage = ref(1)
const evaluationPageSize = ref(10)
const evaluationTotalItems = ref(5)
const evaluationTotalPages = computed(() => Math.ceil(evaluationTotalItems.value / evaluationPageSize.value))
const selectedEvaluations = computed(() => {
  return Object.entries(evaluationSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => filteredEvaluations.value[parseInt(index)]?.id)
    .filter(Boolean)
})

// 模拟服务评价数据
const allEvaluations = ref([
  {
    id: 1,
    providerName: '家政小丁',
    serviceName: '新装修开荒平米1千【包边边角角】',
    userName: '小强',
    rating: 5,
    content: '非常好',
    evaluationTime: '2025-05-17 22:53:47'
  },
  {
    id: 2,
    providerName: '家政小敏',
    serviceName: '地板打蜡10平米',
    userName: '小张',
    rating: 4.5,
    content: '我很满意！',
    evaluationTime: '2025-05-17 22:29:12'
  },
  {
    id: 3,
    providerName: '家政小月',
    serviceName: '沙发深度清洁【包含皮革养护】',
    userName: '小张',
    rating: 4,
    content: '很好！',
    evaluationTime: '2025-05-17 22:29:12'
  },
  {
    id: 4,
    providerName: '家政小黄',
    serviceName: '深度保洁小时全【全屋大扫除】',
    userName: '小张',
    rating: 4.5,
    content: '非常好！',
    evaluationTime: '2025-05-13 09:09:00'
  },
  {
    id: 5,
    providerName: '家政小丁',
    serviceName: '4小时全屋日常保洁【中户型推荐】快速上门',
    userName: '小张',
    rating: 3.5,
    content: '非常好',
    evaluationTime: '2025-05-09 16:45:17'
  }
])

// 过滤后的服务评价数据
const filteredEvaluations = computed(() => {
  let filtered = allEvaluations.value
  
  // 搜索过滤
  if (evaluationSearchKeyword.value) {
    const keyword = evaluationSearchKeyword.value.toLowerCase()
    filtered = filtered.filter(evaluation => 
      evaluation.providerName.toLowerCase().includes(keyword) ||
      evaluation.serviceName.toLowerCase().includes(keyword) ||
      evaluation.userName.toLowerCase().includes(keyword)
    )
  }
  
  // 分页
  const startIndex = (evaluationCurrentPage.value - 1) * evaluationPageSize.value
  const endIndex = startIndex + evaluationPageSize.value
  return filtered.slice(startIndex, endIndex)
})

// 计算要显示的页码
const evaluationPagesToShow = computed(() => {
  const pages = []
  const total = evaluationTotalPages.value
  const current = evaluationCurrentPage.value
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
    } else if (current >= total - 2) {
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      for (let i = current - 2; i <= current + 2; i++) {
        pages.push(i)
      }
    }
  }
  
  return pages
})

// 切换全选
const toggleEvaluationSelectAll = () => {
  filteredEvaluations.value.forEach((_, index) => {
    evaluationSelectedItems.value[index] = evaluationSelectAll.value
  })
}

// 重置搜索
const resetEvaluationSearch = () => {
  evaluationSearchKeyword.value = ''
  evaluationCurrentPage.value = 1
}

// 搜索评价
const searchEvaluations = () => {
  evaluationCurrentPage.value = 1
}

// 批量删除
const batchDeleteEvaluations = () => {
  const selectedIds = Object.entries(evaluationSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => filteredEvaluations.value[parseInt(index)]?.id)
    .filter(Boolean)
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的评价')
    return
  }
  
  // 获取要删除的记录详情用于确认
  const selectedRecords = allEvaluations.value.filter(record => selectedIds.includes(record.id))
  
  const confirmMessage = `确定要删除选中的 ${selectedIds.length} 条评价吗？\n\n删除详情：\n- 记录数量：${selectedIds.length} 条\n- 涉及用户：${[...new Set(selectedRecords.map(r => r.userName))].join('、')}\n\n此操作不可撤销！`
  
  if (confirm(confirmMessage)) {
    try {
      // 从评价列表中删除选中的项目
      allEvaluations.value = allEvaluations.value.filter(record => !selectedIds.includes(record.id))
      evaluationTotalItems.value -= selectedIds.length
      
      // 清空选择
      evaluationSelectAll.value = false
      evaluationSelectedItems.value = {}
      
      // 如果当前页没有数据了，回到上一页
      if (filteredEvaluations.value.length === 0 && evaluationCurrentPage.value > 1) {
        evaluationCurrentPage.value--
      }
      
      // 显示删除成功信息
      alert(`批量删除成功！\n已删除 ${selectedIds.length} 条评价`)
      
      // 记录删除操作日志
      console.log('批量删除评价:', {
        deletedIds: selectedIds,
        deletedRecords: selectedRecords,
        timestamp: new Date().toISOString()
      })
      
    } catch (error) {
      console.error('批量删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

// 删除单条评价
const deleteEvaluation = (id: number) => {
  if (confirm('确定要删除这个评价吗？')) {
    // 模拟删除操作
    console.log('删除评价:', id)
    // 这里应该调用API删除数据
    alert('删除成功')
  }
}

// 切换页面
const changeEvaluationPage = (page: number) => {
  if (page >= 1 && page <= evaluationTotalPages.value) {
    evaluationCurrentPage.value = page
    // 重置选择
    evaluationSelectAll.value = false
    evaluationSelectedItems.value = {}
  }
}


// 服务者认证相关状态和方法
// 使用家政服务者管理中声明的变量
// providerPageSize 已在上面声明，这里不再重复声明
// 使用家政服务者管理中声明的providerTotalItems和providerTotalPages变量

// 模拟服务者认证数据
const certProviders = ref([
  {
    id: 1,
    name: '家政小敏',
    certificateUrl: 'https://picsum.photos/seed/cert1/100/100',
    idCardUrl: 'https://picsum.photos/seed/id1/100/100',
    idCardNumber: '34101019990906-4566',
    contact: '13889877788',
    address: '合肥市枫叶社区...',
    status: '审核中',
    reviewComment: ''
  },
  {
    id: 2,
    name: '家政小黄',
    certificateUrl: 'https://picsum.photos/seed/cert2/100/100',
    idCardUrl: 'https://picsum.photos/seed/id2/100/100',
    idCardNumber: '34101019910908-6576',
    contact: '13888993377',
    address: '合肥市光大大道...',
    status: '通过',
    reviewComment: ''
  },
  {
    id: 3,
    name: '家政小月',
    certificateUrl: 'https://picsum.photos/seed/cert3/100/100',
    idCardUrl: 'https://picsum.photos/seed/id3/100/100',
    idCardNumber: '34101019931008-4564',
    contact: '13889979966',
    address: '合肥市光大大道...',
    status: '通过',
    reviewComment: ''
  },
  {
    id: 4,
    name: '家政小丁',
    certificateUrl: 'https://picsum.photos/seed/cert4/100/100',
    idCardUrl: 'https://picsum.photos/seed/id4/100/100',
    idCardNumber: '34101019951008-5674',
    contact: '13888997788',
    address: '合肥市光大大道...',
    status: '通过',
    reviewComment: ''
  }
])

// 过滤后的服务者认证数据
const filteredCertProviders = computed(() => {
  let filtered = certProviders.value
  
  // 搜索过滤
  if (certProviderSearchKeyword.value) {
    filtered = filtered.filter(item => 
      item.name.toLowerCase().includes(certProviderSearchKeyword.value.toLowerCase())
    )
  }
  
  // 分页
  const startIndex = (certProviderCurrentPage.value - 1) * providerPageSize.value
  const endIndex = startIndex + providerPageSize.value
  return filtered.slice(startIndex, endIndex)
})

// 计算要显示的页码
const certProviderPagesToShow = computed(() => {
  const pages = []
  const total = providerTotalPages.value
  const current = certProviderCurrentPage.value
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
    } else if (current >= total - 2) {
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      for (let i = current - 2; i <= current + 2; i++) {
        pages.push(i)
      }
    }
  }
  
  return pages
})

// 重置搜索
const resetProviderSearch = () => {
  certProviderSearchKeyword.value = ''
  certProviderCurrentPage.value = 1
}

// 批量删除
const batchDeleteProvider = () => {
  const selectedIds = Object.entries(certProviderSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => certProviders.value[parseInt(index)]?.id)
    .filter(Boolean)
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的认证记录')
    return
  }
  
  // 获取要删除的认证记录详情用于确认
  const selectedRecords = certProviders.value.filter(record => selectedIds.includes(record.id))
  
  const confirmMessage = `确定要删除选中的 ${selectedIds.length} 条认证记录吗？\n\n删除详情：\n- 记录数量：${selectedIds.length} 条\n- 涉及服务者：${selectedRecords.map(r => r.name).join('、')}\n\n此操作不可撤销！`
  
  if (confirm(confirmMessage)) {
    try {
      // 从认证列表中删除选中的记录
      certProviders.value = certProviders.value.filter(record => !selectedIds.includes(record.id))
      
      // 清空选择
      certProviderSelectAll.value = false
      certProviderSelectedItems.value = {}
      
      // 显示删除成功信息
      alert(`批量删除成功！\n已删除 ${selectedIds.length} 条认证记录`)
      
      // 记录删除操作日志
      console.log('批量删除认证记录:', {
        deletedIds: selectedIds,
        deletedRecords: selectedRecords,
        timestamp: new Date().toISOString()
      })
      
    } catch (error) {
      console.error('批量删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

// 审核服务者
const reviewProvider = (id: number) => {
  // 模拟审核操作
  console.log('审核服务者:', id)
  alert('审核服务者功能开发中')
}

// 删除服务者
const deleteCertProvider = (id: number) => {
  if (confirm('确定要删除这条记录吗？')) {
    // 模拟删除操作
    console.log('删除服务者:', id)
    // 这里应该调用API删除数据
    alert('删除成功')
  }
}

// 切换页面
const changeProviderPage = (page: number) => {
  if (page >= 1 && page <= providerTotalPages.value) {
    certProviderCurrentPage.value = page
    // 重置选择
    certProviderSelectAll.value = false
    certProviderSelectedItems.value = []
  }
}

// 切换页面
const changeServicePage = (page: number) => {
  if (page >= 1 && page <= serviceTotalPages.value) {
    serviceCurrentPage.value = page
    // 重置选择
    serviceSelectAll.value = false
    serviceSelectedItems.value = {}
  }
}


// 退出登录
const logout = () => {
  showUserDropdown.value = false
  // 清除用户信息
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  localStorage.removeItem('avatar')
  userInfo.value = {
    username: '',
    role: ''
  }
  userAvatar.value = ''
  // 跳转到登录页
  router.push('/login')
}
</script>

<style scoped>
/* 服务者认证相关样式 */
.provider-verification .data-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.provider-verification .certificate-image,
.provider-verification .id-card-image {
  width: 60px;
  height: 60px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.provider-verification .certificate-image:hover,
.provider-verification .id-card-image:hover {
  transform: scale(1.1);
}

.provider-verification .status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.provider-verification .passed-status {
  background: #e6f7ef;
  color: #00c48c;
}

.provider-verification .pending-status {
  background: #fff7e6;
  color: #fa8c16;
}

.provider-verification .reviewed-text {
  color: #007bff;
  font-size: 14px;
}

.provider-verification .review-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.provider-verification .review-btn:hover {
  background: #0056b3;
}

/* 容器样式 */
.admin-home-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  position: fixed;
  top: 0;
  left: 0;
  overflow: hidden;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 顶部导航栏样式 */
.top-header {
  background: #8B4513;
  color: white;
  height: 60px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 30px;
}

.logo-section {
  display: flex;
  align-items: center;
}

.logo {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: white;
}

.breadcrumb {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.user-section {
  position: relative;
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.1);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  color: #fff;
  font-size: 16px;
  opacity: 0.8;
}

.username {
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.dropdown-arrow {
  font-size: 12px;
  color: white;
  transition: transform 0.3s ease;
  opacity: 0.8;
}

.dropdown-arrow.active {
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 150px;
  z-index: 1000;
  overflow: hidden;
  margin-top: 5px;
}

.user-dropdown::before {
  content: '';
  position: absolute;
  top: -8px;
  right: 20px;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-bottom: 8px solid white;
}

.dropdown-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  color: #333;
  font-size: 14px;
}

.dropdown-item:hover {
  background: #f8f9fa;
}

.dropdown-item:last-child {
  border-top: 1px solid #eee;
}

/* 主要内容区域容器 */
.content-wrapper {
  display: flex;
  flex: 1;
  height: calc(100vh - 60px);
  overflow: hidden;
}

/* 侧边导航菜单 */
.sidebar-nav {
  width: 200px;
  background: white;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  flex-shrink: 0;
}

/* 所有菜单项背景色统一 */
.nav-item {
  background: white;
}

.nav-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.nav-item:hover {
  background: #f8f9fa;
}

.nav-item.active {
  background: #8B4513;
  border-left: 3px solid #8B4513;
  color: white;
}

.nav-item.active .nav-text {
  color: white;
  font-weight: 600;
}

/* 子菜单样式 */
.sub-menu {
  background: #f8f9fa;
  margin-left: 0;
  border-radius: 0;
  overflow: hidden;
}

.sub-item {
  margin: 0;
  border-left: none;
  padding-left: 40px;
  background: transparent;
  border-radius: 0;
}

.sub-item:hover {
  background: #e9ecef;
  color: #8B4513;
}

.sub-item.active {
  background: #8B4513;
  color: white;
  border-left: 3px solid #8B4513;
}

.sub-item.active .nav-text {
  color: white;
  font-weight: 600;
}

/* 导航图标样式优化 */
.nav-icon {
  margin-right: 10px;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

/* 展开箭头样式优化 */
.nav-item.active .expand-arrow {
  color: white;
}

/* 展开箭头动画 */
.expand-arrow {
  transition: transform 0.3s ease;
}

.expand-arrow.expanded {
  transform: rotate(180deg);
}

.nav-icon {
  margin-right: 10px;
  font-size: 16px;
}

.nav-text {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  flex: 1;
}

.expand-arrow {
  font-size: 12px;
  color: #666;
  margin-left: 5px;
}

.nav-item.active .expand-arrow {
  color: #8B4513;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
  background: #e3f2fd;
  box-sizing: border-box;
}

.content-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 欢迎信息区域 */
.welcome-section {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 15px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.welcome-text {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #d4edda;
  color: #155724;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.status-icon {
  width: 20px;
  height: 20px;
  background: #28a745;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

/* 系统公告区域 */
.announcements-section {
  background: white;
}

.section-title {
  color: #333;
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
}

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.announcement-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #007bff;
}

.announcement-bullet {
  width: 8px;
  height: 8px;
  background: #ccc;
  border-radius: 50%;
  margin-top: 8px;
  flex-shrink: 0;
}

.announcement-content {
  flex: 1;
}

.announcement-title {
  color: #333;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.announcement-description {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
}

.announcement-time {
  color: #999;
  font-size: 12px;
}

/* 空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #666;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-text {
  font-size: 16px;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
  background: #f8f9fa;
  border-radius: 8px 8px 0 0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #e9ecef;
  color: #333;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
  border-radius: 0 0 8px 8px;
}

/* 表单样式 */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #007bff;
}

.form-input.error {
  border-color: #f44336;
}

.error-message {
  display: block;
  color: #f44336;
  font-size: 12px;
  margin-top: 4px;
}

.password-hint {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

/* 按钮样式 */
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-secondary {
  background: #f8f9fa;
  color: #6c757d;
  border: 1px solid #dee2e6;
}

.btn-secondary:hover {
  background: #e9ecef;
  color: #495057;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover {
  background: #0056b3;
}

/* 居家贴士相关样式 */
.tip-image {
  width: 60px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}

.no-image {
  width: 60px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  color: #999;
  font-size: 12px;
  border-radius: 4px;
}

.tip-intro {
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.view-content-btn {
  padding: 4px 12px;
  font-size: 12px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.view-content-btn:hover {
  background: #0056b3;
}

/* 服务预约管理样式 */
.appointment-management {
  width: 100%;
}

.appointment-management .data-table th {
  background-color: #f8f9fa;
  color: #6c757d;
  font-weight: 600;
  padding: 12px 8px;
  text-align: center;
  border-bottom: 2px solid #dee2e6;
}

.appointment-management .data-table th:last-child {
  text-align: center;
  min-width: 120px;
}

.appointment-management .search-btn,
.appointment-management .reset-btn {
  font-weight: 500;
  font-size: 14px;
}

.section-header {
  margin-bottom: 20px;
}

.search-operation-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-box {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-input {
  min-width: 250px;
}

.search-btn {
  background: #007bff;
  color: white;
}

.reset-btn {
  background: #6c757d;
  color: white;
}

.delete-btn {
  background: #dc3545;
  color: white;
}

.data-table-container {
  overflow-x: auto;
  margin-bottom: 20px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px 8px;
  text-align: left;
  border-bottom: 1px solid #dee2e6;
  font-size: 14px;
}

.data-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #495057;
  white-space: nowrap;
}

.data-table td {
  color: #333;
}

.checkbox-header,
.checkbox-cell {
  width: 40px;
  text-align: center;
}

.checkbox {
  transform: scale(1.2);
}

.price-cell {
  color: #ff4500;
  font-weight: 500;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-completed {
  background: #d4edda;
  color: #155724;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-processing {
  background: #fff3cd;
  color: #856404;
}


.operation-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  padding: 8px;
}

.assign-btn {
  background: #3b82f6;
  color: white;
  padding: 8px 16px;
  font-size: 14px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  font-weight: 500;
}

.assign-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.delete-btn {
  background: #ef4444;
  color: white;
  padding: 8px;
  font-size: 14px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 8px;
}

.delete-btn:hover {
  background: #dc2626;
  transform: translateY(-1px);
}

.batch-delete-btn {
  background: #ef4444;
  color: white;
  padding: 10px 20px;
  font-size: 14px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  font-weight: 500;
  min-width: 100px;
}

.batch-delete-btn:hover {
  background: #dc2626;
  transform: translateY(-1px);
}

.batch-delete-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
}

.table-row:hover {
  background: #f8f9fa;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
}

.page-info {
  color: #6c757d;
  font-size: 14px;
}

.page-controls {
  display: flex;
  gap: 5px;
  align-items: center;
}

.page-btn {
  padding: 6px 12px;
  font-size: 14px;
  background: white;
  color: #495057;
  border: 1px solid #dee2e6;
  min-width: 36px;
}

.page-btn:hover:not(:disabled) {
  background: #e9ecef;
}

.page-btn.active {
  background: #8B4513;
  color: white;
  border-color: #8B4513;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 提现记录相关样式 */
.withdrawal-management {
  padding: 20px;
}

.withdrawal-management .section-header h2 {
  margin-bottom: 20px;
  color: #333;
  font-size: 18px;
}

.withdrawal-management .search-operation-area {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.withdrawal-management .search-box {
  display: flex;
  gap: 10px;
}

.withdrawal-management .search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.withdrawal-management .btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.withdrawal-management .search-btn {
  background-color: #4CAF50;
  color: white;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.withdrawal-management .reset-btn {
  background-color: #FFA500;
  color: white;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.withdrawal-management .delete-btn {
  background-color: #FF4444;
  color: white;
}

.withdrawal-management .delete-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

/* 批量删除按钮特殊样式 */
.withdrawal-management .operation-buttons .delete-btn {
  background: #dc3545 !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  min-width: 120px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.withdrawal-management .operation-buttons .delete-btn:hover:not(:disabled) {
  background: #c82333 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.withdrawal-management .operation-buttons .delete-btn:active:not(:disabled) {
  background: #bd2130 !important;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.withdrawal-management .operation-buttons .delete-btn:disabled {
  background: #6c757d !important;
  color: white !important;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

.withdrawal-management .data-table-container {
  overflow-x: auto;
  margin-bottom: 20px;
}

.withdrawal-management .data-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.withdrawal-management .data-table th,
.withdrawal-management .data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.withdrawal-management .data-table th {
  background-color: #f5f5f5;
  font-weight: bold;
  color: #333;
}

.withdrawal-management .table-row:hover {
  background-color: #f9f9f9;
}

.withdrawal-management .checkbox-header,
.withdrawal-management .checkbox-cell {
  width: 50px;
  text-align: center;
}

.withdrawal-management .price-cell {
  color: #FF4444;
  font-weight: bold;
}

.withdrawal-management .operation-cell {
  width: 80px;
  text-align: center;
}

.withdrawal-management .pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.withdrawal-management .page-info {
  color: #666;
}

.withdrawal-management .page-controls {
  display: flex;
  gap: 5px;
}

.withdrawal-management .page-btn {
  min-width: 32px;
  height: 32px;
  padding: 0 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.withdrawal-management .page-btn.active {
  background-color: #4CAF50;
  color: white;
  border-color: #4CAF50;
}

.withdrawal-management .page-btn:disabled {
  background-color: #f5f5f5;
  color: #ccc;
  cursor: not-allowed;
}

/* 服务分类管理样式 */
.category-management {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
}

.category-management h2 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

/* 服务分类管理按钮样式 */
.category-management .search-btn {
  background-color: #4CAF50;
  color: white;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.category-management .reset-btn {
  background-color: #FFA500;
  color: white;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.category-management .operation-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

.category-management .add-btn {
  background: linear-gradient(135deg, #17a2b8, #138496);
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 80px;
  text-align: center;
}

.category-management .add-btn:hover {
  background: linear-gradient(135deg, #138496, #117a8b);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(23, 162, 184, 0.3);
}

.category-management .operation-buttons .delete-btn {
  background: linear-gradient(135deg, #dc3545, #c82333);
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 100px;
  text-align: center;
}

.category-management .operation-buttons .delete-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #c82333, #bd2130);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.category-management .operation-buttons .delete-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

/* 服务分类编辑对话框样式 */
.category-dialog {
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.category-dialog .modal-body {
  padding: 20px;
}

.category-dialog .form-group {
  margin-bottom: 20px;
}

.category-dialog label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.category-dialog .form-input,
.category-dialog .form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.category-dialog .form-input:focus,
.category-dialog .form-textarea:focus {
  outline: none;
  border-color: #17a2b8;
  box-shadow: 0 0 0 2px rgba(23, 162, 184, 0.1);
}

.category-dialog .form-textarea {
  resize: vertical;
  min-height: 80px;
}

.category-dialog .icon-selector {
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 15px;
  background: #f9f9f9;
}

.category-dialog .icon-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 8px;
  margin-bottom: 15px;
}

.category-dialog .icon-option {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  font-size: 20px;
  transition: all 0.3s ease;
  background: white;
}

.category-dialog .icon-option:hover {
  border-color: #17a2b8;
  background: #f0f8ff;
  transform: scale(1.1);
}

.category-dialog .icon-option.selected {
  border-color: #17a2b8;
  background: #17a2b8;
  color: white;
  transform: scale(1.1);
}

.category-dialog .selected-icon-preview {
  text-align: center;
  padding: 10px;
  background: white;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.category-dialog .preview-icon {
  font-size: 24px;
  display: inline-block;
}

.category-dialog .no-icon {
  color: #999;
  font-style: italic;
}

.category-dialog .form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.category-dialog .cancel-btn {
  background: #6c757d;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.category-dialog .cancel-btn:hover {
  background: #545b62;
}

.category-dialog .save-btn {
  background: linear-gradient(135deg, #17a2b8, #138496);
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.category-dialog .save-btn:hover {
  background: linear-gradient(135deg, #138496, #117a8b);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(23, 162, 184, 0.3);
}

.category-search-area {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.category-search-area input {
  flex: 1;
  max-width: 300px;
  height: 36px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.category-search-area input:focus {
  outline: none;
  border-color: #409eff;
}

.category-search-area button {
  height: 36px;
  padding: 0 15px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-search-area .search-btn {
  background: #409eff;
  color: #fff;
}

.category-search-area .search-btn:hover {
  background: #66b1ff;
}

.category-search-area .reset-btn {
  background: #fff;
  color: #606266;
  border: 1px solid #dcdfe6;
}

.category-search-area .reset-btn:hover {
  color: #409eff;
  border-color: #c6e2ff;
}

.category-search-area .add-btn {
  background: #67c23a;
  color: #fff;
}

.category-search-area .add-btn:hover {
  background: #85ce61;
}

.category-search-area .batch-delete-btn {
  background: #f56c6c;
  color: #fff;
}

.category-search-area .batch-delete-btn:hover {
  background: #f78989;
}

.category-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.category-table th,
.category-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.category-table th {
  font-weight: 600;
  color: #606266;
  background: #f5f7fa;
}

.category-table tr:hover {
  background: #f5f7fa;
}

.category-table .icon-cell {
  font-size: 24px;
}

.category-table .action-buttons {
  display: flex;
  gap: 8px;
}

.category-table .edit-btn,
.category-table .delete-btn {
  padding: 4px 10px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-table .edit-btn {
  background: #409eff;
  color: #fff;
}

.category-table .edit-btn:hover {
  background: #66b1ff;
}

.category-table .delete-btn {
  background: #f56c6c;
  color: #fff;
}

.category-table .delete-btn:hover {
  background: #f78989;
}

.category-pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}

.category-pagination button {
  min-width: 32px;
  height: 32px;
  padding: 0 8px;
  border: 1px solid #dcdfe6;
  background: #fff;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-pagination button:hover:not(:disabled) {
  color: #409eff;
  border-color: #c6e2ff;
}

.category-pagination button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.category-pagination button.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

/* 轮播图管理样式 */
.carousel-management {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
}

.carousel-management h2 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

.carousel-search-area {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.carousel-search-area input {
  flex: 1;
  max-width: 300px;
  height: 36px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.carousel-search-area input:focus {
  outline: none;
  border-color: #409eff;
}

.carousel-search-area button {
  height: 36px;
  padding: 0 15px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.carousel-search-area .search-btn {
  background: #409eff;
  color: #fff;
}

.carousel-search-area .search-btn:hover {
  background: #66b1ff;
}

.carousel-search-area .reset-btn {
  background: #fff;
  color: #606266;
  border: 1px solid #dcdfe6;
}

.carousel-search-area .reset-btn:hover {
  color: #409eff;
  border-color: #c6e2ff;
}

.carousel-search-area .add-btn {
  background: #67c23a;
  color: #fff;
}

.carousel-search-area .add-btn:hover {
  background: #85ce61;
}

.carousel-search-area .batch-delete-btn {
  background: #f56c6c;
  color: #fff;
}

.carousel-search-area .batch-delete-btn:hover {
  background: #f78989;
}

/* 轮播图信息操作按钮布局 */
.carousel-management .operation-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

/* 轮播图信息批量删除按钮特殊样式 */
.carousel-management .operation-buttons .delete-btn {
  background: #dc3545 !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 120px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.carousel-management .operation-buttons .delete-btn:hover:not(:disabled) {
  background: #c82333 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.carousel-management .operation-buttons .delete-btn:active:not(:disabled) {
  background: #bd2130 !important;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.carousel-management .operation-buttons .delete-btn:disabled {
  background: #6c757d !important;
  color: white !important;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

/* 轮播图信息新增按钮特殊样式 */
.carousel-management .operation-buttons .add-btn {
  background: linear-gradient(135deg, #17a2b8, #138496) !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 80px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.2);
}

.carousel-management .operation-buttons .add-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #138496, #117a8b) !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(23, 162, 184, 0.3);
}

.carousel-management .operation-buttons .add-btn:active:not(:disabled) {
  background: linear-gradient(135deg, #117a8b, #0f6674) !important;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.2);
}

/* 轮播图信息搜索按钮横向字体样式 */
.carousel-management .search-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.carousel-management .reset-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.carousel-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.carousel-table th,
.carousel-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.carousel-table th {
  font-weight: 600;
  color: #606266;
  background: #f5f7fa;
}

.carousel-table tr:hover {
  background: #f5f7fa;
}

.carousel-table .image-cell img {
  width: 100px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.carousel-table .action-buttons {
  display: flex;
  gap: 8px;
}

.carousel-table .edit-btn,
.carousel-table .delete-btn {
  padding: 4px 10px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.carousel-table .edit-btn {
  background: #409eff;
  color: #fff;
}

.carousel-table .edit-btn:hover {
  background: #66b1ff;
}

.carousel-table .delete-btn {
  background: #f56c6c;
  color: #fff;
}

.carousel-table .delete-btn:hover {
  background: #f78989;
}

.carousel-pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}

.carousel-pagination button {
  min-width: 32px;
  height: 32px;
  padding: 0 8px;
  border: 1px solid #dcdfe6;
  background: #fff;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.carousel-pagination button:hover:not(:disabled) {
  color: #409eff;
  border-color: #c6e2ff;
}

.carousel-pagination button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.carousel-pagination button.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

/* 系统公告管理样式 */
.notice-management {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 600px;
}

.notice-management .section-header {
  margin-bottom: 20px;
}

.notice-management .section-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.notice-management .search-operation-area {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-management .search-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.notice-management .form-input.search-input {
  width: 300px;
  padding: 8px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.notice-management .form-input.search-input:focus {
  outline: none;
  border-color: #409eff;
}

.notice-management .operation-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

.notice-management .btn {
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  border: none;
  min-width: 60px;
}

.notice-management .btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.notice-management .btn.search-btn {
  background-color: #409eff;
  color: white;
}

.notice-management .btn.search-btn:hover:not(:disabled) {
  background-color: #66b1ff;
}

.notice-management .btn.reset-btn {
  background-color: #fff;
  color: #606266;
  border: 1px solid #dcdfe6;
}

.notice-management .btn.reset-btn:hover:not(:disabled) {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

.notice-management .btn.add-btn {
  background-color: #67c23a;
  color: white;
}

.notice-management .btn.add-btn:hover:not(:disabled) {
  background-color: #85ce61;
}

/* 系统公告新增按钮特殊样式 */
.notice-management .operation-buttons .add-btn {
  background: linear-gradient(135deg, #17a2b8, #138496) !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 80px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.2);
}

.notice-management .operation-buttons .add-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #138496, #117a8b) !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(23, 162, 184, 0.3);
}

.notice-management .operation-buttons .add-btn:active:not(:disabled) {
  background: linear-gradient(135deg, #117a8b, #0f6674) !important;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.2);
}

.notice-management .btn.edit-btn {
  background-color: #e6a23c;
  color: white;
}

.notice-management .btn.edit-btn:hover:not(:disabled) {
  background-color: #ebb563;
}

.notice-management .btn.delete-btn {
  background-color: #f56c6c;
  color: white;
}

.notice-management .btn.delete-btn:hover:not(:disabled) {
  background-color: #f78989;
}

/* 系统公告批量删除按钮特殊样式 */
.notice-management .operation-buttons .delete-btn {
  background: #dc3545 !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 120px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.notice-management .operation-buttons .delete-btn:hover:not(:disabled) {
  background: #c82333 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.notice-management .operation-buttons .delete-btn:active:not(:disabled) {
  background: #bd2130 !important;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.notice-management .operation-buttons .delete-btn:disabled {
  background: #6c757d !important;
  color: white !important;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

.notice-management .data-table-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
  margin-bottom: 20px;
}

.notice-management .data-table {
  width: 100%;
  border-collapse: collapse;
}

.notice-management .data-table th,
.notice-management .data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.notice-management .data-table th {
  background-color: #f5f7fa;
  font-weight: 500;
  color: #606266;
  font-size: 14px;
  white-space: nowrap;
}

.notice-management .data-table td {
  font-size: 14px;
  color: #303133;
}

.notice-management .data-table tbody tr:hover {
  background-color: #f5f7fa;
}

.notice-management .checkbox-header,
.notice-management .checkbox-cell {
  width: 60px;
  text-align: center;
}

.notice-management .checkbox {
  cursor: pointer;
}

.notice-management td:nth-child(2) {
  width: 180px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-management td:nth-child(3) {
  width: 400px;
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #606266;
}

.notice-management td:nth-child(4) {
  width: 180px;
  text-align: center;
  color: #909399;
}

.notice-management .operation-cell {
  width: 120px;
  text-align: center;
  white-space: nowrap;
}

.notice-management .operation-cell .btn {
  padding: 4px 12px;
  margin: 0 5px;
  font-size: 12px;
}

.notice-management .pagination {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-management .page-info {
  color: #909399;
  font-size: 14px;
}

.notice-management .page-controls {
  display: flex;
  align-items: center;
  gap: 5px;
}

.notice-management .page-btn {
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  padding: 0 8px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  border: 1px solid #dcdfe6;
  background-color: #fff;
  transition: all 0.3s;
}

.notice-management .page-btn:hover:not(:disabled) {
  border-color: #409eff;
  color: #409eff;
}

.notice-management .page-btn.active {
  background-color: #409eff;
  border-color: #409eff;
  color: white;
}

.notice-management .page-btn:disabled {
  cursor: not-allowed;
  color: #c0c4cc;
  border-color: #ebeef5;
  background-color: #f5f7fa;
}

.notice-management .page-btn:disabled:hover {
  border-color: #ebeef5;
  color: #c0c4cc;
  background-color: #f5f7fa;
}

/* 居家贴士管理样式 */
.tips-management .operation-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

.tips-management .operation-buttons .delete-btn {
  background: #dc3545 !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 120px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.tips-management .operation-buttons .delete-btn:hover:not(:disabled) {
  background: #c82333 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.tips-management .operation-buttons .delete-btn:active:not(:disabled) {
  background: #bd2130 !important;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.tips-management .operation-buttons .delete-btn:disabled {
  background: #6c757d !important;
  color: white !important;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

/* 居家贴士新增按钮特殊样式 */
.tips-management .operation-buttons .add-btn {
  background: linear-gradient(135deg, #17a2b8, #138496) !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 80px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.2);
}

.tips-management .operation-buttons .add-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #138496, #117a8b) !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(23, 162, 184, 0.3);
}

.tips-management .operation-buttons .add-btn:active:not(:disabled) {
  background: linear-gradient(135deg, #117a8b, #0f6674) !important;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.2);
}

/* 居家贴士表格操作按钮样式 */
.tips-management table td:last-child {
  display: flex;
  flex-direction: row;
  gap: 8px;
  align-items: center;
  justify-content: center;
}

.tips-management table td:last-child .edit-btn {
  background: linear-gradient(135deg, #ffc107, #e0a800) !important;
  color: #212529 !important;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 50px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.tips-management .operation-cell .edit-btn:hover {
  background: linear-gradient(135deg, #e0a800, #d39e00) !important;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(255, 193, 7, 0.3);
}

.tips-management table td:last-child .delete-btn {
  background: linear-gradient(135deg, #dc3545, #c82333) !important;
  color: white !important;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 50px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.tips-management .operation-cell .delete-btn:hover {
  background: linear-gradient(135deg, #c82333, #bd2130) !important;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.3);
}

/* 居家贴士搜索按钮横向字体样式 */
.tips-management .search-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.tips-management .reset-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

/* 服务评价管理样式 */
.evaluation-management {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 600px;
}

.evaluation-management .section-header {
  margin-bottom: 20px;
}

.evaluation-management .section-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.evaluation-management .search-operation-area {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.evaluation-management .search-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.evaluation-management .form-input.search-input {
  width: 300px;
  padding: 8px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.evaluation-management .form-input.search-input:focus {
  outline: none;
  border-color: #409eff;
}

.evaluation-management .operation-buttons {
  display: flex;
  gap: 10px;
}

.evaluation-management .btn {
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  border: 1px solid transparent;
  min-width: 60px;
}

.evaluation-management .btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.evaluation-management .btn.search-btn {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

.evaluation-management .btn.search-btn:hover:not(:disabled) {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.evaluation-management .btn.reset-btn {
  background-color: #fff;
  color: #606266;
  border-color: #dcdfe6;
}

.evaluation-management .btn.reset-btn:hover:not(:disabled) {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

.evaluation-management .btn.delete-btn {
  background-color: #f56c6c;
  color: white;
  border-color: #f56c6c;
}

.evaluation-management .btn.delete-btn:hover:not(:disabled) {
  background-color: #f78989;
  border-color: #f78989;
}

/* 服务评价批量删除按钮特殊样式 */
.evaluation-management .operation-buttons .delete-btn {
  background: #dc3545 !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 120px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.evaluation-management .operation-buttons .delete-btn:hover:not(:disabled) {
  background: #c82333 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.evaluation-management .operation-buttons .delete-btn:active:not(:disabled) {
  background: #bd2130 !important;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.evaluation-management .operation-buttons .delete-btn:disabled {
  background: #6c757d !important;
  color: white !important;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

.evaluation-management .data-table-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
  margin-bottom: 20px;
}

.evaluation-management .data-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.evaluation-management .data-table th,
.evaluation-management .data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.evaluation-management .data-table th {
  background-color: #f5f7fa;
  font-weight: 500;
  color: #606266;
  font-size: 14px;
}

.evaluation-management .data-table td {
  font-size: 14px;
  color: #303133;
}

.evaluation-management .data-table tbody tr:hover {
  background-color: #f5f7fa;
}

.evaluation-management .checkbox-header,
.evaluation-management .checkbox-cell {
  width: 60px;
  text-align: center;
  white-space: nowrap;
  overflow: visible;
}

.evaluation-management .checkbox {
  cursor: pointer;
  margin: 0;
}

.evaluation-management th:nth-child(2),
.evaluation-management td:nth-child(2) {
  width: 120px;
  text-align: center;
}

.evaluation-management th:nth-child(3),
.evaluation-management td:nth-child(3) {
  width: 180px;
}

.evaluation-management th:nth-child(4),
.evaluation-management td:nth-child(4) {
  width: 100px;
  text-align: center;
}

.evaluation-management th:nth-child(5),
.evaluation-management td:nth-child(5) {
  width: 120px;
  text-align: center;
}

.evaluation-management th:nth-child(6),
.evaluation-management td:nth-child(6) {
  width: 200px;
}

.evaluation-management th:nth-child(7),
.evaluation-management td:nth-child(7) {
  width: 160px;
  text-align: center;
  color: #909399;
}

.evaluation-management .operation-cell {
  width: 80px;
  text-align: center;
  white-space: nowrap;
  overflow: visible;
}

.evaluation-management .operation-cell .btn {
  padding: 4px 12px;
  margin: 0 2px;
  font-size: 12px;
  min-width: 50px;
}

.evaluation-management .rating-cell {
  position: relative;
}

.evaluation-management .star-rating {
  color: #f7ba2a;
  font-size: 14px;
  display: inline-block;
  margin-right: 5px;
}

.evaluation-management .star-rating .star {
  cursor: default;
  transition: none;
}

.evaluation-management .star-rating .star.filled {
  color: #f7ba2a;
}

.evaluation-management .star-rating .star:not(.filled) {
  color: #e4e7ed;
}

.evaluation-management .rating-value {
  font-size: 12px;
  color: #909399;
  vertical-align: middle;
}

.evaluation-management .pagination {
  background-color: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.evaluation-management .page-info {
  color: #909399;
  font-size: 14px;
}

.evaluation-management .page-controls {
  display: flex;
  align-items: center;
  gap: 5px;
}

.evaluation-management .btn.page-btn {
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  padding: 0 8px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  border: 1px solid #dcdfe6;
  background-color: #fff;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.evaluation-management .btn.page-btn:hover:not(:disabled) {
  border-color: #409eff;
  color: #409eff;
}

.evaluation-management .btn.page-btn.active {
  background-color: #409eff;
  border-color: #409eff;
  color: white;
}

.evaluation-management .btn.page-btn:disabled {
  cursor: not-allowed;
  color: #c0c4cc;
  border-color: #ebeef5;
  background-color: #f5f7fa;
}

.evaluation-management .btn.page-btn:disabled:hover {
  border-color: #ebeef5;
  color: #c0c4cc;
  background-color: #f5f7fa;
}

/* 家政服务管理样式 */
.service-management {
  width: 100%;
}

.service-management .search-operation-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.service-management .search-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.service-management .operation-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

.service-management .btn {
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: none;
  min-width: 80px;
  text-align: center;
}

.service-management .btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.service-management .btn:active {
  transform: translateY(0);
}

.service-management .create-btn {
  background: linear-gradient(135deg, #17a2b8, #138496);
  color: white;
}

.service-management .create-btn:hover {
  background: linear-gradient(135deg, #138496, #117a8b);
}

.service-management .delete-btn {
  background: linear-gradient(135deg, #dc3545, #c82333);
  color: white;
}

.service-management .delete-btn:hover {
  background: linear-gradient(135deg, #c82333, #bd2130);
}

.service-management .delete-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.service-management .search-btn {
  background: linear-gradient(135deg, #007bff, #0056b3);
  color: white;
}

.service-management .search-btn:hover {
  background: linear-gradient(135deg, #0056b3, #004085);
}

.service-management .reset-btn {
  background: linear-gradient(135deg, #6c757d, #545b62);
  color: white;
}

.service-management .reset-btn:hover {
  background: linear-gradient(135deg, #545b62, #3d4043);
}

/* 服务编辑对话框样式 */
.service-dialog {
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.service-dialog .modal-body {
  padding: 20px;
}

.service-dialog .form-group {
  margin-bottom: 20px;
}

.service-dialog .form-row {
  display: flex;
  gap: 15px;
}

.service-dialog .form-row .form-group {
  flex: 1;
}

.service-dialog label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.service-dialog .form-input,
.service-dialog .form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.service-dialog .form-input:focus,
.service-dialog .form-textarea:focus {
  outline: none;
  border-color: #17a2b8;
  box-shadow: 0 0 0 2px rgba(23, 162, 184, 0.1);
}

.service-dialog .form-textarea {
  resize: vertical;
  min-height: 80px;
}

.service-dialog .image-upload-area {
  position: relative;
}

.service-dialog .upload-container {
  width: 100%;
}

.service-dialog .upload-button {
  width: 100%;
  min-height: 120px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.service-dialog .upload-button:hover {
  border-color: #17a2b8;
  background-color: #f8f9fa;
}

.service-dialog .upload-placeholder {
  text-align: center;
  color: #666;
}

.service-dialog .upload-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.service-dialog .upload-text {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

.service-dialog .upload-hint {
  font-size: 12px;
  color: #999;
}

.service-dialog .image-preview-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.service-dialog .preview-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
}

.service-dialog .image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 6px;
}

.service-dialog .image-preview-container:hover .image-overlay {
  opacity: 1;
}

.service-dialog .change-image-btn,
.service-dialog .remove-image-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.service-dialog .change-image-btn {
  background: #17a2b8;
  color: white;
}

.service-dialog .change-image-btn:hover {
  background: #138496;
}

.service-dialog .remove-image-btn {
  background: #dc3545;
  color: white;
}

.service-dialog .remove-image-btn:hover {
  background: #c82333;
}

.service-dialog .form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.service-dialog .cancel-btn {
  background: #6c757d;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.service-dialog .cancel-btn:hover {
  background: #545b62;
}

.service-dialog .save-btn {
  background: linear-gradient(135deg, #17a2b8, #138496);
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.service-dialog .save-btn:hover {
  background: linear-gradient(135deg, #138496, #117a8b);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(23, 162, 184, 0.3);
}

.image-cell {
  width: 80px;
}

.service-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.detail-view-btn {
  background: #007bff;
  color: white;
  padding: 4px 8px;
  font-size: 12px;
}

.create-btn {
  background: #17a2b8;
  color: white;
}

.edit-btn {
  background: #ffc107;
  color: #212529;
  padding: 4px 8px;
  font-size: 12px;
}

/* 服务者认证管理样式 */
.provider-verification {
  width: 100%;
}

.certificate-cell,
.id-card-cell {
  width: 100px;
}

.certificate-image,
.id-card-image {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.status-cell {
  width: 100px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.passed-status {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.reviewed-text {
  color: #6c757d;
  font-size: 14px;
}

/* 收藏信息管理样式 */
.favorites-management {
  width: 100%;
}

.favorites-management .section-header {
  margin-bottom: 20px;
}

.favorites-management .section-header h2 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.favorites-management .search-operation-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.favorites-management .search-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.favorites-management .search-input {
  width: 300px;
  height: 36px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.favorites-management .search-input:focus {
  border-color: #409eff;
}

.favorites-management .btn {
  padding: 6px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid;
  outline: none;
}

.favorites-management .search-btn {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

.favorites-management .search-btn:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.favorites-management .reset-btn {
  background-color: #fff;
  color: #606266;
  border-color: #dcdfe6;
}

.favorites-management .reset-btn:hover {
  color: #409eff;
  border-color: #c6e2ff;
}

.favorites-management .add-btn {
  background-color: #67c23a;
  color: white;
  border-color: #67c23a;
}

.favorites-management .add-btn:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}

.favorites-management .delete-btn {
  background-color: #f56c6c;
  color: white;
  border-color: #f56c6c;
}

.favorites-management .delete-btn:hover:not(:disabled) {
  background-color: #f78989;
  border-color: #f78989;
}

.favorites-management .delete-btn:disabled {
  background-color: #f5f7fa;
  color: #c0c4cc;
  border-color: #ebeef5;
  cursor: not-allowed;
}

/* 收藏信息操作按钮布局 */
.favorites-management .operation-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

/* 收藏信息批量删除按钮特殊样式 */
.favorites-management .operation-buttons .delete-btn {
  background: #dc3545 !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 120px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.favorites-management .operation-buttons .delete-btn:hover:not(:disabled) {
  background: #c82333 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.favorites-management .operation-buttons .delete-btn:active:not(:disabled) {
  background: #bd2130 !important;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.favorites-management .operation-buttons .delete-btn:disabled {
  background: #6c757d !important;
  color: white !important;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

/* 收藏信息新增按钮特殊样式 */
.favorites-management .operation-buttons .add-btn {
  background: linear-gradient(135deg, #17a2b8, #138496) !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 80px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.2);
}

.favorites-management .operation-buttons .add-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #138496, #117a8b) !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(23, 162, 184, 0.3);
}

.favorites-management .operation-buttons .add-btn:active:not(:disabled) {
  background: linear-gradient(135deg, #117a8b, #0f6674) !important;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.2);
}

/* 用户管理子菜单搜索按钮横向字体样式 */
/* 普通用户管理 */
.normal-user-management .search-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.normal-user-management .reset-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

/* 家政服务者管理 */
.provider-management .search-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.provider-management .reset-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

/* 管理员管理 */
.admin-management .search-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.admin-management .reset-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

/* 服务者认证管理 */
.provider-verification .search-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.provider-verification .reset-btn {
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

/* 用户管理子菜单操作按钮布局 */
/* 普通用户管理操作按钮布局 */
.normal-user-management .operation-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

.normal-user-management .operation-buttons .add-btn {
  background: linear-gradient(135deg, #17a2b8, #138496) !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 80px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.2);
}

.normal-user-management .operation-buttons .add-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #138496, #117a8b) !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(23, 162, 184, 0.3);
}

.normal-user-management .operation-buttons .delete-btn {
  background: #dc3545 !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 120px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.normal-user-management .operation-buttons .delete-btn:hover:not(:disabled) {
  background: #c82333 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.normal-user-management .operation-buttons .delete-btn:disabled {
  background: #6c757d !important;
  color: white !important;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

/* 家政服务者管理操作按钮布局 */
.provider-management .operation-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

.provider-management .operation-buttons .add-btn {
  background: linear-gradient(135deg, #17a2b8, #138496) !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 80px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.2);
}

.provider-management .operation-buttons .add-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #138496, #117a8b) !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(23, 162, 184, 0.3);
}

.provider-management .operation-buttons .delete-btn {
  background: #dc3545 !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 120px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.provider-management .operation-buttons .delete-btn:hover:not(:disabled) {
  background: #c82333 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.provider-management .operation-buttons .delete-btn:disabled {
  background: #6c757d !important;
  color: white !important;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

/* 管理员管理操作按钮布局 */
.admin-management .operation-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

.admin-management .operation-buttons .add-btn {
  background: linear-gradient(135deg, #17a2b8, #138496) !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 80px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.2);
}

.admin-management .operation-buttons .add-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #138496, #117a8b) !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(23, 162, 184, 0.3);
}

.admin-management .operation-buttons .delete-btn {
  background: #dc3545 !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 120px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.admin-management .operation-buttons .delete-btn:hover:not(:disabled) {
  background: #c82333 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.admin-management .operation-buttons .delete-btn:disabled {
  background: #6c757d !important;
  color: white !important;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

/* 服务者认证管理操作按钮布局 */
.provider-verification .operation-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

.provider-verification .operation-buttons .delete-btn {
  background: #dc3545 !important;
  color: white !important;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 120px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.provider-verification .operation-buttons .delete-btn:hover:not(:disabled) {
  background: #c82333 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.provider-verification .operation-buttons .delete-btn:active:not(:disabled) {
  background: #bd2130 !important;
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.2);
}

.provider-verification .operation-buttons .delete-btn:disabled {
  background: #6c757d !important;
  color: white !important;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

/* 数据统计界面样式 */
.statistics-dashboard {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 关键指标卡片样式 */
.metrics-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.metric-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.metric-icon.recharge {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: white;
}

.metric-icon.withdrawal {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
}

.metric-icon.provider {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
}

.metric-icon.user {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
}

.metric-content {
  flex: 1;
}

.metric-value {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1;
  margin-bottom: 4px;
}

.metric-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

/* 图表区域样式 */
.charts-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

.chart-container {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-header {
  margin-bottom: 20px;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.chart-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.chart-content {
  position: relative;
}

.chart-content canvas {
  max-width: 100%;
  height: auto;
}

/* 图表图例样式 */
.chart-legend {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #374151;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
  flex-shrink: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .metrics-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .statistics-dashboard {
    padding: 15px;
  }
  
  .metric-card {
    padding: 16px;
  }
  
  .metric-value {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .metrics-cards {
    grid-template-columns: 1fr;
  }
}

.favorites-management .data-table-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.favorites-management .data-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.favorites-management th,
.favorites-management td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.favorites-management th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #606266;
  font-size: 14px;
}

.favorites-management td {
  color: #303133;
  font-size: 14px;
}

.favorites-management .table-row:hover {
  background-color: #f5f7fa;
}

.favorites-management .checkbox-header,
.favorites-management .checkbox-cell {
  width: 50px;
  text-align: center;
}

.favorites-management .operation-cell {
  width: 120px;
  text-align: center;
  white-space: nowrap;
}

.favorites-management .operation-cell .btn {
  padding: 4px 8px;
  margin: 0 2px;
  font-size: 12px;
}

.favorites-management .edit-btn {
  background-color: #e6a23c;
  color: white;
  border-color: #e6a23c;
}

.favorites-management .edit-btn:hover {
  background-color: #ebb563;
  border-color: #ebb563;
}

.favorites-management .pagination {
  background-color: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.favorites-management .page-info {
  color: #909399;
  font-size: 14px;
}

.favorites-management .page-controls {
  display: flex;
  align-items: center;
  gap: 5px;
}

.favorites-management .btn.page-btn {
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  padding: 0 8px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  border: 1px solid #dcdfe6;
  background-color: #fff;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.favorites-management .btn.page-btn:hover:not(:disabled) {
  border-color: #409eff;
  color: #409eff;
}

.favorites-management .btn.page-btn.active {
  background-color: #409eff;
  border-color: #409eff;
  color: white;
}

.favorites-management .btn.page-btn:disabled {
  cursor: not-allowed;
  color: #c0c4cc;
  border-color: #ebeef5;
  background-color: #f5f7fa;
}

.favorites-management .btn.page-btn:disabled:hover {
  border-color: #ebeef5;
  color: #c0c4cc;
  background-color: #f5f7fa;
}

/* 新增收藏信息对话框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-dialog {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.favorite-dialog {
  min-width: 450px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #ebeef5;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #909399;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.3s;
}

.close-btn:hover {
  background-color: #f5f7fa;
  color: #606266;
}

.modal-body {
  padding: 24px;
}

.modal-body .form-group {
  margin-bottom: 20px;
}

.modal-body .form-group:last-child {
  margin-bottom: 0;
}

.modal-body label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #606266;
  font-size: 14px;
}

.modal-body .form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
  background-color: #fff;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.modal-body .form-input:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.modal-body .form-input.error {
  border-color: #f56c6c;
}

.modal-body .form-input.error:focus {
  border-color: #f56c6c;
  box-shadow: 0 0 0 2px rgba(245, 108, 108, 0.2);
}

.error-message {
  display: block;
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  line-height: 1.4;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #ebeef5;
  background-color: #fafafa;
  border-radius: 0 0 8px 8px;
}

.modal-footer .btn {
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
  min-width: 80px;
}

.modal-footer .btn-secondary {
  background-color: #fff;
  border-color: #dcdfe6;
  color: #606266;
}

.modal-footer .btn-secondary:hover {
  background-color: #f5f7fa;
  border-color: #c0c4cc;
  color: #606266;
}

.modal-footer .btn-primary {
  background-color: #409eff;
  border-color: #409eff;
  color: white;
}

.modal-footer .btn-primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

/* 轮播图对话框样式 */
.carousel-dialog {
  min-width: 500px;
}

.image-upload-section {
  margin-top: 8px;
}

.current-image {
  position: relative;
  width: 100%;
  max-width: 400px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.current-image .preview-image {
  width: 100%;
  height: auto;
  display: block;
}

.current-image .image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.current-image:hover .image-overlay {
  opacity: 1;
}

.upload-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100px;
  border: 2px dashed #dcdfe6;
  border-radius: 4px;
  background-color: #fafafa;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-placeholder:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

/* 普通用户管理样式 */
.normal-user-management .status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.normal-user-management .status-active {
  background-color: #e6f7e9;
  color: #389e0d;
  border: 1px solid #b7eb8f;
}

.normal-user-management .status-inactive {
  background-color: #fff2e8;
  color: #cf1322;
  border: 1px solid #ffccc7;
}

/* 家政服务者管理样式 */
.provider-management .status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.provider-management .status-approved {
  background-color: #e6f7e9;
  color: #389e0d;
  border: 1px solid #b7eb8f;
}

.provider-management .status-pending {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.provider-management .status-rejected {
  background-color: #fff2e8;
  color: #cf1322;
  border: 1px solid #ffccc7;
}

/* 管理员管理样式 */
.admin-management .btn.reset-pwd-btn {
  background-color: #faad14;
  color: #fff;
  margin-left: 8px;
}

.admin-management .btn.reset-pwd-btn:hover {
  background-color: #ffc53d;
}

/* 响应式设计调整 */
@media (max-width: 768px) {
  .search-operation-area {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    justify-content: stretch;
  }
  
  .search-input {
    flex: 1;
  }
  
  .pagination {
    flex-direction: column;
    gap: 10px;
  }
}

/* 个人资料对话框样式 */
.personal-profile-dialog {
  max-width: 600px;
  width: 95%;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.avatar-container {
  flex-shrink: 0;
}

.avatar-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.avatar-text {
  font-size: 32px;
  font-weight: 600;
  color: white;
}

.avatar-info h4 {
  margin: 0 0 4px 0;
  font-size: 24px;
  font-weight: 600;
}

.avatar-info p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.profile-section {
  background: #f8fafc;
  border-radius: 8px;
  padding: 20px;
}

.profile-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 8px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.form-group input {
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-group input:disabled {
  background-color: #f9fafb;
  color: #6b7280;
  cursor: not-allowed;
}

.field-note {
  font-size: 12px;
  color: #6b7280;
  font-style: italic;
}

.button-group {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #2563eb, #1e40af);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #e5e7eb;
  border-color: #9ca3af;
}

/* 个人资料对话框响应式设计 */
@media (max-width: 768px) {
  .personal-profile-dialog {
    width: 98%;
    margin: 10px;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
  }
  
  .button-group {
    flex-direction: column;
  }
}
</style>
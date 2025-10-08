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
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'regularUsers' }" @click="setActiveMenu('regularUsers')">
              <div class="nav-icon">👤</div>
              <span class="nav-text">普通用户</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'serviceProviders' }" @click="setActiveMenu('serviceProviders')">
              <div class="nav-icon">🧹</div>
              <span class="nav-text">家政服务者</span>
            </li>
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'adminUsers' }" @click="setActiveMenu('adminUsers')">
              <div class="nav-icon">🛡️</div>
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

        <!-- 其他菜单项的内容区域 -->
        <div v-else class="content-section">
          <!-- 居家贴士管理 -->
          <div v-if="activeMenu === 'tips'" class="tips-management">
            <div class="section-header">
              <h2>居家贴士管理</h2>
            </div>
            
            <!-- 搜索和操作区域 -->
            <div class="search-operation-area">
              <div class="search-box">
                <input 
                  type="text" 
                  v-model="tipsSearchKeyword" 
                  class="form-input search-input" 
                  placeholder="请输入贴士标题或内容查询"
                >
                <button class="btn search-btn" @click="searchTips">搜索</button>
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
                    <th width="50">
                      <input 
                        type="checkbox" 
                        v-model="selectAllTips" 
                        @change="toggleSelectAllTips"
                      >
                    </th>
                    <th width="80">图片</th>
                    <th>标题</th>
                    <th>简介</th>
                    <th>内容</th>
                    <th width="150">发布时间</th>
                    <th width="80">阅读量</th>
                    <th width="100">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="tip in displayedTips" :key="tip.id">
                    <td>
                      <input 
                        type="checkbox" 
                        :value="tip.id" 
                        v-model="selectedTips"
                      >
                    </td>
                    <td>
                      <img 
                        v-if="tip.image" 
                        :src="tip.image" 
                        alt="贴士图片" 
                        class="tip-image"
                      >
                      <div v-else class="no-image">暂无图片</div>
                    </td>
                    <td>{{ tip.title }}</td>
                    <td class="tip-intro">{{ tip.intro }}</td>
                    <td class="tip-content">
                      <button class="btn view-content-btn" @click="showTipContent(tip)">查看内容</button>
                    </td>
                    <td>{{ tip.publishTime }}</td>
                    <td>{{ tip.views }}</td>
                    <td>
                      <button class="btn edit-btn" @click="editTip(tip)">编辑</button>
                      <button class="btn delete-btn" @click="deleteTip(tip.id)">删除</button>
                    </td>
                  </tr>
                </tbody>
              </table>
              
              <!-- 分页控件 -->
              <div class="pagination-controls">
                <div class="page-info">
                  共 {{ filteredTips.length }} 条
                </div>
                <div class="page-buttons">
                  <button 
                    class="btn page-btn" 
                    @click="currentPage > 1 && (currentPage--, updateTipsDisplay())"
                    :disabled="currentPage === 1"
                  >
                    上一页
                  </button>
                  <button 
                    v-for="page in totalPages" 
                    :key="page"
                    class="btn page-btn"
                    :class="{ active: currentPage === page }"
                    @click="currentPage = page; updateTipsDisplay()"
                  >
                    {{ page }}
                  </button>
                  <button 
                    class="btn page-btn" 
                    @click="currentPage < totalPages && (currentPage++, updateTipsDisplay())"
                    :disabled="currentPage === totalPages"
                  >
                    下一页
                  </button>
                </div>
              </div>
            </div>
          </div>
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
                <button class="btn delete-btn" @click="batchDelete">批量删除</button>
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
                    <th>分配</th>
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
                    <td>
                      <span v-if="item.assigned" class="assigned-badge assigned">已分配</span>
                      <span v-else class="assigned-badge unassigned">未分配</span>
                    </td>
                    <td class="operation-cell">
                      <button class="btn assign-btn" @click="assignService(item.id)">
                        分配
                      </button>
                      <button class="btn detail-btn" @click="viewDetail(item.id)">
                        📋
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
                        :value="record.id"
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
                <button class="btn add-btn" @click="showAddCategoryDialog = true">新增</button>
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
                        :value="evaluation.id"
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
                        :value="carousel.id"
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
                        :value="favorite.id"
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
                  v-model="providerSearchKeyword" 
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
                        v-model="providerSelectAll"
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
                  <tr v-for="(item, index) in filteredProviders" :key="index" class="table-row">
                    <td class="checkbox-cell">
                      <input 
                        type="checkbox" 
                        v-model="providerSelectedItems[index]"
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
                      <button class="btn delete-btn" @click="deleteProvider(item.id)">
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
                  :disabled="providerCurrentPage === 1"
                  @click="changeProviderPage(providerCurrentPage - 1)"
                >
                  &lt;
                </button>
                <button 
                  v-for="page in providerPagesToShow" 
                  :key="page"
                  class="btn page-btn" 
                  :class="{ active: providerCurrentPage === page }"
                  @click="changeProviderPage(page)"
                >
                  {{ page }}
                </button>
                <button 
                  class="btn page-btn" 
                  :disabled="providerCurrentPage === providerTotalPages"
                  @click="changeProviderPage(providerCurrentPage + 1)"
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
          </div>
        </div>
      </main>
    </div>

    <!-- 个人资料模态框 -->
    <div v-if="showPersonalProfileModal" class="modal-overlay" @click="showPersonalProfileModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>个人资料</h2>
          <button class="close-btn" @click="showPersonalProfileModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="avatar-section">
            <div class="avatar-wrapper" @click="triggerAvatarUpload">
              <img v-if="personalInfoForm.avatar" :src="personalInfoForm.avatar" alt="头像" class="avatar-image">
              <img v-else-if="userAvatar" :src="userAvatar" alt="头像" class="avatar-image">
              <div v-else class="avatar-placeholder">👤</div>
              <div v-if="isEditingPersonalInfo" class="avatar-upload-hint">点击更换头像</div>
            </div>
            <input 
              ref="avatarInput" 
              type="file" 
              accept="image/*" 
              style="display: none" 
              @change="handleAvatarUpload"
            >
          </div>
          
          <div class="form-group">
            <label>用户名：</label>
            <input 
              type="text" 
              v-model="personalInfoForm.username" 
              class="form-input" 
              :disabled="!isEditingPersonalInfo"
            >
          </div>
          
          <div class="form-group">
            <label>姓名：</label>
            <input 
              type="text" 
              v-model="personalInfoForm.name" 
              class="form-input" 
              :disabled="!isEditingPersonalInfo"
              :class="{ 'error': isEditingPersonalInfo && (!personalInfoForm.name || !personalInfoForm.name.trim()) }"
            >
          </div>
          
          <div class="form-group">
            <label>邮箱：</label>
            <input 
              type="email" 
              v-model="personalInfoForm.email" 
              class="form-input" 
              :disabled="!isEditingPersonalInfo"
            >
          </div>
          
          <div class="form-group">
            <label>电话：</label>
            <input 
              type="text" 
              v-model="personalInfoForm.phone" 
              class="form-input" 
              :disabled="!isEditingPersonalInfo"
            >
          </div>
          
          <div class="form-group">
            <label>创建时间：</label>
            <input 
              type="text" 
              v-model="personalInfoForm.createTime" 
              class="form-input" 
              disabled
            >
          </div>
          
          <div class="form-group">
            <label>角色：</label>
            <input 
              type="text" 
              v-model="personalInfoForm.role" 
              class="form-input" 
              disabled
            >
          </div>
        </div>
        <div class="modal-footer">
          <template v-if="isEditingPersonalInfo">
            <button class="btn btn-secondary" @click="cancelEditPersonalInfo">取消</button>
            <button class="btn btn-primary" @click="savePersonalInfo">保存</button>
          </template>
          <template v-else>
            <button class="btn btn-secondary" @click="showPersonalProfileModal = false">关闭</button>
            <button class="btn btn-primary" @click="startEditPersonalInfo">编辑</button>
          </template>
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
            <div class="password-hint">密码可以设置为任何内容</div>
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 用户信息
const userInfo = ref({
  username: 'admin',
  role: 'admin',
  name: '系统管理员',
  email: 'admin@example.com',
  phone: '138****8888',
  createTime: '2025-01-01'
})
const userAvatar = ref('')
const showUserDropdown = ref(false)

// 个人资料相关
const showPersonalProfileModal = ref(false)
const personalInfoForm = ref({})
const isEditingPersonalInfo = ref(false)

// 菜单状态
const activeMenu = ref('home')
const isServiceMenuExpanded = ref(false)
const isUserMenuExpanded = ref(false)

// 计算属性
const isServiceMenuActive = computed(() => {
  return ['appointment', 'services', 'withdrawal', 'category', 'notice', 'evaluation', 'tips', 'carousel', 'favorites'].includes(activeMenu.value)
})

const isUserMenuActive = computed(() => {
  return ['providerCert', 'regularUsers', 'serviceProviders', 'adminUsers'].includes(activeMenu.value)
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

// 收藏信息管理相关状态
const favoritesSearchKeyword = ref('')
const favoritesSelectAll = ref(false)
const favoritesSelectedItems = ref<string[]>([])
const selectedFavorites = ref<string[]>([])
const showAddFavoriteDialog = ref(false)
const favoritesCurrentPage = ref(1)
const favoritesTotalItems = ref(3)
const favoritesTotalPages = ref(1)
const favoritesPagesToShow = ref([1])

// 模拟收藏数据
const filteredFavorites = ref([
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
  }
])

// 收藏信息管理方法
const searchFavorites = () => {
  console.log('搜索收藏信息:', favoritesSearchKeyword.value)
  // 这里应该根据关键词过滤数据
}

const resetFavoritesSearch = () => {
  favoritesSearchKeyword.value = ''
  // 重置过滤条件
}

const toggleFavoritesSelectAll = () => {
  if (favoritesSelectAll.value) {
    favoritesSelectedItems.value = filteredFavorites.value.map(item => item.id)
  } else {
    favoritesSelectedItems.value = []
  }
  updateSelectedFavorites()
}

const updateSelectedFavorites = () => {
  selectedFavorites.value = [...favoritesSelectedItems.value]
}

const editFavorite = (favorite: any) => {
  console.log('编辑收藏信息:', favorite)
  // 打开编辑对话框并填充数据
}

const deleteFavorite = (id: string) => {
  if (confirm('确定要删除这条收藏信息吗？')) {
    console.log('删除收藏信息:', id)
    // 删除数据的逻辑
    const index = filteredFavorites.value.findIndex(item => item.id === id)
    if (index > -1) {
      filteredFavorites.value.splice(index, 1)
      favoritesTotalItems.value--
    }
  }
}

const batchDeleteFavorites = () => {
  if (selectedFavorites.value.length === 0) return
  if (confirm(`确定要删除选中的 ${selectedFavorites.value.length} 条收藏信息吗？`)) {
    console.log('批量删除收藏信息:', selectedFavorites.value)
    // 批量删除的逻辑
    filteredFavorites.value = filteredFavorites.value.filter(
      item => !selectedFavorites.value.includes(item.id)
    )
    favoritesTotalItems.value -= selectedFavorites.value.length
    selectedFavorites.value = []
    favoritesSelectedItems.value = []
    favoritesSelectAll.value = false
  }
}

const changeFavoritesPage = (page: number) => {
  favoritesCurrentPage.value = page
  // 分页逻辑
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
    regularUsers: '普通用户管理',
    serviceProviders: '家政服务者管理',
    adminUsers: '管理员管理'
  }
  return menuTitles[activeMenu.value] || '功能页面'
}

// 处理个人资料
const handlePersonalProfile = () => {
  showUserDropdown.value = false
  // 复制当前用户信息到表单
  personalInfoForm.value = { ...userInfo.value }
  isEditingPersonalInfo.value = false
  showPersonalProfileModal.value = true
}

// 开始编辑个人资料
const startEditPersonalInfo = () => {
  isEditingPersonalInfo.value = true
}

// 取消编辑个人资料
const cancelEditPersonalInfo = () => {
  // 恢复原始数据
  personalInfoForm.value = { ...userInfo.value }
  isEditingPersonalInfo.value = false
}

// 保存个人资料
const savePersonalInfo = () => {
  // 简单验证
  if (!personalInfoForm.value.name || !personalInfoForm.value.name.trim()) {
    alert('请输入姓名')
    return
  }
  
  // 模拟保存操作
  console.log('保存个人资料:', personalInfoForm.value)
  
  // 更新用户信息
  userInfo.value = { ...personalInfoForm.value }
  
  // 保存到localStorage
  localStorage.setItem('adminUserInfo', JSON.stringify(userInfo.value))
  
  alert('个人资料保存成功！')
  isEditingPersonalInfo.value = false
}

// 头像上传相关
const avatarInput = ref<HTMLInputElement | null>(null)

// 触发头像上传
const triggerAvatarUpload = () => {
  if (avatarInput.value) {
    avatarInput.value.click()
  }
}

// 处理头像上传
const handleAvatarUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (file) {
    // 检查文件大小（2MB限制）
    if (file.size > 2 * 1024 * 1024) {
      alert('头像文件大小不能超过2MB')
      return
    }
    
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
      alert('请上传图片文件')
      return
    }
    
    // 创建文件URL用于预览
    const reader = new FileReader()
    reader.onload = (e) => {
      personalInfoForm.value.avatar = e.target?.result as string
      // 同步更新右上角头像
      userAvatar.value = e.target?.result as string
      // 保存到localStorage
      localStorage.setItem('adminAvatar', e.target?.result as string)
    }
    reader.readAsDataURL(file)
  }
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
  } else if (changePasswordForm.value.newPassword.length < 1) {
    passwordErrors.value.newPassword = '新密码不能为空'
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
  
  // 验证原密码是否正确
  const validUsers = getAllValidUsers()
  const currentUser = validUsers.find((u: any) => 
    u.username === userInfo.value.username && 
    u.role === userInfo.value.role
  )
  
  if (!currentUser || currentUser.password !== changePasswordForm.value.oldPassword) {
    passwordErrors.value.oldPassword = '原密码不正确'
    return
  }
  
  // 更新密码到localStorage
  updateUserPassword(changePasswordForm.value.newPassword)
  
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

// 获取所有有效用户
const getAllValidUsers = () => {
  // 预设的测试用户
  const defaultUsers = [
    { username: 'user', password: '123456', role: 'user' },
    { username: 'admin', password: 'admin123', role: 'admin' },
    { username: 'provider', password: 'provider123', role: 'provider' },
    { username: 'test', password: 'test123', role: 'user' }
  ]
  
  // 从localStorage获取注册的用户
  const registeredUsers = JSON.parse(localStorage.getItem('registeredUsers') || '[]')
  
  // 合并所有用户，registeredUsers中的用户会覆盖defaultUsers中的同名用户
  const allUsers = [...defaultUsers]
  
  // 用registeredUsers中的用户覆盖defaultUsers中的同名用户
  registeredUsers.forEach((registeredUser: any) => {
    const existingIndex = allUsers.findIndex(u => u.username === registeredUser.username)
    if (existingIndex !== -1) {
      allUsers[existingIndex] = registeredUser
    } else {
      allUsers.push(registeredUser)
    }
  })
  
  return allUsers
}

// 更新用户密码
const updateUserPassword = (newPassword: string) => {
  const registeredUsers = JSON.parse(localStorage.getItem('registeredUsers') || '[]')
  const currentUser = userInfo.value
  
  // 查找是否已有注册记录
  const existingIndex = registeredUsers.findIndex((u: any) => u.username === currentUser.username && u.role === currentUser.role)
  
  if (existingIndex !== -1) {
    // 更新现有用户
    registeredUsers[existingIndex].password = newPassword
  } else {
    // 添加新用户记录
    registeredUsers.push({
      username: currentUser.username,
      password: newPassword,
      role: currentUser.role
    })
  }
  
  // 保存到localStorage
  localStorage.setItem('registeredUsers', JSON.stringify(registeredUsers))
}

// 居家贴士相关数据
const tipsSearchKeyword = ref('')
const selectedTips = ref<string[]>([])
const selectAllTips = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const showAddTipDialog = ref(false)

// 模拟居家贴士数据
const allTips = ref([
  {
    id: '1',
    title: '让生活空间焕然一新',
    intro: '厨房是家中油污的"重灾区"',
    content: '厨房是家中油污的"重灾区"，尤其是抽油烟机和灶台周围，容易积累顽固油污。清洁时，可以将小苏打与白醋混合，制成强力去污剂，喷洒在油污处，静置10分钟后用湿布擦拭，就能轻松去除油污。另外，定期清洁厨房不仅能保持卫生，还能延长电器使用寿命。',
    image: '/assets/tips1.jpg',
    publishTime: '2025-05-01 15:56:24',
    views: 34
  },
  {
    id: '2',
    title: '打造井井有条的家',
    intro: '衣物收纳要根据季节和使用频率进行分类',
    content: '衣物收纳要根据季节和使用频率进行分类。当季常穿的衣物放在容易取放的位置，过季衣物可以使用真空压缩袋收纳，既节省空间又能防潮防虫。对于小件物品如袜子、内衣等，可以使用分隔收纳盒，一目了然，方便取用。另外，定期整理衣物，将不再穿着的衣物捐赠或处理，能让衣柜保持整洁有序。',
    image: '/assets/tips2.jpg',
    publishTime: '2025-05-02 15:56:24',
    views: 7
  },
  {
    id: '3',
    title: '守护家人的平安',
    intro: '家中的电器使用频繁，用电安全不容忽视',
    content: '家中的电器使用频繁，用电安全不容忽视。首先，要选择质量合格的电器产品，避免使用假冒伪劣商品。其次，不要在同一插座上连接过多电器，以免造成过载。另外，定期检查电线绝缘层是否老化，发现问题及时更换。使用完电器后，最好拔掉电源插头，特别是在雷雨天气。浴室等潮湿环境使用电器更要格外小心，确保安装防水装置。',
    image: '/assets/tips3.jpg',
    publishTime: '2025-05-03 15:56:24',
    views: 2
  },
  {
    id: '4',
    title: '卫生间深度清洁指南',
    intro: '让卫生间作为家中使用频率最高的区域之一',
    content: '卫生间作为家中使用频率最高的区域之一，容易滋生细菌和产生异味。清洁卫生间时，可以使用专用的除菌清洁剂，对马桶、浴缸、洗手盆等进行彻底清洁。对于顽固的水渍和污垢，可以用柠檬切片擦拭，柠檬中的酸性物质能有效去除污渍。另外，保持卫生间通风干燥，使用排气扇或开窗通风，能有效减少细菌滋生和异味产生。',
    image: '/assets/tips4.jpg',
    publishTime: '2025-05-04 15:56:24',
    views: 2
  },
  {
    id: '5',
    title: '告别油烟，焕新美味空间',
    intro: '厨房是美食诞生的地方，却也是油烟的集中地',
    content: '厨房是美食诞生的地方，却也是油烟的集中地。为了保持厨房空气清新，除了安装质量好的抽油烟机外，还可以在厨房放置一些绿色植物，如绿萝、吊兰等，它们能吸收部分有害物质。另外，烹饪时尽量使用中小火，减少油烟的产生。使用完厨房后，及时关闭抽油烟机，但不要立即关闭，可以让它继续运行几分钟，确保油烟完全排出。定期清洁抽油烟机滤网，也是保持其高效工作的关键。',
    image: '/assets/tips5.jpg',
    publishTime: '2025-05-05 15:56:24',
    views: 0
  },
  {
    id: '6',
    title: '让阳台成为舒适的休闲空间',
    intro: '阳台不仅是晾晒衣物的地方',
    content: '阳台不仅是晾晒衣物的地方，还可以打造成舒适的休闲空间。首先，合理规划空间布局，将晾晒区与休闲区分开。可以选择折叠晾衣架，不使用时收起，增加活动空间。在休闲区放置舒适的座椅和小桌子，摆放一些盆栽植物，营造自然舒适的氛围。另外，选择防晒、防水的家具和装饰品，延长使用寿命。还可以安装窗帘或遮阳棚，调节光线，增加隐私性。',
    image: '/assets/tips6.jpg',
    publishTime: '2025-05-06 15:56:24',
    views: 5
  }
])

const filteredTips = ref([...allTips.value])
const displayedTips = ref([...filteredTips.value])

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(filteredTips.value.length / pageSize.value)
})

// 更新显示的贴士
const updateTipsDisplay = () => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  displayedTips.value = filteredTips.value.slice(start, end)
}

// 搜索贴士
const searchTips = () => {
  const keyword = tipsSearchKeyword.value.trim().toLowerCase()
  if (!keyword) {
    filteredTips.value = [...allTips.value]
  } else {
    filteredTips.value = allTips.value.filter(tip =>
      tip.title.toLowerCase().includes(keyword) ||
      tip.intro.toLowerCase().includes(keyword) ||
      tip.content.toLowerCase().includes(keyword)
    )
  }
  currentPage.value = 1
  updateTipsDisplay()
}

// 重置搜索
const resetTipsSearch = () => {
  tipsSearchKeyword.value = ''
  filteredTips.value = [...allTips.value]
  currentPage.value = 1
  updateTipsDisplay()
}

// 全选/取消全选
const toggleSelectAllTips = () => {
  if (selectAllTips.value) {
    selectedTips.value = filteredTips.value.map(tip => tip.id)
  } else {
    selectedTips.value = []
  }
}

// 批量删除
const batchDeleteTips = () => {
  if (confirm(`确定要删除选中的 ${selectedTips.value.length} 条贴士吗？`)) {
    allTips.value = allTips.value.filter(tip => !selectedTips.value.includes(tip.id))
    filteredTips.value = filteredTips.value.filter(tip => !selectedTips.value.includes(tip.id))
    selectedTips.value = []
    selectAllTips.value = false
    updateTipsDisplay()
  }
}

// 删除单个贴士
const deleteTip = (id: string) => {
  if (confirm('确定要删除这条贴士吗？')) {
    allTips.value = allTips.value.filter(tip => tip.id !== id)
    filteredTips.value = filteredTips.value.filter(tip => tip.id !== id)
    selectedTips.value = selectedTips.value.filter(tipId => tipId !== id)
    updateTipsDisplay()
  }
}

// 编辑贴士
const editTip = (tip: any) => {
  // 这里可以实现编辑功能，打开编辑对话框
  console.log('编辑贴士:', tip)
  // 实际项目中这里应该打开编辑模态框
}

// 查看贴士内容
const showTipContent = (tip: any) => {
  // 这里可以实现查看详细内容的功能
  console.log('查看贴士内容:', tip)
  // 实际项目中这里应该打开内容查看模态框
}

// 初始化显示数据
updateTipsDisplay()

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
// 使用之前声明的分页相关变量

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
  // 将日期时间字符串格式化为更易读的格式
  const date = new Date(dateTime)
  if (isNaN(date.getTime())) return dateTime
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
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
  selectAll.value = false
  selectedItems.value = {}
}

// 批量删除
const batchDelete = () => {
  const selectedIds = Object.entries(selectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => appointments.value[parseInt(index)]?.id)
    .filter(Boolean)
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的记录')
    return
  }
  
  if (confirm(`确定要删除选中的 ${selectedIds.length} 条记录吗？`)) {
    // 模拟删除操作
    console.log('批量删除:', selectedIds)
    // 这里应该调用API删除数据
    // 从本地数据中移除选中的项
    appointments.value = appointments.value.filter(item => !selectedIds.includes(item.id))
    alert('删除成功')
    // 清空选择
    selectAll.value = false
    selectedItems.value = {}
  }
}

// 分配服务
const assignService = (id: number) => {
  // 查找对应的预约记录
  const appointment = appointments.value.find(item => item.id === id)
  if (appointment && !appointment.assigned) {
    // 模拟分配操作
    console.log('分配服务:', id)
    // 更新预约状态
    appointment.assigned = true
    appointment.providerName = '家政服务者'
    appointment.servicePhone = '13800138000'
    appointment.status = 'processing'
    appointment.statusText = '处理中'
    alert(`服务预约 ID: ${id} 分配成功`)
  } else {
    alert('该服务已经被分配或不存在')
  }
}

// 查看详情
const viewDetail = (id: number) => {
  // 查找对应的预约记录
  const appointment = appointments.value.find(item => item.id === id)
  if (appointment) {
    // 模拟查看详情操作
    console.log('查看详情:', appointment)
    // 在实际项目中，这里应该打开详情模态框
    const detailInfo = `预约详情：\n` +
      `服务名称: ${appointment.serviceName}\n` +
      `数量: ${appointment.quantity}\n` +
      `总金额: ¥${appointment.totalAmount}\n` +
      `用户姓名: ${appointment.userName}\n` +
      `服务者: ${appointment.providerName || '未分配'}\n` +
      `联系电话: ${appointment.contactPhone}\n` +
      `联系地址: ${appointment.contactAddress || '未提供'}\n` +
      `服务状态: ${appointment.statusText}\n` +
      `开始时间: ${formatDateTime(appointment.startTime)}\n` +
      `结束时间: ${formatDateTime(appointment.endTime)}\n` +
      `预约时间: ${formatDateTime(appointment.appointmentTime)}`
    
    alert(detailInfo)
  } else {
    alert('未找到指定的预约记录')
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
  // 模拟新建操作
  console.log('新建服务')
  alert('新建服务功能开发中')
}

// 批量删除
const batchDeleteService = () => {
  const selectedIds = Object.entries(serviceSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => services.value[index]?.id)
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
    serviceSelectAll.value = false
    serviceSelectedItems.value = {}
  }
}

// 删除单条记录
const deleteService = (id: number) => {
  if (confirm('确定要删除这条记录吗？')) {
    // 模拟删除操作
    console.log('删除服务:', id)
    // 这里应该调用API删除数据
    alert('删除成功')
  }
}

// 编辑服务
const editService = (id: number) => {
  // 模拟编辑操作
  console.log('编辑服务:', id)
  alert('编辑服务功能开发中')
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
    .map(([index]) => parseInt(index))
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
  const selectedIds = Object.entries(withdrawalSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => allWithdrawals.value[index]?.id)
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
    selectAllWithdrawals.value = false
    withdrawalSelectedItems.value = {}
  }
}

// 删除单条记录
const deleteWithdrawal = (id: number) => {
  if (confirm('确定要删除这条记录吗？')) {
    // 模拟删除操作
    console.log('删除提现记录:', id)
    // 这里应该调用API删除数据
    alert('删除成功')
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
    .map(([index]) => parseInt(index))
})

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

// 编辑分类
const editCategory = (category: any) => {
  console.log('编辑分类:', category)
  alert('编辑分类功能开发中')
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

// 轮播图相关状态和方法
const carouselSearchKeyword = ref('')
const carouselSelectAll = ref(false)
const carouselSelectedItems = ref<Record<number, boolean>>({})
const carouselCurrentPage = ref(1)
const carouselPageSize = ref(10)
const showAddCarouselDialog = ref(false)
const carouselTotalItems = ref(2)
const carouselTotalPages = computed(() => Math.ceil(carouselTotalItems.value / carouselPageSize.value))
const selectedCarousels = computed(() => {
  return Object.entries(carouselSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => parseInt(index))
})

// 模拟轮播图数据
const allCarousels = ref([
  {
    id: 1,
    imageUrl: 'https://picsum.photos/seed/service1/300/150',
    serviceName: '全屋清洁 [大扫除]'
  },
  {
    id: 2,
    imageUrl: 'https://picsum.photos/seed/service2/300/150',
    serviceName: '2小时全屋日常保洁 [公司福利] 快速上门'
  }
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

// 切换全选
const toggleCarouselSelectAll = () => {
  filteredCarousels.value.forEach((_, index) => {
    carouselSelectedItems.value[index] = carouselSelectAll.value
  })
}

// 重置搜索
const resetCarouselSearch = () => {
  carouselSearchKeyword.value = ''
  carouselCurrentPage.value = 1
}

// 搜索轮播图
const searchCarousels = () => {
  carouselCurrentPage.value = 1
}

// 批量删除
const batchDeleteCarousels = () => {
  const selectedIds = Object.entries(carouselSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => allCarousels.value[index]?.id)
    .filter(Boolean)
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的轮播图')
    return
  }
  
  if (confirm(`确定要删除选中的 ${selectedIds.length} 张轮播图吗？`)) {
    // 模拟删除操作
    console.log('批量删除轮播图:', selectedIds)
    // 这里应该调用API删除数据
    alert('删除成功')
    // 清空选择
    carouselSelectAll.value = false
    carouselSelectedItems.value = {}
  }
}

// 编辑轮播图
const editCarousel = (carousel: any) => {
  console.log('编辑轮播图:', carousel)
  alert('编辑轮播图功能开发中')
}

// 删除单条轮播图
const deleteCarousel = (id: number) => {
  if (confirm('确定要删除这张轮播图吗？')) {
    // 模拟删除操作
    console.log('删除轮播图:', id)
    // 这里应该调用API删除数据
    alert('删除成功')
  }
}

// 切换页面
const changeCarouselPage = (page: number) => {
  if (page >= 1 && page <= carouselTotalPages.value) {
    carouselCurrentPage.value = page
    // 重置选择
    carouselSelectAll.value = false
    carouselSelectedItems.value = {}
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
  console.log('编辑公告:', notice)
  alert('编辑公告功能开发中')
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
    .map(([index]) => parseInt(index))
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
    .map(([index]) => allEvaluations.value[index]?.id)
    .filter(Boolean)
  
  if (selectedIds.length === 0) {
    alert('请选择要删除的评价')
    return
  }
  
  if (confirm(`确定要删除选中的 ${selectedIds.length} 条评价吗？`)) {
    // 模拟删除操作
    console.log('批量删除评价:', selectedIds)
    // 这里应该调用API删除数据
    alert('删除成功')
    // 清空选择
    evaluationSelectAll.value = false
    evaluationSelectedItems.value = {}
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

// 查看服务详情
const viewServiceDetail = (id: number) => {
  // 模拟查看详情操作
  console.log('查看服务详情:', id)
  alert('查看服务详情功能开发中')
}

// 服务者认证相关状态和方法
const providerSearchKeyword = ref('')
const providerSelectAll = ref(false)
const providerSelectedItems = ref<Record<number, boolean>>({})
const providerCurrentPage = ref(1)
const providerPageSize = ref(10)
const providerTotalItems = ref(4)
const providerTotalPages = computed(() => Math.ceil(providerTotalItems.value / providerPageSize.value))

// 模拟服务者认证数据
const providers = ref([
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

// 过滤后的服务者数据
const filteredProviders = computed(() => {
  let filtered = providers.value
  
  // 搜索过滤
  if (providerSearchKeyword.value) {
    filtered = filtered.filter(item => 
      item.name.toLowerCase().includes(providerSearchKeyword.value.toLowerCase())
    )
  }
  
  // 分页
  const startIndex = (providerCurrentPage.value - 1) * providerPageSize.value
  const endIndex = startIndex + providerPageSize.value
  return filtered.slice(startIndex, endIndex)
})

// 计算要显示的页码
const providerPagesToShow = computed(() => {
  const pages = []
  const total = providerTotalPages.value
  const current = providerCurrentPage.value
  
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
const toggleProviderSelectAll = () => {
  filteredProviders.value.forEach((item, index) => {
    providerSelectedItems.value[index] = providerSelectAll.value
  })
}

// 重置搜索
const resetProviderSearch = () => {
  providerSearchKeyword.value = ''
  providerCurrentPage.value = 1
}

// 批量删除
const batchDeleteProvider = () => {
  const selectedIds = Object.entries(providerSelectedItems.value)
    .filter(([_, selected]) => selected)
    .map(([index]) => providers.value[index]?.id)
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
    providerSelectAll.value = false
    providerSelectedItems.value = {}
  }
}

// 审核服务者
const reviewProvider = (id: number) => {
  // 模拟审核操作
  console.log('审核服务者:', id)
  alert('审核服务者功能开发中')
}

// 删除服务者
const deleteProvider = (id: number) => {
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
    providerCurrentPage.value = page
    // 重置选择
    providerSelectAll.value = false
    providerSelectedItems.value = {}
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
  if (confirm('确定要退出登录吗？')) {
    // 清除用户信息
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('avatar')
    localStorage.removeItem('adminUserInfo')
    localStorage.removeItem('adminAvatar')
    userInfo.value = {
      username: '',
      role: '',
      name: '',
      email: '',
      phone: '',
      createTime: ''
    }
    userAvatar.value = ''
    // 跳转到登录页
    router.push('/login')
  }
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

/* 个人资料模态框样式 */
.avatar-section {
  text-align: center;
  margin-bottom: 24px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.avatar-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #e0e0e0;
}

.avatar-placeholder {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  border: 3px solid #e0e0e0;
}

.avatar-upload-hint {
  position: absolute;
  bottom: -24px;
  left: 0;
  right: 0;
  font-size: 12px;
  color: #666;
}

/* 下拉菜单样式优化 */
.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 4px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  z-index: 1000;
  min-width: 180px;
}

.dropdown-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  border-bottom: 1px solid #f0f0f0;
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
}

/* 用户信息显示样式 */
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.user-info:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 8px;
}

.username {
  font-size: 14px;
  font-weight: 500;
  margin-right: 8px;
}

.dropdown-arrow {
  font-size: 12px;
  transition: transform 0.2s ease;
}

.dropdown-arrow.active {
  transform: rotate(180deg);
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
  white-space: nowrap;
}

.reset-btn {
  background: #ffc107;
  color: #333;
  white-space: nowrap;
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

.assigned-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.assigned {
  background: #d4edda;
  color: #155724;
}

.unassigned {
  background: #f8d7da;
  color: #721c24;
}

.operation-cell {
  display: flex;
  gap: 5px;
  white-space: nowrap;
}

.assign-btn {
  background: #28a745;
  color: white;
  padding: 4px 8px;
  font-size: 12px;
}

.detail-btn {
  background: #6c757d;
  color: white;
  padding: 4px 8px;
  font-size: 12px;
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
  background-color: #007bff;
  color: white;
  white-space: nowrap;
}

.withdrawal-management .reset-btn {
  background-color: #ffc107;
  color: #333;
  white-space: nowrap;
}

.withdrawal-management .delete-btn {
  background-color: #dc3545;
  color: white;
}

.withdrawal-management .delete-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
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
  background: #007bff;
  color: #fff;
  white-space: nowrap;
}

.category-search-area .search-btn:hover {
  background: #66b1ff;
}

.category-search-area .reset-btn {
  background: #ffc107;
  color: #333;
  border: 1px solid #ffc107;
  white-space: nowrap;
}

.category-search-area .reset-btn:hover {
  color: #409eff;
  border-color: #c6e2ff;
}

.category-search-area .add-btn {
  background: #28a745;
  color: #fff;
}

.category-search-area .add-btn:hover {
  background: #218838;
}

.category-search-area .batch-delete-btn {
  background: #dc3545;
  color: #fff;
}

.category-search-area .batch-delete-btn:hover {
  background: #c82333;
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
  background: #ffc107;
  color: #212529;
}

.category-table .edit-btn:hover {
  background: #e0a800;
}

.category-table .delete-btn {
  background: #dc3545;
  color: #fff;
}

.category-table .delete-btn:hover {
  background: #c82333;
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
  background: #007bff;
  color: #fff;
  white-space: nowrap;
}

.carousel-search-area .search-btn:hover {
  background: #66b1ff;
}

.carousel-search-area .reset-btn {
  background: #ffc107;
  color: #333;
  border: 1px solid #ffc107;
  white-space: nowrap;
}

.carousel-search-area .reset-btn:hover {
  color: #409eff;
  border-color: #c6e2ff;
}

.carousel-search-area .add-btn {
  background: #28a745;
  color: #fff;
}

.carousel-search-area .add-btn:hover {
  background: #218838;
}

.carousel-search-area .batch-delete-btn {
  background: #dc3545;
  color: #fff;
}

.carousel-search-area .batch-delete-btn:hover {
  background: #c82333;
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
  background: #ffc107;
  color: #212529;
}

.carousel-table .edit-btn:hover {
  background: #e0a800;
}

.carousel-table .delete-btn {
  background: #dc3545;
  color: #fff;
}

.carousel-table .delete-btn:hover {
  background: #c82333;
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
  gap: 10px;
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
  background-color: #007bff;
  color: white;
  white-space: nowrap;
}

.notice-management .btn.search-btn:hover:not(:disabled) {
  background-color: #66b1ff;
}

.notice-management .btn.reset-btn {
  background-color: #ffc107;
  color: #333;
  border: 1px solid #ffc107;
  white-space: nowrap;
}

.notice-management .btn.reset-btn:hover:not(:disabled) {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

.notice-management .btn.add-btn {
  background-color: #28a745;
  color: white;
}

.notice-management .btn.add-btn:hover:not(:disabled) {
  background-color: #218838;
}

.notice-management .btn.edit-btn {
  background-color: #ffc107;
  color: #212529;
}

.notice-management .btn.edit-btn:hover:not(:disabled) {
  background-color: #e0a800;
}

.notice-management .btn.delete-btn {
  background-color: #dc3545;
  color: white;
}

.notice-management .btn.delete-btn:hover:not(:disabled) {
  background-color: #c82333;
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
  background-color: #007bff;
  color: white;
  border-color: #007bff;
  white-space: nowrap;
}

.evaluation-management .btn.search-btn:hover:not(:disabled) {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.evaluation-management .btn.reset-btn {
  background-color: #ffc107;
  color: #333;
  border-color: #ffc107;
  white-space: nowrap;
}

.evaluation-management .btn.reset-btn:hover:not(:disabled) {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

.evaluation-management .btn.delete-btn {
  background-color: #dc3545;
  color: white;
  border-color: #dc3545;
}

.evaluation-management .btn.delete-btn:hover:not(:disabled) {
  background-color: #c82333;
  border-color: #c82333;
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
  background: #28a745;
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
</style>
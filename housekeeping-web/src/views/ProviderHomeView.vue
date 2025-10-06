<template>
  <div class="provider-home-container">
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
            <span class="username">{{ profileForm.name || userInfo.username }}</span>
            <div class="dropdown-arrow" :class="{ active: showUserDropdown }">▼</div>
          </div>
          <!-- 用户下拉菜单 -->
          <div v-if="showUserDropdown" class="user-dropdown" @click.stop>
            <div class="dropdown-item" @click="handleServiceCertification">
              <span>服务者认证</span>
            </div>
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
          <li class="nav-item" :class="{ active: activeMenu === 'info' }" @click="toggleInfoMenu">
            <div class="nav-icon">📋</div>
            <span class="nav-text">信息管理</span>
            <div class="expand-arrow" :class="{ expanded: isInfoMenuExpanded }">▲</div>
          </li>
          <!-- 信息管理子菜单 -->
          <div v-show="isInfoMenuExpanded" class="sub-menu">
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
            <li class="nav-item sub-item" :class="{ active: activeMenu === 'evaluation' }" @click="setActiveMenu('evaluation')">
              <div class="nav-icon">⭐</div>
              <span class="nav-text">服务评价</span>
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
              <span>您好! {{ userInfo.username }}, 欢迎使用本系统!</span>
            </div>
            <div class="status-badge">
              <div class="status-icon">✓</div>
              <span class="status-text">家政服务者已认证</span>
            </div>
          </div>

          <!-- 系统公告区域 -->
          <div class="announcements-section">
            <h2 class="section-title">系统公告</h2>
            <div class="announcements-list">
              <div class="announcement-item">
                <div class="announcement-bullet"></div>
                <div class="announcement-content">
                  <div class="announcement-title">深度保洁特惠</div>
                  <div class="announcement-description">
                    即日起,本家政推出深度保洁特惠!专业团队携带环保清洁剂,对全屋进行除尘、杀菌、除螨,沙发、地毯深度清洁焕新。新客优惠幅度更大,让家焕然一新!
                  </div>
                  <div class="announcement-time">2025-05-11 15:51:17</div>
                </div>
              </div>
              
              <div class="announcement-item">
                <div class="announcement-bullet"></div>
                <div class="announcement-content">
                  <div class="announcement-title">家电清洗福利</div>
                  <div class="announcement-description">
                    家电久未清洗易藏污垢!现开展家电清洗福利,空调、油烟机、洗衣机等清洗优惠,专业工具+消毒杀菌,保障家电高效运行。活动期间下单赠清洁小工具。
                  </div>
                  <div class="announcement-time">2025-05-11 15:51:17</div>
                </div>
              </div>
              
              <div class="announcement-item">
                <div class="announcement-bullet"></div>
                <div class="announcement-content">
                  <div class="announcement-title">新居开荒保洁特惠</div>
                  <div class="announcement-description">
                    新房开荒不用愁!专业团队采用环保药剂,高效去除装修残留污渍,细致清洁玻璃、地板、厨卫。活动期间,还可免费赠送局部消毒服务,快来预约,迎接清爽新家。
                  </div>
                  <div class="announcement-time">2025-05-11 15:51:17</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 服务预约内容 -->
        <div v-if="activeMenu === 'appointment'" class="content-section">
          <!-- 搜索栏 -->
          <div class="appointment-search-bar">
            <input 
              type="text" 
              placeholder="请输入服务名称查询" 
              class="appointment-search-input" 
              v-model="appointmentSearchKeyword"
              @keyup.enter="searchAppointments"
              @input="searchAppointments"
            >
            <button class="appointment-search-btn" @click="searchAppointments">查询</button>
            <button class="appointment-reset-btn" @click="resetAppointmentSearch">重置</button>
          </div>
          
          <!-- 预约数据表格 -->
          <div class="appointment-table-container">
            <table class="appointment-table">
              <thead>
                <tr>
                  <th>服务名称</th>
                  <th>数量</th>
                  <th>总金额</th>
                  <th>用户</th>
                  <th>服务者</th>
                  <th>联系电话</th>
                  <th>联系地址</th>
                  <th>服务者电话</th>
                  <th>状态</th>
                  <th>开始时间</th>
                  <th>结束时间</th>
                  <th>预约时间</th>
                  <th>服务</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="paginatedAppointments.length === 0">
                  <td colspan="13" class="no-data">暂无数据</td>
                </tr>
                <tr v-else v-for="appointment in paginatedAppointments" :key="appointment.id">
                  <td>{{ appointment.serviceName }}</td>
                  <td>{{ appointment.quantity }}</td>
                  <td class="amount">¥{{ appointment.totalAmount }}</td>
                  <td>{{ appointment.user }}</td>
                  <td>{{ appointment.serviceProvider }}</td>
                  <td>{{ appointment.contactPhone }}</td>
                  <td>{{ appointment.contactAddress }}</td>
                  <td>{{ appointment.serviceProviderPhone }}</td>
                  <td>
                    <span class="status" :class="appointment.status">
                      {{ 
                        appointment.status === 'completed' ? '已完成' : 
                        appointment.status === 'pending' ? '待分配' : 
                        appointment.status === 'in-service' ? '服务中' :
                        appointment.status === 'cancelled' ? '已取消' : '未知状态'
                      }}
                    </span>
                  </td>
                  <td>{{ appointment.startTime }}</td>
                  <td>{{ appointment.endTime || '-' }}</td>
                  <td>{{ appointment.appointmentTime }}</td>
                  <td class="service-cell">
                    <button class="service-btn" @click="handleService(appointment)"
                            :class="{ 'delete-btn': ['in-service', 'completed', 'cancelled'].includes(appointment.status) }">
                      {{ 
                        appointment.status === 'pending' ? '接受' : '删除'
                      }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <!-- 分页 -->
          <div class="appointment-pagination">
            <div class="pagination-info">共{{ filteredAppointments.length }}条，第{{ currentPage }}/{{ totalPages || 1 }}页</div>
            <div class="pagination-controls">
              <button class="page-btn" :disabled="currentPage === 1" @click="prevPage">‹</button>
              <button 
                v-for="page in totalPages" 
                :key="page"
                class="page-btn" 
                :class="{ active: page === currentPage }"
                @click="goToPage(page)"
              >
                {{ page }}
              </button>
              <button class="page-btn" :disabled="currentPage === totalPages" @click="nextPage">›</button>
            </div>
          </div>
        </div>

        <!-- 提现记录内容 -->
        <div v-if="activeMenu === 'withdrawal'" class="content-section">
          <!-- 搜索栏 -->
          <div class="withdrawal-search-bar">
            <input 
              type="date" 
              class="withdrawal-date-input" 
              v-model="withdrawalDate"
            >
            <button class="withdrawal-search-btn" @click="searchWithdrawals">查询</button>
            <button class="withdrawal-reset-btn" @click="resetWithdrawalSearch">重置</button>
          </div>
          
          <!-- 提现按钮 -->
          <div class="withdrawal-action">
            <button class="withdrawal-btn" @click="showWithdrawalModal">提现</button>
          </div>
          
          <!-- 提现记录列表 -->
          <div class="withdrawal-records">
            <div class="withdrawal-record" v-for="record in filteredWithdrawals" :key="record.id">
              <div class="record-amount">¥{{ record.amount }}</div>
              <div class="record-time">{{ record.withdrawalTime }}</div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div class="withdrawal-pagination">
            <div class="pagination-info">共{{ filteredWithdrawals.length }}条</div>
            <div class="pagination-controls">
              <button class="page-btn">‹</button>
              <button class="page-btn active">1</button>
              <button class="page-btn">›</button>
            </div>
          </div>
        </div>

        <!-- 服务评价内容 -->
        <div v-if="activeMenu === 'evaluation'" class="content-section">
          <!-- 搜索栏 -->
          <div class="evaluation-search-bar">
            <input 
              type="text" 
              placeholder="请输入服务名称查询" 
              class="evaluation-search-input" 
              v-model="evaluationSearchKeyword"
              @keyup.enter="searchEvaluations"
              @input="searchEvaluations"
            >
            <button class="evaluation-search-btn" @click="searchEvaluations">查询</button>
            <button class="evaluation-reset-btn" @click="resetEvaluationSearch">重置</button>
          </div>
          
          <!-- 评价数据表格 -->
          <div class="evaluation-table-container">
            <table class="evaluation-table">
              <thead>
                <tr>
                  <th>服务者</th>
                  <th>服务名称</th>
                  <th>用户</th>
                  <th>评分</th>
                  <th>内容</th>
                  <th>评价时间</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="evaluation in filteredEvaluations" :key="evaluation.id">
                  <td>{{ evaluation.serviceProvider }}</td>
                  <td>{{ evaluation.serviceName }}</td>
                  <td>{{ evaluation.user }}</td>
                  <td>
                    <div class="rating-display">
                      <div class="stars">
                        <span 
                          v-for="i in 5" 
                          :key="i" 
                          class="star"
                          :class="{ 
                            'full': i <= Math.floor(evaluation.rating),
                            'half': i === Math.ceil(evaluation.rating) && evaluation.rating % 1 !== 0
                          }"
                        >★</span>
                      </div>
                      <span class="rating-number">{{ evaluation.rating }}</span>
                    </div>
                  </td>
                  <td>{{ evaluation.content }}</td>
                  <td>{{ evaluation.evaluationTime }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <!-- 分页 -->
          <div class="evaluation-pagination">
            <div class="pagination-info">共{{ filteredEvaluations.length }}条</div>
            <div class="pagination-controls">
              <button class="page-btn">‹</button>
              <button class="page-btn active">1</button>
              <button class="page-btn">›</button>
            </div>
          </div>
        </div>

        <!-- 家政服务内容 -->
        <div v-if="activeMenu === 'services'" class="content-section">
          <!-- 搜索栏 -->
          <div class="services-search-bar">
            <input 
              type="text" 
              placeholder="请输入名称查询" 
              class="services-search-input" 
              v-model="servicesSearchKeyword"
              @keyup.enter="searchServices"
              @input="searchServices"
            >
            <button class="services-search-btn" @click="searchServices">查询</button>
            <button class="services-reset-btn" @click="resetServicesSearch">重置</button>
          </div>
          
          <!-- 服务数据表格 -->
          <div class="services-table-container">
            <table class="services-table">
              <thead>
                <tr>
                  <th>图片</th>
                  <th>名称</th>
                  <th>价格</th>
                  <th>单位</th>
                  <th>销量</th>
                  <th>人气</th>
                  <th>分类</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="paginatedServices.length === 0">
                  <td colspan="8" class="no-data">暂无服务数据</td>
                </tr>
                <tr v-else v-for="service in paginatedServices" :key="service.id">
                  <td>
                    <div class="service-image">
                      <div class="image-placeholder">
                        <div class="image-text">{{ service.imageText }}</div>
                        <div class="image-subtitle">极速上门 品质服务</div>
                      </div>
                    </div>
                  </td>
                  <td>{{ service.name }}</td>
                  <td class="price">¥{{ service.price }}</td>
                  <td>{{ service.unit }}</td>
                  <td>{{ service.sales }}</td>
                  <td>{{ service.popularity }}</td>
                  <td>{{ service.category }}</td>
                  <td class="service-actions">
                    <button class="view-btn" @click="viewServiceDetails(service)">查看</button>
                    <button class="edit-btn" @click="editService(service)">编辑</button>
                    <button class="delete-btn" @click="deleteService(service)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <!-- 分页 -->
          <div class="services-pagination">
            <div class="pagination-info">共{{ filteredServices.length }}条，第{{ servicesCurrentPage }}/{{ servicesTotalPages || 1 }}页</div>
            <div class="pagination-controls">
              <button class="page-btn" :disabled="servicesCurrentPage === 1" @click="servicesPrevPage">‹</button>
              <button 
                v-for="page in servicesTotalPages" 
                :key="page"
                class="page-btn" 
                :class="{ active: page === servicesCurrentPage }"
                @click="servicesGoToPage(page)"
              >
                {{ page }}
              </button>
              <button class="page-btn" :disabled="servicesCurrentPage === servicesTotalPages" @click="servicesNextPage">›</button>
            </div>
          </div>
        </div>

        <!-- 服务者认证内容 -->
        <div v-if="activeMenu === 'certification'" class="content-section">
          <div class="certification-container">
            <h2 class="certification-title">服务者认证</h2>
            
            <form class="certification-form" @submit.prevent="submitCertification">
              <!-- 资格证书 -->
              <div class="form-group">
                <label class="form-label">
                  资格证书 <span class="required">*</span>
                </label>
                <div class="file-upload-area" @click="triggerFileUpload('qualification')">
                  <div class="upload-placeholder">
                    <div class="upload-icon">📄</div>
                    <div class="upload-text">点击上传资格证书</div>
                    <div class="upload-hint">支持 JPG、PNG 格式，大小不超过 5MB</div>
                  </div>
                  <input 
                    ref="qualificationInput"
                    type="file" 
                    accept="image/*" 
                    @change="handleFileUpload($event, 'qualification')"
                    style="display: none"
                  >
                </div>
                <div v-if="certificationForm.qualificationFile" class="file-preview">
                  <span class="file-name">{{ certificationForm.qualificationFile.name }}</span>
                  <button type="button" class="remove-file" @click="removeFile('qualification')">×</button>
                </div>
              </div>

              <!-- 身份证 -->
              <div class="form-group">
                <label class="form-label">
                  身份证 <span class="required">*</span>
                </label>
                <div class="file-upload-area" @click="triggerFileUpload('idCard')">
                  <div class="upload-placeholder">
                    <div class="upload-icon">🆔</div>
                    <div class="upload-text">点击上传身份证</div>
                    <div class="upload-hint">支持 JPG、PNG 格式，大小不超过 5MB</div>
                  </div>
                  <input 
                    ref="idCardInput"
                    type="file" 
                    accept="image/*" 
                    @change="handleFileUpload($event, 'idCard')"
                    style="display: none"
                  >
                </div>
                <div v-if="certificationForm.idCardFile" class="file-preview">
                  <span class="file-name">{{ certificationForm.idCardFile.name }}</span>
                  <button type="button" class="remove-file" @click="removeFile('idCard')">×</button>
                </div>
              </div>

              <!-- 身份证号码 -->
              <div class="form-group">
                <label class="form-label">
                  身份证号码 <span class="required">*</span>
                </label>
                <input 
                  type="text" 
                  v-model="certificationForm.idNumber"
                  placeholder="身份证号码"
                  class="form-input"
                  maxlength="18"
                >
              </div>

              <!-- 联系方式 -->
              <div class="form-group">
                <label class="form-label">
                  联系方式 <span class="required">*</span>
                </label>
                <input 
                  type="text" 
                  v-model="certificationForm.contact"
                  placeholder="联系方式"
                  class="form-input"
                  maxlength="20"
                >
              </div>

              <!-- 家庭住址 -->
              <div class="form-group">
                <label class="form-label">
                  家庭住址 <span class="required">*</span>
                </label>
                <textarea 
                  v-model="certificationForm.address"
                  placeholder="家庭住址"
                  class="form-textarea"
                  rows="4"
                ></textarea>
              </div>

              <!-- 提交按钮 -->
              <div class="form-actions">
                <button type="submit" class="submit-btn">提交</button>
              </div>
            </form>
          </div>
        </div>

        <!-- 个人资料内容 -->
        <div v-if="activeMenu === 'profile'" class="content-section">
          <div class="profile-container">
            <h2 class="profile-title">个人资料</h2>
            
            <form class="profile-form" @submit.prevent="submitProfile">
              <!-- 头像 -->
              <div class="form-group avatar-group">
                <label class="form-label">头像</label>
                <div class="avatar-section">
                  <div class="avatar-container" @click="triggerAvatarUpload">
                    <img 
                      v-if="profileForm.avatar" 
                      :src="profileForm.avatar" 
                      alt="头像" 
                      class="avatar-image"
                    >
                    <div v-else class="avatar-placeholder">
                      <div class="avatar-icon">👤</div>
                    </div>
                    <input 
                      ref="avatarInput"
                      type="file" 
                      accept="image/*" 
                      @change="handleAvatarUpload"
                      style="display: none"
                    >
                  </div>
                </div>
              </div>

              <!-- 用户名 -->
              <div class="form-group">
                <label class="form-label">用户名</label>
                <input 
                  type="text" 
                  v-model="profileForm.username"
                  class="form-input readonly"
                  readonly
                >
              </div>

              <!-- 姓名 -->
              <div class="form-group">
                <label class="form-label">姓名</label>
                <input 
                  type="text" 
                  v-model="profileForm.name"
                  placeholder="请输入姓名"
                  class="form-input"
                  maxlength="20"
                >
              </div>

              <!-- 电话 -->
              <div class="form-group">
                <label class="form-label">电话</label>
                <input 
                  type="tel" 
                  v-model="profileForm.phone"
                  placeholder="请输入电话号码"
                  class="form-input"
                  maxlength="11"
                >
              </div>

              <!-- 邮箱 -->
              <div class="form-group">
                <label class="form-label">邮箱</label>
                <input 
                  type="email" 
                  v-model="profileForm.email"
                  placeholder="请输入邮箱地址"
                  class="form-input"
                  maxlength="50"
                >
              </div>

              <!-- 账户余额 -->
              <div class="form-group">
                <label class="form-label">账户余额</label>
                <div class="balance-display">
                  <span class="balance-amount">{{ profileForm.balance }}元</span>
                </div>
              </div>

              <!-- 保存按钮 -->
              <div class="form-actions">
                <button type="submit" class="save-btn">保存</button>
              </div>
            </form>
          </div>
        </div>

        <!-- 修改密码内容 -->
        <div v-if="activeMenu === 'changePassword'" class="content-section">
          <div class="change-password-container">
            <h2 class="change-password-title">修改密码</h2>
            
            <form class="change-password-form" @submit.prevent="submitChangePassword">
              <!-- 原密码 -->
              <div class="form-group">
                <label class="form-label">
                  原密码 <span class="required">*</span>
                </label>
                <div class="password-input-wrapper">
                  <input 
                    :type="showOldPassword ? 'text' : 'password'"
                    v-model="changePasswordForm.oldPassword"
                    placeholder="请输入原密码"
                    class="form-input password-input"
                    maxlength="20"
                  >
                  <button 
                    type="button" 
                    class="password-toggle"
                    @click="togglePasswordVisibility('old')"
                  >
                    <span v-if="showOldPassword">👁️</span>
                    <span v-else>👁️‍🗨️</span>
                  </button>
                </div>
              </div>

              <!-- 新密码 -->
              <div class="form-group">
                <label class="form-label">
                  新密码 <span class="required">*</span>
                </label>
                <div class="password-input-wrapper">
                  <input 
                    :type="showNewPassword ? 'text' : 'password'"
                    v-model="changePasswordForm.newPassword"
                    placeholder="请输入新密码"
                    class="form-input password-input"
                    maxlength="20"
                  >
                  <button 
                    type="button" 
                    class="password-toggle"
                    @click="togglePasswordVisibility('new')"
                  >
                    <span v-if="showNewPassword">👁️</span>
                    <span v-else>👁️‍🗨️</span>
                  </button>
                </div>
              </div>

              <!-- 确认密码 -->
              <div class="form-group">
                <label class="form-label">确认密码</label>
                <div class="password-input-wrapper">
                  <input 
                    :type="showConfirmPassword ? 'text' : 'password'"
                    v-model="changePasswordForm.confirmPassword"
                    placeholder="请确认新密码"
                    class="form-input password-input"
                    maxlength="20"
                  >
                  <button 
                    type="button" 
                    class="password-toggle"
                    @click="togglePasswordVisibility('confirm')"
                  >
                    <span v-if="showConfirmPassword">👁️</span>
                    <span v-else>👁️‍🗨️</span>
                  </button>
                </div>
              </div>

              <!-- 保存按钮 -->
              <div class="form-actions">
                <button type="submit" class="save-btn">保存</button>
              </div>
            </form>
          </div>
        </div>
      </main>
    </div>

    <!-- 发起提现模态框 -->
    <div v-if="showWithdrawalModalFlag" class="modal-overlay" @click="closeWithdrawalModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>发起提现</h3>
          <button class="close-btn" @click="closeWithdrawalModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>金额 <span class="required">*</span></label>
            <div class="amount-input-group">
              <button class="amount-btn minus" @click="decreaseAmount">-</button>
              <input 
                type="number" 
                v-model="withdrawalForm.amount" 
                class="amount-input" 
                placeholder="金额"
                min="0"
                step="0.01"
              >
              <button class="amount-btn plus" @click="increaseAmount">+</button>
            </div>
          </div>
          
          <div class="form-group">
            <label>账号 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="withdrawalForm.account" 
              class="form-input" 
              placeholder="账号"
            >
          </div>
          
          <div class="form-group">
            <label>账户类型 <span class="required">*</span></label>
            <div class="account-type-group">
              <label class="account-type-option" :class="{ active: withdrawalForm.accountType === 'wechat' }">
                <input 
                  type="radio" 
                  v-model="withdrawalForm.accountType" 
                  value="wechat"
                  class="account-type-radio"
                >
                <div class="account-type-content">
                  <div class="account-type-icon wechat">💚</div>
                  <span>微信支付</span>
                </div>
              </label>
              
              <label class="account-type-option" :class="{ active: withdrawalForm.accountType === 'alipay' }">
                <input 
                  type="radio" 
                  v-model="withdrawalForm.accountType" 
                  value="alipay"
                  class="account-type-radio"
                >
                <div class="account-type-content">
                  <div class="account-type-icon alipay">🔵</div>
                  <span>支付宝</span>
                </div>
              </label>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeWithdrawalModal">取消</button>
          <button class="btn btn-primary" @click="submitWithdrawal">保存</button>
        </div>
      </div>
    </div>


    <!-- 个人资料模态框 -->
    <div v-if="showPersonalProfileModal" class="modal-overlay" @click="cancelPersonalProfile">
      <div class="modal-content personal-profile-modal" @click.stop>
        <div class="modal-header">
          <h3>个人资料</h3>
          <button class="close-btn" @click="cancelPersonalProfile">×</button>
        </div>
        <div class="modal-body">
          <form class="profile-form" @submit.prevent="saveProfile">
            <!-- 头像 -->
            <div class="form-group avatar-group">
              <label class="form-label">头像</label>
              <div class="avatar-section">
                <div class="avatar-container" @click="triggerAvatarUpload">
                  <img 
                    v-if="profileForm.avatar" 
                    :src="profileForm.avatar" 
                    alt="头像" 
                    class="avatar-image"
                  >
                  <div v-else class="avatar-placeholder">
                    <div class="avatar-icon">👤</div>
                  </div>
                  <input 
                    ref="avatarInput"
                    type="file" 
                    accept="image/*" 
                    @change="handleAvatarUpload"
                    style="display: none"
                  >
                </div>
              </div>
            </div>
            
            <!-- 姓名 -->
            <div class="form-group">
              <label class="form-label">姓名</label>
              <input 
                type="text" 
                v-model="profileForm.name"
                placeholder="请输入姓名"
                class="form-input"
                maxlength="20"
              >
            </div>
            
            <!-- 电话 -->
            <div class="form-group">
              <label class="form-label">电话</label>
              <input 
                type="tel" 
                v-model="profileForm.phone"
                placeholder="请输入电话号码"
                class="form-input"
                maxlength="11"
              >
            </div>
            
            <!-- 邮箱 -->
            <div class="form-group">
              <label class="form-label">邮箱</label>
              <input 
                type="email" 
                v-model="profileForm.email"
                placeholder="请输入邮箱地址"
                class="form-input"
                maxlength="50"
              >
            </div>
            
            <!-- 服务类型 -->
            <div class="form-group">
              <label class="form-label">服务类型</label>
              <input 
                type="text" 
                v-model="profileForm.serviceType"
                placeholder="请输入服务类型"
                class="form-input"
                maxlength="50"
              >
            </div>
            
            <!-- 个人简介 -->
            <div class="form-group">
              <label class="form-label">个人简介</label>
              <textarea 
                v-model="profileForm.bio"
                placeholder="请输入个人简介"
                class="form-textarea"
                maxlength="200"
                rows="4"
              ></textarea>
            </div>
            
            <!-- 保存按钮 -->
            <div class="form-actions">
              <button type="submit" class="save-btn">保存</button>
            </div>
          </form>
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
          <form class="change-password-form" @submit.prevent="saveChangePassword">
            <div class="form-group">
              <label class="form-label">原密码</label>
              <input 
                type="password" 
                v-model="changePasswordForm.oldPassword"
                placeholder="请输入原密码"
                class="form-input"
                maxlength="20"
              >
            </div>
            <div class="form-group">
              <label class="form-label">新密码</label>
              <input 
                type="password" 
                v-model="changePasswordForm.newPassword"
                placeholder="请输入新密码"
                class="form-input"
                maxlength="20"
              >
              <div class="password-hint">密码至少包含8个字符，包含字母和数字</div>
            </div>
            <div class="form-group">
              <label class="form-label">确认新密码</label>
              <input 
                type="password" 
                v-model="changePasswordForm.confirmPassword"
                placeholder="请再次输入新密码"
                class="form-input"
                maxlength="20"
              >
            </div>
          </form>
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
  username: '家政小丁',
  role: 'provider'
})

// 当前激活的菜单
const activeMenu = ref('home')

// 信息管理菜单展开状态
const isInfoMenuExpanded = ref(false)

// 用户下拉菜单显示状态
const showUserDropdown = ref(false)

// 模态框显示状态
const showPersonalProfileModal = ref(false)
const showChangePasswordModal = ref(false)

// 用户头像
const userAvatar = ref('')

// 服务预约搜索关键词
const appointmentSearchKeyword = ref('')

// 预约数据
const allAppointments = ref([
  {
    id: 1,
    serviceName: '新房精细开荒1平米【包运送垃圾/包验收】',
    quantity: 1,
    totalAmount: 9.9,
    user: '小张',
    serviceProvider: '家政小丁',
    contactPhone: '13988991122',
    contactAddress: '枫叶社区1号楼',
    serviceProviderPhone: '13988997788',
    status: 'completed',
    startTime: '2025-05-18 10:00:00',
    endTime: '2025-05-18 17:30:00',
    appointmentTime: '2025-05-15 22:31:18'
  },
  {
    id: 2,
    serviceName: '沙发保养清洁【包含皮革养护】',
    quantity: 1,
    totalAmount: 50,
    user: '李女士',
    serviceProvider: '家政小丁',
    contactPhone: '13988993344',
    contactAddress: '北京市朝阳区建国路88号',
    serviceProviderPhone: '13988997788',
    status: 'completed',
    startTime: '2025-05-15 10:00:00',
    endTime: '2025-05-15 14:00:00',
    appointmentTime: '2025-05-14 22:42:33'
  },
  {
    id: 3,
    serviceName: '全屋深度保洁3小时',
    quantity: 1,
    totalAmount: 199,
    user: '王先生',
    serviceProvider: '家政小丁',
    contactPhone: '13988995566',
    contactAddress: '海淀区中关村南大街5号',
    serviceProviderPhone: '13988997788',
    status: 'in-service',
    startTime: '2025-05-20 09:00:00',
    endTime: '',
    appointmentTime: '2025-05-19 15:30:22'
  },
  {
    id: 4,
    serviceName: '厨房油烟机深度清洗',
    quantity: 1,
    totalAmount: 120,
    user: '赵女士',
    serviceProvider: '家政小丁',
    contactPhone: '13988997788',
    contactAddress: '西城区德胜门外大街12号',
    serviceProviderPhone: '13988997788',
    status: 'pending',
    startTime: '2025-05-21 14:00:00',
    endTime: '',
    appointmentTime: '2025-05-20 09:15:47'
  },
  {
    id: 5,
    serviceName: '空调清洗保养',
    quantity: 2,
    totalAmount: 160,
    user: '刘先生',
    serviceProvider: '家政小丁',
    contactPhone: '13988999900',
    contactAddress: '丰台区丰台路66号',
    serviceProviderPhone: '13988997788',
    status: 'pending',
    startTime: '2025-05-22 10:00:00',
    endTime: '',
    appointmentTime: '2025-05-20 16:45:12'
  },
  {
    id: 6,
    serviceName: '地毯深度清洁除螨',
    quantity: 3,
    totalAmount: 270,
    user: '陈女士',
    serviceProvider: '家政小丁',
    contactPhone: '13988881122',
    contactAddress: '朝阳区三里屯路19号',
    serviceProviderPhone: '13988997788',
    status: 'cancelled',
    startTime: '2025-05-18 13:00:00',
    endTime: '',
    appointmentTime: '2025-05-17 20:30:55'
  }
])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(5)

// 过滤后的预约数据
const filteredAppointments = ref([...allAppointments.value])

// 计算当前页的数据
const paginatedAppointments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredAppointments.value.slice(start, end)
})

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(filteredAppointments.value.length / pageSize.value)
})

// 上一页
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

// 下一页
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

// 跳转到指定页
const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

// 提现记录搜索日期
const withdrawalDate = ref('')

// 提现记录数据
const allWithdrawals = ref([
  {
    id: 1,
    amount: 100,
    withdrawalTime: '2025-05-13 18:31:07'
  },
  {
    id: 2,
    amount: 100,
    withdrawalTime: '2025-05-12 18:28:58'
  }
])

// 过滤后的提现记录数据
const filteredWithdrawals = ref([...allWithdrawals.value])

// 发起提现模态框显示状态
const showWithdrawalModalFlag = ref(false)

// 提现表单数据
const withdrawalForm = ref({
  amount: 0,
  account: '',
  accountType: 'wechat'
})

// 服务评价搜索关键词
const evaluationSearchKeyword = ref('')

// 服务评价数据
const allEvaluations = ref([
  {
    id: 1,
    serviceProvider: '家政小黄',
    serviceName: '深度保洁5小时【全屋大扫除】',
    user: '小张',
    rating: 4.5,
    content: '非常好!',
    evaluationTime: '2025-05-13 09:00:00'
  }
])

// 过滤后的服务评价数据
const filteredEvaluations = ref([...allEvaluations.value])

// 家政服务搜索关键词
const servicesSearchKeyword = ref('')

// 家政服务数据
const allServices = ref([
  {
    id: 1,
    imageText: '沙发保养·清洁',
    name: '沙发保养清洁【包含皮…',
    price: 50,
    unit: '次',
    sales: 2,
    popularity: 21,
    category: '皮质沙发保养'
  },
  {
    id: 2,
    imageText: '地板打蜡·10平米',
    name: '地板打蜡10平米',
    price: 66,
    unit: '10平米',
    sales: 5,
    popularity: 16,
    category: '地板打蜡'
  },
  {
    id: 3,
    imageText: '长期保洁·服务',
    name: '全屋清洁【大扫除】',
    price: 599,
    unit: '次',
    sales: 0,
    popularity: 7,
    category: '家庭长期保洁'
  },
  {
    id: 4,
    imageText: '卫生间保养·清洁',
    name: '卫生间保养清洁【包含…',
    price: 88,
    unit: '次',
    sales: 0,
    popularity: 0,
    category: '卫生间保养'
  },
  {
    id: 5,
    imageText: '厨房保养·保洁',
    name: '厨房保养清洁【包含油…',
    price: 338,
    unit: '次',
    sales: 0,
    popularity: 9,
    category: '厨房保养'
  }
])

// 过滤后的家政服务数据
const filteredServices = ref([...allServices.value])

// 服务者认证表单数据
const certificationForm = ref({
  qualificationFile: null as File | null,
  idCardFile: null as File | null,
  idNumber: '',
  contact: '',
  address: ''
})

// 文件输入框引用
const qualificationInput = ref<HTMLInputElement | null>(null)
const idCardInput = ref<HTMLInputElement | null>(null)
const avatarInput = ref<HTMLInputElement | null>(null)

// 个人资料表单数据
const profileForm = ref({
  avatar: '',
  username: 'provider',
  name: '家政小丁',
  phone: '13988997766',
  email: 'xdd@xm.com',
  balance: '25.9'
})

// 初始化时同步头像到右上角
if (profileForm.value.avatar) {
  userAvatar.value = profileForm.value.avatar
}

// 修改密码表单数据
const changePasswordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码显示/隐藏状态
const showOldPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

// 从localStorage获取用户信息
const getUserInfo = () => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      const user = JSON.parse(storedUserInfo)
      userInfo.value = {
        username: user.username,
        role: user.role
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 如果获取失败，跳转到登录页
      router.push('/login')
    }
  } else {
    // 如果没有用户信息，跳转到登录页
    router.push('/login')
  }
}

// 组件挂载时获取用户信息
getUserInfo()

// 点击外部区域关闭下拉菜单
const handleClickOutside = (event: Event) => {
  const target = event.target as HTMLElement
  if (!target.closest('.user-section')) {
    showUserDropdown.value = false
  }
}

// 监听点击事件
document.addEventListener('click', handleClickOutside)

// 设置激活菜单
const setActiveMenu = (menu: string) => {
  activeMenu.value = menu
  // 这里可以添加路由跳转逻辑
  console.log('切换到菜单:', menu)
}

// 切换信息管理菜单展开状态
const toggleInfoMenu = () => {
  isInfoMenuExpanded.value = !isInfoMenuExpanded.value
}

// 切换用户下拉菜单
const toggleUserDropdown = () => {
  showUserDropdown.value = !showUserDropdown.value
}

// 处理服务者认证
const handleServiceCertification = () => {
  showUserDropdown.value = false
  setActiveMenu('certification')
}

// 处理个人资料
const handlePersonalProfile = () => {
  showUserDropdown.value = false
  showPersonalProfileModal.value = true
}

// 处理修改密码
const handleChangePassword = () => {
  showUserDropdown.value = false
  showChangePasswordModal.value = true
}

// 模态框取消方法
const cancelPersonalProfile = () => {
  showPersonalProfileModal.value = false
}

const cancelChangePassword = () => {
  showChangePasswordModal.value = false
}

// 头像上传相关
const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

const handleAvatarUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    // 验证文件大小（最大2MB）
    if (file.size > 2 * 1024 * 1024) {
      alert('图片大小不能超过2MB')
      return
    }
    
    // 验证文件类型
    if (!file.type.startsWith('image/')) {
      alert('请选择图片文件')
      return
    }
    
    // 预览图片
    const reader = new FileReader()
    reader.onload = (e) => {
      if (e.target?.result) {
        profileForm.value.avatar = e.target.result as string
        userAvatar.value = e.target.result as string
      }
    }
    reader.readAsDataURL(file)
  }
}

// 保存个人资料
const saveProfile = () => {
  // 验证必填字段
  if (!profileForm.value.name.trim()) {
    alert('请输入姓名')
    return
  }
  
  if (!profileForm.value.phone.trim()) {
    alert('请输入电话号码')
    return
  }
  
  if (!profileForm.value.email.trim()) {
    alert('请输入邮箱地址')
    return
  }
  
  // 验证电话号码格式
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(profileForm.value.phone)) {
    alert('请输入正确的手机号码')
    return
  }
  
  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(profileForm.value.email)) {
    alert('请输入正确的邮箱地址')
    return
  }
  
  // 模拟保存
  console.log('保存个人资料:', profileForm.value)
  alert('个人资料保存成功！')
  
  // 关闭模态框
  showPersonalProfileModal.value = false
}

// 保存修改密码
const saveChangePassword = () => {
  // 验证必填字段
  if (!changePasswordForm.value.oldPassword.trim()) {
    alert('请输入原密码')
    return
  }
  
  if (!changePasswordForm.value.newPassword.trim()) {
    alert('请输入新密码')
    return
  }
  
  if (!changePasswordForm.value.confirmPassword.trim()) {
    alert('请确认新密码')
    return
  }
  
  // 验证新密码格式
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/
  if (!passwordRegex.test(changePasswordForm.value.newPassword)) {
    alert('新密码至少包含8个字符，包含字母和数字')
    return
  }
  
  // 验证两次密码是否一致
  if (changePasswordForm.value.newPassword !== changePasswordForm.value.confirmPassword) {
    alert('两次输入的密码不一致')
    return
  }
  
  // 模拟保存
  console.log('修改密码:', changePasswordForm.value)
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

// 退出登录
const logout = () => {
  if (confirm('确定要退出登录吗？')) {
    // 清除用户信息
    localStorage.removeItem('userInfo')
    // 跳转到登录页
    router.push('/login')
  }
}

// 服务预约搜索功能
const searchAppointments = () => {
  const keyword = appointmentSearchKeyword.value.trim().toLowerCase()
  
  // 搜索时重置到第一页
  currentPage.value = 1
  
  if (keyword === '') {
    filteredAppointments.value = [...allAppointments.value]
    return
  }
  
  const results = allAppointments.value.filter(appointment => 
    appointment.serviceName.toLowerCase().includes(keyword) ||
    appointment.user.toLowerCase().includes(keyword) ||
    appointment.contactPhone.toLowerCase().includes(keyword) ||
    appointment.contactAddress.toLowerCase().includes(keyword)
  )
  
  filteredAppointments.value = results
  
  // 添加搜索结果提示
  if (results.length === 0) {
    alert(`未找到包含"${appointmentSearchKeyword.value}"的预约记录`)
  } else {
    console.log(`找到 ${results.length} 条匹配的预约记录`)
  }
}

// 重置搜索
const resetAppointmentSearch = () => {
  appointmentSearchKeyword.value = ''
  filteredAppointments.value = [...allAppointments.value]
  currentPage.value = 1 // 重置到第一页
  console.log('搜索已重置，显示全部预约记录')
}

// 处理服务
const handleService = (appointment: any) => {
  console.log('处理服务:', appointment)
  
  // 根据不同状态显示不同操作选项
  switch (appointment.status) {
    case 'pending':
      // 待分配状态 - 接受服务
      if (confirm(`确定要接受此服务预约吗？\n服务名称：${appointment.serviceName}\n客户：${appointment.user}\n服务时间：${appointment.startTime}`)) {
        // 更新状态为服务中
        const index = allAppointments.value.findIndex(item => item.id === appointment.id)
        if (index !== -1) {
          allAppointments.value[index].status = 'in-service'
          filteredAppointments.value = [...allAppointments.value]
          alert('服务已接受，状态更新为：服务中')
        }
      }
      break
    case 'in-service':
    case 'completed':
    case 'cancelled':
      // 已分配状态 - 删除预约
      if (confirm(`确定要删除此预约记录吗？\n服务名称：${appointment.serviceName}\n客户：${appointment.user}\n此操作不可撤销！`)) {
        // 从数组中删除预约记录
        const index = allAppointments.value.findIndex(item => item.id === appointment.id)
        if (index !== -1) {
          // 保存当前页码，用于删除后保持在同一页
          const currentPageSize = paginatedAppointments.value.length
          
          // 删除记录
          allAppointments.value.splice(index, 1)
          filteredAppointments.value = [...allAppointments.value]
          
          // 处理分页 - 如果删除后当前页没有数据且不是第一页，则返回上一页
          if (paginatedAppointments.value.length === 0 && currentPage.value > 1) {
            currentPage.value--
          }
          
          alert('预约记录已成功删除！')
        }
      }
      break
    default:
      alert(`服务状态：${appointment.status}\n暂无可执行操作`)
  }
}

// 提现记录搜索功能
const searchWithdrawals = () => {
  if (!withdrawalDate.value) {
    filteredWithdrawals.value = [...allWithdrawals.value]
    return
  }
  
  const searchDate = new Date(withdrawalDate.value)
  filteredWithdrawals.value = allWithdrawals.value.filter(withdrawal => {
    const withdrawalDate = new Date(withdrawal.withdrawalTime.split(' ')[0])
    return withdrawalDate.getTime() === searchDate.getTime()
  })
}

// 重置提现记录搜索
const resetWithdrawalSearch = () => {
  withdrawalDate.value = ''
  filteredWithdrawals.value = [...allWithdrawals.value]
}

// 显示发起提现模态框
const showWithdrawalModal = () => {
  showWithdrawalModalFlag.value = true
  withdrawalForm.value = {
    amount: 0,
    account: '',
    accountType: 'wechat'
  }
}

// 关闭发起提现模态框
const closeWithdrawalModal = () => {
  showWithdrawalModalFlag.value = false
}

// 增加金额
const increaseAmount = () => {
  withdrawalForm.value.amount += 10
}

// 减少金额
const decreaseAmount = () => {
  if (withdrawalForm.value.amount > 0) {
    withdrawalForm.value.amount -= 10
  }
}

// 提交提现申请
const submitWithdrawal = () => {
  if (!withdrawalForm.value.amount || !withdrawalForm.value.account) {
    alert('请填写完整信息')
    return
  }
  
  // 创建新的提现记录
  const newWithdrawal = {
    id: allWithdrawals.value.length + 1,
    amount: withdrawalForm.value.amount,
    withdrawalTime: new Date().toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    }).replace(/\//g, '-')
  }
  
  // 添加到提现记录列表
  allWithdrawals.value.unshift(newWithdrawal)
  filteredWithdrawals.value = [...allWithdrawals.value]
  
  alert('提现申请提交成功！')
  closeWithdrawalModal()
}

// 服务评价搜索功能
const searchEvaluations = () => {
  const keyword = evaluationSearchKeyword.value.trim().toLowerCase()
  if (keyword === '') {
    filteredEvaluations.value = [...allEvaluations.value]
  } else {
    filteredEvaluations.value = allEvaluations.value.filter(evaluation => 
      evaluation.serviceName.toLowerCase().includes(keyword) ||
      evaluation.serviceProvider.toLowerCase().includes(keyword) ||
      evaluation.user.toLowerCase().includes(keyword) ||
      evaluation.content.toLowerCase().includes(keyword)
    )
  }
}

// 重置服务评价搜索
const resetEvaluationSearch = () => {
  evaluationSearchKeyword.value = ''
  filteredEvaluations.value = [...allEvaluations.value]
}

// 服务分页相关
const servicesCurrentPage = ref(1)
const servicesPageSize = ref(3)

// 计算当前页的服务数据
const paginatedServices = computed(() => {
  const start = (servicesCurrentPage.value - 1) * servicesPageSize.value
  const end = start + servicesPageSize.value
  return filteredServices.value.slice(start, end)
})

// 计算服务总页数
const servicesTotalPages = computed(() => {
  return Math.ceil(filteredServices.value.length / servicesPageSize.value)
})

// 服务上一页
const servicesPrevPage = () => {
  if (servicesCurrentPage.value > 1) {
    servicesCurrentPage.value--
  }
}

// 服务下一页
const servicesNextPage = () => {
  if (servicesCurrentPage.value < servicesTotalPages.value) {
    servicesCurrentPage.value++
  }
}

// 跳转到指定页
const servicesGoToPage = (page: number) => {
  if (page >= 1 && page <= servicesTotalPages.value) {
    servicesCurrentPage.value = page
  }
}

// 家政服务搜索功能
const searchServices = () => {
  const keyword = servicesSearchKeyword.value.trim().toLowerCase()
  
  // 搜索时重置到第一页
  servicesCurrentPage.value = 1
  
  if (keyword === '') {
    filteredServices.value = [...allServices.value]
  } else {
    filteredServices.value = allServices.value.filter(service => 
      service.name.toLowerCase().includes(keyword) ||
      service.category.toLowerCase().includes(keyword) ||
      service.imageText.toLowerCase().includes(keyword)
    )
    
    // 添加搜索结果提示
    if (filteredServices.value.length === 0) {
      alert(`未找到包含"${servicesSearchKeyword.value}"的服务`)
    }
  }
}

// 重置家政服务搜索
const resetServicesSearch = () => {
  servicesSearchKeyword.value = ''
  filteredServices.value = [...allServices.value]
  servicesCurrentPage.value = 1 // 重置到第一页
}

// 查看服务详情
const viewServiceDetails = (service: any) => {
  alert(`服务详情：\n名称：${service.name}\n图片文案：${service.imageText}\n价格：¥${service.price}/${service.unit}\n销量：${service.sales}\n人气：${service.popularity}\n分类：${service.category}`)
}

// 编辑服务
const editService = (service: any) => {
  alert(`编辑服务：${service.name}\n此功能将打开编辑表单，允许修改服务信息。`)
  // 这里可以实现打开编辑模态框的逻辑
}

// 删除服务
const deleteService = (service: any) => {
  if (confirm(`确定要删除服务「${service.name}」吗？\n此操作不可撤销！`)) {
    // 从数组中删除服务
    const index = allServices.value.findIndex(item => item.id === service.id)
    if (index !== -1) {
      // 保存当前页码，用于删除后保持在同一页
      const currentPageSize = paginatedServices.value.length
      
      // 删除记录
      allServices.value.splice(index, 1)
      filteredServices.value = [...allServices.value]
      
      // 处理分页 - 如果删除后当前页没有数据且不是第一页，则返回上一页
      if (paginatedServices.value.length === 0 && servicesCurrentPage.value > 1) {
        servicesCurrentPage.value--
      }
      
      alert('服务已成功删除！')
    }
  }
}

// 触发文件上传
const triggerFileUpload = (type: 'qualification' | 'idCard') => {
  if (type === 'qualification' && qualificationInput.value) {
    qualificationInput.value.click()
  } else if (type === 'idCard' && idCardInput.value) {
    idCardInput.value.click()
  }
}

// 处理文件上传
const handleFileUpload = (event: Event, type: 'qualification' | 'idCard') => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (file) {
    // 检查文件大小（5MB限制）
    if (file.size > 5 * 1024 * 1024) {
      alert('文件大小不能超过5MB')
      return
    }
    
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
      alert('请上传图片文件')
      return
    }
    
    if (type === 'qualification') {
      certificationForm.value.qualificationFile = file
    } else if (type === 'idCard') {
      certificationForm.value.idCardFile = file
    }
  }
}

// 移除文件
const removeFile = (type: 'qualification' | 'idCard') => {
  if (type === 'qualification') {
    certificationForm.value.qualificationFile = null
    if (qualificationInput.value) {
      qualificationInput.value.value = ''
    }
  } else if (type === 'idCard') {
    certificationForm.value.idCardFile = null
    if (idCardInput.value) {
      idCardInput.value.value = ''
    }
  }
}

// 提交认证表单
const submitCertification = () => {
  // 验证必填字段
  if (!certificationForm.value.qualificationFile) {
    alert('请上传资格证书')
    return
  }
  
  if (!certificationForm.value.idCardFile) {
    alert('请上传身份证')
    return
  }
  
  if (!certificationForm.value.idNumber.trim()) {
    alert('请输入身份证号码')
    return
  }
  
  if (!certificationForm.value.contact.trim()) {
    alert('请输入联系方式')
    return
  }
  
  if (!certificationForm.value.address.trim()) {
    alert('请输入家庭住址')
    return
  }
  
  // 验证身份证号码格式
  const idNumberRegex = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
  if (!idNumberRegex.test(certificationForm.value.idNumber)) {
    alert('请输入正确的身份证号码')
    return
  }
  
  // 模拟提交
  console.log('提交认证信息:', certificationForm.value)
  alert('认证信息提交成功，请等待审核！')
  
  // 重置表单
  certificationForm.value = {
    qualificationFile: null,
    idCardFile: null,
    idNumber: '',
    contact: '',
    address: ''
  }
  
  // 清空文件输入框
  if (qualificationInput.value) qualificationInput.value.value = ''
  if (idCardInput.value) idCardInput.value.value = ''
}



// 提交个人资料
const submitProfile = () => {
  // 验证必填字段
  if (!profileForm.value.name.trim()) {
    alert('请输入姓名')
    return
  }
  
  if (!profileForm.value.phone.trim()) {
    alert('请输入电话号码')
    return
  }
  
  if (!profileForm.value.email.trim()) {
    alert('请输入邮箱地址')
    return
  }
  
  // 验证电话号码格式
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(profileForm.value.phone)) {
    alert('请输入正确的手机号码')
    return
  }
  
  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(profileForm.value.email)) {
    alert('请输入正确的邮箱地址')
    return
  }
  
  // 模拟保存
  console.log('保存个人资料:', profileForm.value)
  alert('个人资料保存成功！')
}

// 切换密码显示/隐藏
const togglePasswordVisibility = (type: 'old' | 'new' | 'confirm') => {
  switch (type) {
    case 'old':
      showOldPassword.value = !showOldPassword.value
      break
    case 'new':
      showNewPassword.value = !showNewPassword.value
      break
    case 'confirm':
      showConfirmPassword.value = !showConfirmPassword.value
      break
  }
}

// 提交修改密码
const submitChangePassword = () => {
  // 验证必填字段
  if (!changePasswordForm.value.oldPassword.trim()) {
    alert('请输入原密码')
    return
  }
  
  if (!changePasswordForm.value.newPassword.trim()) {
    alert('请输入新密码')
    return
  }
  
  if (!changePasswordForm.value.confirmPassword.trim()) {
    alert('请确认新密码')
    return
  }
  
  // 验证新密码长度
  if (changePasswordForm.value.newPassword.length < 6) {
    alert('新密码长度不能少于6位')
    return
  }
  
  // 验证新密码和确认密码是否一致
  if (changePasswordForm.value.newPassword !== changePasswordForm.value.confirmPassword) {
    alert('新密码和确认密码不一致')
    return
  }
  
  // 验证原密码
  const currentPassword = getCurrentPassword()
  if (changePasswordForm.value.oldPassword !== currentPassword) {
    alert('原密码不正确')
    return
  }
  
  // 验证新密码不能与原密码相同
  if (changePasswordForm.value.oldPassword === changePasswordForm.value.newPassword) {
    alert('新密码不能与原密码相同')
    return
  }
  
  // 更新用户密码
  updateUserPassword(changePasswordForm.value.newPassword)
  
  console.log('修改密码:', changePasswordForm.value)
  alert('密码修改成功！')
  
  // 重置表单
  changePasswordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  
  // 重置密码显示状态
  showOldPassword.value = false
  showNewPassword.value = false
  showConfirmPassword.value = false
}

// 获取当前密码
const getCurrentPassword = () => {
  // 首先检查registeredUsers中是否有更新后的密码
  const registeredUsers = JSON.parse(localStorage.getItem('registeredUsers') || '[]')
  const currentUser = userInfo.value
  const registeredUser = registeredUsers.find((u: any) => u.username === currentUser.username)
  
  if (registeredUser) {
    return registeredUser.password
  }
  
  // 如果没有找到，返回默认密码
  const defaultPasswords: { [key: string]: string } = {
    'user': '123456',
    'admin': 'admin123',
    'provider': 'provider123',
    'test': 'test123'
  }
  
  return defaultPasswords[currentUser.username] || 'provider123'
}

// 更新用户密码
const updateUserPassword = (newPassword: string) => {
  // 获取当前用户信息
  const currentUser = userInfo.value
  
  // 更新localStorage中的用户密码信息
  const updatedUserInfo = {
    ...currentUser,
    password: newPassword,
    passwordUpdated: true // 标记密码已更新
  }
  
  // 保存到localStorage
  localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
  
  // 同时更新registeredUsers中的密码（如果存在）
  const registeredUsers = JSON.parse(localStorage.getItem('registeredUsers') || '[]')
  const userIndex = registeredUsers.findIndex((u: any) => u.username === currentUser.username)
  
  if (userIndex !== -1) {
    registeredUsers[userIndex].password = newPassword
    localStorage.setItem('registeredUsers', JSON.stringify(registeredUsers))
  } else {
    // 如果是默认用户，添加到registeredUsers中
    const newUser = {
      username: currentUser.username,
      password: newPassword,
      role: currentUser.role
    }
    registeredUsers.push(newUser)
    localStorage.setItem('registeredUsers', JSON.stringify(registeredUsers))
  }
}
</script>

<style scoped>
/* 全局重置 */
* {
  box-sizing: border-box;
}

.provider-home-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  position: fixed;
  top: 0;
  left: 0;
}

/* 顶部导航栏 */
.top-header {
  height: 60px;
  background: linear-gradient(135deg, #8B4513 0%, #A0522D 100%);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 100;
  flex-shrink: 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 30px;
}

.logo-section {
  display: flex;
  align-items: center;
}

.logo {
  color: white;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.breadcrumb {
  color: white;
  font-size: 14px;
}

.user-section {
  display: flex;
  align-items: center;
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* 用户头像 */
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

.logout-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 用户下拉菜单样式 */
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
}

/* 侧边导航菜单 */
.sidebar-nav {
  width: 200px;
  background: white;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  flex-shrink: 0;
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
  background: #e3f2fd;
  border-left: 3px solid #8B4513;
  color: #8B4513;
}

.nav-item.active .nav-text {
  color: #8B4513;
  font-weight: 600;
}

/* 子菜单样式 */
.sub-menu {
  background: #f8f9fa;
  border-left: 3px solid #e3f2fd;
  margin-left: 20px;
  border-radius: 0 8px 8px 0;
  overflow: hidden;
}

.sub-item {
  margin: 0;
  border-left: none;
  padding-left: 20px;
  background: transparent;
  border-radius: 0;
}

.sub-item:hover {
  background: #e9ecef;
}

.sub-item.active {
  background: #e3f2fd;
  color: #8B4513;
  border-left: 3px solid #8B4513;
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
  background: white;
  box-sizing: border-box;
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

/* 服务预约搜索栏 */
.appointment-search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  justify-content: flex-start;
}

.appointment-search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 250px;
}

.appointment-search-btn {
  padding: 8px 16px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.appointment-search-btn:hover {
  background: #218838;
}

.appointment-reset-btn {
  padding: 8px 16px;
  background: #ffc107;
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.appointment-reset-btn:hover {
  background: #e0a800;
}

/* 预约表格样式 */
.appointment-table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.appointment-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.appointment-table th {
  background: #f8f9fa;
  padding: 12px 8px;
  text-align: left;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #dee2e6;
  white-space: nowrap;
}

.appointment-table td {
  padding: 12px 8px;
  border-bottom: 1px solid #f1f3f4;
  color: #333;
  vertical-align: middle;
}

.appointment-table tbody tr:hover {
  background: #f8f9fa;
}

.appointment-table tbody tr:last-child td {
  border-bottom: none;
}

/* 金额样式 */
.amount {
  color: #dc3545;
  font-weight: 600;
}

/* 状态标签 */
.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.status.completed {
  background: #d4edda;
  color: #155724;
}

.status.pending {
  background: #fff3cd;
  color: #856404;
}

.status.in-service {
  background: #d1ecf1;
  color: #0c5460;
}

.status.cancelled {
  background: #f8d7da;
  color: #721c24;
}

/* 服务按钮 */
.service-cell {
  padding: 8px;
}

.service-btn {
  padding: 6px 12px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.service-btn:hover {
  background: #0056b3;
  opacity: 0.9;
  transform: translateY(-1px);
}

/* 删除按钮样式 */
.service-btn.delete-btn {
  background: #dc3545;
  color: white;
}

.service-btn.delete-btn:hover {
  background: #c82333;
  opacity: 0.9;
  transform: translateY(-1px);
}

/* 家政服务操作按钮样式 */
.service-actions {
  display: flex;
  gap: 8px;
}

.view-btn,
.edit-btn,
.delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.view-btn {
  background-color: #17a2b8;
  color: white;
}

.view-btn:hover {
  background-color: #138496;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.edit-btn {
  background-color: #ffc107;
  color: #212529;
}

.edit-btn:hover {
  background-color: #e0a800;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.delete-btn {
  background-color: #dc3545;
  color: white;
}

.delete-btn:hover {
  background-color: #c82333;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .service-actions {
    flex-direction: column;
    gap: 4px;
  }

  .view-btn,
  .edit-btn,
  .delete-btn {
    width: 100%;
    padding: 4px 8px;
    font-size: 12px;
  }
}

/* 预约分页样式 */
.appointment-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.pagination-info {
  color: #666;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  gap: 5px;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.page-btn:hover {
  background: #f8f9fa;
  transform: translateY(-1px);
}

.page-btn.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.page-btn:disabled:hover {
  background: white;
  transform: none;
}

/* 无数据提示 */
.no-data {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 14px;
  background: #f8f9fa;
  font-style: italic;
}

/* 提现记录搜索栏 */
.withdrawal-search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  justify-content: flex-start;
}

.withdrawal-date-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 200px;
}

.withdrawal-search-btn {
  padding: 8px 16px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.withdrawal-search-btn:hover {
  background: #218838;
}

.withdrawal-reset-btn {
  padding: 8px 16px;
  background: #ffc107;
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.withdrawal-reset-btn:hover {
  background: #e0a800;
}

/* 提现按钮 */
.withdrawal-action {
  margin-bottom: 20px;
}

.withdrawal-btn {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
}

.withdrawal-btn:hover {
  background: #0056b3;
}

/* 提现记录列表 */
.withdrawal-records {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.withdrawal-record {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f1f3f4;
}

.withdrawal-record:last-child {
  border-bottom: none;
}

.record-amount {
  color: #dc3545;
  font-size: 16px;
  font-weight: 600;
}

.record-time {
  color: #666;
  font-size: 14px;
}

/* 提现记录分页 */
.withdrawal-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
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
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
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
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #666;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.required {
  color: #dc3545;
}

.amount-input-group {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.amount-btn {
  background: #f8f9fa;
  border: none;
  padding: 8px 12px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  color: #666;
  border-right: 1px solid #ddd;
}

.amount-btn:last-child {
  border-right: none;
  border-left: 1px solid #ddd;
}

.amount-btn:hover {
  background: #e9ecef;
}

.amount-input {
  flex: 1;
  border: none;
  padding: 8px 12px;
  font-size: 14px;
  outline: none;
}

.form-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.account-type-group {
  display: flex;
  gap: 15px;
}

.account-type-option {
  flex: 1;
  border: 2px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.account-type-option:hover {
  border-color: #007bff;
}

.account-type-option.active {
  border-color: #28a745;
  background: #f8fff9;
}

.account-type-radio {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}

.account-type-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.account-type-icon {
  font-size: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #eee;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-secondary {
  background: white;
  color: #666;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background: #f8f9fa;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover {
  background: #0056b3;
}

/* 服务评价搜索栏 */
.evaluation-search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  justify-content: flex-start;
}

.evaluation-search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 250px;
}

.evaluation-search-btn {
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.evaluation-search-btn:hover {
  background: #0056b3;
}

.evaluation-reset-btn {
  padding: 8px 16px;
  background: #ffc107;
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.evaluation-reset-btn:hover {
  background: #e0a800;
}

/* 服务评价表格样式 */
.evaluation-table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.evaluation-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.evaluation-table th {
  background: #f8f9fa;
  padding: 12px 8px;
  text-align: left;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #dee2e6;
  white-space: nowrap;
}

.evaluation-table td {
  padding: 12px 8px;
  border-bottom: 1px solid #f1f3f4;
  color: #333;
  vertical-align: middle;
}

.evaluation-table tbody tr:hover {
  background: #f8f9fa;
}

.evaluation-table tbody tr:last-child td {
  border-bottom: none;
}

/* 评分显示样式 */
.rating-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  color: #ddd;
  font-size: 16px;
  transition: color 0.2s ease;
}

.star.full {
  color: #ffc107;
}

.star.half {
  color: #ffc107;
  position: relative;
}

.star.half::after {
  content: '★';
  position: absolute;
  left: 0;
  top: 0;
  width: 50%;
  overflow: hidden;
  color: #ddd;
}

.rating-number {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

/* 服务评价分页样式 */
.evaluation-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

/* 家政服务搜索栏 */
.services-search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  justify-content: flex-start;
}

.services-search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 250px;
}

.services-search-btn {
  padding: 8px 16px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.services-search-btn:hover {
  background: #218838;
}

.services-reset-btn {
  padding: 8px 16px;
  background: #ffc107;
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.services-reset-btn:hover {
  background: #e0a800;
}

/* 家政服务表格样式 */
.services-table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.services-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.services-table th {
  background: #f8f9fa;
  padding: 12px 8px;
  text-align: left;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #dee2e6;
  white-space: nowrap;
}

.services-table td {
  padding: 12px 8px;
  border-bottom: 1px solid #f1f3f4;
  color: #333;
  vertical-align: middle;
}

.services-table tbody tr:hover {
  background: #f8f9fa;
}

.services-table tbody tr:last-child td {
  border-bottom: none;
}

/* 服务图片样式 */
.service-image {
  width: 80px;
  height: 60px;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.image-placeholder::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 0;
  height: 0;
  border-left: 20px solid transparent;
  border-top: 20px solid #ff4757;
}

.image-placeholder::after {
  content: '';
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 12px;
  height: 12px;
  background: #007bff;
  border-radius: 50%;
}

.image-text {
  color: white;
  font-size: 10px;
  font-weight: 600;
  text-align: center;
  line-height: 1.2;
  margin-bottom: 2px;
}

.image-subtitle {
  color: white;
  font-size: 8px;
  text-align: center;
  opacity: 0.9;
}

/* 价格样式 */
.price {
  color: #dc3545;
  font-weight: 600;
}

/* 查看详情按钮 */
.view-details-btn {
  padding: 6px 12px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.view-details-btn:hover {
  background: #0056b3;
}

/* 家政服务分页样式 */
.services-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

/* 服务者认证样式 */
.certification-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.certification-title {
  text-align: center;
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 40px;
}

.certification-form {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 30px;
}

.form-label {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.required {
  color: #dc3545;
  margin-left: 4px;
}

/* 文件上传区域 */
.file-upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.file-upload-area:hover {
  border-color: #007bff;
  background: #f0f8ff;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-icon {
  font-size: 48px;
  opacity: 0.6;
}

.upload-text {
  font-size: 16px;
  color: #666;
  font-weight: 500;
}

.upload-hint {
  font-size: 14px;
  color: #999;
}

/* 文件预览 */
.file-preview {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #e8f5e8;
  padding: 12px 16px;
  border-radius: 6px;
  margin-top: 12px;
  border: 1px solid #c3e6c3;
}

.file-name {
  font-size: 14px;
  color: #2d5a2d;
  font-weight: 500;
}

.remove-file {
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-file:hover {
  background: #c82333;
}

/* 表单输入框 */
.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  font-family: inherit;
  resize: vertical;
  min-height: 100px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

/* 表单操作按钮 */
.form-actions {
  text-align: center;
  margin-top: 40px;
}

.submit-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 14px 40px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
}

.submit-btn:hover {
  background: #0056b3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
}

.submit-btn:active {
  transform: translateY(0);
}

/* 个人资料样式 */
.profile-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.profile-title {
  text-align: center;
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 40px;
}

.profile-form {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* 头像区域 */
.avatar-group {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.avatar-section {
  display: flex;
  align-items: center;
}

.avatar-container {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid #e0e0e0;
  transition: all 0.3s ease;
  position: relative;
}

.avatar-container:hover {
  border-color: #007bff;
  transform: scale(1.05);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
}

.avatar-icon {
  font-size: 32px;
  color: #999;
}

/* 只读输入框 */
.form-input.readonly {
  background: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
}

/* 账户余额显示 */
.balance-display {
  display: flex;
  align-items: center;
}

.balance-amount {
  background: #007bff;
  color: white;
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 16px;
}

/* 保存按钮 */
.save-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 14px 40px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
}

.save-btn:hover {
  background: #0056b3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
}

.save-btn:active {
  transform: translateY(0);
}

/* 修改密码样式 */
.change-password-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
}

.change-password-title {
  text-align: center;
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 40px;
}

.change-password-form {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* 密码输入框包装器 */
.password-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.password-input {
  padding-right: 50px;
  width: 100%;
}

.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  padding: 4px;
  border-radius: 4px;
  transition: color 0.3s ease;
}

.password-toggle:hover {
  color: #007bff;
}

.password-toggle:focus {
  outline: none;
  color: #007bff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-wrapper {
    flex-direction: column;
  }
  
  .sidebar-nav {
    width: 100%;
    height: auto;
    order: 2;
  }
  
  .nav-menu {
    display: flex;
    padding: 10px 0;
    overflow-x: auto;
  }
  
  .nav-item {
    flex-shrink: 0;
    padding: 10px 20px;
    border-bottom: 3px solid transparent;
  }
  
  .nav-item.active {
    border-bottom-color: #8B4513;
  }
  
  .main-content {
    order: 1;
    padding: 20px;
  }
  
  .welcome-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-content {
    padding: 0 15px;
  }
  
  .logo {
    font-size: 16px;
  }
  
  .breadcrumb {
    font-size: 12px;
  }
  
  .welcome-text {
    font-size: 12px;
  }
  
  .welcome-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .appointment-search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .appointment-search-input {
    width: 100%;
  }
  
  .appointment-table-container {
    overflow-x: auto;
  }
  
  .appointment-table {
    min-width: 1200px;
  }
  
  .withdrawal-search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .withdrawal-date-input {
    width: 100%;
  }
  
  .account-type-group {
    flex-direction: column;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
  
  .evaluation-search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .evaluation-search-input {
    width: 100%;
  }
  
  .evaluation-table-container {
    overflow-x: auto;
  }
  
  .evaluation-table {
    min-width: 800px;
  }
  
  .services-search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .services-search-input {
    width: 100%;
  }
  
  .services-table-container {
    overflow-x: auto;
  }
  
  .services-table {
    min-width: 1000px;
  }
  
  .certification-container {
    padding: 10px;
  }
  
  .certification-form {
    padding: 20px;
  }
  
  .certification-title {
    font-size: 24px;
    margin-bottom: 30px;
  }
  
  .file-upload-area {
    padding: 30px 15px;
  }
  
  .upload-icon {
    font-size: 36px;
  }
  
  .profile-container {
    padding: 10px;
  }
  
  .profile-form {
    padding: 20px;
  }
  
  .profile-title {
    font-size: 24px;
    margin-bottom: 30px;
  }
  
  .avatar-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .avatar-container {
    width: 70px;
    height: 70px;
  }
  
  .avatar-icon {
    font-size: 28px;
  }
  
  .change-password-container {
    padding: 10px;
  }
  
  .change-password-form {
    padding: 20px;
  }
  
  .change-password-title {
    font-size: 24px;
    margin-bottom: 30px;
  }
}

@media (max-width: 480px) {
  .announcement-item {
    padding: 15px;
  }
  
  .announcement-title {
    font-size: 14px;
  }
  
  .announcement-description {
    font-size: 13px;
  }
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


/* 个人资料模态框样式 */
.personal-profile-modal {
  max-width: 600px;
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.form-input {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.form-textarea {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
  transition: border-color 0.3s ease;
}

.form-textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

/* 头像区域 */
.avatar-group {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-section {
  display: flex;
  align-items: center;
}

.avatar-container {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid #ddd;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-container:hover {
  border-color: #007bff;
  transform: scale(1.05);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-icon {
  font-size: 32px;
  color: #666;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.save-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.save-btn:hover {
  background: #0056b3;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
}

.save-btn:active {
  transform: translateY(0);
}

/* 修改密码模态框样式 */
.change-password-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.password-hint {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

/* 按钮样式 */
.btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
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
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
}

.btn-primary:active {
  transform: translateY(0);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: 10px;
  }
  
  .modal-header {
    padding: 15px;
  }
  
  .modal-body {
    padding: 15px;
  }
  
  .modal-footer {
    padding: 15px;
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
    margin: 5px 0;
  }
  
  .avatar-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .avatar-container {
    width: 70px;
    height: 70px;
  }
  
  .avatar-icon {
    font-size: 28px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .save-btn {
    width: 100%;
  }
}
</style>

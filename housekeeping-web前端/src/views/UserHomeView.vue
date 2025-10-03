<template>
  <div class="user-home-container">
    <!-- 顶部导航栏 -->
    <header class="top-header">
      <div class="header-content">
        <div class="logo-section">
          <h1 class="logo">家政服务平台</h1>
        </div>
        <div class="user-section">
          <div class="user-info" @click="toggleUserDropdown">
            <div class="user-avatar">
              <img v-if="userAvatar" :src="userAvatar" alt="头像" class="avatar-image">
              <div v-else class="avatar-placeholder">🐻</div>
            </div>
            <span class="username">{{ userInfo.username }}</span>
            <div class="dropdown-arrow" :class="{ active: showUserDropdown }">▼</div>
          </div>
          
          <!-- 用户下拉菜单 -->
          <div v-if="showUserDropdown" class="user-dropdown" @click.stop>
            <div class="dropdown-item" @click="handleMyFavorites">
              <span>我的收藏</span>
            </div>
            <div class="dropdown-item" @click="handleRechargeRecords">
              <span>充值记录</span>
            </div>
            <div class="dropdown-item" @click="handlePersonalInfo">
              <span>个人信息</span>
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
            <span class="nav-text">首页</span>
          </li>
          <li class="nav-item" :class="{ active: activeMenu === 'services' }" @click="setActiveMenu('services')">
            <div class="nav-icon">🧹</div>
            <span class="nav-text">家政服务</span>
          </li>
          <li class="nav-item" :class="{ active: activeMenu === 'tips' }" @click="setActiveMenu('tips')">
            <div class="nav-icon">💡</div>
            <span class="nav-text">居家小贴士</span>
          </li>
          <li class="nav-item" :class="{ active: activeMenu === 'booking' }" @click="setActiveMenu('booking')">
            <div class="nav-icon">📅</div>
            <span class="nav-text">家政预约</span>
          </li>
          <li class="nav-item" :class="{ active: activeMenu === 'reviews' }" @click="setActiveMenu('reviews')">
            <div class="nav-icon">⭐</div>
            <span class="nav-text">服务评价</span>
          </li>
        </ul>
      </nav>

      <!-- 主内容区域 -->
    <main class="main-content">
      <!-- 首页内容 -->
      <div v-if="activeMenu === 'home'" class="content-section">
        <!-- 英雄区域 -->
        <div class="hero-section">
          <div class="hero-content">
            <div class="hero-text">
              <h1 class="hero-title">家政服务</h1>
              <h2 class="hero-subtitle">特惠</h2>
            </div>
            <div class="hero-illustration">
              <div class="room-scene">
                <div class="sofa">
                  <div class="woman">
                    <div class="woman-body"></div>
                    <div class="woman-head"></div>
                    <div class="book"></div>
                  </div>
                  <div class="child">
                    <div class="child-body"></div>
                    <div class="child-head"></div>
                  </div>
                  <div class="cat"></div>
                </div>
                <div class="plant"></div>
                <div class="clothes"></div>
                <div class="stool"></div>
                <div class="pictures">
                  <div class="picture-1"></div>
                  <div class="picture-2"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 服务分类标签 -->
        <div class="service-category-tag">
          <span>家政服务</span>
        </div>
        
        <!-- 服务网格 -->
        <div class="home-services-grid">
          <div class="service-card" @click="setActiveMenu('services')">
            <div class="service-icon daily-cleaning">🧹</div>
            <h3>日常保洁</h3>
            <p>专业工具,擦洗扫拖</p>
          </div>
          
          <div class="service-card" @click="setActiveMenu('services')">
            <div class="service-icon window-cleaning">🪟</div>
            <h3>擦玻璃</h3>
            <p>专业喷剂,洁净玻璃</p>
          </div>
          
          <div class="service-card" @click="setActiveMenu('services')">
            <div class="service-icon deep-cleaning">🧽</div>
            <h3>深度保洁</h3>
            <p>明星服务,洁净超乎想象</p>
          </div>
          
          <div class="service-card" @click="setActiveMenu('services')">
            <div class="service-icon new-home">🏠</div>
            <h3>新居开荒</h3>
            <p>新居入住前...</p>
          </div>
          
          <div class="service-card" @click="setActiveMenu('services')">
            <div class="service-icon kitchen">🍳</div>
            <h3>厨房保养</h3>
            <p>厨房深度清洁...</p>
          </div>
          
          <div class="service-card" @click="setActiveMenu('services')">
            <div class="service-icon bathroom">🚿</div>
            <h3>卫生间保养</h3>
            <p>卫生间专业清洁...</p>
          </div>
        </div>
      </div>

      <!-- 家政服务内容 -->
      <div v-if="activeMenu === 'services'" class="content-section">
        <!-- 服务分类导航 -->
        <div class="service-categories">
          <div
            v-for="category in serviceCategories"
            :key="category.id"
            class="category-item"
            :class="{ active: activeCategory === category.id }"
            @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </div>
        </div>

        <div v-if="categoryErrorMessage" class="service-status error-message">{{ categoryErrorMessage }}</div>
        <div v-else-if="isCategoryLoading" class="service-status loading-message">分类加载中...</div>

        <!-- 搜索栏 -->
        <div class="search-bar">
          <input
            type="text"
            placeholder="请输入服务名称搜索"
            class="search-input"
            v-model="serviceSearchKeyword"
            @keyup.enter.prevent="searchServices()"
            @input="handleServiceSearchInput"
          >
          <button class="search-btn" @click="searchServices()">搜索</button>
          <button v-if="serviceSearchKeyword" class="clear-btn" @click="clearServiceSearch">清空</button>
        </div>

        <div v-if="serviceErrorMessage" class="service-status error-message">{{ serviceErrorMessage }}</div>
        <div v-else-if="isServiceLoading" class="service-status loading-message">服务加载中...</div>
        <div v-else>
          <div class="services-grid" v-if="filteredServices.length > 0">
            <div v-for="service in filteredServices" :key="service.id" class="service-card">
              <div class="card-decorations">
                <div class="red-triangle top-left"></div>
                <div class="red-triangle top-right"></div>
              </div>
              <div class="service-title-bar">
                <span class="service-title">{{ service.name }}</span>
              </div>
              <div class="service-subtitle">极速上门 品质服务</div>
              <div class="service-description">{{ service.description }}</div>
              <div class="service-footer">
                <div class="service-price">¥{{ service.price }}/{{ service.unit }}</div>
                <div class="service-booked">已约{{ service.booked }}次</div>
              </div>
            </div>
          </div>

          <div v-else class="no-results">
            <div class="no-results-icon">🔍</div>
            <div class="no-results-text">未找到相关服务</div>
            <div class="no-results-hint">请尝试其他关键词或清空搜索条件</div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <div class="pagination-info">共{{ servicePageInfo.total }}条</div>
          <div class="pagination-controls">
            <button
              class="page-btn"
              :disabled="servicePageInfo.current <= 1"
              @click="changeServicePage(servicePageInfo.current - 1)"
            >‹</button>
            <button class="page-btn active">{{ servicePageInfo.current }}</button>
            <button
              class="page-btn"
              :disabled="servicePageInfo.pages === 0 || servicePageInfo.current >= servicePageInfo.pages"
              @click="changeServicePage(servicePageInfo.current + 1)"
            >›</button>
            <span class="page-summary">共{{ servicePageInfo.pages }}页</span>
          </div>
        </div>
      </div>

      <!-- 居家小贴士内容 -->
      <div v-if="activeMenu === 'tips'" class="content-section">
        <h2>居家小贴士</h2>
        
        <!-- 搜索框 -->
        <div class="tips-search-bar">
          <input 
            type="text" 
            placeholder="请输入标题关键字搜索" 
            class="tips-search-input" 
            v-model="tipsSearchKeyword"
            @keyup.enter="searchTips"
            @input="searchTips"
          >
          <button class="tips-search-btn" @click="searchTips">搜索</button>
          <button v-if="tipsSearchKeyword" class="clear-btn" @click="clearTipsSearch">清空</button>
        </div>
        
        <div class="tips-list">
          <div v-for="tip in filteredTips" :key="tip.id" class="tip-item">
            <div class="tip-image">
              <img :src="tip.image" :alt="tip.title">
            </div>
            <div class="tip-content">
              <h3>{{ tip.title }}</h3>
              <p>{{ tip.content }}</p>
              <div class="tip-meta">
                <span class="tip-views">👁️ {{ tip.views }}</span>
                <span class="tip-date">🕐 {{ tip.date }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 无搜索结果提示 -->
        <div v-if="filteredTips.length === 0" class="no-results">
          <div class="no-results-icon">🔍</div>
          <div class="no-results-text">未找到相关小贴士</div>
          <div class="no-results-hint">请尝试其他关键词或清空搜索条件</div>
        </div>
      </div>

      <!-- 家政预约内容 -->
      <div v-if="activeMenu === 'booking'" class="content-section">
        <h2>家政预约</h2>
        
        <!-- 搜索栏 -->
        <div class="booking-search-bar">
          <input 
            type="text" 
            placeholder="请输入服务名称查询" 
            class="booking-search-input" 
            v-model="bookingSearchKeyword"
            @keyup.enter="searchBookings"
            @input="searchBookings"
          >
          <button class="booking-search-btn" @click="searchBookings">查询</button>
          <button v-if="bookingSearchKeyword" class="clear-btn" @click="clearBookingSearch">清空</button>
        </div>
        
        <!-- 预约数据表格 -->
        <div class="booking-table-container">
          <table class="booking-table">
            <thead>
              <tr>
                <th>服务名称</th>
                <th>数量</th>
                <th>总金额</th>
                <th>服务者</th>
                <th>联系电话</th>
                <th>联系地址</th>
                <th>服务者电话</th>
                <th>状态</th>
                <th>开始时间</th>
                <th>结束时间</th>
                <th>预约时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="booking in filteredBookings" :key="booking.id">
                <td>{{ booking.serviceName }}</td>
                <td>{{ booking.quantity }}</td>
                <td class="amount">¥{{ booking.totalAmount }}</td>
                <td>{{ booking.serviceProvider || '-' }}</td>
                <td>{{ booking.contactPhone || '-' }}</td>
                <td>{{ booking.contactAddress || '-' }}</td>
                <td>{{ booking.serviceProviderPhone || '-' }}</td>
               <td>
                 <span class="status" :class="booking.status">
                   {{ 
                     booking.status === 'completed' ? '已完成' : 
                     booking.status === 'pending' ? '待分配' : 
                     booking.status === 'in-service' ? '服务中' :
                     booking.status === 'evaluated' ? '已评价' :
                     booking.status === 'cancelled' ? '已取消' : '未知状态'
                   }}
                 </span>
               </td>
                <td>{{ booking.startTime }}</td>
                <td>{{ booking.endTime }}</td>
                <td>{{ booking.appointmentTime || '-' }}</td>
               <td class="operation-cell">
                 <div class="operation-buttons">
                   <button v-if="booking.status === 'completed'" class="operation-btn evaluate" @click="evaluateBooking(booking)">评价</button>
                   <button v-if="booking.status === 'pending' || booking.status === 'in-service'" class="operation-btn cancel" @click="cancelBooking(booking.id)">取消</button>
                   <span v-if="booking.status === 'evaluated' || booking.status === 'cancelled'" class="no-action">-</span>
                 </div>
               </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <!-- 分页 -->
        <div class="booking-pagination">
          <div class="pagination-info">共{{ filteredBookings.length }}条</div>
          <div class="pagination-controls">
            <button class="page-btn">‹</button>
            <button class="page-btn active">1</button>
            <button class="page-btn">2</button>
            <button class="page-btn">›</button>
          </div>
        </div>
      </div>

      <!-- 服务评价内容 -->
      <div v-if="activeMenu === 'reviews'" class="content-section">
        <h2>服务评价</h2>
        
        <!-- 搜索栏 -->
        <div class="reviews-search-bar">
          <input 
            type="text" 
            placeholder="请输入服务名称查询" 
            class="reviews-search-input" 
            v-model="reviewsSearchKeyword"
            @keyup.enter="searchReviews"
            @input="searchReviews"
          >
          <button class="reviews-search-btn" @click="searchReviews">查询</button>
          <button v-if="reviewsSearchKeyword" class="clear-btn" @click="clearReviewsSearch">清空</button>
        </div>
        
        <!-- 批量删除按钮 -->
        <div class="batch-actions">
          <button class="batch-delete-btn" @click="batchDelete">批量删除</button>
        </div>
        
        <!-- 评价数据表格 -->
        <div class="reviews-table-container">
          <table class="reviews-table">
            <thead>
              <tr>
                <th>
                  <input type="checkbox" class="select-all" @change="toggleSelectAll">
                </th>
                <th>服务者</th>
                <th>服务名称</th>
                <th>评分</th>
                <th>内容</th>
                <th>评价时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="review in filteredReviews" :key="review.id">
                <td><input type="checkbox" class="row-checkbox"></td>
                <td>{{ review.serviceProvider }}</td>
                <td>{{ review.serviceName }}</td>
                <td>
                  <div class="star-rating">
                    <span v-for="i in 5" :key="i" class="star" 
                          :class="{
                            'filled': i <= Math.floor(review.rating),
                            'half': i === Math.ceil(review.rating) && review.rating % 1 !== 0,
                            'empty': i > Math.ceil(review.rating)
                          }">★</span>
                  </div>
                </td>
                <td>{{ review.content }}</td>
                <td>{{ review.reviewTime }}</td>
                <td>
                  <button class="action-btn edit" @click="editReview(review)">✏️</button>
                  <button class="action-btn delete" @click="deleteReview(review.id)">🗑️</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <!-- 分页 -->
        <div class="reviews-pagination">
          <div class="pagination-info">共{{ filteredReviews.length }}条</div>
          <div class="pagination-controls">
            <button class="page-btn">‹</button>
            <button class="page-btn active">1</button>
            <button class="page-btn">›</button>
          </div>
        </div>
      </div>
    </main>
    </div>

    <!-- 编辑评价模态框 -->
    <div v-if="showReviewModal" class="modal-overlay" @click="cancelReviewEdit">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>编辑评价</h3>
          <button class="close-btn" @click="cancelReviewEdit">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>服务提供者：</label>
            <input type="text" v-model="reviewForm.serviceProvider" class="form-input">
          </div>
          <div class="form-group">
            <label>服务名称：</label>
            <input type="text" v-model="reviewForm.serviceName" class="form-input">
          </div>
          <div class="form-group">
            <label>评分：</label>
            <div class="rating-input">
              <select v-model="reviewForm.rating" class="form-select">
                <option value="1">1星</option>
                <option value="1.5">1.5星</option>
                <option value="2">2星</option>
                <option value="2.5">2.5星</option>
                <option value="3">3星</option>
                <option value="3.5">3.5星</option>
                <option value="4">4星</option>
                <option value="4.5">4.5星</option>
                <option value="5">5星</option>
              </select>
              <div class="star-preview">
                <span v-for="i in 5" :key="i" class="star" 
                      :class="{
                        'filled': i <= Math.floor(reviewForm.rating),
                        'half': i === Math.ceil(reviewForm.rating) && reviewForm.rating % 1 !== 0,
                        'empty': i > Math.ceil(reviewForm.rating)
                      }">★</span>
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>评价内容：</label>
            <textarea v-model="reviewForm.content" class="form-textarea" rows="3"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="cancelReviewEdit">取消</button>
          <button class="btn btn-primary" @click="saveReviewEdit">保存</button>
        </div>
      </div>
    </div>

    <!-- 预约评价模态框 -->
    <div v-if="showBookingEvaluateModal" class="modal-overlay" @click="cancelBookingEvaluate">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>服务评价</h3>
          <button class="close-btn" @click="cancelBookingEvaluate">×</button>
        </div>
        <div class="modal-body">
          <div v-if="evaluatingBooking" class="booking-info">
            <h4>服务信息</h4>
            <p><strong>服务名称：</strong>{{ evaluatingBooking.serviceName }}</p>
            <p><strong>服务提供者：</strong>{{ evaluatingBooking.serviceProvider || '未知' }}</p>
            <p><strong>服务时间：</strong>{{ evaluatingBooking.startTime }} - {{ evaluatingBooking.endTime }}</p>
          </div>
          <div class="form-group">
            <label>评分：</label>
            <div class="rating-input">
              <select v-model="bookingEvaluateForm.rating" class="form-select">
                <option value="1">1星</option>
                <option value="1.5">1.5星</option>
                <option value="2">2星</option>
                <option value="2.5">2.5星</option>
                <option value="3">3星</option>
                <option value="3.5">3.5星</option>
                <option value="4">4星</option>
                <option value="4.5">4.5星</option>
                <option value="5">5星</option>
              </select>
              <div class="star-preview">
                <span v-for="i in 5" :key="i" class="star" 
                      :class="{
                        'filled': i <= Math.floor(bookingEvaluateForm.rating),
                        'half': i === Math.ceil(bookingEvaluateForm.rating) && bookingEvaluateForm.rating % 1 !== 0,
                        'empty': i > Math.ceil(bookingEvaluateForm.rating)
                      }">★</span>
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>评价内容：</label>
            <textarea v-model="bookingEvaluateForm.content" class="form-textarea" rows="4" placeholder="请分享您对这次服务的感受..."></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="cancelBookingEvaluate">取消</button>
          <button class="btn btn-primary" @click="saveBookingEvaluate">提交评价</button>
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

    <!-- 个人信息模态框 -->
    <div v-if="showPersonalInfoModal" class="modal-overlay" @click="cancelPersonalInfo">
      <div class="modal-content personal-info-modal" @click.stop>
        <div class="modal-header">
          <h3>个人信息</h3>
          <button class="close-btn" @click="cancelPersonalInfo">×</button>
        </div>
        <div class="modal-body">
          <form class="personal-info-form" @submit.prevent="savePersonalInfo">
            <!-- 头像 -->
            <div class="form-group avatar-group">
              <label class="form-label">头像</label>
              <div class="avatar-section">
                <div class="avatar-container" @click="triggerAvatarUpload">
                  <img 
                    v-if="personalInfoForm.avatar" 
                    :src="personalInfoForm.avatar" 
                    alt="头像" 
                    class="avatar-image"
                  >
                  <div v-else class="avatar-placeholder">
                    <div class="avatar-icon">🐻</div>
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
                v-model="personalInfoForm.username"
                class="form-input readonly"
                readonly
              >
            </div>

            <!-- 姓名 -->
            <div class="form-group">
              <label class="form-label">姓名</label>
              <input 
                type="text" 
                v-model="personalInfoForm.name"
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
                v-model="personalInfoForm.phone"
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
                v-model="personalInfoForm.email"
                placeholder="请输入邮箱地址"
                class="form-input"
                maxlength="50"
              >
            </div>

            <!-- 账户余额 -->
            <div class="form-group">
              <label class="form-label">账户余额</label>
              <div class="balance-display">
                <span class="balance-amount">{{ personalInfoForm.balance }}元</span>
              </div>
            </div>

            <!-- 保存按钮 -->
            <div class="form-actions">
              <button type="submit" class="save-btn">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 充值记录模态框 -->
    <div v-if="showRechargeRecordModal" class="modal-overlay" @click="cancelRechargeRecord">
      <div class="modal-content recharge-record-modal" @click.stop>
        <div class="modal-header">
          <h3>我的充值记录</h3>
          <button class="close-btn" @click="cancelRechargeRecord">×</button>
        </div>
        <div class="modal-body">
          <!-- 充值操作区域 -->
          <div class="recharge-actions">
            <button class="recharge-btn" @click="handleRecharge">我要充值</button>
            <div class="balance-info">
              <span class="balance-label">当前账户余额:</span>
              <span class="balance-amount">¥{{ currentBalance }}</span>
            </div>
          </div>
          
          <!-- 充值记录表格 -->
          <div class="recharge-table-container">
            <table class="recharge-table">
              <thead>
                <tr>
                  <th>金额</th>
                  <th>账户类型</th>
                  <th>账号</th>
                  <th>充值时间</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="record in paginatedRecords" :key="record.id" class="record-row">
                  <td class="amount">¥{{ record.amount }}</td>
                  <td class="account-type">{{ record.accountType }}</td>
                  <td class="account-number">{{ record.accountNumber }}</td>
                  <td class="recharge-time">{{ record.rechargeTime }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <!-- 分页控件 -->
          <div class="pagination">
            <div class="pagination-info">
              共{{ totalRecords }}条
            </div>
            <div class="pagination-controls">
              <button 
                class="page-btn" 
                @click="goToPage(currentPage - 1)"
                :disabled="currentPage === 1"
              >
                &lt;
              </button>
              <button 
                v-for="page in totalPages" 
                :key="page"
                class="page-btn"
                :class="{ active: page === currentPage }"
                @click="goToPage(page)"
              >
                {{ page }}
              </button>
              <button 
                class="page-btn" 
                @click="goToPage(currentPage + 1)"
                :disabled="currentPage === totalPages"
              >
                &gt;
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 充值信息模态框 -->
    <div v-if="showRechargeInfoModal" class="modal-overlay" @click="cancelRechargeInfo">
      <div class="modal-content recharge-info-modal" @click.stop>
        <div class="modal-header">
          <h3>充值信息</h3>
          <button class="close-btn" @click="cancelRechargeInfo">×</button>
        </div>
        <div class="modal-body">
          <form class="recharge-info-form" @submit.prevent="confirmRecharge">
            <!-- 充值金额 -->
            <div class="form-group">
              <label class="form-label">充值金额 <span class="required">*</span></label>
              <div class="amount-input-wrapper">
                <button type="button" class="amount-btn decrease" @click="decreaseAmount">-</button>
                <input 
                  type="number" 
                  v-model="rechargeInfoForm.amount"
                  placeholder="金额"
                  class="form-input amount-input"
                  min="1"
                  max="10000"
                  step="1"
                >
                <button type="button" class="amount-btn increase" @click="increaseAmount">+</button>
              </div>
            </div>

            <!-- 支付账号 -->
            <div class="form-group">
              <label class="form-label">支付账号 <span class="required">*</span></label>
              <input 
                type="text" 
                v-model="rechargeInfoForm.paymentAccount"
                placeholder="支付账号"
                class="form-input"
                maxlength="20"
              >
            </div>

            <!-- 支付方式 -->
            <div class="form-group">
              <label class="form-label">支付方式</label>
              <div class="payment-methods">
                <div class="payment-option">
                  <input 
                    type="radio" 
                    id="wechat" 
                    value="微信支付" 
                    v-model="rechargeInfoForm.paymentMethod"
                    class="payment-radio"
                  >
                  <label for="wechat" class="payment-label">
                    <div class="payment-icon wechat-icon">💚</div>
                    <div class="payment-text">
                      <div class="payment-name">微信支付</div>
                      <div class="payment-subtitle">Wechat Pay</div>
                    </div>
                  </label>
                </div>
                <div class="payment-option">
                  <input 
                    type="radio" 
                    id="alipay" 
                    value="支付宝" 
                    v-model="rechargeInfoForm.paymentMethod"
                    class="payment-radio"
                  >
                  <label for="alipay" class="payment-label">
                    <div class="payment-icon alipay-icon">🔵</div>
                    <div class="payment-text">
                      <div class="payment-name">支付宝</div>
                      <div class="payment-subtitle">ALIPAY</div>
                    </div>
                  </label>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" @click="cancelRechargeInfo">取消</button>
              <button type="submit" class="btn btn-primary">确定</button>
            </div>
          </form>
        </div>
        </div>
      </div>


    </div>
  </template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { categoryAPI, serviceAPI, userAPI, removeToken, getToken } from '@/utils/api'

interface ServiceCategoryOption {
  id: string
  name: string
}

interface ServiceCard {
  id: number
  name: string
  description: string
  price: number
  unit: string
  booked: number
  categoryId: number | null
}

type ServiceQueryMode = 'popular' | 'category' | 'search'

interface ServicePageInfo {
  total: number
  current: number
  size: number
  pages: number
}

const router = useRouter()

// 用户信息
const userInfo = ref({
  username: '用户',
  role: 'user'
})

const userAvatar = ref('')

const personalInfoForm = ref({
  avatar: '',
  username: '',
  name: '',
  phone: '',
  email: '',
  balance: '0.00'
})

const currentBalance = ref(0)

const initializeUser = (): boolean => {
  const token = getToken()
  if (!token) {
    router.replace('/login')
    return false
  }

  const storedUserInfo = localStorage.getItem('userInfo')
  if (!storedUserInfo) {
    router.replace('/login')
    return false
  }

  try {
    const parsed = JSON.parse(storedUserInfo)
    if (parsed.role !== 'user') {
      router.replace('/login')
      return false
    }

    const storedUserData = parsed.userData || {}
    const displayName = storedUserData.realName || parsed.username || '用户'

    userInfo.value = {
      username: displayName,
      role: parsed.role
    }

    if (storedUserData.avatar) {
      userAvatar.value = storedUserData.avatar
    }

    const balanceValue = Number(storedUserData.balance ?? 0)
    currentBalance.value = Number.isNaN(balanceValue) ? 0 : balanceValue

    personalInfoForm.value = {
      avatar: storedUserData.avatar || '',
      username: storedUserData.username || parsed.username || '',
      name: displayName,
      phone: storedUserData.phone || '',
      email: storedUserData.email || '',
      balance: Number.isNaN(balanceValue) ? '0.00' : balanceValue.toFixed(2)
    }

    return true
  } catch (error) {
    console.error('解析用户信息失败:', error)
    localStorage.removeItem('userInfo')
    router.replace('/login')
    return false
  }
}

const fetchUserProfile = async () => {
  try {
    const response = await userAPI.getUserInfo()
    if (response?.data) {
      const user = response.data
      const displayName = user.realName || user.username || userInfo.value.username

      userInfo.value.username = displayName

      personalInfoForm.value = {
        avatar: user.avatar || '',
        username: user.username || '',
        name: displayName,
        phone: user.phone || '',
        email: user.email || '',
        balance: Number(user.balance ?? 0).toFixed(2)
      }

      const balanceValue = Number(user.balance ?? 0)
      currentBalance.value = Number.isNaN(balanceValue) ? 0 : balanceValue

      if (user.avatar) {
        userAvatar.value = user.avatar
      }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

// 当前激活的菜单
const activeMenu = ref('home')

// 用户下拉菜单显示状态
const showUserDropdown = ref(false)

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

// 个人信息相关
const showPersonalInfoModal = ref(false)

// 头像输入框引用
const avatarInput = ref<HTMLInputElement | null>(null)

// 充值记录相关
const showRechargeRecordModal = ref(false)
const currentPage = ref(1)
const pageSize = ref(5)

// 充值信息相关
const showRechargeInfoModal = ref(false)
const rechargeInfoForm = ref({
  amount: 100,
  paymentAccount: '',
  paymentMethod: '微信支付'
})


// 模拟充值记录数据
const rechargeRecords = ref([
  {
    id: 1,
    amount: 500,
    accountType: '支付宝',
    accountNumber: '13900889977',
    rechargeTime: '2025-05-22 22:31:18'
  },
  {
    id: 2,
    amount: 1000,
    accountType: '支付宝',
    accountNumber: '13988997788',
    rechargeTime: '2025-05-21 09:09:20'
  },
  {
    id: 3,
    amount: 1000,
    accountType: '微信支付',
    accountNumber: '13988997788',
    rechargeTime: '2025-05-21 08:56:57'
  },
  {
    id: 4,
    amount: 500,
    accountType: '支付宝',
    accountNumber: '13900889977',
    rechargeTime: '2025-05-20 08:56:36'
  },
  {
    id: 5,
    amount: 1000,
    accountType: '支付宝',
    accountNumber: '13988997788',
    rechargeTime: '2025-05-18 12:03:06'
  },
  {
    id: 6,
    amount: 200,
    accountType: '微信支付',
    accountNumber: '13900889977',
    rechargeTime: '2025-05-15 14:20:15'
  },
  {
    id: 7,
    amount: 800,
    accountType: '支付宝',
    accountNumber: '13988997788',
    rechargeTime: '2025-05-12 10:15:30'
  },
  {
    id: 8,
    amount: 300,
    accountType: '微信支付',
    accountNumber: '13900889977',
    rechargeTime: '2025-05-10 16:45:22'
  },
  {
    id: 9,
    amount: 1500,
    accountType: '支付宝',
    accountNumber: '13988997788',
    rechargeTime: '2025-05-08 09:30:45'
  },
  {
    id: 10,
    amount: 600,
    accountType: '微信支付',
    accountNumber: '13900889977',
    rechargeTime: '2025-05-05 13:25:18'
  },
  {
    id: 11,
    amount: 1200,
    accountType: '支付宝',
    accountNumber: '13988997788',
    rechargeTime: '2025-05-02 11:40:33'
  },
  {
    id: 12,
    amount: 400,
    accountType: '微信支付',
    accountNumber: '13900889977',
    rechargeTime: '2025-04-30 15:55:07'
  }
])

// 充值记录计算属性
const totalRecords = computed(() => rechargeRecords.value.length)
const totalPages = computed(() => Math.ceil(totalRecords.value / pageSize.value))
const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return rechargeRecords.value.slice(start, end)
})

// 小贴士搜索关键词
const tipsSearchKeyword = ref('')

// 预约搜索关键词
const bookingSearchKeyword = ref('')

// 评价搜索关键词
const reviewsSearchKeyword = ref('')

// 服务搜索关键词
const serviceSearchKeyword = ref('')

const servicePageSize = 12

const filteredServices = ref<ServiceCard[]>([])
const servicePageInfo = ref<ServicePageInfo>({
  total: 0,
  current: 1,
  size: servicePageSize,
  pages: 0
})

const serviceCategories = ref<ServiceCategoryOption[]>([{ id: 'all', name: '全部' }])
const activeCategory = ref<string>('all')
const isCategoryLoading = ref(false)
const categoryErrorMessage = ref('')
const isServiceLoading = ref(false)
const serviceErrorMessage = ref('')
const lastServiceQuery = ref<{ mode: ServiceQueryMode; categoryId?: string; keyword?: string }>({ mode: 'popular' })
let serviceSearchTimer: ReturnType<typeof setTimeout> | null = null

const mapServiceRecord = (record: any): ServiceCard => ({
  id: Number(record?.id ?? 0),
  name: record?.name || '未命名服务',
  description: record?.description || record?.content || '暂无描述',
  price: Number(record?.price ?? 0),
  unit: record?.unit || '次',
  booked: Number(record?.bookingCount ?? record?.booked ?? 0),
  categoryId: typeof record?.categoryId === 'number'
    ? record.categoryId
    : record?.categoryId
      ? Number(record.categoryId)
      : null
})

const loadServices = async (
  fetcher: () => Promise<any>,
  mode: ServiceQueryMode,
  payload: { categoryId?: string; keyword?: string } = {},
  page = 1
) => {
  isServiceLoading.value = true
  serviceErrorMessage.value = ''
  try {
    const response = await fetcher()
    const pageData = response?.data
    const records = Array.isArray(pageData?.records)
      ? pageData.records
      : Array.isArray(pageData)
        ? pageData
        : []

    filteredServices.value = records.map(mapServiceRecord)

    const total = typeof pageData?.total === 'number' ? pageData.total : filteredServices.value.length
    const size = typeof pageData?.size === 'number' ? pageData.size : servicePageSize
    const pages =
      typeof pageData?.pages === 'number'
        ? pageData.pages
        : total > 0
          ? Math.ceil(total / size)
          : 0

    servicePageInfo.value = {
      total,
      current: page,
      size,
      pages
    }

    lastServiceQuery.value = { mode, ...payload }
  } catch (error: any) {
    console.error('加载服务失败:', error)
    serviceErrorMessage.value = error?.message || '服务加载失败'
    filteredServices.value = []
    servicePageInfo.value = {
      total: 0,
      current: 1,
      size: servicePageSize,
      pages: 0
    }
  } finally {
    isServiceLoading.value = false
  }
}

const fetchPopularServices = async (page = 1) => {
  await loadServices(
    () => serviceAPI.getPopularServices(page, servicePageSize),
    'popular',
    {},
    page
  )
}

const fetchServicesByCategory = async (categoryId: string, page = 1) => {
  if (categoryId === 'all') {
    await fetchPopularServices(page)
    return
  }

  const numericId = Number(categoryId)
  if (Number.isNaN(numericId)) {
    serviceErrorMessage.value = '无效的分类ID'
    filteredServices.value = []
    servicePageInfo.value = {
      total: 0,
      current: 1,
      size: servicePageSize,
      pages: 0
    }
    return
  }

  await loadServices(
    () => serviceAPI.getServicesByCategory(numericId, page, servicePageSize),
    'category',
    { categoryId },
    page
  )
}

const searchServices = async (page = 1, keywordParam?: string) => {
  const keyword = keywordParam ?? serviceSearchKeyword.value.trim()

  if (!keyword) {
    await fetchServicesByCategory(activeCategory.value, page)
    return
  }

  serviceSearchKeyword.value = keyword

  await loadServices(
    () => serviceAPI.searchServices(keyword, page, servicePageSize),
    'search',
    { keyword },
    page
  )
}

const clearServiceSearch = async () => {
  serviceSearchKeyword.value = ''
  await fetchServicesByCategory(activeCategory.value, 1)
}

const handleServiceSearchInput = () => {
  if (serviceSearchTimer) {
    clearTimeout(serviceSearchTimer)
  }

  serviceSearchTimer = window.setTimeout(() => {
    searchServices()
  }, 400)
}

const selectCategory = async (categoryId: string) => {
  activeCategory.value = categoryId
  serviceSearchKeyword.value = ''
  await fetchServicesByCategory(categoryId, 1)
}

const fetchCategories = async () => {
  isCategoryLoading.value = true
  categoryErrorMessage.value = ''
  try {
    const response = await categoryAPI.getPublicCategories()
    const categories = Array.isArray(response?.data) ? response.data : []

    const normalized = categories.map((category: any): ServiceCategoryOption => ({
      id: String(category?.id),
      name: category?.name || '未命名分类'
    }))

    serviceCategories.value = [
      { id: 'all', name: '全部' },
      ...normalized
    ]
  } catch (error: any) {
    console.error('加载分类失败:', error)
    categoryErrorMessage.value = error?.message || '服务分类加载失败'
    serviceCategories.value = [{ id: 'all', name: '全部' }]
  } finally {
    isCategoryLoading.value = false
  }
}

const handleServiceSectionActivation = () => {
  if (filteredServices.value.length === 0 && !isServiceLoading.value) {
    fetchServicesByCategory(activeCategory.value, 1)
  }
}

const changeServicePage = async (page: number) => {
  if (page < 1 || page === servicePageInfo.value.current) {
    return
  }

  if (servicePageInfo.value.pages > 0 && page > servicePageInfo.value.pages) {
    return
  }

  const query = lastServiceQuery.value

  if (query.mode === 'search' && query.keyword) {
    await searchServices(page, query.keyword)
  } else if (query.mode === 'category' && query.categoryId) {
    await fetchServicesByCategory(query.categoryId, page)
  } else {
    await fetchPopularServices(page)
  }
}

// 小贴士数据
const allTips = ref([
  {
    id: 1,
    title: '让生活空间焕然一新',
    content: '厨房是家中油污的"重灾区",但掌握一些清洁技巧,就能轻松应对。对于顽固的灶台油污,我们可以自制清洁神器。',
    views: 33,
    date: '2025-05-09 15:56:24',
    image: 'https://via.placeholder.com/120x80/ff6b6b/ffffff?text=清洁'
  },
  {
    id: 2,
    title: '打造井井有条的家',
    content: '衣物收纳要根据季节和使用频率进行分类。对于不常穿的过季衣物,可以使用真空压缩袋收纳,这样不仅能节省空间,还能防止衣物受潮发霉。',
    views: 6,
    date: '2025-05-09 15:56:24',
    image: 'https://via.placeholder.com/120x80/4ecdc4/ffffff?text=收纳'
  },
  {
    id: 3,
    title: '守护家人的平安',
    content: '家中的电器使用频繁,用电安全至关重要。要定期检查电器线路,避免电线老化、破损。不使用电器时,要及时拔掉插头,避免待机状态下的耗电和安全隐患。',
    views: 1,
    date: '2025-05-09 15:56:24',
    image: 'https://via.placeholder.com/120x80/45b7d1/ffffff?text=安全'
  },
  {
    id: 4,
    title: '卫生间深度清洁指南:让卫浴空间焕新如初',
    content: '卫生间作为家中使用频率极高的区域,极易藏污纳垢,不仅影响美观,还可能滋生大量细菌,危害家人健康。掌握科学有效的清洁方法,能让卫生间时刻保持干净整洁,为生活带来舒适与安心',
    views: 2,
    date: '2025-05-09 15:56:24',
    image: 'https://via.placeholder.com/120x80/96ceb4/ffffff?text=卫生间'
  }
])

// 过滤后的小贴士数据
const filteredTips = ref([...allTips.value])

// 预约数据
const allBookings = ref([
  {
    id: 1,
    serviceName: '新房精细开荒1平米【包运送垃圾/包验收】',
    quantity: 1,
    totalAmount: 9.9,
    serviceProvider: '家政小丁',
    contactPhone: '张飞',
    contactAddress: '枫叶社区...',
    serviceProviderPhone: '13988997788',
    status: 'completed',
    startTime: '2025-05-09',
    endTime: '2025-05-09',
    appointmentTime: '2025-05-08'
  },
  {
    id: 2,
    serviceName: '地板打蜡10平米',
    quantity: 1,
    totalAmount: 66,
    serviceProvider: '家政小敏',
    contactPhone: '13988997788',
    contactAddress: '枫叶社区...',
    serviceProviderPhone: '13988997788',
    status: 'completed',
    startTime: '2025-05-09',
    endTime: '2025-05-09',
    appointmentTime: '2025-05-08'
  },
  {
    id: 3,
    serviceName: '地板打蜡10平米',
    quantity: 1,
    totalAmount: 66,
    serviceProvider: '',
    contactPhone: '13999887788',
    contactAddress: '合肥市枫...',
    serviceProviderPhone: '',
    status: 'pending',
    startTime: '2025-05-10',
    endTime: '2025-05-10',
    appointmentTime: ''
  },
  {
    id: 4,
    serviceName: '厨房保养清洁【包含油烟机】',
    quantity: 1,
    totalAmount: 338,
    serviceProvider: '家政小黄',
    contactPhone: '13900998877',
    contactAddress: '合肥市枫...',
    serviceProviderPhone: '13988993377',
    status: 'in-service',
    startTime: '2025-05-10',
    endTime: '2025-05-10',
    appointmentTime: ''
  },
  {
    id: 5,
    serviceName: '深度保洁5小时【全屋大扫除】',
    quantity: 2,
    totalAmount: 1076,
    serviceProvider: '家政小黄',
    contactPhone: '',
    contactAddress: '',
    serviceProviderPhone: '',
    status: 'completed',
    startTime: '2025-05-11',
    endTime: '2025-05-11',
    appointmentTime: '2025-05-10'
  }
])

// 过滤后的预约数据
const filteredBookings = ref([...allBookings.value])

// 评价数据
const allReviews = ref([
  {
    id: 1,
    serviceProvider: '家政小丁',
    serviceName: '新房精细开荒1平米【包运送垃圾/包验收】',
    rating: 5,
    content: '非常好',
    reviewTime: '2025-05-17 22:51:47'
  },
  {
    id: 2,
    serviceProvider: '家政小敏',
    serviceName: '地板打蜡10平米',
    rating: 4.5,
    content: '我很满意!',
    reviewTime: '2025-05-17 22:29:12'
  },
  {
    id: 3,
    serviceProvider: '家政小月',
    serviceName: '沙发保养清洁【包含皮革养护】',
    rating: 4,
    content: '很好!',
    reviewTime: '2025-05-17 22:29:12'
  },
  {
    id: 4,
    serviceProvider: '家政小黄',
    serviceName: '深度保洁5小时【全屋大扫除】',
    rating: 4.5,
    content: '非常好!',
    reviewTime: '2025-05-13 09:00:00'
  },
  {
    id: 5,
    serviceProvider: '家政小丁',
    serviceName: '4小时全屋日常保洁【中等户型推荐】快速上门',
    rating: 3.5,
    content: '非常好!',
    reviewTime: '2025-05-09 16:45:17'
  }
])

// 过滤后的评价数据
const filteredReviews = ref([...allReviews.value])

// 评价编辑相关
const showReviewModal = ref(false)
const editingReview = ref<any>(null)
const reviewForm = ref({
  serviceProvider: '',
  serviceName: '',
  rating: 5,
  content: ''
})

// 预约评价相关
const showBookingEvaluateModal = ref(false)
const evaluatingBooking = ref<any>(null)
const bookingEvaluateForm = ref({
  rating: 5,
  content: ''
})

// 预约表单相关
const bookingForm = ref({
  serviceType: '',
  date: '',
  time: '',
  duration: '',
  notes: ''
})


// 设置激活菜单
const setActiveMenu = (menu: string) => {
  activeMenu.value = menu

  if (menu === 'services') {
    handleServiceSectionActivation()
  }
}

// 小贴士搜索功能
const searchTips = () => {
  const keyword = tipsSearchKeyword.value.trim().toLowerCase()
  if (keyword === '') {
    filteredTips.value = [...allTips.value]
  } else {
    filteredTips.value = allTips.value.filter(tip => 
      tip.title.toLowerCase().includes(keyword) ||
      tip.content.toLowerCase().includes(keyword)
    )
  }
}

// 清空小贴士搜索
const clearTipsSearch = () => {
  tipsSearchKeyword.value = ''
  filteredTips.value = [...allTips.value]
}

// 提交预约
const submitBooking = () => {
  console.log('提交预约:', bookingForm.value)
  alert('预约提交成功！我们会尽快与您联系。')
  // 重置表单
  bookingForm.value = {
    serviceType: '',
    date: '',
    time: '',
    duration: '',
    notes: ''
  }
}


// 预约搜索功能
const searchBookings = () => {
  const keyword = bookingSearchKeyword.value.trim().toLowerCase()
  if (keyword === '') {
    filteredBookings.value = [...allBookings.value]
  } else {
    filteredBookings.value = allBookings.value.filter(booking => 
      booking.serviceName.toLowerCase().includes(keyword) ||
      booking.serviceProvider.toLowerCase().includes(keyword) ||
      booking.contactPhone.toLowerCase().includes(keyword) ||
      booking.contactAddress.toLowerCase().includes(keyword)
    )
  }
}

// 清空预约搜索
const clearBookingSearch = () => {
  bookingSearchKeyword.value = ''
  filteredBookings.value = [...allBookings.value]
}

// 评价搜索功能
const searchReviews = () => {
  const keyword = reviewsSearchKeyword.value.trim().toLowerCase()
  if (keyword === '') {
    filteredReviews.value = [...allReviews.value]
  } else {
    filteredReviews.value = allReviews.value.filter(review => 
      review.serviceProvider.toLowerCase().includes(keyword) ||
      review.serviceName.toLowerCase().includes(keyword) ||
      review.content.toLowerCase().includes(keyword)
    )
  }
}

// 清空评价搜索
const clearReviewsSearch = () => {
  reviewsSearchKeyword.value = ''
  filteredReviews.value = [...allReviews.value]
}

// 编辑评价
const editReview = (review: any) => {
  editingReview.value = review
  reviewForm.value = {
    serviceProvider: review.serviceProvider,
    serviceName: review.serviceName,
    rating: review.rating,
    content: review.content
  }
  showReviewModal.value = true
}

// 删除评价
const deleteReview = (reviewId: number) => {
  if (confirm('确定要删除这条评价吗？')) {
    const index = allReviews.value.findIndex(review => review.id === reviewId)
    if (index > -1) {
      allReviews.value.splice(index, 1)
      // 重新搜索以更新显示
      searchReviews()
    }
  }
}

// 保存评价编辑
const saveReviewEdit = () => {
  if (editingReview.value && editingReview.value.id) {
    const index = allReviews.value.findIndex(review => review.id === editingReview.value.id)
    if (index > -1) {
      allReviews.value[index] = {
        ...allReviews.value[index],
        serviceProvider: reviewForm.value.serviceProvider,
        serviceName: reviewForm.value.serviceName,
        rating: reviewForm.value.rating,
        content: reviewForm.value.content
      }
      // 重新搜索以更新显示
      searchReviews()
    }
  }
  showReviewModal.value = false
  editingReview.value = null
}

// 取消编辑
const cancelReviewEdit = () => {
  showReviewModal.value = false
  editingReview.value = null
  reviewForm.value = {
    serviceProvider: '',
    serviceName: '',
    rating: 5,
    content: ''
  }
}

// 预约评价功能
const evaluateBooking = (booking: any) => {
  evaluatingBooking.value = booking
  bookingEvaluateForm.value = {
    rating: 5,
    content: ''
  }
  showBookingEvaluateModal.value = true
}

// 保存预约评价
const saveBookingEvaluate = () => {
  if (evaluatingBooking.value) {
    // 创建新的评价记录
    const newReview = {
      id: allReviews.value.length + 1,
      serviceProvider: (evaluatingBooking.value.serviceProvider || '未知服务者') as string,
      serviceName: (evaluatingBooking.value.serviceName || '') as string,
      rating: bookingEvaluateForm.value.rating,
      content: bookingEvaluateForm.value.content,
      reviewTime: new Date().toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      }).replace(/\//g, '-')
    }
    
    // 添加到评价列表
    allReviews.value.unshift(newReview)
    
    // 更新预约状态为已评价
    if (evaluatingBooking.value.id !== undefined) {
      const bookingIndex = allBookings.value.findIndex(booking => booking.id === evaluatingBooking.value.id)
      if (bookingIndex > -1) {
        allBookings.value[bookingIndex].status = 'evaluated'
      }
    }
    
    // 重新搜索以更新显示
    searchBookings()
    searchReviews()
    
    alert('评价提交成功！')
  }
  showBookingEvaluateModal.value = false
  evaluatingBooking.value = null
}

// 取消预约评价
const cancelBookingEvaluate = () => {
  showBookingEvaluateModal.value = false
  evaluatingBooking.value = null
  bookingEvaluateForm.value = {
    rating: 5,
    content: ''
  }
}

// 取消预约
const cancelBooking = (bookingId: number) => {
  if (confirm('确定要取消这个预约吗？取消后无法恢复。')) {
    const index = allBookings.value.findIndex(booking => booking.id === bookingId)
    if (index > -1) {
      allBookings.value[index].status = 'cancelled'
      // 重新搜索以更新显示
      searchBookings()
    }
    alert('预约已取消！')
  }
}

// 全选/取消全选
const toggleSelectAll = (event: Event) => {
  const target = event.target as HTMLInputElement
  const checkboxes = document.querySelectorAll('.row-checkbox') as NodeListOf<HTMLInputElement>
  checkboxes.forEach(checkbox => {
    checkbox.checked = target.checked
  })
}

// 批量删除
const batchDelete = () => {
  const selectedCheckboxes = document.querySelectorAll('.row-checkbox:checked')
  if (selectedCheckboxes.length === 0) {
    alert('请先选择要删除的评价记录')
    return
  }
  if (confirm(`确定要删除选中的 ${selectedCheckboxes.length} 条评价记录吗？`)) {
    alert('批量删除成功！')
  }
}

// 切换用户下拉菜单
const toggleUserDropdown = () => {
  showUserDropdown.value = !showUserDropdown.value
}

// 处理我的收藏
const handleMyFavorites = () => {
  showUserDropdown.value = false
  alert('我的收藏功能')
}

// 处理充值记录
const handleRechargeRecords = () => {
  showUserDropdown.value = false
  showRechargeRecordModal.value = true
}

// 处理个人信息
const handlePersonalInfo = () => {
  showUserDropdown.value = false
  // 显示个人信息模态框
  showPersonalInfoModal.value = true
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

// 验证修改密码表单
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
    passwordErrors.value.newPassword = '密码长度至少8个字符'
    isValid = false
  } else if (!/[a-zA-Z]/.test(changePasswordForm.value.newPassword) || !/[0-9]/.test(changePasswordForm.value.newPassword)) {
    passwordErrors.value.newPassword = '密码必须包含字母和数字'
    isValid = false
  } else if (changePasswordForm.value.newPassword === changePasswordForm.value.oldPassword) {
    passwordErrors.value.newPassword = '新密码不能与原密码相同'
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
  // 验证表单
  if (!validatePasswordForm()) {
    return
  }
  
  // 模拟API调用
  console.log('修改密码请求:', changePasswordForm.value)
  
  // 模拟成功响应
  setTimeout(() => {
    alert('密码修改成功！请重新登录')
    // 清除用户信息并跳转到登录页
    localStorage.removeItem('userInfo')
    window.location.href = '/login'
  }, 500)
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
    }
    reader.readAsDataURL(file)
  }
}

// 保存个人信息
const savePersonalInfo = () => {
  // 验证必填字段
  if (!personalInfoForm.value.name.trim()) {
    alert('请输入姓名')
    return
  }
  
  if (!personalInfoForm.value.phone.trim()) {
    alert('请输入电话号码')
    return
  }
  
  if (!personalInfoForm.value.email.trim()) {
    alert('请输入邮箱地址')
    return
  }
  
  // 验证电话号码格式
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(personalInfoForm.value.phone)) {
    alert('请输入正确的手机号码')
    return
  }
  
  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(personalInfoForm.value.email)) {
    alert('请输入正确的邮箱地址')
    return
  }
  
  // 模拟保存
  console.log('保存个人信息:', personalInfoForm.value)

  userInfo.value.username = personalInfoForm.value.name

  const storedInfo = localStorage.getItem('userInfo')
  if (storedInfo) {
    try {
      const parsed = JSON.parse(storedInfo)
      parsed.userData = parsed.userData || {}
      parsed.userData.realName = personalInfoForm.value.name
      parsed.userData.phone = personalInfoForm.value.phone
      parsed.userData.email = personalInfoForm.value.email
      parsed.userData.avatar = personalInfoForm.value.avatar
      localStorage.setItem('userInfo', JSON.stringify(parsed))
    } catch (error) {
      console.error('更新本地用户信息失败:', error)
    }
  }
  
  alert('个人信息保存成功！')
  
  // 关闭模态框
  showPersonalInfoModal.value = false
}

// 取消个人信息编辑
const cancelPersonalInfo = () => {
  showPersonalInfoModal.value = false
}

// 充值记录相关方法
const cancelRechargeRecord = () => {
  showRechargeRecordModal.value = false
}

// 处理充值
const handleRecharge = () => {
  showRechargeInfoModal.value = true
}

// 分页跳转
const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

// 充值信息相关方法
const cancelRechargeInfo = () => {
  showRechargeInfoModal.value = false
  // 重置表单
  rechargeInfoForm.value = {
    amount: 100,
    paymentAccount: '',
    paymentMethod: '微信支付'
  }
}

// 增加充值金额
const increaseAmount = () => {
  if (rechargeInfoForm.value.amount < 10000) {
    rechargeInfoForm.value.amount += 10
  }
}

// 减少充值金额
const decreaseAmount = () => {
  if (rechargeInfoForm.value.amount > 1) {
    rechargeInfoForm.value.amount -= 10
  }
}

// 确认充值
const confirmRecharge = () => {
  // 验证必填字段
  if (!rechargeInfoForm.value.amount || rechargeInfoForm.value.amount <= 0) {
    alert('请输入有效的充值金额')
    return
  }
  
  if (!rechargeInfoForm.value.paymentAccount.trim()) {
    alert('请输入支付账号')
    return
  }
  
  if (!rechargeInfoForm.value.paymentMethod) {
    alert('请选择支付方式')
    return
  }
  
  // 模拟充值处理
  console.log('充值信息:', rechargeInfoForm.value)
  
  // 生成新的充值记录
  const newRecord = {
    id: rechargeRecords.value.length + 1,
    amount: rechargeInfoForm.value.amount,
    accountType: rechargeInfoForm.value.paymentMethod,
    accountNumber: rechargeInfoForm.value.paymentAccount,
    rechargeTime: new Date().toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    }).replace(/\//g, '-')
  }
  
  // 添加到充值记录列表的开头
  rechargeRecords.value.unshift(newRecord)
  
  // 更新账户余额
  currentBalance.value += rechargeInfoForm.value.amount

  // 同步更新个人信息中的余额
  personalInfoForm.value.balance = currentBalance.value.toFixed(2)
  
  alert(`充值成功！充值金额：¥${rechargeInfoForm.value.amount}`)
  
  // 关闭模态框
  showRechargeInfoModal.value = false
  
  // 重置表单
  rechargeInfoForm.value = {
    amount: 100,
    paymentAccount: '',
    paymentMethod: '微信支付'
  }
}


// 退出登录
const logout = () => {
  if (confirm('确定要退出登录吗？')) {
    removeToken()
    localStorage.removeItem('userInfo')
    showUserDropdown.value = false
    router.replace('/login')
  }
}

// 点击外部关闭下拉菜单
const handleClickOutside = (event: Event) => {
  const target = event.target as HTMLElement
  if (!target.closest('.user-section')) {
    showUserDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)

  if (!initializeUser()) {
    return
  }

  fetchUserProfile()
  fetchCategories()
  fetchServicesByCategory(activeCategory.value, 1)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)

  if (serviceSearchTimer) {
    clearTimeout(serviceSearchTimer)
    serviceSearchTimer = null
  }
})

</script>

<style scoped>
/* 全局重置 */
* {
  box-sizing: border-box;
}

.user-home-container {
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

.logo-section .logo {
  color: white;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
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
  z-index: 1000;
  min-width: 150px;
  overflow: hidden;
  margin-top: 5px;
}

.dropdown-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  color: #333;
  font-size: 14px;
  border-bottom: 1px solid #f0f0f0;
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background: #f0f8ff;
  color: #007bff;
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
  padding: 20px 0;
  margin: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background: #f8f9fa;
}

.nav-item.active {
  background: #e3f2fd;
  border-left-color: #8B4513;
  color: #8B4513;
}

.nav-icon {
  margin-right: 10px;
  font-size: 16px;
}

.nav-text {
  font-size: 13px;
  font-weight: 500;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
  background: #e3f2fd;
  box-sizing: border-box;
}

.content-section h2 {
  color: #333;
  margin-bottom: 25px;
  font-size: 24px;
  font-weight: 600;
}

.content-section p {
  color: #666;
  font-size: 16px;
  line-height: 1.6;
}

/* 首页样式 */
.hero-section {
  background: linear-gradient(135deg, #ffb3ba 0%, #ffdfba 100%);
  border-radius: 12px;
  margin-bottom: 30px;
  overflow: hidden;
  position: relative;
  min-height: 300px;
}

.hero-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40px;
  height: 100%;
}

.hero-text {
  flex: 1;
  z-index: 2;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  color: #dc3545;
  margin: 0 0 10px 0;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 36px;
  font-weight: 600;
  color: #dc3545;
  margin: 0;
  line-height: 1.2;
}

.hero-illustration {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  z-index: 2;
}

/* 房间场景样式 */
.room-scene {
  position: relative;
  width: 300px;
  height: 200px;
  background: #ffb3ba;
  border-radius: 8px;
  overflow: hidden;
}

.sofa {
  position: absolute;
  bottom: 20px;
  right: 20px;
  width: 120px;
  height: 60px;
  background: #87ceeb;
  border-radius: 8px;
}

.woman {
  position: absolute;
  left: 10px;
  top: -20px;
  width: 30px;
  height: 40px;
}

.woman-body {
  width: 20px;
  height: 25px;
  background: #ff69b4;
  border-radius: 4px;
  margin: 0 auto;
}

.woman-head {
  width: 15px;
  height: 15px;
  background: #8b4513;
  border-radius: 50%;
  margin: 2px auto;
}

.book {
  position: absolute;
  right: -5px;
  top: 5px;
  width: 8px;
  height: 10px;
  background: #4169e1;
  border-radius: 2px;
}

.child {
  position: absolute;
  left: 40px;
  top: -15px;
  width: 20px;
  height: 30px;
}

.child-body {
  width: 15px;
  height: 18px;
  background: #ffff00;
  border-radius: 3px;
  margin: 0 auto;
}

.child-head {
  width: 12px;
  height: 12px;
  background: #8b4513;
  border-radius: 50%;
  margin: 2px auto;
}

.cat {
  position: absolute;
  right: 5px;
  top: -10px;
  width: 15px;
  height: 10px;
  background: #ffffff;
  border-radius: 8px;
}

.plant {
  position: absolute;
  bottom: 10px;
  left: 20px;
  width: 20px;
  height: 40px;
  background: #32cd32;
  border-radius: 10px;
}

.clothes {
  position: absolute;
  left: 10px;
  top: 20px;
  width: 30px;
  height: 20px;
  background: #ffa500;
  border-radius: 4px;
}

.stool {
  position: absolute;
  left: 50px;
  bottom: 10px;
  width: 15px;
  height: 15px;
  background: #8b4513;
  border-radius: 2px;
}

.pictures {
  position: absolute;
  top: 20px;
  right: 20px;
}

.picture-1 {
  width: 20px;
  height: 15px;
  background: #87ceeb;
  border-radius: 2px;
  margin-bottom: 5px;
}

.picture-2 {
  width: 15px;
  height: 12px;
  background: #ff69b4;
  border-radius: 2px;
}

/* 服务分类标签 */
.service-category-tag {
  margin-bottom: 20px;
}

.service-category-tag span {
  background: #20b2aa;
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

/* 首页服务网格 */
.home-services-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.service-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border: 2px solid transparent;
}

.service-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  border-color: #20b2aa;
}

.service-icon {
  font-size: 40px;
  margin-bottom: 15px;
  display: block;
}

.service-card h3 {
  color: #333;
  margin-bottom: 10px;
  font-size: 18px;
  font-weight: 600;
}

.service-card p {
  color: #666;
  font-size: 14px;
  margin: 0;
  line-height: 1.4;
}

/* 服务分类导航 */
.service-categories {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
  padding: 20px;
  border-bottom: 1px solid #eee;
  overflow-x: auto;
  background: #fafafa;
  border-radius: 8px;
}

.category-item {
  padding: 8px 0;
  cursor: pointer;
  color: #666;
  font-size: 14px;
  white-space: nowrap;
  transition: all 0.3s ease;
  border-bottom: 2px solid transparent;
  font-weight: 500;
  position: relative;
}

.category-item:hover {
  color: #8B4513;
}

.category-item.active {
  color: #8B4513;
  border-bottom-color: #8B4513;
  font-weight: 600;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  justify-content: flex-end;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 200px;
}

.search-btn {
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.search-btn:hover {
  background: #0056b3;
}

.clear-btn {
  padding: 8px 16px;
  background: #ffc107;
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
  white-space: nowrap;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
}

.clear-btn:hover {
  background: #e0a800;
}

.service-status {
  margin: 10px 0 20px;
  font-size: 14px;
  color: #555;
}

.service-status.error-message {
  color: #c0392b;
}

.service-status.loading-message {
  color: #555;
}

.page-summary {
  margin-left: 12px;
  font-size: 13px;
  color: #666;
}

/* 无搜索结果样式 */
.no-results {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.no-results-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.no-results-text {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}

.no-results-hint {
  font-size: 14px;
  color: #999;
}

/* 操作按钮样式 - 按照第二张图片的样式 */
.operation-cell {
  padding: 8px;
}

.operation-buttons {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
}

.operation-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  min-width: 60px;
  transition: all 0.3s ease;
}

.operation-btn.evaluate {
  background: #007bff;
  color: white;
}

.operation-btn.evaluate:hover {
  background: #0056b3;
}

.operation-btn.cancel {
  background: #dc3545;
  color: white;
}

.operation-btn.cancel:hover {
  background: #c82333;
}

/* 服务网格样式 */
.services-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.service-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  padding: 20px;
  min-height: 200px;
}

.service-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

/* 卡片装饰元素 */
.card-decorations {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.red-triangle {
  position: absolute;
  width: 0;
  height: 0;
  border-style: solid;
}

.red-triangle.top-left {
  top: 0;
  left: 0;
  border-width: 0 0 20px 20px;
  border-color: transparent transparent #dc3545 transparent;
}

.red-triangle.top-right {
  top: 0;
  right: 0;
  border-width: 0 20px 20px 0;
  border-color: transparent #dc3545 transparent transparent;
}


/* 服务标题栏 */
.service-title-bar {
  background: #f8f9fa;
  border-radius: 20px;
  padding: 8px 16px;
  margin: 20px 0 10px 0;
  text-align: center;
}

.service-title {
  color: #8B4513;
  font-size: 16px;
  font-weight: 600;
}

.service-subtitle {
  color: #999;
  font-size: 12px;
  margin-bottom: 8px;
}

.service-description {
  color: #333;
  font-size: 13px;
  line-height: 1.4;
  margin-bottom: 20px;
}

.service-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.service-price {
  color: #dc3545;
  font-size: 18px;
  font-weight: 600;
}

.service-booked {
  color: #999;
  font-size: 12px;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
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
}

.page-btn:hover {
  background: #f8f9fa;
}

.page-btn.active {
  background: #8B4513;
  color: white;
  border-color: #8B4513;
}

/* 小贴士搜索栏 */
.tips-search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  justify-content: flex-end;
}

.tips-search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 250px;
}

.tips-search-btn {
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.tips-search-btn:hover {
  background: #0056b3;
}

/* 小贴士样式 */
.tips-list {
  display: grid;
  gap: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.tip-item {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  transition: all 0.3s ease;
}

.tip-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.tip-image {
  flex-shrink: 0;
  width: 120px;
  height: 80px;
}

.tip-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.tip-content {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.tip-content h3 {
  color: #333;
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
}

.tip-content p {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 15px;
  flex: 1;
}

.tip-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #999;
}

.tip-views,
.tip-date {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 预约搜索栏 */
.booking-search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  justify-content: flex-end;
}

.booking-search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 250px;
}

.booking-search-btn {
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
  white-space: nowrap;
}

.booking-search-btn:hover {
  background: #0056b3;
}

/* 预约表格样式 */
.booking-table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.booking-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.booking-table th {
  background: #f8f9fa;
  padding: 12px 8px;
  text-align: left;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #dee2e6;
  white-space: nowrap;
}

.booking-table td {
  padding: 12px 8px;
  border-bottom: 1px solid #f1f3f4;
  color: #333;
  vertical-align: middle;
}

.booking-table tbody tr:hover {
  background: #f8f9fa;
}

.booking-table tbody tr:last-child td {
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

.status.evaluated {
  background: #e2e3e5;
  color: #383d41;
}

.status.cancelled {
  background: #f8d7da;
  color: #721c24;
}

/* 操作按钮 */
.action-btn {
  padding: 4px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn.evaluate {
  background: #007bff;
  color: white;
}

.action-btn.evaluate:hover {
  background: #0056b3;
}

.action-btn.cancel {
  background: #dc3545;
  color: white;
}

.action-btn.cancel:hover {
  background: #c82333;
}

/* 预约分页样式 */
.booking-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

/* 评价搜索栏 */
.reviews-search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  justify-content: flex-end;
}

.reviews-search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 250px;
}

.reviews-search-btn {
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
  writing-mode: horizontal-tb;
  text-orientation: mixed;
}

.reviews-search-btn:hover {
  background: #0056b3;
}

/* 批量操作 */
.batch-actions {
  margin-bottom: 20px;
}

.batch-delete-btn {
  padding: 8px 16px;
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.batch-delete-btn:hover {
  background: #c82333;
}

/* 评价表格样式 */
.reviews-table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.reviews-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.reviews-table th {
  background: #f8f9fa;
  padding: 12px 8px;
  text-align: left;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #dee2e6;
  white-space: nowrap;
}

.reviews-table td {
  padding: 12px 8px;
  border-bottom: 1px solid #f1f3f4;
  color: #333;
  vertical-align: middle;
}

.reviews-table tbody tr:hover {
  background: #f8f9fa;
}

.reviews-table tbody tr:last-child td {
  border-bottom: none;
}

/* 复选框样式 */
.select-all,
.row-checkbox {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

/* 星级评分样式 */
.star-rating {
  display: flex;
  gap: 2px;
}

.star {
  font-size: 16px;
  color: #ddd;
}

.star.filled {
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

.star.empty {
  color: #ddd;
}

/* 操作按钮 */
.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  margin: 0 2px;
  transition: all 0.3s ease;
}

.action-btn.edit {
  background: #ffc107;
  color: #333;
}

.action-btn.edit:hover {
  background: #e0a800;
}

.action-btn.delete {
  background: #dc3545;
  color: white;
}

.action-btn.delete:hover {
  background: #c82333;
}

/* 评价分页样式 */
.reviews-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}



/* 响应式设计 */
@media (max-width: 1200px) {
  .services-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .services-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .home-services-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .hero-content {
    flex-direction: column;
    text-align: center;
  }
  
  .room-scene {
    width: 280px;
    height: 180px;
    margin-top: 20px;
  }
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
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
  color: #333;
  font-size: 18px;
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
  color: #333;
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

.form-input, .form-select, .form-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-input:focus, .form-select:focus, .form-textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-input.error {
  border-color: #dc3545;
}

.error-message {
  color: #dc3545;
  font-size: 12px;
  margin-top: 5px;
  display: block;
}

.password-hint {
  font-size: 12px;
  color: #6c757d;
  margin-top: 5px;
}

.rating-input {
  display: flex;
  align-items: center;
  gap: 15px;
}

.form-select {
  width: auto;
  min-width: 100px;
}

.star-preview {
  display: flex;
  gap: 2px;
}

.star-preview .star {
  font-size: 20px;
  color: #ddd;
}

.star-preview .star.filled {
  color: #ffc107;
}

.star-preview .star.half {
  color: #ffc107;
  position: relative;
}

.star-preview .star.half::after {
  content: '★';
  position: absolute;
  left: 0;
  width: 50%;
  overflow: hidden;
  color: #ddd;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #eee;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover {
  background: #0056b3;
}

/* 预约评价模态框特殊样式 */
.booking-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
  border-left: 4px solid #007bff;
}

.booking-info h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
}

.booking-info p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.booking-info strong {
  color: #333;
}

.no-action {
  color: #999;
  font-style: italic;
}

/* 个人信息模态框样式 */
.personal-info-modal {
  max-width: 500px;
  width: 90%;
}

.personal-info-form {
  padding: 0;
}

/* 头像区域 */
.avatar-group {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
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
  border: 2px solid #e0e0e0;
  transition: all 0.3s ease;
  position: relative;
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
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
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

/* 个人信息模态框保存按钮样式 */
.personal-info-form .save-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
  margin-top: 20px;
}

.personal-info-form .save-btn:hover {
  background: #0056b3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
}

.personal-info-form .save-btn:active {
  transform: translateY(0);
}

/* 充值记录模态框样式 */
.recharge-record-modal {
  max-width: 800px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

/* 充值操作区域 */
.recharge-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.recharge-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.recharge-btn:hover {
  background: #0056b3;
  transform: translateY(-1px);
}

.balance-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.balance-label {
  color: #333;
  font-size: 14px;
}

.balance-amount {
  color: #dc3545;
  font-size: 16px;
  font-weight: 600;
}

/* 充值记录表格 */
.recharge-table-container {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.recharge-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

.recharge-table thead {
  background: #f8f9fa;
}

.recharge-table th {
  padding: 12px 15px;
  text-align: left;
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #dee2e6;
  font-size: 14px;
}

.recharge-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #dee2e6;
  font-size: 14px;
}

.record-row:hover {
  background: #f8f9fa;
}

.amount {
  color: #dc3545;
  font-weight: 600;
}

.account-type {
  color: #333;
}

.account-number {
  color: #666;
  font-family: monospace;
}

.recharge-time {
  color: #666;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 15px 0;
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
  padding: 8px 12px;
  border: 1px solid #dee2e6;
  background: white;
  color: #333;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  min-width: 40px;
}

.page-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #007bff;
}

.page-btn.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.page-btn:disabled {
  background: #f8f9fa;
  color: #ccc;
  cursor: not-allowed;
  border-color: #e9ecef;
}

/* 充值信息模态框样式 */
.recharge-info-modal {
  max-width: 500px;
  width: 90%;
}

.recharge-info-form {
  padding: 0;
}

/* 充值金额输入区域 */
.amount-input-wrapper {
  display: flex;
  align-items: center;
  gap: 0;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  overflow: hidden;
  background: white;
}

.amount-btn {
  background: #f8f9fa;
  border: none;
  padding: 12px 16px;
  cursor: pointer;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  transition: all 0.3s ease;
  min-width: 50px;
}

.amount-btn:hover {
  background: #e9ecef;
}

.amount-btn.decrease {
  border-right: 1px solid #dee2e6;
}

.amount-btn.increase {
  border-left: 1px solid #dee2e6;
}

.amount-input {
  border: none;
  padding: 12px 16px;
  font-size: 16px;
  text-align: center;
  flex: 1;
  outline: none;
  background: white;
}

.amount-input:focus {
  outline: none;
  box-shadow: none;
}

/* 支付方式选择 */
.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-option {
  position: relative;
}

.payment-radio {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}

.payment-label {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.payment-label:hover {
  border-color: #007bff;
  background: #f8f9ff;
}

.payment-radio:checked + .payment-label {
  border-color: #007bff;
  background: #f0f8ff;
}

.payment-radio:checked + .payment-label .payment-icon {
  transform: scale(1.1);
}

.payment-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  transition: all 0.3s ease;
}

.wechat-icon {
  background: #07c160;
  color: white;
}

.alipay-icon {
  background: #1677ff;
  color: white;
}

.payment-text {
  flex: 1;
}

.payment-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.payment-subtitle {
  font-size: 12px;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 表单操作按钮 */
.recharge-info-form .form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.recharge-info-form .btn {
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  min-width: 80px;
}

.recharge-info-form .btn-secondary {
  background: #f8f9fa;
  color: #6c757d;
  border: 1px solid #dee2e6;
}

.recharge-info-form .btn-secondary:hover {
  background: #e9ecef;
  color: #495057;
}

.recharge-info-form .btn-primary {
  background: #007bff;
  color: white;
}

.recharge-info-form .btn-primary:hover {
  background: #0056b3;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
}

.recharge-info-form .btn-primary:active {
  transform: translateY(0);
}



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
    border-left: none;
    border-bottom: 3px solid transparent;
  }
  
  .nav-item.active {
    border-left: none;
    border-bottom-color: #8B4513;
  }
  
  .main-content {
    order: 1;
    padding: 20px;
  }
  
  .hero-content {
    flex-direction: column;
    text-align: center;
    padding: 30px 20px;
  }
  
  .hero-title {
    font-size: 36px;
  }
  
  .hero-subtitle {
    font-size: 28px;
  }
  
  .room-scene {
    width: 250px;
    height: 150px;
    margin-top: 20px;
  }
  
  .home-services-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }
  
  .services-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .service-categories {
    gap: 10px;
  }
  
  .category-item {
    padding: 6px 12px;
    font-size: 13px;
  }
  
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    width: 100%;
  }
  
  .tips-search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .tips-search-input {
    width: 100%;
  }
  
  .tip-item {
    flex-direction: column;
  }
  
  .tip-image {
    width: 100%;
    height: 120px;
  }
  
  .booking-search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .booking-search-input {
    width: 100%;
  }
  
  .booking-table-container {
    overflow-x: auto;
  }
  
  .booking-table {
    min-width: 1200px;
  }
  
  .reviews-search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .reviews-search-input {
    width: 100%;
  }
  
  .reviews-table-container {
    overflow-x: auto;
  }
  
  .reviews-table {
    min-width: 1000px;
  }
  
  .personal-info-modal {
    width: 95%;
    margin: 10px;
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
  
  /* 充值记录模态框移动端样式 */
  .recharge-record-modal {
    width: 95%;
    margin: 10px;
    max-height: 95vh;
  }
  
  .recharge-actions {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .recharge-btn {
    width: 100%;
    padding: 12px;
  }
  
  .balance-info {
    justify-content: center;
  }
  
  .recharge-table-container {
    overflow-x: auto;
  }
  
  .recharge-table {
    min-width: 600px;
  }
  
  .recharge-table th,
  .recharge-table td {
    padding: 8px 10px;
    font-size: 12px;
  }
  
  .pagination {
    flex-direction: column;
    gap: 15px;
    align-items: center;
  }
  
  .pagination-controls {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .page-btn {
    padding: 6px 10px;
    font-size: 12px;
    min-width: 35px;
  }
  
  /* 充值信息模态框移动端样式 */
  .recharge-info-modal {
    width: 95%;
    margin: 10px;
  }
  
  .amount-input-wrapper {
    flex-direction: column;
    gap: 0;
  }
  
  .amount-btn {
    width: 100%;
    padding: 10px;
    font-size: 16px;
    min-width: auto;
  }
  
  .amount-btn.decrease {
    border-right: none;
    border-bottom: 1px solid #dee2e6;
  }
  
  .amount-btn.increase {
    border-left: none;
    border-top: 1px solid #dee2e6;
  }
  
  .amount-input {
    padding: 15px;
    font-size: 18px;
  }
  
  .payment-methods {
    gap: 10px;
  }
  
  .payment-label {
    padding: 12px;
  }
  
  .payment-icon {
    width: 35px;
    height: 35px;
    font-size: 18px;
  }
  
  .payment-name {
    font-size: 14px;
  }
  
  .payment-subtitle {
    font-size: 11px;
  }
  
  .recharge-info-form .form-actions {
    flex-direction: column;
    gap: 10px;
  }
  
  .recharge-info-form .btn {
    width: 100%;
    padding: 14px;
    font-size: 16px;
  }
  
}

</style>
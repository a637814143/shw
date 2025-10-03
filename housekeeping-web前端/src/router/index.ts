import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
    },
    {
      path: '/user-home',
      name: 'user-home',
      component: () => import('../views/UserHomeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/provider-home',
      name: 'provider-home',
      component: () => import('../views/ProviderHomeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin-home',
      name: 'admin-home',
      component: () => import('../views/AdminHomeView.vue'),
      meta: { requiresAuth: true }
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  if (to.meta.requiresAuth) {
    // 检查用户是否已登录
    const userInfo = localStorage.getItem('userInfo')
    
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo)
        // 检查登录时间是否超过24小时
        const loginTime = new Date(user.loginTime)
        const now = new Date()
        const hoursDiff = (now.getTime() - loginTime.getTime()) / (1000 * 60 * 60)
        
        if (hoursDiff > 24) {
          // 登录过期，清除用户信息并跳转到登录页
          localStorage.removeItem('userInfo')
          next('/login')
        } else {
          // 用户已登录且未过期，检查角色权限
          if (user.role === 'admin') {
            // 管理员角色只能访问管理员页面
            if (to.name !== 'admin-home') {
              next('/admin-home')
            } else {
              next()
            }
          } else if (user.role === 'provider') {
            // 家政人员角色只能访问家政人员页面
            if (to.name !== 'provider-home') {
              next('/provider-home')
            } else {
              next()
            }
          } else {
            // 用户角色只能访问用户页面
            if (to.name !== 'user-home') {
              next('/user-home')
            } else {
              next()
            }
          }
        }
      } catch (error) {
        // 用户信息格式错误，清除并跳转到登录页
        localStorage.removeItem('userInfo')
        next('/login')
      }
    } else {
      // 用户未登录，跳转到登录页
      next('/login')
    }
  } else {
    // 不需要认证的路由，直接访问
    next()
  }
})

export default router

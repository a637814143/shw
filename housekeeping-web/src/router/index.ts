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
      path: '/panel',
      name: 'panel',
      component: () => import('../views/PanelView.vue'),
      meta: { requiresAuth: true }
    },
  ],
})

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const userInfo = localStorage.getItem('userInfo')

    if (!userInfo) {
      next('/login')
      return
    }

    try {
      const user = JSON.parse(userInfo)
      const loginTime = new Date(user.loginTime)
      const now = new Date()
      const hoursDiff = (now.getTime() - loginTime.getTime()) / (1000 * 60 * 60)

      if (hoursDiff > 24) {
        localStorage.removeItem('userInfo')
        next('/login')
      } else {
        next()
      }
    } catch (error) {
      localStorage.removeItem('userInfo')
      next('/login')
    }
  } else if ((to.path === '/' || to.path === '/login') && localStorage.getItem('userInfo')) {
    next('/panel')
  } else {
    next()
  }
})

export default router

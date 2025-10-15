import { createRouter, createWebHistory } from 'vue-router'

import { AUTH_ROLE_KEY, AUTH_TOKEN_KEY } from '../constants/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login',
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
      path: '/user',
      name: 'user-dashboard',
      component: () => import('../views/UserDashboardView.vue'),
    },
  ],
})

router.beforeEach((to, _from, next) => {
  if (to.name === 'user-dashboard') {
    const role = sessionStorage.getItem(AUTH_ROLE_KEY)
    const token = sessionStorage.getItem(AUTH_TOKEN_KEY)
    if (role !== 'user' || !token) {
      window.alert('请先使用用户账号登录')
      next({ name: 'login' })
      return
    }
  }

  next()
})

export default router

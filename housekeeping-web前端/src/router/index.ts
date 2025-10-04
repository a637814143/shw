import { createRouter, createWebHistory } from 'vue-router'
import { clearSession, getSession } from '../utils/api'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard',
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
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/DashboardView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard',
    },
  ],
})

router.beforeEach((to, from, next) => {
  const session = getSession()
  if (to.meta.requiresAuth && !session) {
    clearSession()
    next({ name: 'login', replace: true })
    return
  }

  if ((to.name === 'login' || to.name === 'register') && session) {
    next({ name: 'dashboard', replace: true })
    return
  }

  next()
})

export default router

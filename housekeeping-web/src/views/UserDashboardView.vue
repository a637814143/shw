<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <h1 class="platform-title">家政服务平台</h1>
    </header>
    <div class="dashboard-main">
      <aside class="sidebar">
        <nav class="menu" aria-label="主导航">
          <a class="menu-item active" href="#">
            <span class="menu-icon" aria-hidden="true">🏠</span>
            首页
          </a>
          <a class="menu-item" href="#">
            <span class="menu-icon" aria-hidden="true">🧽</span>
            家政服务
          </a>
          <a class="menu-item" href="#">
            <span class="menu-icon" aria-hidden="true">💡</span>
            居家小贴士
          </a>
          <a class="menu-item" href="#">
            <span class="menu-icon" aria-hidden="true">⭐</span>
            服务评价
          </a>
          <a class="menu-item highlight" href="#">
            <span class="menu-icon" aria-hidden="true">🎁</span>
            特惠
          </a>
        </nav>
      </aside>
      <section class="content">
        <div class="hero">
          <div>
            <h2 class="hero-title">家政服务</h2>
            <p class="hero-subtitle">专业团队，贴心守护您的家</p>
          </div>
          <button class="hero-cta" type="button">立即预约</button>
        </div>
        <div class="services-grid" role="list">
          <article v-for="service in services" :key="service.name" class="service-card" role="listitem">
            <div class="service-icon" aria-hidden="true">{{ service.icon }}</div>
            <h3 class="service-title">{{ service.name }}</h3>
            <p class="service-desc">{{ service.description }}</p>
          </article>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
const services = [
  {
    name: '日常保洁',
    description: '定期清洁，保持家居整洁焕新',
    icon: '🧹',
  },
  {
    name: '擦玻璃',
    description: '专业工具，晶莹剔透视野',
    icon: '🪟',
  },
  {
    name: '深度保洁',
    description: '全屋深层清洁，呵护家人健康',
    icon: '🧼',
  },
  {
    name: '新居开荒',
    description: '入住前彻底清洁，打造舒适新家',
    icon: '🏡',
  },
  {
    name: '厨房保养',
    description: '油污重灾区专属清洁方案',
    icon: '🍳',
  },
  {
    name: '卫生间保养',
    description: '除垢消菌，还原清新空间',
    icon: '🛁',
  },
]
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  font-family: 'Source Han Sans', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background-color: #eef2f6;
}

.dashboard-header {
  background-color: #f8f9fa;
  padding: 20px 32px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(30, 58, 95, 0.05);
}

.platform-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #1e3a5f;
  letter-spacing: 2px;
}

.dashboard-main {
  flex: 1;
  display: flex;
  min-height: 0;
}

.sidebar {
  width: 260px;
  background-color: #1e3a5f;
  padding: 32px 20px;
  color: #ffffff;
}

.menu {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 12px;
  color: inherit;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.12);
  transform: translateX(4px);
}

.menu-item.active {
  background-color: rgba(255, 255, 255, 0.18);
}

.menu-item.highlight {
  background-color: rgba(255, 107, 53, 0.16);
  color: #ff6b35;
  font-weight: 600;
}

.menu-item.highlight:hover {
  background-color: rgba(255, 107, 53, 0.24);
}

.menu-icon {
  font-size: 20px;
}

.content {
  flex: 1;
  padding: 40px 48px;
  background-color: #ffffff;
  overflow-y: auto;
}

.hero {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 32px;
  border-bottom: 1px solid #e5e7eb;
}

.hero-title {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: #1e3a5f;
}

.hero-subtitle {
  margin-top: 8px;
  font-size: 16px;
  color: #4b5563;
}

.hero-cta {
  padding: 12px 24px;
  border: none;
  border-radius: 999px;
  background: linear-gradient(135deg, #ff8142, #ff6b35);
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 12px 24px rgba(255, 107, 53, 0.25);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.hero-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 32px rgba(255, 107, 53, 0.28);
}

.services-grid {
  margin-top: 32px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px;
}

.service-card {
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 12px 30px rgba(30, 58, 95, 0.08);
  border: 1px solid rgba(226, 232, 240, 0.7);
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 40px rgba(30, 58, 95, 0.12);
}

.service-icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background-color: rgba(30, 58, 95, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: #1e3a5f;
}

.service-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.service-desc {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
}

@media (max-width: 1080px) {
  .services-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .dashboard-main {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    display: flex;
    justify-content: center;
    padding: 20px 16px;
  }

  .menu {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }

  .menu-item {
    flex: 1 1 45%;
    justify-content: center;
  }

  .content {
    padding: 32px 20px;
  }

  .services-grid {
    grid-template-columns: repeat(1, minmax(0, 1fr));
  }
}
</style>

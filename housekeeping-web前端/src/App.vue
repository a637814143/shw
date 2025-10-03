<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <RouterView />
</template>

<style>
/* 全局样式重置，确保所有页面占满全屏 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  min-height: 100%;
  width: 100%;
  background: #e3f2fd;
}

#app {
  min-height: 100vh;
  width: 100%;
}
</style>

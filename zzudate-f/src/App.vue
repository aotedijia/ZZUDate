<script setup>
import { onMounted } from 'vue'
import { dailySync } from './utils/userSync'

onMounted(() => {
  dailySync()
})
</script>

<template>
  <router-view />
</template>

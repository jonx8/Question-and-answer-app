<script setup lang="ts">

import AppBar from "@/components/AppBar.vue";
import AppFooter from "@/components/AppFooter.vue";
</script>

<template>
  <AppBar/>
  <v-main>
    <router-view/>
  </v-main>
  <AppFooter/>
</template>

<style scoped>

</style>

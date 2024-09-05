<script setup lang="ts">

import AppBar from "@/components/AppBar.vue";
import AppFooter from "@/components/AppFooter.vue";
import {onMounted} from "vue";
import api from "@/api";
import {useUserStore} from "@/store/user";

const userStore = useUserStore()

onMounted(loadUserData)
async function loadUserData() {
  if (userStore.userProfile.id) {
    const response = await api.usersApi.getUserInfo(userStore.userProfile.id)
    userStore.userProfile = response.data
  } else {
    setTimeout(loadUserData, 1000)
  }
}
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

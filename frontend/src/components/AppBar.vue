<script setup lang="ts">

import {ref} from "vue";
import {useKeycloak} from "@/plugins/keycloak";
const keycloak = useKeycloak();
const navbarBtn = ref(0)
const menuItems = [
  {
    title: 'Home',
    path: '/',
    icon: 'mdi-home',
  },
  {
    title: 'Profile',
    path: '/profile',
    icon: 'mdi-account',
  }
]


async function logout() {
  window.location.replace(keycloak.createLogoutUrl())
}

</script>

<template>
  <v-toolbar color="primary">
    <v-toolbar-title> Questions</v-toolbar-title>
    <v-spacer/>
    <v-btn-toggle class="mr-5" border mandatory v-model="navbarBtn" base-color="primary">
      <v-btn v-for="(item, index) in menuItems"
             active-color="black"
             :active="navbarBtn == index"
             :key="index"
             :value="index"
             :to="item.path"
             :icon="item.icon"
      />
    </v-btn-toggle>
    <v-btn icon="mdi-export" v-if="keycloak.authenticated" @click="logout()"/>
  </v-toolbar>
</template>

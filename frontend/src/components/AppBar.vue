<script setup lang="ts">

import {ref} from "vue";

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

</script>

<template>
  <v-toolbar color="primary">
    <v-toolbar-title> Questions</v-toolbar-title>
    <v-spacer/>
    <v-btn-toggle mandatory v-model="navbarBtn" base-color="primary">
      <v-btn v-for="(item, index) in menuItems"
             active-color="black"
             :active="navbarBtn == index"
             :key="index"
             :value="index"
             :to="item.path"
             :icon="item.icon"
      />
    </v-btn-toggle>
    <v-btn icon="mdi-export"/>
  </v-toolbar>
</template>

<style>

</style>

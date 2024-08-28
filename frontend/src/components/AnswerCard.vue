<script setup lang="ts">
import {Answer} from "@/api/generated";
import {KeycloakProfile} from "keycloak-js";

withDefaults(defineProps<{
  answer: Answer,
  userData: KeycloakProfile,
  withLink: boolean
}>(), {withLink: _ => false})
</script>

<template>
  <v-card>
    <v-container class="d-inline-flex">
      <v-img inline src="/src/assets/default-user-avatar.png" cover rounded="circle" height="50"
             width="50"
             class="mr-3"></v-img>
      <p class="font-weight-medium pt-2 cursor-pointer">
        <router-link class="text-black app-link" :to="'/profile/' + answer.author">
          {{ userData.firstName }} {{ userData.lastName }}
        </router-link>
      </p>

    </v-container>
    <v-card-subtitle>{{ answer.createdAt }}</v-card-subtitle>
    <v-card-title>
      <router-link class="text-black app-link" :to="'/questions/' + answer.question?.id">
        {{ answer.question?.title }}
      </router-link>
    </v-card-title>
    <v-card-text>
      {{ answer.text }}
    </v-card-text>
  </v-card>
</template>

<style scoped>

</style>

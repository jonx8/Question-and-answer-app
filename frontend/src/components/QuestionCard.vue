<script setup lang="ts">
import type {Question} from "@/api/generated";
import {useKeycloak} from "@/plugins/keycloak";

const keycloak = useKeycloak()
withDefaults(defineProps<{ question: Question, withLink: Boolean }>(), {withLink: _ => false})


</script>

<template>
  <v-card>
    <v-container class="d-flex justify-space-between ma-0 pa-0">
      <v-card-title class="cursor-pointer">
        <router-link v-if="withLink" :to="'/questions/' + question.id"
                     class="text-black app-link"
        >
          {{ question.title }}
        </router-link>
        <span v-else>{{ question.title }}</span>
      </v-card-title>
      <v-card-actions v-if="question.author != keycloak.subject">
        <v-btn prepend-icon="mdi-pencil">Answer</v-btn>
      </v-card-actions>
    </v-container>
    <v-card-text class="text-red " v-if="question.status != 'PUBLISHED'">
      {{ question.status }}
    </v-card-text>
    <v-card-subtitle>{{ question.createdAt }}</v-card-subtitle>
    <v-card-text>{{ question.text }}</v-card-text>

  </v-card>
</template>

<style scoped>
</style>

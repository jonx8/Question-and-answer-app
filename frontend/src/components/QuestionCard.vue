<script setup lang="ts">
import type {Question} from "@/api/generated";


withDefaults(defineProps<{ question: Question, withLink: Boolean }>(), {withLink: _ => false})


</script>

<template>
  <v-card>
    <v-card-title>
      <router-link v-if="withLink" :to="'/questions/' + question.id" class="question-link">
        {{ question.title }}
      </router-link>
      <span v-else>{{ question.title }}</span>
    </v-card-title>
    <v-card-subtitle>{{ question.createdAt }}</v-card-subtitle>
    <v-card-text>{{ question.text }}</v-card-text>
  </v-card>
</template>

<style scoped>
.question-link {
  text-decoration: none;
  color: black;
}

.question-link:hover {
  text-decoration: underline;
}
</style>

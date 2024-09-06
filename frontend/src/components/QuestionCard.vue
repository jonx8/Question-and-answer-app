<script setup lang="ts">
import type {Question} from "@/api/generated/questions";
import NewAnswerDialog from "@/components/NewAnswerDialog.vue";
import {useUserStore} from "@/store/user";
import DeleteQuestionDialog from "@/components/DeleteQuestionDialog.vue";

const userStore = useUserStore()
defineProps<{ question: Question, withLink: boolean }>()

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
      <v-card-actions>
        <v-btn prepend-icon="mdi-pencil" v-if="question.author != userStore.userProfile.id">Answer
          <NewAnswerDialog :question="question"/>
        </v-btn>
        <v-btn v-else>
          <v-icon color="red" size="25px">mdi-trash-can</v-icon>
          <DeleteQuestionDialog :question-id="question.id"/>
        </v-btn>
      </v-card-actions>
    </v-container>
    <v-card-text class="text-red " v-if="question.status != 'PUBLISHED'">
      {{ question.status }}
    </v-card-text>
    <v-card-subtitle>{{ question.createdAt }}</v-card-subtitle>
    <v-card-text>{{ question.text }}</v-card-text>

  </v-card>
</template>

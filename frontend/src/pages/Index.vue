<script lang="ts" setup>

import QuestionCard from "@/components/QuestionCard.vue";
import {onBeforeUpdate, onMounted, reactive} from "vue";
import api from "@/api";
import {Question} from "@/api/generated";

const questions = reactive([] as Question[])
onMounted(async () => await loadQuestions())
onBeforeUpdate(async () => await loadQuestions())


async function loadQuestions() {
  const response = await api.questionsApi.getQuestions()
  Object.assign(questions, response.data)
}

</script>

<template>

  <v-container class="d-flex flex-column align-center mt-5">
    <h2 class="align-center">Feed</h2>
    <QuestionCard :with-link="true"
                  v-for="question in questions" :key="question.id"
                  :question="question"
                  class="ma-4 w-50"
    />
  </v-container>
</template>

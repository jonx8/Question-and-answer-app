<script lang="ts" setup>

import QuestionCard from "@/components/QuestionCard.vue";
import {onBeforeUpdate, onMounted, ref} from "vue";
import api from "@/api";
import {QuestionHeader} from "@/api/generated";

const questions = ref([] as QuestionHeader[])
onMounted(async () => await loadQuestions())
onBeforeUpdate(async () => await loadQuestions())


async function loadQuestions() {
  const response = await api.questionsApi.getQuestions()
  questions.value = response.data
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
    <h3 v-if="questions.length == 0">There are no any questions yet.</h3>
  </v-container>
</template>

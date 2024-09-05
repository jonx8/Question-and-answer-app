<script lang="ts" setup>

import QuestionCard from "@/components/QuestionCard.vue";
import {onBeforeUpdate, onMounted, ref} from "vue";
import api from "@/api";
import {QuestionHeader} from "@/api/generated/questions";
import NewQuestionDialog from "@/components/NewQuestionDialog.vue";
import {useUserStore} from "@/store/user";

const questions = ref([] as QuestionHeader[])
const userStore = useUserStore()
onMounted(loadQuestions)
onBeforeUpdate(loadQuestions)


async function loadQuestions() {
  if (userStore.userProfile.id) {
    const response = await api.questionsApi.getQuestions()
    questions.value = response.data
  } else {
    setTimeout(loadQuestions, 500)
  }
}

</script>

<template>

  <v-container class="d-flex flex-column align-center mt-5">
    <h2 class="pa-0">Feed</h2>
    <v-btn class="align-self-end">New question
      <NewQuestionDialog/>
    </v-btn>

    <QuestionCard :with-link="true"
                  v-for="question in questions" :key="question.id"
                  :question="question"
                  class="ma-4 w-50"
    />
    <h3 v-if="questions.length == 0">There are no any questions yet.</h3>
  </v-container>
</template>

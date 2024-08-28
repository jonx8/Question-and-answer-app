<script setup lang="ts">

import {onBeforeUpdate, onMounted, reactive, ref, watch} from "vue";
import api from "@/api";
import QuestionCard from "@/components/QuestionCard.vue";
import {useKeycloak} from "@/plugins/keycloak";
import {Answer, QuestionHeader} from "@/api/generated";
import AnswerCard from "@/components/AnswerCard.vue";

const keycloak = useKeycloak()

const selectedBlock = ref('questions')
const questions = ref([] as QuestionHeader[])
const answers = ref([] as Answer[])
const userProfile = reactive({
  id: '',
  username: '',
  email: '',
  firstName: '',
  lastName: '',
})

watch(selectedBlock, async (value) => {
  if (value == 'questions') {
    await loadUserQuestions();
  } else {
    await loadUserAnswers()
  }
})


onMounted(async () => {
  await loadUserData();
  await loadUserQuestions()
})

onBeforeUpdate(async () => {
  await loadUserData();
  await loadUserQuestions();
})

keycloak.loadUserProfile()

async function loadUserData() {
  Object.assign(userProfile, await keycloak.loadUserProfile())
}

async function loadUserQuestions() {
  const response = await api.questionsApi.getQuestions(userProfile.id)
  questions.value = response.data

}

async function loadUserAnswers() {
  const response = await api.answersApi.getAnswersByAuthor(userProfile.id)
  answers.value = response.data
}
</script>

<template>
  <v-container v-if="keycloak.authenticated">
    <v-card flat class="d-flex">
      <v-img
        rounded="circle"
        height="200"
        width="200"
        src="/src/assets/default-user-avatar.png"
        cover
      ></v-img>
      <v-container>
        <v-card-title class="cursor-pointer">
          {{ userProfile.firstName }} {{ userProfile.lastName }}
        </v-card-title>
        <v-card-subtitle>@{{ userProfile.username }}</v-card-subtitle>
        <v-card-actions class="ma-4">
          <v-btn-toggle mandatory shaped v-model="selectedBlock" border>
            <v-btn width="100" value="questions">
              <span>Questions</span>
            </v-btn>
            <v-btn width="100" value="answers">
              <span>Answers</span>
            </v-btn>
          </v-btn-toggle>
        </v-card-actions>
      </v-container>
    </v-card>
    <v-container class="w-50" v-if="selectedBlock == 'questions'">
      <QuestionCard v-for="question in questions"
                    :key="question.id"
                    :question="question"
                    :with-link="true"
                    class="ma-4"/>
      <h4 v-if="questions.length == 0">You have not asked any questions yet.</h4>
    </v-container>
    <v-container class="w-50" v-else>
      <AnswerCard v-for="answer in answers"
                  :answer="answer" :user-data="userProfile"
                  :key="answer.id" class="ma-4"/>
      <h4 v-if="answers.length == 0">You have not answered any questions yet.</h4>
    </v-container>
  </v-container>

</template>

<style scoped>

</style>

<script setup lang="ts">

import {onBeforeUpdate, onMounted, ref, watch} from "vue";
import api from "@/api";
import QuestionCard from "@/components/QuestionCard.vue";
import {useKeycloak} from "@/plugins/keycloak";
import {Answer, QuestionHeader} from "@/api/generated/questions";
import AnswerCard from "@/components/AnswerCard.vue";
import {useUserStore} from "@/store/user";
import {UserRepresentation} from "@/api/generated/users";

const keycloak = useKeycloak()
const props = defineProps({id: {type: String, required: false}})
const selectedBlock = ref('questions')
const questions = ref([] as QuestionHeader[])
const answers = ref([] as Answer[])
const userStore = useUserStore()
const userProfile = ref({} as UserRepresentation)

watch(selectedBlock, async (value) => {
  if (value == 'questions') {
    await loadUserQuestions();
  } else {
    await loadUserAnswers()
  }
})

onMounted(async () => {
  await loadUserData()
})

onBeforeUpdate(async () => {
  if (selectedBlock.value == "questions") {
    await loadUserQuestions()
  } else {
    await loadUserAnswers()
  }
})

async function loadUserData() {
  const userId = props.id || userStore.userProfile.id
  if (userId) {
    const response = await api.usersApi.getUserInfo(userId)
    userProfile.value = response.data
  } else {
    setTimeout(loadUserData, 1000)
  }
}

async function loadUserQuestions() {
  if (userProfile.value.id) {
    const response = await api.questionsApi.getQuestions(userProfile.value.id)
    questions.value = response.data
  }
}

async function loadUserAnswers() {
  if (userProfile.value.id) {
    const response = await api.answersApi.getAnswersByAuthor(userProfile.value.id)
    answers.value = response.data
  }
}
</script>

<template>
  <v-container v-if="keycloak.authenticated" :key="userProfile.id">
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
          {{ userStore.userProfile.firstName }}
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
                  :with-link="true"
                  :answer="answer" :user-data="userProfile"
                  :key="answer.id" class="ma-4"/>
      <h4 v-if="answers.length == 0">You have not answered any questions yet.</h4>
    </v-container>
  </v-container>

</template>

<style scoped>

</style>

<script setup lang="ts">
import {onBeforeUpdate, onMounted, reactive} from "vue";
import api from "@/api";
import {Question} from "@/api/generated/questions";
import QuestionCard from "@/components/QuestionCard.vue";
import AnswerCard from "@/components/AnswerCard.vue";


const props = defineProps({id: {type: String, required: true}})

const question = reactive({} as Question)
onMounted(async () => await loadQuestionInfo())
onBeforeUpdate(async () => await loadQuestionInfo())

async function loadQuestionInfo() {
  const response = await api.questionsApi.getQuestion(parseInt(props.id))
  Object.assign(question, response.data)
}

</script>

<template>
  <v-container class="d-flex flex-column align-center">
    <QuestionCard :question="question" :with-link=false class="w-50"/>
    <h3 class="justify-center mt-5">Answers</h3>
    <v-container class="d-flex justify-center">
      <AnswerCard v-for="answer in question.answers"
                  :user-data="{firstName: 'Ivan', lastName: 'Ivanov'}"
                  :key="answer.id"
                  :answer="answer"
                  :with-link="false"
                  class="ma-3 w-66"

      />
      <h4 v-if="question.answers?.length == 0">No answers yet</h4>
    </v-container>
  </v-container>
</template>

<style scoped>

</style>

<script setup lang="ts">
import {onBeforeUpdate, onMounted, reactive} from "vue";
import api from "@/api";
import {Question} from "@/api/generated";
import QuestionCard from "@/components/QuestionCard.vue";


const props = defineProps({
  questionId: {type: String, required: true}
})

const question = reactive({} as Question)
onMounted(async () => await loadQuestionInfo())
onBeforeUpdate(async () => await loadQuestionInfo())

async function loadQuestionInfo() {
  const response = await api.questionsApi.getQuestion(props.questionId)
  Object.assign(question, response.data)
}

</script>

<template>
  <v-container class="d-flex flex-column align-center">
    <QuestionCard :question="question" class="w-50"/>
  </v-container>
</template>

<style scoped>

</style>

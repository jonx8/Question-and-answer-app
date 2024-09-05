<script setup lang="ts">
import api from "@/api";
import {QuestionStatusEnum} from "@/api/generated/questions";
import axios from "axios";
import {ref} from "vue";

const questionTitle = ref("")
const questionText = ref("")
const questionStatus = ref<QuestionStatusEnum>(QuestionStatusEnum.Published)
const selectItems = [
  {title: "Published", value: QuestionStatusEnum.Published},
  {title: "Draft", value: QuestionStatusEnum.Draft}
]

async function onPostClick() {
  try {
    await api.questionsApi.createQuestion(
      {title: questionTitle.value, text: questionText.value, status: questionStatus.value})
  } catch (e) {
      if (axios.isAxiosError(e)) {
        console.log(e?.response?.data.detail)
      }
  }

}
</script>

<template>
  <v-dialog activator="parent" width="60%">
    <template v-slot:default="{ isActive }">
      <v-card class="pa-6">
        <v-card-title>New question</v-card-title>
        <v-form>
          <v-text-field v-model="questionTitle" placeholder="Title"/>
          <v-textarea placeholder="Write your question" no-resize v-model="questionText"/>
          <v-select v-model="questionStatus"
                    :items="selectItems"
                    item-title="title"
                    item-value="value"
            >
          </v-select>
        </v-form>
        <template v-slot:actions>
          <v-btn
            rounded
            text="Close"
            @click="isActive.value = false"
          ></v-btn>
          <v-btn
            rounded
            base-color="white"
            class="bg-blue-darken-1"
            text="Post"
            @click="isActive.value = false; onPostClick()"
          ></v-btn>
        </template>

      </v-card>
    </template>
  </v-dialog>
</template>


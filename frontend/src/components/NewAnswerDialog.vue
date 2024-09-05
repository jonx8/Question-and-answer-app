<script setup lang="ts">
import {useUserStore} from "@/store/user"
import api from "@/api";
import {Question} from "@/api/generated/questions";
import axios from "axios";

const props = defineProps<{ question: Question }>()
let answerText = ""

async function onPostClick() {
  if (props.question.id) {
    try {
      await api.questionsApi.addAnswer(props.question.id, {text: answerText})
    } catch (e) {
      if (axios.isAxiosError(e)) {
        console.log(e?.response?.data?.detail)
      }
    }
  }
}
</script>

<template>
  <v-dialog activator="parent" width="60%">
    <template v-slot:default="{ isActive }">
      <v-card class="pa-6">
        <v-card-title>New answer</v-card-title>
        <v-card-subtitle>{{ question.title }}</v-card-subtitle>

        <v-textarea placeholder="Write your answer" no-resize v-model="answerText"/>
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


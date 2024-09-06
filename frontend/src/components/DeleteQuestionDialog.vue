<script setup lang="ts">

import api from "@/api";
import axios from "axios";

const props = defineProps<{ questionId: bigint }>()

async function deleteQuestion() {
  try {
    await api.questionsApi.deleteQuestion(props.questionId)
  } catch (e) {
    if (axios.isAxiosError(e)) {
      console.log(e?.response?.data.detail)
    }
  }
}

</script>

<template>
  <v-dialog activator="parent" width="30%">
    <template v-slot:default="{ isActive }">
      <v-card class="text-center">
        <v-card-title>Are you sure you want to delete this question?</v-card-title>
        <v-card-actions>
          <v-btn @click="isActive.value = false">Cancel</v-btn>
          <v-btn @click="isActive.value = false; deleteQuestion()" base-color="red">Confirm</v-btn>
        </v-card-actions>

      </v-card>
    </template>
  </v-dialog>
</template>


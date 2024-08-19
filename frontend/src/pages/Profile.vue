<script setup lang="ts">

import {ref} from "vue";

const firstName = "Andrei"
const lastName = "Malykh"
const username = "User"

const selectedBlock = ref('questions')

const questions = [
  {
    id: "dfgdgsdgsd",
    title: "How i met your mother?",
    text: "gdfgsdfgsdfkghsdfkfghesdh gidg lisdfghlihlisdfhglif"
  },
  {
    id: "gdfgdfgdfgfd",
    title: "How i met your father",
    text: "gdfgsdfgsdfkghsdfkfghesdh gidg lisdfghlihlisdfhglif"
  }
]
</script>

<template>
  <v-container>
    <v-card flat class="d-flex">
      <v-img
        rounded="circle"
        height="200"
        width="200"
        src="https://cdn.vuetifyjs.com/images/cards/cooking.png"
        cover
      ></v-img>
      <v-container>
        <v-card-title>{{ firstName }} {{ lastName }}</v-card-title>
        <v-card-subtitle>@{{ username }}</v-card-subtitle>
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
      <v-card v-for="question in questions" :key="question.id" class="ma-8">
        <v-card-title>
          <router-link :to="'/questions/' + question.id">{{ question.title }}</router-link>
        </v-card-title>
        <v-card-text>{{ question.text }}</v-card-text>
      </v-card>
    </v-container>
    <v-container class="w-50" v-else-if="selectedBlock == 'answers'">
      <v-card></v-card>
    </v-container>
  </v-container>

</template>

<style scoped>

</style>

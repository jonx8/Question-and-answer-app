import {defineStore} from "pinia";
import {UserRepresentation} from "@/api/generated/users";

export const useUserStore = defineStore("user", {
    state: () => ({userProfile: {} as UserRepresentation}),
  }
)


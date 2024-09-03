import {defineStore} from "pinia";
import api from "@/api";
import {UserRepresentation} from "@/api/generated/users";

export const useUserStore = defineStore("user", {
    state: () => ({userProfile: {} as UserRepresentation}),
    actions: {
      async loadUserData() {
        if (!this.userProfile.id) {
          throw new Error("User id is undefined!")
        }
        Object.assign(this.userProfile, await api.usersApi.getUserInfo(this.userProfile.id))
      }
    }
  }
)


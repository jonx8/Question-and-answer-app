import VueKeycloak from "@dsb-norge/vue-keycloak-js"
import {VueKeycloakOptions} from "@dsb-norge/vue-keycloak-js/dist/types";
import router from "@/router";
import {inject} from "vue";
import Keycloak from "keycloak-js";
import api from "@/api";


export function useKeycloak(): Keycloak {
  const instance = inject(VueKeycloak.KeycloakSymbol)
  if (instance == undefined) throw new Error('Keycloak is not registered')
  return <Keycloak>instance
}


export const kcOptions: VueKeycloakOptions = {
  config: {
    url: import.meta.env.VITE_KEYCLOAK_PATH,
    realm: "question-and-answer-app",
    clientId: "frontend",
  },
  init: {
    onLoad: "check-sso",
  },
  logout: {
    redirectUri: window.location.origin,
  },
  onReady: keycloak => {
    api.registerKeycloak(keycloak)
    router.beforeEach((to, _, next) => {
      if (to.meta.secured && !keycloak.authenticated) {
        keycloak.login({
          redirectUri: window.location.origin + to.fullPath,
        }).then()
      } else {
        next()
      }
    })
  }
}

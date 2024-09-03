

import vuetify from './vuetify'
import router from '../router'

import type { App } from 'vue'
import VueKeycloak from "@dsb-norge/vue-keycloak-js";
import {kcOptions} from "@/plugins/keycloak";
import store from "@/store";

export function registerPlugins (app: App) {
  app
    .use(store)
    .use(VueKeycloak, kcOptions)
    .use(vuetify)
    .use(router)
}

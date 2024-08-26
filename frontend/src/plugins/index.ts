

import vuetify from './vuetify'
import router from '../router'

import type { App } from 'vue'
import VueKeycloak from "@dsb-norge/vue-keycloak-js";
import {kcOptions} from "@/plugins/keycloak";

export function registerPlugins (app: App) {
  app
    .use(VueKeycloak, kcOptions)
    .use(vuetify)
    .use(router)
}

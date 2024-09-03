import axios from "axios";
import {AnswerControllerApi, Configuration, QuestionControllerApi} from "@/api/generated/questions";
import {UserControllerApi} from "@/api/generated/users";
import Keycloak from "keycloak-js";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_PATH,
  withCredentials: true
})

let keycloak: Keycloak | null = null;

const config = new Configuration({
  basePath: import.meta.env.VITE_API_PATH,
  accessToken: () => {
    if (keycloak == null) {
      throw new Error("Keycloak not provided to API client");
    }
    if (!keycloak.authenticated || keycloak.token == null) {
      throw new Error("User is not authenticated")
    }
    return keycloak.token;
  },
})

export default {
  questionsApi: new QuestionControllerApi(config, "", axiosInstance),
  answersApi: new AnswerControllerApi(config, "", axiosInstance),
  usersApi: new UserControllerApi(config, "", axiosInstance),
  registerKeycloak(kcInstance: Keycloak) {
    keycloak = kcInstance;

    axiosInstance.interceptors.request.use((config) => {
      if (!kcInstance.authenticated) throw new Error("User is not authenticated")
      config.headers.setAuthorization(`Bearer ${kcInstance.token}`)
      return config
    })
  }

}

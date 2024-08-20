import axios from "axios";
import {Configuration, QuestionControllerApi} from "@/api/generated";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_PATH,
})

const config = new Configuration({
  basePath: import.meta.env.VITE_API_PATH
})

export default {
  questionsApi: new QuestionControllerApi(config, "", axiosInstance)
}

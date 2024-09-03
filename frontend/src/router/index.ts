import {createRouter, createWebHistory} from "vue-router";
import Default from "@/layouts/default/Default.vue";
import Index from "@/pages/Index.vue";
import Profile from "@/pages/Profile.vue";
import Question from "@/pages/QuestionDetail.vue";


const routes = [
  {
    path: '',
    component: Default,
    children: [
      {
        path: '',
        name: 'Home',
        component: Index,
        meta: {
          secured: true,
        }
      },
      {
        path: '/profile/:id?',
        name: 'profile',
        component: Profile,
        meta: {
          secured: true,
        },
        props: true
      },
      {
        path: '/questions/:id',
        name: 'questions',
        component: Question,
        meta: {
          secured: true,
        },
        props: true
      }
    ]
  }
]


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// Workaround for https://github.com/vitejs/vite/issues/11804
router.onError((err, to) => {
  if (err?.message?.includes?.('Failed to fetch dynamically imported module')) {
    if (!localStorage.getItem('vuetify:dynamic-reload')) {
      console.log('Reloading page to fix dynamic import error')
      localStorage.setItem('vuetify:dynamic-reload', 'true')
      location.assign(to.fullPath)
    } else {
      console.error('Dynamic import error, reloading page did not fix it', err)
    }
  } else {
    console.error(err)
  }
})

router.isReady().then(() => {
  localStorage.removeItem('vuetify:dynamic-reload')
})

export default router

import { createRouter, createWebHistory } from "vue-router";
import PresentationView from "../views/PresentationView.vue";
import AboutView from "../views/AboutView.vue";
import ContactView from "../views/ContactView.vue";
import LoginView from "../views/LoginView.vue";
import VerificationView from "../views/VerificationView.vue";
import RegisterView from "../views/RegisterView.vue";
import AuthorView from "../views/AuthorView.vue";
import ProfileView from "../views/ProfileView.vue";
import CourseView from "../views/CourseView.vue";
import DashboardView from "../views/DashboardView.vue";
import LessonsView from "../views/LessonsView.vue";
import CuestionarioLeccion from "../views/CuestionarioLeccion.vue";
import CourseListView from "../views/CourseListView.vue";
import Quizzes from "../views/Quizzes.vue";
import QuizDetail from "../views/Sections/QuizDetail.vue";
import PasswordRecovery from "../components/PasswordRecovery.vue";
import ResetPassword from '../views/ResetPassword.vue';
import VerifyRecoveryCode from "../components/VerifyRecoveryCode.vue";
import ResolveQuiz from "../views/ResolveQuiz.vue";
import Certificado from '../views/Sections/Certificado.vue';

import { useAppStore } from "@/stores"; // Importación correcta del store

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/verify-recovery-code",
      name: "verify-recovery-code",
      component: VerifyRecoveryCode,
    },
    {
      path: "/password-recovery",
      name: "password-recovery",
      component: PasswordRecovery,
    },
    {
      path: "/pages/cuestionario/:lessonId",
      name: "cuestionario-leccion",
      component: CuestionarioLeccion,
    },
    {
      path: "/profile",
      name: "profile",
      component: ProfileView,
    },
    {
      path: "/",
      name: "presentation",
      component: PresentationView,
    },
    {
      path: "/pages/about-us",
      name: "about",
      component: AboutView,
    },
    {
      path: "/pages/contact-us",
      name: "contactus",
      component: ContactView,
    },
    {
      path: "/pages/author",
      name: "author",
      component: AuthorView,
    },
    {
      path: "/pages/login",
      name: "login-view",
      component: LoginView,
    },
    {
      path: "/pages/login/verification",
      name: "verification-view",
      component: VerificationView,
    },
    {
      path: "/pages/register",
      name: "register-view",
      component: RegisterView,
    },
    {
      path: "/pages/course",
      name: "course",
      component: CourseView,
    },
    {
      path: "/pages/",
      name: "lessons",
      component: LessonsView,
    },
    {
      path: "/dashboard",
      name: "dashboard",
      component: DashboardView,
    },
    {
      path: "/courses",
      name: "coursesList",
      component: CourseListView,
    },
    {
      path: '/quizzes',
      name: 'Quizzes',
      component: Quizzes,
      props: route => ({ courseId: route.query.courseId, courseTitle: route.query.courseTitle }),
    },
    {
      path: '/quiz/:quizId',
      name: 'QuizDetail',
      component: QuizDetail,
    },
    {
      path: '/reset-password',
      name: 'ResetPassword',
      component: ResetPassword,
    },
    {
      path: '/resolve-quiz',
      name: 'ResolveQuiz',
      component: ResolveQuiz,
    },
    {
      path: '/certificado',
      name: 'Certificado',
      component: Certificado
    },
    {
      path: '/admin/usuarios',
      name: 'AdminUsuarios',
      component: () => import('@/views/AdminUsuarios.vue'),
      meta: { requiresAuth: true, role: [4] }
    },
    {
      path: '/admin/logs',
      name: 'AdminLogs',
      component: () => import('@/views/AdminLogs.vue'),
      meta: { requiresAuth: true, role: [3, 4] }
    },
    {
      path: '/admin/dashboard',
      name: 'admin-dashboard',
      component: () => import('@/views/AdminDashboardView.vue'),
      meta: { requiresAuth: true, role: [3] }
    },
    {
      path: '/admin/page-dashboard',
      name: 'AdminPageDashboard',
      component: () => import('@/views/AdminPageDashboard.vue'),
      meta: { requiresAuth: true, role: [3] }
    },
    {
      path: '/admin/page-reports',
      name: 'AdminPageReports',
      component: () => import('@/views/AdminPageReports.vue'),
      meta: { requiresAuth: true, role: [3] }
    },
  ],
});

// Guard de rutas para roles admin
router.beforeEach((to, from, next) => {
  // Instancia del store (debe llamarse como función dentro del guard)
  const appStore = useAppStore();
  if (to.meta.requiresAuth && to.meta.role) {
    const userRole = appStore.getTipoPersona;
    if (to.meta.role.includes(userRole)) {
      next();
    } else {
      next({ name: 'login-view' });
    }
  } else {
    next();
  }
});

export default router;
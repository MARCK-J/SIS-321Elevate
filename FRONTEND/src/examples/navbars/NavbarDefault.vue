<template>
  <nav
    class="navbar navbar-expand-lg top-0"
    :class="{
      'z-index-3 w-100 shadow-none navbar-transparent position-absolute my-3':
        props.transparent,
      'my-3 blur border-radius-lg z-index-3 py-2 shadow py-2 start-0 end-0 mx-4 position-absolute mt-4':
        props.sticky,
      'navbar-light bg-white py-3': props.light,
      'navbar-dark bg-gradient-dark z-index-3 py-3': props.dark,
    }"
  >
    <div
      :class="
        props.transparent || props.light || props.dark
          ? 'container'
          : 'container-fluid px-0'
      "
    >
      <RouterLink
        class="navbar-brand d-none d-md-block"
        :class="[
          (props.transparent && textDark.value) || !props.transparent
            ? 'text-dark font-weight-bolder ms-sm-3'
            : 'text-white font-weight-bolder ms-sm-3',
        ]"
        to="/"
        rel="tooltip"
        title="Navbar Pc"
        data-placement="bottom"
      >
        ELEVATE
      </RouterLink>
      <RouterLink
        class="navbar-brand d-block d-md-none"
        :class="
          props.transparent || props.dark
            ? 'text-white'
            : 'font-weight-bolder ms-sm-3'
        "
        to="/"
        rel="tooltip"
        title="Navbar cel"
        data-placement="bottom"
      >
        ELEVATE
      </RouterLink>
      <button
        class="navbar-toggler"
        type="button"
        @click="toggleMenu"
        aria-controls="navbarNav"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon">
          <i class="material-icons">menu</i>
        </span>
      </button>
      <div class="collapse navbar-collapse" :class="{ show: isMenuOpen }" id="navbarNav">
        <ul class="navbar-nav ms-auto">
          <li class="nav-item mx-2">
            <router-link
              role="button"
              class="nav-link ps-2 d-flex cursor-pointer align-items-center"
              :class="getTextColor()"
              to="/courses"
            >
              <i
                class="material-icons opacity-6 me-2 text-md"
                :class="getTextColor()"
                >book</i
              >
              Cursos
            </router-link>
          </li>
          <li class="nav-item dropdown mx-2" @click="toggleDropdown">
            <a
              role="button"
              class="nav-link ps-2 d-flex cursor-pointer align-items-center"
              :class="getTextColor()"
              id="dropdownMenuPages"
              aria-expanded="false"
            >
              <i
                class="material-icons opacity-6 me-2 text-md"
                :class="getTextColor()"
                >dashboard</i
              >
              Paginas
              <img
                :src="getArrowColor()"
                alt="down-arrow"
                class="arrow ms-2 d-lg-block d-none"
              />
              <img
                :src="getArrowColor()"
                alt="down-arrow"
                class="arrow ms-1 d-lg-none d-block ms-auto"
              />
            </a>
            <div
              class="dropdown-menu dropdown-menu-animation ms-n3 dropdown-md p-3 border-radius-xl mt-0 mt-lg-3"
              :class="{ show: isDropdownOpen }"
              aria-labelledby="dropdownMenuPages"
            >
              <div class="row">
                <div class="col-12 px-4 py-2">
                  <div class="row">
                    <div class="position-relative">
                      <div
                        class="dropdown-header text-dark font-weight-bolder d-flex align-items-center px-1"
                      >
                        Paginas de destino
                      </div>
                      <RouterLink
                        :to="{ name: 'about' }"
                        class="dropdown-item border-radius-md"
                      >
                        <span>Sobre nosotros</span>
                      </RouterLink>
                      <RouterLink
                        :to="{ name: 'contactus' }"
                        class="dropdown-item border-radius-md"
                      >
                        <span>Contactanos</span>
                      </RouterLink>
                      <div v-if="isLoggedIn"></div>
                      <div class="RegisterLogin" v-else>
                        <div
                          class="dropdown-header text-dark font-weight-bolder d-flex align-items-center px-0 mt-3"
                        >
                          Cuenta
                        </div>
                        <RouterLink
                          :to="{ name: 'login-view' }"
                          class="dropdown-item border-radius-md"
                        >
                          <span>Iniciar Sesion</span>
                        </RouterLink>
                        <RouterLink
                          :to="{ name: 'register-view' }"
                          class="dropdown-item border-radius-md"
                        >
                          <span>Registro</span>
                        </RouterLink>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </li>
          <!-- Panel exclusivo para Administrador de Página (rol 3) -->
          <li v-if="isAdminPage" class="nav-item mx-2">
            <router-link
              role="button"
              class="nav-link ps-2 d-flex cursor-pointer align-items-center"
              :class="getTextColor()"
              to="/admin/page-dashboard"
            >
              <i
                class="material-icons opacity-6 me-2 text-md"
                :class="getTextColor()"
                >admin_panel_settings</i
              >
              Panel
            </router-link>
          </li>
          <li v-else-if="isLoggedIn" class="nav-item mx-2">
            <router-link
              role="button"
              class="nav-link ps-2 d-flex cursor-pointer align-items-center"
              :class="getTextColor()"
              to="/dashboard"
            >
              <i
                class="material-icons opacity-6 me-2 text-md"
                :class="getTextColor()"
                >person</i
              >
              Panel
            </router-link>
          </li>
          <li v-if="isLoggedIn">
            <button @click="cerrarSesion" class="btn btn-danger ms-2" href="/">
              Cerrar Sesión
            </button>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { RouterLink } from "vue-router";
import { computed, ref, watch } from "vue";
import { useWindowsWidth } from "../../assets/js/useWindowsWidth";
import { useAppStore } from "@/stores"; // Pinia store
import Swal from "sweetalert2";

import ArrDark from "@/assets/img/down-arrow-dark.svg";
import DownArrWhite from "@/assets/img/down-arrow-white.svg";

// Variables relacionadas con el props
const props = defineProps({
  action: {
    type: Object,
    route: String,
    color: String,
    label: String,
  },
  transparent: {
    type: Boolean,
    default: false,
  },
  light: {
    type: Boolean,
    default: false,
  },
  dark: {
    type: Boolean,
    default: false,
  },
  sticky: {
    type: Boolean,
    default: false,
  },
  darkText: {
    type: Boolean,
    default: false,
  },
});

const appStore = useAppStore();
const isLoggedIn = computed(() => appStore.isLoggedIn);
const isAdminPage = computed(() => appStore.getTipoPersona === 3);

const cerrarSesion = () => {
  appStore.logout();
  Swal.fire('Éxito', 'Cierre de sesión exitoso', 'success').then(() => {
    window.location.href = '/'; // Asegúrate de ajustar la ruta a la correcta
  });
};

// set text color
const getTextColor = () => {
  let color;
  if (props.transparent && textDark.value) {
    color = "text-dark";
  } else if (props.transparent) {
    color = "text-white";
  } else {
    color = "text-dark";
  }

  return color;
};

// set nav color on mobile && desktop
let textDark = ref(props.darkText);
const { type } = useWindowsWidth();

if (type.value === "mobile") {
  textDark.value = true;
} else if (type.value === "desktop" && textDark.value == false) {
  textDark.value = false;
}

watch(
  () => type.value,
  (newValue) => {
    if (newValue === "mobile") {
      textDark.value = true;
    } else {
      textDark.value = false;
    }
  }
);

const getArrowColor = () => {
  return props.transparent || props.dark ? DownArrWhite : ArrDark;
};

// Reactive state for menu toggle
const isMenuOpen = ref(false);
const isDropdownOpen = ref(false);

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
};
</script>

<style scoped>
.navbar-toggler {
  border: none;
  &:focus {
    box-shadow: none;
  }
}

.dropdown-menu.show {
  display: block;
}
</style>
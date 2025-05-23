<template>
  <div class="container position-sticky z-index-sticky top-0">
    <div class="row">
      <div class="col-12">
        <DefaultNavbar :sticky="true" />
      </div>
    </div>
  </div>
  <div class="banner-container">
  </div>

  <template v-if="isLogin">
      <div class="DashboardContainer">
        <div class="barralateral">
          <MySideBar @optionSelected="optionSelected" />
        </div>
        <div class="content">
          <component :is="currentComponent" />
        </div>
      </div>
    </template>
    <template v-else>
      <div class="administrador">
        <div class="tarjeta">
          <h1>Debe encontrarse registrado para visualizar su panel</h1>
          <p>Puede volver a la pagina principal</p>
          <router-link to="/" class="ruteo">Home</router-link>
        </div>
      </div>
    </template>
</template>

<script>
import DefaultNavbar from "../examples/navbars/NavbarDefault.vue";
import MySideBar from "./Sections/SideBar.vue";// Asegúrate de que la ruta sea correcta
import ProfileView from "./ProfileView.vue";
import CreateCourse from "./CreateCourse.vue";
import { useAppStore } from "../stores";
import { computed } from "vue";
import MyCourses from "./MyCourses.vue";
import MyCoursesFavorites from "./MyCoursesFavorites.vue";

export default {
  components: {
    DefaultNavbar,
    MySideBar,
    ProfileView,
    CreateCourse,
    MyCourses,
    MyCoursesFavorites,
  },
  setup() {
    const appStore = useAppStore(); // Instancia del store de la aplicación

    const isLogin = computed(() => appStore.isLoggedIn);

    return {
      isLogin,
    };
  },
  data() {
    return {
      currentComponent: null, // Inicializar currentComponent
    };
  },
  methods: {
    optionSelected(option) {
      switch (option) {
        case "profile":
          this.currentComponent = "ProfileView"; // Cambiar componente según la opción seleccionada
          break;
        case "create_courses":
          this.currentComponent = "CreateCourse"; // Cambiar componente según la opción seleccionada
          break;
        case "my_courses":
          this.currentComponent = "MyCourses"; // Cambiar componente según la opción seleccionada
          break;
        case "my_courses_favorites":
          this.currentComponent = "MyCoursesFavorites"; // Cambiar componente según la opción seleccionada
          break;  
        default:
          this.currentComponent = null; // Manejar otros casos si es necesario
      }
    },
  },
};
</script>

<style scoped>
.administrador {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  height: 80vh;
  background-color: white;
}
.tarjeta {
  width: 80%;
  border: 1px solid black;
  border-radius: 15px;
  background-color:#FCEEDA;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.ruteo{
  background-color: #566e66;
  color: white;
  padding: 10px;
  border: 1px solid black;
  border-radius: 10px;
  text-align: center;
  width: 30%;
}
.DashboardContainer {
  display: flex;
  flex-direction: row;
}

.banner-container {
  width: 100%;
  height: 85px;
  background-color: rgb(172, 242, 242);
}

.barralateral {
  height: 145vh; /* Mantiene la altura de la barra lateral */
  background-color: rgb(172, 242, 242);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
}

.content {
  width: 100%;
}

</style>

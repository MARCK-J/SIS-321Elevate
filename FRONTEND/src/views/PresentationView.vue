<script setup>
import { onMounted, onUnmounted } from "vue";

//example components
import NavbarDefault from "../examples/navbars/NavbarDefault.vue";
import Header from "../examples/Header.vue";
import FilledInfoCard from "../examples/cards/infoCards/FilledInfoCard.vue";

//Vue Material Kit 2 components
import MaterialSocialButton from "@/components/MaterialSocialButton.vue";

// sections
import PresentationCounter from "./Sections/PresentationCounter.vue";
import PresentationPages from "./Sections/PresentationPages.vue";
import PresentationExample from "./Sections/PresentationExample.vue";
import BuiltByDevelopers from "./Sections/BuiltByDevelopers.vue";
import PresentationInformation from "./Sections/PresentationInformation.vue";

//images
import vueMkHeader from "@/assets/img/vue-mk-header.jpg";


//hooks
const body = document.getElementsByTagName("body")[0];
onMounted(() => {
  body.classList.add("presentation-page");
  body.classList.add("bg-gray-200");
});
onUnmounted(() => {
  body.classList.remove("presentation-page");
  body.classList.remove("bg-gray-200");
});
</script>

<template>
  <div class="container position-sticky z-index-sticky top-0">
    <div class="row">
      <div class="col-12">
        <NavbarDefault :sticky="true" />
      </div>
    </div>
  </div>
  <Header>
    <div
      class="page-header min-vh-75"
      :style="{ backgroundImage: `url(${vueMkHeader})` }"
      loading="lazy"
    >
      <div class="container">
        <div class="row">
          <div class="col-lg-7 text-center mx-auto position-relative">
            <h1 class="custom-title">
              ELEVATE
            </h1>
            <p class="custom-subtitle">
              Donde tu aprendizaje se eleva
            </p>
          </div>
        </div>
      </div>
    </div>
  </Header>

  <div class="card card-body blur shadow-blur mx-3 mx-md-4 mt-n6">
    <PresentationCounter />
    <PresentationInformation />
    <PresentationExample  />
    <PresentationPages />
    <BuiltByDevelopers />

  <div class="card card-body blur shadow-blur mx-3 mx-md-4 mt-n6 rounded-3">
  

  <div class="container my-5">
    <div class="row g-4">
      <div class="col-lg-4">
        <FilledInfoCard
          class="p-4 rounded-3 shadow-sm"
          :color="{ text: 'white', background: 'bg-gradient-primary' }"
          :icon="{ component: 'flag', color: 'white' }"
          title="Integración rápida"
          description="Únete a nuestro plantel docente con solo registrarte."
          :action="{
            route: '#',
            label: {  }
          }"
        />
      </div>
      <div class="col-lg-4">
        <FilledInfoCard
          class="p-4 rounded-3 shadow-sm"
          :color="{ text: 'white', background: 'bg-gradient-info' }"
          :icon="{ component: 'precision_manufacturing', color: 'white' }"
          title="Expansión de views"
          description="Hazte notar en nuestra plataforma como docente y aumenta tu alcance."
          :action="{
            route: '#',
            label: {  }
          }"
        />
      </div>
      <div class="col-lg-4">
        <FilledInfoCard
          class="p-4 rounded-3 shadow-sm"
          :color="{ text: 'white', background: 'bg-gradient-warning' }"
          :icon="{ component: 'receipt_long', color: 'white' }"
          title="Inspira a miles de jóvenes"
          description="Buscamos expertos comprometidos con el cambio en la educación. ¡Tú podrías ser uno!"
          :action="{
            route: '#',
            label: {  }
          }"
        />
      </div>
    </div>
  </div>

  <div class="py-5" style="background-color: #e7dac0;">
    <div class="container">
      <div class="row align-items-center">
        <div class="col-lg-5 ms-auto text-center text-lg-start">
          <h4 class="mb-1">¡Llegaste al final, pero la educación continúa!</h4>
          <p class="lead mb-0">Síguenos en nuestras redes sociales</p>
        </div>
        <div class="col-lg-5 me-lg-auto text-center text-lg-end mt-4">
          <MaterialSocialButton
            component="twitter"
            color="twitter"
            label="Tweet"
            class="me-2"
            :route="{href: 'https://x.com/ElevateHWN', target: '_blank'}"
          />
          <MaterialSocialButton
            component="facebook-square"
            color="facebook"
            label="Share"
            class="me-2"
            :route="{href: 'https://www.facebook.com/ElevateWellnessClub?locale=es_LA', target: '_blank'}"
          />
          <MaterialSocialButton
            component="instagram"
            color="instagram"
            label="Follow"
            :route="{href: 'https://www.instagram.com/elevate_wellness_club?utm_source=ig_web_button_share_sheet&igsh=ZDNlZDc0MzIxNw==', target: '_blank'}"
          />
        </div>
      </div>
    </div>
  </div>
</div>

  </div>
</template>

<style>
/* Asegúrate de que estos estilos sean cargados después de cualquier otro CSS que pueda sobrescribirlos */

/* Estilo para el título */
.custom-title {
  font-size: 6rem; /* Ajusta el tamaño del texto según sea necesario */
  font-weight: 700; /* Peso de la fuente más grueso */
  letter-spacing: -1px; /* Espaciado de letras más ajustado */
  text-shadow: 2px 2px 4px rgba(201, 196, 196, 0.5); /* Sombra para hacer el texto más legible sobre fondos claros */
  margin: 0; /* Asegúrate de que no haya márgenes inesperados */

}

/* Estilo para el subtítulo */
.custom-subtitle {
  font-size: 2rem; /* Tamaño del texto ajustado para mejor visualización */
  font-weight: 400; /* Peso de la fuente */
  line-height: 1.5; /* Altura de línea para mejorar la legibilidad */
  color: rgb(35, 38, 39); /* Color del texto */
  text-shadow: 2px 2px 4px rgba(253, 253, 253, 0.5);
  margin: 0; /* Asegúrate de que no haya márgenes inesperados */
  padding: 0.5rem; /* Añadido para visualizar el borde claramente */
  border-radius: 5px; /* Opcional: redondear las esquinas del borde */
}

.bg-light {
  background-color: #e7dac0 !important;
}

</style>

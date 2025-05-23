<script setup>
// example component
import HorizontalTeamCard from "@/examples/cards/teamCards/HorizontalTeamCard.vue";

// images
import emma from "@/assets/img/team-5.jpg";
import william from "@/assets/img/bruce-mars.jpg";
import ivana from "@/assets/img/team-6.png";
import marquez from "@/assets/img/ivana-square.jpg";
</script>

<template>
  <section class="pb-5 position-relative bg-gradient-dark mx-n3">
    <div class="container">
      <div class="row">
        <div class="col-md-8 text-start mb-5 mt-5">
          <h3 class="text-white z-index-1 position-relative">
            Nuestro equipo
          </h3>
          <p class="text-white opacity-8 mb-0">
            Los profesionales encargados de hacerte llegar este E-learning son:
          </p>
        </div>
      </div>
      <div class="row">
        <div class="col-lg-6 col-12">
          <HorizontalTeamCard
            class="mt-4"
            :image="emma"
            :profile="{ name: 'Luis Angel Huanca', link: 'javascript:;' }"
            :position="{ label: 'Desarrolador Frontend ', color: 'success' }"
            description="Desarrolla interfaces atractivas y funcionales, asegurando una experiencia de usuario fluida y dinámica."
          />
        </div>
        <div class="col-lg-6 col-12">
          <HorizontalTeamCard
            class="mt-lg-4 mt-5"
            :image="william"
            :profile="{ name: 'Rodrigo Camilo Mendez Gallardo', link: 'javascript:;' }"
            :position="{ label: 'Diseñador UI/UX', color: 'success' }"
            description="Crea diseños intuitivos y centrados en el usuario, mejorando la interacción y la estética de la plataforma."
          />
        </div>
      </div>
      <div class="row mt-4">
        <div class="col-lg-6 col-12">
          <HorizontalTeamCard
            class="mt-4 z-index-2"
            :image="ivana"
            :profile="{ name: 'Marco Reynols', link: 'javascript:;' }"
            :position="{ label: 'Desarrolador Backend', color: 'success' }"
            description="Gestiona la lógica y el funcionamiento interno, garantizando un rendimiento eficiente y seguro de la aplicación."
          />
        </div>
        <div class="col-lg-6 col-12">
          <HorizontalTeamCard
            class="mt-lg-4 mt-5 z-index-2"
            :image="marquez"
            :profile="{ name: 'Adrian Acarapi Roca', link: 'javascript:;' }"
            :position="{ label: 'Bases de Datos', color: 'success' }"
            description="Administra y organiza los datos para asegurar su disponibilidad, integridad y seguridad en la plataforma."
          />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
// example component
import DefaultCounterCard from "../../examples/cards/counterCards/DefaultCounterCard.vue";
</script>
<template>
  <section class="pt-4 pb-6" id="count-stats">
    <div class="container">
      <h4>Empresas que confian en nosotros:</h4><br>
      <div class="row mb-7">
        <div class="col-lg-2 col-md-4 col-6 mb-4">
          <img
            class="w-100 opacity-7"
            src="@/assets/img/logos/gray-logos/logo-coinbase.svg"
            alt="logo"
          />
        </div>
        <div class="col-lg-2 col-md-4 col-6 mb-4">
          <img
            class="w-100 opacity-7"
            src="@/assets/img/logos/gray-logos/logo-nasa.svg"
            alt="logo"
          />
        </div>
        <div class="col-lg-2 col-md-4 col-6 mb-4">
          <img
            class="w-100 opacity-7"
            src="@/assets/img/logos/gray-logos/logo-netflix.svg"
            alt="logo"
          />
        </div>
        <div class="col-lg-2 col-md-4 col-6 mb-4">
          <img
            class="w-100 opacity-7"
            src="@/assets/img/logos/gray-logos/logo-pinterest.svg"
            alt="logo"
          />
        </div>
        <div class="col-lg-2 col-md-4 col-6 mb-4">
          <img
            class="w-100 opacity-7"
            src="@/assets/img/logos/gray-logos/logo-spotify.svg"
            alt="logo"
          />
        </div>
        <div class="col-lg-2 col-md-4 col-6 mb-4">
          <img
            class="w-100 opacity-7"
            src="@/assets/img/logos/gray-logos/logo-vodafone.svg"
            alt="logo"
          />
        </div>
      </div>
      <div class="row justify-content-center text-center">
        <div class="col-md-3">
          <DefaultCounterCard
            title="Proyectos"
            description="Lideramos grandes proyectos relacionados con la educacion"
            :count="5234"
            :duration="3000"
          />
        </div>
        <div class="col-md-3">
          <DefaultCounterCard
            title="Horas"
            description="Que cumpla con los estándares de calidad requeridos por nuestros usuarios."
            :count="3400"
            suffix="+"
            :duration="3000"
          />
        </div>
        <div class="col-md-3">
          <DefaultCounterCard
            title="Soporte"
            description="Involucrados con la educacion y en entregar productos de alta calidad"
            :count="24"
            suffix="/7"
            :duration="4000"
          />
        </div>
      </div>
    </div>
  </section>
</template>

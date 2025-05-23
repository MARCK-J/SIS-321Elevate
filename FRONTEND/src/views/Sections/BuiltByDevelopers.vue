<script setup></script>

<template>
  <div class="container mt-5">
    <div
      class="page-header py-6 my-3 border-radius-xl position-relative overflow-hidden"
      :style="{
        backgroundImage:
          'url(https://raw.githubusercontent.com/creativetimofficial/public-assets/master/soft-ui-design-system/assets/img/desktop.jpg)',
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      }"
      loading="lazy"
    >
      <span class="mask bg-gradient-dark opacity-7"></span>
      <div class="container position-relative z-index-2">
        <div class="row align-items-center">
          <div class="col-lg-7">
            <h4 class="text-white mb-4">¿Te gustaría ser parte de nuestro equipo de docentes?</h4>
            <h1 class="text-white mb-4">Siempre tenemos espacio para un nuevo profesor!</h1>
            <p class="lead text-white opacity-8 mb-4">
              Si estás comprometido con la educación y deseas generar un impacto en la sociedad, puedes unirte como docente y publicar tus cursos en nuestra plataforma.
            </p>
            <a
              href="/pages/register"
              class="btn btn-light text-dark btn-lg"
            >
              Registrate con nosotros
              <i class="fas fa-arrow-right ms-2"></i>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  position: relative;
  overflow: hidden;
  border-radius: 1rem;
}

.mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.container {
  position: relative;
  z-index: 2;
}

h1, h4 {
  font-family: 'Roboto', sans-serif;
}

h1 {
  font-size: 3rem;
  font-weight: 700;
}

h4 {
  font-size: 1.5rem;
  font-weight: 400;
}

.lead {
  font-size: 1.25rem;
}

.btn-light {
  background-color: #ffffff;
  color: #212529;
  border: none;
}

.btn-light:hover {
  background-color: #f8f9fa;
  color: #000000;
}
</style>

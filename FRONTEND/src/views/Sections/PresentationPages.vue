<script setup>
import ExampleCard from "../../components/ExampleCard.vue";
import MaterialBadge from "../../components/MaterialBadge.vue";
// images
import aboutImage from '../../assets/img/illustrations/AboutUs.jpg'; // Reemplaza con el nombre de tu imagen
import contactImage from '../../assets/img/illustrations/ContacUs.jpg'; // Reemplaza con el nombre de tu imagen
import loginImage from '../../assets/img/illustrations/login.jpg'; // Reemplaza con el nombre de tu imagen
import registerImage from '../../assets/img/illustrations/register.jpg'; 

</script>
<template>
  <section >
    <div class="container">
      <div class="row">
        <div class="row text-center my-sm-5 mt-5">
          <div class="col-lg-6 mx-auto">
            <MaterialBadge color="success" class="mb-3"
              >Incrementa tu creatividad</MaterialBadge
            >
            <h2 class="">Visita nuestros fantasticos cursos</h2>
            <p class="lead">
              El aprendizaje es un viaje, y hoy comenzamos una nueva aventura juntos. <br />
              Cada lección es una oportunidad para crecer. ¡Empecemos a aprender!
            </p>
          </div>
        </div>
      </div>
    </div>
    <div class="container">
      <div class="row">
        <div class="col-md-8">
          <div class="row mt-4">
            <div class="col-md-6 mt-md-0 mt-5">
              <ExampleCard
                class="shadow-lg"
                title="About Us Page"
                :image="aboutImage"
                route="about"
              />
            </div>
            <div class="col-md-6 mt-md-0 mt-5">
              <ExampleCard
                class="shadow-lg"
                title="Contact Us Page"
                :image="contactImage"
                route="contactus"
              />
            </div>
          </div>
          <div class="row mt-4">
            <div class="col-md-6 mt-md-0 mt-5">
              <ExampleCard
                classes="mt-5"
                title="Registrate"
                :image="registerImage"
                route="login-view"
              />
            </div>
            <div class="col-md-6 mt-md-0 mt-5">
              <ExampleCard
                classes="shadow-lg"
                title="Author Page"
                image="https://raw.githubusercontent.com/creativetimofficial/public-assets/master/material-design-system/presentation/pages/author.jpg"
                route="author"
              />
            </div>
          </div>
        </div>
        <div class="col-md-3 mx-auto mt-md-0 mt-3">
          <div class="position-sticky" style="top: 100px !important">
            <h3>
              Explora las infinitas posibilidades de aprendizaje que ofrecemos.
            </h3>
            <h6 class="text-secondary font-weight-normal">
              Nuestra plataforma se centra en brindarte las herramientas 
              necesarias para que te enfoques en tu educación y crecimiento personal, 
              dejando de lado las preocupaciones sobre el diseño. Aquí, tu aprendizaje es 
              nuestra prioridad.
            </h6>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

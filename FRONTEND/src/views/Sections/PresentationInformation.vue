<script setup>
import RotatingCard from "../../examples/cards/rotatingCards/RotatingCard.vue";
import RotatingCardFront from "../../examples/cards/rotatingCards/RotatingCardFront.vue";
import RotatingCardBack from "../../examples/cards/rotatingCards/RotatingCardBack.vue";
import DefaultInfoCard from "../../examples/cards/infoCards/DefaultInfoCard.vue";
</script>

<template>
  <section class="bg-light-gray" id="features">
    <div class="container">
      <div class="row align-items-center">
        <div class="col-lg-5 mx-auto p-lg-4 mt-lg-0 mt-4">
          <RotatingCard class="shadow-lg rounded-lg">
            <RotatingCardFront
              image="https://images.unsplash.com/photo-1569683795645-b62e50fbf103?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80"
              icon="touch_app"
              title="Los mejores <br /> docentes"
              description="Nuestros docentes son muy profesionales y están dispuestos a ayudarte en todo momento."
              class="bg-primary text-white rounded-lg p-4"
            />
            <RotatingCardBack
              image="https://images.unsplash.com/photo-1498889444388-e67ea62c464b?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1365&q=80"
              title="La mejor calidad de cursos"
              description="Nuestros cursos están diseñados para que aprendas de la mejor manera posible."
              class="bg-secondary text-white rounded-lg p-4"
            />
          </RotatingCard>
        </div>
        <div class="col-lg-7 mt-5 mt-lg-0">
          <div class="row">
            <DefaultInfoCard
              icon="content_copy"
              title="Documentación a tu alcance"
              description="Cada curso tiene su propia documentación para que puedas repasar lo aprendido."
              class="bg-white shadow-sm rounded-lg p-4 mb-4"
              :style="{ borderLeft: '4px solid #007bff' }"
            />
            <DefaultInfoCard
              icon="flip_to_front"
              title="Recursos a tu alcance"
              description="Desde videos interactivos, cuestionarios para que tu educación sea de la mejor."
              class="bg-white shadow-sm rounded-lg p-4 mb-4"
              :style="{ borderLeft: '4px solid #28a745' }"
            />
          </div>
          <div class="row">
            <DefaultInfoCard
              class="bg-white shadow-sm rounded-lg p-4 mb-4"
              icon="price_change"
              title="Totalmente gratis"
              description="Todos nuestros cursos son gratuitos, no tienes que pagar nada para aprender."
              :style="{ borderLeft: '4px solid #dc3545' }"
            />
            <DefaultInfoCard
              class="bg-white shadow-sm rounded-lg p-4 mb-4"
              icon="devices"
              title="Responsivos"
              description="Nuestros cursos son responsivos, puedes acceder desde cualquier dispositivo."
              :style="{ borderLeft: '4px solid #ffc107' }"
            />
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* Fondo del contenedor */
.bg-light-gray {
  background-color: #f0e5d6;
}

/* Estilo de la tarjeta rotativa */
.rotating-card {
  transition: transform 0.6s ease-in-out;
}

/* Estilo para las tarjetas frontal y posterior */
.rotating-card .card-front {
  background: linear-gradient(135deg, #007bff, #00d084);
}

.rotating-card .card-back {
  background: linear-gradient(135deg, #6c757d, #343a40);
}

/* Estilo para los títulos y descripciones dentro de las tarjetas */
.rotating-card .title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #ffffff;
}

.rotating-card .description {
  font-size: 1rem;
  color: #ffffff;
}

/* Estilo para las tarjetas de información */
.default-info-card {
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.default-info-card .title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
}

.default-info-card .description {
  font-size: 1rem;
  color: #666;
}

/* Espaciado y márgenes */
.mb-4 {
  margin-bottom: 1.5rem;
}

.mt-5 {
  margin-top: 3rem;
}

.p-lg-4 {
  padding: 2rem;
}

.shadow-lg {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.shadow-sm {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>

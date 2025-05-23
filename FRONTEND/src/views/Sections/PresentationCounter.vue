<script setup>
import DefaultCounterCard from "../../examples/cards/counterCards/DefaultCounterCard.vue";
</script>

<template>
  <section class="pt-5 pb-5 bg-gradient-primary text-white" id="count-stats">
    <div class="container">
      <div class="row">
        <div class="col-lg-10 mx-auto py-4 px-3 border border-dark rounded shadow-lg">
          <div class="row">
            <div class="col-md-4 d-flex justify-content-center align-items-center mb-4 mb-md-0">
              <DefaultCounterCard
                color="primary"
                title="Cursos pensando en tu educación"
                description="Aprende con los mejores en su área de una manera didáctica"
                :count="70"
                suffix="+"
                :duration="3000"
                divider="vertical"
              />
            </div>
            <div class="col-md-4 d-flex justify-content-center align-items-center mb-4 mb-md-0">
              <DefaultCounterCard
                color="warning"
                title="Ejercicios de práctica"
                description="Cada docente preparó prácticas en las que puedes repasar tu conocimiento"
                :count="15"
                suffix="+"
                :duration="3000"
                divider="vertical"
              />
            </div>
            <div class="col-md-4 d-flex justify-content-center align-items-center">
              <DefaultCounterCard
                color="success"
                title="Cuestionarios"
                description="Porque la práctica es igual de importante que la teoría, preparamos cuestionarios para cada curso."
                :count="4"
                :duration="3000"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>


section {
  background-color: #798592;
}


.col-md-4 {
  margin-bottom: 20px;
}

.col-md-4:last-of-type {
  margin-bottom: 0;
}


.default-counter-card {
  background-color: #ffffff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.default-counter-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}


.default-counter-card .title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a1818;
}

.default-counter-card .description {
  font-size: 1rem;
  color: #250f0f;
}


.container {
  max-width: 1140px;
}


section .row {
  margin: 0;
}
.bg-gradient-primary {
  background: linear-gradient(100deg, #5bd4f9, #2567ed);
}
</style>

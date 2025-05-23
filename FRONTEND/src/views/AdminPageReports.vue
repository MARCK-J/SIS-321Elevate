<template>
  <div class="container mt-4">
    <h2>Reportes del Sistema</h2>
    <div class="card mb-4">
      <div class="card-header">Reporte de Inscripciones</div>
      <div class="card-body">
        <!-- Aquí puedes mostrar tablas, gráficos, etc. -->
        <p>Total de inscripciones: {{ reportData.totalEnrollments }}</p>
        <!-- Más reportes -->
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const reportData = ref({
  totalEnrollments: 0
})

onMounted(async () => {
  // Llama a tus endpoints para reportes reales
  try {
    // Ejemplo: const res = await axios.get('...');
    // reportData.value.totalEnrollments = res.data.result;
  } catch (e) {
    // Manejo de error
  }
})
</script>
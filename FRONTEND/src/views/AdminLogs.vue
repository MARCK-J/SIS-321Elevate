<template>
  <div class="container mt-4">
    <Breadcrumbs :routes="breadcrumbs" />
    <h2 class="mb-4">Logs de Seguridad</h2>
    <div class="card">
      <div class="card-header">Registros de Actividad</div>
      <div class="card-body p-0">
        <table class="table table-hover mb-0">
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Usuario</th>
              <th>Acción</th>
              <th>Detalle</th>
            </tr>
          </thead>
          <tbody>
            <!-- Aquí irá el listado de logs -->
            <tr v-for="log in logs" :key="log.id">
              <td>{{ log.fecha }}</td>
              <td>{{ log.usuario }}</td>
              <td>{{ log.accion }}</td>
              <td>{{ log.detalle }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Breadcrumbs from '@/examples/Breadcrumbs.vue'

const breadcrumbs = [
  { route: '/', label: 'Inicio' },
  { route: '/admin/logs', label: 'Logs de Seguridad' }
]

// Datos de ejemplo (luego se reemplazarán por datos reales)
const logs = ref([
  { id: 1, fecha: '2025-05-18 10:00', usuario: 'Juan Pérez', accion: 'Login', detalle: 'Inicio de sesión exitoso' },
  { id: 2, fecha: '2025-05-18 10:05', usuario: 'Ana Ruiz', accion: 'Error', detalle: 'Contraseña incorrecta' }
])
</script>
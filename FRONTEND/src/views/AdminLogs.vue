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
import { ref, onMounted } from 'vue'
import axios from 'axios'
import Breadcrumbs from '@/examples/Breadcrumbs.vue'

const breadcrumbs = [
  { route: '/', label: 'Inicio' },
  { route: '/admin/logs', label: 'Logs de Seguridad' }
]

const logs = ref([])

const fetchLogs = async () => {
  try {
    const response = await axios.get('http://localhost:9999/api/v1/logs-seguridad/all')
    // El backend retorna un array de logs con logId, userId, action, details, ipAddress, timestamp
    logs.value = (response.data.result || []).map(log => ({
      id: log.logId,
      fecha: log.timestamp ? new Date(log.timestamp).toLocaleString() : '',
      usuario: log.userId ?? 'N/A', // Si quieres mostrar el email/nombre, debes hacer otra petición aquí
      accion: log.action,
      detalle: log.details
    }))
  } catch (e) {
    logs.value = []
  }
}

onMounted(fetchLogs)
</script>
<template>
  <div class="admin-main-container">
    <div class="container position-sticky z-index-sticky top-0">
      <div class="row">
        <div class="col-12">
          <DefaultNavbar :sticky="true" />
        </div>
      </div>
    </div>

    <div class="admin-banner">
      <div class="banner-content">
        <h1>Panel de Administrador de Página</h1>
        <p class="welcome-text">Bienvenido, Administrador de Página</p>
        <div class="quick-stats">
          <div class="quick-stat">
            <span class="stat-icon">👥</span>
            <span class="stat-value">{{ stats.users }}</span>
            <span class="stat-label">Usuarios</span>
          </div>
          <div class="quick-stat">
            <span class="stat-icon">📚</span>
            <span class="stat-value">{{ stats.courses }}</span>
            <span class="stat-label">Cursos</span>
          </div>
          <div class="quick-stat">
            <span class="stat-icon">📝</span>
            <span class="stat-value">{{ stats.enrollments }}</span>
            <span class="stat-label">Inscripciones</span>
          </div>
        </div>
      </div>
    </div>

    <div class="admin-dashboard">
      <div class="admin-sidebar">
        <div class="sidebar-header">
          <h3>Menú</h3>
        </div>
        <div 
          class="sidebar-option"
          v-for="option in adminOptions"
          :key="option.value"
          @click="currentView = option.value"
          :class="{ active: currentView === option.value }"
        >
          <span class="material-icons-round">{{ option.icon }}</span>
          <span>{{ option.label }}</span>
        </div>
      </div>

      <div class="admin-content">
        <div v-if="currentView === 'stats'" class="view-container">
          <div class="dashboard-section">
            <h2>Resumen General</h2>
            <div class="grid-container">
              <!-- Estadística de usuarios por tipo -->
              <div class="chart-card">
                <h3>Usuarios por Tipo</h3>
                <div class="svg-chart-container">
                  <svg viewBox="0 0 400 200" class="bar-chart">
                    <rect
                      x="60"
                      :y="200 - userStats.estudiantes * 2"
                      width="80"
                      :height="userStats.estudiantes * 2"
                      fill="#6a8c7d"
                      rx="4"
                    />
                    <rect
                      x="200"
                      :y="200 - userStats.docentes * 2"
                      width="80"
                      :height="userStats.docentes * 2"
                      fill="#566e66"
                      rx="4"
                    />
                    <text x="100" y="195" text-anchor="middle" font-size="14" fill="#666">Estudiantes</text>
                    <text x="240" y="195" text-anchor="middle" font-size="14" fill="#666">Docentes</text>
                    <text x="100" :y="200 - userStats.estudiantes * 2 - 10" text-anchor="middle" font-size="16" fill="#333">{{ userStats.estudiantes }}</text>
                    <text x="240" :y="200 - userStats.docentes * 2 - 10" text-anchor="middle" font-size="16" fill="#333">{{ userStats.docentes }}</text>
                  </svg>
                </div>
              </div>

              <!-- Gráfico de cursos por categoría -->
              <div class="chart-card">
                <h3>Cursos por Categoría</h3>
                <div class="svg-chart-container">
                  <svg viewBox="0 0 400 200" class="bar-chart">
                    <g v-for="(cat, idx) in categoriesWithCounts" :key="cat.id">
                      <rect
                        :x="40 + idx * 60"
                        :y="200 - cat.count * 10"
                        width="40"
                        :height="cat.count * 10"
                        :fill="barColors[idx % barColors.length]"
                        rx="4"
                      />
                      <text
                        :x="60 + idx * 60"
                        y="195"
                        text-anchor="middle"
                        font-size="12"
                        fill="#666"
                        style="writing-mode: vertical-lr; glyph-orientation-vertical: 0;"
                      >
                        {{ cat.nameCategory }}
                      </text>
                      <text
                        :x="60 + idx * 60"
                        :y="200 - cat.count * 10 - 5"
                        text-anchor="middle"
                        font-size="14"
                        fill="#333"
                      >
                        {{ cat.count }}
                      </text>
                    </g>
                  </svg>
                </div>
              </div>
            </div>
          </div>
          <!-- Reporte resumen -->
          <div class="dashboard-section mt-4">
            <h2>Reporte General</h2>
            <table class="table table-bordered">
              <thead>
                <tr>
                  <th>Categoría</th>
                  <th>Cursos</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="cat in categoriesWithCounts" :key="cat.id">
                  <td>{{ cat.nameCategory }}</td>
                  <td>{{ cat.count }}</td>
                </tr>
                <tr>
                  <td><strong>Total Cursos</strong></td>
                  <td><strong>{{ stats.courses }}</strong></td>
                </tr>
              </tbody>
            </table>
            <table class="table table-bordered mt-3">
              <thead>
                <tr>
                  <th>Tipo de Usuario</th>
                  <th>Cantidad</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>Estudiantes</td>
                  <td>{{ userStats.estudiantes }}</td>
                </tr>
                <tr>
                  <td>Docentes</td>
                  <td>{{ userStats.docentes }}</td>
                </tr>
                <tr>
                  <td><strong>Total Usuarios</strong></td>
                  <td><strong>{{ stats.users }}</strong></td>
                </tr>
              </tbody>
            </table>
            <div class="mt-3">
              <strong>Total Inscripciones:</strong> {{ stats.enrollments }}
            </div>
          </div>
        </div>

        <!-- Enlaces a reportes y logs -->
        <div v-if="currentView === 'reports'" class="view-container">
          <router-link to="/admin/page-reports" class="btn btn-primary mb-3">Ver Reportes</router-link>
          <router-link to="/admin/logs" class="btn btn-outline-secondary mb-3">Ver Logs de Seguridad</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import DefaultNavbar from "../examples/navbars/NavbarDefault.vue"

const stats = ref({
  users: 0,
  courses: 0,
  enrollments: 0
})

const userStats = ref({
  estudiantes: 0,
  docentes: 0
})

const categories = ref([])
const courses = ref([])
const categoriesWithCounts = computed(() => {
  // Devuelve [{id, nameCategory, count}]
  return categories.value.map(cat => {
    const count = courses.value.filter(c => c.categoryCourseId === cat.id).length;
    return { ...cat, count };
  });
});
const barColors = ['#566e66', '#6a8c7d', '#8db39e', '#b0d9bf', '#d3ffe0', '#f1e4cb', '#f9c784', '#f7b267'];

const adminOptions = [
  { value: 'stats', label: 'Estadísticas', icon: 'insights' },
  { value: 'reports', label: 'Reportes y Logs', icon: 'bar_chart' }
]
const currentView = ref('stats')

onMounted(async () => {
  try {
    // Usuarios
    const usersRes = await axios.get('http://localhost:9999/api/v1/user/all', {
      headers: { Accept: "application/json" }
    });
    const usersArr = Array.isArray(usersRes.data.result) ? usersRes.data.result : [];
    stats.value.users = usersArr.length;
    userStats.value.estudiantes = usersArr.filter(u => u.role && u.role.name === "Estudiante").length;
    userStats.value.docentes = usersArr.filter(u => u.role && u.role.name === "Docente").length;

    // Cursos
    const coursesRes = await axios.get('http://localhost:9999/api/v1/courses/all', {
      headers: { Accept: "application/json" }
    });
    courses.value = Array.isArray(coursesRes.data.result) ? coursesRes.data.result : [];
    stats.value.courses = courses.value.length;

    // Categorías
    const categoriesRes = await axios.get('http://localhost:9999/api/v1/category/all', {
      headers: { Accept: "application/json" }
    });
    categories.value = Array.isArray(categoriesRes.data.result) ? categoriesRes.data.result.map(cat => ({
      id: cat.id,
      nameCategory: cat.nameCategory
    })) : [];

    // Inscripciones
    const enrollmentsRes = await axios.get('http://localhost:9999/api/v1/enrollments/all', {
      headers: { Accept: "application/json" }
    });
    stats.value.enrollments = Array.isArray(enrollmentsRes.data.result) ? enrollmentsRes.data.result.length : 0;
  } catch (e) {
    // Manejo de error
    console.error("Error al cargar estadísticas:", e);
  }
});
</script>

<style scoped>
/* ...estilos igual que antes... */
.admin-main-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}
.admin-banner {
  background: linear-gradient(135deg, #566e66 0%, #3a4d44 100%);
  color: white;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.banner-content {
  max-width: 1200px;
  margin: 0 auto;
}
.welcome-text {
  font-size: 1.2rem;
  opacity: 0.9;
  margin-bottom: 1.5rem;
}
.quick-stats {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 1.5rem;
}
.quick-stat {
  background: rgba(255, 255, 255, 0.15);
  padding: 1rem 1.5rem;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 100px;
}
.stat-icon {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}
.stat-value {
  font-size: 1.8rem;
  font-weight: bold;
}
.stat-label {
  font-size: 0.9rem;
  opacity: 0.9;
}
.admin-dashboard {
  display: flex;
  min-height: calc(100vh - 200px);
}
.admin-sidebar {
  width: 280px;
  background: white;
  box-shadow: 2px 0 10px rgba(0,0,0,0.05);
  padding: 1rem 0;
}
.sidebar-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
  margin-bottom: 1rem;
}
.sidebar-header h3 {
  color: #566e66;
  font-size: 1.2rem;
}
.sidebar-option {
  display: flex;
  align-items: center;
  padding: 0.8rem 1.5rem;
  cursor: pointer;
  transition: all 0.2s;
  color: #555;
}
.sidebar-option:hover {
  background-color: #f0f2f5;
  color: #566e66;
}
.sidebar-option.active {
  background-color: #e6f0ed;
  color: #566e66;
  border-left: 4px solid #566e66;
}
.sidebar-option .material-icons-round {
  margin-right: 10px;
  font-size: 1.2rem;
}
.admin-content {
  flex: 1;
  padding: 2rem;
  background-color: white;
  margin: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.view-container {
  animation: fadeIn 0.3s ease;
}
.dashboard-section {
  margin-bottom: 2rem;
}
.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}
.chart-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-top: 4px solid #566e66;
}
.chart-card h3 {
  color: #333;
  font-size: 1.2rem;
  margin-bottom: 1rem;
}
.chart-container {
  position: relative;
  height: 250px;
  width: 100%;
}
.svg-chart-container {
  width: 100%;
  height: 200px;
  margin-top: 1rem;
}
.bar-chart, .pie-chart, .line-chart {
  width: 100%;
  height: 100%;
}
.pie-chart {
  margin: 0 auto;
  display: block;
}
.line-chart {
  background-color: #f9f9f9;
  border-radius: 4px;
}
@media (max-width: 768px) {
  .admin-dashboard {
    flex-direction: column;
  }
  .admin-sidebar {
    width: 100%;
    display: flex;
    overflow-x: auto;
    padding: 0.5rem 0;
  }
  .sidebar-option {
    flex-direction: column;
    padding: 0.8rem;
    min-width: 100px;
    text-align: center;
  }
  .sidebar-option .material-icons-round {
    margin-right: 0;
    margin-bottom: 5px;
  }
  .quick-stats {
    flex-direction: column;
    gap: 1rem;
    align-items: center;
  }
  .quick-stat {
    width: 80%;
  }
}
</style>
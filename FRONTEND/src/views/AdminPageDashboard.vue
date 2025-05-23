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
              <!-- grafico de barras svg, inscripciones por mes (ejemplo) -->
              <div class="chart-card">
                <h3>Inscripciones Mensuales</h3>
                <div class="svg-chart-container">
                  <svg viewBox="0 0 400 200" class="bar-chart">
                    <rect v-for="(value, index) in enrollmentData" 
                      :key="index"
                      :x="index * 60 + 10" 
                      :y="200 - value * 2" 
                      width="40" 
                      :height="value * 2"
                      fill="#566e66"
                      rx="4"
                    />
                    <text v-for="(value, index) in enrollmentData"
                      :key="'text-'+index"
                      :x="index * 60 + 30"
                      y="195"
                      text-anchor="middle"
                      font-size="12"
                      fill="#666"
                    >
                      {{ months[index] }}
                    </text>
                    <text v-for="(value, index) in enrollmentData"
                      :key="'value-'+index"
                      :x="index * 60 + 30"
                      :y="200 - value * 2 - 5"
                      text-anchor="middle"
                      font-size="12"
                      fill="#333"
                    >
                      {{ value }}
                    </text>
                  </svg>
                </div>
              </div>

              <!-- grafico de torta svg: distribución de cursos por categoría (ejemplo) -->
              <div class="chart-card">
                <h3>Cursos por Categoría</h3>
                <div class="svg-chart-container">
                  <svg viewBox="0 0 200 200" class="pie-chart">
                    <path v-for="(slice, index) in pieSlices" 
                      :key="index"
                      :d="slice.path"
                      :fill="slice.color"
                    />
                    <circle cx="100" cy="100" r="50" fill="#f5f7fa" />
                    <text v-for="(label, index) in pieLabels"
                      :key="'label-'+index"
                      :x="label.x"
                      :y="label.y"
                      text-anchor="middle"
                      font-size="10"
                      fill="#333"
                    >
                      {{ label.text }}
                    </text>
                  </svg>
                </div>
              </div>
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

const months = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun']
const enrollmentData = ref([65, 59, 80, 81, 56, 72]) // Puedes reemplazar con datos reales

const adminOptions = [
  { value: 'stats', label: 'Estadísticas', icon: 'insights' },
  { value: 'reports', label: 'Reportes y Logs', icon: 'bar_chart' }
]
const currentView = ref('stats')

// Para el gráfico de torta (ejemplo de categorías)
const pieSlices = computed(() => {
  const data = [35, 25, 20, 15, 5];
  const colors = ['#566e66', '#6a8c7d', '#8db39e', '#b0d9bf', '#d3ffe0'];
  let cumulativeAngle = 0;
  
  return data.map((value, index) => {
    const angle = (value / 100) * 360;
    const startAngle = cumulativeAngle;
    cumulativeAngle += angle;
    
    const startX = 100 + Math.cos((startAngle - 90) * Math.PI / 180) * 100;
    const startY = 100 + Math.sin((startAngle - 90) * Math.PI / 180) * 100;
    const endX = 100 + Math.cos((cumulativeAngle - 90) * Math.PI / 180) * 100;
    const endY = 100 + Math.sin((cumulativeAngle - 90) * Math.PI / 180) * 100;
    
    const largeArcFlag = angle > 180 ? 1 : 0;
    
    return {
      path: `M100,100 L${startX},${startY} A100,100 0 ${largeArcFlag},1 ${endX},${endY} Z`,
      color: colors[index]
    };
  });
});

const pieLabels = computed(() => {
  const labels = ['Programación', 'Diseño', 'Marketing', 'Negocios', 'Otros'];
  const angles = [0, 72, 144, 216, 288];
  
  return angles.map((angle, index) => {
    const x = 100 + Math.cos((angle - 90) * Math.PI / 180) * 70;
    const y = 100 + Math.sin((angle - 90) * Math.PI / 180) * 70;
    
    return {
      x,
      y,
      text: labels[index]
    };
  });
});

onMounted(async () => {
  try {
    // Usuarios
    const usersRes = await axios.get('http://localhost:9999/api/v1/user/all', {
      headers: { Accept: "application/json" }
    });
    stats.value.users = Array.isArray(usersRes.data.result) ? usersRes.data.result.length : 0;

    // Cursos
    const coursesRes = await axios.get('http://localhost:9999/api/v1/courses/all', {
      headers: { Accept: "application/json" }
    });
    stats.value.courses = Array.isArray(coursesRes.data.result) ? coursesRes.data.result.length : 0;

    // Inscripciones
    const enrollmentsRes = await axios.get('http://localhost:9999/api/v1/enrollments/all', {
      headers: { Accept: "application/json" }
    });
    stats.value.enrollments = Array.isArray(enrollmentsRes.data.result) ? enrollmentsRes.data.result.length : 0;

    // Puedes procesar enrollmentData y pieSlices con datos reales aquí si lo deseas
  } catch (e) {
    // Manejo de error
    console.error("Error al cargar estadísticas:", e);
  }
});
</script>

<style scoped>
/* Copia los estilos del AdminDashboardView.vue aquí para mantener la apariencia */
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
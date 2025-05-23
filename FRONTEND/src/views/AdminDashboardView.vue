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
        <h1>Panel de Administración</h1>
        <p class="welcome-text">Bienvenido, Administrador</p>
        <div class="quick-stats">
          <div class="quick-stat">
            <span class="stat-icon">👥</span>
            <span class="stat-value">1,248</span>
            <span class="stat-label">Usuarios</span>
          </div>
          <div class="quick-stat">
            <span class="stat-icon">📚</span>
            <span class="stat-value">56</span>
            <span class="stat-label">Cursos</span>
          </div>
          <div class="quick-stat">
            <span class="stat-icon">💰</span>
            <span class="stat-value">$12,480</span>
            <span class="stat-label">Ingresos</span>
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

      <!-- Desde aqui empieza el Dashboard -->
      <div class="admin-content">
        <!-- graficos -->
        <div v-if="currentView === 'stats'" class="view-container">
          <div class="dashboard-section">
            <h2>Resumen General</h2>
            <div class="grid-container">
              <!-- grafico de barras svg, estan las inscripciones por mes como ejemplo -->
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

              <!-- grafico de torta svg: puse la distribucion de los cursos como ejemplo -->
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

              <!-- grafico de Línea svg: ejemplo ingresos -->
              <div class="chart-card">
                <h3>Ingresos Mensuales</h3>
                <div class="svg-chart-container">
                  <svg viewBox="0 0 400 200" class="line-chart">
                    <polyline 
                      points="20,180 60,150 100,120 140,100 180,80 220,60 260,40 300,20 340,40 380,60"
                      fill="none"
                      stroke="#566e66"
                      stroke-width="3"
                    />
                    <circle v-for="(point, index) in linePoints"
                      :key="index"
                      :cx="point.x"
                      :cy="point.y"
                      r="4"
                      fill="#566e66"
                    />
                    <text v-for="(month, index) in months"
                      :key="'month-'+index"
                      :x="index * 40 + 20"
                      y="195"
                      text-anchor="middle"
                      font-size="10"
                      fill="#666"
                    >
                      {{ month }}
                    </text>
                  </svg>
                </div>
              </div>

              <!-- Tarjetas de metricas rápidas -->
              <div class="metric-card">
                <div class="metric-content">
                  <span class="metric-icon">📈</span>
                  <div class="metric-text">
                    <h4>Crecimiento</h4>
                    <p class="metric-value">+12%</p>
                    <p class="metric-description">vs mes anterior</p>
                  </div>
                </div>
              </div>

              <div class="metric-card">
                <div class="metric-content">
                  <span class="metric-icon">🎓</span>
                  <div class="metric-text">
                    <h4>Cursos nuevos</h4>
                    <p class="metric-value">5</p>
                    <p class="metric-description">este mes</p>
                  </div>
                </div>
              </div>

              <div class="metric-card">
                <div class="metric-content">
                  <span class="metric-icon">⭐</span>
                  <div class="metric-text">
                    <h4>Valoración</h4>
                    <p class="metric-value">4.8/5</p>
                    <p class="metric-description">(328 reseñas)</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- vista de lods usuarios -->
        <div v-if="currentView === 'users'" class="view-container">
          <h2>Gestión de Usuarios</h2>
          <div class="users-table-container">
            <div class="table-header">
              <h3>Listado de Usuarios</h3>
              <button class="add-user-btn">
                <span class="material-icons-round">person_add</span>
                Agregar Usuario
              </button>
            </div>
            <div class="table-responsive">
              <table class="users-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Email</th>
                    <th>Rol</th>
                    <th>Fecha Registro</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="user in sampleUsers" :key="user.id">
                    <td>{{ user.name }}</td>
                    <td>{{ user.email }}</td>
                    <td><span class="role-badge" :class="user.role">{{ user.role }}</span></td>
                    <td>{{ user.registered }}</td>
                    <td class="actions">
                      <button class="action-btn edit">
                        <span class="material-icons-round">edit</span>
                      </button>
                      <button class="action-btn delete">
                        <span class="material-icons-round">delete</span>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- vista de cursos -->
        <div v-if="currentView === 'courses'" class="view-container">
          <h2>Gestión de Cursos</h2>
          <div class="courses-grid">
            <div class="course-card" v-for="course in sampleCourses" :key="course.id">
              <div class="course-image">
                <img :src="course.image" :alt="course.title">
                <span class="category-badge">{{ course.category }}</span>
              </div>
              <div class="course-info">
                <h3>{{ course.title }}</h3>
                <p class="instructor">{{ course.instructor }}</p>
                <div class="course-meta">
                  <span class="students">
                    <span class="material-icons-round">people</span>
                    {{ course.students }}
                  </span>
                  <span class="rating">
                    <span class="material-icons-round">star</span>
                    {{ course.rating }}
                  </span>
                </div>
                <div class="course-actions">
                  <button class="btn-edit">
                    <span class="material-icons-round">edit</span>
                    Editar
                  </button>
                  <button class="btn-view">
                    <span class="material-icons-round">visibility</span>
                    Ver
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import DefaultNavbar from "../examples/navbars/NavbarDefault.vue";

export default {
  components: {
    DefaultNavbar
  },
  data() {
    return {
      currentView: 'stats',
      months: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
      enrollmentData: [65, 59, 80, 81, 56, 72],
      adminOptions: [
        { value: 'stats', label: 'Estadísticas', icon: 'insights' },
        { value: 'users', label: 'Usuarios', icon: 'people' },
        { value: 'courses', label: 'Cursos', icon: 'school' }
      ],
      sampleUsers: [
        { id: 1, name: 'Juan Pérez', email: 'juan@example.com', role: 'admin', registered: '15/03/2023' },
        { id: 2, name: 'María Gómez', email: 'maria@example.com', role: 'docente', registered: '22/04/2023' },
        { id: 3, name: 'Carlos Ruiz', email: 'carlos@example.com', role: 'usuario', registered: '10/05/2023' }
      ],
      sampleCourses: [
        { id: 1, title: 'Introducción a Vue.js', instructor: 'Juan Pérez', category: 'Programación', 
          students: 245, rating: 4.8, image: 'https://via.placeholder.com/300x200?text=Vue.js' },
        { id: 2, title: 'Diseño UX/UI', instructor: 'María Gómez', category: 'Diseño', 
          students: 189, rating: 4.7, image: 'https://via.placeholder.com/300x200?text=UX/UI' }
      ]
    };
  },
  computed: {
    pieSlices() {
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
    },
    pieLabels() {
      const labels = ['Programación', 'Diseño', 'Marketing', 'Negocios', 'Otros'];
      const angles = [0, 72, 144, 216, 288]; // Ángulos aproximados
      
      return angles.map((angle, index) => {
        const x = 100 + Math.cos((angle - 90) * Math.PI / 180) * 70;
        const y = 100 + Math.sin((angle - 90) * Math.PI / 180) * 70;
        
        return {
          x,
          y,
          text: labels[index]
        };
      });
    },
    linePoints() {
      const values = [180, 150, 120, 100, 80, 60, 40, 20, 40, 60];
      return values.map((value, index) => ({
        x: index * 40 + 20,
        y: value
      }));
    }
  }
};
</script>

<style scoped>

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

.metric-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
}

.metric-content {
  display: flex;
  align-items: center;
  gap: 1rem;
  width: 100%;
}

.metric-icon {
  font-size: 2rem;
  background: #e6f0ed;
  color: #566e66;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.metric-text {
  flex: 1;
}

.metric-text h4 {
  color: #555;
  font-size: 1rem;
  margin-bottom: 0.3rem;
}

.metric-value {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 0.2rem;
}

.metric-description {
  font-size: 0.8rem;
  color: #777;
}

.users-table-container {
  margin-top: 1.5rem;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.add-user-btn {
  background: #566e66;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  transition: all 0.2s;
}

.add-user-btn:hover {
  background: #3a4d44;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th, .users-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.users-table th {
  background: #f5f7fa;
  color: #566e66;
  font-weight: 600;
}

.role-badge {
  display: inline-block;
  padding: 0.3rem 0.6rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.role-badge.admin {
  background: #e6f0ed;
  color: #566e66;
}

.role-badge.docente {
  background: #fff8e6;
  color: #d4a017;
}

.role-badge.usuario {
  background: #f0f5ff;
  color: #3a6fd4;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.edit {
  background: #e6f0ed;
  color: #566e66;
}

.action-btn.delete {
  background: #ffebee;
  color: #d32f2f;
}

.action-btn:hover {
  transform: scale(1.1);
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}

.course-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.course-image {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.category-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #566e66;
  color: white;
  padding: 0.3rem 0.6rem;
  border-radius: 20px;
  font-size: 0.8rem;
}

.course-info {
  padding: 1rem;
}

.course-info h3 {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.instructor {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 0.8rem;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  color: #555;
}

.course-meta span {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

.course-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-edit, .btn-view {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.5rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit {
  background: #e6f0ed;
  color: #566e66;
}

.btn-view {
  background: #f0f5ff;
  color: #3a6fd4;
}

.btn-edit:hover, .btn-view:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
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
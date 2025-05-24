<template>
  <div class="courses-section">
    <div class="header-section">
      <h2>Mis Cursos</h2>
      <div v-if="isDocente()" class="edit-mode-switch">
        <label for="editModeToggle">Modo Edición</label>
        <label class="switch">
          <input id="editModeToggle" type="checkbox" v-model="isEditMode" />
          <span class="slider round"></span>
        </label>
      </div>
    </div>

    <div v-if="isAdminPage()" class="alert alert-warning mt-4">
      <strong>Esta función no está disponible para el Administrador de Página.</strong>
    </div>

    <div v-else class="courses-container">
      <div class="course" v-for="(course, index) in courses" :key="index">
        <img :src="course.image" alt="Imagen del curso" class="course-image" />
        
        <div v-if="isEditMode">
          <input v-model="course.title" placeholder="Editar título" class="edit-input" />
          <textarea v-model="course.description" placeholder="Editar descripción" class="edit-input"></textarea>
          <button class="save-button" @click="updateCourse(course)">Guardar Cambios</button>
          <button class="delete-button" @click="deleteCourse(course.courseId)">Eliminar Curso</button>
        </div>
        
        <div v-else>
          <h3 class="course-title">Curso: {{ course.title }}</h3>
          <p class="course-description">{{ course.description }}</p>
          <div class="d-flex justify-content-around">
            <RouterLink :to="{ name: 'course', query: { courseId: course.courseId, title: course.title } }">
              <button class="go-to-course-button">Ingresar</button>
            </RouterLink>
            <div v-if="isEstudiante()">
              <button class="unsubscribe-button" @click="confirmUnsubscribe(course.enrollmentId, course.title)">Desinscribirme</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="isModalVisible" class="custom-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h5 v-if="!modalMessage">Confirmar Desinscripción</h5>
          <h5 v-else>{{ modalMessage }}</h5>
          <button class="close-button" @click="closeModal">×</button>
        </div>
        <div class="modal-body" v-if="!modalMessage">
          ¿Estás seguro que deseas desinscribirte del curso <strong>{{ selectedCourseTitle }}</strong>?
        </div>
        <div class="modal-footer" v-if="!modalMessage">
          <button class="cancel-button" @click="cancelUnsubscribe">Cancelar</button>
          <button class="confirm-button" @click="unsubscribe">Desinscribirme</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { RouterLink } from "vue-router";
import { ref, onMounted } from "vue";
import { useAppStore } from "@/stores";

// Define la URL base de la API usando la variable de entorno
// Esto se resolverá a "http://localhost:9999" en desarrollo
// y a "https://tu-backend-render-url.onrender.com" en producción
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL;

export default {
  components: {
    RouterLink,
  },
  setup() {
    const store = useAppStore();
    const courses = ref([]);
    const selectedCourseId = ref(null);
    const selectedCourseTitle = ref("");
    const isModalVisible = ref(false);
    const modalMessage = ref("");
    const isEditMode = ref(false);

    const isEstudiante = () => {
      return store.getTipoPersona === 1;
    };

    const isDocente = () => {
      return store.getTipoPersona === 2;
    };

    const isAdminPage = () => {
      return store.getTipoPersona === 3;
    };

    const fetchEnrollments = async () => {
      try {
        let response;
        const id = store.getIdentificador;
        const rol = store.getTipoPersona;

        if (rol === 1) {
          response = await axios.get(`${API_BASE_URL}/api/v1/enrollments/student/${id}`, {
            headers: { Accept: "application/json" },
          });
          if (response.data.code === "200-OK") {
            const enrollments = response.data.result;
            const coursePromises = enrollments.map((enrollment) =>
              axios
                .get(`${API_BASE_URL}/api/v1/courses/${enrollment.coursesCourseId}`, {
                  headers: { Accept: "application/json" },
                })
                .then((courseResponse) => ({
                  ...courseResponse.data.result,
                  enrollmentId: enrollment.enrollmentId,
                }))
            );
            const courseResponses = await Promise.all(coursePromises);
            courses.value = courseResponses;
          } else {
            console.error("Error al obtener inscripciones:", response.data.message);
          }
        } else if (rol === 2) {
          response = await axios.get(`${API_BASE_URL}/api/v1/courses/teacher/${id}`, {
            headers: { Accept: "application/json" },
          });
          if (response.data.code === "200-OK") {
            courses.value = response.data.result;
          } else {
            console.error("Error al obtener cursos del docente:", response.data.message);
          }
        }
      } catch (error) {
        console.error("Error en la solicitud de inscripciones o cursos:", error);
      }
    };

    const confirmUnsubscribe = (courseId, courseTitle) => {
      selectedCourseId.value = courseId;
      selectedCourseTitle.value = courseTitle;
      isModalVisible.value = true;
      modalMessage.value = "";
    };

    const unsubscribe = async () => {
      try {
        const response = await axios.delete(`${API_BASE_URL}/api/v1/enrollments/${selectedCourseId.value}`, {
          headers: { Accept: "application/json" },
        });
        if (response.data.code === "200-OK") {
          modalMessage.value = `Te desinscribiste con éxito del curso "${selectedCourseTitle.value}".`;
          courses.value = courses.value.filter((course) => course.enrollmentId !== selectedCourseId.value);
          setTimeout(closeModal, 2000);
        } else {
          console.error("Error al desinscribirse:", response.data.message);
        }
      } catch (error) {
        console.error("Error en la solicitud de desinscripción:", error);
      }
    };

    const cancelUnsubscribe = () => {
      modalMessage.value = "Operación Cancelada.";
      setTimeout(closeModal, 2000);
    };

    const closeModal = () => {
      isModalVisible.value = false;
      modalMessage.value = "";
    };

    const updateCourse = async (course) => {
      try {
        const response = await axios.put(`${API_BASE_URL}/api/v1/courses/${course.courseId}`, {
          title: course.title,
          description: course.description,
        }, {
          headers: { Accept: "application/json" },
        });
        if (response.data.code === "200-OK") {
          console.log("Curso actualizado con éxito");
        } else {
          console.error("Error al actualizar el curso:", response.data.message);
        }
      } catch (error) {
        console.error("Error en la solicitud de actualización de curso:", error);
      }
    };

    const deleteCourse = async (courseId) => {
      try {
        const response = await axios.delete(`${API_BASE_URL}/api/v1/courses/${courseId}`, {
          headers: { Accept: "application/json" },
        });
        if (response.data.code === "200-OK") {
          courses.value = courses.value.filter((course) => course.courseId !== courseId);
          console.log("Curso eliminado con éxito");
        } else {
          console.error("Error al eliminar el curso:", response.data.message);
        }
      } catch (error) {
        console.error("Error en la solicitud de eliminación de curso:", error);
      }
    };

    onMounted(() => {
      fetchEnrollments();
    });

    return {
      courses,
      selectedCourseId,
      selectedCourseTitle,
      isModalVisible,
      modalMessage,
      isEditMode,
      isEstudiante,
      isDocente,
      isAdminPage,
      confirmUnsubscribe,
      unsubscribe,
      cancelUnsubscribe,
      closeModal,
      updateCourse,
      deleteCourse,
    };
  },
};
</script>

<style scoped>
.header-section {
  display: flex;
  align-items: center;
  margin-top: 25px;
  margin-left: 25px;
}

.edit-mode-switch {
  display: flex;
  align-items: center;
  margin-left: 20px;
  margin-top: 5px; 
}

.courses-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-left: 25px;
  margin-right: 25px;
}

.course {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 20px;
  max-width: 300px;
  max-height: 500px;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.course-image {
  max-width: 100%;
  max-height: 200px;
  width: auto;
  height: auto;
  object-fit: cover;
  border-radius: 8px;
}

.course-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin: 10px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-description {
  flex-grow: 1;
  margin: 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
}

.switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 20px;
  margin-left: 10px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 20px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #2196F3;
}

input:checked + .slider:before {
  transform: translateX(20px);
}

.edit-input {
  width: 100%;
  margin-top: 5px;
  padding: 8px;
  font-size: 1rem;
}

.save-button, .delete-button {
  padding: 10px 20px;
  margin-top: 10px;
  font-size: 1rem;
}

.unsubscribe-button {
  background-color: #ffcccc;
  color: #333;
}

.custom-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  animation: fadeIn 0.3s ease-in-out;
}

.modal-header {
  padding: 1rem;
  border-bottom: 1px solid #e2e6ea;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h5 {
  margin: 0;
  font-size: 1.25rem;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.modal-body {
  padding: 1rem;
  font-size: 1rem;
  color: #333;
}

.modal-footer {
  padding: 1rem;
  border-top: 1px solid #e2e6ea;
  display: flex;
  justify-content: flex-end;
}

.cancel-button,
.confirm-button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  margin-left: 0.5rem;
}

.cancel-button {
  background-color: #6c757d;
  color: white;
}

.cancel-button:hover {
  background-color: #5a6268;
}

.confirm-button {
  background-color: #dc3545;
  color: white;
}

.confirm-button:hover {
  background-color: #c82333;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
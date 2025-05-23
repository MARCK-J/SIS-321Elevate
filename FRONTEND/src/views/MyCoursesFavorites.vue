<template>
    <div class="courses-section">
      <div class="header-section">
        <h2>Mis Cursos</h2>
      </div>
  
      <div class="courses-container">
        <div class="course" v-for="(course, index) in courses" :key="index">
          <img :src="course.image" alt="Imagen del curso" class="course-image" />
          <div>
            <h3>Curso: {{ course.title }}</h3>
            <p>{{ course.description }}</p>
            <div class="d-flex justify-content-around">
              <RouterLink :to="{ name: 'course', query: { courseId: course.courseId, title: course.title } }">
                <button class="go-to-course-button">Ingresar</button>
              </RouterLink>
              <!-- <div v-if="isEstudiante()">
                <button class="unsubscribe-button" @click="confirmUnsubscribe(course.enrollmentId, course.title)">Quitar de Favoritos</button>
              </div> -->
            </div>
          </div>
        </div>
      </div>
  
      <div v-if="isModalVisible" class="custom-modal">
        <div class="modal-content">
          <div class="modal-header">
            <h5 v-if="!modalMessage">Confirmar Quitar de Favoritos</h5>
            <h5 v-else>{{ modalMessage }}</h5>
            <button class="close-button" @click="closeModal">×</button>
          </div>
          <div class="modal-body" v-if="!modalMessage">
            ¿Estás seguro que deseas quitar de favoritos el curso <strong>{{ selectedCourseTitle }}</strong>?
          </div>
          <div class="modal-footer" v-if="!modalMessage">
            <button class="cancel-button" @click="cancelUnsubscribe">Cancelar</button>
            <button class="confirm-button" @click="unsubscribe">Continuar</button>
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
  
      const fetchCoursesFavorites = async () => {
        try {
          let response;
          const id = store.getIdentificador;
          response = await axios.get(`http://localhost:9999/api/v1/favorites/student/${id}`, {
            headers: { Accept: "application/json" },
          });
          if (response.data.code === "200-OK") {
            const enrollments = response.data.result;
            const coursePromises = enrollments.map((enrollment) =>
              axios
                .get(`http://localhost:9999/api/v1/courses/${enrollment.courseId}`, {
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
          const response = await axios.delete(`http://localhost:9999/api/v1/enrollments/${selectedCourseId.value}`, {
            headers: { Accept: "application/json" },
          });
          if (response.data.code === "200-OK") {
            modalMessage.value = `Quitaste de favoritos el curso "${selectedCourseTitle.value}".`;
            courses.value = courses.value.filter((course) => course.enrollmentId !== selectedCourseId.value);
            setTimeout(closeModal, 2000);
          } else {
            console.error("Error al quitar de favoritos:", response.data.message);
          }
        } catch (error) {
          console.error("Error en la solicitud de quitar de favoritos:", error);
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
  
      onMounted(() => {
        fetchCoursesFavorites();
      });
  
      return {
        courses,
        selectedCourseId,
        selectedCourseTitle,
        isModalVisible,
        modalMessage,
        isEditMode,
        isEstudiante,
        confirmUnsubscribe,
        unsubscribe,
        cancelUnsubscribe,
        closeModal,
      };
    },
  };
  </script>
  
  <style scoped>
  .header-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .courses-container {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .course {
    border: 1px solid #ccc;
    border-radius: 8px;
    padding: 20px;
    max-width: 300px;
    text-align: center;
  }
  
  .course-image {
    max-width: 100%;
    max-height: 200px;
    width: auto;
    height: auto;
    object-fit: cover;
    border-radius: 8px;
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
<script>
// Importaciones
import { ref, onMounted, computed } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import Swal from "sweetalert2";
import MaterialButton from "@/components/MaterialButton.vue";
import { useAppStore } from "@/stores";
import CourseModal from "./CourseModal.vue";
import { AuthService } from "../../services/authService";
import ProgressBar from "@/components/ProgressBar.vue";
import EditCourseModal from "./EditCourseModal.vue";

export default {
  props: {
    id: {
      type: Number,
      default: 0,
      required: false,
    },
  },
  components: {
    MaterialButton,
    CourseModal,
    ProgressBar,
    EditCourseModal,
  },
  data() {
    return {
      inscrito: false,
      showPopup: false,
      showDeletePopup: false,
      selectedLesson: null,
      lessons: [],
      totalLessons: 0,
      showModal: false,
      showEditModal: false,
      courseId: 0,
      courseData: { abilities: "" },
      store: useAppStore(),
      showEditModal: false,
      fieldToEdit: "",
      editContent: "",
      userData: {},
      draggingLesson: null, // Nueva propiedad para arrastrar lecciones
      isModalVisible: false,
      selectedCourseId: null,
      enrollmentID: 0,
      isFavorito: false,
      favoriteCourseId: 0,
      getCertificate:false,

    };
  },
  computed: {
    userId() {
      return this.store.getIdentificador;
    },
    rolId() {
      return this.store.getTipoPersona;
    },
    isDocente() {
      return this.rolId === 2;
    },
    isEstudiante() {
      return this.rolId === 1;
    },
    isInscrito() {
      return this.inscrito;
    },
    abilitiesArray() {
      return this.courseData.abilities.split(";");
    },
    completedLessons() {
      return this.lessons.filter((lesson) => lesson.complete).length;
    },
    progress() {
      if (this.lessons.length === 0) {
        this.getCertificate = false;
        return 0;
      }
      const progressPercentage = (this.completedLessons / this.lessons.length) * 100;
      this.getCertificate = progressPercentage === 100;
      return Math.floor(progressPercentage); // Redondea hacia abajo para obtener un valor entero
    }
  },
  methods: {
    verCertificado() {
      const fullname = this.userData.firstName + " " + this.userData.lastName;
      console.log('nombre completo ' + fullname);
      console.log('Curso ' + this.courseData.title);
      this.$router.push({
        name: "Certificado",
        query: { courseTitle: this.courseData.title, name: fullname },
      });      
    },
    fetchCourseId() {
      const route = useRoute();
      this.courseId = route.query.courseId ? Number(route.query.courseId) : 0;
    },
    async fetchLessonsByCourseId(courseId) {
      try {
        const response = await axios.get(
          `http://localhost:9999/api/v1/lessons/course/${courseId}/ordered`,
          {
            headers: { Accept: "application/json" },
          }
        );

        if (response.status === 200) {
          this.lessons = response.data.result;
        } else {
          console.error(
            "Error al obtener las lecciones:",
            response.data.message
          );
        }
      } catch (error) {
        console.error("Error en la solicitud de lecciones:", error);
      }
    },

    async fetchCourseById() {
      try {
        const response = await axios.get(
          `http://localhost:9999/api/v1/courses/${this.courseId}`,
          {
            headers: { Accept: "application/json" },
          }
        );

        if (response.status === 200) {
          this.courseData = response.data.result;

          await this.fetchLessonsByCourseId(this.courseId);
        } else {
          console.error("Error al obtener el curso:", response.data.message);
        }
      } catch (error) {
        console.error("Error en la solicitud del curso:", error);
      }
    },
    openDeletePopup(lesson) {
      this.selectedLesson = lesson;
      this.showDeletePopup = true;
    },
    closeDeletePopup() {
      this.showDeletePopup = false;
      this.selectedLesson = null;
    },
    async deleteLesson() {
      if (this.selectedLesson) {
        try {
          const response = await axios.delete(
            `http://localhost:9999/api/v1/lessons/${this.selectedLesson.lessonsId}`,
            {
              headers: { Accept: "application/json" },
            }
          );
          if (response.status == 200) {
            await this.fetchLessonsByCourseId(this.courseId);
            Swal.fire(
              "Eliminada",
              "La lección ha sido eliminada con éxito.",
              "success"
            );
          } else {
            Swal.fire("Error", "No se pudo eliminar la lección.", "error");
          }
        } catch (error) {
          Swal.fire("Error", "No se pudo eliminar la lección.", "error");
        } finally {
          this.closeDeletePopup();
        }
      }
    },
    async updateLessonOrder(lesson) {
      try {
        const response = await axios.put(
          `http://localhost:9999/api/v1/lessons/${lesson.lessonsId}`,
          {
            order: lesson.order,
          }
        );
        if (response.status === 200) {
          Swal.fire("Éxito", "Orden de la lección actualizado.", "success");
        } else {
          Swal.fire(
            "Error",
            "No se pudo actualizar el orden de la lección.",
            "error"
          );
        }
      } catch (error) {
        console.error("Error al actualizar el orden de la lección:", error);
      }
    },
    async toggleCompleted(lesson) {
      console.log('Al marcar como completado: '+lesson.lessonsId);
      try {
        const response = await axios.put(
          `http://localhost:9999/api/v1/lessons/${lesson.lessonsId}/complete`,
          );

        if (response.data.code === "200-OK") {
          lesson.completed = true; // Marca la lección como completada
          console.log(`Lección ${lesson.lessonsId} marcada como completada.`);
        } else {
          console.error("Error al marcar la lección como completada:", response.data.message);
        }
      } catch (error) {
        console.error("Error en la solicitud para marcar la lección como completada:", error);
      }
    },
    
    async fetchUserData() {
      try {
        const response = await axios.get(
          `http://localhost:9999/api/v1/user/${this.userId}`,
          {
            headers: { Accept: "application/json" },
          }
        );

        if (response.data.code === "200-OK") {
          this.userData = response.data.result;
        } else {
          console.error(
            "Error al obtener los datos del usuario:",
            response.data.message
          );
        }
      } catch (error) {
        console.error("Error en la solicitud de datos del usuario:", error);
      }
    },
    async fetchEnrollmentId() {
      try {
        const response = await axios.get(
          `http://localhost:9999/api/v1/enrollments/student/${this.userId}`,
          {
            headers: { Accept: "application/json" },
          }
        );

        if (response.status === 200) {
          const courseData = response.data.result;
          if (courseData && Array.isArray(courseData)) {
            let enrollmentID = null;
            const enrollmentFound = courseData.some((enrollment) => {
              if (
                enrollment.studentUserId === this.userId &&
                enrollment.coursesCourseId == this.courseId
              ) {
                enrollmentID = enrollment.enrollmentId;
                return true;
              }
              return false;
            });
            this.inscrito = enrollmentFound;
            this.enrollmentID = enrollmentID;
            console.log("enrollmentID:", this.enrollmentID);
          } else {
            console.error("Datos de inscripción no válidos:", courseData);
          }
        } else {
          console.error(
            "Error al obtener la inscripción:",
            response.data.message
          );
        }
      } catch (error) {
        console.error("Error en la solicitud de inscripción:", error);
      }
    },
    async confirmInscription() {
      this.showPopup = false;
      try {
        const response = await axios.post(
          "http://localhost:9999/api/v1/enrollments/create",
          {
            studentUserId: this.userId,
            coursesCourseId: this.courseId,
            enrollmentDate: new Date().toISOString().split("T")[0],
          },
          {
            headers: {
              "Content-Type": "application/json",
              Accept: "application/json",
            },
          }
        );

        if (response.data.code === "200-OK") {
          AuthService.sendConfirmation(
            this.userData.email,
            this.courseData.title,
            new Date().toISOString().split("T")[0],
            this.courseData.duration
          );
          console.log("al inscribirse " + response.data.result.enrollmentId);
          this.enrollmentID = response.data.result.enrollmentId;
          Swal.fire("Éxito", "Inscripción Confirmada!!!", "success");
          this.inscrito = true;
        } else {
          Swal.fire("Error", "No se pudo confirmar la inscripción.", "error");
        }
      } catch (error) {
        Swal.fire("Error", "No se pudo confirmar la inscripción.", "error");
      }
    },
    irAQuizzes() {
      this.$router.push({
        name: "Quizzes",
        query: { courseId: this.courseId, courseTitle: this.courseData.title },
      });
    },
    startLesson(lessonId, courseTitle) {
      if (this.isDocente || this.isEstudiante) {
        this.$router.push({
          path: "/pages/",
          query: {
            courseId: this.courseId,
            lessonId: lessonId,
            courseTitle: courseTitle,
          },
        });
      } else {
        Swal.fire("Error", "No se encuentra registrado", "error");
      }
    },
    openPopup() {
      this.showPopup = true;
    },
    closePopup() {
      this.showPopup = false;
    },
    async saveEdit() {
      if (this.fieldToEdit === "title") {
        this.courseData.title = this.editContent;
      } else if (this.fieldToEdit === "description") {
        this.courseData.description = this.editContent;
      } else if (this.fieldToEdit === "abilities") {
        this.courseData.abilities = this.editContent;
      }
      const response = await axios.put(
        `http://localhost:9999/api/v1/courses/${this.courseId}`,
        { [this.fieldToEdit]: this.editContent }
      );
      console.log(response.data); // Verificar respuesta
      localStorage.setItem("courseData", JSON.stringify(this.courseData));
      Swal.fire("Éxito", "Cambios guardados con éxito.", "success");
      this.closeEditModal();
    },
    closeEditModal() {
      this.showEditModal = false;
    },
    formatDate(date) {
      const d = new Date(date);
      const options = { day: "2-digit", month: "2-digit", year: "numeric" };
      return d.toLocaleDateString("es-ES", options);
    },
    confirmUnsubscribe() {
      this.isModalVisible = true;
    },
    async unsubscribe() {
      try {
        console.log("id del enrollment" + this.enrollmentID);
        const response = await axios.delete(
          `http://localhost:9999/api/v1/enrollments/${this.enrollmentID}`,
          {
            headers: { Accept: "application/json" },
          }
        );
        console.log("respuesta al desinscribirse" + response.data.code);
        if (response.data.code == "200-OK") {
          new Swal(
            "Éxito",
            "Te desinscribiste con éxito del curso.",
            "success"
          );
          AuthService.sendUnsubscriptionEmail(
            this.userData.email,
            this.courseData.title
          );
          this.inscrito = false;
          this.closeModal();
        } else {
          console.error("Error al desinscribirse:", response.data.message);
        }
      } catch (error) {
        console.error("Error en la solicitud de desinscripción:", error);
      }
    },

    async fetchFavoriteCourse() {
      try {
        const response = await axios.get(
          `http://localhost:9999/api/v1/favorites/student/${this.userId}`,
          {
            headers: { Accept: "application/json" },
          }
        );
        if (response.status === 200) {
          const favoriteData = response.data.result;
          if (favoriteData && Array.isArray(favoriteData)) {
            let favoriteID = null;
            const favoriteFound = favoriteData.some((favorite) => {
              if (
                favorite.studentUserId === this.userId &&
                favorite.courseId == this.courseId
              ) {
                favoriteID = favorite.favoriteId;
                return true;
              }
              return false;
            });
            this.isFavorito = favoriteFound;
            this.favoriteID = favoriteID;
            console.log("favoriteID:", this.favoriteID);
          } else {
            console.error("Datos de inscripción no válidos:", favoriteData);
          }
        } else {
          console.error(
            "Error al obtener la inscripción:",
            response.data.message
          );
        }
      } catch (error) {
        console.error("Error en la solicitud de inscripción:", error);
      }
    },
    async toggleFavorito() {
      if (!this.isFavorito) {
        try {
        const response = await axios.post(
          "http://localhost:9999/api/v1/favorites/add",
          {
            courseId: this.courseId,
            studentUserId: this.userId,
          },
          {
            headers: {
              "Content-Type": "application/json",
              Accept: "application/json",
            },
          }
        );

        if (response.data.code === "200-OK") {
          Swal.fire("Éxito", "Añadio con exito a la lista de favoritos!!!", "success");
          this.isFavorito = true;
        } else {
          Swal.fire("Error", "No se pudo agregar a favoritos", "error");
        }
      } catch (error) {
        Swal.fire("Error", "Se presento el siguiente error." + error, "error" );
      }
      } else {
        // Mostrar SweetAlert de confirmación
        const result = await Swal.fire({
          title: "¿Estás seguro?",
          text: "¿Quieres quitarlo de favoritos?",
          icon: "warning",
          showCancelButton: true,
          confirmButtonColor: "#3085d6",
          cancelButtonColor: "#d33",
          confirmButtonText: "Sí, quitarlo",
          cancelButtonText: "No, cancelar",
        });

        if (result.isConfirmed) {
          this.isFavorito = false; // Quitar de favoritos
          Swal.fire("Eliminado", "Se ha quitado de tus favoritos.", "success");
        }
      }
    },

    closeModal() {
      this.isModalVisible = false;
    },
  },
  watch: {
    lessons: {
      handler() {
        this.totalLessons = this.lessons.length;
      },
      immediate: true,
    },
  },
  async mounted() {
    this.fetchCourseId();
    this.fetchCourseById();
    this.fetchUserData();
    if (this.isEstudiante) {
      await this.fetchEnrollmentId();
      await this.fetchFavoriteCourse();
    }
    this.selectedCourseId = this.courseData.id; // Asigna el ID del curso actual
  },
};
</script>

<template>
  <div class="row">
    <div class="rating">
      <p>Calificación del curso:</p>
      <span
        v-for="star in 5"
        :key="star"
        class="star"
        :class="{ filled: star <= this.courseData.rating }"
      >
        ★
      </span>
    </div>
    <div v-if="isEstudiante">
      <button class="favorite-button" @click="toggleFavorito">
        {{ isFavorito ? "Favorito ⭐" : "Guardar en favoritos" }}
      </button>
      
    </div>

    <div v-if="isDocente" class="opcionesDocentes">
      <div class="container py-2">
        <button class="btn btn-secondary mb-3" @click="showModal = true">
          Crear Nueva Lección
        </button>
        <CourseModal
          :show="showModal"
          :courseId="courseId"
          @close="showModal = false"
          @lessonCreated="fetchCourseById"
        />

        <button @click="irAQuizzes" class="btn btn-primary">
          Mis Cuestionarios
        </button>

        <button @click="showEditModal = true" class="btn btn-warning">
          Editar Curso
        </button>

        <EditCourseModal
          :show="showEditModal"
          :course="courseData"
          :courseId="courseId"
          @close="showEditModal = false"
          @courseUpdated="fetchCourseById"
        />
      </div>
    </div>

    <header>
      <div
        v-if="courseData"
        class="page-header min-vh-100 d-flex align-items-center justify-content-center"
        :style="{
          backgroundImage: `url(${courseData.image})`,
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          backgroundSize: 'cover',
        }"
      >
        <span class="mask bg-gradient-dark opacity-7"></span>
        <div class="container text-center text-white">
          <div class="row">
            <div class="col-lg-8 mx-auto">
              <h1 class="display-3 mb-4 font-weight-bold">
                {{ courseData.title }}
              </h1>
              <p class="lead mb-4">
                {{ courseData.description }}
              </p>
              <div v-if="isEstudiante">
                <div
                  v-if="isInscrito"
                  class="d-flex align-items-center justify-content-around"
                >
                  <MaterialButton color="success" class="mt-4"
                    >Suscrito</MaterialButton
                  >
                  <MaterialButton
                    color="primary"
                    class="mt-4"
                    @click="confirmUnsubscribe"
                    >Desinscribirse</MaterialButton
                  >
                </div>
                <div v-else>
                  <MaterialButton color="white" class="mt-4" @click="openPopup"
                    >Inscribirse</MaterialButton
                  >
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="isModalVisible" class="custom-modal">
        <div class="modal-content">
          <div class="modal-header">
            <h5>Confirmar Desinscripción</h5>
            <button class="close-button" @click="closeModal">×</button>
          </div>
          <div class="modal-body">
            ¿Estás seguro que deseas desinscribirte del curso
            <strong>{{ courseData.title }}</strong
            >?
          </div>
          <div class="modal-footer">
            <button class="cancel-button" @click="closeModal">Cancelar</button>
            <button class="confirm-button" @click="unsubscribe">
              Desinscribirme
            </button>
          </div>
        </div>
      </div>

      <section class="skills py-5">
        <div class="container">
          <h2 class="text-center mb-5">Habilidades que Obtendrás</h2>
          <ul class="list-unstyled d-flex justify-content-center flex-wrap">
            <li
              v-for="(ability, index) in abilitiesArray"
              :key="index"
              class="mb-4 px-4"
            >
              <i class="fas fa-check-circle mr-2"></i> {{ ability }}
            </li>
          </ul>
        </div>
      </section>

      <!-- Progress bar at the top right of the lessons part -->
      <div v-if="isEstudiante" class="progress-bar-wrapper">
        <span class="progress-text">Progress {{progress}} %</span>
        <ProgressBar :progress="progress" />

        
      </div>

      <div class="info-curso py-5">
        <div class="container">
          <h1 class="text-center mb-5">Listado de Lecciones</h1>
          <div class="lecciones-list d-flex justify-content-center flex-wrap">
            <div
              v-for="(lesson, index) in lessons"
              :key="index"
              class="leccion-card"
            >
              <img
                src="https://img.freepik.com/vector-premium/hombre-que-sienta-pila-libros_165488-4983.jpg"
                alt="Lección"
                class="leccion-image"
              />
              <div class="leccion-content p-3">
                <h2>{{ lesson.title }}</h2>
                <p><strong>Duración:</strong> {{ lesson.duration }}</p>
                <p>{{ lesson.description }}</p>

                <div v-if="isEstudiante">
                  <div v-if="isInscrito">
                    <button
                      @click="startLesson(lesson.lessonsId, courseData.title)"
                      class="btn-start"
                    >
                      Iniciar
                    </button>
                    <br />
                    <input
                      type="checkbox"
                      :checked="lesson.complete"
                      @change="toggleCompleted(lesson)"
                    />
                    Marcar como completada
                  </div>
                </div>
                <div v-if="isDocente">
                  <button
                    @click="startLesson(lesson.lessonsId, courseData.title)"
                    class="btn-start"
                  >
                    Iniciar
                  </button>
                  <p><strong>Orden actual:</strong> {{ lesson.order }}</p>
                  <label for="lesson-order-{{ index }}">Cambiar orden:</label>
                  <select
                    :id="'lesson-order-' + index"
                    v-model="lesson.order"
                    @change="updateLessonOrder(lesson)"
                  >
                    <option v-for="n in lessons.length" :key="n" :value="n">
                      {{ n }}
                    </option>
                  </select>
                  <button
                    @click="openDeletePopup(lesson)"
                    class="btn btn-danger mt-3 w-100"
                  >
                    Eliminar Lección
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div v-if="isEstudiante && this.getCertificate == true" class="certificado">
            <h2>Certificado de finalización de curso:</h2>
            <br />
            <h3>
              Usted concluyó el curso satisfactoriamente, puede descargar su
              certificado aquí:
            </h3>
            <button class="btn-ver-certificado" @click="verCertificado">
              Ver Certificado
            </button>
          </div>
        </div>
      </div>
      <!-- Popup de confirmación para eliminar lección -->
      <div
        v-if="showDeletePopup"
        class="popup-overlay d-flex align-items-center justify-content-center"
      >
        <div class="popup bg-white p-4 rounded shadow">
          <h2>Confirmar Eliminación</h2>
          <p>¿Estás seguro de que deseas eliminar esta lección?</p>
          <div class="d-flex justify-content-between mt-4">
            <MaterialButton @click="deleteLesson" color="danger"
              >Confirmar</MaterialButton
            >
            <MaterialButton @click="closeDeletePopup" color="secondary"
              >Cancelar</MaterialButton
            >
          </div>
        </div>
      </div>
    </header>

    <!-- Popup de confirmación -->
    <div v-if="showPopup" class="popup-overlay">
      <div class="popup">
        <h2>Confirmar Inscripción</h2>
        <p>¿Estás seguro de que deseas inscribirte en este curso?</p>
        <div class="popup-buttons">
          <MaterialButton color="white" @click="confirmInscription"
            >Confirmar</MaterialButton
          >
          <MaterialButton color="none" @click="closePopup"
            >Cancelar</MaterialButton
          >
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.rating {
  display: flex;
  align-items: center;
}
.rating p{
  margin: 0;
  font-size: larger;
  padding-right: 15px;
}

.star {
  font-size: 24px;
  color: gray;
  margin: 0;
  padding: 0;
}

.star.filled {
  color: orange;
}


.favorite-button {
  background-color: #4aa4d8;
  border: none;
  color: white;
  padding: 10px 20px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  margin: 4px 2px;
  cursor: pointer;
  border-radius: 12px;
  transition: background-color 0.3s ease;
}

.favorite-button:hover {
  background-color: #ff9900;
}

/* Estilos del formulario y modal */
.create-lesson-container {
  background-color: #fbebd5;
  padding: 2rem;
  border-radius: 12px;
}

.field-group {
  margin-bottom: 1rem;
}

.form-label {
  font-size: 1rem;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 1rem;
}

.modal-footer .btn-create {
  background-color: #28a745;
  color: white;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.btn-create:hover {
  background-color: #218838;
}
h1.display-3 {
  color: white; /* Cambia el color del título a blanco */
}
/* Fondo de la página principal */
.skills[data-v-607194c2] {
  background-color: #eee3c5;
  padding: 2rem 0;
}
.opcionesDocentes {
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: start;
}
.page-header {
  position: relative;
  background-size: cover;
  background-position: center;
  height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgb(170, 158, 133);
}

.mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
}

/* Sección de Habilidades */
.skills {
  background-color: #f3f3f3;
  padding: 2rem 0;
}

.skills ul {
  font-size: 1.2rem;
  list-style: none;
  padding: 0;
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}

.skills ul li {
  background-color: #28a745;
  color: white;
  padding: 1.5rem 2rem;
  margin: 10px 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, background-color 0.3s ease;
  min-width: 220px;
}

.skills ul li:hover {
  background-color: #218838;
  transform: translateY(-5px);
}

.skills ul li i {
  margin-right: 10px;
}

/* Listado de Lecciones */
.lecciones-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  background-color: #f0c0c7;
  padding: 2rem 0;
}

.leccion-card {
  width: 260px;
  margin: 20px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.leccion-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.2);
}

.leccion-image {
  width: 100%;
  border-radius: 12px 12px 0 0;
  height: 150px;
  object-fit: cover;
}

.leccion-content {
  padding: 1.5rem;
  background-color: #ffedda;
  flex-grow: 1; /* Make the content take the remaining space */
  display: flex;
  flex-direction: column;
  justify-content: space-between; /* Ensure the content is spaced evenly */
  border-radius: 0 0 8px 8px;
  height: 63%;
}

.leccion-content h2 {
  font-size: 1.3rem;
  color: #333;
  margin-bottom: 0.5rem;
}

.leccion-content p {
  font-size: 1rem;
  color: #555;
}

.btn-start {
  background-color: #ffc107;
  color: #333;
  padding: 0.7rem 1.5rem;
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

.btn-start:hover {
  background-color: #e0a800;
}

/* Sección del Instructor */

.bg-light {
  background-color: #c5d9f7 !important;
}
.instructor {
  padding: 2rem 0;
}

.instructor-info {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: left;
}

.instructor-photo {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  margin-right: 20px;
  border: 2px solid #ffc107;
}

.instructor-bio h3 {
  font-size: 1.5rem;
  color: #333;
}

.instructor-bio p {
  font-size: 1.1rem;
  color: #2b2626;
}

/* Popup overlay */
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup {
  background-color: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
  text-align: center;
  width: 300px;
}

.popup h2 {
  font-size: 1.4rem;
  margin-bottom: 1rem;
  color: #333;
}

.popup-buttons {
  display: flex;
  justify-content: space-around;
  margin-top: 1.5rem;
}

.popup-buttons button {
  padding: 0.5rem 1.2rem;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.popup-buttons button:first-child {
  background-color: #28a745;
  color: white;
}

.popup-buttons button:first-child:hover {
  background-color: #218838;
}

.popup-buttons button:last-child {
  background-color: #ffc107;
  color: #333;
}

.popup-buttons button:last-child:hover {
  background-color: #e0a800;
}

/* Estilos del popup */
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  text-align: center;
}

.popup h2 {
  margin-bottom: 20px;
}

.popup-buttons {
  display: flex;
  justify-content: space-around;
}

/* Diseño de la lista de lecciones */
.leccion-card {
  background-color: #f8f9fa;
  border: 1px solid #e2e6ea;
  border-radius: 8px;
  width: 300px;
  margin: 15px;
  text-align: center;
}

.leccion-image {
  width: 100%;
  border-bottom: 1px solid #e2e6ea;
}

.leccion-content {
  padding: 15px;
}

.btn-danger {
  background-color: #da4733;
  color: #333;
  padding: 0.7rem 1.5rem;
  border-radius: 8px;
  transition: background-color 0.3s ease;
  margin: 0ch;
}

.btn-danger:hover {
  background-color: #c82333;
}

.progress-bar-wrapper {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  align-items: center;
  width: 250px;
}

.progress-text {
  margin-right: 10px;
  font-size: 16px;
  color: #333;
}

.drag-handle {
  cursor: grab;
  background-color: #ddd;
  border: none;
  padding: 5px;
  border-radius: 5px;
}

.btn-ver-certificado {
  background-color: #4990d3; /* Verde */
  color: white;
  padding: 0.7rem 1.5rem;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.3s ease;
}

.btn-ver-certificado:hover {
  background-color: #45a049; /* Verde más oscuro */
  transform: translateY(-2px);
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

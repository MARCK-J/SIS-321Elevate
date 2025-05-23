<template>
  <div class="modal" tabindex="-1" role="dialog" v-if="show">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Crear Cuestionario</h5>
          <button
            type="button"
            class="close"
            @click="closeModal"
            aria-label="Close"
          >
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="createQuiz">
            <div class="form-group">
              <label for="quizTitle">Título</label>
              <input
                type="text"
                class="form-control "
                id="quizTitle"
                v-model="quizTitle"
                required
              />
            </div>
            <div class="form-group">
              <label for="quizDescription">Descripción</label>
              <textarea
                class="form-control border border-dark rounded"
                id="quizDescription"
                v-model="quizDescription"
                required
              ></textarea>
            </div>
            <div class="form-group">
              <label for="quizDueDate">Fecha Límite</label>
              <input
                type="date"
                class="form-control"
                id="quizDueDate"
                v-model="quizDueDate"
                required
              />
            </div>
            <div class="form-group">
              <label for="lessonSelect" class="form-label">Lección</label>
              <select
                id="lessonSelect"
                v-model="selectedLessonId"
                class="form-control border border-dark rounded"
                required
              >
                <option value="" disabled>Selecciona una lección</option>
                <option
                  v-for="lesson in lessons"
                  :key="lesson.lessonsId"
                  :value="lesson.lessonsId"
                >
                  {{ lesson.title }}
                </option>
              </select>
            </div>

            <button type="submit" class="btn btn-primary">
              Crear Cuestionario
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Swal from "sweetalert2";

export default {
  props: {
    show: {
      type: Boolean,
      required: true,
    },
    courseId: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      quizTitle: "",
      quizDescription: "",
      quizDueDate: "",
      selectedLessonId: null,
      selectedLessonTitle: "",
      lessons: [],
    };
  },
  methods: {
    async fetchLessons() {
      try {
        const response = await axios.get(
          `http://localhost:9999/api/v1/lessons/course/${this.courseId}`,
          {
            headers: {
              Accept: "application/json",
            },
          }
        );
        this.lessons = response.data.result;
      } catch (error) {
        console.error("Error fetching lessons:", error);
      }
    },
    selectLesson(lesson) {
      this.selectedLessonId = lesson.lessonsId;
      this.selectedLessonTitle = lesson.title;
    },
    async createQuiz() {
      try {
        await axios.post(
          "http://localhost:9999/api/v1/quizzes",
          {
            title: this.quizTitle,
            description: this.quizDescription,
            dueDate: this.quizDueDate,
            lessonsId: this.selectedLessonId,
            courseId: this.courseId,
          },
          {
            headers: {
              "Content-Type": "application/json",
              Accept: "application/json",
            },
          }
        );
        this.$emit("quizCreated");
        Swal.fire({
          title: "Éxito",
          text: "Creación de cuestionario exitosa",
          icon: "success",
        });
        this.resetForm();
        this.closeModal();
      } catch (error) {
        console.error("Error creating quiz:", error);
        Swal.fire({
          title: "Error",
          text: "No se pudo crear el cuestionario. Intente nuevamente.",
          icon: "error",
        });
      }
    },
    resetForm() {
      this.quizTitle = "";
      this.quizDescription = "";
      this.quizDueDate = "";
      this.selectedLessonId = null;
      this.selectedLessonTitle = "";
    },
    closeModal() {
      this.$emit("close");
    },
  },
  async mounted() {
    await this.fetchLessons();
  },
};
</script>

<style scoped>
.modal {
  display: block;
  background: rgba(0, 0, 0, 0.5);
}

.dropdown-menu {
  max-height: 200px;
  overflow-y: auto;
}
</style>

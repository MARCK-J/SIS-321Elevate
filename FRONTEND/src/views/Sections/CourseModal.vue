<template>
  <div class="modal" tabindex="-1" role="dialog" v-if="show">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Crear Nueva Lección</h5>
          <button type="button" class="close" @click="closeModal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="create-lesson-container"></div>
          <form @submit.prevent="createLesson">
            <div class="form-container">
              <div class="second-part">
                <!-- Título de la Lección -->
                <div class="field-group">
                  <label for="lesson-title" class="form-label">Título de la Lección</label>
                  <input id="lesson-title" type="text" class="form-control" v-model="titleLesson" placeholder="Título de la lección" required />
                </div>

                <!-- Descripción -->
                <div class="field-group">
                  <label for="lesson-description" class="form-label">Descripción</label>
                  <textarea id="lesson-description" class="form-control" v-model="descriptionLesson" placeholder="Descripción de la lección" rows="4" required></textarea>
                </div>

                <!-- Duración -->
                <div class="field-group">
                  <label for="lesson-duration" class="form-label">Duración</label>
                  <input id="lesson-duration" type="text" class="form-control" v-model="durationLesson" placeholder="Ej. 1h 30m" required />
                </div>

                <!-- Contenido -->
                <div class="field-group">
                  <label for="lesson-content" class="form-label">Contenido</label>
                  <textarea id="lesson-content" class="form-control" v-model="contentLesson" placeholder="Contenido de la lección" rows="4" required></textarea>
                </div>

                <!-- Enlace de Video -->
                <div class="field-group">
                  <label for="lesson-video" class="form-label">Enlace del Video</label>
                  <input id="lesson-video" type="url" class="form-control" v-model="videoLesson" placeholder="URL del video (opcional)" />
                </div>

                <!-- Orden -->
                <div class="field-group">
                  <label for="lesson-order" class="form-label">Orden</label>
                  <input id="lesson-order" type="number" class="form-control" v-model="orderLesson" placeholder="Orden de la lección" required />
                </div>
              </div>
            </div>

            <!-- Botón para crear lección -->
            <div class="modal-footer justify-content-between">
              <MaterialButton variant="gradient" color="dark" data-bs-dismiss="modal" @click="closeModal">Cerrar</MaterialButton>
              <MaterialButton type="submit" variant="gradient" color="success" class="mb-0">Crear Lección</MaterialButton>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Swal from "sweetalert2";
import MaterialButton from "@/components/MaterialButton.vue";

export default {
  components: {
    MaterialButton,
  },
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
      titleLesson: "",
      descriptionLesson: "",
      durationLesson: "",
      contentLesson: "",
      videoLesson: "",
      orderLesson: 0,
    };
  },
  methods: {
    async createLesson() {
      try {
        // Datos de la nueva lección
        const newLesson = {
          title: this.titleLesson,
          description: this.descriptionLesson,
          duration: this.durationLesson,
          content: this.contentLesson,
          video: this.videoLesson || '', // El video es opcional
          order: this.orderLesson,
          courseId: this.courseId, // Vincula la lección con el curso
        };

        // Realizar la solicitud POST para crear la lección
        const response = await axios.post(
          "http://localhost:9999/api/v1/lessons/create",
          newLesson,
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        if (response.data.code === "200-OK") {
          Swal.fire("Éxito", "La lección ha sido creada exitosamente.", "success");
          this.$emit("lessonCreated"); // Emitir evento para actualizar la lista de lecciones
          this.resetForm();
          this.closeModal();
        } else {
          console.error("Error al crear la lección:", response.data.message);
          Swal.fire("Error", "No se pudo crear la lección.", "error");
        }
      } catch (error) {
        console.error("Error en la solicitud de creación de lección:", error);
        Swal.fire("Error", "Hubo un problema al crear la lección.", "error");
      }
    },
    resetForm() {
      this.titleLesson = "";
      this.descriptionLesson = "";
      this.durationLesson = "";
      this.contentLesson = "";
      this.videoLesson = "";
      this.orderLesson = 0;
    },
    closeModal() {
      this.$emit("close");
    },
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

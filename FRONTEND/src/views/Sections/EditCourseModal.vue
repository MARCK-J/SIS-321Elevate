<template>
  <div v-if="show" class="modal">
    <div class="modal-content">
      <span class="close" @click="$emit('close')">&times;</span>
      <h2>Editar Curso</h2>
      <form @submit.prevent="updateCourse">
        <div class="form-container">
          <div class="first-part">
            <div class="field-group">
              <label for="profile-image-url" class="form-label">Imagen de Perfil (URL)</label>
              <input id="profile-image-url" type="url" class="form-control" v-model="course.image" placeholder="https://example.com/imagen.jpg" required />
            </div>
            <div class="field-group">
              <label for="course-title" class="form-label">Título del Curso</label>
              <input id="course-title" type="text" class="form-control" v-model="course.title" placeholder="Introduzca el título del curso" required />
            </div>
            <div class="field-group">
              <label for="course-description" class="form-label">Descripción</label>
              <textarea id="course-description" class="form-control" v-model="course.description" placeholder="Introduzca la descripción del curso" rows="4" required></textarea>
            </div>
            <div class="field-group">
              <label for="course-duration" class="form-label">Duración (horas)</label>
              <input id="course-duration" type="number" v-model="course.duration" class="form-control" placeholder="Duración en minutos" required min="1" step="1" />
            </div>
          </div>
          <div class="second-part">
            <div class="field-group">
              <label class="form-label">Disponibilidad</label>
              <div class="availability-options">
                <label class="radio-option">
                  <input type="radio" v-model="course.available" :value="true" required /> Disponible
                </label>
                <label class="radio-option">
                  <input type="radio" v-model="course.available" :value="false" required /> No disponible
                </label>
              </div>
            </div>
            <div class="field-group">
              <label for="course-category" class="form-label">Categoría</label>
              <select id="course-category" v-model="course.categoryCourseId" class="form-control" required>
                <option value="" disabled>Selecciona una categoría</option>
                <option v-for="category in categories" :key="category.id" :value="category.id">{{ category.nameCategory }}</option>
              </select>
            </div>
            <div class="field-group">
              <label class="form-label">Habilidades</label>
              <div v-for="(skill, index) in course.abilitiesArray" :key="index" class="skill-input">
                <div class="input-with-remove">
                  <input type="text" v-model="course.abilitiesArray[index]" class="form-control skill-input-field" placeholder="Habilidad" required />
                  <button type="button" @click="removeSkill(index)" class="btn-remove-skill">X</button>
                </div>
              </div>
              <button type="button" @click="addSkill" class="btn-add-skill">Agregar Habilidad</button>
            </div>
            <div class="field-group">
              <label class="form-label">Puntuación</label>
              <div class="rating-display">
                ⭐
                <span>({{ course.rating }} estrellas)</span>
              </div>
            </div>
          </div>
        </div>
        <div class="action-buttons">
          <button type="submit" class="btn-create">Actualizar Curso</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Swal from "sweetalert2";

export default {
  props: {
    show: Boolean,
    course: Object,
    courseId: Number,
  },
  data() {
    return {
      categories: [],
    };
  },
  methods: {
    async updateCourse() {
      try {
        const abilities = this.course.abilitiesArray.join(";");
        const payload = {
          ...this.course,
          abilities,
        };
        await axios.put(`http://localhost:9999/api/v1/courses/${this.courseId}`, payload);
        Swal.fire({
          title: "Curso Actualizado",
          text: "El curso ha sido actualizado exitosamente.",
          icon: "success",
          confirmButtonText: "Aceptar",
        });
        this.$emit('courseUpdated');
        this.$emit('close');
      } catch (error) {
        console.error("Error al actualizar el curso:", error);
        Swal.fire({
          title: "Error",
          text: "No se pudo actualizar el curso. Intente nuevamente.",
          icon: "error",
          confirmButtonText: "Aceptar",
        });
      }
    },
    async fetchCategories() {
      try {
        const response = await axios.get("http://localhost:9999/api/v1/category/all");
        this.categories = response.data.result;
      } catch (error) {
        console.error("Error al obtener las categorías:", error);
      }
    },
    addSkill() {
      this.course.abilitiesArray.push("");
    },
    removeSkill(index) {
      this.course.abilitiesArray.splice(index, 1);
    },
  },
  watch: {
    course: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.course.abilitiesArray = newVal.abilities.split(";");
        }
      },
    },
  },
  mounted() {
    this.fetchCategories();
  },
};
</script>

<style scoped>
/* Estilo general del formulario */
.modal {
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 12px;
  width: 900px;
  max-width: 100%;
  max-height: 80vh; /* Set a maximum height */
  overflow-y: auto; /* Enable vertical scrolling */
  position: relative; /* Ensure the close button is positioned correctly */
}

.close {
  position: absolute;
  top: 10px;
  right: 10px;
  cursor: pointer;
}

.form-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  align-items: center;
  flex-direction: row;
}

.Habilidades {
  display: flex;
  flex-direction: column;
  overflow-y: scroll;
}

.form-label {
  font-size: 1rem;
  font-weight: 500;
  color: #555;
  margin-bottom: 0.5rem;
  display: block;
}

.form-control {
  width: 50vh;
  padding: 0.75rem 1rem;
  border: 1px solid #151010;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s ease-in-out;
  background-color: #ffffff;
}

.form-control:focus {
  border-color: #5b5f97;
  outline: none;
}

.availability-options {
  display: flex;
  gap: 1.5rem;
}

.radio-option {
  font-size: 1rem;
  color: #333;
}

.radio-option input {
  margin-right: 0.5rem;
}

.input-with-remove {
  position: relative;
}

.btn-remove-skill {
  position: absolute;
  right: 5px;
  top: 50%;
  transform: translateY(-50%);
  background-color: transparent;
  color: rgb(0, 0, 0);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  padding: 0.3rem 0.6rem;
  font-size: 0.6rem;
  transition: background-color 0.3s ease;
}

.btn-remove-skill:hover {
  background-color: #c82333;
}

.btn-add-skill {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 0.5rem;
}

.btn-add-skill:hover {
  background-color: #0056b3;
}

.btn-create {
  background-color: #28a745;
  color: white;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1.2rem;
  font-weight: 600;
  text-align: center;
  transition: background-color 0.3s ease;
  width: 100%;
}

.btn-create:hover {
  background-color: #218838;
}
</style>
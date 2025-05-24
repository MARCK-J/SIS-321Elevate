<template>
  <div v-if="!isAdminPage">
    <div class="create-course-container">
      <div class="header-container">
        <h2 class="title">Crear Nuevo Curso</h2>
      </div>
      <br />
      <form @submit.prevent="createCourse">
        <div class="form-container">
          <div class="first-part">
            <!-- Imagen de perfil (por URL) -->
            <div class="field-group">
              <label for="profile-image-url" class="form-label"
                >Imagen de Perfil (URL)</label
              >
              <input
                id="profile-image-url"
                type="url"
                class="form-control"
                v-model="course.profileImage"
                placeholder="https://example.com/imagen.jpg"
                required
              />
            </div>

            <!-- Título del curso -->
            <div class="field-group">
              <label for="course-title" class="form-label"
                >Título del Curso</label
              >
              <input
                id="course-title"
                type="text"
                class="form-control"
                v-model="course.title"
                placeholder="Introduzca el título del curso"
                required
              />
            </div>

            <!-- Descripción -->
            <div class="field-group">
              <label for="course-description" class="form-label"
                >Descripción</label
              >
              <textarea
                id="course-description"
                class="form-control"
                v-model="course.description"
                placeholder="Introduzca la descripción del curso"
                rows="4"
                required
              ></textarea>
            </div>
            <!-- Duración -->
            <div class="field-group">
              <label for="course-duration" class="form-label"
                >Duración (horas)</label
              >
              <input
                id="course-duration"
                type="number"
                v-model="course.duration"
                class="form-control"
                placeholder="Duración en minutos"
                required
                min="1"
                step="1"
              />
            </div>
          </div>

          <div class="second-part">
            <!-- Disponibilidad -->
            <div class="field-group">
              <label class="form-label">Disponibilidad</label>
              <div class="availability-options">
                <label class="radio-option">
                  <input
                    type="radio"
                    v-model="course.availability"
                    value="true"
                    required
                  />
                  Disponible
                </label>
                <label class="radio-option">
                  <input
                    type="radio"
                    v-model="course.availability"
                    value="false"
                    required
                  />
                  No disponible
                </label>
              </div>
            </div>

            <!-- Categorías -->
            <div class="field-group">
              <label for="course-category" class="form-label">Categoría</label>
              <select
                id="course-category"
                v-model="course.category"
                class="form-control"
                required
              >
                <option value="" disabled>Selecciona una categoría</option>
                <option
                  v-for="category in categories"
                  :key="category.id"
                  :value="category.id"
                >
                  {{ category.nameCategory }}
                </option>
              </select>
            </div>

            <!-- Habilidades -->
            <div class="field-group">
              <label class="form-label">Habilidades</label>
              <div
                v-for="(skill, index) in course.skills"
                :key="index"
                class="skill-input"
              >
                <div class="input-with-remove">
                  <input
                    type="text"
                    v-model="course.skills[index]"
                    class="form-control skill-input-field"
                    placeholder="Habilidad"
                    required
                  />
                  <button
                    type="button"
                    @click="removeSkill(index)"
                    class="btn-remove-skill"
                  >
                    X
                  </button>
                </div>
              </div>
              <button type="button" @click="addSkill" class="btn-add-skill">
                Agregar Habilidad
              </button>
            </div>

            <!-- Puntuación (1 estrella por defecto) -->
            <div class="field-group">
              <label class="form-label">Puntuación</label>
              <div class="rating-display">
                ⭐
                <span>(1 estrella)</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Botón de crear curso -->
        <div class="action-buttons">
          <button type="submit" class="btn-create">Crear Curso</button>
        </div>
      </form>
    </div>
  </div>
  <div v-else>
    <div class="create-course-container text-center py-5">
      <h2 class="text-danger">Esta función no está disponible para el Administrador de Página.</h2>
      <p>El Administrador de Página no puede crear cursos.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import Swal from "sweetalert2";
import { useAppStore } from "@/stores";

// Define la URL base de la API usando la variable de entorno
// Esto se resolverá a "http://localhost:9999" en desarrollo
// y a "https://tu-backend-render-url.onrender.com" en producción
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL;

const appStore = useAppStore();
const isAdminPage = computed(() => appStore.getTipoPersona === 3);

// Datos del curso
const initialCourseState = {
  title: "",
  profileImage: "",
  description: "",
  availability: true,
  category: "",
  skills: [""],
  rating: 1, // Rating por defecto
  duration: 0, // Duración por defecto
};

const course = ref({ ...initialCourseState }); // Clonamos el estado inicial

// Lista de categorías
const categories = ref([]);

// Obtener el userId directamente desde el store usando computed
const userId = computed(() => appStore.getIdentificador);

// Función para obtener las categorías de la API
const fetchCategories = async () => {
  try {
    const response = await axios.get(
      `${API_BASE_URL}/api/v1/category/all`
    );
    categories.value = response.data.result;
  } catch (error) {
    console.error("Error al obtener las categorías:", error);
    Swal.fire({
      title: "Error",
      text: "No se pudieron cargar las categorías. Intente nuevamente.",
      icon: "error",
      confirmButtonText: "Aceptar",
    });
  }
};

// Ejecutar fetchCategories cuando el componente se monte
onMounted(fetchCategories);

// Función para crear el curso
const createCourse = async () => {
  const abilities = course.value.skills.join(";");
  const payload = {
    title: course.value.title,
    image: course.value.profileImage,
    description: course.value.description,
    abilities: abilities,
    avaibalable: course.value.availability,
    categoryCourseId: course.value.category, // Asume que tienes el ID de la categoría
    teacherUserId: userId.value, // Asume que tienes el ID del profesor
    rating: course.value.rating, // Incluimos la puntuación fija de 1
    duration: course.value.duration, // Incluimos la duración
  };

  try {
    const response = await axios.post(
      `${API_BASE_URL}/api/v1/courses/create`,
      payload,
      {
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
      }
    );
    Swal.fire({
      title: "Creación de curso",
      text: "Curso creado con éxito.",
      icon: "success",
      confirmButtonText: "Aceptar",
    });
    // Restablecer el formulario a su estado inicial
    course.value = { ...initialCourseState };

    console.log("Curso creado exitosamente:", response.data);
  } catch (error) {
    Swal.fire({
      title: "Error",
      text: "No se pudo crear el curso. Intente nuevamente.",
      icon: "error",
      confirmButtonText: "Aceptar",
    });
    console.error("Error al crear el curso:", error);
  }
};

// Función para añadir más habilidades
const addSkill = () => {
  course.value.skills.push("");
};

// Función para eliminar habilidades
const removeSkill = (index) => {
  course.value.skills.splice(index, 1);
};
</script>

<style scoped>
/* ...estilos igual que antes... */
.create-course-container {
  background-color: #fbebd5;
  padding: 1rem;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  max-width: 900px;
  margin: 20px auto;
  transition: transform 0.3s ease-in-out;
}

.create-course-container:hover {
  transform: translateY(-5px);
}

.header-container {
  background-color: #599ce4; /* Fondo azul */
  padding: 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: 12px 12px 12px 12px;
}

.title {
  color: white;
  font-size: 2rem;
  font-weight: 600;
  margin: 0;
}

.form-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  align-items: center;
  flex-direction: row;
}

.field-group {
  margin-bottom: 1rem;
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

/* Estilo de los botones */
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

/* Responsivo */
@media (max-width: 768px) {
  .create-course-container {
    padding: 2rem;
  }

  .btn-create {
    font-size: 1rem;
  }
}
</style>
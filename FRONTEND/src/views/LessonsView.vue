<template>
   <BaseLayout
    :title="lessonData?.title || 'Título de la lección'"
    :breadcrumb="[ 
      { label: 'cursos', route: '/' },
      { label: courseTitle, route: '/' },
      { label: lessonData?.title || 'Título de la lección' },
    ]"
  >
    <div class="container">
      <main class="main-content">
        <div class="video-container" v-if="lessonData && lessonData.video">
          <iframe
            :src="getEmbedUrl(lessonData.video)"
            frameborder="0"
            allowfullscreen
          ></iframe>
        </div>
        <div v-else>
          <p>No hay video disponible.</p>
        </div>

        <aside class="sidebar">
          <div class="download-section">
            <h3>Archivos Descargables</h3>
            <ul>
              <li>
                <a href="#" class="btn-download-exercise"
                  >Ejercicios de práctica</a
                >
              </li>
              <li>
                <a href="#" class="btn-download-resource"
                  >PDF - Teoría de la lección</a
                >
              </li>
            </ul>
          </div>
          <div class="evaluation-section">
            <h3>Evaluaciones de la lección</h3>
      <!-- Espacio para los quizzes -->
      <div class="quizzes-section">
        <div>
          <div v-for="quiz in quizzes" :key="quiz.quizId" class="quiz-button">
            <button @click="goToQuiz(quiz.quizId)" class="btn btn-evaluate">{{ quiz.title }}</button>
          </div>
        </div>
      </div>
          </div>
        </aside>
      </main>

      <!-- Información de la lección editable -->
      <div class="lesson-info" v-if="lessonData">
        <div v-if="!isEditing">
          <h2>{{ lessonData.title }}</h2>
          <p>Duración: {{ lessonData.duration }}</p>
          <h3>{{ lessonData.description }}</h3>
          <p>{{ lessonData.content }}</p>
          <ul>
            <li>Orden de la lección: {{ lessonData.order }}</li>
            <li v-if="lessonData.complete">Lección completa</li>
            <li v-else>Lección no completada</li>
          </ul>
            <button v-if="canEditContent()" @click="enableEditing" class="btn-edit-content">
            Editar contenido
            </button>
        </div>

        <!-- Formulario para editar los datos de la lección -->
        <div v-else class="edit-form">
          <label for="title">Título de la lección</label>
          <input id="title" v-model="editableLessonData.title" type="text" class="input-field" />

          <label for="duration">Duración</label>
          <input id="duration" v-model="editableLessonData.duration" type="text" class="input-field" />

          <label for="description">Descripción</label>
          <textarea id="description" v-model="editableLessonData.description" class="textarea-field"></textarea>

          <label for="content">Contenido</label>
          <textarea id="content" v-model="editableLessonData.content" class="textarea-field"></textarea>

          <div class="button-group">
            <button @click="saveLesson" class="btn-save-content">
              Guardar cambios
            </button>
            <button @click="cancelEditing" class="btn-cancel-edit">
              Cancelar
            </button>
          </div>
        </div>
      </div>

      <!-- Popup para crear cuestionario -->
      <div v-if="showQuizPopup" class="popup-overlay">
        <div class="popup-content">
          <button @click="showQuizPopup = false" class="btn-close-popup">X</button>
          <CuestionarioLeccion :lessonId="lessonId" />
        </div>
      </div>
    </div>
  </BaseLayout>
</template>

<script>
import { useRoute, useRouter } from "vue-router";
import { computed, ref, onMounted } from "vue";
import BaseLayout from "../layouts/sections/components/BaseLayout.vue";
import axios from "axios";
import { useAppStore } from "@/stores";

export default {
  components: {
    BaseLayout,
  },
  setup() {
    // Acceder a la ruta actual
    const route = useRoute();
    const router = useRouter();

    // Extraer `courseId` y `courseTitle` de la query
    const courseId = computed(() => route.query.courseId || 0);
    const courseTitle = computed(() => route.query.courseTitle || "");
    const lessonId = computed(() => route.query.lessonId || "");

    const lessonData = ref(null); // Para almacenar los datos de la lección
    const isEditing = ref(false); // Estado de edición
    const editableLessonData = ref({}); // Datos editables de la lección
    const quizzes = ref([]); // Para almacenar los quizzes de la lección

    // Función para obtener la lección por `lessonId`
    const fetchLessonById = async (lessonId) => {
      try {
        const response = await axios.get(
          `http://localhost:9999/api/v1/lessons/${lessonId.value}`, // Usar el lessonId para la consulta
          {
            headers: {
              Accept: "application/json",
            },
          }
        );

        if (response.data.code === "200-OK") {
          lessonData.value = response.data.result; // Guardar los datos de la lección
          editableLessonData.value = { ...lessonData.value }; // Copiar datos para edición
        } else {
          console.error("Error al obtener la lección:", response.data.message);
        }
      } catch (error) {
        console.error("Error al obtener la lección:", error);
      }
    };

    // Función para obtener los quizzes por `lessonId`
    const fetchQuizzesByLessonId = async (lessonId) => {
      try {
        const response = await axios.get(
          `http://localhost:9999/api/v1/quizzes/lesson/${lessonId.value}`
        );
        quizzes.value = response.data.result;
      } catch (error) {
        console.error("Error al obtener los quizzes:", error);
      }
    };

    // Llamar a la función en el montaje del componente
    onMounted(() => {
      if (lessonId.value) {
        fetchLessonById(lessonId); // Llamar a la función para obtener la lección
        canEditContent(); // Verificar si el usuario puede editar el contenido
        fetchQuizzesByLessonId(lessonId); // Llamar a la función para obtener los quizzes
      }
    });

    const goBack = () => {
      window.history.back(); // Alternativa a this.$router.go(-1)
    };

    // Función para convertir la URL del video a una URL embebida
    const getEmbedUrl = (url) => {
      let videoId = null;
      if (url.includes('youtube.com')) {
        videoId = url.split('v=')[1]?.split('&')[0];
      } else if (url.includes('youtu.be')) {
        videoId = url.split('youtu.be/')[1]?.split('?')[0];
      }
      return videoId ? `https://www.youtube.com/embed/${videoId}` : url;
    };

    // Habilitar edición
    const enableEditing = () => {
      isEditing.value = true;
    };

    // Cancelar edición
    const cancelEditing = () => {
      isEditing.value = false;
      editableLessonData.value = { ...lessonData.value }; // Restaurar datos originales
    };

    // Guardar los cambios en la lección
    const saveLesson = async () => {
      try {
        const response = await axios.put(
          `http://localhost:9999/api/v1/lessons/${lessonId.value}`,
          editableLessonData.value,
          {
            headers: {
              Accept: "application/json",
              "Content-Type": "application/json",
            },
          }
        );

        if (response.data.code === "200-OK") {
          lessonData.value = { ...editableLessonData.value }; // Actualizar datos
          isEditing.value = false; // Salir del modo de edición
        } else {
          console.error("Error al actualizar la lección:", response.data.message);
        }
      } catch (error) {
        console.error("Error al actualizar la lección:", error);
      }
    };

    // Función para verificar si el usuario puede editar el contenido
    const canEditContent = () => {
      const store = useAppStore(); // Acceder al store global
      const role = store.getTipoPersona; // Obtener el rol del usuario
      return role == 2; // Permitir edición si es docente
    };

    // Función para navegar a la vista de quizzes
    const evaluateLesson = () => {
  router.push({
    name: 'Quizzes',  // Asegúrate de que esta ruta esté registrada en tu archivo de rutas (e.g., 'Quizzes')
    query: {
      courseId: courseId.value,
      courseTitle: courseTitle.value,
      lessonId: lessonId.value,
    },
  });
};

    // Función para redirigir a la pantalla de resolución del quiz
    const goToQuiz = (quizId) => {
      router.push({ path: `/resolve-quiz`, query: { quizId } });
    };

    return {
      courseId,
      canEditContent,
      courseTitle,
      lessonData, // Datos de la lección
      editableLessonData, // Datos editables
      isEditing, // Estado de edición
      enableEditing, // Habilitar edición
      cancelEditing, // Cancelar edición
      saveLesson, // Guardar cambios
      goBack,
      getEmbedUrl,
      evaluateLesson, // Nueva función para ir a la vista de quizzes
      quizzes, // Datos de los quizzes
      goToQuiz, // Función para ir a la pantalla de resolución del quiz
    };
  },
};
</script>


<style scoped>
/* Estilos globales */
body {
  font-family: "Poppins", sans-serif;
  background-color: #f8f8d9;
  margin: 0;
  padding: 0;
}

.container[data-v-3350a6d5] {
    display: flex;
    flex-direction: column;
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
    background-color: rgb(107, 149, 226);
}

/* Contenedor principal */
.container {
  display: flex;
  flex-direction: column;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* Estilo del encabezado */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: #f8f8d9;
  padding: 10px 20px;
  color: #fff;
  border-radius: 8px;
}

.btn-back {
  margin-right: auto; /* Empuja el botón hacia la izquierda */
  background-color: #f76c6c;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-back:hover {
  background-color: #f44747;
}


/* Estilo del contenido principal */
.main-content {
  display: flex;
  align-items: stretch;
  flex-wrap: wrap;
  gap: 20px;
}

/* Video */
.video-container {
  flex: 2;
}

.video-container iframe {
  width: 100%;
  height: 400px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}



/* Formulario de edición */
.edit-form {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

/* Estilos de campos de texto y textarea */
.input-field, .textarea-field {
  width: 100%;
  padding: 12px;
  margin-bottom: 15px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.input-field:focus, .textarea-field:focus {
  border-color: #1e90ff;
  box-shadow: 0 2px 8px rgba(30, 144, 255, 0.2);
  outline: none;
}

.textarea-field {
  min-height: 100px;
  resize: vertical;
}

/* Grupo de botones */
.button-group {
  display: flex;
  gap: 15px;
}

/* Botón de edición con estilo mejorado */
.btn-edit-content {
  background-color: #007bff; /* Azul vibrante */
  color: white;
  padding: 12px 24px; /* Aumentar el padding para un botón más grande */
  border: none;
  border-radius: 50px; /* Bordes redondeados para un look moderno */
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.3); /* Sombra ligera */
  transition: background-color 0.3s ease, box-shadow 0.3s ease, transform 0.2s ease;
}

.btn-edit-content:hover {
  background-color: #0056b3; /* Azul más oscuro al hacer hover */
  box-shadow: 0 6px 12px rgba(0, 123, 255, 0.4); /* Sombra más profunda al hacer hover */
  transform: translateY(-2px); /* Efecto de levantamiento al hacer hover */
}

.btn-edit-content:active {
  background-color: #004080; /* Azul más oscuro al hacer clic */
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.2); /* Sombra reducida al hacer clic */
  transform: translateY(0); /* Restaurar el botón al hacer clic */
}


/* Botón de guardar cambios */
.btn-save-content {
  background-color: #28a745;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

.btn-save-content:hover {
  background-color: #218838;
  box-shadow: 0 4px 8px rgba(40, 167, 69, 0.3);
}

/* Botón de cancelar */
.btn-cancel-edit {
  background-color: #dc3545;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

.btn-cancel-edit:hover {
  background-color: #c82333;
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

/* Sección de descarga y evaluación */
.download-section,
.evaluation-section {
  background-color: #eed481;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.btn-download-exercise {
  display: inline-block;
  background-color: #1231e0; /* Color de fondo azul */
  color: white;
  padding: 10px 15px;
  border-radius: 5px;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.btn-download-resource {
  display: inline-block;
  background-color: #f50e21; /* Color de fondo azul */
  color: white;
  padding: 10px 15px;
  border-radius: 5px;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.btn-download:hover {
  background-color: #0056b3; /* Color al pasar el mouse */
}

.evaluation-section {
  background-color: #9cf0bc; /* Color de fondo verde claro */
}

.btn-evaluate {
  background-color: #086b1d;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  display: block;
  width: 100%;
  text-align: center;
  margin-top: 10px; /* Espacio adicional en la parte superior */
}

.btn-evaluate:hover {
  background-color: #1f1e38;
}

/* Información de la lección */
.lesson-info {
  flex: 1 0 100%; /* Full width */
  background-color: #eed481;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-top: 20px; /* Espacio adicional en la parte superior */
}

.lesson-info h2 {
  font-size: 22px;
  color: #2b2e4a;
  margin-bottom: 10px;
}

.lesson-info p {
  color: #4a4a4a;
  line-height: 1.6;
}

.lesson-info ul {
  list-style-type: none;
  padding-left: 0;
}

.lesson-info ul li {
  margin-bottom: 10px;
  position: relative;
}

.lesson-info ul li::before {
  content: "•";
  color: #f76c6c;
  font-weight: bold;
  position: absolute;
  left: -20px;
}

/* Barra lateral */
.sidebar {
  flex: 1;
  background-color: #b9baf8;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.sidebar h3 {
  font-size: 18px;
  color: #2b2e4a;
  margin-bottom: 10px;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
}

.sidebar ul li {
  margin-bottom: 15px;
}

/* Sección de quizzes */
.quizzes-section {
  margin-top: 20px;
}

.quiz-button {
  margin-bottom: 10px;
}

</style>

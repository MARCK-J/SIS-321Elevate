<script setup>
// Importar `useRoute` de Vue Router para acceder a los parámetros de la query
import { useRoute } from "vue-router";
import { ref, onMounted } from 'vue';
import axios from 'axios';
import BaseLayout from "../../layouts/sections/components/BaseLayout.vue";
import View from "../../layouts/sections/components/View.vue";
import QuestionModal from './QuestionModal.vue';

// Acceder a la ruta actual
const route = useRoute();

// Extraer `quizId` de los parámetros de la ruta
const quizId = ref(route.params.quizId || 0);

// Datos del cuestionario y preguntas
const quiz = ref({});
const questions = ref([]);

// Estado del modal
const showModal = ref(false);

// Función para obtener los detalles del cuestionario y sus preguntas
const fetchQuizDetails = async () => {
  try {
    const response = await axios.get(`http://localhost:9999/api/v1/quizzes/${quizId.value}`, {
      headers: {
        Accept: 'application/json',
      },
    });
    quiz.value = response.data.result;

    // Obtener las preguntas del cuestionario
    const questionsResponse = await axios.get(`http://localhost:9999/api/v1/questions/quiz/${quizId.value}`, {
      headers: {
        Accept: 'application/json',
      },
    });

    const questionsData = questionsResponse.data.result;

    // Verificar que questionsData sea un array
    if (Array.isArray(questionsData)) {
      // Obtener las opciones para cada pregunta
      for (const question of questionsData) {
        const optionsResponse = await axios.get(`http://localhost:9999/api/v1/options/question/${question.questionId}`, {
          headers: {
            Accept: 'application/json',
          },
        });
        question.options = optionsResponse.data.result;
      }

      questions.value = questionsData;
    } else {
      console.error('questionsData is not an array:', questionsData);
    }
  } catch (error) {
    console.error('Error fetching quiz details:', error);
  }
};

// Ejecutar fetchQuizDetails cuando el componente se monte
onMounted(fetchQuizDetails);

</script>

<template>
  <BaseLayout
    :title="quiz.title"
    :breadcrumb="[ 
      { label: 'Cursos', route: '/' },
      { label: quiz.courseTitle },
      { label: 'Cuestionarios', route: `/quizzes?courseId=${quiz.courseId}&courseTitle=${quiz.courseTitle}` },
      { label: quiz.title },
    ]"
  >
    <View title="Detalles del Cuestionario" id="header-1">
      <div class="container mt-5">
        <h2>{{ quiz.title }}</h2>
        <p>{{ quiz.description }}</p>
        <p><strong>Fecha Límite:</strong> {{ formatDate(quiz.dueDate) }}</p>
        <button class="btn btn-primary mt-3" @click="showModal = true">
          Añadir Pregunta
        </button>
        <div v-for="question in questions" :key="question.questionId" class="mt-4">
          <h4>{{ question.content }}</h4>
          <ul>
            <li v-for="option in question.options" :key="option.optionId" :class="{'correct': option.correct, 'incorrect': !option.correct}">
              {{ option.content }}
            </li>
          </ul>
        </div>
      </div>
    </View>
    <QuestionModal :show="showModal" :quizId="quizId" @close="showModal = false" @questionCreated="fetchQuizDetails" />
  </BaseLayout>
</template>

<script>
function formatDate(dateString) {
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Date(dateString).toLocaleDateString(undefined, options);
}
</script>

<style scoped>
.container {
  z-index: 2;
  position: relative;
}

.btn-primary {
  background-color: #007bff;
  border-color: #007bff;
}

.btn-primary:hover {
  background-color: #0056b3;
  border-color: #004085;
}

.correct {
  color: green;
}

.incorrect {
  color: red;
}
</style>
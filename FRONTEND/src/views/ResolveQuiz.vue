<template>
  <BaseLayout>
    <div class="resolve-quiz" v-if="quizData">
      <h1>{{ quizData.title }}</h1>
      <div v-for="question in questions" :key="question.questionId" class="question">
        <h3>{{ question.content }}</h3>
        <div v-for="option in question.options" :key="option.optionId" class="option">
          <label>
            <input type="radio" :name="question.questionId" :value="option.optionId" />
            {{ option.content }}
          </label>
        </div>
      </div>
    </div>
    <div v-else>
      <p>Cargando...</p>
    </div>
  </BaseLayout>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import BaseLayout from "../layouts/sections/components/BaseLayout.vue";

export default {
  components: {
    BaseLayout,
  },
  setup() {
    const route = useRoute();
    const quizId = computed(() => route.query.quizId || 0);

    const quizData = ref(null);
    const questions = ref([]);

    // Función para obtener el quiz por `quizId`
    const fetchQuizById = async (quizId) => {
      try {
        const response = await axios.get(
          `http://localhost:9999/api/v1/quizzes/${quizId.value}`
        );
        quizData.value = response.data.result;
      } catch (error) {
        console.error("Error al obtener el quiz:", error);
      }
    };

    // Función para obtener las preguntas por `quizId`
    const fetchQuestionsByQuizId = async (quizId) => {
      try {
        const response = await axios.get(
          `http://localhost:9999/api/v1/questions/quiz/${quizId.value}`
        );
        const questionsData = response.data.result;
        if (Array.isArray(questionsData)) {
          for (const question of questionsData) {
            const optionsResponse = await axios.get(
              `http://localhost:9999/api/v1/options/question/${question.questionId}`
            );
            question.options = optionsResponse.data.result;
          }
          questions.value = questionsData;
        } else {
          console.error("La respuesta de preguntas no es un array:", questionsData);
        }
      } catch (error) {
        console.error("Error al obtener las preguntas:", error);
      }
    };

    onMounted(() => {
      if (quizId.value) {
        fetchQuizById(quizId);
        fetchQuestionsByQuizId(quizId);
      } else {
        console.error("Quiz ID no válido:", quizId.value);
      }
    });

    return {
      quizData,
      questions,
    };
  },
};
</script>

<style scoped>
.resolve-quiz {
  padding: 20px;
}

.question {
  margin-bottom: 20px;
}

.option {
  margin-left: 20px;
}
</style>
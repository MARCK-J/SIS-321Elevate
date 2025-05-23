<template>
    <div>
      <h2>{{ courseTitle }} - Cuestionario</h2>
      
      <p v-if="!isEnrolled">Para continuar debes inscribirte al curso.</p>
      
      <ul v-if="questions.length">
        <li v-for="(question, index) in questions" :key="index">
          <h3>{{ question.text }}</h3>
          <ul>
            <li v-for="(answer, i) in question.answers" :key="i">
              <input type="radio" :name="`question-${index}`" :value="answer" v-model="userAnswers[index]" />
              {{ answer }}
            </li>
          </ul>
        </li>
      </ul>
      
      <button @click="submitQuiz" :disabled="!isEnrolled">Enviar</button>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  
  // Aquí se recibe el título del curso y el ID como props
  const props = defineProps(['courseTitle', 'courseId']);
  
  // Ejemplo de preguntas (puedes reemplazar esto con datos de tu API)
  const questions = ref([
    { text: "¿Cuál es la capital de Francia?", answers: ["Madrid", "Roma", "París", "Londres"] },
    { text: "¿Cuál es la capital de Alemania?", answers: ["Berlín", "Lisboa", "Viena", "Varsovia"] },
  ]);
  
  // Para almacenar respuestas de usuario
  const userAnswers = ref(Array(questions.value.length).fill(null));
  
  // Estado de inscripción del usuario
  const isEnrolled = ref(false);
  
  // Función para verificar si el usuario está inscrito en el curso
  const checkEnrollment = async () => {
    try {
      // Suponiendo que tienes una función o API para verificar la inscripción del usuario
      const response = await fetch(`/api/check-enrollment?courseId=${props.courseId}`);
      const data = await response.json();
      isEnrolled.value = data.isEnrolled; // Cambia esto según la estructura de tu respuesta
    } catch (error) {
      console.error("Error al verificar la inscripción:", error);
    }
  };
  
  // Llamamos a checkEnrollment cuando el componente se monta
  onMounted(() => {
    checkEnrollment();
  });
  
  const submitQuiz = () => {
    if (isEnrolled.value) {
      console.log('Respuestas del usuario:', userAnswers.value);
      // Aquí podrías enviar las respuestas a tu backend o verificar resultados
    }
  };
  </script>
  
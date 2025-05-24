<template>
  <div class="cuestionario-container">
    <h1>Crear Cuestionario</h1>

    <!-- Input para el título del quiz -->
    <div class="input-group">
      <label>Título del Cuestionario:</label>
      <input type="text" v-model="quizTitulo" class="text-input" placeholder="Escribe el título del cuestionario..." required />
      <span v-if="!quizTitulo" class="error-message">Este campo es obligatorio.</span>
    </div>

    <!-- Input para la descripción del quiz -->
    <div class="input-group">
      <label>Descripción del Cuestionario:</label>
      <textarea v-model="quizDescripcion" class="text-input" placeholder="Escribe la descripción del cuestionario..." required></textarea>
      <span v-if="!quizDescripcion" class="error-message">Este campo es obligatorio.</span>
    </div>

    <!-- Input para la fecha límite del quiz -->
    <div class="input-group">
      <label>Fecha Límite:</label>
      <input type="date" v-model="quizFechaLimite" class="text-input" required />
      <span v-if="!quizFechaLimite" class="error-message">Este campo es obligatorio.</span>
    </div>

    <div class="preguntas-scroll-container">
      <div v-for="(pregunta, index) in preguntas" :key="index" class="pregunta-container">
        <div class="pregunta-header">
          <h3>Pregunta {{ index + 1 }}</h3>
          <button @click="eliminarPregunta(index)" class="btn-eliminar">Eliminar Pregunta</button>
        </div>

        <!-- Input de la pregunta -->
        <div class="input-group">
          <label>Pregunta:</label>
          <input type="text" v-model="pregunta.texto" class="text-input" placeholder="Escribe tu pregunta..." required />
          <span v-if="!pregunta.texto" class="error-message">Este campo es obligatorio.</span>
        </div>

        <!-- Contenedor con scroll para respuestas de opción múltiple -->
        <div class="respuestas-scroll-container">
          <div v-for="(respuesta, rIndex) in pregunta.respuestas" :key="rIndex" class="respuesta-container">
            <input type="text" v-model="respuesta.texto" class="text-input" placeholder="Escribe una respuesta..." required />
            <span v-if="!respuesta.texto" class="error-message">Este campo es obligatorio.</span>
            <input type="radio" :name="'correcta-' + index" v-model="pregunta.respuestaCorrecta" :value="rIndex" />
            <label>Correcta</label>
            <button @click="eliminarRespuesta(index, rIndex)" class="btn-eliminar-respuesta">Eliminar</button>
          </div>
        </div>
        <button @click="agregarRespuesta(index)" class="btn-agregar-respuesta">+ Añadir Respuesta</button>
      </div>
    </div>

    <!-- Botón para agregar nueva pregunta -->
    <button @click="agregarPregunta" class="btn-agregar-pregunta">+ Añadir Pregunta</button>

    <!-- Botón para enviar el cuestionario -->
    <button @click="enviarCuestionario" class="btn-enviar">Enviar Cuestionario</button>

    <!-- Ventana emergente para confirmación -->
    <div v-if="mensajeExito" class="popup">
      {{ mensajeExito }}
    </div>
  </div>
</template>

<script>
import axios from 'axios';

// Define la URL base de la API usando la variable de entorno
// Esto se resolverá a "http://localhost:9999" en desarrollo
// y a "https://tu-backend-render-url.onrender.com" en producción
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL;

export default {
  data() {
    return {
      quizTitulo: '',
      quizDescripcion: '',
      quizFechaLimite: '',
      preguntas: [
        {
          texto: '',
          respuestas: [{ texto: '' }, { texto: '' }],
          respuestaCorrecta: null
        }
      ],
      mensajeExito: ''
    };
  },
  methods: {
    agregarPregunta() {
      this.preguntas.push({
        texto: '',
        respuestas: [{ texto: '' }, { texto: '' }],
        respuestaCorrecta: null
      });
    },
    eliminarPregunta(index) {
      this.preguntas.splice(index, 1);
    },
    agregarRespuesta(index) {
      this.preguntas[index].respuestas.push({ texto: '' });
    },
    eliminarRespuesta(pIndex, rIndex) {
      this.preguntas[pIndex].respuestas.splice(rIndex, 1);
    },
    async enviarCuestionario() {
      // Validar que todos los campos del quiz y las preguntas están llenos
      let valido = true;
      if (!this.quizTitulo || !this.quizDescripcion || !this.quizFechaLimite) {
        valido = false;
      }
      for (const pregunta of this.preguntas) {
        if (!pregunta.texto || pregunta.respuestas.some((respuesta) => !respuesta.texto) || pregunta.respuestaCorrecta === null) {
          valido = false;
          break;
        }
      }
      if (!valido) {
        this.mensajeExito = ''; // Resetea el mensaje de éxito
        return;
      }

      try {
        // Crear el quiz
        const quizResponse = await axios.post(`${API_BASE_URL}/api/v1/quizzes`, {
          title: this.quizTitulo,
          description: this.quizDescripcion,
          dueDate: this.quizFechaLimite,
          lessonsId: 1 // Asegúrate de pasar el ID de la lección correcta
        },);

        const quizId = quizResponse.data.id;

        // Crear preguntas y opciones
        for (const pregunta of this.preguntas) {
          const questionResponse = await axios.post(`${API_BASE_URL}/api/v1/questions`, {
            quizId: quizId,
            content: pregunta.texto,
            questionType: 'multiple choice'
          }
          );

          const questionId = questionResponse.data.id;

          for (const [rIndex, respuesta] of pregunta.respuestas.entries()) {
            await axios.post(`${API_BASE_URL}/api/v1/options`, {
              questionId: questionId,
              content: respuesta.texto,
              isCorrect: rIndex === pregunta.respuestaCorrecta
            });
          }
        }

        // Mostrar mensaje de éxito
        this.mensajeExito = 'Cuestionario creado exitosamente para la lección.';
      } catch (error) {
        console.error('Error al enviar el cuestionario:', error);
      }
    }
  }
};
</script>

<style scoped>
.cuestionario-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 30px;
  background-color: #f1e4cb;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.preguntas-scroll-container {
  max-height: 400px;
  overflow-y: auto;
  margin-top: 10px;
  margin-bottom: 10px;
}

.pregunta-container {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pregunta-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.input-group {
  margin-top: 10px;
}

.label {
  font-weight: bold;
}

.text-input,
.select-input,
.file-input {
  width: 100%;
  padding: 10px;
  margin-top: 5px;
  border-radius: 5px;
  border: 1px solid #ccc;
  font-size: 16px;
}

.respuestas-scroll-container {
  max-height: 200px;
  overflow-y: auto;
  margin-top: 10px;
  margin-bottom: 10px;
}

.respuesta-container {
  display: flex;
  align-items: center;
  margin-top: 10px;
  margin-bottom: 10px;
}

.text-input {
  flex-grow: 1;
  margin-right: 10px;
}

.error-message {
  color: red;
  font-size: 12px;
}

.btn-agregar-pregunta,
.btn-agregar-respuesta,
.btn-enviar,
.btn-eliminar,
.btn-eliminar-respuesta {
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-agregar-pregunta,
.btn-agregar-respuesta,
.btn-enviar {
  background-color: #4caf50;
  color: white;
}

.btn-eliminar,
.btn-eliminar-respuesta {
  background-color: #e74c3c;
  color: white;
}

.btn-eliminar:hover,
.btn-eliminar-respuesta:hover {
  background-color: #c0392b;
}

.btn-agregar-respuesta:hover,
.btn-agregar-pregunta:hover,
.btn-enviar:hover {
  background-color: #388e3c;
}

.btn-agregar-pregunta {
  margin-top: 20px;
}

.btn-enviar {
  margin-top: 30px;
  width: 100%;
  background-color: #2196f3;
}

.btn-enviar:hover {
  background-color: #1976d2;
}

.popup {
  margin-top: 20px;
  padding: 10px;
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
  border-radius: 5px;
  text-align: center;
}
</style>
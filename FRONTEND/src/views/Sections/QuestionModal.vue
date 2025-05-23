<template>
  <div class="modal" tabindex="-1" role="dialog" v-if="show">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Crear Pregunta</h5>
          <button type="button" class="close" @click="closeModal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="createQuestion">
            <div class="form-group">
              <label for="questionContent">Pregunta</label>
              <input type="text" class="form-control" id="questionContent" v-model="questionContent" required>
            </div>
            <div class="form-group">
              <label for="questionType">Tipo de Pregunta</label>
              <select class="form-control" id="questionType" v-model="questionType" required>
                <option value="multiple choice">Opción Múltiple</option>
                <option value="true/false">Verdadero/Falso</option>
              </select>
            </div>
            <div class="form-group">
              <label>Opciones</label>
              <div class="options-container">
                <div v-for="(option, index) in options" :key="index" class="option-item">
                  <div class="input-group mb-2 align-items-start">
                    <input 
                      type="text" 
                      class="form-control" 
                      v-model="option.content" 
                      :class="{'correct-option': option.correct, 'incorrect-option': !option.correct}" 
                      placeholder="Opción" 
                      required
                    >
                    <div class="input-group-append">
                      <button type="button" class="btn btn-danger" @click="removeOption(index)">Eliminar</button>
                    </div>
                  </div>
                </div>
              </div>
              <button type="button" class="btn btn-secondary" @click="addCorrectOption">Añadir Respuesta Correcta</button>
              <button type="button" class="btn btn-secondary" @click="addIncorrectOption">Añadir Respuesta Incorrecta</button>
            </div>
            <button type="submit" class="btn btn-primary">Crear Pregunta</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Swal from 'sweetalert2';

export default {
  props: {
    show: {
      type: Boolean,
      required: true,
    },
    quizId: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      questionContent: '',
      questionType: 'multiple choice',
      options: [],
    };
  },
  methods: {
    addCorrectOption() {
      this.options.push({ content: '', correct: true });
    },
    addIncorrectOption() {
      this.options.push({ content: '', correct: false });
    },
    removeOption(index) {
      this.options.splice(index, 1);
    },
    async createQuestion() {
      try {
        const questionResponse = await axios.post('http://localhost:9999/api/v1/questions', {
          quizId: this.quizId,
          content: this.questionContent,
          questionType: this.questionType,
        }, {
          headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
          },
        });

        const questionId = questionResponse.data.result.questionId;

        // Crear las opciones
        for (const option of this.options) {
          await axios.post('http://localhost:9999/api/v1/options', {
            questionId: questionId,
            content: option.content,
            correct: option.correct,
          }, {
            headers: {
              'Content-Type': 'application/json',
              Accept: 'application/json',
            },
          });
        }

        Swal.fire({
          title: "Éxito",
          text: "Pregunta y opciones creadas exitosamente",
          icon: "success",
        });

        // Limpiar el formulario
        this.resetForm();
        this.$emit('questionCreated');
        this.closeModal();
      } catch (error) {
        console.error('Error creating question and options:', error);
        Swal.fire({
          title: "Error",
          text: "No se pudo crear la pregunta y opciones. Intente nuevamente.",
          icon: "error",
        });
      }
    },
    resetForm() {
      this.questionContent = '';
      this.questionType = 'multiple choice';
      this.options = [];
    },
    closeModal() {
      this.$emit('close');
    },
  },
};
</script>

<style scoped>
.modal {
  display: block;
  background: rgba(0, 0, 0, 0.5);
}

.options-container {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #ccc;
  padding: 10px;
  border-radius: 5px;
}

.option-item {
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.correct-option {
  background-color: #d4edda;
  border-color: #c3e6cb;
}

.incorrect-option {
  background-color: #f8d7da;
  border-color: #f5c6cb;
}

.input-group-text {
  display: flex;
  align-items: center;
}

.input-group-append {
  display: flex;
  align-items: center;
}

.radio-label {
  margin-right: 10px;
}
</style>
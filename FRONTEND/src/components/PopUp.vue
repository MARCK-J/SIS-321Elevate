<template>
    <div v-if="loading" class="loading-panel">
      <p>Cargando...</p>
    </div>
    <div v-else class="pop-up">
      <div class="pop-up-inner">
        <div class="pop-up-close" @click="close">&times;</div>
        <h1>Sube un archivo</h1>
        <form @submit.prevent="submitForm" class="upload-form">
          <div class="form-group">
            <label for="title">Título:</label>
            <input v-model="form.title" id="title" type="text" required>
          </div>
          <div class="form-group">
            <label for="date">Fecha de subida:</label>
            <input v-model="form.date" id="date" type="date" required>
          </div>
          <div class="form-group">
            <label for="description">Descripción:</label>
            <textarea v-model="form.description" id="description" rows="4" required></textarea>
          </div>
          <div class="form-group">
            <label for="file">Archivo:</label>
            <input ref="fileInput" id="file" type="file" @change="handleFileUpload" required>
          </div>
          <div class="pop-up-button">
            <button type="submit">Enviar</button>
          </div>
        </form>
        <div class="pop-up-button">
          <button @click="close">Cerrar</button>
        </div>
      </div>
    </div>
  </template>
  <script>
  import Swal from "sweetalert2";

  export default {
    data() {
      return {
        loading: false,
        form: {
          title: '',
          date: '',
          description: '',
          file: null,
        },
      };
    },
    methods: {
      submitForm() {
        // Simula la subida del archivo (puedes reemplazar esto con tu lógica de subida real)
        const formData = new FormData();
        formData.append('title', this.form.title);
        formData.append('date', this.form.date);
        formData.append('description', this.form.description);
        formData.append('file', this.form.file);
  
        // Puedes usar axios o fetch para enviar los datos al servidor
        // Aquí hay un ejemplo con axios:
        /*
        axios.post('tu-url-de-subida', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        .then(response => {
          console.log('Archivo subido con éxito', response);
          this.close();
        })
        .catch(error => {
          console.error('Error al subir el archivo:', error);
        });
        */
  
        // Simulación de respuesta exitosa
        Swal.fire('Éxito', `Archivo subido con exito`, 'success');
        this.close();
      },
      handleFileUpload(event) {
        const file = event.target.files[0];
        if (file) {
          this.form.file = file;
        }
      },
      close() {
        this.$emit('close');
      },
    },
  };
  </script>
  <style>
  .loading-panel {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 10;
    height: 100vh;
    width: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    color: #fff;
    font-size: 1.5rem;
  }
  
  .pop-up {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 10;
    padding: 32px 16px;
    height: 100vh;
    width: 100%;
    background-color: rgba(144, 216, 214, 0.5);
    display: grid;
    place-items: center;
  }
  
  .pop-up-inner {
    background-color: #fff;
    color: #000;
    position: relative;
    width: 70%;
    max-width: 600px;
    padding: 40px;
    border-radius: 8px;
    box-shadow: 0 5px 5px rgba(0, 0, 0, 0.2);
    transition: all 250ms ease-in-out;
  }
  
  .pop-up-close {
    position: absolute;
    height: 52px;
    width: 52px;
    display: flex;
    justify-content: center;
    align-items: center;
    top: 0;
    right: 0;
    font-size: 3rem;
    color: black;
    cursor: pointer;
  }
  
  .upload-form {
    display: flex;
    flex-direction: column;
  }
  
  .form-group {
    margin-bottom: 16px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 8px;
  }
  
  .form-group input,
  .form-group textarea {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  
  .pop-up-button {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .pop-up-button button {
    background-color: rgb(164, 227, 200);
    color: #000;
    border: 1px solid black;
    border-radius: 10px;
    padding: 5px 20px;
    margin: 19px 0px 0px 0px;
  }
  </style>
  
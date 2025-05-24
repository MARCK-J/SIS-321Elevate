<template>
    <div class="login">
      <router-link to="/" class="back-link">Regresar</router-link>
      <LoginCard @submit="onSubmit" />
    </div>
  </template>
  
  <script>
  import LoginCard from "/src/components/LoginCard.vue";
  import axios from 'axios';
  
  export default {
    name: "login-view",
    components: {
      LoginCard
    },
    methods: {
      async onSubmit() {
        try {
          // Obtener el token captcha (ejemplo usando reCAPTCHA)
          const captchaToken = await this.$recaptchaInstance.execute('login');
          
          // Incluir el token en la solicitud
          const userData = {
            email: this.email,
            password: this.password,
            captchaToken: captchaToken
          };
          
          const response = await axios.post('/api/v1/auth/login', userData);
          // Procesar respuesta...
        } catch (error) {
          // Manejar error...
        }
      }
    }
  };
  </script>
  
  <style>
  .login {
    background-image: url('@/assets/img/Register_Login/Fondo3.jpg');
    width: 100%;
    background-size: cover;
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
  }
  
  .back-link {
    position: absolute;
    top: 20px;
    left: 20px;
    color: rgb(255, 255, 255);
    background-color: rgb(85, 94, 94);
    text-decoration: none;
    font-size: 18px;
    padding: 10px 30px;
    border: 1px solid black;
    border-radius: 15px;
  }
  </style>

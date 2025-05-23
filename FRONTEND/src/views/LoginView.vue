<template>
    <div class="login">
      <router-link to="/" class="back-link">Regresar</router-link>
      <LoginCard />
    </div>
  </template>
  
  <script>
  import LoginCard from "/src/components/LoginCard.vue";
  
  export default {
    name: "login-view",
    components: {
      LoginCard
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

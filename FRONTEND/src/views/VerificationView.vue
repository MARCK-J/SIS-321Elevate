<template>
  <div class="verificacion">
    <router-link to="/pages/login" class="back-link">Regresar</router-link>
    <VerificationCard/>
  </div>
</template>

<script>
import VerificationCard from '../components/VerificationCard.vue';

export default {
  name: "verificacion-view",
  components: {
    VerificationCard,
  },
};
</script>

<style>
.verificacion {
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
  color: black;
  background-color: rgb(158, 229, 229);
  text-decoration: none;
  font-size: 18px;
  padding: 10px 30px;
  border: 1px solid black;
  border-radius: 15px;
}
</style>

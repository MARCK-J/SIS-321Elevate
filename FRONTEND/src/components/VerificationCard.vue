<template>
    <div class="TarjetaVerificacion">
      <img src="../assets/img/Register_Login/Elevate.png" alt="Logo Elevate" />
      <br />
      <h3>Verificacion de dos pasos</h3>
      <p>Se le envio un codigo de verificacion a su correo</p>
      <p>por favor ingrese ese codigo en el siguiente campo para la validacion</p>
      <form @submit.prevent="validacion" class="form">
        <div class="CustomInput">
          <p>Codigo de verificacion:</p>
        <input
          v-model="codigoIngresado"
          placeholder="Ingrese su codigo de verificacion"
          type="text"
          required
        />
        </div>
        <button type="submit">Continuar</button>
      </form>
    </div>
  </template>
<script lang="ts">
import { defineComponent } from 'vue'; 
import Swal from 'sweetalert2'; 
import { useAppStore } from '../stores';

export default defineComponent({
  name: "NavBar",
  data() {
    const appStore = useAppStore();
    const codigoEsperadoN = appStore.getVerificationCode; 
    const identificador = appStore.getIdentificador; 
    
    return {
      codigoIngresado: "",
      codigoEsperado: codigoEsperadoN,
      userId:identificador,
      role:this.$route.params.role,
    };
  },
  methods:{
    validacion(){
      if (this.codigoIngresado === this.codigoEsperado) {

        
        const appStore = useAppStore();
        appStore.setIdentificador(this.userId);
        appStore.setTipoPersona(this.role);
        appStore.setLogin(true);
        
        Swal.fire({
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 3000,
          icon: 'success',
          title: 'Felicidades',
          text: 'Su inicio de sesion se completo correctamente',
        });
        this.$router.push("/");
        // Accede al estado del store para imprimir los valores
        console.log(appStore.identificador);
        console.log(appStore.tipoPersona);
        console.log(appStore.login);
      }else{
        Swal.fire({
        icon: 'error',
        title: 'Codigo incorrecto',
        timer: 2000,
        text: 'Introduzca el codigo enviado a su correo',
      });
      }
    }
  },
});
</script>


  <style>
  .CustomInput {
      width: 85%;
      display: flex;
      justify-content: center;
      flex-direction: column;
      margin: 10px 0px;
  }
  .CustomInput p {
      padding: 0;
      margin: 0;
  }
  .CustomInput input {
      border: 2px solid black;
      border-radius: 25px;
      padding: 10px 20px;
  }
  .form {
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
  
  .TarjetaVerificacion {
    background: #d5f3fb;
    border: 3px solid black;
    width: 30%;
    height: fit-content;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    padding: 40px 8px;
    border-radius: 30px;
    color: black;
  }
  .TarjetaVerificacion p{
    width: 80%;
  }
  
  .TarjetaVerificacion img {
    width: 35%;
  }
  
  .enlace {
    margin: 10px 0px;
  }
  
  .TarjetaVerificacion button {
    width: 85%;
    border-radius: 25px;
    padding: 10px;
    background-color: blue;
    border: 2px solid black;
    color: #fff;
  }
  
  .registro-enlace-container {
    display: flex;
    flex-direction: row;
    align-items: center;
  }
  @media (max-width: 860px) {
    .TarjetaVerificacion {
      width: 70%;
      padding: 10px 2px;
    }
  }
  </style>
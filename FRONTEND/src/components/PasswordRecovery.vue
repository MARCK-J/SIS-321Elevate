<template>
  <div class="password-recovery-container">
    <div class="password-recovery-box">
      <h3>Recuperación de Contraseña</h3>
      <form @submit.prevent="enviarCodigo" class="recovery-form">
        <div class="custom-input">
          <label for="email">Correo electrónico:</label>
          <input 
            v-model="email" 
            type="email" 
            id="email" 
            placeholder="Ingrese su correo electrónico" 
            required 
          />
          <i class="icon-email"></i>
        </div>
        <button type="submit" class="submit-btn">
          <i class="icon-send"></i> Enviar Código de Recuperación
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import Swal from "sweetalert2";
import axios from "axios";
import { AuthService } from "../services/authService";

export default {
  data() {
    return {
      email: "",
    };
  },
  methods: {
    async validarEmail() {
      try {
        const response = await axios.get(`http://localhost:9999/api/v1/user/email-exists?email=${this.email}`);
        return response.data.result;
      } catch (error) {
        Swal.fire({
          title: "Error",
          text: "No se pudo validar el correo electrónico. Intenta nuevamente",
          icon: "error",
        });
        return false;
      }
    },
    async enviarCodigo() {
      const emailValido = await this.validarEmail();
      if (!emailValido) {
        Swal.fire({
          title: "Correo no encontrado",
          text: "El correo electrónico ingresado no está registrado",
          icon: "error",
        });
        return;
      }

      try {
        await AuthService.sendPasswordRecoveryCode(this.email);
        Swal.fire({
          title: "Código enviado",
          text: "Revisa tu correo electrónico para el código de recuperación",
          icon: "info",
        });
        this.$router.push("/verify-recovery-code");
      } catch (error) {
        Swal.fire({
          title: "Error",
          text: "No se pudo enviar el código. Intenta nuevamente",
          icon: "error",
        });
        console.log(error);
      }
    },
  },
};
</script>

<style scoped>
.password-recovery-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #83a4d4, #b6fbff);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.password-recovery-box {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

h3 {
  color: #333;
  font-size: 24px;
  margin-bottom: 20px;
}

.recovery-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.custom-input {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}

.custom-input label {
  font-size: 14px;
  color: #555;
  margin-bottom: 5px;
}

.custom-input input {
  padding: 12px 15px;
  padding-left: 35px;
  font-size: 16px;
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 25px;
  transition: border-color 0.3s;
}

.custom-input input:focus {
  border-color: #007bff;
  outline: none;
}

.icon-email {
  position: absolute;
  left: 10px;
  top: 35px;
  font-size: 18px;
  color: #999;
}

.submit-btn {
  background-color: #28a745;
  color: white;
  padding: 12px 20px;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.submit-btn i {
  font-size: 18px;
}

.submit-btn:hover {
  background-color: #218838;
}

.submit-btn:focus {
  outline: none;
}

@media (max-width: 500px) {
  .password-recovery-box {
    width: 90%;
  }
}
</style>

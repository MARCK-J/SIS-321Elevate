<template>
  <div class="VerifyRecoveryCode">
    <div class="form-container">
      <h3>Verificar Código de Recuperación</h3>
      <form @submit.prevent="verificarCodigo">
        <div class="CustomInput">
          <label for="recoveryCode">Código de Verificación:</label>
          <input
            id="recoveryCode"
            v-model="recoveryCode"
            type="text"
            placeholder="Ingrese el código"
            required
            maxlength="6"
          />
        </div>
        <button type="submit" class="submit-button">Verificar Código</button>
      </form>
    </div>
  </div>
</template>

<script>
import { useAppStore } from "@/stores";
import Swal from "sweetalert2";

export default {
  data() {
    return {
      recoveryCode: "",
    };
  },
  methods: {
    verificarCodigo() {
      const authStore = useAppStore();
      if (this.recoveryCode === authStore.recoveryCode) {
        Swal.fire({
          title: "Código verificado",
          text: "Puede continuar para restablecer su contraseña",
          icon: "success",
        });
        this.$router.push("/reset-password"); // Redirige a la vista de restablecimiento de contraseña
      } else {
        Swal.fire({
          title: "Código incorrecto",
          text: "El código ingresado no coincide",
          icon: "error",
        });
      }
    },
  },
};
</script>

<style scoped>
.VerifyRecoveryCode {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f4f6f9;
  padding: 20px;
}

.form-container {
  background-color: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h3 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.CustomInput {
  margin-bottom: 20px;
}

.CustomInput label {
  font-size: 14px;
  color: #333;
  display: block;
  margin-bottom: 8px;
}

.CustomInput input {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 8px;
  margin-top: 8px;
}

.submit-button {
  width: 100%;
  padding: 12px;
  background-color: #007bff;
  color: white;
  font-size: 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 20px;
  transition: background-color 0.3s;
}

.submit-button:hover {
  background-color: #0056b3;
}
</style>

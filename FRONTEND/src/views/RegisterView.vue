<template>
  <div class="app">
    <div class="background-container">
      <img
        class="background-image"
        src="/src/assets/img/Register_Login/Fondo1.jpg"
        alt="Background Image"
      />
      <div class="form-container">
        <h2 class="form-title">Registro</h2>
        <p>Complete los siguientes campos, por favor</p>
        <form @submit.prevent="handleRegisterSubmit" class="text-start">
          <div class="form-group">
            <input
              type="text"
              v-model="firstName"
              class="form-control"
              placeholder="Nombres"
              required
            />
          </div>
          <div class="form-group">
            <input
              type="text"
              v-model="lastName"
              class="form-control"
              placeholder="Apellidos"
              required
            />
          </div>
          <div class="form-group">
            <input
              type="email"
              v-model="email"
              class="form-control"
              placeholder="Correo electrónico"
              required
            />
          </div>
          <div class="form-group">
            <input
              type="text"
              v-model="username"
              class="form-control"
              placeholder="Nombre de usuario"
              readonly
              required
            />
          </div>
          <div class="form-group">
            <input
              type="password"
              v-model="password"
              class="form-control"
              placeholder="Contraseña"
              required
            />
          </div>
          <div class="form-group">
            <input
              type="password"
              v-model="passwordConf"
              class="form-control"
              placeholder="Confirmar contraseña"
              required
            />
          </div>
          <div class="form-group">
            <select v-model="userType" class="form-control" required>
              <option value="" disabled>Selecciona tu rol</option>
              <option value="student">Estudiante</option>
              <option value="teacher">Docente</option>
            </select>
          </div>
          <!-- Campo de institución solo para docentes -->
          <div class="form-group" v-if="userType === 'teacher'">
            <select v-model="institucionId" class="form-control" required>
              <option value="" disabled>Selecciona tu institución</option>
              <option
                v-for="institucion in instituciones"
                :key="institucion.institucionId"
                :value="institucion.institucionId"
              >
                {{ institucion.nombre }}
              </option>
            </select>
          </div>
          <div class="validation">
            <div v-for="(validation, index) in validations" :key="index">
              <Icon
                :icon="validation.icon"
                width="16"
                height="16"
                :color="validation.color"
              />
              <p :class="validation.class">{{ validation.message }}</p>
            </div>
          </div>

          <!-- Reemplazar el componente vue-recaptcha con esto: -->
          <div class="mb-3 d-flex justify-content-center">
            <div id="g-recaptcha" ref="recaptchaContainer"></div>
          </div>

          <div class="text-center">
            <button
              type="submit"
              class="btn bg-gradient-info w-100 mt-4 mb-0"
              :disabled="!captchaVerified"
            >
              Registrarse
            </button>
          </div>
        </form>
        <p class="login-message">
          ¿Ya tienes una cuenta?
          <router-link to="/pages/login"
            >Inicia sesión aquí</router-link
          >
        </p>
      </div>
    </div>
    <div v-if="showSuccessPopup" class="popup-overlay" @click="closePopup">
      <div class="popup-content" @click.stop>
        <h3>Registro exitoso</h3>
        <p>¡Te has registrado correctamente!</p>
        <button @click="closePopup" class="btn btn-primary">Cerrar</button>
      </div>
    </div>
  </div>
</template>
<script>
import { ref, onMounted, watch } from 'vue';
import { Icon } from "@iconify/vue";
import Swal from "sweetalert2";
import axios from 'axios';

export default {
  components: {
    Icon,
  },
  setup() {
    const firstName = ref("");
    const lastName = ref("");
    const email = ref("");
    const username = ref("");
    const password = ref("");
    const passwordConf = ref("");
    const userType = ref("");
    const institucionId = ref("");
    const instituciones = ref([]);
    const showSuccessPopup = ref(false);
    const captchaVerified = ref(false);
    const recaptchaContainer = ref(null);

    // Watcher para sincronizar el email con el username
    watch(email, (newEmail) => {
      username.value = newEmail;
    });

    const validations = [
      {
        key: "confirmPassword",
        icon: "fluent:error-circle-20-regular",
        color: "red",
        class: "validation_error",
        message: "Las contraseñas deben ser iguales",
      },
      {
        key: "minLength",
        icon: "fluent:error-circle-20-regular",
        color: "red",
        class: "validation_error",
        message: "La contraseña debe ser de al menos 12 caracteres",
      },
      {
        key: "lowercase",
        icon: "fluent:error-circle-20-regular",
        color: "red",
        class: "validation_error",
        message: "La contraseña debe contener al menos una minuscula",
      },
      {
        key: "uppercase",
        icon: "fluent:error-circle-20-regular",
        color: "red",
        class: "validation_error",
        message: "La contraseña debe contener al menos una mayuscula",
      },
      {
        key: "specialChar",
        icon: "fluent:error-circle-20-regular",
        color: "red",
        class: "validation_error",
        message: "La contraseña debe contener al menos un caracter especial",
      },
      {
        key: "number",
        icon: "fluent:error-circle-20-regular",
        color: "red",
        class: "validation_error",
        message: "La contraseña debe contener al menos un numero",
      },
    ];

    // Cargar el script de reCAPTCHA
    onMounted(() => {
      // Crear elemento script
      const script = document.createElement('script');
      script.src = 'https://www.google.com/recaptcha/api.js';
      script.async = true;
      script.defer = true;
      
      // Cuando el script se carga, inicializar reCAPTCHA
      script.onload = () => {
        // Asegurarnos de que grecaptcha existe
        if (window.grecaptcha) {
          setTimeout(() => {
            try {
              window.grecaptcha.render('g-recaptcha', {
                'sitekey': '6LdU40YrAAAAALfS-EXHQXuF6xaHB8aONfT8L9R_',
                'callback': onCaptchaSuccess
              });
            } catch (e) {
              console.error('Error al renderizar reCAPTCHA:', e);
            }
          }, 100);
        }
      };
      
      // Agregar script a la página
      document.head.appendChild(script);
    });
    
    // Función llamada cuando el captcha es resuelto correctamente
    const onCaptchaSuccess = () => {
      captchaVerified.value = true;
    };
    
    const goBack = () => {
      this.$router.push("/");
    };

    const showError = (message) => {
      Swal.fire({
        icon: 'error',
        title: 'Oops...',
        text: message,
        showClass: {
          popup: 'animate__animated animate__bounceIn'
        },
        hideClass: {
          popup: 'animate__animated animate__fadeOut'
        }
      });
    };

    const getCurrentDate = () => {
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      const day = String(today.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };

    const loadInstituciones = async () => {
      try {
        const response = await axios.get("http://localhost:9999/api/v1/instituciones/all");
        // El backend retorna un array de objetos con institucionId, nombre, dominioInstitucional
        instituciones.value = response.data.result || [];
      } catch (error) {
        showError("No se pudieron cargar las instituciones.");
      }
    };

    const getEmailDomain = (email) => {
      // Extrae el dominio de un correo electrónico
      if (!email) return "";
      const parts = email.split("@");
      return parts.length === 2 ? parts[1].toLowerCase() : "";
    };

    const handleRegisterSubmit = async () => {
      if (!captchaVerified.value) {
        // Mostrar mensaje de error si el captcha no está validado
        Swal.fire('Error', 'Por favor, complete el captcha', 'error');
        return;
      }

      // Incluir el token del captcha en la solicitud
      const payload = {
        firstName: firstName.value,
        lastName: lastName.value,
        email: email.value,
        username: username.value,
        password: password.value,
        role: userRole,
        verification: false,
        activation: false,
        dateJoin: currentDate
      };
      if (userRole === 2) {
        payload.institucionId = Number(institucionId.value);
      }
      payload.recaptchaToken = captchaResponse.value;

      try {
        if (
          !firstName.value ||
          !lastName.value ||
          !email.value ||
          !username.value ||
          !password.value ||
          !passwordConf.value
        ) {
          showError("Todos los campos obligatorios deben estar llenos.");
          return;
        }
        if (password.value !== passwordConf.value) {
          showError("Las contraseñas no coinciden.");
          return;
        }
        if (!validatePassword(password.value)) {
          showError("La contraseña no cumple con los requisitos.");
          return;
        }
        const userRole = userType.value === "student" ? 1 : userType.value === "teacher" ? 2 : null;
        if (userRole === null) {
          showError("Debe seleccionar un tipo de usuario válido.");
          return;
        }
        // Validar institucionId correctamente para docentes
        if (userRole === 2) {
          const institucionIdNum = Number(institucionId.value);
          if (!institucionIdNum || institucionIdNum <= 0 || isNaN(institucionIdNum)) {
            showError("Debe seleccionar una institución válida.");
            return;
          }
          // Validar dominio del correo con el dominio de la institución
          const institucion = instituciones.value.find(
            (i) => Number(i.institucionId) === institucionIdNum
          );
          if (!institucion) {
            showError("Debe seleccionar una institución válida.");
            return;
          }
          const emailDomain = getEmailDomain(email.value);
          const institucionDomain = institucion.dominioInstitucional?.toLowerCase();
          if (!emailDomain || !institucionDomain || emailDomain !== institucionDomain) {
            showError(
              `El correo debe pertenecer al dominio institucional: ${institucionDomain}`
            );
            return;
          }
        }
        // No permitir roles 3 ni 4 desde el frontend
        if (userRole !== 1 && userRole !== 2) {
          showError("Rol no permitido.");
          return;
        }
        const currentDate = getCurrentDate();
        // Construir el payload según el backend
        const payload = {
          firstName: firstName.value,
          lastName: lastName.value,
          email: email.value,
          username: username.value,
          password: password.value,
          role: userRole,
          verification: false,
          activation: false,
          dateJoin: currentDate
        };
        if (userRole === 2) {
          payload.institucionId = Number(institucionId.value);
        }
        payload.recaptchaToken = captchaResponse.value;

        const response = await axios.post(
          "http://localhost:9999/api/v1/user/signup",
          payload
        );
        Swal.fire({
          icon: "success",
          title: "Registro exitoso",
          text: "Te has registrado correctamente.",
          timer: 2000,
          showConfirmButton: false
        });
        resetForm();
        this.$router.push('/');
      } catch (error) {
        const msg = error.response?.data?.message || "Error al crear la persona";
        showError(msg);
      }

      // Reset captcha después de enviar
      if (window.grecaptcha) {
        window.grecaptcha.reset();
        captchaVerified.value = false;
      }
    };

    const resetForm = () => {
      firstName.value = "";
      lastName.value = "";
      email.value = "";
      username.value = "";
      password.value = "";
      passwordConf.value = "";
      userType.value = "";
      institucionId.value = "";
    };

    const validatePassword = (password) => {
      return (
        password.length >= 12 &&
        /\d/.test(password) &&
        /[a-z]/.test(password) &&
        /[A-Z]/.test(password) &&
        /[!@#$%^&*()_+\-\[\]{};':"\\|,.<>\/?]/.test(password)
      );
    };

    const validateForm = () => {
      validations.forEach((validation) => {
        switch (validation.key) {
          case "confirmPassword":
            validation.icon =
              password.value === passwordConf.value
                ? "lets-icons:check-fill"
                : "fluent:error-circle-20-regular";
            validation.color =
              password.value === passwordConf.value ? "green" : "red";
            validation.class =
              password.value === passwordConf.value
                ? "validation_check"
                : "validation_error";
            break;
          case "minLength":
            validation.icon =
              password.value.length >= 12
                ? "lets-icons:check-fill"
                : "fluent:error-circle-20-regular";
            validation.color = password.value.length >= 12 ? "green" : "red";
            validation.class =
              password.value.length >= 12
                ? "validation_check"
                : "validation_error";
            break;
          case "lowercase":
            validation.icon = /[a-z]/.test(password.value)
              ? "lets-icons:check-fill"
              : "fluent:error-circle-20-regular";
            validation.color = /[a-z]/.test(password.value) ? "green" : "red";
            validation.class = /[a-z]/.test(password.value)
              ? "validation_check"
              : "validation_error";
            break;
          case "uppercase":
            validation.icon = /[A-Z]/.test(password.value)
              ? "lets-icons:check-fill"
              : "fluent:error-circle-20-regular";
            validation.color = /[A-Z]/.test(password.value) ? "green" : "red";
            validation.class = /[A-Z]/.test(password.value)
              ? "validation_check"
              : "validation_error";
            break;
          case "specialChar":
            validation.icon = /[!@#$%^&*()_+\-\[\]{};':"\\|,.<>\/?]/.test(password.value)
              ? "lets-icons:check-fill"
              : "fluent:error-circle-20-regular";
            validation.color = /[!@#$%^&*()_+\-\[\]{};':"\\|,.<>\/?]/.test(password.value)
              ? "green"
              : "red";
            validation.class = /[!@#$%^&*()_+\-\[\]{};':"\\|,.<>\/?]/.test(password.value)
              ? "validation_check"
              : "validation_error";
            break;
          case "number":
            validation.icon = /\d/.test(password.value)
              ? "lets-icons:check-fill"
              : "fluent:error-circle-20-regular";
            validation.color = /\d/.test(password.value) ? "green" : "red";
            validation.class = /\d/.test(password.value)
              ? "validation_check"
              : "validation_error";
            break;
        }
      });
    };

    const closePopup = () => {
      showSuccessPopup.value = false;
    };

    return {
      firstName,
      lastName,
      email,
      username,
      password,
      passwordConf,
      userType,
      institucionId,
      instituciones,
      showSuccessPopup,
      captchaVerified,
      recaptchaContainer,
      handleRegisterSubmit,
      goBack,
      showError,
      getCurrentDate,
      loadInstituciones,
      getEmailDomain,
      resetForm,
      validatePassword,
      validateForm,
      closePopup,
      validations
    };
  }
};
</script>
<style scoped>
/* ...estilos igual que antes... */
.validation {
  padding: 8px;
  border: 1px solid black;
  border-radius: 15px;
}
.validation div {
  display: flex;
  align-items: center;
}
.validation_error {
  margin-left: 8px;
  font-size: 12px;
  padding: 0;
  margin: 0;
  color: red;
}
.validation_check {
  margin-left: 8px;
  font-size: 12px;
  padding: 0;
  margin: 0;
  color: green;
}
.app {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
.background-container {
  position: relative;
  width: 100%;
  height: 100%;
}
.background-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.form-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  width: 32%;
}
.form-title {
  color: #333;
  font-size: 24px;
  text-align: center;
}
.form-group {
  width: 90%;
}
.form {
  display: flex;
  flex-direction: column;
}

.form-control {
  padding: 10px;
  border: none;
  border-bottom: 1px solid #ccc;
  background-color: transparent;
  width: 100%;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.form-control:focus {
  border-bottom-color: #007bff;
  outline: none;
}

.button-group {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
}

.btn {
  padding: 8px 20px;
  margin: 0px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  text-transform: uppercase;
  transition: all 0.3s ease;
  margin: 0 10px;
}

.btn-secondary {
  background-color: #999;
  color: #fff;
  border: 1px solid #999;
}

.btn-primary {
  background-color: #007bff;
  color: #fff;
  border: none;
}

.btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.login-message {
  margin-top: 5px;
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-bottom: 5px;
}

.alert {
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 5px;
}

.alert-danger {
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
}

.alert-success {
  background-color: #c6dbcb;
  border: 1px solid #c3e6cb;
  color: #155724;
}

@media only screen and (max-width: 768px) {
  .form-container {
    padding: 20px;
  }
}

/* You may want to complete this media query or remove it if not needed */
/* @media only screen and ... */

</style>
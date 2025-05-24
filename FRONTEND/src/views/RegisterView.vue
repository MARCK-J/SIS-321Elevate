<template>
  <div class="app">
    <div class="background-container">
      <img class="background-image" src="/src/assets/img/Register_Login/Fondo1.jpg" alt="Background Image" />
      <div class="form-container">
        <h2 class="form-title">Registro</h2>
        <p>Complete los siguientes campos, por favor</p>
        <form @submit.prevent="handleRegisterSubmit" class="text-start">
          <div class="form-group">
            <input type="text" v-model="firstName" class="form-control" placeholder="Nombres" required />
          </div>
          <div class="form-group">
            <input type="text" v-model="lastName" class="form-control" placeholder="Apellidos" required />
          </div>
          <div class="form-group">
            <input type="email" v-model="email" class="form-control" placeholder="Correo electrónico" required />
          </div>
          <div class="form-group">
            <input
              type="text"
              :value="email"
              class="form-control"
              placeholder="Nombre de usuario"
              disabled
              required
            />
          </div>
          <div class="form-group">
            <input type="password" v-model="password" class="form-control" placeholder="Contraseña" required />
          </div>
          <div class="form-group">
            <input type="password" v-model="passwordConf" class="form-control" placeholder="Confirmar contraseña" required />
          </div>
          <div class="form-group">
            <select v-model="roleId" class="form-control" required>
              <option value="" disabled>Selecciona tu rol</option>
              <option v-for="rol in roles" :key="rol.roleId" :value="rol.roleId">
                {{ rol.name }}
              </option>
            </select>
          </div>
          <div class="form-group" v-if="isDocente">
            <select v-model="institucionId" class="form-control" required>
              <option value="" disabled>Selecciona tu institución</option>
              <option v-for="institucion in instituciones" :key="institucion.institucionId" :value="institucion.institucionId">
                {{ institucion.nombre }}
              </option>
            </select>
          </div>
          <div class="validation">
            <div v-for="(validation, index) in validations" :key="index">
              <Icon :icon="validation.icon" width="16" height="16" :color="validation.color" />
              <p :class="validation.class">{{ validation.message }}</p>
            </div>
          </div>
          <div class="mb-3 d-flex justify-content-center">
            <div id="g-recaptcha" ref="recaptchaContainer"></div>
          </div>
          <div class="text-center">
            <button type="submit" class="btn bg-gradient-info w-100 mt-4 mb-0" :disabled="!captchaVerified">
              Registrarse
            </button>
          </div>
        </form>
        <p class="login-message">
          ¿Ya tienes una cuenta?
          <router-link to="/pages/login">Inicia sesión aquí</router-link>
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
import { ref, onMounted, watch, computed, reactive } from 'vue';
import { Icon } from "@iconify/vue";
import Swal from "sweetalert2";
import axios from 'axios';

export default {
  components: { Icon },
  setup() {
    const firstName = ref("");
    const lastName = ref("");
    const email = ref("");
    const password = ref("");
    const passwordConf = ref("");
    const roleId = ref("");
    const roles = ref([]);
    const institucionId = ref("");
    const instituciones = ref([]);
    const showSuccessPopup = ref(false);
    const captchaVerified = ref(false);
    const recaptchaContainer = ref(null);
    const captchaToken = ref(""); // Nueva variable para el token de captcha

    // Validaciones reactivas
    const validations = reactive([
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
    ]);

    // Computed para saber si el rol seleccionado es Docente
    const isDocente = computed(() => {
      const selectedRole = roles.value.find(r => r.roleId == roleId.value);
      return selectedRole && selectedRole.name === "Docente";
    });

    // Cargar roles desde el backend y filtrar solo Estudiante y Docente
    const loadRoles = async () => {
      try {
        const response = await axios.get("http://localhost:9999/api/v1/roles/all");
        // Acceder al array de roles anidado
        const rolesArray = response.data?.result?.result || [];
        roles.value = rolesArray.filter(
          r => r.name === "Estudiante" || r.name === "Docente"
        );
      } catch (error) {
        Swal.fire('Error', 'No se pudieron cargar los roles', 'error');
      }
    };

    const loadInstituciones = async () => {
      try {
        const response = await axios.get("http://localhost:9999/api/v1/instituciones/all");
        instituciones.value = response.data.result || [];
      } catch (error) {
        Swal.fire('Error', 'No se pudieron cargar las instituciones', 'error');
      }
    };

    // Cargar el script de reCAPTCHA
    onMounted(() => {
      loadRoles();
      loadInstituciones();
      const script = document.createElement('script');
      script.src = 'https://www.google.com/recaptcha/api.js';
      script.async = true;
      script.defer = true;
      script.onload = () => {
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
      if (!document.querySelector('script[src="https://www.google.com/recaptcha/api.js"]')) {
        document.head.appendChild(script);
      }
    });

    const onCaptchaSuccess = (response) => {
      console.log("Captcha verificado:", response);
      captchaVerified.value = true;
      // Guarda el token en una variable para usarlo en el envío
      captchaToken.value = response;
    };

    // Validar la contraseña en tiempo real y actualizar colores
    watch([password, passwordConf], () => {
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
    });

    // Validar contraseña antes de enviar
    const validatePassword = (password) => {
      return (
        password.length >= 12 &&
        /\d/.test(password) &&
        /[a-z]/.test(password) &&
        /[A-Z]/.test(password) &&
        /[!@#$%^&*()_+\-\[\]{};':"\\|,.<>\/?]/.test(password)
      );
    };

    // Enviar el formulario
    const handleRegisterSubmit = async () => {
      if (!captchaVerified.value) {
        Swal.fire('Error', 'Por favor, complete el CAPTCHA', 'error');
        return;
      }
      if (
        !firstName.value ||
        !lastName.value ||
        !email.value ||
        !password.value ||
        !passwordConf.value ||
        !roleId.value
      ) {
        Swal.fire('Error', 'Todos los campos obligatorios deben estar llenos.', 'error');
        return;
      }
      if (password.value !== passwordConf.value) {
        Swal.fire('Error', 'Las contraseñas no coinciden.', 'error');
        return;
      }
      if (!validatePassword(password.value)) {
        Swal.fire('Error', 'La contraseña no cumple con los requisitos.', 'error');
        return;
      }

      // Validar institución si es docente
      const selectedRole = roles.value.find(r => r.roleId == roleId.value);
      if (selectedRole && selectedRole.name === "Docente") {
        if (!institucionId.value) {
          Swal.fire('Error', 'Debe seleccionar una institución válida.', 'error');
          return;
        }
        const institucion = instituciones.value.find(
          (i) => Number(i.institucionId) === Number(institucionId.value)
        );
        if (!institucion) {
          Swal.fire('Error', 'Debe seleccionar una institución válida.', 'error');
          return;
        }
        const emailDomain = email.value.split("@")[1]?.toLowerCase();
        const institucionDomain = institucion.dominioInstitucional?.toLowerCase();
        if (!emailDomain || !institucionDomain || emailDomain !== institucionDomain) {
          Swal.fire('Error', `El correo debe pertenecer al dominio institucional: ${institucionDomain}`, 'error');
          return;
        }
      }

      // Construir el payload
      try {
        const payload = {
          firstName: firstName.value,
          lastName: lastName.value,
          email: email.value,
          username: email.value,
          password: password.value,
          role: { roleId: Number(roleId.value) },
          verification: false,
          activation: false,
          dateJoin: new Date().toISOString().split("T")[0],
          // Obtener el token directamente del evento onCaptchaSuccess
          captchaToken: document.getElementById('g-recaptcha-response')?.value
        };
        
        const response = await axios.post(
          "http://localhost:9999/api/v1/user/signup?roleId=" + roleId.value,
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
      } catch (error) {
        const msg = error.response?.data?.message || "Error al crear la persona";
        Swal.fire('Error', msg, 'error');
      }
      if (window.grecaptcha) {
        window.grecaptcha.reset();
        captchaVerified.value = false;
      }
    };

    const resetForm = () => {
      firstName.value = "";
      lastName.value = "";
      email.value = "";
      password.value = "";
      passwordConf.value = "";
      roleId.value = "";
      institucionId.value = "";
    };

    const closePopup = () => {
      showSuccessPopup.value = false;
    };

    return {
      firstName,
      lastName,
      email,
      password,
      passwordConf,
      roleId,
      roles,
      institucionId,
      instituciones,
      showSuccessPopup,
      captchaVerified,
      recaptchaContainer,
      handleRegisterSubmit,
      resetForm,
      validations,
      isDocente,
      closePopup
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
</style>
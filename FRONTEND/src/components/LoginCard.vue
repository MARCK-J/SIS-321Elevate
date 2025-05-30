<template>
  <div class="TarjetaLogin">
    <img src="../assets/img/Register_Login/Elevate.png" alt="Logo Elevate" />
    <h3>Bienvenido</h3>
    <p>Inicie su sesión para continuar en Elevate</p>
    <form @submit.prevent="continuar" class="form">
      <div class="CustomInput">
        <p>Correo electronico:</p>
        <input v-model="identifier" placeholder="Ingrese su correo electronico/nombre de usuario" type="text" required>
      </div>
      <div class="CustomInput">
        <p>Contraseña:</p>
        <input v-model="password" placeholder="Ingrese su contraseña" type="password" required/>
      </div>
      <router-link to="/password-recovery" class="enlace">¿Olvidaste tu contraseña?</router-link>
      <div class="mb-3 d-flex justify-content-center">
        <div id="g-recaptcha-login" ref="recaptchaContainerLogin"></div>
      </div>
      <button 
        type="submit" 
        class="btn bg-gradient-info w-100 mt-4 mb-0"
        :disabled="!captchaVerified"
      >
        Iniciar sesión
      </button>
    </form>
    <div class="registro-enlace-container">
      <span>¿No tienes una cuenta?</span>
      <router-link to="/pages/register" class="enlace">Regístrate</router-link>
    </div>
  </div>
</template>

<script>
import Swal from "sweetalert2";
import axios from "axios";
import { useAppStore } from "@/stores/index.js";
import { AuthService } from "../services/authService";
import { ref, onMounted } from 'vue';

export default {
  data() {
    return {
      identifier: "",
      password: "",
      randomCode: "",
    };
  },
  setup() {
    const captchaVerified = ref(false);
    const recaptchaContainerLogin = ref(null);

    // Cargar el script de reCAPTCHA
    onMounted(() => {
      const script = document.createElement('script');
      script.src = 'https://www.google.com/recaptcha/api.js';
      script.async = true;
      script.defer = true;
      script.onload = () => {
        if (window.grecaptcha) {
          setTimeout(() => {
            try {
              window.grecaptcha.render('g-recaptcha-login', {
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
      } else {
        setTimeout(() => {
          try {
            if (window.grecaptcha && recaptchaContainerLogin.value) {
              window.grecaptcha.render('g-recaptcha-login', {
                'sitekey': '6LdU40YrAAAAALfS-EXHQXuF6xaHB8aONfT8L9R_',
                'callback': onCaptchaSuccess
              });
            }
          } catch (e) {
            console.error('Error al renderizar reCAPTCHA:', e);
          }
        }, 100);
      }
    });

    const onCaptchaSuccess = () => {
      captchaVerified.value = true;
    };

    return {
      captchaVerified,
      recaptchaContainerLogin,
    };
  },
  methods: {
    async continuar() {
      try {
        const response = await axios.post("http://localhost:9999/api/v1/user/login", {
          identifier: this.identifier,
          password: this.password,
        });

        const { code, result } = response.data;

        if (code === "200-OK") {
          const appStore = useAppStore();
          const { email, userId, role, verification } = result;

          // Corrección: extraer roleId y roleName del objeto role
          const roleId = role?.roleId;
          const roleName = role?.name;

          appStore.setIdentificador(userId);
          appStore.setTipoPersona(roleId);

          // Guarda el usuario completo en localStorage para uso posterior
          localStorage.setItem('user', JSON.stringify(result));

          // Lógica para roles
          if (roleId === 1 || roleId === 2) {
            if (verification) {
              this.randomCode = AuthService.generateCode();
              await AuthService.sendMail(email, this.randomCode);
              this.$router.push({ name: 'verification-view', params: { userId, role: roleId, code: this.randomCode } });
              Swal.fire({
                title: "Código enviado",
                text: "Se ha enviado un código de verificación a tu correo",
                icon: "info",
              });
            } else {
              this.iniciarSesion(userId, roleId);
            }
          } else if (roleName === "AdminPagina") {
            Swal.fire({
              title: "Administrador de Página",
              text: "Has iniciado sesión como Administrador de Página.",
              icon: "info",
            });
            this.iniciarSesion(userId, roleId);
          } else if (roleName === "OSI") {
            Swal.fire({
              title: "Oficial de Seguridad de Informacion",
              text: "Has iniciado sesión como Oficial de Seguridad de Informacion.",
              icon: "info",
            });
            this.iniciarSesion(userId, roleId);
          } else {
            Swal.fire({
              title: "Rol no válido",
              text: "Contacta al administrador para más información.",
              icon: "error",
            });
          }
        } else {
          throw new Error(`Código de respuesta inesperado: ${code}`);
        }
      } catch (error) {
        console.error('Error en la solicitud:', error);
        Swal.fire({
          title: "Error",
          text: error.response && error.response.data ? error.response.data.message : "Correo electrónico o contraseña incorrectos",
          icon: "error",
        });
      }
    },
    iniciarSesion(userId, roleId) {
      const appStore = useAppStore();
      appStore.setIdentificador(userId);
      appStore.setTipoPersona(roleId);
      appStore.setLogin(true);

      localStorage.setItem('user', JSON.stringify({
        userId,
        role: roleId
      }));

      Swal.fire({
        title: "Éxito",
        text: "Inicio de sesión exitoso",
        icon: "success",
      });

      // Redirigir según el rol
      if (roleId === 4) {
        this.$router.push({ name: 'AdminUsuarios' });
      } else {
        this.$router.push("/");
      }
    },
  },
};
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

.TarjetaLogin {
  background: #d5f3fb;
  border: 3px solid black;
  height: fit-content;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 20px 8px;
  border-radius: 30px;
  color: black;
}

.TarjetaLogin img {
  width: 50%;
}

.enlace {
  margin: 10px 0px;
  color: #0026ff;
  text-decoration: underline;
}

.TarjetaLogin button {
  width: 85%;
  border-radius: 25px;
  padding: 10px;
  background-color: #007bff;
  border: 2px solid black;
  color: #fff;
}

.registro-enlace-container {
  display: flex;
  flex-direction: row;
  align-items: center;
}
@media (max-width: 860px) {
  .TarjetaLogin {
    width: 70%;
    padding: 10px 2px;
  }
}
</style>
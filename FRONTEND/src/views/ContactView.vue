<script setup>
import { ref } from "vue";
import { onMounted } from "vue";
import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import { AuthService } from "../services/authService";
import image from "@/assets/img/illustrations/illustration-signin.jpg";
import Swal from "sweetalert2";

const name = ref('');
const email = ref('');
const comment = ref('');

const submitForm = async () => {
  console.log('cambios' + name.value + " ahora el email" + email.value + 'Ahora el comentario ' + comment.value);
  try {
    AuthService.sendFeedbackEmail(name.value, email.value, comment.value);
    AuthService.sendFeedbackReportEmail(name.value, email.value, comment.value, 'marco.reynolds@ucb.edu.bo');
    Swal.fire({
      icon: 'success',
      title: 'Comentario/Queja enviado exitosamente.',
      showConfirmButton: false,
      timer: 1500
    });
    // Limpiar el formulario después de enviar
    name.value = '';
    email.value = '';
    comment.value = '';
  } catch (error) {
    console.error('Error al enviar el comentario/queja:', error);
    Swal.fire({
      icon: 'error',
      title: 'Hubo un error al enviar su comentario/queja.',
      text: 'Por favor, inténtelo de nuevo.',
    });
  }
};
</script>

<template>
  <div class="container position-sticky z-index-sticky top-0">
    <div class="row">
      <div class="col-12">
        <DefaultNavbar
          :sticky="true"
          :action="{
            route: 'https://www.creative-tim.com/product/vue-material-kit-pro',
            color: 'bg-gradient-success',
            label: 'Buy Now',
          }"
        />
      </div>
    </div>
  </div>
  <section>
    <div class="page-header min-vh-100 custom-background"> <!-- Color de fondo de la página -->
      <div class="container">
        <div class="row">
          <div
            class="col-6 d-lg-flex d-none h-100 my-auto pe-0 position-absolute top-0 start-0 text-center justify-content-center flex-column"
          >
            <div
              class="position-relative h-100 m-3 px-7 border-radius-lg d-flex flex-column justify-content-center"
              :style="{
                backgroundImage: `url(${image})`,
                backgroundSize: 'cover',
              }"
              loading="lazy"
            ></div>
          </div>
          <div
            class="mt-8 col-xl-5 col-lg-6 col-md-7 d-flex flex-column ms-auto me-auto ms-lg-auto me-lg-5"
          >
            <div class="card custom-card d-flex justify-content-center shadow-lg my-sm-0 my-sm-6 mt-8 mb-5">
              <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2 bg-transparent">
                <div class="header-title p-3">
                  <h3 class="text-white mb-0">Contáctanos</h3>
                </div>
              </div>
              <div class="card-body">
                <p class="pb-3">
                  Si tienes algún problema o duda respecto a nuestro sistema, no dudes en contactarnos.
                </p>
                <form id="contact-form" method="post" autocomplete="off" @submit.prevent="submitForm">
                  <div class="card-body p-0 my-3">
                    <div class="row">
                      <div class="col-md-6">
                        <div class="mb-4">
                          <label for="name" class="form-label">Nombre Completo</label>
                          <input
                            type="text"
                            class="form-control"
                            id="name"
                            v-model="name"
                            placeholder="Nombre Completo"
                            required
                          />
                        </div>
                      </div>
                      <div class="col-md-6 ps-md-2">
                        <div class="mb-4">
                          <label for="email" class="form-label">Correo electrónico</label>
                          <input
                            type="email"
                            class="form-control"
                            id="email"
                            v-model="email"
                            placeholder="NombreUsuario@gmail.com"
                            required
                          />
                        </div>
                      </div>
                    </div>
                    <div class="form-group mb-0 mt-md-0 mt-4">
                      <label for="comment" class="form-label">Describe tu problema o duda</label>
                      <textarea
                        id="comment"
                        class="form-control"
                        rows="6"
                        v-model="comment"
                        placeholder="Describe tu problema o duda"
                        required
                      ></textarea>
                    </div>
                    <div class="row">
                      <div class="col-md-12 text-center">
                        <button
                          class="btn btn-success mt-3 mb-0 custom-button"
                          type="submit"
                        >Enviar mensaje</button>
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <DefaultFooter />
</template>

<style scoped>
/* Fondo personalizado */
.custom-background {
  background-color: #c2d1b9; /* Fondo gris claro para toda la página */
  padding-top: 20px;
  padding-bottom: 20px;
}

/* Estilo personalizado para la tarjeta */
.custom-card {
  background: #ffffff; /* Fondo blanco */
  border-radius: 10px; /* Bordes ligeramente redondeados */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Sombra para darle profundidad */
  padding: 20px; /* Espaciado interno */
}

/* Estilo para el encabezado de la tarjeta */
.header-title {
  background: #4caf50; /* Fondo verde */
  border-radius: 10px 10px 0 0; /* Bordes redondeados solo en la parte superior */
  text-align: center;
}

/* Estilo para el botón */
.custom-button {
  background-color: #4caf50; /* Fondo verde */
  color: white;
  border-radius: 20px; /* Bordes redondeados */
  padding: 10px 20px;
  transition: background-color 0.3s ease; /* Transición suave */
}

.custom-button:hover {
  background-color: #388e3c; /* Cambiar color al pasar el ratón */
}
</style>

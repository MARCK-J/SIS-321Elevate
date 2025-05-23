<template>
  <div class="profile-container">
    <div class="profile-card">
      <div class="profile-header">
        <img :src="profileImage" alt="User Image" class="profile-pic" />
      </div>
      <div class="profile-info">
        <div class="first-part">
          <div class="field-group">
            <label>Username</label>
            <input :disabled="!editMode" v-model="username" />
          </div>
          <div class="field-group">
            <label>Nombre</label>
            <input :disabled="!editMode" v-model="firstName" />
          </div>
          <div class="field-group">
            <label>Apellido</label>
            <input :disabled="!editMode" v-model="lastName" />
          </div>
          <div class="field-group">
            <label>Email</label>
            <input :disabled="!editMode" v-model="email" />
          </div>
        </div>
        <div class="second-part">
          <div class="field-group">
            <label>Contraseña</label>
            <input type="password" :disabled="!editMode" v-model="password" />
          </div>
          <div class="field-group">
            <label>Rol</label>
            <input v-model="roleText" disabled />
          </div>
          <div class="field-group">
            <label>Verificación</label>
            <div class="verification-options">
              <label
                ><input
                  type="radio"
                  v-model="verification"
                  value="true"
                  :disabled="!editMode"
                />
                Sí</label
              >
              <label
                ><input
                  type="radio"
                  v-model="verification"
                  value="false"
                  :disabled="!editMode"
                />
                No</label
              >
            </div>
          </div>
          <div class="field-group">
            <label>Fecha de Creación</label>
            <input v-model="creationDate" disabled />
          </div>
          <i v-if="!editMode" class="fas fa-pencil-alt" @click="toggleEdit"></i>
          <div v-if="editMode" class="action-buttons">
            <button @click="cancelEdit" class="btn-cancel">Cancelar</button>
            <button @click="updateProfile" class="btn-submit">
              Actualizar
            </button>
          </div>
        </div>
      </div>
      <br />
      <div>
        <div
          class="d-flex justify-content-between align-items-center"
          v-if="!activation"
        >
          <div class="d-flex align-items-center">
            <Icon
              icon="mdi:account-alert"
              width="25"
              height="25"
              style="color: red"
            />
            <span class="ms-2">Su cuenta no se encuentra verificada</span>
          </div>
          <MaterialButton
          variant="gradient"
          color="info"
          data-bs-toggle="modal"
          data-bs-target="#exampleModal">
            Verificar Cuenta
          </MaterialButton>
          <!-- Modal -->
          <div
            class="modal fade"
            id="exampleModal"
            tabindex="-1"
            aria-labelledby="exampleModalLabel"
            aria-hidden="true"
          >
            <div class="modal-dialog">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title" id="exampleModalLabel">
                    Verificación de cuenta
                  </h5>
                  <MaterialButton
                    color="none"
                    class="btn-close"
                    data-bs-dismiss="modal"
                    aria-label="Close"
                  >
                  </MaterialButton>
                </div>
                <div class="modal-body">
                  Estimado usuario, <br>

Se le envio una confirmacion de activacion de cuenta a la dirección de correo electrónico asociada con su perfil. Una vez recibido su correo de activacion podra acceder a todas las funcionalidades de la plataforma.

Le recomendamos revisar su bandeja de entrada. <br> 
Al hacer clic en "Continuar", se enviará el mensaje de activacion a su correo. Si desea cancelar este proceso, presione el botón "Cancelar".
                </div>
                <div class="modal-footer justify-content-between">
                  <MaterialButton
                    variant="gradient"
                    color="dark"
                    data-bs-dismiss="modal"

                  >
                    Cancelar
                  </MaterialButton>
                  <MaterialButton
                    variant="gradient"
                    color="success"
                    class="mb-0"
                    @click="profileActivation"
                    data-bs-dismiss="modal"

                  >
                    Continuar
                  </MaterialButton>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="d-flex justify-content-between align-items-center" v-else>
          <div class="d-flex align-items-center">
            <Icon
              icon="mdi:account-check"
              width="25"
              height="25"
              style="color: green"
            />
            <span class="ms-2">Su cuenta se encuentra verificada</span>
          </div>
        </div>
      </div>
    </div>
  </div>
 

</template>

<script>
import NavbarDefault from "../examples/navbars/NavbarDefault.vue";
import Swal from "sweetalert2";
import axios from "axios";
import { ref, onMounted, computed } from "vue";
import { useAppStore } from "@/stores"; // Pinia store
import { Icon } from "@iconify/vue";
import MaterialButton from "@/components/MaterialButton.vue";
import { AuthService } from "../services/authService";
import { Modal } from 'bootstrap'; // Importa Bootstrap Modal para cerrarlo programáticamente


export default {
  components: {
    NavbarDefault,
    Icon,
    MaterialButton,
  },
  setup() {
    const appStore = useAppStore();
    const identificador = appStore.getIdentificador;

    // Definir las propiedades reactivas
    const username = ref("");
    const firstName = ref("");
    const lastName = ref("");
    const email = ref("");
    const role = ref("");
    const creationDate = ref("");
    const verification = ref(false);
    const activation = ref(false);
    const editMode = ref(false); // Usar ref para el modo de edición
    const password = ref(""); // Nueva propiedad para la contraseña

    // Función para obtener los datos del usuario
    const getUserData = async () => {
      try {
        const response = await axios.get(
          `http://localhost:9999/api/v1/user/${identificador}`
        );
        const userData = response.data.result;

        username.value = userData.username;
        firstName.value = userData.firstName;
        lastName.value = userData.lastName;
        email.value = userData.email;
        role.value = userData.role;
        creationDate.value = userData.dateJoin;
        verification.value = userData.verification;
        activation.value = userData.activation;
        password.value = "";

      } catch (error) {
        console.error("Error al obtener los datos del usuario:", error);
        Swal.fire(
          "Error",
          "No se pudo cargar la información del usuario.",
          "error"
        );
      }
    };

    // Función para actualizar el perfil
    const updateProfile = async () => {
      try {
        // Usar la nueva contraseña si fue cambiada, si no mantener la original
        const updatedPassword = password.value
          ? password.value
          : null;

        // Preparar el payload para la solicitud PUT
        const payload = {
          username: username.value,
          firstName: firstName.value,
          lastName: lastName.value,
          email: email.value,
          password: updatedPassword, // Actualizar con la nueva o mantener la original
          verification: verification.value, // Asegurar que sea booleano
          activation: activation.value,
        };

        console.log("VERIFICACION: " + verification.value);

        // Hacer la solicitud PUT para actualizar el perfil
        await axios.put(
          `http://localhost:9999/api/v1/user/${identificador}`,
          payload
        );

        Swal.fire("Éxito", "Perfil actualizado correctamente.", "success");
        editMode.value = false; // Desactivar el modo de edición después de actualizar
      } catch (error) {
        console.error("Error al actualizar el perfil:", error);
        Swal.fire("Error", "No se pudo actualizar el perfil.", "error");
      }
    };


    // Función para activar el modo de edición
    const toggleEdit = () => {
      editMode.value = true;
    };

    // Función para cancelar la edición
    const cancelEdit = () => {
      editMode.value = false;
    };

    const profileActivation = async () =>{
      try {
        // Usar la nueva contraseña si fue cambiada, si no mantener la original
        const updatedPassword = password.value
          ? password.value
          : null;

        // Preparar el payload para la solicitud PUT
        const payload = {
          username: username.value,
          firstName: firstName.value,
          lastName: lastName.value,
          email: email.value,
          password: updatedPassword, // Actualizar con la nueva o mantener la original
          verification: verification.value, // Asegurar que sea booleano
          activation: true,
        };

        console.log("VERIFICACION: " + verification.value);

        await AuthService.sendActivation(email.value);
        // Hacer la solicitud PUT para actualizar el perfil
        await axios.put(
          `http://localhost:9999/api/v1/user/${identificador}`,
          payload
        );


        // Mostrar el SweetAlert cuando se complete el proceso
        Swal.fire("Éxito", "Proceso de activación completado.", "success").then(() => {
          // Cierra el modal programáticamente
          const modalElement = document.getElementById('exampleModal');
          const modal = Modal.getInstance(modalElement); // Obtener la instancia del modal
          modal.hide(); // Cerrar el modal
        });      } catch (error) {
        console.error("Error al actualizar el perfil:", error);
      }
    }

    const roleText = computed(() => {
      return role.value === 1
        ? "Estudiante"
        : role.value === 2
        ? "Docente"
        : "Otro";
    });

    // Cargar los datos del usuario al montar el componente
    onMounted(() => {
      if (identificador) {
        getUserData();
      }
    });

    return {
      identificador,
      firstName,
      username,
      lastName,
      email,
      roleText,
      creationDate,
      verification,
      activation,
      password, 
      editMode,
      toggleEdit,
      cancelEdit,
      updateProfile,
      profileActivation,
    };
  },
  data() {
    return {
      profileImage:
        "https://img.freepik.com/vector-premium/icono-circulo-usuario-anonimo-ilustracion-vector-estilo-plano-sombra_520826-1931.jpg",
      userImage: new URL("@/assets/img/dg1.jpg", import.meta.url).href,
    };
  },
};
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  height: 145vh;
  background-size: cover;
  background-position: center;
  background-color: #ccc;
  padding: 15px;
}

.profile-card {
  background-color: rgba(255, 255, 255, 0.9);
  padding: 2.5rem;
  margin-top: 50px;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  width: 80%;
  max-width: 750px;
  height: fit-content;
}

.profile-info {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex-wrap: wrap;
}

.profile-header {
  text-align: center;
}

.profile-pic {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  background-color: #f0f0f0;
}

.field-group {
  width: 100%;
  margin-bottom: 1rem;
}

.field-group input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.verification-options label {
  margin-right: 10px;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 1.5rem;
}

.btn-cancel {
  background-color: #dc3545;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-submit {
  background-color: #28a745;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.field-group i {
  cursor: pointer;
  color: #007bff;
  margin-left: 10px;
}

@media (max-width: 768px) {
  .profile-info {
    flex-direction: column;
  }

  .first-part,
  .second-part {
    width: 100%;
  }

  .action-buttons {
    flex-direction: column;
    gap: 10px;
  }
}

</style>

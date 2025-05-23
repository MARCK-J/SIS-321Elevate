<template>
  <div class="container mt-4">
    <Breadcrumbs :routes="breadcrumbs" />
    <h2 class="mb-4">Administrador de Usuarios y Roles</h2>
    <div class="mb-3 text-end">
      <button class="btn btn-primary me-2" @click="abrirModalCrearUsuario">Crear Usuario</button>
      <button class="btn btn-primary me-2" @click="abrirModalCrearRol">Crear Nuevo Rol</button>
      <router-link to="/admin/logs" class="btn btn-outline-secondary">
        Ver Logs de Seguridad
      </router-link>
    </div>
    <div class="card mb-4">
      <div class="card-header">Usuarios</div>
      <div class="card-body p-0">
        <table class="table table-hover mb-0">
          <thead>
            <tr>
              <th>Usuario</th>
              <th>Email</th>
              <th>Rol</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="usuario in usuarios" :key="usuario.userId">
              <td>{{ usuario.firstName }} {{ usuario.lastName }}</td>
              <td>{{ usuario.email }}</td>
              <td>
                <select v-model="usuario.role" @change="asignarRol(usuario)" class="form-select form-select-sm">
                  <option v-for="rol in roles" :key="rol.id" :value="rol.id">{{ rol.nombre }}</option>
                </select>
              </td>
              <td>
                <button class="btn btn-sm btn-warning me-2" @click="abrirModalEditarUsuario(usuario)">Editar</button>
                <button class="btn btn-sm btn-danger" @click="eliminarUsuario(usuario)">Eliminar</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div class="card">
      <div class="card-header">Roles</div>
      <div class="card-body p-0">
        <table class="table table-hover mb-0">
          <thead>
            <tr>
              <th>Rol</th>
              <th>Descripción</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="rol in roles" :key="rol.id">
              <td>{{ rol.nombre }}</td>
              <td>{{ rol.descripcion }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal Crear/Editar Usuario -->
    <div class="modal fade" tabindex="-1" :class="{ show: showModalUsuario }" style="display: block;" v-if="showModalUsuario">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ usuarioEditando ? 'Editar Usuario' : 'Crear Usuario' }}</h5>
            <button type="button" class="btn-close" @click="cerrarModalUsuario"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Nombre</label>
              <input v-model="formUsuario.nombre" type="text" class="form-control" />
            </div>
            <div class="mb-3">
              <label class="form-label">Apellido</label>
              <input v-model="formUsuario.apellido" type="text" class="form-control" />
            </div>
            <div class="mb-3">
              <label class="form-label">Email</label>
              <input v-model="formUsuario.email" type="email" class="form-control" />
            </div>
            <div class="mb-3">
              <label class="form-label">Nombre de usuario</label>
              <input v-model="formUsuario.username" type="text" class="form-control" />
            </div>
            <div class="mb-3">
              <label class="form-label">Contraseña</label>
              <input v-model="formUsuario.password" type="password" class="form-control" :required="!usuarioEditando"/>
            </div>
            <div class="mb-3">
              <label class="form-label">Rol</label>
              <select v-model="formUsuario.rolId" class="form-select">
                <option v-for="rol in roles" :key="rol.id" :value="rol.id">{{ rol.nombre }}</option>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="cerrarModalUsuario">Cancelar</button>
            <button class="btn btn-primary" @click="guardarUsuario">{{ usuarioEditando ? 'Guardar Cambios' : 'Crear Usuario' }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import Swal from 'sweetalert2'
import Breadcrumbs from '@/examples/Breadcrumbs.vue'

const breadcrumbs = [
  { route: '/', label: 'Inicio' },
  { route: '/admin/usuarios', label: 'Administrador de Usuarios' }
]

const usuarios = ref([])
const roles = ref([
  { id: 1, nombre: 'Estudiante', descripcion: 'Acceso a cursos' },
  { id: 2, nombre: 'Docente', descripcion: 'Gestión de cursos' },
  { id: 3, nombre: 'Admin Página', descripcion: 'Administrador de Página' },
  { id: 4, nombre: 'Admin Usuarios', descripcion: 'Administrador de Usuarios' }
])

const showModalUsuario = ref(false)
const usuarioEditando = ref(null)
const formUsuario = ref({
  nombre: '',
  apellido: '',
  email: '',
  username: '',
  password: '',
  rolId: 1
})

onMounted(() => {
  cargarUsuarios()
})

async function cargarUsuarios() {
  try {
    const response = await axios.get('http://localhost:9999/api/v1/user/all')
    if (response.data.code === "200-OK") {
      usuarios.value = response.data.result
    } else {
      Swal.fire('Error', 'No se pudieron cargar los usuarios', 'error')
    }
  } catch (error) {
    Swal.fire('Error', 'No se pudieron cargar los usuarios', 'error')
  }
}

function abrirModalCrearUsuario() {
  usuarioEditando.value = null
  formUsuario.value = {
    nombre: '',
    apellido: '',
    email: '',
    username: '',
    password: '',
    rolId: 1
  }
  showModalUsuario.value = true
}
function abrirModalEditarUsuario(usuario) {
  usuarioEditando.value = usuario
  formUsuario.value = {
    nombre: usuario.firstName,
    apellido: usuario.lastName || '',
    email: usuario.email,
    username: usuario.username || usuario.email.split('@')[0],
    password: '',
    rolId: usuario.role
  }
  showModalUsuario.value = true
}
function cerrarModalUsuario() {
  showModalUsuario.value = false
}

async function guardarUsuario() {
  if (
    !formUsuario.value.nombre ||
    !formUsuario.value.apellido ||
    !formUsuario.value.email ||
    !formUsuario.value.username ||
    (!usuarioEditando.value && !formUsuario.value.password)
  ) {
    Swal.fire('Error', 'Todos los campos son obligatorios', 'error')
    return
  }
  const rolId = Number(formUsuario.value.rolId)
  const payload = {
    firstName: formUsuario.value.nombre,
    lastName: formUsuario.value.apellido,
    email: formUsuario.value.email,
    username: formUsuario.value.username,
    password: formUsuario.value.password,
    role: rolId,
    dateJoin: new Date().toISOString().split('T')[0]
  }
  try {
    if (usuarioEditando.value) {
      // Modificar usuario existente
      await axios.put(
        `http://localhost:9999/api/v1/user/${usuarioEditando.value.userId}`,
        payload
      )
      Swal.fire('Éxito', 'Usuario actualizado correctamente', 'success')
    } else {
      // Crear usuario
      if (rolId === 3 || rolId === 4) {
        // Crear admin usando endpoint especial
        const userData = JSON.parse(localStorage.getItem('user'))
        const adminUserId = userData?.userId || userData?.id
        if (!adminUserId) {
          Swal.fire('Error', 'No se pudo obtener el ID del administrador actual', 'error')
          return
        }
        await axios.post(
          'http://localhost:9999/api/v1/user/admin/create',
          payload,
          {
            headers: {
              "adminUserId": String(adminUserId),
              "Content-Type": "application/json",
              Accept: "application/json"
            }
          }
        )
        Swal.fire('Éxito', 'Administrador creado correctamente', 'success')
      } else {
        // Crear estudiante/docente usando endpoint normal
        await axios.post(
          'http://localhost:9999/api/v1/user/signup',
          payload,
          {
            headers: {
              "Content-Type": "application/json",
              Accept: "application/json"
            }
          }
        )
        Swal.fire('Éxito', 'Usuario creado correctamente', 'success')
      }
    }
    cerrarModalUsuario()
    cargarUsuarios()
  } catch (error) {
    Swal.fire('Error', error.response?.data?.message || 'No se pudo guardar el usuario', 'error')
  }
}

async function eliminarUsuario(usuario) {
  Swal.fire({
    title: '¿Estás seguro?',
    text: `¿Deseas eliminar al usuario ${usuario.firstName} ${usuario.lastName}?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'Cancelar'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        await axios.delete(`http://localhost:9999/api/v1/user/${usuario.userId}`)
        Swal.fire('Eliminado', 'Usuario eliminado correctamente', 'success')
        cargarUsuarios()
      } catch (error) {
        Swal.fire('Error', error.response?.data?.message || 'No se pudo eliminar el usuario', 'error')
      }
    }
  })
}

async function asignarRol(usuario) {
  try {
    const payload = {
      ...usuario,
      role: usuario.role
    }
    await axios.put(`http://localhost:9999/api/v1/user/${usuario.userId}`, payload)
    Swal.fire('Éxito', 'Rol actualizado correctamente', 'success')
    cargarUsuarios()
  } catch (error) {
    Swal.fire('Error', error.response?.data?.message || 'No se pudo actualizar el rol', 'error')
  }
}
</script>

<style scoped>
.modal {
  display: block;
  background: rgba(0,0,0,0.3);
}
.modal.fade:not(.show) {
  display: none;
}
</style>
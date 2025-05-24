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
                <select v-model="usuario.roleId" @change="asignarRol(usuario)" class="form-select form-select-sm">
                  <option v-for="rol in roles" :key="rol.id" :value="rol.id">{{ rol.nombre }}</option>
                </select>
                <div v-if="usuario.role && usuario.role.name" class="mt-1 text-muted small">
                  Actual: {{ usuario.role.name }}
                </div>
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
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="rol in roles" :key="rol.id">
              <td>{{ rol.nombre }}</td>
              <td>{{ rol.descripcion }}</td>
              <td>
                <button class="btn btn-sm btn-warning me-2" @click="abrirModalEditarRol(rol)">Editar</button>
                <button class="btn btn-sm btn-danger" @click="eliminarRol(rol)">Eliminar</button>
              </td>
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
              <input v-model="formUsuario.email" type="email" class="form-control" @input="syncUsernameWithEmail" />
            </div>
            <div class="mb-3">
              <label class="form-label">Nombre de usuario</label>
              <input :value="formUsuario.email" type="text" class="form-control" disabled />
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

    <!-- Modal Crear/Editar Rol -->
    <div class="modal fade" tabindex="-1" :class="{ show: showModalRol }" style="display: block;" v-if="showModalRol">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ rolEditando ? 'Editar Rol' : 'Crear Rol' }}</h5>
            <button type="button" class="btn-close" @click="cerrarModalRol"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Nombre</label>
              <input v-model="formRol.nombre" type="text" class="form-control" />
            </div>
            <div class="mb-3">
              <label class="form-label">Descripción</label>
              <input v-model="formRol.descripcion" type="text" class="form-control" />
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="cerrarModalRol">Cancelar</button>
            <button class="btn btn-primary" @click="guardarRol">{{ rolEditando ? 'Guardar Cambios' : 'Crear Rol' }}</button>
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
const roles = ref([])

const showModalUsuario = ref(false)
const usuarioEditando = ref(null)
const formUsuario = ref({
  nombre: '',
  apellido: '',
  email: '',
  password: '',
  rolId: ''
})

const showModalRol = ref(false)
const rolEditando = ref(null)
const formRol = ref({
  nombre: '',
  descripcion: ''
})

onMounted(() => {
  cargarRoles()
  cargarUsuarios()
})

async function cargarRoles() {
  try {
    const response = await axios.get('http://localhost:9999/api/v1/roles/all')
    const rolesArray = response.data?.result?.result || response.data?.result || []
    roles.value = rolesArray.map(r => ({
      id: r.roleId,
      nombre: r.name,
      descripcion: r.description
    }))
  } catch (error) {
    Swal.fire('Error', 'No se pudieron cargar los roles', 'error')
  }
}

async function cargarUsuarios() {
  try {
    const response = await axios.get('http://localhost:9999/api/v1/user/all')
    if (response.data.code === "200-OK") {
      usuarios.value = response.data.result.map(u => ({
        ...u,
        roleId: u.role?.roleId || '', // para selects
      }))
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
    password: '',
    rolId: roles.value.length > 0 ? roles.value[0].id : ''
  }
  showModalUsuario.value = true
}
function abrirModalEditarUsuario(usuario) {
  usuarioEditando.value = usuario
  formUsuario.value = {
    nombre: usuario.firstName,
    apellido: usuario.lastName || '',
    email: usuario.email,
    password: '',
    rolId: usuario.role?.roleId || ''
  }
  showModalUsuario.value = true
}
function cerrarModalUsuario() {
  showModalUsuario.value = false
}

// Sincroniza el nombre de usuario con el email (aunque el campo está deshabilitado)
function syncUsernameWithEmail() {
  // No es necesario porque el campo username está deshabilitado y siempre igual a email
}

async function guardarUsuario() {
  if (
    !formUsuario.value.nombre ||
    !formUsuario.value.apellido ||
    !formUsuario.value.email ||
    (!usuarioEditando.value && !formUsuario.value.password) ||
    !formUsuario.value.rolId
  ) {
    Swal.fire('Error', 'Todos los campos son obligatorios', 'error')
    return
  }
  const rolId = Number(formUsuario.value.rolId)
  const selectedRole = roles.value.find(r => r.id === rolId)
  const payload = {
    firstName: formUsuario.value.nombre,
    lastName: formUsuario.value.apellido,
    email: formUsuario.value.email,
    username: formUsuario.value.email, // SIEMPRE igual al email
    password: formUsuario.value.password,
    role: { roleId: rolId, name: selectedRole?.nombre },
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
          `http://localhost:9999/api/v1/user/admin/create?roleId=${rolId}`,
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
          `http://localhost:9999/api/v1/user/signup?roleId=${rolId}`,
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
    const selectedRole = roles.value.find(r => r.id === Number(usuario.roleId))
    const payload = {
      ...usuario,
      username: usuario.email, // SIEMPRE igual al email
      role: { roleId: selectedRole.id, name: selectedRole.nombre }
    }
    await axios.put(`http://localhost:9999/api/v1/user/${usuario.userId}`, payload)
    Swal.fire('Éxito', 'Rol actualizado correctamente', 'success')
    cargarUsuarios()
  } catch (error) {
    Swal.fire('Error', error.response?.data?.message || 'No se pudo actualizar el rol', 'error')
  }
}

// --- Roles CRUD ---
function abrirModalCrearRol() {
  rolEditando.value = null
  formRol.value = {
    nombre: '',
    descripcion: ''
  }
  showModalRol.value = true
}
function abrirModalEditarRol(rol) {
  rolEditando.value = rol
  formRol.value = {
    nombre: rol.nombre,
    descripcion: rol.descripcion
  }
  showModalRol.value = true
}
function cerrarModalRol() {
  showModalRol.value = false
}

async function guardarRol() {
  if (!formRol.value.nombre || !formRol.value.descripcion) {
    Swal.fire('Error', 'Todos los campos son obligatorios', 'error')
    return
  }
  try {
    if (rolEditando.value) {
      // Modificar rol existente
      await axios.put(
        `http://localhost:9999/api/v1/roles/${rolEditando.value.id}`,
        {
          name: formRol.value.nombre,
          description: formRol.value.descripcion,
          creationDate: new Date().toISOString().split('T')[0]
        }
      )
      Swal.fire('Éxito', 'Rol actualizado correctamente', 'success')
    } else {
      // Crear rol
      await axios.post(
        `http://localhost:9999/api/v1/roles/create`,
        {
          name: formRol.value.nombre,
          description: formRol.value.descripcion,
          creationDate: new Date().toISOString().split('T')[0]
        }
      )
      Swal.fire('Éxito', 'Rol creado correctamente', 'success')
    }
    cerrarModalRol()
    cargarRoles()
  } catch (error) {
    Swal.fire('Error', error.response?.data?.message || 'No se pudo guardar el rol', 'error')
  }
}

async function eliminarRol(rol) {
  Swal.fire({
    title: '¿Estás seguro?',
    text: `¿Deseas eliminar el rol ${rol.nombre}?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'Cancelar'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        await axios.delete(`http://localhost:9999/api/v1/roles/${rol.id}`)
        Swal.fire('Eliminado', 'Rol eliminado correctamente', 'success')
        cargarRoles()
      } catch (error) {
        Swal.fire('Error', error.response?.data?.message || 'No se pudo eliminar el rol', 'error')
      }
    }
  })
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
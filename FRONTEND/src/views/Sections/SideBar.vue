<template>
  <div>
    <div class="sidebar-container" :class="{ collapsed: isCollapsed }">
      <button class="toggle-sidebar-button" @click="toggleCollapse">
        <span v-if="isCollapsed">☰</span>
        <span v-else>☰</span>
      </button>
      <nav class="sidebar" :style="{ width: isCollapsed ? '5%' : '20%' }">
        <ul class="nav flex-column">
          <!-- Perfil -->
          <li class="nav-item">
            <div class="nav-link" @click="selectOption('profile')">
              <span v-if="isCollapsed">👤</span>
              <span v-else>👤 Perfil</span>
            </div>
          </li>

          <!-- Mostrar estas opciones solo si el identificador es 2 (Docente) -->
          <template v-if="identificador == '2'">
            <li class="nav-item">
              <div class="nav-link" @click="selectOption('create_courses')">
                <span v-if="isCollapsed">📚</span>
                <span v-else>📚 Crear Cursos</span>
              </div>
            </li>
          </template>

          <li class="nav-item">
            <div class="nav-link" @click="selectOption('my_courses')">
              <span v-if="isCollapsed">🎓</span>
              <span v-else>🎓 Mis Cursos</span>
            </div>
          </li>
          <!-- Favoritos -->
          <template v-if="identificador == '1'">
            <li class="nav-item">
              <div class="nav-link" @click="selectOption('my_courses_favorites')">
                <span v-if="isCollapsed">⭐</span>
                <span v-else>⭐ Favoritos</span>
              </div>
            </li>
          </template>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from "vue";
import { useAppStore } from "../../stores";

export default defineComponent({
  name: "Sidebar",
  emits: ["optionSelected"],
  setup(props, { emit }) {
    const isCollapsed = ref(true);

    // Acceder al store de Pinia para obtener el identificador de tipo de persona
    const appStore = useAppStore();
    const identificador = appStore.tipoPersona;
    console.log("Identificador de rol: " + identificador);

    const toggleCollapse = () => {
      isCollapsed.value = !isCollapsed.value;
    };

    const selectOption = (option) => {
      emit("optionSelected", option);
    };

    return {
      isCollapsed,
      toggleCollapse,
      selectOption,
      identificador, // Exponer el identificador del store
    };
  },
});
</script>

<style scoped>
.sidebar-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  transition: width 0.3s ease;
}

.sidebar-container.collapsed .sidebar {
  width: 60px;
}

.sidebar {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
}

.nav {
  width: 100%;
}

.nav-item {
  width: 100%;
}

.nav-link {
  color: #000;
  cursor: pointer;
}

.nav-link:hover {
  color: #939292;
}

.toggle-sidebar-button {
  background-color: rgb(84, 84, 239);
  border: 1px solid black;
  margin: 10px 20px;
  cursor: pointer;
}

.toggle-sidebar-button:hover {
  background-color: rgb(50, 50, 150);
}

.toggle-sidebar-button span {
  color: white;
}
</style>

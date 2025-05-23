<template>
  <BaseLayout
    title="Listado de Cursos"
    :breadcrumb="[ { label: 'Cursos', route: '/' }, { label: 'Busqueda de cursos' } ]"
  >
    <div class="">
      <div class="row justify-space-between py-0">
        <div class="input-group">
          <span class="input-group-text">
            <i class="fas fa-search" aria-hidden="true"></i>
          </span>
          <input
            v-model="searchQuery"
            :type="inputType"
            class="form-control input-group-dynamic mb-2"
            :placeholder="inputPlaceholder"
            required
          />
        </div>
      </div>

      <div class="col-4 d-flex align-items-center">
        <MaterialButton
          variant="gradient"
          color="primary"
          @click="executeSearch"
        >
          Buscar
        </MaterialButton>
      </div>

      <div class="col-12 mt-3">
        <div class="dropdown" @mouseleave="closeDropdown">
          <MaterialButton
            variant="gradient"
            color="success"
            class="dropdown-toggle"
            id="dropdownMenuButton"
            @click="showDropdown = !showDropdown"
            aria-expanded="showDropdown"
          >
            Filtrar por:
            {{
              selectedFilter.charAt(0).toUpperCase() + selectedFilter.slice(1)
            }}
          </MaterialButton>

          <ul
            class="dropdown-menu px-2 py-3"
            :class="{ show: showDropdown }"
            aria-labelledby="dropdownMenuButton"
          >
            <li>
              <a
                class="dropdown-item"
                @click="
                  selectedFilter = 'title';
                  closeDropdown();
                "
                >Título</a
              >
            </li>
            <li>
              <a
                class="dropdown-item"
                @click="
                  selectedFilter = 'rating';
                  closeDropdown();
                "
                >Puntuación</a
              >
            </li>
            <li>
              <a
                class="dropdown-item"
                @click="
                  selectedFilter = 'duration';
                  closeDropdown();
                "
                >Duración</a
              >
            </li>
          </ul>
        </div>
      </div>
    </div>

    <div class="row">
      <div
        class="col-md-6 mb-4"
        v-for="course in courses"
        :key="course.courseId"
      >
        <RouterLink :to="{ name: 'course', query: { courseId: course.courseId, title: course.title } }">
          <div class="card h-100">
            <div class="row g-0">
              <div class="col-md-4 d-flex align-items-center">
                <img
                  :src="course.image"
                  class="img-fluid rounded-start mx-auto"
                  alt="Imagen del curso"
                />
              </div>
              <div class="col-md-8">
                <div class="card-body">
                  <h5 class="card-title">{{ course.title }}</h5>
                  <p class="card-text text-truncate">{{ course.description }}</p>
                  <p class="card-text">
                    <small class="text-muted">
                      Disponibilidad:
                      <span v-if="course.available" class="text-success"
                        >Disponible</span
                      >
                      <span v-else class="text-danger">No disponible</span>
                    </small>
                  </p>
                  <p class="card-text">
                    <strong>Habilidades:</strong>
                    {{ course.abilities.split(";").join(", ") }}
                  </p>
                  <p class="card-text">
                    <strong>Categoría:</strong> {{ course.categoryName }}
                  </p>
                  <p class="card-text">
                    <strong>Puntuación:</strong> {{ course.rating }}
                  </p>
                  <p class="card-text">
                    <strong>Duración:</strong> {{ course.duration }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </RouterLink>
      </div>
    </div>

    <!-- Paginación -->
    <div class="pagination-container">
      <button @click="prevPage" :disabled="currentPage === 0">Anterior</button>
      <span>Página {{ currentPage + 1 }} de {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage >= totalPages - 1">Siguiente</button>
    </div>
  </BaseLayout>
</template>

<script>
import axios from "axios";
import Swal from "sweetalert2";
import BaseLayout from "../layouts/sections/components/BaseLayout.vue";
import MaterialInput from "@/components/MaterialInput.vue";
import MaterialButton from "@/components/MaterialButton.vue";
import { RouterLink } from "vue-router";

export default {
  components: {
    BaseLayout,
    MaterialInput,
    MaterialButton,
    RouterLink,
  },
  data() {
    return {
      searchQuery: "",
      selectedFilter: "title",
      showDropdown: false,
      courses: [],
      categories: [],
      itemsPerPage: 10,
      currentPage: 0,
      totalPages: 0,
    };
  },
  computed: {
    inputType() {
      return this.selectedFilter === "rating" ? "number" : "text";
    },
    inputPlaceholder() {
      if (this.selectedFilter === "title") {
        return "Buscar por título";
      } else if (this.selectedFilter === "rating") {
        return "Buscar por puntuación";
      } else if (this.selectedFilter === "duration") {
        return "Buscar por duración";
      }
      return "Buscar";
    },
  },
  methods: {
    async fetchCategories() {
      try {
        const response = await axios.get("http://localhost:9999/api/v1/category/all", {
          headers: {
            Accept: "application/json",
          },
        });
        if (response.data.code === "200-OK") {
          this.categories = response.data.result;
        } else {
          console.error("Error al obtener categorías:", response.data.message);
        }
      } catch (error) {
        console.error("Error en la solicitud de categorías:", error);
      }
    },
    async fetchCourses(page = 0) {
      try {
        let response;
        const query = this.searchQuery.toLowerCase();
        const filter = this.selectedFilter;
        if (filter === "title") {
          response = await axios.get(`http://localhost:9999/api/v1/courses/title?title=${query}&page=${page}&size=${this.itemsPerPage}&sort=title`, {
            headers: {
              Accept: "application/json",
            },
          });
        } else if (filter === "rating") {
          response = await axios.get(`http://localhost:9999/api/v1/courses/rating?rating=${query}&page=${page}&size=${this.itemsPerPage}&sort=rating`, {
            headers: {
              Accept: "application/json",
            },
          });
        } else if (filter === "duration") {
          response = await axios.get(`http://localhost:9999/api/v1/courses/duration?duration=${query}&page=${page}&size=${this.itemsPerPage}&sort=duration`, {
            headers: {
              Accept: "application/json",
            },
          });
        }
        if (response.data.code === "200-OK") {
          const courses = response.data.result.content;
          this.courses = courses.map(course => {
            const category = this.categories.find(cat => cat.id === course.categoryCourseId);
            return {
              ...course,
              categoryName: category ? category.nameCategory : "Sin categoría",
            };
          });
          this.totalPages = response.data.result.totalPages;
          this.currentPage = response.data.result.currentPage;
        } else {
          console.error("Error al obtener cursos:", response.data.message);
        }
      } catch (error) {
        console.error("Error en la solicitud de cursos:", error);
      }
    },
    async executeSearch() {
      this.currentPage = 0;
      await this.fetchCourses(this.currentPage);
    },
    closeDropdown() {
      this.showDropdown = false;
    },
    nextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++;
        this.fetchCourses(this.currentPage);
      }
    },
    prevPage() {
      if (this.currentPage > 0) {
        this.currentPage--;
        this.fetchCourses(this.currentPage);
      }
    },
  },
  async mounted() {
    await this.fetchCategories();
    this.fetchCourses();
  },
};
</script>

<style scoped>
.text-truncate {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.card {
  height: 300px; /* Altura específica para las tarjetas */
  overflow: hidden; /* Ocultar el contenido que se desborda */
  background-color: bisque;
}

.card-body {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination-container button {
  padding: 10px 20px;
  margin: 0 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.pagination-container button:disabled {
  cursor: not-allowed;
  background-color: #cccccc;
}

.pagination-container span {
  font-size: 1rem;
}
</style>
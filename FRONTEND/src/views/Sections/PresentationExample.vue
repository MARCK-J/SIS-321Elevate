  <script setup>
  import { ref, onMounted } from "vue";
  import ExampleCard from "../../components/ExampleCard.vue";
  import MaterialBadge from "../../components/MaterialBadge.vue";
  import axios from "axios";
  import DefaultImage from "@/assets/img/defaultCourse.jpg";
  import { useRouter } from 'vue-router'; // Importar useRouter

  // Estado para almacenar los datos
  const data = ref([]);
  const router = useRouter(); // Crear instancia de router

  // Función para obtener las categorías desde el endpoint
  const fetchCategories = async () => {
    try {
      const response = await axios.get("http://localhost:9999/api/v1/category/all");
      if (response.data.code === "200-OK") {
        return response.data.result; // Devuelve las categorías
      } else {
        console.error("Error al obtener las categorías:", response.data.message);
        return [];
      }
    } catch (error) {
      console.error("Error en la solicitud de categorías:", error);
      return [];
    }
  };

  // Función para obtener los cursos desde el endpoint
  const fetchCourses = async () => {
    try {
      const response = await axios.get("http://localhost:9999/api/v1/courses/all");
      if (response.data.code === "200-OK") {
        return response.data.result; // Devuelve los cursos
      } else {
        console.error("Error al obtener los cursos:", response.data.message);
        return [];
      }
    } catch (error) {
      console.error("Error en la solicitud de cursos:", error);
      return [];
    }
  };

  // Función para generar el contenido dinámicamente
  const generateContent = async () => {
    const categories = await fetchCategories();
    const courses = await fetchCourses();
    

    // Mapa para organizar los cursos por categoría
    const categoryMap = {};

    // Agrupar los cursos por categoryCourseId
    courses.forEach(course => {
      const categoryId = course.categoryCourseId;
      if (!categoryMap[categoryId]) {
        categoryMap[categoryId] = [];
      }
      categoryMap[categoryId].push({
        id: course.courseId, // Agregar ID del curso
        image: course.image || DefaultImage, // Usar DefaultImage si no hay imagen
        title: course.title,
        available: course.available,
      });
    });

    // Crear la estructura final para cada categoría con sus cursos
    data.value = categories.map(category => {
      return {
        heading: category.nameCategory,
        items: (categoryMap[category.id] || []).slice(-3) // Obtener los últimos 3 cursos de cada categoría
      };
    });
  };

  // Llamar a la función en el montaje del componente
  onMounted(() => {
    generateContent();
  });
  </script>

  <template>
    <section class="my-5">
      <div class="container">
        <div class="row justify-content-center text-center mb-5">
          <div class="col-lg-8">
            <MaterialBadge color="primary" class="mb-3">
              Explora nuestras colecciones de cursos
            </MaterialBadge>
            <h2 class="text-primary mb-3">Aquí empieza tu preparación para el futuro</h2>
            <p class="lead text-muted">
              Estamos comprometidos con tu educación, ofreciéndote los mejores cursos en un solo lugar.
            </p>
          </div>
        </div>
      </div>
      <div class="container">
        <div
          v-for="({ heading, items }, index) in data"
          :key="heading"
          :class="`row ${index > 0 ? 'pt-lg-2' : ''}`"
        >
          <div :class="`col-lg-4`">
            <div
              class="sticky-top pb-lg-5 pb-3 mt-lg-0 mt-5 px-3"
              style="top: 100px"
            >
              <h3 class="text-dark font-weight-bold mb-3">{{ heading }}</h3>
            </div>
          </div>
          <div :class="`col-lg-8`">
            <div :class="`row ${index > 0 ? 'mt-4' : ''}`">
              <div
                class="col-md-6 col-lg-4 mt-4"
                v-for="{ id, image, title, available } in items"
                :key="id"
                >
                <ExampleCard
                  :title="title"
                  class=" shadow-lg rounded-3 p-3 border border-dark"
                  :image="image"
                  :route="'course'" 
                  :courseId="id "
                  :style="{ width: '225px', height: '125px' }" 
                />
                <p class="text-muted">{{ available ? 'No disponible' : 'Disponible' }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </template>

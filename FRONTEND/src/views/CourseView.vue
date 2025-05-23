<script setup>

// Importar `useRoute` de Vue Router para acceder a los parámetros de la query
import { useRoute } from "vue-router";

// Acceder a la ruta actual
const route = useRoute();

// Extraer `courseId` y `title` de la query
const id = route.query.courseId || 0;
const title = route.query.title || "";

// Importar componentes
import BaseLayout from "../layouts/sections/components/BaseLayout.vue";
import View from "../layouts/sections/components/View.vue";
import CourseStructure from "./Sections/CourseStructure.vue";

</script>

<template>
  <BaseLayout
    :title="title"
    :breadcrumb="[ 
      { label: 'Cursos', route: '/' },
      { label: title },
    ]"
  >
    <View :title=title id="header-1">
      <CourseStructure/>
    </View>
  </BaseLayout>
</template>

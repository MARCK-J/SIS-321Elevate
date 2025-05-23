<script setup>
// Importar `useRoute` de Vue Router para acceder a los parámetros de la query
import { useRoute } from "vue-router";
import { ref, onMounted } from 'vue';

// Acceder a la ruta actual
const route = useRoute();

// Extraer `courseId` y `courseTitle` de la query
const courseId = ref(route.query.courseId || 0);
const courseTitle = ref(route.query.courseTitle || "");

// Verificar los valores de los parámetros
console.log('Este es mi titulo ' + courseTitle.value);
console.log('Este es mi courseId ' + courseId.value);

// Importar componentes
import BaseLayout from "../layouts/sections/components/BaseLayout.vue";
import View from "../layouts/sections/components/View.vue";
import QuizzesStructure from "./Sections/QuizzesStructure.vue";

</script>

<template>
  <BaseLayout
    :title="courseTitle"
    :breadcrumb="[ 
      { label: 'Cursos', route: '/' },
      { label: courseTitle },
      { label: 'Cuestionarios' },
    ]"
  >
    <View title="Cuestionarios del curso" id="header-1">
      <QuizzesStructure :courseId="courseId" :courseTitle="courseTitle"/>
    </View>
  </BaseLayout>
</template>
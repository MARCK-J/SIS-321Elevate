<script setup>
import { ref } from "vue";
import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import CenteredFooter from "@/examples/footers/FooterCentered.vue";
import Breadcrumbs from "@/examples/Breadcrumbs.vue";


// Define props
const props = defineProps({
  breadcrumb: {
    type: Array,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
});
</script>
<template>
  <DefaultNavbar light />
  <div class="container mt-5">
    <div class="row">
      <div class="col-lg-12 mx-auto">
        <div class="mb-4 w-100 w-md-50 ">
          <Breadcrumbs :routes="props.breadcrumb" />
          <h3>{{ props.title }}</h3>
        </div>
        <slot />
      </div>
    </div>
  </div>
  <CenteredFooter />
</template>

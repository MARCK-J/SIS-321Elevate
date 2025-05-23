<script setup>
defineProps({
  route: {
    type: Object,
    required: false,
  },
  color: {
    type: String,
    required: true,
  },
  component: {
    type: String,
    required: true,
  },
  label: {
    type: String,
    default: "",
  },
});
</script>

<template>
  <a :href="route.href" class="btn me-2" :class="`btn-${color}`" target="_blank">
    <i
      class="fab"
      :class="`fa-${component} ${
        $attrs.class == 'btn-icon-only' ? 'me-0' : 'me-1'
      }`"
    ></i>
    {{ label }}
  </a>
</template>
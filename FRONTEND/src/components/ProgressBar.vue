<template>
    <div class="progress-bar-container">
      <div class="progress-bar" :style="{ width: progress + '%' }"></div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      progress: {
        type: Number,
        required: true,
      },
    },
  };
  </script>
  
  <style scoped>
  .progress-bar-container {
    width: 100%;
    background-color: #e0e0e0;
    border-radius: 8px;
    overflow: hidden;
    height: 20px;
    position: relative;
  }
  
  .progress-bar {
    height: 100%;
    background-color: #28a745;
    transition: width 0.3s ease;
  }
  </style>
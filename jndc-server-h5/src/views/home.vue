<!-- src/views/Home.vue -->
<template>
  <div>
    <h1>Home Page</h1>
    <p>Welcome to the Home Page!</p>
    <router-link to="/about">Go to About</router-link>
  </div>
</template>
<script>
export default {
  name: 'Home',
};
</script>

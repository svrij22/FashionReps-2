<template>
  <item-collection-page/>
</template>

<script>
import ItemCollectionPage from "@/components/ItemCollectionPage";

export default {
  name: 'App',
  components: {
    ItemCollectionPage,
  }
}
</script>

<style>
@import "~bootstrap/dist/css/bootstrap.min.css";
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>

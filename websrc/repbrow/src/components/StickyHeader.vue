<template>
  <div class="root">
    <nav class="navbar bd-navbar navbar-light bg-light sticky-top position-sticky">
      <div class="container">
        <button class="form-control form-control-sm" type="button" style="width: 50px" @click="toggleSellerSideBar">
          <img src="../assets/settings_icon_128522.png" style="width: 20px">
        </button>
        <div class="form-group searchbar">
          <input type="search" class="form-control form-control-sm" placeholder="Search" v-on:change="doSearch" v-model="query">
        </div>
        <a class="navbar-brand" href="#">RepBrowser 1.0</a>
        <button class="form-control form-control-sm" type="button" style="width: 50px">
          <img src="../assets/filter_icon_143777.png" style="width: 20px">
        </button>
      </div>
    </nav>
  </div>
</template>

<script>
export default {
  name: "StickyHeader",
  data(){
    return {
      query: ""
    }
  },
  methods:{
    doSearch(){
      this.$emit("searchq", this.query)
    },
    toggleSellerSideBar(){
      this.$emit("toggleseller");
    }
  }
}
</script>

<style scoped>
  .root{
    width: 100%;
    position: fixed;
    top: 0;
    z-index: 1000;
  }

  .searchbar{
    flex-grow: 1;
    margin-left: 10px;
    margin-right: 10px;
  }
  .navbar{
    width: 100%;
    padding-left: 20px;
    padding-right: 20px;
    background-color: maroon!important;
  }

  .bd-navbar{
    box-shadow: 0 0.25rem 0.25rem rgb(0 0 0 / 25%), inset 0 -1px 5px rgb(0 0 0 / 25%);
  }

  a{
    font-size: 17px;
    font-weight: bold;
    color: white!important;
  }
</style>
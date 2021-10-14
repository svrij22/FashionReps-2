<template>
  <sticky-header  v-on:searchq="(q) => this.doSearch(q)" v-on:toggleseller="toggleSellerSideBar"/>
  <div class="content">
    <sidebar v-show="sellerSideBar"/>
    <item-container :data="displayedItems" :isloading="isLoading"/>
  </div>
</template>

<script>
import StickyHeader from "@/components/StickyHeader";
import _ from "lodash";
import ItemContainer from "@/components/ItemContainer";
import Sidebar from "@/components/Sidebar";

export default {
  name: "ItemCollectionPage",
  components: {Sidebar, ItemContainer, StickyHeader},
  data() {
    return {
      data: [],
      page: 0,
      perpage: 50,
      sellerSideBar: true,
      isLoading: true
    }
  },
  computed: {
    displayedItems(){
      this.perpage;
      this.data;
      return _.take(this.data, this.perpage);
    },
  },
  methods: {
    toggleSellerSideBar(){
      this.sellerSideBar = !this.sellerSideBar
    },
    doSearch(query) {
      console.log(query)
      fetch("http://localhost:8070/items/search?" + new URLSearchParams({
        param: query,
      }))
          .then(response => response.json())
          .then(data => {
            this.data = data
            this.isLoading = false;
          });
    },
    getAll() {
      console.log("get")
      fetch("http://localhost:8070/items")
          .then(response => response.json())
          .then(data => {
            this.data = data
            this.isLoading = false;
          });
    },
    atEnd() {
      var c = [document.scrollingElement.scrollHeight, document.body.scrollHeight, document.body.offsetHeight].sort(function (a, b) {
        return b - a
      }) // select longest candidate for scrollable length
      return (window.innerHeight + window.scrollY + 450 >= c[0]) // compare with scroll position + some give
    },
    scrolling() {
      if (this.atEnd()) {
        this.perpage += 20;
      }
    },
    openPage(){
      return;
    }
  },
  mounted() {
    window.addEventListener('scroll', this.scrolling, {passive: true});
    this.getAll();
  }
}
</script>

<style scoped>
  .content{
    display: flex;
    flex-direction: row;
    justify-content: center;
    height: 100%;
    width: 100%;
  }
</style>
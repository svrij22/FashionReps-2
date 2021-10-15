<template>
  <div>
    <sticky-header v-on:searchq="(q) => this.SetQuery(q)" v-on:toggleseller="toggleSellerSideBar"/>
    <div class="content">
      <sidebar v-show="sellerSideBar" v-if="!isLoading"/>
      <item-container
          :data="displayedItems"
          :isloading="isLoading"
          :pages="pages"
          v-on:loaditems="(p) => this.SetPage(p)"/>
    </div>
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
      fashionItems: [],
      page: 0,
      query: "",
      amountOfItems: 0,
      perpage: 50,
      sellerSideBar: true,
      isLoading: true
    }
  },
  computed: {
    displayedItems(){
      this.perpage;
      this.amountOfItems;
      return _.take(this.fashionItems, this.perpage);
    },
    pages(){
      return Math.min(Math.ceil(this.amountOfItems / 500), 15)
    }
  },
  methods: {
    toggleSellerSideBar(){
      this.sellerSideBar = !this.sellerSideBar
    },
    SetQuery(q){
      this.query = q;
      this.page = 0;
      this.doSearch();
    },
    SetPage(p){
      this.page = p;
      this.doSearch();
    },
    doSearch() {
      fetch("http://localhost:8075/items/search?" + new URLSearchParams({
        param: this.query,
        page: this.page
      }))
          .then(response => response.json())
          .then(data => {
            this.fashionItems = data.fashionItems;
            this.amountOfItems = data.amountOfItems;
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
    this.doSearch("");
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
<template>
  <div class="root" :style="containerStyle">
    <img src="~../assets/Spin-1s-200px.gif" class="spinner" v-if="isloading" style="width: 20px; margin-top: 100px"/>
    <div class="item-container">
      <div class="card item-card" v-for="item in displayData" :key="item.itemId" @click="() => openPage(item.itemId)">

        <div class="image-holder">
          <img class="card-img-top" :src="item.itemImg +'.webp?w=250&h=250&cp=1'" alt="Card image cap">
        </div>

        <div class="card-body">
          <p class="card-title">{{item.itemName}} - {{item.itemId}}</p>
          <p class="card-price">{{getPriceText(item.price)}}</p>
          <p class="card-sold">{{(item.sold)}} Sold</p>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ItemContainer",
  props: ["data","containerstyle", "isloading"],
  computed: {
    displayData(){
      return this.data;
    },
    containerStyle(){
      return this.containerstyle;
    }
  },
  methods:{
    getPriceText(yuan) {
      return "¥ " + yuan + " / $" + Math.round(yuan / 6.4511869)
    },
    openPage(itemid){
      window.open("https://weidian.com/item.html?itemID=" + itemid)
    }
  }
}
</script>

<style scoped>
  .root{
    display: flex;
    align-items: center;
    flex-direction: column;
    align-content: center;
  }

  .item-container{
    margin-top: 80px;
    display: grid;
    grid-template-columns: 25% 25% 25% 25%;
    width: 100%;
    max-width: 1100px;
  }

  .item-card{
    margin: 8px;
    cursor: pointer;
  }

  .item-card:hover{
    background-color: #e8e8e8;
  }

  @media screen and (max-width: 900px){
    .item-container{
      grid-template-columns: 33% 33% 33%
    }
  }

  @media screen and (max-width: 500px){
    .item-container{
      grid-template-columns: 50% 50%
    }
  }

  .card-body{
    padding: 8px;
    text-align: left;
    font-size: 11px;
  }

  .card-title{
    display: block; /* Fallback for non-webkit */
    display: -webkit-box;
    height: 2.6em; /* Fallback for non-webkit, line-height * 2 */
    line-height: 1.3em;
    -webkit-line-clamp: 2; /* if you change this, make sure to change the fallback line-height and height */
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .card-price{
    color: rgb(245, 57, 57);
    font-size: 0.8rem;
    line-height: 0.8rem;
  }

  p{
    margin: 3px;
  }

  .card-img-top{
    object-fit: cover;
  }

  .card-sold{
    font-size: 0.8rem;
    line-height: 0.8rem;
  }
</style>
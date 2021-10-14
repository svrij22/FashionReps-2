import { createApp } from 'vue'
import App from './App.vue'
import Vue3Material from 'vue3-material';

import '@popperjs/core'; // Edit here
import 'bootstrap/dist/js/bootstrap.bundle';

createApp(App)
    .use(Vue3Material)
    .mount('#app')

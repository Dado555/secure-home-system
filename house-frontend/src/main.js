import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import vuetify from "./plugins/vuetify";

import axios from "axios";
import jwt_decode from "jwt-decode";
import moment from "moment";

Vue.config.productionTip = false;

Vue.filter("formatDate", function (value) {
  if (value) return moment(String(value)).format("DD.MM.YYYY. HH:mm:ss.SSS");
});

// Configure axios to always include JWT when sending a request
axios.interceptors.request.use(
  (config) => {
    let jwt = sessionStorage.getItem("jwt");
    if (jwt) {
      if (config.headers) {
        config.headers.Authorization = `Bearer ${jwt}`;
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Authority guard
router.beforeEach((to, from, next) => {
  const { authenticated, authorities } = to.meta;

  if (to.name === "Home" && sessionStorage.getItem("jwt")) {
    next({ name: "CertificateRequests" });
  }

  if (authenticated) {
    let jwt = sessionStorage.getItem("jwt");
    if (jwt) {
      let decodedToken = jwt_decode(jwt);
      if (authorities.some((element) => decodedToken.roles.includes(element))) {
        if (
          !decodedToken.defaultPasswordChanged &&
          to.name !== "ChangeDefaultPassword"
        )
          next({ name: "ChangeDefaultPassword" });
        else next();
      } else {
        next({ name: "home" });
      }
    } else {
      next({ name: "home" });
    }
  } else {
    next();
  }
});

new Vue({
  router,
  vuetify,
  render: (h) => h(App),
}).$mount("#app");

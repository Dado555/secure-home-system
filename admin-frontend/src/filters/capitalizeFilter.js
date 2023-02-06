import Vue from "vue";

Vue.filter("capitalize", (param) => {
  return param.charAt(0).toUpperCase() + param.slice(1).toLowerCase();
});

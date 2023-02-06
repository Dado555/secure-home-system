import Vue from "vue";

Vue.filter("removeUnderscores", (param) => {
  return param.replaceAll("_", " ");
});

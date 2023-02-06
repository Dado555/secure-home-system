import Vue from "vue";
import VueRouter from "vue-router";
import HomeView from "../views/HomeView.vue";

Vue.use(VueRouter);

const roles = { house_head: "HOUSE_HEAD", house_tenant: "HOUSE_TENANT" };

const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView,
    meta: {
      authenticated: false,
      authorities: [],
    },
  },
  {
    path: "/household",
    name: "household",
    component: () => import("../views/HouseholdView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.house_head, roles.house_tenant],
    },
  },
  {
    path: "/real-estate",
    name: "real-estate",
    component: () => import("../views/RealEstateView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.house_head, roles.house_tenant],
    },
  },
  {
    path: "/change-default-password",
    name: "ChangeDefaultPassword",
    component: () => import("../views/ChangeDefaultPasswordView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.house_head, roles.house_tenant],
    },
  },
  {
    path: "/real-estate/:id",
    name: "real-estate-monitor",
    component: () => import("../views/RealEstateMonitorView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.house_head, roles.house_tenant],
    },
  },
  {
    path: "/alarms",
    name: "AlarmsView",
    component: () => import("../views/AlarmsView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.house_head, roles.house_tenant],
    },
  },
];

const router = new VueRouter({
  routes,
});

export default router;

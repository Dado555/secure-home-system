import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const roles = { admin: "ADMIN" };

const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("../views/HomeView.vue"),
    meta: {
      authenticated: false,
      authorities: [],
    },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/LoginView.vue"),
    meta: {
      authenticated: false,
      authorities: [],
    },
  },
  {
    path: "/certificates",
    name: "CertificateList",
    component: () => import("../views/CertificateListView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.admin],
    },
  },
  {
    path: "/certificates/:alias",
    name: "CertificateDetailed",
    component: () => import("../views/DetailedCertificateView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.admin],
    },
  },
  {
    path: "/certificate-download",
    name: "CertificateDownload",
    component: () => import("../views/CertificateDownloadView.vue"),
    meta: {
      authenticated: false,
      authorities: [],
    },
  },
  {
    path: "/requests",
    name: "CertificateRequests",
    component: () => import("../views/CertificateRequestsView"),
    meta: {
      authenticated: true,
      authorities: [roles.admin],
    },
  },
  {
    path: "/households",
    name: "AllHouseholdsView",
    component: () => import("../views/AllHouseholdsView"),
    meta: {
      authenticated: true,
      authorities: [roles.admin],
    },
  },
  {
    path: "/households/:id",
    name: "HouseholdDetailedView",
    component: () => import("../views/HouseholdDetailedView"),
    meta: {
      authenticated: true,
      authorities: [roles.admin],
    },
  },
  {
    path: "/certificate-verification",
    name: "CertificateVerification",
    component: () => import("../views/CertificateVerificationView.vue"),
    meta: {
      authenticated: false,
      authorities: [],
    },
  },
  {
    path: "/change-default-password",
    name: "ChangeDefaultPassword",
    component: () => import("../views/ChangeDefaultPasswordView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.admin],
    },
  },
  {
    path: "/households/:householdId/real-estate/:realEstateId",
    name: "DevicesView",
    component: () => import("../views/DevicesView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.admin],
    },
  },
  {
    path: "/logs",
    name: "LogsView",
    component: () => import("../views/LogsView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.admin],
    },
  },
  {
    path: "/alarms",
    name: "AlarmsView",
    component: () => import("../views/AlarmsView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.admin],
    },
  }, 
  {
    path: "/rules",
    name: "Rules",
    component: () => import("../views/RulesView.vue"),
    meta: {
      authenticated: true,
      authorities: [roles.admin],
    },
  },
];

const router = new VueRouter({
  routes,
});

export default router;

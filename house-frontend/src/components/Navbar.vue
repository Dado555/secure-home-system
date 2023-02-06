<template>
  <v-app-bar app color="primary" dark>
    <div class="d-flex align-center">
      <v-btn text>
        <span class="mr-2">MY HOUSE DASHBOARD</span>
        <span v-if="loggedIn">-> Logged in as: {{ email }} ({{ roles }})</span>
      </v-btn>
    </div>
    <v-spacer></v-spacer>
    <v-btn v-if="!loggedIn" @click="$router.push('/')" text> Login </v-btn>
    <v-btn
      v-if="loggedIn && (roles === 'HOUSE_HEAD' || roles === 'HOUSE_TENANT')"
      @click="redirect('alarms')"
      text
    >
      Alarms
    </v-btn>
    <v-btn
      v-if="loggedIn && (roles === 'HOUSE_HEAD' || roles === 'HOUSE_TENANT')"
      @click="redirect('household')"
      text
    >
      Household
    </v-btn>
    <v-btn
      v-if="loggedIn && (roles === 'HOUSE_HEAD' || roles === 'HOUSE_TENANT')"
      @click="redirect('real-estate')"
      text
    >
      Real Estate
    </v-btn>
    <v-btn
      v-if="loggedIn && (roles === 'HOUSE_HEAD' || roles === 'HOUSE_TENANT')"
      v-on:click="logout"
      text
    >
      Logout
    </v-btn>
    <v-snackbar v-model="snackbar" :timeout="timeout">
      {{ snackbarMessage }}

      <template v-slot:action="{ attrs }">
        <v-btn color="blue" text v-bind="attrs" @click="snackbar = false">
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-app-bar>
</template>

<script>
import { authService } from "../service/authService";

export default {
  name: "Navbar",
  data() {
    return {
      loggedIn: false,
      roles: "",
      email: "",
      snackbarMessage: "Error logging out.",
      snackbar: false,
      timeout: 5000,
    };
  },
  mounted() {
    if (authService.userLoggedIn()) {
      let decodedToken = authService.getDecodedToken();
      this.loggedIn = true;
      this.roles = decodedToken.roles;
      this.email = decodedToken.sub;
    }
    this.$root.$on("loginSuccess", (data) => {
      this.loggedIn = authService.userLoggedIn();
      this.roles = data.roles;
      this.email = data.email;
    });
  },
  methods: {
    redirect(routeName) {
      this.$router.push("/" + routeName).catch((error) => {});
    },
    logout() {
      this.logoutBtnLoading = true;
      authService
        .logout()
        .then((_) => {
          this.logoutBtnLoading = false;
          sessionStorage.removeItem("jwt");
          this.loggedIn = false;
          this.$router.push({ name: "home" });
        })
        .catch((_) => {
          this.logoutBtnLoading = false;
          this.snackbar = true;
        });
    },
  },
};
</script>

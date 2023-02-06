<template>
  <v-app-bar app color="primary" dark>
    <div class="d-flex align-center">
      <v-btn href="#/" text>
        <span class="mr-2">Secure Home Admin</span>
      </v-btn>
    </div>
    <v-spacer></v-spacer>
    <v-btn v-if="!loggedIn" @click="redirect('Login')" text> Login </v-btn>
    <v-btn v-if="loggedIn && roles === 'ADMIN'" @click="redirect('Rules')" text>
      Rules
    </v-btn>
    <v-btn
      v-if="loggedIn && roles === 'ADMIN'"
      @click="redirect('LogsView')"
      text
    >
      Logs
    </v-btn>

    <v-btn
        v-if="loggedIn && roles === 'ADMIN'"
        @click="redirect('AlarmsView')"
        text
    >
      Alarms
    </v-btn>

    <v-btn
      v-if="loggedIn && roles === 'ADMIN'"
      @click="redirect('CertificateRequests')"
      text
    >
      Requests
    </v-btn>
    <v-btn
      v-if="loggedIn && roles === 'ADMIN'"
      @click="redirect('CertificateList')"
      text
    >
      Certificates
    </v-btn>
    <v-btn
      v-if="loggedIn && roles === 'ADMIN'"
      @click="redirect('AllHouseholdsView')"
      text
    >
      Households
    </v-btn>
    <v-btn
      v-if="loggedIn && roles === 'ADMIN'"
      v-on:click="logout"
      text
      :loading="logoutBtnLoading"
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
      logoutBtnLoading: false,
      roles: "",
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
    }
    this.$root.$on("loginSuccess", (roles) => {
      this.loggedIn = authService.userLoggedIn();
      this.roles = roles;
    });
  },
  methods: {
    redirect(routeName) {
      this.$router.push({ name: routeName }).catch((error) => {});
    },
    logout() {
      this.logoutBtnLoading = true;
      authService
        .logout()
        .then((_) => {
          this.logoutBtnLoading = false;
          sessionStorage.removeItem("jwt");
          console.log("logout successful");
          this.loggedIn = false;
          this.$router.push({ name: "Login" });
        })
        .catch((_) => {
          this.logoutBtnLoading = false;
          this.snackbar = true;
        });
    },
  },
};
</script>

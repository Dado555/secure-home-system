<template>
  <v-container>
    <v-row align="center" justify="center">
      <v-col cols="6" md="6" class="header-col">
        <h1>Login</h1>
      </v-col>
    </v-row>
    <form @submit.prevent="login">
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="8" lg="6" xl="4">
          <v-card elevation="4">
            <v-row align="center" justify="center">
              <v-col cols="8" sm="8" md="8" lg="10" xl="10">
                <v-text-field label="Email" required v-model="payload.email">
                </v-text-field>
              </v-col>
            </v-row>
            <v-row align="center" justify="center">
              <v-col cols="8" sm="8" md="8" lg="10" xl="10">
                <v-text-field
                  label="Password"
                  :type="valuePassword ? 'password' : 'text'"
                  @click:append="() => (valuePassword = !valuePassword)"
                  :append-icon="valuePassword ? 'mdi-eye-off' : 'mdi-eye'"
                  required
                  v-model="payload.password"
                >
                </v-text-field>
              </v-col>
            </v-row>

            <v-row align="center" justify="center">
              <v-col cols="8" sm="8" md="8" lg="10" xl="10">
                <v-btn
                  depressed
                  color="primary"
                  type="submit"
                  :disabled="payload.email === '' || payload.password === ''"
                  :loading="loginBtnLoading"
                >
                  Login
                </v-btn>
              </v-col>
            </v-row>
          </v-card>
        </v-col>
      </v-row>
    </form>

    <v-snackbar v-model="snackbar" :timeout="timeout">
      {{ text }}

      <template v-slot:action="{ attrs }">
        <v-btn color="blue" text v-bind="attrs" @click="snackbar = false">
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import { authService } from "../service/authService";
import jwt_decode from "jwt-decode";

export default {
  name: "Login",
  data: () => {
    return {
      valuePassword: String,
      payload: {
        email: "",
        password: "",
      },
      loginBtnLoading: false,
      snackbar: false,
      text: "Wrong username/password combination.",
      timeout: 5000,
    };
  },
  methods: {
    login() {
      this.loginBtnLoading = true;
      authService
        .login(this.payload)
        .then((response) => {
          this.loginBtnLoading = false;
          let decodedToken = jwt_decode(response.data.jwt);
          if (decodedToken.roles === "ADMIN") {
            authService.setToken(response.data.jwt);
            this.$root.$emit("loginSuccess", decodedToken.roles);
            if (authService.defaultPasswordChanged())
              this.$router.push({ name: "CertificateList" });
            else this.$router.push({ name: "ChangeDefaultPassword" });
          } else {
            this.snackbar = true;
            this.loginBtnLoading = false;
          }
        })
        .catch((error) => {
          if (error.response) this.text = error.response.data.message;
          this.snackbar = true;
          this.loginBtnLoading = false;
        });
    },
  },
};
</script>

<style scoped>
.header-col {
  text-align: center;
  margin-top: 2%;
}
</style>

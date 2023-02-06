<template>
  <v-container>
    <v-row align="center" justify="center">
      <v-col cols="6" md="6" class="header-col">
        <h1>Change default password</h1>
      </v-col>
    </v-row>
    <v-form @submit.prevent="changePassword" v-model="formValid">
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="8" lg="6" xl="4">
          <v-card elevation="4">
            <v-row align="center" justify="center">
              <v-col cols="8" sm="8" md="8" lg="10" xl="10">
                <v-text-field
                  label="Old password"
                  :type="valueOldPassword ? 'password' : 'text'"
                  @click:append="() => (valueOldPassword = !valueOldPassword)"
                  :append-icon="valueOldPassword ? 'mdi-eye-off' : 'mdi-eye'"
                  required
                  v-model="payload.oldPassword"
                  :rules="oldPasswordRules"
                >
                </v-text-field>
              </v-col>
            </v-row>
            <v-row align="center" justify="center">
              <v-col cols="8" sm="8" md="8" lg="10" xl="10">
                <v-text-field
                  label="New password"
                  hint="At least 8 characters, 1 number, 1 uppercase and 1 special character (!@#$)."
                  persistent-hint
                  :type="valueNewPassword ? 'password' : 'text'"
                  @click:append="() => (valueNewPassword = !valueNewPassword)"
                  :append-icon="valueNewPassword ? 'mdi-eye-off' : 'mdi-eye'"
                  required
                  v-model="payload.newPassword"
                  :rules="newPasswordRules"
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
                  :disabled="!formValid"
                  :loading="btnLoading"
                >
                  Confirm
                </v-btn>
              </v-col>
            </v-row>
          </v-card>
        </v-col>
      </v-row>
    </v-form>

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
import { userService } from "../service/userService";
import { authService } from "../service/authService";

export default {
  name: "ChangeDefaultPassword",
  data: () => {
    return {
      formValid: false,
      valueOldPassword: String,
      valueNewPassword: String,
      snackbar: false,
      timeout: 5000,
      text: "An error occurred.",
      btnLoading: false,
      oldPasswordRules: [(p) => !!p || "Old password cannot be empty."],
      newPasswordRules: [
        (p) => !!p || "New password cannot be empty",
        (p) => p.length >= 8 || "Minimum password length is 8 characters.",
        (p) =>
          /.*[!@#$].*/.test(p) ||
          "New password does not contain any special characters (!@#$).",
        (p) =>
          /.*[0-9].*/.test(p) || "New password does not contain any numbers.",
        (p) =>
          /.*[a-z].*/.test(p) ||
          "New password does not contain any lowercase letters.",
        (p) =>
          /.*[A-Z].*/.test(p) ||
          "New password does not contain any uppercase letters.",
      ],
      payload: {
        oldPassword: "",
        newPassword: "",
      },
    };
  },
  methods: {
    changePassword() {
      this.btnLoading = true;
      userService
        .changeDefaultPassword(authService.userId(), this.payload)
        .then((_) => {
          this.btnLoading = false;
          this.text = "Password successfully changed.";
          this.snackbar = true;
          sessionStorage.removeItem("jwt");
          this.$router.push({ name: "home" });
        })
        .catch((error) => {
          this.btnLoading = false;
          if (error.response) this.text = error.response.data.message;
          else this.text = "An error occurred.";
          this.snackbar = true;
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

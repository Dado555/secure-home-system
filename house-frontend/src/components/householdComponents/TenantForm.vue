<template>
  <v-container>
    <v-dialog v-model="dialog" persistent max-width="600px">
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          v-if="edit"
          color="primary"
          v-bind="attrs"
          v-on="on"
          :disabled="userRole !== 'HOUSE_HEAD'"
        >
          Edit
        </v-btn>
        <v-btn
          v-else
          color="primary"
          v-bind="attrs"
          v-on="on"
          :disabled="userRole !== 'HOUSE_HEAD'"
        >
          Add new tenant
        </v-btn>
      </template>
      <v-card>
        <v-card-title>
          <span v-if="!edit" class="text-h5">Add Tenant</span>
          <span v-else class="text-h5">Edit Tenant</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6" md="6">
                <v-text-field
                  label="Name"
                  v-model="name"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6" md="6">
                <v-text-field
                  label="Surname"
                  v-model="surname"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" v-if="!edit">
                <v-text-field
                  label="Email"
                  v-model="email"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" v-if="!edit">
                <v-text-field
                  label="Password"
                  hint="At least 8 characters, 1 number, 1 uppercase and 1 special character (!@#$)."
                  :type="showPassword ? 'text' : 'password'"
                  v-model="password"
                  :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                  @click:append="showPassword = !showPassword"
                  required
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="dialog = false">
            Cancel
          </v-btn>
          <v-btn color="blue darken-1" text type="submit" @click="submit()">
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
import { authService } from "../../service/authService";

export default {
  props: ["edit", "userData", "householdID", "callback"],
  data: () => ({
    showPassword: false,
    dialog: false,
    snackbar: false,
    timeout: 2000,
    text: "",
    userRole: "",
    name: "",
    surname: "",
    email: "",
    password: "",
  }),
  mounted() {
    this.userRole = authService.roles();
    if (this.edit) {
      this.name = this.userData.name;
      this.surname = this.userData.surname;
    }
  },
  methods: {
    submit() {
      let requestBody = {};
      if (this.edit) {
        if (this.name.trim() === "" || this.surname.trim() === "") {
          this.text = "You need to fill in all fields.";
          this.snackbar = true;
          return;
        }

        requestBody = {
          name: this.name,
          surname: this.surname,
        };
        this.callback(requestBody, this.userData.id);
      } else {
        if (
          this.name.trim() === "" ||
          this.surname.trim() === "" ||
          this.email.trim() === "" ||
          this.password.trim() === ""
        ) {
          this.text = "You need to fill in all fields.";
          this.snackbar = true;
          return;
        }

        if (!this.validateEmail(this.email)) {
          this.text = "Email address is not valid.";
          this.snackbar = true;
          return;
        }

        if (!this.validatePassword(this.password)) {
          this.text = "Password is not following given policy.";
          this.snackbar = true;
          return;
        }

        requestBody = {
          householdID: this.householdID,
          name: this.name,
          surname: this.surname,
          email: this.email,
          password: this.password,
        };
        this.callback(requestBody);
      }
      this.dialog = false;
    },
    validatePassword(password) {
      if (
        password.length >= 8 &&
        password.match(".*[0-9].*") &&
        password.match(".*[a-z].*") &&
        password.match(".*[A-Z].*") &&
        password.match(".*[!@#$].*")
      ) {
        return true;
      }
      return false;
    },
    validateEmail(email) {
      return String(email)
        .toLowerCase()
        .match(
          /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        );
    },
  },
};
</script>

<template>
  <v-container>
    <v-row align="center" justify="center">
      <v-col cols="12" md="6">
        <v-flex class="text-center">
          <h1>Certificate and Key download</h1>
        </v-flex>
        <br />
        <v-text-field
          v-model="alias"
          label="Alias"
          hint="Alias is your e-mail address"
          persistent-hint
        ></v-text-field>
        <v-text-field
          v-model="password"
          :type="valuePassword ? 'password' : 'text'"
          @click:append="() => (valuePassword = !valuePassword)"
          :append-icon="valuePassword ? 'mdi-eye-off' : 'mdi-eye'"
          label="Password"
          hint="Password was provided to you via e-mail"
          persistent-hint
        ></v-text-field>
        <br />
        <br />
        <v-btn
          :disabled="alias === '' || password === ''"
          color="primary"
          @click="submitDownload()"
          >Download</v-btn
        >
      </v-col>
    </v-row>
    <v-snackbar left v-model="snackbarCertificate" :timeout="timeout">
      {{ textCertificate }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="blue"
          text
          v-bind="attrs"
          @click="snackbarCertificate = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
    <v-snackbar right v-model="snackbarKey" :timeout="timeout">
      {{ textKey }}

      <template v-slot:action="{ attrs }">
        <v-btn color="blue" text v-bind="attrs" @click="snackbarKey = false">
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import { certificateService } from "@/service/certificateService";
export default {
  name: "CertificateDownload",
  data() {
    return {
      valuePassword: String,
      alias: "",
      password: "",
      snackbarCertificate: false,
      snackbarKey: false,
      textCertificate: "",
      textKey: "",
      timeout: 5000,
    };
  },
  methods: {
    submitDownload() {
      certificateService
        .downloadCertificate(this.alias, this.$route.query.token)
        .then((message) => {
          this.textCertificate = message;
          this.snackbarCertificate = true;
        });
      certificateService
        .downloadKey(this.alias, this.password, this.$route.query.token)
        .then((message) => {
          this.textKey = message;
          this.snackbarKey = true;
        });
    },
  },
};
</script>

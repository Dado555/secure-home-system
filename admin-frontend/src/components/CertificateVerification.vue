<template>
  <v-container>
    <v-row align="center" justify="center">
      <v-col cols="12" md="6">
        <v-flex class="text-center">
          <h1>Certificate Verification</h1>
        </v-flex>
        <v-file-input
          label="Certificate file"
          show-size
          counter
          chips
          accept=".cer"
          v-model="cerFile"
        />
        <v-btn
          color="primary"
          @click="verify()"
          :disabled="!cerFile"
          :loading="verifyBtnLoading"
          >Verify</v-btn
        >
      </v-col>
    </v-row>
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
import { certificateService } from "../service/certificateService";
export default {
  name: "CertificateVerification",
  data: () => {
    return {
      cerFile: undefined,
      verifyBtnLoading: false,
      text: "",
      snackbar: false,
      timeout: 5000,
    };
  },
  methods: {
    verify() {
      this.verifyBtnLoading = true;
      certificateService
        .verifyCertificate(this.cerFile)
        .then((response) => {
          const data = response.data;
          this.verifyBtnLoading = false;
          this.snackbar = true;
          if (data.certificateValid)
            this.text = "Certificate verified succesfully.";
          else this.text = data.message;
        })
        .catch((error) => {
          this.verifyBtnLoading = false;
          this.snackbar = true;
          if (error.response) this.text = error.response.data.message;
          else this.text = "An error has occurred.";
        });
    },
  },
};
</script>

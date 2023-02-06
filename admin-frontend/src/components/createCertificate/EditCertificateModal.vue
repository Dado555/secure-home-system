<template>
  <v-container>
    <v-row justify="space-around">
      <v-col cols="auto">
        <v-dialog
          transition="dialog-bottom-transition"
          max-width="90%"
          max-height="90%"
          v-model="openDialog"
          persistent
        >
          <v-card>
            <v-toolbar color="primary" dark
              >Edit certificate request
            </v-toolbar>
            <v-card-text>
              <certificate-version-info
                style="margin-top: 2%"
                :certEdit="certRequest"
                @closeDialog="$emit('closingModal')"
                @saveChanges="saveChanges($event)"
              />
            </v-card-text>
          </v-card>
        </v-dialog>
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
import CertificateVersionInfo from "@/components/createCertificate/CertificateVersionInfo";
import { certificateService } from "@/service/certificateService";
export default {
  name: "EditCertificateModal",
  components: { CertificateVersionInfo },
  props: ["openDialog", "certRequest"],
  data: () => {
    return {
      snackbar: false,
      timeout: 5000,
      text: "",
    };
  },
  watch: {
    openDialog: function (newVal, oldVal) {
      if (!newVal) {
        this.$emit("closingModal");
      }
    },
  },
  methods: {
    saveChanges(value) {
      certificateService
        .editRequest(value, this.certRequest.id)
        .then((_) => {
          this.snackbar = true;
          this.text = "Request successfully updated.";
        })
        .catch((error) => {
          this.snackbar = true;
          if (error.response) this.text = error.response.data.message;
          else this.text = "An error has occurred.";
        });
    },
  },
};
</script>

<style scoped></style>

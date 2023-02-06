<template>
  <v-form>
    <v-container>
      <v-row> Extended Key Usage </v-row>
      <v-row>
        <v-col v-for="(key, i) in keyUsages" :key="i">
          <v-checkbox
            v-model="chosenKeyUsages"
            :label="key.text"
            color="blue"
            :value="key.value"
            hide-details
          />
        </v-col>
      </v-row>
    </v-container>
  </v-form>
</template>

<script>
export default {
  name: "ExtendedKeyUsage",
  props: ["checkedOptions"],
  data() {
    return {
      form: {},
      keyUsages: [
        { text: "Any Extended Key Usage", value: "ANY_EXTENDED_KEY_USAGE" },
        { text: "Code Signing", value: "CODE_SIGNING" },
        { text: "E-mail Protection", value: "EMAIL_PROTECTION" },
        { text: "IP Security End System", value: "IP_SECURITY_END_SYSTEM" },
        {
          text: "IP Security Tunnel Termination",
          value: "IP_SECURITY_TUNNEL_TERMINATION",
        },
        { text: "IP Security User", value: "IP_SECURITY_USER" },
        { text: "OCSP Signing", value: "OCSP_SIGNING" },
        { text: "Smartcard Logon", value: "SMARTCARD_LOGON" },
        { text: "Time Stamping", value: "TIME_STAMPING" },
        {
          text: "TLS Web Client Authentication",
          value: "TLS_WEB_CLIENT_AUTHENTICATION",
        },
        {
          text: "TLS Web Server Authentication",
          value: "TLS_WEB_SERVER_AUTHENTICATION",
        },
      ],
      chosenKeyUsages: [],
    };
  },
  mounted() {
    if (this.checkedOptions) this.chosenKeyUsages = [...this.checkedOptions];
  },
  watch: {
    chosenKeyUsages: function () {
      this.$emit("collectionChange", [...this.chosenKeyUsages]);
    },
  },
};
</script>

<style scoped></style>

<template>
  <v-form>
    <v-container>
      <v-row> Key Usage </v-row>
      <v-row>
        <v-row>
          <v-col v-for="(key, i) in keyUsages" :key="i">
            <v-checkbox
              v-model="chosenKeyUsages"
              :label="key.text"
              color="blue"
              :value="key.value"
              hide-details
            ></v-checkbox>
          </v-col>
        </v-row>
      </v-row>
    </v-container>
  </v-form>
</template>

<script>
export default {
  name: "KeyUsage",
  props: ["checkedOptions"],
  data() {
    return {
      form: {},
      keyUsages: [
        { text: "Certificate Signing", value: "CERTIFICATE_SIGNING" },
        { text: "Decipher Only", value: "DECIPHER_ONLY" },
        { text: "Key Agreement", value: "KEY_AGREEMENT" },
        { text: "CRL Sign", value: "CRL_SIGN" },
        { text: "Digital Signature", value: "DIGITAL_SIGNATURE" },
        { text: "Key Encipherment", value: "KEY_ENCIPHERMENT" },
        { text: "Data Encipherment", value: "DATA_ENCIPHERMENT" },
        { text: "Encipher Only", value: "ENCIPHER_ONLY" },
        { text: "Non Repudiation", value: "NON_REPUDIATION" },
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

<template>
  <v-form style="width: 100%" label-position="right" label-width="150px">
    <v-row> Key Identifier </v-row>
    <v-row>
      <v-col>
        <v-checkbox v-model="form.keyId" label="Select" />
      </v-col>
    </v-row>
  </v-form>
</template>

<script>
export default {
  name: "SubjectKeyIdentifier",
  data() {
    return {
      form: {},
      generationMethods: ["160-bit Hash", "64-bit Hash"],
      chosenGenMethod: "",
    };
  },
};
</script>

<style scoped></style>

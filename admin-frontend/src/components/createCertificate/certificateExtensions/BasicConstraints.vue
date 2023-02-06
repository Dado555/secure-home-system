<template>
  <v-form
    style="width: 90%; font-size: 40px"
    label-position="right"
    label-width="150px"
  >
    <v-row> Basic Constraints </v-row>
    <v-row>
      <v-checkbox
        v-model="form.isCA"
        label="Subject is a CA"
        color="blue"
        value="isCA"
        hide-details
        row
      />
    </v-row>

    <v-row>
      <v-text-field v-model="form.pathLength" label="Path length constraint" />
    </v-row>
  </v-form>
</template>

<script>
export default {
  name: "BasicConstraints",
  data() {
    return {
      form: {
        isCA: false,
        pathLength: null,
      },
    };
  },
};
</script>

<style scoped></style>

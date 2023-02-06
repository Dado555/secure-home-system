<template>
  <v-container>
    <v-row> Alternative name </v-row>
    <v-row style="padding-top: 24px">
      <v-expansion-panels>
        <v-expansion-panel>
          <v-expansion-panel-header>
            <v-row no-gutters>
              <v-col> General Name </v-col>
            </v-row>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <v-row>
              <v-col cols="12"> Name </v-col>
            </v-row>
            <v-row v-for="(name, i) in names" :key="i">
              <v-col cols="4">
                <v-text-field :value="name" readonly />
              </v-col>
              <v-col>
                <v-btn icon color="red" @click="deleteName(name)">
                  <v-icon>mdi-minus-thick</v-icon>
                </v-btn>
              </v-col>
            </v-row>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-row>
    <v-row>
      <v-col cols="10">
        <v-text-field label="New name" v-model="newName" />
      </v-col>
      <v-col cols="2">
        <v-btn
          icon
          color="blue"
          style="margin-top: 20px"
          :disabled="newName === ''"
          @click="addNewName()"
        >
          <v-icon>mdi-plus-thick</v-icon>
        </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: "SubjectAlternativeName",
  props: ["alternativeNames"],
  data() {
    return {
      form: {},
      names: [],
      newName: "",
    };
  },
  mounted() {
    if (this.alternativeNames) this.names = [...this.alternativeNames];
  },
  methods: {
    resetNewName() {
      this.newName = "";
    },
    addNewName() {
      this.names.push(this.newName);
      this.$emit("collectionChange", [...this.names]);
      this.newName = "";
    },
    deleteName(name) {
      this.names = this.names.filter((item) => item !== name);
      this.$emit("collectionChange", [...this.names]);
    },
  },
};
</script>

<style scoped></style>

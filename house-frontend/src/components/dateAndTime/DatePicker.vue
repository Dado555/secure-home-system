<template>
  <v-menu
    ref="menu"
    v-model="menu"
    :close-on-content-click="false"
    :return-value.sync="date"
    transition="scale-transition"
    offset-y
    min-width="auto"
  >
    <template v-slot:activator="{ on, attrs }">
      <v-text-field
        v-model="date"
        :label="title"
        prepend-icon="mdi-calendar"
        readonly
        v-bind="attrs"
        v-on="on"
      ></v-text-field>
    </template>
    <v-date-picker v-model="date" no-title scrollable>
      <v-spacer></v-spacer>
      <v-btn text color="primary" @click="menu = false"> Cancel </v-btn>
      <v-btn
        text
        color="primary"
        @click="
          $refs.menu.save(date);
          save();
        "
      >
        OK
      </v-btn>
    </v-date-picker>
  </v-menu>
</template>

<script>
export default {
  name: "DatePicker",
  props: ["title", "isStart", "fromReport"],
  data() {
    return {
      date: null,
      menu: false,
    };
  },
  mounted() {
    this.$root.$on("resetDateTime", () => {
      this.date = null;
    });
  },
  methods: {
    save() {
      if (this.isStart) {
        if (!this.fromReport) {
          this.$root.$emit("startDateSaved", this.date);
        } else {
          this.$root.$emit("reportStartDateSaved", this.date);
        }
      } else {
        if (!this.fromReport) {
          this.$root.$emit("endDateSaved", this.date);
        } else {
          this.$root.$emit("reportEndDateSaved", this.date);
        }
      }
    },
  },
};
</script>

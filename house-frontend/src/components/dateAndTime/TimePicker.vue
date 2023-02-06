<template>
  <v-menu
    ref="menu"
    v-model="menu2"
    :close-on-content-click="false"
    :nudge-right="40"
    :return-value.sync="time"
    transition="scale-transition"
    offset-y
    max-width="290px"
    min-width="290px"
  >
    <template v-slot:activator="{ on, attrs }">
      <v-text-field
        v-model="time"
        :label="title"
        prepend-icon="mdi-clock-time-four-outline"
        readonly
        v-bind="attrs"
        v-on="on"
      ></v-text-field>
    </template>
    <v-time-picker
      v-if="menu2"
      v-model="time"
      full-width
      @click:minute="
        $refs.menu.save(time);
        save();
      "
    ></v-time-picker>
  </v-menu>
</template>

<script>
export default {
  name: "TimePicker",
  props: ["title", "isStart", "fromReport"],
  data() {
    return {
      time: null,
      menu2: false,
    };
  },
  mounted() {
    this.$root.$on("resetDateTime", () => {
      this.time = null;
    });
  },
  methods: {
    save() {
      if (this.isStart) {
        if (!this.fromReport) {
          this.$root.$emit("startTimeSaved", this.time);
        } else {
          this.$root.$emit("reportStartTimeSaved", this.time);
        }
      } else {
        if (!this.fromReport) {
          this.$root.$emit("endTimeSaved", this.time);
        } else {
          this.$root.$emit("reportEndTimeSaved", this.time);
        }
      }
    },
  },
};
</script>

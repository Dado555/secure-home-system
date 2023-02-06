<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-flex class="text-center">
          <h1>Rules</h1>
        </v-flex>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-tabs v-model="tab">
          <v-tab> Temperature rules </v-tab>
          <v-tab> Frequency rules </v-tab>
        </v-tabs>

        <v-tabs-items v-model="tab">
          <v-tab-item>
            <temperature-rules />
          </v-tab-item>
          <v-tab-item>
            <frequency-rules />
          </v-tab-item>
        </v-tabs-items>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import TemperatureRules from "../rules/TemperatureRules.vue";
import FrequencyRules from "../rules/FrequencyRules.vue";

export default {
  name: "Rules",
  components: { TemperatureRules, FrequencyRules },
  data: () => {
    return {
      tab: 0,
    };
  },
};
</script>

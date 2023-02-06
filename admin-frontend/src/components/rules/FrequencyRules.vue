<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-btn color="primary" @click="dialog = true">
          Add frequency rule
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-simple-table>
          <template v-slot:default>
            <thead>
              <tr>
                <th class="text-left">Device id</th>
                <th class="text-left">Number of occurences</th>
                <th class="text-left">Seconds</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in frequencyRules" :key="item.id">
                <td>{{ item.deviceId }}</td>
                <td>{{ item.numberOfOccurrences }}</td>
                <td>{{ item.seconds }}</td>
              </tr>
            </tbody>
          </template>
        </v-simple-table>
      </v-col>
    </v-row>
    <v-dialog
      transition="dialog-bottom-transition"
      max-width="90%"
      max-height="90%"
      v-model="dialog"
    >
      <v-card>
        <v-toolbar color="primary" dark
          >Add frequency rule
          <v-spacer />
          <v-btn @click="dialog = false" text>&times;</v-btn>
        </v-toolbar>
        <v-card-text>
          <v-row align="center" justify="center">
            <v-col cols="12" md="4">
              <v-text-field
                type="number"
                min="1"
                label="Device id"
                v-model="newFrequencyRule.deviceId"
              />
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field
                type="number"
                min="1"
                label="Number of occurrences"
                v-model="newFrequencyRule.numberOfOccurrences"
              />
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field
                type="number"
                min="2"
                label="Seconds"
                v-model="newFrequencyRule.seconds"
              />
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-btn @click="dialog = false"> Cancel </v-btn>
          <v-btn @click="addFrequencyRule()" color="primary"> Confirm </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar v-model="snackbar" :timeout="timeout">
      {{ snackbarMessage }}

      <template v-slot:action="{ attrs }">
        <v-btn color="blue" text v-bind="attrs" @click="snackbar = false">
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import { ruleService } from "../../service/ruleService";

export default {
  name: "FrequencyRules",
  data: () => {
    return {
      dialog: false,
      snackbar: false,
      timeout: 5000,
      snackbarMessage: "",
      frequencyRules: [],
      newFrequencyRule: {
        deviceId: 1,
        numberOfOccurrences: 1,
        seconds: 2,
      },
    };
  },
  mounted() {
    this.getRules();
  },
  methods: {
    getRules() {
      ruleService.getFrequencyRules().then((response) => {
        this.frequencyRules = response.data;
      });
    },
    addFrequencyRule() {
      ruleService
        .createFrequencyRule(this.newFrequencyRule)
        .then((response) => {
          this.dialog = false;
          this.frequencyRules.push(response.data);
          this.snackbar = true;
          this.snackbarMessage = "Rule successfully created.";
        })
        .catch((error) => {
          if (error.response)
            this.snackbarMessage = error.response.data.message;
          else this.snackbarMessage = "An error occurred";
          this.snackbar = true;
        });
    },
  },
};
</script>

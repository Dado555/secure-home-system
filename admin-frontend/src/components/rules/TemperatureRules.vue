<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-btn color="primary" @click="dialog = true">
          Add temperature rule
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
                <th class="text-left">Min temperature</th>
                <th class="text-left">Max temperature</th>
                <th class="text-left">Temperature scale</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in temperatureRules" :key="item.id">
                <td>{{ item.deviceId }}</td>
                <td>{{ item.minTemperature }}</td>
                <td>{{ item.maxTemperature }}</td>
                <td>{{ item.temperatureScale }}</td>
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
          >Add temperature rule
          <v-spacer />
          <v-btn @click="dialog = false" text>&times;</v-btn>
        </v-toolbar>
        <v-card-text>
          <v-row align="center" justify="center">
            <v-col cols="12" md="3">
              <v-text-field
                type="number"
                min="1"
                label="Device id"
                v-model="newTemperatureRule.deviceId"
              />
            </v-col>
            <v-col cols="12" md="3">
              <v-text-field
                type="number"
                label="Min temperature"
                v-model="newTemperatureRule.minTemperature"
              />
            </v-col>
            <v-col cols="12" md="3">
              <v-text-field
                type="number"
                label="Max temperature"
                v-model="newTemperatureRule.maxTemperature"
              />
            </v-col>
            <v-col cols="12" md="3">
              <v-select
                label="Temperature scale"
                :items="scales"
                v-model="newTemperatureRule.temperatureScale"
              />
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-btn @click="dialog = false"> Cancel </v-btn>
          <v-btn @click="addTemperatureRule()" color="primary"> Confirm </v-btn>
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
  name: "TemperatureRules",
  data: () => {
    return {
      temperatureRules: [],
      dialog: false,
      snackbar: false,
      timeout: 5000,
      snackbarMessage: "",
      scales: ["C", "F"],
      newTemperatureRule: {
        deviceId: 1,
        minTemperature: 0,
        maxTemperature: 50,
        temperatureScale: "C",
      },
    };
  },
  mounted() {
    this.getTemperatureRules();
  },
  methods: {
    addTemperatureRule() {
      ruleService
        .createTemperatureRule(this.newTemperatureRule)
        .then((response) => {
          this.dialog = false;
          this.temperatureRules.push(response.data);
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
    getTemperatureRules() {
      ruleService.getTemperatureRules().then((response) => {
        this.temperatureRules = response.data;
      });
    },
  },
};
</script>

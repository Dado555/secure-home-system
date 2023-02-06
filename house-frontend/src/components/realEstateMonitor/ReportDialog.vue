<template>
  <v-dialog v-model="dialog" persistent max-width="600px">
    <template v-slot:activator="{ on, attrs }">
      <v-btn
        style="margin-left: 10px; margin-bottom: 10px"
        small
        elevation="2"
        v-bind="attrs"
        v-on="on"
        >Generate report</v-btn
      >
    </template>
    <v-card>
      <v-card-title>
        <span class="text-h5">Significant event report</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col>
              <date-picker
                :title="'Choose start date'"
                :fromReport="true"
                :isStart="true"
              ></date-picker>
            </v-col>
            <v-col>
              <time-picker
                :title="'Choose start time'"
                :fromReport="true"
                :isStart="true"
              ></time-picker>
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <date-picker
                :title="'Choose end date'"
                :fromReport="true"
                :isStart="false"
              ></date-picker>
            </v-col>
            <v-col>
              <time-picker
                :title="'Choose end time'"
                :fromReport="true"
                :isStart="false"
              ></time-picker>
            </v-col>
          </v-row>
          <v-row v-if="reportData">
            <v-col
              ><h4>
                Number of received messages : {{ reportData.numberOfMessages }}
              </h4></v-col
            >
          </v-row>
          <v-row v-if="reportData">
            <v-col
              ><h4>
                INFO messages : {{ reportData.numberOfInformations }}
              </h4></v-col
            >
          </v-row>
          <v-row v-if="reportData">
            <v-col
              ><h4>
                WARNING messages : {{ reportData.numberOfWarnings }}
              </h4></v-col
            >
          </v-row>
          <v-row v-if="reportData">
            <v-col
              ><h4>ERROR messages : {{ reportData.numberOfErrors }}</h4></v-col
            >
          </v-row>
          <v-row v-if="reportData">
            <v-col
              ><h4>
                Alarms triggered : {{ reportData.numberOfDeviceAlarms }}
              </h4></v-col
            >
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue darken-1"
          text
          @click="
            clearForm();
            dialog = false;
          "
        >
          Close
        </v-btn>
        <v-btn color="blue darken-1" text @click="submitForm()">
          Generate report
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import DatePicker from "../dateAndTime/DatePicker.vue";
import TimePicker from "../dateAndTime/TimePicker.vue";

import { reportService } from "../../service/reportService";

export default {
  components: { DatePicker, TimePicker },
  name: "ReportDialog",
  props: ["deviceIds"],
  data() {
    return {
      dialog: false,
      startDate: null,
      startTime: null,
      endDate: null,
      endTime: null,
      reportData: null,
    };
  },
  mounted() {
    this.$root.$on("reportStartDateSaved", (date) => {
      this.startDate = date;
    });
    this.$root.$on("reportEndDateSaved", (date) => {
      this.endDate = date;
    });
    this.$root.$on("reportStartTimeSaved", (time) => {
      this.startTime = time;
    });
    this.$root.$on("reportEndTimeSaved", (time) => {
      this.endTime = time;
    });
  },
  methods: {
    submitForm() {
      let requestBody = {
        from: this.startDate + "T" + this.startTime,
        to: this.endDate + "T" + this.endTime,
        deviceIds: this.deviceIds,
      };
      reportService.getReport(requestBody).then((response) => {
        this.reportData = response.data;
      });
    },
    clearForm() {
      this.reportData = null;
    },
  },
};
</script>

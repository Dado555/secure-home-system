<template>
  <v-row>
    <v-col>
      <v-text-field label="Device name" v-model="deviceName"></v-text-field>
    </v-col>
    <v-col>
      <v-text-field label="Message content" v-model="message"></v-text-field>
    </v-col>
    <v-col
      ><v-select
        :items="messageTypes"
        label="Message type"
        v-model="messageType"
        item-text="name"
        item-value="value"
      ></v-select>
    </v-col>
    <v-col>
      <date-picker
        :title="'Start date'"
        :isStart="true"
        :fromReport="false"
      ></date-picker>
    </v-col>
    <v-col>
      <time-picker
        :title="'Start time'"
        :isStart="true"
        :fromReport="false"
      ></time-picker>
    </v-col>
    <v-col>
      <date-picker
        :title="'End date'"
        :isStart="false"
        :fromReport="false"
      ></date-picker>
    </v-col>
    <v-col>
      <time-picker
        :title="'End time'"
        :isStart="false"
        :fromReport="false"
      ></time-picker>
    </v-col>
    <v-col style="margin-top: 15px">
      <v-btn elevation="2" color="primary" @click="filter()">Filter</v-btn>
      <v-btn elevation="2" @click="resetParams()">Reset</v-btn>
    </v-col>
  </v-row>
</template>

<script>
import DatePicker from "../dateAndTime/DatePicker.vue";
import TimePicker from "../dateAndTime/TimePicker.vue";

export default {
  components: { DatePicker, TimePicker },
  name: "MessageSearchBar",
  data() {
    return {
      messageTypes: [
        { name: "INFO", value: "INFO" },
        { name: "WARNING", value: "WARNING" },
        { name: "ERROR", value: "ERROR" },
        { name: "ALL", value: null },
      ],
      messageType: null,
      deviceName: "",
      message: "",
      startDate: new Date(new Date().setDate(new Date().getDate() - 10))
        .toISOString()
        .substr(0, 10),
      startTime: "00:00",
      endDate: new Date(new Date().setDate(new Date().getDate() + 1))
        .toISOString()
        .substr(0, 10),
      endTime: "00:00",
    };
  },
  mounted() {
    this.$root.$on("startDateSaved", (date) => {
      this.startDate = date;
    });
    this.$root.$on("endDateSaved", (date) => {
      this.endDate = date;
    });
    this.$root.$on("startTimeSaved", (date) => {
      this.startTime = date;
    });
    this.$root.$on("endTimeSaved", (date) => {
      this.endTime = date;
    });
  },
  methods: {
    filter() {
      let parameters = {
        deviceName: this.deviceName,
        message: this.message,
        messageType: this.messageType,
        from: this.startDate + "T" + this.startTime,
        to: this.endDate + "T" + this.endTime,
      };
      this.$root.$emit("messageFilterParamsUpdated", parameters);
    },
    resetParams() {
      this.messageType = null;
      this.deviceName = "";
      this.message = "";
      this.startTime = "00:00";
      this.endTime = "00:00";
      this.startDate = new Date(new Date().setDate(new Date().getDate() - 10))
        .toISOString()
        .substr(0, 10);
      this.endDate = new Date(new Date().setDate(new Date().getDate() + 1))
        .toISOString()
        .substr(0, 10);
      this.filter();
      this.$root.$emit("resetDateTime");
    },
  },
};
</script>

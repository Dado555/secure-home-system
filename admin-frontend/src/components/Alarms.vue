<template>
  <v-container>
    <v-flex class="text-center">
      <h1>Alarms</h1>
    </v-flex>

    <v-row>
      <v-col cols="12">
        <v-expansion-panels>
          <v-expansion-panel>
            <v-expansion-panel-header>
              <strong><v-icon>mdi-filter</v-icon> Advanced filters</strong>
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <v-row>
                <v-col cols="12" md="4">
                  <v-select
                    label="Alarm types"
                    multiple
                    :items="alarmTypes"
                    item-text="text"
                    item-value="value"
                    item-color="color"
                    clearable
                    chips
                    deletable-chips
                    v-model="searchPayload.alarmTypes"
                  />
                </v-col>
                <v-col cols="6" md="4">
                  <v-datetime-picker
                    v-model="searchPayload.timestampFrom"
                    label="Timestamp from"
                    :time-picker-props="timeProps"
                    :dateFormat="dateFormat"
                    :timeFormat="timeDisplayFormat"
                  >
                    <template v-slot:dateIcon>
                      <v-icon> mdi-calendar </v-icon>
                    </template>
                    <template v-slot:timeIcon>
                      <v-icon> mdi-clock </v-icon>
                    </template>
                    <template v-slot:actions="{ parent }">
                      <v-row align="end" justify="end">
                        <v-spacer></v-spacer>
                        <v-col cols="4">
                          <v-btn depressed @click.native="parent.clearHandler"
                            >Clear</v-btn
                          >
                        </v-col>
                        <v-col cols="4">
                          <v-btn
                            depressed
                            color="primary"
                            @click="parent.okHandler"
                            >Ok</v-btn
                          >
                        </v-col>
                      </v-row>
                    </template>
                  </v-datetime-picker>
                </v-col>
                <v-col cols="6" md="4">
                  <v-datetime-picker
                    v-model="searchPayload.timestampTo"
                    label="Timestamp to"
                    :time-picker-props="timeProps"
                    :dateFormat="dateFormat"
                    :timeFormat="timeDisplayFormat"
                  >
                    <template v-slot:dateIcon>
                      <v-icon> mdi-calendar </v-icon>
                    </template>
                    <template v-slot:timeIcon>
                      <v-icon> mdi-clock </v-icon>
                    </template>
                    <template v-slot:actions="{ parent }">
                      <v-row align="end" justify="end">
                        <v-spacer></v-spacer>
                        <v-col cols="4">
                          <v-btn depressed @click.native="parent.clearHandler"
                            >Clear</v-btn
                          >
                        </v-col>
                        <v-col cols="4">
                          <v-btn
                            depressed
                            color="primary"
                            @click="parent.okHandler"
                            >Ok</v-btn
                          >
                        </v-col>
                      </v-row>
                    </template>
                  </v-datetime-picker>
                </v-col>
                <v-col cols="12" md="2">
                  <v-btn
                    @click="getAlarms()"
                    color="primary"
                    :style="{ 'margin-top': '5px' }"
                    >Confirm</v-btn
                  >
                </v-col>
              </v-row>
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-data-table
          :headers="headers"
          :loading="loading"
          :items="alarms"
          :options.sync="options"
          :server-items-length="totalAlarms"
        >
          <template v-slot:item.type="{ item }">
            <strong :style="{ color: logTypeColor(item.type) }">
              {{ item.type }}
            </strong>
          </template>
          <template v-slot:item.timestamp="{ item }">
            <p>
              {{ item.timestamp | formatDate2 }}
            </p>
          </template>
          <template v-slot:item.third="{ item }">
            <p v-if="item.type === 'IP_MALICIOUS'">
              ip: {{ item.alarm.ipAddress }}
            </p>
            <p v-if="item.type === 'MANY_REQUESTS'">
              ip: {{ item.alarm.ipAddress }}
            </p>
            <p v-if="item.type === 'LOG_ERROR'">
              ip: {{ item.alarm.ipAddress }}, message: {{ item.alarm.message }}
            </p>
            <p v-if="item.type === 'LOGIN_FAIL'">
              email: {{ item.alarm.userEmail }}
            </p>
            <p v-if="item.type === 'CUSTOM_LOG_RULE'">
              ip: {{ item.alarm.ipAddress }}, message: {{ item.alarm.message }}
            </p>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <br />
    <br />
    <h2>Custom Log Rules</h2>
    <v-row justify="start">
      <v-col md="1">
        <v-select
          v-model="logType"
          :items="logTypes"
          style="width: 300px"
          label="Log Type"
        ></v-select>
      </v-col>
      <v-col md="1">
        <v-text-field
          style="width: 300px"
          label="Log Substring"
          v-model="logSubstring"
        ></v-text-field>
      </v-col>
      <v-col md="1">
        <v-btn
          color="primary"
          small
          style="margin-top: 20px"
          @click="createNewCustomLogAlarm()"
          >Add new log alarm</v-btn
        >
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <!-- item-key="name" -->
        <v-data-table
          :headers="headersLogRules"
          :items="logRuleItems"
          class="elevation-1"
        >
          <template v-slot:item="row">
            <tr>
              <td>{{ row.item.type }}</td>
              <td v-if="row.item.substring !== ''">{{ row.item.substring }}</td>
              <td v-else><h3>NOT SPECIFIED</h3></td>
              <td align="right">
                <v-btn
                  class="mx-2"
                  dark
                  color="primary"
                  @click="deleteLogRule(row.item.id)"
                >
                  Delete
                </v-btn>
              </td>
            </tr>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { alarmService } from "../service/alarmService.js";

export default {
  name: "Alarms",
  data: () => {
    let thirdHeaderText = "";
    return {
      logRuleItems: [],
      headersLogRules: [
        {
          text: "Type",
          align: "start",
          sortable: false,
        },
        {
          text: "Message substring",
          align: "start",
          sortable: false,
        },
        {
          text: "Action",
          align: "end",
          sortable: false,
        },
      ],
      logTypes: ["ERROR", "INFO", "DEBUG", "WARN"],
      logType: "INFO",
      logSubstring: "",
      intervalId: null,
      timeProps: {
        format: "24hr",
      },
      dateFormat: "dd.MM.yyyy.",
      timeDisplayFormat: "HH:mm",
      searchPayload: {
        alarmTypes: [
          "LOG_ERROR",
          "LOGIN_FAIL",
          "IP_MALICIOUS",
          "MANY_REQUESTS",
          "CUSTOM_LOG_RULE",
        ],
        timestampFrom: null,
        timestampTo: null,
      },
      alarmTypes: [
        { text: "LOGIN_FAIL", value: "LOGIN_FAIL", color: "green" },
        { text: "LOG_ERROR", value: "LOG_ERROR", color: "red" },
        { text: "IP_MALICIOUS", value: "IP_MALICIOUS", color: "blue" },
        { text: "MANY_REQUESTS", value: "MANY_REQUESTS", color: "yellow" },
        { text: "CUSTOM_LOG_RULE", value: "CUSTOM_LOG_RULE", color: "orange" },
      ],

      totalAlarms: 0,
      alarms: [],
      loading: false,
      options: {},
      headers: [
        {
          text: "Type",
          align: "start",
          sortable: true,
          value: "type",
        },
        {
          text: "Timestamp",
          align: "start",
          sortable: true,
          value: "timestamp",
        },
        {
          text: "More info",
          align: "start",
          sortable: true,
          value: "third",
        },
      ],
    };
  },
  mounted() {
    this.intervalId = setInterval(this.getAlarms, 5000);
    this.fetchAllLogRules();
  },
  beforeDestroy() {
    clearInterval(this.intervalId);
  },
  methods: {
    deleteLogRule(id) {
      alarmService.deleteLogAlarm(id).then((response) => {
        this.fetchAllLogRules();
      });
    },
    fetchAllLogRules() {
      alarmService.getAllLogAlarms().then((response) => {
        this.logRuleItems = response.data;
      });
    },
    createNewCustomLogAlarm() {
      let requestBody = {
        type: this.logType,
        substring: this.logSubstring,
      };
      alarmService.createNewLogAlarm(requestBody).then((response) => {
        this.logSubstring = "";
        this.logType = "INFO";
        this.fetchAllLogRules();
      });
    },
    getAlarms() {
      const { sortBy, sortDesc, page, itemsPerPage } = this.options;
      let sortCriteria = sortBy.length === 0 ? "timestamp" : sortBy;
      let sortDir;
      if (sortDesc.length !== 0)
        if (sortDesc + "" === "false") sortDir = "asc";
        else sortDir = "desc";
      else sortDir = "desc";

      let sort = `${sortCriteria},${sortDir}`;

      let from;
      if (this.searchPayload.timestampFrom === null) from = "";
      else {
        let offset =
          this.searchPayload.timestampFrom.getTimezoneOffset() * 60000; //offset in milliseconds
        from = new Date(
          this.searchPayload.timestampFrom - offset
        ).toISOString();
      }

      let to;
      if (this.searchPayload.timestampTo === null) to = "";
      else {
        let offset = this.searchPayload.timestampTo.getTimezoneOffset() * 60000; //offset in milliseconds
        to = new Date(this.searchPayload.timestampTo - offset).toISOString();
      }
      this.getAlarmsFromApi(
        page - 1,
        itemsPerPage,
        sort,
        this.searchPayload.alarmTypes,
        from,
        to
      );
    },
    getAlarmsFromApi(page, size, sort, alarmType, timestampFrom, timestampTo) {
      this.loading = true;
      console.log("fetching alarms");
      alarmService
        .getAllAlarmsByType(
          page,
          size,
          sort,
          alarmType,
          timestampFrom,
          timestampTo
        )
        .then((response) => {
          const page = response.data;
          this.alarms = page.content;
          this.totalAlarms = page.totalElements;
          this.loading = false;
        });
    },
    logTypeColor(type) {
      if (type === "LOGIN_FAIL") return "green";
      else if (type === "LOG_ERROR") return "red";
      else if (type === "IP_MALICIOUS") return "blue";
      else if (type === "MANY_REQUESTS") return "yellow";
    },
  },
  watch: {
    options: {
      handler() {
        this.getAlarms();
      },
      deep: true,
    },
  },
};
</script>

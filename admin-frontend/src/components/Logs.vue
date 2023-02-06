<template>
  <v-container>
    <v-flex class="text-center">
      <h1>Logs</h1>
    </v-flex>
    <v-row align="center" justify="center">
      <v-col cols="12" md="10">
        <v-text-field
          label="Enter query..."
          hint="Applies to ip address, message, component, username and method parameters"
          persistent-hint
          v-model="searchPayload.query"
        />
      </v-col>

      <v-col cols="12" md="2">
        <v-btn @click="getLogs" color="primary" :style="{ 'margin-top': '5px' }"
          >Confirm</v-btn
        >
      </v-col>
    </v-row>
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
                    label="Log types"
                    multiple
                    :items="logTypes"
                    item-text="text"
                    item-value="value"
                    item-color="color"
                    clearable
                    chips
                    deletable-chips
                    v-model="searchPayload.logTypes"
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
          :items="logs"
          :options.sync="options"
          :server-items-length="totalLogs"
          class="elevation-1"
        >
          <template v-slot:item.timestamp="{ item }">
            <p>
              {{ item.timestamp | formatDate2 }}
            </p>
          </template>
          <template v-slot:item.logType="{ item }">
            <strong :style="{ color: logTypeColor(item.logType) }">
              {{ item.logType }}
            </strong>
          </template>
          <template v-slot:item.component="{ item }">
            <p>
              {{ item.component | getShortenedComponent }}
            </p>
          </template>
          <template v-slot:item.methodParams="{ item }">
            <p>
              <v-menu offset-y>
                <template v-slot:activator="{ on, attrs }">
                  <v-btn
                    color="primary"
                    v-bind="attrs"
                    v-on="on"
                    :disabled="item.methodParams.length === 0"
                    :style="{ marginTop: '15px' }"
                  >
                    View method params
                  </v-btn>
                </template>
                <v-list>
                  <v-list-item
                    v-for="(param, index) in item.methodParams"
                    :key="index"
                  >
                    <v-list-item-title>{{ param }}</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>
            </p>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { logService } from "../service/logService";
export default {
  name: "Logs",
  data: () => {
    return {
      timeProps: {
        format: "24hr",
      },
      dateFormat: "dd.MM.yyyy.",
      timeDisplayFormat: "HH:mm",
      searchPayload: {
        query: "",
        logTypes: ["ERROR", "INFO"],
        regex: false,
        timestampFrom: null,
        timestampTo: null,
      },
      logTypes: [
        { text: "INFO", value: "INFO", color: "green" },
        { text: "ERROR", value: "ERROR", color: "red" },
      ],

      totalLogs: 0,
      logs: [],
      loading: false,
      options: {},
      headers: [
        {
          text: "Message",
          align: "start",
          sortable: true,
          value: "message",
        },
        {
          text: "Timestamp",
          align: "start",
          sortable: true,
          value: "timestamp",
        },
        {
          text: "IP Address",
          align: "start",
          sortable: true,
          value: "ipAddress",
        },
        {
          text: "Log Type",
          align: "start",
          sortable: true,
          value: "logType",
        },
        {
          text: "Component",
          align: "start",
          sortable: true,
          value: "component",
        },
        {
          text: "Method parameters",
          align: "start",
          sortable: false,
          value: "methodParams",
        },
        {
          text: "Username",
          align: "start",
          sortable: true,
          value: "username",
        },
      ],
    };
  },
  mounted() {
    this.getLogs();
  },
  methods: {
    getLogs() {
      const { sortBy, sortDesc, page, itemsPerPage } = this.options;
      let sortCriteria = sortBy.length === 0 ? "timestamp" : sortBy;
      let sortDir;
      if (sortDesc.length !== 0)
        if (sortDesc + "" === "false") sortDir = "asc";
        else sortDir = "desc";
      else sortDir = "desc";

      let sort = `${sortCriteria},${sortDir}`;

      let from;
      if (this.searchPayload.timestampFrom === null) from = null;
      else {
        from = this.searchPayload.timestampFrom.toISOString();
      }

      let to;
      if (this.searchPayload.timestampTo === null) to = null;
      else {
        to = this.searchPayload.timestampTo.toISOString();
      }
      this.fetchLogsFromApi(
        page - 1,
        itemsPerPage,
        sort,
        this.searchPayload.query,
        this.searchPayload.logTypes,
        from,
        to
      );
    },
    fetchLogsFromApi(
      page,
      size,
      sort,
      query,
      logType,
      timestampFrom,
      timestampTo
    ) {
      // console.log("EO ME!");
      this.loading = true;
      logService
        .getAllLogs(
          page,
          size,
          sort,
          query,
          logType,
          timestampFrom,
          timestampTo
        )
        .then((response) => {
          const page = response.data;
          this.logs = page.content;
          this.totalLogs = page.totalElements;
          this.loading = false;
        });
    },
    logTypeColor(type) {
      if (type === "INFO") return "green";
      else if (type === "ERROR") return "red";
    },
  },
  watch: {
    options: {
      handler() {
        this.getLogs();
      },
      deep: true,
    },
  },
};
</script>

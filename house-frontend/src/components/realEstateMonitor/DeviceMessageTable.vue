<template>
  <v-row>
    <v-col>
      <v-flex>
        <h2 style="display: inline">Device messages</h2>
        <report-dialog :deviceIds="deviceIds"></report-dialog>
      </v-flex>
      <br />
      <message-search-bar></message-search-bar>
      <v-data-table
        :headers="headers"
        :items="messages"
        :options.sync="options"
        :server-items-length="totalMessages"
        :loading="loading"
        class="elevation-1"
      >
        <template v-slot:item="row">
          <tr>
            <td>{{ row.item.deviceName }}</td>
            <td v-if="row.item.messageType === 'INFO'" style="color: blue">
              {{ row.item.messageType }}
            </td>
            <td v-if="row.item.messageType === 'WARNING'" style="color: orange">
              {{ row.item.messageType }}
            </td>
            <td v-if="row.item.messageType === 'ERROR'" style="color: red">
              {{ row.item.messageType }}
            </td>
            <td>{{ row.item.message }}</td>
            <td>
              {{ row.item.timestamp | formatDate }}
            </td>
          </tr>
        </template>
      </v-data-table>
    </v-col>
  </v-row>
</template>

<script>
import { deviceMessagesService } from "../../service/deviceMessagesService";
import MessageSearchBar from "./MessageSearchBar.vue";
import ReportDialog from "./ReportDialog.vue";

export default {
  components: { MessageSearchBar, ReportDialog },
  name: "DeviceMessageTable",
  props: ["deviceIds", "refreshPeriod"],
  data() {
    return {
      polling: {},
      messages: [],
      totalMessages: 0,
      options: {},
      loading: true,
      filterParameters: {
        deviceName: "",
        messageType: null,
        message: "",
        from:
          new Date(new Date().setDate(new Date().getDate() - 10))
            .toISOString()
            .substr(0, 10) + "T00:00",
        to:
          new Date(new Date().setDate(new Date().getDate() + 1))
            .toISOString()
            .substr(0, 10) + "T00:00",
      },
      headers: [
        {
          text: "Device name",
          align: "start",
          sortable: false,
        },
        {
          text: "Message type",
          align: "start",
          sortable: false,
        },
        {
          text: "Message content",
          align: "start",
          sortable: false,
        },
        {
          text: "Timestamp",
          align: "start",
          sortable: false,
        },
      ],
    };
  },
  filters: {
    formatDate: (value) => {
      if (!value) return "";

      let dateTime = value.split("T");
      let date = dateTime[0];
      let time = dateTime[1].split(".")[0];

      let dateTokens = date.split("-");

      return (
        dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0] + " " + time
      );
    },
  },
  beforeDestroy() {
    clearInterval(this.polling);
  },
  watch: {
    options: {
      handler() {
        this.fetchDeviceMessages();
      },
      deep: true,
    },
    deviceIds: {
      handler() {
        this.fetchDeviceMessages();
      },
    },
    refreshPeriod: {
      handler() {
        this.polling = setInterval(() => {
          this.fetchDeviceMessages();
        }, this.refreshPeriod * 1000);
      },
    },
  },
  mounted() {
    this.$root.$on("messageFilterParamsUpdated", (parameters) => {
      this.filterParameters = parameters;
      this.fetchDeviceMessages();
    });
  },
  methods: {
    fetchDeviceMessages() {
      this.loading = true;
      const { sortBy, sortDesc, page, itemsPerPage } = this.options;
      deviceMessagesService
        .getAllForRealEstate(page - 1, itemsPerPage, {
          deviceIds: this.deviceIds,
          deviceName: this.filterParameters.deviceName,
          messageType: this.filterParameters.messageType,
          message: this.filterParameters.message,
          from: this.filterParameters.from,
          to: this.filterParameters.to,
        })
        .then((response) => {
          this.messages = response.data.content;
          this.totalMessages = response.data.totalElements;
          this.loading = false;
        });
    },
  },
};
</script>

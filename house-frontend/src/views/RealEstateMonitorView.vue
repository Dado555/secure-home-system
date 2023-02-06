<template>
  <v-container>
    <v-row>
      <v-col>
        <v-flex class="text-center">
          <h1>Real Estate Dashboard - {{ realEstate.name }}</h1>
        </v-flex>
        <br />
        <br />
        <h2>Installed devices</h2>
        <v-data-table
          :headers="headers"
          :items="devices"
          :options.sync="options"
          :server-items-length="totalDevices"
          :loading="loading"
          class="elevation-1"
        >
          <template v-slot:item="row">
            <tr>
              <td>{{ row.item.id }}</td>
              <td>{{ row.item.name }}</td>
              <td>{{ row.item.regexFilter }}</td>
            </tr>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <br />
    <br />
    <device-message-table
      :deviceIds="deviceIds"
      :refreshPeriod="realEstate.deviceReadingPeriod"
    ></device-message-table>
  </v-container>
</template>

<script>
import { deviceService } from "../service/deviceService";
import { realEstateService } from "../service/realestateService";

import DeviceMessageTable from "../components/realEstateMonitor/DeviceMessageTable.vue";

export default {
  name: "RealEstateMonitorView",
  components: { DeviceMessageTable },
  data() {
    return {
      realEstate: {},
      devices: [],
      totalDevices: 0,
      options: {},
      loading: true,
      deviceIds: [],
      headers: [
        {
          text: "Device ID",
          align: "start",
          sortable: false,
        },
        {
          text: "Device name",
          align: "start",
          sortable: false,
        },
        {
          text: "Regex filter",
          align: "start",
          sortable: false,
        },
      ],
    };
  },
  watch: {
    options: {
      handler() {
        this.fetchDevices();
      },
      deep: true,
    },
  },
  mounted() {
    realEstateService
      .getSingleRealEstate(this.$route.params.id)
      .then((response) => {
        this.realEstate = response.data;
      });
  },
  methods: {
    fetchDevices() {
      this.loading = true;
      const { sortBy, sortDesc, page, itemsPerPage } = this.options;
      deviceService
        .getAllForRealEstate(page - 1, itemsPerPage, this.$route.params.id)
        .then((response) => {
          this.devices = response.data.content;
          for (let device of this.devices) {
            this.deviceIds.push(device.id);
          }
          this.totalDevices = response.data.totalElements;
          this.loading = false;
        });
    },
  },
};
</script>

<template>
  <v-container>
    <v-row>
      <v-col>
        <v-flex class="text-center">
          <h1>Configure devices for {{ realEstate.name }}</h1></v-flex
        >
        <br />
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
              <td align="right">
                <device-form :deviceData="row.item" :edit="true"></device-form>
              </td>
              <td align="right">
                <v-btn
                  class="mx-2"
                  dark
                  color="primary"
                  @click="deleteDevice(row.item.id)"
                >
                  Delete
                </v-btn>
              </td>
            </tr>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <br />
    <br />
    <v-row align="center" justify="center">
      <device-form :edit="false"></device-form>
    </v-row>
    <v-snackbar v-model="snackbar" :timeout="timeout">
      {{ text }}

      <template v-slot:action="{ attrs }">
        <v-btn color="blue" text v-bind="attrs" @click="snackbar = false">
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import { realEstateService } from "../service/realEstateService";
import { deviceService } from "../service/deviceService";

import DeviceForm from "../components/devices/DeviceForm.vue";

export default {
  name: "DevicesView",
  components: { DeviceForm },
  data() {
    return {
      text: "",
      snackbar: false,
      timeout: 2000,
      realEstate: {},
      devices: [],
      totalDevices: 0,
      options: {},
      loading: true,
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
        {
          text: "Action",
          align: "end",
          sortable: false,
        },
        {
          text: "Action",
          align: "end",
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
      .getSingleRealEstate(this.$route.params.realEstateId)
      .then((response) => {
        this.realEstate = response.data;
      });

    this.$root.$on("addDeviceFormSubmitted", (requestbody) => {
      requestbody.realEstateId = this.realEstate.id;
      deviceService
        .createDevice(requestbody)
        .then((response) => {
          this.fetchDevices();
        })
        .catch((err) => {
          this.text = err.response.data.message;
          this.snackbar = true;
        });
    });
    this.$root.$on("configureDeviceFormSubmitted", (data) => {
      deviceService
        .updateDevice(data.request, data.deviceId)
        .then((response) => {
          this.devices = this.devices.map((device) => {
            if (device.id === data.deviceId) {
              device.name = data.request.name;
              device.regexFilter = data.request.regexFilter;
            }
            return device;
          });
        })
        .catch((err) => {
          this.text = err.response.data.message;
          this.snackbar = true;
        });
    });
  },
  methods: {
    fetchDevices() {
      this.loading = true;
      const { sortBy, sortDesc, page, itemsPerPage } = this.options;
      deviceService
        .getAllForRealEstate(
          page - 1,
          itemsPerPage,
          this.$route.params.realEstateId
        )
        .then((response) => {
          this.devices = response.data.content;
          this.totalDevices = response.data.totalElements;
          this.loading = false;
          console.log(this.devices);
        });
    },
    deleteDevice(id) {
      deviceService.deleteDevice(id).then((response) => {
        this.devices = this.devices.filter((device) => device.id !== id);
      });
    },
  },
};
</script>

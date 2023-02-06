<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-flex class="text-center">
          <h1>Alarms</h1>
        </v-flex>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-simple-table>
          <template v-slot:default>
            <thead>
              <tr>
                <th class="text-left">Device id</th>
                <th class="text-left">Device message</th>
                <th class="text-left">Device name</th>

                <th class="text-left">Timestamp</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(alarm, index) in alarms"
                :key="alarm.id"
                :class="{ newAlarm: index < colorIndex }"
              >
                <td>{{ alarm.deviceId }}</td>
                <td>{{ alarm.deviceMessage }}</td>
                <td>{{ alarm.deviceName }}</td>
                <td>{{ alarm.timestamp | formatDate }}</td>
              </tr>
            </tbody>
          </template>
        </v-simple-table>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { alarmService } from "../service/alarmsService";
export default {
  name: "Alarms",
  data: () => {
    return {
      colorIndex: -1,
      alarms: [],
      intervalId: null,
    };
  },
  mounted() {
    this.intervalId = setInterval(this.getAlarms, 5000);
  },
  beforeDestroy() {
    clearInterval(this.intervalId);
  },
  methods: {
    getAlarms() {
      let previousAlarmsLength = this.alarms.length;
      alarmService.getAlarms().then((response) => {
        this.alarms = response.data;
        this.colorIndex = this.alarms.length - previousAlarmsLength;
      });
    },
  },
};
</script>

<style scoped>
.newAlarm {
  color: red;
  font-weight: 800;
}
</style>

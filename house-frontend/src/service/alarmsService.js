const axios = require("axios");

class AlarmService {
  getAlarms() {
    return axios.get(`${process.env.VUE_APP_BASE_PATH}/alarms`);
  }
}

export const alarmService = new AlarmService();

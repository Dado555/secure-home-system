const axios = require("axios");

class AlarmService {
  getAllAlarmsByType(page, size, sort, alarmTypes, timestampFrom, timeStampTo) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/alarms/byType?alarmTypes=${alarmTypes}&timestampFrom=${timestampFrom}&timestampTo=${timeStampTo}&page=${page}&size=${size}&sort=${sort}`
    );
  }

  getAllAlarms(page, size, sort) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/alarms?page=${page}&size=${size}&sort=${sort}`
    );
  }

  createNewLogAlarm(payload) {
    return axios.post(
      `${process.env.VUE_APP_BASE_PATH}/alarms/log-alarm`,
      payload
    );
  }

  getAllLogAlarms(payload) {
    return axios.get(`${process.env.VUE_APP_BASE_PATH}/alarms/log-alarm`);
  }

  deleteLogAlarm(id) {
    return axios.delete(
      `${process.env.VUE_APP_BASE_PATH}/alarms/log-alarm/${id}`
    );
  }
}

export const alarmService = new AlarmService();

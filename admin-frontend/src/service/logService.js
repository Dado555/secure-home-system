const axios = require("axios");

class LogService {
  getAllLogs(page, size, sort, query, logTypes, timestampFrom, timeStampTo) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/logs?query=${encodeURIComponent(
        query
      )}&logTypes=${logTypes}&timestampFrom=${timestampFrom}&timestampTo=${timeStampTo}&page=${page}&size=${size}&sort=${sort}`
    );
  }
}

export const logService = new LogService();

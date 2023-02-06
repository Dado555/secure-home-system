const axios = require("axios");

class ReportService {
  getReport(requestBody) {
    return axios.post(`${process.env.VUE_APP_BASE_PATH}/reports`, requestBody);
  }
}

export const reportService = new ReportService();

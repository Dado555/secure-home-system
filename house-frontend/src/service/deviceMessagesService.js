const axios = require("axios");

class DeviceMessagesService {
  getAllForRealEstate(page, size, requestBody) {
    return axios.post(
      `${process.env.VUE_APP_BASE_PATH}/messages/retrieveFiltered?page=${page}&size=${size}&sort=timestamp,desc`,
      requestBody
    );
  }
}

export const deviceMessagesService = new DeviceMessagesService();

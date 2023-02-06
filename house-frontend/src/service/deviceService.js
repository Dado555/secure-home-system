const axios = require("axios");

class DeviceService {
  getAllForRealEstate(page, size, realEstateId) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/devices/real-estate/${realEstateId}?page=${page}&size=${size}`
    );
  }
}

export const deviceService = new DeviceService();

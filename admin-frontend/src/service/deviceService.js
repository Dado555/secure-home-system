const axios = require("axios");

class DeviceService {
  getAllForRealEstate(page, size, realEstateId) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/devices/real-estate/${realEstateId}?page=${page}&size=${size}`
    );
  }

  createDevice(requestBody) {
    return axios.post(`${process.env.VUE_APP_BASE_PATH}/devices/`, requestBody);
  }

  updateDevice(requestBody, deviceId) {
    return axios.put(
      `${process.env.VUE_APP_BASE_PATH}/devices/${deviceId}`,
      requestBody
    );
  }

  deleteDevice(id) {
    return axios.delete(`${process.env.VUE_APP_BASE_PATH}/devices/${id}`);
  }
}

export const deviceService = new DeviceService();

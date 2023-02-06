const axios = require("axios");

class RealEstateService {
  getAllForHousehold(page, size, householdId) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/real-estate/household/${householdId}?page=${page}&size=${size}`
    );
  }

  getSingleRealEstate(id) {
    return axios.get(`${process.env.VUE_APP_BASE_PATH}/real-estate/${id}`);
  }

  deleteRealEstate(id) {
    return axios.delete(`${process.env.VUE_APP_BASE_PATH}/real-estate/${id}`);
  }
}

export const realEstateService = new RealEstateService();

const axios = require("axios");

class RealEstateService {
  getRealEstatesForUser(userId) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/real-estate/tenant/${userId}`
    );
  }

  getSingleRealEstate(id) {
    return axios.get(`${process.env.VUE_APP_BASE_PATH}/real-estate/${id}`);
  }

  createRealEstate(requestBody) {
    return axios.post(
      `${process.env.VUE_APP_BASE_PATH}/real-estate/create`,
      requestBody
    );
  }

  updateRealEstate(requestBody) {
    return axios.put(
      `${process.env.VUE_APP_BASE_PATH}/real-estate/update`,
      requestBody
    );
  }

  updateRealEstateVisibility(requestBody, type) {
    return axios.put(
      `${process.env.VUE_APP_BASE_PATH}/real-estate/visibility/` + type,
      requestBody
    );
  }

  updateRealEstateName(requestBody) {
    return axios.put(
      `${process.env.VUE_APP_BASE_PATH}/real-estate/update-name`,
      requestBody
    );
  }
}

export const realEstateService = new RealEstateService();

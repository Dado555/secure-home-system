const axios = require("axios");

class HouseholdService {
  getAllHouseholds(page, size) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/households?page=${page}&size=${size}`
    );
  }

  deleteHousehold(id) {
    return axios.delete(`${process.env.VUE_APP_BASE_PATH}/households/${id}`);
  }
}

export const householdService = new HouseholdService();

const axios = require("axios");

class HouseholdService {
  getHouseholdForUser(userId) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/households/user/${userId}`
    );
  }
}

export const householdService = new HouseholdService();

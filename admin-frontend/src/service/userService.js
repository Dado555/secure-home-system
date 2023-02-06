const axios = require("axios");

class UserService {
  getTenantsForHousehold(page, size, householdId) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/users/household/${householdId}?page=${page}&size=${size}`
    );
  }

  deleteUser(id) {
    return axios.delete(`${process.env.VUE_APP_BASE_PATH}/users/${id}`);
  }

  updateTenantRole(requestBody) {
    return axios.put(
      `${process.env.VUE_APP_BASE_PATH}/users/tenant/role`,
      requestBody
    );
  }

  changeDefaultPassword(userId, payload) {
    return axios.put(
      `${process.env.VUE_APP_BASE_PATH}/users/${userId}/change-default-password`,
      payload
    );
  }
}

export const userService = new UserService();

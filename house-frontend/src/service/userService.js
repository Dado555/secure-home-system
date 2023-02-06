const axios = require("axios");

class UserService {
  getTenantsForHousehold(page, size, householdId) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/users/household/${householdId}?page=${page}&size=${size}`
    );
  }

  getTenantsForHouseholdId(householdId) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/users/household-id/${householdId}`
    );
  }

  deleteUser(id) {
    return axios.delete(`${process.env.VUE_APP_BASE_PATH}/users/${id}`);
  }

  upgradeUserRole(id) {
    return axios.put(
      `${process.env.VUE_APP_BASE_PATH}/users/tenant/role/${id}`
    );
  }

  updateTenantRole(requestBody) {
    return axios.put(
      `${process.env.VUE_APP_BASE_PATH}/users/tenant/role`,
      requestBody
    );
  }

  updateTenant(requestBody, tenantId) {
    return axios.put(
      `${process.env.VUE_APP_BASE_PATH}/users/tenant/${tenantId}`,
      requestBody
    );
  }

  createTenant(requestBody) {
    return axios.post(
      `${process.env.VUE_APP_BASE_PATH}/users/tenant`,
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

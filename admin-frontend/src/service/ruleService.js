const axios = require("axios");

class RuleService {
  getTemperatureRules() {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/rules/temperature-rules`
    );
  }

  getFrequencyRules() {
    return axios.get(`${process.env.VUE_APP_BASE_PATH}/rules/frequency-rules`);
  }

  createFrequencyRule(rule) {
    return axios.post(
      `${process.env.VUE_APP_BASE_PATH}/rules/frequency-rules`,
      rule
    );
  }

  createTemperatureRule(rule) {
    return axios.post(
      `${process.env.VUE_APP_BASE_PATH}/rules/temperature-rules`,
      rule
    );
  }
}

export const ruleService = new RuleService();

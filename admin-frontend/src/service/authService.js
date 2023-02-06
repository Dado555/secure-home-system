import jwtDecode from "jwt-decode";

const axios = require("axios");

class AuthService {
  login(payload) {
    return axios.post(
      `${process.env.VUE_APP_BASE_PATH}/users/authenticate`,
      payload
    );
  }

  setToken(token) {
    sessionStorage.setItem("jwt", token);
  }

  roles() {
    let jwt = sessionStorage.getItem("jwt");
    if (jwt) {
      let token = jwtDecode(jwt);
      return token.roles;
    } else return "";
  }

  getDecodedToken() {
    let jwt = sessionStorage.getItem("jwt");
    if (jwt) {
      return jwtDecode(jwt);
    } else return undefined;
  }

  userLoggedIn() {
    return sessionStorage.getItem("jwt") !== null;
  }

  userId() {
    let jwt = sessionStorage.getItem("jwt");
    if (jwt) {
      let token = jwtDecode(jwt);
      return token.userId;
    } else return -1;
  }

  defaultPasswordChanged() {
    let jwt = sessionStorage.getItem("jwt");
    if (jwt) {
      let token = jwtDecode(jwt);
      return token.defaultPasswordChanged;
    } else return "";
  }

  logout() {
    return axios.get(`${process.env.VUE_APP_BASE_PATH}/users/logout`);
  }
}

export const authService = new AuthService();

const axios = require("axios");

class CertificateService {
  getAllCertificates() {
    return axios.get(`${process.env.VUE_APP_BASE_PATH}/certificates`);
  }

  getCertificate(alias) {
    return axios.get(`${process.env.VUE_APP_BASE_PATH}/certificates/${alias}`);
  }

  revokeCertificate(payload) {
    return axios.post(
      `${process.env.VUE_APP_BASE_PATH}/certificates/revoke`,
      payload
    );
  }

  validateCertificate(alias) {
    return axios.get(
      `${process.env.VUE_APP_BASE_PATH}/certificates/valid/${alias}`
    );
  }

  verifyCertificate(certificateFile) {
    const formData = new FormData();
    formData.append("cerFile", certificateFile);
    const config = {
      headers: {
        "content-type": "multipart/form-data",
      },
    };
    return axios.post(
      `${process.env.VUE_APP_BASE_PATH}/certificates/valid-cer`,
      formData,
      config
    );
  }

  async downloadCertificate(alias, token) {
    let message = "";
    try {
      let response = await axios.get(
        `${process.env.VUE_APP_BASE_PATH}/certificates/download-certificate?alias=${alias}&token=${token}`,
        {
          responseType: "blob",
        }
      );
      this.initiateDownload(response, "cer", alias);
      message = "Certificate downloaded successfully.";
    } catch (error) {
      let errorText = await error.response.data.text();
      let errorObj = JSON.parse(errorText);
      message = errorObj.message;
    }
    return message;
  }

  async downloadKey(alias, password, token) {
    let message = "";
    try {
      let response = await axios.post(
        `${process.env.VUE_APP_BASE_PATH}/certificates/download-key`,
        { alias, password, token },
        {
          responseType: "blob",
        }
      );
      this.initiateDownload(response, "key", alias);
      message = "Key downloaded successfully.";
    } catch (error) {
      let errorText = await error.response.data.text();
      let errorObj = JSON.parse(errorText);
      message = errorObj.message;
    }
    return message;
  }

  initiateDownload(response, extension, alias) {
    const blob = new Blob([response.data], {
      type: "application/" + extension,
    });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = `${alias}-secure-home-${extension}.${extension}`;
    link.click();
    URL.revokeObjectURL(link.href);
  }

  determineResponseType(req) {
    let answer = "arraybuffer";
    if (req.headers.accept) {
      const match = /.+\/(.+)/gi.exec(req.headers.accept);
      if (
        ["javascript", "json", "plain", "csv", "xml", "css", "html"].some(
          (st) => match[1] == st
        )
      ) {
        answer = match[1];
      }
    }
    return answer;
  }

  extractResponseData(res) {
    let answer = res.data;
    if (res.headers["content-type"]) {
      const match = /.+\/(.+)/gi.exec(res.headers["content-type"]);
      if (
        ["javascript", "json", "plain", "csv", "xml", "css", "html"].some(
          (st) => match[1] == st
        )
      ) {
        answer = answer.toString("utf-8");
      }
    }
    return answer;
  }

  createRequest(requestDTO) {
    return axios.post(`${process.env.VUE_APP_BASE_PATH}/requests`, requestDTO);
  }

  editRequest(requestDTO, requestId) {
    return axios.put(
      `${process.env.VUE_APP_BASE_PATH}/requests/` + requestId,
      requestDTO
    );
  }

  getAllCertificateRequests() {
    return axios.get(`${process.env.VUE_APP_BASE_PATH}/requests/getAll`);
  }

  approveCertificate(id) {
    return axios.post(
      `${process.env.VUE_APP_BASE_PATH}/requests/approve/` + id
    );
  }
}

export const certificateService = new CertificateService();

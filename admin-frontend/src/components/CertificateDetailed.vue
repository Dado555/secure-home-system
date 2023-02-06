<template>
  <v-container>
    <h1>
      Certificate: {{ certificate.alias }}
      <v-chip v-if="!certificateValid" color="error">INVALID</v-chip>
    </h1>
    <div
      v-if="
        certificate.alias === 'intermediate_ca' ||
        certificate.alias === 'root_ca'
      "
    >
      This certificate cannot be revoked by admins.
    </div>
    <div v-else>
      <v-menu offset-y v-if="certificateValid">
        <template v-slot:activator="{ on, attrs }">
          <v-btn
            color="error"
            dark
            v-bind="attrs"
            v-on="on"
            :loading="revocationBtnLoading"
          >
            Revoke
          </v-btn>
        </template>
        <v-list>
          <v-list-item
            v-for="(item, index) in certificateRevocationReasons"
            :key="index"
            @click="revoke(item.value)"
          >
            <v-list-item-title>{{ item.text }}</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>

      <p v-else>Reason for invalidity: {{ certificateInvalidityMessage }}</p>
    </div>
    <br />
    <br />
    <table class="table-content">
      <tr>
        <th colspan="2">General info</th>
      </tr>
      <tr>
        <td><b>Issued</b></td>
        <td>{{ certificate.certificate.validity.notBefore }}</td>
      </tr>
      <tr>
        <td><b>Expires</b></td>
        <td>{{ certificate.certificate.validity.notAfter }}</td>
      </tr>
      <tr>
        <td><b>Serial Number</b></td>
        <td>{{ certificate.certificate.serialNumber }}</td>
      </tr>
      <tr>
        <td><b>Subject is CA</b></td>
        <td>{{ isCA }}</td>
      </tr>
    </table>
    <br />
    <br />
    <table class="table-content">
      <tr>
        <th colspan="2">Subject</th>
      </tr>
      <tr>
        <td><b>Email</b></td>
        <td>{{ certificate.certificate.subject.attributes[8].value }}</td>
      </tr>
      <tr>
        <td><b>Common name</b></td>
        <td>{{ certificate.certificate.subject.attributes[0].value }}</td>
      </tr>
      <tr v-if="san !== ''">
        <td><b>Subject alternative name</b></td>
        <td>{{ san }}</td>
      </tr>
      <tr>
        <td><b>Organization</b></td>
        <td>{{ certificate.certificate.subject.attributes[3].value }}</td>
      </tr>
      <tr>
        <td><b>OrganizationUnit</b></td>
        <td>{{ certificate.certificate.subject.attributes[4].value }}</td>
      </tr>
      <tr>
        <td><b>Country</b></td>
        <td>{{ certificate.certificate.subject.attributes[5].value }}</td>
      </tr>
      <tr>
        <td><b>State/Province</b></td>
        <td>{{ certificate.certificate.subject.attributes[6].value }}</td>
      </tr>
      <tr>
        <td><b>City</b></td>
        <td>{{ certificate.certificate.subject.attributes[7].value }}</td>
      </tr>
    </table>
    <br />
    <br />
    <table class="table-content">
      <tr>
        <th colspan="2">Issuer</th>
      </tr>
      <tr>
        <td><b>Email</b></td>
        <td>{{ certificate.certificate.issuer.attributes[8].value }}</td>
      </tr>
      <tr>
        <td><b>Common name</b></td>
        <td>{{ certificate.certificate.issuer.attributes[0].value }}</td>
      </tr>
      <tr>
        <td><b>Organization</b></td>
        <td>{{ certificate.certificate.issuer.attributes[3].value }}</td>
      </tr>
      <tr>
        <td><b>OrganizationUnit</b></td>
        <td>{{ certificate.certificate.issuer.attributes[4].value }}</td>
      </tr>
      <tr>
        <td><b>Country</b></td>
        <td>{{ certificate.certificate.issuer.attributes[5].value }}</td>
      </tr>
      <tr>
        <td><b>State</b></td>
        <td>{{ certificate.certificate.issuer.attributes[6].value }}</td>
      </tr>
      <tr>
        <td><b>City</b></td>
        <td>{{ certificate.certificate.issuer.attributes[7].value }}</td>
      </tr>
    </table>
    <br />
    <br />
    <table class="table-content">
      <tr>
        <th colspan="2">Certificate Extensions</th>
      </tr>
      <tr v-if="subjectKeyIdentifier !== ''">
        <td><b>Subject key identifier</b></td>
        <td>0x{{ subjectKeyIdentifier }}</td>
      </tr>
      <tr v-if="certificate.aki !== ''">
        <td><b>Authority key identifier</b></td>
        <td>0x{{ certificate.aki }}</td>
      </tr>
      <tr v-if="keyUsages !== ''">
        <td><b>Key usages</b></td>
        <td>{{ keyUsages }}</td>
      </tr>
      <tr v-if="extKeyUsages !== ''">
        <td><b>Extended key usages</b></td>
        <td>{{ extKeyUsages }}</td>
      </tr>
    </table>
    <br />
    <br />
    <br />
    <br />
    <v-snackbar v-model="snackbar" :timeout="timeout">
      {{ snackbarMessage }}

      <template v-slot:action="{ attrs }">
        <v-btn color="blue" text v-bind="attrs" @click="snackbar = false">
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import { certificateService } from "../service/certificateService";
var pki = require("node-forge").pki;

export default {
  name: "CertificateList",
  data() {
    return {
      revocationBtnLoading: false,
      snackbar: false,
      timeout: 2000,
      snackbarMessage: "",
      certificateRevocationReasons: [
        { value: "UNSPECIFIED", text: "Unspecified" },
        { value: "KEY_COMPROMISE", text: "Key compromise" },
        { value: "CA_COMPROMISE", text: "CA compromise" },
        { value: "AFFILIATION_CHANGED", text: "Affiliation changed" },
        { value: "SUPERSEDED", text: "Superseded" },
        { value: "CESSATION_OF_OPERATION", text: "Cessation of operation" },
        { value: "CERTIFICATE_HOLD", text: "Certificate hold" },
        { value: "PRIVILEGE_WITHDRAWN", text: "Privilege withdrawn" },
        { value: "AA_COMPROMISE", text: "AA compromise" },
      ],
      revocationPayload: {
        certificateAlias: "",
        reason: "",
      },
      certificateValid: true,
      certificateInvalidityMessage: "",
      certificate: {
        certificate: {
          validity: {},
          subject: {
            attributes: [
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
            ],
          },
          issuer: {
            attributes: [
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
              { value: "" },
            ],
          },
        },
      },
      san: "",
      isCA: false,
      keyUsages: "",
      extKeyUsages: "",
      subjectKeyIdentifier: "",
    };
  },
  mounted() {
    certificateService
      .validateCertificate(this.$route.params.alias)
      .then((response) => {
        const data = response.data;
        this.certificateValid = data.certificateValid;
        this.certificateInvalidityMessage = data.message;
      });

    certificateService
      .getCertificate(this.$route.params.alias)
      .then((response) => {
        response.data.certificate = pki.certificateFromPem(
          response.data.certificate
        );
        this.certificate = response.data;
        let dateTokens = this.certificate.certificate.validity.notBefore
          .toString()
          .split(" ");
        this.certificate.certificate.validity.notBefore =
          dateTokens[1] + " " + dateTokens[2] + " " + dateTokens[3];
        dateTokens = this.certificate.certificate.validity.notAfter
          .toString()
          .split(" ");
        this.certificate.certificate.validity.notAfter =
          dateTokens[1] + " " + dateTokens[2] + " " + dateTokens[3];
        this.parseExtensions(this.certificate.certificate);
      });
  },
  methods: {
    revoke(reason) {
      this.revocationBtnLoading = true;
      this.revocationPayload.certificateAlias = this.certificate.alias;
      this.revocationPayload.reason = reason;
      certificateService
        .revokeCertificate(this.revocationPayload)
        .then((_) => {
          this.revocationBtnLoading = false;
          this.certificateValid = false;
          this.certificateInvalidityMessage = "Revoked because " + reason;
          this.snackbar = true;
          this.snackbarMessage = "Certificate revoked successfully.";
        })
        .catch((error) => {
          this.revocationBtnLoading = false;
          this.snackbar = true;
          if (error.response)
            this.snackbarMessage = error.response.data.message;
          else this.snackbarMessage = "An error has occurred.";
        });
    },
    parseExtensions(certificate) {
      for (let extension of certificate.extensions) {
        switch (extension.name) {
          case "basicConstraints":
            this.isCA = extension.cA;
            break;
          case "subjectKeyIdentifier":
            this.parseSubjectKeyIdentifier(extension);
            break;
          case "authorityKeyIdentifier":
            this.parseAuthorityKeyIdentifier();
            break;
          case "keyUsage":
            this.parseKeyUsages(extension);
            break;
          case "extKeyUsage":
            this.parseExtendedKeyUsages(extension);
            break;
          case "subjectAltName":
            this.parseSubjectAlternativeNames(extension);
            break;
        }
      }
    },
    parseAuthorityKeyIdentifier() {
      let aki = "";
      let count = 0;
      for (let char of this.certificate.aki) {
        count++;
        aki += char;
        if (count === 4) {
          aki += " ";
          count = 0;
        }
      }
      this.certificate.aki = aki.toUpperCase();
    },
    parseSubjectKeyIdentifier(extension) {
      let ski = "";
      let count = 0;
      for (let char of extension.subjectKeyIdentifier) {
        count++;
        ski += char;
        if (count === 4) {
          ski += " ";
          count = 0;
        }
      }
      this.subjectKeyIdentifier = ski.toUpperCase();
    },
    parseKeyUsages(extension) {
      let allUsages = "";
      if (extension.cRLSign) {
        allUsages += "CRL sign, ";
      }
      if (extension.dataEncipherment) {
        allUsages += "Data encipherment, ";
      }
      if (extension.decipherOnly) {
        allUsages += "Decipher only, ";
      }
      if (extension.digitalSignature) {
        allUsages += "Digital signature, ";
      }
      if (extension.encipherOnly) {
        allUsages += "Encipher only, ";
      }
      if (extension.keyAgreement) {
        allUsages += "Key agreement, ";
      }
      if (extension.keyCertSign) {
        allUsages += "Certificate signing, ";
      }
      if (extension.keyEncipherment) {
        allUsages += "Key encipherment, ";
      }
      if (extension.nonRepudiation) {
        allUsages += "Non repudiation, ";
      }
      this.keyUsages = allUsages.slice(0, -2);
    },
    parseExtendedKeyUsages(extension) {
      let allExtUsages = "";
      if (extension["1.3.6.1.4.1.311.20.2.2"]) {
        allExtUsages += "Smartcard Logon, ";
      }
      if (extension["1.3.6.1.5.5.7.3.5"]) {
        allExtUsages += "IP Security End System, ";
      }
      if (extension["1.3.6.1.5.5.7.3.6"]) {
        allExtUsages += "IP Security Tunnel Termination, ";
      }
      if (extension["1.3.6.1.5.5.7.3.7"]) {
        allExtUsages += "IP Security User, ";
      }
      if (extension["1.3.6.1.5.5.7.3.9"]) {
        allExtUsages += "OCSP Signing, ";
      }
      if (extension["2.5.29.37.0"]) {
        allExtUsages += "Any Extended Key Usage, ";
      }
      if (extension.clientAuth) {
        allExtUsages += "TLS Web Client Authentication, ";
      }
      if (extension.codeSigning) {
        allExtUsages += "Code Signing, ";
      }
      if (extension.emailProtection) {
        allExtUsages += "Email Protection, ";
      }
      if (extension.serverAuth) {
        allExtUsages += "TLS Web Server Authentication, ";
      }
      if (extension.timeStamping) {
        allExtUsages += "Time stamping, ";
      }
      this.extKeyUsages = allExtUsages.slice(0, -2);
    },
    parseSubjectAlternativeNames(extension) {
      let allNames = "";
      for (let nameObj of extension.altNames) {
        if (nameObj.type === 1) {
          allNames += "Email:" + nameObj.value + ", ";
        } else if (nameObj.type === 2) {
          allNames += "DNS:" + nameObj.value + ", ";
        } else if (nameObj.type === 7) {
          allNames += "IP Address:" + nameObj.ip + ", ";
        }
        this.san = allNames.slice(0, -2);
      }
    },
  },
};
</script>

<style scoped>
.table-content {
  font-family: Arial, Helvetica, sans-serif;
  border-collapse: collapse;
  width: 100%;
}

.table-content td,
.table-content th {
  border: 1px solid #ddd;
  padding: 8px;
}

.table-content tr td:first-child {
  width: 30em;
  min-width: 10em;
  max-width: 30em;
  word-break: break-all;
}

.table-content tr:nth-child(even) {
  background-color: #f2f2f2;
}

.table-content tr:hover {
  background-color: #ddd;
}

.table-content th {
  padding-top: 12px;
  padding-bottom: 12px;
  text-align: left;
  background-color: #4185f2;
  color: white;
}
</style>

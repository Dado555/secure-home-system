<template>
  <v-container>
    <v-flex class="text-center">
      <h1>All issued certificates</h1>
    </v-flex>
    <br />
    <br />
    <v-data-table
      :headers="headers"
      :items="certificates"
      item-key="name"
      :page.sync="page"
      :items-per-page="itemsPerPage"
      hide-default-footer
      class="elevation-1"
      @page-count="pageCount = $event">
      <template v-slot:item="row">
        <tr>
          <td>{{ row.item.alias }}</td>
          <td>{{ row.item.certificate.issuer.attributes[0].value }}</td>
          <td>{{ row.item.certificate.subject.attributes[0].value }}</td>
          <td>{{ row.item.certificate.validity.notBefore }}</td>
          <td>{{ row.item.certificate.validity.notAfter }}</td>
          <td align="center">
            <v-btn
              class="mx-2"
              dark
              small
              color="primary"
              @click="detailedView(row.item)"
            >
              Detailed view
            </v-btn>
          </td>
        </tr>
      </template>
    </v-data-table>
    <div class="text-center pt-2">
      <v-pagination
          v-model="page"
          :length="pageCount"
      ></v-pagination>
    </div>
  </v-container>
</template>

<script>
import { certificateService } from "../service/certificateService";
var pki = require("node-forge").pki;

export default {
  name: "CertificateList",
  data() {
    return {
      certificates: [],
      headers: [
        {
          text: "Alias",
          align: "start",
          sortable: false,
          value: "alias",
        },
        { text: "Issuer", value: "certificate.issuer.attributes[0].value" },
        { text: "Subject", value: "certificate.subject.attributes[0].value" },
        { text: "Valid from", value: "certificate.validity.notBefore" },
        { text: "Valid to", value: "certificate.validity.notAfter" },
        { text: "" }
      ],
      page: 1,
      pageCount: 0,
      itemsPerPage: 10,
    };
  },
  mounted() {
    certificateService.getAllCertificates().then((response) => {
      this.certificates = response.data;
      for (let i = 0; i < this.certificates.length; i++) {
        let parsedCert = pki.certificateFromPem(
          this.certificates[i].certificate
        );
        let dateTokens = parsedCert.validity.notBefore.toString().split(" ");
        parsedCert.validity.notBefore =
          dateTokens[1] + " " + dateTokens[2] + " " + dateTokens[3];
        dateTokens = parsedCert.validity.notAfter.toString().split(" ");
        parsedCert.validity.notAfter =
          dateTokens[1] + " " + dateTokens[2] + " " + dateTokens[3];
        this.certificates[i].certificate = parsedCert;
      }
    });
  },
  methods: {
    detailedView(certificate) {
      this.$router.push("certificates/" + certificate.alias);
    },
  },
};
</script>

<template>
  <v-container>
    <v-flex class="text-center">
      <h1>All certificate requests</h1>
    </v-flex>
    <br />
    <br />
    <v-data-table
      :headers="headers"
      :items="requests"
      item-key="name"
      :page.sync="page"
      :items-per-page="itemsPerPage"
      hide-default-footer
      class="elevation-1"
      @page-count="pageCount = $event"
    >
      <template v-slot:item="row">
        <tr>
          <td>{{ row.item.name }}</td>
          <td>{{ row.item.email }}</td>
          <td>{{ row.item.organization }}</td>
          <td>{{ row.item.validityStart | formatDate }}</td>
          <td>{{ row.item.validityEnd | formatDate }}</td>
          <td align="right">
            <v-btn
              class="mx-2"
              dark
              small
              color="primary"
              @click="approveCertificate(row.item.id)"
              :loading="approveBtnLoading"
            >
              Approve Certificate
            </v-btn>
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
      <v-pagination v-model="page" :length="pageCount"></v-pagination>
    </div>
    <edit-certificate-modal
      :openDialog="editRequest"
      @closingModal="editRequest = false"
      :certRequest="certRequest"
    />
    <v-snackbar v-model="snackbar" :timeout="timeout">
      {{ text }}

      <template v-slot:action="{ attrs }">
        <v-btn color="blue" text v-bind="attrs" @click="snackbar = false">
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import { certificateService } from "@/service/certificateService";
import EditCertificateModal from "@/components/createCertificate/EditCertificateModal";

export default {
  name: "CertificateRequestsList",
  components: { EditCertificateModal },
  data() {
    return {
      approveBtnLoading: false,
      snackbar: false,
      text: "",
      timeout: 5000,
      requests: [],
      headers: [
        { text: "Person", align: "start", value: "request.name" },
        { text: "Email", value: "request.email" },
        { text: "Organization", value: "request.organization" },
        { text: "Valid from", value: "request.validityStart" },
        { text: "Valid to", value: "request.validityEnd" },
        { text: "" },
      ],
      page: 1,
      pageCount: 0,
      itemsPerPage: 10,
      editRequest: false,
      certRequest: null,
    };
  },
  mounted() {
    certificateService.getAllCertificateRequests().then((response) => {
      this.requests = response.data;
    });
  },
  methods: {
    detailedView(request) {
      this.certRequest = request;
      this.editRequest = true;
    },
    approveCertificate(requestId) {
      this.approveBtnLoading = true;
      certificateService
        .approveCertificate(requestId)
        .then((_) => {
          this.approveBtnLoading = false;
          this.snackbar = true;
          this.text = "Certificate approved!";
          this.requests = this.requests.filter(
            (request) => request.id !== requestId
          );
        })
        .catch((error) => {
          this.approveBtnLoading = false;
          this.snackbar = true;
          if (error.response) this.text = error.response.data.message;
          else this.text = "An error has occurred.";
        });
    },
  },
};
</script>

<style scoped></style>

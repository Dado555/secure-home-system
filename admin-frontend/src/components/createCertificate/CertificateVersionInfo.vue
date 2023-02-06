<template>
  <v-container>
    <v-form style="width: 90%" label-position="right" label-width="150px">
      <v-row> Algorithm </v-row>
      <v-radio-group mandatory>
        <v-row>
          <v-col>
            <v-radio label="RSA" value="RSA" readonly />
          </v-col>
        </v-row>
      </v-radio-group>

      <v-row> Validity period </v-row>
      <v-row>
        <v-col cols="12" md="6">
          <v-menu
            ref="fromDate"
            v-model="menuFromDate"
            :close-on-content-click="false"
            :return-value.sync="dateFrom"
            transition="scale-transition"
            offset-y
            min-width="auto"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="form.validityStart"
                label="Validity start"
                prepend-icon="mdi-calendar"
                readonly
                v-bind="attrs"
                v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker v-model="form.validityStart" no-title scrollable>
              <v-spacer></v-spacer>
              <v-btn text color="primary" @click="menuFromDate = false">
                Cancel
              </v-btn>
              <v-btn
                text
                color="primary"
                @click="$refs.fromDate.save(form.validityStart)"
              >
                OK
              </v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>

        <v-col cols="12" md="6">
          <v-menu
            ref="toDate"
            v-model="menuToDate"
            :close-on-content-click="false"
            :return-value.sync="dateTo"
            transition="scale-transition"
            offset-y
            min-width="auto"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="form.validityEnd"
                label="Validity end"
                prepend-icon="mdi-calendar"
                readonly
                v-bind="attrs"
                v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker v-model="form.validityEnd" no-title scrollable>
              <v-spacer></v-spacer>
              <v-btn text color="primary" @click="menuToDate = false">
                Cancel
              </v-btn>
              <v-btn
                text
                color="primary"
                @click="$refs.toDate.save(form.validityEnd)"
              >
                OK
              </v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>
      </v-row>

      <v-row>
        <v-col v-if="certEdit">
          <certificate-name
            :nameEdit="name"
            @form-name-input-changed="updateName($event)"
            @reset="resetName()"
          />
        </v-col>
        <v-col v-else>
          <certificate-name
            @form-name-input-changed="updateName($event)"
            @reset="resetName()"
          />
        </v-col>
      </v-row>

      <v-row> Extensions </v-row>
      <v-row>
        <add-certificate-extensions
          @extensionsForm="getExtensionsForm($event)"
          :extensionsEdit="[...extensions]"
        />
      </v-row>

      <v-card-actions class="justify-end" v-if="certEdit">
        <v-btn text @click="closeDialog()">Close</v-btn>
        <v-btn text @click="saveChanges()">Save</v-btn>
      </v-card-actions>

      <v-card-actions v-else class="justify-end" style="margin-top: 30px">
        <v-btn @click="onReset()" color="error">Reset</v-btn>
        <v-btn @click="onSubmit()" color="primary" :loading="submitBtnLoading"
          >Submit</v-btn
        >
      </v-card-actions>
    </v-form>
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
import AddCertificateExtensions from "@/components/createCertificate/certificateExtensions/AddCertificateExtensions";
import { certificateService } from "@/service/certificateService";
import CertificateName from "./CertificateName.vue";

export default {
  name: "CertificateVersionInfo",
  components: { AddCertificateExtensions, CertificateName },
  props: ["certEdit"],
  data() {
    return {
      submitBtnLoading: false,
      snackbar: false,
      timeout: 5000,
      text: "",
      menuFromDate: false,
      menuToDate: false,
      dateFrom: new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
        .toISOString()
        .substr(0, 10),
      dateTo: new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
        .toISOString()
        .substr(0, 10),
      name: {},
      extensions: [],
      nameFields: [
        "email",
        "name",
        "surname",
        "organization",
        "organizationUnit",
        "commonName",
        "province",
        "city",
        "countryCode",
      ],
      form: {
        email: "",
        name: "",
        surname: "",
        organization: "",
        organizationUnit: "",
        countryCode: "",
        validityStart: null,
        validityEnd: null,
        authorityKeyIdentifier: false,
        subjectKeyIdentifier: false,
        subjectAlternativeNames: [],
        keyUsages: [],
        extendedKeyUsages: [],
        commonName: "",
        province: "",
        city: "",
      },
    };
  },
  methods: {
    resetName() {
      this.nameFields.forEach((nameField) => {
        this.form[nameField] = "";
        this.name[nameField] = "";
      });
    },
    updateName(value) {
      this.form[value.fieldName] = value.fieldValue;
      this.name[value.fieldName] = value.fieldValue;
    },
    getExtensionsForm(extensions) {
      this.extensions = [...extensions];
      for (let ext of extensions) {
        switch (ext.name) {
          case "Authority Key Identifier":
            this.form.authorityKeyIdentifier = true;
            break;
          case "Subject Key Identifier":
            this.form.subjectKeyIdentifier = true;
            break;
          case "Key Usage":
            this.form.keyUsages = ext.usages;
            break;
          case "Extended Key Usage":
            this.form.extendedKeyUsages = ext.extUsages;
            break;
          case "Subject Alternative Name":
            this.form.subjectAlternativeNames = ext.alternativeNames;
            break;
        }
      }
      this.snackbar = true;
      this.text = "Extensions updated successfully.";
    },
    onSubmit() {
      this.submitBtnLoading = true;

      let payload = { ...this.form };

      payload.validityStart = new Date(this.form.validityStart);
      payload.validityEnd = new Date(this.form.validityEnd);

      if (!payload.extendedKeyUsages) payload.extendedKeyUsages = [];
      if (!payload.keyUsages) payload.keyUsages = [];
      if (!payload.subjectAlternativeNames)
        payload.subjectAlternativeNames = [];

      certificateService
        .createRequest(payload)
        .then((_) => {
          this.submitBtnLoading = false;
          this.snackbar = true;
          this.text = "Request successfully created!";
        })
        .catch((error) => {
          this.submitBtnLoading = false;
          this.snackbar = true;
          if (error.response) this.text = error.response.data.message;
          else this.text = "An error has occurred.";
        });
    },
    onReset() {
      Object.keys(this.form).map((f) => {
        if (typeof this.form[f] === "boolean") {
          this.form[f] = false;
        } else if (Array.isArray(this.form[f])) {
          this.form[f] = [];
        } else {
          this.form[f] = "";
        }
      });
      this.form.algorithm = "RSA";
      this.form.validityStart = null;
      this.form.validityEnd = null;
    },
    closeDialog() {
      this.$emit("closeDialog");
    },
    saveChanges() {
      this.$emit("saveChanges", { ...this.form });
    },
  },
  mounted() {
    if (this.certEdit) {
      this.form = {
        id: this.certEdit.id,
        email: this.certEdit.email,
        name: this.certEdit.name,
        surname: this.certEdit.surname,
        organization: this.certEdit.organization,
        organizationUnit: this.certEdit.organizationUnit,
        countryCode: this.certEdit.countryCode,
        algorithm: this.certEdit.algorithm,
        validityStart: this.certEdit.validityStart,
        validityEnd: this.certEdit.validityEnd,
        authorityKeyIdentifier: this.certEdit.authorityKeyIdentifier,
        subjectKeyIdentifier: this.certEdit.subjectKeyIdentifier,
        subjectAlternativeNames: this.certEdit.subjectAlternativeNames,
        keyUsages: this.certEdit.keyUsages,
        extendedKeyUsages: this.certEdit.extendedKeyUsages,
        commonName: this.certEdit.commonName,
        province: this.certEdit.province,
        city: this.certEdit.city,
      };
      if (this.form.authorityKeyIdentifier)
        this.extensions.push({
          name: "Authority Key Identifier",
          objectId: "2.5.29.35",
          critical: true,
        });
      if (this.form.subjectKeyIdentifier)
        this.extensions.push({
          name: "Subject Key Identifier",
          objectId: "2.5.29.14",
          critical: true,
        });
      if (this.form.subjectAlternativeNames.length > 0)
        this.extensions.push({
          name: "Subject Alternative Name",
          objectId: "2.5.29.17",
          critical: true,
          alternativeNames: this.form.subjectAlternativeNames,
        });
      if (this.form.keyUsages.length > 0)
        this.extensions.push({
          name: "Key Usage",
          objectId: "2.5.29.15",
          critical: true,
          usages: this.form.keyUsages,
        });
      if (this.form.extendedKeyUsages.length > 0)
        this.extensions.push({
          name: "Extended Key Usage",
          objectId: "2.5.29.15",
          critical: true,
          extUsages: this.form.extendedKeyUsages,
        });
      this.name = {
        name: this.form.name,
        email: this.form.email,
        surname: this.form.surname,
        organization: this.form.organization,
        organizationUnit: this.form.organizationUnit,
        countryCode: this.form.countryCode,
        commonName: this.form.commonName,
        province: this.form.province,
        city: this.form.city,
      };
    }
  },
};
</script>

<style scoped></style>

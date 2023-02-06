<template>
  <v-container>
    <v-expansion-panels>
      <v-expansion-panel>
        <v-expansion-panel-header>
          <v-row no-gutters>
            <v-col> Add Extensions </v-col>
          </v-row>
        </v-expansion-panel-header>
        <v-expansion-panel-content>
          <v-row>
            <v-col cols="4"> Extension name </v-col>
            <v-col cols="4"> Object identifier </v-col>
          </v-row>
          <v-row v-for="(extension, i) in extensions" :key="i">
            <v-col cols="4">
              <v-text-field v-model="extension.name" readonly></v-text-field>
            </v-col>
            <v-col cols="4">
              <v-text-field
                v-model="extension.objectId"
                readonly
              ></v-text-field>
            </v-col>
            <v-col cols="1">
              <v-btn icon color="red" @click="deleteExtension(extension)">
                <v-icon>mdi-minus-thick</v-icon>
              </v-btn>
            </v-col>
            <v-col cols="1" v-if="canEdit.indexOf(extension.name) !== -1">
              <v-btn icon color="red" @click="prepareToEdit(extension)">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
            </v-col>
          </v-row>

          <v-card-actions>
            <v-menu offset-y>
              <template v-slot:activator="{ on, attrs }">
                <v-btn text color="danger" v-bind="attrs" v-on="on">
                  Use Standard Template
                </v-btn>
              </template>
              <v-list>
                <v-list-item-group v-model="chosenTemplate">
                  <v-list-item v-for="item in templates" :key="item">
                    <v-list-item-title>{{ item }}</v-list-item-title>
                  </v-list-item>
                </v-list-item-group>
              </v-list>
            </v-menu>
            <v-spacer></v-spacer>
            <!-- <v-btn icon color="blue" @click="addExtensionModal = true">
              <v-icon>mdi-plus-thick</v-icon>
            </v-btn> -->
            <v-spacer></v-spacer>
            <v-btn text color="secondary" @click="resetExtensions()">
              Reset
            </v-btn>
            <v-btn text color="primary" @click="addExtensions()"> OK </v-btn>
          </v-card-actions>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
    <!-- <extension-type-modal
      :openDialog="addExtensionModal"
      @closingModal="addExtensionModal = false"
    /> -->
    <edit-extension-modal
      :openDialog="editExtensionModal"
      @closingModal="editExtensionModal = false"
      :extension="editExtension"
      @collectionChange="editExtensionData($event)"
    />
  </v-container>
</template>

<script>
import ExtensionTypeModal from "@/components/createCertificate/certificateExtensions/ExtensionTypeModal";
import EditExtensionModal from "@/components/createCertificate/certificateExtensions/EditExtensionModal";
export default {
  name: "AddCertificateExtensions",
  components: { EditExtensionModal, ExtensionTypeModal },
  props: ["extensionsEdit"],
  data() {
    return {
      addExtensionModal: false,
      editExtensionModal: false,
      editExtension: null,
      editExtensionType: "",
      extensions: [],
      canEdit: ["Key Usage", "Extended Key Usage", "Subject Alternative Name"],
      templates: ["CA", "SSL Server", "SSL Client", "Code Signing"],
      chosenTemplate: "",
      templateExtensions: {
        CA: [
          {
            name: "Authority Key Identifier",
            objectId: "2.5.29.35",
            critical: true,
          },
          {
            name: "Basic Constraints",
            objectId: "2.5.29.19",
            critical: false,
            isCA: true,
          },
          {
            name: "Key Usage",
            objectId: "2.5.29.15",
            critical: false,
            usages: ["CERTIFICATE_SIGNING", "CRL_SIGN"],
          },
          {
            name: "Subject Key Identifier",
            objectId: "2.5.29.14",
            critical: false,
          },
        ],
        "SSL Server": [
          {
            name: "Authority Key Identifier",
            objectId: "2.5.29.35",
            critical: true,
          },
          {
            name: "Extended Key Usage",
            objectId: "2.5.29.15",
            critical: true,
            extUsages: ["TLS_WEB_SERVER_AUTHENTICATION"],
          },
          {
            name: "Key Usage",
            objectId: "2.5.29.15",
            critical: false,
            usages: ["DIGITAL_SIGNATURE", "KEY_ENCIPHERMENT"],
          },
          {
            name: "Subject Alternative Name",
            objectId: "2.5.29.17",
            critical: true,
            alternativeNames: ["Mrki"],
          },
          {
            name: "Subject Key Identifier",
            objectId: "2.5.29.14",
            critical: true,
          },
        ],
        "SSL Client": [
          {
            name: "Authority Key Identifier",
            objectId: "2.5.29.35",
            critical: true,
          },
          {
            name: "Extended Key Usage",
            objectId: "2.5.29.15",
            critical: true,
            extUsages: ["TLS_WEB_CLIENT_AUTHENTICATION"],
          },
          {
            name: "Key Usage",
            objectId: "2.5.29.15",
            critical: false,
            usages: ["DIGITAL_SIGNATURE", "KEY_ENCIPHERMENT"],
          },
          {
            name: "Subject Key Identifier",
            objectId: "2.5.29.14",
            critical: true,
          },
        ],
        "Code Signing": [
          {
            name: "Authority Key Identifier",
            objectId: "2.5.29.35",
            critical: true,
          },
          {
            name: "Extended Key Usage",
            objectId: "2.5.29.15",
            critical: true,
            extUsages: ["CODE_SIGNING"],
          },
          {
            name: "Key Usage",
            objectId: "2.5.29.15",
            critical: false,
            usages: ["DIGITAL_SIGNATURE"],
          },
          {
            name: "Subject Key Identifier",
            objectId: "2.5.29.14",
            critical: true,
          },
        ],
      },
    };
  },
  methods: {
    deleteExtension(ext) {
      this.extensions.splice(
        this.extensions.findIndex((a) => a.name === ext.name),
        1
      );
    },
    resetExtensions() {
      this.extensions = [];
      this.$emit("extensionsForm", [...this.extensions]);
    },
    addExtensions() {
      this.$emit("extensionsForm", [...this.extensions]);
    },
    prepareToEdit(extension) {
      this.editExtension = { ...extension };
      this.editExtensionModal = true;
    },
    editExtensionData(data) {
      for (let ext of Object.values(this.extensions)) {
        if (ext.name === this.editExtension.name) {
          if (ext.name === "Key Usage") {
            ext.usages = [...data];
          } else if (ext.name === "Extended Key Usage") {
            ext.extUsages = [...data];
          } else if (ext.name === "Subject Alternative Name") {
            ext.alternativeNames = [...data];
          }
        }
      }
    },
  },
  watch: {
    chosenTemplate: function () {
      switch (this.chosenTemplate) {
        case 0:
          this.extensions = [...this.templateExtensions.CA];
          break;
        case 1:
          this.extensions = [...this.templateExtensions["SSL Server"]];
          break;
        case 2:
          this.extensions = [...this.templateExtensions["SSL Client"]];
          break;
        case 3:
          this.extensions = [...this.templateExtensions["Code Signing"]];
          break;
      }
    },
    extensionsEdit() {
      this.extensions = [...this.extensionsEdit];
    },
  },
  mounted() {
    if (this.extensionsEdit) this.extensions = [...this.extensionsEdit];
  },
};
</script>

<style scoped></style>

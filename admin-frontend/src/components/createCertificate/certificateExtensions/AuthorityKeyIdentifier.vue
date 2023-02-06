<template>
  <v-form style="width: 95%" label-position="right" label-width="150px">
    <v-row> Authority Cert Issuer </v-row>

    <v-row>
      <v-expansion-panels>
        <v-expansion-panel>
          <v-expansion-panel-header>
            <v-row no-gutters>
              <v-col> General Name </v-col>
            </v-row>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <v-row>
              <v-col cols="4"> General Name Type </v-col>
              <v-col cols="4"> General Name Value </v-col>
            </v-row>
            <v-row v-for="(name, i) in names" :key="i">
              <v-col cols="4">
                <v-text-field v-model="name.type" readonly></v-text-field>
              </v-col>
              <v-col cols="4">
                <v-text-field v-model="name.value" readonly></v-text-field>
              </v-col>
              <v-col>
                <v-btn icon color="red">
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
              </v-col>
              <v-col>
                <v-btn icon color="red">
                  <v-icon>mdi-minus-thick</v-icon>
                </v-btn>
              </v-col>
            </v-row>

            <v-row>
              <v-expansion-panels>
                <v-expansion-panel>
                  <v-expansion-panel-header disable-icon-rotate>
                    Add New General Name
                    <template v-slot:actions>
                      <v-icon color="blue"> mdi-plus-thick </v-icon>
                    </template>
                  </v-expansion-panel-header>
                  <v-expansion-panel-content>
                    <vue-form
                      :model="form"
                      style="width: 90%"
                      label-position="right"
                      label-width="150px"
                      ref="form"
                    >
                      <vue-form-item
                        field="generalNameType"
                        label="General Name Type: "
                      >
                        <v-radio-group v-model="chosenName.type" column>
                          <v-radio
                            v-for="(type, i) in nameTypes"
                            :label="type"
                            :value="type"
                            :key="i"
                          >
                          </v-radio>
                        </v-radio-group>
                      </vue-form-item>
                      <vue-form-item
                        field="keyIdentifier"
                        label="Key Identifier"
                        style="padding-top: 24px"
                      >
                        <v-expansion-panels
                          v-if="chosenName.type === 'Directory Name'"
                        >
                          <v-expansion-panel>
                            <v-expansion-panel-header>
                              <template v-slot:actions>
                                <v-icon color="red"> mdi-pencil </v-icon>
                              </template>

                              <v-row>
                                <v-col cols="9">
                                  <vue-input
                                    v-model="chosenName.value"
                                    name="nameValue"
                                    v-validate="'required'"
                                    :disabled="true"
                                  />
                                </v-col>
                                <v-col>
                                  <v-btn icon color="red">
                                    <v-icon>mdi-eraser</v-icon>
                                  </v-btn>
                                </v-col>
                              </v-row>
                            </v-expansion-panel-header>
                            <v-expansion-panel-content>
                              <certificate-name />
                            </v-expansion-panel-content>
                          </v-expansion-panel>
                        </v-expansion-panels>
                        <v-row v-else>
                          <vue-input
                            v-model="chosenName.value"
                            name="nameValue"
                            v-validate="'required'"
                            :disabled="false"
                          />
                        </v-row>
                      </vue-form-item>

                      <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn text color="secondary" @click="resetNewName()">
                          Reset
                        </v-btn>
                        <v-btn text color="primary" @click="addNewName()">
                          Save
                        </v-btn>
                      </v-card-actions>
                    </vue-form>
                  </v-expansion-panel-content>
                </v-expansion-panel>
              </v-expansion-panels>
            </v-row>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-row>
  </v-form>
</template>

<script>
import CertificateName from "@/components/createCertificate/CertificateName";
export default {
  name: "AuthorityKeyIdentifier",
  components: { CertificateName },
  data() {
    return {
      form: {
        keyId: false,
        authorityCertIssuer: {},
        authorityCertSerialNumber: false,
      },
      nameTypes: [
        // "Directory Name",
        "DNS Name",
        "IP Address",
        // "Registered ID",
        "RFC 822 Name",
        // "URI",
        // "UPN"
      ],
      names: [],
      chosenName: { type: "", value: "" },
      generationMethods: ["160-bit Hash", "64-bit Hash"],
      chosenGenMethod: "",
    };
  },
  methods: {
    resetNewName() {
      this.chosenName.type = "";
      this.chosenName.value = "";
    },
    addNewName() {
      this.names.push({ ...this.chosenName });
    },
  },
};
</script>

<style scoped></style>

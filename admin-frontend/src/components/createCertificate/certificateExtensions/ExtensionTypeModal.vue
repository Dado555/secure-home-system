<template>
  <v-row justify="center">
    <v-dialog v-model="openDialog" scrollable max-width="1200px" persistent>
      <v-card>
        <v-stepper v-model="stepper">
          <v-stepper-header>
            <v-stepper-step step="1"> Select extension </v-stepper-step>
            <v-divider></v-divider>

            <v-stepper-step step="2"> Define extension </v-stepper-step>
            <v-divider></v-divider>

            <v-stepper-step step="3"> Confirm </v-stepper-step>
          </v-stepper-header>
          <v-stepper-items>
            <v-stepper-content step="1">
              <v-card>
                <v-card-title>Select Extension</v-card-title>
                <v-divider></v-divider>
                <v-card-text>
                  <v-radio-group v-model="chosenExtension">
                    <v-radio
                      v-for="(type, i) in extensionTypes"
                      :label="type.label"
                      :value="type.value"
                      :key="i"
                    ></v-radio>
                  </v-radio-group>
                </v-card-text>
              </v-card>
            </v-stepper-content>

            <v-stepper-content step="2" class="content">
              <v-container
                v-if="chosenExtension === 14"
                style="max-height: 625px; overflow-y: auto"
              >
                <subject-alternative-name />
              </v-container>

              <v-container v-if="chosenExtension === 9">
                <key-usage />
              </v-container>

              <v-container v-if="chosenExtension === 6">
                <extended-key-usage />
              </v-container>
            </v-stepper-content>

            <v-stepper-content step="3" class="content">
              <v-card style="padding-left: 30%; padding-right: 30%">
                <v-card-title
                  style="align-items: center; justify-content: center"
                  >Confirm your decision:</v-card-title
                >
                <v-divider></v-divider>
                <v-card-text style="text-align: center">
                  <v-btn color="primary" text> Confirm </v-btn>
                </v-card-text>
              </v-card>
            </v-stepper-content>

            <v-card-actions style="padding: 0 5% 5%">
              <v-btn
                color="primary"
                text
                @click="prevStep()"
                :disabled="stepper === 1"
              >
                Previous
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn color="primary" text @click="$emit('closingModal')">
                Close
              </v-btn>
              <v-btn
                color="primary"
                text
                @click="nextStep()"
                :disabled="!nextBtnOk"
              >
                Next
              </v-btn>
            </v-card-actions>
          </v-stepper-items>
        </v-stepper>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import AuthorityKeyIdentifier from "@/components/createCertificate/certificateExtensions/AuthorityKeyIdentifier";
import SubjectKeyIdentifier from "@/components/createCertificate/certificateExtensions/SubjectKeyIdentifier";
import SubjectAlternativeName from "@/components/createCertificate/certificateExtensions/SubjectAlternativeName";
import KeyUsage from "@/components/createCertificate/certificateExtensions/KeyUsage";
import ExtendedKeyUsage from "@/components/createCertificate/certificateExtensions/ExtendedKeyUsage";
import BasicConstraints from "@/components/createCertificate/certificateExtensions/BasicConstraints";
export default {
  name: "ExtensionTypeModal",
  components: {
    BasicConstraints,
    ExtendedKeyUsage,
    KeyUsage,
    SubjectAlternativeName,
    SubjectKeyIdentifier,
    AuthorityKeyIdentifier,
  },
  props: ["openDialog"],
  data() {
    return {
      chosenExtension: "",
      criticalExt: false,
      stepper: 1,
      stepSize: 1,
      steps: [1, 2, 3],
      extensionTypes: [
        { label: "Authority Key Identifier:", value: 2 },
        { label: "Extended Key Usage:", value: 6 },
        { label: "Key Usage:", value: 9 },
        { label: "Subject Alternative Name:", value: 14 },
        { label: "Subject Key Identifier:", value: 16 },
      ],
    };
  },
  computed: {
    nextBtnOk() {
      if (this.stepper === 3) return false;
      else if (!this.chosenExtension) return false;
      return true;
    },
  },
  methods: {
    nextStep() {
      this.stepper += this.stepSize;
    },
    prevStep() {
      this.stepper -= this.stepSize;
    },
  },
  watch: {
    openDialog: function (newVal, oldVal) {
      if (!newVal) {
        this.$emit("closingModal");
      }
    },
    chosenExtension: function () {
      if (this.chosenExtension === 2 || this.chosenExtension === 16) {
        this.steps = [1, 3];
        this.stepSize = 2;
      } else {
        this.steps = [1, 2, 3];
        this.stepSize = 1;
      }
    },
  },
};
</script>

<style scoped>
.vue-checkbox__label {
  font-size: 50px;
}

.content {
  max-height: 300px;
  overflow-y: auto;
}
</style>

<template>
  <v-row justify="space-around">
    <v-col cols="auto">
      <v-dialog
        transition="dialog-bottom-transition"
        max-width="1000"
        v-model="openDialog"
        persistent
      >
        <v-toolbar color="primary" dark>{{ ext.name }} Edit</v-toolbar>
        <v-card>
          <v-card-text>
            <v-container
              v-if="ext.name === 'Subject Alternative Name'"
              style="max-height: 625px; overflow-y: auto"
            >
              <subject-alternative-name
                :alternativeNames="ext.alternativeNames"
                @collectionChange="setAlternativeNames($event)"
              />
            </v-container>

            <v-container v-if="ext.name === 'Key Usage'">
              <key-usage
                :checkedOptions="ext.usages"
                @collectionChange="setKeyUsages($event)"
              />
            </v-container>

            <v-container v-if="ext.name === 'Extended Key Usage'">
              <extended-key-usage
                :checkedOptions="ext.extUsages"
                @collectionChange="setExtendedUsages($event)"
              />
            </v-container>
          </v-card-text>
          <v-card-actions class="justify-end">
            <v-btn text @click="$emit('closingModal')">Close</v-btn>
            <v-btn
              text
              @click="
                saveChanges();
                $emit('closingModal');
              "
              >Save</v-btn
            >
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-col>
  </v-row>
</template>

<script>
import SubjectAlternativeName from "@/components/createCertificate/certificateExtensions/SubjectAlternativeName";
import KeyUsage from "@/components/createCertificate/certificateExtensions/KeyUsage";
import ExtendedKeyUsage from "@/components/createCertificate/certificateExtensions/ExtendedKeyUsage";

export default {
  name: "EditExtensionModal",
  props: ["openDialog", "extension"],
  components: { ExtendedKeyUsage, KeyUsage, SubjectAlternativeName },
  data() {
    return {
      extUsages: [],
      usages: [],
      names: [],
      ext: {},
    };
  },
  watch: {
    openDialog() {
      this.ext = { ...this.extension };
    },
  },
  mounted() {
    this.ext = { ...this.extension };
  },
  methods: {
    saveChanges() {
      if (this.ext.name === "Subject Alternative Name")
        this.$emit("collectionChange", [...this.names]);
      else if (this.ext.name === "Key Usage")
        this.$emit("collectionChange", [...this.usages]);
      else if (this.ext.name === "Extended Key Usage")
        this.$emit("collectionChange", [...this.extUsages]);
    },
    setAlternativeNames(names) {
      this.names = names;
    },
    setKeyUsages(usages) {
      this.usages = usages;
    },
    setExtendedUsages(usages) {
      this.extUsages = usages;
    },
  },
};
</script>

<style scoped></style>

<template>
  <v-container>
    <v-expansion-panels>
      <v-expansion-panel>
        <v-expansion-panel-header>
          <v-row no-gutters>
            <v-col> Edit name </v-col>
          </v-row>
        </v-expansion-panel-header>
        <v-expansion-panel-content>
          <v-form>
            <v-container v-for="(name, i) in nameTypes" :key="i">
              <v-row>
                <v-col>
                  <v-text-field
                    v-model="form[name.value]"
                    :label="name.label"
                    @input="emitTextFieldChange(name.value, $event)"
                  />
                </v-col>
              </v-row>
            </v-container>
          </v-form>
          <v-card-actions>
            <v-btn text color="error" @click="onReset()"> Reset </v-btn>
          </v-card-actions>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-container>
</template>

<script>
export default {
  name: "CertificateName",
  props: ["nameEdit"],
  data() {
    return {
      form: {
        email: "",
        name: "",
        surname: "",
        commonName: "",
        organization: "",
        organizationUnit: "",
        countryCode: "",
        province: "",
        city: "",
      },
      nameTypes: [
        { label: "Email:", value: "email" },
        { label: "Name:", value: "name" },
        { label: "Surname:", value: "surname" },
        { label: "Common Name:", value: "commonName" },
        { label: "Organization Name (O):", value: "organization" },
        { label: "Organization Unit (OU):", value: "organizationUnit" },
        { label: "City:", value: "city" },
        { label: "Province:", value: "province" },
        { label: "Country Code:", value: "countryCode" },
      ],
    };
  },
  watch: {
    nameEdit: {
      handler(newValue, oldValue) {
        this.form = { ...newValue };
      },
      deep: true,
    },
  },
  methods: {
    emitTextFieldChange(fieldName, fieldValue) {
      const payload = { fieldName, fieldValue };
      this.$emit("form-name-input-changed", payload);
    },
    onReset() {
      Object.keys(this.form).map((f) => {
        this.form[f] = "";
      });
      this.$emit("reset");
    },
  },
  mounted() {
    if (this.nameEdit) {
      this.form = { ...this.nameEdit };
    }
  },
};
</script>

<style scoped></style>

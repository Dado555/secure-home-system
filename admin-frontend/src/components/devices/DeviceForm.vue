<template>
  <v-container>
    <v-dialog v-model="dialog" persistent max-width="600px">
      <template v-slot:activator="{ on, attrs }">
        <v-btn v-if="!edit" color="primary" dark v-bind="attrs" v-on="on">
          Add new device
        </v-btn>
        <v-btn v-else color="primary" dark v-bind="attrs" v-on="on">
          Configure device
        </v-btn>
      </template>
      <v-card>
        <v-card-title>
          <span v-if="!edit" class="text-h5">Add new device</span>
          <span v-else class="text-h5">Configure device</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col>
                <v-text-field
                  label="Device name"
                  v-model="name"
                  required
                ></v-text-field>
                <v-text-field
                  label="Regex filter"
                  v-model="filter"
                  required
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="dialog = false">
            Cancel
          </v-btn>
          <v-btn color="blue darken-1" text @click="save()"> Save </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
export default {
  name: "DeviceForm",
  props: ["edit", "deviceData"],
  data() {
    return {
      text: "",
      snackbar: false,
      timeout: 2000,
      dialog: false,
      name: "",
      filter: "",
    };
  },
  mounted() {
    if (this.deviceData) {
      this.name = this.deviceData.name;
      this.filter = this.deviceData.regexFilter;
    }
  },
  methods: {
    save() {
      if (!this.validateName()) {
        this.text = "You need to insert a name";
        this.snackbar = true;
        return;
      }

      if (!this.validateFilter()) {
        this.text = "You need to insert a valid regular expression";
        this.snackbar = true;
        return;
      }

      if (this.edit) {
        this.$root.$emit("configureDeviceFormSubmitted", {
          request: {
            name: this.name,
            regexFilter: this.filter,
          },
          deviceId: this.deviceData.id,
        });
      } else {
        this.$root.$emit("addDeviceFormSubmitted", {
          name: this.name,
          regexFilter: this.filter,
        });
        this.name = "";
        this.filter = "";
      }
      this.dialog = false;
    },
    validateName() {
      return this.name.trim() !== "";
    },
    validateFilter() {
      if (this.filter.trim() === "") {
        return false;
      }
      try {
        new RegExp(this.regexFilter);
        return true;
      } catch (e) {
        return false;
      }
    },
  },
};
</script>

<template>
  <v-container>
    <v-dialog v-model="dialog" persistent max-width="600px">
      <template v-slot:activator="{ on, attrs }">
        <v-btn
            v-if="edit"
            color="primary"
            v-bind="attrs"
            v-on="on"
            :disabled="userRole !== 'HOUSE_HEAD'">
          Edit
        </v-btn>
        <v-btn
            v-else
            color="primary"
            v-bind="attrs"
            v-on="on"
            :disabled="userRole !== 'HOUSE_HEAD'">
          Add new real estate
        </v-btn>
      </template>
      <v-card>
        <v-card-title>
          <span v-if="!edit" class="text-h5">Add real estate</span>
          <span v-else class="text-h5">Edit real estate</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6" md="6">
                <v-text-field
                    label="Name"
                    v-model="name"
                    required
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="12" sm="6" md="6">
                <v-text-field
                    label="Device reading period"
                    type="number"
                    step="any"
                    min="5"
                    ref="input"
                    :rules="[numberRule]"
                    v-model="number"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row v-if="!edit && canBeSeenBy">
              <v-container style="overflow-x:auto; white-space: nowrap;"
                           class="d-flex flex-row mb-6" flat tile>
                <v-container style="overflow-x:auto; white-space: nowrap;"
                             class="d-flex flex-row mb-6" flat tile
                             v-for="user in tenants" v-bind:key="user.id">
                  <v-switch v-if="user.authority='HOUSE_HEAD'" color="primary" v-model="canBeSeenBy[user.id]">
                    <template v-slot:label>
                      {{ user.name }} {{ user.surname }}
                    </template>
                  </v-switch>
                  <v-switch v-else v-model="canBeSeenBy[user.id]">
                    <template v-slot:label>
                      {{ user.name }} {{ user.surname }}
                    </template>
                  </v-switch>
                </v-container>
              </v-container>
            </v-row>

            <v-row v-if="edit && canBeSeenBy">
              <v-container style="overflow-x:auto; white-space: nowrap;"
                           class="d-flex flex-row mb-6" flat tile>
                <v-container style="overflow-x:auto; white-space: nowrap;"
                             class="d-flex flex-row mb-6" flat tile
                             v-for="user in tenants" v-bind:key="user.id">
                  <v-switch v-if="showTenantIdInEdit(user)" v-model="canBeSeenBy[user.id]">
                    <template v-slot:label>
                      {{ user.name }} {{ user.surname }}
                    </template>
                  </v-switch>
                </v-container>
              </v-container>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="dialog = false">
            Cancel
          </v-btn>
          <v-btn color="blue darken-1" text type="submit" @click="submit()">
            Save
          </v-btn>
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
import {authService} from "@/service/authService";
import {userService} from "@/service/userService";
import {householdService} from "@/service/householdService";

export default {
  props: ["edit", "estateData", "userId", "callback", "usersCanSee", "readingPeriod"],
  name: "RealestateForm",
  data: () => ({
    showPassword: false,
    dialog: false,
    snackbar: false,
    timeout: 2000,
    text: "",
    name: "",
    userRole: "",
    canBeSeenBy: "",
    household: "",
    tenants: [],
    number: 5,
    numberRule: val => {
      if(val < 5) return 'Unesite veci broj'
      return true
    }
  }),
  mounted() {
    this.userRole = authService.roles();
    if (this.edit) {
      this.name = this.estateData.name;
      this.number = this.readingPeriod;
      this.getHouseHoldForUser();
    } else {
      this.getHouseHoldForUser();
    }
  },
  methods: {
    getHouseHoldForUser() {
      householdService.getHouseholdForUser(this.userId)
          .then((response)=> {
            this.household = response.data;
            this.getTenantsForHousehold();
          })
          .catch((err) => {
            this.text = err.response.data.message;
            this.snackbar = true;
          });
    },

    showTenantIdInEdit(user) {
      if(user.authority === 'HOUSE_HEAD')
        return false;
      for (let i = 0; i < this.usersCanSee.length; i++) {
        if (user.id === this.usersCanSee[i])
          return false;
      }
      return true;
    },

    getTenantsForHousehold() {
      userService
          .getTenantsForHouseholdId(this.household.id)
          .then((response) => {
            this.tenants = response.data;
            this.canBeSeenBy = {};
            for(let tenant of this.tenants) {
              this.canBeSeenBy[tenant.id] = false;
            }
          })
          .catch((err) => {
            this.text = err.response.data.message;
            this.snackbar = true;
          });
    },

    editFormSubmit() {
      if(this.name.trim() === "") {
        this.text = "You need to fill in all fields.";
        this.snackbar = true;
        return {};
      }

      let canSeeIds = [];
      for (let [key, value] of Object.entries(this.canBeSeenBy)) {
        if (value)
          canSeeIds.push(parseInt(key));
      }

      return {
        name: this.name,
        id: this.estateData.id,
        canBeSeenByIds: [...canSeeIds],
        householdId: this.household.id,
        readingPeriod: this.number
      };
    },

    addFormSubmit() {
      if(this.name.trim() === "") {
        this.text = "You need to fill in all fields.";
        this.snackbar = true;
        return {};
      }

      let canSeeIds = [];
      for (let [key, value] of Object.entries(this.canBeSeenBy)) {
        if (value)
          canSeeIds.push(parseInt(key));
      }
      return {
        name: this.name,
        canBeSeenByIds: [...canSeeIds],
        householdId: this.household.id,
        readingPeriod: this.number
      }
    },

    submit() {
      let requestBody = {};
      if (this.edit) {
        requestBody = this.editFormSubmit();
        if(requestBody.name)
          this.callback(requestBody);
      } else {
        requestBody = this.addFormSubmit();
        this.callback(requestBody);
      }
      this.dialog = false;
    },
  },
}
</script>

<style scoped>

</style>
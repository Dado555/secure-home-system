<template>
  <v-container>
    <v-row>
      <v-col>
        <v-flex class="text-center">
          <h1>My Household</h1>
          <br />
          <br />
          <v-data-table
            :headers="headers"
            :items="tenants"
            :options.sync="options"
            :server-items-length="totalTenants"
            :loading="loading"
            class="elevation-1"
          >
            <template v-slot:item="row">
              <tr>
                <td align="left">{{ row.item.email }}</td>
                <td align="left">{{ row.item.name }}</td>
                <td align="left">{{ row.item.surname }}</td>
                <td align="left">
                  {{ row.item.authority }}
                </td>
                <td align="left">
                  <tenant-form
                    :edit="true"
                    :userData="row.item"
                    :householdID="householdId"
                    :callback="updateTenant"
                  ></tenant-form>
                </td>
                <td align="left" v-if="row.item.authority === 'HOUSE_TENANT'">
                  <v-btn
                    class="mx-2"
                    color="primary"
                    @click="upgradeRole(row.item.id)"
                    :disabled="userRole !== 'HOUSE_HEAD'"
                  >
                    Upgrade role
                  </v-btn>
                </td>
                <td align="left" v-else>
                  <h2 style="color: gray">MAX AUTHORITY</h2>
                </td>
              </tr>
            </template>
          </v-data-table>
          <br />
        </v-flex>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <tenant-form
          :edit="false"
          :userData="{}"
          :householdID="householdId"
          :callback="createTenant"
        ></tenant-form>
      </v-col>
    </v-row>
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
import { authService } from "../service/authService";
import { userService } from "../service/userService";
import { householdService } from "../service/householdService";
import TenantForm from "../components/householdComponents/TenantForm.vue";

export default {
  components: { TenantForm },
  data() {
    return {
      snackbar: false,
      text: "",
      timeout: 2000,
      tenants: [],
      totalTenants: 0,
      loading: true,
      options: {},
      householdId: -1,
      userId: -1,
      userRole: "",
      headers: [
        {
          text: "Email",
          align: "start",
          sortable: false,
        },
        {
          text: "Name",
          align: "start",
          sortable: false,
        },
        {
          text: "Surname",
          align: "start",
          sortable: false,
        },
        {
          text: "Role",
          align: "start",
          sortable: false,
        },
        {
          text: "Action",
          align: "start",
          sortable: false,
        },
        {
          text: "Action",
          align: "start",
          sortable: false,
        },
      ],
    };
  },
  watch: {
    options: {
      handler() {
        this.fetchHouseholdTenants();
      },
      deep: true,
    },
  },
  mounted() {
    this.userRole = authService.roles();
    this.userId = authService.getUserId();
    householdService.getHouseholdForUser(this.userId).then((response) => {
      this.householdId = response.data.id;
      this.fetchHouseholdTenants();
    });
  },
  methods: {
    fetchHouseholdTenants() {
      this.loading = true;
      const { sortBy, sortDesc, page, itemsPerPage } = this.options;
      userService
        .getTenantsForHousehold(page - 1, itemsPerPage, this.householdId)
        .then((response) => {
          this.tenants = response.data.content;
          this.totalTenants = response.data.totalElements;
          this.loading = false;
        });
    },
    upgradeRole(userId) {
      userService.upgradeUserRole(userId).then((response) => {
        this.tenants.forEach((t) => {
          if (t.id === userId) {
            t.authority = "HOUSE_HEAD";
          }
        });
      });
    },
    updateTenant(requestBody, id) {
      userService.updateTenant(requestBody, id).then((response) => {
        this.tenants.forEach((t) => {
          if (t.id === id) {
            t.name = requestBody.name;
            t.surname = requestBody.surname;
          }
        });
      });
    },
    createTenant(requestBody) {
      userService
        .createTenant(requestBody)
        .then((response) => {
          this.fetchHouseholdTenants();
        })
        .catch((err) => {
          if (err.response.data.message.includes("SQL")) {
            this.text = "That email is allready in use.";
          } else {
            this.text = err.response.data.message;
          }
          this.snackbar = true;
        });
    },
  },
};
</script>

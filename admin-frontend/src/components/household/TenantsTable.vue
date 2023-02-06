<template>
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
        <td>{{ row.item.email }}</td>
        <td>{{ row.item.name }}</td>
        <td>{{ row.item.surname }}</td>
        <td>
          <v-select
            style="margin-top: 20px; width: 200px"
            solo
            dense
            :items="roles"
            v-model="row.item.authority"
            @change="updateRole(row.item.id, row.item.authority)"
          ></v-select>
        </td>
        <td align="right">
          <v-btn
            class="mx-2"
            dark
            color="primary"
            @click="deleteUser(row.item.id)"
          >
            Delete
          </v-btn>
        </td>
      </tr>
    </template>
  </v-data-table>
</template>

<script>
import { userService } from "../../service/userService";

export default {
  name: "TenantsTableView",
  data() {
    return {
      tenants: [],
      totalTenants: 0,
      loading: true,
      options: {},
      roles: ["HOUSE_HEAD", "HOUSE_TENANT"],
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
          align: "end",
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
  methods: {
    fetchHouseholdTenants() {
      this.loading = true;
      const { sortBy, sortDesc, page, itemsPerPage } = this.options;
      userService
        .getTenantsForHousehold(
          page - 1,
          itemsPerPage,
          this.$route.params["id"]
        )
        .then((response) => {
          this.tenants = response.data.content;
          this.totalTenants = response.data.totalElements;
          this.loading = false;
        });
    },
    deleteUser(id) {
      userService.deleteUser(id).then((response) => {
        this.fetchHouseholdTenants();
        this.$root.$emit("tenantsUpdated");
      });
    },
    updateRole(id, role) {
      let requestBody = {
        id: id,
        role: role,
      };
      userService.updateTenantRole(requestBody).then((response) => {
        this.$root.$emit("tenantsUpdated");
      });
    },
  },
};
</script>

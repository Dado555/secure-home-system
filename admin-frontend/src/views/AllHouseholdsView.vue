<template>
  <v-container>
    <v-row>
      <v-col>
        <v-flex class="text-center">
          <h1>Households management</h1>
        </v-flex>
        <br />
        <br />
        <v-data-table
          :headers="headers"
          :items="households"
          :options.sync="options"
          :server-items-length="totalHouseholds"
          :loading="loading"
          class="elevation-1"
        >
          <template v-slot:item="row">
            <tr>
              <td>{{ row.item.id }}</td>
              <td>
                <span v-for="user in row.item.houseHeads" v-bind:key="user.id">
                  {{ user.name + " " + user.surname + ", " }}
                </span>
              </td>
              <td>
                <v-btn
                  class="mx-2"
                  dark
                  color="primary"
                  @click="detailedView(row.item.id)"
                >
                  Detailed view
                </v-btn>
              </td>
              <td>
                <v-btn
                  class="mx-2"
                  dark
                  color="primary"
                  @click="deleteHousehold(row.item.id)"
                >
                  Delete
                </v-btn>
              </td>
            </tr>
          </template>
        </v-data-table>
        <v-snackbar v-model="snackbar" :timeout="timeout">
          {{ text }}

          <template v-slot:action="{ attrs }">
            <v-btn color="blue" text v-bind="attrs" @click="snackbar = false">
              Close
            </v-btn>
          </template>
        </v-snackbar>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { householdService } from "../service/householdService";

export default {
  name: "AllHouseholdsView",
  data() {
    return {
      snackbar: false,
      text: "",
      timeout: 2000,
      households: [],
      totalHouseholds: 0,
      loading: true,
      options: {},
      headers: [
        {
          text: "Household ID",
          align: "start",
          sortable: false,
        },
        {
          text: "House Heads",
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
        this.fetchHouseholds();
      },
      deep: true,
    },
  },
  mounted() {},
  methods: {
    fetchHouseholds() {
      this.loading = true;
      const { sortBy, sortDesc, page, itemsPerPage } = this.options;
      householdService
        .getAllHouseholds(page - 1, itemsPerPage)
        .then((response) => {
          this.households = response.data.content;
          this.totalHouseholds = response.data.totalElements;
          this.loading = false;
        });
    },
    detailedView(householdId) {
      this.$router.push("/households/" + householdId);
    },
    deleteHousehold(id) {
      householdService
        .deleteHousehold(id)
        .then((response) => {
          this.fetchHouseholds();
        })
        .catch((err) => {
          this.text = err.response.data.message;
          this.snackbar = true;
        });
    },
  },
};
</script>

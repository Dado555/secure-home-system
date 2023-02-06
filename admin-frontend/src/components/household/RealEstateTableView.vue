<template>
  <v-container>
    <v-row>
      <v-col>
        <v-data-table
          :headers="headers"
          :items="realEstate"
          :options.sync="options"
          :server-items-length="totalRealestate"
          :loading="loading"
          class="elevation-1"
        >
          <template v-slot:item="row">
            <tr>
              <td>{{ row.item.name }}</td>
              <td>
                <v-select
                  style="margin-top: 20px; width: 250px"
                  solo
                  dense
                  label="List of users"
                  :items="row.item.canBeSeenBy"
                ></v-select>
              </td>
              <td align="right">
                <v-btn
                  class="mx-2"
                  dark
                  color="primary"
                  @click="seeDevicesRealEstate(row.item.id)"
                >
                  Configure devices
                </v-btn>
              </td>
              <td align="right">
                <v-btn
                  class="mx-2"
                  dark
                  color="primary"
                  @click="deleteRealEstate(row.item.id)"
                >
                  Delete
                </v-btn>
              </td>
            </tr>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { realEstateService } from "../../service/realEstateService";

export default {
  name: "RealEstateTableView",
  data() {
    return {
      realEstate: [],
      totalRealestate: 0,
      loading: true,
      options: {},
      headers: [
        {
          text: "Name",
          align: "start",
          sortable: false,
        },
        {
          text: "Can be seen by",
          align: "start",
          sortable: false,
        },
        {
          text: "Action",
          align: "end",
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
        this.fetchRealEstate();
      },
      deep: true,
    },
  },
  mounted() {
    this.$root.$on("tenantsUpdated", () => {
      this.fetchRealEstate();
    });
  },
  methods: {
    fetchRealEstate() {
      this.loading = true;
      const { sortBy, sortDesc, page, itemsPerPage } = this.options;
      realEstateService
        .getAllForHousehold(page - 1, itemsPerPage, this.$route.params.id)
        .then((response) => {
          this.realEstate = response.data.content;
          for (let i = 0; i < this.realEstate.length; i++) {
            for (let j = 0; j < this.realEstate[i].canBeSeenBy.length; j++) {
              this.realEstate[i].canBeSeenBy[j] =
                this.realEstate[i].canBeSeenBy[j].name +
                " " +
                this.realEstate[i].canBeSeenBy[j].surname;
            }
          }
          this.totalRealestate = response.data.totalElements;
          this.loading = false;
        });
    },
    deleteRealEstate(id) {
      realEstateService.deleteRealEstate(id).then((response) => {
        this.fetchRealEstate();
      });
    },
    seeDevicesRealEstate(id) {
      this.$router.push(this.$router.currentRoute.path + "/real-estate/" + id);
    },
  },
};
</script>

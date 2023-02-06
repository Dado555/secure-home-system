<template>
  <v-container>
    <v-row>
      <v-col>
        <v-flex class="text-center">
          <h1>My real estate</h1>
        </v-flex>
        <br />
        <v-data-table
          :headers="headers"
          :items="realEstates"
          :options.sync="options"
          :server-items-length="totalEstates"
          :loading="loading"
          class="elevation-1"
        >
          <template v-slot:item="row">
            <tr>
              <td align="left">{{ row.item.name }}</td>
              <td align="left">
                <v-container
                  style="overflow-x: auto; white-space: nowrap"
                  class="d-flex flex-row mb-6"
                  flat
                  tile
                >
                  <v-container
                    style="overflow-x: auto; white-space: nowrap"
                    class="d-flex flex-row mb-6"
                    flat
                    tile
                    v-for="user in row.item.canBeSeenBy"
                    v-bind:key="user.id"
                  >
                    <v-switch
                      v-if="user.authority !== 'HOUSE_HEAD'"
                      color="primary"
                      input-value="true"
                      @change="canSeeSwitch(row.item.id, user.id)"
                    >
                      <template v-slot:label>
                        {{ user.name }} {{ user.surname }}
                      </template>
                    </v-switch>
                    <v-switch v-else disabled input-value="true">
                      <template v-slot:label>
                        {{ user.name }} {{ user.surname }} - HH
                      </template>
                    </v-switch>
                  </v-container>
                </v-container>
              </td>
              <td align="left" v-if="!loading">
                <realestate-form
                  :edit="true"
                  :estateData="row.item"
                  :usersCanSee="row.item.canBeSeenBy.map(({ id }) => id)"
                  :reading-period="row.item.readingPeriod"
                  :userId="userId"
                  :callback="updateRealEstateName"
                ></realestate-form>
              </td>
              <td>
                <v-btn color="primary" @click="monitorRealEstate(row.item.id)"
                  >Monitor</v-btn
                >
              </td>
            </tr>
          </template>
        </v-data-table>
        <br />
      </v-col>
    </v-row>
    <v-row v-if="!loading">
      <v-col>
        <realestate-form
          :edit="false"
          :estateData="{}"
          :userId="userId"
          :callback="createRealEstate"
        ></realestate-form>
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
import { authService } from "@/service/authService";
import { realEstateService } from "@/service/realestateService";
import RealestateForm from "@/components/realestateComponents/RealestateForm";

export default {
  components: { RealestateForm },
  data() {
    return {
      realEstates: [],
      headers: [
        {
          text: "Name",
          align: "start",
          sortable: true,
        },
        {
          text: "Can be seen by",
          align: "start",
          sortable: false,
        },
      ],
      snackbar: false,
      timeout: 2000,
      text: "",
      loading: true,
      options: {},
      totalEstates: 0,
      userId: -1,
      userRole: "",
      cantSeeIds: {
        type: Array,
        required: true,
      },
    };
  },
  watch: {
    options: {
      handler() {
        this.getRealEstates();
      },
      deep: true,
    },
  },
  mounted() {
    this.userRole = authService.roles();
    this.userId = authService.getUserId();
    this.getRealEstates();
  },
  methods: {
    canSeeSwitch(reId, userId) {
      realEstateService
        .updateRealEstateVisibility(
          { userIds: [userId], realEstateId: reId },
          "remove"
        )
        .then((response) => {
          this.getRealEstates();
        })
        .catch((err) => {
          this.text = err.response.data.message;
          this.snackbar = true;
        });
    },

    getRealEstates() {
      this.loading = true;
      const { sortBy, sortDesc, page, itemsPerPage } = this.options;
      realEstateService.getRealEstatesForUser(this.userId).then((response) => {
        this.realEstates = response.data;
        this.totalEstates = response.data.length;
        this.loading = false;
      });
    },

    createRealEstate(realEstateBody) {
      realEstateService
        .createRealEstate(realEstateBody)
        .then((response) => {
          this.getRealEstates();
        })
        .catch((err) => {
          this.text = err.response.data.message;
          this.snackbar = true;
        });
    },

    updateRealEstateName(realEstateBody) {
      realEstateService
        .updateRealEstateName(realEstateBody)
        .then((response) => {
          realEstateService
            .updateRealEstateVisibility(
              {
                userIds: [...realEstateBody.canBeSeenByIds],
                realEstateId: realEstateBody.id,
              },
              "add"
            )
            .then((response) => {
              this.getRealEstates();
            })
            .catch((err) => {
              this.text = err.response.data.message;
              this.snackbar = true;
            });
        })
        .catch((err) => {
          this.text = err.response.data.message;
          this.snackbar = true;
        });
    },
    monitorRealEstate(id) {
      this.$router.push(this.$router.currentRoute.path + "/" + id);
    },
  },
};
</script>

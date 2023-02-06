<template>
  <v-container>
    <v-row>
      <v-col>
        <v-flex class="text-center">
          <h1>Detailed view</h1>
        </v-flex>
        <br />
        <br />
        <h2>Household Tenants</h2>
        <tenants-table-view></tenants-table-view>
        <br />
        <br />
        <h2>Household Real Estate</h2>
        <real-estate-table-view></real-estate-table-view>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import RealEstateTableView from "../components/household/RealEstateTableView.vue";
import TenantsTableView from "../components/household/TenantsTable.vue";

export default {
  name: "HouseholdDetailedView",
  components: { RealEstateTableView, TenantsTableView },
};
</script>

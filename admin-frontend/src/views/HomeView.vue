<template>
  <div style="margin: 0 5%">
    <h1 style="text-align: center; margin: 2%"> Request new certificate </h1>
    <v-card style="padding: 1%">
      <CertificateVersionInfo style="padding-left: 10%"/>
    </v-card>
  </div>
</template>

<script>
import CertificateVersionInfo from "@/components/createCertificate/CertificateVersionInfo";
export default {
  name: "Home",
  components: {CertificateVersionInfo},
};
</script>

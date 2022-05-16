<template>
  <q-page class="flex column flex-center" padding>
    <h2>Pujar fitxer Gestib</h2>
    <p>Alerta: el fitxer ha de ser del centre i de l'any acadèmic actual.</p>
    <q-file v-model="file" label="Fitxer" />
    <div class="flex q-ma-lg">
      <q-btn @click="upload" color="primary" class="q-ma-md">Puja fitxer XML</q-btn>
    </div>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'PageIndex',
  data () {
    return {
      file: null
    }
  },
  methods:{

    upload: async function(){
      console.log("Entra a upload",this.file);

      const formData = new FormData();
      formData.append("arxiu", this.file);

      const response = await this.$axios.post(
        process.env.API + "/sync/uploadfile",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
          withCredentials: true,
        }
      );

      const dataResponse = await response.data;
/*
      const responseGSuite = await this.$axios.post(process.env.API + "/sync/gsuiteuserstodatabase");
      const dataResponseGSuite = await responseGSuite.data;
*/
      //const responseNewUsers = await this.$axios.post(process.env.API + "/sync/newusers");
      //const responseReassignaEquipsEducatius = await this.$axios.get(process.env.API + "/sync/reassignarGrupsClasse",{});

      //const responseGSuite = await this.$axios.get(process.env.API + "/gsuite/hello");

    }
  }
})
</script>

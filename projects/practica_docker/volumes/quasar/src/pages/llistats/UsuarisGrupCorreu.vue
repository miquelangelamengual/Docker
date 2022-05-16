<template>
  <q-page class="flex column" padding>
    <p class="text-h3">Llistat d'usuaris desglossat per grup de correu</p>

    <div class="row">
      <q-checkbox v-for="grupCorreu of grupsCorreu" class="col-3" v-model="grupsCorreuModel" :val="grupCorreu" :label="grupCorreu.gsuiteEmail" />
    </div>
    <q-btn color="primary" icon="save" label="Llistat" @click="llistat" />

  </q-page>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'LlistatAlumnatGrupForm',
  data() {
    return {
      grupsCorreu: [],
      grupsCorreuModel: []
    }
  },
  created() {
    this.get();
  },
  methods: {
    get: async function () {
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })
      const responseGrupsCorreu = await this.$axios.get(process.env.API + '/grupcorreu/llistat');
      const dataGrupsCorreu = await responseGrupsCorreu.data;

      this.grupsCorreu = dataGrupsCorreu;
      dialog.hide();
    },
    llistat: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })
      await this.$axios.post(process.env.API + '/google/sheets/usuarisgrupcorreu',this.grupsCorreuModel);
      dialog.hide();
    }
  }
})
</script>

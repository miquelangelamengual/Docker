<template>
  <q-page class="flex column" padding>
    <p class="text-h3">Llistat d'alumnat desglossat per grup</p>

    <div class="row">
      <q-checkbox v-for="grupClasse of grupsClasse" class="col-3" v-model="grupsClasseModel" :val="grupClasse" :label="grupClasse.gestibNom" />
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
      grupsClasse: [],
      grupsClasseModel: []
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
      const responseGrupsClasse = await this.$axios.get(process.env.API + '/grup/llistat');
      const dataGrupsClasse = await responseGrupsClasse.data;
      /* TODO: PROMISE.ALL */
      const gClasse = dataGrupsClasse.map(async g=>{
        const responseCurs = await this.$axios.get(process.env.API + '/curs/getByCodiGestib/'+g.gestibCurs)
        const dataCurs = await responseCurs.data;
        g.gestibNom = dataCurs.gestibNom + ' '+ g.gestibNom;
        return g;
      });
      this.grupsClasse = await Promise.all(gClasse);
      dialog.hide();
    },
    llistat: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })
      await this.$axios.post(process.env.API + '/google/sheets/alumnatpergrup',this.grupsClasseModel);
      dialog.hide();
    }
  }
})
</script>

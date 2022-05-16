<template>
  <q-page class="flex column" padding>
    <p class="text-h3">Llistat de dispositius Chromebook per usuari</p>

    <q-btn color="primary" icon="save" label="Llistat" @click="llistat" />

  </q-page>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'LlistatAlumnatGrupForm',
  data() {
    return {
    }
  },
  created() {
    this.get();
  },
  methods: {
    get: async function () {
    },
    llistat: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })
      await this.$axios.post(process.env.API + '/google/sheets/usuarisdispositiu');
      dialog.hide();
    }
  }
})
</script>

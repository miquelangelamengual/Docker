<template>
  <q-page class="flex flex-center">
    Index...
    <p>Usuari: {{usuari}}</p>
    <q-btn @click="notifica" label="Notificació d'exemple"/>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'PageIndex',
  data() {
    return {
      usuari: "",
    }
  },
  created() {
    this.get();
  },
  methods: {
    get: async function () {
      //await this.$axios.get(process.env.API + '/gsuite/groups/load');
      //await this.$axios.get(process.env.API + '/gsuite/calendar/load');
      //await this.$axios.get(process.env.API + '/gsuite/departaments/load');
      //await this.$axios.get(process.env.API + '/gsuite/cursos/load');
      //await this.$axios.post(process.env.API + '/apps/grupscooperatius/genetica');
      //await this.$axios.post(process.env.API + '/auth/samplenotification');
    },
    notifica:async function () {
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })
      this.usuari="Joan";
      await this.$axios.post(process.env.API + '/auth/samplenotification');
      dialog.hide();

      //Redirect
      //this.$router.go();
      //this.$router.replace("/inici");
      //this.$router.push("/inici");
      //this.$router.push({ path: '/inici',hash: '#team' });
      this.reset();
    },
    reset() {
      Object.assign(this.$data, this.$options.data.call(this));
      this.$options.created.call(this);
    }
  }
})
</script>

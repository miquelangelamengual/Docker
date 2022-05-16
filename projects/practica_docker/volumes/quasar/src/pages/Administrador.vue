<template>
  <q-page class="flex column" padding>
    <h3>Eines d'administrador</h3>

    <q-btn @click="backupDatabase" color="primary" class="q-ma-md">Backup Database</q-btn>
    <small class="text-center">Còpia de seguretat de la base de dades. Upload al bucket de Google.</small>

    <q-btn @click="reassignarGrupsGSuiteToBBDD" color="primary" class="q-ma-md">Reassignar grups de GSuite a la BBDD.</q-btn>
    <small class="text-center">No actualitza GSuite, només la BBDD</small>

    <q-btn @click="sync" color="primary" class="q-ma-md">Sincronitza</q-btn>
    <small class="text-center">Si hi ha fitxer pujat, comença la sincronització. Alerta, dura molt de temps.</small>

    <q-btn @click="syncAlumnes" color="primary" class="q-ma-md">Reassigna grups alumnes</q-btn>
    <small class="text-center">Resincronitza els grups de TOTS els alumnes (Gestib i GSuite)</small>

    <q-btn @click="syncProfessors" color="primary" class="q-ma-md">Reassigna grups professors</q-btn>
    <small class="text-center">Resincronitza els grups de TOTS els professors (Gestib i GSuite)</small>

    <q-btn @click="provaGmail" color="primary" class="q-ma-md">Prova Gmail</q-btn>
    <small class="text-center">Missatge de prova de Gmail</small>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'PageIndex',
  created() {
    this.get();
  },
  methods: {
    get: async function () {
    },
    backupDatabase:async function(){
      await this.$axios.get(process.env.API + '/administrator/backupdatabase');
    },
    reassignarGrupsGSuiteToBBDD: async function(){
      await this.$axios.get(process.env.API + '/sync/reassignarGrupsCorreuGSuiteToDatabase');
    },
    sync: async function(){
      await this.$axios.post(process.env.API + "/sync/sincronitza");
    },
    syncAlumnes: async function(){
      await this.$axios.get(process.env.API + "/sync/reassignarGrupsAlumnes");
    },
    syncProfessors: async function(){
      await this.$axios.get(process.env.API + "/sync/reassignarGrupsProfessors");
    },
    provaGmail: async function(){
      await this.$axios.post(process.env.API + "/proves/gmail");
    }

  }
})
</script>

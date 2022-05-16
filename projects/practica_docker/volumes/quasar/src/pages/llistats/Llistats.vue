<template>
  <q-page class="flex flex-center">
    <q-list bordered class="rounded-borders">
      <q-item v-for="llistat in llistats" clickable v-ripple :to="llistat.url">
        <q-item-section avatar>
          <q-icon color="primary" name="assignment" />
        </q-item-section>
        <q-item-section>
          <q-item-label>{{llistat.nom}}</q-item-label>
        </q-item-section>
      </q-item>
    </q-list>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'PageCalendari',
  data() {
    return {
      llistats: []
    }
  },
  created() {
    this.get();
  },
  methods: {
    get: async function () {

      this.llistats.push({
        nom: 'Alumnat per grup',
        url: '/llistats/alumnatgrup'
      });

      this.llistats.push({
        nom: 'Usuaris per grup de correu',
        url: '/llistats/usuarisgrupcorreu'
      });

      this.llistats.push({
        nom: 'Usuaris per dispositiu',
        url: '/llistats/usuarisdispositiu'
      });

      this.llistats.push({
        nom: 'Usuaris personalitzat',
        url: '/llistats/usuariscustom'
      });
    }
  }
})
</script>

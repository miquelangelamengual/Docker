<template>
  <q-page class="flex flex-center">
    <q-list bordered class="rounded-borders">
      <q-item v-for="app in llistatApps" clickable v-ripple :to="app.url">
        <q-item-section>
          <q-item-label>{{app.nom}}</q-item-label>
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
      llistatApps: []
    }
  },
  created() {
    this.get();
  },
  methods: {
    get: async function () {

      this.llistatApps.push({
        nom: 'Grups Cooperatius',
        url: '/apps/grupscooperatius'
      });
    }
  }
})
</script>

<template>
  <q-page class="flex flex-center">
    <q-list bordered class="rounded-borders">
      <q-item v-for="calendari in calendaris" clickable v-ripple>
        <q-item-section top class="col-10 gt-sm">
          <q-item-label class="q-mt-sm">{{calendari.gsuiteDescripcio}}</q-item-label>
          <q-item-label class="q-mt-sm">{{calendari.gsuiteEmail}}</q-item-label>
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
      calendaris: [],
      confirm: false
    }
  },
  created() {
    this.get();
  },
  methods: {
    get: async function () {
      let response = await this.$axios.get(process.env.API + '/calendari/llistat');
      let data = await response.data;

      this.calendaris = data;
    }
  }
})
</script>

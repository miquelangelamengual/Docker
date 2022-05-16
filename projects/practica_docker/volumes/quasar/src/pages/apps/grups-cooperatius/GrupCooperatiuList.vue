<template>
  <q-page class="flex column" padding>

    <div>
      <q-btn-group push class="q-mb-lg q-mr-lg">
        <q-btn  color="primary" label="Nova mescla" icon="add" :to="'/apps/grupscooperatius/mescla'"/>
        <q-btn v-if="selected.length === 0" disable color="primary" label="Editar" icon="edit" />
        <q-btn  v-if="selected.length > 0" color="primary" label="Editar" icon="edit" :to="'/grupcorreu/'+selected[0].gsuiteEmail"/>
      </q-btn-group>

      <q-btn-group push class="q-mb-lg">
        <q-btn  color="primary" label="Gestionar ítems" icon="settings" :to="'/apps/grupscooperatius/items'"/>
      </q-btn-group>
    </div>

    <q-table
      title="Grups de correu GSuite"
      :rows="grupsCorreu"
      :columns="columnes"
      row-key="idgrup"
      selection="single"
      :filter="filter"
      v-model:selected="selected"
    >
      <template v-slot:top-right>
        <q-input borderless dense debounce="300" v-model="filter" placeholder="Cerca">
          <template v-slot:append>
            <q-icon name="search" />
          </template>
        </q-input>
      </template>
    </q-table>

  </q-page>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'PageGrupCorreu',
  data() {
    return {
      grupsCorreu: [],
      columnes: [],
      selected: [],
      filter: '',
    }
  },
  created() {
    this.get();
  },
  methods: {
    get: async function () {
      this.columnes = [
        {
          name: 'email',
          required: true,
          label: 'Correu electrònic',
          align: 'left',
          field: row => row.gsuiteEmail,
          sortable: true
        },
        {
          name: 'gsuitenom',
          required: true,
          label: 'Nom GSuite',
          align: 'left',
          field: row => row.gsuiteNom,
          format: val => `${val}`,
          sortable: true
        }
      ]

      let response = await this.$axios.get(process.env.API + '/grupcorreu/llistat');
      let data = await response.data;

      this.grupsCorreu = data;
    }
  }
})
</script>

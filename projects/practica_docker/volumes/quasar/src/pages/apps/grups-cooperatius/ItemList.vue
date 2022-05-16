<template>
  <q-page class="flex column" padding>
    <div>
      <q-btn-group push class="q-mb-lg q-mr-lg">
        <q-btn  color="primary" label="Nou ítem" icon="add" :to="'/apps/grupscooperatius/item'"/>
        <q-btn  v-if="selected.length == 0" disable color="primary" label="Editar" icon="edit" />
        <q-btn  v-if="selected.length > 0" color="primary" label="Editar" icon="edit" :to="'/apps/grupscooperatius/item/'+selected[0].id"/>
      </q-btn-group>

      <q-btn-group push class="q-mb-lg">
        <q-btn  color="primary" label="Gestionar grups cooperatius" icon="settings" :to="'/apps/grupscooperatius'"/>
      </q-btn-group>
    </div>

    <q-table
      title="Ítems dels grups cooperatius"
      :rows="items"
      :columns="columnes"
      row-key="id"
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

<script lang="ts">
import { defineComponent } from 'vue';
import {GrupCooperatiuService} from 'src/service/GrupCooperatiuService';
import {Item} from "src/model/apps/grupsCooperatius/Item";
import {ValorItem} from "src/model/apps/grupsCooperatius/ValorItem";
import {QTableColumn} from "quasar";

export default defineComponent({
  name: 'PageGrupCorreu',
  data() {
    return {
      items: [] as Item[],
      columnes: [] as QTableColumn[],
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
          name: 'nom',
          required: true,
          label: 'Nom',
          align: 'left',
          field: row => row.nom,
          sortable: true
        },
        {
          name: 'valors',
          required: true,
          label: 'Valors',
          align: 'left',
          field: row => row.valorsItem?row.valorsItem.map((v:ValorItem)=>v.valor + " ("+v.pes+"%)").join(', '):'',
          sortable: true
        }
      ]

      const items:Array<Item>= await GrupCooperatiuService.getItems();

      for (const item of items) {
        item.valorsItem = await GrupCooperatiuService.getValorItems(item);
        this.items.push(item);
      }
    }
  }
})
</script>

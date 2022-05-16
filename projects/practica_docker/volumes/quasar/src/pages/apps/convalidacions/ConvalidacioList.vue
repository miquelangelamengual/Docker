<template>
  <q-page class="flex column" padding>
    <div>
      <q-btn-group push class="q-mb-lg q-mr-lg">
        <q-btn  color="primary" label="Nova convalidació" icon="add" :to="'/apps/convalidacions/convalidacio'"/>
      </q-btn-group>

      <ConvalidacioListMenu></ConvalidacioListMenu>
    </div>
    <q-table
      title="Convalidacions"
      :rows="convalidacions"
      :columns="columnes"
      row-key="id"
      selection="single"
      :filter="filter"
      style="max-width: 100%"
      v-model:selected="selected"
    >
      <template v-slot:top-right>
        <q-input borderless dense debounce="300" v-model="filter" placeholder="Cerca">
          <template v-slot:append>
            <q-icon name="search" />
          </template>
        </q-input>
      </template>
      <template v-slot:body-cell-accions="props">
        <q-td :props="props">
          <div>
            <q-btn-group push>
              <q-btn icon="edit" color="primary" :to="'/apps/convalidacions/convalidacio/'+props.value">
                <q-tooltip>
                  Edita
                </q-tooltip>
              </q-btn>
              <q-btn icon="delete" color="primary" @click="esborrarConvalidacio(props.value)">
                <q-tooltip>
                  Esborra
                </q-tooltip>
              </q-btn>
            </q-btn-group>
          </div>
        </q-td>
      </template>
    </q-table>

  </q-page>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import {ConvalidacioService} from 'src/service/ConvalidacioService';
import {CategoriaConvalidacio} from "src/model/apps/convalidacions/CategoriaConvalidacio";
import {QTableColumn} from "quasar";
import {ItemConvalidacio} from "src/model/apps/convalidacions/ItemConvalidacio";
import {ConvalidacioConvalidacio} from "src/model/apps/convalidacions/ConvalidacioConvalidacio";
import ConvalidacioListMenu from 'components/ConvalidacioList_Menu.vue';


export default defineComponent({
  name: 'ConvalidacioList',
  data() {
    return {
      convalidacions: [] as ConvalidacioConvalidacio[],
      columnes: [] as QTableColumn[],
      selected: [],
      filter: '',
    }
  },
  components: { ConvalidacioListMenu },
  created() {
    this.get();
  },
  methods: {
    get: async function () {
      this.columnes = [
        {
          name: 'origens',
          required: true,
          label: 'Estudis Origen',
          align: 'left',
          field: row => row.origens.map((i:ItemConvalidacio)=>i.label).join(', '),
          sortable: true,
          style: 'overflow: hidden; white-space:pre-wrap; word-wrap:break-word; text-overflow: ellipsis;'
        },
        {
          name: 'convalida',
          required: true,
          label: 'Convaliden a',
          align: 'left',
          field: row => row.convalida.map((i:ItemConvalidacio)=>i.label).join(', '),
          sortable: true,
          style: 'overflow: hidden; white-space:pre-wrap; word-wrap:break-word; text-overflow: ellipsis;'
        },
        {
          name: 'accions',
          required: true,
          label: 'Accions',
          align: 'center',
          field: row => row.id,
          sortable: false
        }
      ]

      const convalidacions:Array<ConvalidacioConvalidacio>= await ConvalidacioService.getConvalidacions();
      this.convalidacions = convalidacions;
    },
    esborrarConvalidacio(id:number){
      this.$q.dialog({
        title: 'Confirm',
        message: 'Vol eliminar aquesta convalidació?',
        ok: "D'acord",
        cancel: "Cancel·la",
        persistent: true
      }).onOk(async () => {
        console.log('>>>> OK',id)
        await ConvalidacioService.esborrarConvalidacio(id);
        //Refresh data
        window.location.reload();
      })
    }
  }
})
</script>

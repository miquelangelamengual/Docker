<template>
  <q-page class="flex column" padding>
    <div>
      <q-btn-group push class="q-mb-lg q-mr-lg">
        <q-btn  color="primary" label="Nova sol·licitud" icon="add" :to="'/apps/convalidacions/solicitud'"/>
      </q-btn-group>

      <ConvalidacioListMenu></ConvalidacioListMenu>
    </div>

    <q-table
      title="Sol·licituds"
      :rows="solicituds"
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
      <template v-slot:body-cell-accions="props">
        <q-td :props="props">
          <div>
            <q-btn-group push>
              <q-btn icon="edit" color="primary" :to="'/apps/convalidacions/solicitud/'+props.value">
                <q-tooltip>
                  Edita
                </q-tooltip>
              </q-btn>
              <q-btn icon="delete" color="primary" @click="esborrarSolicitud(props.value)">
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
import ConvalidacioListMenu from 'components/ConvalidacioList_Menu.vue';
import {SolicitudConvalidacio} from "src/model/apps/convalidacions/SolicitudConvalidacio";


export default defineComponent({
  name: 'SolicitudList',
  data() {
    return {
      solicituds: [] as SolicitudConvalidacio[],
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
          name: 'nom',
          required: true,
          label: 'Nom',
          align: 'left',
          field: row => row.nom,
          sortable: true
        },
        {
          name: 'categoria',
          required: true,
          label: 'Categoria',
          align: 'left',
          field: row => row.categoria.nom,
          sortable: true
        },
        {
          name: 'accions',
          required: true,
          label: 'Accions',
          align: 'center',
          field: row => row.id,
          sortable: true
        }
      ]

      const solicituds:Array<SolicitudConvalidacio>= await ConvalidacioService.getSolicituds();
      this.solicituds = solicituds;
    },
    esborrarSolicitud(id:number){
      this.$q.dialog({
        title: 'Confirm',
        message: 'Vol eliminar aquesta sol·licitud?',
        ok: "D'acord",
        cancel: "Cancel·la",
        persistent: true
      }).onOk(async () => {
        console.log('>>>> OK',id)
        await ConvalidacioService.esborrarSolicitud(id);
        //Refresh data
        window.location.reload();
      })
    }
  }
})
</script>

<template>
  <q-page class="flex column" padding>
    <div>
      <ConvalidacioListMenu></ConvalidacioListMenu>
    </div>
    <p class="text-h5">Estudis ORIGEN</p>

    <q-select
      filled
      :model-value="itemsOrigenSelected"
      use-input
      hide-selected
      fill-input
      input-debounce="0"
      :options="itemsFiltered"
      @filter="itemsFilterFn"
      @input-value="setItemsOrigenModel"
      label="Afegir estudis d'origen"
      class="q-mb-lg"
    >
      <template v-slot:no-option>
        <q-item>
          <q-item-section class="text-grey">
            Sense resultats
          </q-item-section>
        </q-item>
      </template>
    </q-select>

    <q-list bordered separator>
      <q-item v-for="item in convalidacio.origens" clickable>
        <q-item-section>{{item.label}}</q-item-section>
        <q-item-section>
          <q-btn icon="delete" color="primary" dense @click="deleteItemOrigen(item)">
            <q-tooltip>
              Esborra
            </q-tooltip>
          </q-btn>
        </q-item-section>
      </q-item>
    </q-list>

    <p class="text-h5">Estudis DESTÍ</p>

    <q-select
      filled
      :model-value="itemsDestiSelected"
      use-input
      hide-selected
      fill-input
      input-debounce="0"
      :options="itemsFiltered"
      @filter="itemsFilterFn"
      @input-value="setItemsDestiModel"
      label="Afegir estudis de destí"
      class="q-mb-lg"
    >
      <template v-slot:no-option>
        <q-item>
          <q-item-section class="text-grey">
            Sense resultats
          </q-item-section>
        </q-item>
      </template>
    </q-select>

    <q-list bordered separator>
      <q-item v-for="item in convalidacio.convalida" clickable>
        <q-item-section>{{item.label}}</q-item-section>
        <q-item-section>
          <q-btn icon="delete" color="primary" dense @click="deleteItemDesti(item)">
            <q-tooltip>
              Esborra
            </q-tooltip>
          </q-btn>
        </q-item-section>
      </q-item>
    </q-list>

    <p class="text-h5">Resum</p>
    <p>Els estudis <strong>{{convalidacio.origens.map(i=>`${i.codi+' - ' || ''}${i.nom}`).join(", ") || 'SENSE ESTUDIS'}}</strong> convaliden a <strong>{{convalidacio.convalida.map(i=>`${i.codi+' - ' || ''}${i.nom}`).join(", ") || 'SENSE ESTUDIS'}}</strong></p>

    <q-btn color="primary" icon="save" label="Desar" @click="save" />

  </q-page>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import {ItemConvalidacio} from "src/model/apps/convalidacions/ItemConvalidacio";
import {ConvalidacioService} from "src/service/ConvalidacioService";
import {ConvalidacioConvalidacio} from "src/model/apps/convalidacions/ConvalidacioConvalidacio";
import ConvalidacioListMenu from 'components/ConvalidacioList_Menu.vue';


export default defineComponent({
  name: 'ConvalidacioForm',
  data() {
    return {
      convalidacio: {
        origens: [] as ItemConvalidacio[],
        convalida: [] as ItemConvalidacio[],
      } as ConvalidacioConvalidacio,
      items: [] as ItemConvalidacio[],
      itemsFiltered: [] as ItemConvalidacio[],
      itemsOrigenSelected: [] as ItemConvalidacio[],
      itemsDestiSelected: [] as ItemConvalidacio[],
    };
  },
  components: { ConvalidacioListMenu },
  created() {
    this.get();
  },
  methods: {
    get: async function () {
      /*const dialog = await this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })*/

      const items:ItemConvalidacio[] = await ConvalidacioService.getItems();
      this.items = items;
      this.itemsFiltered = items;

      //await dialog.hide();
    },
    setItemsOrigenModel (val:string) {
      //console.log(val)
      const item:ItemConvalidacio|undefined = this.items.find((item:ItemConvalidacio)=>item.label===val);
      if(item != undefined) {
        this.convalidacio.origens.push(item);
        this.itemsOrigenSelected = [];
      }
    },
    setItemsDestiModel (val:string) {
      //console.log(val)
      const item:ItemConvalidacio|undefined = this.items.find((item:ItemConvalidacio)=>item.label===val);
      if(item != undefined) {
        this.convalidacio.convalida.push(item);
        this.itemsDestiSelected = [];
      }
    },
    itemsFilterFn (val:string, update:Function) {
      if (val === '') {
        update(() => {
          this.itemsFiltered = this.items;
        })
        return
      }

      update(() => {
        const needle = val.toLowerCase()
        this.itemsFiltered = this.items.filter(i=>i.label!.toLowerCase().indexOf(needle) > -1 || false);
      })
    },
    deleteItemOrigen(item:ItemConvalidacio){
      this.convalidacio.origens = this.convalidacio.origens.filter(i=>i!==item);
    },
    deleteItemDesti(item:ItemConvalidacio){
      this.convalidacio.convalida = this.convalidacio.convalida.filter(i=>i!==item);
    },
    async save(){
      await ConvalidacioService.saveConvalidacio(this.convalidacio);
    }
  }
})
</script>

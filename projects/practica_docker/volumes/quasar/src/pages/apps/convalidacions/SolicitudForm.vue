<template>
  <q-page class="flex column" padding>
    <div>
      <ConvalidacioListMenu></ConvalidacioListMenu>
    </div>

    <p class="text-h5">Alumne</p>

    <q-select
      filled
      v-model="solicitud.alumne"
      use-input
      input-debounce="0"
      label="Alumne"
      :options="alumnesFiltered"
      @filter="alumneFilterFn"
    >
      <template v-slot:no-option>
        <q-item>
          <q-item-section class="text-grey">
            No results
          </q-item-section>
        </q-item>
      </template>
    </q-select>

    <p class="text-h5">Estudis cursats anteriorment</p>

    <q-select
      filled
      v-model="solicitud.alumne"
      use-input
      input-debounce="0"
      label="Alumne"
      :options="alumnesFiltered"
      @filter="alumneFilterFn"
    >
      <template v-slot:no-option>
        <q-item>
          <q-item-section class="text-grey">
            No results
          </q-item-section>
        </q-item>
      </template>
    </q-select>
<!--
    <q-input v-model="titulacio.codi" label="Codi" />
    <q-input v-model="titulacio.nom" label="Nom" />

    <q-select
      v-model="titulacio.categoria"
      :options="categories"
      label="Categoria titulació"
      class="q-mb-lg"
    />

    <p class="text-h5">Mòduls i/o Assignatures depenents del títol {{titulacio.nom}}</p>
    <p class="text-caption">Les Qualificacions Professionals i Unitats de Competència s'han d'introduir com a títols nous, ja que són INDEPENDENTS del títol.</p>

    <div>
      <q-btn color="primary" label="Nou ítem del títol" icon="edit" @click="nouItemConvalidacio" class="q-mb-lg q-mt-lg"/>
    </div>

    <q-list bordered class="rounded-borders">

      <q-item-label header v-if="titulacio && titulacio.composa && titulacio.composa.length > 0">Composició del títol</q-item-label>

      <template v-for="item in titulacio.composa">
        <q-item  clickable dense class="q-mb-md">
          <q-item-section top class="col-10 q-ma-none flex row">
            <q-input class="col-6" v-model="item.codi" label="Codi"/>
            <q-input class="col-6" v-model="item.nom" label="Nom"/>
            <q-select
              v-model="item.categoria"
              :options="categories"
              label="Categoria ítem"
              class="q-mb-lg"
            />
          </q-item-section>

          <q-item-section top side>
            <div class="text-grey-8">
              <q-btn size="12px" flat dense round icon="delete" @click="deleteItemConvalidacio(item)" />
            </div>
          </q-item-section>
        </q-item>
        <q-separator />
      </template>
    </q-list>


    <q-btn color="primary" icon="save" label="Desar" @click="save" />
-->
  </q-page>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import {Item} from "src/model/apps/grupsCooperatius/Item";
import {GrupCooperatiuService} from "src/service/GrupCooperatiuService";
import {ValorItem} from "src/model/apps/grupsCooperatius/ValorItem";
import {ItemConvalidacio} from "src/model/apps/convalidacions/ItemConvalidacio";
import {CategoriaConvalidacio} from "src/model/apps/convalidacions/CategoriaConvalidacio";
import {ConvalidacioService} from "src/service/ConvalidacioService";
import {Usuari} from "src/model/Usuari";
import ConvalidacioListMenu from 'components/ConvalidacioList_Menu.vue';
import {SolicitudConvalidacio} from "src/model/apps/convalidacions/SolicitudConvalidacio";
import {UsuariService} from "src/service/UsuariService";


export default defineComponent({
  name: 'SolicitudForm',
  data() {
    return {
      solicitud: {} as SolicitudConvalidacio,
      titulacions: [] as ItemConvalidacio[],
      alumnes: [] as Usuari[],
      alumnesFiltered: [] as Usuari[]
    }
  },
  components: { ConvalidacioListMenu },
  created() {
    this.get();
  },
  methods: {
    get: async function () {
      const dialog = await this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      const titulacions:ItemConvalidacio[] = await  ConvalidacioService.getTitulacions();
      this.titulacions = titulacions.map((titulacio:ItemConvalidacio)=>{
        titulacio.label = titulacio.nom;
        titulacio.value = titulacio.id!.toString();
        return titulacio;
      });

      const usuaris:Usuari[] = await UsuariService.findUsuarisActius();
      this.alumnes = usuaris.filter((u:Usuari)=>u.esAlumne).map((u:Usuari)=>{
        u.label = u.nomComplet;
        u.value = u.id.toString();
        return u;
      })
      this.alumnesFiltered = this.alumnes;

      const id:string = (this.$route.params.id)?this.$route.params.id[0]:'';

      if(id && id!='') {
        console.log(id)
        //const titulacio:ItemConvalidacio = await ConvalidacioService.getTitulacioById(parseInt(id));

        //this.titulacio = titulacio;
      }

      await dialog.hide();
    },
    alumneFilterFn (val:string, update:Function) {
      if (val === '') {
        update(() => {
          this.alumnesFiltered = this.alumnes;
        })
        return
      }

      update(() => {
        const needle = val.toLowerCase()
        this.alumnesFiltered = this.alumnes.filter(i=>i.label!.toLowerCase().indexOf(needle) > -1 || false);
      })
    },
    save: async function(){
      const dialog = await this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      //await ConvalidacioService.saveTitulacio(this.titulacio);
      await dialog.hide();

      //Redirect
      await this.$router.push('/apps/convalidacions/titulacions');
    }
  }
})
</script>

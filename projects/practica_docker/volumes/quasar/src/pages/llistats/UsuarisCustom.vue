<template>
  <q-page class="flex column" padding>
    <p class="text-h3">Llistat d'usuaris personalitzat</p>

    <q-select
      filled
      :model-value="usuarisSelected"
      use-input
      hide-selected
      fill-input
      input-debounce="0"
      :options="usuarisFiltered"
      @filter="usuarisFilterFn"
      @input-value="setUsuarisModel"
      label="Afegir usuaris"
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

    <q-select
      filled
      :model-value="grupsCorreuSelected"
      use-input
      hide-selected
      fill-input
      input-debounce="0"
      :options="grupsCorreuFiltered"
      @filter="grupsCorreuFilterFn"
      @input-value="setGrupsCorreuModel"
      label="Afegir usuaris des d'un grup de correu"
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
      <q-item v-for="usuari in usuarisLlistat" clickable>
        <q-item-section>{{usuari.cognom1}} {{usuari.cognom2}}, {{usuari.nom}}</q-item-section>
        <q-item-section>
          <q-btn icon="delete" color="primary" dense @click="deleteUsuarisLlistat(usuari)">
            <q-tooltip>
              Esborra
            </q-tooltip>
          </q-btn>
        </q-item-section>
      </q-item>
    </q-list>

    <q-btn color="primary" icon="save" label="Llistat" @click="llistat" />

  </q-page>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import {Usuari} from "../../model/Usuari";
import {UsuariService} from "src/service/UsuariService";
import {GrupCorreu} from "src/model/google/GrupCorreu";
import {GrupCorreuService} from "src/service/GrupCorreuService";

export default defineComponent({
  name: 'UsuarisCustom',
  data() {
    return {
      usuarisLlistat: [] as Usuari[],
      usuaris: [] as Usuari[],
      usuarisFiltered: [] as Usuari[],
      usuarisSelected: [] as Usuari[],
      grupsCorreu: [] as GrupCorreu[],
      grupsCorreuFiltered: [] as GrupCorreu[],
      grupsCorreuSelected: [] as GrupCorreu[],
    }
  },
  created() {
    this.get();
  },
  methods: {
    get: async function () {
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })
      const usuaris:Usuari[] = await UsuariService.findUsuarisActius();
      this.usuaris = usuaris;
      this.usuarisFiltered = usuaris;

      const grupsCorreu:GrupCorreu[] = await GrupCorreuService.findAll();
      this.grupsCorreu = grupsCorreu;
      this.grupsCorreuFiltered = grupsCorreu;
      dialog.hide();
    },
    setUsuarisModel (val:string) {
      //console.log(val)
      const usuari:Usuari|undefined = this.usuaris.find((usuari:Usuari)=>usuari.label===val);
      if(usuari != undefined) {
        this.usuarisLlistat.push(usuari);
        this.usuarisLlistat.sort((a:Usuari,b:Usuari)=>{
          const astr = a.cognom1+' '+a.cognom2+' '+a.nom;
          const bstr = b.cognom1+' '+b.cognom2+' '+b.nom;
          return astr.localeCompare(bstr);
        })
        this.usuarisSelected = [];
      }
    },
    async setGrupsCorreuModel (val:string) {
      //console.log(val)
      const grupCorreu:GrupCorreu|undefined = this.grupsCorreu.find((grupCorreu:GrupCorreu)=>grupCorreu.label===val);
      if(grupCorreu != undefined) {
        const grupCorreuUsuaris:GrupCorreu = await GrupCorreuService.getGrupAmbUsuaris(grupCorreu);
        grupCorreuUsuaris.usuaris?.forEach((usuari:Usuari)=>{
          this.usuarisLlistat.push(usuari);
        })
        this.usuarisLlistat.sort((a:Usuari,b:Usuari)=>{
          const astr = a.cognom1+' '+a.cognom2+' '+a.nom;
          const bstr = b.cognom1+' '+b.cognom2+' '+b.nom;
          return astr.localeCompare(bstr);
        })
        this.grupsCorreuSelected = [];
      }
    },
    usuarisFilterFn (val:string, update:Function) {
      if (val === '') {
        update(() => {
          this.usuarisFiltered = this.usuaris;
        })
        return
      }

      update(() => {
        const needle = val.toLowerCase()
        this.usuarisFiltered = this.usuaris.filter(i=>i.label!.toLowerCase().indexOf(needle) > -1 || false);
      })
    },
    grupsCorreuFilterFn (val:string, update:Function) {
      if (val === '') {
        update(() => {
          this.grupsCorreuFiltered = this.grupsCorreu;
        })
        return
      }

      update(() => {
        const needle = val.toLowerCase()
        this.grupsCorreuFiltered = this.grupsCorreu.filter(i=>i.label!.toLowerCase().indexOf(needle) > -1 || false);
      })
    },
    deleteUsuarisLlistat(usuari:Usuari){
      this.usuarisLlistat = this.usuarisLlistat.filter(i=>i!==usuari);
    },
    llistat: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })
      await this.$axios.post(process.env.API + '/google/sheets/usuariscustom',this.usuarisLlistat.map((u:any)=> {
        u.idusuari = u.id
        return u;
      }));

      dialog.hide();
    }
  }
})
</script>

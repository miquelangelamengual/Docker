<template>
  <q-page class="flex column" padding>
    <p class="text-h3">{{item.nom}}</p>

    <q-input v-model="item.nom" label="Nom" />

    <p class="text-h5">Valors de l'ítem {{item.nom}}</p>

    <div>
      <q-btn color="primary" label="Nou valor" icon="edit" @click="nouValor" class="q-mb-lg q-mt-lg"/>
    </div>

    <q-list bordered class="rounded-borders">

      <q-item-label header v-if="item && item.valors && item.valors.length > 0">Ítems</q-item-label>

      <template v-for="valor in item.valorsItem">
        <q-item  clickable v-ripple dense class="q-mb-md">
          <q-item-section top class="col-10 q-ma-none flex row">
            <q-input class="col-6" v-model="valor.valor" label="Nom" hint="Nom del valor. P. ex: HOME, DONA, 10, 5..."/>
            <q-input class="col-6" v-model="valor.pes" label="Pes" type="number" min="0" max="100" hint="Valors permesos: de 0% a 100%" />
          </q-item-section>

          <q-item-section top side>
            <div class="text-grey-8">
              <q-btn size="12px" flat dense round icon="delete" @click="deleteValor(valor)" />
            </div>
          </q-item-section>
        </q-item>
        <q-separator />
      </template>
    </q-list>


    <q-btn color="primary" icon="save" label="Desar" @click="save" />

  </q-page>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import {Item} from "src/model/apps/grupsCooperatius/Item";
import {GrupCooperatiuService} from "src/service/GrupCooperatiuService";
import {ValorItem} from "src/model/apps/grupsCooperatius/ValorItem";

export default defineComponent({
  name: 'PageGrupCorreuForm',
  data() {
    return {
      item: {} as Item
    }
  },
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

      const id:string = this.$route.params.id[0];

      if(id) {
        console.log(id)
        const item:Item = await GrupCooperatiuService.getItemById(parseInt(id));
        item.valorsItem = await GrupCooperatiuService.getValorItems(item);

        this.item = item;
      }

      await dialog.hide();
    },
    nouValor: function(){
      const valorItem: ValorItem = {
        pes: 100, valor: "", item: this.item
      }
      this.item.valorsItem?.push(valorItem)
    },
    deleteValor: function(v:ValorItem){
      this.item.valorsItem = this.item.valorsItem?.filter((valor:ValorItem)=>valor.id!==v.id);
    },
    save: async function(){
      const dialog = await this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      await GrupCooperatiuService.save(this.item);
      await dialog.hide();

      //Redirect
      await this.$router.push('/apps/grupscooperatius/items');
    }
  }
})
</script>

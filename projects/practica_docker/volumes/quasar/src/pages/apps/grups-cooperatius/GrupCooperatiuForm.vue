<template>
  <q-page class="flex column" padding>
    <h1 class="text-h3">Mescla de grups</h1>

    <p class="text-h3">{{grupCooperatiu.nom}}</p>

    <q-input v-model="grupCooperatiu.nom" label="Nom de la mescla" />
    <q-input v-model="grupCooperatiu.numGrups" label="Número de grups" type="number" min="1" />

    <!-- USUARIS -->
    <p class="text-h5">Selecciona els usuaris de la mescla</p>

    <q-select
      filled
      :model-value="userSelected"
      use-input
      hide-selected
      fill-input
      input-debounce="0"
      :options="userOptions"
      @filter="userFilterFn"
      @input-value="setUserModel"
      label="Afegir usuaris de GSuite"
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
      :model-value="grupSelected"
      use-input
      hide-selected
      fill-input
      input-debounce="0"
      :options="grupOptions"
      @filter="grupFilterFn"
      @input-value="setGrupModel"
      label="Afegir grups de GSuite"
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

    <!-- ITEMS -->
    <p class="text-h5">Selecciona els ítems de la mescla</p>
    <p class="text">Només es tindran en compte els ítems en la mescla genètica</p>

    <q-select
      filled
      :model-value="itemSelected"
      use-input
      hide-selected
      fill-input
      input-debounce="0"
      :options="itemOptions"
      @filter="itemFilterFn"
      @input-value="setItemModel"
      label="Afegir ítems"
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

    <q-list bordered class="rounded-borders">

      <q-item-label header v-if="grupCooperatiu.items.length > 0">Ítems</q-item-label>

      <q-item v-for="item in grupCooperatiu.items" clickable v-ripple>
        <q-item-section top class="col-10 gt-sm">
          <q-item-label class="q-mt-sm col-8">{{item.item.nom}}</q-item-label>
          <q-item-label class="q-mt-sm col-4"><q-input v-model="item.percentatge" label="Ponderació" type="number" min="0" max="100" /></q-item-label>
        </q-item-section>

        <q-item-section top side>
          <div class="text-grey-8 q-gutter-xs">
            <q-btn size="12px" flat dense round icon="delete" @click="deleteItem(item)" />
          </div>
        </q-item-section>
      </q-item>
    </q-list>

    <q-list bordered class="rounded-borders">

      <q-item-label header v-if="grupCooperatiu.members.length > 0">Usuaris</q-item-label>

      <q-expansion-item v-for="user in grupCooperatiu.members"
                        clickable
                        v-ripple
                        expand-separator
                        default-opened
                        icon="perm_identity"
                        :label="user.nom">
        <q-item-section top class="col-10 gt-sm">
          <q-list bordered class="rounded-borders">

            <q-item-label header v-if="grupCooperatiu.items.length > 0">Ítems</q-item-label>

            <q-item v-for="(item,idx) in grupCooperatiu.items" clickable v-ripple>
              <q-item-section top class="col-10 gt-sm">
                <q-item-label class="q-mt-sm col-8">{{item.item.nom}}</q-item-label>
                <q-item-label class="q-mt-sm col-4">
                  <q-select v-model="user.valorsItemMapped[idx]" label="Valor" :options="item.item.valorsMapped" />
                </q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-item-section>

        <q-item-section top side>
          <div class="text-grey-8 q-gutter-xs q-mb-lg q-mt-md q-mr-md">
            <q-btn size="12px" color="red" label="Esborrar" icon="delete" @click="deleteMember(user)" />
          </div>
        </q-item-section>
      </q-expansion-item>
    </q-list>



    <!-- MESCLES -->

    <q-btn  v-if="grupCooperatiu.members.length > 0" color="primary" label="Mescla aleatòria" icon="edit" @click="mesclaAleatoria" class="q-mb-lg q-mt-lg"/>
    <q-btn  v-if="grupCooperatiu.members.length > 0 && grupCooperatiu.items.length > 0" color="primary" label="Mescla genètica" icon="edit" @click="mesclaGenetica" class="q-mb-lg q-mt-lg"/>

    <p class="text-h3" v-if="result.length > 0">Resultat</p>
    <q-list v-if="result.length > 0" v-for="(grup,i) in result" bordered class="rounded-borders q-mb-lg">

      <q-item-label header>Grup {{i+1}}</q-item-label>

      <q-item v-for="membre in grup.membres" clickable v-ripple>
        <q-item-section top class="col-10 gt-sm">
          <q-item-label class="q-mt-sm">{{membre.nom}}</q-item-label>
        </q-item-section>
      </q-item>

      <q-item v-for="item in grupCooperatiu.items" clickable v-ripple>
        <q-item-section top class="gt-sm">
          <q-item-label class="q-mt-sm">{{item.item.nom}}</q-item-label>
          <q-item-label v-for="valorItem in item.item.valorsMapped" class="q-mt-sm">{{valorItem.label}} {{countValorItemMembers(valorItem,grup.membres)}}</q-item-label>
        </q-item-section>
      </q-item>
    </q-list>

    <div v-show="result.length > 0">
      <q-select
        :model-value="itemChart"
        :options="itemOptions"
        label="Ítem a visualitzar"
        class="q-mb-lg"
        @input-value="paintGraph"
        @update:model-value="paintGraph"
      />
      <canvas id="myChart" width="400" height="400"></canvas>
    </div>

  </q-page>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import {Item} from "src/model/apps/grupsCooperatius/Item";
import {ItemGrupCooperatiu} from "src/model/apps/grupsCooperatius/ItemGrupCooperatiu";
import {UsuariService} from "src/service/UsuariService";
import {Usuari} from "src/model/Usuari";
import {GrupCooperatiuService} from "src/service/GrupCooperatiuService";
import {GrupCorreu} from "src/model/google/GrupCorreu";
import {GrupCorreuService} from "src/service/GrupCorreuService";
import {Membre} from "src/model/apps/grupsCooperatius/Membre";
import {Agrupament} from "src/model/apps/grupsCooperatius/Agrupament";
import {ValorItem} from "src/model/apps/grupsCooperatius/ValorItem";
import {ValorItemMembre} from "src/model/apps/grupsCooperatius/ValorItemMembre";
import Chart from 'chart.js/auto'



export default defineComponent({
  name: 'PageGrupCorreuForm',
  data() {
    return {
      grupCooperatiu: {
        idGrupCooperatiu: null,
        nom: '',
        members: [] as Membre[],
        items: [] as ItemGrupCooperatiu[],
        numGrups: 2
      },
      userSelected:[] as Usuari[],
      grupSelected: [] as GrupCorreu[],
      itemSelected: [] as Item[],
      userOptions: new Array<{label:string, value:string}>(),
      grupOptions: new Array<{label:string, value:string}>(),
      itemOptions: new Array<{label:string, value:string}>(),
      filterUsuaris: '',
      filterGrups: '',
      filterItems: '',
      users: [] as Usuari[],
      grups: [] as GrupCorreu[],
      items: [] as Item[],
      result:[] as Agrupament[],
      itemChart: null
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

      const usuaris:Array<Usuari>= await UsuariService.findUsuarisActius();
      this.users=usuaris;

      const grupsCorreu:Array<GrupCorreu> = await GrupCorreuService.findAll();
      this.grups = grupsCorreu;

      const items:Array<Item>= await GrupCooperatiuService.getItems();
      this.items = items;

      this.userOptions = this.users.map((user:Usuari)=>{
        return {
          label: user.nomComplet+' ('+user.email+')',
          value: user.email
        }
      })

      this.grupOptions = this.grups.map(grup=>{
        return {
          label: grup.nom + ' ('+grup.email+')',
          value: grup.email
        }
      })

      this.itemOptions = this.items.map(item=>{
        return {
          label: item.nom,
          value: item.id.toString()
        }
      })

      dialog.hide();
    },
    deleteMember: async function(member:Membre){
      this.grupCooperatiu.members = this.grupCooperatiu.members.filter(m=>{
        return m != member
      })

      await this.updateMembersItems()
    },
    deleteItem: async function (item: ItemGrupCooperatiu){
      this.grupCooperatiu.items = this.grupCooperatiu.items.filter(i=>{
        return i.item.id != item.item.id
      })
      await this.updateMembersItems()
    },
    async setUserModel (val:string) {
      const usuari:Usuari|undefined = this.users.find(user=> {
        return user.nomComplet + ' ('+user.email+')' === val
      });
      if(usuari){
        //console.log("Usuari.ts ",usuari);
        const member:Membre = {
          nom: usuari.nomComplet,
          valorsItemMembre: [],
          valorsItemMapped: []
        }
        this.grupCooperatiu.members.push(member)
        this.grupCooperatiu.members.sort((a,b)=>a.nom.localeCompare(b.nom))
        this.userSelected = [];
      }

      await this.updateMembersItems()
    },
    async setGrupModel (val:string) {
      //console.log("setGrupModel",val)
      let grup:GrupCorreu|undefined = this.grups.find(grup=> {
        return grup.nom + ' ('+grup.email+')' === val
      })
      if(grup){
        const grupCorreuUsuaris:GrupCorreu|undefined = await GrupCorreuService.getGrupAmbUsuaris(grup);
        if(grupCorreuUsuaris && grupCorreuUsuaris.usuaris) {
          grupCorreuUsuaris.usuaris.forEach((user: Usuari) => {
            this.grupCooperatiu.members.push({
              nom: user.nomComplet,
              valorsItemMembre: [],
              valorsItemMapped: []
            })
            this.grupSelected = [];
          })
        }
      }

      this.grupCooperatiu.members.sort((a,b)=>a.nom.localeCompare(b.nom))

      await this.updateMembersItems()
    },
    async setItemModel (val:string) {
      //console.log(val);
      let item = this.items.find(item=> {
        return item.nom === val
      })
      if(item){
        this.grupCooperatiu.items.push({
          item: item,
          percentatge: 100
        })

        const valorItem:ValorItem|undefined = (item.valorsItem)?item.valorsItem[0]:undefined;
        if(valorItem!=undefined) {
          this.grupCooperatiu.members.forEach((membre: Membre) => {
            membre.valorsItemMembre.push({
              membre: membre,
              valorItem: valorItem
            })
          })
        }

        this.itemSelected = [];
      }
      await this.updateMembersItems()
    },
    userFilterFn (val:string, update:Function) {
      if (val === '') {
        update(() => {
          this.userOptions = this.users.map(user=>{
            return {
              label: user.nomComplet + ' ('+user.email+')',
              value: user.email
            }
          })
        })
        return
      }

      update(() => {
        const needle = val.toLowerCase()
        this.userOptions = this.users.filter(v => {
          let cognom1 = false;
          let cognom2 = false;
          let nom = false;
          let email = false;

          if(v.nom) {
            nom = v.nom.toLowerCase().indexOf(needle) > -1
          }

          if(v.cognom1) {
            cognom1 = v.cognom1.toLowerCase().indexOf(needle) > -1
          }

          if(v.cognom2) {
            cognom2 = v.cognom2.toLowerCase().indexOf(needle) > -1
          }

          if(v.email) {
            email = v.email.toLowerCase().indexOf(needle) > -1
          }
          return  nom || cognom1 || cognom2 || email;
        }).map(user=>{
          return {
            label: user.nomComplet + ' ('+user.email+')',
            value: user.email
          }
        })
      })
    },
    grupFilterFn (val:string, update:Function) {
      if (val === '') {
        update(() => {
          this.grupOptions = this.grups.map(grup=>{
            return {
              label: grup.nom + ' ('+grup.email+')',
              value: grup.email
            }
          })
        })
        return
      }

      update(() => {
        const needle = val.toLowerCase()
        this.grupOptions = this.grups.filter(v => {
          let nom = false;
          let email = false;

          if(v.nom) {
            nom = v.nom.toLowerCase().indexOf(needle) > -1
          }

          if(v.email) {
            email = v.email.toLowerCase().indexOf(needle) > -1
          }
          return nom || email;
        }).map(grup=>{
          return {
            label: grup.nom + ' ('+grup.email+')',
            value: grup.email
          }
        })
      })
    },
    itemFilterFn (val:string, update:Function) {
      if (val === '') {
        update(() => {
          this.itemOptions = this.items.map(item=>{
            return {
              label: item.nom,
              value: item.id.toString()
            }
          })
        })
        return
      }

      update(() => {
        const needle = val.toLowerCase()
        this.itemOptions = this.items.filter(v => {
          let nom = false;

          if(v.nom) {
            nom = v.nom.toLowerCase().indexOf(needle) > -1
          }
          return nom;
        }).map(item=>{
          return {
            label: item.nom,
            value: item.id.toString()
          }
        })
      })
    },
    async updateMembersItems(){
      /*const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })*/
      //Actualitzam items de membres
      this.grupCooperatiu.members.forEach( (m:Membre) =>{
        //inicialitzem o actualitzem
        if(!m.valorsItemMembre) {
          m.valorsItemMembre = [];
        } else {
          //Esborrem ítems que no estiguin seleccionats, és a dir, filtrem nomes els ítems que són dins l'array delm grup cooperatiu
          //m.valorsItemMembre = m.valorsItemMembre.filter((it)=>this.grupCooperatiu.items.find((it2)=>it.valorItem.item?.id===it2.item.id));
        }

        //Actualitzem valors del llistat de cada ítem, és a dir, el select amb les opcions disponibles
        this.grupCooperatiu.items.forEach(async (itemGrupCooperatiu:ItemGrupCooperatiu,index)=>{
          const valorsItem = await GrupCooperatiuService.getValorItems(itemGrupCooperatiu.item);

          itemGrupCooperatiu.item.valorsItem = valorsItem;
          //Clonem l'array d'objectes amb spread {...a}
          const clonedValorsItem = valorsItem.map(a => {return {...a}})
          itemGrupCooperatiu.item.valorsMapped = clonedValorsItem.map((v:ValorItem)=>{
            return {
              label:v.valor + ' (' + v.pes+'%)',
              value:v.id+''
            }
          })

          //Si el valor de la llista desplegable dels ítems està buit, posem el primer valor per defecte
          //Així s'estalvien anar un per un a posar tots els valors, el primer ja vindrà seleccionat.
          //Aquí canviem el valor del model pel primer ítem de la llista
          m.valorsItemMapped[index] = itemGrupCooperatiu.item.valorsMapped[0];
          //console.log(m);
        })
      })

      //dialog.hide();
    },
    mesclaAleatoria: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      //Clonem l'objecte
      const grupCooperatiu = {...this.grupCooperatiu};
      grupCooperatiu.items.map(item=>delete item.item.valorsItem)

      //Fem lo de JSON per convertir l'objecte Proxy a array pla
      const grups = await this.$axios.post(process.env.API + '/apps/grupscooperatius/aleatori',grupCooperatiu);
      this.result = grups.data
      dialog.hide();
    },
    mesclaGenetica: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      //Clonem l'objecte
      const grupCooperatiu = {...this.grupCooperatiu};
      grupCooperatiu.items.map(item=>delete item.item.valorsItem)
      const grups = await this.$axios.post(process.env.API + '/apps/grupscooperatius/genetica',grupCooperatiu);
      this.result = grups.data

      this.paintGraph()
      dialog.hide();
    },
    countValorItemMembers(valorItem:{label:string;value:string},membres:Membre[]) {

      let count:number = 0;
      membres.forEach((m:Membre)=>{
        //console.log(m.nom,m.valorsItemMembre,valorItem)
        m.valorsItemMembre.forEach((vim:any)=>{
          if((vim.valorItem.idvalorItem)===parseInt(valorItem.value)){
            count++;
          }
        })
      })
      return count;
    },
    paintGraph(){
      /*if(this.chart !== null){
        this.chart.destroy();
      }*/
      console.log("Repintar gràfic",this.itemChart)
      const canvas = document.getElementById('myChart') as HTMLCanvasElement;
      const ctx: CanvasRenderingContext2D = canvas.getContext('2d') as CanvasRenderingContext2D;
      if(ctx != null) {
        const result = this.result;
        const labels = result.map((r,idx)=>'Grup '+idx);
        const datasets = [];
        result.forEach(grup=>{
          const dataset = {};
          grup.membres.forEach(membre=>{

          })
        })
        const chart = new Chart(ctx, {
          type: 'bar',
          data: {
            labels: labels,
            datasets: [{
              label: '# of Votes',
              data: [12, 19, 3, 5, 2, 3],
              backgroundColor: [
                'rgba(255, 99, 132, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(153, 102, 255, 0.2)',
                'rgba(255, 159, 64, 0.2)'
              ],
              borderColor: [
                'rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)'
              ],
              borderWidth: 1
            }]
          }
        });
      }
    }
  }
})
</script>

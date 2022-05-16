<template>
  <q-page class="flex column" padding>
    <p class="text-h3">{{grup.gsuiteNom}}</p>
    <p class="text-h6">{{grup.gsuiteEmail}}</p>

    <q-input v-model="grup.gsuiteNom" label="Nom" />
    <q-input v-model="grup.gsuiteEmail" label="Email" class="q-mb-lg" />

    <q-select
      filled
      :model-value="selected"
      use-input
      hide-selected
      fill-input
      input-debounce="0"
      :options="options"
      @filter="filterFn"
      @input-value="setModel"
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

    <q-select v-model="grup.grupCorreuTipus" :options="optionsGrupCorreuTipus" label="Tipus de grup" />

    <q-select v-if="grup.grupCorreuTipus=='ALUMNAT' || grup.grupCorreuTipus=='PROFESSORAT'" v-model="grup.gestibGrup" :options="grupClasseOptions" label="Grup classe associat" />

    <q-btn v-if="grup.grupCorreuTipus=='ALUMNAT' || grup.grupCorreuTipus=='PROFESSORAT' || grup.grupCorreuTipus=='TUTORS_FCT' || grup.grupCorreuTipus=='DEPARTAMENT'" label="Autoemplenar grup" color="primary" @click="confirmAutoemplenar = true" />

    <q-list bordered class="rounded-borders">

      <q-separator spaced v-if="grupMembers.length > 0" />
      <q-item-label header v-if="grupMembers.length > 0">Grups de correu</q-item-label>

      <q-item v-for="grup in grupMembers" clickable v-ripple>
        <q-item-section top class="col-10 gt-sm">
          <q-item-label class="q-mt-sm">{{grup.gsuiteNom}}</q-item-label>
          <q-item-label class="q-mt-sm">{{grup.gsuiteEmail}}</q-item-label>
        </q-item-section>

        <q-item-section top side>
          <div class="text-grey-8 q-gutter-xs">
            <q-btn size="12px" flat dense round icon="delete" @click="deleteGrupMember(grup)" />
          </div>
        </q-item-section>
      </q-item>

      <q-separator spaced v-if="members.length > 0" />
      <q-item-label header v-if="members.length > 0">Usuaris</q-item-label>

      <q-item v-for="user in members" clickable v-ripple>
        <q-item-section top class="col-10 gt-sm">
          <q-item-label class="q-mt-sm">{{user.gsuiteFamilyName}}, {{user.gsuiteGivenName}}</q-item-label>
          <q-item-label class="q-mt-sm">{{user.gsuiteEmail}}</q-item-label>
        </q-item-section>

        <q-item-section top side>
          <div class="text-grey-8 q-gutter-xs">
            <q-btn size="12px" flat dense round icon="delete" @click="deleteMember(user)" />
          </div>
        </q-item-section>
      </q-item>
    </q-list>

    <q-btn color="primary" icon="save" label="Desar" @click="save" />

    <q-dialog v-model="confirmAutoemplenar" persistent>
      <q-card>
        <q-card-section class="row items-center">
          <q-avatar icon="signal_wifi_off" color="primary" text-color="white" />
          <span class="q-ml-sm">Està segur que vol autoemplenar el grup d'usuaris?</span>
          <span class="q-ml-sm">Aquesta acció treurà els actuals membres i afegirà els nous a partir de l'arxiu de Gestib.</span>
          <span class="q-ml-sm">Aquesta acció no es pot desfer.</span>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel·la" color="primary" v-close-popup />
          <q-btn flat label="Autoemplena" color="primary" @click="autoemplenaUsuaris" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'PageGrupCorreuForm',
  data() {
    return {
      grup: {},
      members: [],
      users: [],
      grupMembers: [],
      grups: [],
      grupsClasse:[],
      selected:[],
      grupSelected: [],
      options: [],
      grupOptions: [],
      optionsGrupCorreuTipus: [],
      grupClasseOptions: [],
      filterUsuaris: '',
      filterGrups: '',
      selectedUsuaris: [],
      selectedGrups: [],
      columnesGrup: [],
      columnesUsuari: [],
      confirmAutoemplenar: false
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

      this.columnesGrup = [
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

      this.columnesUsuari = [
        {
          name: 'nom',
          required: true,
          label: 'Nom',
          align: 'left',
          field: row => row.gestibNom,
          sortable: true
        },
        {
          name: 'cognom1',
          required: true,
          label: 'Cognom 1',
          align: 'left',
          field: row => row.gestibCognom1,
          sortable: true
        },
        {
          name: 'cognom2',
          required: true,
          label: 'Cognom 2',
          align: 'left',
          field: row => row.gestibCognom2,
          sortable: true
        },
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
          field: row => row.gsuiteFullName,
          format: val => `${val}`,
          sortable: true
        }
      ]

      const id = this.$route.params.id;

      if(id) {
        const response = await this.$axios.get(process.env.API + '/grupcorreu/grupambusuaris/' + id);
        const data = await response.data;
        this.grup = data;

        this.members = data.usuaris.sort((a,b)=>{
          if(a.gsuiteFamilyName != b.gsuiteFamilyName){
            return a.gsuiteFamilyName.localeCompare(b.gsuiteFamilyName)
          }
          return a.gsuiteGivenName.localeCompare(b.gsuiteGivenName)
        });
        this.grupMembers = data.grupCorreus.sort((a,b)=>{
          return a.gsuiteEmail.localeCompare(b.gsuiteEmail)
        });
      }

      const responseUsers = await this.$axios.get(process.env.API + '/usuaris/llistat/actius');
      const dataUsers = await responseUsers.data;
      this.users = dataUsers;

      const responseGroups = await this.$axios.get(process.env.API + '/grupcorreu/llistat');
      const dataGroups = await responseGroups.data;
      this.grups = dataGroups;

      const responseGrupsClasse = await this.$axios.get(process.env.API + '/grup/llistat');
      const dataGrupsClasse = await responseGrupsClasse.data;
      /* TODO: PROMISE.ALL */
      const gClasse = dataGrupsClasse.map(async g=>{
        const responseCurs = await this.$axios.get(process.env.API + '/curs/getByCodiGestib/'+g.gestibCurs)
        const dataCurs = await responseCurs.data;
        g.curs = dataCurs;
        return g;
      });
      this.grupsClasse = await Promise.all(gClasse);


      this.options = this.users.map(user=>{
        return {
          label: user.gsuiteFamilyName + ', ' + user.gsuiteGivenName+ ' ('+user.gsuiteEmail+')',
          value: user.gsuiteEmail
        }
      })

      this.grupOptions = this.grups.map(grup=>{
        return {
          label: grup.gsuiteNom + ' ('+grup.gsuiteEmail+')',
          value: grup.gsuiteEmail
        }
      })
      this.optionsGrupCorreuTipus = ['GENERAL','ALUMNAT','PROFESSORAT','TUTORS_FCT','DEPARTAMENT']

      this.grupClasseOptions = this.grupsClasse.map(g=>{
        return {
          label: g.curs.gestibNom + g.gestibNom,
          value: g.gestibIdentificador
        }
      })

      if(id){
        this.grup.grupCorreuTipus = this.optionsGrupCorreuTipus.find(tipus=>tipus==this.grup.grupCorreuTipus);
        this.grup.gestibGrup = this.grupClasseOptions.find(gco=>gco.value==this.grup.gestibGrup);
      }

      dialog.hide();
    },
    deleteMember: function(member){
      this.members = this.members.filter(m=>{
        return m != member
      })
    },
    deleteGrupMember: function(member){
      this.grupMembers = this.grupMembers.filter(m=>{
        return m != member
      })
    },
    setModel (val) {
      let usuari = this.users.find(user=> {
        return user.gsuiteFamilyName + ', ' + user.gsuiteGivenName + ' ('+user.gsuiteEmail+')' === val
      })
      if(usuari){
        this.members.push(usuari)
        this.selected = [];
      }
    },
    filterFn (val, update) {
      if (val === '') {
        update(() => {
          this.options = this.users.map(user=>{
            return {
              label: user.gsuiteFamilyName + ', ' + user.gsuiteGivenName + ' ('+user.gsuiteEmail+')',
              value: user.gsuiteEmail
            }
          })
        })
        return
      }

      update(() => {
        const needle = val.toLowerCase()
        this.options = this.users.filter(v => {
          let cognoms = false;
          let nom = false;
          let email = false;

          if(v.gsuiteFamilyName) {
            cognoms = v.gsuiteFamilyName.toLowerCase().indexOf(needle) > -1
          }

          if(v.gsuiteGivenName) {
            nom = v.gsuiteGivenName.toLowerCase().indexOf(needle) > -1
          }

          if(v.gsuiteEmail) {
            email = v.gsuiteEmail.toLowerCase().indexOf(needle) > -1
          }
          return cognoms || nom || email;
        }).map(user=>{
          return {
            label: user.gsuiteFamilyName + ', ' + user.gsuiteGivenName + ' ('+user.gsuiteEmail+')',
            value: user.gsuiteEmail
          }
        })
      })
    },
    setGrupModel (val) {
      let grup = this.grups.find(grup=> {
        return grup.gsuiteNom + ' ('+grup.gsuiteEmail+')' === val
      })
      if(grup){
        this.grupMembers.push(grup)
        this.grupSelected = [];
      }
    },
    grupFilterFn (val, update) {
      if (val === '') {
        update(() => {
          this.grupOptions = this.grups.map(grup=>{
            return {
              label: grup.gsuiteNom + ' ('+grup.gsuiteEmail+')',
              value: grup.gsuiteEmail
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

          if(v.gsuiteNom) {
            nom = v.gsuiteNom.toLowerCase().indexOf(needle) > -1
          }

          if(v.gsuiteEmail) {
            email = v.gsuiteEmail.toLowerCase().indexOf(needle) > -1
          }
          return nom || email;
        }).map(grup=>{
          return {
            label: grup.gsuiteNom + ' ('+grup.gsuiteEmail+')',
            value: grup.gsuiteEmail
          }
        })
      })
    },
    save: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      this.grup.usuaris = this.members;
      this.grup.grupCorreus = this.grupMembers;
      if(this.grup.gestibGrup && this.grup.gestibGrup.value) {
        this.grup.gestibGrup = this.grup.gestibGrup.value;
      }
      await this.$axios.post(process.env.API + '/grupcorreu/desar',this.grup);
      dialog.hide();
      //Redirect
      this.$router.push('/grupcorreu/list');
    },
    autoemplenaUsuaris: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      this.grup.usuaris = this.members;
      this.grup.grupCorreus = this.grupMembers;
      if(this.grup.gestibGrup && this.grup.gestibGrup.value) {
        this.grup.gestibGrup = this.grup.gestibGrup.value;
      }
      await this.$axios.post(process.env.API + '/grupcorreu/desar',this.grup);

      const response = await this.$axios.get(process.env.API + '/grupcorreu/grupambusuaris/' + this.grup.gsuiteEmail);
      const data = await response.data;
      this.grup = data;

      await this.$axios.post(process.env.API + '/grupcorreu/autoemplenar',this.grup);
      dialog.hide();
      //Redirect
      //this.$router.push('/grupcorreu/'+this.grup.gsuiteEmail);
      this.reset();
    },
    reset() {
      Object.assign(this.$data, this.$options.data.call(this));
      this.$options.created.call(this);
    }
  }
})
</script>

<template>
  <q-page class="flex column" padding>
    <h3>{{usuari.gsuiteFullName}}</h3>

    <q-card flat bordered class="q-mb-md">
      <q-card-section>
        <div class="text-h6">Informació General</div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <p><span class="text-weight-bold">Rol:</span> {{usuari.rol}}</p>
      </q-card-section>
    </q-card>

    <q-card flat bordered class="q-mb-md">
      <q-card-section>
        <div class="text-h6">Informació de Gestib</div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <p><span class="text-weight-bold">Nom:</span> {{usuari.gestibNom}}</p>
        <p><span class="text-weight-bold">Primer cognom:</span> {{usuari.gestibCognom1}}</p>
        <p><span class="text-weight-bold">Segon cognom:</span> {{usuari.gestibCognom2}}</p>
      </q-card-section>
    </q-card>

    <q-card flat bordered class="q-mb-md">
      <q-card-section>
        <div class="text-h6">Informació de GSuite</div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <p><span class="text-weight-bold">Nom Complet:</span> {{usuari.gsuiteFullName}}</p>
        <p><span class="text-weight-bold">Correu electrònic:</span> {{usuari.gsuiteEmail}}</p>
      </q-card-section>
    </q-card>

    <q-card flat bordered class="q-mb-md">
      <q-card-section>
        <div class="text-h6">Grups de correu de l'usuari</div>
      </q-card-section>

      <q-card-section class="q-pt-none">

        <q-btn class="q-md" label="Assignar nou grup" color="primary" @click="confirmNouGrup = true" />

        <q-list class="rounded-borders">
          <q-separator spaced />

          <q-item clickable v-ripple v-for="grupCorreu in grupsCorreuUsuari">

            <q-item-section top class="col-2">
              <q-item-label class="q-mt-sm">{{grupCorreu.gsuiteNom}}</q-item-label>
            </q-item-section>

            <q-item-section top>
              <q-item-label class="q-mt-sm">
                <a :href="'mailto:'+grupCorreu.gsuiteEmail" target="_blank" class="text-accent">{{grupCorreu.gsuiteEmail}}</a>
              </q-item-label>
            </q-item-section>

            <q-item-section top side>
              <div class="text-grey-8 q-gutter-xs">
                <q-btn size="12px" flat dense round icon="delete" @click="confirmEsborrarGrup = true; grupEsborrarSelected=grupCorreu.gsuiteEmail" />
                <q-btn size="12px" flat dense round icon="visibility" :to="'/grupcorreu/'+grupCorreu.gsuiteEmail"/>
              </div>
            </q-item-section>
          </q-item>
        </q-list>
      </q-card-section>
    </q-card>

    <q-card flat bordered>
      <q-card-section>
        <div class="text-h6">Accions</div>
      </q-card-section>

      <q-card-actions align="right" class="q-pt-none">
        <q-btn class="q-ma-md" label="Reseteja contrasenya" color="primary" @click="confirmResetPassword = true" />
        <q-btn class="q-ma-md" label="Reassignar grups" color="primary" @click="confirmReassignarGrups = true" />
      </q-card-actions>
    </q-card>

    <q-dialog v-model="confirmResetPassword" persistent>
      <q-card>
        <q-card-section class="row items-center">
          <q-avatar icon="security" color="primary" text-color="white" class="q-ma-lg" />
          <div class="flex column">
            <span class="q-ml-sm">Està segur que vol resetejar la contrasenya de l'usuari?</span>
            <span class="q-ml-sm">Aquesta acció no es pot desfer.</span>
            <p>Nova contrasenya: <span class="fa-bold">{{newPassword}}</span></p>
          </div>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel·la" color="primary" v-close-popup />
          <q-btn flat label="Reseteja contrasenya" color="primary" @click="resetPassword" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <q-dialog v-model="confirmReassignarGrups" persistent>
      <q-card>
        <q-card-section class="row items-center">
          <q-avatar icon="signal_wifi_off" color="primary" text-color="white"/>
          <span class="q-ml-sm">Està segur que vol reassignar l'usuari?</span>
          <span class="q-ml-sm">Aquesta acció no es pot desfer. Es reassignaran els grups d'alumnat i professorat automàtic. Els grups introduits manualment no es modificaran.</span>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel·la" color="primary" v-close-popup/>
          <q-btn flat label="Reagrupa" color="primary" @click="reagruparUsuari" v-close-popup/>
        </q-card-actions>
      </q-card>
    </q-dialog>

    <q-dialog v-model="confirmNouGrup" persistent>
      <q-card>
        <q-card-section class="row items-center">
          <q-avatar icon="group" color="primary" text-color="white"/>
          <span class="q-ml-sm">Assignar grups nous a l'usuari</span>
        </q-card-section>

        <q-card-section>
          <q-select
            v-model="grupSelected"
            :options="grupOptions"
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
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel·la" color="primary" v-close-popup/>
          <q-btn flat label="Assignar nou/s grup/s" color="primary" @click="nouGrupUsuari" v-close-popup/>
        </q-card-actions>
      </q-card>
    </q-dialog>

    <q-dialog v-model="confirmEsborrarGrup" persistent>
      <q-card>
        <q-card-section class="row items-center">
          <q-avatar icon="signal_wifi_off" color="primary" text-color="white"/>
          <span class="q-ml-sm">Està segur que vol esborrar l'usuari del grup <strong>{{grupEsborrarSelected}}</strong>?</span>
          <span class="q-ml-sm">Aquesta acció no es pot desfer. Es reassignaran els grups d'alumnat i professorat automàtic. Els grups introduits manualment no es modificaran.</span>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel·la" color="primary" v-close-popup/>
          <q-btn flat label="Esborrar" color="primary" @click="esborrarUsuariGrup" v-close-popup/>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'Profile',
  data(){
    return {
      usuari: {},
      grupsCorreuUsuari: [],
      grupsCorreu: [],
      grupOptions: [],
      grupSelected: null,
      grupEsborrarSelected: null,
      newPassword: 'iesmanacor2021',
      confirmResetPassword: false,
      confirmReassignarGrups: false,
      confirmNouGrup: false,
      confirmEsborrarGrup: false
    }
  },
  created(){
    this.get();
  },
  methods: {
    get: async function(){
      const idusuari = this.$route.params.id;

      if(idusuari) {
        const responseProfile = await this.$axios.get(process.env.API + '/usuaris/profile/' + idusuari);
        const dataProfile = await responseProfile.data;
        this.usuari = dataProfile;

        const responseGrupsCorreuUsuari = await this.$axios.get(process.env.API + '/grupcorreu/grupsusuari/' + idusuari);
        const dataGrupsCorreuUsuari = await responseGrupsCorreuUsuari.data;
        this.grupsCorreuUsuari = dataGrupsCorreuUsuari;
      }

      const responseGrupsCorreu = await this.$axios.get(process.env.API + '/grupcorreu/llistat')
      const dataGrupsCorreu = await responseGrupsCorreu.data;
      this.grupsCorreu = dataGrupsCorreu;

      this.grupOptions = this.grupsCorreu.map(grup=>{
        return {
          label: grup.gsuiteNom + ' ('+grup.gsuiteEmail+')',
          value: grup.gsuiteEmail
        }
      })
    },
    resetPassword: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      const responseResetPassword = await this.$axios.post(process.env.API + '/usuari/reset',{
        usuari: this.usuari.gsuiteEmail,
        password: this.newPassword
      });

      dialog.hide();

      //Refresh data
      this.reset();
    },
    reagruparUsuari: async function () {
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      const usuaris = [this.usuari];
      await this.$axios.post(process.env.API + '/sync/reassignarGrups', usuaris);

      dialog.hide();

      //Refresh data
      this.reset();
    },
    nouGrupUsuari: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })


      await this.$axios.post(process.env.API + '/grupcorreu/addmember', {
        group: this.grupSelected.value,
        user: this.usuari.idusuari
      });

      dialog.hide();

      //Refresh data
      this.reset();
    },
    esborrarUsuariGrup: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })


      await this.$axios.post(process.env.API + '/grupcorreu/removemember', {
        group: this.grupEsborrarSelected,
        user: this.usuari.idusuari
      });

      this.grupEsborrarSelected = null;

      dialog.hide();

      //Refresh data
      this.reset();
    },
    setGrupModel (val) {
      let grup = this.grupsCorreu.find(grup=> {
        return grup.gsuiteNom + ' ('+grup.gsuiteEmail+')' === val
      })
      if(grup){
        //this.grupMembers.push(grup)
        //this.grupSelected = [];
      }
    },
    grupFilterFn (val, update) {
      if (val === '') {
        update(() => {
          this.grupOptions = this.grupsCorreu.map(grup=>{
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
        this.grupOptions = this.grupsCorreu.filter(v => {
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
    reset() {
      Object.assign(this.$data, this.$options.data.call(this));
      this.$options.created.call(this);
    }
  }
})
</script>

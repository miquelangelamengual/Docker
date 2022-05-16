<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          Usuaris IES Manacor
        </q-toolbar-title>

        <!--div>Ajuda</div-->
        <q-btn flat label="Torna" color="white" icon="arrow_circle_left" @click="goBack" />

      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      :mini="$q.screen.lt.lg"
      bordered
    >
      <q-list>
        <q-item clickable to="/dashboard">
          <q-item-section avatar>
            <q-icon name="home_work" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Principal</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable to="/usuari/list" v-if="rolsUser.find(rol=>rol===rols.ADMINISTRADOR || rol===rols.DIRECTOR || rol===rols.CAP_ESTUDIS)">
          <q-item-section avatar>
            <q-icon name="groups" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Usuaris</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable to="/grupcorreu/list" v-if="rolsUser.find(rol=>rol===rols.ADMINISTRADOR || rol===rols.DIRECTOR || rol===rols.CAP_ESTUDIS)">
          <q-item-section avatar>
            <q-icon name="contact_mail" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Grups de correu</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable to="/calendari/list" v-if="rolsUser.find(rol=>rol==rols.ADMINISTRADOR || rol==rols.DIRECTOR)">
          <q-item-section avatar>
            <q-icon name="calendar_today" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Calendaris</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable to="/llistats" v-if="rolsUser.find(rol=>rol==rols.ADMINISTRADOR || rol==rols.DIRECTOR || rol==rols.CAP_ESTUDIS)">
          <q-item-section avatar>
            <q-icon name="summarize" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Llistats</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable to="/upload" v-if="rolsUser.find(rol=>rol==rols.ADMINISTRADOR || rol==rols.DIRECTOR)">
          <q-item-section avatar>
            <q-icon name="file_upload" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Pujar fitxer Gestib</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable to="/login">
          <q-item-section avatar>
            <q-icon name="login" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Login</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>

import { defineComponent, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {Rol} from '../model/Rol.ts'


export default defineComponent({
  name: 'MainLayout',

  setup () {
    const leftDrawerOpen = ref(false)
    const rolsUser = JSON.parse(localStorage.getItem("rol")) || []; //Inicialitzem a un array buit si no existeix cap rol
    const router = useRouter()
    const route = useRoute()
    const rols = Rol;

    return {
      rolsUser,
      rols,
      leftDrawerOpen,
      toggleLeftDrawer () {
        leftDrawerOpen.value = !leftDrawerOpen.value
      },
      goBack(){
        router.go(-1);
      }
    }
  }
})
</script>

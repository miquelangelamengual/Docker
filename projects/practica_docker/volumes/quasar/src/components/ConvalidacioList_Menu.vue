<template>
  <q-btn-group push class="q-mb-lg">
    <q-btn  color="primary" label="Sol·licituds" icon="settings" :to="'/apps/convalidacions/solicituds'"/>
    <q-btn  color="primary" label="Convalidacions" icon="settings" :to="'/apps/convalidacions/convalidacions'"/>
    <q-btn  color="primary" label="Titulacions" icon="settings" :to="'/apps/convalidacions/titulacions'"/>
    <q-btn  color="primary" label="Categories" icon="settings" :to="'/apps/convalidacions/categories'"/>
  </q-btn-group>
  <p>hola</p>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'ConvalidacioList_Menu',
});
</script>

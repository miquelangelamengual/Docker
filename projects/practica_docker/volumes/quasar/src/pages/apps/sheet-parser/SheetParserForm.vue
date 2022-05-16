<template>
  <q-page class="flex column" padding>

    <q-input v-model="sheet.id" label="Id del Sheet de Drive" />
    <q-input v-model="sheet.titol" label="Titol" class="q-mb-lg" />
    <q-input v-model="sheet.numHeaders" type="number" label="Número de files de la capçalera" class="q-mb-lg"/>
    <q-input v-model="sheet.numRowsAlumnes" type="number" label="Número de files de l'alumne" class="q-mb-lg" />
    <q-input v-model="sheet.numColumnEmail" type="number" label="Número columne on hi ha l'email" class="q-mb-lg" />

    <q-btn label="Previsualització correu" color="primary" class="q-mb-lg" @click="draftEmail" />
    <q-btn label="Enviar correu als alumnes" color="primary" class="q-mb-lg" @click="sendEmail" />

    <div v-for="(email,indexemail) in emails" class="q-mb-lg q-mt-lg">
      <p>Correu alumne: <strong>{{emailsAlumne[indexemail]}}</strong></p>
      <table>
        <tr v-for="(row,index) in email" :class="{ 'header': index<sheet.numHeaders }">
          <td v-for="value in row">{{value}}</td>
          <td v-if="row && (numColumnesTotal - row.length)>0" v-for="n in (numColumnesTotal - row.length)"></td>
        </tr>
      </table>
    </div>
  </q-page>
</template>

<style scoped>
table{
  border-collapse: collapse;
  border-spacing: 0;
  margin: 10px 0;
}

td,th{
  border: solid 1px black;
  padding: 5px;
}

.header{
  background: #DDDDDD;
  text-align: center;
  font-weight: bold;
}
</style>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'PageSheetParserForm',
  data() {
    return {
      sheet: {
        id:'1C-7utJJmPTZRxVYh0XuJZ88EHkDd6Tu2Le5gi4Hkzw4',
        titol: 'Nota',
        numHeaders: 2,
        numRowsAlumnes: 2,
        numColumnEmail: 3
      },
      emails: [],
      emailsAlumne: [],
      numAlumnes: 0,
      numColumnesTotal: 0
    }
  },
  created() {
    this.get();
  },
  methods: {
    get: async function () {
      /*const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      dialog.hide();*/
    },
    draftEmail: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      this.emails = [];
      this.emailsAlumne = [];
      const liniesFetch = await this.$axios.post(process.env.API + '/apps/sheetparser/draft',this.sheet.id)
      const linies = liniesFetch.data;

      this.numAlumnes = (linies.length - this.sheet.numHeaders) / this.sheet.numRowsAlumnes;
      this.numColumnesTotal = linies[0].length;

      //Header
      const header = [];
      for(let i=0; i<this.sheet.numHeaders; i++){
        header.push(linies[i]);
      }

      console.log("header:",header);

      //Email
      for(let i=0; i<this.numAlumnes; i++){
        const email = [];
        //Header
        for(let j=0; j<header.length; j++){
          email.push(header[j])
        }
        //Alumne
        for(let j=0; j<this.sheet.numRowsAlumnes; j++){
          email.push(linies[this.sheet.numHeaders+(i*this.sheet.numRowsAlumnes)+j])

          //Email alumne
          if(j==0){
            const liniaCorreu = linies[this.sheet.numHeaders+(i*this.sheet.numRowsAlumnes)+j];
            const correuAlumne = liniaCorreu[this.sheet.numColumnEmail-1];
            this.emailsAlumne.push(correuAlumne);
          }
        }

        this.emails.push(email);
      }

      dialog.hide();
    },
    sendEmail: async function(){
      const dialog = this.$q.dialog({
        message: 'Carregant...',
        progress: true, // we enable default settings
        persistent: true, // we want the user to not be able to close it
        ok: false // we want the user to not be able to close it
      })

      await this.$axios.post(process.env.API + '/apps/sheetparser/send',{
        titol: this.sheet.titol,
        idsheet: this.sheet.id,
        numHeaders: this.sheet.numHeaders,
        numRowsAlumnes: this.sheet.numRowsAlumnes,
        numColumnEmail: this.sheet.numColumnEmail
      });

      dialog.hide();
      //Redirect
      this.$router.push('/apps/sheetparser');
    }
  }
})
</script>

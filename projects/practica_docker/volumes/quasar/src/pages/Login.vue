<template>
  <q-page class="flex flex-center column background" padding>
    <!--img src="logo/hiclipart.com.png" class="logologin" alt="logo"-->
    <q-card class="bg-negative text-white q-mb-md" v-if="displayError">
      <q-card-section>
        <div class="text-h6">Error d'autenticació</div>
      </q-card-section>
      <q-card-section>El seu nom d'usuari o contrasenya són invàlids.</q-card-section>
    </q-card>

    <br><br>
    <div class="row q-mt-md q-mb-md">
    </div>
    <q-card class="text-black q-mb-md background-card">
      <q-card-section class="flex flex-center column">
        <q-img
          src="../../public/logoiesmanacor.png"
          :width="'100%'"
          alt="Logo IES Manacor"
        />

        <h3 class="text-center text-black-50">Usuaris IES Manacor</h3>
        <q-btn label="Entra amb Google" @click="signGoogle" size="xl" icon="lock" class="q-mb-md" color="primary"/>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<style scoped>
.logologin{
  height:150px;
}
.background{
  background-image: url("../../public/centre02.jpg");
  background-size: cover;
}

.background-card{
  background: rgba(255,255,255,0.9);
}
</style>

<script>

  export default {
    name: 'Login', data() {
      return {
        displayError: false
      };
    },
    methods: {
      async signGoogle() {
        try {
          const googleUser = await this.$gAuth.signIn()

          const token = googleUser.getAuthResponse().id_token;
          const response = await this.$axios.post(process.env.API+'/auth/google/login', token)

          if (response && response.data) {
            //Desem primer el token per poder enviar-lo a la petició de rol.
            const tokenData = await response.data;
            localStorage.setItem('token', tokenData);

            const responseRol = await this.$axios.get(process.env.API+'/auth/profile/rol',{
              method: 'GET',
              headers: {
                Authorization: tokenData
              }
            })
            const rolData = await responseRol.data
            localStorage.setItem('rol',JSON.stringify(rolData));
            this.displayError = false;

            const previous = (this.$route && this.$route.query)?this.$route.query.q:null;
            console.log(previous,this.$route.query.q)

            if(previous){
              window.location.href = previous;
            } else {
              //Redirect
              this.$router.push('/dashboard');
            }
          } else {
            this.displayError = true;
          }

          console.log(response);
        } catch (error) {
          // On fail do something
          console.error(error);
          return null;
        }
        /*
        try {
          const authCode = await this.$gAuth.getAuthCode()
          console.log("Auth code: " + authCode)
          const response = await this.$axios.post(process.env.API+'/auth/google/login', { code: authCode, redirect_uri: 'postmessage' })
        } catch (error) {
          // On fail do something
          console.log("Error",error)
        }
*/
      }
    }
  }
</script>

<template>
    <q-page class="flex flex-center column">

    </q-page>
</template>

<style></style>

<script>

    export default {
        name: 'Logout', data() {
            return {

            };
        },
        created(){
            localStorage.removeItem('token');
            localStorage.clear();

            //Redirect
            this.$router.push('/login');
        }
    }
</script>

import { boot } from 'quasar/wrappers'
import GAuth from "vue3-google-oauth2";


export default boot(({ app }) => {
  const scopes = [];
  scopes.push("profile")
  scopes.push("email")
  //const gAuthOptions = { clientId: '327064841381-mj06ksqhlvb32a8v4n3tdbn9u4rmuvkj.apps.googleusercontent.com', scope: scopes.join(" "), prompt: 'consent', fetch_basic_profile: false }
  const gAuthOptions = { clientId: process.env.GOOGLE_CLIENT_ID, scope: scopes.join(" "), prompt: 'consent', fetch_basic_profile: false }
  app.use(GAuth, gAuthOptions)

  app.config.globalProperties.$GAuth = GAuth
})

import { boot } from 'quasar/wrappers';
import axios, { AxiosInstance } from 'axios';
import {Notify} from 'quasar'

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $axios: AxiosInstance;
  }
}

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
const api = axios.create({ baseURL: 'https://api.example.com' });

export default boot(({ app,router }) => {
  // Add a request interceptor
  axios.interceptors.request.use(function (config) {
    if (!localStorage.getItem('token')) {
      localStorage.setItem('token', 'bean');
    }
    axios.defaults.withCredentials = true;
    axios.defaults.headers.common['Authorization'] = 'Bearer '+localStorage.getItem('token');

    return config;
  }, function (error) {
    // Do something with request error
    return Promise.reject(error);
  });


  // Add a response interceptor
  axios.interceptors.response.use(function (response) {
    if (response && response.data && response.data.notifyMessage) {
      if (response.data.notifyType && response.data.notifyType == 'SUCCESS') {
        Notify.create({
          message: response.data.notifyMessage,
          type: 'positive'
        })
      } else if (response.data.notifyType && response.data.notifyType == 'WARNING') {
        Notify.create({
          message: response.data.notifyMessage,
          type: 'warning'
        })
      } else if (response.data.notifyType && response.data.notifyType == 'ERROR') {
        Notify.create({
          message: response.data.notifyMessage,
          type: 'negative'
        })
      } else {
        Notify.create({
          message: response.data.notifyMessage,
          type: 'info'
        })
      }
    }

    return response;
  }, function (error) {
    let redirectToLogin = true
    if (error && error.response && (error.response.status >= 400 && error.response.status < 500)) {
      console.log('Unauthorized responses',error.response.data);
      //localStorage.removeItem('token');
      //localStorage.clear();
      if (error && error.response && error.response.data && error.response.data.notifyMessage) {
        if (error.response.data.notifyType && error.response.data.notifyType == 'SUCCESS') {
          Notify.create({
            message: error.response.data.notifyMessage,
            type: 'positive'
          })
        } else if (error.response.data.notifyType && error.response.data.notifyType == 'WARNING') {
          Notify.create({
            message: error.response.data.notifyMessage,
            type: 'warning'
          })
        } else if (error.response.data.notifyType && error.response.data.notifyType == 'ERROR') {
          Notify.create({
            message: error.response.data.notifyMessage,
            type: 'negative'
          })
        } else {
          Notify.create({
            message: error.response.data.notifyMessage,
            type: 'info'
          })
        }

        redirectToLogin = error.response.data.redirectToLogin;
      }

      let url = new URL(window.location.href);

      if(url.pathname != '/login' && redirectToLogin){
        router.push('/login?q='+encodeURIComponent(window.location.href));
      }
    } else {
      console.log(error,error.response, error.request)
    }
  });
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios;
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api = api;
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you can easily perform requests against your app's API

});

export { api,axios };

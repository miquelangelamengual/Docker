import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/Index.vue') },
      { path: 'inici', component: () => import('pages/Index.vue') },
      { path: 'dashboard', component: () => import('pages/Dashboard.vue') },
      { path: 'upload', component: () => import('pages/UploadFile.vue') },
      { path: 'grupcorreu/list', component: () => import('pages/GrupCorreuList.vue') },
      { path: 'grupcorreu', component: () => import('pages/GrupCorreuForm.vue') },
      { path: 'grupcorreu/:id', component: () => import('pages/GrupCorreuForm.vue') },
      { path: 'calendari/list', component: () => import('pages/CalendariList.vue') },
      { path: 'usuari/list', component: () => import('pages/UsuariList.vue') },
      { path: 'llistats', component: () => import('pages/llistats/Llistats.vue') },
      { path: 'llistats/alumnatgrup', component: () => import('pages/llistats/AlumnatGrup.vue') },
      { path: 'llistats/usuarisgrupcorreu', component: () => import('pages/llistats/UsuarisGrupCorreu.vue') },
      { path: 'llistats/usuarisdispositiu', component: () => import('pages/llistats/UsuarisDispositiu.vue') },
      { path: 'llistats/usuariscustom', component: () => import('pages/llistats/UsuarisCustom.vue') },
      { path: 'administrador', component: () => import('pages/Administrador.vue') },
      { path: 'profile/:id', component: () => import('pages/Profile.vue') },
      { path: 'apps', component: () => import('pages/apps/LlistatApps.vue') },
      { path: 'apps/grupscooperatius', component: () => import('pages/apps/grups-cooperatius/GrupCooperatiuList.vue') },
      { path: 'apps/grupscooperatius/mescla', component: () => import('pages/apps/grups-cooperatius/GrupCooperatiuForm.vue') },
      { path: 'apps/grupscooperatius/items', component: () => import('pages/apps/grups-cooperatius/ItemList.vue') },
      { path: 'apps/grupscooperatius/item', component: () => import('pages/apps/grups-cooperatius/ItemForm.vue') },
      { path: 'apps/grupscooperatius/item/:id', component: () => import('pages/apps/grups-cooperatius/ItemForm.vue') },
      { path: 'apps/sheetparser', component: () => import('pages/apps/sheet-parser/SheetParserForm.vue') },
      { path: 'apps/convalidacions/categories', component: () => import('pages/apps/convalidacions/CategoriaList.vue') },
      { path: 'apps/convalidacions/categoria', component: () => import('pages/apps/convalidacions/CategoriaForm.vue') },
      { path: 'apps/convalidacions/categoria/:id', component: () => import('pages/apps/convalidacions/CategoriaForm.vue') },
      { path: 'apps/convalidacions/titulacions', component: () => import('pages/apps/convalidacions/TitulacioList.vue') },
      { path: 'apps/convalidacions/titulacio', component: () => import('pages/apps/convalidacions/TitulacioForm.vue') },
      { path: 'apps/convalidacions/titulacio/:id', component: () => import('pages/apps/convalidacions/TitulacioForm.vue') },
      { path: 'apps/convalidacions/convalidacions', component: () => import('pages/apps/convalidacions/ConvalidacioList.vue') },
      { path: 'apps/convalidacions/convalidacio', component: () => import('pages/apps/convalidacions/ConvalidacioForm.vue') },
      { path: 'apps/convalidacions/convalidacio/:id', component: () => import('pages/apps/convalidacions/ConvalidacioForm.vue') },
      { path: 'apps/convalidacions/solicituds', component: () => import('pages/apps/convalidacions/SolicitudList.vue') },
      { path: 'apps/convalidacions/solicitud', component: () => import('pages/apps/convalidacions/SolicitudForm.vue') },
      { path: 'apps/convalidacions/solicitud/:id', component: () => import('pages/apps/convalidacions/SolicitudForm.vue') },
    ],
  },
  {
    path: '/',
    component: () => import('layouts/EmptyLayout.vue'),
    children: [
      { path: 'login', component: () => import('pages/Login.vue') },
      { path: 'login/:callback', component: () => import('pages/Login.vue') },
      { path: 'logout', component: () => import('pages/Logout.vue') },
    ]
  },
  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;

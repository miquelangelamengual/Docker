import {axios}  from 'boot/axios'
import {CategoriaConvalidacio} from "src/model/apps/convalidacions/CategoriaConvalidacio";
import {ItemConvalidacio} from "src/model/apps/convalidacions/ItemConvalidacio";
import {ConvalidacioConvalidacio} from "src/model/apps/convalidacions/ConvalidacioConvalidacio";
import {SolicitudConvalidacio} from "src/model/apps/convalidacions/SolicitudConvalidacio";

export class ConvalidacioService {

  //CATEGORIES
  static async getCategoriaById(id:number): Promise<CategoriaConvalidacio> {
    const response = await axios.get(process.env.API + '/apps/convalidacions/categoria/' + id);
    const data = await response.data;
    return {
      id: data.idcategoria,
      nom: data.nom
    }
  }

  static async getCategories(): Promise<Array<CategoriaConvalidacio>> {
    const response = await axios.get(process.env.API + '/apps/convalidacions/categories');
    const data = await response.data;
    return data.map((categoria:any):CategoriaConvalidacio=>{
        return {
          id: categoria.idcategoria,
          nom: categoria.nom
        }
    });
  }

  static async saveCategoria(categoria:CategoriaConvalidacio):Promise<void>{
    await axios.post(process.env.API + '/apps/convalidacions/categoria/desar',categoria);
  }

  static async esborrarCategoria(id:number):Promise<void>{
    console.log("ID",id);
    const categoria:CategoriaConvalidacio = await this.getCategoriaById(id);
    console.log("Categoria",categoria);
    await axios.post(process.env.API + '/apps/convalidacions/categoria/esborrar',categoria);
  }

  //TITULACIONS
  static async getTitulacions(): Promise<Array<ItemConvalidacio>> {
    const response = await axios.get(process.env.API + '/apps/convalidacions/titulacions');
    const data = await response.data;
    return data.map((titulacio: any): ItemConvalidacio => {
      return {
        id: titulacio.iditem,
        codi: titulacio.codi,
        nom: titulacio.nom,
        categoria: titulacio.categoria
      }
    });
  }

  static async getItems(): Promise<Array<ItemConvalidacio>> {
    const response = await axios.get(process.env.API + '/apps/convalidacions/items');
    const data = await response.data;
    const itemsMap = data.map((item: any): Promise<ItemConvalidacio> => {
      return ConvalidacioService.mapToItemConvalidacio(item)
    });
    return await Promise.all(itemsMap) as Array<ItemConvalidacio>;
  }

  static getItemPare(items:ItemConvalidacio[], itemFill:ItemConvalidacio): ItemConvalidacio|null {
    for(const itemPare of items){
      if(itemPare.composa) {
        for(const it of itemPare.composa){
          if (it.id === itemFill.id) {
            return itemPare;
          }
        }
      }
    }
    return null;
  }

  static async getTitulacioById(id:number): Promise<ItemConvalidacio> {
    const response = await axios.get(process.env.API + '/apps/convalidacions/titulacio/' + id);
    const data = await response.data;
    return {
      id: data.iditem,
      codi: data.codi,
      nom: data.nom,
      categoria: {
        id: data.categoria.idcategoria,
        nom: data.categoria.nom,
        label: data.categoria.nom,
        value: data.categoria.idcategoria
      } as CategoriaConvalidacio,
      composa: data.composa.map((i:any)=>{
        return ConvalidacioService.mapToItemConvalidacio(i)
      })
    }
  }

  static async saveTitulacio(titulacio:ItemConvalidacio):Promise<void>{
    await axios.post(process.env.API + '/apps/convalidacions/titulacio/desar',titulacio);
  }

  static async esborrarTitulacio(id:number):Promise<void>{
    console.log("ID",id);
    const titulacio:ItemConvalidacio = await this.getTitulacioById(id);
    await axios.post(process.env.API + '/apps/convalidacions/titulacio/esborrar',titulacio);
  }


  //CONVALIDACIONS
  static async getConvalidacions(): Promise<Array<ConvalidacioConvalidacio>> {
    const response = await axios.get(process.env.API + '/apps/convalidacions/convalidacio/llistat');
    const data = await response.data;
    const dataMap = data.map(async (convalidacio: any): Promise<ConvalidacioConvalidacio> => {
      const origensMap = convalidacio.origens.map((i:any):Promise<ItemConvalidacio>=>{
        return ConvalidacioService.mapToItemConvalidacio(i)
      })
      const origensResolt= await Promise.all(origensMap);

      const convalidaMap = convalidacio.convalida.map((i:any):Promise<ItemConvalidacio>=>{
        return ConvalidacioService.mapToItemConvalidacio(i)
      })
      const convalidaResolt= await Promise.all(convalidaMap);

      return {
        id: convalidacio.idconvalidacio,
        origens: origensResolt as ItemConvalidacio[],
        convalida: convalidaResolt as ItemConvalidacio[],
      }
    });
    return await Promise.all(dataMap) as Array<ConvalidacioConvalidacio>;
  }

  static async saveConvalidacio(convalidacio:ConvalidacioConvalidacio):Promise<void>{
    await axios.post(process.env.API + '/apps/convalidacions/convalidacio/desar',convalidacio);
  }

  static async getConvalidacioById(id:number): Promise<ConvalidacioConvalidacio> {
    const response = await axios.get(process.env.API + '/apps/convalidacions/convalidacio/' + id);
    const data = await response.data;

    const origensMap = data.origens.map((i:any):Promise<ItemConvalidacio>=>{
      return ConvalidacioService.mapToItemConvalidacio(i)
    })
    const origensResolt= await Promise.all(origensMap);
    console.log("Origens resolts",origensResolt);

    return {
      id: 0,
      origens: [],
      convalida: []
    }
    /*return {
      id: data.idconvalidacio,
      origens: origensResolt,
      convalida: data.convalida.map((i:any):Promise<ItemConvalidacio>=>{
        return ConvalidacioService.mapToItemConvalidacio(i)
      }),
    }*/
  }

  static async esborrarConvalidacio(id:number):Promise<void>{
    console.log("ID",id);
    const convalidacio:ConvalidacioConvalidacio = await this.getConvalidacioById(id);
    await axios.post(process.env.API + '/apps/convalidacions/convalidacio/esborrar',convalidacio);
  }


  //SOL·LICITUDS
  static async getSolicituds(): Promise<Array<SolicitudConvalidacio>> {
    const response = await axios.get(process.env.API + '/apps/convalidacions/solicitud/llistat');
    const data = await response.data;
    return data.map((solicitud: any): SolicitudConvalidacio => {
      return {
        estat: solicitud.estat,
        estudisOrigen: solicitud.estudisOrigen,
        estudisEnCurs: solicitud.estudisDesti,
        alumne: solicitud.alumne,
      }
    });
  }

  static async esborrarSolicitud(id:number):Promise<void>{
    console.log("ID",id);
    //const solicitud:SolicitudConvalidacio = await this.getConvalidacioById(id);
    //await axios.post(process.env.API + '/apps/convalidacions/convalidacio/esborrar',convalidacio);
  }
  //PRIVATE
  private static async mapToItemConvalidacio(item:any):Promise<ItemConvalidacio>{
    const itemConvalidacio:ItemConvalidacio = {
        id: item.iditem,
        codi: item.codi,
        nom: item.nom,
        categoria: item.categoria,
        composa: item.composa.map((i:any)=>{
          return {
            id: i.iditem,
            codi: i.codi,
            nom: i.nom,
            categoria: {
              id: i.categoria.idcategoria,
              nom: i.categoria.nom,
              label: i.categoria.nom,
              value: i.categoria.idcategoria
            } as CategoriaConvalidacio
          }
        })
      }

      const responseItems = await axios.get(process.env.API + '/apps/convalidacions/items');
      const items:ItemConvalidacio[] = await responseItems.data;
      const itemPare:ItemConvalidacio|null = ConvalidacioService.getItemPare(items,item);
      if(itemPare===null){
        itemConvalidacio.label = `${item.codi+' - ' || ''}${item.nom} (${item.categoria.nom})`
      } else {
        itemConvalidacio.label = `${item.codi+' - ' || ''}${item.nom} (${item.categoria.nom}). Títol: ${itemPare.nom} (${itemPare.categoria.nom})`
      }
      itemConvalidacio.value = item.iditem;
      return itemConvalidacio;
  }
}

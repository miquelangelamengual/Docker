import {axios} from "boot/axios";
import {Usuari} from "src/model/Usuari";
import {Grup} from "src/model/gestib/Grup";
import {GrupCorreu} from "src/model/google/GrupCorreu";

export class GrupCorreuService {
  static async findAll(): Promise<Array<GrupCorreu>> {
    const responseGroups = await axios.get(process.env.API + '/grupcorreu/llistat');
    const dataGroups = await responseGroups.data;
    return dataGroups.map((grupCorreu: any): GrupCorreu => {
      return {
        id: grupCorreu.idgrup,
        email: grupCorreu.gsuiteEmail,
        nom: grupCorreu.gsuiteNom,
        descripcio: grupCorreu.gsuiteDescripcio,
        tipus: grupCorreu.grupCorreuTipus,
        label: grupCorreu.gsuiteNom + '('+grupCorreu.gsuiteEmail+')',
        value: grupCorreu.idgrup
      }
    });
  }

  static async getGrupAmbUsuaris(grup:GrupCorreu): Promise<GrupCorreu> {
      const response = await axios.get(process.env.API + '/grupcorreu/grupambusuaris/'+grup.email);
      const grupCorreu = await response.data;
      return {
        id: grupCorreu.idgrup,
        email: grupCorreu.gsuiteEmail,
        nom: grupCorreu.gsuiteNom,
        descripcio: grupCorreu.gsuiteDescripcio,
        tipus: grupCorreu.grupCorreuTipus,
        usuaris: grupCorreu.usuaris.map((usuari:any):Usuari=>{
          return{
            id: usuari.idusuari,
            email: usuari.gsuiteEmail,
            nom: usuari.gestibNom,
            cognom1: usuari.gestibCognom1,
            cognom2: usuari.gestibCognom2,
            nomComplet: usuari.gsuiteFullName
          }
        })
      }
  }
}

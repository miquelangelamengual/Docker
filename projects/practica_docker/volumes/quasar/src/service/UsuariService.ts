import {axios} from "boot/axios";
import {Usuari} from "src/model/Usuari";

export class UsuariService {
  static async findUsuarisActius(): Promise<Array<Usuari>> {
    const responseUsers = await axios.get(process.env.API + '/usuaris/llistat/actius');
    const data = await responseUsers.data;
    return data.map((usuari:any):Usuari=>{
      return {
        id: usuari.idusuari,
        email: usuari.gsuiteEmail,
        nom: usuari.gestibNom,
        cognom1: usuari.gestibCognom1,
        cognom2: usuari.gestibCognom2,
        nomComplet: usuari.gsuiteFullName,
        esAlumne: usuari.gestibAlumne,
        esProfessor: usuari.gestibProfessor,
        label: usuari.gsuiteFullName,
        value: usuari.idusuari
      }
    }).sort((a:Usuari,b:Usuari)=>a.nomComplet.localeCompare(b.nomComplet));
  }
}

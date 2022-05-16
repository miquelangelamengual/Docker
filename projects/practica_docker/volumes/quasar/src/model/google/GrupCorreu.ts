import {Usuari} from "src/model/Usuari";

export interface GrupCorreu {
  id: number;
  email: string;
  nom: string;
  descripcio: string;
  tipus: string;
  usuaris?: Array<Usuari>;
  label?: string;
  value?: string;
}

import {CategoriaConvalidacio} from "src/model/apps/convalidacions/CategoriaConvalidacio";

export interface ItemConvalidacio {
  id?: number;
  codi: string;
  nom: string;
  categoria: CategoriaConvalidacio;
  composa?: Array<ItemConvalidacio>;
  label?: string;
  value?: string;
}

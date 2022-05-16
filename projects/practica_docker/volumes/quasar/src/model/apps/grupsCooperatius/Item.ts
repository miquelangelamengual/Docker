import {Usuari} from "src/model/Usuari";
import {ValorItem} from "src/model/apps/grupsCooperatius/ValorItem";
import {ItemGrupCooperatiu} from "src/model/apps/grupsCooperatius/ItemGrupCooperatiu";

export interface Item {
  id: number;
  nom: string;
  usuari?: Usuari;
  valorsItem?: Array<ValorItem>;
  valorsMapped?: Array<{label:string;value:string}>;
  itemsGrupCooperatiu?: Array<ItemGrupCooperatiu>;
}

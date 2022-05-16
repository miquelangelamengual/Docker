import {Agrupament} from "src/model/apps/grupsCooperatius/Agrupament";
import {ValorItemMembre} from "src/model/apps/grupsCooperatius/ValorItemMembre";
import {Item} from "src/model/apps/grupsCooperatius/Item";

export interface Membre {
  nom: string;
  agrupament?: Agrupament;
  valorsItemMembre: Array<ValorItemMembre>;
  valorsItemMapped: Array<{label:string;value:string;}>;
}

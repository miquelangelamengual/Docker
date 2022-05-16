import {ValorItemMembre} from "src/model/apps/grupsCooperatius/ValorItemMembre";
import {Item} from "src/model/apps/grupsCooperatius/Item";

export interface ValorItem{
  id?: number;
  valor: string;
  pes: number;
  valorsItemMembre?: Array<ValorItemMembre>;
  item: Item;
}

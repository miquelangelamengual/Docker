import {ValorItem} from "src/model/apps/grupsCooperatius/ValorItem";
import {Membre} from "src/model/apps/grupsCooperatius/Membre";

export interface ValorItemMembre {
  id?: number;
  membre: Membre;
  valorItem: ValorItem;
}

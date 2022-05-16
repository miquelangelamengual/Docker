import {GrupCooperatiu} from "src/model/apps/grupsCooperatius/GrupCooperatiu";
import {Item} from "src/model/apps/grupsCooperatius/Item";

export interface ItemGrupCooperatiu {
  percentatge: number;
  grupCooperatiu?: GrupCooperatiu;
  item: Item;
}

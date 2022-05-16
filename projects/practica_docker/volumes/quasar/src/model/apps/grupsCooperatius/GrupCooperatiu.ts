import {Usuari} from "src/model/Usuari";
import {ItemGrupCooperatiu} from "src/model/apps/grupsCooperatius/ItemGrupCooperatiu";
import {Agrupament} from "src/model/apps/grupsCooperatius/Agrupament";

export interface GrupCooperatiu {
  nom: string;
  itemsGrupCooperatiu: Array<ItemGrupCooperatiu>;
  agrupaments: Array<Agrupament>;
  usuari: Usuari;
}

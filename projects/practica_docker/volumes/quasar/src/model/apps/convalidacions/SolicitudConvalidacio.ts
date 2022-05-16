import {Usuari} from "src/model/Usuari";
import {ItemConvalidacio} from "src/model/apps/convalidacions/ItemConvalidacio";

export interface SolicitudConvalidacio {
  id?: number;
  estat: string;
  observacions?: string;
  estudisOrigen: Array<ItemConvalidacio>;
  estudisOrigenManual?: string;
  estudisOrigenObservacions?: string;
  estudisEnCurs: ItemConvalidacio;
  estudisEnCursManual?: string;
  estudisEnCursObservacions?: string;
  alumne: Usuari;
}

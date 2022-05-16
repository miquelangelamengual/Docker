import {ItemConvalidacio} from "src/model/apps/convalidacions/ItemConvalidacio";

export interface ConvalidacioConvalidacio {
  id?: number;
  origens: Array<ItemConvalidacio>;
  convalida: Array<ItemConvalidacio>;
}

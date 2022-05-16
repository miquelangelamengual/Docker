import {axios}  from 'boot/axios'
import {Item} from "src/model/apps/grupsCooperatius/Item";
import {ValorItem} from "src/model/apps/grupsCooperatius/ValorItem";

export class GrupCooperatiuService {
  static async getItemById(id:number): Promise<Item> {
    const response = await axios.get(process.env.API + '/apps/grupscooperatius/item/' + id);
    const data = await response.data;
    return {
      id: data.iditem,
      nom: data.nom
    }
  }

  static async getItems(): Promise<Array<Item>> {
    const response = await axios.get(process.env.API + '/apps/grupscooperatius/items');
    const data = await response.data;
    return data.map((item:any):Item=>{
        return {
          id: item.iditem,
          nom: item.nom
        }
    });
  }

  static async getValorItems(item:Item):Promise<Array<ValorItem>> {
    const responseValors = await axios.get(process.env.API + '/apps/grupscooperatius/item/valors/' + item.id);
    const dataValors = await responseValors.data;

    return dataValors.map((valorItem:any):ValorItem=>{
      return {
        id: valorItem.idvalorItem,
        pes: valorItem.pes,
        valor: valorItem.valor,
        item: item
      }
    })
  }

  static async save(item:Item):Promise<void>{
    await axios.post(process.env.API + '/apps/grupscooperatius/item/desar',item);
  }
}

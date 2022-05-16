package cat.iesmanacor.gestibgsuite.manager.apps.grupscooperatius;

import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.Item;
import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import cat.iesmanacor.gestibgsuite.repository.apps.grupscooperatius.ItemRepository;
import cat.iesmanacor.gestibgsuite.repository.apps.grupscooperatius.ValorItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ValorItemRepository valorItemRepository;

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public Item getItemById(Long id){
        //Ha de ser findById i no getById perquè getById és Lazy
        return itemRepository.findById(id).get();
        //return itemRepository.getById(id);
    }

    public List<Item> findAllByUsuari(Usuari usuari){
        return itemRepository.findAllByUsuari(usuari);
    }

    public void deleteAllValorsByItem(Item item){
        valorItemRepository.deleteAllByItem(item);
    }
}


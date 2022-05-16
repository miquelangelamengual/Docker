package cat.iesmanacor.gestibgsuite.manager.apps.grupscooperatius;

import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.Item;
import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.ValorItem;
import cat.iesmanacor.gestibgsuite.repository.apps.grupscooperatius.ValorItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValorItemService {

    @Autowired
    private ValorItemRepository valorItemRepository;

    public ValorItem findById(Long id){
        return valorItemRepository.findById(id).get();
    }

    public List<ValorItem> findAllValorsByItem(Item item){
        return valorItemRepository.findAllByItem(item);
    }

    public ValorItem save(ValorItem valorItem){
        return valorItemRepository.save(valorItem);
    }
}


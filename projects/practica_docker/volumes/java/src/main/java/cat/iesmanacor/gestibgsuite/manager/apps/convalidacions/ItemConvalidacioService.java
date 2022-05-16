package cat.iesmanacor.gestibgsuite.manager.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.CategoriaConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.ItemConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.Item;
import cat.iesmanacor.gestibgsuite.repository.apps.convalidacions.CategoriaConvalidacioRepository;
import cat.iesmanacor.gestibgsuite.repository.apps.convalidacions.ItemConvalidacioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemConvalidacioService {
    @Autowired
    private ItemConvalidacioRepository itemConvalidacioRepository;

    public List<ItemConvalidacio> findAllTitulacions(){
       List<ItemConvalidacio> all = itemConvalidacioRepository.findAll();
       List<ItemConvalidacio> excluded = new ArrayList<>();
       List<ItemConvalidacio> result = new ArrayList<>();

       for(ItemConvalidacio itemConvalidacio: all){
           excluded.addAll(itemConvalidacio.getComposa());
       }

        for(ItemConvalidacio itemConvalidacio: all){
            if(!excluded.contains(itemConvalidacio)){
                result.add(itemConvalidacio);
            }
        }

       return result;
    }

    public List<ItemConvalidacio> findAll(){
        return itemConvalidacioRepository.findAll();
    }

    public ItemConvalidacio getItemConvalidacioById(Long id){
        //Ha de ser findById i no getById perquè getById és Lazy
        return itemConvalidacioRepository.findById(id).get();
        //return itemRepository.getById(id);
    }

    public ItemConvalidacio save(ItemConvalidacio itemConvalidacio) {
        return itemConvalidacioRepository.save(itemConvalidacio);
    }

    public void esborrar(ItemConvalidacio itemConvalidacio){
        itemConvalidacioRepository.delete(itemConvalidacio);
    }

}


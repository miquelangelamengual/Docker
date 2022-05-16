package cat.iesmanacor.gestibgsuite.manager.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.CategoriaConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.Item;
import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import cat.iesmanacor.gestibgsuite.repository.apps.convalidacions.CategoriaConvalidacioRepository;
import cat.iesmanacor.gestibgsuite.repository.apps.grupscooperatius.ItemRepository;
import cat.iesmanacor.gestibgsuite.repository.apps.grupscooperatius.ValorItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaConvalidacioService {
    @Autowired
    private CategoriaConvalidacioRepository categoriaConvalidacioRepository;

    public CategoriaConvalidacio save(CategoriaConvalidacio categoriaConvalidacio) {
        return categoriaConvalidacioRepository.save(categoriaConvalidacio);
    }

    public CategoriaConvalidacio getCategoriaConvalidacioById(Long id){
        //Ha de ser findById i no getById perquè getById és Lazy
        return categoriaConvalidacioRepository.findById(id).get();
        //return itemRepository.getById(id);
    }

    public List<CategoriaConvalidacio> findAll(){
        return categoriaConvalidacioRepository.findAll();
    }

    public void esborrar(CategoriaConvalidacio categoriaConvalidacio){
        categoriaConvalidacioRepository.delete(categoriaConvalidacio);
    }
}


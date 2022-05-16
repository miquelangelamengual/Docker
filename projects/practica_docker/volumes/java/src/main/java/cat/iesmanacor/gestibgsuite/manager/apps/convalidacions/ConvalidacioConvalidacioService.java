package cat.iesmanacor.gestibgsuite.manager.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.ConvalidacioConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.ItemConvalidacio;
import cat.iesmanacor.gestibgsuite.repository.apps.convalidacions.ConvalidacioConvalidacioRepository;
import cat.iesmanacor.gestibgsuite.repository.apps.convalidacions.ItemConvalidacioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConvalidacioConvalidacioService {
    @Autowired
    private ConvalidacioConvalidacioRepository convalidacioConvalidacioRepository;

    public List<ConvalidacioConvalidacio> findAll(){
       return convalidacioConvalidacioRepository.findAll();
    }


    public ConvalidacioConvalidacio getConvalidacioConvalidacioById(Long id){
        //Ha de ser findById i no getById perquè getById és Lazy
        return convalidacioConvalidacioRepository.findById(id).get();
        //return itemRepository.getById(id);
    }

    public ConvalidacioConvalidacio save(ConvalidacioConvalidacio convalidacioConvalidacio) {
        return convalidacioConvalidacioRepository.save(convalidacioConvalidacio);
    }

    public void esborrar(ConvalidacioConvalidacio convalidacioConvalidacio){
        convalidacioConvalidacioRepository.delete(convalidacioConvalidacio);
    }

}


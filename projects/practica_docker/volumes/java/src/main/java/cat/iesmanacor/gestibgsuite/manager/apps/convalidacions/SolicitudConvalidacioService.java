package cat.iesmanacor.gestibgsuite.manager.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.ConvalidacioConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.SolicitudConvalidacio;
import cat.iesmanacor.gestibgsuite.repository.apps.convalidacions.ConvalidacioConvalidacioRepository;
import cat.iesmanacor.gestibgsuite.repository.apps.convalidacions.SolicitudConvalidacioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudConvalidacioService {
    @Autowired
    private SolicitudConvalidacioRepository solicitudConvalidacioRepository;

    public List<SolicitudConvalidacio> findAll(){
       return solicitudConvalidacioRepository.findAll();
    }

    public SolicitudConvalidacio getSolicitudConvalidacioById(Long id){
        //Ha de ser findById i no getById perquè getById és Lazy
        return solicitudConvalidacioRepository.findById(id).get();
        //return solicitudConvalidacioRepository.getById(id);
    }

    public SolicitudConvalidacio save(SolicitudConvalidacio solicitudConvalidacio) {
        return solicitudConvalidacioRepository.save(solicitudConvalidacio);
    }

    public void esborrar(SolicitudConvalidacio solicitudConvalidacio){
        solicitudConvalidacioRepository.delete(solicitudConvalidacio);
    }

}


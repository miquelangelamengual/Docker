package cat.iesmanacor.gestibgsuite.repository.apps.grupscooperatius;

import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.Item;
import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByUsuari(Usuari usuari);
}

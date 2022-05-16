package cat.iesmanacor.gestibgsuite.repository.apps.grupscooperatius;

import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.Item;
import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.ValorItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValorItemRepository extends JpaRepository<ValorItem, Long> {
    void deleteAllByItem(Item item);

    List<ValorItem> findAllByItem(Item item);
}

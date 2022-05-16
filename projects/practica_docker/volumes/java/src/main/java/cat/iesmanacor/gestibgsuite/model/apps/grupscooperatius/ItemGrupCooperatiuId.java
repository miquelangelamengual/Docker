package cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius;

import lombok.Data;

import java.io.Serializable;


public @Data class ItemGrupCooperatiuId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long item;
    private Long grupCooperatiu;

}

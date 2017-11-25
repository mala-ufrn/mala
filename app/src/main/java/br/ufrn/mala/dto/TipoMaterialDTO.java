package br.ufrn.mala.dto;

import java.io.Serializable;

/**
 * Created by Hugo Oliveira on 24/11/17.
 */

public class TipoMaterialDTO implements Serializable {

    private String descricao;

    private Integer idTipoMaterial;

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getIdTipoMaterial() { return idTipoMaterial; }

    public void setIdTipoMaterial(Integer idTipoMaterial) { this.idTipoMaterial = idTipoMaterial; }
}

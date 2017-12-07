package br.ufrn.mala.dto;

import java.io.Serializable;

/**
 * Created by Hugo Oliveira on 24/11/17.
 */

public class StatusMaterialDTO implements Serializable {

    private String descricao;

    private Integer idStatusMaterial;

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getIdStatusMaterial() { return idStatusMaterial; }

    public void setIdStatusMaterial(Integer idStatusMaterial) { this.idStatusMaterial = idStatusMaterial; }
}

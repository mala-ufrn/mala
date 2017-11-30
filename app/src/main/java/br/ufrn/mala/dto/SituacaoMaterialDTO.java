package br.ufrn.mala.dto;

import java.io.Serializable;

/**
 * Created by Hugo Oliveira on 24/11/17.
 */

public class SituacaoMaterialDTO implements Serializable {

    private String descricao;

    private Integer idSituacaoMaterial;

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getIdSituacaoMaterial() { return idSituacaoMaterial; }

    public void setIdSituacaoMaterial(Integer idSituacaoMaterial) { this.idSituacaoMaterial = idSituacaoMaterial; }
}

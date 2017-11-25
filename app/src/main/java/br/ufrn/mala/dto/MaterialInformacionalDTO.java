package br.ufrn.mala.dto;

import java.io.Serializable;

/**
 * Created by Hugo Oliveira on 24/11/17.
 */

public class MaterialInformacionalDTO implements Serializable {

    private String autor;

    private String codigoBarras;

    private String colecao;

    private Long dataDisponivel;

    private Integer idBiblioteca;

    private Long idMaterialInformacional;

    private Integer idSituacaoMaterial;

    private Integer idStatusMaterial;

    private Integer idTipoMaterial;

    private String localizacao;

    private Integer registroSistema;

    private String titulo;

    public String getAutor() { return autor; }

    public void setAutor(String autor) { this.autor = autor; }

    public String getCodigoBarras() { return codigoBarras; }

    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getColecao() { return colecao; }

    public void setColecao(String colecao) { this.colecao = colecao; }

    public Long getDataDisponivel() { return dataDisponivel; }

    public void setDataDisponivel(Long dataDisponivel) { this.dataDisponivel = dataDisponivel; }

    public Integer getIdBiblioteca() { return idBiblioteca; }

    public void setIdBiblioteca(Integer idBiblioteca) { this.idBiblioteca = idBiblioteca; }

    public Long getIdMaterialInformacional() { return idMaterialInformacional; }

    public void setIdMaterialInformacional(Long idMaterialInformacional) { this.idMaterialInformacional = idMaterialInformacional; }

    public Integer getIdSituacaoMaterial() { return idSituacaoMaterial; }

    public void setIdSituacaoMaterial(Integer idSituacaoMaterial) { this.idSituacaoMaterial = idSituacaoMaterial; }

    public Integer getIdStatusMaterial() { return idStatusMaterial; }

    public void setIdStatusMaterial(Integer idStatusMaterial) { this.idStatusMaterial = idStatusMaterial; }

    public Integer getIdTipoMaterial() { return idTipoMaterial; }

    public void setIdTipoMaterial(Integer idTipoMaterial) { this.idTipoMaterial = idTipoMaterial; }

    public String getLocalizacao() { return localizacao; }

    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public Integer getRegistroSistema() { return registroSistema; }

    public void setRegistroSistema(Integer registroSistema) { this.registroSistema = registroSistema; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
}

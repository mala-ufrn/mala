package br.ufrn.mala.dto;

import java.io.Serializable;

/**
 * Created by Hugo Oliveira on 24/11/17.
 */

public class AcervoDTO implements Serializable {

    private String ano;

    private String[] assunto;

    private String autor;

    private String[] autoresSecundarios;

    private String descricaoFisica;

    private String edicao;

    private String editora;

    private String enderecoEletronico;

    private Integer idBiblioteca;

    private Integer idTipoMaterial;

    private String intervaloPaginas;

    private String isbn;

    private String issn;

    private String localPublicacao;

    private String notaConteudo;

    private String notasGerais;

    private String notasLocais;

    private String numeroChamada;

    private Integer quantidade;

    private Integer registroSistema;

    private String resumo;

    private String serie;

    private String subTitulo;

    private Integer tipoMaterial;

    private String titulo;

    public String getAno() { return ano; }

    public void setAno(String ano) { this.ano = ano; }

    public String[] getAssunto() { return assunto; }

    public void setAssunto(String[] assunto) { this.assunto = assunto; }

    public String getAutor() { return autor; }

    public void setAutor(String autor) { this.autor = autor; }

    public String[] getAutoresSecundarios() { return autoresSecundarios; }

    public void setAutoresSecundarios(String[] autoresSecundarios) { this.autoresSecundarios = autoresSecundarios; }

    public String getDescricaoFisica() { return descricaoFisica; }

    public void setDescricaoFisica(String descricaoFisica) { this.descricaoFisica = descricaoFisica; }

    public String getEdicao() { return edicao; }

    public void setEdicao(String edicao) { this.edicao = edicao; }

    public String getEditora() { return editora; }

    public void setEditora(String editora) { this.editora = editora; }

    public String getEnderecoEletronico() { return enderecoEletronico; }

    public void setEnderecoEletronico(String enderecoEletronico) { this.enderecoEletronico = enderecoEletronico; }

    public Integer getIdBiblioteca() { return idBiblioteca; }

    public void setIdBiblioteca(Integer idBiblioteca) { this.idBiblioteca = idBiblioteca; }

    public Integer getIdTipoMaterial() { return idTipoMaterial; }

    public void setIdTipoMaterial(Integer idTipoMaterial) { this.idTipoMaterial = idTipoMaterial; }

    public String getIntervaloPaginas() { return intervaloPaginas; }

    public void setIntervaloPaginas(String intervaloPaginas) { this.intervaloPaginas = intervaloPaginas; }

    public String getIsbn() { return isbn; }

    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getIssn() { return issn; }

    public void setIssn(String issn) { this.issn = issn; }

    public String getLocalPublicacao() { return localPublicacao; }

    public void setLocalPublicacao(String localPublicacao) { this.localPublicacao = localPublicacao; }

    public String getNotaConteudo() { return notaConteudo; }

    public void setNotaConteudo(String notaConteudo) { this.notaConteudo = notaConteudo; }

    public String getNotasGerais() { return notasGerais; }

    public void setNotasGerais(String notasGerais) { this.notasGerais = notasGerais; }

    public String getNotasLocais() { return notasLocais; }

    public void setNotasLocais(String notasLocais) { this.notasLocais = notasLocais; }

    public String getNumeroChamada() { return numeroChamada; }

    public void setNumeroChamada(String numeroChamada) { this.numeroChamada = numeroChamada; }

    public Integer getQuantidade() { return quantidade; }

    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Integer getRegistroSistema() { return registroSistema; }

    public void setRegistroSistema(Integer registroSistema) { this.registroSistema = registroSistema; }

    public String getResumo() { return resumo; }

    public void setResumo(String resumo) { this.resumo = resumo; }

    public String getSerie() { return serie; }

    public void setSerie(String serie) { this.serie = serie; }

    public String getSubTitulo() { return subTitulo; }

    public void setSubTitulo(String subTitulo) { this.subTitulo = subTitulo; }

    public Integer getTipoMaterial() { return tipoMaterial; }

    public void setTipoMaterial(Integer tipoMaterial) { this.tipoMaterial = tipoMaterial; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
}

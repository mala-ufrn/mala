package br.ufrn.mala.dto;

import java.io.Serializable;

/**
 * Created by Joel Felipe on 02/10/17.
 */

public class EmprestimoDTO implements Serializable {

    private String autor;

    private String codigoBarras;

    private Long cpfCnpjUsuario;

    private Long dataDevolucao;

    private Long dataEmpretimo;

    private Long dataRenovacao;

    private Integer idBiblioteca;

    private Integer idEmprestimo;

    private Integer idMaterialInformacional;

    private String numeroChamada;

    private Long prazo;

    private String tipoEmprestimo;

    private String titulo;

    private BibliotecaDTO biblioteca;

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Long getCpfCnpjUsuario() {
        return cpfCnpjUsuario;
    }

    public void setCpfCnpjUsuario(Long cpfCnpjUsuario) {
        this.cpfCnpjUsuario = cpfCnpjUsuario;
    }

    public Long getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Long dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public Long getDataEmpretimo() {
        return dataEmpretimo;
    }

    public void setDataEmpretimo(Long dataEmpretimo) {
        this.dataEmpretimo = dataEmpretimo;
    }

    public Long getDataRenovacao() {
        return dataRenovacao;
    }

    public void setDataRenovacao(Long dataRenovacao) {
        this.dataRenovacao = dataRenovacao;
    }

    public Integer getIdBiblioteca() {
        return idBiblioteca;
    }

    public void setIdBiblioteca(Integer idBiblioteca) {
        this.idBiblioteca = idBiblioteca;
    }

    public Integer getIdEmprestimo() {
        return idEmprestimo;
    }

    public void setIdEmprestimo(Integer idEmprestimo) {
        this.idEmprestimo = idEmprestimo;
    }

    public Integer getIdMaterialInformacional() {
        return idMaterialInformacional;
    }

    public void setIdMaterialInformacional(Integer idMaterialInformacional) {
        this.idMaterialInformacional = idMaterialInformacional;
    }

    public String getNumeroChamada() {
        return numeroChamada;
    }

    public void setNumeroChamada(String numeroChamada) {
        this.numeroChamada = numeroChamada;
    }

    public Long getPrazo() {
        return prazo;
    }

    public void setPrazo(Long prazo) {
        this.prazo = prazo;
    }

    public String getTipoEmprestimo() {
        return tipoEmprestimo;
    }

    public void setTipoEmprestimo(String tipoEmprestimo) {
        this.tipoEmprestimo = tipoEmprestimo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public BibliotecaDTO getBiblioteca() {
        return biblioteca;
    }

    public void setBiblioteca(BibliotecaDTO biblioteca) {
        this.biblioteca = biblioteca;
    }
}

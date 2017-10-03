package br.ufrn.mala.dto;

/**
 * Created by Joel Felipe on 02/10/17.
 */

public class EmprestimoDTO {

    private String autor;

    private String biblioteca;

    private String codigoBarras;

    private Long dataDevolucao;

    private Long dataEmpretimo;

    private Long dataRenovacao;

    private Integer idEmprestimo;

    private Integer idUsuario;

    private String numeroChamada;

    private Long prazo;

    private String tipoEmprestimo;

    private String titulo;

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getBiblioteca() {
        return biblioteca;
    }

    public void setBiblioteca(String biblioteca) {
        this.biblioteca = biblioteca;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
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

    public Integer getIdEmprestimo() {
        return idEmprestimo;
    }

    public void setIdEmprestimo(Integer idEmprestimo) {
        this.idEmprestimo = idEmprestimo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
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
}

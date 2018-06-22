package br.ufrn.mala.dto;

import java.io.Serializable;

/**
 * Created by Joel Felipe on 22/06/2018.
 */

public class PoliticaEmprestimoDTO implements Serializable {

    private Integer idPoliticaEmprestimo;

    private String tipoVinculoUsuarioBiblioteca;

    private Integer prazoEmprestimo;

    private Integer quantidadeMateriais;

    private Integer quantidadeRenovacoes;

    private String tipoEmprestimo;

    private String tipoPrazo;

    public Integer getIdPoliticaEmprestimo() {
        return idPoliticaEmprestimo;
    }

    public void setIdPoliticaEmprestimo(Integer idPoliticaEmprestimo) {
        this.idPoliticaEmprestimo = idPoliticaEmprestimo;
    }

    public Integer getPrazoEmprestimo() {
        return prazoEmprestimo;
    }

    public void setPrazoEmprestimo(Integer prazoEmprestimo) {
        this.prazoEmprestimo = prazoEmprestimo;
    }

    public Integer getQuantidadeMateriais() {
        return quantidadeMateriais;
    }

    public void setQuantidadeMateriais(Integer quantidadeMateriais) {
        this.quantidadeMateriais = quantidadeMateriais;
    }

    public Integer getQuantidadeRenovacoes() {
        return quantidadeRenovacoes;
    }

    public void setQuantidadeRenovacoes(Integer quantidadeRenovacoes) {
        this.quantidadeRenovacoes = quantidadeRenovacoes;
    }

    public String getTipoEmprestimo() {
        return tipoEmprestimo;
    }

    public void setTipoEmprestimo(String tipoEmprestimo) {
        this.tipoEmprestimo = tipoEmprestimo;
    }

    public String getTipoPrazo() {
        return tipoPrazo;
    }

    public void setTipoPrazo(String tipoPrazo) {
        this.tipoPrazo = tipoPrazo;
    }

    public String getTipoVinculoUsuarioBiblioteca() {
        return tipoVinculoUsuarioBiblioteca;
    }

    public void setTipoVinculoUsuarioBiblioteca(String tipoVinculoUsuarioBiblioteca) {
        this.tipoVinculoUsuarioBiblioteca = tipoVinculoUsuarioBiblioteca;
    }
}

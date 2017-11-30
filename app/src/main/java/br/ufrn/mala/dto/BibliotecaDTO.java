package br.ufrn.mala.dto;

import java.io.Serializable;

/**
 * Created by Joel Felipe on 04/11/2017.
 */

public class BibliotecaDTO implements Serializable {

    private String descricao;

    private String email;

    private Integer idBiblioteca;

    private String sigla;

    private String site;

    private String telefone;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getIdBiblioteca() {
        return idBiblioteca;
    }

    public void setIdBiblioteca(Integer idBiblioteca) { this.idBiblioteca = idBiblioteca; }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        if (sigla == null) {
            return "Todas";
        }
        else {
            return sigla + " - " + descricao;
        }
    }
}

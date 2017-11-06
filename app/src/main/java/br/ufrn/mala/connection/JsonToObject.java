package br.ufrn.mala.connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.mala.dto.BibliotecaDTO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.JsonStringInvalidaException;

/**
 * Created by Joel Felipe on 02/10/17.
 */

public class JsonToObject {

    public static UsuarioDTO toUsuario(String text) throws JsonStringInvalidaException {
        UsuarioDTO usuario = new UsuarioDTO();
        if(!text.equalsIgnoreCase("")){
            try {
                JSONObject jsonList = new JSONObject(text);
                usuario.setAtivo(jsonList.getBoolean("ativo"));
                usuario.setChaveFoto(jsonList.getString("chave-foto"));
                usuario.setCpfCnpj(jsonList.getLong("cpf-cnpj"));
                usuario.setEmail(jsonList.getString("email"));
                usuario.setIdFoto(jsonList.getLong("id-foto"));
                usuario.setIdUnidade(jsonList.getInt("id-unidade"));
                usuario.setIdUsuario(jsonList.getLong("id-usuario"));
                usuario.setLogin(jsonList.getString("login"));
                usuario.setNomePessoa(jsonList.getString("nome-pessoa"));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                throw new JsonStringInvalidaException(e.getMessage());
            }
        }else{
            throw new JsonStringInvalidaException("String vazia!");
        }
        return usuario;
    }

    public static List<EmprestimoDTO> toEmprestimos(String text) throws JsonStringInvalidaException{
        List<EmprestimoDTO> emprestimos = new ArrayList<>();
        if(!text.equalsIgnoreCase("")){
            try {
                JSONArray array = new JSONArray(text);
                for(int i = 0; i < array.length(); ++i){
                    JSONObject jsonList = array.getJSONObject(i);
                    EmprestimoDTO emprestimo = new EmprestimoDTO();
                    emprestimo.setAutor(convertJSONObject(jsonList, "autor", String.class));
                    emprestimo.setCodigoBarras(convertJSONObject(jsonList, "codigo-barras", String.class));
                    emprestimo.setCpfCnpjUsuario(convertJSONObject(jsonList, "cpf-cnpj-usuario", Long.class));
                    emprestimo.setDataDevolucao(convertJSONObject(jsonList, "data-devolucao", Long.class));
                    emprestimo.setDataEmpretimo(convertJSONObject(jsonList, "data-emprestimo", Long.class));
                    emprestimo.setDataRenovacao(convertJSONObject(jsonList, "data-renovacao", Long.class));
                    emprestimo.setIdBiblioteca(convertJSONObject(jsonList, "id-biblioteca", Integer.class));
                    emprestimo.setIdEmprestimo(convertJSONObject(jsonList, "id-emprestimo", Integer.class));
                    emprestimo.setIdMaterialInformacional(convertJSONObject(jsonList, "id-material-informacional", Integer.class));
                    emprestimo.setNumeroChamada(convertJSONObject(jsonList, "numero-chamada", String.class));
                    emprestimo.setPrazo(convertJSONObject(jsonList, "prazo", Long.class));
                    emprestimo.setTipoEmprestimo(convertJSONObject(jsonList, "tipo-emprestimo", String.class));
                    emprestimo.setTitulo(convertJSONObject(jsonList, "titulo", String.class));

                    emprestimos.add(emprestimo);
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                throw new JsonStringInvalidaException(e.getMessage());
            }
        }else{
            throw new JsonStringInvalidaException("String vazia!");
        }
        return emprestimos;
    }

    public static BibliotecaDTO toBiblioteca(String text) throws JsonStringInvalidaException {
        BibliotecaDTO biblioteca = new BibliotecaDTO();
        if(!text.equalsIgnoreCase("")){
            try {
                JSONObject jsonList = new JSONObject(text);
                biblioteca.setDescricao(convertJSONObject(jsonList, "descricao", String.class));
                biblioteca.setEmail(convertJSONObject(jsonList, "email", String.class));
                biblioteca.setIdBiblioteca(convertJSONObject(jsonList, "id-biblioteca", Integer.class));
                biblioteca.setSigla(convertJSONObject(jsonList, "sigla", String.class));
                biblioteca.setSite(convertJSONObject(jsonList, "site", String.class));
                biblioteca.setTelefone(convertJSONObject(jsonList, "telefone", String.class));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                throw new JsonStringInvalidaException(e.getMessage());
            }
        }else{
            throw new JsonStringInvalidaException("String vazia!");
        }
        return biblioteca;
    }

    private static <T> T convertJSONObject(JSONObject json, String param, Class<T> classOfT) throws JSONException {
        if (json.isNull(param))
            return null;
        else
            return classOfT.cast(json.get(param));
    }
}

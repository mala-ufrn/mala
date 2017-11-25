package br.ufrn.mala.connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.mala.dto.AcervoDTO;
import br.ufrn.mala.dto.BibliotecaDTO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.MaterialInformacionalDTO;
import br.ufrn.mala.dto.SituacaoMaterialDTO;
import br.ufrn.mala.dto.StatusMaterialDTO;
import br.ufrn.mala.dto.TipoMaterialDTO;
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

    public static MaterialInformacionalDTO toMaterialInformacional(String text) throws JsonStringInvalidaException {
        MaterialInformacionalDTO materialInformacional = new MaterialInformacionalDTO();
        if(!text.equalsIgnoreCase("")){
            try {
                JSONObject jsonList = new JSONObject(text);
                materialInformacional.setAutor(convertJSONObject(jsonList, "autor", String.class));
                materialInformacional.setCodigoBarras(convertJSONObject(jsonList, "codigo-barras", String.class));
                materialInformacional.setColecao(convertJSONObject(jsonList, "colecao", String.class));
                materialInformacional.setDataDisponivel(convertJSONObject(jsonList, "data-disponivel", Long.class));
                materialInformacional.setIdBiblioteca(convertJSONObject(jsonList, "id-biblioteca", Integer.class));
                materialInformacional.setIdMaterialInformacional(convertJSONObject(jsonList, "id-material-informacional", Long.class));
                materialInformacional.setIdSituacaoMaterial(convertJSONObject(jsonList, "id-situacao-material", Integer.class));
                materialInformacional.setIdStatusMaterial(convertJSONObject(jsonList, "id-status-material", Integer.class));
                materialInformacional.setIdTipoMaterial(convertJSONObject(jsonList, "id-tipo-material", Integer.class));
                materialInformacional.setLocalizacao(convertJSONObject(jsonList, "localizacao", String.class));
                materialInformacional.setRegistroSistema(convertJSONObject(jsonList, "registro-sistema", Integer.class));
                materialInformacional.setTitulo(convertJSONObject(jsonList, "titulo", String.class));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                throw new JsonStringInvalidaException(e.getMessage());
            }
        }else{
            throw new JsonStringInvalidaException("String vazia!");
        }
        return materialInformacional;
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

    public static List<AcervoDTO> toAcervoList(String text) throws JsonStringInvalidaException {
        List<AcervoDTO> acervoList = new ArrayList<>();
        if(!text.equalsIgnoreCase("")){
            try {
                JSONArray array = new JSONArray(text);
                for(int i = 0; i < array.length(); ++i) {
                    JSONObject jsonList = array.getJSONObject(i);
                    AcervoDTO acervoItem = new AcervoDTO();
                    acervoItem.setAno(convertJSONObject(jsonList, "ano", String.class));

                    JSONArray subArray = jsonList.getJSONArray("assunto");
                    String[] assuntoArray = new String[subArray.length()];
                    for(int j = 0; j < subArray.length(); j++)
                        assuntoArray[i] = subArray.getString(i);
                    acervoItem.setAssunto(assuntoArray);

                    acervoItem.setAutor(convertJSONObject(jsonList, "autor", String.class));

                    subArray = jsonList.getJSONArray("autores-secundarios");
                    String[] autorSecArray = new String[subArray.length()];
                    for(int j = 0; j < subArray.length(); j++)
                        autorSecArray[i] = subArray.getString(i);
                    acervoItem.setAutoresSecundarios(autorSecArray);

                    acervoItem.setDescricaoFisica(convertJSONObject(jsonList, "descricao-fisica", String.class));
                    acervoItem.setEdicao(convertJSONObject(jsonList, "edicao", String.class));
                    acervoItem.setEditora(convertJSONObject(jsonList, "editora", String.class));
                    acervoItem.setEnderecoEletronico(convertJSONObject(jsonList, "endereco-eletronico", String.class));
                    acervoItem.setIdBiblioteca(convertJSONObject(jsonList, "id-biblioteca", Integer.class));
                    acervoItem.setIdTipoMaterial(convertJSONObject(jsonList, "id-tipo-material", Integer.class));
                    acervoItem.setIntervaloPaginas(convertJSONObject(jsonList, "intervalo-paginas", String.class));
                    acervoItem.setIsbn(convertJSONObject(jsonList, "isbn", String.class));
                    acervoItem.setIssn(convertJSONObject(jsonList, "issn", String.class));
                    acervoItem.setLocalPublicacao(convertJSONObject(jsonList, "local-publicacao", String.class));
                    acervoItem.setNotaConteudo(convertJSONObject(jsonList, "nota-conteudo", String.class));
                    acervoItem.setNotasGerais(convertJSONObject(jsonList, "notas-gerais", String.class));
                    acervoItem.setNotasLocais(convertJSONObject(jsonList, "notas-locais", String.class));
                    acervoItem.setNumeroChamada(convertJSONObject(jsonList, "numero-chamada", String.class));
                    acervoItem.setQuantidade(convertJSONObject(jsonList, "quantidade", Integer.class));
                    acervoItem.setRegistroSistema(convertJSONObject(jsonList, "registro-sistema", Integer.class));
                    acervoItem.setResumo(convertJSONObject(jsonList, "resumo", String.class));
                    acervoItem.setSerie(convertJSONObject(jsonList, "serie", String.class));
                    acervoItem.setSubTitulo(convertJSONObject(jsonList, "sub-titulo", String.class));
                    acervoItem.setTipoMaterial(convertJSONObject(jsonList, "tipo-material", String.class));
                    acervoItem.setTitulo(convertJSONObject(jsonList, "titulo", String.class));

                    acervoList.add(acervoItem);
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                throw new JsonStringInvalidaException(e.getMessage());
            }
        }else{
            throw new JsonStringInvalidaException("String vazia!");
        }
        return acervoList;
    }

    public static List<BibliotecaDTO> toBibliotecas(String text) throws JsonStringInvalidaException {
        List<BibliotecaDTO> bibliotecas = new ArrayList<>();
        if(!text.equalsIgnoreCase("")){
            try {
                JSONArray array = new JSONArray(text);
                for(int i = 0; i < array.length(); ++i) {
                    JSONObject jsonList = array.getJSONObject(i);
                    BibliotecaDTO biblioteca = new BibliotecaDTO();
                    biblioteca.setDescricao(convertJSONObject(jsonList, "descricao", String.class));
                    biblioteca.setEmail(convertJSONObject(jsonList, "email", String.class));
                    biblioteca.setIdBiblioteca(convertJSONObject(jsonList, "id-biblioteca", Integer.class));
                    biblioteca.setSigla(convertJSONObject(jsonList, "sigla", String.class));
                    biblioteca.setSite(convertJSONObject(jsonList, "site", String.class));
                    biblioteca.setTelefone(convertJSONObject(jsonList, "telefone", String.class));

                    bibliotecas.add(biblioteca);
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                throw new JsonStringInvalidaException(e.getMessage());
            }
        }else{
            throw new JsonStringInvalidaException("String vazia!");
        }
        return bibliotecas;
    }

    public static List<SituacaoMaterialDTO> toSituacoesMaterial(String text) throws JsonStringInvalidaException {
        List<SituacaoMaterialDTO> situacoesMaterial = new ArrayList<>();
        if(!text.equalsIgnoreCase("")){
            try {
                JSONArray array = new JSONArray(text);
                for(int i = 0; i < array.length(); ++i) {
                    JSONObject jsonList = array.getJSONObject(i);
                    SituacaoMaterialDTO situacaoMaterial = new SituacaoMaterialDTO();
                    situacaoMaterial.setDescricao(convertJSONObject(jsonList, "descricao", String.class));
                    situacaoMaterial.setIdSituacaoMaterial(convertJSONObject(jsonList, "id-situacao-material", Integer.class));
                    situacoesMaterial.add(situacaoMaterial);
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                throw new JsonStringInvalidaException(e.getMessage());
            }
        }else{
            throw new JsonStringInvalidaException("String vazia!");
        }
        return situacoesMaterial;
    }

    public static List<StatusMaterialDTO> toStatusMaterial(String text) throws JsonStringInvalidaException {
        List<StatusMaterialDTO> statusMateriais = new ArrayList<>();
        if(!text.equalsIgnoreCase("")){
            try {
                JSONArray array = new JSONArray(text);
                for(int i = 0; i < array.length(); ++i) {
                    JSONObject jsonList = array.getJSONObject(i);
                    StatusMaterialDTO statusMaterial = new StatusMaterialDTO();
                    statusMaterial.setDescricao(convertJSONObject(jsonList, "descricao", String.class));
                    statusMaterial.setIdStatusMaterial(convertJSONObject(jsonList, "id-status-material", Integer.class));
                    statusMateriais.add(statusMaterial);
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                throw new JsonStringInvalidaException(e.getMessage());
            }
        }else{
            throw new JsonStringInvalidaException("String vazia!");
        }
        return statusMateriais;
    }

    public static List<TipoMaterialDTO> toTiposMaterial(String text) throws JsonStringInvalidaException {
        List<TipoMaterialDTO> tiposMateriais = new ArrayList<>();
        if(!text.equalsIgnoreCase("")){
            try {
                JSONArray array = new JSONArray(text);
                for(int i = 0; i < array.length(); ++i) {
                    JSONObject jsonList = array.getJSONObject(i);
                    TipoMaterialDTO statusMaterial = new TipoMaterialDTO();
                    statusMaterial.setDescricao(convertJSONObject(jsonList, "descricao", String.class));
                    statusMaterial.setIdTipoMaterial(convertJSONObject(jsonList, "id-tipo-material", Integer.class));
                    tiposMateriais.add(statusMaterial);
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                throw new JsonStringInvalidaException(e.getMessage());
            }
        }else{
            throw new JsonStringInvalidaException("String vazia!");
        }
        return tiposMateriais;
    }



    private static <T> T convertJSONObject(JSONObject json, String param, Class<T> classOfT) throws JSONException {
        if (json.isNull(param))
            return null;
        else
            return classOfT.cast(json.get(param));
    }
}

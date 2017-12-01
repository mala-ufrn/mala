package br.ufrn.mala.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.mala.dto.AcervoDTO;
import br.ufrn.mala.dto.BibliotecaDTO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.SituacaoMaterialDTO;
import br.ufrn.mala.dto.StatusMaterialDTO;
import br.ufrn.mala.dto.TipoMaterialDTO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.JsonStringInvalidaException;
import br.ufrn.mala.util.Constants;

/**
 * Conexão com o banco de dados da aplicação</a>
 *
 * @author Joel Felipe
 * @see <a href="https://pt.wikipedia.org/wiki/Singleton">Singleton</a>
 */

public class SQLiteConnection {
    private static SQLiteConnection sqLiteConnection;
    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;
    private UsuarioDTO usuarioLogado;

    public static SQLiteConnection getInstance(Context context){
        if(sqLiteConnection == null)
            sqLiteConnection = new SQLiteConnection(context);
        return sqLiteConnection;
    }

    private SQLiteConnection(Context context) {
        SQLiteHelper helper = new SQLiteHelper(context);
        readableDatabase = helper.getReadableDatabase();
        writableDatabase = helper.getWritableDatabase();

        SharedPreferences mPrefs = context.getSharedPreferences(Constants.KEY_USER_INFO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String usuario = mPrefs.getString("UsuarioLogado", null);
        if (usuario != null)
            usuarioLogado = gson.fromJson(usuario, UsuarioDTO.class);
    }

    /**
     * Atualizar o usuário logado na aplicação
     * @param usuarioLogado Usuario logado na aplicação
     * @return Usuário logado na aplicação
     */
    public void setUsuarioLogado(UsuarioDTO usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    /**
     * Atualizar o usuário logado na aplicação
     * @return Usuário logado na aplicação
     */
    public UsuarioDTO getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * Consulta a quantidade de empréstimos do usuário logado, no banco de dados
     * @param ativo Indica se os empréstimos consultados serão os ativos, inativos ou ambos
     * @return Quantidade de empréstimos
     */
    public Integer getQuantidadeEmprestimos(Boolean ativo) {
        String sql = "SELECT * " +
                "FROM emprestimos " +
                "WHERE ativo = ? " +
                "AND cpf_cnpj_usuario = ? ";
        return readableDatabase.rawQuery(sql, new String[] {ativo.toString(), usuarioLogado.getCpfCnpj().toString()}).getCount();
    }

    /**
     * Consulta os empréstimos do usuário logado, no banco de dados
     * @param ativo Indica se os empréstimos consultados serão os ativos(true), inativos(false) ou ambos(null)
     * @param offset Offset usado na consulta
     * @return Quantidade de empréstimos
     */
    public List<EmprestimoDTO> getEmprestimos(Boolean ativo, Integer offset) {
        String sql = "SELECT * " +
                "FROM emprestimo " +
                "JOIN biblioteca USING(id_biblioteca)" +
                "WHERE ativo = ? " +
                "AND cpf_cnpj_usuario = ? " +
                "ORDER BY data_emprestimo DESC " +
                "LIMIT 20 " +
                "OFFSET ?";
        Cursor result = readableDatabase.rawQuery(sql, new String[] {ativo.toString(), usuarioLogado.getCpfCnpj().toString(), offset.toString()});
        List<EmprestimoDTO> emprestimos = new ArrayList<>();
        while (result.moveToNext()){
            EmprestimoDTO emprestimo = new EmprestimoDTO();
            emprestimo.setAutor(result.getString(result.getColumnIndex("autor")));
            emprestimo.setCodigoBarras(result.getString(result.getColumnIndex("codigo_barras")));
            emprestimo.setCpfCnpjUsuario(result.getLong(result.getColumnIndex("cpf_cnpj_usuario")));
            emprestimo.setDataEmpretimo(result.getLong(result.getColumnIndex("data_emprestimo")));

            if (!result.isNull(result.getColumnIndex("data_renovacao")))
                emprestimo.setDataRenovacao(result.getLong(result.getColumnIndex("data_renovacao")));
            else
                emprestimo.setDataRenovacao(null);

            if (!result.isNull(result.getColumnIndex("data_devolucao")))
                emprestimo.setDataDevolucao(result.getLong(result.getColumnIndex("data_devolucao")));
            else
                emprestimo.setDataDevolucao(null);

            emprestimo.setIdBiblioteca(result.getInt(result.getColumnIndex("id_biblioteca")));
            emprestimo.setIdEmprestimo(result.getInt(result.getColumnIndex("id_emprestimo")));
            emprestimo.setIdMaterialInformacional(result.getInt(result.getColumnIndex("id_material_informacional")));
            emprestimo.setNumeroChamada(result.getString(result.getColumnIndex("numero_chamada")));
            emprestimo.setPrazo(result.getLong(result.getColumnIndex("prazo")));
            emprestimo.setTipoEmprestimo(result.getString(result.getColumnIndex("tipo_emprestimo")));
            emprestimo.setTitulo(result.getString(result.getColumnIndex("titulo")));

            BibliotecaDTO biblioteca = new BibliotecaDTO();
            biblioteca.setIdBiblioteca(result.getInt(result.getColumnIndex("id_biblioteca")));
            biblioteca.setDescricao(result.getString(result.getColumnIndex("descricao")));
            biblioteca.setEmail(result.getString(result.getColumnIndex("email")));
            biblioteca.setSigla(result.getString(result.getColumnIndex("sigla")));
            biblioteca.setSite(result.getString(result.getColumnIndex("site")));
            biblioteca.setTelefone(result.getString(result.getColumnIndex("telefone")));

            emprestimo.setBiblioteca(biblioteca);
            emprestimos.add(emprestimo);
        }
        result.close();
        return emprestimos;
    }

    /**
     * Consulta as bibliotecas cadastradas no banco de dados
     * @return Lista de bibliotecas
     */
    public List<BibliotecaDTO> getBibliotecas(boolean toSearch) {
        String sql = "SELECT * " +
                "FROM biblioteca";
        if (toSearch) {

            sql += " WHERE id_biblioteca NOT IN ( 723675, 792473, 792657, 1266705, 1606845," +
                                                " 1607401, 1645680, 1698319, 1857962, 1863870," +
                                                " 2001419, 2040152, 2040954, 2055309, 2094851," +
                                                " 2094948, 2094949, 2094953, 2094955, 2094956," +
                                                " 2094961, 2094962, 2094963, 2095036, 2095037," +
                                                " 2095045, 2095046, 2095047, 2096829, 2108193," +
                                                " 2156263, 2159842, 2210677 )";
        }
        Cursor result = readableDatabase.rawQuery(sql, new String[]{});
        List<BibliotecaDTO> bibliotecas = new ArrayList<>();
        while (result.moveToNext()){
            BibliotecaDTO biblioteca = new BibliotecaDTO();
            biblioteca.setIdBiblioteca(result.getInt(result.getColumnIndex("id_biblioteca")));
            biblioteca.setDescricao(result.getString(result.getColumnIndex("descricao")));
            biblioteca.setEmail(result.getString(result.getColumnIndex("email")));
            biblioteca.setSigla(result.getString(result.getColumnIndex("sigla")));
            biblioteca.setSite(result.getString(result.getColumnIndex("site")));
            biblioteca.setTelefone(result.getString(result.getColumnIndex("telefone")));
            bibliotecas.add(biblioteca);
        }
        result.close();
        return bibliotecas;
    }

    /**
     * Consulta as situacoes de material cadastradas no banco de dados
     * @return Lista de situações de matarial
     */
    public List<SituacaoMaterialDTO> getSituacoesMaterial() {
        String sql = "SELECT * " +
                "FROM situacao_material";
        Cursor result = readableDatabase.rawQuery(sql, new String[] {});
        List<SituacaoMaterialDTO> situacoesMaterial = new ArrayList<>();
        while (result.moveToNext()){
            SituacaoMaterialDTO situacaoMaterial = new SituacaoMaterialDTO();
            situacaoMaterial.setDescricao(result.getString(result.getColumnIndex("descricao")));
            situacaoMaterial.setIdSituacaoMaterial(result.getInt(result.getColumnIndex("id_situacao_material")));
            situacoesMaterial.add(situacaoMaterial);
        }
        result.close();
        return situacoesMaterial;
    }

    /**
     * Consulta os status de material cadastrados no banco de dados
     * @return Lista de status de material
     */
    public List<StatusMaterialDTO> getStatusMateriais() {
        String sql = "SELECT * " +
                "FROM status_material";
        Cursor result = readableDatabase.rawQuery(sql, new String[] {});
        List<StatusMaterialDTO> statusMateriais = new ArrayList<>();
        while (result.moveToNext()){
            StatusMaterialDTO statusMaterial = new StatusMaterialDTO();
            statusMaterial.setDescricao(result.getString(result.getColumnIndex("descricao")));
            statusMaterial.setIdStatusMaterial(result.getInt(result.getColumnIndex("id_status_material")));
            statusMateriais.add(statusMaterial);
        }
        result.close();
        return statusMateriais;
    }

    /**
     * Consulta os tipos de material cadastrados no banco de dados
     * @return Lista de tipos de material
     */
    public List<TipoMaterialDTO> getTiposMaterial() {
        String sql = "SELECT * " +
                "FROM tipo_material";
        Cursor result = readableDatabase.rawQuery(sql, new String[] {});
        List<TipoMaterialDTO> tiposMaterial = new ArrayList<>();
        while (result.moveToNext()){
            TipoMaterialDTO tipoMaterial = new TipoMaterialDTO();
            tipoMaterial.setDescricao(result.getString(result.getColumnIndex("descricao")));
            tipoMaterial.setIdTipoMaterial(result.getInt(result.getColumnIndex("id_tipo_material")));
            tiposMaterial.add(tipoMaterial);
        }
        result.close();
        return tiposMaterial;
    }

    /**
     * Inserir o empréstimo, no banco de dados
     * @param emprestimo Emprestimo a ser inserido
     * @param ativo Indica se o empréstimo está ativo, inativo
     */
    public void insertEmprestimo(EmprestimoDTO emprestimo, Boolean ativo){
        //insertBiblioteca(emprestimo.getBiblioteca());
        String sql = "REPLACE INTO emprestimo (" +
                "id_emprestimo, " +
                "autor, " +
                "ativo, " +
                "codigo_barras, " +
                "cpf_cnpj_usuario, " +
                "data_devolucao, " +
                "data_emprestimo, " +
                "data_renovacao, " +
                "id_biblioteca, " +
                "id_material_informacional, " +
                "numero_chamada, " +
                "prazo, " +
                "tipo_emprestimo, " +
                "titulo " +
                ") " +
                "VALUES (" +
                emprestimo.getIdEmprestimo() + ", " +
                "'" + emprestimo.getAutor() + "', " +
                "'" + ativo.toString() + "', " +
                "'" + emprestimo.getCodigoBarras() + "', " +
                emprestimo.getCpfCnpjUsuario() + ", " +
                emprestimo.getDataDevolucao() + ", " +
                emprestimo.getDataEmpretimo() + ", " +
                emprestimo.getDataRenovacao() + ", " +
                emprestimo.getIdBiblioteca() + ", " +
                emprestimo.getIdMaterialInformacional() + ", " +
                "'" + emprestimo.getNumeroChamada() + "', " +
                emprestimo.getPrazo() + ", " +
                "'" + emprestimo.getTipoEmprestimo() + "', " +
                "'" + emprestimo.getTitulo() + "' " +
                ")";

        writableDatabase.execSQL(sql);
    }

    /**
     * Inserir um titulo do acervo no banco de dados
     * @param acervoObj objeto Json representndo um título
     */
    public void insertAcervo (JSONObject acervoObj) throws JSONException {
        String sql = "INSERT INTO acervo (" +
                "ano, " +
                "autor, " +
                "descricao_fisica, " +
                "edicao, " +
                "editora, " +
                "endereco_eletronico, " +
                "id_biblioteca, " +
                "id_tipo_material, " +
                "intervalo_paginas, " +
                "isbn TEXT, " +
                "issn TEXT, " +
                "local_publicacao, " +
                "nota_conteudo, " +
                "notas_gerais, " +
                "notas_locais, " +
                "numero_chamada, " +
                "quantidade, " +
                "registro_sistema, " +
                "resumo, " +
                "serie, " +
                "sub_titulo, " +
                "tipo_material" +
                "titulo, " +
                ") " +
                "VALUES (" +
                acervoObj.getString("ano") + ", " +
                acervoObj.getString("autor") + "', " +
                acervoObj.getString("descricao-fisica") + "', " +
                acervoObj.getString("edicao") + "', " +
                acervoObj.getString("editora") + "', " +
                acervoObj.getString("endereco-eletronico") + "', " +
                acervoObj.getInt("id-biblioteca") + "', " +
                acervoObj.getInt("id-tipo-material") + "', " +
                acervoObj.getString("intervalo-paginas") + "', " +
                acervoObj.getString("isbn") + "', " +
                acervoObj.getString("issn") + "', " +
                acervoObj.getString("local-publicacao") + "', " +
                acervoObj.getString("nota-conteudo") + "', " +
                acervoObj.getString("notas-gerais") + "', " +
                acervoObj.getString("notas-locais") + "', " +
                acervoObj.getString("numero-chamada") + "', " +
                acervoObj.getInt("quantidade") + "', " +
                acervoObj.getInt("registro-sistema") + "', " +
                acervoObj.getString("resumo") + "', " +
                acervoObj.getString("serie") + "', " +
                acervoObj.getString("sub-titulo") + "', " +
                acervoObj.getString("tipo-material") + "', " +
                acervoObj.getString("titulo") + "', " +
                ")";
        writableDatabase.execSQL(sql);
    }

    /**
     * Inserir um autor secundário para titulo do acervo no banco de dados
     * @param autor autor secundário
     * @param registroSist chave do título ao qual o autor é relacionado
     */
    public void insertAutorSecundario (String autor, int registroSist) {
        String sql = "INSERT INTO acervo_autor_secundario (" +
                "autor, " +
                "id_acervo, " +
                ") " +
                "VALUES (" +
                autor + ", " +
                registroSist + "', " +
                ")";
        writableDatabase.execSQL(sql);
    }

    /**
     * Inserir um autor secundário para titulo do acervo no banco de dados
     * @param assunto Assunto relacionado ao título
     * @param registroSist chave do título ao qual o autor é relacionado
     */
    public void insertAssunto (String assunto, int registroSist) {
        String sql = "INSERT INTO acervo_assunto (" +
                "autor, " +
                "id_acervo, " +
                ") " +
                "VALUES (" +
                assunto + ", " +
                registroSist + "', " +
                ")";
        writableDatabase.execSQL(sql);
    }

    public int insertAcervoJsonList(String acervosJson) throws JSONException, JsonStringInvalidaException {

        int listSize = 0;

        if (!acervosJson.equalsIgnoreCase("")) {
            JSONArray array = new JSONArray(acervosJson);
            listSize = array.length();

            for (int i = 0; i < array.length(); ++i) {
                JSONObject jsonObject = array.getJSONObject(i);

                // Inicia uma TRANSACTION
                writableDatabase.beginTransaction();
                try {
                    // insere o título
                    insertAcervo(jsonObject);
                    int regSistema = jsonObject.getInt("registro-sistema");

                    // insere os assuntos
                    String[] assuntosArray = (String[])jsonObject.get("assunto");
                    if (assuntosArray != null)
                        for (String assunto: assuntosArray) {
                            if (!assunto.equalsIgnoreCase("")){
                                insertAssunto(assunto, regSistema);
                            }
                        }
                    // insere os autores secundários
                    String[] autorSecArray = (String[])jsonObject.get("autores-secundarios");
                    if (autorSecArray != null)
                    for (String autor: autorSecArray) {
                        if (!autor.equalsIgnoreCase("")){
                            insertAutorSecundario(autor, regSistema);
                        }
                    }
                    writableDatabase.setTransactionSuccessful();
                } catch (Exception e) {
                    throw e;
                // fecha a TRANSACTION
                } finally {
                    writableDatabase.endTransaction();
                }
            }
        }

        return listSize;
    }
    /**
     * Inserir a bilbioteca no banco de dados
     * @param biblioteca Bilioteca a ser inserida
     */
    public void insertBiblioteca(BibliotecaDTO biblioteca){
        String sql = "REPLACE INTO biblioteca (" +
                "id_biblioteca, " +
                "descricao, " +
                "email, " +
                "telefone, " +
                "sigla, " +
                "site " +
                ") " +
                "VALUES (" +
                biblioteca.getIdBiblioteca() + ", " +
                "'" + biblioteca.getDescricao() + "', " +
                "'" + biblioteca.getEmail() + "', " +
                "'" + biblioteca.getTelefone() + "', " +
                "'" + biblioteca.getSigla() + "', " +
                "'" + biblioteca.getSite() + "' " +
                ")";

        writableDatabase.execSQL(sql);
    }

    /**
     * Inserir a situacao de material no banco de dados
     * @param situacaoMaterial Situacao de material a ser inserida
     */
    public void insertSituacaoMaterial(SituacaoMaterialDTO situacaoMaterial){
        String sql = "REPLACE INTO situacao_material (" +
                "descricao, " +
                "id_situacao_material" +
                ") " +
                "VALUES (" +
                "'" + situacaoMaterial.getDescricao() + "', " +
                situacaoMaterial.getIdSituacaoMaterial() +
                ")";

        writableDatabase.execSQL(sql);
    }

    /**
     * Inserir o status de material no banco de dados
     * @param statusMaterial Status de material a ser inserido
     */
    public void insertStatusMaterial(StatusMaterialDTO statusMaterial){
        String sql = "REPLACE INTO status_material (" +
                "descricao, " +
                "id_status_material" +
                ") " +
                "VALUES (" +
                "'" + statusMaterial.getDescricao() + "', " +
                statusMaterial.getIdStatusMaterial() +
                ")";

        writableDatabase.execSQL(sql);
    }

    /**
     * Inserir a situacao de material no banco de dados
     * @param tipoMaterial Situacao de material a ser inserida
     */
    public void insertTipoMaterial(TipoMaterialDTO tipoMaterial){
        String sql = "REPLACE INTO tipo_material (" +
                "descricao, " +
                "id_tipo_material" +
                ") " +
                "VALUES (" +
                "'" + tipoMaterial.getDescricao() + "', " +
                tipoMaterial.getIdTipoMaterial() +
                ")";

        writableDatabase.execSQL(sql);
    }
}

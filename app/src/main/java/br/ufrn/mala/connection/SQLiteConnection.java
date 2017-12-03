package br.ufrn.mala.connection;

import android.content.ContentValues;
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
     * Consulta as bibliotecas retornadas na busca
     * @return Lista de bibliotecas com livros resultantes da busca.
     */
    public List<BibliotecaDTO> getBibliotecasAcervo() {
        String sql = "SELECT * " +
                "FROM biblioteca " +
                "JOIN (SELECT DISTINCT id_biblioteca " +
                "      FROM acervo)" +
                "      USING(id_biblioteca)";
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

    public List<AcervoDTO> getAcervo() {
        String sql = "SELECT * " +
                "FROM acervo";
        Cursor result = readableDatabase.rawQuery(sql, new String[]{});
        List<AcervoDTO> acervo = new ArrayList<>();
        while (result.moveToNext()){
            AcervoDTO tituloAcervo = new AcervoDTO();
            tituloAcervo.setIdAcervo(result.getInt(result.getColumnIndex("id_acervo")));
            tituloAcervo.setAno(result.getString(result.getColumnIndex("ano")));
            tituloAcervo.setAutor(result.getString(result.getColumnIndex("autor")));
            tituloAcervo.setDescricaoFisica(result.getString(result.getColumnIndex("descricao_fisica")));
            tituloAcervo.setEdicao(result.getString(result.getColumnIndex("edicao")));
            tituloAcervo.setEditora(result.getString(result.getColumnIndex("editora")));
            tituloAcervo.setEnderecoEletronico(result.getString(result.getColumnIndex("endereco_eletronico")));
            tituloAcervo.setIdBiblioteca(result.getInt(result.getColumnIndex("id_biblioteca")));
            tituloAcervo.setIdTipoMaterial(result.getInt(result.getColumnIndex("id_tipo_material")));
            tituloAcervo.setIntervaloPaginas(result.getString(result.getColumnIndex("intervalo_paginas")));
            tituloAcervo.setIsbn(result.getString(result.getColumnIndex("isbn")));
            tituloAcervo.setIssn(result.getString(result.getColumnIndex("issn")));
            tituloAcervo.setLocalPublicacao(result.getString(result.getColumnIndex("local_publicacao")));
            tituloAcervo.setNotaConteudo(result.getString(result.getColumnIndex("nota_conteudo")));
            tituloAcervo.setNotasGerais(result.getString(result.getColumnIndex("notas_gerais")));
            tituloAcervo.setNotasLocais(result.getString(result.getColumnIndex("notas_locais")));
            tituloAcervo.setNumeroChamada(result.getString(result.getColumnIndex("numero_chamada")));
            tituloAcervo.setQuantidade(result.getInt(result.getColumnIndex("quantidade")));
            tituloAcervo.setRegistroSistema(result.getInt(result.getColumnIndex("registro_sistema")));
            tituloAcervo.setResumo(result.getString(result.getColumnIndex("resumo")));
            tituloAcervo.setSerie(result.getString(result.getColumnIndex("serie")));
            tituloAcervo.setSubTitulo(result.getString(result.getColumnIndex("sub_titulo")));
            tituloAcervo.setTipoMaterial(result.getString(result.getColumnIndex("tipo_material")));
            tituloAcervo.setTitulo(result.getString(result.getColumnIndex("titulo")));
            acervo.add(tituloAcervo);
        }
        result.close();
        return acervo;
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
    public void insertAcervo (JSONObject acervoObj, int idAcervo) throws JSONException {

        ContentValues values = new ContentValues();
        long retvalue = 0;
        values.put("id_acervo", idAcervo);
        values.put("ano", acervoObj.optString("ano"));
        values.put("autor", acervoObj.optString("autor"));
        values.put("descricao_fisica", acervoObj.optString("descricao-fisica"));
        values.put("edicao", acervoObj.optString("edicao"));
        values.put("editora", acervoObj.optString("editora"));
        values.put("endereco_eletronico", acervoObj.optString("endereco-eletronico"));
        values.put("id_biblioteca", acervoObj.optInt("id-biblioteca"));
        values.put("id_tipo_material", acervoObj.optInt("id-tipo-material"));
        values.put("intervalo_paginas", acervoObj.optString("intervalo-paginas"));
        values.put("isbn", acervoObj.optString("isbn"));
        values.put("issn", acervoObj.optString("issn"));
        values.put("local_publicacao", acervoObj.optString("local-publicacao"));
        values.put("nota_conteudo", acervoObj.optString("nota-conteudo"));
        values.put("notas_gerais", acervoObj.optString("notas-gerais"));
        values.put("notas_locais", acervoObj.optString("notas-locais"));
        values.put("numero_chamada", acervoObj.optString("numero-chamada"));
        values.put("quantidade", acervoObj.optInt("quantidade"));
        values.put("registro_sistema", acervoObj.optInt("registro-sistema"));
        values.put("resumo", acervoObj.optString("resumo"));
        values.put("serie", acervoObj.optString("serie"));
        values.put("sub_titulo", acervoObj.optString("sub-titulo"));
        values.put("tipo_material", acervoObj.optString("tipo-material"));
        values.put("titulo", acervoObj.optString("titulo"));

        retvalue = writableDatabase.insertWithOnConflict("acervo", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Inserir um autor secundário para titulo do acervo no banco de dados
     * @param autor autor secundário
     * @param idAcervo chave do título ao qual o autor é relacionado
     */
    public void insertAutorSecundario (String autor, int idAcervo) {
        String sql = "INSERT INTO acervo_autor_secundario (" +
                "autor, " +
                "id_acervo" +
                ") " +
                "VALUES (" +
                "'" + autor + "', " +
                idAcervo +
                ")";
        writableDatabase.execSQL(sql);
    }

    /**
     * Inserir um autor secundário para titulo do acervo no banco de dados
     * @param assunto Assunto relacionado ao título
     * @param idAcervo chave do título ao qual o autor é relacionado
     */
    public void insertAssunto (String assunto, int idAcervo) {
        String sql = "INSERT INTO acervo_assunto (" +
                "assunto, " +
                "id_acervo" +
                ") " +
                "VALUES (" +
                "'" + assunto + "', " +
                idAcervo +
                ")";
        writableDatabase.execSQL(sql);
    }

    public int insertAcervoJsonList(String acervosJson, int offset) throws JSONException, JsonStringInvalidaException {

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
                    insertAcervo(jsonObject, offset + i);

                    // insere os assuntos
                    if (jsonObject.has("assunto")){
                        JSONArray assuntosArray = jsonObject.optJSONArray("assunto");
                        if (assuntosArray != null) {
                            for (int j = 0; j < array.length(); ++j) {
                                JSONObject assuntoObject = array.getJSONObject(j);
                                String assunto = (assuntoObject.keys()).next();
                                if (!assunto.equalsIgnoreCase("")) {
                                    insertAssunto(assunto, offset + i);
                                }
                            }
                        }
                    }
                    // insere os autores secundários
                    if (jsonObject.has("autores-secundarios")) {
                        JSONArray autorSecArray = jsonObject.optJSONArray("autores-secundarios");
                        if (autorSecArray != null) {
                            for (int j = 0; j < array.length(); ++j) {
                                JSONObject AutorSecObject = array.getJSONObject(j);
                                String autor = (AutorSecObject.keys()).next();
                                if (!autor.equalsIgnoreCase("")) {
                                    insertAutorSecundario(autor, offset + i);
                                }
                            }
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

    public void limparAcervoBD() {
        writableDatabase.delete("acervo", null, null);
        writableDatabase.delete("acervo_autor_secundario", null, null);
        writableDatabase.delete("acervo_assunto", null, null);
    }
}

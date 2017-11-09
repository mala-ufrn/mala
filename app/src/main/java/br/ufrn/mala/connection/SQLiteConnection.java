package br.ufrn.mala.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.mala.dto.BibliotecaDTO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.UsuarioDTO;
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
            emprestimo.setDataDevolucao(result.getLong(result.getColumnIndex("data_devolucao")));
            emprestimo.setDataEmpretimo(result.getLong(result.getColumnIndex("data_emprestimo")));
            emprestimo.setDataRenovacao(result.getLong(result.getColumnIndex("data_renovacao")));
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
        return emprestimos;
    }

    /**
     * Inserir o empréstimo, no banco de dados
     * @param emprestimo Emprestimo a ser inserido
     * @param ativo Indica se o empréstimo está ativo, inativo
     * @return Quantidade de empréstimos
     */
    public void insertEmprestimo(EmprestimoDTO emprestimo, Boolean ativo){
        insertBiblioteca(emprestimo.getBiblioteca());
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
     * Inserir a bilbioteca no banco de dados
     * @param biblioteca Bilioteca a ser inserida
     * @return Quantidade de empréstimos
     */
    private void insertBiblioteca(BibliotecaDTO biblioteca){
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
}

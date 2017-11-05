package br.ufrn.mala.connection;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;

/**
 * Created by Joel Felipe on 04/11/2017.
 */

public class SQLiteFactoryDAO implements AbstractFactoryDAO {

    private static SQLiteFactoryDAO sqLiteFactoryDAO;
    private SQLiteConnection sqLiteConnection;

    public static SQLiteFactoryDAO getInstance(Context context){
        if(sqLiteFactoryDAO == null)
            sqLiteFactoryDAO = new SQLiteFactoryDAO(context);
        return sqLiteFactoryDAO;
    }

    private SQLiteFactoryDAO(Context context) {
        sqLiteConnection = SQLiteConnection.getInstance(context);
    }

    @Override
    public UsuarioDTO getUsuarioLogado(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return sqLiteConnection.getUsuarioLogado();
    }

    @Override
    public Integer getQuantidadeHistoricoEmprestimos(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return sqLiteConnection.getQuantidadeEmprestimos(false);
    }

    @Override
    public List<EmprestimoDTO> getHistoricoEmprestimos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        return sqLiteConnection.getEmprestimos(false, offset);
    }

    @Override
    public List<EmprestimoDTO> getEmprestimosAtivos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        return sqLiteConnection.getEmprestimos(true, offset);
    }

    public void insertEmprestimos(List<EmprestimoDTO> emprestimos, Boolean ativo){
        for (EmprestimoDTO emprestimo: emprestimos)
            sqLiteConnection.insertEmprestimo(emprestimo, ativo);
    }
}

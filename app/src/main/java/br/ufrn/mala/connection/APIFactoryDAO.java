package br.ufrn.mala.connection;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import br.ufrn.mala.dto.BibliotecaDTO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;

/**
 * Created by Joel Felipe on 04/11/2017.
 */

public class APIFactoryDAO implements AbstractFactoryDAO{
    private static APIFactoryDAO apiFactoryDAO;
    private static SQLiteFactoryDAO sqLiteFactoryDAO;
    private APIConnection apiConnection;

    public static APIFactoryDAO getInstance(Context context){
        if(apiFactoryDAO == null)
            apiFactoryDAO = new APIFactoryDAO(context);
        return apiFactoryDAO;
    }

    private APIFactoryDAO(Context context) {
        apiConnection = APIConnection.getInstance(context);
        sqLiteFactoryDAO = SQLiteFactoryDAO.getInstance(context);
    }

    @Override
    public UsuarioDTO getUsuarioLogado(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        String usuarioLogado = apiConnection.getUsuarioLogado(token);
        return JsonToObject.toUsuario(usuarioLogado);
    }

    @Override
    public Integer getQuantidadeHistoricoEmprestimos(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return apiConnection.getQuantidadeEmprestimos(token, false);
    }

    @Override
    public List<EmprestimoDTO> getHistoricoEmprestimos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        String emprestimosJson = apiConnection.getEmprestimos(token, false, offset);
        List<EmprestimoDTO> historicoEmprestimos = JsonToObject.toEmprestimos(emprestimosJson);
        for (EmprestimoDTO emprestimo: historicoEmprestimos)
            emprestimo.setBiblioteca(getBibliotecaById(token, emprestimo.getIdBiblioteca()));
        sqLiteFactoryDAO.insertEmprestimos(historicoEmprestimos, false);
        return historicoEmprestimos;
    }

    @Override
    public List<EmprestimoDTO> getEmprestimosAtivos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        String emprestimosJson = apiConnection.getEmprestimos(token, true, offset);
        List<EmprestimoDTO> emprestimosAtivos = JsonToObject.toEmprestimos(emprestimosJson);
        for (EmprestimoDTO emprestimo: emprestimosAtivos)
            emprestimo.setBiblioteca(getBibliotecaById(token, emprestimo.getIdBiblioteca()));
        sqLiteFactoryDAO.insertEmprestimos(emprestimosAtivos, true);
        return emprestimosAtivos;
    }

    private BibliotecaDTO getBibliotecaById(String token, Integer idBiblioteca) throws IOException, JsonStringInvalidaException, ConnectionException {
        String biblioteca = apiConnection.getBiblioteca(token, idBiblioteca);
        return JsonToObject.toBiblioteca(biblioteca);
    }
}

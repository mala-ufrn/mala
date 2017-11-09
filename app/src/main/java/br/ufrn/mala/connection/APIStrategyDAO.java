package br.ufrn.mala.connection;

import android.content.Context;
import android.util.SparseArray;

import java.io.IOException;
import java.util.List;

import br.ufrn.mala.dto.BibliotecaDTO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;

/**
 * Estratégia concreta <i>(ConcreteStrategy)</i> de acesso aos dados <i>(DAO)</i> por meio da consulta na API da UFRN.
 *
 * @author Joel Felipe
 * @since 04/11/2017
 * @see <a href="https://pt.wikipedia.org/wiki/Strategy">Strategy</a>
 * @see <a href="https://pt.wikipedia.org/wiki/Objeto_de_acesso_a_dados">DAO</a>
 * @see <a href="https://pt.wikipedia.org/wiki/Singleton">Singleton</a>
 */

public class APIStrategyDAO implements StrategyDAO {
    private static APIStrategyDAO apiStrategyDAO;
    private static SQLiteStrategyDAO sqLiteStrategyDAO;
    private APIConnection apiConnection;

    public static APIStrategyDAO getInstance(Context context){
        if(apiStrategyDAO == null)
            apiStrategyDAO = new APIStrategyDAO(context);
        return apiStrategyDAO;
    }

    private APIStrategyDAO(Context context) {
        apiConnection = APIConnection.getInstance(context);
        sqLiteStrategyDAO = SQLiteStrategyDAO.getInstance(context);
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
        SparseArray<BibliotecaDTO> bibliotecas = new SparseArray<>();
        for (BibliotecaDTO biblioteca: getBibliotecas(token))
            bibliotecas.put(biblioteca.getIdBiblioteca(), biblioteca);
        String emprestimosJson = apiConnection.getEmprestimos(token, false, offset);
        List<EmprestimoDTO> historicoEmprestimos = JsonToObject.toEmprestimos(emprestimosJson);
        for (EmprestimoDTO emprestimo: historicoEmprestimos)
            emprestimo.setBiblioteca(bibliotecas.get(emprestimo.getIdBiblioteca()));
        sqLiteStrategyDAO.insertEmprestimos(historicoEmprestimos, false);
        return historicoEmprestimos;
    }

    @Override
    public List<EmprestimoDTO> getEmprestimosAtivos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        SparseArray<BibliotecaDTO> bibliotecas = new SparseArray<>();
        for (BibliotecaDTO biblioteca: getBibliotecas(token))
            bibliotecas.put(biblioteca.getIdBiblioteca(), biblioteca);
        String emprestimosJson = apiConnection.getEmprestimos(token, true, offset);
        List<EmprestimoDTO> emprestimosAtivos = JsonToObject.toEmprestimos(emprestimosJson);
        for (EmprestimoDTO emprestimo: emprestimosAtivos)
            emprestimo.setBiblioteca(bibliotecas.get(emprestimo.getIdBiblioteca()));
        sqLiteStrategyDAO.insertEmprestimos(emprestimosAtivos, true);
        return emprestimosAtivos;
    }

    private List<BibliotecaDTO> getBibliotecas(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        String biblioteca = apiConnection.getBibliotecas(token);
        return JsonToObject.toBibliotecas(biblioteca);
    }
}

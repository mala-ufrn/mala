package br.ufrn.mala.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.util.List;

import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;

/**
 * @author Joel Felipe
 *
 * Fachada <i>(Façade)</i> de acesso aos dados <i>(DAO)</i> necessários para o funcionamento da aplicação
 *
 * Implementa os seguintes padrões de projeto
 * @see <a href="https://pt.wikipedia.org/wiki/Fa%C3%A7ade">Façade</a>
 * @see <a href="https://pt.wikipedia.org/wiki/Objeto_de_acesso_a_dados">DAO</a>
 * @see <a href="https://pt.wikipedia.org/wiki/Singleton">Singleton</a>
 */

public class FacadeDAO {

    private StrategyDAO strategyDAO;
    private boolean connected;

    public static FacadeDAO getInstance(Context context){
        return new FacadeDAO(context);
    }

    private FacadeDAO(Context context) {
        connected = isOnline(context);
        if (connected)
            strategyDAO = APIStrategyDAO.getInstance(context);
        else
            strategyDAO = SQLiteStrategyDAO.getInstance(context);
    }

    /**
     * Consultar o usuário logado na aplicação
     * @param token Token de acesso à API da SINFO
     * @return Usuário logado na aplicação
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public UsuarioDTO getUsuarioLogado(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return strategyDAO.getUsuarioLogado(token);
    }

    /**
     * Consultar a quantidade de empréstimos do histórico de empréstimos do usuário logado
     * @param token Token de acesso à API da UFRN
     * @return Quantidade de empréstimos
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public Integer getQuantidadeHistoricoEmprestimos(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return strategyDAO.getQuantidadeHistoricoEmprestimos(token);
    }

    /**
     * Consultar o histórico de empréstimos do usuário logado
     * @param token Token de acesso à API da UFRN
     * @param offset Offset usado na consulta
     * @return Historico de empréstimos
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public List<EmprestimoDTO> getHistoricoEmprestimos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        return strategyDAO.getHistoricoEmprestimos(token, offset);
    }

    /**
     * Consultar os empréstimos ativos do usuário logado
     * @param token Token de acesso à API da UFRN
     * @param offset Offset usado na consulta
     * @return Empréstimos ativos
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public List<EmprestimoDTO> getEmprestimosAtivos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        return strategyDAO.getEmprestimosAtivos(token, offset);
    }

    /**
     * Carrega as bibliotecas cadastradas na SINFO para o banco SQLite
     * @param token Token de acesso à API da UFRN
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadBibliotecas(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && ((APIStrategyDAO)strategyDAO).loadBibliotecas(token);
    }

    /**
     * Carrega as situações de materiais informacionais cadastradas na SINFO para o banco SQLite
     * @param token Token de acesso à API da UFRN
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadSituacoesMaterial(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && ((APIStrategyDAO)strategyDAO).loadSituacoesMaterial(token);
    }

    /**
     * Carrega os status de materiais informacionais cadastrados na SINFO para o banco SQLite
     * @param token Token de acesso à API da UFRN
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadStatusMaterial(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && ((APIStrategyDAO)strategyDAO).loadStatusMaterial(token);
    }

    /**
     * Carrega os tipos de materiais informacionais cadastrados na SINFO para o banco SQLite
     * @param token Token de acesso à API da UFRN
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadTiposMaterial(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && ((APIStrategyDAO)strategyDAO).loadTiposMaterial(token);
    }

    /**
     * Verifica se o aparelho possui conexão com a internet
     * @param context
     * @return
     */
    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

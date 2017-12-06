package br.ufrn.mala.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import br.ufrn.mala.dto.AcervoDTO;
import br.ufrn.mala.dto.BibliotecaDTO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.MaterialInformacionalDTO;
import br.ufrn.mala.dto.TipoMaterialDTO;
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
    private Context context;
    private boolean connected;

    public static FacadeDAO getInstance(Context context){
        return new FacadeDAO(context);
    }

    private FacadeDAO(Context context) {
        connected = isOnline(context);
        this.context = context;
        if (connected)
            strategyDAO = APIStrategyDAO.getInstance(context);
        else
            strategyDAO = SQLiteStrategyDAO.getInstance(context);
    }

    /**
     * Consultar o usuário logado na aplicação
     * @return Usuário logado na aplicação
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public UsuarioDTO getUsuarioLogado() throws IOException, JsonStringInvalidaException, ConnectionException {
        return strategyDAO.getUsuarioLogado();
    }

    /**
     * Consultar a quantidade de empréstimos do histórico de empréstimos do usuário logado
     * @return Quantidade de empréstimos
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public Integer getQuantidadeHistoricoEmprestimos() throws IOException, JsonStringInvalidaException, ConnectionException {
        return strategyDAO.getQuantidadeHistoricoEmprestimos();
    }

    /**
     * Consultar o histórico de empréstimos do usuário logado
     * @param offset Offset usado na consulta
     * @return Historico de empréstimos
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public List<EmprestimoDTO> getHistoricoEmprestimos(Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        return strategyDAO.getHistoricoEmprestimos(offset);
    }

    /**
     * Consultar as bibliotecas disponíveis no banco
     * @return Lista de bibliotecas
     */
    public List<BibliotecaDTO> getBibliotecas(boolean toSearch) {
        return SQLiteStrategyDAO.getInstance(context).getBibliotecas(toSearch);
    }

    /**
     * Consultar os tipos de material disponíveis no banco
     * @return Lista de tipos de empréstimo
     */
    public List<TipoMaterialDTO> getTiposMaterial() {
        return SQLiteStrategyDAO.getInstance(context).getTiposMaterial();
    }

    /**
     * Consultar o Material Informacional pelo código de barras, na API
     * @param codBarras Código de Barras
     * @return Empréstimos ativos
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public MaterialInformacionalDTO getMaterialInformacional(String codBarras) throws IOException, JsonStringInvalidaException, ConnectionException {
        if (connected) {
            return ((APIStrategyDAO)strategyDAO).getMaterialInformacional(codBarras);
        }
        else {
            return null;
        }
    }

    /**
     * Consultar os empréstimos ativos do usuário logado
     * @param offset Offset usado na consulta
     * @return Empréstimos ativos
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public List<EmprestimoDTO> getEmprestimosAtivos(Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        System.out.println(strategyDAO.getEmprestimosAtivos(offset));
        return SQLiteConnection.getInstance(context).getEmprestimos(true, offset);
    }

    /**
     * Consultar as bibliotecas distintas resultantes da busca
     * @return Lista de bibliotecas da Busca
     */
    public List<BibliotecaDTO> getBibliotecasAcervo() {
        return SQLiteStrategyDAO.getInstance(context).getBibliotecasAcervo();
    }

    /**
     * Consultar os títulos resultantes da busca
     * @return Lista de Títulos da Busca
     */
    public List<AcervoDTO> getAcervo(String orderBy) {
        return SQLiteStrategyDAO.getInstance(context).getAcervo(orderBy);
    }

    public String[] getAutoresSec(int idAcervo) {
        return SQLiteStrategyDAO.getInstance(context).getAutoresSec(idAcervo);
    }

    public String[] getAssuntos(int idAcervo) {
        return SQLiteStrategyDAO.getInstance(context).getAssuntos(idAcervo);
    }

    public BibliotecaDTO getBiblioteca(int idBibl) {
        return SQLiteStrategyDAO.getInstance(context).getBiblioteca(idBibl);
    }

    /**
     * Consultar os empréstimos ativos do usuário logado
     * @param token Token de acesso à API da UFRN
     * @param titulo Título para a consulta
     * @param autor autor para a consulta
     * @param assunto assunto para a consulta
     * @param idBiblioteca biblioteca desejada para a consulta
     * @param idTipoMaterial tipo de material desejado na consulta
     * @return Lista de Títulos consultados no Acervo
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public int buscarAcervo(String token, String titulo, String autor, String assunto,
                                     String idBiblioteca, String idTipoMaterial, int offset) throws IOException, JsonStringInvalidaException, ConnectionException, JSONException {
        if (connected) {
            return ((APIStrategyDAO)strategyDAO).buscarAcervo(titulo, autor, assunto, idBiblioteca, idTipoMaterial, offset);
        }
        else {
            return -1;
        }
    }

    /**
     * Limpa as informações guardadas relativas à consulta no acervo
     */
    public void limparAcervoBD() {
        SQLiteStrategyDAO.getInstance(context).limparAcervoBD();
    }

    /**
     * Carrega as bibliotecas cadastradas na SINFO para o banco SQLite
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadBibliotecas() throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && ((APIStrategyDAO) strategyDAO).loadBibliotecas();
    }

    /**
     * Carrega as situações de materiais informacionais cadastradas na SINFO para o banco SQLite
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadSituacoesMaterial() throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && ((APIStrategyDAO)strategyDAO).loadSituacoesMaterial();
    }

    /**
     * Carrega os status de materiais informacionais cadastrados na SINFO para o banco SQLite
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadStatusMaterial() throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && ((APIStrategyDAO)strategyDAO).loadStatusMaterial();
    }

    /**
     * Carrega os tipos de materiais informacionais cadastrados na SINFO para o banco SQLite
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadTiposMaterial() throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && ((APIStrategyDAO)strategyDAO).loadTiposMaterial();
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

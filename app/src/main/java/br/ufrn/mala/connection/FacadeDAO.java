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

    private static FacadeDAO facadeDAO;
    private StrategyDAO strategyDAO;
    private SQLiteStrategyDAO sqLiteStrategyDAO;
    private APIStrategyDAO apiStrategyDAO;
    private boolean connected;

    public static FacadeDAO getInstance(Context context){
        if (facadeDAO == null){
            facadeDAO = new FacadeDAO(context);
        }
        facadeDAO.updateStrategy(context);
        return facadeDAO;
    }

    private FacadeDAO(Context context) {
        sqLiteStrategyDAO = SQLiteStrategyDAO.getInstance(context);
        apiStrategyDAO = APIStrategyDAO.getInstance(context);
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
        return sqLiteStrategyDAO.getBibliotecas(toSearch);
    }

    /**
     * Consultar os tipos de material disponíveis no banco
     * @return Lista de tipos de empréstimo
     */
    public List<TipoMaterialDTO> getTiposMaterial() {
        return sqLiteStrategyDAO.getTiposMaterial();
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
            return apiStrategyDAO.getMaterialInformacional(codBarras);
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
        strategyDAO.getEmprestimosAtivos(offset);
        return sqLiteStrategyDAO.getEmprestimosAtivos(offset);
    }

    /**
     * Consultar as bibliotecas distintas resultantes da busca
     * @return Lista de bibliotecas da Busca
     */
    public List<BibliotecaDTO> getBibliotecasAcervo() {
        return sqLiteStrategyDAO.getBibliotecasAcervo();
    }

    /**
     * Consultar os títulos resultantes da busca
     * @return Lista de Títulos da Busca
     */
    public List<AcervoDTO> getAcervo(String orderBy) {
        return sqLiteStrategyDAO.getAcervo(orderBy);
    }

    public String[] getAutoresSec(int idAcervo) {
        return sqLiteStrategyDAO.getAutoresSec(idAcervo);
    }

    public String[] getAssuntos(int idAcervo) {
        return sqLiteStrategyDAO.getAssuntos(idAcervo);
    }

    public BibliotecaDTO getBiblioteca(int idBibl) {
        return sqLiteStrategyDAO.getBiblioteca(idBibl);
    }

    /**
     * Consultar os empréstimos ativos do usuário logado
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
    public int buscarAcervo(String titulo, String autor, String assunto,
                                     String idBiblioteca, String idTipoMaterial, int offset) throws IOException, JsonStringInvalidaException, ConnectionException, JSONException {
        if (connected) {
            return apiStrategyDAO.buscarAcervo(titulo, autor, assunto, idBiblioteca, idTipoMaterial, offset);
        }
        else {
            return -1;
        }
    }

    /**
     * Limpa as informações guardadas relativas à consulta no acervo
     */
    public void limparAcervoBD() {
        sqLiteStrategyDAO.limparAcervoBD();
    }

    /**
     * Carrega as bibliotecas cadastradas na SINFO para o banco SQLite
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadBibliotecas() throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && apiStrategyDAO.loadBibliotecas();
    }

    /**
     * Carrega as situações de materiais informacionais cadastradas na SINFO para o banco SQLite
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadSituacoesMaterial() throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && apiStrategyDAO.loadSituacoesMaterial();
    }

    /**
     * Carrega os status de materiais informacionais cadastrados na SINFO para o banco SQLite
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadStatusMaterial() throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && apiStrategyDAO.loadStatusMaterial();
    }

    /**
     * Carrega os tipos de materiais informacionais cadastrados na SINFO para o banco SQLite
     * @return true se o load for correto
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public boolean loadTiposMaterial() throws IOException, JsonStringInvalidaException, ConnectionException {
        return connected && apiStrategyDAO.loadTiposMaterial();
    }

    /**
     * Verifica se o aparelho possui conexão com a internet
     * @param context
     * @return
     */
    private static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Verifica se o aparelho possui conexão com a internet
     * @param context
     * @return
     */
    private void updateStrategy(Context context) {
        connected = isOnline(context);
        if (isOnline(context))
            strategyDAO = apiStrategyDAO;
        else
            strategyDAO = sqLiteStrategyDAO;
    }
}

package br.ufrn.mala.connection;

import java.io.IOException;
import java.util.List;

import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;

/**
 * Inteface <i>(Strategy)</i> a ser implementada por uma estratégia concreta <i>(ConcreteStrategy)</i> de acesso aos dados <i>(DAO)</i>.
 *
 * <p>Implementado pelas classes: {@link APIStrategyDAO} e {@link SQLiteStrategyDAO}.
 *
 * @author Joel Felipe
 * @since 04/11/2017
 * @see <a href="https://pt.wikipedia.org/wiki/Strategy">Strategy</a>
 * @see <a href="https://pt.wikipedia.org/wiki/Objeto_de_acesso_a_dados">DAO</a>
 */

public interface StrategyDAO {

    /**
     * Consultar o usuário logado na aplicação
     * @param token Token de acesso à API da UFRN
     * @return Usuário logado na aplicação
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    UsuarioDTO getUsuarioLogado(String token) throws IOException, JsonStringInvalidaException, ConnectionException;

    /**
     * Consultar a quantidade de empréstimos do histórico de empréstimos do usuário logado
     * @param token Token de acesso à API da UFRN
     * @return Quantidade de empréstimos
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    Integer getQuantidadeHistoricoEmprestimos(String token) throws IOException, JsonStringInvalidaException, ConnectionException;

    /**
     * Consultar o histórico de empréstimos do usuário logado
     * @param token Token de acesso à API da UFRN
     * @param offset Offset usado na consulta
     * @return Historico de empréstimos
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    List<EmprestimoDTO> getHistoricoEmprestimos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException;

    /**
     * Consultar os empréstimos ativos do usuário logado
     * @param token Token de acesso à API da UFRN
     * @param offset Offset usado na consulta
     * @return Empréstimos ativos
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    List<EmprestimoDTO> getEmprestimosAtivos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException;

}

package br.ufrn.mala.connection;

import java.io.IOException;
import java.util.List;

import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;

/**
 * Created by Joel Felipe on 04/11/2017.
 */

public interface AbstractFactoryDAO {

    UsuarioDTO getUsuarioLogado(String token) throws IOException, JsonStringInvalidaException, ConnectionException;

    Integer getQuantidadeHistoricoEmprestimos(String token) throws IOException, JsonStringInvalidaException, ConnectionException;

    List<EmprestimoDTO> getHistoricoEmprestimos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException;

    List<EmprestimoDTO> getEmprestimosAtivos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException;

}

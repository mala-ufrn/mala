package br.ufrn.mala.connection;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;

/**
 * Created by Joel Felipe on 02/10/2017.
 */

public class FachadaAPI {

    private static FachadaAPI fachadaAPI;
    private APIConnection apiConnection;

    public static FachadaAPI getInstance(Context context){
        if(fachadaAPI == null)
            fachadaAPI = new FachadaAPI(context);
        return fachadaAPI;
    }

    private FachadaAPI(Context context) {
        apiConnection = APIConnection.getInstance(context);
    }

    public UsuarioDTO setUsuarioLogado(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return apiConnection.setUsuarioLogado(token);
    }

    public List<EmprestimoDTO> getHistoricoEmprestimos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        String emprestimosJson = apiConnection.getEmprestimos(token, false, offset);
        return JsonToObject.toEmprestimos(emprestimosJson);
    }

    public List<EmprestimoDTO> getEmprestimosAtivos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        String emprestimosJson = apiConnection.getEmprestimos(token, true, offset);
        return JsonToObject.toEmprestimos(emprestimosJson);
    }
}

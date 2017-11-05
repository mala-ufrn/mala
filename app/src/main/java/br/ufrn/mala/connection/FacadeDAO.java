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
 * Created by Joel Felipe on 02/10/2017.
 */

public class FacadeDAO {

    private AbstractFactoryDAO abstractFactoryDAO;

    public static FacadeDAO getInstance(Context context){
        return new FacadeDAO(context);
    }

    private FacadeDAO(Context context) {
        if (isOnline(context))
            abstractFactoryDAO = APIFactoryDAO.getInstance(context);
        else
            abstractFactoryDAO = SQLiteFactoryDAO.getInstance(context);
    }

    public UsuarioDTO getUsuarioLogado(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return abstractFactoryDAO.getUsuarioLogado(token);
    }

    public Integer getQuantidadeHistoricoEmprestimos(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return abstractFactoryDAO.getQuantidadeHistoricoEmprestimos(token);
    }

    public List<EmprestimoDTO> getHistoricoEmprestimos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        return abstractFactoryDAO.getHistoricoEmprestimos(token, offset);
    }

    public List<EmprestimoDTO> getEmprestimosAtivos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        return abstractFactoryDAO.getEmprestimosAtivos(token, offset);
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

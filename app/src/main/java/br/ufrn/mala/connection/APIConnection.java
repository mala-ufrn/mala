package br.ufrn.mala.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.gson.Gson;

import java.io.IOException;

import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;
import br.ufrn.mala.util.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Joel Felipe on 02/10/17.
 */

public class APIConnection {

    private static APIConnection apiConnection;
    private UsuarioDTO usuarioLogado;

    private final String URL_BASE = "https://apitestes.info.ufrn.br/";
    private final String API_KEY = "5HkQu4mrJG6sgeQCArhCaLQOyP81BpPZRaEnqy0n";

    private String PATH_USUARIO_INFO = "usuario/v0.1/usuarios/info";
    private String PATH_BIBLIOTECA = "biblioteca/v0.1/emprestimos";

    public static APIConnection getInstance(Context context){
        if(apiConnection == null)
            apiConnection = new APIConnection(context);
        return apiConnection;
    }

    private APIConnection(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.KEY_USER_INFO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String usuario = mPrefs.getString("UsuarioLogado", null);
        if (usuario != null)
            usuarioLogado = gson.fromJson(usuario, UsuarioDTO.class);
    }

    public UsuarioDTO setUsuarioLogado(String token) throws JsonStringInvalidaException, IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_USUARIO_INFO)
                .build()
                .toString();
        String usuario = getDados(token, url);
        usuarioLogado = JsonToObject.toUsuario(usuario);
        return usuarioLogado;
    }

    public String getEmprestimos(String token, Boolean ativo, Integer offset) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_BIBLIOTECA)
                .appendQueryParameter("id-usuario", usuarioLogado.getIdUsuario().toString())
                .appendQueryParameter("emprestado", ativo.toString())
                .appendQueryParameter("limit", Integer.toString(20))
                .appendQueryParameter("offset", offset.toString())
                .appendQueryParameter("order-desc", "data-emprestimo")
                .build()
                .toString();
        return getDados(token, url);
    }

    private String getDados(String token, String url) throws IOException, ConnectionException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("X-Api-Key", API_KEY)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        System.out.println(result);
        if (!response.isSuccessful())
            throw new ConnectionException("Erro ao se conectar com o servidor", new Throwable(response.message()));
        return result;
    }
}
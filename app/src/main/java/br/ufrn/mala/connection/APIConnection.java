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
    private static SQLiteConnection sqLiteConnection;
    private UsuarioDTO usuarioLogado;

    private final String URL_BASE = "https://apitestes.info.ufrn.br/";
    private final String API_KEY = "njLEaabCzQwBjh7D5rHtOrVlUu7OF5wh2JdjHsB2";

    private String PATH_USUARIO_INFO = "usuario/v0.1/usuarios/info";

    private String PATH_BIBLIOTECA = "biblioteca/v0.1";
    private String PATH_BIBLIOTECA_BIBLIOTECAS = PATH_BIBLIOTECA + "/bibliotecas";
    private String PATH_BIBLIOTECA_EMPRESTIMOS = PATH_BIBLIOTECA + "/emprestimos";

    public static APIConnection getInstance(Context context){
        if(apiConnection == null)
            apiConnection = new APIConnection(context);
        return apiConnection;
    }

    private APIConnection(Context context) {
        sqLiteConnection = SQLiteConnection.getInstance(context);

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
        sqLiteConnection.setUsuarioLogado(usuarioLogado);
        return usuarioLogado;
    }

    public String getUsuarioLogado(String token) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_USUARIO_INFO)
                .build()
                .toString();
        return getDados(token, url);
    }

    public String getBiblioteca(String token, Integer idBiblioteca) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_BIBLIOTECA_BIBLIOTECAS)
                .appendPath(idBiblioteca.toString())
                .build()
                .toString();
        return getDados(token, url);
    }

    public Integer getQuantidadeEmprestimos(String token, Boolean ativo) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_BIBLIOTECA_EMPRESTIMOS)
                .appendQueryParameter("cpf-cnpj-usuario", usuarioLogado.getCpfCnpj().toString())
                .appendQueryParameter("emprestado", ativo.toString())
                .build()
                .toString();
        return Integer.parseInt(getCabecalho(token, url));
    }

    public String getEmprestimos(String token, Boolean ativo, Integer offset) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_BIBLIOTECA_EMPRESTIMOS)
                .appendQueryParameter("cpf-cnpj-usuario", usuarioLogado.getCpfCnpj().toString())
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

    private String getCabecalho(String token, String url) throws IOException, ConnectionException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("X-Api-Key", API_KEY)
                .addHeader("paginado", "true")
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        String result = response.header("x-total");
        System.out.println(result);
        if (!response.isSuccessful())
            throw new ConnectionException("Erro ao se conectar com o servidor", new Throwable(response.message()));
        return result;
    }
}
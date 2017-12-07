package br.ufrn.mala.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

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
 * Conexão com a <a href="https://api.ufrn.br/">API de Sistemas da UFRN</a>
 *
 * @author Joel Felipe
 * @see <a href="https://pt.wikipedia.org/wiki/Singleton">Singleton</a>
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
    private String PATH_BIBLIOTECA_SITUACOES = PATH_BIBLIOTECA + "/sitacoes-materiais";
    private String PATH_BIBLIOTECA_STATUS = PATH_BIBLIOTECA + "/status-materiais";
    private String PATH_BIBLIOTECA_TIPOS_MATERIAL = PATH_BIBLIOTECA + "/tipos-materiais";
    private String PATH_BIBLIOTECA_EMPRESTIMOS = PATH_BIBLIOTECA + "/emprestimos";
    private String PATH_BIBLIOTECA_MATERIAIS = PATH_BIBLIOTECA + "/materiais-informacionais";
    private String PATH_BIBLIOTECA_ACERVOS = PATH_BIBLIOTECA + "/acervos";

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

    /**
     * Atualizar o usuário logado na aplicação
     * @param token Token de acesso à API da UFRN
     * @return Usuário logado na aplicação
     * @throws IOException
     * @throws JsonStringInvalidaException
     * @throws ConnectionException
     */
    public UsuarioDTO setUsuarioLogado(String token) throws JsonStringInvalidaException, IOException, ConnectionException {
        String usuario = getUsuarioLogado(token);
        usuarioLogado = JsonToObject.toUsuario(usuario);
        String url = Uri.parse("https://sigaa.ufrn.br/sigaa/verProducao")
                .buildUpon()
                .appendQueryParameter("idProducao", usuarioLogado.getIdFoto().toString())
                .appendQueryParameter("key", usuarioLogado.getChaveFoto())
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
        usuarioLogado.setFoto(bitmap);
        sqLiteConnection.setUsuarioLogado(usuarioLogado);
        return usuarioLogado;
    }

    /**
     * Consultar o usuário logado na aplicação
     * @param token Token de acesso à API da UFRN
     * @return JSON do usuário logado na aplicação
     * @throws IOException
     * @throws ConnectionException
     */
    public String getUsuarioLogado(String token) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_USUARIO_INFO)
                .build()
                .toString();

        return getDados(token, url);
    }

    /**
     * Consultar todas as bibliotecas da UFRN, na API da UFRN
     * @param token Token de acesso à API da UFRN
     * @return JSON da biblioteca
     * @throws IOException
     * @throws ConnectionException
     */
    public String getBibliotecas(String token) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_BIBLIOTECA_BIBLIOTECAS)
                .appendQueryParameter("limit", "100")
                .build()
                .toString();
        return getDados(token, url);
    }

    /**
     * Consultar todas as Situações de Material, na API da UFRN
     * @param token Token de acesso à API da UFRN
     * @return JSON das Situações
     * @throws IOException
     * @throws ConnectionException
     */
    public String getSituacoesMaterial(String token) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_BIBLIOTECA_SITUACOES)
                .appendQueryParameter("limit", "100")
                .build()
                .toString();
        return getDados(token, url);
    }

    /**
     * Consultar todos os Status de Material, na API da UFRN
     * @param token Token de acesso à API da UFRN
     * @return JSON dos Status
     * @throws IOException
     * @throws ConnectionException
     */
    public String getStatusMaterial(String token) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_BIBLIOTECA_STATUS)
                .appendQueryParameter("limit", "100")
                .build()
                .toString();
        return getDados(token, url);
    }

    /**
     * Consultar todos os Tipos de Material, na API da UFRN
     * @param token Token de acesso à API da UFRN
     * @return JSON dos Tipos de Material
     * @throws IOException
     * @throws ConnectionException
     */
    public String getTiposMaterial(String token) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_BIBLIOTECA_TIPOS_MATERIAL)
                .appendQueryParameter("limit", "100")
                .build()
                .toString();
        return getDados(token, url);
    }

    /**
     * Consultar a quantidade de empréstimos do usuário logado, na API da UFRN
     * @param token Token de acesso à API da UFRN
     * @param ativo Indica se os empréstimos consultados serão os ativos(true), inativos(false) ou ambos(null)
     * @return Quantidade de empréstimos
     * @throws IOException
     * @throws ConnectionException
     */
    public Integer getQuantidadeEmprestimos(String token, Boolean ativo) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_BIBLIOTECA_EMPRESTIMOS)
                .appendQueryParameter("cpf-cnpj-usuario", usuarioLogado.getCpfCnpj().toString())
                .appendQueryParameter("emprestado", ativo.toString())
                .build()
                .toString();
        return getQuantidadeConsulta(token, url);
    }

    /**
     * Consulta os empréstimos do usuário logado, na API da UFRN
     * @param token Token de acesso à API da UFRN
     * @param ativo Indica se os empréstimos consultados serão os ativos(true), inativos(false) ou ambos(null)
     * @param offset Offset usado na consulta
     * @return JSON da lista de empréstimos
     * @throws IOException
     * @throws ConnectionException
     */
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

    /**
     * Consulta um material informacional pelo cód. barras fornecido, na API da UFRN
     * @param token Token de acesso à API da UFRN
     * @param codBarras Código de barras a ser consultado
     * @return JSON do Material Informacional
     * @throws IOException
     * @throws ConnectionException
     */
    public String getMaterialInformacional(String token, String codBarras) throws IOException, ConnectionException {
        String url = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_BIBLIOTECA_MATERIAIS)
                .appendQueryParameter("codigo-barras", codBarras)
                .build()
                .toString();
        return getDados(token, url);
    }

    /**
     * Consulta Títulos no acervo pelos parâmetros fornecidos, na API da UFRN
     * @param token Token de acesso à API da UFRN
     * @param titulo título de material a ser consultado
     * @param autor autor principal ou secundário do material a ser consultado
     * @param assunto assunto de material a ser consultado
     * @param idBib identificador da biblioteca a ser consultada
     * @param idTipoMat identificador do tipo de materais serem consultados
     * @param offset Offset usado na consulta
     * @return JSON dos Títulos do Acervo
     * @throws IOException
     * @throws ConnectionException
     */
    public String getAcervo(String token, String titulo, String autor, String assunto, String idBib,
                            String idTipoMat, Integer offset) throws IOException, ConnectionException {
        Uri.Builder uriBuilder = Uri.parse(URL_BASE)
                .buildUpon()
                .appendEncodedPath(PATH_BIBLIOTECA_ACERVOS);

        if (!titulo.equalsIgnoreCase(""))
            uriBuilder.appendQueryParameter("titulo", titulo);
        if (!autor.equalsIgnoreCase(""))
            uriBuilder.appendQueryParameter("autor", autor);
        if (!assunto.equalsIgnoreCase(""))
            uriBuilder.appendQueryParameter("assunto", assunto);
        if (!idBib.equalsIgnoreCase(""))
            uriBuilder.appendQueryParameter("id-biblioteca", idBib);
        if (!idTipoMat.equalsIgnoreCase(""))
            uriBuilder.appendQueryParameter("id-tipo-material", idTipoMat);

        String url = uriBuilder.appendQueryParameter("limit", "100")
                .appendQueryParameter("offset", offset.toString())
                .build()
                .toString();

        Log.d("URL", url);
        return getDados(token, url);
    }

    /**
     * Realizar a consulta, na API da UFRN
     * @param token Token de acesso à API da UFRN
     * @param url URL da consulta
     * @return JSON da consulta
     * @throws IOException
     * @throws ConnectionException
     */
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

    /**
     * Resgatar a quantidade total de registros da consulta
     * @param token Token de acesso à API da UFRN
     * @param url URL da consulta
     * @return JSON da consulta
     * @throws IOException
     * @throws ConnectionException
     */
    private Integer getQuantidadeConsulta(String token, String url) throws IOException, ConnectionException {
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
        return Integer.parseInt(result);
    }
}
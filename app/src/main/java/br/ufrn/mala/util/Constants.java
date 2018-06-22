package br.ufrn.mala.util;

/**
 * Created by Joel Felipe on 02/10/17.
 */

public class Constants {

    //Constantes de autenticação
    public static final String CLIENT_ID_VALUE = "mala-id";
    public static final String SECRET_KEY = "segredo";

    public static final String REDIRECT_URI = "https://api.ufrn.br";
    public static final String AUTHORIZATION_URL = "https://autenticacao.info.ufrn.br/authz-server/oauth/authorize";
    public static final String ACCESS_TOKEN_URL = "https://autenticacao.info.ufrn.br/authz-server/oauth/token";
    public static final String RESPONSE_TYPE_PARAM = "response_type";
    public static final String RESPONSE_TYPE_AUTHORIZATION = "code";;
    public static final String RESPONSE_TYPE_REFRESH = "refresh_token";
    public static final String GRANT_TYPE_AUTHORIZATION = "authorization_code";
    public static final String GRANT_TYPE_REFRESH = "refresh_token";
    public static final String CLIENT_ID_PARAM = "client_id";
    public static final String REDIRECT_URI_PARAM = "redirect_uri";

    //Constantes de autorização
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String KEY_EXPIRES_AT = "expires_at";
    public static final String KEY_USER_INFO = "user_info";
}

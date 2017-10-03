package br.ufrn.mala.connection;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.IOException;

import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;
import br.ufrn.mala.util.Constants;
import ca.mimic.oauth2library.OAuthResponse;

/**
 * Created by Joel Felipe on 02/10/2017.
 */

public class Preferences {

    public static void savePreferences(Context context, OAuthResponse response) throws IOException, JsonStringInvalidaException, ConnectionException {
        String accessToken = response.getAccessToken();
        String refreshToken = response.getRefreshToken();
        Long expiresIn = response.getExpiresIn();
        Long expiresAt = response.getExpiresAt();

        SharedPreferences preferences = context.getSharedPreferences(Constants.KEY_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.KEY_ACCESS_TOKEN, accessToken);
        editor.putString(Constants.KEY_REFRESH_TOKEN, refreshToken);
        editor.putLong(Constants.KEY_EXPIRES_IN, expiresIn);
        editor.putLong(Constants.KEY_EXPIRES_AT, expiresAt);

        UsuarioDTO usuarioLogado = FachadaAPI.getInstance(context).setUsuarioLogado(accessToken);
        Gson gson = new Gson();
        String json = gson.toJson(usuarioLogado); // myObject - instance of MyObject
        editor.putString("UsuarioLogado", json);
        editor.apply();
    }
}

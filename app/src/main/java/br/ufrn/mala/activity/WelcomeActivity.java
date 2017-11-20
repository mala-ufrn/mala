package br.ufrn.mala.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.ufrn.mala.connection.Preferences;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;
import br.ufrn.mala.util.Constants;
import ca.mimic.oauth2library.OAuth2Client;
import ca.mimic.oauth2library.OAuthResponse;

/**
 * Created by Joel Felipe on 02/10/17.
 */

public class WelcomeActivity extends AppCompatActivity {


    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = this.getSharedPreferences(Constants.KEY_USER_INFO, 0);
        String accessToken = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);
        String refreshtoken = preferences.getString(Constants.KEY_REFRESH_TOKEN, null);
        Long expiresAt = preferences.getLong(Constants.KEY_EXPIRES_AT, 0);

        if (accessToken != null) {
            if (System.currentTimeMillis() > expiresAt){
                new WelcomeActivity.RefreshTokenAsyncTask().execute(refreshtoken);
            }
            else{
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private class RefreshTokenAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(WelcomeActivity.this, "", "loading", true);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.i("doInBackground", "doInBackground");
            try {
                OAuth2Client client;
                Map<String, String> map = new HashMap<>();
                map.put(Constants.RESPONSE_TYPE_REFRESH, params[0]);

                client = new OAuth2Client.Builder(Constants.CLIENT_ID_VALUE, Constants.SECRET_KEY, Constants.ACCESS_TOKEN_URL)
                        .grantType(Constants.GRANT_TYPE_REFRESH)
                        .parameters(map)
                        .build();

                OAuthResponse response = client.requestAccessToken();
                if (response.isSuccessful()) {
                    Preferences.savePreferences(WelcomeActivity.this, response);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JsonStringInvalidaException e) {
                Toast.makeText(WelcomeActivity.this, "Ocorreu algum erro na renovação do Token", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (ConnectionException e) {
                Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (status) {
                Intent startProfileActivity = new Intent(WelcomeActivity.this, MainActivity.class);
                startProfileActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                WelcomeActivity.this.startActivity(startProfileActivity);
            }
            else {
                Intent startGetNewAcessActivity = new Intent(WelcomeActivity.this, LoginActivity.class);
                startGetNewAcessActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                WelcomeActivity.this.startActivity(startGetNewAcessActivity);
            }
        }
    }
}

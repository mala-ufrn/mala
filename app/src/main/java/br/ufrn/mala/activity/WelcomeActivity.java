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

import br.ufrn.mala.R;
import br.ufrn.mala.connection.FacadeDAO;
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

        Log.d("ExpiresAt", String.valueOf(expiresAt));
        Log.d("CurrentTime", String.valueOf(System.currentTimeMillis()));

        if (accessToken != null) {
            if (System.currentTimeMillis() > expiresAt){
                new WelcomeActivity.RefreshTokenAsyncTask().execute(refreshtoken);
            }
            else{
                new WelcomeActivity.LoadResourcesAsyncTask().execute(accessToken);
            }
        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);;
        }
    }

    // TODO Ver como vai ficar a renovação do token, por enquanto estou chamando AsyncTask de carregar recursos no posExecute
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
                // Provisório?
                SharedPreferences preferences = WelcomeActivity.this.getSharedPreferences(Constants.KEY_USER_INFO, 0);
                String accessToken = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);

                new WelcomeActivity.LoadResourcesAsyncTask().execute(accessToken);
            }
            else {
                Intent startGetNewAcessActivity = new Intent(WelcomeActivity.this, LoginActivity.class);
                startGetNewAcessActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                WelcomeActivity.this.startActivity(startGetNewAcessActivity);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        }
    }

    private class LoadResourcesAsyncTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(WelcomeActivity.this, "", getString(R.string.load_libraries), true);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.i("doInBackground", "doInBackground");
            boolean success = false;
            try {
                success = FacadeDAO.getInstance(WelcomeActivity.this).loadBibliotecas(params[0]);
                publishProgress(1);
                success &= FacadeDAO.getInstance(WelcomeActivity.this).loadSituacoesMaterial(params[0]);
                publishProgress(2);
                success &= FacadeDAO.getInstance(WelcomeActivity.this).loadStatusMaterial(params[0]);
                publishProgress(3);
                success &= FacadeDAO.getInstance(WelcomeActivity.this).loadTiposMaterial(params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JsonStringInvalidaException e) {
                Toast.makeText(WelcomeActivity.this, "Ocorreu algum erro interno", Toast.LENGTH_SHORT).show();
            } catch (ConnectionException e) {
                Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            switch (values[0]) {
                case 1 :
                    pd.setMessage(getString(R.string.load_mat_situations));
                    break;
                case 2:
                    pd.setMessage(getString(R.string.load_mat_status));
                    break;
                case 3:
                    pd.setMessage(getString(R.string.load_mat_types));
                    break;
                default:
                    pd.setMessage("Loading");
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);;
        }
    }
}

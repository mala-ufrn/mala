package br.ufrn.mala.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.ufrn.mala.R;
import br.ufrn.mala.connection.Preferences;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;
import br.ufrn.mala.util.Constants;
import ca.mimic.oauth2library.OAuth2Client;
import ca.mimic.oauth2library.OAuthResponse;

/**
 * Created by Joel Felipe on 02/10/17.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences preferences = this.getSharedPreferences(Constants.KEY_USER_INFO, 0);
        String accessToken = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);
        String refreshtoken = preferences.getString(Constants.KEY_REFRESH_TOKEN, null);
        Long expiresAt = preferences.getLong(Constants.KEY_EXPIRES_AT, 0);
        if (accessToken != null) {
            if (System.currentTimeMillis() > expiresAt){
                new RefreshTokenAsyncTask().execute(refreshtoken);
            }
            else{
                Intent intent = new Intent(this, EmprestimosAtivosActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the LogonActivity
        moveTaskToBack(true);
    }

    public void logar(View v) {
        Log.d(TAG, "Login");
        Intent i = new Intent(this, LogonActivity.class);
        startActivity(i);
    }

    public void sair(View view) {
        SharedPreferences preferences = this.getSharedPreferences(Constants.KEY_USER_INFO, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        try {
            File dir = this.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Saiu", Toast.LENGTH_SHORT).show();
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }

    private class RefreshTokenAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(LoginActivity.this, "", "loading", true);
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
                    Preferences.savePreferences(LoginActivity.this, response);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JsonStringInvalidaException e) {
                Toast.makeText(LoginActivity.this, "Ocorreu algum erro interno", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (ConnectionException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Intent startProfileActivity = new Intent(LoginActivity.this, EmprestimosAtivosActivity.class);
                LoginActivity.this.startActivity(startProfileActivity);
            }
        }
    }
}

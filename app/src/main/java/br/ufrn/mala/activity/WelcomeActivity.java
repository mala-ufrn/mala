package br.ufrn.mala.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import br.ufrn.mala.R;
import br.ufrn.mala.connection.FacadeDAO;
import br.ufrn.mala.util.Constants;

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
            new WelcomeActivity.LoadResourcesAsyncTask().execute();
        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);;
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
            boolean success = true;
            try {
                success &= FacadeDAO.getInstance(WelcomeActivity.this).loadBibliotecas();
                publishProgress(1);
                success &= FacadeDAO.getInstance(WelcomeActivity.this).loadSituacoesMaterial();
                publishProgress(2);
                success &= FacadeDAO.getInstance(WelcomeActivity.this).loadStatusMaterial();
                publishProgress(3);
                success &= FacadeDAO.getInstance(WelcomeActivity.this).loadTiposMaterial();

            } catch (Exception e) {
                e.printStackTrace();
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
                    pd.setMessage("Carregando informações");
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
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
    }
}

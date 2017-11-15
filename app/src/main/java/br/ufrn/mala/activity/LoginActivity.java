package br.ufrn.mala.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import br.ufrn.mala.R;

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
}

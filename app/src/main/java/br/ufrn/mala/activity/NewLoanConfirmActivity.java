package br.ufrn.mala.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.ufrn.mala.R;

public class NewLoanConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_loan_confirm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

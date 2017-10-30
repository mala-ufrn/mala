package br.ufrn.mala.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import br.ufrn.mala.R;

public class NovoEmprestimoActivity extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_emprestimo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_novo_emprestimo);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                camReader(v);
            }

        });


    }


    public void camReader(View v) {
        Toast.makeText(this, "Chama a activity da camera", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

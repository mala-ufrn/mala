package br.ufrn.mala.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import br.ufrn.mala.R;

public class NovoEmprestimoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_emprestimo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_novo_emprestimo);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Chamar a camera", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

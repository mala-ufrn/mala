package br.ufrn.mala.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;

import br.ufrn.mala.R;
import br.ufrn.mala.dto.MaterialInformacionalDTO;

public class NewLoanConfirmActivity extends AppCompatActivity {

    MaterialInformacionalDTO material;
    Spinner loanTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_loan_confirm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loanTypeSpinner = (Spinner) findViewById(R.id.loan_type_spinner);

        material = (MaterialInformacionalDTO) getIntent().getSerializableExtra("material");
        if(material != null) {
            prepareMaterialDetails();
            prepareSpinner();
        }
    }

    private void prepareMaterialDetails(){

        TextView codigo = (TextView) findViewById(R.id.cod_Barra_detail_new_loan);
        TextView titulo = (TextView) findViewById(R.id.titulo_detail_new_loan);
        TextView autor = (TextView) findViewById(R.id.autor_detail_new_loan);
        TextView tipoMaterial = (TextView) findViewById(R.id.tipo_detail_new_loan);
        TextView statusMaterial = (TextView) findViewById(R.id.status_detail_new_loan);

        codigo.setText(material.getCodigoBarras());
        titulo.setText(material.getTitulo());
        autor.setText(material.getAutor());
        tipoMaterial.setText(material.getTipoMaterial().getDescricao());
        statusMaterial.setText(material.getStatusMaterial().getDescricao());
    }

    private void prepareSpinner(){
        String[] normalTypeSpinOptions = {getString(R.string.spinner_normal_option),
                getString(R.string.spinner_special_option),
                getString(R.string.spinner_fotocopy_option)};
        String[] specialTypeSpinOptions = {getString(R.string.spinner_fotocopy_option)};
        ArrayAdapter<String> loanTypeSpinAdapter;

        if (material.getStatusMaterial().getDescricao().equalsIgnoreCase("ESPECIAL")) {
            loanTypeSpinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, specialTypeSpinOptions);
        } else {
            loanTypeSpinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, normalTypeSpinOptions);
            if(!material.getStatusMaterial().getDescricao().equalsIgnoreCase("REGULAR")) {
                loanTypeSpinner.setEnabled(false);
            }
        }

        loanTypeSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loanTypeSpinner.setAdapter(loanTypeSpinAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

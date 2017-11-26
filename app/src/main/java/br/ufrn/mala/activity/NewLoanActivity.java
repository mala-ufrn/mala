package br.ufrn.mala.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.barcode.*;
import br.ufrn.mala.connection.FacadeDAO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.MaterialInformacionalDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;
import br.ufrn.mala.util.Constants;


/**
 * Created by Paulo Lopes on 20/10/17.
 */

public class NewLoanActivity extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private FloatingActionButton fab;
    private TextView inputBarCode;
    private Button buscarMaterial;
    private ProgressDialog pd;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_loan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab_novo_emprestimo);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                camReader(v);
            }

        });
        inputBarCode = (TextView) findViewById(R.id.new_loan_barcode_input);
        buscarMaterial = (Button) findViewById(R.id.btn_buscar_Material);
        SharedPreferences preferences = getSharedPreferences(Constants.KEY_USER_INFO, 0);
        accessToken = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);

        buscarMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputBarCode.getText().toString().trim().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Nenhum código de barras digitado", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    new NewLoanActivity.SearchMaterailTask().execute(accessToken, inputBarCode.getText().toString());
                }
            }
        });
    }

    /*
    * Método que executa leitura de código de barras, executado pelo Cam Scanner
     */
    public void camReader(View v) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    //TODO Falta tratar os diversos modelos de código de barras ou aplicar regex no padrão usado nas bibliotecas
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String scanContent = scanResult.getContents();
            String scanFormat = scanResult.getFormatName();

            inputBarCode.setText(scanContent);
            EditText et = (EditText) inputBarCode;
            et.setSelection(et.getText().length());

        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Nenhum código de barras foi detectado", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private class SearchMaterailTask extends AsyncTask<String, Void, MaterialInformacionalDTO> {

        protected void onPreExecute() {
            pd = ProgressDialog.show(NewLoanActivity.this, "", getString(R.string.search_material), true);
        }

        protected MaterialInformacionalDTO doInBackground(String... params) {
            try {
                return FacadeDAO.getInstance(NewLoanActivity.this).getMaterialInformacional(params[0], params[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MaterialInformacionalDTO result) {
            super.onPostExecute(result);
            pd.dismiss();
            if (result == null) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.materail_not_found), Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                Intent i = new Intent(NewLoanActivity.this, NewLoanConfirmActivity.class);
                i.putExtra("material", result);
                startActivity(i);
                inputBarCode.setText("");
            }
        }
    }
}

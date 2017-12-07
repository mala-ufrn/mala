package br.ufrn.mala.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.ufrn.mala.R;
import br.ufrn.mala.barcode.IntentIntegrator;
import br.ufrn.mala.barcode.IntentResult;
import br.ufrn.mala.connection.FacadeDAO;
import br.ufrn.mala.dto.MaterialInformacionalDTO;


/**
 * Created by Paulo Lopes on 20/10/17.
 */

public class NewLoanActivity extends AppCompatActivity {

    final int ESTATUS_NAO_CIRCULA = 3;
    final int SITUACAO_DISPONIVEL = 1;


    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private FloatingActionButton fab;
    private TextView inputBarCode;
    private Button buscarMaterial;
    private ProgressDialog pd;

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

        buscarMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputBarCode.getText().toString().trim().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.no_barcode), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    new NewLoanActivity.SearchMaterailTask().execute(inputBarCode.getText().toString());
                }
            }
        });
    }

    /*
    * Método que executa leitura de código de barras pelo Cam Scanner
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

            new NewLoanActivity.SearchMaterailTask().execute(scanContent);

        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.no_barcode), Toast.LENGTH_SHORT);
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
                return FacadeDAO.getInstance(NewLoanActivity.this).getMaterialInformacional(params[0]);
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
                if(result.getIdStatusMaterial() == ESTATUS_NAO_CIRCULA ){
                    DialogFragment newFragment = new NewLoanActivity.EmprestimoEstatusNaoCircula();
                    newFragment.show(getFragmentManager(), "status_blocked");
                } else if(result.getIdSituacaoMaterial() !=  SITUACAO_DISPONIVEL) {
                    DialogFragment newFragment = new NewLoanActivity.EmprestimoSituacaoNaoDisponivel();
                    newFragment.show(getFragmentManager(), "situacao_nao_disponivel");
                }else{
                    Intent i = new Intent(NewLoanActivity.this, NewLoanConfirmActivity.class);
                    i.putExtra("material", result);
                    startActivity(i);
                    inputBarCode.setText("");
                }

            }
        }

    }

    public static class EmprestimoEstatusNaoCircula extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.status_material_nao_circula)
                    .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public static class EmprestimoSituacaoNaoDisponivel extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.situacao_material_nao_disponivel)
                    .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}

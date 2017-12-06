package br.ufrn.mala.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import br.ufrn.mala.R;
import br.ufrn.mala.connection.SQLiteConnection;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.MaterialInformacionalDTO;

public class NewLoanConfirmActivity extends AppCompatActivity {


    MaterialInformacionalDTO material;
    Spinner loanTypeSpinner;
    Button realizeEmprestimo;
    AppCompatImageButton typesInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_loan_confirm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loanTypeSpinner = (Spinner) findViewById(R.id.loan_type_spinner);
        realizeEmprestimo = (Button) findViewById(R.id.btn_realizar_emprestimo);

        material = (MaterialInformacionalDTO) getIntent().getSerializableExtra("material");
        if (material != null) {
            prepareMaterialDetails();
            prepareSpinner();

            realizeEmprestimo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment newFragment = new NewLoanConfirmActivity.ConfirmSenhaSisbi();
                    newFragment.show(getFragmentManager(), "confirm_sisbi_pass");
                }
            });

        } else {
            realizeEmprestimo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.materail_not_found), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }

        typesInfo = (AppCompatImageButton) findViewById(R.id.btn_types_info);
        typesInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new NewLoanConfirmActivity.TypesOfLoans();
                newFragment.show(getFragmentManager(), "type_of_loans");

            }
        });
    }

    private void prepareMaterialDetails() {

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

    private void prepareSpinner() {
        String[] normalTypeSpinOptions = {getString(R.string.spinner_normal_option),
                getString(R.string.spinner_special_option),
                getString(R.string.spinner_fotocopy_option)};
        String[] specialTypeSpinOptions = {getString(R.string.spinner_fotocopy_option)};
        ArrayAdapter<String> loanTypeSpinAdapter;

        if (material.getStatusMaterial().getDescricao().equalsIgnoreCase("ESPECIAL")) {
            loanTypeSpinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, specialTypeSpinOptions);
        } else {
            loanTypeSpinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, normalTypeSpinOptions);
            if (!material.getStatusMaterial().getDescricao().equalsIgnoreCase("REGULAR")) {
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


    public static class TypesOfLoans extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(Html.fromHtml(getString(R.string.type_of_loans)))
                    .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    @SuppressLint("ValidFragment")
    public class ConfirmSenhaSisbi extends DialogFragment {

        SQLiteConnection sqLiteConnection = SQLiteConnection.getInstance(this.getActivity());


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_senha_sisbi, null))
                    .setPositiveButton(R.string.loan_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(!loanProceed()) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        getString(R.string.sisbi_pass_error), Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        }
                    })
                    .setNegativeButton(R.string.back_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }

                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }


        /*
         * tipo normal && statusMaterial := regular  => 15 dias
         * tipo especial && statusMaterial := regular || especial => 1 dia
         *tipo fotocópia && statusMaterial := regular || especial => 3 horas
         */

        private boolean loanProceed() {
            Calendar calendar = Calendar.getInstance();
            EmprestimoDTO emprestimo = new EmprestimoDTO();

            emprestimo.setTitulo(material.getTitulo());
            emprestimo.setAutor(material.getAutor());
            emprestimo.setBiblioteca(material.getBiblioteca());
            emprestimo.setDataEmpretimo(calendar.getTimeInMillis());

            String tipo = loanTypeSpinner.getSelectedItem().toString();
            if (tipo.equals(R.string.spinner_special_option)) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            } else if (tipo.equals(R.string.spinner_fotocopy_option))
                calendar.add(Calendar.HOUR, 3);
            else
                calendar.add(Calendar.DAY_OF_YEAR, 15);
            emprestimo.setPrazo(calendar.getTimeInMillis());
            emprestimo.setCodigoBarras(material.getCodigoBarras());
            emprestimo.setTipoEmprestimo(tipo);
            sqLiteConnection.insertEmprestimo(emprestimo, true);

            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.loan_concluded), Toast.LENGTH_SHORT);
            toast.show();

            onSupportNavigateUp();
            return true;
        }
    }


}

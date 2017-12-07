package br.ufrn.mala.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import br.ufrn.mala.R;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.util.DataUtil;

/**
 * Created by paulo on 03/11/17.
 */

public class LoanDetailsActivity extends AppCompatActivity {
    EmprestimoDTO emprestimo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loan_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emprestimo = (EmprestimoDTO) getIntent().getSerializableExtra("emprestimo");
        if(emprestimo != null) {
            prepareDetail();
        }
    }

    /**
     * Prepara a tela de detalhes do empréstimo
     */

    private void prepareDetail(){

        TextView codigo = (TextView) findViewById(R.id.ln_cod_barras_input);
        TextView titulo = (TextView) findViewById(R.id.ln_title_input);
        TextView autor = (TextView) findViewById(R.id.ln_author_input);
        TextView biblioteca = (TextView) findViewById(R.id.ln_library_input);
        TextView localizacao = (TextView) findViewById(R.id.ln_local_input);
        TextView dtEmprestimo = (TextView) findViewById(R.id.ln_borrow_dt_input);
        TextView labelDtRenovado = (TextView) findViewById(R.id.ln_reneaw_dt_lbl);
        TextView dtRenovado = (TextView) findViewById(R.id.ln_reneaw_dt_input);
        TextView labelDtDevolvido = (TextView) findViewById(R.id.ln_return_dt_lbl);
        TextView dtDevolvido = (TextView) findViewById(R.id.ln_return_dt_input);
        TextView labelPzDevolução = (TextView) findViewById(R.id.ln_deadline_dt_lbl);
        TextView pzDevolução = (TextView) findViewById(R.id.ln_deadline_dt_input);

        codigo.setText(emprestimo.getCodigoBarras());
        titulo.setText(emprestimo.getTitulo());
        autor.setText(emprestimo.getAutor());
        biblioteca.setText(emprestimo.getBiblioteca().getDescricao());
        localizacao.setText(emprestimo.getNumeroChamada());
        dtEmprestimo.setText(String.valueOf(DataUtil.formatLongToDate(emprestimo.getDataEmpretimo())));
        if(emprestimo.getDataRenovacao() != null){
            dtRenovado.setText(String.valueOf(DataUtil.formatLongToDate(emprestimo.getDataRenovacao())));
        }
        else {
            labelDtRenovado.setVisibility(TextView.GONE);
            dtRenovado.setVisibility(TextView.GONE);
        }
        if(emprestimo.getDataDevolucao() != null){
            dtDevolvido.setText(String.valueOf(DataUtil.formatLongToDate(emprestimo.getDataDevolucao())));
            labelPzDevolução.setVisibility(TextView.GONE);
        }
        else {
            labelDtDevolvido.setVisibility(TextView.GONE);
            dtDevolvido.setVisibility(TextView.GONE);

            pzDevolução.setText(String.valueOf(DataUtil.formatLongToDate(emprestimo.getPrazo())));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

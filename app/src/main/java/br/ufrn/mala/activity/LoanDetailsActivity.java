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

        emprestimo = (EmprestimoDTO) getIntent().getSerializableExtra("tituloAcervo");
        if(emprestimo != null) {
            prepareDetail();
        }
    }

    /**
     * Prepara a tela de detalhes do empréstimo
     */

    private void prepareDetail(){

        TextView codigo = (TextView) findViewById(R.id.lblCod_Barra_detail_loan);
        TextView titulo = (TextView) findViewById(R.id.lblTitulo_detail_loan);
        TextView autor = (TextView) findViewById(R.id.lblAutor_detail_loan);
        TextView edicao = (TextView) findViewById(R.id.lblEdicao_detail_loan);

        TextView biblioteca = (TextView) findViewById(R.id.lblBiblioteca_detail_loan);
        TextView dtEmprestimo = (TextView) findViewById(R.id.lblDtEmprestimo_detail_loan);
        TextView dtRenovado = (TextView) findViewById(R.id.lblDtRenovacaEmprestimo_detail_loan);
        TextView dtDevolvido = (TextView) findViewById(R.id.lblDtDevolucao_detail_loan);

        codigo.setText("Código :"+emprestimo.getCodigoBarras());
        titulo.setText("Titulo :"+emprestimo.getTitulo());
        autor.setText("Autor: " + emprestimo.getAutor());
        edicao.setText("Chamada: " + emprestimo.getNumeroChamada());
        dtDevolvido.setVisibility(TextView.GONE);
        dtRenovado.setVisibility(TextView.GONE);
        dtEmprestimo.setText("Emprestado em: " + String.valueOf(DataUtil.formatLongToDate(emprestimo.getDataEmpretimo())));

        if(emprestimo.getDataDevolucao() != null){
            dtDevolvido.setVisibility(TextView.VISIBLE);
            dtDevolvido.setText("Devolvido em: " + String.valueOf(DataUtil.formatLongToDate(emprestimo.getDataDevolucao())));
        }
        if(emprestimo.getDataRenovacao() != null){
            dtRenovado.setVisibility(TextView.VISIBLE);
            dtRenovado.setText("Renovado em: " + String.valueOf(DataUtil.formatLongToDate(emprestimo.getDataRenovacao())));
        }
        biblioteca.setText("Biblioteca: "+emprestimo.getBiblioteca().getDescricao());

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

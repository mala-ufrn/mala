package br.ufrn.mala.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.ufrn.mala.R;
import br.ufrn.mala.connection.FacadeDAO;
import br.ufrn.mala.dto.AcervoDTO;

/**
 * Created by Hugo e Mirna on 05/12/17.
 */

public class SearchResultDetailsActivity extends AppCompatActivity {

    AcervoDTO tituloAcervo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tituloAcervo = (AcervoDTO) getIntent().getSerializableExtra("titulo_acervo");
        if(tituloAcervo.getBiblioteca() != null) {
            prepareDetails();
        }
        else {
            new FillItenDetailsTask().execute();
        }
    }

    /**
     * Prepara a tela de detalhes do empréstimo
     */

    private void prepareDetails(){

        TextView regSis = (TextView) findViewById(R.id.sr_regsis_input);
        TextView titulo = (TextView) findViewById(R.id.sr_title_input);
        TextView subTituloLabel = (TextView) findViewById(R.id.sr_subtitle_lbl);
        TextView subTitulo = (TextView) findViewById(R.id.sr_subtitle_input);
        TextView autoresLabel = (TextView) findViewById(R.id.sr_author_lbl);
        LinearLayout autores = (LinearLayout) findViewById(R.id.sr_author_input);
        TextView assuntosLabel = (TextView) findViewById(R.id.sr_subject_lbl);
        LinearLayout assuntos = (LinearLayout) findViewById(R.id.sr_subject_input);
        TextView edicaoLabel = (TextView) findViewById(R.id.sr_edition_lbl);
        TextView edicao = (TextView) findViewById(R.id.sr_edition_input);
        TextView editoraLabel = (TextView) findViewById(R.id.sr_pub_house_lbl);
        TextView editora = (TextView) findViewById(R.id.sr_pub_house_input);
        TextView anoLabel = (TextView) findViewById(R.id.sr_year_lbl);
        TextView ano = (TextView) findViewById(R.id.sr_year_input);
        TextView biblioteca = (TextView) findViewById(R.id.sr_library_input);
        TextView localizacao = (TextView) findViewById(R.id.sr_local_input);
        TextView tipoMaterial = (TextView) findViewById(R.id.sr_mat_type_input);
        TextView quantidade = (TextView) findViewById(R.id.sr_qnt_input);

        regSis.setText(tituloAcervo.getRegistroSistema().toString());

        titulo.setText(tituloAcervo.getTitulo());

        if (tituloAcervo.getSubTitulo() == null || tituloAcervo.getSubTitulo().equalsIgnoreCase("")) {
            subTituloLabel.setVisibility(TextView.GONE);
            subTitulo.setVisibility(TextView.GONE);
        }
        else {
            subTitulo.setText(tituloAcervo.getSubTitulo());
        }

        if ((tituloAcervo.getAutor() == null || tituloAcervo.getAutor().equalsIgnoreCase("")) &&
                tituloAcervo.getAutoresSecundarios().length == 0){
            autoresLabel.setVisibility(TextView.GONE);
        }
        else {
            if(!(tituloAcervo.getAutor() == null || tituloAcervo.getAutor().equalsIgnoreCase(""))){
                TextView autor = new TextView(this);
                autor.setText(tituloAcervo.getAutor());
                autor.setSingleLine(true);
                autor.setEllipsize(TextUtils.TruncateAt.END);
                autor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                autores.addView(autor);
            }
            for (String autorStr: tituloAcervo.getAutoresSecundarios()) {
                TextView autor = new TextView(this);
                autor.setText(autorStr);
                autor.setSingleLine(true);
                autor.setEllipsize(TextUtils.TruncateAt.END);
                autor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                autores.addView(autor);
            }
        }

        if (tituloAcervo.getAssunto().length == 0) {
            assuntosLabel.setVisibility(TextView.GONE);
        }
        else {
            for (String assuntoStr: tituloAcervo.getAssunto()) {
                TextView assunto = new TextView(this);
                assunto.setText(assuntoStr);
                assunto.setSingleLine(true);
                assunto.setEllipsize(TextUtils.TruncateAt.END);
                assunto.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                assuntos.addView(assunto);
            }
        }

        if (tituloAcervo.getEdicao() == null || tituloAcervo.getEdicao().equalsIgnoreCase("")) {
            edicaoLabel.setVisibility(TextView.GONE);
            edicao.setVisibility(TextView.GONE);
        }
        else {
            edicao.setText(tituloAcervo.getEdicao());
        }

        if (tituloAcervo.getEditora() == null || tituloAcervo.getEditora().equalsIgnoreCase("")) {
            editoraLabel.setVisibility(TextView.GONE);
            editora.setVisibility(TextView.GONE);
        }
        else {
            editora.setText(tituloAcervo.getEditora());
        }

        if (tituloAcervo.getAno() == null || tituloAcervo.getAno().equalsIgnoreCase("")) {
            anoLabel.setVisibility(TextView.GONE);
            ano.setVisibility(TextView.GONE);
        }
        else {
            ano.setText(tituloAcervo.getAno());
        }

        biblioteca.setText(tituloAcervo.getBiblioteca().getDescricao());
        localizacao.setText(tituloAcervo.getNumeroChamada());
        tipoMaterial.setText(tituloAcervo.getTipoMaterial());
        quantidade.setText(tituloAcervo.getQuantidade().toString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private class FillItenDetailsTask extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... params) {
            try {
                tituloAcervo.setAutoresSecundarios(FacadeDAO.getInstance(SearchResultDetailsActivity.this).getAutoresSec(tituloAcervo.getIdAcervo()));
                tituloAcervo.setAssunto(FacadeDAO.getInstance(SearchResultDetailsActivity.this).getAssuntos(tituloAcervo.getIdAcervo()));
                tituloAcervo.setBiblioteca(FacadeDAO.getInstance(SearchResultDetailsActivity.this).getBiblioteca(tituloAcervo.getIdBiblioteca()));
                return Boolean.TRUE;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                prepareDetails();
            }
        }
    }

}

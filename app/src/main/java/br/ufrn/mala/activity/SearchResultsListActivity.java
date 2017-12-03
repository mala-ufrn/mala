package br.ufrn.mala.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.auxiliar.ResultsListAdapter;
import br.ufrn.mala.connection.FacadeDAO;
import br.ufrn.mala.dto.AcervoDTO;
import br.ufrn.mala.dto.BibliotecaDTO;

public class SearchResultsListActivity extends AppCompatActivity {

    private List<BibliotecaDTO> libResultsList;
    private List<AcervoDTO> searchResults;
    private ExpandableListView expandableListResults;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new FillResults().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.act_search_results_sort, menu);
        return true;
    }

    private void prepareResultsList() {
        //Preenchendo lista de empréstimos ativos
        expandableListResults = (ExpandableListView) findViewById(R.id.list_results);

        // cria os grupos
        List<String> listaBibliotecas = new ArrayList<>();

        // cria a lista de sublistas
        HashMap<String, List<AcervoDTO>> listaItensGrupo = new HashMap<>();

        for (BibliotecaDTO biblioteca: libResultsList) {
            listaBibliotecas.add(biblioteca.toString());
            List<AcervoDTO> listaTitulos = new ArrayList<>();
            for (Iterator<AcervoDTO> i = searchResults.iterator(); i.hasNext();) {
                AcervoDTO result = i.next();
                if (result.getIdBiblioteca().intValue() == biblioteca.getIdBiblioteca().intValue()) {
                    listaTitulos.add(result);
                    i.remove();
                }
            }
            listaItensGrupo.put(biblioteca.toString(), listaTitulos);
        }
        ResultsListAdapter resultListAdapter = new ResultsListAdapter(this, listaBibliotecas, listaItensGrupo);
        expandableListResults.setAdapter(resultListAdapter);

/*
        expandableListViewEmprestimo.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                int position = 1; // Sempre há o group header 0, que conta como primeiro item

                for (int groupIndex = 0; groupIndex < groupPosition; groupIndex++)
                    position += parent.getExpandableListAdapter().getChildrenCount(groupIndex);

                position += groupPosition + childPosition;

                Intent i = new Intent(view.getContext(), LoanDetailsActivity.class);
                i.putExtra("emprestimo", (Serializable) expandableListViewEmprestimo.getAdapter().getItem(position));
                startActivity(i);

                return false;
            }
        });
        */
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private class FillResults extends AsyncTask<Void, Void, List<BibliotecaDTO>> {

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(SearchResultsListActivity.this,"",getString(R.string.generate_list), true);
            super.onPreExecute();
        }

        protected List<BibliotecaDTO> doInBackground(Void... params) {
            return FacadeDAO.getInstance(SearchResultsListActivity.this).getBibliotecasAcervo();
        }

        @Override
        protected void onPostExecute(List<BibliotecaDTO> result) {
            super.onPostExecute(result);
            libResultsList = result;
            new FillAcervoResults().execute();
        }
    }

    private class FillAcervoResults extends AsyncTask<Void, Void, List<AcervoDTO>> {
        protected List<AcervoDTO> doInBackground(Void... params) {
            try {
                return FacadeDAO.getInstance(SearchResultsListActivity.this).getAcervo();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<AcervoDTO> result) {
            super.onPostExecute(result);
            searchResults = result;
            prepareResultsList();
            pd.dismiss();
        }
    }
}

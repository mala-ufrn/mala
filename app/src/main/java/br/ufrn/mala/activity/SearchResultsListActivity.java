package br.ufrn.mala.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import java.io.Serializable;
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
    private ResultsListAdapter resultsListAdapter;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new FillResults().execute();

        if (getIntent().hasExtra("SearchWarning")){
            int warning = getIntent().getIntExtra("SearchWarning", 0);
            if (warning == 102) {
                DialogFragment newFragment = new OverflowDialogFragment();
                newFragment.show(getFragmentManager(), "overflow");
            }
            else if (warning == 101) {
                DialogFragment newFragment = new PartialSearchDialogFragment();
                newFragment.show(getFragmentManager(), "partial_search");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.act_search_results_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_by_title) {
            new FillAcervoResults().execute("titulo");
        }
        else if (id == R.id.sort_by_author) {
            new FillAcervoResults().execute("autor");
        }
        else if (id == R.id.sort_by_date){
            new FillAcervoResults().execute("ano");
        }

        return super.onOptionsItemSelected(item);
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

        if (resultsListAdapter == null) {
            resultsListAdapter = new ResultsListAdapter(this, listaBibliotecas, listaItensGrupo);
            expandableListResults.setAdapter(resultsListAdapter);
        }
        else {
            resultsListAdapter.setNewItems(listaBibliotecas, listaItensGrupo);
        }

        // Expande todos os grupos do ListView
        for (int i = 0; i < expandableListResults.getExpandableListAdapter().getGroupCount(); i++)
            expandableListResults.expandGroup(i);

        expandableListResults.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                int position = 1; // Sempre há o group header 0, que conta como primeiro item

                for (int groupIndex = 0; groupIndex < groupPosition; groupIndex++)
                    position += parent.getExpandableListAdapter().getChildrenCount(groupIndex);

                position += groupPosition + childPosition;

                Intent i = new Intent(SearchResultsListActivity.this, SearchResultDetailsActivity.class);
                i.putExtra("titulo_acervo", (Serializable) expandableListResults.getAdapter().getItem(position));
                startActivity(i);

                return false;
            }
        });
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
            new FillAcervoResults().execute("none");
        }
    }

    private class FillAcervoResults extends AsyncTask<String, Void, List<AcervoDTO>> {
        @Override
        protected void onPreExecute() {
            if (!pd.isShowing()){
                pd = ProgressDialog.show(SearchResultsListActivity.this,"",getString(R.string.sorting), true);
            }
        }

        protected List<AcervoDTO> doInBackground(String... params) {
            try {
                return FacadeDAO.getInstance(SearchResultsListActivity.this).getAcervo(params[0]);
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

    public static class OverflowDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.overflow_alert)
                    .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public static class PartialSearchDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.partial_search_alert)
                    .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}

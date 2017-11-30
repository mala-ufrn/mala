package br.ufrn.mala.activity.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.activity.LoanDetailsActivity;
import br.ufrn.mala.activity.MainActivity;
import br.ufrn.mala.auxiliar.HistoricalListAdapter;
import br.ufrn.mala.connection.FacadeDAO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.util.Constants;

/**
 * Created by paulo on 23/10/17.
 */

public class HistoricalListFragment extends Fragment {

    int offsetEmprestimos = 0;
    ProgressDialog pd;

    List<EmprestimoDTO> listaEmprestimos;
    ListView listViewEmprestimos;

    private String accessToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Muda o título na ActionBar
        getActivity().setTitle(getResources().getText(R.string.app_my_loans_history));
        // Esconde o fab e o menu de três pontos

        MainActivity ma = ((MainActivity)getActivity());
        ma.showThreeDotsMenu(false);
        FloatingActionButton fab = (FloatingActionButton)ma.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);


        accessToken = ((MainActivity)getActivity()).getAccessToken();

        refreshList();
        ma.refreshLoans = false;

        return inflater.inflate(R.layout.fragment_historical_list, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // Muda o título na ActionBar
            getActivity().setTitle(getResources().getText(R.string.app_my_loans_history));

            // Esconde o fab e o menu de três pontos
            MainActivity ma = ((MainActivity)getActivity());
            ma.showThreeDotsMenu(false);
            FloatingActionButton fab = (FloatingActionButton)ma.findViewById(R.id.fab);
            fab.setVisibility(View.GONE);

            if (ma.refreshLoans) {
                refreshList();
                ma.refreshLoans = false;
            }

            // Mostra topo da lista
            listViewEmprestimos.setSelectionAfterHeaderView();
        }
    }

    private void refreshList() {
        // Popula a lista de emprestimos
        if (accessToken != null) {
            new HistoricoEmprestimosTask().execute(accessToken);
        }
    }

    private void prepareListHistorico() {

        listViewEmprestimos = (ListView) getActivity().findViewById(R.id.list_historico_emprestimos);
        // cria um listHistoricoEmprestimos com a lista de emprestimos
        HistoricalListAdapter listEmprestimosAdaptador = new HistoricalListAdapter(getActivity(), listaEmprestimos);
        // define o apadtador do listView
        listViewEmprestimos.setAdapter(listEmprestimosAdaptador);

        listEmprestimosAdaptador.notifyDataSetChanged();

        listViewEmprestimos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Passando o emprestimoDTO pelo bundle
                Intent i = new Intent(view.getContext(), LoanDetailsActivity.class);
                i.putExtra("emprestimo", (Serializable) listViewEmprestimos.getAdapter().getItem(position));
                startActivity(i);
            }
        });

        //listViewEmprestimos.setOnScrollListener(new EndlessScrollListener());
        /*listViewEmprestimos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Passando o emprestimoDTO pelo bundle
                Intent i = new Intent(view.getContext(), LoanDetailsActivity.class);
                i.putExtra("emprestimo", (Serializable) listViewEmprestimos.getAdapter().getItem(position));
                startActivity(i);

                return false;
            }
        });*/
    }

    private class HistoricoEmprestimosTask extends AsyncTask<String, Void, List<EmprestimoDTO>> {

        protected void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), "", getString(R.string.load_loans_history), true);
        }

        protected List<EmprestimoDTO> doInBackground(String... params) {
            try {
                return FacadeDAO.getInstance(getActivity()).getHistoricoEmprestimos(params[0], offsetEmprestimos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<EmprestimoDTO> result) {
            super.onPostExecute(result);
            if (result != null) {
                listaEmprestimos = result;
                prepareListHistorico();
            }
            pd.dismiss();
        }
    }

/*
    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 0;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    offsetEmprestimos = listViewEmprestimos.getAdapter().getCount();

                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                SharedPreferences preferences = getActivity().getSharedPreferences(Constants.KEY_USER_INFO, 0);
                accessToken = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);
                if (accessToken != null) {
                    //TODO Preciso ver como atualiza sem ficar no loading eterno
                    //new EmprestimosAtivosTask().execute(accessToken);
                }
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }
    */


}

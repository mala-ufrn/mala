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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.activity.EmprestimoDetalheActivity;
import br.ufrn.mala.activity.NovoEmprestimoActivity;
import br.ufrn.mala.auxiliar.ListHistoricoEmprestimosAdaptador;
import br.ufrn.mala.connection.FacadeDAO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;
import br.ufrn.mala.util.Constants;

/**
 * Created by paulo on 23/10/17.
 */

public class HistoricoEmprestimosFragment extends Fragment {

    String accessToken;
    int offsetEmprestimos = 0;
    ProgressDialog pd;


    List<EmprestimoDTO> listaEmprestimos;
    ListView listViewEmprestimos;
    FloatingActionButton fab;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getText(R.string.app_my_loans_history));

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        // Pegar o token de acesso
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.KEY_USER_INFO, 0);
        String accessToken = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);

        // Popula a lista de emprestimos
        if (accessToken != null) {
            new EmprestimosAtivosTask().execute(accessToken);
        }
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), NovoEmprestimoActivity.class));
            }

        });

        fab.setImageResource(R.drawable.ic_add_black_24dp);


        return inflater.inflate(R.layout.fragment_list_history_loan, container, false);
    }


    /**
     * Método para preencher a lista de novos empréstimos
     * @param lista
     */
    private void prepareListEmprestimos(List<EmprestimoDTO> lista) {

        listViewEmprestimos = (ListView) getActivity().findViewById(R.id.list_historico_emprestimos);
        // cria um listHistoricoEmprestimos com a lista de emprestimos
        ListHistoricoEmprestimosAdaptador listEmprestimosAdaptador = new ListHistoricoEmprestimosAdaptador(getActivity(), lista);
        // define o apadtador do listView
        listViewEmprestimos.setAdapter(listEmprestimosAdaptador);

        listViewEmprestimos.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int btnPosY = fab.getScrollY();

                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    fab.animate().cancel();
                    fab.animate().translationYBy(150);

                } else if (scrollState == SCROLL_STATE_FLING) {
                    fab.animate().cancel();
                    fab.animate().translationYBy(150);

                } else if (scrollState == SCROLL_STATE_IDLE) {
                    fab.animate().cancel();
                    fab.animate().translationY(btnPosY);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        //listViewEmprestimos.setOnScrollListener(new EndlessScrollListener());
        listViewEmprestimos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Passando o emprestimoDTO pelo bundle
                Intent i = new Intent(view.getContext(), EmprestimoDetalheActivity.class);
                i.putExtra("emprestimo", (Serializable) listViewEmprestimos.getAdapter().getItem(position));
                startActivity(i);

                return false;
            }
        });
    }


    private class EmprestimosAtivosTask extends AsyncTask<String, Void, List<EmprestimoDTO>> {

        protected void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), "", "loading", true);
        }

        protected List<EmprestimoDTO> doInBackground(String... params) {
            try {
                return FacadeDAO.getInstance(getActivity()).getHistoricoEmprestimos(params[0], offsetEmprestimos);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JsonStringInvalidaException e) {
                // Toast.makeText(getActivity(), "Ocorreu algum erro interno", Toast.LENGTH_SHORT).show();
            } catch (ConnectionException e) {
                // Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<EmprestimoDTO> result) {
            super.onPostExecute(result);
            if (result != null) {
                if (listaEmprestimos != null){
                    listaEmprestimos.addAll(result);
                }else {
                    listaEmprestimos = result;
                }
                prepareListEmprestimos(listaEmprestimos);
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

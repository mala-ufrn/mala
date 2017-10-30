package br.ufrn.mala.activity.Fragment;

import android.animation.TimeInterpolator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.activity.NovoEmprestimoActivity;
import br.ufrn.mala.auxiliar.ListEmprestimosAdaptador;
import br.ufrn.mala.auxiliar.ListHistoricoEmprestimosAdaptador;
import br.ufrn.mala.connection.FachadaAPI;
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
    ExpandableListView expandableListViewEmprestimo;
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


        return inflater.inflate(R.layout.fragment_list_loan, container, false);
    }

    private void prepareListEmprestimos(List<EmprestimoDTO> lista) {
        //Preenchendo lista de empréstimos ativos
        expandableListViewEmprestimo = (ExpandableListView) getActivity().findViewById(R.id.list_emprestimos);

        // cria os grupos
        List<String> listaGrupos = new ArrayList<>();
        listaGrupos.add("Normais");
        listaGrupos.add("Especiais");
        listaGrupos.add("Fotocópias");

        // cria os itens de cada grupo
        List<EmprestimoDTO> listaNormais = new ArrayList<>();
        for (EmprestimoDTO emprestimo : listaEmprestimos)
            if (emprestimo.getTipoEmprestimo().equals("NORMAL"))
                listaNormais.add(emprestimo);

        List<EmprestimoDTO> listaEspeciais = new ArrayList<>();
        for (EmprestimoDTO emprestimo : listaEmprestimos)
            if (emprestimo.getTipoEmprestimo().equals("ESPECIAL"))
                listaEspeciais.add(emprestimo);

        List<EmprestimoDTO> listaFotocopias = new ArrayList<>();
        for (EmprestimoDTO emprestimo : listaEmprestimos)
            if (emprestimo.getTipoEmprestimo().equals("FOTOCÓPIA"))
                listaFotocopias.add(emprestimo);

        // cria o "relacionamento" dos grupos com seus itens
        HashMap<String, List<EmprestimoDTO>> listaItensGrupo = new HashMap<>();
        listaItensGrupo.put(listaGrupos.get(0), listaNormais);
        listaItensGrupo.put(listaGrupos.get(1), listaEspeciais);
        listaItensGrupo.put(listaGrupos.get(2), listaFotocopias);

        // cria um listEmprestimosAdaptador (BaseExpandableListAdapter) com os dados acima
        ListHistoricoEmprestimosAdaptador listEmprestimosAdaptador = new ListHistoricoEmprestimosAdaptador(getActivity(), listaGrupos, listaItensGrupo);
        // define o apadtador do ExpandableListView
        expandableListViewEmprestimo.setAdapter(listEmprestimosAdaptador);

        expandableListViewEmprestimo.setOnScrollListener(new AbsListView.OnScrollListener() {
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

        expandableListViewEmprestimo.setOnScrollListener(new EndlessScrollListener());

        // Expande todos os grupos do ListView
        for (int i = 0; i < expandableListViewEmprestimo.getExpandableListAdapter().getGroupCount(); i++)
            expandableListViewEmprestimo.expandGroup(i);

    }


    private class EmprestimosAtivosTask extends AsyncTask<String, Void, List<EmprestimoDTO>> {

        protected void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), "", "loading", true);
        }

        protected List<EmprestimoDTO> doInBackground(String... params) {
            try {
                return FachadaAPI.getInstance(getActivity()).getHistoricoEmprestimos(params[0], offsetEmprestimos);
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
                    offsetEmprestimos = expandableListViewEmprestimo.getAdapter().getCount();

                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                SharedPreferences preferences = getActivity().getSharedPreferences(Constants.KEY_USER_INFO, 0);
                accessToken = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);
                if (accessToken != null) {
                    new EmprestimosAtivosTask().execute(accessToken);
                }
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }


}

package br.ufrn.mala.activity.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.activity.LoanDetailsActivity;
import br.ufrn.mala.activity.MainActivity;
import br.ufrn.mala.activity.NewLoanActivity;
import br.ufrn.mala.auxiliar.LoanListAdapter;
import br.ufrn.mala.connection.FacadeDAO;
import br.ufrn.mala.dto.EmprestimoDTO;

/**
 * Created by paulo on 23/10/17.
 */

public class LoanListFragment extends Fragment {

    int offsetEmprestimos = 0;

    private ProgressDialog pd;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<EmprestimoDTO> listaEmprestimos;
    private ExpandableListView expandableListViewEmprestimo;

    private String accessToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_loan_list, container, false);

        getActivity().setTitle(getResources().getText(R.string.my_loans));

        ((NavigationView)getActivity().findViewById(R.id.nav_view)).getMenu().getItem(0).setChecked(true);

        MainActivity ma = ((MainActivity)getActivity());
        ma.showThreeDotsMenu(true);

        FloatingActionButton fab = (FloatingActionButton) ma.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), NewLoanActivity.class));
            }
        });
        fab.setImageResource(R.drawable.ic_add_black_24dp);

        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary_lighter));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                 @Override
                 public void onRefresh() {
                     refreshList();
                 }
         });

        accessToken = ((MainActivity)getActivity()).getAccessToken();

        refreshList();

        return fragmentView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // Muda o título
            getActivity().setTitle(getResources().getText(R.string.my_loans));

            // Marca o Nav menu (para caso de backbutton)
            ((NavigationView)getActivity().findViewById(R.id.nav_view)).getMenu().getItem(0).setChecked(true);

            // Ativa o Fab e o ThreeDotsMenu
            MainActivity ma = ((MainActivity)getActivity());
            ma.showThreeDotsMenu(true);
            FloatingActionButton fab = (FloatingActionButton) ma.findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);

            // Expande o ExpandableView e mostra o topo
            if (expandableListViewEmprestimo != null) {
                for (int i = 0; i < expandableListViewEmprestimo.getExpandableListAdapter().getGroupCount(); i++)
                    expandableListViewEmprestimo.expandGroup(i);
                expandableListViewEmprestimo.setSelectionAfterHeaderView();
            }
        }
    }


    public void refreshList() {
        if (accessToken != null) {
            new EmprestimosAtivosTask().execute(accessToken);
            ((MainActivity)getActivity()).refreshLoans = true;
        }
    }

    private void prepareListEmprestimos() {
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

        // cria um loanListAdapter (BaseExpandableListAdapter) com os dados acima
        LoanListAdapter loanListAdapter = new LoanListAdapter(getActivity(), listaGrupos, listaItensGrupo);
        // define o apadtador do ExpandableListView
        expandableListViewEmprestimo.setAdapter(loanListAdapter);


        // Expande todos os grupos do ListView
        for (int i = 0; i < expandableListViewEmprestimo.getExpandableListAdapter().getGroupCount(); i++)
            expandableListViewEmprestimo.expandGroup(i);

        expandableListViewEmprestimo.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                Intent i = new Intent(view.getContext(), LoanDetailsActivity.class);
                i.putExtra("emprestimo", (Serializable) expandableListViewEmprestimo.getExpandableListAdapter().getChild(groupPosition, childPosition));
                startActivity(i);

                return false;
            }
        });
    }

    private class EmprestimosAtivosTask extends AsyncTask<String, Void, List<EmprestimoDTO>> {

        protected void onPreExecute() {
            if (!swipeRefreshLayout.isRefreshing())
                pd = ProgressDialog.show(getActivity(), "", getString(R.string.load_active_loans), true);
        }

        protected List<EmprestimoDTO> doInBackground(String... params) {
            try {
                return FacadeDAO.getInstance(getActivity()).getEmprestimosAtivos(offsetEmprestimos);
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
                prepareListEmprestimos();
            }
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            else
                pd.dismiss();
        }
    }


}

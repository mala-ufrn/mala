package br.ufrn.mala.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.auxiliar.ListEmprestimosAdaptador;
import br.ufrn.mala.connection.FachadaAPI;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;
import br.ufrn.mala.util.Constants;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog pd;
    ExpandableListView expandableListViewEmprestimo;
    List<EmprestimoDTO> listaEmprestimos;

    private Integer offsetEmprestimos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_principal);
        offsetEmprestimos = 0;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Pegar o token de acesso
        SharedPreferences preferences = this.getSharedPreferences(Constants.KEY_USER_INFO, 0);
        String accessToken = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);

        // Popula a lista de emprestimos
        if (accessToken != null) {
            new PrincipalActivity.EmprestimosAtivosTask().execute(accessToken);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.act_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_newLoan) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.myLoan) {
            // Handle the myLoan activity
        } else if (id == R.id.loan_historical) {

        } else if (id == R.id.changePass_Sisbi) {

        } else if (id == R.id.issue_discharge) {
            //Quitação de vinculo
        } else if (id == R.id.faq) {

        } else if (id == R.id.aboutUs) {

        } else if (id == R.id.exit) {
            sair(findViewById(R.id.principal_view));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.principal_view);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void sair(View view) {
        SharedPreferences preferences = this.getSharedPreferences(Constants.KEY_USER_INFO, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        try {
            File dir = this.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.finish();
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }

    private void prepareListEmprestimos() {
        //Preenchendo lista de empréstimos ativos
        expandableListViewEmprestimo = (ExpandableListView) findViewById(R.id.list_emprestimos);

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
        ListEmprestimosAdaptador listEmprestimosAdaptador = new ListEmprestimosAdaptador(this, listaGrupos, listaItensGrupo);
        // define o apadtador do ExpandableListView
        expandableListViewEmprestimo.setAdapter(listEmprestimosAdaptador);
        // Expande todos os grupos do ListView
        for (int i = 0; i < expandableListViewEmprestimo.getExpandableListAdapter().getGroupCount(); i++)
            expandableListViewEmprestimo.expandGroup(i);
    }


    private class EmprestimosAtivosTask extends AsyncTask<String, Void, List<EmprestimoDTO>> {

        protected void onPreExecute() {
            pd = ProgressDialog.show(PrincipalActivity.this, "", "loading", true);
        }

        protected List<EmprestimoDTO> doInBackground(String... params) {
            try {
                return FachadaAPI.getInstance(PrincipalActivity.this).getEmprestimosAtivos(params[0], offsetEmprestimos);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JsonStringInvalidaException e) {
                Toast.makeText(PrincipalActivity.this, "Ocorreu algum erro interno", Toast.LENGTH_SHORT).show();
            } catch (ConnectionException e) {
                Toast.makeText(PrincipalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            pd.dismiss();
        }
    }

}

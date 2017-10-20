package br.ufrn.mala.activity;

import android.content.SharedPreferences;
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
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.auxiliar.LoanListAdapter;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.util.Constants;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LoanListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader; //TODO Criar model para emprestimo.
    HashMap<String, List<EmprestimoDTO>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_principal);
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


        // Aqui são os códigos fora do layout padrão

        expListView = (ExpandableListView) findViewById(R.id.list_emprestimos);

        // prepara os dados da lista - mocados até entao
        prepareListData();

        listAdapter = new LoanListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);


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
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        finish();
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

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String,List<EmprestimoDTO>>();

        // Adding child data
        listDataHeader.add("Normais");
        listDataHeader.add("Especiais");
        listDataHeader.add("Fotocópia");

        // Adding child data
        List<EmprestimoDTO> top250 = new ArrayList<EmprestimoDTO>();
        EmprestimoDTO auxDTO = new EmprestimoDTO();

        auxDTO.setTitulo("Assim falou mamãe");
        auxDTO.setAutor("Friedrich Nietzsche");
        auxDTO.setBiblioteca("Biblioteca Central Zila Mamede");
        auxDTO.setDataDevolucao(Calendar.getInstance().getTimeInMillis());

        top250.add(auxDTO);
        auxDTO = new EmprestimoDTO();

        auxDTO.setTitulo("A Hora da Estrela");
        auxDTO.setAutor("Clarice Linspector");
        auxDTO.setBiblioteca("Biblioteca Central Zila Mamede");
        auxDTO.setDataDevolucao(Calendar.getInstance().getTimeInMillis());

        top250.add(auxDTO);
        auxDTO = new EmprestimoDTO();
        auxDTO.setTitulo("Como ser bom no fifinha");
        auxDTO.setAutor("Joel Minion");
        auxDTO.setBiblioteca("Biblioteca Central Zila Mamede");
        auxDTO.setDataDevolucao(Calendar.getInstance().getTimeInMillis());

        top250.add(auxDTO);

        auxDTO = new EmprestimoDTO();
        auxDTO.setTitulo("Como ser bom no fifinha com um texto extremamente grande de verdade grande mesmo aqui oh como é grante");
        auxDTO.setAutor("Joel Minion");
        auxDTO.setBiblioteca("Biblioteca Central Zila Mamede");
        auxDTO.setDataDevolucao(Calendar.getInstance().getTimeInMillis());
        top250.add(auxDTO);

        List<String> nowShowing = new ArrayList<String>();


        List<String> comingSoon = new ArrayList<String>();


        listDataChild.put(listDataHeader.get(0), top250); // Header, qtd, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);


    }
}

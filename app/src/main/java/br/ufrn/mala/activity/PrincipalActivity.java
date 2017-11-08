package br.ufrn.mala.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;

import com.google.gson.Gson;

import br.ufrn.mala.R;
import br.ufrn.mala.activity.Fragment.EmprestimosFragment;
import br.ufrn.mala.activity.Fragment.HistoricoEmprestimosFragment;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.util.Constants;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager fm;

    private UsuarioDTO usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.principal_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Pegar o token de acesso
        SharedPreferences preferences = this.getSharedPreferences(Constants.KEY_USER_INFO, 0);
        String accessToken = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);

        Gson gson = new Gson();
        String usuario = preferences.getString("UsuarioLogado", null);
        if (usuario != null)
            usuarioLogado = gson.fromJson(usuario, UsuarioDTO.class);

        View header = navigationView.getHeaderView(0);
        TextView profName = (TextView)header.findViewById(R.id.profile_name);
        profName.setText(usuarioLogado.getNomePessoa());

        // Cria um novo fragment
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_content, new EmprestimosFragment());
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.principal_view);
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
        Fragment fragment = fm.findFragmentById(R.id.fragment_content);
        int id = item.getItemId();

        if (id == R.id.myLoan) {


            FragmentTransaction ft = fm.beginTransaction();
            fm.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.remove(fragment);
            ft.add(R.id.fragment_content, new EmprestimosFragment());
            ft.commit();
        } else if (id == R.id.loan_historical) {
            FragmentTransaction ft = fm.beginTransaction();
            fm.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.remove(fragment);
            ft.add(R.id.fragment_content, new HistoricoEmprestimosFragment());
            ft.commit();

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
}

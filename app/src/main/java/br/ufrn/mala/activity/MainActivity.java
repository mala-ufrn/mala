package br.ufrn.mala.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

import com.google.gson.Gson;

import br.ufrn.mala.R;
import br.ufrn.mala.activity.Fragment.LoanListFragment;
import br.ufrn.mala.activity.Fragment.HistoricalListFragment;
import br.ufrn.mala.activity.Fragment.SearchFragment;
import br.ufrn.mala.connection.FacadeDAO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.util.Constants;
/**
 * Created by Paulo Lopes on 12/10/17.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public boolean refreshLoans;

    private Menu threeDotsMenu;
    private int previousMenuItemSelected;
    private FragmentManager fm;
    private UsuarioDTO usuarioLogado;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.principal_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View v = getCurrentFocus();

                if (v != null && v instanceof EditText) {
                        hideKeyboard(MainActivity.this);
                        v.clearFocus();
                    }

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Pegar o token de acesso
        SharedPreferences preferences = this.getSharedPreferences(Constants.KEY_USER_INFO, 0);
        accessToken = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);

        Gson gson = new Gson();
        String usuario = preferences.getString("UsuarioLogado", null);
        if (usuario != null)
            usuarioLogado = gson.fromJson(usuario, UsuarioDTO.class);

        View header = navigationView.getHeaderView(0);
        TextView profName = (TextView)header.findViewById(R.id.profile_name);
        profName.setText(usuarioLogado.getNomePessoa());

        // Cria um novo fragment
        fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.fragment_content, new LoanListFragment(), "LoanList")
                .commit();

        previousMenuItemSelected = R.id.my_loan;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    //Extreme Go Horse Rulez!
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.principal_view);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment loanList = fm.findFragmentByTag("LoanList");

            if (loanList != null) {
                boolean flag = true;
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : fm.getFragments())
                    if (!(f.isHidden()) && !(f instanceof LoanListFragment)) {
                        ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
                        ft.hide(f);
                        flag = false;
                    }
                if (flag) {
                    if(Build.VERSION.SDK_INT >= 16)
                        this.finishAffinity();
                    super.onBackPressed();
                }

                ft.show(loanList).commit();
                previousMenuItemSelected = R.id.my_loan;
            } else
                fm.beginTransaction()
                        .add(R.id.fragment_content, new LoanListFragment(), "LoanList")
                        .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        threeDotsMenu = menu;
        getMenuInflater().inflate(R.menu.act_main_three_dots, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload) {
            LoanListFragment loanList = (LoanListFragment)fm.findFragmentByTag("LoanList");
            if (loanList != null) {
                loanList.refreshList();
            }
        }
        else if (id == R.id.action_newLoan) {
            startActivity(new Intent(this, NewLoanActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showThreeDotsMenu(boolean showMenu){
        if(threeDotsMenu == null)
            return;
        threeDotsMenu.setGroupVisible(R.id.act_loan_three_dots_group, showMenu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id != previousMenuItemSelected) {
            previousMenuItemSelected = id;

            FragmentTransaction ft = fm.beginTransaction();

            // esconde todos os fragmentos
            for (Fragment f : fm.getFragments())
                ft.hide(f);

            if (id == R.id.my_loan) {
                Fragment loanList = fm.findFragmentByTag("LoanList");
                if (loanList != null) {

                    ft.show(loanList)
                            .commit();
                } else
                    ft.add(R.id.fragment_content, new LoanListFragment(), "LoanList")
                            .commit();
            } else if (id == R.id.loan_historical) {
                Fragment histList = fm.findFragmentByTag("HistList");

                if (histList != null) {
                    ft.show(histList)
                            .commit();
                } else
                    ft.add(R.id.fragment_content, new HistoricalListFragment(), "HistList")
                            .commit();
            } else if (id == R.id.search_in_collection) {
                Fragment searchFrag = fm.findFragmentByTag("Search");

                if (searchFrag != null) {
                    ft.show(searchFrag)
                            .commit();
                } else
                    ft.add(R.id.fragment_content, new SearchFragment(), "Search")
                            .commit();
            } else if (id == R.id.change_pass_sisbi) {
                // Mudar Senha da Biblioteca
            } else if (id == R.id.issue_discharge) {
                // Quitação de vinculo
            } else if (id == R.id.faq) {
                // Tela de FAQs
            } else if (id == R.id.about_us) {
                // Tela dos Desenvolvedores

            } else if (id == R.id.exit) {
                quit(findViewById(R.id.principal_view));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.principal_view);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void quit(View view) {
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

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}

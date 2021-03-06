package br.ufrn.mala.activity.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.activity.MainActivity;
import br.ufrn.mala.activity.SearchResultsListActivity;
import br.ufrn.mala.connection.FacadeDAO;
import br.ufrn.mala.dto.BibliotecaDTO;
import br.ufrn.mala.dto.TipoMaterialDTO;

/**
 * Created by Hugo e Mirna on 27/11/17.
 */

public class SearchFragment extends Fragment {

    private ProgressDialog pd;
    private EditText titleInput, authorInput, subjectInput;
    private Spinner libSpinner, matTypeSpinner;
    private Button searchBtn, clearBtn;
    private String accessToken, inputTitle, inputAuthor, inputSubject;
    private BibliotecaDTO inputBiblioteca;
    private TipoMaterialDTO inputTipoMaterial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        accessToken = ((MainActivity)getActivity()).getAccessToken();

        // Muda o título na ActionBar
        getActivity().setTitle(getResources().getText(R.string.app_search));
        // Esconde o fab e o menu de três pontos
        MainActivity ma = ((MainActivity)getActivity());
        ma.showThreeDotsMenu(false);
        FloatingActionButton fab = (FloatingActionButton)ma.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleInput = (EditText)getActivity().findViewById(R.id.search_title_input);
        authorInput = (EditText)getActivity().findViewById(R.id.search_author_input);
        subjectInput = (EditText)getActivity().findViewById(R.id.search_subject_input);
        libSpinner = (Spinner)getActivity().findViewById(R.id.search_lib_spinner);
        matTypeSpinner = (Spinner)getActivity().findViewById(R.id.search_mat_type_spinner);

        clearBtn = (Button)getActivity().findViewById(R.id.btn_clear_form);
        searchBtn = (Button)getActivity().findViewById(R.id.btn_search_form);

        // SharedPreferences preferences = getActivity().getSharedPreferences(Constants.KEY_USER_INFO, 0);
        // String accessToken = preferences.getString(Constants.KEY_ACCESS_TOKEN, null);

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearForm();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inputTitle = titleInput.getText().toString();
                inputAuthor = authorInput.getText().toString();
                inputSubject = subjectInput.getText().toString();
                inputBiblioteca = (BibliotecaDTO)libSpinner.getSelectedItem();
                inputTipoMaterial = (TipoMaterialDTO)matTypeSpinner.getSelectedItem();

                MainActivity.hideKeyboard(getActivity());

                if (inputTitle.equalsIgnoreCase("") && inputAuthor.equalsIgnoreCase("") &&
                        inputSubject.equalsIgnoreCase("") && inputBiblioteca.getIdBiblioteca() == null &&
                        inputTipoMaterial.getIdTipoMaterial() == null) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            getString(R.string.no_search_fields), Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    if (accessToken != null) {
                        new ClearSearchDB().execute();
                    }
                }
            }
        });

        populateLibrarySpinners();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // Muda o título na ActionBar
            getActivity().setTitle(getResources().getText(R.string.app_search));

            // Esconde o fab e o menu de três pontos
            MainActivity ma = ((MainActivity)getActivity());
            ma.showThreeDotsMenu(false);
            FloatingActionButton fab = (FloatingActionButton)ma.findViewById(R.id.fab);
            fab.setVisibility(View.GONE);
        }
        else {
            clearForm();
        }
    }

    private void populateLibrarySpinners() {
        new FillLibrariesSpinnerTask().execute();
        new FillMatTypeSpinnerTask().execute();
    }

    private void clearForm() {
        titleInput.setText("");
        titleInput.clearFocus();
        authorInput.setText("");
        authorInput.clearFocus();
        subjectInput.setText("");
        subjectInput.clearFocus();
        libSpinner.setSelection(0);
        matTypeSpinner.setSelection(0);
    }

    private class ClearSearchDB extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "", getString(R.string.please_wait), true);
        }

        protected Boolean doInBackground(Void... params) {
            try {
                FacadeDAO.getInstance(getActivity()).limparAcervoBD();
                return Boolean.TRUE;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                String lib = (inputBiblioteca.getIdBiblioteca() == null)? "" : inputBiblioteca.getIdBiblioteca().toString();
                String type = (inputTipoMaterial.getIdTipoMaterial() == null)? "":inputTipoMaterial.getIdTipoMaterial().toString();

                new SearchInCollectionTask().execute(accessToken, inputTitle, inputAuthor, inputSubject, lib, type);
            }
            else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.no_connection), Toast.LENGTH_SHORT);
                toast.show();
            }
            super.onPostExecute(aBoolean);
        }
    }

    private class SearchInCollectionTask extends AsyncTask<String, Integer, Integer> {

        protected void onPreExecute() {
            pd.setMessage(getString(R.string.search_collection));
        }

        protected Integer doInBackground(String... params) {
            int answer, offset = 0;

            do {
                // Valor para informar que houve um erro intermediário
                answer = 101;
                try {
                    // Pega os número de títulos retornado
                    answer = FacadeDAO.getInstance(getActivity()).buscarAcervo(params[0], params[1],
                            params[2], params[3], params[4], params[5], offset);

                    // Repassa erro ou insucesso na busca
                    if (answer <= 0)
                        return answer;

                    offset += answer;

                    if (answer == 100 && offset != 300) {
                        // atualiza o texto do Process Dialog
                        publishProgress(offset);
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
            } while (answer == 100 && offset <= 200);

            if (answer == 100 && offset == 300) {
                return 102;
            }
            if (answer == 101 && offset == 0)
                return -1;

            return answer;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pd.setMessage(getString(R.string.search_progress1) + " " + values[0] + " " + getString(R.string.search_progress2));
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            pd.dismiss();

            if (result >= 0) {
                Intent i = new Intent(getActivity(), SearchResultsListActivity.class);
                if (result > 100) {
                    i.putExtra("SearchWarning", result);
                }
                startActivity(i);
            }
            else if (result == -1) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.no_connection), Toast.LENGTH_SHORT);
                toast.show();
            }
            else if (result == -2) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.titles_not_found), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class FillLibrariesSpinnerTask extends AsyncTask<String, Void, List<BibliotecaDTO>> {

        protected List<BibliotecaDTO> doInBackground(String... params) {
            try {
                return FacadeDAO.getInstance(getActivity()).getBibliotecas(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<BibliotecaDTO> result) {
            super.onPostExecute(result);
            if (result != null) {
                ArrayList<BibliotecaDTO> libArrayList = new ArrayList<>(result);

                BibliotecaDTO empty = new BibliotecaDTO();
                libArrayList.add(0, empty);

                ArrayAdapter<BibliotecaDTO> libArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_search_item, libArrayList);
                libArrayAdapter.setDropDownViewResource(R.layout.spinner_search_dropdown_item);
                libSpinner.setAdapter(libArrayAdapter);
            }
        }
    }

    private class FillMatTypeSpinnerTask extends AsyncTask<String, Void, List<TipoMaterialDTO>> {

        protected void onPreExecute() {
        }

        protected List<TipoMaterialDTO> doInBackground(String... params) {
            try {
                return FacadeDAO.getInstance(getActivity()).getTiposMaterial();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<TipoMaterialDTO> result) {
            super.onPostExecute(result);
            if (result != null) {
                ArrayList<TipoMaterialDTO> matTypeArrayList = new ArrayList<>(result);

                TipoMaterialDTO empty = new TipoMaterialDTO();
                matTypeArrayList.add(0, empty);

                ArrayAdapter<TipoMaterialDTO> matTypeArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_search_item, matTypeArrayList);
                matTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_search_dropdown_item);
                matTypeSpinner.setAdapter(matTypeArrayAdapter);
            }
        }
    }
}

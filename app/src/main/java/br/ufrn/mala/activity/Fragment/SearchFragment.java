package br.ufrn.mala.activity.Fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.activity.LoanDetailsActivity;
import br.ufrn.mala.activity.MainActivity;
import br.ufrn.mala.connection.FacadeDAO;
import br.ufrn.mala.dto.BibliotecaDTO;
import br.ufrn.mala.dto.TipoMaterialDTO;
import br.ufrn.mala.util.Constants;

/**
 * Created by Hugo e Mirna on 27/11/17.
 */

public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Muda o título na ActionBar
        getActivity().setTitle(getResources().getText(R.string.app_search));
        // Esconde o fab e o menu de três pontos
        MainActivity ma = ((MainActivity)getActivity());
        ma.showThreeDotsMenu(false);
        FloatingActionButton fab = (FloatingActionButton)ma.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        populateLibrarySpinners();

        return inflater.inflate(R.layout.fragment_search, container, false);
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
    }

    private void populateLibrarySpinners() {
        new FillLibrariesSpinnerTask().execute();
        new FillMatTypeSpinnerTask().execute();
    }

    private class FillLibrariesSpinnerTask extends AsyncTask<String, Void, List<BibliotecaDTO>> {

        protected void onPreExecute() {
        }

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
                Spinner libSpinner = (Spinner)getActivity().findViewById(R.id.search_lib_spinner);

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
                Spinner matTypeSpinner = (Spinner)getActivity().findViewById(R.id.search_mat_type_spinner);

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

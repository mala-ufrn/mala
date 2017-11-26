package br.ufrn.mala.connection;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.squareup.moshi.Json;

import java.io.IOException;
import java.util.List;

import br.ufrn.mala.dto.BibliotecaDTO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.MaterialInformacionalDTO;
import br.ufrn.mala.dto.SituacaoMaterialDTO;
import br.ufrn.mala.dto.StatusMaterialDTO;
import br.ufrn.mala.dto.TipoMaterialDTO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;

/**
 * Estratégia concreta <i>(ConcreteStrategy)</i> de acesso aos dados <i>(DAO)</i> por meio da consulta na API da UFRN.
 *
 * @author Joel Felipe
 * @since 04/11/2017
 * @see <a href="https://pt.wikipedia.org/wiki/Strategy">Strategy</a>
 * @see <a href="https://pt.wikipedia.org/wiki/Objeto_de_acesso_a_dados">DAO</a>
 * @see <a href="https://pt.wikipedia.org/wiki/Singleton">Singleton</a>
 */

public class APIStrategyDAO implements StrategyDAO {
    private static APIStrategyDAO apiStrategyDAO;
    private static SQLiteStrategyDAO sqLiteStrategyDAO;
    private APIConnection apiConnection;
    private SQLiteConnection sqLiteConnection;

    public static APIStrategyDAO getInstance(Context context){
        if(apiStrategyDAO == null)
            apiStrategyDAO = new APIStrategyDAO(context);
        return apiStrategyDAO;
    }

    private APIStrategyDAO(Context context) {
        apiConnection = APIConnection.getInstance(context);
        sqLiteConnection = SQLiteConnection.getInstance(context);
        sqLiteStrategyDAO = SQLiteStrategyDAO.getInstance(context);
    }

    @Override
    public UsuarioDTO getUsuarioLogado(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        String usuarioLogado = apiConnection.getUsuarioLogado(token);
        return JsonToObject.toUsuario(usuarioLogado);
    }

    @Override
    public Integer getQuantidadeHistoricoEmprestimos(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        return apiConnection.getQuantidadeEmprestimos(token, false);
    }

    @Override
    public List<EmprestimoDTO> getHistoricoEmprestimos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        SparseArray<BibliotecaDTO> bibliotecas = new SparseArray<>();
        for (BibliotecaDTO biblioteca: sqLiteConnection.getBibliotecas())
            bibliotecas.put(biblioteca.getIdBiblioteca(), biblioteca);
        String emprestimosJson = apiConnection.getEmprestimos(token, false, offset);
        List<EmprestimoDTO> historicoEmprestimos = JsonToObject.toEmprestimos(emprestimosJson);
        for (EmprestimoDTO emprestimo: historicoEmprestimos)
            emprestimo.setBiblioteca(bibliotecas.get(emprestimo.getIdBiblioteca()));
        sqLiteStrategyDAO.insertEmprestimos(historicoEmprestimos, false);
        return historicoEmprestimos;
    }

    @Override
    public List<EmprestimoDTO> getEmprestimosAtivos(String token, Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        SparseArray<BibliotecaDTO> bibliotecas = new SparseArray<>();
        for (BibliotecaDTO biblioteca: sqLiteConnection.getBibliotecas())
            bibliotecas.put(biblioteca.getIdBiblioteca(), biblioteca);
        String emprestimosJson = apiConnection.getEmprestimos(token, true, offset);
        List<EmprestimoDTO> emprestimosAtivos = JsonToObject.toEmprestimos(emprestimosJson);
        for (EmprestimoDTO emprestimo: emprestimosAtivos)
            emprestimo.setBiblioteca(bibliotecas.get(emprestimo.getIdBiblioteca()));
        sqLiteStrategyDAO.insertEmprestimos(emprestimosAtivos, true);
        return emprestimosAtivos;
    }

    public MaterialInformacionalDTO getMaterialInformacional(String token, String codBarras) throws IOException, JsonStringInvalidaException, ConnectionException {
        String materialJson = apiConnection.getMaterialInformacional(token, codBarras);
        if (!materialJson.equalsIgnoreCase("[]")) {
            SparseArray<BibliotecaDTO> bibliotecaSparseArray = new SparseArray<>();
            for (BibliotecaDTO biblioteca: sqLiteConnection.getBibliotecas())
                bibliotecaSparseArray.put(biblioteca.getIdBiblioteca(), biblioteca);
            SparseArray<SituacaoMaterialDTO> situacaoMaterialSparseArray = new SparseArray<>();
            for (SituacaoMaterialDTO situacaoMaterial: sqLiteConnection.getSituacoesMaterial())
                situacaoMaterialSparseArray.put(situacaoMaterial.getIdSituacaoMaterial(), situacaoMaterial);
            SparseArray<StatusMaterialDTO> statusMaterialSparseArray = new SparseArray<>();
            for (StatusMaterialDTO statusMaterial : sqLiteConnection.getStatusMateriais())
                statusMaterialSparseArray.put(statusMaterial.getIdStatusMaterial(), statusMaterial);
            SparseArray<TipoMaterialDTO> tipoMaterialparseArray = new SparseArray<>();
            for (TipoMaterialDTO tipoMaterial : sqLiteConnection.getTiposMaterial())
                tipoMaterialparseArray.put(tipoMaterial.getIdTipoMaterial(), tipoMaterial);
            MaterialInformacionalDTO materialInformacional = JsonToObject.toMaterialInformacional(materialJson);
            materialInformacional.setBiblioteca(bibliotecaSparseArray.get(materialInformacional.getIdBiblioteca()));
            materialInformacional.setSituacaoMaterial(situacaoMaterialSparseArray.get(materialInformacional.getIdSituacaoMaterial()));
            materialInformacional.setStatusMaterial(statusMaterialSparseArray.get(materialInformacional.getIdStatusMaterial()));
            materialInformacional.setTipoMaterial(tipoMaterialparseArray.get(materialInformacional.getIdTipoMaterial()));
            return materialInformacional;
        }
        else {
            return null;
        }
    }

    public boolean loadBibliotecas(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        String bibliotecasJson = apiConnection.getBibliotecas(token);
        List<BibliotecaDTO> bibliotecas = JsonToObject.toBibliotecas(bibliotecasJson);
        if (!bibliotecas.isEmpty()) {
            sqLiteStrategyDAO.insertBibliotecas(bibliotecas);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean loadSituacoesMaterial(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        String situacoesJson = apiConnection.getSituacoesMaterial(token);
        List<SituacaoMaterialDTO> situacoesMaterial = JsonToObject.toSituacoesMaterial(situacoesJson);
        if (!situacoesMaterial.isEmpty()) {
            sqLiteStrategyDAO.insertSituacoesMaterial(situacoesMaterial);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean loadStatusMaterial(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        String statusJson = apiConnection.getStatusMaterial(token);
        List<StatusMaterialDTO> statusMateriais = JsonToObject.toStatusMaterial(statusJson);
        if (!statusMateriais.isEmpty()) {
            sqLiteStrategyDAO.insertStatusMaterial(statusMateriais);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean loadTiposMaterial(String token) throws IOException, JsonStringInvalidaException, ConnectionException {
        String tiposJson = apiConnection.getTiposMaterial(token);
        List<TipoMaterialDTO> tiposMateriais = JsonToObject.toTiposMaterial(tiposJson);
        if (!tiposMateriais.isEmpty()) {
            sqLiteStrategyDAO.insertTiposMaterial(tiposMateriais);
            return true;
        }
        else {
            return false;
        }
    }
}

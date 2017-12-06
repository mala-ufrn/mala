package br.ufrn.mala.connection;

import android.content.Context;
import android.util.SparseArray;

import org.json.JSONException;

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
        apiStrategyDAO.updateCredetials(context);
        return apiStrategyDAO;
    }

    private APIStrategyDAO(Context context) {
        apiConnection = APIConnection.getInstance(context);
        sqLiteConnection = SQLiteConnection.getInstance(context);
        sqLiteStrategyDAO = SQLiteStrategyDAO.getInstance(context);
    }

    @Override
    public UsuarioDTO getUsuarioLogado() throws IOException, JsonStringInvalidaException, ConnectionException {
        String usuarioLogado = apiConnection.getUsuarioLogado();
        return JsonToObject.toUsuario(usuarioLogado);
    }

    @Override
    public Integer getQuantidadeHistoricoEmprestimos() throws IOException, JsonStringInvalidaException, ConnectionException {
        return apiConnection.getQuantidadeEmprestimos(false);
    }

    @Override
    public List<EmprestimoDTO> getHistoricoEmprestimos(Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        SparseArray<BibliotecaDTO> bibliotecas = new SparseArray<>();
        for (BibliotecaDTO biblioteca: sqLiteConnection.getBibliotecas(false))
            bibliotecas.put(biblioteca.getIdBiblioteca(), biblioteca);
        String emprestimosJson = apiConnection.getEmprestimos(false, offset);
        List<EmprestimoDTO> historicoEmprestimos = JsonToObject.toEmprestimos(emprestimosJson);
        for (EmprestimoDTO emprestimo: historicoEmprestimos)
            emprestimo.setBiblioteca(bibliotecas.get(emprestimo.getIdBiblioteca()));
        sqLiteStrategyDAO.insertEmprestimos(historicoEmprestimos, false);
        return historicoEmprestimos;
    }

    @Override
    public List<EmprestimoDTO> getEmprestimosAtivos(Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        SparseArray<BibliotecaDTO> bibliotecas = new SparseArray<>();
        for (BibliotecaDTO biblioteca: sqLiteConnection.getBibliotecas(false))
            bibliotecas.put(biblioteca.getIdBiblioteca(), biblioteca);
        String emprestimosJson = apiConnection.getEmprestimos(true, offset);
        List<EmprestimoDTO> emprestimosAtivos = JsonToObject.toEmprestimos(emprestimosJson);
        for (EmprestimoDTO emprestimo: emprestimosAtivos)
            emprestimo.setBiblioteca(bibliotecas.get(emprestimo.getIdBiblioteca()));
        sqLiteStrategyDAO.insertEmprestimos(emprestimosAtivos, true);
        return emprestimosAtivos;
    }

    public MaterialInformacionalDTO getMaterialInformacional(String codBarras) throws IOException, JsonStringInvalidaException, ConnectionException {
        String materialJson = apiConnection.getMaterialInformacional(codBarras);
        if (!materialJson.equalsIgnoreCase("[]")) {
            SparseArray<BibliotecaDTO> bibliotecaSparseArray = new SparseArray<>();
            for (BibliotecaDTO biblioteca: sqLiteConnection.getBibliotecas(false))
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

    public int buscarAcervo(String titulo, String autor, String assunto,
                                     String idBiblioteca, String idTipoMaterial, int offset) throws IOException, JsonStringInvalidaException, ConnectionException, JSONException {

            String acervosJson = apiConnection.getAcervo(titulo, autor, assunto, idBiblioteca, idTipoMaterial, offset);

            // Busca não retornou nada
            if (offset == 0 && acervosJson.equalsIgnoreCase("[]"))
                return -2;

            // Conta as linhas inseridas no banco e retorna este valor
            return sqLiteConnection.insertAcervoJsonList(acervosJson, offset);
    }

    public boolean loadBibliotecas() throws IOException, JsonStringInvalidaException, ConnectionException {
        String bibliotecasJson = apiConnection.getBibliotecas();
        List<BibliotecaDTO> bibliotecas = JsonToObject.toBibliotecas(bibliotecasJson);
        if (!bibliotecas.isEmpty()) {
            sqLiteStrategyDAO.insertBibliotecas(bibliotecas);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean loadSituacoesMaterial() throws IOException, JsonStringInvalidaException, ConnectionException {
        String situacoesJson = apiConnection.getSituacoesMaterial();
        List<SituacaoMaterialDTO> situacoesMaterial = JsonToObject.toSituacoesMaterial(situacoesJson);
        if (!situacoesMaterial.isEmpty()) {
            sqLiteStrategyDAO.insertSituacoesMaterial(situacoesMaterial);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean loadStatusMaterial() throws IOException, JsonStringInvalidaException, ConnectionException {
        String statusJson = apiConnection.getStatusMaterial();
        List<StatusMaterialDTO> statusMateriais = JsonToObject.toStatusMaterial(statusJson);
        if (!statusMateriais.isEmpty()) {
            sqLiteStrategyDAO.insertStatusMaterial(statusMateriais);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean loadTiposMaterial() throws IOException, JsonStringInvalidaException, ConnectionException {
        String tiposJson = apiConnection.getTiposMaterial();
        List<TipoMaterialDTO> tiposMateriais = JsonToObject.toTiposMaterial(tiposJson);
        if (!tiposMateriais.isEmpty()) {
            sqLiteStrategyDAO.insertTiposMaterial(tiposMateriais);
            return true;
        }
        else {
            return false;
        }
    }

    public void updateCredetials(Context context){
        apiConnection.updateCredentials(context);
    }
}

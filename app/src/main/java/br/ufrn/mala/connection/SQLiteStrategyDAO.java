package br.ufrn.mala.connection;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import br.ufrn.mala.dto.AcervoDTO;
import br.ufrn.mala.dto.BibliotecaDTO;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.dto.PoliticaEmprestimoDTO;
import br.ufrn.mala.dto.SituacaoMaterialDTO;
import br.ufrn.mala.dto.StatusMaterialDTO;
import br.ufrn.mala.dto.TipoMaterialDTO;
import br.ufrn.mala.dto.UsuarioDTO;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;

/**
 * Estratégia concreta <i>(ConcreteStrategy)</i> de acesso aos dados <i>(DAO)</i> por meio do banco de dados da aplicação.
 *
 * @author Joel Felipe
 * @since 04/11/2017
 * @see <a href="https://pt.wikipedia.org/wiki/Strategy">Strategy</a>
 * @see <a href="https://pt.wikipedia.org/wiki/Objeto_de_acesso_a_dados">DAO</a>
 * @see <a href="https://pt.wikipedia.org/wiki/Singleton">Singleton</a>
 */

public class SQLiteStrategyDAO implements StrategyDAO {

    private static SQLiteStrategyDAO sqLiteStrategyDAO;
    private SQLiteConnection sqLiteConnection;

    public static SQLiteStrategyDAO getInstance(Context context){
        if(sqLiteStrategyDAO == null){
            synchronized (SQLiteStrategyDAO.class){
                if(sqLiteStrategyDAO == null){
                    sqLiteStrategyDAO = new SQLiteStrategyDAO(context);
                }
            }
        }
        return sqLiteStrategyDAO;
    }

    private SQLiteStrategyDAO(Context context) {
        sqLiteConnection = SQLiteConnection.getInstance(context);
    }

    @Override
    public UsuarioDTO getUsuarioLogado() throws IOException, JsonStringInvalidaException, ConnectionException {
        return sqLiteConnection.getUsuarioLogado();
    }

    @Override
    public Integer getQuantidadeHistoricoEmprestimos() throws IOException, JsonStringInvalidaException, ConnectionException {
        return sqLiteConnection.getQuantidadeEmprestimos(false);
    }

    @Override
    public List<EmprestimoDTO> getHistoricoEmprestimos(Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        return sqLiteConnection.getEmprestimos(false, offset);
    }

    @Override
    public List<EmprestimoDTO> getEmprestimosAtivos(Integer offset) throws IOException, JsonStringInvalidaException, ConnectionException {
        return sqLiteConnection.getEmprestimos(true, offset);
    }

    @Override
    public List<PoliticaEmprestimoDTO> getPoliticasEmprestimos() throws IOException, JsonStringInvalidaException, ConnectionException{
        return sqLiteConnection.getPoliticasEmprestimo();
    }

    public List<BibliotecaDTO> getBibliotecas(boolean toSearch) {
        return sqLiteConnection.getBibliotecas(toSearch);
    }

    public List<TipoMaterialDTO> getTiposMaterial() {
        return sqLiteConnection.getTiposMaterial();
    }

    public List<BibliotecaDTO> getBibliotecasAcervo() {
        return sqLiteConnection.getBibliotecasAcervo();
    }

    public List<AcervoDTO> getAcervo(String orderBy) {
        return sqLiteConnection.getAcervo(orderBy);
    }

    public String[] getAutoresSec(int idAcervo) {
        return sqLiteConnection.getAutoresSec(idAcervo);
    }

    public String[] getAssuntos(int idAcervo) {
        return sqLiteConnection.getAssuntos(idAcervo);
    }

    public BibliotecaDTO getBiblioteca(int idBibl) {
        return sqLiteConnection.getBiblioteca(idBibl);
    }


        public void limparAcervoBD () {
        sqLiteConnection.limparAcervoBD();
    }

    public void insertEmprestimos(List<EmprestimoDTO> emprestimos, Boolean ativo){
        for (EmprestimoDTO emprestimo: emprestimos)
            sqLiteConnection.insertEmprestimo(emprestimo, ativo);
    }

    public void insertBibliotecas(List<BibliotecaDTO> bibliotecas){
        for (BibliotecaDTO biblioteca: bibliotecas)
            sqLiteConnection.insertBiblioteca(biblioteca);
    }

    public void insertSituacoesMaterial(List<SituacaoMaterialDTO> situacoesMaterial){
        for (SituacaoMaterialDTO situacaoMaterial: situacoesMaterial)
            sqLiteConnection.insertSituacaoMaterial(situacaoMaterial);
    }

    public void insertStatusMaterial(List<StatusMaterialDTO> statusMateriais){
        for (StatusMaterialDTO statusMaterial: statusMateriais)
            sqLiteConnection.insertStatusMaterial(statusMaterial);
    }

    public void insertTiposMaterial(List<TipoMaterialDTO> tiposMaterial){
        for (TipoMaterialDTO tipoMaterial: tiposMaterial)
            sqLiteConnection.insertTipoMaterial(tipoMaterial);
    }

    public void insertPoliticasEmprestimo(List<PoliticaEmprestimoDTO> politicasEmprestimo){
        for (PoliticaEmprestimoDTO politicaEmprestimo: politicasEmprestimo)
            sqLiteConnection.insertPoliticaEmprestimo(politicaEmprestimo);
    }
}

package br.ufrn.mala.auxiliar;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.dto.EmprestimoDTO;
import br.ufrn.mala.util.DataUtil;

/**
 * Created by Paulo Lopes on 30/10/2017
 */

public class ListHistoricoEmprestimosAdaptador extends BaseAdapter {

    private List<EmprestimoDTO> lstGrupos;

    private Context context;

    public ListHistoricoEmprestimosAdaptador(Context context, List<EmprestimoDTO> grupos) {
        // inicializa as variáveis da classe
        this.context = context;
        lstGrupos = grupos;
    }

    @Override
    public int getCount() {
        return lstGrupos.size();
    }

    @Override
    public Object getItem(int position) {
        return lstGrupos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lstGrupos.get(position).getIdEmprestimo();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.list_history_loan_item, parent, false);

        }
        EmprestimoDTO emprestimo = lstGrupos.get(position);
        TextView tvTitulo = (TextView) convertView.findViewById(R.id.lbllistTitulo_history_loan);
        TextView tvAutor = (TextView) convertView.findViewById(R.id.lbllistAutor_history_loan);
        TextView tvDevolucao = (TextView) convertView.findViewById(R.id.lbllistDtDevolucao_history_loan);
        TextView tvBiblioteca = (TextView) convertView.findViewById(R.id.lbllistBiblioteca_history_loan);

        // Populando os campos

        tvTitulo.setText(emprestimo.getTitulo());
        tvAutor.setText("Autor: " + emprestimo.getAutor());
        tvDevolucao.setText("Devolvido em: " + String.valueOf(DataUtil.formatLongToDate(emprestimo.getDataDevolucao())));
        tvBiblioteca.setText(emprestimo.getBiblioteca().getDescricao());
        return convertView;
    }


}

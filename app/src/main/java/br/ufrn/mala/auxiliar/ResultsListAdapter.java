package br.ufrn.mala.auxiliar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.dto.AcervoDTO;

/**
 * Created by Hugo Oliveira on 02/12/17.
 */

public class ResultsListAdapter extends BaseExpandableListAdapter {

    private List<String> lstGrupos;
    private HashMap<String, List<AcervoDTO>> lstItensGrupos;
    private Context context;

    public ResultsListAdapter(Context context, List<String> grupos, HashMap<String, List<AcervoDTO>> itensGrupos) {
        // inicializa as variáveis da classe
        this.context = context;
        lstGrupos = grupos;
        lstItensGrupos = itensGrupos;
    }

    @Override
    public int getGroupCount() {
        // retorna a quantidade de grupos
        return lstGrupos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // retorna a quantidade de itens de um grupo
        return lstItensGrupos.get(getGroup(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // retorna um grupo
        return lstGrupos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // retorna um item do grupo
        return lstItensGrupos.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // retorna o id do grupo, porém como nesse exemplo
        // o grupo não possui um id específico, o retorno
        // será o próprio groupPosition
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // retorna o id do item do grupo, porém como nesse exemplo
        // o item do grupo não possui um id específico, o retorno
        // será o próprio childPosition
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // retorna se os ids são específicos (únicos para cada
        // grupo ou item) ou relativos
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // cria os itens principais (grupos)
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_result_group, null);
        }

        TextView libGroup = (TextView) convertView.findViewById(R.id.lib_group);
        TextView titleQnt = (TextView) convertView.findViewById(R.id.title_qnt);

        libGroup.setText((String) getGroup(groupPosition));
        titleQnt.setText("(" + getChildrenCount(groupPosition) + ")");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // cria os subitens (itens dos grupos)
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_result_item, null);
        }

        TextView tvTitulo = (TextView) convertView.findViewById(R.id.lbl_result_title);
        TextView tvAutor = (TextView) convertView.findViewById(R.id.lbl_result_author);
        TextView tvLocalizacao = (TextView) convertView.findViewById(R.id.lbl_result_local);
        TextView tvEdicao = (TextView) convertView.findViewById(R.id.lbl_result_edition);
        TextView tvQnt = (TextView) convertView.findViewById(R.id.lbl_result_qnt);

        AcervoDTO acertoItem = (AcervoDTO) getChild(groupPosition, childPosition);
        tvTitulo.setText(acertoItem.getTitulo());
        tvAutor.setText(acertoItem.getAutor());
        tvLocalizacao.setText(context.getString(R.string.lbl_localization) + " " + (acertoItem.getNumeroChamada()));
        int idTipo = acertoItem.getIdTipoMaterial();
        if (acertoItem.getEdicao() == null || acertoItem.getEdicao().equalsIgnoreCase("null") ||
                acertoItem.getEdicao().equalsIgnoreCase("")) {
            tvEdicao.setPadding(0,0,0,0);
            tvEdicao.setVisibility(TextView.GONE);
        }
        else {
            tvEdicao.setText(acertoItem.getEdicao());
        }
        int qnt = acertoItem.getQuantidade();
        if(idTipo == 2 || idTipo == 3) {
            if (qnt > 1)
                tvQnt.setText(acertoItem.getQuantidade() + " " + context.getString(R.string.fascicles));
            else
                tvQnt.setText(acertoItem.getQuantidade() + " " + context.getString(R.string.single_fascicle));
        }
        else {
            if (qnt > 1)
                tvQnt.setText(acertoItem.getQuantidade() + " " + context.getString(R.string.exemplars));
            else
                tvQnt.setText(acertoItem.getQuantidade() + " " + context.getString(R.string.single_exemplar));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // retorna se o subitem (item do grupo) é selecionável
        return true;
    }

    public void setNewItems(List<String> listDataHeader,HashMap<String, List<AcervoDTO>> listChildData) {
        this.lstGrupos = listDataHeader;
        this.lstItensGrupos = listChildData;
        notifyDataSetChanged();
    }
}

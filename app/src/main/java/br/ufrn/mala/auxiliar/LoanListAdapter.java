package br.ufrn.mala.auxiliar;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import br.ufrn.mala.R;
import br.ufrn.mala.dto.EmprestimoDTO;

import static java.util.GregorianCalendar.*;

/**
 * Created by paulo on 20/10/17.
 */

public class LoanListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<EmprestimoDTO>> _listDataChild;

    public LoanListAdapter(Context context, List<String> listDataHeader,
                           HashMap<String, List<EmprestimoDTO>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final EmprestimoDTO childobj = (EmprestimoDTO) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_loan_item, null);
        }

        TextView autorListChild = (TextView) convertView
                .findViewById(R.id.lbllistAutor);
        TextView titleListChild = (TextView) convertView
                .findViewById(R.id.lbllistTitulo);
        TextView prazoListChild = (TextView) convertView
                .findViewById(R.id.lbllistPrazo);
        TextView placeListChild = (TextView) convertView
                .findViewById(R.id.lbllistBiblioteca);

        autorListChild.setText(childobj.getAutor());
        titleListChild.setText(childobj.getTitulo());
        placeListChild.setText(childobj.getBiblioteca());
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date dt = new Date(childobj.getDataDevolucao());

        prazoListChild.setText("Prazo: "+fmt.format(dt));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_loan_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

package br.ufrn.mala.connection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Joel Felipe on 04/11/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mala";
    private static final int DATABASE_VERSION = 1;
    private static final String BIBLIOTECA_CREATE =
            "CREATE TABLE biblioteca ( " +
                    "id_biblioteca INTEGER PRIMARY KEY, " +
                    "descricao TEXT, " +
                    "email TEXT, " +
                    "telefone TEXT, " +
                    "sigla TEXT, " +
                    "site TEXT " +
            ");";
    private static final String EMPRESTIMO_CREATE =
            "CREATE TABLE emprestimo ( " +
                    "id_emprestimo INTEGER PRIMARY KEY, " +
                    "autor TEXT, " +
                    "ativo TEXT, " +
                    "codigo_barras TEXT, " +
                    "cpf_cnpj_usuario INTEGER, " +
                    "data_devolucao INTEGER, " +
                    "data_emprestimo INTEGER, " +
                    "data_renovacao INTEGER, " +
                    "id_biblioteca INTEGER, " +
                    "id_material_informacional INTEGER, " +
                    "numero_chamada TEXT, " +
                    "prazo INTEGER, " +
                    "tipo_emprestimo TEXT, " +
                    "titulo TEXT, " +
                    "FOREIGN KEY(id_biblioteca) REFERENCES biblioteca(id_biblioteca) " +
            "); ";

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BIBLIOTECA_CREATE);
        db.execSQL(EMPRESTIMO_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS biblioteca");
        db.execSQL("DROP TABLE IF EXISTS emprestimo");
        onCreate(db);
    }
}

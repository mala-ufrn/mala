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

    private static final String SITUACAO_MATERIAL_CREATE =
            "CREATE TABLE situacao_material ( " +
                    "descricao TEXT, " +
                    "id_situacao_material INTEGER PRIMARY KEY" +
                    ");";

    private static final String STATUS_MATERIAL_CREATE =
            "CREATE TABLE status_material ( " +
                    "descricao TEXT, " +
                    "id_status_material INTEGER PRIMARY KEY" +
                    ");";

    private static final String TIPO_MATERIAL_CREATE =
            "CREATE TABLE tipo_material ( " +
                    "descricao TEXT, " +
                    "id_tipo_material INTEGER PRIMARY KEY" +
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

    private static final String ACERVO_CREATE =
            "CREATE TABLE acervo ( " +
                    "ano TEXT, " +
                    "autor TEXT, " +
                    "descricao_fisica TEXT, " +
                    "edicao TEXT, " +
                    "editora TEXT, " +
                    "endereco_eletronico TEXT, " +
                    "id_biblioteca INTEGER, " +
                    "id_tipo_material INTEGER, " +
                    "intervalo_paginas TEXT, " +
                    "isbn TEXT, " +
                    "issn TEXT, " +
                    "local_publicacao TEXT, " +
                    "nota_conteudo TEXT, " +
                    "notas_gerais TEXT, " +
                    "notas_locais TEXT, " +
                    "numero_chamada TEXT, " +
                    "quantidade INTEGER, " +
                    "registro_sistema INTEGER PRIMARY KEY, " +
                    "resumo TEXT, " +
                    "serie TEXT, " +
                    "sub_titulo TEXT, " +
                    "tipo_material TEXT" +
                    "titulo TEXT, " +
                    "FOREIGN KEY(id_tipo_material) REFERENCES tipo_material(id_tipo_material), " +
                    "FOREIGN KEY(id_biblioteca) REFERENCES biblioteca(id_biblioteca) " +
                    "); ";

    private static final String ACERVO_AUTOR_SECUNDARIO_CREATE =
            "CREATE TABLE acervo_autor_secundario ( " +
                    "autor TEXT, " +
                    "id_acervo INTEGER, " +
                    "FOREIGN KEY(id_acervo) REFERENCES acervo(registro_sistema) " +
                    ");";

    private static final String ACERVO_ASSUNTO_CREATE =
            "CREATE TABLE acervo_assunto ( " +
                    "assunto TEXT, " +
                    "id_acervo INTEGER, " +
                    "FOREIGN KEY(id_acervo) REFERENCES acervo(registro_sistema) " +
                    ");";

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BIBLIOTECA_CREATE);
        db.execSQL(SITUACAO_MATERIAL_CREATE);
        db.execSQL(STATUS_MATERIAL_CREATE);
        db.execSQL(TIPO_MATERIAL_CREATE);
        db.execSQL(EMPRESTIMO_CREATE);
        db.execSQL(ACERVO_CREATE);
        db.execSQL(ACERVO_AUTOR_SECUNDARIO_CREATE);
        db.execSQL(ACERVO_ASSUNTO_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS biblioteca");
        db.execSQL("DROP TABLE IF EXISTS emprestimo");
        db.execSQL("DROP TABLE IF EXISTS situacao_material");
        db.execSQL("DROP TABLE IF EXISTS status_material");
        db.execSQL("DROP TABLE IF EXISTS tipo_material");
        db.execSQL("DROP TABLE IF EXISTS acervo");
        db.execSQL("DROP TABLE IF EXISTS acervo_autor_secundario");
        db.execSQL("DROP TABLE IF EXISTS acervo_assunto");
        onCreate(db);
    }
}

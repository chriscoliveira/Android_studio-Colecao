package com.gmail.coliveira.christian.colecao;

/*
 * 12-11-15 adicionado ao sistemde exportacao a criacao de 2 arquivos csv Notas e Moedas. 
 * 090216 corrigido falha na troca de telas e organização por pais/ano
 * 15062018 -> acrescentado na tabela o campo datacadastro. e modificado as funçoes de importar e exportar e o dialog para trabalhar com este campo.
-> modificado a tela inicial com mais 1 tela de notas brasileiras.
-> colocado na tela inicial a data de ccompilação do apk
-> ao importar e exportar aparece um alerta mostrando as informações de notas e moedas no banco.
-> corrigido o erro ao cadastrar algo a partir da tela inicial
-> adicionado ao anexo do email o resumoColecao.txt, este arquivo precisa ser colocado na pasta Download do celular antes de importar os dados.
-> 02072018 alterado o conteudo do email quando enviado para mostrar no corpo do email as quantidades de cada item e adicionado por padrao meu email

 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.util.SortedList;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.coliveira.christian.colecao.services.Notificacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressLint("DefaultLocale")
public class ZInfoDB {

    public static String BANCO_MOD = "tbl_mod";
    public static String MODIFICA = "modifica";

    public static String NOME_BANCO = "Numismatica";
    public static String NOME_TABELA = "Colecao";
    public static String ID = "_id";
    public static String PAIS = "pais";
    public static String ANO = "ano";
    public static String KRAUSE = "krause";
    public static String VALOR = "valor";
    public static String MOEDA = "moeda";
    public static String ANVERSO = "anverso";
    public static String REVERSO = "reverso";
    public static String MATERIAL = "material";
    public static String DIAMETRO = "diametro";
    public static String DETALHE = "detalhe";
    public static String TIPO = "tipo";
    public static String QUALIDADE = "qualidade";
    public static String DATACADASTRO = "datacadastro";
    public ZUtilitarios zutilitarios = new ZUtilitarios();
    public Dialog dialog = null;
    public Button btAcaoDialog, btApagar;
    public ListView MostraDados;
    public SimpleCursorAdapter adapterLista;
    public Cursor cursor;
    public String ValoresMoedas = "", ValoresNotas = "", Valores = "", Resumo = "", CRITERIO = null;
    protected SQLiteDatabase bancoDados = null;

    Notificacao notificacao = new Notificacao();

    int contagem = 0;

    public void CriaBanco(Activity activity) {
        String criaTabela = "CREATE TABLE IF NOT EXISTS " + NOME_TABELA + "(" + ID + " INTEGER PRIMARY KEY, " + PAIS
                + " TEXT, " + ANO + " INTEGER, " + KRAUSE + " TEXT," + VALOR + " TEXT, " + MOEDA + " TEXT," + TIPO
                + " TEXT," + QUALIDADE + " TEXT," + MATERIAL + " TEXT," + DIAMETRO + " TEXT," + DETALHE + " TEXT,"
                + ANVERSO + " TEXT," + REVERSO + " TEXT," + DATACADASTRO + " TEXT" + ")";

        String criaTabelaModificao = "CREATE TABLE IF NOT EXISTS "+BANCO_MOD+"("+MODIFICA+" TXT)";


        bancoDados = activity.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
        bancoDados.execSQL(criaTabela);
        //bancoDados.execSQL(criaTabelaModificao);
        bancoDados.getPath();
        bancoDados.close();
    }

    public void AbreBanco(Activity activity) {
        bancoDados = activity.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
        //bancoDados.getPath();
        //bancoDados = activity.openOrCreateDatabase(BANCO_MOD,Context.MODE_PRIVATE,null);
        bancoDados.getPath();
    }

    public void FechaBanco() {
        bancoDados.close();
    }

    public void dialog(final Activity activity, final String acao, final String tipo, String texto) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.tela_cadastro); // xml com o conteudo do dialog
        dialog.setTitle(texto); // titulo do dialog Cadastrar Nova Moedas"
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setCanceledOnTouchOutside(false);

        String[] TIPOITEM = new String[]{"Moeda", "Nota"};
        String[] TODOSPAISES = new String[]{"Acores", "Afeganistao", "Africa Central Beac", "Africa Do Sul", "Africa Equatorial Francesa", "Africa Occidental", "Africa Ocidental Britanica", "Africa Ocidental Francesa", "Africa Oriental Alema", "Africa Oriental Britanica", "Albania", "Alderney", "Alemanha", "Alemanha Gdr", "Alemanha Terceiro Reich", "Andorra", "Angola", "Anguilla", "Antigua E Barbuda", "Antilhas Holandesas", "Arabia Do Sul", "Arabia Saudita", "Argelia", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijao", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgica", "Belize", "Benin", "Bermudas", "Biafra", "Birmania", "Boemia E Moravia", "Bolivia", "Borneu Do Norte", "Bosnia Herzegovina", "Botsuana", "Brasil", "Brunei", "Bulgaria", "Burquina Faso", "Burundi", "Butao", "Cabo Verde", "Camaroes", "Cambodja", "Canada", "Cazaquistao", "Ceilao", "Chade", "Chile", "China", "China Japones", "China Republica", "Chipre", "Cidade Do Vaticano", "Cidade Livre De Danzig", "Cochinchina Francesa", "Colombia", "Comores", "Congo", "Congo Belga", "Congo Rdc", "Coreia Do Norte", "Coreia Do Sul", "Costa Do Marfim", "Costa Rica", "Cracovia", "Creta", "Croacia", "Cuba", "Curacao", "Dinamarca", "Djibouti", "Dominica", "Dominio De Terra Nova", "Egito", "El Salvador", "Emirados Arabes Unidos", "Equador", "Eritreia", "Eslovaquia", "Eslovenia", "Espanha", "Espanha Guerra Civil", "Estabelecimentos Dos Estreitos", "Estado Livre Do Congo", "Estados Da Africa Equatorial", "Estados Do Caribe Oriental", "Estados Papais", "Estonia", "Etiopia", "Eua", "Fiji", "Filipinas", "Finlandia", "Franca", "Frances Dos Afars E Issas", "Gabao", "Gambia", "Gana", "Georgia", "Georgia Do Sul", "Gibraltar", "Granada", "Grecia", "Groenlandia", "Guatemala", "Guernsey", "Guiana", "Guiana Britanica", "Guine", "Guine-Bissau", "Guine Equatorial", "Haiti", "Hiderabade", "Holanda", "Honduras", "Honduras Britanicas", "Hong Kong", "Hungria", "Iemen", "Iemen Do Sul", "Ilha De Ascensao", "Ilha De Man", "Ilhas Cayman", "Ilhas Cook", "Ilhas Falklands", "Ilhas Faroe", "Ilhas Marshall", "Ilhas Pitcairn", "Ilhas Salomao", "Ilhas Turcas E Caicos", "Ilhas Virgens Britanicas", "Imperio Alemao", "Imperio Otomano", "India", "India Britanica", "India Portuguesa", "Indias Orientais Neerlandesas", "Indochina Francesa", "Indonesia", "Ira", "Iraque", "Irlanda", "Islandia", "Israel", "Italia", "Iugoslavia", "Jamaica", "Japao", "Jersey", "Jordania", "Katanga", "Kiribati", "Kuwait", "Laos", "Lesoto", "Letonia", "Libano", "Liberia", "Libia", "Liechtenstein", "Lituania", "Lombardo-Veneto", "Luxemburgo", "Macau", "Macedonia", "Madagascar", "Malasia", "Malasia Peninsular", "Malasia Peninsular E Borneu Britanico", "Malawi", "Maldivas", "Mali", "Malta", "Marrocos", "Mauricias", "Mauritania", "Mexico", "Mocambique", "Moldavia", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Nagorno-Karabakh", "Namibia", "Nauru", "Nepal", "Nicaragua", "Niger", "Nigeria", "Niue", "Noruega", "Nova Caledonia", "Nova Guine", "Nova Guine Alema", "Novas Hebridas", "Nova Zelandia", "Oceania Francesa", "Oman", "Ordem De Malta", "Palau", "Palestina", "Panama", "Papua Nova Guine", "Paquistao", "Paraguai", "Peru", "Polinesia Francesa", "Polonia", "Portugal", "Qatar", "Qatar E Dubai", "Quenia", "Quirguistao", "Reino Unido", "Republica Centro-Africana", "Republica Checa", "Republica Dominicana", "Republica Sul-Africana", "Reuniao", "Rodesia", "Rodesia Do Sul", "Rodesia E Niassalandia", "Romenia", "Ruanda", "Ruanda-Burundi", "Ruanda-Urundi", "Russia", "Saara Ocidental", "Samoa", "San Marino", "Santa Helena", "Santa Helena E Ascensao", "Santa Lucia", "Sao Cristovao E Nevis", "Sao Pedro E Miquelao", "Sao Tome E Principe", "Sao Vicente E Granadinas", "Sarawak", "Sarre", "Seicheles", "Senegal", "Serra Leoa", "Servia", "Singapura", "Siria", "Somalia", "Somalia Italiana", "Somalilandia", "Somalilandia Francesa", "Spitsbergen", "Sri Lanka", "Suazilandia", "Sudao", "Sudao Do Sul", "Suecia", "Suica", "Suriname", "Tailandia", "Taiwan", "Tajiquistao", "Tanzania", "Tchecoslovaquia", "Territorio Antartico Britanico", "Territorio Britanico Do Oceano Indico", "Timor Leste", "Timor Portugues", "Togo", "Tokelau", "Tonga", "Transnistria", "Trinidad E Tobago", "Tristao Da Cunha", "Tunisia", "Turquemenistao", "Turquia", "Tuva", "Tuvalu", "Ucrania", "Uganda", "Uniao Sovietica", "Uruguai", "Uzbequistao", "Vanuatu", "Venezuela", "Vietnam", "Vietnam Do Sul", "Zaire", "Zambia", "Zanzibar", "Zimbabue"};
        String[] TODOSMATERIAIS = new String[]{"ACO", "ACO BRONZE", "ACO COBRE", "ACO COBRE NIQUEL", "ACO INOX", "ACO LATAO", "ACO NIQUEL", "ALUMINIO", "ALUMINIO BRONZE", "ALUMINIO COBRE NIQUEL", "ALUMINIO NIQUEL BRONZE", "BI METALICA", "BRONZE", "BRONZE ACO", "BRONZE NIQUEL", "COBRE", "COBRE ACO", "COBRE CUPRO NIQUEL", "COBRE FERRO", "COBRE LATAO", "COBRE NIQUEL", "COBRE NIQUEL ZINCO", "COBRE ZINCO", "COBRE ZINCO MAGNESIO NIQUEL", "COBRE ZINCO NIQUEL", "FERRO NIQUEL", "INOX", "LATAO", "LATAO ACO", "LATAO CUPRO NIQUEL", "LATAO DE ACO REVESTIDO", "LATAO REVESTIDO DE ACO", "MANGANES LATAO", "NIQUEL", "NIQUEL ACO", "NIQUEL BRONZE", "NIQUEL LATAO", "PAPEL", "PAPEL BAMBU", "POLIMERO", "PRATA", "OURO"
        };
        String[] CLASSIFICACAO = new String[]{"BNC", "FC", "FE", "SOB", "BELA", "MBC", "BC", "REG", "UTG", "REPOR"};
        String[] TIPOMOEDA = new String[]{"AFGHANIS", "ARIARY", "AUSTRALES", "BAHT", "BAISA", "BILETOV", "BOLIVAR", "BOLIVIANO", "CORDOBA", "COROA", "COUPON", "CROWN", "CRUZADO", "CRUZADO NOVO", "CRUZEIRO", "CRUZEIRO NOVO", "CRUZEIRO REAL", "DENAR", "DINAR", "DINARA", "DIRHAM", "DIRHAN", "DOLAR", "DOLAR AUSTRALIANO", "DOLAR BAAMIANO", "DOLAR CANADENSE", "DONG", "DRACHMA", "DRAM", "ESCUDO", "EURO", "FEN", "FLORIN", "FORINT", "FRANCO", "FRANCO SUICO", "GOURDE", "GROSCHEN", "GROSZ", "GROSZE", "GROSZY", "GUARANI", "GULDEN", "HALALAS", "HRYVNIA", "INTIS", "JIAO", "KARBOVANTE", "KINA", "KIP", "KOPECK", "KRONA", "KRONE", "KRONEN", "KROON", "KROONI", "KUNA", "KURUS", "KWACHA", "KWANCHA", "KWANZA", "KYAT", "LARI", "LEI", "LEU", "LEV", "LEVA", "LIBRA", "LIRA", "LIRE", "LITA", "LIVRE", "MARK", "MARKKA", "MARKKAA", "METICAIS", "MONGO", "NAIRA", "NEW PENCE", "NEW PENNY", "NEW SHEQEL", "NGULTRUM", "NOUVEAUX MAKUTA", "NUEVO PESO", "NUEVO SOL", "ORE", "ORE DANMARK", "ORE NORGE", "ORE SVERIGE", "PAISA", "PAISE", "PARA", "PATACA", "PENCE", "PENNIA", "PENNY", "PESETA", "PESO", "PESO BOLIVIANO", "PESO URUGUAIO", "PFENNING", "PIASTRE", "PISO", "POISHA", "POUND", "POUND SUDANESE", "QUETZAL", "RAND", "RAPPEN", "REAL", "REICH", "REICHMARK", "REICHSPFENNING", "REIS", "RIAL", "RIEL", "RINGGIT", "RUBLE", "RUBLEI", "RUBLO", "RUPEE", "RUPIAH", "RYAL", "RYEL", "SATANG", "SCHILLING", "SEN", "SHEQEL", "SLOTY", "SOL DE ORO", "SOM", "SOMONI", "STOTINOV", "SUCRE", "TAKA", "TENGE", "TOGROG", "TOLAR", "TUGRIK", "TYIYN", "WHAN", "WON", "WU JIAO", "YEN", "YUAN", "ZLOTY", "ZLOTYCH"};

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(activity,
                android.R.layout.simple_dropdown_item_1line, TIPOITEM);

        ArrayAdapter<String> adapterPais = new ArrayAdapter<>(activity,
                android.R.layout.simple_dropdown_item_1line, TODOSPAISES);

        ArrayAdapter<String> adapterMaterial = new ArrayAdapter<>(activity,
                android.R.layout.simple_dropdown_item_1line, TODOSMATERIAIS);

        ArrayAdapter<String> adapterQualidade = new ArrayAdapter<>(activity,
                android.R.layout.simple_dropdown_item_1line, CLASSIFICACAO);

        ArrayAdapter<String> adapterMoeda = new ArrayAdapter<>(activity,
                android.R.layout.simple_dropdown_item_1line, TIPOMOEDA);

        final TextView tvi = dialog.findViewById(R.id.tvi);
        final EditText etAno = dialog.findViewById(R.id.etAno);
        final AutoCompleteTextView etPais = dialog.findViewById(R.id.etPais);
        final EditText etKrause = dialog.findViewById(R.id.etKrause);
        final EditText etValor = dialog.findViewById(R.id.etValor);
        final AutoCompleteTextView etMoeda = dialog.findViewById(R.id.etMoeda);
        final AutoCompleteTextView etTipo = dialog.findViewById(R.id.etTipo);
        final AutoCompleteTextView etQualidade = dialog.findViewById(R.id.etQualidade);
        final AutoCompleteTextView etMaterial = dialog.findViewById(R.id.etMaterial);
        final EditText etDiametro = dialog.findViewById(R.id.etDiametro);
        final EditText etDetalhe = dialog.findViewById(R.id.etDetalhe);
        final EditText etAnverso = dialog.findViewById(R.id.etAnverso);
        final EditText etReverso = dialog.findViewById(R.id.etReverso);

        etPais.setAdapter(adapterPais);
        etTipo.setAdapter(adapterTipo);
        etMoeda.setAdapter(adapterMoeda);
        etQualidade.setAdapter(adapterQualidade);
        etMaterial.setAdapter(adapterMaterial);

        btAcaoDialog = dialog.findViewById(R.id.btGravarMoeda);

        View btn = dialog.findViewById(R.id.btApagar);
        btn.setVisibility(View.GONE);

        if (texto.equals("Cadastrar Moedas Nacional") || texto.equals("Cadastrar Moeda Estrangeira")) {
            etTipo.setText("Moeda");
        }
        if (texto.equals("Cadastrar Nota")) {
            etTipo.setText("Nota");
        }

        if (acao.equals("add")) {
            btAcaoDialog.setText("Cadastrar");

        }
        if (acao.equals("del")) {
            btAcaoDialog.setText("Apagar");
        }
        if (acao.equals("atz")) {
            btAcaoDialog.setText("Atualizar");
            btn.setVisibility(View.VISIBLE);
        }


        btAcaoDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (acao.equals("add")) {
                    insert(activity, NOME_TABELA, etPais.getText().toString(), etAno.getText().toString().toUpperCase(),
                            etKrause.getText().toString().toUpperCase(), etValor.getText().toString().toUpperCase(), etMoeda.getText().toString().toUpperCase(),
                            etTipo.getText().toString(), etQualidade.getText().toString().toUpperCase(),
                            etMaterial.getText().toString().toUpperCase(), etDiametro.getText().toString().toUpperCase(),
                            etDetalhe.getText().toString().toUpperCase(), etAnverso.getText().toString().toUpperCase(),
                            etReverso.getText().toString().toUpperCase());

                }
                if (acao.equals("del"))
                    delete(activity, NOME_TABELA,etPais.getText().toString(), tvi.getText().toString(), tipo);
                if (acao.equals("atz")) {
                    atualiza(activity, NOME_TABELA, etPais.getText().toString(), etAno.getText().toString().toUpperCase(),
                            etKrause.getText().toString().toUpperCase(), etValor.getText().toString().toUpperCase(), etMoeda.getText().toString().toUpperCase(),
                            etTipo.getText().toString(), etQualidade.getText().toString().toUpperCase(),
                            etMaterial.getText().toString().toUpperCase(), etDiametro.getText().toString().toUpperCase(),
                            etDetalhe.getText().toString().toUpperCase(), etAnverso.getText().toString().toUpperCase(),
                            etReverso.getText().toString().toUpperCase(), tvi.getText().toString().toUpperCase());
                }
                dialog.cancel();
                activity.recreate();
            }

        });

        btApagar = dialog.findViewById(R.id.btApagar);
        btApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(activity, NOME_TABELA,etPais.getText().toString(), tvi.getText().toString(), tipo);
            }
        });

        Button btFechar = dialog.findViewById(R.id.btFechar);
        btFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        dialog.show();
    }

    public void enviaDadosDialog(Activity activity, String tabela, int Posicao) {
        try {
            AbreBanco(activity);
            cursor = bancoDados.query(tabela, null, ID + "=" + Posicao, null, null, null, null);
            final TextView tvi = dialog.findViewById(R.id.tvi);
            final EditText etAno = dialog.findViewById(R.id.etAno);
            final EditText etPais = dialog.findViewById(R.id.etPais);
            final EditText etKrause = dialog.findViewById(R.id.etKrause);
            final EditText etValor = dialog.findViewById(R.id.etValor);
            final EditText etMoeda = dialog.findViewById(R.id.etMoeda);
            final EditText etTipo = dialog.findViewById(R.id.etTipo);
            final EditText etQualidade = dialog.findViewById(R.id.etQualidade);
            final EditText etMaterial = dialog.findViewById(R.id.etMaterial);
            final EditText etDiametro = dialog.findViewById(R.id.etDiametro);
            final EditText etDetalhe = dialog.findViewById(R.id.etDetalhe);
            final EditText etAnverso = dialog.findViewById(R.id.etAnverso);
            final EditText etReverso = dialog.findViewById(R.id.etReverso);
            final TextView tvdatacadastro = dialog.findViewById(R.id.tv_datacadastro);

            while (cursor.moveToNext()) {
                etPais.setText(cursor.getString(cursor.getColumnIndex(PAIS)));
                etAno.setText(cursor.getString(cursor.getColumnIndex(ANO)));
                etKrause.setText(cursor.getString(cursor.getColumnIndex(KRAUSE)));
                etValor.setText(cursor.getString(cursor.getColumnIndex(VALOR)));
                etMoeda.setText(cursor.getString(cursor.getColumnIndex(MOEDA)));
                etTipo.setText(cursor.getString(cursor.getColumnIndex(TIPO)));
                etQualidade.setText(cursor.getString(cursor.getColumnIndex(QUALIDADE)));
                etMaterial.setText(cursor.getString(cursor.getColumnIndex(MATERIAL)));
                etDiametro.setText(cursor.getString(cursor.getColumnIndex(DIAMETRO)));
                etDetalhe.setText(cursor.getString(cursor.getColumnIndex(DETALHE)));
                etAnverso.setText(cursor.getString(cursor.getColumnIndex(ANVERSO)));
                etReverso.setText(cursor.getString(cursor.getColumnIndex(REVERSO)));
                tvi.setText(cursor.getString(cursor.getColumnIndex(ID)));
                tvdatacadastro.setText(cursor.getString(cursor.getColumnIndex(DATACADASTRO)));
            }

            FechaBanco();

        } catch (Exception er) {
            zutilitarios.toast(activity, "Erro ao enviar os dados para o Dialog: " + er);
        }
    }


    // -----------------------------------------------------------------------------------

    // TODO

    @SuppressWarnings("deprecation")
    public void FiltrarRelatorios(Activity activity, String acao) {

        MostraDados = activity.findViewById(R.id.lvListagem);

        if (acao == "ultimos") {
            if (VerificaFiltrarRelatorios(activity, acao)) {
                adapterLista = new SimpleCursorAdapter(activity, R.layout.tela_listagem_itens, cursor,
                        new String[]{TIPO, PAIS, ANO, KRAUSE, VALOR, MOEDA, ID, DIAMETRO, MATERIAL, ANVERSO, REVERSO},
                        new int[]{R.id.tvTipo, R.id.tvPais, R.id.tvAno, R.id.tvKrause, R.id.tvValor, R.id.tvMoeda,
                                R.id.tvId, R.id.tvDiametro, R.id.tvMaterial, R.id.tvAnverso, R.id.tvReverso});
                MostraDados.setAdapter(adapterLista);
            } else {
                zutilitarios.toast(activity, "Não existem dados a exibir: ");
                MostraDados.setAdapter(null);
            }
        }
    }
    private boolean VerificaFiltrarRelatorios(Activity activity, String acao) {
        try {
            AbreBanco(activity);
            String ordem = "_id desc";

            cursor = bancoDados.query(NOME_TABELA, null, null, null, null, null, ordem);
            if (cursor.getCount() != 0) // se existir registro
            {
                cursor.moveToFirst(); // movimenta para o 1º registro
                return true;
            } else
                return false;
        } catch (Exception er) {
            zutilitarios.toast(activity, "Nao existe registros a exibir");
            return false;
        } finally {
            FechaBanco();
        }

    }



    // -----------------------------------------------------------------------------------

    // TODO

    @SuppressWarnings("deprecation")
    public void FiltrarRegistros(Activity activity, String tipo, String pais, String ordem) {
        soma(activity, tipo, pais, "");
        MostraDados = activity.findViewById(R.id.lvListagem);

        if (VerificaFiltrarRegistros(activity, tipo, pais, ordem)) {
            adapterLista = new SimpleCursorAdapter(activity, R.layout.tela_listagem_itens, cursor,
                    new String[]{TIPO, PAIS, ANO, KRAUSE, VALOR, MOEDA, ID, DIAMETRO, MATERIAL, ANVERSO, REVERSO},
                    new int[]{R.id.tvTipo, R.id.tvPais, R.id.tvAno, R.id.tvKrause, R.id.tvValor, R.id.tvMoeda,
                            R.id.tvId, R.id.tvDiametro, R.id.tvMaterial, R.id.tvAnverso, R.id.tvReverso});
            MostraDados.setAdapter(adapterLista);
        } else {
            zutilitarios.toast(activity, "Não existem dados a exibir: pais="+pais);
            MostraDados.setAdapter(null);
        }
    }

    private boolean VerificaFiltrarRegistros(Activity activity, String tipo, String pais, String ordem) {
        try {
            AbreBanco(activity);
            if (tipo.equals("Nota") & pais.equals(""))
                CRITERIO = "TIPO = 'Nota'";
            if (tipo.equals("Nota") & pais.equals("Brasil"))
                CRITERIO = "TIPO = 'Nota' and PAIS = 'Brasil'";
            if (tipo.equals("Nota") & pais.equals("noBrasil"))
                CRITERIO = "TIPO = 'Nota' and PAIS != 'Brasil'";
            if (tipo.equals("Moeda") & pais.equals("Brasil"))
                CRITERIO = "TIPO = 'Moeda' and PAIS = 'Brasil'";
            if (tipo.equals("Moeda") & pais.equals(""))
                CRITERIO = "TIPO = 'Moeda' and PAIS != 'Brasil'";
            if (tipo.equals("") & !pais.equals(""))
                CRITERIO = "PAIS = '" + pais + "'";
            if (ordem.equals(""))
                ordem = "_id desc";

            cursor = bancoDados.query(NOME_TABELA, null, CRITERIO, null, null, null, ordem);
            if (cursor.getCount() != 0) // se existir registro
            {
                cursor.moveToFirst(); // movimenta para o 1º registro
                return true;
            } else
                return false;
        } catch (Exception er) {
            zutilitarios.toast(activity, "Nao existe registros a exibir");
            return false;
        } finally {
            FechaBanco();
        }

    }


    public void FiltroPesquisa(Activity activity, String texto) {

        MostraDados = activity.findViewById(R.id.lvListagem);

        if (VerificaFiltrarPesquisa(activity, texto)) // verifica se a função é true
        {
            adapterLista = new SimpleCursorAdapter(activity, R.layout.tela_listagem_pesquisa, cursor,
                    new String[]{TIPO, PAIS, ANO, KRAUSE, VALOR, MOEDA, ID, MATERIAL},
                    new int[]{R.id.tvTipo, R.id.tvPais, R.id.tvAno, R.id.tvKrause, R.id.tvValor, R.id.tvMoeda,
                            R.id.tvId, R.id.tvMaterial});
            MostraDados.setAdapter(adapterLista); // executa a ação
        } else {
            zutilitarios.toast(activity, "Não existem dados a exibir: ");
            MostraDados.setAdapter(null);
        }
    }

    private boolean VerificaFiltrarPesquisa(Activity activity, String texto) {
        try {
            AbreBanco(activity);

            CRITERIO = "DETALHE like '%" + texto + "%' OR ANVERSO  like '%" + texto + "%' OR REVERSO like '%" + texto +
                    "%' OR KRAUSE like '%" + texto + "%' OR MATERIAL like '%" + texto + "%' OR MOEDA like '%" + texto +
                    "%' OR ANO like '%" + texto + "%' OR PAIS like '%" + texto + "%' OR QUALIDADE like '%" + texto +
                    "%' OR DIAMETRO like '%" + texto + "%' OR VALOR like '%" + texto + "%'";


            cursor = bancoDados.query(NOME_TABELA, null, CRITERIO, null,
                    null, null, null);

            if (cursor.getCount() != 0) // se existir registro
            {
                cursor.moveToFirst(); // movimenta para o 1º registro
                return true;
            } else
                return false;
        } catch (Exception er) {
            zutilitarios.toast(activity, "Nao existe registros a exibir");
            return false;
        } finally {
            FechaBanco();
        }

    }


    public void CarregaOrdenado(Activity activity, String tipo, String order, String pais) {

        soma(activity, tipo, pais, "");
        MostraDados = activity.findViewById(R.id.lvListagem);
        if (VerificaOrdenado(activity, tipo, order, pais)) // verifica se a função é true
        {
            adapterLista = new SimpleCursorAdapter(activity, R.layout.tela_listagem_itens, cursor,
                    new String[]{TIPO, PAIS, ANO, KRAUSE, VALOR, MOEDA, ID, DIAMETRO, MATERIAL, ANVERSO, REVERSO},
                    new int[]{R.id.tvTipo, R.id.tvPais, R.id.tvAno, R.id.tvKrause, R.id.tvValor, R.id.tvMoeda,
                            R.id.tvId, R.id.tvDiametro, R.id.tvMaterial, R.id.tvAnverso, R.id.tvReverso});
            MostraDados.setAdapter(adapterLista); // executa a ação
        } else {
            zutilitarios.toast(activity, "Não existem dados a exibir: ");
            MostraDados.setAdapter(null);
        }
    }

    private boolean VerificaOrdenado(Activity activity, String tipo, String order, String pais) {
        try {
            AbreBanco(activity);
            if (tipo.equals("Nota") & pais.equals(""))
                CRITERIO = "TIPO = 'Nota'";
            if (tipo.equals("Moeda") & pais.equals("Brasil"))
                CRITERIO = "TIPO = 'Moeda' and PAIS = 'Brasil'";
            if (tipo.equals("Moeda") & pais.equals(""))
                CRITERIO = "TIPO = 'Moeda' and PAIS != 'Brasil'";
            if (tipo.equals("") & !pais.equals(""))
                CRITERIO = "PAIS = '" + pais + "'";
            if (order.equals(""))
                order = "_id desc";

            cursor = bancoDados.query(NOME_TABELA, null, CRITERIO, null, null, null, order);
            if (cursor.getCount() != 0) // se existir registro
            {
                cursor.moveToFirst(); // movimenta para o 1º registro
                return true;
            } else
                return false;
        } catch (Exception er) {
            zutilitarios.toast(activity, "Nao existe registros a exibir");
            return false;
        } finally {
            FechaBanco();
        }

    }

    public void insert(Activity activity, String tabela, String pais, String ano, String krause, String valor,
                       String moeda, String tipo, String qualidade, String material, String diametro, String detalhe,
                       String anverso, String reverso) {
        Date date = new Date();
        String data = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
        String Aano = "0", Ttipo = "Moeda";
        if (pais.equals("")) {
            Toast.makeText(activity, "Campo PAÍS em branco, cadastro cancelado...", Toast.LENGTH_SHORT).show();
        }
        if (!pais.equals("")) {
            if (!ano.equals("")) Aano = ano;
            if (!tipo.equals("")) Ttipo = tipo;
            try {
                AbreBanco(activity);
                ContentValues contentValuesCampos = new ContentValues();
                contentValuesCampos.put(PAIS, pais);
                contentValuesCampos.put(ANO, Aano);
                contentValuesCampos.put(VALOR, valor);
                contentValuesCampos.put(KRAUSE, krause);
                contentValuesCampos.put(VALOR, valor);
                contentValuesCampos.put(MOEDA, moeda);
                contentValuesCampos.put(TIPO, Ttipo);
                contentValuesCampos.put(QUALIDADE, qualidade);
                contentValuesCampos.put(MATERIAL, material);
                contentValuesCampos.put(DIAMETRO, diametro);
                contentValuesCampos.put(DETALHE, detalhe);
                contentValuesCampos.put(ANVERSO, anverso);
                contentValuesCampos.put(REVERSO, reverso);
                contentValuesCampos.put(DATACADASTRO, data);
                bancoDados.insert(tabela, null, contentValuesCampos);
                zutilitarios.toast(activity, "Cadastro ok!");

                notificacao.createNotification(activity,"Parabens!!!","Item cadastrado: "+ano+" "+pais+"-"+valor+" "+moeda+"\n Faça o backup da coleção.");

            } catch (Exception e) {

            }
            try {
                FiltrarRegistros(activity, tipo, "", "pais asc, ano asc");
            } catch (Exception e) {
                return;
            }
        }

        dialog.cancel();


    }

    public void delete(final Activity activity, final String tabela,final String pais, String id, final String tipo) {
        try {
            final String texto = ID + " = " + id;
            AbreBanco(activity);

            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setTitle("APAGAR REGISTRO???");
            alert.setMessage("Você tem certeza que deseja APAGAR O REGISTRO?");
            alert.setPositiveButton("APAGAR", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    bancoDados.delete(tabela, texto, null);
                    notificacao.createNotification(activity,"Aviso!!!","Item removido! \n Faça o backup da coleção.");
                    FechaBanco();
                    FiltrarRegistros(activity, tipo, pais, "pais asc, ano asc");
                    dialog.cancel();
                }
            });
            alert.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.show();
            dialog.cancel();

        } catch (Exception e) {
        }

    }

    public void atualiza(Activity activity, String tabela, String pais, String ano, String krause, String valor,
                         String moeda, String tipo, String qualidade, String material, String diametro, String detalhe,
                         String anverso, String reverso, String id) {
        String Aano = "0", Ttipo = "Moeda";
        if (!ano.equals("")) Aano = ano;
        if (!tipo.equals("")) Ttipo = tipo;
        if (pais.equals("")) {
            Toast.makeText(activity, "Campo PAÍS em branco, cadastro cancelado...", Toast.LENGTH_SHORT).show();
        }
        if (!pais.equals("")) {

            try {
                String texto = ID + " = " + id;
                AbreBanco(activity);
                ContentValues contentValuesCampos = new ContentValues();
                contentValuesCampos.put(PAIS, pais);
                contentValuesCampos.put(ANO, Aano);
                contentValuesCampos.put(VALOR, valor);
                contentValuesCampos.put(KRAUSE, krause);
                contentValuesCampos.put(VALOR, valor);
                contentValuesCampos.put(MOEDA, moeda);
                contentValuesCampos.put(TIPO, Ttipo);
                contentValuesCampos.put(QUALIDADE, qualidade);
                contentValuesCampos.put(MATERIAL, material);
                contentValuesCampos.put(DIAMETRO, diametro);
                contentValuesCampos.put(DETALHE, detalhe);
                contentValuesCampos.put(ANVERSO, anverso);
                contentValuesCampos.put(REVERSO, reverso);
                bancoDados.update(tabela, contentValuesCampos, texto, null);

                notificacao.createNotification(activity,"Parabens!!!","Item atualizado: "+ano+" "+pais+"-"+valor+" "+moeda+"\n Faça o backup da coleção.");
                FechaBanco();
                FiltrarRegistros(activity, tipo, pais, "pais asc, ano asc");

            } catch (Exception e) {
            }
        }
    }

    public void soma(Activity activity, String tipo, String pais, String texto) {
        Integer cont = 0;
        AbreBanco(activity);
        if (tipo.equals("Nota") & pais.equals(""))
            CRITERIO = "TIPO = 'Nota'";
        if (tipo.equals("Moeda") & pais.equals("Brasil"))
            CRITERIO = "TIPO = 'Moeda' and PAIS = 'Brasil'";
        if (tipo.equals("Moeda") & pais.equals(""))
            CRITERIO = "TIPO = 'Moeda' and PAIS != 'Brasil'";
        if (tipo.equals("") & !pais.equals(""))
            CRITERIO = "PAIS = '" + pais + "'";
        if (tipo.equals("Nota") & pais.equals("noBrasil"))
            CRITERIO = "TIPO = 'Nota' and PAIS != 'Brasil'";
        if (tipo.equals("Nota") & pais.equals("Brasil"))
            CRITERIO = "TIPO = 'Nota' and PAIS = 'Brasil'";
        if (tipo.equals("pesquisa"))
            CRITERIO = "DETALHE like '%" + texto + "%' OR ANVERSO  like '%" + texto + "%' OR REVERSO like '%" + texto +
                    "%' OR KRAUSE like '%" + texto + "%' OR MATERIAL like '%" + texto + "%' OR MOEDA like '%" + texto +
                    "%' OR ANO like '%" + texto + "%' OR PAIS like '%" + texto + "%' OR QUALIDADE like '%" + texto +
                    "%' OR DETALHE like '%" + texto + "%'";
        cursor = bancoDados.query(NOME_TABELA, null, CRITERIO, null, null, null, null);

        while (cursor.moveToNext()) {
            cont++;
        }

        TextView tvresumo = activity.findViewById(R.id.tvResumao);
        tvresumo.setText("Total de " + tipo + " = " + cont);
        FechaBanco();

    }


    public void ExportaTXT(Activity activity) {

        contagem = 0;
        AbreBanco(activity);
        Cursor cc = bancoDados.query(NOME_TABELA, null, null, null, null, null, null);

        while (cc.moveToNext()) {
            Valores += "INSERT INTO " + NOME_TABELA + " (" + PAIS + "," + ANO + "," + KRAUSE + "," + VALOR + "," + MOEDA
                    + "," + TIPO + "," + QUALIDADE + "," + MATERIAL + "," + DIAMETRO + "," + DETALHE + "," + ANVERSO
                    + "," + REVERSO + "," + DATACADASTRO + ") VALUES ('";

            Valores += cc.getString(cc.getColumnIndex(PAIS)) + "',"
                    + cc.getString(cc.getColumnIndex(ANO)) + ",'"
                    + cc.getString(cc.getColumnIndex(KRAUSE)) + "','"
                    + cc.getString(cc.getColumnIndex(VALOR)) + "','"
                    + cc.getString(cc.getColumnIndex(MOEDA)) + "','"
                    + cc.getString(cc.getColumnIndex(TIPO)) + "','"
                    + cc.getString(cc.getColumnIndex(QUALIDADE)) + "','"
                    + cc.getString(cc.getColumnIndex(MATERIAL)) + "','"
                    + cc.getString(cc.getColumnIndex(DIAMETRO)) + "','"
                    + cc.getString(cc.getColumnIndex(DETALHE)) + "','"
                    + cc.getString(cc.getColumnIndex(ANVERSO)) + "','"
                    + cc.getString(cc.getColumnIndex(REVERSO)) + "','"
                    + cc.getString(cc.getColumnIndex(DATACADASTRO)) + "'),; \n";
            contagem++;
        }

        Valores = Valores.replaceAll(",;", ";");

        SalvarArquivoTXT(Valores, activity);
        cc.close();
        zutilitarios.toast(activity, "Exportacao dos dados realizada com sucesso!");
        FechaBanco();
    }

    private void SalvarArquivoTXT(String valor, Activity activity) {
        File arq;
        FileOutputStream fos;
        byte[] dados;
        try {
            arq = new File(Environment.getExternalStorageDirectory(), "/Download/bancoMoedas.txt");
            fos = new FileOutputStream(arq.toString()); // transforma o texto digitado em array de bytes
            dados = valor.getBytes();  // escreve os dados e fecha o arquivo
            fos.write(dados);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            zutilitarios.toast(activity, "Exportação com erro! " + e);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void ExportaCSV(Activity activity) {
        contagem=0;
        AbreBanco(activity);
        Date date = new Date();
        String data = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
        Cursor cc = bancoDados.query(NOME_TABELA, null, "tipo = 'Moeda'", null, null, null, "pais asc");
        zutilitarios.toast(activity, "Exportando..");
        ValoresMoedas = "";
        ValoresMoedas += PAIS + "," + ANO + "," + KRAUSE + "," + VALOR + "," + MOEDA + "," + TIPO + "," + QUALIDADE
                + "," + MATERIAL + "," + DIAMETRO + "," + DETALHE + "," + ANVERSO + "," + REVERSO + "," + DATACADASTRO + "; \n";
        while (cc.moveToNext()) {
            ValoresMoedas += cc.getString(cc.getColumnIndex(PAIS)) + ","
                    + cc.getString(cc.getColumnIndex(ANO)) + ","
                    + cc.getString(cc.getColumnIndex(KRAUSE)) + ","
                    + cc.getString(cc.getColumnIndex(VALOR)) + ","
                    + cc.getString(cc.getColumnIndex(MOEDA)) + ","
                    + cc.getString(cc.getColumnIndex(TIPO)) + ","
                    + cc.getString(cc.getColumnIndex(QUALIDADE)) + ","
                    + cc.getString(cc.getColumnIndex(MATERIAL)) + ","
                    + cc.getString(cc.getColumnIndex(DIAMETRO)) + ","
                    + cc.getString(cc.getColumnIndex(DETALHE)) + ","
                    + cc.getString(cc.getColumnIndex(ANVERSO)) + ","
                    + cc.getString(cc.getColumnIndex(REVERSO)) + ","
                    + cc.getString(cc.getColumnIndex(DATACADASTRO)) + "; \n";
            contagem++;
        }
        Cursor cc1 = bancoDados.query(NOME_TABELA, null, "tipo = 'Nota'", null, null, null, "pais asc");

        ValoresNotas = "";
        ValoresNotas += PAIS + "," + ANO + "," + KRAUSE + "," + VALOR + "," + MOEDA + "," + TIPO + "," + QUALIDADE + ","
                + MATERIAL + "," + DIAMETRO + "," + DETALHE + "," + ANVERSO + "," + REVERSO + "," + DATACADASTRO + "; \n";
        while (cc1.moveToNext()) {
            ValoresNotas += cc1.getString(cc1.getColumnIndex(PAIS)) + ","
                    + cc1.getString(cc1.getColumnIndex(ANO)) + ","
                    + cc1.getString(cc1.getColumnIndex(KRAUSE)) + ","
                    + cc1.getString(cc1.getColumnIndex(VALOR)) + ","
                    + cc1.getString(cc1.getColumnIndex(MOEDA)) + ","
                    + cc1.getString(cc1.getColumnIndex(TIPO)) + ","
                    + cc1.getString(cc1.getColumnIndex(QUALIDADE)) + ","
                    + cc1.getString(cc1.getColumnIndex(MATERIAL)) + ","
                    + cc1.getString(cc1.getColumnIndex(DIAMETRO)) + ","
                    + cc1.getString(cc1.getColumnIndex(DETALHE)) + ","
                    + cc1.getString(cc1.getColumnIndex(ANVERSO)) + ","
                    + cc1.getString(cc1.getColumnIndex(REVERSO)) + ","
                    + cc1.getString(cc1.getColumnIndex(DATACADASTRO)) + "; \n";
            contagem++;
        }

        ValoresNotas = ValoresNotas.replaceAll(",;", ";");
        ValoresMoedas = ValoresMoedas.replaceAll(",;", ";");
        SalvarArquivoCSV(ValoresNotas, ValoresMoedas, activity);
        cc.close();
        FechaBanco();

    }

    private void SalvarArquivoCSV(String valorNotas, String valorMoedas, Activity activity) {
        File arq;
        FileOutputStream fos;
        byte[] dados;
        try {
            arq = new File(Environment.getExternalStorageDirectory(), "/Download/bancoNotas.csv");
            fos = new FileOutputStream(arq.toString());// transforma o texto digitado em array de bytes
            dados = valorNotas.getBytes();// escreve os dados e fecha o arquivo
            fos.write(dados);
            fos.flush();
            fos.close();

            arq = new File(Environment.getExternalStorageDirectory(), "/Download/bancoMoedas.csv");
            fos = new FileOutputStream(arq.toString());// transforma o texto digitado em array de bytes
            dados = valorMoedas.getBytes();// escreve os dados e fecha o arquivo
            fos.write(dados);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            zutilitarios.toast(activity, "Exportação com erro! " + e);// trace("Erro : " + e.getMessage());
        }
    }


    public void importarLista(Activity activity) {
        try {
            zutilitarios.toast(activity, "Importando registros...");
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "/Download/bancoMoedas.txt");
            AbreBanco(activity);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                bancoDados.execSQL(line);
            }
            zutilitarios.toast(activity, "Registros Importados com Sucesso!");
        } catch (IOException e) {
            zutilitarios.toast(activity, "Ocorreu um erro ao importar os dados: " + e);
        }
        FechaBanco();
    }

    public void contaPais(Activity activity) {
        AbreBanco(activity);
        Integer contagem = 0, pais = 0, count = 0, countMoeda = 0;


        cursor = bancoDados.query(true, NOME_TABELA, null, null, null, "pais", null, "pais asc", null, null);
        while (cursor.moveToNext()) {
            contagem++;
        }


        String StringPais[] = new String[contagem];
        String StringPaisContNotas[] = new String[contagem];
        String StringPaisContMoeda[] = new String[contagem];
        String StringPaisFinal[] = new String[contagem];


        //cria um array com todos os paises
        cursor = bancoDados.query(true, NOME_TABELA, null, null, null, "pais", null, "pais asc", null, null);
        while (cursor.moveToNext()) {
            StringPais[pais] = cursor.getString(cursor.getColumnIndex("pais"));
            pais++;
        }

        //conta o numero de notas cadastradas no pais selecionado
        for (int i = 0; i < pais; i++) {
            Cursor mCount = bancoDados.rawQuery("select count(*) from Colecao where pais='" + StringPais[i] + "' and tipo='Nota'", null);
            mCount.moveToFirst();
            count = mCount.getInt(0);
            StringPaisContNotas[i] = "" + count;
            mCount.close();
        }

        //conta o numero de moedas cadastradas no pais selecionado
        for (int i = 0; i < pais; i++) {
            Cursor mCountMoeda = bancoDados.rawQuery("select count(*) from Colecao where pais='" + StringPais[i] + "' and tipo='Moeda'", null);
            mCountMoeda.moveToFirst();
            countMoeda = mCountMoeda.getInt(0);
            StringPaisContMoeda[i] = "" + countMoeda;
            mCountMoeda.close();
        }

        for (int i = 0; i < pais; i++) {
            StringPaisFinal[i] = StringPais[i] + "-\t Moedas=" + StringPaisContMoeda[i] + "\t Notas=" + StringPaisContNotas[i];
        }


        final List<String> list = new ArrayList<String>(Arrays.asList(StringPaisFinal));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (activity, android.R.layout.simple_list_item_1, list);
        ListView MostrarDados = activity.findViewById(R.id.lvListagemPais);
        MostrarDados.setAdapter(arrayAdapter);
        TextView tvresumo = activity.findViewById(R.id.tvResumao);
        tvresumo.setText("Total de paises " + contagem);
    }







    public void ResumoColecao(Activity activity) {
        Integer cont1 = 0, cont2 = 0, cont3 = 0, cont4 = 0;
        AbreBanco(activity);

        CRITERIO = "TIPO = 'Nota'";
        cursor = bancoDados.query(NOME_TABELA, null, CRITERIO, null, null, null, null);

        while (cursor.moveToNext()) {cont1++;}

        CRITERIO = "TIPO = 'Nota' and PAIS = 'Brasil'";
        cursor = bancoDados.query(NOME_TABELA, null, CRITERIO, null, null, null, null);

        while (cursor.moveToNext()) {cont2++;}

        CRITERIO = "TIPO = 'Moeda' and PAIS = 'Brasil'";
        cursor = bancoDados.query(NOME_TABELA, null, CRITERIO, null, null, null, null);

        while (cursor.moveToNext()) {cont3++;}

        CRITERIO = "TIPO = 'Moeda'";
        cursor = bancoDados.query(NOME_TABELA, null, CRITERIO, null, null, null, null);

        while (cursor.moveToNext()) {cont4++;}

        Date date = new Date();
        String stringDate = DateFormat.getDateTimeInstance().format(date);
        Resumo = Resumo + "Data do Backup: " + stringDate + "\n \n";
        Resumo = Resumo + "Notas Brasil = " + cont2 + "   \n";
        Integer notaEst = cont1 - cont2;
        Resumo = Resumo + "Notas Estrangeiras= " + notaEst + "   \n";
        Resumo = Resumo + "\n";
        Resumo = Resumo + "Moedas Brasil = " + cont3 + "   \n";
        Integer moedaEst = cont4 - cont3;
        Resumo = Resumo + "Moedas Estrangeiras = " + moedaEst + "   \n";
        Resumo = Resumo + "\n";
        Resumo = Resumo + "Notas Total = " + cont1 + "   \n";
        Resumo = Resumo + "Moedas Total = " + cont4 + "   \n";
        Integer total = cont1 + cont4;
        Resumo = Resumo + "Colecao Total = " + total + "   \n";


        /*
         *
         * procedimento para colocar quantida de pais por pais no arquivo
         *
         */
        Resumo = Resumo + "\n\n Lista de paises \n";

        Integer contagem = 0, pais = 0, count = 0, countMoeda = 0, paisCont = 0;

        //pega quantidade de cadastros
        cursor = bancoDados.query(true, NOME_TABELA, null, null, null, "pais", null, "pais asc", null, null);
        while (cursor.moveToNext()) {
            contagem++;
        }

        String StringPais[] = new String[contagem];
        String StringPaisContNotas[] = new String[contagem];
        String StringPaisContMoeda[] = new String[contagem];
        String StringPaisFinal[] = new String[contagem];

        // pega os paises únicos e armazena em um array

        cursor = bancoDados.query(true, NOME_TABELA, null, null, null, "pais", null, "pais asc", null, null);
        while (cursor.moveToNext()) {
            StringPais[pais] = cursor.getString(cursor.getColumnIndex("pais"));
            pais++;
        }

        for (int i = 0; i < pais; i++) {
            // conta quantas repetiçoes existem de cada um com base no array anterior NOTAS
            Cursor mCount = bancoDados.rawQuery("select count(*) from Colecao where pais='" + StringPais[i] + "' and tipo='Nota'", null);
            mCount.moveToFirst();
            count = mCount.getInt(0);
            StringPaisContNotas[i] = "" + count;
            mCount.close();
        }

        for (int i = 0; i < pais; i++) {
            // conta quantas repetiçoes existem de cada um com base no array anterior Moeda
            Cursor mCountMoeda = bancoDados.rawQuery("select count(*) from Colecao where pais='" + StringPais[i] + "' and tipo='Moeda'", null);
            mCountMoeda.moveToFirst();
            countMoeda = mCountMoeda.getInt(0);
            StringPaisContMoeda[i] = "" + countMoeda;
            mCountMoeda.close();
        }

        //trata as informações em uma linha
        for (int i = 0; i < pais; i++) {
            StringPaisFinal[i] = StringPais[i] +"  ->  Moedas=" + StringPaisContMoeda[i] +"  ->  Notas=" + StringPaisContNotas[i];
            Resumo = Resumo + "\n" + StringPaisFinal[i];
        }

        /*
         *
         * Fim do procedimento pais por pais
         *
         */

        FechaBanco();

        File arq1;
        FileOutputStream fos1;
        byte[] dados;
        try {
            arq1 = new File(Environment.getExternalStorageDirectory(), "/Download/resumoColecao.txt");
            fos1 = new FileOutputStream(arq1.toString());            // transforma o texto digitado em array de bytes
            dados = Resumo.getBytes();            // escreve os dados e fecha o arquivo
            fos1.write(dados);
            fos1.flush();
            fos1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void deletar(Activity activity) {
        AbreBanco(activity);
        try {
            bancoDados.delete(NOME_TABELA, null, null);
            zutilitarios.toast(activity, "Dados Apagados com sucesso!");

            //TODO Limpa nitificacao de backup
            bancoDados.delete(BANCO_MOD,null,null);
            //TODO fim
        } catch (Exception er) {
            zutilitarios.toast(activity, "Erro: " + er);
        }
        FechaBanco();
    }


}
package com.gmail.coliveira.christian.colecao;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class TPesquisa extends AppCompatActivity {

    ZInfoDB zinfodb = new ZInfoDB();


    ListView MostrarDados;
    int pos = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_pesquisa);


        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Cadastrar Novo item", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                NovoCadastro(TPesquisa.this);
            }
        });

        zinfodb.AbreBanco(this);

		/*
            coleta o texto digitado e a opcao de filtro desejada, ai faz uma busca no banco de dados em busca do resultado
		*/

        final EditText edtPesquisa = findViewById(R.id.edtpesquisa);

        edtPesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valorPesquisa = edtPesquisa.getText().toString();
                zinfodb.FiltroPesquisa(TPesquisa.this, valorPesquisa);
                zinfodb.soma(TPesquisa.this,"pesquisa","",valorPesquisa);
            }
        });

        MostrarDados =  findViewById(R.id.lvListagem);

        MostrarDados.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int posicao, long arg3) {
                final TextView t_id = (TextView) view.findViewById(R.id.tvId);
                pos = Integer.parseInt((String) t_id.getText());
                zinfodb.dialog(TPesquisa.this, "atz", "", "Resultado da Pesquisa: " + edtPesquisa.getText().toString());
                zinfodb.enviaDadosDialog(TPesquisa.this, zinfodb.NOME_TABELA, pos);
                return false;
            }
        });

    }



    private void NovoCadastro(Activity activity) {
        zinfodb.dialog(activity, "add", "Nota", "Cadastrar Nota");
    }

    @Override
    public void onBackPressed() {

        this.finish();
    }

}

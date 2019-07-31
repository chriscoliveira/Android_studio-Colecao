package com.gmail.coliveira.christian.colecao;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Relatorios extends AppCompatActivity {

    ZInfoDB zinfodb = new ZInfoDB();

    EditText etPais, etAno, etValor, etTipo;

    ListView MostrarDados;
    int pos = 1;


    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_listagemest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setBackgroundResource(R.color.notanacional);
        toolbar.setTitleTextColor(getResources().getColor(R.color.moedanacional));



        zinfodb.AbreBanco(this);
        zinfodb.FiltrarRelatorios(this,"ultimos");

        MostrarDados = findViewById(R.id.lvListagem);
        MostrarDados.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int posicao, long arg3) {
                final TextView t_id = (TextView) view.findViewById(R.id.tvId);
                pos = Integer.parseInt((String) t_id.getText());
                zinfodb.dialog(Relatorios.this, "atz", "Nota", "Editar Nota");
                zinfodb.enviaDadosDialog(Relatorios.this, zinfodb.NOME_TABELA, pos);
                return false;
            }
        });
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_relatorios, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_qtepais:
                //zinfodb.FiltrarRegistros(this, "Nota", "", "pais asc, ano asc");
                return true;
            case R.id.menu_ultcadas:
                zinfodb.FiltrarRelatorios(this,"ultimos");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

*/

    @Override
    public void onBackPressed() {
        //	Intent intent = new Intent(this, TPrincipal.class);
        //	startActivity(intent);
        this.finish();
    }

}

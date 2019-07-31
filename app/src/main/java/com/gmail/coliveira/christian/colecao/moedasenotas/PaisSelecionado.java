package com.gmail.coliveira.christian.colecao.moedasenotas;

import android.app.Activity;
import android.content.Intent;
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

import com.gmail.coliveira.christian.colecao.R;
import com.gmail.coliveira.christian.colecao.ZInfoDB;

public class PaisSelecionado extends AppCompatActivity {

    ZInfoDB zinfodb = new ZInfoDB();

    EditText etPais, etAno, etValor, etTipo;

    ListView MostrarDados;
    int pos = 1;

    String pais;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle parametrosRecebidos = intent.getExtras();
        pais = parametrosRecebidos.getString("pais");

        setContentView(R.layout.tela_listagempaisselec);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(pais);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Cadastrar Novo item", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                NovoCadastro(PaisSelecionado.this);
            }
        });
        zinfodb.AbreBanco(this);
        zinfodb.FiltrarRegistros(this, "", pais, "tipo");

        MostrarDados = findViewById(R.id.lvListagem);
        MostrarDados.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int posicao, long arg3) {
                final TextView t_id =  view.findViewById(R.id.tvId);
                pos = Integer.parseInt((String) t_id.getText());
                zinfodb.dialog(PaisSelecionado.this, "atz", "Moeda", "Editar Moeda");
                zinfodb.enviaDadosDialog(PaisSelecionado.this, zinfodb.NOME_TABELA, pos);
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_moeda_colecao, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_AnoAZ:
                zinfodb.FiltrarRegistros(this, "", pais, "ano asc");
                return true;
            case R.id.menu_AnoZA:
                zinfodb.FiltrarRegistros(this, "", pais, "ano desc");
                return true;
            case R.id.menu_Por_Moeda_az:
                zinfodb.FiltrarRegistros(this, "", pais, "moeda asc");
                return true;
            case R.id.menu_ID_AZ:
                zinfodb.FiltrarRegistros(this, "", pais, "_id asc");
                return true;
            case R.id.menu_ID_ZA:
                zinfodb.FiltrarRegistros(this, "", pais, "_id desc");
                return true;
            case R.id.menu_Material:
                zinfodb.CarregaOrdenado(this, "", "material asc", pais);
                return true;
            case R.id.menu_Diametro:
                zinfodb.CarregaOrdenado(this, "", "diametro asc", pais);
                return true;
            case R.id.menu_Krause:
                zinfodb.CarregaOrdenado(this, "", "krause asc", pais);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void NovoCadastro(Activity activity) {
        zinfodb.dialog(activity, "add", "", "Cadastrar Moeda");
    }

    @Override
    public void onBackPressed() {
        voltarpagina();
    }

    public void voltarpagina() {
        Intent intent = new Intent(this, ColecaoCompleta.class);
        startActivity(intent);
        this.finish();
    }
}

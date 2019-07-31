package com.gmail.coliveira.christian.colecao.moedasenotas;

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

import com.gmail.coliveira.christian.colecao.R;
import com.gmail.coliveira.christian.colecao.ZInfoDB;

public class Moedas extends AppCompatActivity {

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

        toolbar.setBackgroundResource(R.color.moedaestrangeira);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Cadastrar Novo item", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                NovoCadastro(Moedas.this);
            }
        });

        zinfodb.AbreBanco(this);
        zinfodb.FiltrarRegistros(this, "Moeda", "", "pais asc, ano asc");

        MostrarDados = findViewById(R.id.lvListagem);

        MostrarDados.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int posicao, long arg3) {
                final TextView t_id = view.findViewById(R.id.tvId);
                pos = Integer.parseInt((String) t_id.getText());
                zinfodb.dialog(Moedas.this, "atz", "Moeda", "Editar Moeda Estrangeira");
                zinfodb.enviaDadosDialog(Moedas.this, zinfodb.NOME_TABELA, pos);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_estrangeira, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_Est:
                zinfodb.FiltrarRegistros(this, "Moeda", "", "pais asc, ano asc");
                return true;
            case R.id.menu_Nac:
                zinfodb.FiltrarRegistros(this, "Moeda", "Brasil", "ano asc");
                return true;
            case R.id.menu_AnoAZ:
                zinfodb.CarregaOrdenado(this, "Moeda", "ano asc", "");
                return true;
            case R.id.menu_AnoZA:
                zinfodb.CarregaOrdenado(this, "Moeda", "ano desc", "");
                return true;
            case R.id.menu_PaisAZ:
                zinfodb.CarregaOrdenado(this, "Moeda", "pais asc", "");
                return true;
            case R.id.menu_PaisZA:
                zinfodb.CarregaOrdenado(this, "Moeda", "pais desc", "");
                return true;
            case R.id.menu_Por_Moeda_az:
                zinfodb.CarregaOrdenado(this, "Moeda", "moeda asc", "");
                return true;
            case R.id.menu_ID_AZ:
                zinfodb.CarregaOrdenado(this, "Moeda", "_id asc", "");
                return true;
            case R.id.menu_ID_ZA:
                zinfodb.CarregaOrdenado(this, "Moeda", "_id desc", "");
                return true;
            case R.id.menu_Material:
                zinfodb.CarregaOrdenado(this, "Moeda", "material asc", "");
                return true;
            case R.id.menu_Diametro:
                zinfodb.CarregaOrdenado(this, "Moeda", "diametro asc", "");
                return true;
            case R.id.menu_Krause:
                zinfodb.CarregaOrdenado(this, "Moeda", "krause asc", "");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void NovoCadastro(Activity activity) {
        zinfodb.dialog(activity, "add", "Moeda", "Cadastrar Moeda Estrangeira");
    }

    @Override
    public void onBackPressed() {

        this.finish();
    }

}

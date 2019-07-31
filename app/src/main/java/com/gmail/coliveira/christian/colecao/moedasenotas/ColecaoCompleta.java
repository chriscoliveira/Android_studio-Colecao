package com.gmail.coliveira.christian.colecao.moedasenotas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gmail.coliveira.christian.colecao.Inicio;
import com.gmail.coliveira.christian.colecao.R;
import com.gmail.coliveira.christian.colecao.ZInfoDB;

public class ColecaoCompleta extends AppCompatActivity {
    ZInfoDB zinfodb = new ZInfoDB();

    ListView MostrarDados;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_listagem_pais);

        zinfodb.contaPais(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(ColecaoCompleta.this.getTitle());

        MostrarDados = findViewById(R.id.lvListagemPais);
        MostrarDados.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                irParaPais(arg2);
            }

        });

    }

    public void irParaPais(int arg2) {
        Intent intent = new Intent(ColecaoCompleta.this, PaisSelecionado.class);
        //TODO preciso capturar somente o nome do pais da linha clidada
        String[] enviapais = MostrarDados.getItemAtPosition(arg2).toString().split("-");
        //Log.i("PAIS", "" + enviapais[0]);
        intent.putExtra("pais", enviapais[0]);
        startActivity(intent);
        ColecaoCompleta.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_paispais, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_VoltarPagina:
                Intent intent = new Intent(this, Inicio.class);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        this.finish();
    }


}
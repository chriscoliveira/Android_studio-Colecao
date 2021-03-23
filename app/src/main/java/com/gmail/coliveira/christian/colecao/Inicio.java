package com.gmail.coliveira.christian.colecao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.coliveira.christian.colecao.fragment.ConversorFragment;
import com.gmail.coliveira.christian.colecao.fragment.Euro1Fragment;
import com.gmail.coliveira.christian.colecao.fragment.Euro2Fragment;
import com.gmail.coliveira.christian.colecao.fragment.Euro3Fragment;
import com.gmail.coliveira.christian.colecao.fragment.Euro4Fragment;
import com.gmail.coliveira.christian.colecao.fragment.Euro5Fragment;
import com.gmail.coliveira.christian.colecao.fragment.Simbolo1Fragment;
import com.gmail.coliveira.christian.colecao.fragment.Simbolo2Fragment;
import com.gmail.coliveira.christian.colecao.fragment.NumerosFragment;
import com.gmail.coliveira.christian.colecao.login.ConfiguracaoFirebase;
import com.gmail.coliveira.christian.colecao.moedasenotas.ColecaoCompleta;
import com.gmail.coliveira.christian.colecao.moedasenotas.Moedas;
import com.gmail.coliveira.christian.colecao.moedasenotas.MoedasBr;
import com.gmail.coliveira.christian.colecao.moedasenotas.Notas;
import com.gmail.coliveira.christian.colecao.moedasenotas.NotasBr;
import com.gmail.coliveira.christian.colecao.services.Notificacao;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.gmail.coliveira.christian.colecao.ZInfoDB.NOME_TABELA;
import static com.gmail.coliveira.christian.colecao.ZInfoDB.VALOR_VENDA;

public class Inicio extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Dialog dialog = null;
    Button btApagar;
    FirebaseAuth auth;
    Cursor cursor;

    String Resumo = "", CRITERIO = null;
    ZInfoDB zinfodb = new ZInfoDB();
    Notificacao notificacao = new Notificacao();
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inicio);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Cadastrar Novo item", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                zinfodb.dialog(Inicio.this, "add", "", "Cadastrar Nota");
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        Menu menu = navigationView.getMenu();

        MenuItem moedaNac= menu.findItem(R.id.nav_moedanac);
        MenuItem notaNac= menu.findItem(R.id.nav_notanac);
        MenuItem moedaInt= menu.findItem(R.id.nav_moedaint);
        MenuItem notaInt= menu.findItem(R.id.nav_notaint);
        MenuItem colecao= menu.findItem(R.id.nav_colecao);
        MenuItem add= menu.findItem(R.id.nav_novo);
        MenuItem euro1= menu.findItem(R.id.nav_euro);
        MenuItem euro2= menu.findItem(R.id.nav_euro1);
        MenuItem euro3= menu.findItem(R.id.nav_euro2);
        MenuItem euro4= menu.findItem(R.id.nav_euro3);
        MenuItem euro5= menu.findItem(R.id.nav_euro4);


        setTextColorForMenuItem(moedaNac, R.color.notanacional);
        setTextColorForMenuItem(notaNac, R.color.notanacional);

        setTextColorForMenuItem(notaInt, R.color.moedaestrangeira);
        setTextColorForMenuItem(moedaInt, R.color.moedaestrangeira);

        setTextColorForMenuItem(colecao, R.color.notaestrangeira);
        setTextColorForMenuItem(add, R.color.colorAccent);


        setTextColorForMenuItem(euro1, R.color.moedaestrangeira);
        setTextColorForMenuItem(euro2, R.color.moedaestrangeira);
        setTextColorForMenuItem(euro3, R.color.moedaestrangeira);
        setTextColorForMenuItem(euro4, R.color.moedaestrangeira);
        setTextColorForMenuItem(euro5, R.color.moedaestrangeira);

        navigationView.setNavigationItemSelectedListener(this);




        //exibe a versao de compilacao
        try {
            TextView tvVersao = findViewById(R.id.tvVersa);
            Date buildDate = new Date(BuildConfig.BUILD_TIME);
            tvVersao.setText(" " + buildDate.toString());
        } catch (Exception e) {
            Log.i("TESTE", "erro ao exibir a versao" + e);
        }


        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();




        //
        //imagens


    }


    private void setTextColorForMenuItem(MenuItem menuItem, @ColorRes int color) {
        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, color)), 0, spanString.length(), 0);
        menuItem.setTitle(spanString);
    }


    @Override
    protected void onStart() {
        super.onStart();
        try {
            resumo();
        } catch (Exception e) {
            Log.d("erro", "" + e);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            mensagemSaida(this, "Fechar Aplicativo", "Tem certeza que deseja sair?");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            try {
                auth.signOut();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_pesquisa) {
            startActivity(new Intent(this, TPesquisa.class));
        } else if (id == R.id.nav_moedaint) {
            startActivity(new Intent(this, Moedas.class));
        } else if (id == R.id.nav_moedanac) {
            startActivity(new Intent(this, MoedasBr.class));
        } else if (id == R.id.nav_notaint) {
            startActivity(new Intent(this, Notas.class));
        } else if (id == R.id.nav_notanac) {
            startActivity(new Intent(this, NotasBr.class));
        } else if (id == R.id.nav_relatorios) {
            startActivity(new Intent(this, Relatorios.class));
        } else if (id == R.id.nav_colecao) {
            startActivity(new Intent(this, ColecaoCompleta.class));
        } else if (id == R.id.nav_option) {
            startActivity(new Intent(this, TOpcoes.class));
        } else if (id == R.id.nav_inicio) {
            /*ResumoFragment resumoFragment = new ResumoFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame,resumoFragment);
            transaction.commit();
            */


        } else if (id == R.id.nav_euro) {
            Euro1Fragment euro1Fragment = new Euro1Fragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, euro1Fragment);
            transaction.commit();
        } else if (id == R.id.nav_euro1) {
            Euro2Fragment euro2Fragment = new Euro2Fragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, euro2Fragment);
            transaction.commit();
        } else if (id == R.id.nav_euro2) {
            Euro3Fragment euro3Fragment = new Euro3Fragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, euro3Fragment);
            transaction.commit();
        } else if (id == R.id.nav_euro3) {
            Euro4Fragment euro4Fragment = new Euro4Fragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, euro4Fragment);
            transaction.commit();
        } else if (id == R.id.nav_euro4) {
            Euro5Fragment euro5Fragment = new Euro5Fragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, euro5Fragment);
            transaction.commit();
        } else if (id == R.id.nav_simbolos) {
            Simbolo1Fragment simbolo1Fragment = new Simbolo1Fragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, simbolo1Fragment);
            transaction.commit();
        } else if (id == R.id.nav_simbolos1) {
            Simbolo2Fragment simbolo2Fragment = new Simbolo2Fragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, simbolo2Fragment);
            transaction.commit();
        }else if (id == R.id.nav_datas) {
            ConversorFragment conversorFragment = new ConversorFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, conversorFragment);
            transaction.commit();
        } else if (id == R.id.nav_numeracao) {
            NumerosFragment numerosFragment = new NumerosFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, numerosFragment);
            transaction.commit();
        } else if (id == R.id.nav_novo) {

            zinfodb.dialog(this, "add", "", "Cadastrar");
        } else if (id == R.id.nav_sair) {
            mensagemSaida(this, "Fechar Aplicativo", "Tem certeza que deseja sair?");
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void mensagemSaida(Activity activity, String titulo, String mensagem) {
        AlertDialog.Builder CaixaAlerta = new AlertDialog.Builder(activity);
        CaixaAlerta.setMessage(mensagem);
        CaixaAlerta.setTitle(titulo);
        CaixaAlerta.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finishAffinity();
            }
        });
        CaixaAlerta.setNegativeButton("NAO", null);
        CaixaAlerta.show();

    }


    public void resumo() {
        Integer cont1 = 0, cont2 = 0, cont3 = 0, cont4 = 0;
        zinfodb.AbreBanco(this);


        TextView tvColecao = findViewById(R.id.tvColecaoTotal);

        // verifica os criterios do filtro

        CRITERIO = "TIPO = 'Nota'";
        cursor = zinfodb.bancoDados.query(NOME_TABELA, null, CRITERIO, null, null, null, null);

        while (cursor.moveToNext()) {
            cont1++;
        }

        CRITERIO = "TIPO = 'Nota' and PAIS = 'Brasil'";
        cursor = zinfodb.bancoDados.query(NOME_TABELA, null, CRITERIO, null, null, null, null);

        while (cursor.moveToNext()) {
            cont2++;
        }

        CRITERIO = "TIPO = 'Moeda' and PAIS = 'Brasil'";
        cursor = zinfodb.bancoDados.query(NOME_TABELA, null, CRITERIO, null, null, null, null);

        while (cursor.moveToNext()) {
            cont3++;
        }

        CRITERIO = "TIPO = 'Moeda'";
        cursor = zinfodb.bancoDados.query(NOME_TABELA, null, CRITERIO, null, null, null, null);

        while (cursor.moveToNext()) {
            cont4++;
        }

        SimpleDateFormat Hora = new SimpleDateFormat("HH");

        Date date = new Date();
        Integer horaFormatada = Integer.valueOf(Hora.format(date));

        String saudacao = "";
        if (horaFormatada >= 18 && horaFormatada < 6) {
            saudacao = "Boa noite! \n\n\n";
        } else if (horaFormatada >= 12 && horaFormatada < 18) {
            saudacao = "Boa tarde! \n\n\n";
        } else if (horaFormatada >= 6 && horaFormatada < 12) {
            saudacao = "Bom dia! \n\n\n";
        }
        Double valor_total = 0.0;
        cursor = zinfodb.bancoDados.query(NOME_TABELA, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            valor_total = valor_total + cursor.getFloat(cursor.getColumnIndex(VALOR_VENDA));
        }
        String svalor_total ="";
        svalor_total = new DecimalFormat("#,##0.00").format(valor_total);

        Resumo = "";
        Resumo = saudacao;
        String stringDate = DateFormat.getDateTimeInstance().format(date);
        Resumo = Resumo + stringDate + "\n \n";
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
        Resumo = Resumo + "Colecao Total = " + total + "   \n\n";
        Resumo = Resumo + "Valor Total = R$ " + svalor_total+"   \n";

        tvColecao.setText(Resumo);
    }
}

package com.gmail.coliveira.christian.colecao;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

public class CarregaSistema extends Activity {
	ZInfoDB infodb = new ZInfoDB();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carregando);
		// cria o banco de dados (InfoDB.java)
		try {
			infodb.CriaBanco(this);
		} catch (SQLException e) {
			Log.i("Cria banco.java", "Erro: " + e);
		}

		try {
			startActivity(new Intent(CarregaSistema.this,Inicio.class));
			CarregaSistema.this.finish();
		}catch (Exception e){
			Log.i("AVISO",e.getMessage().toString());
		}
	}

}

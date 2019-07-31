package com.gmail.coliveira.christian.colecao;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;

public class ZUtilitarios {

	public void mensagem(Activity activity, String titulo, String mensagem) {
		AlertDialog.Builder CaixaAlerta = new AlertDialog.Builder(activity);
		CaixaAlerta.setMessage(mensagem);
		CaixaAlerta.setTitle(titulo);
		CaixaAlerta.setNeutralButton("OK", null);
		CaixaAlerta.show();
	}

	public void toast(Activity activity, String aviso) {
		Toast.makeText(activity, aviso, Toast.LENGTH_SHORT).show();
	}

}

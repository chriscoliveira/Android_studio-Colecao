package com.gmail.coliveira.christian.colecao;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.coliveira.christian.colecao.login.ConfiguracaoFirebase;
import com.gmail.coliveira.christian.colecao.login.UsuarioFirebase;
import com.gmail.coliveira.christian.colecao.services.Permissao;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Exportar extends AppCompatActivity {

    ZInfoDB zinfodb = new ZInfoDB();

    FirebaseStorage storage;
    private ProgressBar progressBar;
    private StorageReference storageReference;
    private String[] permissoesNecessarias = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_exportar);

        Permissao.validarPermissoes(this, permissoesNecessarias, 1);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        FirebaseUser usuarioRecebe = UsuarioFirebase.getUsuarioAtual();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }

    }

    private void alertaValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    public void enviarEmail() {
        try {
            // prepara o conteudo do email
            progressBar.findViewById(R.id.progressBar2);
            progressBar.setProgress(70);
            Date date = new Date();
            String stringDate = DateFormat.getDateTimeInstance().format(date);
            String assunto = "Colecao de Moedas - backup";

            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "/Download/resumoColecao.txt");
            BufferedReader br = null;
            String line;
            String sql = "\n";
            try {
                br = new BufferedReader(new FileReader(file));


                while ((line = br.readLine()) != null) {
                    sql += line + " \n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String messagem = "Segue em anexo um backup dos dados - " + stringDate + " - " + sql;


            //anexa os arquivos ao email
            String filename1 = "bancoMoedas.txt";
            String filename2 = "bancoMoedas.csv";
            String filename3 = "bancoNotas.csv";
            String filename4 = "resumoColecao.txt";

            File file1 = new File(Environment.getExternalStorageDirectory() + File.separator + "Download", filename1);
            File file2 = new File(Environment.getExternalStorageDirectory() + File.separator + "Download", filename2);
            File file3 = new File(Environment.getExternalStorageDirectory() + File.separator + "Download", filename3);
            File file4 = new File(Environment.getExternalStorageDirectory() + File.separator + "Download", filename4);

            Uri contentUri1 = FileProvider.getUriForFile(this, "com.gmail.coliveira.christian.colecao", file1);
            Uri contentUri2 = FileProvider.getUriForFile(this, "com.gmail.coliveira.christian.colecao", file2);
            Uri contentUri3 = FileProvider.getUriForFile(this, "com.gmail.coliveira.christian.colecao", file3);
            Uri contentUri4 = FileProvider.getUriForFile(this, "com.gmail.coliveira.christian.colecao", file4);

            ArrayList<Uri> uris = new ArrayList<Uri>();
            uris.add(contentUri1);
            uris.add(contentUri2);
            uris.add(contentUri3);
            uris.add(contentUri4);


            //declara os itens para o compartilhamento
            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uris);
            shareIntent.setType("text/plain");
            FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
            String email = usuario.getCurrentUser().getEmail();

            shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, assunto);
            shareIntent.putExtra(Intent.EXTRA_TEXT, messagem);

            startActivity(Intent.createChooser(shareIntent, ""));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "aviso   " + e, Toast.LENGTH_LONG).show();
        }
    }


    public void upload() {
        try {
            SimpleDateFormat formataData = new SimpleDateFormat("ddMMyy_HHmm");
            Date data = new Date();
            String dataFormatada = formataData.format(data);

            String userId= UsuarioFirebase.getIdUser();

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            File sdcard = Environment.getExternalStorageDirectory();
            final String filename1 = "bancoMoedas.txt";
            final String filename2 = "bancoMoedas.csv";
            final String filename3 = "bancoNotas.csv";
            final String filename4 = "resumoColecao.txt";

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //TODO file1
            Uri file1 = Uri.fromFile(new File(sdcard, "/Download/" + filename1));
            StorageReference ref = storageReference.child(userId).child("bancodados/" + filename1);
            ref.putFile(file1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Exportar.this, filename1 + " Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Exportar.this, filename1 + " Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            StorageReference ref_ = storageReference.child(userId).child("bancodados_"+dataFormatada+"/" + filename1);
            ref_.putFile(file1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Exportar.this, filename1 + " Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Exportar.this, filename1 + " Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //TODO file2
            Uri file2 = Uri.fromFile(new File(sdcard, "/Download/" + filename2));
            StorageReference ref1 = storageReference.child(userId).child("bancodados/" + filename2);
            ref1.putFile(file2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Exportar.this, filename2 + " Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Exportar.this, filename2 + " Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            StorageReference ref_1 = storageReference.child(userId).child("bancodados_"+dataFormatada+"/" + filename2);
            ref_1.putFile(file2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Exportar.this, filename1 + " Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Exportar.this, filename1 + " Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //TODO file3
            Uri file3 = Uri.fromFile(new File(sdcard, "/Download/" + filename3));
            StorageReference ref2 = storageReference.child(userId).child("bancodados/" + filename3);
            ref2.putFile(file3)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Exportar.this, filename3 + " Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Exportar.this, filename3 + " Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            StorageReference ref_2 = storageReference.child(userId).child("bancodados_"+dataFormatada+"/" + filename3);
            ref_2.putFile(file3)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Exportar.this, filename1 + " Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Exportar.this, filename1 + " Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //TODO file4
            Uri file4 = Uri.fromFile(new File(sdcard, "/Download/" + filename4));
            StorageReference ref3 = storageReference.child(userId).child("bancodados/" + filename4);
            ref3.putFile(file4)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Exportar.this, filename4 + " Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Exportar.this, filename4 + " Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            StorageReference ref_3 = storageReference.child(userId).child("bancodados_"+dataFormatada+"/" + filename4);
            ref_3.putFile(file4)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Exportar.this, filename1 + " Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Exportar.this, filename1 + " Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            Log.i("erro", "erro     :    " + e);
        }
    }


    public void exportar(View view) {

        zinfodb.ResumoColecao(Exportar.this);
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "/Download/resumoColecao.txt");
        BufferedReader br = null;
        String line;
        String sql = "\n";
        try {
            br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {

                sql += line + " \n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(Exportar.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(Exportar.this);
        }
        builder.setTitle("Exportar Registros")
                .setMessage(sql)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        ProgressBar progressBarCircle = findViewById(R.id.progressBar2);
                        progressBarCircle.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        acao( );
                        final TextView tvStatus = findViewById(R.id.tvStatus);
                        Button btExportar = findViewById(R.id.bt_exportar);
                        btExportar.setVisibility(View.GONE);
                        tvStatus.setText("Aguarde enquanto o Backup é iniciado....");

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void acao() {
        progressBar.setVisibility(View.VISIBLE);

                    zinfodb.ExportaTXT(Exportar.this);

                    zinfodb.ExportaCSV(Exportar.this);

                    enviarEmail();


                    upload();

                    finish();
    }

    @Override
    public void onBackPressed() {
        //     Intent intent = new Intent(this, TPrincipal.class);
        //   startActivity(intent);
        this.finish();
    }




}

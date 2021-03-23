package com.gmail.coliveira.christian.colecao;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.gmail.coliveira.christian.colecao.login.UsuarioFirebase;
import com.gmail.coliveira.christian.colecao.services.Permissao;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TOpcoes extends AppCompatActivity {


    protected SQLiteDatabase bancoDados = null;
    ZUtilitarios zutilitarios = new ZUtilitarios();
    ZInfoDB zinfodb = new ZInfoDB();
    CheckBox cbemail, cbnuvem;
    Integer status_importacaoResumo = 0, status_importacaoBanco = 0;
    FirebaseStorage storage;
    private StorageReference storageReference;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opcoes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setBackgroundResource(R.color.notanacional);

        Permissao.validarPermissoes(this, permissoesNecessarias, 1);

        Button btApagar = findViewById(R.id.btApagar);
        Button btExportar = findViewById(R.id.btExportar);

        Button btImportar = findViewById(R.id.btImportar);
        Button btImportarManual = findViewById(R.id.btImportarManual);


        btImportar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //importa da nuvem

                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();
                String userId = UsuarioFirebase.getIdUser();

                final String filename4 = "bancoMoedas.txt";
                final String filename1 = "resumoColecao.txt";

                StorageReference islandRef1 = storageReference.child(userId).child("bancodados").child(filename1);
                File rootPath1 = new File(Environment.getExternalStorageDirectory(), "Download");
                if (!rootPath1.exists()) {
                    rootPath1.mkdirs();
                }

                final File localFile1 = new File(rootPath1, filename1);
                islandRef1.getFile(localFile1).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(TOpcoes.this, "Arquivo baixado " + localFile1.toString(), Toast.LENGTH_SHORT).show();

                        //  updateDb(timestamp,localFile.toString(),position);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(TOpcoes.this, "Erro ao baixar o arquivo : " + localFile1.toString(), Toast.LENGTH_SHORT).show();

                    }
                });

                StorageReference islandRef4 = storageReference.child(userId).child("bancodados").child(filename4);
                File rootPath4 = new File(Environment.getExternalStorageDirectory(), "Download");
                if (!rootPath4.exists()) {
                    rootPath4.mkdirs();
                }

                final File localFile4 = new File(rootPath4, filename4);
                islandRef4.getFile(localFile4).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(TOpcoes.this, "Arquivo baixado " + localFile4.toString(), Toast.LENGTH_SHORT).show();
                        //importa para o SQLite

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

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(TOpcoes.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(TOpcoes.this);
                        }
                        builder.setTitle("Importar registros")
                                .setMessage(sql + "\nTem certeza que deseja importar Registros para o banco?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        zinfodb.importarLista(TOpcoes.this);
                                        startActivity(new Intent(TOpcoes.this, Inicio.class));
                                        TOpcoes.this.finish();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                        startActivity(new Intent(TOpcoes.this, Inicio.class));
                                        TOpcoes.this.finish();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        //fim da importacao do SQLite

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(TOpcoes.this, "Erro ao baixar o arquivo : " + localFile4.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                //fim da importacao nuvem

            }
        });

        btImportarManual.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //importa para o SQLite
                final String filename4 = "bancoMoedas.txt";
                final String filename1 = "resumoColecao.txt";
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

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(TOpcoes.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(TOpcoes.this);
                }
                builder.setTitle("Importar registros")
                        .setMessage(sql + "\nTem certeza que deseja importar Registros para o banco?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                zinfodb.importarLista(TOpcoes.this);
                                startActivity(new Intent(TOpcoes.this, Inicio.class));
                                TOpcoes.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                startActivity(new Intent(TOpcoes.this, Inicio.class));
                                TOpcoes.this.finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                //fim da importacao do SQLite

            }

        });

        btExportar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(TOpcoes.this, Exportar.class));
                finish();
            }
        });

        btApagar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(TOpcoes.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(TOpcoes.this);
                }
                builder.setTitle("Apagar Banco de Dados")
                        .setMessage("Tem certeza que deseja apagar o banco de dados?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                zinfodb.deletar(TOpcoes.this);

                                //startActivity(new Intent(TOpcoes.this, Inicio.class));
                                //TOpcoes.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_opcoes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_moedas_euro1:
                setContentView(R.layout.op_euro1);
                return true;
            case R.id.menu_moedas_euro2:
                setContentView(R.layout.op_euro2);
                return true;
            case R.id.menu_moedas_euro3:
                setContentView(R.layout.op_euro3);
                return true;
            case R.id.menu_moedas_euro4:
                setContentView(R.layout.op_euro4);
                return true;
            case R.id.menu_locais_euro:
                setContentView(R.layout.op_cunhagem_euro);
                return true;
            case R.id.menu_moedas_japao:
                setContentView(R.layout.op_data_japao);
                return true;
            case R.id.menu_simbolos_moedas1:
                setContentView(R.layout.op_simbol1);
                return true;
            case R.id.menu_simbolos_moedas2:
                setContentView(R.layout.op_simbol2);
                return true;
            case R.id.menu_simbolos_moedas3:
                setContentView(R.layout.op_numeracao);
                return true;
            case R.id.menu_simbolos_moedas4:
                setContentView(R.layout.op_conversor);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //     Intent intent = new Intent(this, TPrincipal.class);
        //   startActivity(intent);
        this.finish();
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


}

package com.gmail.coliveira.christian.colecao.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i("notificacao", "notificacao recebida");

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }



}


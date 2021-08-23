package com.example.commutingapp.utils.InternetConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class ConnectionManager {

    private final ConnectivityManager connectivityManager;

    public ConnectionManager(Context form) {
        connectivityManager = (ConnectivityManager) form.getSystemService(Context.CONNECTIVITY_SERVICE);

    }


    public boolean internetConnectionAvailable() {
        NetworkInfo internetConnection = connectivityManager.getActiveNetworkInfo();
        return internetConnection != null &&
                internetConnection.isConnected() &&
                internetConnection.isAvailable();

    }


}

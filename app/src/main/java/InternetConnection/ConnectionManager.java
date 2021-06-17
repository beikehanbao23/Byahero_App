package InternetConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatActivity;

public class ConnectionManager {

    private final ConnectivityManager connectivityManager;

    public ConnectionManager(AppCompatActivity form) {
        connectivityManager = (ConnectivityManager) form.getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    private boolean hasWifiConnection() {
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiConnection != null && wifiConnection.isConnected();
    }

    private boolean hasMobileDataConnection() {
        NetworkInfo mobileDataConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mobileDataConnection != null && mobileDataConnection.isConnected();
    }

    public boolean PhoneHasInternetConnection() {
        return hasWifiConnection() || hasMobileDataConnection();
    }


}

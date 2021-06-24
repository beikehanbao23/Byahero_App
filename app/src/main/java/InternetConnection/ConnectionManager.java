package InternetConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatActivity;

public class ConnectionManager {

    private final ConnectivityManager connectivityManager;

    public ConnectionManager(Context form) {
        connectivityManager = (ConnectivityManager) form.getSystemService(form.CONNECTIVITY_SERVICE);

    }


    public boolean PhoneHasInternetConnection() {
        NetworkInfo internetConnection = connectivityManager.getActiveNetworkInfo();
        return internetConnection != null && internetConnection.isConnected() && internetConnection.isAvailable();

    }


}

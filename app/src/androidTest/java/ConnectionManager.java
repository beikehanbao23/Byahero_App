import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatActivity;

public class ConnectionManager {

    private static ConnectivityManager connectivityManager;

    public ConnectionManager(AppCompatActivity form){
         connectivityManager = (ConnectivityManager)form.getSystemService(Context.CONNECTIVITY_SERVICE);
    }



private static boolean isNotConnectedToWifi(){
    NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    return wifiConnection == null && !wifiConnection.isConnected();
}

private static boolean isNotConnectedToMobileData(){
    NetworkInfo mobileData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    return mobileData == null && !mobileData.isConnected();
}

public static boolean hasNoInternetConnection(){
    return isNotConnectedToMobileData()||isNotConnectedToWifi();
}


}

package br.cesupa.fisiovr.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by luis.portugal on 03/08/2017.
 */

public class Util {
    public static boolean isConnect(Context contexto){
        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if ((netInfo != null) && (netInfo.isConnectedOrConnecting()) && (netInfo.isAvailable()))
            return true;

        Toast.makeText(contexto, "No Internet Connection.", Toast.LENGTH_LONG).show();
        return false;
    }
}

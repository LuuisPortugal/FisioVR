package br.cesupa.fisiovr.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
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


    //Mostra o carregando
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(Context c, final View view1, final View view2, final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = c.getResources().getInteger(android.R.integer.config_shortAnimTime);

            view1.setVisibility(show ? View.GONE : View.VISIBLE);
            view1.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view1.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            view2.setVisibility(show ? View.VISIBLE : View.GONE);
            view2.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view2.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            view2.setVisibility(show ? View.VISIBLE : View.GONE);
            view1.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

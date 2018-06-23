package study.group.Utilities;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;

import study.group.R;

public class ConnectionDetector {
    Context context;
    public ProgressDialog mProgressDialog;

    public ConnectionDetector(Context context)
    {
        this.context = context;
    }

    public boolean isConnected()
    {
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivity != null)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if(info != null)
            {
                if(info.getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.bad_connection);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        }).show();
        return false;
    }

}

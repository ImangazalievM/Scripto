package imangazaliev.scripto.sample.interfaces;

import android.content.Context;
import android.widget.Toast;

import imangazaliev.scripto.js.ScriptoSecure;

public class AndroidInterface {

    private Context context;

    public AndroidInterface(Context context) {
        this.context = context;
    }

    @ScriptoSecure
    public void showToastMessage(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}

package com.diego09310.freakcontroller;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by diego on 4/05/17.
 */

public class SettingsActivity extends Activity {

    public static final String SERVER_IP = "ip_addr";
    public static final String PORT = "port";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}

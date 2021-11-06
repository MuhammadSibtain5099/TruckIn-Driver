package com.sibtain.truckindriver.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesCustomClass {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String firstLunch = "lunchFirstTime";
    SharedPreferences sharedpreferences;
    public SharedPreferencesCustomClass(){

    }
    public SharedPreferencesCustomClass(Context context){
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }

    public Boolean save() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(firstLunch, true);
        return editor.commit();
    }

    public Boolean getValue(){

        return sharedpreferences.getBoolean(firstLunch,false);

    }
}

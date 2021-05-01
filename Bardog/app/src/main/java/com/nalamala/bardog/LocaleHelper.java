package com.nalamala.bardog;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Locale;

public class LocaleHelper {

    final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    final String DEFAULT_LANGUAGE = "iw";

    Context mContext;

    public LocaleHelper(Context context) {
        mContext = context;
    }

    public void setDefault() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        setLocale(preferences.getString(SELECTED_LANGUAGE, DEFAULT_LANGUAGE));
    }

    public boolean setNewLanguage(String lang) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // language already set
        if (lang.equals(preferences.getString(SELECTED_LANGUAGE, DEFAULT_LANGUAGE))) return false;

        // save new language
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, lang);
        editor.apply();

        setLocale(lang);

        return true;
    }

    public void initLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String lang = preferences.getString(SELECTED_LANGUAGE, DEFAULT_LANGUAGE);
        setLocale(lang);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = mContext.getResources();
        Configuration configuration = mContext.getResources().getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

}

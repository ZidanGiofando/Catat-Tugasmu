package com.example.dailytask.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "DailyTaskPreference";

    // Keys
    private static final String KEY_IS_LOGIN = "is_login";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHOTO = "photo";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {

        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        editor = preferences.edit();
    }

    /**
     * Simpan data user setelah login/register berhasil
     */
    public void saveUser(String uid,
                         String name,
                         String email,
                         String photo) {

        editor.putBoolean(KEY_IS_LOGIN, true);
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHOTO, photo);

        editor.apply();
    }

    /**
     * Status login
     */
    public boolean isLoggedIn() {

        return preferences.getBoolean(KEY_IS_LOGIN, false);

    }

    /**
     * UID Firebase
     */
    public String getUid() {

        return preferences.getString(KEY_UID, "");

    }

    /**
     * Nama user
     */
    public String getName() {

        return preferences.getString(KEY_NAME, "");

    }

    /**
     * Email user
     */
    public String getEmail() {

        return preferences.getString(KEY_EMAIL, "");

    }

    /**
     * URL foto profil
     */
    public String getPhoto() {

        return preferences.getString(KEY_PHOTO, "");

    }

    /**
     * Update nama
     */
    public void setName(String name) {

        editor.putString(KEY_NAME, name);
        editor.apply();

    }

    /**
     * Update email
     */
    public void setEmail(String email) {

        editor.putString(KEY_EMAIL, email);
        editor.apply();

    }

    /**
     * Update foto profil
     */
    public void setPhoto(String photo) {

        editor.putString(KEY_PHOTO, photo);
        editor.apply();

    }

    /**
     * Logout user
     */
    public void logout() {

        editor.clear();
        editor.apply();

    }

}
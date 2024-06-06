package com.loptr.kherod.uygdl.utlities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.model.Admob;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Config {

    public static final String BASE_URL = "https://apps-player.com/liteplayer/API/";
    public static final String PLAYLIST_KEY = "Playlist_items" ;
    public static final String DEVICE_LANGUAGE = "deviceLanguage";
    public static final String REMOVE_ADS = "remove_ads" ;
    public static final String PRODUCT_ID = "product_id" ;
    public static int REMOVE_ADS_MONTHLY_INDEX = 0 ;
    public static int REMOVE_ADS_YEARLY_INDEX = 1 ;

    public static boolean ifShouldDisableAds(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.RemoveAdKey) , false);
    }

    public static void shareApp(Context context){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = context.getString(R.string.download) +
                "\n\n" +
                "https://play.google.com/store/apps/details?id=" + context.getPackageName();
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "share with"));
    }

    public static void updateLocale(Context context) {
        String lang = getAppLanguage(context);
        Log.i("ab_do" , "App language " + lang);
        if (lang.equals(context.getString(R.string.default_language))) {
            Log.i("ab_do" , "default language");
            setLocaleToDefaultDeviceLanguage(context);
        }
        else {
            String localeStr = lang.equals(context.getString(R.string.arabic)) ? "ar" : "en";
            setLocale(context, localeStr);
        }
    }

    private static void setLocaleToDefaultDeviceLanguage(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = sharedPreferences.getString(Config.DEVICE_LANGUAGE , Locale.getDefault().getLanguage());
        setLocale(context, lang);
    }

    public static void updateTheme(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        int theme_id = sharedPreferences.getInt(activity.getString(R.string.THEME_KEY) , activity.getResources().getInteger(R.integer.default_theme));
        Log.i("ab_do" , "Theme Id " + theme_id);

        if (theme_id == activity.getResources().getInteger(R.integer.default_theme)) {
            activity.setTheme(R.style.Theme_LitePlayer);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.black_theme)) {
            activity.setTheme(R.style.black_theme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_brown)) {
            activity.setTheme(R.style.theme_brown);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_green)) {
            activity.setTheme(R.style.theme_green);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_grey)) {
            activity.setTheme(R.style.theme_grey);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_Pink)) {
            activity.setTheme(R.style.theme_Pink);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_red)) {
            activity.setTheme(R.style.theme_red);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_Deep_Purple)) {
            activity.setTheme(R.style.theme_Deep_Purple);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_light_green)) {
            activity.setTheme(R.style.theme_light_green);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_orange)) {
            activity.setTheme(R.style.theme_orange);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_Purple)) {
            activity.setTheme(R.style.theme_Purple);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_purple_200)) {
            activity.setTheme(R.style.theme_purple_200);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_Teal)) {
            activity.setTheme(R.style.theme_Teal);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_yellow)) {
            activity.setTheme(R.style.theme_yellow);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_red_dark)) {
            activity.setTheme(R.style.theme_red_dark);
        }
    }

    private static void setLocale(Context context, String localeStr) {
        Locale locale = new Locale(localeStr);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        //config.setLayoutDirection(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static String getAppLanguage (Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.language_key), context.getString(R.string.default_language));
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String formatTime(long timestamp){
        Date date = new Date(timestamp);
        SimpleDateFormat formatter= new SimpleDateFormat("mm:ss" , Locale.getDefault());
        return formatter.format(date);
    }

    public static void loadNativeAd(Context context, TemplateView templateView){
        if(admob != null){

            AdLoader adLoader = new AdLoader.Builder(context, Config.admob.getNativeAd())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NotNull NativeAd nativeAd) {
                            templateView.setNativeAd(nativeAd);
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    public static Admob admob;

}

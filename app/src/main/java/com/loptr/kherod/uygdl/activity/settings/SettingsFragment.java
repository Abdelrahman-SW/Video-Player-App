package com.loptr.kherod.uygdl.activity.settings;

import static com.loptr.kherod.uygdl.utlities.Config.updateLocale;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.utlities.Config;

import petrov.kristiyan.colorpicker.ColorPicker;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    ColorPicker colorPicker ;
    //InterstitialAd interstitial_Ad = null;
    AlertDialog dialog;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_screen, rootKey);
        // we must update the summary for each preference form the default shared Preference value
        // as if we did not do that i will take the value that saved in the xml file...
        //createAfterApplyThemeInterstitialAd();
        loadSummary();
    }

    private void loadSummary() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Preference languagePreference = findPreference(getString(R.string.language_key));
        if (languagePreference != null){
            String lang = sharedPreferences.getString(getString(R.string.language_key)  , getString(R.string.default_language));
            if (lang.equals(getString(R.string.default_language))) {
                languagePreference.setSummary(getString(R.string.auto));
            }
            else
            languagePreference.setSummary(lang);
            languagePreference.setOnPreferenceChangeListener(this);
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.language_key))) {
            // user has change the app language finish the activity :)
            requireActivity().finish();
        }
        else {
            preference.setSummary(newValue.toString());
        }
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if(preference.getKey().equals(getString(R.string.privacyPolicyKey))) {
            showPrivacyPolicy();
            return true ;
        }

        if(preference.getKey().equals(getString(R.string.termsOfUseKey))) {
            showTermsOfUse();
            return true ;
        }

        if(preference.getKey().equals(getString(R.string.feedback))) {
            sendFeedback();
            return true ;
        }

        if(preference.getKey().equals(getString(R.string.MoreApps))) {
            moreApps();
            return true ;
        }

        if(preference.getKey().equals(getString(R.string.rate_key))) {
            rateApp();
            return true ;
        }

      
        if(preference.getKey().equals(getString(R.string.soundKey))) {
            editSound();
            return true ;
        }

        if(preference.getKey().equals(getString(R.string.ApperanceKey))) {
            showPickerColorDialog();
            return true ;
        }

        if(preference.getKey().equals(getString(R.string.share_key))) {
            shareApp();
            return true ;
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void shareApp() {
        Config.shareApp(requireContext());
    }

    private void showPickerColorDialog() {
        if (colorPicker == null)
        CreatePickerDialog();
        if (colorPicker.getDialogViewLayout().getParent() != null) {
            ((ViewGroup)colorPicker.getDialogViewLayout().getParent()).removeView(colorPicker.getDialogViewLayout());
        }
        colorPicker.show();
    }

    private void CreatePickerDialog() {
        colorPicker = new ColorPicker(requireActivity());
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position,int color) {
                setThemeColor(color);
            }

            @Override
            public void onCancel(){
            }
        })
        .setRoundColorButton(true)
        .setColumns(5)
        .setDefaultColorButton(getCurrentColor())
        .addListenerButton(getString(R.string.ok), getDefaultButtonStyle(Color.BLUE) , new ColorPicker.OnButtonListener() {
            @Override
            public void onClick(View v, int position, int color) {
                colorPicker.getPositiveButton().callOnClick();
            }
        })
        .addListenerButton(getString(R.string.cancel), getDefaultButtonStyle(Color.BLACK) , new ColorPicker.OnButtonListener() {
            @Override
            public void onClick(View v, int position, int color) {
                colorPicker.getNegativeButton().callOnClick();
                ((ViewGroup)getView()).removeView(v);
            }
        })
       .addListenerButton(getString(R.string.default_btn), getDefaultButtonStyle(Color.RED) , new ColorPicker.OnButtonListener() {
            @Override
            public void onClick(View v, int position, int color) {
                   colorPicker.dismissDialog();
                  ((ViewGroup)getView()).removeView(v);
                   setDefaultColor();
            }
        })
        .disableDefaultButtons(true)
        .setTitle(getString(R.string.colorPicker))
        .setColors(R.array.PickerColorsDialog);
    }

    private int getCurrentColor() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return sharedPreferences.getInt(getString(R.string.CURRENT_COLOR_KEY) ,getResources().getColor(R.color.white));
    }

    private void setThemeColor(int color) {

        // the user has choose his color so update the theme
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (color == getResources().getColor(R.color.black)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.black_theme)).apply();
        }
        else if (color == getResources().getColor(R.color.white)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.default_theme)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_green)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_green)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_brown)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_brown)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_grey)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_grey)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_Pink)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_Pink)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_red)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_red)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_Deep_Purple)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_Deep_Purple)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_light_green)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_light_green)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_Purple)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_Purple)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_purple_200)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_purple_200)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_Teal)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_Teal)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_yellow)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_yellow)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_red_dark)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_red_dark)).apply();
        }
        else if (color == getResources().getColor(R.color.theme_orange)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.theme_orange)).apply();
        }
        sharedPreferences.edit().putInt(getString(R.string.CURRENT_COLOR_KEY) , color).apply();
        requireActivity().finish();
//        if (!Config.ifShouldDisableAds(requireContext())) {
//            ShowIntestinalAd();
//        }
//        else {
           // requireActivity().finish();
//        }

    }

    private void setDefaultColor() {
        // set the default theme of the app
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.default_theme)).apply();
        sharedPreferences.edit().putInt(getString(R.string.CURRENT_COLOR_KEY) , getResources().getColor(R.color.white)).apply();
        requireActivity().finish();
    }

    @NonNull
    private Button getDefaultButtonStyle(int Color) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        Button button = new Button(requireContext());
        button.setLayoutParams(params);
        TypedValue outValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        button.setBackgroundResource(outValue.resourceId);
        button.setTextColor(Color);
        return button;
    }

    private void editSound() {
        updateLocale(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.edit_sound_dialog , null) ;
        SeekBar seekBar = view.findViewById(R.id.seek_bar);
        seekBar.setProgress(getSavedProgress());
        ImageView close = view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveSoundChanges(seekBar.getProgress());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private int getSavedProgress() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return sharedPreferences.getInt(getString(R.string.soundKey) , 7);
    }

    private void saveSoundChanges(int finalProgress) {
        Log.i("ab_do" , "Final Progress " + finalProgress);
        Snackbar.make(getView() , getString(R.string.changeDone) ,Snackbar.LENGTH_SHORT).show();
        PreferenceManager.getDefaultSharedPreferences(requireContext())
        .edit().putInt(getString(R.string.soundKey) , finalProgress).apply();
    }


    private void rateApp() {
        startActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse("http://play.google.com/store/apps/details?id=" + requireActivity().getPackageName())));
    }

    private void moreApps() {
        startActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse("https://apps-player.com")));
    }

    private void sendFeedback() {
        // implicit intent
        Intent intent = new Intent(Intent.ACTION_SENDTO) ;
        intent.setData(Uri.parse("mailto:")) ;
        intent.putExtra(Intent.EXTRA_EMAIL , new String [] {"support@apps-player.com"}) ;
        intent.putExtra(Intent.EXTRA_SUBJECT , "My feedback for (Lite Player APP)");
        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext() , "Error" , Toast.LENGTH_LONG).show();
        }
        }


    private void showTermsOfUse() {
        startActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse("https://apps-player.com/TermsOfUse.html")));
    }

    private void showPrivacyPolicy() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://apps-player.com/PrivacyPolicy.html")));
        }catch (Exception exception) {
            Log.i("ab_do" , exception.getMessage());
        }
    }

//    private void createAfterApplyThemeInterstitialAd() {
//        if (Config.admob == null) return;
//        AdRequest adRequest = new AdRequest.Builder().build();
//        InterstitialAd.load(requireContext(),Config.admob.getInterstitial(), adRequest,
//                new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        Log.i("ab_do"  , "onAdLoadedInterstitialAd");
//                        interstitial_Ad = interstitialAd;
//                        interstitial_Ad.setFullScreenContentCallback(new FullScreenContentCallback() {
//                            @Override
//                            public void onAdDismissedFullScreenContent() {
//                                Log.i("ab_do"  , "onAdDismissedFullScreenContent");
//                                requireActivity().finish();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        interstitial_Ad = null ;
//                    }
//                });
//    }
}

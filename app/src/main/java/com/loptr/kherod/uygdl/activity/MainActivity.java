package com.loptr.kherod.uygdl.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.jiajunhui.xapp.medialoader.bean.VideoItem;
import com.loptr.kherod.uygdl.PlaylistDatabase.FavouriteItem;
import com.loptr.kherod.uygdl.PlaylistDatabase.Playlist;
import com.loptr.kherod.uygdl.PlaylistDatabase.PlaylistViewModel;
import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.activity.playlist.PlaylistItemsActivity;
import com.loptr.kherod.uygdl.activity.settings.SettingsActivity;
import com.loptr.kherod.uygdl.adapter.FoldersAdapter;
import com.loptr.kherod.uygdl.adapter.MusicAdapter;
import com.loptr.kherod.uygdl.adapter.PlaylistAdapter;
import com.loptr.kherod.uygdl.adapter.VideosAdapter;
import com.loptr.kherod.uygdl.utlities.Config;
import com.loptr.kherod.uygdl.databinding.ActivityMainBinding;
import com.loptr.kherod.uygdl.dialog.AddUrlDialog;
import com.loptr.kherod.uygdl.fragments.FolderFragment;
import com.loptr.kherod.uygdl.fragments.MusicFragment;
import com.loptr.kherod.uygdl.fragments.PlaylistFragment;
import com.loptr.kherod.uygdl.fragments.VideosFragment;
import com.loptr.kherod.uygdl.model.Admob;
import com.loptr.kherod.uygdl.model.MusicModel;
import com.loptr.kherod.uygdl.model.PlaylistItem;
import com.loptr.kherod.uygdl.model.VideoModel;
import com.loptr.kherod.uygdl.network.ApiClient;
import com.loptr.kherod.uygdl.network.ApiService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import petrov.kristiyan.colorpicker.ColorPicker;

import static com.loptr.kherod.uygdl.utlities.Config.admob;
import static com.loptr.kherod.uygdl.utlities.Config.updateLocale;

public class MainActivity extends AppCompatActivity implements FoldersAdapter.onFolderSelected, MusicAdapter.onMusicSelected, VideosAdapter.onVideoSelected, PlaylistAdapter.onPlaylistSelected, PlaylistAdapter.onPlayMenuSelected, VideosAdapter.onVideoClickCheckBox, MusicAdapter.onMusicClickCheckBox
        , VideosAdapter.onVideoMenuClicked, MusicAdapter.onMusicMenuClicked {

    private ConsentInformation consentInformation;
    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private final String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding mBinding;
    private final int PERMISSIONS_REQUEST_STORAGE = 1;
    private final CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;
    private InterstitialAd afterInterstitialAd = null;
    private Fragment currentFragment = null;
    private Menu menu;
    //private SearchView searchView;
    private MenuItem deleteAllPlaylistItem, gridOrList;
    private boolean isGrid = false;
    BottomSheetDialog videoOptionsDialog;
    BottomSheetDialog musicOptionsDialog;
    MenuItem searchViewItem;
    PlaylistViewModel playlistViewModel;
    AlertDialog dialog;
    ColorPicker colorPicker;

    //InterstitialAd goToSettingsAd = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("ab_do", "onCreate");
        Config.updateLocale(this);
        Config.updateTheme(this);
        //setTheme(R.style.test);
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        //checkAction();
        applyPermissions();
        init();
        initBottomNavigation();
        initRetrofit();
        // Set tag for under age of consent. false means users are not under age
        // of consent.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            this,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                    Log.w(TAG, String.format("%s: %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }

                                // Consent has been gathered.
                                if (consentInformation.canRequestAds()) {
                                    initAds();
                                }
                            }
                    );
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w(TAG, String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initAds();
        }
    }
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        sharedPreferences.edit().clear().apply();

//    private void checkAction() {
//        if (getIntent().getAction() !=null && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
//             if (getIntent().getData()!=null) {
//                 Log.i("ab_do", "url " + getIntent().getData().toString());
//                 startActivity(new Intent(getBaseContext(), webViewActivity.class).setAction(getIntent().getData().toString()).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
//                 finish();
//             }
//        }
//    }

    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
    }

    @Override
    protected void onStart() {
        updateLocale(this);
        super.onStart();
    }

//    private void createGoToSettingsDialog() {
//        if (admob == null) return;
//        AdRequest adRequest = new AdRequest.Builder().build();
//        InterstitialAd.load(this,admob.getInterstitial(), adRequest,
//                new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        Log.i("ab_do"  , "onAdLoadedInterstitialAd");
//                        goToSettingsAd = interstitialAd;
//                        goToSettingsAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                            @Override
//                            public void onAdDismissedFullScreenContent() {
//                                Log.i("ab_do"  , "onAdDismissedFullScreenContent");
//                                goToSettingsActivity();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        goToSettingsAd = null ;
//                    }
//                });
//    }

    private void initAds() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }
        if (!Config.ifShouldDisableAds(this)) {
            // if not should disable ad :
            Log.i("ab_do", "Display ads");
            if (admob == null)
                getAdmobData();
            else {
                initMobileAds();
            }
        } else {
            Log.i("ab_do", "Not Display ads");
        }
    }

    private void initMobileAds() {
        MobileAds.initialize(MainActivity.this, initializationStatus -> {
            createBannerAd();  // create top banner
            //createGoToSettingsDialog();
        });
    }


    private void applyPermissions() {
        Log.i("ab_do", "applyPermissions");
        if (IfPermissionsGranted()) {
            Log.i("ab_do", "Permission yeees");
            initDefaultFragment();
        } else {
            Log.i("ab_do", "Permission no");
            requestPermission();
        }
    }

    private void init() {
        setSupportActionBar(mBinding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBinding.toolbarTxt.setText(getString(R.string.videos_title));
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
        initSwipeRefresher();
    }

    private void initDefaultFragment() {
        mBinding.bottomNavigation.show(1, true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        //showSearchView(View.VISIBLE);
        mBinding.toolbarTxt.setText(getString(R.string.videos_title));
        initFragment(FolderFragment.getFolderFragment(mBinding.swipeRefresh));
    }

    private void initSwipeRefresher() {
        mBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
    }

    private void doRefresh() {
        mBinding.swipeRefresh.setRefreshing(true);
        if (currentFragment instanceof FolderFragment) {
            ((FolderFragment) currentFragment).doRefresh();
        } else if (currentFragment instanceof MusicFragment) {
            ((MusicFragment) currentFragment).doRefresh();
        } else if (currentFragment instanceof PlaylistFragment) {
            ((PlaylistFragment) currentFragment).doRefresh();
        } else if (currentFragment instanceof VideosFragment) {
            ((VideosFragment) currentFragment).doRefresh();
        }
    }

    private void editSound() {
        updateLocale(this);
        View view = LayoutInflater.from(this).inflate(R.layout.edit_sound_dialog, null);
        SeekBar seekBar = view.findViewById(R.id.seek_bar);
        seekBar.setProgress(getSavedProgress());
        ImageView close = view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt(getString(R.string.soundKey), 7);
    }

    private void saveSoundChanges(int finalProgress) {
        Log.i("ab_do", "Final Progress " + finalProgress);
        Snackbar.make(mBinding.getRoot(), getString(R.string.changeDone), Snackbar.LENGTH_SHORT).show();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putInt(getString(R.string.soundKey), finalProgress).apply();
    }

    private void initBottomNavigation() {
        mBinding.bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_video_library_24));
        mBinding.bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_music_player));
        mBinding.bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_playlist));
        mBinding.bottomNavigation.show(1, true);
        //initFragment(new videoFragment());
        mBinding.bottomNavigation.setOnClickMenuListener(model -> {
            if (!IfPermissionsGranted()) {
                requestPermission();
                return null;
            }
            Config.updateLocale(this);
            switch (model.getId()) {
                case 1:
                    initDefaultFragment();
                    break;

                case 2:
                    initFragment(MusicFragment.getMusicFragment(mBinding.swipeRefresh));
                    mBinding.toolbarTxt.setText(getString(R.string.music));
                    //showSearchView(View.VISIBLE);
                    break;

                case 3:
                    initFragment(PlaylistFragment.getPlaylistFragment(mBinding.swipeRefresh));
                    mBinding.toolbarTxt.setText(getString(R.string.playlist));
                    //showSearchView(View.VISIBLE);
                    break;
            }
            return null;
        });
    }

//    private void showSearchView(int visible) {
//        if (searchView != null)
//            searchView.setVisibility(visible);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Config.updateLocale(this);
        if (menu != null) {
            menu.close();
            menu.clear();
        }
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        this.deleteAllPlaylistItem = menu.findItem(R.id.deleteAllPlaylist);
        this.gridOrList = menu.findItem(R.id.gridOrList);
        deleteAllPlaylistItem.setVisible(false);
        gridOrList.setVisible(false);
        //searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchViewItem = menu.findItem(R.id.search);
        //prepareSearchView();
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            //noinspection RestrictedApi
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

//    private void prepareSearchView() {
//        searchView.setSubmitButtonEnabled(true);
//        searchView.setQueryHint(getString(R.string.search_hint));
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if (getCurrentFilter() == null) return false ;
//                getCurrentFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (getCurrentFilter() == null) return false ;
//                getCurrentFilter().filter(newText);
//                return false;
//            }
//        });
//    }

    private Filter getCurrentFilter() {
        if (currentFragment instanceof FolderFragment) {
            return ((FolderFragment) currentFragment).adapter.getFilter();
        } else if (currentFragment instanceof MusicFragment) {
            return ((MusicFragment) currentFragment).adapter.getFilter();
        } else if (currentFragment instanceof PlaylistFragment) {
            return ((PlaylistFragment) currentFragment).adapter.getFilter();
        } else if (currentFragment instanceof VideosFragment) {
            return ((VideosFragment) currentFragment).adapter.getFilter();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onCreateOptionsMenu(menu);
            initDefaultFragment();
            return true;
        }
//        if (itemId == R.id.refresh) {
//            mBinding.swipeRefresh.setRefreshing(true);
//            doRefresh();
//            return true;
//        }
        else if (itemId == R.id.url) {
            addUrl();
            return true;
        } else if (itemId == R.id.sound_edit) {
            editSound();
            return true;
        }


//        else if (itemId == R.id.polish) {
//            showPickerColorDialog();
//            return true;
//        }

        else if (itemId == R.id.settings) {
            goToSettingsActivity();
            return true;
        } else if (itemId == R.id.deleteAllPlaylist) {
            if (currentFragment instanceof PlaylistFragment) {
                ((PlaylistFragment) currentFragment).deleteAllPlaylists();
            }
        } else if (itemId == R.id.gridOrList) {
            switchRecycleViewLayout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchRecycleViewLayout() {
        if (currentFragment instanceof VideosFragment) {
            if (isGrid) {
                gridOrList.setIcon(R.drawable.ic_baseline_grid_on_24);
                searchViewItem.setVisible(true);
            } else {
                gridOrList.setIcon(R.drawable.ic_baseline_format_list_bulleted_24);
                searchViewItem.setVisible(false);
                Log.i("ab_doo", "Gone");
            }
            ((VideosFragment) currentFragment).initRecyclerview(!isGrid);
            isGrid = !isGrid;
        }
    }

    private void goToSettingsActivity() {
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
        finish();
    }

    private void initFragment(Fragment fragment) {
        setMenuItemsVisibility(fragment);
        currentFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mBinding.fragmentContainer.getId(), fragment);
        transaction.commit();
    }

    private void setMenuItemsVisibility(Fragment fragment) {
        if (deleteAllPlaylistItem != null) {
            deleteAllPlaylistItem.setVisible(fragment instanceof PlaylistFragment);
        }

        if (gridOrList != null) {
            gridOrList.setVisible(fragment instanceof VideosFragment);
        }

    }

    private void showPickerColorDialog() {
        if (colorPicker == null)
            CreatePickerDialog();
        if (colorPicker.getDialogViewLayout().getParent() != null) {
            ((ViewGroup) colorPicker.getDialogViewLayout().getParent()).removeView(colorPicker.getDialogViewLayout());
        }
        colorPicker.show();
    }

    private void CreatePickerDialog() {
        colorPicker = new ColorPicker(this);
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        setThemeColor(color);
                    }

                    @Override
                    public void onCancel() {
                    }
                })
                .setRoundColorButton(true)
                .setColumns(5)
                .setDefaultColorButton(getCurrentColor())
                .addListenerButton(getString(R.string.ok), getDefaultButtonStyle(Color.BLUE), new ColorPicker.OnButtonListener() {
                    @Override
                    public void onClick(View v, int position, int color) {
                        colorPicker.getPositiveButton().callOnClick();
                    }
                })
                .addListenerButton(getString(R.string.cancel), getDefaultButtonStyle(Color.BLACK), new ColorPicker.OnButtonListener() {
                    @Override
                    public void onClick(View v, int position, int color) {
                        colorPicker.getNegativeButton().callOnClick();
                        ((ViewGroup) mBinding.getRoot()).removeView(v);
                    }
                })
                .addListenerButton(getString(R.string.default_btn), getDefaultButtonStyle(Color.RED), new ColorPicker.OnButtonListener() {
                    @Override
                    public void onClick(View v, int position, int color) {
                        colorPicker.dismissDialog();
                        ((ViewGroup) mBinding.getRoot()).removeView(v);
                        setDefaultColor();
                    }
                })
                .disableDefaultButtons(true)
                .setTitle(getString(R.string.colorPicker))
                .setColors(R.array.PickerColorsDialog);
    }

    private int getCurrentColor() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt(getString(R.string.CURRENT_COLOR_KEY), getResources().getColor(R.color.white));
    }

    private void setThemeColor(int color) {

        // the user has choose his color so update the theme
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (color == getResources().getColor(R.color.black)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.black_theme)).apply();
        } else if (color == getResources().getColor(R.color.white)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.default_theme)).apply();
        } else if (color == getResources().getColor(R.color.theme_green)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_green)).apply();
        } else if (color == getResources().getColor(R.color.theme_brown)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_brown)).apply();
        } else if (color == getResources().getColor(R.color.theme_grey)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_grey)).apply();
        } else if (color == getResources().getColor(R.color.theme_Pink)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_Pink)).apply();
        } else if (color == getResources().getColor(R.color.theme_red)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_red)).apply();
        } else if (color == getResources().getColor(R.color.theme_Deep_Purple)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_Deep_Purple)).apply();
        } else if (color == getResources().getColor(R.color.theme_light_green)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_light_green)).apply();
        } else if (color == getResources().getColor(R.color.theme_Purple)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_Purple)).apply();
        } else if (color == getResources().getColor(R.color.theme_purple_200)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_purple_200)).apply();
        } else if (color == getResources().getColor(R.color.theme_Teal)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_Teal)).apply();
        } else if (color == getResources().getColor(R.color.theme_yellow)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_yellow)).apply();
        } else if (color == getResources().getColor(R.color.theme_red_dark)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_red_dark)).apply();
        } else if (color == getResources().getColor(R.color.theme_orange)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_orange)).apply();
        }
        sharedPreferences.edit().putInt(getString(R.string.CURRENT_COLOR_KEY), color).apply();
        recreate();
//        if (!Config.ifShouldDisableAds(requireContext())) {
//            ShowIntestinalAd();
//        }
//        else {
        // requireActivity().finish();
//        }

    }

    private void setDefaultColor() {
        // set the default theme of the app
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.default_theme)).apply();
        sharedPreferences.edit().putInt(getString(R.string.CURRENT_COLOR_KEY), getResources().getColor(R.color.white)).apply();
        recreate();
    }

    @NonNull
    private Button getDefaultButtonStyle(int Color) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Button button = new Button(this);
        button.setLayoutParams(params);
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        button.setBackgroundResource(outValue.resourceId);
        button.setTextColor(Color);
        return button;
    }


    public void getAdmobData() {
        Log.i("ab_do", "getAdmobData");
        disposable.add(
                apiService
                        .getAdmob()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Admob>() {
                            @Override
                            protected void onStart() {
                                super.onStart();
                                Log.i("ab_do", "onStart adMob");
                            }

                            @Override
                            public void onSuccess(Admob admob) {
                                Log.i("ab_do", "onSuccess adMob");
                                if (Config.admob == null) {
                                    Config.admob = admob;
                                    try {
                                        ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                                        Bundle bundle = ai.metaData;
                                        String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                        Log.d(TAG, "Name Found: " + myApiKey);
                                        ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", admob.getApp_id());//you can replace your key APPLICATION_ID here
                                        String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                        Log.d(TAG, "ReNamed Found: " + ApiKey);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                                    } catch (NullPointerException e) {
                                        Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                                    }

                                    initMobileAds();

                                    //Test Mediation
                                    MediationTestSuite.launch(MainActivity.this);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do", "error admob " + e.getMessage());
                            }
                        })
        );
    }

    private void initRetrofit() {
        apiService = ApiClient.getClient(this).create(ApiService.class);
    }

    private void createBannerAd() {
        if (admob == null) return;
        AdManagerAdView adView = new AdManagerAdView(this);
        adView.setAdSizes(getAdSize());
        adView.setAdUnitId(admob.getBanner());
        adView.setAdListener(new AdListener() {
                                 @Override
                                 public void onAdLoaded() {
                                     Log.i("ab_do", "onAdLoaded");
                                     if (adView.getParent() != null) {
                                         ((ViewGroup) adView.getParent()).removeView(adView);
                                     }
                                     mBinding.bannerTopContainer.addView(adView);
                                     mBinding.bannerTopContainer.setVisibility(View.VISIBLE);
                                 }

                                 @Override
                                 public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                     super.onAdFailedToLoad(loadAdError);
                                     mBinding.bannerTopContainer.setVisibility(View.GONE);
                                 }
                             }

        );
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    public void addUrl() {
        AddUrlDialog dialog = new AddUrlDialog(this);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);
        dialog.show();
    }

    public void openUrl(String url) {
        Intent intent = new Intent(this, ExoplayerActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
        //mBinding.progressBarLayout.setVisibility(View.GONE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_STORAGE) {
            if (grantResults != null && grantResults.length != 0)
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    getSnackbar().show();
                } else {
                    try {
                        initDefaultFragment();
                    } catch (Exception exception) {
                        finishAffinity();
                    }
                }
        }
    }

    @NotNull
    private Snackbar getSnackbar() {
        Snackbar snackbar = Snackbar.make(mBinding.bottomNavigation, R.string.give_permission, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.lightBlue));
        snackbar.setAction(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        return snackbar;
    }


    public void share() {
        Config.shareApp(this);
    }

    public boolean IfPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED);
        } else
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ArrayList<String> perms = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // still need the write storage perms before android 10
            perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.READ_MEDIA_VIDEO);
            perms.add(Manifest.permission.READ_MEDIA_IMAGES);
            perms.add(Manifest.permission.READ_MEDIA_AUDIO);
        } else {
            perms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        String[] perms_arr = new String[perms.size()];
        perms_arr = perms.toArray(perms_arr);
        ActivityCompat.requestPermissions(this,
                perms_arr, PERMISSIONS_REQUEST_STORAGE);
    }

    private void createInterstitialAd2() {

        if (admob != null && admob.getInterstitial2() != null) {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(this, admob.getInterstitial2(), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            afterInterstitialAd = interstitialAd;
                            afterInterstitialAd.show(MainActivity.this);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        createInterstitialAd2();
    }

    @Override
    public void onSelectFolder(List<VideoItem> videoItems) {
        mBinding.toolbarTxt.setText(getString(R.string.videos));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initFragment(new VideosFragment(this, videoItems, mBinding.swipeRefresh));
    }

    @Override
    public void onSelectMusic(String url) {
        openUrl(url);
    }

    @Override
    public void onSelectedVideo(String url) {
        openUrl(url);
    }

//    public void ShowDialog(Context context) {
//        //setting up progress dialog
//        progressDialog = new ProgressDialog(context);
//        progressDialog.show();
//        progressDialog.setContentView(R.layout.progress_dialog);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                finishLoadingImages();
//            }
//        });
//    }


    @Override
    public void onBackPressed() {
        if (currentFragment instanceof VideosFragment) {
            onCreateOptionsMenu(menu);
            initDefaultFragment();
        } else
            super.onBackPressed();
    }

    @Override
    public void onSelectMenu(Playlist playlist) {
        ((PlaylistFragment) currentFragment).selectedPlaylist = playlist;
        ((PlaylistFragment) currentFragment).showEditPlaylistBottomSheetDialog();
    }

    @Override
    public void onSelectPlaylist(Playlist playlist) {
        Intent intent = new Intent(getBaseContext(), PlaylistItemsActivity.class);
        intent.putExtra(Config.PLAYLIST_KEY, playlist);
        startActivity(intent);
    }

    @Override
    public void onCheckBoxClicked(MusicModel musicModel, boolean isChecked) {

    }

    @Override
    public void onCheckBoxClicked(VideoModel videoModel, boolean isChecked) {

    }

    @Override
    public void onClickVideoMenu(VideoModel videoModel) {
        videoOptionsDialog = createBottomSheetDialog();
        View view = getDialogView();
        videoOptionsDialog.setContentView(view);
        TextView fileName = view.findViewById(R.id.MediaFileName);
        TextView play = view.findViewById(R.id.playMediaFile);
        TextView share = view.findViewById(R.id.shareMediaFile);
        TextView addToFavourite = view.findViewById(R.id.AddToFavourite);
        fileName.setText(String.format(getString(R.string.mediaTxt), videoModel.getVideoItem().getDisplayName()));
        if (videoModel.isFavourite()) {
            addToFavourite.setText(getString(R.string.remove_from_favourite));
            addToFavourite.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_like), null, null, null);
        }
        initVideoActionsListeners(play, share, addToFavourite, videoModel);
        videoOptionsDialog.show();
    }

    private void initVideoActionsListeners(TextView play, TextView share, TextView favourite, VideoModel videoModel) {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(videoModel.getVideoItem().getPath());
                videoOptionsDialog.dismiss();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareMedia(Uri.parse(videoModel.getVideoItem().getPath()));
                videoOptionsDialog.dismiss();
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideoToFavouritePlaylist(videoModel);
            }
        });
    }

    private void addVideoToFavouritePlaylist(VideoModel videoModel) {
        if (videoModel.isFavourite()) {
            removeVideoFromFavouritePlaylist(videoModel);
            Snackbar.make(mBinding.bottomNavigation, getString(R.string.removed_succes), Snackbar.LENGTH_SHORT).show();
        } else {
            // add :
            Playlist favouritePlaylist = getFavouritePlaylist();
            PlaylistItem playlistItem = new PlaylistItem((int) favouritePlaylist.getId(), videoModel.getVideoItem().getDisplayName(), Config.formatTime(videoModel.getVideoItem().getDuration()), videoModel.getVideoItem().getPath(), false);
            ArrayList<PlaylistItem> playlistItems = favouritePlaylist.getPlaylistItems();
            playlistItems.add(playlistItem);
            favouritePlaylist.setPlaylistItems(playlistItems);
            playlistViewModel.updatePlaylist(favouritePlaylist);
            Snackbar.make(mBinding.bottomNavigation, getString(R.string.added_succes), Snackbar.LENGTH_SHORT).show();
            playlistViewModel.insertFavouriteItem(new FavouriteItem(videoModel.getVideoItem().getPath(), videoModel.getVideoItem().getDisplayName()));
            videoModel.setFavourite(true);
        }
        videoOptionsDialog.dismiss();
    }

    private void initMusicActionsListeners(TextView play, TextView share, TextView favourite, MusicModel musicModel) {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(musicModel.getUrl());
                musicOptionsDialog.dismiss();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareMedia(Uri.parse(musicModel.getUrl()));
                musicOptionsDialog.dismiss();
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMusicToFavouritePlaylist(musicModel);
            }
        });
    }

    private void addMusicToFavouritePlaylist(MusicModel musicModel) {
        if (musicModel.isFavourite()) {
            removeMusicFromFavouritePlaylist(musicModel);
            Snackbar.make(mBinding.bottomNavigation, getString(R.string.removed_succes), Snackbar.LENGTH_SHORT).show();
        } else {
            // add :
            Playlist favouritePlaylist = getFavouritePlaylist();
            PlaylistItem playlistItem = new PlaylistItem((int) favouritePlaylist.getId(), musicModel.getTitle(), musicModel.getArtist(), musicModel.getUrl(), true);
            ArrayList<PlaylistItem> playlistItems = favouritePlaylist.getPlaylistItems();
            playlistItems.add(playlistItem);
            favouritePlaylist.setPlaylistItems(playlistItems);
            playlistViewModel.updatePlaylist(favouritePlaylist);
            playlistViewModel.insertFavouriteItem(new FavouriteItem(musicModel.getUrl(), musicModel.getTitle()));
            Snackbar.make(mBinding.bottomNavigation, getString(R.string.added_succes), Snackbar.LENGTH_SHORT).show();
            musicModel.setFavourite(true);
        }
        musicOptionsDialog.dismiss();
    }

    private View getDialogView() {
        return LayoutInflater.from(this).inflate(R.layout.media_actions, (ViewGroup) mBinding.getRoot(), false);
    }

    @Override
    public void onClickMusicMenu(MusicModel musicModel) {
        musicOptionsDialog = createBottomSheetDialog();
        View view = getDialogView();
        musicOptionsDialog.setContentView(view);
        TextView fileName = view.findViewById(R.id.MediaFileName);
        TextView play = view.findViewById(R.id.playMediaFile);
        TextView share = view.findViewById(R.id.shareMediaFile);
        TextView addToFavourite = view.findViewById(R.id.AddToFavourite);
        fileName.setText(String.format(getString(R.string.mediaTxt), musicModel.getTitle()));
        if (musicModel.isFavourite()) {
            addToFavourite.setText(getString(R.string.remove_from_favourite));
            addToFavourite.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_like), null, null, null);
        }
        initMusicActionsListeners(play, share, addToFavourite, musicModel);
        musicOptionsDialog.show();
    }

    private BottomSheetDialog createBottomSheetDialog() {
        return new BottomSheetDialog(this, R.style.bottomSheetDialogTheme);
    }


    private void ShareMedia(Uri uri) {
        new ShareCompat.IntentBuilder(this)
                .setStream(uri)
                .setType("*/*")
                .startChooser();
    }

    private Playlist getFavouritePlaylist() {
        return playlistViewModel.getFavouritePlaylist();
    }

    private void removeMusicFromFavouritePlaylist(MusicModel musicModel) {
        musicModel.setFavourite(false);
        removeMusicFromFavourite(musicModel);
        removeMusic(musicModel);
    }

    private void removeMusicFromFavourite(MusicModel musicModel) {
        List<FavouriteItem> favouriteItems = playlistViewModel.getAllFavouriteItems();
        Iterator<FavouriteItem> itr = favouriteItems.iterator();
        while (itr.hasNext()) {
            FavouriteItem item = itr.next();
            if (item.getPath().equals(musicModel.getUrl()) && item.getName().equals(musicModel.getTitle())) {
                itr.remove();
                break;
            }
        }
        playlistViewModel.deleteAllFavouriteItems();
        playlistViewModel.insertAllFavouriteItems(favouriteItems);
    }

    private void removeVideoFromFavourite(VideoModel videoModel) {
        List<FavouriteItem> favouriteItems = playlistViewModel.getAllFavouriteItems();
        Iterator<FavouriteItem> itr = favouriteItems.iterator();
        while (itr.hasNext()) {
            FavouriteItem item = itr.next();
            if (item.getPath().equals(videoModel.getVideoItem().getPath()) && item.getName().equals(videoModel.getVideoItem().getDisplayName())) {
                itr.remove();
                break;
            }
        }
        playlistViewModel.deleteAllFavouriteItems();
        playlistViewModel.insertAllFavouriteItems(favouriteItems);
    }

    private void removeVideoFromFavouritePlaylist(VideoModel videoModel) {
        videoModel.setFavourite(false);
        removeVideoFromFavourite(videoModel);
        removeVideo(videoModel);
    }

    private void removeVideo(VideoModel videoModel) {
        Playlist playlist = getFavouritePlaylist();
        ArrayList<PlaylistItem> playlistItems = playlist.getPlaylistItems();
        Iterator<PlaylistItem> itr = playlistItems.iterator();
        while (itr.hasNext()) {
            PlaylistItem item = itr.next();
            if (!item.isMusic()) {
                if (item.getPath().equals(videoModel.getVideoItem().getPath())
                        && item.getName().equals(videoModel.getVideoItem().getDisplayName())) {
                    itr.remove();
                    break;
                }
            }
        }
        playlist.setPlaylistItems(playlistItems);
        playlistViewModel.updatePlaylist(playlist);
    }

    private void removeMusic(MusicModel musicModel) {
        Playlist playlist = getFavouritePlaylist();
        ArrayList<PlaylistItem> playlistItems = playlist.getPlaylistItems();
        Iterator<PlaylistItem> itr = playlistItems.iterator();
        while (itr.hasNext()) {
            PlaylistItem item = itr.next();
            if (item.isMusic()) {
                if (item.getPath().equals(musicModel.getUrl())
                        && item.getName().equals(musicModel.getTitle())) {
                    itr.remove();
                    break;
                }
            }
        }
        playlist.setPlaylistItems(playlistItems);
        playlistViewModel.updatePlaylist(playlist);
    }

    @Override
    protected void onStop() {
        destroyFragmentInstances();
        Log.i("ab_do", "onDestroy");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //destroyFragmentInstances();
        Log.i("ab_do", "onDestroy");
        super.onDestroy();
    }

    private void destroyFragmentInstances() {
        Log.i("ab_do", "destroyFragmentInstances");
        FolderFragment.folderFragment = null;
        MusicFragment.musicFragment = null;
        PlaylistFragment.playlistFragment = null;
    }
}
package com.loptr.kherod.uygdl.activity.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.loptr.kherod.uygdl.activity.MainActivity;
import com.loptr.kherod.uygdl.utlities.Config;
import com.loptr.kherod.uygdl.databinding.ActivitySettingsBinding;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Config.updateTheme(this);
        Config.updateLocale(this);
        updateLayoutDirection();
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void updateLayoutDirection() {
        //
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true ;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        boolean isLeftToRight = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_LTR ;
        if (!isLeftToRight) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        startActivity(new Intent(this , MainActivity.class));
        super.finish();
    }
}
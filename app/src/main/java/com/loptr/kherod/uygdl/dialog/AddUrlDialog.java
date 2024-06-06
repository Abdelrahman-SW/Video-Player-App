package com.loptr.kherod.uygdl.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.loptr.kherod.uygdl.R;
import com.loptr.kherod.uygdl.activity.MainActivity;
import com.loptr.kherod.uygdl.databinding.DialogAddUrlBinding;

public class AddUrlDialog extends Dialog {

    DialogAddUrlBinding mBinding;
    Context context;

    public AddUrlDialog(@NonNull Context context) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_add_url, null, false);
        setContentView(mBinding.getRoot());

        this.context = context;
        initListeners();
    }

    private void initListeners(){
        mBinding.btnOpen.setOnClickListener(v -> {
            if(mBinding.etUrl.getText().toString().isEmpty()){
                mBinding.etUrl.setError(context.getString(R.string.required));
            }else{
                ((MainActivity)context).openUrl(mBinding.etUrl.getText().toString());
                AddUrlDialog.this.dismiss();
            }
        });
    }

}

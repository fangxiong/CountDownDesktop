package com.fax.showdt.utils;

import android.content.Context;

import com.fax.showdt.R;

import es.dmoral.toasty.Toasty;

public class ToastShowUtils {

    public static void showCommonToast(Context context,String message,int duration){
        Toasty.custom(context,message,null, context.getResources().getColor(R.color.c_FCF43C),context.getResources().getColor(R.color.c_222222),duration,false,true).show();
    }
}

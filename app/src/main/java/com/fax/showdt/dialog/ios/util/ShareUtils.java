package com.fax.showdt.dialog.ios.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import java.io.File;
import androidx.core.content.FileProvider;

public class ShareUtils {

    public static void shareText(Context context,String title,String content){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        shareIntent = Intent.createChooser(shareIntent, title);
        context.startActivity(shareIntent);
    }

    public static boolean shareImg(Context context,String title,String picFilePath){
        File shareFile = new File(picFilePath);
        if (!shareFile.exists())
            return false;
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", shareFile);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
        }
        intent.setType("image/*");
        Intent chooser = Intent.createChooser(intent,title);
        if(intent.resolveActivity(context.getPackageManager()) != null){
            context.startActivity(chooser);
        }
        return true;
    }

}

package com.fax.showdt.db;


import android.content.Context;

import com.fax.showdt.bean.CustomWidgetConfig;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-9-24
 * Description:widget数据库
 */
@Database(entities = {CustomWidgetConfig.class}, version = 1,exportSchema = false)
public abstract class WidgetDatabase extends RoomDatabase {

    private static WidgetDatabase INSTANCE;
    private static final Object SLOCK = new Object();
    public abstract WidgetDao mWidgetDao();

    public static WidgetDatabase getInstance(Context context){
        synchronized (SLOCK){
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WidgetDatabase.class,"widget.db")
                        .build();
            }
            return INSTANCE;
        }
    }

}

package com.fax.cddt.manager.widget;

import android.content.Context;

import com.fax.cddt.AppContext;
import com.fax.cddt.bean.CustomWidgetConfig;
import com.fax.cddt.db.WidgetDao;
import com.fax.cddt.db.WidgetDatabase;

import java.util.List;

import io.reactivex.Single;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-9-24
 * Description:封装对桌面widget的数据库操作
 */
public class CustomWidgetConfigDao {

    public static CustomWidgetConfigDao mInstance;
    private WidgetDao mWidgetDao;

    private CustomWidgetConfigDao(Context context) {
        mWidgetDao = WidgetDatabase.getInstance(context).mWidgetDao();
    }

    public static CustomWidgetConfigDao getInstance(Context context) {
        if (mInstance == null) {
            synchronized (CustomWidgetConfigDao.class) {
                if (mInstance == null) {
                    mInstance = new CustomWidgetConfigDao(context);
                }
            }
        }
        return mInstance;
    }

    public Single<List<CustomWidgetConfig>> getAllConfig() {
        return mWidgetDao.getAll();
    }

    public synchronized void insert(CustomWidgetConfig config) {
        mWidgetDao.insertAll(config);
    }

    public synchronized void insertAll(List<CustomWidgetConfig> list) {
        mWidgetDao.insertList(list);
    }

    public synchronized void delete(List<CustomWidgetConfig> list) {
        mWidgetDao.delete(list);
    }

    public synchronized void update(CustomWidgetConfig config) {
        mWidgetDao.update(config);
    }

    public Single<CustomWidgetConfig> findByThemeId(long id) {
        return mWidgetDao.loadById(id);
    }
}

package com.fax.showdt.db;


import com.fax.showdt.bean.CustomWidgetConfig;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Single;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-9-24
 * Description:封装对桌面widget的增删改查
 */
@Dao
public interface WidgetDao {

    @Query("SELECT * FROM CustomWidgetConfig")
    Single<List<CustomWidgetConfig>> getAll();

    @Query("SELECT * FROM CustomWidgetConfig where id IN (:tid)")
    Single<CustomWidgetConfig> loadById(long tid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CustomWidgetConfig... configs);

    @Insert
    void insertList(List<CustomWidgetConfig> list);

    @Delete
    void delete(List<CustomWidgetConfig> list);

    @Update
    void update(CustomWidgetConfig config);

}
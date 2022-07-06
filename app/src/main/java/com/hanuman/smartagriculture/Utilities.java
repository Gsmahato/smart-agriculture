package com.hanuman.smartagriculture;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Binder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utilities extends Binder {
    public Context context;

    public static void appBarColor(ActionBar actionBar, Context context){
        //action bar color
        ColorDrawable colorDrawable
                = new ColorDrawable(ContextCompat.getColor(context, R.color.splashColor ));
        actionBar.setBackgroundDrawable(colorDrawable); //action bar ends
    }
    public static String formatDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String dayDate = cal.get(Calendar.DAY_OF_MONTH)+"";
        return dayDate;
    }
    public static String formatMonth(Date date){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("MMM");
        return simpleDateFormat.format(date);
    }
    public static String formatDay(Date date){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("EEE");
        return simpleDateFormat.format(date);
    }
}

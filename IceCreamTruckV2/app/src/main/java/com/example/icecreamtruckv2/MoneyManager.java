package com.example.icecreamtruckv2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class MoneyManager extends AppWidgetProvider {
    private static final double DEFAULT = 3.50;
    private static double breakfast_cost = DEFAULT;
    private static double lunch_cost = DEFAULT;
    private static double dinner_cost = DEFAULT;

    private static double other_cost = 0.00;

    private static final String AddBreakfastClick = "abTag";
    private static final String AddLunchClick = "alTag";
    private static final String AddDinnerClick = "adTag";
    private static final String MinusBreakfastClick = "mbTag";
    private static final String MinusLunchClick = "mlTag";
    private static final String MinusDinnerClick = "mdTag";

    private static final String AddOthersClick = "aoTag";
    private static final String MinusOthersClick = "moTag";

    static private RemoteViews views;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

        views = new RemoteViews(context.getPackageName(), R.layout.money_manager);
        views.setTextViewText(R.id.breakfast_cost, currency.format(breakfast_cost));
        views.setTextViewText(R.id.lunch_cost, currency.format(lunch_cost));
        views.setTextViewText(R.id.dinner_cost, currency.format(dinner_cost));
        views.setTextViewText(R.id.other_cost, currency.format(other_cost));

        views.setOnClickPendingIntent(R.id.add_breakfast, getPendingSelfIntent(context,appWidgetId, AddBreakfastClick));
        views.setOnClickPendingIntent(R.id.add_lunch, getPendingSelfIntent(context,appWidgetId, AddLunchClick));
        views.setOnClickPendingIntent(R.id.add_dinner, getPendingSelfIntent(context,appWidgetId, AddDinnerClick));
        views.setOnClickPendingIntent(R.id.minus_breakfast, getPendingSelfIntent(context,appWidgetId, MinusBreakfastClick));
        views.setOnClickPendingIntent(R.id.minus_lunch, getPendingSelfIntent(context,appWidgetId, MinusLunchClick));
        views.setOnClickPendingIntent(R.id.minus_dinner, getPendingSelfIntent(context,appWidgetId, MinusDinnerClick));

        views.setOnClickPendingIntent(R.id.add_others, getPendingSelfIntent(context,appWidgetId, AddOthersClick));
        views.setOnClickPendingIntent(R.id.minus_others, getPendingSelfIntent(context,appWidgetId, MinusOthersClick));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected PendingIntent getPendingSelfIntent(Context context,int appWidgetId, String action) {
        Intent intent = new Intent(context, MoneyManager.class);
        intent.setAction(action);
        int[] idArray = new int[]{appWidgetId};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        views = new RemoteViews(context.getPackageName(), R.layout.money_manager);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MoneyManager.class));

        if (AddBreakfastClick.equals(intent.getAction())){
            breakfast_cost+=0.05;
        }
        else if (AddLunchClick.equals(intent.getAction())){
            lunch_cost+=0.05;
        }
        else if (AddDinnerClick.equals(intent.getAction())){
            dinner_cost+=0.05;
        }
        else if (MinusBreakfastClick.equals(intent.getAction())){
            breakfast_cost-=0.05;
        }
        else if (MinusLunchClick.equals(intent.getAction())){
            lunch_cost-=0.05;
        }
        else if (MinusDinnerClick.equals(intent.getAction())){
            dinner_cost-=0.05;
        }
        else if (AddOthersClick.equals(intent.getAction())){
            other_cost+=0.05;
        }
        else if (MinusOthersClick.equals(intent.getAction())){
            other_cost-=0.05;
        }
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}


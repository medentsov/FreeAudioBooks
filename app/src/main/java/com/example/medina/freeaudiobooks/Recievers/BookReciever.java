package com.example.medina.freeaudiobooks.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.medina.freeaudiobooks.BookActivity;

/**
 * simple broadcast receiver which get the data from
 * SearchBooksActivity and starts BookActivity
 */
public class BookReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClassName(context.getPackageName(), BookActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

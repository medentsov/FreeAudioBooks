package com.example.medina.freeaudiobooks;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.medina.freeaudiobooks.Services.PlayerService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class PlayServiceTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }


    @Test
    public void checkOnStartCommand() {
        final PlayerService playerService = new PlayerService();
        context.startService(new Intent(context, PlayServiceTest.class));
        assertEquals(Service.START_STICKY, playerService.onStartCommand(new Intent(), 0, 0));
    }

    @Test
    public void checkOnCreate() throws Exception {
        final PlayerService playerService= new PlayerService();
        context.startService(new Intent(context, PlayServiceTest.class));
        playerService.onCreate();
        assertTrue(PlayerService.isServiceStarted());
    }

    @Test
    public void checkOnDestroy() throws Exception {
        final PlayerService playerService= new PlayerService();
        context.startService(new Intent(context, PlayServiceTest.class));

        playerService.onDestroy();
        assertFalse(PlayerService.isServiceStarted());
    }
}

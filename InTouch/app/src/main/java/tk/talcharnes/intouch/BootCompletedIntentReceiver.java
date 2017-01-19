package tk.talcharnes.intouch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Tal on 1/19/2017.
 * Credit to: http://www.jjoe64.com/2011/06/autostart-service-on-device-boot.html
 */


public class BootCompletedIntentReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, FService.class);
            pushIntent.setAction("ACTION_RESTARTED");
            context.startService(pushIntent);
        }
    }
}

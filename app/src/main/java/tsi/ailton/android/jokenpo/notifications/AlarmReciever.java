package tsi.ailton.android.jokenpo.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import tsi.ailton.android.jokenpo.R;

public class AlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_ranking, null);
        PersonalNotification.createNotification("Alteração no Ranking", "Uma nova pontuação foi cadastrada no ranking.", view);
    }
}

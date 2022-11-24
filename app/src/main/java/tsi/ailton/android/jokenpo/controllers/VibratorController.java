package tsi.ailton.android.jokenpo.controllers;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibratorController {
    public static final long[] MORTAL_KOMBAT_THEME = new long[]{100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 50, 50, 100, 800};
    public static final long[] OVERTURE_1928_INTRO = new long[]{75, 25, 75, 25, 75, 25, 75, 525, 75, 25, 75, 25, 75, 25, 75, 25, 75, 25, 75, 25, 75, 225, 75, 25, 75, 25, 75, 25, 75, 225, 75, 25, 75, 25, 75, 25, 75, 525, 75, 25, 75, 25, 75, 25, 75, 25, 75, 25, 75, 25, 75, 225, 75, 25, 75, 25, 75, 25, 75, 225};
    public static final long[] NOCTURNE = new long[]{660, 60, 180, 60, 60, 180, 60, 180, 60, 180, 420, 60, 180, 60, 60, 180, 60, 180, 60, 180, 420, 60, 180, 60, 60, 180, 60, 180, 60, 180, 420, 60, 180, 60, 60, 180, 60, 180, 420, 60, 420, 60};
    public static final long[] BATTLEFIELD_4_DRUM_RHYTHM = new long[]{100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 50, 50, 100, 800};
    public static final long[] BONEY_M_RASPUTIN = new long[]{100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 200, 100, 200, 100, 200, 100, 200, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 50, 50, 100, 800};
    public static final long[] SUPER_MARIO_THEME_INTRO = new long[]{125,75,125,275,200,275,125,75,125,275,200,600,200,600};
    public static final long[] GOGO_POWER_RANGERS = new long[]{150,150,150,150,75,75,150,150,150,150,450};
    public static final long[] MICHAEL_JACKSON_SMOOTH_CRIMINAL = new long[]{0,300,100,50,100,50,100,50,100,50,100,50,100,50,150,150,150,450,100,50,100,50,150,150,150,450,100,50,100,50,150,150,150,450,150,150};
    public static final long[] JAMES_BOND_007 = new long[]{200,100,200,275,425,100,200,100,200,275,425,100,75,25,75,125,75,25,75,125,100,100};



    private Vibrator vibrator;
    private Context context;

    public VibratorController(Context context) {
        this.context = context;
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void vibrate(Long duration, int amplitude) {
        final VibrationEffect vibrationEffect;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrationEffect = VibrationEffect.createOneShot(duration, amplitude);
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect);
        } else {
            vibrator.cancel();
            vibrator.vibrate(duration);
        }
    }

    public void vibrate(Long duration) {
        vibrate(duration, VibrationEffect.DEFAULT_AMPLITUDE);
    }

    public void vibrate(long[] pattern) {
        final VibrationEffect vibrationEffect;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrationEffect = VibrationEffect.createWaveform(pattern, -1);
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect);
        } else {
            vibrator.cancel();
            vibrator.vibrate(pattern, -1);
        }
    }
}

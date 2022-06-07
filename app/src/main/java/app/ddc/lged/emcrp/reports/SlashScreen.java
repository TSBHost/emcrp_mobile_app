package app.ddc.lged.emcrp.reports;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.home.GridViewActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SlashScreen extends AppCompatActivity {

    private static final int UI_ANIMATION_DELAY = 5000;
    private final Handler mHideHandler = new Handler();
    LinearLayout linearLayout;

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(SlashScreen.this, GridViewActivity.class);
            startActivity(intent);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash_screen);
        linearLayout = (LinearLayout) findViewById(R.id.slashscreenid);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SlashScreen.this, GridViewActivity.class);
                startActivity(intent);
            }
        });
    }

    ////// back key pressed closed app with 3 times pressed//////
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finishAffinity();
            System.exit(0);
        }
        else {
            Toast.makeText(this, "Press again to exit",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 5 * 1000);

        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}

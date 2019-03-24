package com.example.myapplication;

import android.os.Bundle;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainPage extends AppCompatActivity {

    MediaPlayer horn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        horn = MediaPlayer.create(this, R.raw.horn_sound);

        /*Button hornButton = (Button) this.findViewById(R.id.horn_button);
        hornButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horn.start();
            }
        });*/
    }

    public void playHorn(View v) {
        horn.start();
    }

    public void mapWidget(View v) {
    }


    public void sosWidget(View v) {

    }

    public void settingsWidget(View v) {
        setContentView(R.layout.settings_widget);
    }

    public void helpWidget(View v) {
        setContentView(R.layout.help_widget);
    }

    public void hornWidget(View v) {
        setContentView(R.layout.horn_widget);
    }

    public void backToMenu(View v) {
        setContentView(R.layout.main_page);
    }
}

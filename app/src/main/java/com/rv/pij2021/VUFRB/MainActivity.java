package com.rv.pij2021.VUFRB;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rv.pij2021.VUFRB.service.DecisionBlocksRA;
import com.rv.pij2021.VUFRB.service.SensorService;

public class MainActivity extends AppCompatActivity {
    private DecisionBlocksRA decisionBlocksRA;
    private SensorService sensorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonAbrir = findViewById(R.id.buttonAbrir);
        TextView textViewBloco = findViewById(R.id.textViewBloco);

        decisionBlocksRA = new DecisionBlocksRA();
        sensorService = new SensorService(this);

        buttonAbrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("RAUFRB", "OLA");
                sensorService.positionNetwork();
                textViewBloco.setText(decisionBlocksRA.bloco(sensorService).titulo);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorService.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorService.onPause();
    }

}
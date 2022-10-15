package com.example.steppers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Menu extends AppCompatActivity {

    //back
    private void exitmenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //BackButton
        ImageButton back = findViewById(R.id.backbutton);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                exitmenu();
            }
        });
    }
}
package com.example.steppers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class EditParameters extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_parameters);
        //Links "backbutton" button to Menu Activity (Return to Menu)
        ImageButton backtoMenu = findViewById(R.id.backbutton);
        backtoMenu.setOnClickListener(Edit_Para -> {
            Intent intent = new Intent(EditParameters.this, Menu.class);
            startActivity(intent);
        });
    }
}
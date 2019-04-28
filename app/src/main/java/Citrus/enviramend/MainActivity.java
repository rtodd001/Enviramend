package Citrus.enviramend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText text;
    private static int INGREDIENT_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        text = findViewById(R.id.editText);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button scanBtn =  findViewById(R.id.button1);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IngredientActivity.class);
                intent.putExtra(getResources().getString(R.string.Bundle_Start_Ingredient),"nuTeLla");
                startActivityForResult(intent,INGREDIENT_CODE);
            }
        });

    }
}

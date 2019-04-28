package Citrus.enviramend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import Citrus.enviramend.R;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 10; //not too sure why this is 10 so be careful
    private static final int INTENT_CODE = 34932;
    private static final int VISION_CODE = 11111;
    private static int INGREDIENT_CODE = 1234;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.TextView);
        //creates button scanBtn, use findVewById to get a result and we cast it by (Button)
        Button scanBtn =  findViewById(R.id.scanBtn);
        //ImageView imageView = (ImageView)findViewById(R.id.imageView);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VisionActivity.class);
                startActivityForResult(intent,VISION_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == VISION_CODE){
            if(resultCode == Activity.RESULT_OK){
                //figure out what gets returned
                String text = data.getStringExtra(this.getResources().getString(R.string.VISION_RETURN));
                Toast.makeText(getApplicationContext(),"In main: " + text, Toast.LENGTH_LONG).show();
                textView.setText("Result is " + text);
                Intent intent = new Intent(MainActivity.this, IngredientActivity.class);
                intent.putExtra(getResources().getString(R.string.Bundle_Start_Ingredient),text);
                startActivityForResult(intent,INGREDIENT_CODE);
            }
        }
        else if(requestCode == INGREDIENT_CODE){
            if(resultCode == Activity.RESULT_OK){
                String ing = data.getStringExtra(getResources().getString(R.string.Ingredient_Intent_Return));
                textView.setText(ing);
            }
        }
    }
}

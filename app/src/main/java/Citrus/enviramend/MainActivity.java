package Citrus.enviramend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import Citrus.enviramend.R;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 10; //not too sure why this is 10 so be careful
    private static final int INTENT_CODE = 34932;
    private static final int VISION_CODE = 11111;
    private static final int BARCODE_INTENT = 2222;
    private static int INGREDIENT_CODE = 1234;
    private TextView textView;
    private Map<String, String> badForEnvironment = new HashMap<>();



    private String[] split(String input){
        Log.e("Split",input);
        input = input.toLowerCase();
        String[] output;
        output = input.split("[:,()]");
        for(int i = 0; i < output.length; i++){
            output[i] = output[i].trim();
        }
        return output;
    }
    /*private Vector<String> splat(String input){
        input = input.toLowerCase();
        Vector<String> output = new Vector<>();
        input = input.substring(input.indexOf(':'));
        output = input.split("[:,()]");
        for(int i = 0; i < output.length; i++){
            output[i] = output[i].trim();
        }
        return output;
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        badForEnvironment.put("palm oil","Palm oil is contributing to deforestation in Indonesia and Malaysia. Clearing forests to grow oil palm trees contributes to global warming, it also leads to habitat loss in one of the most biodiverse areas of the world.");
        badForEnvironment.put("egg", "A carton of eggs have a carbon footprint of 5 pounds, and a water footprint of 2,400 liters." );
        badForEnvironment.put("eggs", "A carton of eggs have a carbon footprint of 5 pounds, and a water footprint of 2,400 liters.");
        badForEnvironment.put("lamb", "Lamb, along with beef, have the largest impact on greenhouse gas emissions.");
        badForEnvironment.put("beef", "Beef production requires far more water, land, and nitrogen fertilizer, and it creates more greenhouse gas emissions than other forms of animal protein.");
        badForEnvironment.put("pork", "One kilo of pork creates 7.9 kilos of carbon emissions.");
        badForEnvironment.put("chicken", "Poultry ranks in the top 10 in per-capita emissions in the US, with just over 5kg of CO2 per kg");
        badForEnvironment.put("cheese", "Cheese is a major carbon dioxide contributor. Cheese production is also energy intensive due to the several processes involved in separating raw milk from low-fat cream as well as pasteurization, cooling, ripening and churning.");
        badForEnvironment.put("salmon", "Salmon farming is one of the most destructive aquaculture systems. Waste from farms, chemicals, and disease causing parasites are released directly into the ocean waters, threatening other marine life.");
        badForEnvironment.put("tuna", "Tuna is a victim of overfishing, the methodologies of large commercial fishing vessels to catch tuna is threatening their numbers.");
        badForEnvironment.put("almonds", "Growing almonds requires a lot of water.");
        badForEnvironment.put("almond", "Growing almonds requires a lot of water.");
        badForEnvironment.put("almond milk", "Growing almonds requires a lot of water.");
        badForEnvironment.put("soybeans", "In Brazil, the area of forest cleared for soybean plantations is responsible for the release of over 473 million tons of carbon dioxide.");
        badForEnvironment.put("chocolate", "Cacao plantations are responsible for huge amounts of deforestation.");
        //badForEnvironment.put("sugar", "According to the World Wildlife Fund, sugar cane production has caused a greater loss of biodiversity than any other crop on the planet.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.TextView);
        //creates button scanBtn, use findVewById to get a result and we cast it by (Button)
        ImageButton scanBtn =  (ImageButton) findViewById(R.id.uploadImage);
        ImageButton barBtn =  (ImageButton) findViewById(R.id.barcodeBtn);

        //ImageView imageView = (ImageView)findViewById(R.id.imageView);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VisionActivity.class);
                startActivityForResult(intent,VISION_CODE);
                //Intent intent = new Intent(MainActivity.this, BarcodeScan.class);
                //startActivityForResult(intent,INTENT_CODE);
            }
        });
        barBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarcodeScan.class);
                startActivityForResult(intent,BARCODE_INTENT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == VISION_CODE){
            if(resultCode == Activity.RESULT_OK){
                //figure out what gets returned
                String text = data.getStringExtra(this.getResources().getString(R.string.VISION_RETURN));
                Log.e("MainActivity", "IN VISION_INTENT");
                //Toast.makeText(getApplicationContext(),"In main: " + text, Toast.LENGTH_LONG).show();
                textView.setText("Result is " + text);
                Intent intent = new Intent(MainActivity.this, IngredientActivity.class);
                intent.putExtra(getResources().getString(R.string.Bundle_Start_Ingredient),text);
                startActivityForResult(intent,INGREDIENT_CODE);
            }
        }
        else if(requestCode == BARCODE_INTENT){
            if(resultCode == Activity.RESULT_OK){
                String text = data.getStringExtra(this.getResources().getString(R.string.BARCODE_RETURN));
                //Toast.makeText(getApplicationContext(),"In main: " + text, Toast.LENGTH_LONG).show();
                Log.e("MainActivity", "IN BARCODE_INTENT");
                //textView.setText("Result is " + text);
                Intent intent = new Intent(MainActivity.this, IngredientActivity.class);
                intent.putExtra(getResources().getString(R.string.Bundle_Start_Ingredient),text);
                startActivityForResult(intent,INGREDIENT_CODE);
            }
            else {
                Toast.makeText(getApplicationContext(),"Failed to return from Barcode", Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode == INGREDIENT_CODE){
            if(resultCode == Activity.RESULT_OK){
                String ing = data.getStringExtra(getResources().getString(R.string.Ingredient_Intent_Return));
                Log.e("MainActivity", "IN INGREDIENT_INTENT");
                textView.setText(ing);
                String output = "";
                String[] splitString = split(ing);
                for(String item : splitString){
                    if(badForEnvironment.containsKey(item)) {

                        output =  '-' + (badForEnvironment.get(item) + "\n") + output;
                    }
                }
                if(output == ""){
                    textView.setText("You're good to go. :)");
                }
                else{
                    textView.setText(output);
                }
            }
        }
    }
}

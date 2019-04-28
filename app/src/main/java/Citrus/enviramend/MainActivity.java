package Citrus.enviramend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import Citrus.enviramend.R;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 10; //not too sure why this is 10 so be careful
    private static final int INTENT_CODE = 34932;
    private static final int VISION_CODE = 11111;

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creates button scanBtn, use findVewById to get a result and we cast it by (Button)
        Button scanBtn =  findViewById(R.id.scanBtn);
        //ImageView imageView = (ImageView)findViewById(R.id.imageView);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarcodeScan.class);
                startActivityForResult(intent,INTENT_CODE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == VISION_CODE){
            if(resultCode == Activity.RESULT_OK){
                //figure out what gets returned
                String text = data.getStringExtra(this.getResources().getString(R.string.VISION_RETURN));
                textView.setText(text);
            }
        }
    }
}

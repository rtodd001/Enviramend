package Citrus.enviramend;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Allergy extends AppCompatActivity {
    TextView textView;
    Button add, back;
    EditText editText;
    String allergies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Allergy", "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy);
        allergies = "";
        textView = findViewById(R.id.textView);
        add = findViewById(R.id.add);
        back = findViewById(R.id.back);
        editText = findViewById(R.id.editText);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().equals("")) {
                    allergies = allergies + "\n" + editText.getText().toString();
                    Log.e("Allergy", editText.getText().toString());
                    textView.setText(allergies);
                    editText.setText("");
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                Log.e("Allergy",allergies);
                returnIntent.putExtra(getResources().getString(R.string.Allergy_Intent_Return), allergies);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}

package Citrus.enviramend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

//import org.apache.http.util.ByteArrayBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class VisionActivity extends AppCompatActivity {

    TextView textView;
    private static final int SELECT_IMAGE = 10;
    public Bitmap bitmap;
    public FirebaseVisionImage imageFile;
    static  Uri uri;
    private  static  final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);
        FirebaseApp.initializeApp(this);
        Intent pickImage = new Intent(Intent.ACTION_PICK);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
        Uri dir = Uri.parse(path);
        pickImage.setDataAndType(dir, "image/*");
        startActivityForResult(pickImage, SELECT_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SELECT_IMAGE){
            if (resultCode == Activity.RESULT_OK){
                uri = data.getData();
                startOCR();
            }
        }
    }

    private void startOCR() {
        try {
            imageFile = FirebaseVisionImage.fromFilePath(getApplicationContext(), uri);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed to get image", Toast.LENGTH_LONG).show();
        }

        //can change to on device as well
        //FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getCloudTextRecognizer();
        Task<FirebaseVisionText> result =
                detector.processImage(imageFile).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        Toast.makeText(getApplicationContext(), "OCR Successful", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to process image", Toast.LENGTH_LONG).show();
                            }
                        }
                );
        //be careful with this its a makeshift wait function
        while (!result.isComplete()) {
        }
        String resultText = result.getResult().getText();
        for (FirebaseVisionText.TextBlock block : result.getResult().getTextBlocks()) {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionText.Line line : block.getLines()) {
                String lineText = line.getText();
                Float lineConfidence = line.getConfidence();
                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (FirebaseVisionText.Element element : line.getElements()) {
                    String elementText = element.getText();
                    Float elementConfidence = element.getConfidence();
                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }
        Toast.makeText(getApplicationContext(),resultText,Toast.LENGTH_LONG).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(this.getResources().getString(R.string.VISION_RETURN), resultText);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}

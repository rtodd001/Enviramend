package Citrus.enviramend;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class BarcodeCamera extends AppCompatActivity {

    String imageFilePath;
    String pathToFile;
    ImageView imageView;
    //static FirebaseVisionImage image;
    static Uri uri;
    Bitmap bitmap;
    private static final int SELECT_IMAGE = 10;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_camera);
        FirebaseApp.initializeApp(this);
        imageView = findViewById(R.id.imageView);
        if(Build.VERSION.SDK_INT >=23){
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},2);

        }
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,SELECT_IMAGE);*/
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();
            if(photoFile != null){
                pathToFile = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(BarcodeCamera.this,"Citrus.enviramend.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePic, SELECT_IMAGE);
            }

            //openCameraIntent();
        }
    }

    private File createPhotoFile(){
        String name = new SimpleDateFormat("MMddyyyy_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name,".jpg", storageDir);
        }catch (IOException e){
            Log.d("myLog", "Exception: " + e.toString());
        }
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                bitmap = BitmapFactory.decodeFile(pathToFile);
                startOCR();
                /*bitmap = (Bitmap)data.getExtras().get("data");
                image = FirebaseVisionImage.fromBitmap(bitmap);
                imageView.setImageBitmap(bitmap);
                image = FirebaseVisionImage.fromBitmap(bitmap);
                startOCR();*/
            }
            //don't compare the data to null, it will always come as  null because we are providing a file URI, so load with the imageFilePath we obtained before opening the cameraIntent
            //Glide.with(this).load(imageFilePath).into(mImageView);
            // If you are using Glide.
        }
    }
    private void startOCR() {
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_UPC_A)
                        .build();
        FirebaseVisionImage imagePic = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector(options);
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(imagePic)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        //Toast.makeText(getApplicationContext(), barcodes.get(0).getRawValue(), Toast.LENGTH_LONG).show();
                        if (!barcodes.isEmpty()) {
                            String resultText = barcodes.get(0).getRawValue();
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(getApplicationContext().getResources().getString(R.string.BARCODE_RETURN), resultText);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Barcode failed", Toast.LENGTH_LONG).show();
                    }
                });
        while (!result.isComplete()){}

        //Toast.makeText(getApplicationContext(), ResultText,Toast.LENGTH_LONG).show();

    }




    /*private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,SELECT_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  *//* prefix *//*
                ".jpg",         *//* suffix *//*
                storageDir      *//* directory *//*
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }*/
}

package www.example.com.nmttranslator;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.util.Arrays;
import java.util.List;

public class ImageInputActivity extends AppCompatActivity {

    Button capture, upload;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_input);
        capture = findViewById(R.id.capture);
        upload = findViewById(R.id.upload);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ImageInputActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ImageInputActivity.this,
                            Manifest.permission.CAMERA)){

                    }else{
                        ActivityCompat.requestPermissions(ImageInputActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                    .getCloudTextRecognizer();
            FirebaseVisionCloudTextRecognizerOptions options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
                    .setLanguageHints(Arrays.asList("hi"))
                    .build();
            Task<FirebaseVisionText> result = detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText result) {
                    // Task completed successfully
                    // [START_EXCLUDE]
                    // [START get_text_cloud]

                    String resultText = result.getText();
                    Toast.makeText(ImageInputActivity.this, resultText, Toast.LENGTH_SHORT).show();
                    for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
                        String blockText = block.getText();
                        Float blockConfidence = block.getConfidence();
                        List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                        Point[] blockCornerPoints = block.getCornerPoints();
                        Rect blockFrame = block.getBoundingBox();
                        for (FirebaseVisionText.Line line: block.getLines()) {
                            String lineText = line.getText();
                            Float lineConfidence = line.getConfidence();
                            List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                            Point[] lineCornerPoints = line.getCornerPoints();
                            Rect lineFrame = line.getBoundingBox();
                            for (FirebaseVisionText.Element element: line.getElements()) {
                                String elementText = element.getText();
                                Float elementConfidence = element.getConfidence();
                                List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                Point[] elementCornerPoints = element.getCornerPoints();
                                Rect elementFrame = element.getBoundingBox();
                            }
                        }
                    }

                    // [END get_text_cloud]
                    // [END_EXCLUDE]
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ImageInputActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
                }
            });

        }
        if (requestCode == UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
        }
    }
}

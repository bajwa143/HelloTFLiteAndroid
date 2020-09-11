package com.demo.tflitedemo;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {
    EditText etInputValue;
    TextView tvResult;
    TextView tvHeading;


    Interpreter interpreter;
    float[][] inputValue = new float[1][1];
    float[][] outputValue = new float[1][1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etInputValue = findViewById(R.id.input_value);
        tvResult = findViewById(R.id.textview_result);
        tvHeading = findViewById(R.id.heading_textview);
        String labelText = "This application demonstrate finding solution of linear equation<br><b>y = 3x</b><br>" +
                "using machine learning model on linear regression";
        tvHeading.setText(HtmlCompat.fromHtml(labelText, HtmlCompat.FROM_HTML_MODE_COMPACT));

        // call method to initialize tflite model
        loadAndSetupTFLite();
    }

    public void Test(View view) {
        if (etInputValue.getText().length() > 0) {
            // get input value from edit text
            inputValue[0][0] = Integer.parseInt(etInputValue.getText().toString());

            if (interpreter != null) {
                interpreter.run(inputValue, outputValue);
                tvResult.setText(String.valueOf(outputValue[0][0]));
            } else {
                Toast.makeText(this, "Can't load model correctly", Toast.LENGTH_SHORT).show();
            }
        } else {
            etInputValue.setError("Enter some number");
        }
    }

    public void loadAndSetupTFLite() {
        // Set the interpreter options
        Interpreter.Options options = new Interpreter.Options();
        try {
            // get the file descriptor of the model file
            AssetFileDescriptor fileDescriptor = getAssets().openFd("model.tflite");
            // Open file input stream
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());

            // Read the file channels along with its offset and length
            FileChannel fileChannel = inputStream.getChannel();
            long statOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();

            // load the TFLite model
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, statOffset, declaredLength);
            // Interpreter class to load a model and drive model inference
            interpreter = new Interpreter(mappedByteBuffer, options);

        } catch (Exception exception) {
            Log.e("TFLite Demo", "Error " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}

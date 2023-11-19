package com.example.ejercicio24;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.gcacace.signaturepad.views.SignaturePad;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private SignaturePad signaturePad;
    private Button btnSaveSignature;
    private EditText editTextDescription;
    private LinearLayout linearSignatures;
    private SignatureDAO signatureDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signaturePad = findViewById(R.id.signaturePad);
        btnSaveSignature = findViewById(R.id.btnSaveSignature);
        editTextDescription = findViewById(R.id.editTextDescription);
        linearSignatures = findViewById(R.id.linearSignatures);
        signatureDAO = new SignatureDAO(this);

        btnSaveSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
                byte[] signatureBlob = convertBitmapToBlob(signatureBitmap);

                // Obtener la descripción del EditText
                String description = editTextDescription.getText().toString();

                long id = signatureDAO.insertSignature(description, signatureBlob);

                if (id != -1) {
                    Toast.makeText(MainActivity.this, "Firma guardada con éxito", Toast.LENGTH_SHORT).show();
                    // Actualizar el CardView con las firmas almacenadas
                    updateCardView();
                    // Limpiar SignaturePad y EditText
                    signaturePad.clear();
                    editTextDescription.getText().clear();
                } else {
                    Toast.makeText(MainActivity.this, "Error al guardar la firma", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Mostrar las firmas almacenadas en el CardView
        updateCardView();
    }

    private void updateCardView() {
        Cursor cursor = signatureDAO.getAllSignatures();
        linearSignatures.removeAllViews(); // Limpiar vistas antiguas

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Obtener datos de la firma
                byte[] signatureBlob = cursor.getBlob(cursor.getColumnIndexOrThrow("blob_data"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                // Crear ImageView para la firma
                ImageView signatureImageView = new ImageView(this);
                Bitmap signatureBitmap = BitmapFactory.decodeByteArray(signatureBlob, 0, signatureBlob.length);
                signatureImageView.setImageBitmap(signatureBitmap);

                // Crear TextView para la descripción
                TextView descriptionTextView = new TextView(this);
                descriptionTextView.setText(description);

                // Agregar ImageView y TextView al LinearLayout
                linearSignatures.addView(signatureImageView);
                linearSignatures.addView(descriptionTextView);
            } while (cursor.moveToNext());
        }

        // Cerrar el cursor después de usarlo
        if (cursor != null) {
            cursor.close();
        }
    }

    private byte[] convertBitmapToBlob(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}


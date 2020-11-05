package gestagro.ejemplo.gestagro_recognition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.gestagro_recognition.R;

public class pantalla1 extends AppCompatActivity {
    Button btnSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla1);
    }

    //Metodo de los botones-> Siguiente
    public void Siguiente (View v){
        Intent siguiente = new Intent(this,MainActivity.class);
        startActivity(siguiente);
    }
    //boton salir de la aplicacion

}

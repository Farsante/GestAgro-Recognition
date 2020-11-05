package gestagro.ejemplo.gestagropmv2;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.gestagropmv2.R;

import static com.example.gestagropmv2.R.mipmap.ic_launcher;

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

package gestagro.ejemplo.gestagropmv2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestagropmv2.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //-----------------------------------------------------------------

    //-----------------------------------------------------------------
    Button btnHit;

    ProgressDialog pd;
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    ProgressDialog prgDialog ;
//-----------------------------------------------------------------
    // metodo para el boton Anterior
    public void Anterior (View v){
        Intent anterior = new Intent(this, pantalla1.class);
        startActivity(anterior);
    }
    //-----------------------------------------------------------------


    // Set Cancelable as False

    String encodedString;
    com.loopj.android.http.RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    Uri imagenUri;
    private static int RESULT_LOAD_IMG = 1;
    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 5;

                try {
                    //   bitmap = BitmapFactory.decodeFile(imgPath, options);
                   bitmap = BitmapFactory.decodeFile(imagenUri.getPath().toString(), options);
                  //  Toast.makeText(getApplicationContext(), "abc" + imagenUri.getPath(), Toast.LENGTH_LONG).show();
               //  InputStream imageStream = getContentResolver().openInputStream(imagenUri);
                 //   bitmap = BitmapFactory.decodeStream(imageStream);
                    // BitmapFactory.decodeFi
                    System.out.println("la imagen" + imagenUri.toString());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Must compress the Image to reduce image size to make upload easy
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                    byte[] byte_arr = stream.toByteArray();
                    // Encode Image to String
                    encodedString = Base64.encodeToString(byte_arr, 0);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                prgDialog.setMessage("Iniciando subida");
                // Put converted Image string into Async Http Post param
                params.put("image", encodedString);
                params.put("filename",imagenUri.getPath().substring(1+imagenUri.getPath().toString().lastIndexOf("/")));
                // Trigger Image upload
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }

    private void triggerImageUpload() {
        makeHTTPCall();
    }

    private void makeHTTPCall() {
        try {
            prgDialog.setMessage("Comunicándose con el servidor");
            AsyncHttpClient client = new AsyncHttpClient();
            // Don't forget to change the IP address to your LAN address. Port no as well.
            client.post("http://www.fletessalta.com.ar/gestagro/subir.php",
                    params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            prgDialog.hide();
                            //Toast.makeText(getApplicationContext(), new String(responseBody), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "ESCANEANDO", Toast.LENGTH_LONG).show();
                            EditText et = findViewById(R.id.Wurl);
                            new JsonTask().execute("http://www.fletessalta.com.ar/gestagro/prueba2.php?url=http://www.fletessalta.com.ar/gestagro/" + imagenUri.getPath().substring(1+imagenUri.getPath().toString().lastIndexOf("/")) );
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            prgDialog.hide();
                            // When Http response code is '404'
                            if (statusCode == 404) {
                                Toast.makeText(getApplicationContext(),
                                        "Requested resource not found",
                                        Toast.LENGTH_LONG).show();
                            }
                            // When Http response code is '500'
                            else if (statusCode == 500) {
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong at server end",
                                        Toast.LENGTH_LONG).show();
                            }
                            // When Http response code other than 404, 500
                            else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Error Occured n Most Common Error: n1. Device not connected to Internetn2. Web App is not deployed in App servern3. App server is not runningn HTTP Status code : "
                                                + statusCode, Toast.LENGTH_LONG)
                                        .show();
                            }
                        }


                        // When the response returned by REST has Http
                        // response code other than '200' such as '404',
                        // '500' or '403' etc

                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            String resultado = "";
            try {
                JSONObject jObject = new JSONObject(result);
                JSONArray j1 = jObject.getJSONArray("images");
                JSONObject j2 = j1.getJSONObject(0);
                JSONArray j3 = j2.getJSONArray("classifiers");
                JSONObject j4 = j3.getJSONObject(0);
                JSONArray j5 = j4.getJSONArray("classes");
                JSONObject j6;
                String[] lista = new String[]{"Alternaria","Cancro de los Cítricos", "Moho", "Mosca Blanca","Mosca Minadora","Musgo","Trips"};
                int[] listaText = new int[]{R.id.plaga1, R.id.plaga2, R.id.plaga3, R.id.plaga4, R.id.plaga5, R.id.plaga6, R.id.plaga7};
                int[] listaPb = new int[]{R.id.pb1, R.id.pb2, R.id.pb3, R.id.pb4, R.id.pb5, R.id.pb6, R.id.pb7};

                for(int i = 0; i<=6; i++){
                    j6 = j5.getJSONObject(i);
                    Double valor = Double.parseDouble(j6.get("score").toString()) *100;
                    if (valor<50) {
                        ((ProgressBar) findViewById(listaPb[i])).getProgressDrawable().setColorFilter(Color.rgb(141, 219, 46), android.graphics.PorterDuff.Mode.SRC_IN);
                    }else{
                        ((ProgressBar) findViewById(listaPb[i])).getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                    ((TextView) (findViewById(listaText[i]))).setText(lista[i] + " " + String.format("%.2f",valor) + "%")  ;
                    ((ProgressBar) (findViewById(listaPb[i]))).setProgress((int) Math.round(valor));
                    //resultado = resultado + j6.get("class").toString() + " : " +  + "\n";
                }

              //  JSONObject images = new JSONObject(jObject.getString("images"));
               // JSONArray classifiers = images.getJSONArray("classifiers");

                //((TextView) findViewById(R.id.plaga1)).setText(resultado);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prgDialog  = new ProgressDialog(MainActivity.this);
        prgDialog.setCancelable(false);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
       // NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
      //  navigationView.setNavigationItemSelectedListener(this);

        Button bs = findViewById(R.id.buttonscan);
        bs.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "ESCANEANDO", Toast.LENGTH_LONG).show();
                EditText et = findViewById(R.id.Wurl);
                ((ImageView) findViewById(R.id.imageView4)).setImageURI(null);
                new JsonTask().execute("http://www.fletessalta.com.ar/gestagro/prueba2.php?url=" + et.getText()) ;
            }
        });


        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();




        Button bfoto = findViewById(R.id.buttonphoto);

        bfoto.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                    try {
                        Toast.makeText(getApplicationContext(), "Tome la fotografía en modo horizontal", Toast.LENGTH_LONG).show();
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "fname_" +
                                    String.valueOf(System.currentTimeMillis()) + ".jpg"));
                            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            Log.e("URI", imageUri.toString());
                            imagenUri = imageUri;



                            // Convert image to String using Base64



                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }





            }
        });


        Button bgaleria = findViewById(R.id.buttongaleria);

        bgaleria.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                try {


                    Intent intent=new Intent(Intent.ACTION_PICK);
                    // Sets the type as image/*. This ensures only components of type image are selected
                    intent.setType("image/*");
                    //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                    // Launching the Intent
                    startActivityForResult(intent,GALLERY_REQUEST_CODE);
                       // imagenUri = imageUri;



                        // Convert image to String using Base64




                }catch (Exception e){
                    e.printStackTrace();
                }





            }
        });
check();
    }
    final int REQUEST_IMAGE_CAPTURE = 1;
    final int GALLERY_REQUEST_CODE = 3;

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx); }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getApplicationContext(), new String("probando" + requestCode + "-" + resultCode), Toast.LENGTH_LONG).show();
        if((requestCode==REQUEST_IMAGE_CAPTURE )&& (resultCode == RESULT_OK)) {
            ((ImageView) findViewById(R.id.imageView4)).setImageURI(imagenUri);
            Toast.makeText(getApplicationContext(), new String("SUBIENDO IMAGEN"), Toast.LENGTH_LONG).show();
            prgDialog.show();

            encodeImagetoString();
        }
        if((requestCode==GALLERY_REQUEST_CODE) && (resultCode == RESULT_OK)){
            Uri selectedImage = data.getData();
            String ruta = getRealPathFromURI(selectedImage);
            ((ImageView) findViewById(R.id.imageView4)).setImageURI(selectedImage);
            imagenUri = Uri.fromFile(new File(ruta));
            Toast.makeText(getApplicationContext(), new String("SUBIENDO IMAGEN" ), Toast.LENGTH_LONG).show();
            prgDialog.show();

            encodeImagetoString();
        }


    }
final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    private void check(){
        if ( (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )){
            // Permission is not granted

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
            dlgAlert.setMessage("Autores: \n\t\t\t\tJuane Francisco\n\t\t\t\tBazán Facundo\n\t\t\t\tBorsella, Luciana\n\t\t\t\tVillegas, Nicolas");
            dlgAlert.setTitle("GestAgro PMV2");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

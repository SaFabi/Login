package com.example.morpheus.login;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnconsultar, btnGuardar;
    EditText etId, etNombres, etTelefono;
    TextView txtmsj;
    String cadena = "";
    String inicio = "[";
    String fin = "]";
    char coma = ',';
    String comillas= "\"";
    int inicioNum = 0;
    int finNum = 0;
    int comillasNum = 0;
    String[]  datos;
    String [][] lista;
    String Nombre, Telefono, id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnconsultar = (Button)findViewById(R.id.btnConsultar);
        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        etId = (EditText)findViewById(R.id.etId);
        txtmsj = (TextView)findViewById(R.id.txtmsj);
        etNombres = (EditText)findViewById(R.id.etNombres);
        etTelefono = (EditText)findViewById(R.id.etTelefono);

        btnconsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new ConsultarDatos().execute("http://192.168.1.84/CursoAndroid/consulta.php?id="+etId.getText().toString());
            }
        });


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CargarDatos().execute("http://192.168.1.84/CursoAndroid/registro.php?nombres="+etNombres.getText().toString()+"&tel="+etTelefono.getText().toString());
            }
        });



    }

    private class CargarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getApplicationContext(), "Se almacenaron los datos correctamente", Toast.LENGTH_LONG).show();

        }
    }


    private class ConsultarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            JSONArray ja = null;
            try {
                //JSONObject objeto = new JSONObject(result);
                ja = new JSONArray(result);
                int tamano = result.length();
                //Ciclo para encontrar el incio y el fin de la cadena
                for (int i = 0; i < tamano;i++){
                   String c = (String.valueOf(result.charAt(i)));
                    if (c.equals(inicio)){
                        if (inicioNum == i){
                            inicioNum = i;
                        }else  {

                        }
                    }
                    if (c.equals(fin)) {
                        finNum = i;
                        //String.valueOf(result.charAt(i)).concat(", ");

                    }
                    if (c.equals(comillas)){
                        comillasNum = i;
                    }
                } for (int i = inicioNum; i<finNum;i++){

                    String a = (String.valueOf(result.charAt(i)));
                    if (a.equals(fin)) {
                        cadena+=",";

                    }
                    if (a.equals(inicio)== false && a.equals(fin) == false && a.equals(comillas) == false){
                        cadena+=a;
                    }
                }
                //Toast.makeText(getApplicationContext(), cadena, Toast.LENGTH_SHORT).show();
                datos= cadena.split(",");
                //Toast.makeText(getApplicationContext(), datos[0], Toast.LENGTH_SHORT).show();
                //----------------------------------------------------------------------------------
                //Para llenar la matriz con todos los datos
                lista = new  String[4][3];
                int i = 0;

                    for (int j = 0; j<4;j++){
                        for (int k = 0; k<3;k++){
                            lista[j][k]=datos[i];
                            i++;
                        }
                    }



                for (int j = 0; j<4;j++){
                    for (int k = 0; k<3;k++){
                        id = lista[j][k];
                        Nombre = lista[j][k+1];
                        Telefono= lista[j][k+2];
                        etId.setText(id);
                        etNombres.setText(Nombre);
                        etTelefono.setText(Telefono);


                        break;
                    }
                    etId.setText("");
                    etTelefono.setText("");
                    etNombres.setText("");

                }
               /* etNombres.setText(datos[1].toString());
                etTelefono.setText(datos[2].toString());*/
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl = myurl.replace(" ","%20");
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("respuesta", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}

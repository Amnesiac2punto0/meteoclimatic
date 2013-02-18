/*(C) Copyright

    This file is part of widget Meteoclimatic.
    Autor: Antonio Cristóbal Álvarez Abellán -> acabellan@gmail.com
    
    */

package deeloco.android.meteoclimatic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.widget.TextView;

public class datosEstacion extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//Recuperamos el parámetro del código de la estación 
		String codigoEstacion = (String) getIntent().getExtras().get("codigo");
		String nombreEstacion = (String) getIntent().getExtras().get("nombre");
		Log.d("datosEstacion.java","Código="+codigoEstacion);
		Log.d("datosEstacion.java","Nombre="+nombreEstacion);
		setContentView(R.layout.datos_estacion);
		
		TextView cabecera = (TextView) findViewById(R.id.tw_cabecera);
		cabecera.setText(nombreEstacion);
		
		//Capturamos los datos de la estación
		
		estacion ee=new estacion(datosEstacion.this,nombreEstacion,codigoEstacion);
		
		if (!(ee.capturarDatos()))
    	{
			//Error en la captura de los datos
    		Log.d("datosEstacion","Error al capturar datos");
    		cabecera.setText("Error captura de datos");
    	}
		else
		{
			//Temperatura
			TextView temp = (TextView) findViewById(R.id.temp);
			temp.setText(""+ee.getDatosMeteo().T);
			TextView temp_max = (TextView) findViewById(R.id.temp_max);
			temp_max.setText(""+ee.getDatosMeteo().Tmax);
			TextView temp_min = (TextView) findViewById(R.id.temp_min);
			temp_min.setText(""+ee.getDatosMeteo().Tmin);
			//Humedad
			TextView hum = (TextView) findViewById(R.id.hum);
			hum.setText(""+ee.getDatosMeteo().H);
			TextView hum_max = (TextView) findViewById(R.id.hum_max);
			hum_max.setText(""+ee.getDatosMeteo().Hmax);
			TextView hum_min = (TextView) findViewById(R.id.hum_min);
			hum_min.setText(""+ee.getDatosMeteo().Hmin);
			//Viento
			TextView viento = (TextView) findViewById(R.id.viento);
			viento.setText(ee.getDatosMeteo().getDirViento()+" "+ee.getDatosMeteo().W);
			TextView viento_max = (TextView) findViewById(R.id.viento_max);
			viento_max.setText(""+ee.getDatosMeteo().Wmax);
			//Presión
			TextView pre = (TextView) findViewById(R.id.pre);
			pre.setText(""+ee.getDatosMeteo().B);
			TextView pre_max = (TextView) findViewById(R.id.pre_max);
			pre_max.setText(""+ee.getDatosMeteo().Bmax);
			TextView pre_min = (TextView) findViewById(R.id.pre_min);
			pre_min.setText(""+ee.getDatosMeteo().Bmin);
			//Presión
			TextView prec = (TextView) findViewById(R.id.prec);
			prec.setText(""+ee.getDatosMeteo().P);			

		}

		WebView webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setBackgroundColor(0);
		
		webView.getSettings().setBuiltInZoomControls(true);
		
		 final Activity activity = this;
		 final ProgressDialog dialog = ProgressDialog.show(activity, "",getString(R.string.cargando_datos), true);
		 webView.setWebChromeClient(new WebChromeClient() {
	            public void onProgressChanged(WebView view, int progress)
	            {
	            	if(progress == 100)
	                    dialog.cancel();
	            }
	            
	        });
		 
		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		webView.loadUrl("http://www.meteoclimatic.com/addons/graf24h.php?st="+codigoEstacion+"&id=es");
		

		//webView.setWebViewClient(new HelloWebViewClient());
		
	}
	
	
	

}

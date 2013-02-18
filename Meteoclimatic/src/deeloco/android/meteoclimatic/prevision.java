/*(C) Copyright

    This file is part of widget Meteoclimatic.
    Autor: Antonio Cristóbal Álvarez Abellán -> acabellan@gmail.com
    
    */

package deeloco.android.meteoclimatic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.widget.TextView;
import android.widget.Toast;

public class prevision extends Activity {
	
	String tag="previsiones";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prevision);
		
		WebView webView = (WebView) findViewById(R.id.wvMapa);
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
		webView.loadUrl("http://www.meteoclimatic.com/bot/previsio.jpg?");
		
		TextView tw_prevision=(TextView )findViewById(R.id.tw_prevision);

		descargar_fichero txtPrevision=new descargar_fichero(this, "http://www.meteoclimatic.com/bot/", "previ_es.txt", "/sdcard/meteoclimatic/");
		if (txtPrevision.download())
		{
			tw_prevision.setText(textoPrevision("/sdcard/meteoclimatic/previ_es.txt"));
		}
		else
		{
			Toast.makeText(getBaseContext(),R.string.fallo_conexion,Toast.LENGTH_LONG).show();
			
		}
		
		
	}
	
	
	private String textoPrevision(String path)
	{
		String retorno="<div class=\"hprevi\"><h3>&iquest;Qu&eacute; tiempo har&aacute;?</h3><h4>Pron&oacute;stico confeccionado el 10/07/2011 - 08:00 UTC</h4><div class=\"mapaprevi\"><img src=\"/bot/previsio.jpg?\" border=\"0\" alt=\"\" /></div><div class=\"text\">Pocos cambios en el tiempo de hoy. Un calor soportable, aunque con bochorno en muchas regiones, y pocas nubes en general. Las que haya, estar&aacute;n por el norte peninsular sobre todo. Vientos del este o del norte, pero flojos m&aacute;s bien.<br /><br />Ser&aacute; un d&iacute;a nublado en general por As Mari&ntilde;as gallegas, el Cant&aacute;brico o el norte de provincias como Burgos y Palencia. De vez en cuando se producir&aacute; alguna lluvia local, m&aacute;s probable por el mar.<br /><br />Algunas nubes bajas se ir&aacute;n colocando por el Mediterr&aacute;neo, sobre todo en Catalunya, Castell&oacute;n y Valencia. Tambi&eacute;n en el resto de Galicia, las costas de Portugal o en algunas zonas cercanas al mar de Baleares. Nubes altas por las costas mediterr&aacute;neas andaluzas, si acaso, con algunas gotas de barro en la costa o en Melilla.<br /><br />Esta tarde se formar&aacute;n tambi&eacute;n algunos nubarrones en Cazorla, El R&iacute;o Mundo, el Maestrazgo-G&uacute;dar o en el Pirineo, que dejar&aacute;n chubascos aislados. Por lo dem&aacute;s seguir&aacute; el sol.<br /><br />Temperaturas de hasta 35 o 36 grados, no m&aacute;s altas, en el interior de Murcia, de Andaluc&iacute;a o en Extremadura. En general estaremos entre los 26 y los 33, m&aacute;s bajas en el Cant&aacute;brico, con m&aacute;ximas que poco pasar&aacute;n de los 20 grados.<br /><br />El viento soplar&aacute; de levante en muchas costas, con solano en el sur espa&ntilde;ol, aunque flojo. Ser&aacute;n del nordeste o del norte en el Cant&aacute;brico, Galicia y las costas portuguesas. Marejada por Galicia, el Estrecho o Canarias.<br /><br /><br />Departamento de Pron&oacute;stico de Meteoclimatic</div></div><script type=\"text/javascript\">document.getElementById(\"previ\").style.display=\"none\";</script>";
		retorno="";
		try
		{
		
		File f = new File(path);
		FileInputStream fileIS = new FileInputStream(f);
		BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
		String readString = new String();
		int indice=0;
		
		 
		// lee cada línea
		boolean swCargarLinea=true;
		while((readString = buf.readLine())!= null)
			{
				readString=readString.trim();
				//Hora y fecha de la toma de los datos
				
				if (readString.indexOf("div class=\"mapaprevi\"")>-1)
				{
					//Dejamos de cargar datos
					swCargarLinea=false;
				}
				if (readString.indexOf("</div>")>-1)
				{
					//Volvemos a cargar los datos
					swCargarLinea=true;
				}
				if (readString.indexOf("<script type=\"text/javascript\">")>-1)
				{
					//Volvemos a cargar los datos
					swCargarLinea=false;
				}
				
				//Datos meteorológicos
				if (swCargarLinea)
				{
					//La siguiente linea tiene los datos
					retorno+=readString;
				}
		    }
		fileIS.close();
		}
		catch (FileNotFoundException e)
		{
			 
			//e.printStackTrace();
			Log.d(tag, "Error :"+e.getMessage());
			return null;
			 
		} 
		catch (IOException e)
		{
			 
			//e.printStackTrace();
			Log.d(tag, "Error :"+e.getMessage());
			return null;
			 
		}

		return ""+Html.fromHtml(retorno);
	}
	
	
	

}

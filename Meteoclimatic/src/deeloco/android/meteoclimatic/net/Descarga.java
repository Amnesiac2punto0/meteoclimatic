package deeloco.android.meteoclimatic.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Clase que se encarga de descargar datos de intenet
 * @author aalvarezabellan
 *
 */
public class Descarga {
	
	
	public void descargas()
	{
		
	}
	
	
	/**
	 * Descarga una imagen de una URL y la convierte a drawable.
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Drawable drawableFromUrl(String url) throws IOException 
	{
		if (url!=null)
		{
			Log.d("Descargas", "Descargando Imagen "+url);
		    Bitmap x;
	
		    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		    connection.connect();
		    InputStream input = connection.getInputStream();
	
		    x = BitmapFactory.decodeStream(input);
		    return new BitmapDrawable(x);
		}
		
		return null;
	}
	
	

}

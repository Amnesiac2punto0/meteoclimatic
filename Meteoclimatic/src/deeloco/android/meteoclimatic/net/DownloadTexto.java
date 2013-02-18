package deeloco.android.meteoclimatic.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import org.apache.http.util.ByteArrayBuffer;

import deeloco.android.meteoclimatic.R;
import android.util.Log;
import android.widget.Toast;

public class DownloadTexto extends Download{
	
	private String texto;
	private String TAG="DownloadTexto";
	private String error=null;

	/**
	 * Constructor de la clase
	 * @param url
	 */
	public DownloadTexto(String url)
	{
		super(url);
	}

	/**
	 * Función que devuelve el error de la última descarga
	 * @return the error
	 */
	public String getError() {
		return this.error;
	}

	/**
	 * Asigna un valor al error de la clase
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	public String getData() {
		// TODO Auto-generated method stub
		startDownload();
		return texto;
	}

	/**
	 * Descarga un texto
	 */
	public void startDownload() {
		// TODO Auto-generated method stub
		Charset iso88591charset = Charset.forName("ISO-8859-1");
		Charset UTF8charset = Charset.forName("UTF-8");
		if (this.urlDownload!=null)
		{
			try
			{
				Log.d(TAG,"Inicio descarga de datos..."+this.urlDownload);
				
				URL url = new URL(this.urlDownload);
				URLConnection connection = url.openConnection();
				connection.setConnectTimeout(timeOutConnection);
				connection.setReadTimeout(timeOutRead);
				
				HttpURLConnection httpConnection=(HttpURLConnection) connection;
				
				int responseCode=httpConnection.getResponseCode();
				Log.d(TAG,"Conexi�n HTTP ..."+responseCode);
				
				if (responseCode==HttpURLConnection.HTTP_OK)
				{
					
					InputStream input = connection.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(input);
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
		            int current = 0;
		            while ((current = bis.read()) != -1) 
		            {
		            	baf.append((byte) current);
		            }
		            
		            // Convert the Bytes read to a String. 
		            //this.texto = new String(baf.toByteArray(),UTF8charset);
		            this.texto = new String(baf.toByteArray());
				}
			}
			
			catch (UnknownHostException ue)
			{
				throw new RuntimeException(ue);
			}
	
			catch (SocketTimeoutException to)
			{
				throw new RuntimeException(to);
			}
			
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			this.texto="";
		}
	}

	/**
	 * Guarda el texto en la SD
	 */
	@Override
	public boolean guardarDatosSD(String pathSD) {
		// TODO Auto-generated method stub
		boolean retorno=false;
		
		try
		{
			startDownload();
			File file = new File(pathSD);
			FileOutputStream fos = new FileOutputStream(file);   
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
            myOutWriter.write(this.texto);
            myOutWriter.close();
            fos.close();
            retorno=true;
		}
		catch (UnknownHostException ue)
		{
			Log.e("guardarDatosSD","UnknownHostException .. "+ue.getMessage());
			this.error="Dirección de descarga desconocida";
			throw new RuntimeException(ue);
		}

		catch (SocketTimeoutException to)
		{
			Log.e("guardarDatosSD","SocketTimeoutException .. "+to.getMessage());
			this.error="Tiempo excedido. Es posible que el servdor esté caido";
			throw new RuntimeException(to);
		}
		
		catch (Exception e)
		{
			Log.e("guardarDatosSD","Exception .."+e.getMessage()+"..."+e.getClass());
			this.error="Error en la descarga de los datos.";
			throw new RuntimeException(e);
			
		}
		
		return retorno;
	}

	
}

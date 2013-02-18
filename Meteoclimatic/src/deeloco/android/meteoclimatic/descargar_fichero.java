/*(C) Copyright

    This file is part of widget Meteoclimatic.
    Autor: Antonio Cristóbal Álvarez Abellán -> acabellan@gmail.com
    
    */

package deeloco.android.meteoclimatic;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class descargar_fichero {
	
	private Context contexto;
	private static final int READ_TIME_OUT=3000;
	private static final int CONNECT_TIME_OUT=3000;
	
	
	private String tag="meteoclimatic.descargar_fichero";
	
	/**
	 * URL del fichero que se quiere descargar
	 */
	private String URL;
	
	/**
	 * nombre del fichero que se quiere descargar
	 */
	private String nombreFichero;
	
	/**
	 * path de la tarjeta SD donde se va a guardar el fichero descargado.
	 * Se guardara con el mismo nombre que tenga en la descarga
	 */
	private String pathSD;
	
	/**
	 * Get del atributo URL del fichero que se quiere descargar.
	 * @return
	 * String URL
	 */
	public String getURL() {
		return URL;
	}
	
	/**
	 * Set del atributo URL del fichero que se quiere descargar.
	 * @param 
	 * String URL
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}
	
	/**
	 * Get del atributo pathSD, path donde se guardara el fichero descargado
	 * @return
	 * String pathSD
	 */
	public String getPathSD() {
		return pathSD;
	}
	
	/**
	 * Set del atributo pathSD, path donde se guardara el fichero descargado
	 * @param 
	 * String pathSD
	 * 
	 */
	public void setPathSD(String pathSD) {
		this.pathSD = pathSD;
	}

	/**
	 * Get del atributo nombreFichero 
	 * @return
	 * String nombreFichero
	 */
	public String getNombreFichero() {
		return nombreFichero;
	}

	/**
	 * Set del atributo nombreFichero
	 * @param
	 * String nombreFichero
	 * 
	 */
	public void setNombreFichero(String nombreFichero) {
		this.nombreFichero = nombreFichero;
	}

	/**
	 * Constructor
	 * @param uRL
	 * @param nombreFichero
	 * @param pathSD
	 */
	public descargar_fichero(Context contexto,String uRL, String nombreFichero, String pathSD) {
		super();
		URL = uRL;
		this.nombreFichero = nombreFichero;
		this.pathSD = pathSD;
		this.contexto=contexto;
		
		String path=this.pathSD+this.nombreFichero;
		//Comprobamos si el fichero esta creado. Si es que no, se crea.
    	File f=new File(path);
    	if (!f.exists())
    	{
    		//El fichero no existe, hay que crearlo
    		try{
    			boolean dir = new File(this.pathSD).mkdir(); //Creamos el directorio
    		}
    		catch (Exception e){ //Error al crear el directorio o el fichero
    			Log.e(tag,"Error al crear el directorio: " + e.getMessage());
    		}
    	}
		
		
		
	}
	
	/**
	 * Realiza la descarga del fichero en la URL especificada y la guarda en pathSD
	 * @return
	 * Boolean, 'true' si la descarga se ha realizado correctamente y 'false' si ha habido un error.
	 */
	public boolean download()
	{
		boolean retorno=true;

		if (HaveNetworkConnection())
		//if (false)
		{
			try
			{
				URL url = new URL(this.URL+this.nombreFichero);
				File file = new File(this.pathSD+this.nombreFichero);
				long startTime = System.currentTimeMillis();
				
				/* Abrir una conexión a la URL. */
	            URLConnection ucon = url.openConnection();
	            /* Configuración time out */
	            ucon.setReadTimeout(READ_TIME_OUT);
	            ucon.setConnectTimeout(CONNECT_TIME_OUT);
	            /*
	             * Define InputStreams para leer desde el URLConnection.
	             */
	            InputStream is = ucon.getInputStream();
	            BufferedInputStream bis = new BufferedInputStream(is);
	            /*
	             * Leer bytes del buffer hasta que no haya más que leer (-1)
	             */
	            
	            ByteArrayBuffer baf = new ByteArrayBuffer(50);
	            int current = 0;
	            while ((current = bis.read()) != -1) 
	            {
	            	baf.append((byte) current);
	            }
	            /* Convert the Bytes read to a String. */
	            FileOutputStream fos = new FileOutputStream(file);
	            fos.write(baf.toByteArray());
	            fos.close();
	            Log.d(tag, "Descarga realizada en "+ ((System.currentTimeMillis() - startTime) / 1000)+ " sec, de"+url);
	
			}
			catch (SocketTimeoutException e)
			{
				retorno=false;
				Log.d(tag, "Error TimeOut al descargar el fichero "+this.nombreFichero+" : "+e.getMessage());
			}
			catch (IOException e)
			{
				//Error en la descarga del fichero
				retorno=false;
				Log.d(tag, "Error al descargar el fichero "+this.nombreFichero+" : "+e.getMessage());
			}
			return retorno;
		}
		else
		{
			//Error en la descarga porque no hay conexión a Internet
			Log.d(tag, "Sin Conexión");
			retorno=false;
		}
		return retorno;
	}
	
	private boolean HaveNetworkConnection()
	{
	    boolean HaveConnectedWifi = false;
	    boolean HaveConnectedMobile = false;
	    
	    
	    //contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
	    
	    //ConnectivityManager cm = meteoclimatic.getAppConnectivityManager();
	    ConnectivityManager cm =(ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo)
	    {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	            {
	                HaveConnectedWifi = true;
	                Log.d(tag, "Conexión Wifi Activa");
	                
	            }
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	            {
	                HaveConnectedMobile = true;
	                Log.d(tag, "Conexión Mobile Activa");
	            }
	    }
	    if (!(HaveConnectedWifi || HaveConnectedMobile))
	    {
	    	Log.d(tag, "Sin Conexión Activa");
	    }
	    return HaveConnectedWifi || HaveConnectedMobile;
	}
	
	
	public boolean existeFichero(String nombreFichero, String pathSD)
	{
		
		String path=pathSD+nombreFichero;
    	File f=new File(path);
    	return f.exists();
   	}
	

}

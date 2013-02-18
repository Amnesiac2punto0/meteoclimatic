/*(C) Copyright

    This file is part of widget Meteoclimatic. 
    Autor: Antonio Cristóbal Álvarez Abellán -> acabellan@gmail.com
    
    */

package deeloco.android.meteoclimatic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import deeloco.android.meteoclimatic.net.DownloadTexto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.util.Log;

/**
 * Clase que permite gestionar los datos meteorológicos de una estación.
 * @author Antonio Cristóbal Álvarez Abellán
 *
 */

public class estacion {
	
	private String tag="meteoclimatic.estacion";
	private String preURL="http://meteoclimatic.com/feed/rss/";
	private String pathSD="/sdcard/meteoclimatic/";
	private DownloadTexto dt;
	
	/**
	 * ATRIBUTOS
	 */
	private String nombre; //Nombre de la estación
	private String codigo; //Código de la estación
	private datosMeteorologicos datosMeteo = new datosMeteorologicos(); //Datos meteorológicos
	private datosMeteorologicos datosMeteoPrev=new datosMeteorologicos(); //Datos meteorológicos previos
	private Context contexto;
	
	
	/**
	 * CONSTRUCTORES
	 */
	
	public estacion(Context contexto,String nombre, String codigo) {
		this.contexto=contexto;
		this.nombre = nombre;
		this.codigo = codigo;
		if (!crearFichero())
			setEstado(false);
	}
	
	public estacion() {
		this.nombre = "-- ------";
		this.codigo = "";
	}
	
	public estacion(Context contexto){
		this.contexto=contexto;
		this.nombre = "-- ------";
		this.codigo = "";
	}
	
	
	/**
	 * GETTER Y SETTER
	 */
	
	public void setContexto(Context contexto){
		this.contexto=contexto;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
		if (!crearFichero())
			setEstado(false);
	}
	public datosMeteorologicos getDatosMeteo() {
		return datosMeteo;
	}
	public void setDatosMeteo(datosMeteorologicos datosMeteo) {
		this.datosMeteo = datosMeteo;
	}
	public datosMeteorologicos getDatosMeteoPrev(){
		return this.datosMeteoPrev;
	}
	public void setEstado(boolean activa)
	{
		this.datosMeteo.activa=activa;
	}


	/**
	 * MÉTODOS
	 */
	
	
	/**
	 * Constructor
	 * @param uRL
	 * @param nombreFichero
	 * @param pathSD
	 */
	private boolean crearFichero() 
	{
		boolean retorno=false;
		String path=this.pathSD+this.codigo;
		//Comprobamos si el fichero esta creado. Si es que no, se crea.
    	File f=new File(path);
    	if (!f.exists())
    	{
    		//El fichero no existe, hay que crearlo
    		try
    		{
    			boolean dir = new File(this.pathSD).mkdir(); //Creamos el directorio
    			retorno=true;
    		}
    		catch (Exception e)
    		{ //Error al crear el directorio o el fichero
    			Log.e(tag,"Error al crear el directorio: " + e.getMessage());
    		}
    	}
    	return retorno;
	}
	
	/**
	 * Captura los datos meteorológicos del fichero que se ha descargado.
	 * El fichero debe estar en la carpeta meteoclimatic que esta en la SD
	 * El nombre del fichero será el código de la estación
	 * @return
	 * Retorna 'true' si se ha capturado todos los datos y 'false' si no se ha podido capturar los datos.
	 */

	public boolean descargarCapturarDatos()
	{
		
		//Abrir el fichero con nombre this.nombre;
		boolean retorno=true;
		
		if (this.codigo!=null)
        {
            if (descargarDatos())
            {
            	//Si la descarga es correcta
            	//Ahora hay que analizar el fichero descargado para obtener los datos meteorológicos
            	obtenerDatos od=new obtenerDatos();
            	datosMeteorologicos datosMeteo=null;
            	datosMeteo=od.getDatosMeteo(pathSD+this.codigo);
            	Log.d(tag,"DatosMeteo T. Antes="+this.datosMeteo.T);
            	this.datosMeteoPrev=this.datosMeteo;
            	this.datosMeteo=datosMeteo;
            	Log.d(tag,"DatosMeteo T. Despues="+this.datosMeteo.T);
            	
            }
            else
            {
            	//Error en la descarga
            	retorno=false;
            }
        }
        else
        {
        	//Ahora hay que analizar el fichero descargado para obtener los datos meteorológicos
        	obtenerDatos od=new obtenerDatos();
        	datosMeteorologicos datosMeteo=null;
        	datosMeteo=od.getDatosMeteo(pathSD+this.codigo);
        	this.datosMeteoPrev=this.datosMeteo;
        	this.datosMeteo=datosMeteo;
        }
		
		//Extrear los datos meteorológicos
		
		return retorno; 

	}
	
	/**
	 * Descarga los datos meteorológicos de meteoclimatic y almacena el RSS en la tarjeta SD.
	 * @return
	 * <true> si la descarga ha sido correcta y <false> si se ha producido algún fallo.
	 */
	public boolean descargarDatos()
	{
//        descargar_fichero downFile=new descargar_fichero(this.contexto,preURL, this.codigo, pathSD);
//        return downFile.download();

        String urlRSSDatosEstacion=this.preURL+this.codigo;
        dt=new DownloadTexto(urlRSSDatosEstacion);
		dt.setTimeOutConnection(3000);
		dt.setTimeOutRead(3000);
		return dt.guardarDatosSD(this.pathSD+this.codigo);
	}
	
	/**
	 * Captura los datos meteorológicos del fichero RSS que se encuentra en la tarjeta SD
	 * @return
	 * <true> si la captura ha sido correcta y <false> si se ha producido algún fallo, como la no existencia del fichero.
	 */
	public boolean capturarDatos()
	{
		boolean retorno=true;
		//Comprobamos que la clase tiene código
		if (this.codigo!=null)
        {
			//Comprobamos si el fichero esta creado. Si es que no, se crea.
			String path=this.pathSD+this.codigo;
	    	File f=new File(path);
	    	if (!f.exists())
	    	{
	    		//Fichero no existe. Cargamos unos valores por defecto
	    		datosMeteorologicos datosMeteo=new datosMeteorologicos();
            	this.datosMeteo=datosMeteo;
            	retorno=false;
	    	}
	    	else
	    	{
	    		//El fichero existe, cargamos los valores del mismo
            	//Analizar el fichero para obtener los datos meteorológicos
            	obtenerDatos od=new obtenerDatos();
            	datosMeteorologicos datosMeteo=null;
            	datosMeteo=od.getDatosMeteo(pathSD+this.codigo);
            	this.datosMeteo=datosMeteo;
	    		
	    	}
        }
		return retorno; 
	}
	
	
	
	/**
	 * Retorna el bitmap del icono que representa el estado del cielo
	 * @return
	 * Bitmap del icono
	 */
	public Bitmap getIcono()
	{
		String nombreIcono=this.datosMeteo.estadoCielo;
		
		Bitmap retorno=null;
		String pathNombre=pathSD+nombreIcono+".png";
    	File f=new File(pathNombre);
    	if(f.exists())
    	{
    		//Existe el fichero, devolvemos el bitmap del SD
	       BitmapFactory.Options options = new BitmapFactory.Options();
	       options.inSampleSize = 1;
	       try
	       {
	    	   retorno = BitmapFactory.decodeFile(pathNombre, options).copy(Config.ARGB_8888, true);
	       }
	       catch (Exception e)
	       {
	    	   retorno=BitmapFactory.decodeResource(contexto.getResources(), R.drawable.icon);
	       }
    	}
    	else
    	{
    		//No existe el fichero, lo descargamos a la SD, para que este disponible para la próxima vez
    		Log.d(tag,"Descargando icono");
    		String URLIcono="http://www.meteoclimatic.com/ico/64x64/";
    		descargar_fichero downloadFile=new descargar_fichero(contexto,URLIcono , nombreIcono+".png", pathSD);
    		if (downloadFile.download())
    		{
    			//La descarga se ha llevado a cabo
		       BitmapFactory.Options options = new BitmapFactory.Options();
		       options.inSampleSize = 1;
		       retorno = BitmapFactory.decodeFile(pathNombre, options);     			
    		}
    		else
    		{
    			//Error en la descargar, pasarle el icono de meteoclimatic
    			retorno=BitmapFactory.decodeResource(contexto.getResources(), R.drawable.icon);
    		}

    	}
		return retorno;
		
	}
	
	/**
	 * Calcula el incremento de la lluvia entre los datos meteorológicos actuales y los previos
	 * @return
	 * double incremento de la lluvia
	 */
	public double incrementoLluvia()
	{
	    //--- Redondear un número con x decimales
	    //public static double redondear(double numero, int decimales ) {
	    //    return Math.round(numero*Math.pow(10,decimales))/Math.pow(10,decimales);
	    //}
		//return this.datosMeteo.P-this.datosMeteoPrev.P;
		
		double numero=this.datosMeteo.P-this.datosMeteoPrev.P;
		int decimales=2;
		return Math.round(numero*Math.pow(10,decimales))/Math.pow(10,decimales);
	}

	
}

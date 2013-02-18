/*(C) Copyright

    This file is part of widget Meteoclimatic.
    Autor: Antonio Cristóbal Álvarez Abellán -> acabellan@gmail.com
    
    */

package deeloco.android.meteoclimatic;

import java.io.*;
import java.util.ArrayList;

import android.text.Html;
import android.util.Log;


/**
 * Clase que extra información de un fichero.
 * @author Antonio Cristóbal Álvarez Abellán
 *
 */
public class obtenerDatos {
	
	String tag="meteoclimatic.obtenerDatos";
	
	/**
	 * ATRIBUTOS
	 */
	
	private ArrayList <String> nomEstaciones=new ArrayList <String>(); //Nombre de las estaciones extraidas
	private ArrayList <String> codEstaciones=new ArrayList <String>(); //Código de las estaciones extraidas
	
	
	/**
	 * GETTER
	 */
	
	public String[] getNomEstaciones() 
	{
		String arrayNomEstaciones[] = new String[nomEstaciones.size()];
		nomEstaciones.toArray(arrayNomEstaciones);
		return arrayNomEstaciones;
	}
	
	public String[] getCodEstaciones() 
	{
		String arrayCodEstaciones[] = new String[codEstaciones.size()];
		codEstaciones.toArray(arrayCodEstaciones);
		return arrayCodEstaciones;
	}
	
	
	/**
	 * MÉTODOS
	 */
	
	
	
	public obtenerDatos() {
		super();
	}
	/**
	 * Dado el nombre de una fichero, extrae los nombre de las estaciones y los códigos, el resultado los mete en nomEstaciones[] y codEstaciones[]
	 * 
	 */
	public boolean getEstaciones(String nomFichero)
	{
		
		try
		{
				
			File f = new File(nomFichero);
			FileInputStream fileIS = new FileInputStream(f);
			//is.setEncoding("ISO-8859-1"); 
			BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
			String readString = new String();
			int indice=0;
			String codigo="";
			String nombre="";
			 
			// lee cada línea
			 
			while((readString = buf.readLine())!= null)
				{
					readString=readString.trim();
					//Extraemos el nombre de la estación
					if (readString.indexOf("<title>")>-1)
					{
						//Es un título
						nombre=readString.substring(7, readString.length()-8);
						
					}
					
					//Extraemos el código de la estación
					if (readString.indexOf("<link>")>-1)
					{
						//Es un enlace, que tiene el código de la estación
						codigo=readString.substring(readString.length()-26, readString.length()-7);
						//nomEstaciones.add(nombre);
						nomEstaciones.add(""+Html.fromHtml(""+nombre));
						//codEstaciones.add(codigo);
						codEstaciones.add(""+Html.fromHtml(""+codigo));
						indice++;
					}
					
			    }
			fileIS.close();
			 
		} 
		catch (FileNotFoundException e)
		{
			 
			//e.printStackTrace();
			Log.d(tag, "Error :"+e.getMessage());
			return false;
			 
		} 
		catch (IOException e)
		{
			 
			//e.printStackTrace();
			Log.d(tag, "Error :"+e.getMessage());
			return false;
			 
		}
		
		return true;
	}
	
	/**
	 * Método que extrae datos meteorológicos de un fichero y lo devuelve.
	 * @param nomFichero
	 * Nombre del fichero donde se encuentra los datos meteorológicos
	 * @return
	 * Retorna la clase datosMeteorologicos con los datos extraidos del fichero
	 */
	public datosMeteorologicos getDatosMeteo(String nomFichero)
	{
		datosMeteorologicos retorno=new datosMeteorologicos();
		String lineaDatos="";
		String lineaFechaHora="";
		
		try
		{
				
			File f = new File(nomFichero);
			FileInputStream fileIS = new FileInputStream(f);
			//is.setEncoding("ISO-8859-1"); 
			BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
			String readString = new String();
			int indice=0;
			
			 
			// lee cada línea
			 
			while((readString = buf.readLine())!= null)
				{
					readString=readString.trim();
					//Hora y fecha de la toma de los datos
					
					if (readString.indexOf("Actualizado")>-1)
					{
						//Esta línea tiene los datos de fecha y hora
						lineaFechaHora=readString.substring(18, readString.length()-5);
					}
					
					//Datos meteorológicos
					if (readString.indexOf("[[<BEGIN:")>-1)
					{
						//La siguiente linea tiene los datos
						readString = buf.readLine();
						lineaDatos=readString.substring(23, readString.length()-3);
						break;	
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
		
		//Parsear la cadena 'lineaDatos' y extraer los datos meteorológicos
		
		//String colores = "rojo,amarillo,verde,azul,morado,marrón";
		String[] bloquesDatos = lineaDatos.split(";");
		String[] bloquesFechaHora = lineaFechaHora.split(" ");
		 
		// 
		/**
		 * Ya tenemos un array donde cada elemento es un bloque de datos
		 * (T;Tmax;Tmin);(H;Hmax;hmin);(B;Bmax;Bmin);(W;Wmax;Dir);(P);Nombre_estación>
		 */
		for (int i = 0; i < bloquesDatos.length; i++) {
			
			//Quitamos parentesis
			if (bloquesDatos[i].contains("("))
			{
				bloquesDatos[i]=bloquesDatos[i].substring(1, bloquesDatos[i].length());
			}
			if (bloquesDatos[i].contains(")"))
			{
				bloquesDatos[i]=bloquesDatos[i].substring(0, bloquesDatos[i].length()-1);
			}
			bloquesDatos[i] =bloquesDatos[i].replace(",", ".");
		}
		
		if (lineaDatos!="")
		{
			retorno.T=Double.parseDouble(bloquesDatos[0]);
			retorno.Tmax=Double.parseDouble(bloquesDatos[1]);
			retorno.Tmin=Double.parseDouble(bloquesDatos[2]);
			retorno.estadoCielo=bloquesDatos[3];
			retorno.H=Double.parseDouble(bloquesDatos[4]);
			retorno.Hmax=Double.parseDouble(bloquesDatos[5]);
			retorno.Hmin=Double.parseDouble(bloquesDatos[6]);
			retorno.B=Double.parseDouble(bloquesDatos[7]);
			retorno.Bmax=Double.parseDouble(bloquesDatos[8]);
			retorno.Bmin=Double.parseDouble(bloquesDatos[9]);
			retorno.W=Double.parseDouble(bloquesDatos[10]);
			retorno.Wmax=Double.parseDouble(bloquesDatos[11]);
			retorno.D=Double.parseDouble(bloquesDatos[12]);
			retorno.P=Double.parseDouble(bloquesDatos[13]);
			retorno.fecha=bloquesFechaHora[0];
			retorno.hora=bloquesFechaHora[1];
			retorno.activa=true;
		}
		else
		{
			//La estación no esta activa porque el fichero RSS existe, pero no tiene datos
			retorno.activa=false;
		}
		return retorno;
	}
	
	

}

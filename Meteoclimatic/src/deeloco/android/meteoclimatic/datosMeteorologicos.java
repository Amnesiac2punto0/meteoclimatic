/*(C) Copyright

    This file is part of widget Meteoclimatic.
    Autor: Antonio Cristóbal Álvarez Abellán -> acabellan@gmail.com
    
    */

package deeloco.android.meteoclimatic;

/**
 * Clase que describe los datos meteorológico que nos podemos encontrar en una estación
 * @author Antonio Cristóbal Álvarez Abellán
 *
 */

public class datosMeteorologicos {
	/**
	 * Temperatura
	 */
	double T;
	/**
	 * Temperatura Máxima
	 */
	double Tmax;
	/**
	 * Temperatua Mínima
	 */
	double Tmin;
	/**
	 * Humedad
	 */
	double H;
	/**
	 * Humedad Máxima
	 */
	double Hmax;
	/**
	 * Humedad Mínima
	 */
	double Hmin;
	/**
	 * Barómetro (Presión atmosférica)
	 */
	double B;
	/**
	 * Barómetro Máximo
	 */
	double Bmax;
	/**
	 * Barómetro Mínimo
	 */
	double Bmin;
	/**
	 * Wind (Viento)
	 */
	double W;
	/**
	 * Viento Máximo
	 */
	double Wmax;
	/**
	 * Precipitación
	 */
	double P;
	
	/**
	 * Dirección del viento
	 */
	double D;
	
	/**
	 * Fecha de la toma de los datos 
	 */
	String fecha;
	
	/**
	 * Hora en que se tomaron los datos
	 */
	String hora;
	
	/**
	 * Si la estación está operativa o no
	 */
	boolean activa;
	
	/**
	 * Nombre del icono que identifica el estado del cielo.
	 */
	String estadoCielo;
	
	
	public datosMeteorologicos()
	{
		this.B=0.0;
		this.Bmax=0.0;
		this.Bmin=0.0;
		this.H=0.0;
		this.Hmax=0.0;
		this.Hmin=0.0;
		this.P=0.0;
		this.T=0.0;
		this.Tmax=0.0;
		this.Tmin=0.0;
		this.W=0.0;
		this.Wmax=0.0;
		this.D=0.0;
		this.estadoCielo="sun";
	}
	
	/**
	 * Convierte el atributo D (Dirección del viento, en grados) en puntos cardinales (N,S,E,W).
	 * @return
	 * String con los puntos cardinales 
	 */
	public String getDirViento()
	{
		if ((this.D>337.5)||(this.D<22.5)) return "N";
		if ((this.D>22.5)&&(this.D<67.5)) return "NE";
		if ((this.D>67.5)&&(this.D<112.5)) return "E";
		if((this.D>112.5)&&(this.D<157.5)) return "SE";
		if((this.D>157.5)&&(this.D<202.5)) return "S";
		if((this.D>202.5)&&(this.D<247.5)) return "SO";
		if((this.D>247.5)&&(this.D<292.5)) return "O";
		if((this.D>292.5)&&(this.D<337.5)) return "NO";

		return "";

	}
	
	
//	public boolean parserRSSMeteoclimatic(String RSSMeteoclimatic)
//	{
//		String lineaDatos="";
//		String lineaFechaHora="";
//		
//		if (RSSMeteoclimatic.indexOf("[[<BEGIN:")>-1)
//					{
//						//La siguiente linea tiene los datos
//						String[] bloquesDatos = RSSMeteoclimatic.split(";");	
//						/**
//						 * Ya tenemos un array donde cada elemento es un bloque de datos
//						 * ...;(T;Tmax;Tmin);(H;Hmax;hmin);(B;Bmax;Bmin);(W;Wmax;Dir);(P);Nombre_estación>...
//						 */
//						int bloqueInicio=0;
//						for (int i = 0; i < bloquesDatos.length; i++) {
//							
//							//Quitamos parentesis
//							if (bloquesDatos[i].contains("(")) bloqueInicio=i;
//							if (bloqueInicio>0)
//							{
//								if (bloquesDatos[i].contains("]]"))
//								{
//									break; //Salimos del for
//								}
//								if (bloquesDatos[i].contains("("))
//								{
//									bloquesDatos[i]=bloquesDatos[i].substring(1, bloquesDatos[i].length());
//								}
//								if (bloquesDatos[i].contains(")"))
//								{
//									bloquesDatos[i]=bloquesDatos[i].substring(0, bloquesDatos[i].length()-1);
//								}
//								bloquesDatos[i] =bloquesDatos[i].replace(",", ".");
//							}
//						}
//						
//						if (lineaDatos!="")
//						{
//							this.T=Double.parseDouble(bloquesDatos[bloqueInicio]);
//							this.Tmax=Double.parseDouble(bloquesDatos[bloqueInicio+1]);
//							this.Tmin=Double.parseDouble(bloquesDatos[bloqueInicio+2]);
//							this.estadoCielo=bloquesDatos[bloqueInicio+3];
//							this.H=Double.parseDouble(bloquesDatos[bloqueInicio+4]);
//							this.Hmax=Double.parseDouble(bloquesDatos[bloqueInicio+5]);
//							this.Hmin=Double.parseDouble(bloquesDatos[bloqueInicio+6]);
//							this.B=Double.parseDouble(bloquesDatos[bloqueInicio+7]);
//							this.Bmax=Double.parseDouble(bloquesDatos[bloqueInicio+8]);
//							this.Bmin=Double.parseDouble(bloquesDatos[bloqueInicio+9]);
//							this.W=Double.parseDouble(bloquesDatos[bloqueInicio+10]);
//							this.Wmax=Double.parseDouble(bloquesDatos[bloqueInicio+11]);
//							this.D=Double.parseDouble(bloquesDatos[bloqueInicio+12]);
//							this.P=Double.parseDouble(bloquesDatos[bloqueInicio+13]);
//							this.fecha=bloquesFechaHora[0];
//							this.hora=bloquesFechaHora[1];
//							this.activa=true;
//						}
//						else
//						{
//							//La estación no esta activa porque el fichero RSS existe, pero no tiene datos
//							this.activa=false;
//						}
//						return retorno;
//						
//					}
//	}

}

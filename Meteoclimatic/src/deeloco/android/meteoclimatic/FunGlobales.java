/*(C) Copyright

    This file is part of GastosMovil.
    
    GastosMovil is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GastosMovil is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GastosMovil.  If not, see <http://www.gnu.org/licenses/>.
    
    Autor: Antonio Cristóbal Álvarez Abellán -> acabellan@gmail.com
    
    */

package deeloco.android.meteoclimatic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

public class FunGlobales extends Activity{
	
	
	/**
	 * Redondear un número con x decimales
	 * @param numero
	 * @param decimales
	 * @return
	 */
    public static double redondear(double numero, int decimales ) {
        return Math.round(numero*Math.pow(10,decimales))/Math.pow(10,decimales);
    }

    /**
     * Hay que devolver el texto del mes de Inicio y el siguiente, si mesInicio es Diciembre el siguiente es Enero
     * @param meses
     * @param mesInicio
     * @param diaInicio
     * @return
     */
    public static String periodo(String meses[],int mesInicio,int diaInicio,boolean doslineas){
    	
    	
    	String retorno="";
    	String saltoDeLinea=(doslineas)?"\n":"";
    	if (diaInicio==1)
    	{
    		retorno =meses[mesInicio];
    	}
    	else
    	{
    		
    		Calendar calendario= Calendar.getInstance();
    	    int mDia = calendario.get(Calendar.DAY_OF_MONTH);
    		Log.d("Periodo", "Hoy es " + mDia + ". mesInicio="+mesInicio+". diaInicio="+diaInicio+saltoDeLinea+"Otra línea");
    		//calendario.
    		if (mDia<diaInicio)
    		{
    			mesInicio=(mesInicio==2)?13:mesInicio--;
    		}
    		
    		if (mesInicio<13)
    		{
    			retorno= meses[mesInicio].substring(0, 3)+"/"+saltoDeLinea+meses[mesInicio+1].substring(0,3);	
    		}
    		else
    		{
    			retorno= meses[13].substring(0,3)+"/"+saltoDeLinea+meses[2].substring(0, 3);
    		}
    		
    	}
    	return retorno;	
    }
    
    /**
     * Devuelve el símbolo de la moneda local
     * ESTA MAL. SOLO DEVUELVE € o $
     * @return
     */
    public static String monedaLocal(){
    	Currency currency = Currency.getInstance(Locale.getDefault());
    	String codigoMoneda=currency.getCurrencyCode();
    	//Log.d("CODIGO MONEDA",codigoMoneda);
    	if (codigoMoneda.equals("EUR"))
    	{
    		return "€";
    		
    	}
    	if (codigoMoneda.equals("USS"))
    	{
    		return "$";
    		
    	}
    	return "€";
    }
    
    /**
     * Convierte segundos en una cadena de caracteres formateados horas, min. y seg.
     * @param totalsegundos
     * Segundos a formatear
     * @return
     * String con las horas, min. y seg. que corresponden a los segundos pasados como parámetros.
     */
    public static String segundosAHoraMinutoSegundo(int totalsegundos){
    	int horas=0;
    	int minutos=0;
    	int segundos=0;
    	String retorno="";
    	
    	minutos=totalsegundos/60;
    	segundos=totalsegundos%60;
    	
    	if (minutos>60)
    	{
    		horas=minutos/60;
    		minutos=minutos%60;
    		retorno=horas+ "h. "+minutos+"m. "+segundos+"s.";
    	}
    	else
    	{
    		retorno=minutos+"m. "+segundos+"s.";
    	}
    	
    	return retorno;
    }

    /**
     * Convierte segundos en una cadena de caracteres formateados en min. y seg.
     * @param totalsegundos
     * Segundos a formatear
     * @return
     * String con los min. y seg. que corresponden a los segundos pasados como parámetros.
     */

    public static String segundosAMinutoSegundo(int totalsegundos){
    	int minutos=0;
    	int segundos=0;
    	String retorno="";
    	minutos=totalsegundos/60;
    	segundos=totalsegundos%60;
    	retorno=minutos+"m. "+segundos+"s.";
    	return retorno;
    }
    
    
    /**
     * Comprueba si un Intent está disponible en el sistema 
     * @param context
     * @param action
     * @return boolean
     */

    public static boolean estaIntentDisponible(Context context, String action) 
    {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,0);
        return list.size() > 0;
    }
    
    /**
     * Comprueba si un Intent está disponible en el sistema
     * @param context
     * @param action
     * @param uri
     * @return boolean
     */

    public static boolean estaIntentDisponible(Context context, String action,Uri uri) 
    {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action,uri);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,0);
        return list.size() > 0;
    }
    
    /**
     * Comprueba si en Market esta instalado en el dispositivo
     * @param context
     * @return boolean
     * True si está instalado y False si no lo está.
     */
    public static boolean estaMarketInstalado(Context context)
    {
    	
    	final PackageManager packageManager = context.getPackageManager();
    	String packagename = context.getPackageName();
    	String url = "market://details?id=" + packagename;

    	Intent intentMarket = new Intent(Intent.ACTION_VIEW);
    	intentMarket.setData(Uri.parse(url));

    	List<ResolveInfo> list = packageManager.queryIntentActivities(intentMarket,PackageManager.MATCH_DEFAULT_ONLY);

    	return list.size() > 0;
    }
    
    /**
     * Dado una fecha devuelve una cadena con el día de la semana correspondiente.
     * @param fecha String con la fecha
     * @return String con el día de la semana.
     */
    public static String diaSemana(String fecha)
    {
    	String diasCortos[] = {"Lun.","Mar.","Mie.","Jue.","Vie.","Sab.","Dom."};
    	
    	String sFecha =fecha.substring(0, 10).trim();
		String sHora=fecha.substring(10, fecha.length()).trim();
		String sDia=sFecha.substring(0,2);
		String sMes=sFecha.substring(3, 5);
		String sAno=sFecha.substring(6, 10);

		int ano= Integer.parseInt(sAno);
		int mes= Integer.parseInt(sMes)-1;
		int dia= Integer.parseInt(sDia);
		
		Calendar calendario=new GregorianCalendar(ano,mes,dia);
		calendario.setFirstDayOfWeek(Calendar.MONDAY);
		int diaSemana=calendario.get(Calendar.DAY_OF_WEEK);
		String shora=sHora;
		
		switch (diaSemana) {
		case Calendar.SUNDAY:
			diaSemana=6;
			break;

		default:
			diaSemana=diaSemana-2;
			break;
		}


    	return diasCortos[diaSemana];
    }
    
    /**
     * Dado un numero de teléfono, le quita el prefijo del pais, si tiene. Si no tiene prefijo, devuelve el numero. 
     * @param numero String al que hay que quitar el prefijo, si tiene.
     * @return String con el numero sin el prefijo.
     */
    public static String quitarPrePais(String numero)
    {
    	if (numero.length()<12)
    		return numero;
    	
    	String retorno="";
    	String tres=numero.substring(0, 3);
    	String cuatro=numero.substring(0, 4);
    	
    	if (tres.equals("+34"))
    	{
    		retorno=numero.substring(3);
    	}
    	
    	if (cuatro.equals("0034"))
    	{
    		retorno=numero.substring(4);
    	}
    	return retorno;
    }
    
    /**
     * Dado un mes (numero) devuelve el mes anterior que le corresponda
     * @param mes mes actual
     * @return
     * 	intero que el mes anterior
     */
    public static int mesAnterior(int mes)
    {
    	int retorno=mes;
    	if (mes==1)
    	{
    		//Estamos en el primer mes, el mes anterio es diciembre=12
    		retorno=12;
    	}
    	else
    	{
    		retorno--;
    	}
    	return retorno;
    }
    
    
    /**
     * Dado un mes (numero) devuelve el mes siguiente que le corresponda
     * @param mes mes actual
     * @return
     * 	entero que sería el mes posterior
     */
    public static int mesPosterior(int mes)
    {
    	int retorno=mes;
    	if (mes==12)
    	{
    		//Estamos en el último mes, el mes posterior es enero=1
    		retorno=1;
    	}
    	else
    	{
    		retorno++;
    	}
    	return retorno;
    }
    
    /**
     * Dado dos fechas una con formato dd/MM/yyyy kk:mm:ss y otra de tipo long, devuelve la diferencia de meses entre la fecha uno y la dos
     * @param fechaUno
     * @param fechaDos
     * @return
     */
    public static int diferenciaMes(String fechaUno,long fechaDos)
    {
    	String sfechaDos=DateFormat.format("dd/MM/yyyy kk:mm:ss",new Date(fechaDos)).toString();
    	int mesUno=Integer.parseInt(fechaUno.substring(3, 5));
    	int mesDos=Integer.parseInt(sfechaDos.substring(3, 5));
    	return (mesDos-mesUno);
    	
    }
    
    /**
     * Devuelve si dos fechas estan en el mismo periodo o en periodo de facturación diferentes
     * @param fechaUno
     * @param fechaDos
     * @return
     */
    public static boolean diferentePeriodo(String fechaUno,long fechaDos,int diaFacturacion)
    {
    	boolean retorno=false;
    	try
    	{
	    	
	    	String sfechaDos=DateFormat.format("dd/MM/yyyy kk:mm:ss",new Date(fechaDos)).toString();
	    	int mesUno=Integer.parseInt(fechaUno.substring(3, 5));
	    	int mesDos=Integer.parseInt(sfechaDos.substring(3, 5));
	    	int diaUno=Integer.parseInt(fechaUno.substring(0, 2));
	    	int diaDos=Integer.parseInt(sfechaDos.substring(0, 2));
	//    	Log.d("FunGlobales","mesUno="+mesUno+" mesDos="+mesDos);
	//    	Log.d("FunGlobales","diaUno="+diaUno+" diaDos="+diaDos);
	//    	Log.d("FunGlobales","diaFacturacion="+diaFacturacion);
	    	if ((mesDos-mesUno)==0)
	    	{
	    		//Mismo mes
	    		Log.d("FunGlobales","Mismo mes");
	    		
	    		if ((diaUno<diaFacturacion)&&(diaDos>=diaFacturacion))
	    			retorno= true; //Diferente periodo
	    		else
	    			retorno=false;
	    	}
	    	else
	    	{
	    		//Meses distintos
	    		Log.d("FunGlobales","Distinto mes");
	    		if ((diaUno>=diaFacturacion)&&(diaDos<diaFacturacion))
	    			retorno= false; //Diferente periodo
	    		else
	    			retorno=true;
	    		
	    	}
    	}
    	catch (Exception e)
    	{
    		
    	}
    	return retorno;
    }

    /**
     * Convierte un objeto serializable a array de byte
     * @param object
     * @return byte[]
     * @return null si hay error.
     */
    public static byte[] serializeObject(Object o) { 
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
     
        try { 
          ObjectOutput out = new ObjectOutputStream(bos); 
          out.writeObject(o); 
          out.close(); 
     
          // Get the bytes of the serialized object 
          byte[] buf = bos.toByteArray(); 
     
          return buf; 
        } catch(IOException ioe) { 
          Log.e("serializeObject", "error", ioe); 
     
          return null; 
        } 
      }
    
    /**
     * Convierte un array de byte en un objeto serializable
     * @param b
     * @return object
     * @return null si hay error.
     */
    public static Object deserializeObject(byte[] b) { 
        try { 
          ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b)); 
          Object object = in.readObject(); 
          in.close(); 
     
          return object; 
        } catch(ClassNotFoundException cnfe) { 
          Log.e("deserializeObject", "class not found error", cnfe); 
     
          return null; 
        } catch(IOException ioe) { 
          Log.e("deserializeObject", "io error", ioe); 
     
          return null; 
        } 
      } 

 
}

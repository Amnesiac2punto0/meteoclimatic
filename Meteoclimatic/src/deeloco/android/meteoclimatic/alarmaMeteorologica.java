/*(C) Copyright

    This file is part of widget Meteoclimatic. 
    Autor: Antonio Cristóbal Álvarez Abellán -> acabellan@gmail.com
    
    */

package deeloco.android.meteoclimatic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Clase que permite gestionar las alarmas meteorológicas
 * @author Antonio Cristóbal Álvarez Abellán
 *
 */

public class alarmaMeteorologica {
	
	
	/**
	 * ATRIBUTOS
	 */
	private String nombre; //Nombre de la estación
	private String codigo; //Código de la estación
	private datosMeteorologicos datosMeteo = new datosMeteorologicos(); //Datos meteorológicos
	private datosMeteorologicos datosMeteoPrev=new datosMeteorologicos(); //Datos meteorológicos previos
	private Context contexto;
	private int appWidgetId;
	
	private boolean alarmTempAlta=false;
	private boolean alarmTempBaja=false;
	private boolean alarmLluvia=false;
	private boolean alarmViento=false;
	
	
	/**
	 * CONSTRUCTORES
	 */
		
	public alarmaMeteorologica() {
	}
	
	public alarmaMeteorologica(Context contexto,int appWidgetId,String nombre, String codigo,datosMeteorologicos datosMeteo) {
		this.contexto=contexto;
		this.nombre = nombre;
		this.codigo = codigo;
		this.datosMeteo=datosMeteo;
		this.appWidgetId=appWidgetId;
	}
		
	/**
	 * GETTER Y SETTER
	 */
	
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
	}
	public datosMeteorologicos getDatosMeteo() {
		return datosMeteo;
	}
	public void setDatosMeteo(datosMeteorologicos datosMeteo) {
		this.datosMeteoPrev=this.datosMeteo;
		this.datosMeteo = datosMeteo;
	}
	public int getAppWidgetId() {
		return appWidgetId;
	}
	public void setAppWidgetId(int appWidgetId) {
		this.appWidgetId = appWidgetId;
	}
	


	/**
	 * MÉTODOS
	 */
	
	/**
	 * Carga los valores de los parámetros que necesita la clase para funcionar.
	 */
	public void setParam(Context contexto,int appWidgetId,String nombre, String codigo,datosMeteorologicos datosMeteo,datosMeteorologicos datosMeteoPrev) {
		this.contexto=contexto;
		this.nombre = nombre;
		this.codigo = codigo;
		this.datosMeteoPrev=datosMeteoPrev;
		this.datosMeteo=datosMeteo;
		this.appWidgetId=appWidgetId;
	}
	

	
	/**
	 * Comprueba si las alarmas se cumplen y lanzar la alarma en el áres de notificación.
	 */
	public void comprobarAlarmas()
	{
		/*Comprobar la alarma de temperatura*/
		//String trans=PreferenceManager.getDefaultSharedPreferences(context).getString("listTransparencia"+"_"+appWidgetId, "FF");
		
		boolean alarmaTempActivadaAlta=PreferenceManager.getDefaultSharedPreferences(contexto).getBoolean("chbox_alarmaTempAlta"+"_"+appWidgetId, false);
		boolean alarmaTempActivadaBaja=PreferenceManager.getDefaultSharedPreferences(contexto).getBoolean("chbox_alarmaTempBaja"+"_"+appWidgetId, false);
		boolean alarmaLluviaActivada=PreferenceManager.getDefaultSharedPreferences(contexto).getBoolean("chbox_alarmaLluvia"+"_"+appWidgetId, false);
		boolean alarmaVientoActivada=PreferenceManager.getDefaultSharedPreferences(contexto).getBoolean("chbox_alarmaViento"+"_"+appWidgetId, false);
		String strRingtonePreference = PreferenceManager.getDefaultSharedPreferences(contexto).getString("ringtonePref", "DEFAULT_SOUND");        

		
		//ALARMA TEMPERATURA ALTA
		if (alarmaTempActivadaAlta) 
		{ /*Alarma temperatura ALTA activada*/
			double alarmaTempAlta=Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(contexto).getString("txt_valorAlarmaTempAlta"+"_"+appWidgetId, "99"));
			Log.d("alarmaMeteorológica", "Comprobando alarma.Max="+alarmaTempAlta+". Temp. Actual="+datosMeteo.T);
			if ((datosMeteo.T>alarmaTempAlta)&&(datosMeteoPrev.T<alarmaTempAlta))
			{
				//Lanzar alarma de temperatura máxima
				Log.d("alarmaMeteorológica", "Lanzando alarma");
				NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
				
				int icon = R.drawable.icon;
				CharSequence tickerText = "Alarma Meteoclimatic";
				long when = System.currentTimeMillis();
				Notification notification = new Notification(icon, tickerText, when);
	
				notification.sound = Uri.parse(strRingtonePreference);
				
				CharSequence contentTitle = "Alarma de temperatura Alta";
				CharSequence contentText = datosMeteo.T+"ºC en "+this.nombre;
				Intent notificationIntent = new Intent(contexto,tabPantalla.class);
				//launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				notificationIntent.putExtra("codigo", this.codigo);
				notificationIntent.putExtra("nombre", this.nombre);
		
		  		PendingIntent contentIntent = PendingIntent.getActivity(contexto, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				notification.setLatestEventInfo(contexto, contentTitle, contentText,contentIntent);
				mNotificationManager.notify(appWidgetId, notification);
			}
		}
			
			//ALARMA TEMPERATURA BAJA
			if (alarmaTempActivadaBaja) 
			{ /*Alarma temperatura BAJA activada*/
				double alarmaTempBaja=Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(contexto).getString("txt_valorAlarmaTempBaja"+"_"+appWidgetId, "99"));
				Log.d("alarmaMeteorológica", "Comprobando alarma.Baja="+alarmaTempBaja+". Temp. Actual="+datosMeteo.T);
				Log.d("alarmaMeteorológica", "Temperatura anterior="+datosMeteoPrev.T);
				if ((datosMeteo.T<alarmaTempBaja)&&(datosMeteoPrev.T>alarmaTempBaja))
				{
					//Lanzar alarma de temperatura máxima
					Log.d("alarmaMeteorológica", "Lanzando alarma Temp. Baja");
					NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
					
					int icon = R.drawable.icon;
					CharSequence tickerText = "Alarma Meteoclimatic";
					long when = System.currentTimeMillis();
					Notification notification = new Notification(icon, tickerText, when);
					
					notification.sound = Uri.parse(strRingtonePreference);
					
					CharSequence contentTitle = "Alarma de temperatura Baja";
					CharSequence contentText = datosMeteo.T+"ºC en "+this.nombre;
					Intent notificationIntent = new Intent(contexto,tabPantalla.class);
					//launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
					notificationIntent.putExtra("codigo", this.codigo);
					notificationIntent.putExtra("nombre", this.nombre);
			
			  		PendingIntent contentIntent = PendingIntent.getActivity(contexto, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(contexto, contentTitle, contentText,contentIntent);
					mNotificationManager.notify(appWidgetId, notification);
				}

			}
			
			
			//ALARMA LLUVIA
			if (alarmaLluviaActivada) 
			{ /*Alarma LLUVIA activada*/
				double alarmaLluvia=Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(contexto).getString("txt_valorAlarmaLluvia"+"_"+appWidgetId, "99"));
				Log.d("alarmaMeteorológica", "Comprobando alarma.Lluvia="+alarmaLluvia+". Lluvia. Actual="+datosMeteo.P);
				if ((datosMeteo.P-datosMeteoPrev.P)>alarmaLluvia)
				{
					//Lanzar alarma de temperatura máxima
					Log.d("alarmaMeteorológica", "Lanzando alarma Lluvia");
					NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
					
					int icon = R.drawable.icon;
					CharSequence tickerText = "Alarma Meteoclimatic";
					long when = System.currentTimeMillis();
					Notification notification = new Notification(icon, tickerText, when);
					
					notification.sound = Uri.parse(strRingtonePreference);
					
					CharSequence contentTitle = "Alarma de lluvia";
					CharSequence contentText = datosMeteo.P+"mm en "+this.nombre;
					Intent notificationIntent = new Intent(contexto,tabPantalla.class);
					//launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
					notificationIntent.putExtra("codigo", this.codigo);
					notificationIntent.putExtra("nombre", this.nombre);
			
			  		PendingIntent contentIntent = PendingIntent.getActivity(contexto, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(contexto, contentTitle, contentText,contentIntent);
					mNotificationManager.notify(appWidgetId, notification);
				}

			}
			
			//ALARMA VIENTO
			if (alarmaVientoActivada) 
			{ /*Alarma VIENTO activada*/
				double alarmaViento=Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(contexto).getString("txt_valorAlarmaViento"+"_"+appWidgetId, "99"));
				Log.d("alarmaMeteorológica", "Comprobando alarma.Viento="+alarmaViento+". Viento. Actual="+datosMeteo.W);
				if ((datosMeteo.W>alarmaViento)&&(datosMeteoPrev.W<alarmaViento))
				{
					//Lanzar alarma de temperatura máxima
					Log.d("alarmaMeteorológica", "Lanzando alarma Viento");
					NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
					
					int icon = R.drawable.icon;
					CharSequence tickerText = "Alarma Meteoclimatic";
					long when = System.currentTimeMillis();
					Notification notification = new Notification(icon, tickerText, when);
					
					notification.sound = Uri.parse(strRingtonePreference);
					
					CharSequence contentTitle = "Alarma de viento";
					CharSequence contentText = datosMeteo.W+"km/h en "+this.nombre;
					Intent notificationIntent = new Intent(contexto,tabPantalla.class);
					//launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
					notificationIntent.putExtra("codigo", this.codigo);
					notificationIntent.putExtra("nombre", this.nombre);
			
			  		PendingIntent contentIntent = PendingIntent.getActivity(contexto, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(contexto, contentTitle, contentText,contentIntent);
					mNotificationManager.notify(appWidgetId, notification);
				}

			}
			
		}
		
	}


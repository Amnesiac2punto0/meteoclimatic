/*(C) Copyright

    This file is part of widget Meteoclimatic.
    Autor: Antonio Cristóbal Álvarez Abellán -> acabellan@gmail.com
    
    */

package deeloco.android.meteoclimatic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import deeloco.android.meteoclimatic.Preferencias;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class meteoclimatic extends AppWidgetProvider{
	
	private static final String PREFS_NAME = "deeloco.android.meteoclimatic.meteoclimatic_preferencias";
	private static Context contexto;
	static String tag="meteoclimatic.meteoclimatic";
	private static final String PREF_PREFIJO_CLAVE_NOMBRE = "nombre_";
	private static final String PREF_PREFIJO_CLAVE_CODIGO = "codigo_";
	public static String MY_WIDGET_UPDATE = "MY_OWN_WIDGET_UPDATE";
	private static String color_fondo;
	private static String color_medida;
	private static String color_unidad;
	private static alarmaMeteorologica alarma=new alarmaMeteorologica();
	//static estacion ee=new estacion();
	private static HashMap<Integer, estacion> estaciones = new HashMap<Integer, estacion>(); //Array asociativo con las estaciones. Clave=appWidgetIds, valor=clase estación
	
	@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		//Intent intent = new Intent(context,UpdateService.class);
        //context.startService(intent);
		//super.onUpdate(context, appWidgetManager, appWidgetIds);
		contexto=context.getApplicationContext();
		final int N = appWidgetIds.length;
    	
    	Log.d(tag,"Numero de appWidgetIds="+N);
	    for (int i=0; i < N; i++) {
	        int appWidgetId = appWidgetIds[i];
	        if (!estaciones.containsKey(appWidgetId))
	        {
	        	estaciones.put(appWidgetId, new estacion());
	        }
	        //update the widget
	        updateAppWidget(context,appWidgetManager, appWidgetId);
	    }  
    }
 //}
	
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(tag, "onDeleted");
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            Preferencias.deleteTitlePref(context, appWidgetIds[i]);
        }
    }
    
    
    @Override
    public void onEnabled(Context context) {
    	//prepare Alarm Service to trigger Widget
		
		   Intent intent = new Intent(meteoclimatic.MY_WIDGET_UPDATE);
		   PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		   AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

		   //Calculamos los milisegundos
		   String p=PreferenceManager.getDefaultSharedPreferences(context).getString("listPeriodo", "30");
		   int periodo=0;
		   try
		   {
			   periodo=Integer.parseInt(p); //en minutos
			   periodo=periodo*60; //En segundos
			   periodo=periodo*1000; //En milisegundos
		   }
		   catch(Exception e)
		   {
			   Toast.makeText(context,R.string.error_periodo,Toast.LENGTH_LONG).show();
		   }
		   alarmManager.cancel(pendingIntent);
		   Log.d(tag,"Configurando ALARMA para un periodo de "+periodo);
		   if (periodo>0)
			   alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), periodo, pendingIntent);
		   Log.d(tag, "onEnabled");
    }
    
    
    @Override
    public void onDisabled(Context context) {
    	//Desactivamos todas las alarmas, cuande se cancele el último widget
    	Log.d(tag, "onDisabled");
        Intent intent = new Intent(meteoclimatic.MY_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        Log.d(tag,"Cancelando todas las alarmas");        
         }
	
	
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		
		RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_pantalla);
		
		//ACTUALIZACIÓN
		//updateViews.setViewVisibility(R.id.imagenActual,0);//0=visible
		appWidgetManager.updateAppWidget(appWidgetId, updateViews);
		
		//Cargamos el tema correspondiente
		//String tema = meteoclimatic_preferencias.loadPref(context,PREF_PREFIJO_CLAVE_TEMA,appWidgetId);
		String tema=PreferenceManager.getDefaultSharedPreferences(context).getString("listTema"+"_"+appWidgetId, "1");
		if (tema.equals("1"))
		{
			color_fondo="FFFFFF";
			color_medida="#2847CE";
			color_unidad="#000000";
		}
		else
		{
			color_fondo="000000";
			color_medida="#97A7FD";
			color_unidad="#FFFFFF";
		}
		
		//Activar/desactivar el fondo del widget
		//String trans = meteoclimatic_preferencias.loadPref(context,PREF_PREFIJO_CLAVE_TRANS,appWidgetId,"FF");
		String trans=PreferenceManager.getDefaultSharedPreferences(context).getString("listTransparencia"+"_"+appWidgetId, "FF");
		if (trans.length()==2)
			updateViews.setInt(R.id.widget, "setBackgroundColor",android.graphics.Color.parseColor("#"+trans+color_fondo));
		else
			updateViews.setInt(R.id.widget, "setBackgroundColor",android.graphics.Color.parseColor("#"+"88"+color_fondo));
		Log.d(tag,"Transparencias "+trans);
		//updateViews.setInt(R.id.widget, "setBackgroundColor",android.graphics.Color.parseColor("#"+"88"+color_fondo));
		
		//Datos del widget
		
		Date date=new Date();
    	DateFormat format=SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT,Locale.getDefault());
    	//Capturamos los datos de la estación
    	
    	Log.d("meteoclimatic.updateAppWidget","INICIANDO PROCESO para el Id Widget="+appWidgetId);
    	Log.d("meteoclimatic.updateAppWidget","Código de estación="+Preferencias.loadPref(context,PREF_PREFIJO_CLAVE_CODIGO,appWidgetId));

    	String nombreEstacion = Preferencias.loadPref(context,PREF_PREFIJO_CLAVE_NOMBRE,appWidgetId);
    	String codigoEstacion = Preferencias.loadPref(context,PREF_PREFIJO_CLAVE_CODIGO,appWidgetId);
    	if ((nombreEstacion!=null)) //Existe estación para el appWidgetId
    	{
			estacion ee=(estacion) estaciones.get(appWidgetId);
	    	ee.setNombre(nombreEstacion);
	    	ee.setCodigo(codigoEstacion);
	    	ee.setContexto(context);
	    		
    		//FIN DE HILO
	    	
	    	Log.d("meteoclimatic.updateAppWidget","Iniciando actualización :" + format.format(date)+" - Estación: "+ee.getNombre());
	    	if (!(ee.descargarCapturarDatos()))
	    	{
	    		//Error en la descarga/captura de los datos
	    		ee.getDatosMeteo().activa=false;
	    		
	    	}
	    	Log.d("meteoclimatic.updateAppWidget","Estado :" + ee.getDatosMeteo().activa);

	    	if (ee.getDatosMeteo().activa)
	    	{
	    		//LA ESTACIÓN ESTÁ ACTIVA
	    		//updateViews.setImageViewResource(R.id.semaforo,android.R.drawable.presence_online);
	    		//updateViews.setTextViewText(R.id.tw_cab_principal,Html.fromHtml("<font color='green'>on-line</font>"));
	    		updateViews.setTextViewText(R.id.tw_medida_principal,Html.fromHtml("<font color='"+color_medida+"'><b>"+ee.getDatosMeteo().T+"</b>º</font>"));
	    		Log.d(".........",ee.getNombre());
		    	updateViews.setTextViewText(R.id.tw_cab_datos,Html.fromHtml("<font color='"+color_unidad+"'>"+ee.getNombre()+"</font>"));
		    	updateViews.setTextViewText(R.id.tw_linea3_datos_izq,Html.fromHtml("<b>"+format.format(date)+"</b>"));
		    	updateViews.setTextColor(R.id.tw_linea3_datos_izq,Color.parseColor(color_medida));
		    	//Probando fechas
		    	
		    	String fechaEstacion="";
		    	Date d=null;
		    	SimpleDateFormat par;
		    	par = new SimpleDateFormat("dd-MM-yyyy HH:mm");  
		    	par.setTimeZone(TimeZone.getTimeZone("GMT"));
		    	try
		    	{
		    		d= par.parse(ee.getDatosMeteo().fecha+" "+ee.getDatosMeteo().hora);
		    		fechaEstacion=format.format(d);
		    	}
		    	catch (Exception e)
		    	{
		    		Log.d(tag,"Error fecha: "+e.getMessage());
		    		fechaEstacion="S/D";
		    	}
		    	
		    	/*DateFormat dLocal = DateFormat.SHORT;
		    	
		    	String gmtTimeLocal = dLocal.format(d);*/
		    	

		    	Log.d(tag,"d="+d.toLocaleString()+"hora local ->"+format.format(d)+"-> hora UTC ->"+ee.getDatosMeteo().fecha+" "+ee.getDatosMeteo().hora);

		    	updateViews.setTextColor(R.id.tw_linea3_datos_der,Color.parseColor(color_unidad));
		    	updateViews.setTextViewText(R.id.tw_linea3_datos_der,Html.fromHtml(fechaEstacion));
		    	updateViews.setTextColor(R.id.tw_linea1_datos,Color.parseColor(color_unidad));
		    	//updateViews.setTextViewText(R.id.tw_linea1_datos,Html.fromHtml("<font color='"+color_medida+"'><b>"+ee.getDatosMeteo().T+"</b></font>ºC | <font color='"+color_medida+"'><b>"+ee.getDatosMeteo().H+"</b></font>% | <font color='"+color_medida+"'><b>"+ee.getDatosMeteo().B+"</b></font>hPa"));
		    	updateViews.setTextViewText(R.id.tw_linea1_datos,Html.fromHtml("<font color='"+color_medida+"'><b>"+ee.getDatosMeteo().H+"</b></font>% | " +
		    																	"<font color='"+color_medida+"'><b>"+ee.getDatosMeteo().B+"</b></font>hPa | "+
		    																	"<font color='"+color_medida+"'><b>"+ee.getDatosMeteo().P+"</b></font>mm | "+
		    																	"<font color='"+color_medida+"'><b>"+ee.incrementoLluvia()+"</b></font>mm | "
		    																	));
		    	updateViews.setTextColor(R.id.tw_linea2_datos,Color.parseColor(color_unidad));
		    	updateViews.setTextViewText(R.id.tw_linea2_datos,Html.fromHtml("<b>"+ee.getDatosMeteo().W+"</b>km/h [<b>"+ee.getDatosMeteo().getDirViento()+"</b>] | "+
		    																	"<b>"+ee.getDatosMeteo().P+"</b>mm ["+(ee.incrementoLluvia())+" mm]"));
		    	//updateViews.setTextViewText(R.id.tw_linea2_datos,Html.fromHtml("<b>"+ee.getDatosMeteo().W+"</b>km/h [<b>"+ee.getDatosMeteo().getDirViento()+"</b>] | <b>"+ee.getDatosMeteo().P+"</b>mm"));
		    	//Cargamos icono de estado del cielo
		    	updateViews.setImageViewBitmap(R.id.image,ee.getIcono());
		    	
		    	estaciones.put(appWidgetId, ee);
		    	
		    	//Lanzamos alarmas Meteorológicas
		    	alarma.setParam(context,appWidgetId,nombreEstacion,codigoEstacion,ee.getDatosMeteo(),ee.getDatosMeteoPrev());
                alarma.comprobarAlarmas();
		    
	    	}
	    	else
	    	{
	    		ee.capturarDatos();
	    		
	    		//updateViews.setImageViewResource(R.id.semaforo,android.R.drawable.presence_online);
	    		//updateViews.setTextViewText(R.id.tw_cab_principal,Html.fromHtml("<font color='green'>on-line</font>"));
	    		updateViews.setTextViewText(R.id.tw_medida_principal,Html.fromHtml("<font color='"+color_medida+"'><b>"+ee.getDatosMeteo().T+"</b>º</font>"));
		    	updateViews.setTextViewText(R.id.tw_cab_datos,Html.fromHtml("<font color='"+color_unidad+"'>"+ee.getNombre()+"</font>"));
		    	
		    	updateViews.setTextColor(R.id.tw_linea1_datos,Color.parseColor(color_unidad));
		    	updateViews.setTextViewText(R.id.tw_linea1_datos,Html.fromHtml("<font color='"+color_medida+"'><b>"+ee.getDatosMeteo().T+"</b></font>ºC | <font color='"+color_medida+"'><b>"+ee.getDatosMeteo().H+"</b></font>% | <font color='"+color_medida+"'><b>"+ee.getDatosMeteo().B+"</b></font>hPa"));
		    	updateViews.setTextColor(R.id.tw_linea2_datos,Color.parseColor(color_unidad));
		    	updateViews.setTextViewText(R.id.tw_linea2_datos,Html.fromHtml("<b>"+ee.getDatosMeteo().W+"</b>km/h<b>"+ee.getDatosMeteo().getDirViento()+"</b> | <b>"+ee.getDatosMeteo().P+"</b>mm"));
	    		//updateViews.setTextViewText(R.id.tw_cab_principal,Html.fromHtml("<font color='red'>off-line</font>"));
	    		updateViews.setTextViewText(R.id.tw_linea3_datos_izq,Html.fromHtml("<b>"+format.format(date)+"</b>"));
	    		updateViews.setTextColor(R.id.tw_linea3_datos_izq,Color.parseColor(color_medida));
	    		updateViews.setImageViewResource(R.id.image,R.drawable.icon);
	    		ee.getDatosMeteo().activa=true;
	    		//updateViews.setImageViewResource(R.id.semaforo,android.R.drawable.presence_busy);
	    	}
	
	        // Tell the AppWidgetManager to perform an update on the current App Widget
	    	Log.d("meteoclimatic.updateAppWidget","Fin actualización :" + format.format(date)+" - Estación: "+ee.getNombre());
	    	//Cargamos el nuevo valor del objeto estación en el hashMap estaciones
	    	estaciones.put(appWidgetId, ee);
	    	
			//Evento onClick en el widget, para lanzar preferencias  
			Intent launchIntent = new Intent(context,Preferencias.class);
			launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	
	  		PendingIntent intent = PendingIntent.getActivity(context, appWidgetId, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	  		updateViews.setOnClickPendingIntent(R.id.centro, intent);
	      
	  		//appWidgetManager.updateAppWidget(appWidgetId, updateViews);
	  		
	  		//	Evento onClick en el widget, para lanzar datos de la estación  
			Intent launchIntent2 = new Intent(context,tabPantalla.class);
			//launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			launchIntent2.putExtra("codigo", ee.getCodigo());
			launchIntent2.putExtra("nombre", ee.getNombre());
	
	  		PendingIntent intent2 = PendingIntent.getActivity(context, appWidgetId, launchIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
	  		updateViews.setOnClickPendingIntent(R.id.izquierda, intent2);
	  		
	  		//Evento onClick en el widget, para actualización manual
	  		Intent intentUpdate = new Intent(meteoclimatic.MY_WIDGET_UPDATE);
	  		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentUpdate, 0);
	  		//PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentUpdate, 0);
	  		updateViews.setOnClickPendingIntent(R.id.derecha, pendingIntent);
	  		//updateViews.setViewVisibility(R.id.imagenActual,4);//4=invisible
	  		appWidgetManager.updateAppWidget(appWidgetId, updateViews);
    	}

	}
	
    
	/**
	 * Esta es una copia de updateAppWidget, con la diferencia que no descarga los datos meteorológicos del 
	 * servidor. Lo único que hace es actualizar los valores de interface.
	 * Para futuras versiones hay que estudiar la posibilidad de eliminar este método e incluir un
	 * parámero en updateAppWidget, que nos sirva para indicar si hay que descagar o no datos meteorológicos
	 * @param context
	 * @param appWidgetManager
	 * @param appWidgetId
	 */
	static void updateAppWidgetInterface(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		
		RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_pantalla);
		
		
		
		//Cargamos el tema correspondiente
		//String tema = meteoclimatic_preferencias.loadPref(context,PREF_PREFIJO_CLAVE_TEMA,appWidgetId);
		String tema=PreferenceManager.getDefaultSharedPreferences(context).getString("listTema"+"_"+appWidgetId, "1");
		if (tema.equals("1"))
		{
			color_fondo="FFFFFF";
			color_medida="#2847CE";
			color_unidad="#000000";
		}
		else
		{
			color_fondo="000000";
			color_medida="#97A7FD";
			color_unidad="#FFFFFF";
		}
		
		//Activar/desactivar el fondo del widget
		//String trans = meteoclimatic_preferencias.loadPref(context,PREF_PREFIJO_CLAVE_TRANS,appWidgetId,"FF");
		String trans=PreferenceManager.getDefaultSharedPreferences(context).getString("listTransparencia"+"_"+appWidgetId, "FF");
		if (trans.length()==2)
			updateViews.setInt(R.id.widget, "setBackgroundColor",android.graphics.Color.parseColor("#"+trans+color_fondo));
		else
			updateViews.setInt(R.id.widget, "setBackgroundColor",android.graphics.Color.parseColor("#"+"88"+color_fondo));
		//Log.d(tag,"Transparencias "+trans);
		//updateViews.setInt(R.id.widget, "setBackgroundColor",android.graphics.Color.parseColor("#"+"88"+color_fondo));
		
		//Datos del widget
		
		Date date=new Date();
    	DateFormat format=SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT,Locale.getDefault());
    	//Capturamos los datos de la estación
    	
    	Log.d("meteoclimatic.updateAppWidget","INICIANDO PROCESO para el Id Widget="+appWidgetId);
    	Log.d("meteoclimatic.updateAppWidget","Código de estación="+Preferencias.loadPref(context,PREF_PREFIJO_CLAVE_CODIGO,appWidgetId));

    	String nombreEstacion = Preferencias.loadPref(context,PREF_PREFIJO_CLAVE_NOMBRE,appWidgetId);
    	String codigoEstacion = Preferencias.loadPref(context,PREF_PREFIJO_CLAVE_CODIGO,appWidgetId);
    	if ((nombreEstacion!=null)) //Existe estación para el appWidgetId
    	{
    		//¿El objeto ee no se podría obtener del HasMap estaciones?. Porque de esta manera no te va ha actualizar correctamente 
    		//el incremento de la lluvia en el último periodo de actualización.
    		//¡¡¡¡¡¡¡ PROBAR !!!!!!!
    		//NO DEBES CAPTURAR DATOS. AL CAPTURAR DATOS DEL RSS QUE HAY EN LA SD, ESTO SUPONDRÁ QUE LOS VALORES ANTERIORES Y ACTUALES SERÁN
    		//LO MISMO Y EL VALOR DE INCREMENTO DE LLUVIA SERÁ FALSO, YA QUE SIEMPRE SERÁ 0.0 mm
    		
    		estacion ee=(estacion) estaciones.get(appWidgetId);
    		
	    	//estacion ee=new estacion(context,nombreEstacion,codigoEstacion);
	    	//ee.setNombre(nombreEstacion);
	    	//ee.setCodigo(codigoEstacion);
	    	//ee.capturarDatos();
	    	//Log.d("meteoclimatic.updateAppWidget","Iniciando actualización :" + format.format(date)+" - Estación: "+ee.getNombre());
	    	//if (!(ee.capturarDatos()))
	    	//{
	    	//	Log.d("meteoclimatic.updateAppWidget","Error al capturar datos");
	    	//	ee.getDatosMeteo().activa=false;
	    	//}
	    	//Log.d("meteoclimatic.updateAppWidget","Estado :" + ee.getDatosMeteo().activa);
	    	if (ee.getDatosMeteo().activa)
	    	{
	    		//LA ESTACIÓN ESTÁ ACTIVA
	    		//updateViews.setImageViewResource(R.id.semaforo,android.R.drawable.presence_online);
	    		//updateViews.setTextViewText(R.id.tw_cab_principal,Html.fromHtml("<font color='green'>on-line</font>"));
	    		updateViews.setTextViewText(R.id.tw_medida_principal,Html.fromHtml("<font color='"+color_medida+"'><b>"+ee.getDatosMeteo().T+"</b>º</font>"));
		    	updateViews.setTextViewText(R.id.tw_cab_datos,Html.fromHtml("<font color='"+color_unidad+"'>"+ee.getNombre()+"</font>"));
		    	//updateViews.setTextViewText(R.id.tw_linea3_datos_izq,Html.fromHtml("<font color='"+color_medida+"'><b>"+format.format(date)+"</b></font>"));
		    	updateViews.setTextColor(R.id.tw_linea3_datos_izq,Color.parseColor(color_medida));
		    	
		    	//Probando fechas
		    	
		    	String fechaEstacion="";
		    	Date d=null;
		    	SimpleDateFormat par;
		    	par = new SimpleDateFormat("dd-MM-yyyy HH:mm");  
		    	par.setTimeZone(TimeZone.getTimeZone("GMT"));
		    	try
		    	{
		    		d= par.parse(ee.getDatosMeteo().fecha+" "+ee.getDatosMeteo().hora);
		    		fechaEstacion=format.format(d);
		    		
		    	}
		    	catch (Exception e)
		    	{
		    		Log.d(tag,"Error"+e.getMessage());
		    		fechaEstacion="S/D";
		    	}
		    	
		    	/*DateFormat dLocal = DateFormat.SHORT;
		    	
		    	String gmtTimeLocal = dLocal.format(d);*/
		    	

		    	//Log.d(tag,"d="+d.toLocaleString()+"hora local ->"+format.format(d)+"-> hora UTC ->"+ee.getDatosMeteo().fecha+" "+ee.getDatosMeteo().hora);

		    	updateViews.setTextColor(R.id.tw_linea3_datos_der,Color.parseColor(color_unidad));
		    	updateViews.setTextViewText(R.id.tw_linea3_datos_der,Html.fromHtml(fechaEstacion));
		    	updateViews.setTextColor(R.id.tw_linea1_datos,Color.parseColor(color_unidad));
		    	updateViews.setTextViewText(R.id.tw_linea1_datos,Html.fromHtml("<font color='"+color_medida+"'><b>"+ee.getDatosMeteo().T+"</b></font>ºC | <font color='"+color_medida+"'><b>"+ee.getDatosMeteo().H+"</b></font>% | <font color='"+color_medida+"'><b>"+ee.getDatosMeteo().B+"</b></font>hPa"));
		    	updateViews.setTextColor(R.id.tw_linea2_datos,Color.parseColor(color_unidad));
		    	//updateViews.setTextViewText(R.id.tw_linea2_datos,Html.fromHtml("<b>"+ee.getDatosMeteo().W+"</b>km/h [<b>"+ee.getDatosMeteo().getDirViento()+"</b>] | <b>"+ee.getDatosMeteo().P+"</b>mm"));
		    	updateViews.setTextViewText(R.id.tw_linea2_datos,Html.fromHtml("<b>"+ee.getDatosMeteo().W+"</b>km/h [<b>"+ee.getDatosMeteo().getDirViento()+"</b>] | <b>"+ee.getDatosMeteo().P+"</b>mm ["+(ee.incrementoLluvia())+" mm]"));
		    	//Cargamos icono de estado del cielo
		    	updateViews.setImageViewBitmap(R.id.image,ee.getIcono());
		    
	    	}
	    	else
	    	{
	    		ee.capturarDatos();
	    		
	    		//updateViews.setImageViewResource(R.id.semaforo,android.R.drawable.presence_online);
	    		//updateViews.setTextViewText(R.id.tw_cab_principal,Html.fromHtml("<font color='green'>on-line</font>"));
	    		updateViews.setTextViewText(R.id.tw_medida_principal,Html.fromHtml("<font color='"+color_medida+"'><b>"+ee.getDatosMeteo().T+"</b>º</font>"));
		    	updateViews.setTextViewText(R.id.tw_cab_datos,Html.fromHtml("<font color='"+color_unidad+"'>"+ee.getNombre()+"</font>"));
		    	
		    	updateViews.setTextColor(R.id.tw_linea1_datos,Color.parseColor(color_unidad));
		    	updateViews.setTextViewText(R.id.tw_linea1_datos,Html.fromHtml("<font color='"+color_medida+"'><b>"+ee.getDatosMeteo().T+"</b></font>ºC | <font color='"+color_medida+"'><b>"+ee.getDatosMeteo().H+"</b></font>% | <font color='"+color_medida+"'><b>"+ee.getDatosMeteo().B+"</b></font>hPa"));
		    	updateViews.setTextColor(R.id.tw_linea2_datos,Color.parseColor(color_unidad));
		    	updateViews.setTextViewText(R.id.tw_linea2_datos,Html.fromHtml("<b>"+ee.getDatosMeteo().W+"</b>km/h<b>"+ee.getDatosMeteo().getDirViento()+"</b> | <b>"+ee.getDatosMeteo().P+"</b>mm"));
	    		//updateViews.setTextViewText(R.id.tw_cab_principal,Html.fromHtml("<font color='red'>off-line</font>"));
	    		//updateViews.setTextViewText(R.id.tw_linea3_datos_izq,Html.fromHtml("<font color='"+color_medida+"'><b>"+format.format(date)+"</b></font>"));
	    		updateViews.setTextColor(R.id.tw_linea3_datos_izq,Color.parseColor(color_medida));
	    		updateViews.setImageViewResource(R.id.image,R.drawable.icon);
	    		ee.getDatosMeteo().activa=true;
	    		//updateViews.setImageViewResource(R.id.semaforo,android.R.drawable.presence_busy);
	    	}
	
	        // Tell the AppWidgetManager to perform an update on the current App Widget
	    	Log.d("meteoclimatic.updateAppWidget","Fin actualización :" + format.format(date)+" - Estación: "+ee.getNombre());
	    	
			//Evento onClick en el widget, para lanzar preferencias  
			Intent launchIntent = new Intent(context,Preferencias.class);
			launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	
	  		PendingIntent intent = PendingIntent.getActivity(context, appWidgetId, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	  		updateViews.setOnClickPendingIntent(R.id.centro, intent);
	      
	  		//appWidgetManager.updateAppWidget(appWidgetId, updateViews);
	  		
	  		//	Evento onClick en el widget, para lanzar datos de la estación  
			Intent launchIntent2 = new Intent(context,tabPantalla.class);
			//launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			launchIntent2.putExtra("codigo", ee.getCodigo());
			launchIntent2.putExtra("nombre", ee.getNombre());
	
	  		PendingIntent intent2 = PendingIntent.getActivity(context, appWidgetId, launchIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
	  		updateViews.setOnClickPendingIntent(R.id.izquierda, intent2);
	  		
	  		//Evento onClick en el widget, para actualización manual
	  		Intent intentUpdate = new Intent(meteoclimatic.MY_WIDGET_UPDATE);
	  		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentUpdate, 0);
	  		//PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentUpdate, 0);
	  		updateViews.setOnClickPendingIntent(R.id.derecha, pendingIntent);
	  		
	  		appWidgetManager.updateAppWidget(appWidgetId, updateViews);
    	}
		
	}
	
	
	   
    public static ConnectivityManager getAppConnectivityManager()
    {
    	return (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    
    
    public Context getContexto()
    {
    	return contexto;
    }

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		
		if(MY_WIDGET_UPDATE.equals(intent.getAction())){
			
			   //Toast.makeText(context, "Actualizando widget: "+intent.getAction() + context, Toast.LENGTH_LONG).show();
			   //Log.d(tag,"Actualizando widget: "+intent.getAction() +" - "+ AppWidgetManager.getInstance(context));
			   AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			   ComponentName thisAppWidget = new ComponentName(context.getPackageName(), meteoclimatic.class.getName());
			   int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

			   onUpdate(context, appWidgetManager, appWidgetIds);
			   

			   
		}
	}

}

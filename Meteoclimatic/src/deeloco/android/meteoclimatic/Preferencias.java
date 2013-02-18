/*
    This file is part of widget Meteoclimatic.
    Autor: Antonio CristÃ³bal Ã�lvarez AbellÃ¡n -> acabellan@gmail.com
    
    */


package deeloco.android.meteoclimatic;

import deeloco.android.meteoclimatic.net.DownloadTexto;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.os.SystemClock;


public class Preferencias extends PreferenceActivity  implements OnSharedPreferenceChangeListener {
	
	//private PreferenceScreen psRaiz=getPreferenceScreen();
	
	//ValoresPreferencias vp=new ValoresPreferencias(this);
	//private PreferenceScreen ps;
	private String preURL="http://meteoclimatic.com/feed/rss/";
	private String pathSD="/sdcard/meteoclimatic/";
	private String provincia="/sdcard/meteoclimatic/";
	private DownloadTexto dt=null;
	private String array_nombres[]={};
	private String array_codigos[]={};
	private ProgressDialog pd;
	ListPreference listEstacion;
	private int widgetId;
	String tag="Preferencias";
	private static final String PREFS_NAME = "deeloco.android.meteoclimatic.meteoclimatic_preferencias";
	private static final String PREF_PREFIJO_CLAVE_NOMBRE = "nombre_";
	private static final String PREF_PREFIJO_CLAVE_CODIGO = "codigo_";
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private int RETORNO_RESULTADO;
	private boolean cambioEstacion=false;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
		Intent intentOrigen = getIntent();
        Bundle params = intentOrigen.getExtras();
		widgetId = params.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        
        setResult(RESULT_CANCELED);
        
     // Find the widget id from the intent. 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) 
        {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) 
        {
            finish();
        }

        Log.d(tag,"widgetId en preferencias.onCreate="+widgetId+","+mAppWidgetId);
        
        //Modificamos el nombre de la clave de las preferencias en funciÃ³n del widgetid
        //Pais
        String vPref=PreferenceManager.getDefaultSharedPreferences(this).getString("listPais"+"_"+mAppWidgetId, "1");
        ListPreference listPais=(ListPreference)findPreference("listPais");
        listPais.setValue(vPref);
        listPais.setKey("listPais"+"_"+mAppWidgetId);
        listPais.setSummary(listPais.getEntry());
        
        //Provincia
        vPref=PreferenceManager.getDefaultSharedPreferences(this).getString("listProvincia"+"_"+mAppWidgetId, "1");
        ListPreference listProvincia=(ListPreference)findPreference("listProvincia");
        listProvincia.setKey("listProvincia"+"_"+mAppWidgetId);
        
        //Cargamos los valores en provincias
        String pais=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("listPais"+"_"+mAppWidgetId, "ES");
        ListPreference lpProvincia=(ListPreference)findPreference("listProvincia"+"_"+mAppWidgetId);
		if (pais.equals("PT"))
		{
			lpProvincia.setEntries(R.array.regionesPortugal);
			lpProvincia.setEntryValues(R.array.codRegionesPortugal);
		}
		else
		{
			lpProvincia.setEntries(R.array.provinciasEspaña);
			lpProvincia.setEntryValues(R.array.codProvinciasEspaña);
		}
		listProvincia.setValue(vPref);
		listProvincia.setSummary(listProvincia.getEntry());
		provincia=""+vPref;
               
        //Estacion
        vPref=PreferenceManager.getDefaultSharedPreferences(this).getString("listEstacion"+"_"+mAppWidgetId, "1");
        listEstacion=(ListPreference)findPreference("listEstacion");
        listEstacion.setValue(vPref);
        listEstacion.setKey("listEstacion"+"_"+mAppWidgetId);
        String nombreEstacion =Preferencias.loadPref(this,PREF_PREFIJO_CLAVE_NOMBRE,mAppWidgetId);
        listEstacion.setSummary(nombreEstacion);
        
		if (provincia.length()>2)
		{
			//Marcamos en sumario que se estan cargando los datos
			
    		//INICIO DE HILO
    		//segundoPlano background=new segundoPlano();
            //background.run();
    		pd = ProgressDialog.show(this, "", getString(R.string.cargando_datos), true,false);
    		new segundoPlano().start();
    			
    		//FIN DE HILO
            	
            listEstacion.setEntries(array_nombres);
            listEstacion.setEntryValues(array_codigos);
        	
        	if (array_nombres.length==0)
        	{
        		RETORNO_RESULTADO=RESULT_CANCELED;
        	}
		}        
		

        //Periodo
        /*
        vPref=PreferenceManager.getDefaultSharedPreferences(this).getString("listPeriodo"+"_"+mAppWidgetId, "1");
        EditTextPreference listPeriodo=(EditTextPreference)findPreference("listPeriodo");
        listPeriodo.setKey("listPeriodo"+"_"+mAppWidgetId);
        listPeriodo.setText(vPref);
        */
        //Tema
        vPref=PreferenceManager.getDefaultSharedPreferences(this).getString("listTema"+"_"+mAppWidgetId, "1");
        ListPreference listTema=(ListPreference)findPreference("listTema");
        listTema.setKey("listTema"+"_"+mAppWidgetId);
        listTema.setValue(vPref);
        //Transparencia
		vPref=PreferenceManager.getDefaultSharedPreferences(this).getString("listTransparencia"+"_"+mAppWidgetId, "1");
        ListPreference listTrans=(ListPreference)findPreference("listTransparencia");
        listTrans.setKey(listTrans.getKey()+"_"+mAppWidgetId);
        listTrans.setValue(vPref);
        //Log.d(tag,"Transparencias Value= "+listTrans.getValue());
        //Log.d(tag,"Transparencias Key= "+listTrans.getKey());
        
        //Alarma de temperatura alta
        
        boolean vPrefBool=PreferenceManager.getDefaultSharedPreferences(this).getBoolean("chbox_alarmaTempAlta"+"_"+mAppWidgetId, false);
        CheckBoxPreference chbox_alarmaTempMax=(CheckBoxPreference)findPreference("chbox_alarmaTempAlta");
        chbox_alarmaTempMax.setKey("chbox_alarmaTempAlta"+"_"+mAppWidgetId);
        chbox_alarmaTempMax.setChecked(vPrefBool);
        
        vPref=PreferenceManager.getDefaultSharedPreferences(this).getString("txt_valorAlarmaTempAlta"+"_"+mAppWidgetId, "1");
        EditTextPreference txt_valorAlarmaTempMax=(EditTextPreference)findPreference("txt_valorAlarmaTempAlta");
        txt_valorAlarmaTempMax.setKey("txt_valorAlarmaTempAlta"+"_"+mAppWidgetId);
        txt_valorAlarmaTempMax.setText(""+vPref);
        txt_valorAlarmaTempMax.setDependency("chbox_alarmaTempAlta"+"_"+mAppWidgetId);
        
        //	Alarma de temperatura Baja
        
        vPrefBool=PreferenceManager.getDefaultSharedPreferences(this).getBoolean("chbox_alarmaTempBaja"+"_"+mAppWidgetId, false);
        CheckBoxPreference chbox_alarmaTempBaja=(CheckBoxPreference)findPreference("chbox_alarmaTempBaja");
        chbox_alarmaTempBaja.setKey("chbox_alarmaTempBaja"+"_"+mAppWidgetId);
        chbox_alarmaTempBaja.setChecked(vPrefBool);
        
        vPref=PreferenceManager.getDefaultSharedPreferences(this).getString("txt_valorAlarmaTempBaja"+"_"+mAppWidgetId, "1");
        EditTextPreference txt_valorAlarmaTempBaja=(EditTextPreference)findPreference("txt_valorAlarmaTempBaja");
        txt_valorAlarmaTempBaja.setKey("txt_valorAlarmaTempBaja"+"_"+mAppWidgetId);
        txt_valorAlarmaTempBaja.setText(""+vPref);
        txt_valorAlarmaTempBaja.setDependency("chbox_alarmaTempBaja"+"_"+mAppWidgetId);
        
        //	Alarma de lluvia
        
        vPrefBool=PreferenceManager.getDefaultSharedPreferences(this).getBoolean("chbox_alarmaLluvia"+"_"+mAppWidgetId, false);
        CheckBoxPreference chbox_alarmaLluvia=(CheckBoxPreference)findPreference("chbox_alarmaLluvia");
        chbox_alarmaLluvia.setKey("chbox_alarmaLluvia"+"_"+mAppWidgetId);
        chbox_alarmaLluvia.setChecked(vPrefBool);
        
        vPref=PreferenceManager.getDefaultSharedPreferences(this).getString("txt_valorAlarmaLluvia"+"_"+mAppWidgetId, "1");
        EditTextPreference txt_valorAlarmaLluvia=(EditTextPreference)findPreference("txt_valorAlarmaLluvia");
        txt_valorAlarmaLluvia.setKey("txt_valorAlarmaLluvia"+"_"+mAppWidgetId);
        txt_valorAlarmaLluvia.setText(""+vPref);
        txt_valorAlarmaLluvia.setDependency("chbox_alarmaLluvia"+"_"+mAppWidgetId);
        
    	//	Alarma de viento
        
        vPrefBool=PreferenceManager.getDefaultSharedPreferences(this).getBoolean("chbox_alarmaViento"+"_"+mAppWidgetId, false);
        CheckBoxPreference chbox_alarmaViento=(CheckBoxPreference)findPreference("chbox_alarmaViento");
        chbox_alarmaViento.setKey("chbox_alarmaViento"+"_"+mAppWidgetId);
        chbox_alarmaViento.setChecked(vPrefBool);
        
        vPref=PreferenceManager.getDefaultSharedPreferences(this).getString("txt_valorAlarmaViento"+"_"+mAppWidgetId, "1");
        EditTextPreference txt_valorAlarmaViento=(EditTextPreference)findPreference("txt_valorAlarmaViento");
        txt_valorAlarmaViento.setKey("txt_valorAlarmaViento"+"_"+mAppWidgetId);
        txt_valorAlarmaViento.setText(""+vPref);
        txt_valorAlarmaViento.setDependency("chbox_alarmaViento"+"_"+mAppWidgetId);
        
        
	}
	
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        //mensaje();
        //Cuando entras en ajustes, se ejecuta este mÃ©todo
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Controlamos que todos los parÃ¡metros se han metido correctamente

    }
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Let's do something a preference value changes
    	//final PreferenceScreen psRaiz = getPreferenceScreen();
    	
    	if (key.equals("listPais"+"_"+mAppWidgetId)) //Cambia el pais
    	{
    		//Toast.makeText(getBaseContext(),"Lista Pais - "+key,Toast.LENGTH_LONG).show();
    		String pais=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("listPais"+"_"+mAppWidgetId, "EspaÃ±a");
    		ListPreference lpPais= (ListPreference)findPreference("listPais"+"_"+mAppWidgetId);
    		lpPais.setSummary(lpPais.getEntry());
    		//cargamos los valores de provincia en funciÃ³n del pais
    		ListPreference lpProvincia= (ListPreference)findPreference("listProvincia"+"_"+mAppWidgetId);
    		Log.d("Preferencias", "Pais Seleccionado="+pais);
    		if (pais.equals("ES"))
    		{
    			lpProvincia.setEntries(R.array.provinciasEspaña);
    			lpProvincia.setEntryValues(R.array.codProvinciasEspaña);
    		}
    		else
    		{
    			lpProvincia.setEntries(R.array.regionesPortugal);
    			lpProvincia.setEntryValues(R.array.codRegionesPortugal);
    		}
    		
    	}
    	
    	if (key.equals("listProvincia"+"_"+mAppWidgetId)) //Cambia la provincia
    	{
    		
    		//ListPreference lpProvincia= (ListPreference)findPreference("listProvincia"+"_"+mAppWidgetId);
    		//lpProvincia.setSummary(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("listProvincia"+"_"+mAppWidgetId, "Huelva"));
    		ListPreference lpProvincia= (ListPreference)findPreference("listProvincia"+"_"+mAppWidgetId);
    		String nombreProvincia= ""+Html.fromHtml(""+lpProvincia.getEntry());
    		lpProvincia.setSummary(nombreProvincia);
    		
    		provincia=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("listProvincia"+"_"+mAppWidgetId, "EspaÃ±a");
    		//descargar_fichero downFile=new descargar_fichero(Preferencias.this,preURL, provincia, pathSD);
    		ListPreference listEstacion= (ListPreference)findPreference("listEstacion"+"_"+mAppWidgetId);
    		
    		if (provincia.length()>2)
    		{
    			//Marcamos en sumario que se estan cargando los datos
    			
        		String nombreEstacion= ""+listEstacion.getSummary();
        		listEstacion.setSummary(getString(R.string.cargando_datos));

        		//INICIO DE HILO
	    		//segundoPlano background=new segundoPlano();
	            //background.run();
        		pd = ProgressDialog.show(this, "", getString(R.string.cargando_datos), true,false);
        		new segundoPlano().start();
	    			
	    		//FIN DE HILO
	            	
            	//listEstacion.setEntries(array_nombres);
            	//listEstacion.setEntryValues(array_codigos);
            	
            	if (array_nombres.length==0)
            	{
            		RETORNO_RESULTADO=RESULT_CANCELED;
            	}
            	
	    		listEstacion.setSummary(nombreEstacion);
    		}
    		
    	}
    	
    	
    	if (key.equals("listEstacion"+"_"+mAppWidgetId)) //Cambia la estaciÃ³n
    	{
    		ListPreference lpEstacion= (ListPreference)findPreference("listEstacion"+"_"+mAppWidgetId);
    		String nombreEstacion= ""+Html.fromHtml(""+lpEstacion.getEntry());
    		lpEstacion.setSummary(nombreEstacion);
    		
    		String estacion=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("listEstacion"+"_"+mAppWidgetId, "EspaÃ±a");
    		final Context contexto = Preferencias.this;
    		Log.d(tag,"Salvando preferencia para el Widget ID="+mAppWidgetId);
    		//Guardamos las preferencias para el ID del widget
        	Preferencias.savePref(contexto,PREF_PREFIJO_CLAVE_NOMBRE,nombreEstacion ,mAppWidgetId);
        	Preferencias.savePref(contexto,PREF_PREFIJO_CLAVE_CODIGO, estacion,mAppWidgetId);
        	RETORNO_RESULTADO=RESULT_OK;
    		//setResult(RESULT_OK);
        	cambioEstacion=true;
    	}
    	
    	
    	if (key.equals("listPeriodo")) //Cambia el periodo de actualizaciÃ³n
    	{
    		//prepare Alarm Service to trigger Widget
    		
    		   Intent intent = new Intent(meteoclimatic.MY_WIDGET_UPDATE);
    		   PendingIntent pendingIntent = PendingIntent.getBroadcast(Preferencias.this, 0, intent, 0);
    		   AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
    		   /*Calendar calendar = Calendar.getInstance();
    		   calendar.setTimeInMillis(System.currentTimeMillis());
    		   calendar.add(Calendar.SECOND, 10);*/
    		   //Calculamos los milisegundos
    		   //String p=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("listPeriodo"+"_"+mAppWidgetId, "30");
    		   String p=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("listPeriodo","30");
    		   int periodo=0;
    		   try
    		   {
    			   periodo=Integer.parseInt(p); //en minutos
    			   /*if (periodo<31)
    			   {
    				   periodo=30;
    			   }*/
    			   periodo=periodo*60; //En segundos
    			   periodo=periodo*1000; //En milisegundos
    		   }
    		   catch(Exception e)
    		   {
    			   Toast.makeText(getBaseContext(),R.string.error_periodo,Toast.LENGTH_LONG).show();
    			   //periodo=(30*60)*1000;
    		   }
    		   alarmManager.cancel(pendingIntent);
    		   Log.d(tag,"Configurando ALARMA para un periodo de "+periodo);
    		   if (periodo>0)
    			   alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), periodo, pendingIntent);
    	}
    	
    	if (key.equals("listTema"+"_"+mAppWidgetId)) //Cambia tema
    	{
        	RETORNO_RESULTADO=RESULT_OK;
    		//setResult(RESULT_OK);
    	}
    	
    	if (key.equals("listTransparencia"+"_"+mAppWidgetId)) //Cambia transparencia
    	{
        	RETORNO_RESULTADO=RESULT_OK;
    		//setResult(RESULT_OK);
    	}
    	
    	if (key.equals("txt_valorAlarmaTempAlta"+"_"+mAppWidgetId)||key.equals("txt_valorAlarmaTempBaja"+"_"+mAppWidgetId)||
        		key.equals("txt_valorAlarmaLluvia"+"_"+mAppWidgetId)	) //Cambia transparencia
        	{
	    		//Aqui hay que controlar que los valores de alarma que se meten son los correctos
        	}
    	

    }
    
    
    /**
     * Guarda una preferencia con la dupla clave valor
     * @param key
     * 		Clave de la preferencia.
     * @param value
     * 		Valor de la preferencia.
     */
    
    // Write the prefix to the SharedPreferences object for this widget
       static void savePref(Context contexto,String clave, String valor,int appWidgetId) {
    	   Log.d("Preferencias","Guardando la clave "+clave+". Valor "+valor+". WidgetId="+appWidgetId);
           SharedPreferences.Editor prefs = contexto.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
           prefs.putString(clave+appWidgetId, valor);
           prefs.commit();
       }

       // Read the prefix from the SharedPreferences object for this widget.
       // If there is no preference saved, get the default from a resource
       static String loadPref(Context contexto,String clave,int appWidgetId) {
           SharedPreferences prefs = contexto.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
           String prefix = prefs.getString(clave+appWidgetId, null);
           return prefix;
//           if (prefix != null) {
//               return prefix;
//           } else {
//               return null;
//           }
       }
       
       static void deleteTitlePref(Context context, int appWidgetId) {
       }

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		
        // Push widget update to surface with newly set prefix
		if (cambioEstacion)
		{
			final Context contexto = Preferencias.this;
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(contexto);
			meteoclimatic.updateAppWidget(contexto, appWidgetManager,widgetId);
		}
		else
		{
			final Context contexto = Preferencias.this;
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(contexto);
			meteoclimatic.updateAppWidgetInterface(contexto, appWidgetManager,widgetId);
		}
		
        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        //setResult(RESULT_OK, resultValue);
        setResult(RETORNO_RESULTADO, resultValue);
        finish();
	}
	
	
	/**
	 * Clase privada que se ejecuta en segundo plano y que carga los datos de las estaciones de la provincia
	 * @author Antonio
	 *
	 */
	private class segundoPlano extends Thread
	{
		String array_vacio[]={};
		
		
    	public void run() {
    		try {
        	   		Log.d("SegundoPlano","URL="+pathSD+provincia);
	        	   
	        	   //Descargamos el fichero para obtener las estaciones de la provincia
        	   		String urlRSSDatosEstacion=preURL+provincia;
        	   		dt=new DownloadTexto(urlRSSDatosEstacion);
	       			dt.setTimeOutConnection(3000);
	       			dt.setTimeOutRead(3000);
	       			if (dt.guardarDatosSD(pathSD+provincia))
	       			{
	       				obtenerDatos od=new obtenerDatos();
		            	od.getEstaciones(pathSD+provincia);
		            	array_nombres=od.getNomEstaciones();
		            	array_codigos=od.getCodEstaciones();
	       			}
	       			else
	       			{
	       				array_nombres=array_vacio;
		            	array_codigos=array_vacio;
		            	//Toast.makeText(getBaseContext(),R.string.fallo_conexion,Toast.LENGTH_LONG).show();
	       			}
	       			
	        	   	listEstacion.setEntries(array_nombres);
	        	   	listEstacion.setEntryValues(array_codigos);
	        	   	pd.dismiss();
               }
           	catch (Exception e) {
               // if something fails do something smart
           	 pd.dismiss();
           	}
       }
	}
	
	
    

}

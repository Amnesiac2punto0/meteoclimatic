/*(C) Copyright

    This file is part of widget Meteoclimatic.
    Autor: Antonio Cristóbal Álvarez Abellán -> acabellan@gmail.com
    
    */

package deeloco.android.meteoclimatic;




import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.ImageView;

import com.mobaloo.mobadme.MobadmeLibActivity;
import com.mobaloo.mobadme.MobadmeLibActivity.BackProcess;

public class tabPantalla extends TabActivity {
	
	MobadmeLibActivity mla;

	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.pestanas);
	    

	    mla = new MobadmeLibActivity(this);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    
	    
	    //Tomar los valores de nombre de estación y código que estan en preferencias

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, datosEstacion.class);
	    Bundle extras=getIntent().getExtras();
	    intent.putExtra("nombre",extras.getString("nombre"));
	    intent.putExtra("codigo", extras.getString("codigo"));

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("datos").setIndicator("Datos Estación").setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs -> Prevision
	    intent = new Intent().setClass(this, prevision.class);   
	    spec = tabHost.newTabSpec("prevision").setIndicator("Previsión").setContent(intent);
	    tabHost.addTab(spec);
	    
	    // 	Do the same for the other tabs -> Prevision
	    intent = new Intent().setClass(this, AcercaDe.class);   
	    spec = tabHost.newTabSpec("Acerca De").setIndicator("Acerca De").setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	    tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 40;
	    tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 40;
	    tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = 40;
	    
	    
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		ImageView iv = (ImageView) this.findViewById(R.id.ImageView);
		mla.flagRefresh=0;
		//BackProcess b = mla.bp("ameteoclimatic/home", this,iv,0,0);
		BackProcess b = mla.bp("agastosmovil/home", this,iv,0,0);
		
		b.execute();

	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		mla.flagRefresh = 1;
		if (mla.timer!=null){
		mla.timer.cancel();
		}

	}
	
	

}


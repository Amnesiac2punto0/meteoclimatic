package deeloco.android.meteoclimatic.net;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class DownloadDrawable extends Download{
	
	private Drawable drawable;

//	public DownloadDrawable(String url,Handler h)
	public DownloadDrawable(String url)
	{
		super(url);
	}
	
	
	public Drawable getData() {
		// TODO Auto-generated method stub
		startDownload();
		return drawable;
	}

	public void startDownload() {
		// TODO Auto-generated method stub
		Bitmap x;
		drawable=null;
		if (this.urlDownload!=null)
		{
			try
			{
				Log.d("DownloadDrawable","Inicio descarga de datos..."+this.urlDownload);
				URL url = new URL(this.urlDownload);
				URLConnection connection = url.openConnection();
				connection.setConnectTimeout(timeOutConnection);
				connection.setReadTimeout(timeOutRead);
				
				HttpURLConnection httpConnection=(HttpURLConnection) connection;
				
				int responseCode=httpConnection.getResponseCode();
				Log.d("DownloadDrawable","Conexi�n HTTP ..."+responseCode);
				
				if (responseCode==HttpURLConnection.HTTP_OK)
				{
					
					InputStream input = connection.getInputStream();
					x = BitmapFactory.decodeStream(input);
				    drawable= new BitmapDrawable(x);
				}
			}
			
			catch (UnknownHostException ue)
			{
				Log.e("DownloadDrawable","UnknownHostException .. "+ue.getMessage());
			}
	
			catch (SocketTimeoutException to)
			{
				Log.e("DownloadDrawable","SocketTimeoutException .. "+to.getMessage());
			}
			catch (Exception e)
			{
				Log.e("DownloadDrawable","Exception .."+e.getMessage()+"..."+e.getClass());
				
			}
		}
	}


	@Override
	public boolean guardarDatosSD(String pathSD) {
		// TODO Auto-generated method stub
		return false;
	}

}

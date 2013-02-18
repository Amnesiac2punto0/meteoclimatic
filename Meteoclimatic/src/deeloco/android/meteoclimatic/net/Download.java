package deeloco.android.meteoclimatic.net;

public abstract class Download {
	
	String urlDownload;
//	Handler handler;
	int timeOutConnection=0;
	int timeOutRead=0;
	
	/**
	 * Constructor de la clase abstracta
	 * @param url
	 * @param h
	 */
//	public Download(String url,Handler h)
	public Download(String url)
	{
		this.urlDownload=url;
//		this.handler=h;
	}
	
	public abstract Object getData();
	
	/**
	 * Método abstracto para guardar los datos descargados en la SD
	 * @return
	 */
	public abstract boolean guardarDatosSD(String pathSD);


	public int getTimeOutConnection() {
		return timeOutConnection;
	}


	public void setTimeOutConnection(int timeOutConnection) {
		this.timeOutConnection = timeOutConnection;
	}


	public int getTimeOutRead() {
		return timeOutRead;
	}


	public void setTimeOutRead(int timeOutRead) {
		this.timeOutRead = timeOutRead;
	}
	
	
	
}

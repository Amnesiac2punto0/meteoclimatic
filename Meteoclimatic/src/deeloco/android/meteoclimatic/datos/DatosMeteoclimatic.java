package deeloco.android.meteoclimatic.datos;

import android.content.Context;

public class DatosMeteoclimatic {
	
	/**
	 * ATRIBUTOS
	 */

	private String preURL="http://meteoclimatic.com/feed/rss/";
	private String codPais="";
	private String codRegion="";
	private String codProvincia="";
	private String codEstacion="";
	private String codCompleto="";
	
	
	/**
	 * MÉTODOS
	 */

	/**
	 * CONSTRUCTORES
	 */
	
	public DatosMeteoclimatic(String codPais,String codRegion,String codProvincia,String codEstacion) {
		// TODO Auto-generated constructor stub
		this.codPais=codPais;
		this.codRegion=codRegion;
		this.codProvincia=codProvincia;
		this.codEstacion=codEstacion;
		this.codCompleto=codPais+codRegion+codProvincia+codEstacion;
	}
	
	public DatosMeteoclimatic(String codCompleto)
	{
		this.codCompleto=codCompleto;
		
	}
	
	/**
	 * Devuelve el código completo del dato al que se está accediendo
	 * @return
	 */
	public String getCodCompleto()
	{
		return this.codCompleto;
	}
}

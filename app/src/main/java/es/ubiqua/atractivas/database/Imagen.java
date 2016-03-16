package es.ubiqua.atractivas.database;

/**
 * Created by administrador on 21/05/14.
 */
public class Imagen {
	private long id;
	private long rss;
	private String url;
	private String texto;

	public Imagen() {
	}

	public Imagen(String textobruto) {
		this.parse( textobruto );
	}

	public long getId() {
		return id;
	}

	public Imagen setId(long id) {
		this.id = id;
		return this;
	}

	public long getRssId() {
		return this.rss;
	}

	public Imagen setRssId(long id) {
		this.rss = id;
		return this;
	}

	public String getTexto() {
		return this.texto;
	}

	public Imagen setTexto(String texto) {
		this.texto = texto;
		return this;
	}

	public String getUrl() {
		return this.url;
	}

	public Imagen setUrl(String url) {
		this.url = url;
		return this;
	}

	public Imagen parse(String textobruto) {
		int ini = textobruto.indexOf( "<img" ) + 10; // añadimos los en número de caracteres de "<img src='"
		int fin = textobruto.indexOf( "/>" ) - 1; // restamos el "'"

		this.setUrl( textobruto.substring( ini, fin ) );
		this.setTexto( textobruto.substring( fin + 3 ) );

		return this;
	}
}

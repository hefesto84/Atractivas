package es.ubiqua.atractivas.youtube.model;

public class YoutubeItem {
	
	private String kind;
	private String etag;
	private String id;
	private YoutubeSnippet snippet;
	
	public YoutubeItem(){
		
	}

	public String getKind() {
		return kind;
	}

	public String getEtag() {
		return etag;
	}

	public String getId() {
		return id;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public void setId(String id) {
		this.id = id;
	}

	public YoutubeSnippet getSnippet() {
		return snippet;
	}

	public void setSnippet(YoutubeSnippet snippet) {
		this.snippet = snippet;
	}
}

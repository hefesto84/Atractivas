package es.ubiqua.atractivas.youtube.model;

public class YoutubeThumbnailEntry {
	
	private String url;
	private int width;
	private int height;
	
	public YoutubeThumbnailEntry(){
		
	}

	public String getUrl() {
		return url;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}

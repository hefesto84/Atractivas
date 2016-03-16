package es.ubiqua.atractivas.youtube.model;

public class YoutubeResource {
	
	private String kind;
	private String videoId;
	
	public YoutubeResource(){
		
	}

	public String getKind() {
		return kind;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
}

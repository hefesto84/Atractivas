package es.ubiqua.atractivas.youtube.model;

import com.google.gson.annotations.SerializedName;

public class YoutubeSnippet {
	
	private String publishedAt;
	private String channelId;
	private String title;
	private String description;
	@SerializedName("thumbnails")
	private YoutubeThumbnail thumbnails;
	private String channelTitle;
	private String playlistId;
	private int position;
	private YoutubeResource resourceId;
	
	public YoutubeSnippet(){
		
	}

	public String getPublishedAt() {
		return publishedAt;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}


	public void setPublishedAt(String publishedAt) {
		this.publishedAt = publishedAt;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public YoutubeThumbnail getThumbnails() {
		return thumbnails;
	}

	public void setThumbnails(YoutubeThumbnail thumbnails) {
		this.thumbnails = thumbnails;
	}

	public String getChannelTitle() {
		return channelTitle;
	}

	public String getPlaylistId() {
		return playlistId;
	}

	public int getPosition() {
		return position;
	}

	public YoutubeResource getResourceId() {
		return resourceId;
	}

	public void setChannelTitle(String channelTitle) {
		this.channelTitle = channelTitle;
	}

	public void setPlaylistId(String playlistId) {
		this.playlistId = playlistId;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setResourceId(YoutubeResource resourceId) {
		this.resourceId = resourceId;
	}

}

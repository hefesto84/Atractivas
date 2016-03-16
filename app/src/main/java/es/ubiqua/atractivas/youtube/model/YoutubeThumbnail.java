package es.ubiqua.atractivas.youtube.model;

import com.google.gson.annotations.SerializedName;

public class YoutubeThumbnail {
	
	private String channelTitle;
	private String playlistId;
	private int position;
	
	@SerializedName("default")
	private YoutubeThumbnailEntry mDefault;
	@SerializedName("medium")
	private YoutubeThumbnailEntry mMedium;
	@SerializedName("high")
	private YoutubeThumbnailEntry mHigh;
	@SerializedName("standard")
	private YoutubeThumbnailEntry mStandard;
	
	public YoutubeThumbnail(){
		
	}

	public YoutubeThumbnailEntry getmDefault() {
		return mDefault;
	}

	public YoutubeThumbnailEntry getmMedium() {
		return mMedium;
	}

	public YoutubeThumbnailEntry getmHigh() {
		return mHigh;
	}

	public YoutubeThumbnailEntry getmStandard() {
		return mStandard;
	}

	public void setmDefault(YoutubeThumbnailEntry mDefault) {
		this.mDefault = mDefault;
	}

	public void setmMedium(YoutubeThumbnailEntry mMedium) {
		this.mMedium = mMedium;
	}

	public void setmHigh(YoutubeThumbnailEntry mHigh) {
		this.mHigh = mHigh;
	}

	public void setmStandard(YoutubeThumbnailEntry mStandard) {
		this.mStandard = mStandard;
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

	public void setChannelTitle(String channelTitle) {
		this.channelTitle = channelTitle;
	}

	public void setPlaylistId(String playlistId) {
		this.playlistId = playlistId;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}

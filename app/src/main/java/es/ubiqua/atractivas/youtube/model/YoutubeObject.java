package es.ubiqua.atractivas.youtube.model;

import java.util.ArrayList;
import java.util.List;

public class YoutubeObject {
	
	private List<YoutubeItem> items;
	private String kind;
	private String etag;
	private String nextPageToken;
	private YoutubePageInfo pageInfo;
	
	public YoutubeObject(){
		items = new ArrayList<YoutubeItem>();
	}

	public List<YoutubeItem> getItems() {
		return items;
	}

	public String getKind() {
		return kind;
	}

	public String getEtag() {
		return etag;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

	public YoutubePageInfo getPageInfo() {
		return pageInfo;
	}

	public void setItems(List<YoutubeItem> items) {
		this.items = items;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

	public void setPageInfo(YoutubePageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
}

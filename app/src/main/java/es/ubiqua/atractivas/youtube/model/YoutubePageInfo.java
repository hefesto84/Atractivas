package es.ubiqua.atractivas.youtube.model;

public class YoutubePageInfo {
	
	private int totalResults;
	private int resultsPerPage;
	
	public YoutubePageInfo(){
		
	}

	public int getTotalResults() {
		return totalResults;
	}

	public int getResultsPerPage() {
		return resultsPerPage;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	public void setResultsPerPage(int resultsPerPage) {
		this.resultsPerPage = resultsPerPage;
	}
}

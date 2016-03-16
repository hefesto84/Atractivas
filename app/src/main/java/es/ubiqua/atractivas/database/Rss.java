package es.ubiqua.atractivas.database;

/**
 * Created by administrador on 06/05/14.
 */
public class Rss {
	private long    id;
	private String  title;
	private String  link;
	private String  image;
	private String  description;
	private String  parentCategory;
	private String  category;
	private String  type;
	private String  pubDate;
	private String  fullText;
	private String  guid;
	private Integer favorito;
	private Integer orden;

	public Rss() {
	}

	public Rss(long id, String title, String link, String image, String description, String parentCategory, String category, String type, String pubDate, String fullText, String guid, Boolean favorito, int orden) {
		this.id             = id;
		this.title          = title;
		this.link           = link;
		this.image          = image;
		this.description    = description;
		this.parentCategory = parentCategory;
		this.category       = category;
		this.type           = type;
		this.pubDate        = pubDate;
		this.fullText       = fullText;
		this.guid           = guid;
		this.favorito       = favorito ? 1 : 0;
		this.orden          = orden;
	}

	public long getId() {
		return this.id;
	}

	public Rss setId(long id) {
		this.id = id;
		return this;
	}

	public String getTitle() {
		return this.title;
	}

	public Rss setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getLink() {
		return this.link;
	}

	public Rss setLink(String link) {
		this.link = link;
		return this;
	}

	public String getImage() {
		return this.image;
	}

	public Rss setImage(String image) {
		this.image = image;
		return this;
	}

	public String getDescription() {
		return this.description;
	}

	public Rss setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getParentCategory() {
		return this.parentCategory;
	}

	public Rss setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
		return this;
	}

	public String getCategory() {
		return this.category;
	}

	public Rss setCategory(String category) {
		this.category = category;
		return this;
	}

	public String getType() {
		return this.type;
	}

	public Rss setType(String type) {
		this.type = type;
		return this;
	}

	public String getPubDate() {
		return this.pubDate;
	}

	public Rss setPubDate(String pubDate) {
		this.pubDate = pubDate;
		return this;
	}

	public String getFullText() {
		return this.fullText;
	}

	public Rss setFullText(String fullText) {
		this.fullText = fullText;
		return this;
	}

	public String getGuid() {
		return this.guid;
	}

	public Rss setGuid(String guid) {
		this.guid = guid;
		return this;
	}

	public Boolean getFavorito() {
		return this.favorito == 1;
	}

	public Rss setFavorito(Boolean favorito) {
		this.favorito = favorito ? 1 : 0;
		return this;
	}

	public Integer getOrden() {
		return this.orden;
	}

	public Rss setOrden(Integer orden) {
		this.orden = orden;
		return this;
	}

	@Override
	public String toString() {
		return this.title;
	}
}

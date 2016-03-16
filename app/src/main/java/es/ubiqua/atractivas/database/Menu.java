package es.ubiqua.atractivas.database;

/**
 * Created by administrador on 02/05/14.
 */
public class Menu {
	private long id;
	private String category;
	private String image;

	public Menu() {
	}

	public Menu(long id, String category, String image) {
		this.id       = id;
		this.category = category;
		this.image    = image;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getParentCategory() {
		return this.category;
	}

	public void setParentCategory( String category ) {
		this.category = category;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return category;
	}
}

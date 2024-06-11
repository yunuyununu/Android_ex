package android;

import lombok.Data;

@Data
public class BookDTO {
	private int book_code;
	private String book_name;
	private String press;
	private int price;
	private int amount;

}

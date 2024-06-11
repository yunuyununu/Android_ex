package com.example.network;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDTO {
    private int book_code;
    private String book_name;
    private String press;
    private int price;
    private int amount;
}

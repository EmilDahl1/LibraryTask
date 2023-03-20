import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Date;

/**
 * Class is a framework for BOOK object.
 */

public class Book {
    public int ISBN;
    public String Title;
    public int inStock;

    public Book(int ISBN, String title, int inStock) {
        this.ISBN = ISBN;
        Title = title;
        this.inStock = inStock;
    }

    public Book (int isbn){
        this.ISBN = isbn;
    }

    public Book() {
    }

    public int getISBN() {
        return ISBN;
    }

    public void setISBN(int ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }
}

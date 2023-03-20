import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.plugins.MockMaker;
import org.mockito.Mockito;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
public class MockTest {

    Functionality func = null;
    public ArrayList<Book> Mockbooks = new ArrayList<>();

    //Maybe not needed
    void setUp () {
        Mockbooks.add(new Book(1234, "title1", 1));
        Mockbooks.add(new Book(1001, "title2", 1));
        Mockbooks.add(new Book(1003, "title3", 1));
        Mockbooks.add(new Book(1004, "title4", 1));
        Mockbooks.add(new Book(1002, "title5", 1));
    }

    @Mock
    Book book;


  @Test
  void MockBookExists()  {
      Functionality func = Mockito.mock(Functionality.class);

      Mockito.when(func.bookExist(1234)).thenReturn(true);
      assertEquals(true, func.bookExist(1234));

      Mockito.when(func.bookExist(1234)).thenReturn(false);
      assertEquals(false, func.bookExist(1234));
  }


    @Test
    void MockBookInStorage()  {
        Functionality func = Mockito.mock(Functionality.class);

        Mockito.when(func.bookInStorage(1234)).thenReturn(1);
        assertEquals(1, func.bookInStorage(1234));
    }

    @Test
    void MockCheckUserType() {
        Functionality func = Mockito.mock(Functionality.class);

        Mockito.when(func.checkUserType(1234)).thenReturn(3);
        assertEquals(3, func.checkUserType(1234));
    }

    @Test
    void MockUserHasBooks() {
        Functionality func = Mockito.mock(Functionality.class);
        Mockito.when(func.userHasBooks(1234)).thenReturn(3);
        assertEquals(3, func.userHasBooks(1234));
    }


    @Test
    void MockCheckSuspension() {
        Functionality func = Mockito.mock(Functionality.class);

        Mockito.when(func.checkSuspension(1234)).thenReturn(true);
        assertEquals(true, func.checkSuspension(1234));
    }

}

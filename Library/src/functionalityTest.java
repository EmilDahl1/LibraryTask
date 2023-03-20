import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




public class functionalityTest {



      @Test
    void TestUserExists() {
        Functionality func = new Functionality();
        boolean result = func.userExist(1234);
        assertTrue(result);
    }


    @Test
    void testUserSuspended () {
        Functionality func = new Functionality();
        boolean result =func.checkSuspension(1234);
        assertEquals(result, true);
    }

    @Test
    void testBookExist () {
        Functionality func = new Functionality();
        boolean actual = func.bookExist(1001);
        assertEquals(true, actual);

    }

    @Test
    void testBookInStorage () {
        Functionality func = new Functionality();
        int actual = func.bookInStorage(1001);
        assertEquals(3, actual);

    }


    @Test
    void testSubtractBook() {
        Functionality func = new Functionality();
        int actual = func.subtractBook(1001);
        int expected = func.bookInStorage(1001);
        assertEquals(expected, actual);
    }

    @Test
    void testAddBook() {
        Functionality func = new Functionality();
        int actual = func.addBook(1001,1);
        int expected = func.bookInStorage(1001);
        assertEquals(expected, actual);
    }


    @Test
    void testUserHasBook() {
        Functionality func = new Functionality();
        int actual = func.userHasBooks(1222);
        int expected = 1;
        assertEquals(expected, actual);
    }

    @Test
    void testCheckUserType() {
        Functionality func = new Functionality();
        int actual = func.checkUserType(9);
        int expected = 5;
        assertEquals(expected, actual);
    }

    @Test
    void testBorrowBook() {
        Functionality func = new Functionality();
        int beforeBorrow = func.userHasBooks(9);
        func.borrowBook(1005,9);
        int afterBorrow = func.userHasBooks(9);
        assertEquals(afterBorrow, beforeBorrow+1);
    }

    @Test
    void testReturnBook() {
        Functionality func = new Functionality();
        int beforeReturn = func.userHasBooks(9);
        func.returnBookHelper(9, 1005);
        int afterReturn = func.userHasBooks(9);
        assertEquals(afterReturn, beforeReturn-1);
    }


}

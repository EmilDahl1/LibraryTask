import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class holds all actions that write to database or offer underlying functionality.
 *
 */

public class Functionality {
    private static final Logger logger = LogManager.getLogger(Functionality.class);
    Scanner scan = new Scanner(System.in);
    String type;
    public ArrayList <Book> books = new ArrayList<>();

    int UserID; // 4 numbers
    int limit;
    /** This is set depending on what type the user is
        UnderGrad = TYPE 1 can borrow 3
        MasterStudent = TYPE 2 can borrow 5
        PHD = TYPE 3  can borrow 7
        Teacher = TYPE 4 can borrow 10
        Libraian when implemented will have type 5 and have limit 100 **/

    String url = "jdbc:mysql://localhost:3306/testning";
    String user = "root";
    String pass = "villebagare";

    public Functionality() {
    }

    /**
     * Unsure of usage.
     * Fills an array with books in database.
     * @return
     */
    public ArrayList<Book> getBooks() {

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement ps = conn.prepareStatement("Select * from Books");
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                int bISBN = rs.getInt(1);
                String title = rs.getString(2);
                int inStock = rs.getInt(3);
                Book book = new Book(bISBN, title, inStock);
                books.add(book);
                System.out.println("yep");
            }

        } catch (SQLException e) {
            logger.error("Failed to connect to server @getBooks");
        }
        return books;
    }

    /**
     * Method allows librarian to remove a certain suspension.
     * Can only be accessed through admin menu.
     */
    public void removeSuspension () {
        System.out.println("Please enter ID of user you wish to unsuspend.");
        int userID = scan.nextInt();
        try {
            Connection connection = DriverManager.getConnection(url, user, pass);
            PreparedStatement st = connection.prepareStatement("delete from suspendedusers where UserID = ?");
            st.setInt(1, userID);
            st.executeUpdate();
            removeStrikes(userID);
            returnUsersBooks(userID);
            System.out.println("Suspension of user: " + userID + " has been removed.");
            System.out.println("User " + userID + " can now log in.");


        } catch (Exception e) {
            logger.error("Failed to connect to server @removeSuspension");
        }

    }


    /**
     * When a suspension is revoked, all books user has loaned is returned.
     * => They are removed from the loanedbooks table.
     * => addBook() is used to set the inventory correctly so new users can loan the books.
     * @param userID
     */
    public void returnUsersBooks (int userID) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement ps = conn.prepareStatement("Select * from loanedbooks where userid = ?");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            ArrayList<Book> list = new ArrayList<>();
            while(rs.next()) {
                int bISBN = rs.getInt(1);
                addBook(bISBN, 1);
                Book bookToAdd = new Book(bISBN);
                list.add(bookToAdd);
            }
        } catch (SQLException e) {
            logger.error("Failed to connect to server @returnUserBooks part1");
        }

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            PreparedStatement ps = conn.prepareStatement("Delete from loanedbooks where userid = ?");
            ps.setInt(1, userID);
             ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to connect to server @returnUserBooks part2");
        }








    }

    /**
     * Writes to DATABASE and first uses bookExist() to see if book exists and is available.
     * If book exists then writes to DATABASE and removes 1 from the correct books "stock" or "numberInStock"
     * -> then ADDS the book combined with the users USERID to the table loanedBooks.
     * LoanDate and ReturnDate is calculated in JAVA and written to DATABASE.
     *
     * @param ISBN
     * @param UserID
     */
    public void borrowBook(int ISBN, int UserID) {

        /**
         * This method must call "bookExist" to check that more than 0 is in stock
         */

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            LocalDate locald = LocalDate.now();
            Date local = java.sql.Date.valueOf(locald);

            LocalDate returnDate = LocalDate.now().plusDays(7);
            Date returnD = java.sql.Date.valueOf(returnDate);

            PreparedStatement ps = conn.prepareStatement("Insert into loanedbooks VALUES (?, ?, ?, ?)");
            ps.setInt(1, ISBN);
            ps.setInt(2, UserID);
            ps.setDate(3, (java.sql.Date) local);
            ps.setDate(4, (java.sql.Date) returnD);

            ps.executeUpdate();
            subtractBook(ISBN);

        } catch (SQLException e) {
            logger.error("Failed to connect to server @borrowBook");
        }

    }

    /**
     * Returns a book
     * ->deletes the loanedBooks instance.
     * ->adds book back in stock
     */
    public void returnBook () {
        System.out.println("Please enter UserID of loaner.");
        int userID = scan.nextInt();
        System.out.println("Please enter ISBN of book to return.");
        int ISBN = scan.nextInt();
        if (!loanExist(userID, ISBN)) {
            main rerun = new main();
            rerun.libraryMenu();
        }

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            PreparedStatement ps = conn.prepareStatement("select * from loanedBooks where userID = ? and ISBN = ?");

            ps.setInt(1, userID);
            ps.setInt(2, ISBN);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                returnBookHelper(userID, ISBN);
            }
            } catch (SQLException e) {
            logger.error("Failed to connect to server @returnBook");
        }
    }


    /**
     * Helper class
     * @param userID
     * @param ISBN
     * @return
     */

    public boolean returnBookHelper (int userID, int ISBN) {
        boolean test = false;
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            PreparedStatement ps = conn.prepareStatement("delete from loanedBooks where userID = ? and ISBN = ?");

            ps.setInt(1, userID);
            ps.setInt(2, ISBN);
            ps.executeUpdate();
            System.out.println("Book ISBN: " + ISBN + " from user: " + userID + " has been returned.");
            addBook(ISBN, 1);
            test = true;
        } catch (SQLException e) {
            logger.error("Failed to connect to server @returnBookHelper");
        }


        return test;
    }

    /**
     * Writes to DATABASE do check if book exist,
     * @param ISBN
     * @return bool
     *
     */
    public boolean bookExist(int ISBN) {
        boolean test = false;
        try {
            Connection connection = DriverManager.getConnection(url, user, pass);
            PreparedStatement st = connection.prepareStatement("select * from books where ISBN = ?");

            st.setInt(1, ISBN);


            ResultSet rs = st.executeQuery();

            if(rs.next()) {
                test = true;
                System.out.println("Book with ISBN " + ISBN + " exists");
                System.out.println("There are currently " + bookInStorage(ISBN) + " available.");
            } else {
                logger.info("ISBN: " + ISBN + " faulty search.");
            }
            connection.close();

        } catch (Exception e) {
            logger.error("Failed to connect to server @bookExist");
        }

        return test;
    }


    /**
     * Checks if there is an existing loan.
     * @param ISBN
     * @param userID
     * @return boolean test
     */

    public boolean loanExist(int userID, int ISBN) {
        boolean test = false;
        try {
            Connection connection = DriverManager.getConnection(url, user, pass);
            PreparedStatement st = connection.prepareStatement("select * from loanedbooks where ISBN = ? and UserID = ?");

            st.setInt(1, ISBN);
            st.setInt(2, userID);


            ResultSet rs = st.executeQuery();

            if(rs.next()) {
                test = true;
            } else {
                System.out.println("This loan does not exist");
                logger.error("Book does not exist @bookExist");
            }
            connection.close();

        } catch (Exception e) {
            logger.error("Failed to connect to server @loanExist");
        }

        return test;
    }

    /**
     * Method checks if there is titles of the book with the corresponding ISBN inStock i.e inStock>0
     * @param ISBN
     * @returns inStock: number of books in stock for the correct ISBN
     */
    public int bookInStorage (int ISBN) {
        int inStock = 0;
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement s = conn.prepareStatement("Select inStock from books where ISBN = ?");
            s.setInt(1, ISBN);
            ResultSet r = s.executeQuery();
            if (r.next()) {
                inStock = r.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Something went wrong");
        }

        return inStock;
    }

    /**
     * This method is called from borrowBook to subtract one from InStock
     *
     * @param ISBN
     * @return used for testing
     */

    public int subtractBook(int ISBN) {
       int subtractedInStock;
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement ps = conn.prepareStatement("update books set inStock = inStock - 1 where ISBN = ?");
            ps.setInt(1, ISBN);

            ps.executeUpdate();
            conn.close();
            System.out.println("Storage for book with ISBN " + ISBN + " has been subtracted");

        } catch (SQLException e) {
            logger.error("Failed to connect to server @subtractBook");
        }
        subtractedInStock = bookInStorage(ISBN);
        return subtractedInStock;
    }

    /**
     * Method writes to DATABASE and adds number to INSTOCK of book corresponding to ISBN.
     *
     * @param ISBN specifies book
     * @param number specifices how many to add
     */
    public int addBook(int ISBN, int number) {
        int addedNumberInStock;
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement ps = conn.prepareStatement("update books set inStock = inStock + ? where ISBN = ?");
            ps.setInt(1, number);
            ps.setInt(2, ISBN);

            ps.executeUpdate();
            conn.close();
            System.out.println("Storage for book with ISBN: " + ISBN + " has been appended by " + number);

        } catch (SQLException e) {
            logger.error("Failed to connect to server @addBook");
        }
        addedNumberInStock = bookInStorage(ISBN);

        return addedNumberInStock;
    }


    /**
     * Used by librarian.
     */
    public void addBookManually() {

        System.out.println("Please enter ISBN: ");
        int ISBN = scan.nextInt();
        System.out.println("Please enter number of books to add:");
        int number = scan.nextInt();

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement ps = conn.prepareStatement("update books set inStock = inStock + ? where ISBN = ?");
            ps.setInt(1, number);
            ps.setInt(2, ISBN);

            ps.executeUpdate();
            conn.close();
            System.out.println("Storage for book with ISBN: " + ISBN + " has been appended by " + number);

        } catch (SQLException e) {
            logger.error("Failed to connect to server @addBookManually");
        }
    }

    /**
     * Method checks what type of user is by writing to DATABASE.
     * @param userID
     * @return type
     */

    public int checkUserType(int userID) {
        int type = 0;

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement ps = conn.prepareStatement("select TypeOfUser from users where UserID = ?");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                type = rs.getInt(1);
            }
            System.out.println(type);

        } catch (SQLException e) {
            logger.error("Failed to connect to server @checkUserType");
        }

        return type;
    }


    /**
     * Method writes to DATABASE to check first if user have loaned books,
     * if -> user has loaned books: write to DATABASE and return books to stock
     * else remove user by deleting from USER table where ISBN = param
     * Alo correct the inStock value of the books that user had loaned.
     * @param userID
     */

    public void deleteUser(int userID) {
        int books = userHasBooks(userID);

        if (books != 0) {
            main rerun = new main();
            rerun.libraryMenu();
        } else
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {

                PreparedStatement s = conn.prepareStatement("Select borrowedBooks from users where UserID = ?");
                s.setInt(1, userID);
                ResultSet result = s.executeQuery();

                result.first();


                PreparedStatement ps = conn.prepareStatement("Delete from users where UserID = ?");
                ps.setInt(1, userID);

                ps.executeUpdate();

                logger.info("User with UserID: " +  userID + " has been deleted.");
            } catch (SQLException e) {
                logger.error("Failed to connect to server @deleteUser");
            }
    }


    /**
     * used by librarian only.
     */
    public void deleteUserManually() {

        System.out.println("Please enter userID to be deleted.");
        int userID = scan.nextInt();
        int books = userHasBooks(userID);

        if (books != 0) {
            main rerun = new main();
            rerun.adminMenu();
        }
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                PreparedStatement ps = conn.prepareStatement("Delete from users where UserID = ?");
                ps.setInt(1, userID);

                ps.executeUpdate();

                System.out.println("User with UserID: " +  userID + " has been deleted.");
            } catch (SQLException e) {
                logger.error("Failed to connect to server @deleteuserManually");
            }
    }

    /**
     * Method writes to database and asks how many books user has borrowed.
     * This can be done by checking instanses of userID in table LOANEDBOOKS or the column
     * BORROWEDBOOKS in the USER table depending on preference.
     *
     * @param userID
     * @return number of borrowed books user has
     *
     */

    public int userHasBooks(int userID) {
        int tempInt = 0;

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement s = conn.prepareStatement("Select * from loanedbooks where UserID = ?");
            s.setInt(1, userID);
            ResultSet result = s.executeQuery();


            while (result.next()) {
                tempInt++;
            }

        } catch (SQLException e) {
            logger.error("Failed to connect to server @userHasBooks");
        }


        System.out.println("User with userID: " + userID + " has " + tempInt + " outstanding loans.");
        if (tempInt>0) {
            System.out.println("Outstanding loans needs to be returned before deletion/suspension.");
        }

        return tempInt;
    }


    /**
     * Writes to DATABASE and creates book.
     * if  -> this books ISBN already exist add one to inStock in DATABASE.
     * else column is created and inStock is set to one
     * This method needs to be checked by userType (librarian) and bookExist to not make doubles
     */
    public void createBook() {

        System.out.println("Please enter book TITLE: ");
        String title = scan.next();
        System.out.println("Please enter ISBN of " + title);
        int ISBN = scan.nextInt();
        System.out.println("Please enter amount of " + title + " to add.");
        int inStock = scan.nextInt();

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement s = conn.prepareStatement("Insert into books (ISBN, TITLE, InStock) VALUES (?,?,?)");
            s.setInt(1, ISBN);
            s.setString(2, title);
            s.setInt(3, inStock);
            s.executeUpdate();

            System.out.println("The book " + title + " has been created and " + inStock + " are now in storage.");

        } catch (SQLException e) {
            logger.error("Failed to connect to server @createBook");
        }
    }







    /**
     * Writes to DATABASE and checks if USERID is suspended (which means an instance of this user exists in the suspendedUsers table)
     * This method ONLY checks for a certain userID.
     *
     * @param
     */
    public boolean checkSuspension(int userID) {
        boolean test = false;
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement s = conn.prepareStatement("select * from suspendedusers where UserID = ?");
            s.setInt(1, userID);
            ResultSet rs = s.executeQuery();


            if (rs.next()) {
                System.out.println("User with ID: " + rs.getInt(1) + " is currently suspended.");
                java.sql.Date date = rs.getDate("suspensionDate");
                System.out.println("User is suspended untill: " + date);
                test = true;
              //  System.exit(0);
            } else {
                System.out.println("User with ID: " + userID + " is not suspended.");
            }

        } catch (SQLException e) {
            logger.error("Failed to connect to server @checkSuspension");
        }

        return test;
    }

    /**
     * Method is used to check all loaned items and add a strike to a user if a loan has exeeded returndate.
     */
    public void upDateSuspensions() {
        LocalDate today = LocalDate.now();
        int userID;
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement s = conn.prepareStatement("SELECT * FROM loanedbooks");
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                userID = rs.getInt(2);
                java.sql.Date sqlDate = rs.getDate(4);
                LocalDate date = sqlDate.toLocalDate();
                if (today.isAfter(date)) {
                    System.out.println("test");
                    addStrike(userID);
                }
            }
            System.out.println("Strikes have been updated.");

        } catch (Exception e) {
            logger.error("Failed to connect to server @upDateSuspensions");
        }
    }




    /**
     * Adds a strike to the specified user.
     * @param userID
     */
    public void addStrike (int userID){
        boolean test = false;

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            PreparedStatement ps = conn.prepareStatement("update users set strikes = strikes + 1 where UserID = ?");
            ps.setInt(1, userID);
            ps.executeUpdate();
            test = getStrikes(userID);
            if (test) {
                suspend(userID);
            }

        } catch (SQLException e) {
            logger.error("Failed to connect to server @addStrike");
        }



    }

    /**
     * Retrieves the amount of strikes user has.
     * @param userID
     * @return boolean depending on amount of strikes x><2
     */
    public boolean getStrikes (int userID){
         boolean test = false;

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            PreparedStatement ps = conn.prepareStatement("Select strikes from Users where UserID = ?");
            ps.setInt(1, userID);
            ResultSet result =  ps.executeQuery();

            if ( result.next()) {
                int t = result.getInt(1);
                System.out.println("User: " + userID + " has " + t + " strikes.");
                if (t >= 2) {
                    test = true;
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to connect to server @getStrikes");
        }
        return test;
    }

    /**
     * Set strikes at 0 for specified user, this is used when user is suspended.
     * Otherwise the users strikes would cause them to be suspended indefinately as suspensions occour at 2 strikes.
     * @param userID
     */

    public void removeStrikes (int userID){

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            PreparedStatement ps = conn.prepareStatement("update users set strikes = 0 where UserID = ?");
            ps.setInt(1, userID);
            ps.executeUpdate();


        } catch (SQLException e) {
            logger.error("Failed to connect to server @removeStrikes");
        }
    }


    /**
     * Suspends user, return loaned book from user which means
     * -> deletes loanedBook instance in correct table
     * -> adds one to inStock to the correct book
     * -> adds user to SuspendedUser table.
     *
     * @param userID
     */
    public void suspend(int userID) {

        /**
         * Method needs returnBook method to function properly.
         * This part only suspends user and does not return loaned books.
         * -> line with UserID and suspensionDate + 15 is added to table suspendedUsers.
         */

        userHasBooks(userID);

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            LocalDate suspensionDate = LocalDate.now().plusDays(15);
            java.sql.Date releaseDate = java.sql.Date.valueOf(suspensionDate);

            PreparedStatement ps = conn.prepareStatement("insert into suspendedusers (UserID, suspensionDate) VALUES (?, ?)");
            ps.setInt(1, userID);
            ps.setDate(2, releaseDate);
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("Failed to connect to server @suspend");
        }
    }

    /**
     * Usable by librarian only.
     */
    public void suspendManually() {

        System.out.println("Please enter UserID to suspend.");
        int userID = scan.nextInt();

        int loans = userHasBooks(userID);

        if (loans > 0) {
            main rerun = new main();
            rerun.adminMenu();
        }

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            LocalDate suspensionDate = LocalDate.now().plusDays(15);
            java.sql.Date releaseDate = java.sql.Date.valueOf(suspensionDate);

            PreparedStatement ps = conn.prepareStatement("insert into suspendedusers (UserID, suspensionDate) VALUES (?, ?)");
            ps.setInt(1, userID);
            ps.setDate(2, releaseDate);
            ps.executeUpdate();
            System.out.println("User " + userID + " is now suspended untill:" + releaseDate);
            main rerun = new main();
            rerun.adminMenu();

        } catch (SQLException ex) {
            logger.error("Failed to connect to server @suspendManually");
        }
    }


    /**
     * Writes to DATABASE and checks if user exists in the USER table.
     *
     * @param userID
     * @return boolean test
     */
    public boolean userExist(int userID) {
        boolean test = false;

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement ps = conn.prepareStatement("select * from users where userID = ?");
            ps.setInt(1, userID);
            ResultSet result = ps.executeQuery();

            if (result.next()) {
               test = true;
            }

        } catch (SQLException e) {
            logger.error("Failed to connect to server @userExists");
        }

        return test;
    }

    /**
     * Method writes to DATABASE and creates a library profile using user input from console.
     * User gets to choose what type of role the profile will have, in a more advanced system this would not be chosen by user and instead set by library employee.
     * User also chooses what their UserID will be, if the UserID is occupied they cannot create their account and gets sent to the first step of the menu process.
     */

    public void createUser() {

        System.out.print("Insert your last four digits in your personal number: ");
        int userID = scan.nextInt();

        userExist(userID);

        /**This chould be done by using some kind of acesscode that is given to a student etc that needs to be checked.
         * These codes could be stored in the DB, and role could be chosen automatically using the code.
         * These codes would be handed out when an individual registers/gets enrolled at the school.
         */

        System.out.println("Insert type of user: ");
        System.out.println("1: Undergrad");
        System.out.println("2: Master student");
        System.out.println("3: PHD");
        System.out.println("4: Teacher");
        System.out.println("5: Librarian");
        int typeOfUser = scan.nextInt();

        switch (typeOfUser) {
            case 1:
                limit = 3;
                type = "undergrad.";
                break;
            case 2:
                limit = 5;
                type = "Master student.";
                break;
            case 3:
                limit = 7;
                type = "PHD.";
                break;
            case 4:
                limit = 10;
                type = "Teacher.";
                break;
            case 5:
                limit = 100;
                type = "Librarian.";
                break;
        }

        System.out.println(typeOfUser+ " " + userID+ " " + limit);

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            PreparedStatement s = conn.prepareStatement("insert into users (TypeOfUser, userID, borrowedBooks, loanLimit, strikes, isSuspended) VALUES (?,?,?,?,?,?)");
            s.setInt(1, typeOfUser);
            s.setInt(2, userID);
            s.setInt(3, 0);
            s.setInt(4, limit);
            s.setInt(5, 0);
            s.setInt(6, 0);
            s.executeUpdate();

            logger.info("The registered users userID is: " + userID + " and is the following type of user:" + type +  " Maximum amount of loans is " + limit);
            main rerun = new main();
            rerun.libraryMenu();
        } catch (SQLException e) {
            logger.error("Failed to connect to server @createUser");
        }
    }
}

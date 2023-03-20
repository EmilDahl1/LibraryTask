import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;
import org.apache.logging.log4j.core.LoggerContext;

public class main {


    /**
     * Hub that both acts as main aswell as holding all in-console menu choices for the user.
     */

        public static Scanner scan = new Scanner(System.in);
       private static final Logger logger = LogManager.getLogger(main.class);
        static Functionality run1 = new Functionality();

        public static void main(String[] args) {
            main run = new main();
            run.startProgram();
        }

        public void startProgram () {
            char choicecVariable;
            int userID;
            boolean test;
            System.out.println("Are you a registered user? (Y/N)");
            choicecVariable = scan.next().charAt(0);


            if (choicecVariable == 'Y' || choicecVariable == 'y') {
                System.out.println("Please enter UserID");
                userID = scan.nextInt();

                Functionality run = new Functionality();
                boolean r = run.userExist(userID);
                if(r) {
                    test = run.checkSuspension(userID);
                    if (!test) {
                        logger.info("User " + userID + " logged in.");
                        libraryMenu();
                    }
                } else {
                    System.out.println("UserID is not registered, please create your profile.");
                    logger.info("Faulty login attempt :" + userID);
                    startProgram();
                }

            } else if (choicecVariable == 'N' || choicecVariable == 'n') {
                run1.createUser();
            }
        }

        public  void libraryMenu() {
            int choiceVariable;

            do {
                System.out.println("\nLibrary of Linnéuniversitetet");
                System.out.println("============================");
                System.out.println("1. Return book");
                System.out.println("2. Loan book");
                System.out.println("3. Administration");
                System.out.println("4. Delete user");
                System.out.println("5. End program");

                System.out.println("Choose a number: ");
                choiceVariable = scan.nextInt();

                switch (choiceVariable){
                    case 1:
                        run1.returnBook();
                        break;
                    case 2:
                        int ISBN;
                        int userID;
                        System.out.println("Enter ISBN of desired book.");
                        ISBN = scan.nextInt();
                        boolean test = run1.bookExist(ISBN);
                        if (!test) {
                            libraryMenu();
                        }
                        System.out.println("Enter your user ID.");
                        userID = scan.nextInt();
                        run1.borrowBook(ISBN, userID);
                        System.out.println("You have sucessfully loaned the book with ISBN: " + ISBN);
                        LocalDate returnDate = LocalDate.now().plusDays(7);
                        Date returnD = java.sql.Date.valueOf(returnDate);
                        System.out.println("Your return date is: " + returnD);
                        break;
                    case 3:
                        System.out.println("Please enter credentials.");
                        System.out.println("UserID: ");
                        int UserID = scan.nextInt();
                        int userType = run1.checkUserType(UserID);

                        if (userType != 5) {
                            System.out.println("You do not have access to administration.");
                            libraryMenu();
                        }
                        System.out.println("Please enter password: ");
                        int tempPass = scan.nextInt();
                        if (tempPass != 1111) {
                            System.out.println("Wrong password refer to IT coordinator.");
                            libraryMenu();
                        } else
                           adminMenu();
                        break;
                    case 4:
                        System.out.println("Enter ID of user that you wish to delete.");
                        userID = scan.nextInt();
                        run1.deleteUser(userID);

                }
            } while (choiceVariable != 5);
            System.out.println("Cya!");
            System.exit(0);

        }

        public void adminMenu() {
            System.out.println("Welcome to administration.");
            System.out.println("1. Update suspensions.");
            System.out.println("2. Add book in Stock");
            System.out.println("3. Delete user.");
            System.out.println("4. Create book.");
            System.out.println("5. Suspend user.");
            System.out.println("6. Create user.");
            System.out.println("7. Remove suspension.");
            System.out.println("8. Main menu.");
            int choice = scan.nextInt();

            switch (choice) {
                case 1: run1.upDateSuspensions();
                       adminMenu();
                    break;
                case 2: run1.addBookManually();
                    adminMenu();
                    break;
                case 3: run1.deleteUserManually();
                    adminMenu();
                    break;
                case 4: run1.createBook();
                    adminMenu();
                    break;
                case 5: run1.suspendManually();
                    adminMenu();
                    break;
                case 6: run1.createUser();
                    adminMenu();
                    break;
                case 7: run1.removeSuspension();
                  break;
                case 8: libraryMenu();
                     break;

            }
        }
    }


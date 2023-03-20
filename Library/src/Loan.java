import java.sql.Date;


/**
 * Class is a framework for LOAN object.
 */
public class Loan {

    public int ISBN;
    public int UserID;
    public Date loanDate;
    public Date returnDate;

    public Loan(int ISBN, int userID, Date loanDate, Date returnDate) {
        this.ISBN = ISBN;
        UserID = userID;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }


    public int getISBN() {
        return ISBN;
    }

    public void setISBN(int ISBN) {
        this.ISBN = ISBN;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}


/**
 * Class is a framework for USER object.

 */


public class User {

    public int typeOfUser;
    public int UserID;
    public int borrowedBooks;
    public int loanLimit;
    public int suspended;

    public boolean isSuspended;

    public User(int typeOfUser, int userID, int borrowedBooks, int loanLimit, int suspended, boolean isSuspended) {
        this.typeOfUser = typeOfUser;
        UserID = userID;
        this.borrowedBooks = borrowedBooks;
        this.loanLimit = loanLimit;
        this.suspended = suspended;
        this.isSuspended = isSuspended;
    }

    public User() {
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public void setSuspended(boolean suspended) {
        isSuspended = suspended;
    }

    public int getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(int typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(int borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public int getLoanLimit() {
        return loanLimit;
    }

    public void setLoanLimit(int loanLimit) {
        this.loanLimit = loanLimit;
    }

    public int getSuspended() {
        return suspended;
    }

    public void setSuspended(int suspended) {
        this.suspended = suspended;
    }


}

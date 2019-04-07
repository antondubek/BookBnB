package server;

/**
 * Class to represent a lender of a particular book
 */
public class Lender {
    private String ID;
    private String name;
    private String city;
    private String loanLength;


    //Getters
    public String getName(){
        return name;
    }

    public String getLoanLength(){
        return loanLength;
    }

    public String getCity(){
        return city;
    }

    public String getID() { return ID; }

    //setters
    public void setLoanLength(String loanLength){
        this.loanLength = loanLength;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCity(String city){
        this.city = city;
    }

    public void setID(String ID) { this.ID = ID; }

    /**
     * Lender constructor.
     * @param name name of the registered user who is lending a book
     * @param loanLength length of time the user is willing to lender the book for
     * @param city city where the user lends his books.
     */
    public Lender(String ID, String name, String city, String loanLength ){
        this.name = name;
        this.loanLength = loanLength;
        this.city = city;
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "name: " + this.name + " loanLength: " + this.loanLength + " city: " + this.city;
    }




}


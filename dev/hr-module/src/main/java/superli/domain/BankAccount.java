package superli.domain;

public class BankAccount {
     private String OwnerName;
    private String bankName;
    private int accountNumber;


    public BankAccount(String bankName, int accountNumber, String accountOwnerName) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.OwnerName = accountOwnerName;
    }
    public String getBankName() {
        return bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public int getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getAccountOwnerName() {
        return OwnerName;
    }
    public void setAccountOwnerName(String accountOwnerName) {
        this.OwnerName = accountOwnerName;
    }



}

package superli.domain;

public class StoreBranch {

    private int branchId;
    private String name;
    private String address;

    public StoreBranch(int branchId ,String name, String address){
        this.branchId = branchId;
        this.name = name;
        this.address = address;
    }
    public int getBranchId(){
        return branchId;
    }
    public String getName(){
        return name;
    }
    public String getAddress(){
        return address;
    }
}

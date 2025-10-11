package Classes;

public class Interface {

    public static void init(){
        System.out.println("\nFinal Project Banking System:\n\n*IMPORTANT* This system uses encryption software and file storage for data storage. For the sake of the assignment the code has been adjusted to accept \"TEACHER\" as access to any functions within the bank and will allow you to store the bank(s). (To revert to normal functioning, remove the lines marked with the \"/*SECURITY OVERRIDE*/\" comment). However, if you store the bank(s) with a different password and do not remember what you inputted, you will NOT be able to reload the banks, end of story. Please keep this in mind. */IMPORTANT*");
        System.out.println("\n\nHello and welcome to Brendan's banking system!");
    }
    public static void Menu(){
        //prints menu
        System.out.println("\nPlease enter a number and hit \"enter/return\":");
        System.out.println("Press \"1\" to create a bank");
        System.out.println("Press \"2\" to select a bank (allowing you to work with its members)");
        System.out.println("Press \"3\" to deselect the selected bank");
        System.out.println("Press \"4\" to create a bank account (for 4-9 a bank must be selected)");
        System.out.println("Press \"5\" to remove a bank account");
        System.out.println("Press \"6\" to withdraw from a bank account");
        System.out.println("Press \"7\" to deposit to a bank account");
        System.out.println("Press \"8\" to check a bank account balance");
        System.out.println("Press \"9\" to print out all members of the currently selected bank");
        System.out.println("Press \"10\" to load a bank from a file (must first have been saved) (please try to load your banks in order to prevent overwriting them)");
        System.out.println("Press \"11\" to save the current bank to a file (must be done before exiting for bank to be loadable)");
        System.out.println("\tPlease note, only one bank can be stored per bank number at a time. Writing a new bank to the number will overwrite the previously stored bank. When loading a bank, the system will try to conserve the bank number that a bank was stored with; however, if a bank with a high number is loaded early it will be placed into a low number slot and will be overwritten when a lower number bank is loaded.");
        System.out.println("Press \"12\" to delete a specific bank save file. This bank will be deleted FOREVER (a really long time)");
        System.out.println("Press \"13\" to delete all currently saved banks (this does not destroy currently loaded banks only their save files). These banks will be deleted FOREVER (a really long time)"); /*SECURITY OVERRIDE*/
        System.out.println("\nType \"EXIT\" to close the system");
    }
    //basic prompts
    public static void createAccount(){
        System.out.println("Creating account. Please input your name. Additionally, you may enter an initial balance on a separate line. Even if you co not chose do to so, you must hit enter twice:");
    }
    public static void removeAccount(){
        System.out.println("Removing account. Please input the account routing number and pin, each on their own line:");
    }
    public static void DepositMoney(){
        System.out.println("Depositing money. Please input your routing number, passcode, and the amount to deposit, each on their own line:");
    }
    public static void WithdrawMoney(){
        System.out.println("Withdrawing money. Please input your routing number, passcode, and the amount to withdraw, each on their own line:");
    }
    public static void checkBalance(){
        System.out.println("Checking balance. Please input your routing number and passcode, each on their own line:");
    }
    public static void createBank(){
        System.out.println("Creating bank. Please input the main passcode to the bank. (This is required to retrieve the bank from storage!):");
    }
    public static void loadBank(){
        System.out.println("Loading bank. Please input the bank number, (The number is the order you created it in (starting at 0) when you originally created it) and main passcode. Please put each on their own line:"); 
    }
    public static void selectBank(){
        System.out.println("Selecting bank. Please input the bank number as a plain number. The number is the order you created it in (starting at 0) or the number the bank was stored with:");
    }
    public static void showMembers(){
        System.out.println("Printing Members. Please input the main passcode for the bank:");
    }

    //basic prints
    public static void saveBank(){
        System.out.println("Saving bank.");
    }
    public static void DeselectBank(){
        System.out.println("Bank deselected.");
    }
    public static void exit(){
        System.out.println("Thank you for your business! Please come again!");

    }
       
    //outputs
    public static void Status(boolean status){
        if(status){System.out.println("Action successful.");}
        else{System.out.println("Action NOT successful.");}
    }
    public static void BalanceReport(double balance){
        int temp=(int)(balance * 100 +0.5);
        if(balance==-1){System.out.println("Failed to find account.");}
        else{System.out.println("Your current balance is $" + (temp/100.) + (temp % 10 ==0 ? "0" : "") + ".");}
    }
    public static void reportPin(String pin){
        System.out.println("Account creation successful.");
        System.out.println("Your passcode to access your bank account is " + pin + ". Please try to remember them.");
    }

    //deletes
    public static void deleteBank(){
        System.out.println("Deleting bank. Please input the bank number and main passcode, each on their own line:");
    }
    public static void deleteAll(){/*SECURITY OVERRIDE*/
       System.out.println("Deleting all bank save files."); /*SECURITY OVERRIDE*/
    } /*SECURITY OVERRIDE*/
}
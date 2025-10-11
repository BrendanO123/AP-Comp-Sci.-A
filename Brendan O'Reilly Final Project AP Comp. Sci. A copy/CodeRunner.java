
import java.util.ArrayList;
import java.util.Scanner;

import Classes.Bank;
import Classes.Interface;

//run main file, make sure entire folder is present for dependencies and file storage
//see read me for details
public class CodeRunner{
    public static void main(String[] args){
        //vars
        ArrayList<Bank> banks = new ArrayList<Bank>();
        Scanner scan = new Scanner(System.in);
        String input="1";
        int selectedBank=-1;

        //init prompt
        Interface.init();

        //main loop
        main : while(!input.equals("EXIT")){

            Interface.Menu(); //prompt menu

            //input
            input=scan.nextLine(); //get input
            if(input.equals("EXIT")){break;} //exit case
            int selection=0; //selection
            try{
                selection=Integer.parseInt(input); //try to parse int menu selection

                //evaluate selection
                switch(selection){

                    //create bank
                    case 1: 

                        Interface.createBank(); //echo selection
                        banks.add(new Bank(scan.nextLine())); //add new bank with passcode from user
                        Interface.Status(true); //report success

                        //auto select
                        if(selectedBank==-1 && banks.size()==1){ //if first bank and none selected
                            selectedBank=0; //select
                            System.out.println("Bank automatically selected."); //report auto-selection
                        }
                        break; //break to avoid default case and avoid next case

                    //select bank
                    case 2:


                        Interface.selectBank(); //echo selection
                        try{
                            selectedBank=Integer.parseInt(scan.nextLine()); //attempt to get int selection
                            if(selectedBank<0 || selectedBank>=banks.size()){ //selection is invalid

                                //deselect
                                selectedBank=-1;
                                System.out.println("Sorry, I do not recognize that as a valid input. Perhaps that bank number doesn't already exist.\n"); 
                                continue main;
                            }
                            //else
                            Interface.Status(true); //report successful
                        }

                        //did not give an int bank number
                        catch(NumberFormatException e){ 
                            System.out.println("Sorry, I do not recognize that as a valid input. Perhaps that bank number doesn't already exist.\n"); 
                            continue main;
                        }
                        break; //avoid other cases

                    //deselect bank
                    case 3:

                        Interface.DeselectBank(); //echo selection
                        selectedBank=-1; //deselect
                        break; //avoid other cases

                    //create bank account
                    case 4:
                        
                        if(selectedBank!=-1){ //if bank has been selected

                            Interface.createAccount(); //echo selection

                            //input
                            String name = scan.nextLine();
                            String pin;
                            try{
                                double startingBalance = Double.parseDouble(scan.nextLine()); //try to get double input
                                if(startingBalance<=0.){
                                    System.out.println("Sorry, you must start with a positive balance. Ejecting. \n"); 
                                    continue main;
                                }
                                pin=banks.get(selectedBank).createAccount(name, startingBalance); //create account
                            }
                            //did not input a double
                            catch(NumberFormatException e){
                                pin=banks.get(selectedBank).createAccount(name); //do not use a starting balance
                            }
                            Interface.reportPin(pin); //report user pin and routing number
                        }

                        //no bank selected
                        else{
                            System.out.println("Sorry, you have to select a bank before doing this. Ejecting. \n");
                            continue main;
                        }
                        break; //avoid other cases

                    case 5:
                        
                        if(selectedBank!=-1){ //if bank has been selected

                            Interface.removeAccount(); //echo selection

                            //input
                            try{
                                //try to get number, remove member, and report status
                                Interface.Status(banks.get(selectedBank).kickAccount(Short.parseShort(scan.nextLine()), scan.nextLine()));
                            }
                            //did not input a short
                            catch(NumberFormatException e){
                                System.out.println("Sorry, you have to select a bank before doing this. Ejecting. \n");
                            continue main;
                            }
                        }

                        //no bank selected
                        else{
                            System.out.println("Sorry, you have to select a bank before doing this. Ejecting. \n");
                            continue main;
                        }
                        break; //avoid other cases
                    
                    //withdraw
                    case 6:

                        //almost the exact same as create
                        if(selectedBank!=-1){
                            Interface.WithdrawMoney();
                            try{
                                short routingNum = Short.parseShort(scan.nextLine());
                                String pin = scan.nextLine();
                                try{
                                    double amount = Double.parseDouble(scan.nextLine());
                                    if(amount<=0.){
                                        System.out.println("Sorry, you must withdraw a positive amount. Ejecting. \n"); 
                                        continue main;
                                    }
                                    //status is reported this time, may easily fail 
                                    Interface.Status(banks.get(selectedBank).WithdrawFunds(String.valueOf(routingNum), amount, pin));
                                }
                                //double is required this time
                                catch(NumberFormatException e){throw e;} //send to next catch statement
                            }
                            //routing number not a short
                            catch(NumberFormatException e){
                                //double is required this time
                                System.out.println("Sorry, I do not recognize that as a valid input. Ejecting. \n"); 
                                continue main;
                            }
                        }
                        else{
                            System.out.println("Sorry, you have to select a bank before doing this. Ejecting. \n");
                            continue main;
                        }
                        break; //avoid other cases

                    //deposit
                    case 7:

                        //the exact same as withdraw, fails less often
                        if(selectedBank!=-1){
                            Interface.DepositMoney();
                            try{
                                short routingNum = Short.parseShort(scan.nextLine());
                                String pin = scan.nextLine();
                                try{
                                    double amount = Double.parseDouble(scan.nextLine());
                                    if(amount<=0.){
                                        System.out.println("Sorry, you must deposit a positive amount. Ejecting. \n"); 
                                        continue main;
                                    }
                                    Interface.Status(banks.get(selectedBank).DepositFunds(String.valueOf(routingNum), amount, pin));
                                }
                                catch(NumberFormatException e){throw e;}
                            }
                            catch(NumberFormatException e){
                                System.out.println("Sorry, I do not recognize that as a valid input. Ejecting. \n"); 
                                continue main;
                            }
                        }
                        else{
                            System.out.println("Sorry, you have to select a bank before doing this. Ejecting. \n");
                            continue main;
                        }
                        break;

                    //check account balance
                    case 8:

                        //very similar again
                        if(selectedBank!=-1){
                            Interface.checkBalance();
                            try{
                                short routingNum = Short.parseShort(scan.nextLine());
                                String pin = scan.nextLine();
                                //double logic is skipped
                                //balance is reported instead of action status
                                Interface.BalanceReport(banks.get(selectedBank).checkBalance(String.valueOf(routingNum), pin));
                            }
                            catch(NumberFormatException e){
                                System.out.println("Sorry, I do not recognize that as a valid input. Ejecting. \n"); 
                                continue main;
                            }
                        }
                        else{
                            System.out.println("Sorry, you have to select a bank before doing this. Ejecting. \n");
                            continue main;
                        }
                        break;

                    case 9:
                        if(selectedBank!=-1){
                            Interface.showMembers(); //echo selection
                            String pin = scan.nextLine(); //get passcode
                            Interface.Status(banks.get(selectedBank).echoMembers(pin)); //print all and report status
                        }
                        else{
                            System.out.println("Sorry, you have to select a bank before doing this. Ejecting. \n");
                            continue main;
                        }
                        break; //avoid other cases
                        
                    //load bank
                    case 10:

                        Interface.loadBank(); //load bank
                        try{
                            int num = Integer.parseInt(scan.nextLine()); //attempt to get bank number

                            //number is already taken
                            if(num>=0 && num<banks.size()){

                                Bank temp=banks.get(num); //store overwritten bank in case of failure
                                String pin = scan.nextLine(); //get passcode
                                System.out.println("Bank with this number already exists and is being overwritten."); //warn
                                banks.set(num, Bank.getInstance(num, pin)); //overwrite

                                //get instance failed
                                if(banks.get(num)==null){

                                    Interface.Status(false); //report status
                                    System.out.println("Previous bank restored."); //report restoration
                                    banks.set(num, temp); //restore

                                }
                                else{
                                    Interface.Status(true); //report status
                                    if(selectedBank ==-1 && banks.size()==1){ //if no bank selected and one bank

                                        selectedBank=0; //select bank
                                        System.out.println("Bank automatically selected."); //report selection

                                    }
                                }
                            }

                            //new number
                            else{

                                String pin = scan.nextLine(); //get passcode
                                banks.add(Bank.getInstance(num, pin)); //add new bank

                                //get instance failed
                                if(banks.get(banks.size()-1)==null){

                                    Interface.Status(false); //report status
                                    banks.remove(banks.size()-1); //remove null

                                }
                                //get instance successful
                                else{

                                    Interface.Status(true); //report status

                                    //skipped numbers
                                    if(num>=banks.size()){

                                        System.out.println( //report details
                                            "Bank numbers have been skipped to load this bank. " + 
                                            "Subsequent bank numbers will count up from " + num + " starting at " + (num+1) + ". " + 
                                            "Please note, this only applies to the numbers for loading a bank from storage, not the number used to select a bank. " + 
                                            "The number to select this bank will be " + (banks.size()-1) + "."
                                        );
                                    }

                                    //no bank selected and only one bank
                                    if(selectedBank ==-1 && banks.size()==1){

                                        selectedBank=0; //select bank
                                        System.out.println("Bank automatically selected."); //report selection

                                    }
                                }
                            }
                        }

                        //bank number not an int
                        catch(NumberFormatException e){
                            System.out.println("Sorry, I do not recognize that as a valid input. Ejecting. \n"); 
                            continue main;
                        }
                        break; //avoid other cases

                    //save bank
                    case 11:

                        if(selectedBank!=-1){ //if a bank is selected

                            Interface.saveBank(); //save bank
                            Interface.Status(banks.get(selectedBank).save()); //report status
                        }

                        //no bank selected
                        else{
                            System.out.println("Sorry, you have to select a bank before doing this. Ejecting. \n");
                            continue main;
                        }
                        break; //avoid other cases

                    //delete a save file
                    case 12:
                        Interface.deleteBank(); //echo selection
                        try{
                            int num = Integer.parseInt(scan.nextLine()); //try to get int bank num
                            String pin = scan.nextLine(); //get passcode
                            Interface.Status(Bank.deleteSpecific(num, pin)); //delete and report status
                        }
                        
                        //bank number not an int
                        catch(NumberFormatException e){
                            System.out.println("Sorry, I do not recognize that as a valid input. Ejecting. \n");
                            continue main;
                        }
                        break;
                    
                    //delete all save files
                    case 13: /*SECURITY OVERRIDE*/
                        Interface.deleteAll(); /*SECURITY OVERRIDE*/
                        Bank.deleteAll(); /*SECURITY OVERRIDE*/
                        break; /*SECURITY OVERRIDE*/
                    
                    //not a valid menu input number
                    default: 
                        System.out.println("Sorry, I do not recognize that as a valid input. Ejecting. \n");
                        continue main;
                }
            }
            //menu input was not an int
            catch(NumberFormatException e){
                System.out.println("Sorry, I do not recognize that as a valid input. Ejecting. \n"); 
                continue main;
            }
        }
        //user exits
        scan.close(); //close scanner
        Interface.exit(); //say goodbye
    }
}
package AI;

public class simpleGGException extends Throwable{

    private boolean win;

    public simpleGGException(boolean won){win=won;}
    public boolean getWon(){return win;}
    
}

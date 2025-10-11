package AI;

class Node{
    private int index;
    private Node next;

    public Node(int index, Node next){this.index=index; this.next=next;}
    public Node(int index){this.index=index; this.next=null;}
    public Node(){this.index=0; this.next=null;}

    public int getIndex(){return index;}
    public Node getNext(){return next;}

    public void setIndex(int index){this.index=index;}
    public void setNext(Node next){this.next=next;}
}
class Que{
    private Node first, last;
    private int length;

    public Que(){length=0; this.first=this.last=null;}
    public Que(int index){this.first=this.last=new Node(index); length=1;}
    public Que(Node first){this.first=this.last=first; length=1;}

    public void add(Node e){
        e.setNext(this.first); 
        this.first=e; 
        if(this.last==null){this.last=this.first;}
         length++;
    }
    public void add(int index){add(new Node(index));}

    public void Cycle(Node e){
        if(this.last!=null){this.last.setNext(e);} 
        this.last=e; 
        length++; 
        if(this.first==null){this.first=this.last;}
    }
    public void Cycle(int index){Cycle(new Node(index));}


    public int getFirst(){
        Node temp=this.first; 
        this.first=this.first.getNext(); 
        length--; 
        if(temp != null){return temp.getIndex();} 
        return 0;
    }
    public int SafeAccessFirst(){if(this.first!=null){return first.getIndex();} return 0;}
    public int SafeAccessLast(){if(this.last!=null){return this.last.getIndex();} return 0;}

    public int size(){return length;}
    public boolean hasNext(){return (first!=null);}
}

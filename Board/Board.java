import java.util.Iterator;
public class Board {
    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    private final int N;
    private short[][] block;
    private int blankI;
    private int blankJ;    
    private Queue<NeighbourBlank> neighbourQueue;
    private int man;
    private int ham;
    public Board(int[][] blocks)  
    {
        if (blocks == null) {
            throw new java.lang.NullPointerException("No input");
        }
       this.N = blocks.length;
       this.block = new short[N][N];
       man = 0;
       ham = 0;
       for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                block[i][j] = (short) blocks[i][j];
                short goal;
                if ((i == (N-1)) && (j == N-1)) {
                    goal = 0;
                } else {
                    goal = (short)(i*N + j + 1);
                }
                if (block[i][j] == 0) {
                    blankI = i;
                    blankJ = j;
                } else {
                    if (block[i][j] != goal){
                        ham++;
                        int rightIndexI = (block[i][j]) / N;
                        int rightIndexJ = (block[i][j]) % N - 1;
                        if (rightIndexJ < 0) {
                            rightIndexI--;
                            rightIndexJ = N - 1;
                        }
                        //System.out.println("Right index for "+
                        //block[i][j]+" is i= "+rightIndexI+" and j = "+rightIndexJ);
                        man += Math.abs(i - rightIndexI) 
                                   + Math.abs(j - rightIndexJ);  
                    }
                }
            }
        }
        neighbourQueue = new Queue<NeighbourBlank>();
    }
    private Board(short[][] blocks)  
    {
        if (blocks == null) {
            throw new java.lang.NullPointerException("No input");
        }
       this.N = blocks.length;
       this.block = new short[N][N];
       man = 0;
       ham = 0;
       for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                block[i][j] = blocks[i][j];
                short goal;
                if ((i == (N-1)) && (j == N-1)) {
                    goal = 0;
                } else {
                    goal = (short) (i*N + j + 1);
                }
                if (block[i][j] == 0) {
                    blankI = i;
                    blankJ = j;
                } else {
                    if (block[i][j] != goal) {
                        ham++;
                        int rightIndexI = (block[i][j]) / N;
                        int rightIndexJ = (block[i][j]) % N - 1;
                        if (rightIndexJ < 0) {
                            rightIndexI--;
                            rightIndexJ = N - 1;
                        }
                        //System.out.println("Right index for "+
                        //block[i][j]+" is i= "+rightIndexI+" and j = "+rightIndexJ);
                        man += Math.abs(i - rightIndexI) 
                                   + Math.abs(j - rightIndexJ);  
                    }
                }
            }
        }
        neighbourQueue = new Queue<NeighbourBlank>();
    }
    // board dimension N
    public int dimension() 
    {
        return N;
    }
    // number of blocks out of place
    public int hamming() 
    {
        return ham;
    }
    // sum of Manhattan distances between blocks and goal
    public int manhattan()  
    {
        return man;
    }
    // is this board the goal board?
    public boolean isGoal() 
    {
        return (hamming() == 0);
    }
    // a boadr that is obtained by exchanging two adjacent blocks in the same row
    public Board twin()   
    {
        short[][] twinBoard = new short[N][N];
        copyBoard(block, twinBoard, N);      
        int i = 0;        
        boolean swapDone = false;
        while ((i < N) && (!swapDone)) {
            if (i == blankI) {
                i++;
            }
            short item = twinBoard[i][0];
            twinBoard[i][0] = twinBoard[i][1];
            twinBoard[i][1] = item;
            swapDone = true;
        }
        return new Board(twinBoard);                    
    }
    // does this board equal y?
    public boolean equals(Object y)  
    {
        if (y == null) return false;
        if (this.getClass() != y.getClass()) return false;        
        Board that = (Board) y;       
        if (that.dimension() != N) return false;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (block[i][j] != that.block[i][j]) {
                    return false;
                }
            }
        }
        return true;           
    }
    private void buildNeighbourList() 
    {
        int[][] neighbourList = {
            {1, 0},
            {0, 1},
            {0, -1},
            {-1, 0}
        };
        int maxNeighbours = 4;
        int neighbourI = 0;
        int neighbourJ = 0; 
        for (int curr = maxNeighbours-1; curr >= 0; curr--) {
            neighbourI = blankI + neighbourList[curr][0];
            neighbourJ = blankJ + neighbourList[curr][1];
            //System.out.println("Neighbour I and 
            //J "+neighbourI+" and " +neighbourJ);
            if ((neighbourI >= 0) && (neighbourI < N) 
                && (neighbourJ >= 0) && (neighbourJ < N)) { 
                neighbourQueue.enqueue(new 
                    NeighbourBlank(neighbourI, neighbourJ));               
            }
        }          
    }
    private static void copyBoard(short[][] from, short[][] to, int size)
    {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                to[i][j] = from[i][j];
            }
        }
    }
    private class NeighbourBlank {
        private int myI;
        private int myJ;
        public NeighbourBlank(int i, int j)
        {
            myI = i;
            myJ = j;            
        }
    }
    /*public Iterable<Board> neighbors()
    {
        buildNeighbourList();      
        return boardQueue;
    } 
    */
    // all neighboring boards
    public Iterable<Board> neighbors()
    {
        return new Iterable<Board>() 
        {
            @Override
            public Iterator<Board> iterator()
            {
                return new BoardIterator();
            }
        };        
    }    
    private void swap(int i, int j, int k, int l)
    {
        short temp = block[i][j];
        block[i][j] = block[k][l];
        block[k][l] = temp;
    }
    private class BoardIterator implements Iterator<Board> {       
        private int myBlankI, myBlankJ;
        public BoardIterator() 
        {
            buildNeighbourList();   
            myBlankI = blankI;
            myBlankJ = blankJ;
        }
        public boolean hasNext() 
        {
                swap(myBlankI, myBlankJ, blankI, blankJ);
                myBlankI = blankI;
                myBlankJ = blankJ;
                return (!neighbourQueue.isEmpty());
        }
        public Board next() 
        {
             NeighbourBlank nextNeighbour = neighbourQueue.dequeue();
             swap(nextNeighbour.myI, nextNeighbour.myJ, myBlankI,  myBlankJ);
             myBlankI = nextNeighbour.myI;
             myBlankJ = nextNeighbour.myJ;
             return (new Board(block));
        }
        public void remove() 
        {
            //Unsupported
        }        
    }
    // string representation of this board (in the output format specified below)
    public String toString()   
    {
        String s = "";
        s += N + "\n";
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s = s + block[i][j]+" ";                
            }
            s = s + "\n";
        }
        return s;
    }
     // unit tests (not graded)
    public static void main(String[] args)
    {
        In in = new In(args[0]); 
        int N = in.readInt();
        int[][] inBoard = new int[N][N];  
        int i = 0; 
        int j = 0;
        while (!in.isEmpty()) {
            inBoard[i][j] = in.readInt();
            j++;
            if (j >= N) {
                i++;
                j = 0;
            }            
            if (i >= N) {
                break;
            }
        }
        Board myBoard = new Board(inBoard);
        System.out.print(myBoard.toString());
        System.out.println("Hamming =  "+ myBoard.hamming());
        System.out.println("Manhattan = "+ myBoard.manhattan());
        Board twinBoard = myBoard.twin();
        System.out.println("Twin Board:");
        System.out.print(twinBoard.toString());
        System.out.println("Dimension"+myBoard.dimension());
        System.out.println("Neighbours");        
        Iterable<Board> myNeighbours = myBoard.neighbors();
        Iterator<Board> iter = myNeighbours.iterator();
        while (iter.hasNext()) {
            System.out.println("Next neighbour");
            System.out.print(iter.next().toString());
        }
        System.out.print("Original Board\n"+myBoard.toString());
    }
}
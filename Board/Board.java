import java.util.Iterator;
public class Board {
    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    private final int N;
    private int[][] goal;
    private int[][] block;
    private int blank_i;
    private int blank_j;    
    private Queue<NeighbourBlank> neighbourQueue;
    int man;
    int ham;
    public Board(int[][] blocks)  
    {
       this.N = blocks.length;
       this.block = new int[N][N];
       man = 0;
       ham = 0;
       this.goal = new int[N][N];
       for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                block[i][j] = blocks[i][j];
                if ((i == (N-1)) && (j == N-1)) {
                    goal[i][j] = 0;
                } else {
                    goal[i][j] = i*N+j+1;
                }
                if (block[i][j] == 0) {
                    blank_i = i;
                    blank_j = j;
                } else {
                    if (block[i][j] != goal[i][j]) {
                        ham++;
                        int right_index_i = (block[i][j])/N;
                        int right_index_j = (block[i][j])%N - 1;
                        if (right_index_j < 0) {
                            right_index_i--;
                            right_index_j = N - 1;
                        }
                        //System.out.println("Right index for "+block[i][j]+" is i= "+right_index_i+" and j = "+right_index_j);
                        man += Math.abs(i - right_index_i) + Math.abs(j - right_index_j);  
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
        int[][] twinBoard = new int[N][N];
        copyBoard(block, twinBoard, N);      
        int i = 0;        
        boolean swapDone = false;
        while ((i < N) && (!swapDone)) {
            if (i == blank_i) {
                i++;
            }
            int item = twinBoard[i][0];
            twinBoard[i][0] = twinBoard[i][1];
            twinBoard[i][1] = item;
            swapDone = true;
        }
        return new Board(twinBoard);                    
    }
    // does this board equal y?
    public boolean equals(Object y)  
    {
        Board that = (Board) y;       
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
        for(int currNeighbour = maxNeighbours-1; currNeighbour >= 0; currNeighbour--) {
            neighbourI = blank_i + neighbourList[currNeighbour][0];
            neighbourJ = blank_j + neighbourList[currNeighbour][1];
            //System.out.println("Neighbour I and J "+neighbourI+" and " +neighbourJ);
            if ((neighbourI >= 0) && (neighbourI < N) &&
                (neighbourJ >= 0) && (neighbourJ < N)) { 
                neighbourQueue.enqueue(new NeighbourBlank(neighbourI, neighbourJ));               
            }
        }          
    }
    private static void copyBoard(int[][] from, int[][] to, int size)
    {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                to[i][j] = from[i][j];
            }
        }
    }
    private class NeighbourBlank {
        int myI;
        int myJ;
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
                return new BoardIterator() ;
            }
        };        
    }    
    private void swap(int i, int j, int k, int l)
    {
        int temp = block[i][j];
        block[i][j] = block[k][l];
        block[k][l] = temp;
    }
    private class BoardIterator implements Iterator<Board> {       
        private int myBlankI, myBlankJ;
        public BoardIterator() 
        {
            buildNeighbourList();   
            myBlankI = blank_i;
            myBlankJ = blank_j;
        }
        public boolean hasNext() 
        {
                swap(myBlankI, myBlankJ, blank_i, blank_j);
                myBlankI = blank_i;
                myBlankJ = blank_j;
            if (!neighbourQueue.isEmpty()) {

                return true;
            } else {
                return false;
            }
        }
        public Board next() 
        {
             NeighbourBlank nextNeighbour = neighbourQueue.dequeue();
             swap(nextNeighbour.myI, nextNeighbour.myJ, myBlankI,  myBlankJ);
             myBlankI = nextNeighbour.myI;
             myBlankJ = nextNeighbour.myJ;
             return(new Board(block));
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
        int i = 0; int j = 0;
        while (!in.isEmpty()) {
            inBoard[i][j]= in.readInt();
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
        while(iter.hasNext()) {
            System.out.println("Next neighbour");
            System.out.print(iter.next().toString());
        }
        System.out.print("Original Board\n"+myBoard.toString());
    }
}
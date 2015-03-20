import java.util.Iterator;
public class Board {
    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    private final int N;
    private int[][] goal;
    private int[][] block;
    private int blank_i;
    private int blank_j;    
    public Board(int[][] blocks)  
    {
       this.N = blocks.length;
       this.block = new int[N][N];
       copyBoard(blocks, block, N);
       
       this.goal = new int[N][N];
       for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (block[i][j] == 0) {
                    blank_i = i;
                    blank_j = j;
                }
                if ((i == (N-1)) && (j == N-1)) {
                    goal[i][j] = 0;
                } else {
                    goal[i][j] = i*N+j+1;
                }
            }
        }
    }
    // board dimension N
    public int dimension() 
    {
        return N;
    }
    // number of blocks out of place
    public int hamming() 
    {
        int distance = 0;
        for(int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if ((block[i][j] != 0) 
                        && (block[i][j] != goal[i][j])) {                    
                    distance++;
                }
            }
        }
        return distance;
    }
    // sum of Manhattan distances between blocks and goal
    public int manhattan()  
    {
        int distance = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if ((block[i][j] != 0) 
                    && (block[i][j] != goal[i][j])) {
                    int right_index_i = block[i][j]/N;
                    int right_index_j = block[i][j]%N - 1;                    
                    distance = distance + Math.abs(i - right_index_i) + Math.abs(j - right_index_j);                    
                }
            }
        }
        return distance;
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
    private static void copyBoard(int[][] from, int[][] to, int size)
    {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                to[i][j] = from[i][j];
            }
        }
    }
    private class BoardIterator implements Iterator<Board> {
        private Board[] boardQueue = new Board[4];
        int numNeighbours = 0;
        public BoardIterator() 
        {
            /*Init some values*/
            int[][] neighbourList = {
                {1, 0},
                {0, 1},
                {0, -1},
                {-1, 0}
            };
            int maxNeighbours = 4;
            int[][] neighbourBoard = new int[N][N];  
            boolean[] neighboursValid = new boolean[maxNeighbours];
            int neighbourI = 0;
            int neighbourJ = 0; 
            for(int currNeighbour = maxNeighbours-1; currNeighbour >= 0; currNeighbour--) {
                neighbourI = blank_i + neighbourList[currNeighbour][0];
                neighbourJ = blank_j + neighbourList[currNeighbour][1];
                //System.out.println("Neighbour I and J "+neighbourI+" and " +neighbourJ);
                if ((neighbourI >= 0) && (neighbourI < N) &&
                    (neighbourJ >= 0) && (neighbourJ < N)) { 
                    copyBoard(block, neighbourBoard, N);
                    neighbourBoard[blank_i][blank_j] = neighbourBoard[neighbourI][neighbourJ];
                    neighbourBoard[neighbourI][neighbourJ] = 0;                    
                    boardQueue[numNeighbours++] = new Board(neighbourBoard);                   
                }
            }                   
        }
        public boolean hasNext() 
        {
            return numNeighbours > 0;
        }
        public Board next() 
        {
             return boardQueue[--numNeighbours];
             
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
    }
}
import java.util.*;
public class Solver {
    private boolean initialSolved;
    private boolean twinSolved;
    private Board myBoard;
    MinPQ<SearchNode> pq;
    MinPQ<SearchNode> pqTwin;
    Queue<Board> solutionQueue;
    int numMoves;
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial)  
    {
        Out out = new Out("test.txt");
        Out out2 = new Out("test_twin.txt");
        pq = new MinPQ<SearchNode>();
        pqTwin = new MinPQ<SearchNode>();
        this.myBoard = initial;
        SearchNode initGame = new SearchNode(initial, null);        
        solutionQueue = new Queue<Board>();
        pq.insert(initGame);
        //Board currBoard = initial;
        Board currBoardTwin = initial.twin();
        SearchNode twinGame = new SearchNode(currBoardTwin, null);
        pqTwin.insert(twinGame);        
        while (!initialSolved && !twinSolved) {
            initGame = pq.delMin();
            Board currBoard = initGame.thisBoard();
            out.println("Best neighbour");
            out.print(currBoard.toString());
            if (currBoard.isGoal()) {
                initialSolved = true;
            }
            solutionQueue.enqueue(currBoard);
            //Solve original board
            Iterable<Board> neighbours = currBoard.neighbors();            
            Board searchBoard;
            out.println("Add neighbours");
            for (Board neighbour: neighbours) {
                if (!seenBefore(initGame, neighbour)) {                    
                    SearchNode newNode = new SearchNode(neighbour, initGame);
                    pq.insert(newNode);  
                    out.println("Neighbour");
                    out.print(neighbour.toString());
                    
                }                          
            }
            //Solve Twin Board
            twinGame = pqTwin.delMin();
            currBoardTwin = twinGame.thisBoard();
            out2.println("Best Twin Neighbour");
            out2.print(currBoardTwin.toString());
            if (currBoardTwin.isGoal()) {
                twinSolved = true;
            }
            Iterable<Board> neighboursTwin = currBoardTwin.neighbors();            
            out2.println("Twin Neighbours");
            for (Board neighbourTwin: neighboursTwin) {
                if (!seenBefore(twinGame, neighbourTwin)) {       
                    //out2.println("Never seen before?"+neverSeenBefore(twinGame, neighbourTwin));
                    SearchNode newNodeTwin = new SearchNode(neighbourTwin, twinGame);                    
                    pqTwin.insert(newNodeTwin);                    
                    out2.println("Twin neighbour");
                    out2.print(neighbourTwin.toString());
                }                          
            }

        }
        if (twinSolved) {
            System.out.println("Twin solved at "+twinGame.numMoves+" moves");
            numMoves = -1;
        } else {
            numMoves = initGame.numMoves();
        }
    }
    private static boolean seenBefore(SearchNode current, Board newBoard) 
    {        
        SearchNode parentNode = current.previous;
        while (parentNode != null) {
            
            if (newBoard.equals(parentNode.thisBoard())) {
                return true;
            }
            parentNode = parentNode.previous;
        }             
        return false;
    }
    private class SearchNode implements Comparable<SearchNode> {
        private SearchNode previous;
        private int numMoves;
        private Board board;   
        private int priority;
        public SearchNode(Board searchBoard, SearchNode parent)
        {
            this.board = searchBoard;
            if (parent == null) {
                this.numMoves = 0;
                this.previous = null;
            } else {
                this.numMoves = parent.numMoves + 1;
                this.previous = parent;
            }
            this.priority = searchBoard.manhattan();
        }
        public int numMoves()
        {
            return numMoves;
        }
        public int priority()
        {
            return priority;
        }
        public Board thisBoard()
        {
            return board;
        }
        public int compareTo(SearchNode that) 
        {
            if (this.priority() == that.priority()) {
                if (this.thisBoard().hamming() > that.thisBoard().hamming()) {
                    return 1;
                } else if (this.thisBoard().hamming() < that.thisBoard().hamming()) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (this.priority() > that.priority()) {
                return 1;
            } else {
                return -1;
            }                
        }
    }
    // is the initial board solvable?
    public boolean isSolvable()  
    {        
        return initialSolved;
    }
    // min number of moves to solve initial board; -1 if unsolvable
    public int moves()    
    {
        return numMoves;
    }
    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution()
    {
        return solutionQueue;
    }
    // solve a slider puzzle (given below)
    public static void main(String[] args) 
    {        
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
            blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        
        // solve the puzzle
        Solver solver = new Solver(initial);
        
        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
public class Solver {
    private boolean initialSolved;
    private boolean twinSolved;
    //private Board myBoard;
    MinPQ<SearchNode> pq;
    MinPQ<SearchNode> pqTwin;
    Stack<Board> solutionStack;
    int numMoves;
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial)  
    {
//        Out out = new Out("test.txt");
        pq = new MinPQ<SearchNode>();
        pqTwin = new MinPQ<SearchNode>();
        //this.myBoard = initial;
        SearchNode initGame = new SearchNode(initial, null, false);        
        solutionStack = new Stack<Board>();
        pq.insert(initGame);
        //Board currBoard = initial;
        Board currBoardTwin = initial.twin();
        SearchNode twinGame = new SearchNode(currBoardTwin, null, true);
        pq.insert(twinGame);        
        while (!initialSolved && !twinSolved) {
            initGame = pq.delMin();
            Board currBoard = initGame.thisBoard();
//            out.println("Best neighbour");
//            out.print(currBoard.toString());
            //System.out.print("Best neighbour:\n"+currBoard.toString());
                               
            if (currBoard.isGoal()) {
                if (initGame.isTwin()) {
                    twinSolved = true;
                    System.out.println("Twin solved at "+initGame.numMoves+" moves");
                    numMoves = -1;
                } else {
                    initialSolved = true;
                    numMoves = initGame.numMoves();
                    createSolutionStack(initGame);                    
                }
                continue;
            } 
            //Solve original board
            Iterable<Board> neighbours = currBoard.neighbors();            
//            out.println("Add neighbours");
            for (Board neighbour: neighbours) {
                if (!seenBefore(initGame, neighbour)) {   
                    SearchNode newNode;
                    if (initGame.isTwin()) {
                        newNode = new SearchNode(neighbour, initGame, true);
                    } else {
                        newNode = new SearchNode(neighbour, initGame, false);
                    }
                    pq.insert(newNode);  
//                    out.println("Neighbour");
//                    out.print(neighbour.toString());
                    
                }                          
            }
        }
    }
    private void createSolutionStack(SearchNode myPath)
    {
        while (myPath != null)
        {
            Board myPathBoard = myPath.thisBoard();
            solutionStack.push(myPathBoard);
            myPath = myPath.previous;
        }
    }
    private static boolean seenBefore(SearchNode current, Board newBoard) 
    {        
        SearchNode parentNode = current.previous;
//        while (parentNode != null) {
//            if (newBoard.equals(parentNode.thisBoard())) {
//                return true;
//            }
//            parentNode = parentNode.previous;
//        }             
//        return false;
        if (parentNode == null) {
            return false;
        } else {
            if (newBoard.equals(parentNode.thisBoard())) {
                return true;
            }
        }
        return false;
    }
    private class SearchNode implements Comparable<SearchNode> {
        private SearchNode previous;
        private int numMoves;
        private Board board;   
        private int priority;
        boolean twin;
        public SearchNode(Board searchBoard, SearchNode parent, boolean type)
        {
            this.board = searchBoard;
            if (parent == null) {
                this.numMoves = 0;
                this.previous = null;
            } else {
                this.numMoves = parent.numMoves + 1;
                this.previous = parent;
            }
            this.twin = type;
            this.priority = searchBoard.manhattan() + numMoves;
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
        public boolean isTwin()
        {
            return twin;
        }
        public int compareTo(SearchNode that) 
        {            
            if (this.priority() == that.priority()) {
                if (this.thisBoard().manhattan() > that.thisBoard().manhattan()) {
                    return 1;
                } else if (this.thisBoard().manhattan() < that.thisBoard().manhattan()) {
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
        return solutionStack;
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
            for (Board board : solver.solution())
                StdOut.println(board);
            StdOut.println("Minimum number of moves = " + solver.moves());
        }
    }
}
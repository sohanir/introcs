public class KdTree {
   // construct an empty set of points 
   private int size;
   private Node root;
   private static final boolean EVEN = true;
   public KdTree()    
   {
       this.size = 0;     
       this.root = null;
   }
   private static class Node {
       private final Point2D p;
       private RectHV rect;
       private Node left;
       private Node right;
       //construct a node
       public Node(Point2D point)
       {
           if (point == null) throw new java.lang.NullPointerException("No input");
           this.p = point;
           this.left = null;
           this.right = null;
           this.rect = null;
       }
       /**
        * Compares this point to that point
        */
       public int compare(Point2D that, boolean level) {
           if (that == null) {
               throw new java.lang.NullPointerException("No input");
           }        
           if (level == EVEN) {
               if (that.x() < p.x()) return -1;
               if (that.x() > p.x()) return +1;
               return 0;
           } else {
               if (that.y() < p.y()) return -1;
               if (that.y() > p.y()) return +1;
               return 0;
           }
       }
   }
   // is the set empty?
   public boolean isEmpty()   
   {
       return (size == 0);
   }
   // number of points in the set
   public int size()
   {
       return size;
   }
   // add the point to the set (if it is not already in the set)
   public void insert(Point2D p)     
   {
       if (p == null) throw new java.lang.NullPointerException("No input");
       RectHV rect = null;
       if (root == null) {
           rect = new RectHV(0, 0, 1, 1);
       }
      root = put(root, p, EVEN);   
      if ((root.rect == null) && (rect != null)) {
          root.rect = rect;
      }
      //size++;
      //System.out.println("Insterted point "+p.toString()+"tree size"+size);
   }
   private static RectHV createRect(Node myNode, boolean level, boolean less)
   {
       RectHV rect = null;
       if (myNode == null) {
           return null;
       }
       double parentXmin = myNode.rect.xmin();
       double parentXmax = myNode.rect.xmax();
       double parentYmin = myNode.rect.ymin();
       double parentYmax = myNode.rect.ymax();
//       System.out.println("ParentXmin"+parentXmin+", ParentYmin = "+parentYmin);
//       System.out.println("ParentXmax"+parentXmax+", ParentYmax = "+parentYmax);
       if (level == EVEN) {
           if (less) {
               rect = new RectHV(parentXmin,
                                 parentYmin, myNode.p.x(), parentYmax);
           } else {
               rect = new RectHV(myNode.p.x(),
                                 parentYmin, parentXmax , parentYmax);    
           }
       } else {
           if (less) {
               rect = new RectHV(parentXmin,
                                 parentYmin, parentXmax, myNode.p.y());
           } else {
               rect = new RectHV(parentXmin, 
                                 myNode.p.y(), parentXmax , parentYmax);    
           }
       }
//       System.out.println("New Rect");
//       System.out.println("Xmin = "+rect.xmin()+", Ymin = "+rect.ymin());
//       System.out.println("Xmax = "+rect.xmax()+", Ymin = "+rect.ymax());
       return rect;
   }       
   private Node put(Node root1, Point2D p, boolean level)
   {       
       if (root1 == null) {     
           Node newNode = new Node(p);
           size++;
           return newNode;
       }
       //System.out.println("Traversing point "+root.p.toString());
       if (p.equals(root1.p)) return root1;
       int cmp = root1.compare(p, level);              
       if (cmp < 0) {         
           //System.out.println("Going left");           
           root1.left = put(root1.left, p, !level);
           if (root1.left.rect == null) {
               RectHV rectLeft = createRect(root1, level, true);
               root1.left.rect = rectLeft;               
           }
       } else if (cmp >= 0) {
           //System.out.println("Going right");           
           root1.right = put(root1.right, p, !level);
           if (root1.right.rect == null) {               
               RectHV rectRight = createRect(root1, level, false);
               root1.right.rect = rectRight;               
           }
       }     
       return root1;
   }
   private boolean search(Node root1, Point2D p, boolean level)
   {       
       boolean ret = false;
       if (root1 == null) {     
           return ret;
       }       
       if (p.equals(root1.p)) return true;
       int cmp = root1.compare(p, level);
       if (cmp < 0) {           
           ret = search(root1.left, p, !level);
       } else if (cmp >= 0) {
           ret = search(root1.right, p, !level);
       } 
       return ret;
   }
   // does the set contain point p? 
   public boolean contains(Point2D p)
   {
       if (p == null) throw new java.lang.NullPointerException("No input");
       return search(root, p, EVEN);
   }
   // draw all points to standard draw
   public void draw()    
   {
       StdDraw.setCanvasSize(800, 800);
       StdDraw.setXscale(0, 1);
       StdDraw.setYscale(0, 1);
       drawMe(root, EVEN);
   }
   private void drawMe(Node tip, boolean level)
   {
       if (tip == null) return;
       StdDraw.setPenRadius(.01);
       StdDraw.setPenColor(StdDraw.BLACK);
       tip.p.draw();
       StdDraw.setPenRadius(.002);       
       if (level == EVEN) {
           StdDraw.setPenColor(StdDraw.RED);
           Point2D from = new Point2D(tip.p.x(), tip.rect.ymin());
           Point2D to = new Point2D(tip.p.x(), tip.rect.ymax());
           from.drawTo(to);
       } else {
           StdDraw.setPenColor(StdDraw.BLUE);
           Point2D from = new Point2D(tip.rect.xmin(), tip.p.y());
           Point2D to = new Point2D(tip.rect.xmax(), tip.p.y());
           from.drawTo(to);           
       }
       drawMe(tip.right, !level);
       drawMe(tip.left, !level);
   }
   private void pointsInRange(Node root1, RectHV rect, Queue<Point2D> pointQueue)
   {
       if (root1 == null) return;
       if (!rect.intersects(root1.rect)) return;
       //System.out.println("Is point "+root.p.toString()+"in the rectangle?");
       if (rect.contains(root1.p)) {
           pointQueue.enqueue(root1.p);
           //System.out.println("Yes indeed");
       }
       pointsInRange(root1.right, rect, pointQueue);
       pointsInRange(root1.left, rect, pointQueue);             
   }
   // all points that are inside the rectangle
   public Iterable<Point2D> range(RectHV rect)   
   {
       if (rect == null) throw new java.lang.NullPointerException("No input");       
       Queue<Point2D> pointQueue = new Queue<Point2D>();
       if (root != null) {
           pointsInRange(root, rect, pointQueue);
       }
       //System.out.println("Number of points in rectange"+pointQueue.size());
       return pointQueue;
   }
   private Point2D closest(Node root1, Point2D p, Point2D best)
   {       
       if (root1 == null) return best;       
       double shortestDistance = p.distanceSquaredTo(best);
       if (shortestDistance < root1.rect.distanceSquaredTo(p)) 
           return best;
       Point2D champion = best;
       //System.out.println("Node inspected"+root1.p.toString());
       if (root1.p.distanceSquaredTo(p) < shortestDistance) {
           champion = root1.p;
       }   
       champion = closest(root1.right, p, champion);
       champion = closest(root1.left, p, champion);
       return champion;
   }
    // a nearest neighbor in the set to point p; null if the set is empty 
   public Point2D nearest(Point2D p)    
   {
       if (p == null) throw new java.lang.NullPointerException("No input");
       Point2D champion = null;
       if (root != null) {
           champion = closest(root, p, root.p);
       }
       return champion;
   }
   // unit testing of the methods (optional) 
   public static void main(String[] args)     
   {
        int N = Integer.parseInt(args[0]);
        KdTree setOfPoints = new KdTree();
        double x0;
        double y0;
        Point2D p;
        Point2D[] pArr = new Point2D[N];
        int repeatPoints = 0;
        for (int i = 0; i < N; i++)
        {
            x0 = Math.random();
            y0 = Math.random();            
            p = new Point2D(x0, y0);
            //System.out.print(p.toString()+"\n"); 
            pArr[i] = p;
            if (setOfPoints.contains(p)) {
                repeatPoints++;
            }
            setOfPoints.insert(p);            
            //System.out.println("Inserted point"+p.toString()+" and size is now "+setOfPoints.size());
        }
        System.out.println("Size of tree"+setOfPoints.size()+" and repeat Points"+repeatPoints);
        setOfPoints.draw();
        int randomIndex = (int) ((N - 1)*Math.random());
        System.out.println("Contains " +pArr[randomIndex].
       toString()+"? "+setOfPoints.contains(pArr[randomIndex]));
        RectHV rect = new RectHV(0.39, 0.67, 0.81, 0.91);
//        RectHV rect = new RectHV(0, 0, 0.01, 0.01);
        StdDraw.setPenRadius(.005);
        StdDraw.setPenColor(StdDraw.BLACK);
        rect.draw();
       
       int numPoints = 0;
       for (Point2D rectPoints: setOfPoints.range(rect)) {
           //System.out.println(rectPoints.toString());
           StdDraw.setPenColor(StdDraw.ORANGE);
           StdDraw.setPenRadius(.005);
           rectPoints.draw();
           numPoints++;
       }
       System.out.println(numPoints+" Points in rectangle "+rect.toString());
       Point2D randomPoint = new Point2D(0.5, 0.5);
       Point2D myNearest = setOfPoints.nearest(randomPoint);
       System.out.println("Nearest to"+randomPoint.
       toString()+" is "+myNearest.toString()); 
       StdDraw.setPenRadius(.02);
       StdDraw.setPenColor(StdDraw.GREEN);
       randomPoint.draw();
       StdDraw.setPenRadius(0.005);
       StdDraw.setPenColor(StdDraw.YELLOW);
       randomPoint.drawTo(myNearest);
//        String filename = args[0];
//        In in = new In(filename);
//
//        //StdDraw.show(0);
//
//        // initialize the two data structures with point from standard input
//        KdTree kdtree = new KdTree();
//        while (!in.isEmpty()) {
//            double x = in.readDouble();
//            double y = in.readDouble();
//            Point2D p = new Point2D(x, y);
//            kdtree.insert(p);
//        }
//        kdtree.draw();
//                RectHV rect = new RectHV(0.25, 0.25, 0.75, 0.75);
//        StdDraw.setPenRadius(.005);
//        StdDraw.setPenColor(StdDraw.BLACK);
//        rect.draw();
//        System.out.println("Points in rectangle"+rect.toString());
//       for (Point2D rectPoints: kdtree.range(rect)) {
//           System.out.println(rectPoints.toString());
//           StdDraw.setPenColor(StdDraw.ORANGE);
//           StdDraw.setPenRadius(.005);
//           rectPoints.draw();
//       }
//       Point2D randomPoint = new Point2D(0.5, 0.5);
//       Point2D myNearest = kdtree.nearest(randomPoint);
//       //System.out.println("Nearest to"+
//       //randomPoint.toString()+" is "+myNearest.toString()); 
//       StdDraw.setPenRadius(.02);
//       StdDraw.setPenColor(StdDraw.GREEN);
//       randomPoint.draw();
//       StdDraw.setPenRadius(0.005);
//       StdDraw.setPenColor(StdDraw.YELLOW);
//       randomPoint.drawTo(myNearest);
   }
}
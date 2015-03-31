import java.util.Iterator;
public class PointSET {
   // construct an empty set of points
   private int size;
   private SET<Point2D> set;
   public PointSET()      
   {
       this.size = 0;
       set = new SET<Point2D>();
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
       if ((p != null) && (!contains(p))) {
           set.add(p);
           size++;
       }       
   }
   // does the set contain point p? 
   public boolean contains(Point2D p) 
   {
       return set.contains(p);
   }
   // draw all points to standard draw
   public void draw()  
   {
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.setPenRadius(.01);
        StdDraw.setPenColor(StdDraw.RED);
       Iterator<Point2D> iter = set.iterator();
       while (iter.hasNext())
       {
           Point2D p = iter.next();
           p.draw();
       }
   }
   // all points that are inside the rectangle
   public Iterable<Point2D> range(RectHV rect) 
   {
       Iterator<Point2D> iter = set.iterator();
       Queue<Point2D> rectPoints = new Queue<Point2D>();       
       while (iter.hasNext()) {
           Point2D p = iter.next();
           if (rect.contains(p)) {
               rectPoints.enqueue(p);
           }
       }
       return rectPoints;
   }
   // a nearest neighbor in the set to point p; null if the set is empty 
   public Point2D nearest(Point2D p)             
   {
       if (isEmpty()) {
           return null;
       }
       Iterator<Point2D> iter = set.iterator();       
       Point2D minP = iter.next();
       double min = p.distanceTo(minP);
       while (iter.hasNext()) {
           Point2D tempNext = iter.next();
           double tempDist = p.distanceTo(tempNext);
           if (tempDist < min) {
               min = tempDist;
               minP = tempNext;
           }
       }
       return minP;
   }
   // unit testing of the methods (optional)
   public static void main(String[] args)     
   {
//        In in = new In(args[0]); 
//        PointSET setOfPoints = new PointSET();
//        int N = in.readInt();        
//        while (!in.isEmpty()) {
//            Point2D p = new Point2D(in.readInt(), in.readInt());
//            setOfPoints.insert(p);
//        }
        int N = Integer.parseInt(args[0]);
        PointSET setOfPoints = new PointSET();
        double x0;
        double y0;
        Point2D p;
        Point2D[] pArr = new Point2D[N];
        for (int i = 0; i < N; i++)
        {
            x0 = Math.random();
            y0 = Math.random();            
            p = new Point2D(x0, y0);
            System.out.print(p.toString()+"\n"); 
            pArr[i] = p;
            setOfPoints.insert(p);
        }
        setOfPoints.draw();
        //int randomIndex = (int) ((N - 1)*Math.random());
        //System.out.println("Contains " +pArr[randomIndex].
        //toString()+"? "+setOfPoints.contains(pArr[randomIndex]));
        RectHV rect = new RectHV(0.25, 0.25, 0.75, 0.75);
        StdDraw.setPenRadius(.005);
        StdDraw.setPenColor(StdDraw.BLACK);
        rect.draw();
        System.out.println("Points in rectangle"+rect.toString());
        for (Point2D rectPoints: setOfPoints.range(rect)) {
           System.out.println(rectPoints.toString());
       }
       //Point2D randomPoint = new Point2D(0.5, 0.5);
       //System.out.println("Nearest to"+randomPoint.toString()+
       //" is "+setOfPoints.nearest(randomPoint).toString());
       
   }
}
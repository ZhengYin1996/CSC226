import java.util.*;
import java.io.*;

/*
* Code Modified from RedBlackBST.java template provided by R.Sedgewick
* @Modifier: Gurjyot Grewal
* CSC226 UVIC - Assignment 2
*/

public class RedBlackBST<Key extends Comparable<Key>, Value> {

    private static final boolean RED   = true;
    private static final boolean BLACK = false;
    private static double numRed = 0;   //number of red In-edges/nodes
    private static double numNodes = 0; //number of total nodes


    private Node root;     // root of the BST
   

    // BST helper node data type
    private class Node {
        private Key key;           // key
        private Value val;         // associated data
        private Node left, right;  // links to left and right subtrees
        private boolean color;     // color of parent link
        private int size;          // subtree count

        public Node(Key key, Value val, boolean color, int size) {
            this.key = key;
            this.val = val;
            this.color = color;
            this.size = size;
        }
    }

    /**
     * Initializes an empty symbol table.
     */
    public RedBlackBST() {
    }

   /***************************************************************************
    *  Node helper methods.
    ***************************************************************************/
    // is node x red; false if x is null ?
    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    // number of node in subtree rooted at x; 0 if x is null
    private int size(Node x) {
        if (x == null) return 0;
        return x.size;
    } 


    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return size(root);
    }

   /**
     * Is this symbol table empty?
     * @return {@code true} if this symbol table is empty and {@code false} otherwise
     */



   /***************************************************************************
    *  Red-black tree insertion.
    ***************************************************************************/

    /**
     * Inserts the specified key-value pair into the symbol table, overwriting the old 
     * value with the new value if the symbol table already contains the specified key.
     * Deletes the specified key (and its associated value) from this symbol table
     * if the specified value is {@code null}.
     *
     * @param key the key
     * @param val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(Key key, Value val) {

        root = put(root, key, val);
        if (root.color == RED) {
            numRed--;   //root can't be red
        }
        root.color = BLACK;
        // assert check();
    }

    // insert the key-value pair in the subtree rooted at h
    private Node put(Node h, Key key, Value val) {
        if (h == null){ 
            numRed++;   //new node added is red to begin with
            numNodes++; //new node added adds 1 to total count
            return new Node(key, val, RED, 1);
        }

        int cmp = key.compareTo(h.key);
        if      (cmp < 0) h.left  = put(h.left,  key, val); 
        else if (cmp > 0) h.right = put(h.right, key, val); 
        else              h.val   = val;

        // fix-up any right-leaning links
        if (isRed(h.right) && !isRed(h.left)){
            h = rotateLeft(h);

        }
        if (isRed(h.left)  &&  isRed(h.left.left)){
            h = rotateRight(h);
        }

        if (isRed(h.left)  &&  isRed(h.right)){
            flipColors(h);
        }
        h.size = size(h.left) + size(h.right) + 1;

        return h;
    }

  
    /***************************************************************************
    *  Red-black tree helper functions.
    ***************************************************************************/

    // make a left-leaning link lean to the right
    private Node rotateRight(Node h) {
        // assert (h != null) && isRed(h.left);
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = x.right.color;
        x.right.color = RED;
        x.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;
        return x;
    }

    // make a right-leaning link lean to the left
    private Node rotateLeft(Node h) {
        // assert (h != null) && isRed(h.right);
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = x.left.color;
        x.left.color = RED;
        x.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;
        return x;
    }

    // flip the colors of a node and its two children
    private void flipColors(Node h) {
        // h must have opposite color of its two children
        // assert (h != null) && (h.left != null) && (h.right != null);
        // assert (!isRed(h) &&  isRed(h.left) &&  isRed(h.right))
        //    || (isRed(h)  && !isRed(h.left) && !isRed(h.right));
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
        numRed--;   //only action that changes number or red nodes
    }

    // Returns a double representing the percent of red nodes in the RB-Tree
    private double percentRed(){
        if(numRed == 0 || numNodes == 0){
            return 0;
        }

        return ((numRed/numNodes)*100.00);
    }

    // Drops, or empties the tree by nullifying the root
    public void drop(){
        root = null;
        numNodes = 0;
        numRed = 0;
    }

    public static void main(String[] args) {
        
        Scanner s;
        // Some of the file reading code below has been taken from previous CSC225/226 templates
        if (args.length > 0){
            //open scanner from file 
            try{
                s = new Scanner(new File(args[0]));
            } catch(java.io.FileNotFoundException e){
                System.out.printf("Unable to open %s\n",args[0]);
                return;
            }
            
            System.out.printf("Reading input values from %s.\n",args[0]);
                    Vector<Integer> inputVector = new Vector<Integer>();

            int v;
            while(s.hasNextInt()){
                v = s.nextInt();
                inputVector.add(v);
            }
            int size = inputVector.size();
            //insert all values into RB-Tree
            RedBlackBST<Integer,Integer> tree = new RedBlackBST<Integer,Integer>();

            for (int i = 0; i < size; i++){
                Integer num = new Integer(inputVector.get(i));
                tree.put(num,num);
            }

            System.out.printf("Read %d values.\n",size);

            long startTime = System.nanoTime();
            double percentage_of_red = tree.percentRed();
            long endTime = System.nanoTime();
            long totalTime = (endTime-startTime);
            System.out.printf("Total Time (nanoseconds): %d\n",totalTime);
            System.out.printf("Percentage of red nodes: %f\n",percentage_of_red);
        }

        else{
    
            int numTrials = 0;
            RedBlackBST<Integer,Integer> rbTree = new RedBlackBST<Integer,Integer>();
            while(numTrials < 300){
                if(numTrials < 100){
                    //10^4
                    rbTree.drop();
                    Random rand = new Random();
                    int nodesInserted = 0;
                    while(nodesInserted < 10000){
                        Integer num = new Integer(rand.nextInt(2147483647));
                        rbTree.put(num,num);
                        nodesInserted++;
                    }

                    double percent = rbTree.percentRed();
                    System.out.printf("Trial 10^4: %f\n",percent);

                    numTrials++;   
                }else if(numTrials >= 100 && numTrials < 200){
                    //10^5
                    rbTree.drop();
                    Random rand = new Random();
                    int nodesInserted = 0;
                    while(nodesInserted < 100000){
                        Integer num = new Integer(rand.nextInt(2147483647));
                        rbTree.put(num,num);
                        nodesInserted++;
                    }

                    double percent = rbTree.percentRed();
                    System.out.printf("Trial 10^5: %f\n",percent);

                    numTrials++;
                }else{
                    //10^6
                    rbTree.drop();
                    Random rand = new Random();
                    int nodesInserted = 0;
                    while(nodesInserted <= 1000000){
                        Integer num = new Integer(rand.nextInt(2147483647));
                        rbTree.put(num,num);  
                        nodesInserted++;
                    }

                    double percent = rbTree.percentRed();
                    System.out.printf("Trial 10^6: %f\n",percent);
                    numTrials++;
                }

            }
        }
    }

}

/*  
Copyright Â© 2000â€“2016, Robert Sedgewick and Kevin Wayne. 
Last updated: Sat Jan 28 06:55:20 EST 2017. */
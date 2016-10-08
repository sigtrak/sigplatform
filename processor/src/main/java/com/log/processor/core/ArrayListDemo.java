import java.util.*;
public class ArrayListDemo {

   public static void main(String args[]) {
      // create an array list
      ArrayList al = new ArrayList();
      System.out.println("Initial size of al: " + al.size());

      // add elements to the array list
      al.add(1);
      al.add(2);
      al.add(3);
      al.add(4);
      al.add(5);
      al.add(6);
      al.add(0, 7);
      al.add(2, 8);
      
      System.out.println("Size of al after additions: " + al.size());

      System.out.println("Contents of al: " + al);

      System.out.println("Size of al after deletions: " + al.size());
      System.out.println("Contents of al: " + al);
   }
}
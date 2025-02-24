package src.datastructures.skiplist;

import java.util.*;

/**
 * Implementation of a Skip-List data structure
 * @author Group  2
 *
 *@param <E> The type of data stored. Must be a Comparable
 */
public class SkipList<E extends Comparable<? super E>> implements Iterable<E> {

  /**
   * Head of the skip-list
   */
  public SLNode<E> head;
  /**
   * Size of the skip list
   */
  private int size;
  /**
   * The maximum level of the skip-list
   */
  private int maxLevel;
  /**
   * Smallest power of 2 that is greater than the current skip-list size
   */
  private int maxCap;
  /**
   * Natural log of 2
   */
  static final double LOG2 = Math.log(2.0);
  /**
   * Minimum possible integer value for the head
   */
  static final int MIN = Integer.MIN_VALUE;
  /**
   * Random number generator
   */
  private Random rand = new Random();

  private Comparator<E> comparator;
  //Constructor

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public SkipList() {
    size = 0;
    maxLevel = 0;
    maxCap = computeMaxCap(maxLevel);
    head = new SLNode(maxLevel, MIN);
  }
  public SkipList(Comparator<E> comparator) {
    size = 0;
    maxLevel = 0;
    maxCap = computeMaxCap(maxLevel);
    head = new SLNode(maxLevel, MIN);
    this.comparator = comparator;
  }

  /**
   * Search for an item in the list
   * @param target The item being sought
   * @return An SLNode array which references the predecessors of the target at each level.
   */
  @SuppressWarnings("unchecked")
  private SLNode<E>[] search(E target) {
    SLNode<E>[] pred = (SLNode<E>[]) new SLNode[maxLevel];
    SLNode<E> current = head;
    for (int i = current.links.length - 1; i >= 0; i--) {
      while (
              current.links[i] != null && comparator.compare(current.links[i].data,target) < 0
      ) {
        current = current.links[i];
      }
      pred[i] = current;
    }
    return pred;
  }

  /**
   * Find an object in the skip-list
   * @param target The item being sought
   * @return A reference to the object in the skip-list that matches
   * 		   the target. If not found, null is returned
   */
  public E find(E target) {
    SLNode<E>[] pred = search(target);

    if (pred[0].links != null  && comparator.compare(pred[0].links[0].data,target) == 0) {
      return pred[0].links[0].data;
    } else {
      return null;
    }
  }

  /**
   * Inserts the given item
   * @param item The item to add
   * @return true as the item is added
   */
  public boolean add(E item) {

    size++;
    SLNode<E>[] pred = search(item);
    if (size > maxCap) {
      maxLevel++;
      maxCap = computeMaxCap(maxLevel);
      head.links = Arrays.copyOf(head.links, maxLevel);
      pred = Arrays.copyOf(pred, maxLevel);
      pred[maxLevel - 1] = head;
    }
    SLNode<E> newNode = new SLNode<E>(logRandom(), item);
    for (int i = 0; i < newNode.links.length; i++) {
      newNode.links[i] = pred[i].links[i];
      pred[i].links[i] = newNode;
    }
    return true;
  }

  /**
   * Removes an instance of the given item
   * @param item The item to remove
   * @return true if the item is removed, false if the item is not in the list
   */
  public boolean remove(E item) {
    SLNode<E>[] pred = search(item);
    if (pred[0].links != null && comparator.compare(pred[0].links[0].data,item) != 0) {
      return false; //item is not in the list
    } else {
      size--; //don't re-adjust maxCap and level, as we may have nodes at these levels
      SLNode<E> deleteNode = pred[0];
      for (int i = 0; i < deleteNode.links.length; i++) {
        if (pred[i].links[i] != null) pred[i].links[i] =
                pred[i].links[i].links[i];
      }
      return true;
    }
  }

  /**
   * Adds the given element
   * 
   * @param element to be added
   * @return true if successful and vice versa
   */
  public boolean update(E element){
    if(this.remove(element)){
      this.add(element);
      return true;
    }
    return false;

  }

  /**
   * Returns the last element of the list
   * 
   * @return generic type element located at the last element
   */
  public E getLast(){
    if(head.links[maxLevel-1] == null){
      return null;
    }
    SLNode<E> current = head.links[maxLevel-1];

    while (current.links[maxLevel-1] != null){
      current = current.links[maxLevel-1];
    }
    return current.data;
  }


  /**
   * Method to generate a logarithmic distributed integer between 1 and maxLevel.
   *  I.E. 1/2 of the values are 1, 1/4 are 2, etc.
   * @return a random logarithmic distributed int between 1 and maxLevel
   */
  private int logRandom() {
    int r = rand.nextInt(maxCap);
    int k = (int) (Math.log(r + 1) / LOG2);
    if (k > maxLevel - 1) k = maxLevel - 1;
    return maxLevel - k;
  }

  /**
   * Recompute the max cap
   * @param level
   * @return
   */
  private int computeMaxCap(int level) {
    return (int) Math.pow(2, level) - 1;
  }

  @SuppressWarnings("rawtypes")
  public String toString() {
    if (size == 0) return "Empty";
    StringBuilder sc = new StringBuilder();
    SLNode itr = head;
    sc.append("Head: " + maxLevel);
    int lineMaker = 0;
    while (itr.links[0] != null) {
      itr = itr.links[0];
      sc.append(" --> " + itr.toString());
      lineMaker++;
      if (lineMaker == 10) {
        sc.append("\n");
        lineMaker = 0;
      }
    }
    return sc.toString();
  }

  /**
   * Iterator class representation for skip list
   * 
   * Returns in decreased order
   */
  class DescendingSkipListIterator<E extends Comparable<E>>
          implements Iterator<E> {

    private Object[] entries;
    private int cursor;

    @SuppressWarnings("unchecked")
    public DescendingSkipListIterator() {
      LinkedList<E> list = new LinkedList<E>();
      this.cursor = 0;

      SLNode<E> current = (SLNode<E>) head;
      current = current.links[0];
      while (current != null) {
        list.addFirst(current.data);
        current = current.links[0];
      }

      entries = list.toArray();
    }

    /**
     * Checks if the next connection exists
     * 
     * @return true if successful and vice versa
     */
    public boolean hasNext() {
      return cursor <= size - 1;
    }

    @SuppressWarnings("unchecked")
    public E next() throws NoSuchElementException {
      if (cursor > size - 1) throw new NoSuchElementException(
              "Cursor is out of bounds..."
      );
      return (E) this.entries[this.cursor++];
    }
  }

  /**
   * Static class to contain data and links
   * @author Group 2
   *
   * @param <E> The type of data stored. Must be a Comparable
   */
  static class SLNode<E> {
    /** layered links of skiplist */
    SLNode<E>[] links;
    /** data of the node */
    E data;

    /**
     * Create a node of level m
     * @param m The level of the node
     * @param data The data to be stored
     */
    @SuppressWarnings("unchecked")
    public SLNode(int m, E data) {
      links = (SLNode<E>[]) new SLNode[m];
      this.data = data;
    }

    /**
     * to string method
     * 
     * @return stringed list representation
     */
    public String toString() {
      return (data.toString() + " |" + links.length + "|");
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Iterator<E> descendingIterator() {
    return new DescendingSkipListIterator();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Iterator<E> iterator() {
    return new DescendingSkipListIterator();
  }
}

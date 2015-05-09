package tk.luoxing123.utils;
/*For single-source shortest paths can be shown to run in O(m+nlgn)
 *using a Fibonacci heap,compared to O(mlgn) using binomial heap
 *
 *Fibonacci heap is represented as a circular,doubly-linked list of
 *trees obeying the min-heap property.  each node store pointers to
 *its parent and some arbitrary child ,degree(the number of children it has)
 * whether marked. heap stores a pointer to the tree with the minimum value
 *
*/
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
public class FibonacciHeap<T,V extends Number> {
    public static class Entry<T>{
        private int degree=0;
        private boolean isMarked = false;
        private Entry<T> next;
        private Entry<T> prev;
        private Entry<T> parent;
        private Entry<T> child;
        private T elem;
        private Double priority;
        private Entry(T elem,Double priority){
            this.next=this;
            this.elem=elem;
            this.priority = priority;
        }
		public T getElem() {
			return elem;
		}
		public void setElem(T elem) {
			this.elem = elem;
		}
        
    }
    private Entry<T> min=null;
    private int size =0;
    public FibonacciHeap() {
    }
    public static <T,V extends Number> FibonacciHeap<T,V>
        merge(FibonacciHeap<T,V> heap,FibonacciHeap<T,V> other) {
        FibonacciHeap<T,V> result = new FibonacciHeap<T,V>();
        result.min = mergeLists(heap.min,other.min);
        result.size = heap.size + other.size;
        heap.size=other.size =0;
        heap.min=other.min=null;
            
        return result;
    }
    
    public Entry<T> enqueue(T value,V priority){
        Entry<T> result= new Entry<T>(value,priority.doubleValue());
        min = mergeLists(min,result);
        ++size;
        return result;
    }
    public Entry<T> dequeue(){
        if(isEmpty()){
            throw new NoSuchElementException("Heap is Empty");
        }
        --size;
        Entry<T> min =this.min;
        if(min.next == min){
            min=null;
        }else{
            min.prev.next=min.next;
            min.next.prev = min.prev;
            min=min.next;
        }
        if(min.child !=null){
            while(true){
                Entry<T> curr = min.child;
                curr.parent =null;
                curr=curr.next;
                if(curr==min.child) break;
            }
        }
        min = mergeLists(min,this.min.child);
        if(min == null) return this.min;
        List<Entry<T>> treeTable = new ArrayList<Entry<T>>();
        List<Entry<T>> toVisit = new ArrayList<Entry<T>>();
        for(Entry<T> curr = min;toVisit.isEmpty()||toVisit.get(0)!=curr;
            curr=curr.next){
            toVisit.add(curr);
        }
        for(Entry<T> curr: toVisit){
            while(true){
                while(curr.degree >= treeTable.size()){
                    treeTable.add(null);
                }
                if(treeTable.get(curr.degree)==null){
                    treeTable.set(curr.degree,curr);
                    break;
                }
                Entry<T> other = treeTable.get(curr.degree);
                treeTable.set(curr.degree,null);
                min = other;
                Entry<T> max = curr;
                if(other.priority > curr.priority){
                    min=curr;
                    max=other;
                }
                max.next.prev = max.prev;
                max.prev.next = max.next;
                max.next = max.prev = max;
                max.child = mergeLists(min.child,max);
                max.parent = min;
                max.isMarked = false;
                ++min.degree;
                curr = min;
            }
            if(curr.priority <= this.min.priority) this.min = curr;
            
        }
        return min;
    }
    private static <T> Entry<T> mergeLists(Entry<T> entry,Entry<T> other){
        if(entry ==null && other ==null){
            return null;
        }else if(entry !=null && other ==null){
            return entry;
        }else if(entry ==null && other !=null){
            return other;
        }else{
            Entry<T> next = entry.next;
            entry.next = other.next;
            entry.next.prev =  entry;
            other.next = next;
            other.next.prev = other;
            return entry.priority < other.priority ? entry:other;
        }
    }
    public void decreaseKey(Entry<T> entry,double priority){
        if(priority > entry.priority){
            throw new IllegalArgumentException("New priority exceed old");
        }
        entry.priority = priority;
        if(entry.parent !=null && entry.priority <= entry.parent.priority)
            cutNode(entry);
        if(entry.priority <= min.priority)
            this.min=entry;
    }
    private void cutNode(Entry<T> entry){
        entry.isMarked = false;
        if(entry.parent == null) return;
        if(entry.next != entry){
            entry.next.prev = entry.prev;
            entry.prev.next = entry.next;
            
        }
        if(entry.parent.child == entry){
            if(entry.next != entry){
                entry.parent.child = entry.next;
            }else{
                entry.parent.child =null;
            }
        }
        --entry.parent.degree;
        entry.prev = entry.next = entry;
        this.min = mergeLists(this.min,entry);
        if(entry.parent.isMarked)
            cutNode(entry.parent);
        else
            entry.parent.isMarked = true;
        entry.parent =null;
    }
    public Entry<T> min(){
        if(isEmpty()){
            throw new NoSuchElementException("Heap is Empty");
        }
        return min;
    }
    public boolean isEmpty(){
        return min==null;
    }
    public int size(){
        return size;
    }
    
}

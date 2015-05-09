package tk.luoxing123.graph;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import tk.luoxing123.entitylink.SemanticRelatness;
import java.util.Set;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import java.util.HashSet;
import java.util.Iterator;
import java.io.IOException;
import org.la4j.vector.Vector;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.vector.source.VectorSource;
import java.util.HashMap;
import java.util.Map;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import org.la4j.factory.Basic2DFactory;
import java.nio.channels.FileChannel;

public class Graph {
	static private final SemanticRelatness sr =
		new SemanticRelatness("/home/luoxing/inference1");
	static final Factory factory = new Basic2DFactory();
	static final Double lambda =0.1;
	private  List<Node> other;
	private  Matrix ul;
	private  Matrix uu; 
	private  Matrix DuulambdaIuuInv;
	private Vector Yu;
	private Vector lambdaY0;
	private Vector Yl ;
	static private Double transition(Node node, Node other){
		//if(node.isSameMention(other))
		//	return 0.0;
        return (sr.loc(node,other)+sr.rel(node,other)
                +sr.coref(node,other))/3.0;
    }
	
	static public double getSim(int i, int j, List<Node> u,List<Node> l){
		System.out.println("start getSim ul " +i +" " +j);
		if(i>u.size() || j> l.size()) return 0.0;
		return transition(u.get(i),l.get(j));
	}
	
	static public double getSim(int i, int j,List<Node> nodes){
		System.out.println("start getSim"+i+":"+j);
		if(i==j) return 1.0;
		if(i>nodes.size() || j> nodes.size()) return 0.0;
		return transition(nodes.get(i),nodes.get(j));
		
	}
	static class  SymSource implements MatrixSource{
		private int size;
		private Map<Integer,Double> values;
		private List<Node> lst;
		public SymSource(List<Node> lst){
			this.size = lst.size();
			this.values = new HashMap<>();
			this.lst = lst;
		}
		
		public int columns(){
			return size;
		}
		public int rows(){
			return size;
		}
		public double get(int i,int j){
			int offset = 0;
			if(i<=j)
				offset = i*size+j;
			else
				offset = j*size+i;
			if(null==values.get(offset)){
				values.put(offset,getSim(i,j,this.lst));
			}
			return values.get(offset);
		}
	}
	static public class LargeMatrix implements MatrixSource{
		public LargeMatrix(MatrixSource m) throws IOException{
			this.m=m;
			this.raf = new RandomAccessFile("/tmp/large","rw");
			try{
			long size1 = 8L*m.columns()*m.rows();
			for(long offset =0;offset<size1;offset+=MAPPING_SIZE){
				long size2 = Math.min(size1 -offset,MAPPING_SIZE);
				mapings.add(raf.getChannel()
							 .map(FileChannel.MapMode.READ_WRITE,
								  offset,size2));
			}
			}catch(IOException e){
				this.raf.close();
				e.printStackTrace();
			}
		}
		private long position(int x,int y){
			return (long)y*columns()+x;
		}
		public double get(int x,int y){
			long p = position(x,y)*8;
			int mapN = (int)(p/MAPPING_SIZE);
			int offN = (int)(p/MAPPING_SIZE);
			return mapings.get(mapN).getDouble(offN);
		}
		public int columns(){
			return m.columns();
		}
		public int rows(){
			return m.rows();
		}
		private static final int MAPPING_SIZE =1 <<30;
		private final List<MappedByteBuffer>
			mapings =  new ArrayList<MappedByteBuffer>();
		private final RandomAccessFile raf;
		private MatrixSource m;
		
		
	}
    public Graph(List<Node> train, List<Node> other){
		this.other = other;
		int l = train.size();
		int u = other.size();
		this.uu = factory.createMatrix(new SymSource(other));
		
		this.ul = factory.createMatrix(new MatrixSource(){
				public int rows(){return u;}
				public int columns(){return l;}
				public double get(int i,int j){
					if(i==j)	return 1.0;
					return getSim(i,j,other,train);
				}
				});
		Vector lst = factory.createVector( new VectorSource(){
				public int length(){return u;	}
				public double  get(int i){
					double value =0;
					int tmp=0;
					while(tmp<uu.columns()){
						value += uu.get(i,tmp);
						tmp++;
					}
					tmp=0;
					while(tmp<ul.columns()){
						value += ul.get(i,tmp);
						tmp++;
					}
					return 1/(value+lambda) ;
				}
			});
		this.DuulambdaIuuInv = factory.createMatrix(new MatrixSource(){
				public int rows(){	return u;	}
				public int columns(){	return u;		}
				public double get(int i,int j){
					if(i==j)
						return lst.get(i);
					else
						return 0.0;
				}
			});
		
		this.Yl=factory.createConstantVector(l,1.0);
		this.lambdaY0=factory.createVector( new VectorSource(){
				public int length()	{return u;}
				public double get(int i){return other.get(i).getPopularity();}
			});
		this.Yu=this.lambdaY0;
		this.lambdaY0= this.lambdaY0.multiply(lambda.doubleValue());
    }
	public Iterable<Node> toResultIterable(){
		Set<Node> set = toResult();
		return set;
	}
	public Iterator<Node> toResultIterator(){
		return toResultIterable().iterator();
	}
    public Set<Node> toResult(){
		for(int i=0;i<3;i++){
			next();
		}
        Set<Node> lst = new HashSet<Node>();
		for(Node node: other){
			boolean isFirst =true;
			for(Node res :lst){
				if(node.isSameMention(res)){
					isFirst =false;
					if(isLessThan(res,node)){
						lst.remove(res);
						lst.add(node);
					}
					break;
				}
			}
			if(isFirst) lst.add(node);
		}
        return lst;
    }
	
	
	
    public void  next(){
        //(Duu+uIuu)^-1(WuuYu+WulYl+uYu0);
		Yu=DuulambdaIuuInv
			.multiply(uu.multiply(Yu).add(ul.multiply(Yl)).add(lambdaY0));
    }
	
	private boolean isLessThan(Node node,Node o){
		return Yu.get(other.indexOf(node))< Yu.get(other.indexOf(o));
	}

	//--------------------------------------------------------
    List<String> travers = new ArrayList<String>();
    public void bfs(Node root){
        Queue<Node> q = new LinkedList<Node>();
        q.add(root);printNode(root);
        root.visited =true;
        while(!q.isEmpty()){
            Node n= q.remove();
            Node child;
            while(true){
                child = getUnVisitedChildNode(n);
                if(child ==null) break;
                child.visited =true;
                printNode(child);
                q.add(child);
            }
        }
        clearNodes();
    }
    private void clearNodes(){
        for(Node n: other){
            n.visited = false;
        }
    }
    private boolean isLinkTo(final Node x, final Node y){
        return false;
    }
    private  Node getUnVisitedChildNode(final Node n){
        for(Node node: other){
            if(isLinkTo(node,n)&& node.visited ==false){
                return node;
            }
        }
        return null;
    }
    private void printNode(final Node n){
        System.out.print("hh"+"|");
    }

}



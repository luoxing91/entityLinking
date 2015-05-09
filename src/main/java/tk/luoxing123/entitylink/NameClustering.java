package tk.luoxing123.entitylink;


import org.la4j.matrix.source.MatrixSource;
import org.la4j.factory.Factory;
import org.la4j.factory.CCSFactory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

import java.util.TreeSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;

import tk.luoxing123.utils.IterableUtils;
import tk.luoxing123.utils.LuceneDocIterator;

import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field;

import java.io.IOException;

import tk.luoxing123.utils.LuceneHelp;

import org.apache.lucene.index.IndexWriter;

public class NameClustering{
	public static double distance(int i ,int j ,List<Document> docs){
		if(docs.get(i).get("fileId")
		   .equals(docs.get(j).get("fileId"))){
			return 1.0;
		}
		return 0.0;
	}
	public static String nilString(Integer i){
		return "NIL"+i;
	}
	public static List<List<Document>> partition(List<Document> docs){
		List<List<Document>> lst = new ArrayList<List<Document>>();
		int max = 400;
		for(int i=0;i<9;i++){
			List<Document> tmp = new ArrayList<Document>();
			for(int j=0;j<max;j++){
				tmp.add(docs.get(i*max+j));
			}
			System.out.println(i);
			lst.add(tmp);
		}
		return lst;
	}
	public static void main(String[] args)	throws IOException{
		//List<Mention> mentions = new ArrayList<Mention>();
		//Random rand = new Random();
		List<Document> lstAll = IterableUtils
			.toList(new LuceneDocIterator("/home/luoxing/nil-mentions"),3900);
		NameClustering cluster;

		IndexWriter writer = LuceneHelp.makeWriter("/home/luoxing/nilLink1");
		List<List<Document>> part = partition(lstAll);

		//= new NameClustering(new double[][]{
		//			{1,1,1,},
		//			{1,1,0,1},
		//			{1,0,1,0},
		//			{1,1,0,1},

		//		});
		int sIndex=0;
		for(List<Document> lst :part){
			cluster=new NameClustering(new MatrixSource(){
			 		public int columns(){
			 			return 400;
					}
			 		public int rows(){
			 			return 400;
			 		}
			 		public double get(int ii,int jj){
						System.out.println(jj+"get distance"+ii);
						return distance(ii,jj,lst);
			 		}
			 	});
			List<Set<Integer>> res = cluster.toResult();
			Map<Integer,Integer> map = new HashMap<Integer,Integer>();

			for(Set<Integer> s: res){
				for(Integer index :s ){
					map.put(index,sIndex);
				}
				sIndex++;
			}
			for(Integer i:map.keySet()){
				Document doc = lst.get(i);
				doc.add(new StringField("nil",nilString(i),
										Field.Store.YES));
				lst.set(i,doc);
			}
			
			for(Document doc :lst){
				writer.addDocument(doc);
			}
		}
		writer.close();
	}

	public void print(){
		StringBuffer str = new StringBuffer(100);
		for(int i=0;i<this.m.rows();i++){
			for(int j=0;j<this.m.columns();j++){
				str.append(","+this.m.get(i,j));
			}
			str.append("\n");
		}
		System.out.println(str.toString());

	}
	public NameClustering(double[][]a ){
		this.factory=  new CCSFactory();
		this.m = factory.createMatrix(a);
		run();
	}
	public NameClustering(MatrixSource source){
		this.factory=  new CCSFactory();
		this.m= factory.createMatrix(source);
		run();
	}
	private void run(){
		normalizeByColumn();
		int j=0;
		while(true){
			Matrix current = m.copy();
			//print();
			System.out.println(j);
			expansion();
			inflation();
			normalizeByColumn();
			j++;
			if(isSame(m,current)&&j>50)
				break;
		}
		fileMap=new LinkedList<Set<Integer>>();
		//TreeSet<Integer> set = new TreeSet<>();
		//fileMap.add(set);
		//otherProcess();
		//for(int j=0;j<m.columns();j++){
		//	for(int i=0;i<m.rows();i++){
		//		process(i,j);
		//	}
		//}
		check = new ArrayList<Boolean>();
		for(int i=0;i<m.rows();i++){
			this.check.add(false);
		}
		toSetResult();
		printRest();
		
	}
	public List<Set<Integer>> toResult(){
		return this.fileMap;
	}
	private Matrix m;
	private Factory factory;
	private List<Set<Integer>> fileMap;
	private List<Boolean> check;
	private void toSetResult(){
		for(int i=0; i<m.rows();i++){
			Set<Integer> set =new TreeSet<>();
			for(int j=0; j<m.rows();j++){
				if(m.get(i,j)>0.01&&!check.get(j)){
					set.add(j);
					check.set(j,true);
				}
			}
			if(!set.isEmpty())
				this.fileMap.add(set);
		}
	}

	private int i=0;
	private void printRest(){
		System.out.println("this is the " +i);
		i++;
		/*
		StringBuffer str = new StringBuffer(100);
		for(Set<Integer> set1: fileMap){
			for(Integer ii : set1){
				str.append(","+ii);
			}
			str.append("\nxx");
		}
		System.out.println(str.toString()+"end");
		*/
	}


	static private boolean  isSame(Matrix m,Matrix o){
			for(int i=0;i<o.rows();i++){
				for(int j=0;j<o.columns();j++){
					if(Math.abs(m.get(i,j)-o.get(i,j))>0.01){
						return false;
					}
				}
			}
			return true;
	}

	 private   void  expansion(){
		this.m = m.multiply(m);

		for(int j=0;j<m.columns();j++){
			for(int i=0;i<m.rows();i++){
				double value = m.get(i,j);
				m.set(i,j,value);
			}
		}
		
	}
	private void  inflation(){
		for(int j=0;j<m.columns();j++){
			for(int i=0;i<m.rows();i++){
				double value = Math.pow(m.get(i,j),2);
				m.set(i,j,value);
			}
		}
	}
	
	private void normalizeByColumn(){
		Vector v = factory.createVector(m.columns());
		 for(int j=0;j<m.columns();j++){
			 double sum =0.0;
			 for(int i=0;i<m.rows();i++){
				 sum+= m.get(i,j);
			 }
			 v.set(j,sum);
		 }
		for(int j=0;j<m.columns();j++){
			for(int i=0;i<m.rows();i++){
				m.set(i,j,m.get(i,j)/v.get(j));
			 }
		}

	}

	

}
	
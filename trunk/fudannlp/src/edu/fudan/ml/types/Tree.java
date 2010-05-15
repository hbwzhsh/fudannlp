package edu.fudan.ml.types;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Tree implements Serializable {
	public int size=0;
	int depth=0;
	List<Integer> nodes = new ArrayList<Integer>();
	Set<Integer> leafs = new HashSet<Integer>();
	HashMap<Integer,Set<Integer>> edges = new HashMap<Integer,Set<Integer>>();
	HashMap<Integer,Integer> edgesInv = new HashMap<Integer,Integer>();
	HashMap<Integer,Set<Integer>> hier = new HashMap<Integer, Set<Integer>>();
	public Integer getNode(int i) {
		return nodes.get(i);
	}
	public void loadFromFileWithEdge(String file, Alphabet alphabet) throws IOException {
		File f = new File(file);
		FileInputStream in = new FileInputStream(f);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line;
		while((line=reader.readLine())!=null){
			String[] tok = line.split(" ");
			addEdge(alphabet.lookupIndex(tok[0]),alphabet.lookupIndex(tok[1]));
		}
		travel();
	}
	public void loadFromFileWithPath(String file, Alphabet alphabet) throws IOException {
		File f = new File(file);
		FileInputStream in = new FileInputStream(f);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line;
		while((line=reader.readLine())!=null){
			String[] tok = line.split(" ");
			for(int i=0;i<tok.length-1;i++){
				addEdge(alphabet.lookupIndex(tok[i]),alphabet.lookupIndex(tok[i+1]));
			}
		}
		travel();
	}
	public void loadFromFile(String file) throws IOException{
		File f = new File(file);
		FileInputStream in = new FileInputStream(f);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line;
		while((line=reader.readLine())!=null){
			String[] tok = line.split(" ");
			addEdge(Integer.parseInt(tok[0]),Integer.parseInt(tok[1]));
		}
		travel();
	}
	public Set<Integer> getLeafs(){
		return 	leafs;
	}
	private void travel() {
		for(int i=0;i<nodes.size();i++){
			int l = getLevel(nodes.get(i));
			if(l>hier.size()|| hier.get(l)==null){
				Set set = new HashSet<Integer>();
				hier.put(l,set);
			}
			hier.get(l).add(i);
			if(edges.get(i)==null){
				leafs.add(i);
			}
		}
	}
	private int getLevel(int i) {
		int n=0;
		Integer j=i;
		while((j=edgesInv.get(j))!=null){
			n++;
		}
		return n;
	}
	private void addEdge(int i, int j) {
		if(!nodes.contains(i)){
			nodes.add(i);
			edges.put(i, new HashSet<Integer>());
			size++;
		}else if(!edges.containsKey(i)){
			edges.put(i, new HashSet<Integer>());
		}
		if(!nodes.contains(j)){
			nodes.add(j);
			size++;
		}
		edgesInv.put(j, i);
		if(!edges.get(i).contains(j)){
			edges.get(i).add(j);
		}
	}
	public static void main(String[] args) throws IOException{
		String file = "D:/Datasets/wipo/e.txt";
		Tree t = new Tree();
		t.loadFromFile(file);
		System.out.println(t.size);
		System.out.println(t.hier.size());
		t.dist(5, 6);
	}
	public ArrayList<Integer> getPath(Integer i) {
		ArrayList<Integer>  list= new ArrayList<Integer> ();
		list.add(i);
		Integer j=i;
		while((j=edgesInv.get(j))!=null){
			list.add(j);
		}
		return list;
	}
	public ArrayList<Integer> getAnc(Integer i) {
		ArrayList<Integer>  list= new ArrayList<Integer> ();
		Integer j=i;
		while((j=edgesInv.get(j))!=null){
			list.add(j);
		}
		return list;
	}
	public int[] getAncIdx(Integer i) {
		ArrayList<Integer>  list = getAnc(i);
		int[] idx = new int[list.size()];
		for(int j=0;j<list.size();j++){
			idx[j] = (int) list.get(j);
		}
		return idx;
	}
	public int dist(Integer i, Integer j) {
		ArrayList<Integer> anci = getPath(i);
		ArrayList<Integer> ancj = getPath(j);
		boolean t = true;
		int idxi;
		for(idxi =0;idxi<anci.size();idxi++){
			if(ancj.contains(anci.get(idxi))){
				break;
			}
		}
		idxi--;
		int idxj;
		for(idxj =0;idxj<ancj.size();idxj++){
			if(anci.contains(ancj.get(idxj))){
				break;
			}
		}
		idxj--;
		int d = getLevel(anci.get(0))- getLevel(anci.get(idxi)) - getLevel(ancj.get(idxj))+getLevel(ancj.get(0))+2;
		return d;
	}
}

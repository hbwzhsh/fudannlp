package edu.fudan.ml.similarity;
import java.util.HashSet;
import edu.fudan.nlp.resources.CiLin;
public class EditDistanceWithSemantic extends EditDistance implements ISimilarity{
	private int wordlen;
	private HashSet<String> synSet;
	public EditDistanceWithSemantic(){
		wordlen = 2;
    	String dataFile = "\\\\10.11.7.3\\f$\\对于共享版《同义词词林》的改进\\improvedThesaurus.data";
		synSet = (HashSet<String>) CiLin.buildSynonymSet(dataFile);
	}
	public <E> float calc(E item1, E item2) throws Exception {   
		if(!(item1 instanceof String)||!(item2 instanceof String))
			throw new Exception("只能处理字符串操作");
		String str1 = (String) item1;
		String str2 = (String) item2;
		float d[][];
		int n = str1.length();   
		int m = str2.length();   
		int i;
		int j;
		char ch1;
		char ch2;
		int cost;
		if(n == 0) {   
			return m;   
		}   
		if(m == 0) {   
			return n;   
		}   
		d = new float[n+1][m+1];   
		for(i=0; i<=n; i++) {
			d[i][0] = i;   
		}   
		for(j=0; j<=m; j++) {
			d[0][j] = j;   
		}   
		for(i=1; i<=n; i++) {
			char cX = str1.charAt(i-1);   
			for(j=1; j<=m; j++) {   
				for(int ii=1;ii<=wordlen;ii++){
					if(ii+i-1>str1.length())
						break;
					for(int jj=1;jj<=wordlen;jj++){
						if(jj+j-1>str2.length())
							break;
						String combine = str1.substring(i-1, ii+i-1)+"|"+str2.substring(j-1,jj+j-1);
						if(synSet.contains(combine)){
							if(d[i+ii-1][j+jj-1]>0)
								d[i+ii-1][j+jj-1]=Math.min(d[i+ii-1][j+jj-1],d[i-1][j-1]+0.1f);
							else
								d[i+ii-1][j+jj-1]=d[i-1][j-1]+0.1f;
						}
					}
				}
				char cY = str2.charAt(j-1);   
				float temp = (cX == cY ? d[i-1][j-1]
							: costReplace(cX,cY) + d[i-1][j-1]);
				if(d[i][j]>0){
					temp = Math.min(temp, d[i][j]);
				}
				d[i][j] = Math.min(temp,
						             Math.min(costDel(cX)+d[i-1][j],
						            		costIns(cY)+d[i][j-1]));
			}   
		}   
		return d[n][m];   
	}   
    public static void main(String[] args) {
    	EditDistanceWithSemantic ed = new EditDistanceWithSemantic();
        String str1 = "发行时间 ";   
        String str2 = "生日";   
        System.out.println("ld="+ed.calc(str1, str2));   
        //System.out.println("sim="+ed.sim(str1, str2));   
    }
}

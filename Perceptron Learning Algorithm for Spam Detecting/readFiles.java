import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** 用Sample抽象的好处是把spam和ham结合在一起了，不用分开考虑，使程序更简洁*/
class Sample {
	/** 样本分类中表示为+1表示ham 或者-1(or 0)表示spam */
    public int label;

    /** 
     * 逻辑回归实现中，HashMap<...>的作用是 统计不同的词数 和 便于TF-IDF中计算n(j)
     * 朴素贝叶斯实现中， HashMap<...>的作用只是 统计不同的词数
     * 感知学习算法，HashMap<...>的作用是 统计不同的词数 和 便于TF-IDF中计算n(j)
     **/
    public HashMap<String, Integer> map;

    public Sample(HashMap<String, Integer> map, int label) {
        this.label = label;
        this.map = map;
    }
}

public class readFiles {
	
	public static final int PositiveLable = 1;
    // NegativeLable=0(约85%)的时候，逻辑回归的精度明显高于NegativeLable=1(约65%)
	public static final int NegativeLable = -1;	// or "-1" "0"
	
	/**
	 * @param 输入所有的train的ham和spam文件,
	 * @return 并返回所有Samples的List数组，Samples巧妙的结合了ham和spam文件
	 **/
	public static List<Sample> read(String isRemoveStopWords, String filePath1, String filePath2) //"./train/ham"
	{	
		File HamDir = new File(filePath1); 	//testHamDir是ham文件目录
		File SpamDir = new File(filePath2);
		if (!SpamDir.isDirectory() || !HamDir.isDirectory()) 
			throw new IllegalArgumentException("Fail to open the test directory !");
		
		String[] HamNameList = HamDir.list(); //HamNameList是trainHamDir下的所有文件名列表
		String[] SpamNameList = SpamDir.list();
		List<Sample> Samples = new ArrayList<Sample>(); // Samples存储所有训练样本
		
		//存储trainHamDir下的所有文件到Samples
		for(int i = 0; i <HamNameList.length; i++) {
			String path = HamDir.getPath() + File.separator + HamNameList[i];
			HashMap<String, Integer> hamCountMap = new HashMap<String, Integer>();

			/**"\\s+"能分割任意空格，但是在这个split("\\s+")和split(" ")对精度的提升没有区别；
			 * replaceAll(...)去除所有的标点符，逻辑回归和正则表达式的精度都提升了0.3% */
			String str = null;
			if(isRemoveStopWords.equalsIgnoreCase("yes")) // Remove StopWords
				str = Stopwords.removeStopWords(getText(path));
				//str = Stopwords.removeStopWords(getText(path).replaceAll("[\\p{Punct}\\pP]", ""));
			else	 // do not Remove StopWords
				str = getText(path);
			StoreAndCountWords(hamCountMap, str.split("\\s+")); 
			
			Sample oneSample = new Sample(hamCountMap, PositiveLable); //注意: PositiveLable表示正样本
			Samples.add(oneSample);
		}
		//存储SpamDir下的所有文件到Samples
		for(int i = 0; i <SpamNameList.length; i++) {
			String path = SpamDir.getPath() + File.separator + SpamNameList[i]; //生成读取路径
			HashMap<String, Integer> spamCountMap = new HashMap<String, Integer>();

			String str = null;
			if(isRemoveStopWords.equalsIgnoreCase("yes")) // Remove StopWords
				str = Stopwords.removeStopWords(getText(path));
				//str = Stopwords.removeStopWords(getText(path).replaceAll("[\\p{Punct}\\pP]", ""));
			else	 // do not Remove StopWords
				str = getText(path);
			StoreAndCountWords(spamCountMap, str.split("\\s+"));
			
			Sample oneSample = new Sample(spamCountMap, NegativeLable); //注意: NegativeLable表示负样本
			Samples.add(oneSample);
		}
		return Samples;
	}
	
	/** Using HashMap to store and count words!!! */
	public static void StoreAndCountWords(Map<String, Integer> hashMap, String[] words)
	{
		for(int i = 0; i <words.length; i++) {
			if (hashMap.containsKey(words[i])) { 
				hashMap.put(words[i], hashMap.get(words[i]) + 1); //如果有的话，加1
			} else { 
				hashMap.put(words[i], 1); //如果没有的话，添加并赋初始值1
			}
		}
	}
	
	/** @return 返回给定路径的文本文件内容，是一条很长的String */
	public static String getText(String filePath)
	{
		try {
			//InputStreamReader isReader =new InputStreamReader(new FileInputStream(filePath),"GBK");
			InputStreamReader isReader =new InputStreamReader(new FileInputStream(filePath));
			BufferedReader reader = new BufferedReader(isReader);
			String aline;
			StringBuilder sb = new StringBuilder();
	
			while ((aline = reader.readLine()) != null)
				sb.append(aline + " ");
			isReader.close();
			reader.close();
			return sb.toString();
			
        } catch (IOException ex) {  
            ex.printStackTrace();  
        } 
        return "";
	}
}

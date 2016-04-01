import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** ��Sample����ĺô��ǰ�spam��ham�����һ���ˣ����÷ֿ����ǣ�ʹ��������*/
class Sample {
	/** ���������б�ʾΪ+1��ʾham ����-1(or 0)��ʾspam */
    public int label;

    /** 
     * �߼��ع�ʵ���У�HashMap<...>�������� ͳ�Ʋ�ͬ�Ĵ��� �� ����TF-IDF�м���n(j)
     * ���ر�Ҷ˹ʵ���У� HashMap<...>������ֻ�� ͳ�Ʋ�ͬ�Ĵ���
     * ��֪ѧϰ�㷨��HashMap<...>�������� ͳ�Ʋ�ͬ�Ĵ��� �� ����TF-IDF�м���n(j)
     **/
    public HashMap<String, Integer> map;

    public Sample(HashMap<String, Integer> map, int label) {
        this.label = label;
        this.map = map;
    }
}

public class readFiles {
	
	public static final int PositiveLable = 1;
    // NegativeLable=0(Լ85%)��ʱ���߼��ع�ľ������Ը���NegativeLable=1(Լ65%)
	public static final int NegativeLable = -1;	// or "-1" "0"
	
	/**
	 * @param �������е�train��ham��spam�ļ�,
	 * @return ����������Samples��List���飬Samples����Ľ����ham��spam�ļ�
	 **/
	public static List<Sample> read(String isRemoveStopWords, String filePath1, String filePath2) //"./train/ham"
	{	
		File HamDir = new File(filePath1); 	//testHamDir��ham�ļ�Ŀ¼
		File SpamDir = new File(filePath2);
		if (!SpamDir.isDirectory() || !HamDir.isDirectory()) 
			throw new IllegalArgumentException("Fail to open the test directory !");
		
		String[] HamNameList = HamDir.list(); //HamNameList��trainHamDir�µ������ļ����б�
		String[] SpamNameList = SpamDir.list();
		List<Sample> Samples = new ArrayList<Sample>(); // Samples�洢����ѵ������
		
		//�洢trainHamDir�µ������ļ���Samples
		for(int i = 0; i <HamNameList.length; i++) {
			String path = HamDir.getPath() + File.separator + HamNameList[i];
			HashMap<String, Integer> hamCountMap = new HashMap<String, Integer>();

			/**"\\s+"�ָܷ�����ո񣬵��������split("\\s+")��split(" ")�Ծ��ȵ�����û������
			 * replaceAll(...)ȥ�����еı������߼��ع��������ʽ�ľ��ȶ�������0.3% */
			String str = null;
			if(isRemoveStopWords.equalsIgnoreCase("yes")) // Remove StopWords
				str = Stopwords.removeStopWords(getText(path));
				//str = Stopwords.removeStopWords(getText(path).replaceAll("[\\p{Punct}\\pP]", ""));
			else	 // do not Remove StopWords
				str = getText(path);
			StoreAndCountWords(hamCountMap, str.split("\\s+")); 
			
			Sample oneSample = new Sample(hamCountMap, PositiveLable); //ע��: PositiveLable��ʾ������
			Samples.add(oneSample);
		}
		//�洢SpamDir�µ������ļ���Samples
		for(int i = 0; i <SpamNameList.length; i++) {
			String path = SpamDir.getPath() + File.separator + SpamNameList[i]; //���ɶ�ȡ·��
			HashMap<String, Integer> spamCountMap = new HashMap<String, Integer>();

			String str = null;
			if(isRemoveStopWords.equalsIgnoreCase("yes")) // Remove StopWords
				str = Stopwords.removeStopWords(getText(path));
				//str = Stopwords.removeStopWords(getText(path).replaceAll("[\\p{Punct}\\pP]", ""));
			else	 // do not Remove StopWords
				str = getText(path);
			StoreAndCountWords(spamCountMap, str.split("\\s+"));
			
			Sample oneSample = new Sample(spamCountMap, NegativeLable); //ע��: NegativeLable��ʾ������
			Samples.add(oneSample);
		}
		return Samples;
	}
	
	/** Using HashMap to store and count words!!! */
	public static void StoreAndCountWords(Map<String, Integer> hashMap, String[] words)
	{
		for(int i = 0; i <words.length; i++) {
			if (hashMap.containsKey(words[i])) { 
				hashMap.put(words[i], hashMap.get(words[i]) + 1); //����еĻ�����1
			} else { 
				hashMap.put(words[i], 1); //���û�еĻ�����Ӳ�����ʼֵ1
			}
		}
	}
	
	/** @return ���ظ���·�����ı��ļ����ݣ���һ���ܳ���String */
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

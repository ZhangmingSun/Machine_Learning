import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/** 把每一个样本，不管正负，转换成一个Instance*/
class Instance {
    public int label;	// 样本分类中表示为+1 ham 或者-1 spam

    /** String表示文本中的一个单词， Double表示该单词对应归一化之后的TF-IDF值*/
    public HashMap<String, Double> data;

    public Instance(HashMap<String, Double> data, int label) {
        this.label = label;
        this.data = data;
    }
}

public class Perceptron_Learning_Algorithm {
	
    /**
	private String isRemoveStopWords = "no";
    private double rate = 0.01; 		// the learning rate
    private int ITERATIONS = 100;		// the number of iterations
    */
	private String isRemoveStopWords;
    private double rate;
    private int ITERATIONS;
    //private double regularParam;
    double Threshold = 0.0; // 测试判断的阈值
    
	/** V_Table的key是训练样本中单词的不同种类数，V_Table的value是逻辑回归中key对应的权重weight */
    private HashMap<String, Double> V_Table;
    private double V_Table_Initial_Weight = 0.0;    // X.get(key) 就是该word对应的 TF*IDF值

    public Perceptron_Learning_Algorithm() {
		// TODO Auto-generated constructor stub
    	this.isRemoveStopWords = "no";
    	this.rate = 0.01; 				/** the learning rate */
    	this.ITERATIONS = 100;			/** the number of iterations */
    	//this.regularParam = 0.00;
	}
    public Perceptron_Learning_Algorithm(String isRemoveStopWords, double LearningRate, int Iterations) {
    	this.isRemoveStopWords = isRemoveStopWords;
    	this.rate = LearningRate; 				/** the learning rate */
    	this.ITERATIONS = Iterations;			/** the number of iterations */
    	//this.regularParam = RegularParam;
	}
    
    //private double sigmoid(double z) { return 1 / (1 + Math.exp(-z));}
    
    /**
     * "Logistic Regression" uses gradient descent method
     * "Perceptron Learning Algorithm" uses Perceptron Train Rule
     */
    public void train(List<Instance> instances) {
        for (int n=0; n<ITERATIONS; n++) {
            //double lik = 0.0;
            for (int i=0; i<instances.size(); i++) {

            	HashMap<String, Double> data = instances.get(i).data;
                double actual_output = classify(data);	// the actual output from classify()
                int label = instances.get(i).label;
                
        		Iterator<String> it = data.keySet().iterator();
        		while(it.hasNext()) {
        			String key = it.next();
        			
        			/** 类似于 “weights[j] = weights[j] + rate * (desired_output - actual_output) * x[j];” */
        			double tmp = V_Table.get(key) + rate * ((double)label - actual_output) * data.get(key);
        			
        			/** 类似于 “weights[j] = weights[j] + rate * (label - predicted) * x[j] - regularTerm;” 
        			double tmp = V_Table.get(key) + rate * ((double)label - predicted) * data.get(key) 
        					- rate * regularParam * V_Table.get(key); //末尾一项是正则项
        			 */
        			V_Table.put(key, tmp);
        		}
                //not necessary for learning
                //lik += (double)label * Math.log(classify(data)) + (1-(double)label) * Math.log(1- classify(data));
            }
            //System.out.println("iteration: " + n + " " + Arrays.toString(weights) + " mle: " + lik);
            //System.out.println("iteration: " + n + " " + " mle: " + lik);
        }
    }/**/
    
    private double classify(HashMap<String, Double> X) {
        double logit = .0;
		Iterator<String> it = X.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			//这条判断语句主要为了防止，在test时，出现未碰到的词从而出错！！！
			if(V_Table.containsKey(key)==true)
				logit += V_Table.get(key) * X.get(key);
		}
		/** implement the function of the perceptron's Threshold(sign) function*/
		if(logit>0)
			logit = 1.0;
		else
			logit = -1.0;
		
		return logit;
        //return sigmoid(logit);
    }/**/
    
	/** Instances存储所有TF*IDF值向量，每个向量代表一个样本*/
	//List<Instance> Instances;
	public void train_Perceptron_Learning_Algorithm() {
		
		List<Sample> Samples = readFiles.read(isRemoveStopWords, "./train/ham", "./train/spam");
		
		V_Table = Generate_V_Table(Samples);

		List<Instance> Instances = BuildInstanceList_By_ComputeTFIDF(Samples);
		
		train(Instances);

		/*
		//输出所有的单词对应的权重
		int count = 0;
		Iterator it = V_Table.keySet().iterator();
		while(it.hasNext()) {
			String key = (String)it.next();
			System.out.println(key + "=" + V_Table.get(key));
			count++;
		}
		System.out.println("Count = " + count);*/
	}
	
	public void test_Perceptron_Learning_Algorithm() {
		List<Sample> Samples = readFiles.read(isRemoveStopWords, "./test/ham", "./test/spam");
		//V_Table = Generate_V_Table(Samples);
		List<Instance> Instances = BuildInstanceList_By_ComputeTFIDF(Samples);
		
		//System.out.println("Number of All Example = " + Instances.size());
		System.out.println("=================Perceptron Learning Algorithm=================");
		int CorrectCount = 0;
		int ErrorCount = 0;
		//int NaN = 0;
        for (int i=0; i<Instances.size(); i++) {
        	HashMap<String, Double> data = Instances.get(i).data;
        	
            double predicted = classify(data); //感知器的输出是1或者-1
            //System.out.println("predicted = " + predicted);
            
            int label = Instances.get(i).label;
            if(label == readFiles.PositiveLable && predicted < Threshold)
            	ErrorCount++;
            else if(label == readFiles.PositiveLable && predicted >= Threshold)
            	CorrectCount++;
            else if(label == readFiles.NegativeLable && predicted >= Threshold)
            	ErrorCount++;
            else if(label == readFiles.NegativeLable && predicted < Threshold)
            	CorrectCount++;
            else
            	;//NaN++;
            	
        }
        System.out.println("CorrectCount = " + CorrectCount);
        System.out.println("ErrorCount = " + ErrorCount);
        //System.out.println("NaNCount = " + NaN);
        System.out.println("Correct Accuracy = " + (double)CorrectCount/Instances.size());
	}
	
	/**
	 * @param 输入train中预处理后的所有samples,
	 * @return 并返回Instances列表，每Instances表示一个TF*IDF向量(hashTable存储)，即一个样本
	 **/
    public List<Instance> BuildInstanceList_By_ComputeTFIDF(List<Sample> samples)
	{
		/** 总样本数，计算TF_IDF中需要用到*/
    	int D = samples.size();
    	double Normalization_Denominator = 0; //归一化的分母值，等于：先每个数的平方相加，再开根号
    	
    	/** Instances存储所有TF*IDF值向量，每个向量代表一个样本*/
    	List<Instance> Instances = new ArrayList<Instance>();
		for(int i = 0; i <samples.size(); i++) {
		    /** 文本中每个单词key对应一个TF*IDF值 */
			HashMap<String, Double> mapTFIDF = new HashMap<String, Double>();
			Normalization_Denominator = 0;
			
			int AllWordsInOneSample = 0;
			Iterator<String> it1 = samples.get(i).map.keySet().iterator();
			while(it1.hasNext()) { 
				String key = it1.next(); //key就是一个单词
				AllWordsInOneSample += samples.get(i).map.get(key);
			}
			
			/** 每次遍历，把一个文本sample 转换成 TF*IDF值组成的向量*/
			Iterator<String> it = samples.get(i).map.keySet().iterator(); //采用Iterator遍历HashMap
			while(it.hasNext()) {  
				String key = it.next(); //key就是一个单词
				
				/** Count word j's occurrence times in the current Sample*/
				int Word_InOneSample = samples.get(i).map.get(key);
				
				/** Statistic word j's occurrence in all Samples, including ham and spam*/
				int WordOccurs_InAllSamples = Count_WordOccurs_InAllSamples(key, samples);
				
				/** Count word j's TF-IDF Value*/
				// 下面的公式1效果不好，可能是没有按TF的公式来计算TF
				//double Word_TF_IDF = (1 + Math.log(Word_InOneSample)) * Math.log((double)(D+1)/(WordOccurs_InAllSamples+1));
				// 下面的公式2效果很好，注意：TF = 某次在文章中出现的次数 / 文章的总词数
				double Word_TF_IDF = ((double)Word_InOneSample/ AllWordsInOneSample) * Math.log((double)D/(WordOccurs_InAllSamples+1));
				
				/** Compute Denominator in Normalization*/
				Normalization_Denominator += Word_TF_IDF * Word_TF_IDF;
				
				mapTFIDF.put(key, Word_TF_IDF);
			}
			
			/** 归一化：Normalization of mapTFIDF */
			//System.out.println("before Math.sqrt =  " + Normalization_Denominator);
			Normalization_Denominator = Math.sqrt(Normalization_Denominator); //开平方根
			//System.out.println("Math.sqrt =  " + Normalization_Denominator);
			Iterator<String> pointer = mapTFIDF.keySet().iterator();
			while(pointer.hasNext()) {
				String key = pointer.next();
				mapTFIDF.put(key, (double)mapTFIDF.get(key) / (double)Normalization_Denominator); //归一化，覆盖原来的那个值
			}
			
			/** Create Instance, very important*/
			Instance oneInstance = new Instance(mapTFIDF, samples.get(i).label);
			Instances.add(oneInstance);
		}
		return Instances;
	}
	
	/** Count word j's occurrence in all Samples, including ham and spam*/
	public int Count_WordOccurs_InAllSamples(String word, List<Sample> samples)
	{
		int WordOccurs=0;
		for(int i = 0; i <samples.size(); i++) {
			if(samples.get(i).map.containsKey(word) == true)
				WordOccurs++;
		}
		return WordOccurs;
	}
	
	/** 创建词汇表 */
	public HashMap<String, Double> Generate_V_Table(List<Sample> samples)
	{
		HashMap<String, Double> VTable = new HashMap<String, Double>();
		for(int i = 0; i <samples.size(); i++) {
			//采用Iterator遍历HashMap
			Iterator<String> it = samples.get(i).map.keySet().iterator(); 
			while(it.hasNext()) {
				String key = it.next(); 		//key就是一个单词
				if(VTable.containsKey(key) == false)	//if VTable don't contain key, then store it to VTable
					VTable.put(key, V_Table_Initial_Weight); 
			}
		}
		return VTable;
	}
	// 没有R，ITER=100时，10(0.949) 1(0.956) 0.1(0.949)
	// 有R=0.001，ITERS=100时，1(0.951) 0.1(0.9539) <<<<<<这个条件不错

	// ITERS=100时，rate=0.01, R=0.001(0.920);
	// ITERS=500时，rate=0.01, R=0.001(0.949);
	// ITERS=1000时，rate=0.01, R=0.001(0.953);
	// ITERS=5000时，rate=0.01, R=0.001(0.947);

	// ITERS=100时，rate=0.01, R=0(0.914); R=0.001(0.920); R=0.01(0.926); R=0.1(0.926); R=1(0.939); R=10(0.949); R=20(0.930);
	// ITERS=500时，rate=0.001, R=0(0.859); R=0.001(0.861); R=0.01(0.878); 
	// 续上 R=0.1(0.889); R=1(0.924); R=10(0.935); R=100(0.949); R=200(0.930);

}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/** ��ÿһ������������������ת����һ��Instance*/
class Instance {
    public int label;	// ���������б�ʾΪ+1 ham ����-1 spam

    /** String��ʾ�ı��е�һ�����ʣ� Double��ʾ�õ��ʶ�Ӧ��һ��֮���TF-IDFֵ*/
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
    double Threshold = 0.0; // �����жϵ���ֵ
    
	/** V_Table��key��ѵ�������е��ʵĲ�ͬ��������V_Table��value���߼��ع���key��Ӧ��Ȩ��weight */
    private HashMap<String, Double> V_Table;
    private double V_Table_Initial_Weight = 0.0;    // X.get(key) ���Ǹ�word��Ӧ�� TF*IDFֵ

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
        			
        			/** ������ ��weights[j] = weights[j] + rate * (desired_output - actual_output) * x[j];�� */
        			double tmp = V_Table.get(key) + rate * ((double)label - actual_output) * data.get(key);
        			
        			/** ������ ��weights[j] = weights[j] + rate * (label - predicted) * x[j] - regularTerm;�� 
        			double tmp = V_Table.get(key) + rate * ((double)label - predicted) * data.get(key) 
        					- rate * regularParam * V_Table.get(key); //ĩβһ����������
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
			//�����ж������ҪΪ�˷�ֹ����testʱ������δ�����ĴʴӶ���������
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
    
	/** Instances�洢����TF*IDFֵ������ÿ����������һ������*/
	//List<Instance> Instances;
	public void train_Perceptron_Learning_Algorithm() {
		
		List<Sample> Samples = readFiles.read(isRemoveStopWords, "./train/ham", "./train/spam");
		
		V_Table = Generate_V_Table(Samples);

		List<Instance> Instances = BuildInstanceList_By_ComputeTFIDF(Samples);
		
		train(Instances);

		/*
		//������еĵ��ʶ�Ӧ��Ȩ��
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
        	
            double predicted = classify(data); //��֪���������1����-1
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
	 * @param ����train��Ԥ����������samples,
	 * @return ������Instances�б�ÿInstances��ʾһ��TF*IDF����(hashTable�洢)����һ������
	 **/
    public List<Instance> BuildInstanceList_By_ComputeTFIDF(List<Sample> samples)
	{
		/** ��������������TF_IDF����Ҫ�õ�*/
    	int D = samples.size();
    	double Normalization_Denominator = 0; //��һ���ķ�ĸֵ�����ڣ���ÿ������ƽ����ӣ��ٿ�����
    	
    	/** Instances�洢����TF*IDFֵ������ÿ����������һ������*/
    	List<Instance> Instances = new ArrayList<Instance>();
		for(int i = 0; i <samples.size(); i++) {
		    /** �ı���ÿ������key��Ӧһ��TF*IDFֵ */
			HashMap<String, Double> mapTFIDF = new HashMap<String, Double>();
			Normalization_Denominator = 0;
			
			int AllWordsInOneSample = 0;
			Iterator<String> it1 = samples.get(i).map.keySet().iterator();
			while(it1.hasNext()) { 
				String key = it1.next(); //key����һ������
				AllWordsInOneSample += samples.get(i).map.get(key);
			}
			
			/** ÿ�α�������һ���ı�sample ת���� TF*IDFֵ��ɵ�����*/
			Iterator<String> it = samples.get(i).map.keySet().iterator(); //����Iterator����HashMap
			while(it.hasNext()) {  
				String key = it.next(); //key����һ������
				
				/** Count word j's occurrence times in the current Sample*/
				int Word_InOneSample = samples.get(i).map.get(key);
				
				/** Statistic word j's occurrence in all Samples, including ham and spam*/
				int WordOccurs_InAllSamples = Count_WordOccurs_InAllSamples(key, samples);
				
				/** Count word j's TF-IDF Value*/
				// ����Ĺ�ʽ1Ч�����ã�������û�а�TF�Ĺ�ʽ������TF
				//double Word_TF_IDF = (1 + Math.log(Word_InOneSample)) * Math.log((double)(D+1)/(WordOccurs_InAllSamples+1));
				// ����Ĺ�ʽ2Ч���ܺã�ע�⣺TF = ĳ���������г��ֵĴ��� / ���µ��ܴ���
				double Word_TF_IDF = ((double)Word_InOneSample/ AllWordsInOneSample) * Math.log((double)D/(WordOccurs_InAllSamples+1));
				
				/** Compute Denominator in Normalization*/
				Normalization_Denominator += Word_TF_IDF * Word_TF_IDF;
				
				mapTFIDF.put(key, Word_TF_IDF);
			}
			
			/** ��һ����Normalization of mapTFIDF */
			//System.out.println("before Math.sqrt =  " + Normalization_Denominator);
			Normalization_Denominator = Math.sqrt(Normalization_Denominator); //��ƽ����
			//System.out.println("Math.sqrt =  " + Normalization_Denominator);
			Iterator<String> pointer = mapTFIDF.keySet().iterator();
			while(pointer.hasNext()) {
				String key = pointer.next();
				mapTFIDF.put(key, (double)mapTFIDF.get(key) / (double)Normalization_Denominator); //��һ��������ԭ�����Ǹ�ֵ
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
	
	/** �����ʻ�� */
	public HashMap<String, Double> Generate_V_Table(List<Sample> samples)
	{
		HashMap<String, Double> VTable = new HashMap<String, Double>();
		for(int i = 0; i <samples.size(); i++) {
			//����Iterator����HashMap
			Iterator<String> it = samples.get(i).map.keySet().iterator(); 
			while(it.hasNext()) {
				String key = it.next(); 		//key����һ������
				if(VTable.containsKey(key) == false)	//if VTable don't contain key, then store it to VTable
					VTable.put(key, V_Table_Initial_Weight); 
			}
		}
		return VTable;
	}
	// û��R��ITER=100ʱ��10(0.949) 1(0.956) 0.1(0.949)
	// ��R=0.001��ITERS=100ʱ��1(0.951) 0.1(0.9539) <<<<<<�����������

	// ITERS=100ʱ��rate=0.01, R=0.001(0.920);
	// ITERS=500ʱ��rate=0.01, R=0.001(0.949);
	// ITERS=1000ʱ��rate=0.01, R=0.001(0.953);
	// ITERS=5000ʱ��rate=0.01, R=0.001(0.947);

	// ITERS=100ʱ��rate=0.01, R=0(0.914); R=0.001(0.920); R=0.01(0.926); R=0.1(0.926); R=1(0.939); R=10(0.949); R=20(0.930);
	// ITERS=500ʱ��rate=0.001, R=0(0.859); R=0.001(0.861); R=0.01(0.878); 
	// ���� R=0.1(0.889); R=1(0.924); R=10(0.935); R=100(0.949); R=200(0.930);

}

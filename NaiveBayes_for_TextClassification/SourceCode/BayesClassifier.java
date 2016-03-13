import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BayesClassifier
{
	public double hamPrior = 0;							// ������� for ham
	public double spamPrior = 0;
	private Set<String> V_Set;							// �� V �洢�ʻ��
	private HashMap<String, Double> hamCondProbMap;		// �洢ham������ȫ��������ʣ����ȸ�V�ĳ���һ��
	private HashMap<String, Double> spamCondProbMap; 	// �洢spam������ȫ���������
	private String isRemoveStopWords;
	
	public BayesClassifier() {
		this.isRemoveStopWords = "no";
	}
	public BayesClassifier(String isRemoveStopWords) {
		this.isRemoveStopWords = isRemoveStopWords;
	}
	/**
	 * training Multinomial Naive Bayes
	 **/
	public void trainNaiveBayes()
	{
		List<Sample> Samples = readFiles.read(isRemoveStopWords, "./train/ham", "./train/spam");
		ComputePrior(Samples);	//�����������
		
		V_Set = Generate_V_Set(Samples); // Build Vocabulary Table

		ComputeConditionProb(Samples); // Compute Condition Prob for Ham and Spam
	}

	/**
	 * Compute Condition Prob for ham class and spam calss, and store it to map
	 **/
	public void ComputeConditionProb(List<Sample> samples)
	{
		HashMap<String, Integer> hamBigMap = new HashMap<String, Integer>();
		HashMap<String, Integer> spamBigMap = new HashMap<String, Integer>();
		int hamBigMapCount = 0;		//ham��ѵ���������ܴ���
		int spamBigMapCount = 0;	//spam��ѵ���������ܴ���
		
		/** ��Ҫ��samples������ham���spam�������ֱ�ϲ���һ����Map��˳�����ÿ�������ܴ��� */
		for(int i = 0; i <samples.size(); i++) //����Iterator����HashMap
		{ 
			Iterator<String> it = samples.get(i).map.keySet().iterator(); 
			if(samples.get(i).label == readFiles.PositiveLable) { //��������˵����Ham
				while(it.hasNext()) {
					String key = it.next();
					if(hamBigMap.containsKey(key) == false) //if don't contain key, then store it
						hamBigMap.put(key, samples.get(i).map.get(key));
					else //if contain key, then add it to original value
						hamBigMap.put(key, hamBigMap.get(key) + samples.get(i).map.get(key));
					hamBigMapCount += samples.get(i).map.get(key);
				}
			} else { //��������˵����spam
				while(it.hasNext()) {
					String key = it.next();
					if(spamBigMap.containsKey(key) == false)
						spamBigMap.put(key, samples.get(i).map.get(key));
					else
						spamBigMap.put(key, spamBigMap.get(key) + samples.get(i).map.get(key));
					spamBigMapCount += samples.get(i).map.get(key);
				}				
			}
		}
		hamCondProbMap = new HashMap<String, Double>();
		spamCondProbMap = new HashMap<String, Double>();
		
		/************���������ʣ���������Ӧ����***********/
		int tmp = 0;
		double tmpProb;
		Iterator<String> it = V_Set.iterator();	//����V_Set
		while(it.hasNext()) {
			String key = (String)it.next(); // key���Ǵ洢�� V �еĴʻ�

			if(hamBigMap.containsKey(key) == true)
				tmp = hamBigMap.get(key);
			else tmp = 0;
			tmpProb = (double)(tmp + 1) / (hamBigMapCount + V_Set.size()); //Compute Condition Prob
			hamCondProbMap.put(key, tmpProb);

			if(spamBigMap.containsKey(key) == true)
				tmp = spamBigMap.get(key);
			else tmp = 0;
			tmpProb = (double)(tmp + 1) / (spamBigMapCount + V_Set.size()); //Compute Condition Prob
			spamCondProbMap.put(key, tmpProb);
		}
	}

	
	/** 
	 * applying Multinomial Naive Bayes
	 **/
	public void testNaiveBayes()
	{
		List<Sample> Samples = readFiles.read(isRemoveStopWords, "./test/ham", "./test/spam");
		int CorrectCount = 0;
		int ErrorCount = 0;
		for(int i = 0; i <Samples.size(); i++)
		{
			double scoreForHam = Math.log(hamPrior);
			double scoreForSpam = Math.log(spamPrior);
			Iterator<String> it = Samples.get(i).map.keySet().iterator(); //����Iterator����HashMap
			while(it.hasNext()) {
				String key = it.next();
				if(V_Set.contains(key) == true)
				{
					scoreForHam += Math.log(hamCondProbMap.get(key));	// score for Ham
					scoreForSpam += Math.log(spamCondProbMap.get(key));	// score for Spam
				}
			}
			if(Samples.get(i).label==readFiles.PositiveLable && scoreForHam >= scoreForSpam)
				CorrectCount++;
			else if(Samples.get(i).label==readFiles.NegativeLable && scoreForSpam >= scoreForHam)
				CorrectCount++;
			else
				ErrorCount++;
		}
		System.out.println("===============Multinomial Naive Bayes===============");
		System.out.println("CorrectCount = " + CorrectCount);
		System.out.println("ErrorCount = " + ErrorCount);
		System.out.println("Test Accuracy = " + (float)CorrectCount/(CorrectCount + ErrorCount));
	}
	
	/**
	 * @param ����train��Ԥ����������samples
	 * @return ������HashSet���͵�V_Set
	 **/
	public HashSet<String> Generate_V_Set(List<Sample> samples)
	{
		HashSet<String> VSet = new HashSet<String>();
		
		for(int i = 0; i <samples.size(); i++) {
			//����Iterator����HashMap
			Iterator<String> it = samples.get(i).map.keySet().iterator(); 
			while(it.hasNext()) {
				String key = it.next(); //key����һ������
				if(VSet.contains(key) == false)	//if VSet don't contain key, then store it to VSet
					VSet.add(key); 
			}
		}
		return VSet;
	}
	
	/** ����������� */
	public void ComputePrior(List<Sample> Samples) 
	{
		int hamNumCount = 0;
		for(int i = 0; i <Samples.size(); i++) {
			if(Samples.get(i).label == readFiles.PositiveLable)
				hamNumCount++;
		}
		hamPrior = (double)hamNumCount / Samples.size();	//0.73434126
		spamPrior = 1 - hamPrior;	//0.26565874
	}
}

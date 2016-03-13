import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BayesClassifier
{
	public double hamPrior = 0;							// 先验概率 for ham
	public double spamPrior = 0;
	private Set<String> V_Set;							// 用 V 存储词汇表
	private HashMap<String, Double> hamCondProbMap;		// 存储ham样本的全部后验概率，长度跟V的长度一样
	private HashMap<String, Double> spamCondProbMap; 	// 存储spam样本的全部后验概率
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
		ComputePrior(Samples);	//计算先验概率
		
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
		int hamBigMapCount = 0;		//ham类训练样本的总词数
		int spamBigMapCount = 0;	//spam类训练样本的总词数
		
		/** 需要把samples样本中ham类和spam类样本分别合并成一个大Map表，顺便计算每个大表的总词数 */
		for(int i = 0; i <samples.size(); i++) //采用Iterator遍历HashMap
		{ 
			Iterator<String> it = samples.get(i).map.keySet().iterator(); 
			if(samples.get(i).label == readFiles.PositiveLable) { //正样本，说明是Ham
				while(it.hasNext()) {
					String key = it.next();
					if(hamBigMap.containsKey(key) == false) //if don't contain key, then store it
						hamBigMap.put(key, samples.get(i).map.get(key));
					else //if contain key, then add it to original value
						hamBigMap.put(key, hamBigMap.get(key) + samples.get(i).map.get(key));
					hamBigMapCount += samples.get(i).map.get(key);
				}
			} else { //负样本，说明是spam
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
		
		/************计算后验概率，并存入相应表中***********/
		int tmp = 0;
		double tmpProb;
		Iterator<String> it = V_Set.iterator();	//遍历V_Set
		while(it.hasNext()) {
			String key = (String)it.next(); // key就是存储在 V 中的词汇

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
			Iterator<String> it = Samples.get(i).map.keySet().iterator(); //采用Iterator遍历HashMap
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
	 * @param 输入train中预处理后的所有samples
	 * @return 并返回HashSet类型的V_Set
	 **/
	public HashSet<String> Generate_V_Set(List<Sample> samples)
	{
		HashSet<String> VSet = new HashSet<String>();
		
		for(int i = 0; i <samples.size(); i++) {
			//采用Iterator遍历HashMap
			Iterator<String> it = samples.get(i).map.keySet().iterator(); 
			while(it.hasNext()) {
				String key = it.next(); //key就是一个单词
				if(VSet.contains(key) == false)	//if VSet don't contain key, then store it to VSet
					VSet.add(key); 
			}
		}
		return VSet;
	}
	
	/** 计算先验概率 */
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

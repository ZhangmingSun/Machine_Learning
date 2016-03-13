
public class mainFunction
{	
	public static void main(String[] args)
	{

		String isRemoveStopWords;
		
		if(args.length == 1) {
			isRemoveStopWords = args[0];
		}
		else {
			System.out.println("The length of input parameter is not matching, please check it!");
			return;
		}
		
		//Build Bayes Classifier
		BayesClassifier classifier = new BayesClassifier(isRemoveStopWords);
		classifier.trainNaiveBayes();
		classifier.testNaiveBayes();
	}
}

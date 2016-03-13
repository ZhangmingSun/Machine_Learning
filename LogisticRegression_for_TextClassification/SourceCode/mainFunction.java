
public class mainFunction
{	
	public static void main(String[] args)
	{
		/**Four parameters:
		 * (1) isRemoveStopWords: yes or no
		 * (2) LearningRate: default is 0.1
		 * (3) Iterations: default is 100
		 * (4) RegularParam: default is 0.001
		**/
		String isRemoveStopWords;
	    double LearningRate; 		/** the learning rate */
	    int Iterations;				/** the number of iterations */
	    double RegularParam;		/** Regular Parameter */
		
		if(args.length == 4) {
			isRemoveStopWords = args[0];
			LearningRate = Double.parseDouble(args[1]);
			Iterations = Integer.parseInt(args[2]);
			RegularParam = Double.parseDouble(args[3]);
		}
		else {
			System.out.println("The length of input parameter is not matching, please check it!");
			return;
		}
		
//	    isRemoveStopWords = "NO";	// isRemoveStopWords is not case sensitive
//	    LearningRate = 0.1; 		/** the learning rate */
//	    Iterations = 100;			/** the number of iterations */
//	    RegularParam = 0.001;		/** Regular Parameter */
		
		
		//Build Logistic Regression Classifier
		LogisticRegression LR = new LogisticRegression(isRemoveStopWords, LearningRate, Iterations, RegularParam);
		LR.trainLogisticRegression();
		LR.testLogisticRegression();
	}
}

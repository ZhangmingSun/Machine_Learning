
public class mainFunction
{	
	public static void main(String[] args)
	{
		/**Four parameters:
		 * (1) isRemoveStopWords: yes or no
		 * (2) LearningRate: default is 0.1
		 * (3) Iterations: default is 100
		**/
		String isRemoveStopWords;
	    double LearningRate; 		/** the learning rate */
	    int Iterations;				/** the number of iterations */
	    //double RegularParam;		/** Regular Parameter */
		/*
		if(args.length == 3) {
			isRemoveStopWords = args[0];
			LearningRate = Double.parseDouble(args[1]);
			Iterations = Integer.parseInt(args[2]);
			//RegularParam = Double.parseDouble(args[3]);
		}
		else {
			System.out.println("The length of input parameter is not matching, please check it!");
			return;
		}*/
		
	    isRemoveStopWords = "Yes";	// isRemoveStopWords is not case sensitive
	    LearningRate = 0.1; 		/** the learning rate */
	    Iterations = 100;			/** the number of iterations */
	    //RegularParam = 0.001;		/** Regular Parameter */
		
		//Build Classifier from Perceptron Learning Algorithm
		Perceptron_Learning_Algorithm Perceptron = new Perceptron_Learning_Algorithm(isRemoveStopWords, LearningRate, Iterations);
		Perceptron.train_Perceptron_Learning_Algorithm();
		Perceptron.test_Perceptron_Learning_Algorithm();

		//String str = Stopwords.readStopwords("./stopword.txt");
		//System.out.println(str);
		//String str = Stopwords.removeStopWords("I am a student in University.");
		//System.out.println(str);
	}
}

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class EM_GMM {
	
	public double[] InputDataSet;
	public int DataLength; // The length of InputDataSet
	
	public int K; // The number k of Cluster (or k component mixture)
	public double[] Fi, Mu, Sigma;
	
	public double Threshold = 0.001; // {0.1, 0.01, 0.001,0.0001}
	
	// Constructor
	public EM_GMM(int K, double[] InputDataSet) {
		this.K = K;
		Fi  = new double[this.K];
		Mu = new double[this.K];
		Sigma = new double[this.K];
		
		this.InputDataSet = InputDataSet;
		DataLength = InputDataSet.length;
		//System.out.println("DataLength: "+DataLength);
    }
	
	/** Gaussian Function, which to form the GMM */
	public double GaussianFunction(double dataPoint, double mu , double sigma){
		double Prob = 0;
		// double java.lang.Math.PI = 3.141592653589793
		Prob = Math.pow(2*Math.PI*sigma, -0.5) * Math.exp((-Math.pow(dataPoint-mu,2)) / (2*sigma));
	    return Prob;
	}
	
	// log2(x) = log(x) / log(2), and x could not be zero
	public static double log2(double x) {
		 return x == 0 ? 0 : (Math.log(x)/Math.log(2));
	}
	
	public void GaussianMixtureModel() throws Exception
	{
		InitializeParameters(); // Initialize Fi, Mu, Sigma
		
		int IterationNum = 0;
		double Diff_Likelihood = 1; //前后两次的似然率差异
		double Pre_Likelihood = 0;
		double Now_Likelihood = 0;
		
		// For every input sample(or data point), we distribute K unit space(k component mixture) 
        double[][] w = new double[DataLength][this.K];
        
		while(Diff_Likelihood > Threshold)
		{
				Pre_Likelihood = Now_Likelihood;
				Now_Likelihood = 0; // This is an important step!
				double SumOfXnProb = 0;
				
				/** ############################## E-Step ##############################  
				 * The main purpose is to compute w[i][j]
				 * The second purpose is to Likelihood
				 * Likelihood is SUM(log( p(x; Fi,Mu,Sigma) ))
				 * Fi is the parameter of Multinomial distribution
				 * Mu and Sigma is the parameter of Gaussian distribution
				 * E-Step由两个循环构成，
				 * 1. 外循环 是 对每一个输入样本“i”进行循环"相加"；
				 * 2. 内循环 是 对某个样本属于每一类“j”的概率进行循环"相加"。
				 **/
				for(int i=0; i< DataLength; i++){
					SumOfXnProb = 0;
					// 下面两个循环是计算p(x; Fi,Mu,log)贝叶斯展开的分子和分母部分
					for(int j=0; j<this.K; j++){ // The number k of Cluster
						w[i][j] = Fi[j] * GaussianFunction(InputDataSet[i], Mu[j], Sigma[j]);
						SumOfXnProb += w[i][j];
					}
					for(int j=0; j<this.K; j++){
						w[i][j] = w[i][j] / SumOfXnProb;
					}
					// 附加值是顺便累加所有样本i，最后求出整个集合的似然率
					Now_Likelihood += log2(SumOfXnProb); // Note: we need the log of SumOfXnProb
				}
				
				/** ############################## M-Step ############################## 
				 * The purpose is to compute the parameter Fi, Mu, Sigma
				 * */
				for(int j=0; j<this.K; j++)
				{
					// 这个SumForClusterj，在计算Fi, Mu, Sigma时，都需要用到
					double SumForClusterj = 0;
					
					//============First, computer the parameter Fi, Mu ============
					double Mu_tmp = 0;
					for(int i=0; i< DataLength; i++){
						SumForClusterj += w[i][j];
						Mu_tmp += w[i][j] * InputDataSet[i];
					}
					Fi[j] = SumForClusterj / DataLength;
					Mu[j] = Mu_tmp / SumForClusterj;
					
					//============Second, after know Mu, we computer the parameter Sigma ============
					double Sigma_tmp = 0;
					for(int i=0; i< DataLength; i++){
						Sigma_tmp += w[i][j] * (InputDataSet[i]-Mu[j])*(InputDataSet[i]-Mu[j]);
					}
					Sigma[j] = Sigma_tmp / SumForClusterj;
					//Sigma[j] = 1;
				}
				
				/** #################### Judge if Likelihood is converged! #################### */
				Diff_Likelihood = Math.abs(Now_Likelihood - Pre_Likelihood);
				
				System.out.println((IterationNum++)+": Diff="+Diff_Likelihood+"; Likelihood="+Now_Likelihood);
				
		}// End of While
		
		/** #################### Output to display the result #################### */
		System.out.println("Num of EM Iteration: " + IterationNum++);
		System.out.println("Whole LogLikelihood: " + Now_Likelihood);
		for(int j=0; j<this.K; j++)
			System.out.println("Fi["+j+"]:"+Fi[j]+" Mu["+j+"]:"+Mu[j]+" Sigma["+j+"]:"+Sigma[j]);
		
		//先把0~9.txt先删除
		for(int j=0; j< 10; j++){
			File dirFile = new File(j+".txt");
			if(dirFile.exists()){
				dirFile.delete();
			}
		}
		//先声明缓存数组
		File[] f = new File[this.K];
		FileOutputStream[] fop = new FileOutputStream[this.K];
		OutputStreamWriter[] writer = new OutputStreamWriter[this.K];
		//new对象
		for(int j=0; j< this.K; j++){
			f[j] = new File(j+".txt");
			fop[j] = new FileOutputStream(f[j]);
			writer[j] = new OutputStreamWriter(fop[j], "UTF-8");
		}
		//把参数Fi，Mu，Sigma写入所在类的文件
		for(int j=0; j<this.K; j++)
			writer[j].append("Fi["+j+"]:"+Fi[j]+" Mu["+j+"]:"+Mu[j]+" Sigma["+j+"]:"+Sigma[j]+"\r\n");
		//判断每个数据点属于哪个cluster
		for(int i=0; i< DataLength; i++){ //for(int i=0; i< 100; i++){
			int cluster = 0; // init is w[i][0]
			for(int j=1; j< this.K; j++){
				if(w[i][j] > w[i][cluster])//w[i][cluster]表示目前最大的值
					cluster = j;
			}
			//String str = InputDataSet[i]+": class="+cluster+"; Prob="+w[i][cluster];
			String str = InputDataSet[i]+"; Prob="+w[i][cluster];
			writer[cluster].append(str+"\r\n");
			//System.out.println(InputDataSet[i]+": class="+cluster+"; Prob="+w[i][cluster]);
		}
		//关闭文件
		for(int j=0; j< this.K; j++){ 
			writer[j].close();
			fop[j].close();
		}
		
	}
	
	/** 
	 * EM算法赋初值策略：
	 * 1. Fi[j]宜均匀分布，值为1，because constraint: the SUM of Fi[j] is "1"
	 * 2. Mu[j]的影响最大，对“Mu[j]”赋初始值的时候，最好在Max和Min之间均匀分布的赋值
	 * 3. Sigma[j]的影响不太大，但是方差初始值均匀时，对结果也有好处！
	 * Note: 如果值负的太过分的话，会出现NaN
	 * 
	 * 发现初始均值Mu的影响最大，假如初始均值Mu以1递增进行初始化的话，结果也很细腻；以2的话，结果也较粗糙；
	 * 聚类是3类时，均值为2,4,6时， 收敛比较慢，分类好像也不是很完美；但是2,7,12时，收敛较快
		当K等于10时，
	    important_value = 1时, 可以看到Mu[7]:6到Mu[8]:15变化太剧烈，跟初始值不均匀有关吧
	  	Fi[0]:0.006565622603092058 Mu[0]:3.3427971424786342 Sigma[0]:0.03543510624948254
		Fi[1]:0.005317682029664407 Mu[1]:3.6302194589634 Sigma[1]:0.4633553315180609
		Fi[2]:0.008425209252804787 Mu[2]:4.444825834122196 Sigma[2]:0.004226159562467701
		Fi[3]:0.05402332349050792 Mu[3]:4.469380461473186 Sigma[3]:0.24296355653121962
		Fi[4]:0.07929288427467082 Mu[4]:5.324217567978784 Sigma[4]:0.62860362618546
		Fi[5]:0.09640903219775625 Mu[5]:5.684944014123599 Sigma[5]:0.24990478106161448
		Fi[6]:0.06283365259963028 Mu[6]:6.495547081853893 Sigma[6]:0.5737140842512655
		Fi[7]:0.02046592688222607 Mu[7]:6.737233385247319 Sigma[7]:0.11463881986905182
		Fi[8]:0.333333333339503 Mu[8]:15.449160787731213 Sigma[8]:0.9671159493274124
		Fi[9]:0.33333333333014475 Mu[9]:25.486654429329228 Sigma[9]:0.9980966181791336
	  	
	 * 可以“important_value = 2”时效果最好，分布最均匀，是根据“Mu[j]”来判断的，
	 * 所以对“Mu[j]”赋初始值的时候，最好在Max和Min之间均匀分布的赋值
	 	Num of EM Iteration: 897
		Whole LogLikelihood: -21752.916859836096
		Fi[0]:0.032222312658242186 Mu[0]:3.8105950183297037 Sigma[0]:0.26474484543436144
		Fi[1]:0.02805787462736628 Mu[1]:4.4406800532982285 Sigma[1]:0.043644545230584246
		Fi[2]:0.2183252393427801 Mu[2]:5.56864094711552 Sigma[2]:0.44589840671790704
		Fi[3]:0.05185087013598539 Mu[3]:6.755124143517645 Sigma[3]:0.2310678796239487
		Fi[4]:0.0028770365689554747 Mu[4]:7.9979183280237605 Sigma[4]:0.08591553213565953
		Fi[5]:5.896868898206178E-4 Mu[5]:12.046953754334583 Sigma[5]:0.008963517537251825
		Fi[6]:0.1035456448085098 Mu[6]:14.611481054284948 Sigma[6]:0.5791063962369868
		Fi[7]:0.1811645790708128 Mu[7]:15.609936379811788 Sigma[7]:0.4634381515117392
		Fi[8]:0.04803342255981164 Mu[8]:16.69032744551873 Sigma[8]:0.42224110653656083
		Fi[9]:0.33333333333771537 Mu[9]:25.486654429230704 Sigma[9]:0.9980966186448885
		
		important_value = 3时, 可以看到Mu[j]:6到Mu[j]:14变化较大
		Fi[j]:0.07624972328449206 Mu[j]:4.4260341039648825 Sigma[j]:0.5016044194557844
		Fi[j]:0.24516529942086857 Mu[j]:5.787590307295683 Sigma[j]:0.7096160106456622
		Fi[j]:0.011918310587056618 Mu[j]:6.714565531633158 Sigma[j]:0.4549427702835652
		Fi[j]:0.015660354423175617 Mu[j]:14.096371604515284 Sigma[j]:0.7965554124655488
		Fi[j]:0.2636718674852586 Mu[j]:15.33652504647071 Sigma[j]:0.7551835390194771
		Fi[j]:0.05400111146581871 Mu[j]:16.39143831954973 Sigma[j]:0.570838267369145
		Fi[j]:0.006964649159420301 Mu[j]:23.31831614813844 Sigma[j]:0.28562814986857454
		Fi[j]:0.16414539306967552 Mu[j]:25.05092119944513 Sigma[j]:0.6108163911170545
		Fi[j]:0.16063297871157775 Mu[j]:25.997186181432347 Sigma[j]:0.6919543246252842
		Fi[j]:0.0015903123926552967 Mu[j]:28.389908929328243 Sigma[j]:0.07104433127526041
		
		important_value = 4或5时, 出现NaN
	 */
	public void InitializeParameters()
	{
		// because constraint: the SUM of Fi[j] is "1"
		double init_Fi = 1 / (double)this.K;
		for(int j=0; j<this.K; j++)
			Fi[j] = init_Fi;
		
		// important_value = 2时效果最好，分布最均匀，是根据“Mu[j]”来判断的
		int important_value = 2;
		for(int j=0; j<this.K; j++)
			Mu[j] = important_value*(j*1+1);
		
		for(int j=0; j<this.K; j++) //Sigma全部设为1
			Sigma[j] = 1;
		/*
		for(int j=0; j<this.K; j++)
			Sigma[j] = 2*(j+1); //以2依次递增
		*/
	}
	
}

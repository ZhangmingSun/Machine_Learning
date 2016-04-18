
public class mainFunction {
	
	public static void main(String[] args) throws Exception {

		String fileName = "./em_data.txt";
		double[] InputDataSet = readFile(fileName);
		
		/** K = 10，还有初始化是均值Mu的赋值，和迭代截止条件Threshold，对结果也有影响 */
		EM_GMM Gmm = new EM_GMM(10, InputDataSet);
		Gmm.GaussianMixtureModel();
		
	}
	
	public static double[] readFile(String fileName)
	{   
		int dataLen = ComputeDataLength(fileName);
	    double[] dataBuf = new double[dataLen];
	    
	    TextFileInput textRead = new TextFileInput(fileName);
		String line = textRead.readLine();
	    int cnt=0;
		while(line!=null)
		{
			dataBuf[cnt] =  Double.parseDouble(line);
			cnt++; 
			line=textRead.readLine();
		}
		return dataBuf;
  	}
	
	/** Compute Data Length in input file */
	public static int ComputeDataLength(String fileName)
    {
		int cnt=0;
		TextFileInput tfi = new TextFileInput(fileName);
		String line = tfi.readLine();
		while(line!=null)
		{	line=tfi.readLine();
			cnt++;
		}
		return cnt;
	}
}

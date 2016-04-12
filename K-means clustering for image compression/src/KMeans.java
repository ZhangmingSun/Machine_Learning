
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
 
public class KMeans {

	private final static int ITERATIONS = 20; // ITERATIONS times of K-means
	
	//e.g.	 ./Koala.jpg 10 ./output/Koala.jpg
	public static void main(String [] args){
		
		if (args.length != 3){
			System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
			return;
		}
		String inputImageFilePath = args[0];
		String Parameter_K = args[1];
		String outputImageFilePath = args[2];

		long timeStart = System.currentTimeMillis();
		try{
			System.out.println("The K-means Algorithm is running ......");
			BufferedImage originalImage = ImageIO.read(new File(inputImageFilePath));
			int k=Integer.parseInt(Parameter_K);
			BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
			ImageIO.write(kmeansJpg, "jpg", new File(outputImageFilePath));
			
			long timeEnd = System.currentTimeMillis();
			System.out.println("The task is finished!");
			System.out.println("The Whole time is " + (timeEnd - timeStart) + "ms!");
			
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
    }
    
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k)
	{
		int w=originalImage.getWidth();
		int h=originalImage.getHeight();
		BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
		Graphics2D g = kmeansImage.createGraphics();
		//相当于：把originalImage的矩阵值，通过中介g--Graphics2D，赋值给了kmeansImage
		g.drawImage(originalImage, 0, 0, w, h, null);
		//System.out.println("w: "+ w); // test to observe the value
		//System.out.println("h: "+ h);
		//System.out.println("getType: "+originalImage.getType());
		
		// Read rgb values from the image
		int[] rgb = new int[w*h];
		int count=0;
		for(int i=0; i<w; i++){
			for(int j=0; j<h; j++){
				rgb[count] = kmeansImage.getRGB(i,j);
				//System.out.print(rgb[count] + " "); // test to observe the value
				count++;
			}
			//System.out.println("");
		}
		
		// Call kmeans algorithm: update the rgb values
		kmeans(rgb,k);

		// Write the new rgb values back to the image
		count=0;
		for(int i=0; i<w; i++){
			for(int j=0; j<h; j++){
				kmeansImage.setRGB(i, j, rgb[count++]); //把更新后的RGB重新写回kmeansImage
			}
		}
		return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k)
    {
    	int len = rgb.length;
    	int[] r = new int[len];
    	int[] g = new int[len];
    	int[] b = new int[len];
    	
    	/** Initialize R,G,B Channel Array */
    	//Alpha通道：Alpha_rgb(255,0,0,0), Alpha会影响透明的效果，Alpha--255不透明，Alpha--0透明
		for(int i=0; i<len; i++) {
			r[i] = (rgb[i]>>16) & 0xff;	//(R)：rgb(255,0,0)
			g[i] = (rgb[i]>>8) & 0xff;	//(G)：rgb(0,255,0)
			b[i] = (rgb[i]) & 0xff;		//(B)：rgb(0,0,255)
		}
		
		/** Initialize K Cluster Centroids */
		int[] init = Initialize_Cluster_Centroids(k, len);
		int[][] K_Centroids = new int[k][3]; 	// 3--means R, G, B channel
		for(int j=0; j<k; j++) {
			K_Centroids[j][0] = r[init[j]];		// 0坐标代表R颜色通道
			K_Centroids[j][1] = g[init[j]];
			K_Centroids[j][2] = b[init[j]];
		}

		//for(int i=0; i<K_Centroids.length; i++) // Display Test
			//System.out.println("Index" + i + " is " + K_Centroids[i]);
		
		/** ######################### Repeat until convergence ######################### */
		int[] dis= new int[len]; 	//为每一个像素点分配一个缓存空间，用于存储到某个聚类中心的距离
		int[] ndx = new int[len];	//每一个像素点属于哪一类聚类中心，相当于索引index--ndx
		int tmpDis; //临时缓存距离

		for(int m=0; m<ITERATIONS; m++)
		{
			/** 1st Step: “Assigning” each training example x(i) to the closest cluster centroid µj. */
			/**为每个点像素点计算到第0个cluster centroid的距离，初始化第0个聚类*/
			for(int j=0; j<len; j++) {
				//dis[j]=(double)Math.sqrt( Math.pow(K_Centroids[0][0]-r[j], 2) + 
						//Math.pow(K_Centroids[0][1]-g[j], 2) + Math.pow(K_Centroids[0][2]-b[j],2) );
				// 直接取绝对值，简单快速有效
				dis[j]=(int)Math.abs(K_Centroids[0][0]-r[j]) + Math.abs(K_Centroids[0][1]-g[j]) + Math.abs(K_Centroids[0][2]-b[j]);
				// 要计算平方，很慢！！！
				//dis[j]=(double)Math.pow(K_Centroids[0][0]-r[j], 2) + Math.pow(K_Centroids[0][1]-g[j], 2) + Math.pow(K_Centroids[0][2]-b[j],2);
				ndx[j] = 0;
			}
			/** 注意循环是从1开始的，为每个像素点分配“距离最近”的聚类的坐标 */
			for(int i=1; i<k; i++) {
				for(int j=0; j<len; j++) {
					//tmpDis = (double)Math.sqrt( Math.pow(K_Centroids[i][0]-r[j], 2) + 
							//Math.pow(K_Centroids[i][0]-g[j], 2) + Math.pow(K_Centroids[i][0]-b[j],2) );
					// 直接取绝对值，简单快速有效
					tmpDis = (int)Math.abs(K_Centroids[i][0]-r[j]) + Math.abs(K_Centroids[i][1]-g[j]) + Math.abs(K_Centroids[i][2]-b[j]);
					// 要计算平方，很慢！！！
					//tmpDis = (double)Math.pow(K_Centroids[i][0]-r[j], 2) + Math.pow(K_Centroids[i][0]-g[j], 2) + Math.pow(K_Centroids[i][0]-b[j],2);
					if(tmpDis < dis[j]) {
						dis[j] = tmpDis;	//说明这个j点到第i类聚类中心更近，需要update
						ndx[j] = i;			//同时，update第i类聚类中心的索引
					}
				}
			}
			
			/** 2nd Step: Moving each cluster centroid µj to the mean of the points assigned to it. */
			int[][] ValueSUM = new int[k][3]; //
			int[] NumSUM = new int[k]; //
	
			for(int j=0; j<len; j++) {
				ValueSUM[ndx[j]][0] += r[j];	//0坐标代表R颜色通道
				ValueSUM[ndx[j]][1] += g[j];	//1坐标代表G颜色通道
				ValueSUM[ndx[j]][2] += b[j];	//2坐标代表B颜色通道
				NumSUM[ndx[j]] += 1;
			}
			for(int i=1; i<k; i++) {
				K_Centroids[i][0] = (int) Math.rint( (float) ValueSUM[i][0] / NumSUM[i]);
				K_Centroids[i][1] = (int) Math.rint( (float) ValueSUM[i][1] / NumSUM[i]);
				K_Centroids[i][2] = (int) Math.rint( (float) ValueSUM[i][2] / NumSUM[i]);
			}
		}
		
		/** ############ 迭代完成后，更新每一个R,G,B数组 ############ */
		for(int j=0; j<len; j++) {
			r[j] = K_Centroids[ndx[j]][0];
			g[j] = K_Centroids[ndx[j]][1];
			b[j] = K_Centroids[ndx[j]][2];
		}
		for(int i=0; i<len; i++) {
			//rgb[i] = ((0xff)<<24)|((r[i]&0x0ff)<<16)|((g[i]&0x0ff)<<8)|(b[i]&0x0ff); //add Alpha to test
			rgb[i] = ((r[i]&0x0ff)<<16)|((g[i]&0x0ff)<<8)|(b[i]&0x0ff);
			//System.out.println("Pixel number="+i+"    RGB value="+rgb[i]);
		}
    }

    /** 
     * Function: Initialize Cluster Centroids
     * @param k: Parameter K of the K-means Algorithm
     * @param length: the total length of pixel numbers in the image
     */
    public static int[] Initialize_Cluster_Centroids(int k, int length)
    {
    	// Select K Cluster Centroids
		int[] K_Centroids = new int[k];
		for(int i=0; i<k; i++)
		{
			Random rand = new Random();
			int min = 0;
			int max = length;
			K_Centroids[i] = rand.nextInt(max - min + 1) + min;
		}
    	return K_Centroids;
    }
    
	 /**
	 红色(R)：rgb(255,0,0)
	 绿色(G)：rgb(0,255,0)
	 蓝色(B)：rgb(0,0,255)
	 白色：rgb(255,255,255)
	 黑色：rgb(0,0,0)
	 青色：rgb(0,255,255)
	 紫色：rgb(255,0,255)
	 **/
    public void RGB_ValueTest()
    {
    	/**=========================RGB value output Test=========================
		// Integer.toBinaryString(-16755216), which is 11111111000000000101010111110000.
		// it made up of 4 bytes: alpha, red, green, blue.
    	// 0xffffff = 16777215; 0x1000000 = 16777216;
    	int rgbValue = -13623277; //-16777216; -7112354; 0xff0088;

    	//(Alpha)：Alpha_rgb(255,0,0,0), Alpha会影响透明的效果，Alpha--255不透明，Alpha--0透明
    	System.out.println((rgbValue>>24) & 0xff);
    	System.out.println((rgbValue>>16) & 0xff); 	//(R)：rgb(255,0,0)
    	System.out.println((rgbValue>>8) & 0xff);	//(G)：rgb(0,255,0)
    	System.out.println((rgbValue) & 0xff);		//(B)：rgb(0,0,255)
		*/
    }
}
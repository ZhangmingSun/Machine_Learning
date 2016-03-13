import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class main {

	//public static ArrayList<String> AttributeRow = new ArrayList<String>(); //保存测试数据的首行
	public static ArrayList<ArrayList<String>> TestExamples = new ArrayList<ArrayList<String>>(); //测试样本集
	
	public static void main(String[] args)
	{
		int L,K;
		String training_set = null;
		String validation_set = null;
		String test_set = null;
		String to_print = null;
		if(args.length == 6) {
			L = Integer.parseInt(args[0]);		//把String变成Int
			K = Integer.parseInt(args[1]);
			training_set = args[2];
			validation_set = args[3];
			test_set = args[4];
			to_print = args[5];
		}
		else {	
			System.out.println("The number of input parameter is not matching, please check up it!!!");
			return;
		}/**/
		
		/*L = 1500;		//把String变成Int
		K = 20;
		training_set = "./data_sets2/training_set.csv";
		validation_set = "./data_sets2/validation_set.csv";
		test_set = "./data_sets2/test_set.csv";
		to_print = "yes";*/
		
		readDataFromFile(training_set, 0); 	// number = 0, 表示training set数据
		readDataFromFile(test_set, 1); 		// number = 1, 表示读取test set数据
		readDataFromFile(validation_set,2); // number = 2, 表示读取validation set数据
			
		//============== Bulid input Attributes List==============
		ArrayList<String> remainAttributes = new ArrayList<String>();
		for(int i = 0; i < ID3.AttributeRow.size()-1; i++){ //注意要“size()-1”，要排除targetAttribute
			remainAttributes.add(ID3.AttributeRow.get(i));
			//System.out.println(ID3.AttributeRow.get(i));
		}
		//============== Bulid Decision Tree by DFS==============
	    Node root1, root2;
	    ID3 ID3Algorithm = new ID3();
	    root1 = ID3Algorithm.BulidDecisionTreeDFS(ID3.examples, remainAttributes, true); //true: Info gain heuristic
	    root2 = ID3Algorithm.BulidDecisionTreeDFS(ID3.examples, remainAttributes, false);//false: Variance impurity heuristic

	    //============== Test Decision Tree with TestSet==============
	    //readDataFromFile(test_set, 1); 		// number = 1, 表示读取test set数据
	    Double accuracy1 = AccuracyOnExamples(root1, TestExamples);
	    Double accuracy2 = AccuracyOnExamples(root2, TestExamples);

	    //==============Build Post Prune Decision Tree==============
	    //readDataFromFile(validation_set,2); // number = 2, 表示读取validation set数据
	    PostPruning Pruning = new PostPruning();
	    Node PrunedTree1 = Pruning.PostPruningAlgorithm(L, K, root1);
	    Node PrunedTree2 = Pruning.PostPruningAlgorithm(L, K, root2);
	    
	    //============== Test Pruned Decision Tree with TestSet==============
	    Double accuracy3 = AccuracyOnExamples(PrunedTree1, TestExamples);
	    Double accuracy4 = AccuracyOnExamples(PrunedTree2, TestExamples);
	    
	    //============== Print Decision Tree by DFS==============
	    if(to_print.equals("yes"))
	    {
		    System.out.println("############ The decision tree1 with Info gain heuristic ############");
		    PrintTree(root1,0);
		    System.out.println("############ The decision tree2 with Variance impurity heuristic ############");
		    PrintTree(root2,0);
		    
		    System.out.println("############ Post Pruned decision tree1 with Info gain ############");
		    PrintTree(PrunedTree1,0);
		    System.out.println("############ Post Pruned decision tree2 with Variance impurity ############");
		    PrintTree(PrunedTree2,0);    	
	    }
	    else if(to_print.equals("no")){}
	    else
	    	System.out.println("The parameter of print is wrong!");
	    
	    System.out.println("The following are four trees's Testing Accuracy: ");
	    System.out.println("1. ID3 Accuracy (Info gain) = " + accuracy1); //testSet1: ; testSet2: 
	    System.out.println("2. ID3 Accuracy (Variance impurity) = " + accuracy2); //testSet1: ; testSet2: 
	    System.out.println("3. PostPruned(Info gain) with L= "+L+", K= "+K+" ==>  Accuracy = " + accuracy3 );
	    System.out.println("4. PostPruned(Variance impurity) with L= "+L+", K= "+K+" ==>  Accuracy = " + accuracy4 );
	    
	    /*============== increased precision in accuracy ==============*/
	    //System.out.println("PostPruned(Info gain) increased precision = " + (accuracy3-accuracy1) );
	    //System.out.println("PostPruned(Variance impurity) increased precision = " + (accuracy4-accuracy2) );
	}
	
	// number = 0, 表示training set数据
	// number = 1, 表示读取test set数据
	// number = 2, 表示读取validation set数据
	public static void readDataFromFile(String fileAddress, int number)
	{
		BufferedReader br = null;
		List<String> list = new ArrayList<String>();
		try{
			br = new BufferedReader(new FileReader(fileAddress)); //输入文件地址
			String stemp;
			while ((stemp = br.readLine()) != null)
				list.add(stemp);
			br.close();
			
			ID3.AttributeRow.clear(); 	//注意一定要清空，不然add()会重复加载而导致出错
			for (int i = 0; i < list.size(); i++) {
				String[] str1 = list.get(i).split(",");
				ArrayList<String> line= new ArrayList<String>(); //注意这行，每次for循环必须开辟一行内存
				for (int j = 0; j < str1.length; j++) {
					if(i==0) ID3.AttributeRow.add(str1[j]);  //保存第一行(即属性行)到AttributeRow列表
					line.add(str1[j]);
				}
				if(number == 0) ID3.examples.add(line);
				else if(number == 1) TestExamples.add(line);	//向测试样本添加数据
				else if(number == 2) PostPruning.ValidationExamples.add(line);	
				else return;
			}
			if(number == 0) ;
			else if(number == 1) TestExamples.remove(0); //注意：我这边删除第一行(属性行)
			else if(number == 2) PostPruning.ValidationExamples.remove(0);
			else return;
			
			//==============Display all examples==============
			/*for (int i = 0; i < TestExamples.size(); i++) {
				for (int j = 0; j < TestExamples.get(i).size(); j++) {
					System.out.print(TestExamples.get(i).get(j));
					if(i==0) System.out.print("  ");
					else System.out.print("   ");					
				}
				System.out.println("");
			}*/
	    }catch (Exception e){
	    	System.out.println(e.getMessage());
	    }		
	}
	
	public static void PrintTree(Node p, int depth)
	{
		if(p.childs.size() == 0) return; //递归截止条件，说明是叶节点；<==== 其实不写也没关系
		
	    for (int i=0; i<p.childs.size(); i++)
	    {
	    	for (int j = 0; j < depth; j++)	//按照树的深度先输出"| "
		    	System.out.print("| ");
	    	
	    	if(p.childs.get(i).attribute == ID3.yes || p.childs.get(i).attribute == ID3.no) //说明是叶节点
	    		System.out.println(p.attribute + " = " + p.childs.get(i).arrived_value + " : " + p.childs.get(i).attribute);
	    	else
	    		System.out.println(p.attribute + " = " + p.childs.get(i).arrived_value + " : ");
	    	
	        PrintTree(p.childs.get(i), depth + 1); //打印下一颗子树
	    }
	    return;
	} 
	
	public static String TestOneExample(Node root, ArrayList<String> oneExample) 
	{
		//System.out.println("AttriValue: " + root.attribute);
		if(root.childs.size() == 0) //递归截止条件1，说明到了叶节点，但是这个条件可能用不上
			return root.attribute;	//应该返回attribute，而不是arrived_value，因为arrived_value是父节点的属性值
		
		int rootAttriIndex = ID3.AttributeRow.indexOf(root.attribute);
		String rootAttriValue = oneExample.get(rootAttriIndex); //获得样本中根节点坐标所在的属性的属性值
		int childIndex = -1;
		for(int i=0; i<root.childs.size(); i++)
			//如果“父节点属性值” 跟 “子节点提前保存的值”相等
			if(rootAttriValue.equals(root.childs.get(i).arrived_value))
				{ childIndex = i; break; }
		if(childIndex == -1) {System.out.println("###Cannot find matching child node!!!###"); return "";}
		
		String value = TestOneExample(root.childs.get(childIndex), oneExample);
		return value;
		
		/*=================下面是递归截止判断的第二种方案：通过判断叶节点=================
		String childNodeAttribute = root.childs.get(childIndex).attribute;
		if(childNodeAttribute == ID3.yes || childNodeAttribute == ID3.no){
			System.out.println("AttriValue: " + childNodeAttribute);
			return childNodeAttribute;
		}else{
			String value = TestOneExample(root.childs.get(childIndex), oneExample);
			return value;
		}*/
	}
	
	public static double AccuracyOnExamples(Node root, ArrayList<ArrayList<String>> inputExamples) {
		//=================一个样本测试=================
		/*String result = TestOneExample(root, inputExamples.get(0));
		System.out.println("First example result: " + result); return 0;*/
		//=================所有样本测试=================
		String result = "";
		int count = 0;
		for(int i=0; i<inputExamples.size(); i++) {
			result = TestOneExample(root, inputExamples.get(i));
			if(result.equals(inputExamples.get(i).get(ID3.MAXLEN-1)))
				count++;
		}
		//System.out.println("count: " + count);
		double accuracy = (double)count / (double)inputExamples.size();
		return accuracy;
	}
	
}

import java.util.ArrayList;
import java.util.Random;

public class PostPruning
{
	public static ArrayList<ArrayList<String>> ValidationExamples = new ArrayList<ArrayList<String>>(); //验证样本集
	public static int nonleafNum = 0;	//决策树非叶节点标记，MarkNonLeafNode()函数需要使用
	
	public static int YesNodeCount = 0; //SearchAndReplaceNode()函数需要使用
	public static int NoNodeCount = 0;
	
	// Output a post-pruned Decision Tree
	public Node PostPruningAlgorithm(int L, int K, Node root)
	{
		Node DBest = (Node)root.clone();
		Double AccuracyBest = main.AccuracyOnExamples(DBest, ValidationExamples);
		Double Accuracy;
		//必须提前申明，for循环中可以有“Node DTree=new DTree();”，但 是不允许 “Node DTree = (Node)root.clone();”
		Node DTree = null; 
		for(int i = 1; i < L; i++) //注意我是从1开始的
		{
			DTree = (Node)root.clone();
			
			//生成一个min和max之间的随机整数
			int min=1; int max=K;
			Random random = new Random();
			int M = random.nextInt(max)%(max-min+1) + min;	
			
			for(int j = 0; j < M; j++)
			{
				if(DTree.childs.size()==0) 	//很重要，万一DTree为空，会发生错误！！！
					break;

			    nonleafNum = 0;				//必须每次提前清空全局变量
			    MarkNonLeafNode(DTree); 	//对每一个非叶节点进行标记
				int N = nonleafNum;			//返回非叶节点数量
				int P = random.nextInt(N) + 1; // random number in range 1 ~ N
				
				SearchAndReplaceNode(DTree, P);
			}
			
			Accuracy = main.AccuracyOnExamples(DTree, ValidationExamples); //测试样本集
			if(Accuracy > AccuracyBest){
				AccuracyBest = Accuracy;
				DBest = (Node)DTree.clone();
			}
		}
		return DBest;
	}
	
	//===================对非叶节点进行标记===================
	public void MarkNonLeafNode(Node root)
	{
		if(root.childs.size() == 0) //递归截止条件1，说明到了叶节点，但是这个条件可能用不上
			return;
		int flag=0; //哨兵值，这个哨兵值非常重要，因为for循环会导致root.MarkNumber可能被赋值两次而出错

		for(int i=0; i<root.childs.size(); i++)
		{
			String childNodeAttribute = root.childs.get(i).attribute;
			if(childNodeAttribute == ID3.yes || childNodeAttribute == ID3.no) { //说明它的子节点是叶节点
				if(flag==0) root.MarkNumber = ++nonleafNum;
				flag=1;
			}else{
				if(flag==0) root.MarkNumber = ++nonleafNum;
				flag=1;
				MarkNonLeafNode(root.childs.get(i));
			}
		}
		// 观察每个非叶结点的标记号
		//System.out.println("### " + root.attribute + ": " + root.MarkNumber);
	}
	
	//===================Search And Replace Node===================
	//第1步：全树搜索标号markNumber的节点
	//第2步：找到后，统计其叶节点中数量最多的Class
	//第3步：清场工作，如childs数组清为null
	public int SearchAndReplaceNode(Node root, int markNumber) //采用多叉树的先序遍历
	{
		if(root.childs.size() == 0) //递归截止条件1，说明到了叶节点，但是这个条件可能用不上
			return 0;
		if(root.MarkNumber == markNumber) //找到匹配节点
		{
		    YesNodeCount=0; NoNodeCount=0;
		    CountMostNumClass(root); //统计叶节点中数量最多的Class，很关键的一步
		    if(YesNodeCount >= NoNodeCount) 
		    	root.attribute = ID3.yes;
		    else
		    	root.attribute = ID3.no;
		    root.MarkNumber = -1;
		    //只能“root.childs.clear()”，不能for循环中 set(i, null) 或者 remove(i)
		    root.childs.clear(); //<<<<<<<非常关键，害得我调试了一个小时
			return 1;
		}
		//其实这把叶节点也搜索了，但是所有叶节点的MarkNumber是-1，所以没影响
		for(int i=0; i<root.childs.size(); i++) 
		{
			int num = SearchAndReplaceNode(root.childs.get(i), markNumber);
			if(num == 1) return 1;
		}
		return 0;
	}
	
	//注意root是非叶节点，统计叶节点中数量最多的Class
	public void CountMostNumClass(Node root)
	{
		if(root.childs.size() == 0) //递归截止条件1，说明到了叶节点，但是这个条件可能用不上
			return;

		//其实这把叶节点也搜索了，但是所有叶节点的MarkNumber是-1，所以没影响
		for(int i=0; i<root.childs.size(); i++) 
		{
			String childNodeAttribute = root.childs.get(i).attribute;
			if(childNodeAttribute == ID3.yes) { //说明root的子节点是Yes叶节点
				YesNodeCount++;
			}else if(childNodeAttribute == ID3.no) { //说明root的子节点是No叶节点
				NoNodeCount++;
			}else{
				CountMostNumClass(root.childs.get(i));
			}
		}
	}
	
	/*=======================下面是在主函数中测试的代码，调试时很有用！！！=======================*/
	
    /*//=======多叉树拷贝clone()测试=======
    System.out.println("=======多叉树拷贝测试1=======");
    Node rootNew = (Node)root.clone();
    PrintTree(rootNew,0);
    for(int i=0; i<rootNew.childs.size(); i++) //进行干扰，测试拷贝的二叉树是否独立
    	rootNew.childs.set(i,null);
    System.out.println("=======多叉树拷贝测试2=======");
    PrintTree(root,0);
    */	
	
    /*==============MarkNonLeafNode()函数测试==============
    nonleafNum = 0;
    MarkNonLeafNode(root2);
    System.out.println("nonleafNum: " + nonleafNum);*/
	
    /*==============CountMostNumClass()函数测试==============
    YesNodeCount=0; NoNodeCount=0;
    //CountMostNumClass(root2.childs.get(0).childs.get(1));
    CountMostNumClass(root2);
    System.out.println("YesNodeCount: " + YesNodeCount);
    System.out.println("NoNodeCount: " + NoNodeCount);
    */	
}

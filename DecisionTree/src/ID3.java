import java.util.ArrayList;
import java.util.HashMap;

/** 
 * ID3 Algorithm Implementation
 */

//决策树节点  
class Node implements Cloneable
{	
	public String attribute; 		//对于非叶节点，attribute就是属性名称; 对于叶节点，attribute是0或者1
	public String arrived_value;	//父节点传递下来的属性值,除了根节点为空，其他所有节点都有值(即非空)
	int MarkNumber;
    ArrayList<Node> childs = new ArrayList<Node>();	//子结点集合，注意它是多叉树
    Node() {
    	attribute = null;
    	arrived_value = null;
    	MarkNumber = -1;
    	//childs = new ArrayList<Node>();
    }
	protected Object clone() //拷贝多叉树
	{
		try {
			Node treeClone = (Node)super.clone();
			//下面这条语句非常比较，相当于为新克隆的treeClone的childs拷贝了一块内存；不然会出错！！！
			treeClone.childs = (ArrayList<Node>) this.childs.clone(); 

			for(int i=0; i<this.childs.size(); i++) {
				if(this.childs.get(i) != null)
					treeClone.childs.set(i, (Node)this.childs.get(i).clone());
			}
			return treeClone;
		}
		catch(CloneNotSupportedException e){
			throw new InternalError(e.toString()); //can not happen-- we support clone here
		}  
	}
}

/**========输出不同的测试两处需要修改的地方========
* First: "static final int MAXLEN = 5;" 或者 MAXLEN = 21
* Second: String yes and String no
*/
public class ID3 {
	
	public ID3(){
		mapAttributeValues();	//一定要初始化，非常关键！！！
	}
	
	public static final int MAXLEN = 21; 		//<===注意此处要根据不同情况进行修改
	public static String yes = "1", no = "0";	//根据不同的例子的要求格式来进行修改 
	//public static yes = "yes", no = "no";
	
	public static ArrayList<ArrayList<String>> examples = new ArrayList<ArrayList<String>>(); //实例集  
	public static HashMap<String, ArrayList<String>> map_attribute_values = new HashMap<String, ArrayList<String>>(); 
	// AttributeRow是readFromCSVFile()函数读入数据的第一行，包含了全部的属性
	public static ArrayList<String> AttributeRow = new ArrayList<String>(); //保存首行即属性行数据
	
	//######################## Very Important Function! ##########################
	// 为每个Attribute开辟一个ArrayList<String>来存放属性值(Values)，并存入hashMap
	// 为了以后的移植程序方便，可以扩展其他多个string属性值，模拟了一个针对本例的map
	public void mapAttributeValues()
	{
		for(int i = 0; i < AttributeRow.size(); i++){
			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add("0"); valueList.add("1");
			map_attribute_values.put(AttributeRow.get(i), valueList);
		}
	}
	
	//根据具体属性和值来计算熵
	public double ComputeEntropy(ArrayList< ArrayList<String> > examples, String attribute, String value, boolean ifparent, boolean heuristic)
	{  
		int countYes=0;
		int countNo=0;
	    int i,j;  
	    boolean done_flag = false;	//哨兵值
	    for(j = 0; j < MAXLEN; j++){  
	        if(done_flag) break;  
	        if(AttributeRow.get(j).equals(attribute)){ 
	            for(i = 1; i < examples.size(); i++){  
	            	if((!ifparent && examples.get(i).get(j).equals(value)) || ifparent){ //ifparent记录是否算父节点
	            		//System.out.println("debug: " + remain_state.get(i).get(MAXLEN - 1));
	            		if(examples.get(i).get(MAXLEN - 1).equals(yes)){  
	                    	countYes++;
	                    }
	                    else countNo++;
	                }  
	            }  
	            done_flag = true;
	        }  
	    }
	    //System.out.println("debug: " + countYes + " " + countNo); //<<<<<<<<<<<<<<test
	    if(countYes == 0 || countNo == 0 ) return 0;//全部是正实例或者负实例
	    double sum = countYes + countNo;
	    
	    double entropy = 0;
	    if(heuristic == true) { // true: Information gain heuristic
		    //具体计算熵 根据[+countYes,-countNo],log2为底通过换底公式换成自然数底数  
	    	entropy = -countYes/sum*Math.log(countYes/sum)/Math.log(2.0) - countNo/sum*Math.log(countNo/sum)/Math.log(2.0);  
	    }else{
	    	entropy = (countYes/sum) * (countNo/sum); // false: Variance impurity heuristic
	    }
	    return entropy;
	}  
	      
	//计算按照属性attribute划分当前剩余实例的信息增益  
	public double ComputeGain(ArrayList< ArrayList<String> > examples, String attribute, boolean heuristic){  
	    int j,k,m;
	    //首先求不做划分时的熵
	    double parent_entropy = ComputeEntropy(examples, attribute, "", true, heuristic);  
	    double children_entropy = 0;
	    //然后求做划分后各个值的熵
	    ArrayList<String> values = map_attribute_values.get(attribute);
	    ArrayList<Double> ratio = new ArrayList<Double>();
	    ArrayList<Integer> count_values = new ArrayList<Integer>();
	    int tempint;
	    //System.out.println("attribute: " + attribute);//<<<<<<<<<<<<<<test
	    for(m = 0; m < values.size(); m++){
	        tempint = 0;  
	        for(k = 0; k < MAXLEN - 1; k++){ //修改，从0开始扫描
	            //if(!attribute_row[k].compare(attribute)){
	        	if(AttributeRow.get(k).equals(attribute)){
	                for(j = 1; j < examples.size(); j++){ //注意：从1开始扫描
	                    //if(!remain_state[j][k].compare(values[m])){  
	                	if(examples.get(j).get(k).equals(values.get(m))){
	                        tempint++;
	                    }
	                }
	            }
	        }
	        count_values.add(tempint);
	        //System.out.println("debug: " + values.get(m));
	    }  
	      
	    for(j = 0; j < values.size(); j++)
	        ratio.add((double)count_values.get(j) / (double)(examples.size()-1));

	    double temp_entropy;
	    for(j = 0; j < values.size(); j++){
	        temp_entropy = ComputeEntropy(examples, attribute, values.get(j), false, heuristic);  
	        children_entropy += ratio.get(j) * temp_entropy;
	        //System.out.println("debug children_entropy: " + ratio.get(j) * temp_entropy);//<<<<<<<<<<<<<<test
	    }
	    //System.out.println("parent-children entropy: " + (parent_entropy - children_entropy));
	    //System.out.println("=======================");//<<<<<<<<<<<<<<test
	    return (parent_entropy - children_entropy);
	}
	
	//找出样例中占多数的正/负性 
	public String MostCommonLabel(ArrayList< ArrayList<String> > examples){  
	    int p = 0, n = 0;  
	    for(int i = 0; i < examples.size(); i++){ //应该从0开始？？？
	    	if(examples.get(i).get(MAXLEN-1).equals(yes)) p++;
	        else n++;
	    }
	    if(p >= n) return "1"; //return "yes" or "+" or "1"
	    else return "0"; //return "no" or "-" or "0"
	}
	  
	//判断样例是否正负性都为label
	public boolean AllTheSameLabel(ArrayList< ArrayList<String> > examples, String label){
	    int count = 0;  
	    for(int i = 0; i < examples.size(); i++){
	    	if(examples.get(i).get(MAXLEN-1).equals(label)) count++;
	    }  
	    if(count == examples.size()-1) return true;
	    else return false;
	}
	
	/** 
	 * 计算信息增益，DFS构建决策树  
	 * @param  examples为输入样本，即剩余待分类的样本
	 * @param  remainAttributes为剩余还没有考虑的属性
	 * @return return the root of the decision tree
	 */
	public Node BulidDecisionTreeDFS(ArrayList<ArrayList<String>> examples, ArrayList<String> remainAttributes, boolean heuristic)
	{  
		Node p = new Node();
	    //先看搜索到树叶的情况  
	    if (AllTheSameLabel(examples, yes)){
	        p.attribute = "1";
	        return p;
	    }
	    if (AllTheSameLabel(examples, no)){
	        p.attribute = "0";
	        return p;
	    }
	    if(remainAttributes.size() == 0){ //所有的属性均已经考虑完了,还没有分尽 
	        String label = MostCommonLabel(examples);
	        p.attribute = label;
	        return p;
	    }
	  
	    //=====================寻找当前列表中最大的增益，maxIndex是其所在的位置=====================
	    double temp_gain=0;
	    //double max_gain = ComputeGain(remain_state, remain_attribute.get(0)); //把第一个属性的Gain赋给max_gain
	    double max_gain = 0;
	    int maxIndex = 0;
	    for(int i=0; i < remainAttributes.size(); i++){ //注意坐标从1开始
	        temp_gain = ComputeGain(examples, remainAttributes.get(i), heuristic);
	        if(temp_gain > max_gain) {  
	            max_gain = temp_gain;
	            maxIndex = i;
	        }
	    }
	    
	    //确定了最佳划分属性，注意保存maxIndex所在的属性，及其属性值，还有该属性该输入列表的坐标Index
	    p.attribute = remainAttributes.get(maxIndex); 
	    ArrayList<String> values = map_attribute_values.get(remainAttributes.get(maxIndex));  
	    int attribueIndex = AttributeRow.indexOf(remainAttributes.get(maxIndex));
	    if( attribueIndex == -1)
	    	System.out.println("can't find the numth of attribute");
	    
        //==============构建 new remaining Attributes list==============
	    ArrayList<String> newAttriList = new ArrayList<String>();
	    for(int j=0; j < remainAttributes.size(); j++)
	    	newAttriList.add(remainAttributes.get(j));
	    newAttriList.remove(maxIndex); //remove the Gain max item
	    
	    for(int i=0; i < values.size(); i++)
	    {
	    	//==============构建子样本==============
		    ArrayList<ArrayList<String>> subExamples = new ArrayList<ArrayList<String>>();  
		    subExamples.add(AttributeRow);
	        for(int j = 1; j < examples.size(); j++){  
	        	if(examples.get(j).get(attribueIndex).equals(values.get(i))){
	        		subExamples.add(examples.get(j));  
	            }  
	        }

	       //表示当前没有这个分支的样例，当前的new_node为叶子节点  
	        if(subExamples.size() == 0){
	        	Node leafNode = new Node();
	        	leafNode.attribute = MostCommonLabel(examples); //最常出现的目标属性值
	        	leafNode.arrived_value = values.get(i); //可有可无！？
	        	p.childs.add(leafNode);
	        }
	        else {
	        	Node newNode = BulidDecisionTreeDFS(subExamples, newAttriList, heuristic);
	        	newNode.arrived_value = values.get(i);	//一定要有！！！
	        	p.childs.add(newNode);
	        }
	    }  
	    return p;  
	}  	
    
}

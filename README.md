# Visibility-Checking
## Trace的格式

* 一个Trace使用JSON表示

* 格式如下例为

  ```json
  {
  	"SUBPROGRAMS":
  	[
  		{
  			"INVOCATIONS":
  			[
  				{"METHOD NAME":"put","ARGUMENTS":[1,0]},
  				{"METHOD NAME":"contains","ARGUMENTS":[0]},
  				{"METHOD NAME":"put","ARGUMENTS":[2,2]}
  			]
  		}, 
  		{
  			"INVOCATIONS":
  			[
  				{"METHOD NAME":"put","ARGUMENTS":[0,0]},
  				{"METHOD NAME":"put","ARGUMENTS":[1,1]},
  				{"METHOD NAME":"put","ARGUMENTS":[3,3]}
  			]
  		}
  	],
  	"HBS":
  	[
  		{
  			"HAPPENBEFORE":
  			[
  				{"PREV":[0,1],"NEXT":[1,1]}
  			]
  		},
  		{
  			"HAPPENBEFORE":
  			[
  				{"PREV":[0,1],"NEXT":[1,2]}
  			]
  		}
  	]
  }
  
  
  ```

* 需要解释的是，"HAPPENBEFORE"中包含了一系列的happen-before关系。"PREV"表示先发生的invocation的编号，"NEXT"表示后发生的invocation的编号。

## 构建基于Happen-Before的DAG

* <po, hbs>包含了若干的hb，基于一个hb可以生成一个对应的DAG

* DAG中的节点包含以下信息：invocation，nexts，prevs，id, pairID

```java
public class Node {
    private Invocation invocation;
    private List<Node> nexts = new ArrayList<>();
    private List<Node> prevs = new ArrayList<>();
    private int id;
    private Pair<Integer, Integer> pairID;
}
```

* nexts和prevs包含了所有的基于hb关系的后继和前驱

* id为每个invocation的unique id，用于识别invocation

  编号方法为：假设一共有m个SubProgram，$SubProgram_i$具有$l_i$个invocation，那么$SubProgram_i$的第j个invocation的编号为$(\sum_{k=0}^{i-1}l_k) + j$.

* pairID也唯一标识了一个invocation，不过是以数对的形式

  编号方法为：假设一共有m个SubProgram，$SubProgram_i$具有$l_i$个invocation，那么$SubProgram_i$的第j个invocation的编号为**<i, j>**.

## 基于DAG生成所有的Linearization

* 使用回溯法生成所有的Linearization
* 一个Linearization就是一个Node的序列。Node中不仅包含了一个Invocation，还有整个DAG图的信息，Vis关系需要这些信息。

## 基于Linearization生成Vis信息

* 定义了LinVisibility类，用于表示一个Linearization里的所有操作可能对应的一个vis关系

## Vis谓词检测

* 实现了weak、basic、monotonic、peer、causal、complete的vis谓词检测

## 抽象数据结构AbstractDataType

* 定义了抽象数据结构AbstractDataType类

* 有String invoke(Invocation invocation)方法。根据invocation的methodName反射出AbstractDataType中的对应方法进行执行

  ```java
  public final String invoke(Invocation invocation) throws Exception {
          String methodName = invocation.getMethodName();
          Class clazz = this.getClass();
          Method method = clazz.getDeclaredMethod(methodName, Invocation.class);
          method.setAccessible(true);
          return (String)method.invoke(this, invocation);
      }
  ```

  执行的返回值使用字符串表示

* 若要实现一个具体的数据结构，只要实现一个AbstractDataType子类即可

  例如MyHashMap类，内置了一个java.util.HashSet，并实现了put、contains方法包装了HashSet.put和HashSet.contains

  ```java
  public class MyHashMap extends AbstractDataType {
      HashMap<Integer, Integer> data = new HashMap<>();
      private String put(Invocation invocation) {
          Integer key = (Integer) invocation.getArguments().get(0);
          Integer value = (Integer) invocation.getArguments().get(1);
          Integer ret = data.put(key, value);
          if (ret == null) {
              return "N";
          } else {
              return Integer.toString(ret);
          }
      }
      private String contains(Invocation invocation) {
          boolean result = data.containsValue(invocation.getArguments().get(0));
          if (result) {
              return "T";
          } else {
              return "F";
          }
      }
  }
  ```

  

## AbstractDataType的执行

* AbstractDataType执行需要有三个参数，一个数据结构的实现Impl、一个Linearization lin、一个在lin下的visibility关系vis。

* 执行过程伪代码如下

  ```python
  let ret = {}
  for (let invocation in lin) {
      let seq = vis(invocation)
      for (let i in seq) {
      	let res = Impl.invoke(seq)
          if (i is last of seq) {
          	ret.append(res)
          }
      }
      Impl.reset()
  }
  yield ret
  ```

  

# Specification类

* 增加了一个Specification类，用Map保存了Method及其对应的Vis类型

# OperationTypes类

* 记录了CRDT中操作的类型：UPDATE、QUERY、QUERY-UPDATE

# Behaviour类

* 增加了一个Behaviour类，保存了一个trace中对应invocation在某次执行时的返回值

# Validation类

* loadTrace函数从一个json文件中加载一个trace

* visibilityRelaxationCheck函数的参数为Specification和AbstractDataType，返回值为Behaviour的集合。

  即传入一个Vis的规约和一个抽象数据结构的实现，返回在该trace下所有可能的执行结果。

* visibilityRelaxationCheck函数首先根据hb有向无环图生成所有的Linearization，然后根据每一个Linearization枚举vis关系，并使用规约Specification的谓词进行检测，最后对符合条件的Linearization和对应的vis关系进行执行，得到Behaviour结果
* RALinCheck函数的参数为OperationTypes和AbstractDataType，返回值为Behaviour的集合。

* RALinCheck函数和visibilityRelaxationCheck函数类似，区别在于
  * RALinCheck函数只使用CausalVisibility的谓词进行检测，筛选出符合CausalVisibility的Linearization和对应的vis关系
  * 在AbstractDataType上，UPDATE操作会执行在其前面的所有UPDATE操作；QUERY操作会执行其vis集合里所有的UPDATE操作
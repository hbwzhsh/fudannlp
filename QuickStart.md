# 组织结构 #
FudanNLP的组织结构可分为5层：
  1. 最底层的操作。比如数据结构、数据表示、数据类型、数据预处理、特征转换等。
  1. 结构化机器学习和人工规则框架。涉及到特征抽取，学习算法、推理算法和模型建立等。
  1. 可插拔的具体算法。比如分类、聚类、半监督和优化等。
  1. 中文自然语言处理应用，比如分词、句法分析等。
  1. 系统应用，比如文本分类、主题词抽取等。
![https://fudannlp.googlecode.com/svn/images/org-small.png](https://fudannlp.googlecode.com/svn/images/org-small.png)

# 目录结构 #
| **目录** | **描述**|
|:-------|:------|
|    /src | 主要功能代码，主目录。 |
|    /test  | 测试或单元测试代码。 |
|    /example     |对外API使用示例代码。|
|    /example-data     | 使用示例需要的数据。 |
|    /apps     | 基于FudanNLP的应用 |
|    /models      |必须的模型文件或知识文件 |
|    /docs | 项目文档  |

# Java包组织结构 #
|     **目录** | **描述** |
|:-----------|:-------|
|      edu.fudan.ml.types | 数据类型。  |
|      edu.fudan.ml.data | 数据读取器。通过Reader接口将原始数据读入，并生产Instance对象。Reader为一个迭代器，依次返回一个Instance对象。|
|      edu.fudan.ml.classifier    | 机器学习。包括分类器和训练器两部分。按照结构化学习的思想分为：特征生成、损失函数和统计推理三部分。|
|      edu.fudan.ml.feature .generator | 特征生成。  |
|      edu.fudan.ml.loss      | 损失函数   |
|      edu.fudan.ml.inf      | 统计推理。这里对于离散的类别，使用简单遍历计算，然后求最大值得方法。|

# 自然语言处理基础相关的Java包 #
|     **目录** | **描述** |
|:-----------|:-------|
| edu.fudan.nlp.pipe |数据特征变换器。这里进行数据不同形式表示之间的转换。比如从文本到向量的转换。|
| edu.fudan.nlp.parser |句法分析包。  |
| edu.fudan.nlp.tag    |序列标注任务训练等。|
| edu.fudan.nlp.cn    |分词、词性标注、实体名识别、以及中文处理一些规则方法。|

# 自然语言处理应用相关的Java包 #
|     **目录** | **描述** |
|:-----------|:-------|
| edu.fudan.nlp.app.keyword | 关键词抽取。 |
|   edu.fudan.nlp.app.tc | 文本分类器。 |

## 处理流程 ##
![https://fudannlp.googlecode.com/svn/images/flow.png](https://fudannlp.googlecode.com/svn/images/flow.png)


# 第三方工具包(Third-Part Libs) #
  * trove 3.0.3 http://trove.starlight-systems.com/
  * commons-cli-1.2.jar(Just needed when invoking fudannlp from the command line) http://commons.apache.org/cli/
  * JRE 1.6 above

# 使用方法(Usages) #
我们提供三种调用方法：
## 命令行调用方式 ##

<font color='#FF0000'>最新版请参考发布包内“FudanNLP Function Test.cmd”示例。</font>

**序列标注
```
	训练：
	java -classpath fudannlp.jar;lib/commons-cli-1.2.jar;lib/trove
jar; edu.fudan.nlp.tag.Tagger -train template train model
	测试：
	java -classpath fudannlp.jar;lib/commons-cli-1.2.jar;lib/trove
jar; edu.fudan.nlp.tag.Tagger model test result
---
```**

**中文自然语言处理
```
分词实例

java -classpath fudannlp.jar;lib/commons-cli-1.2.jar;lib/trove
jar; edu.fudan.nlp.cn.tag.CWSTagger -s models/seg.m "自然语言是人类交流和思维的
主要工具，是人类智慧的结晶。"
自然 语言 是 人类 交流 和 思维 的 主要 工具 ， 是 人类 智慧 的 结晶 。


词性标注实例

java -classpath fudannlp.jar;lib/commons-cli-1.2.jar;lib/trove
jar; edu.fudan.nlp.cn.tag.POSTagger -s models/seg.m models/pos.m "周杰伦出生于台
湾，生日为79年1月18日，他曾经的绯闻女友是蔡依林。"
周杰伦/人名 出生/动词 于/介词 台湾/地名 ，/标点 生日/名词 为/介词 79年/时间短语
1月/时间短语 18日/时间短语 ，/标点 他/人称代词 曾经/副词 的/结构助词 绯闻/名词
女友/名词 是/动词 蔡依林/人名 。/标点

实体名识别实例

java -classpath fudannlp.jar;lib/commons-cli-1.2.jar;lib/trove
jar; edu.fudan.nlp.cn.tag.NERTagger -s models/seg.m models/pos.m "詹姆斯·默多克
和丽贝卡·布鲁克斯 鲁珀特·默多克旗下的美国小报《纽约邮报》的职员被公司律师告知
，保存任何也许与电话窃听及贿赂有关的文件。 "
{詹姆斯·默多克=人名, 丽贝卡·布鲁克斯=实体名, 纽约=地名, 美国=地名}
```**



  * [命令行调用方式](fudannlp_cli.md)
  * [WebServices方式](fudannlp_ws.md)
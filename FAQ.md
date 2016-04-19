## 技术问题 ##
| **问题** | **解决办法** |
|:-------|:---------|
| Exception in thread "main" java.lang.OutOfMemoryError: Java heap space | 增加java Heap size的值。       java -Xmx2g -jar  fudannlp.jar   。 Eclipse调整VM Argument文本框中加入：-Xmx2g |
|如何调整Eclipse内存|打开eclipse  window－preferences－Java －Installed JREs －Edit －Default VM Arguments。   在VM自变量中输入：-Xmx500m|
|Exception  in  thread  "main" java.lang.NoClassDefFoundError: java|需要加入附加的两个jar文件|

## 使用问题 ##
|对繁体文支持的好吗？|模型本身不支持繁体，但是可以通过ChineseTrans.toSimp()函数转为简体|
|:---------|:------------------------------------------|
|请问在models文件夹下的那些压缩包的用途是什么？是词典么？|训练好的模型文件，主要包含两个数据结构：字符串特征的索引，和权重向量         |
|能扼要的介绍一下模型文件的组成吗？|seg是分词，pos是词性，ner是实体名，time是时间提取，dict是分词可选字典|
|FudanNLP支持并行化吗？支持的话怎么用呢？|支持，直接多线程调用就可以                              |
|为什么对英文支持不好|因为训练语料是中文的，因此只能处理中文情况。英文的需要用英文的语料训练。       |
|FudanNPL的词性标注中的每个标注什么意思？|见FudanNLP Book                             |

## 算法问题 ##
|这个分词是用算法的HMM/CRF吗|不是，HMM/CRF是对数线性模型。而我们使用线性模型，但解码的方式是一样的|
|:----------------|:-------------------------------------|
|现用的词库是怎么训练的，语料库的格式是怎么样的？| 主要是序列标注，训练文件示例example-data/structure有。|
|句法分析实例中最后输出的那一串数字代表什么意思呢？例如：“2 2 6 2 5 3 -1 6" | 这串数字代表的是这个词的支配词或者父词的位置，序号以0开始，其中-1表示根词，就是这个词是整个依存树的根。比如2代表的就是第3个词|
|fudannlp提供抽取关键字类 WordExtract的算法是怎样的？|利用Textrank来实现的，具体可以看论文R. Mihalcea and P. Tarau. Textrank: Bringing order into texts. In Proceedings of EMNLP, 2004 或FudanNLP Book|
|介绍下句法分析模块,最后如何得到一个句子的语法分析呀|是action based 依存句法分析                  |
# 介绍(Introduction) #
FudanNLP主要是为中文自然语言处理而开发的工具包，也包含为实现这些任务的机器学习算法和[数据集](DataSet.md)。FudanNLP及其包含数据集使用LGPL3.0许可证。

<font color='#FF0000'>FudanNLP正迁移至<a href='https://github.com/xpqiu/fnlp/'>GitHub</a>，并更名为FNLP，希望以更加开放的方式来协作开发。</font>

<font color='#FF0000'>FudanNLP is being moved to <a href='https://github.com/xpqiu/fnlp/'>GitHub</a> and renamed FNLP. We wish a more open way to collaborative development.</font>


FudanNLP is developed for Chinese natural language processing (NLP), which also includes some machine learning algorithms and [data sets](DataSet.md) to achieve the NLP tasks. FudanNLP is distributed under LGPL3.0.

If you're new to FudanNLP, check out the [Quick Start (使用说明)](QuickStart.md) page, [FudanNLP Book](http://vdisk.weibo.com/s/iyiB0) or [Java-docs](https://fudannlp.googlecode.com/svn/FudanNLP-1.5-API/java-docs/index.html).

You can also use the [Demo Website(演示网站)](http://jkx.fudan.edu.cn/nlp) so that you may check the functionality prior to downloading.

---


**有遇到FudanNLP不能处理的例子，请到这里提交: [协同数据收集](http://code.google.com/p/fudannlp/wiki/CollaborativeCollection)，有问题请查看[FAQ](FAQ.md)或到[微群](http://q.weibo.com/960122)、QQ群（253541693）讨论。**



<font color='#FF0000'> 2013.8.14 发布FudanNLP1.6.1版 </font> [更新日志(ChangeLog)](ChangeLog.md)


---

# 功能(Functions) #

  1. 信息检索： 文本分类 新闻聚类
  1. 中文处理： 中文分词 词性标注 实体名识别 关键词抽取 依存句法分析 时间短语识别
  1. 结构化学习： 在线学习 层次分类 聚类 精确推理

[性能测试(Benchmark)](Benchmark.md)
[开发计划(Development Plan)](DevPlan.md)
[开发人员列表(Developers)](People.md)

# 使用(Usages) #
欢迎大家提供非Java语言的接口。

[PHP](PHP.md) [Python](Python.md) [云服务](fudannlp_ws.md)

# 引用(Citation) #

If you would like to acknowledge our efforts, please cite the following paper.

如果我们的工作对您有帮助，请引用下面论文。

<font color='#FF0000'><b>Xipeng Qiu, Qi Zhang and Xuanjing Huang, FudanNLP: A Toolkit for Chinese Natural Language Processing, In Proceedings of Annual Meeting of the Association for Computational Linguistics (ACL), 2013.</b>
</font>

```
@INPROCEEDINGS{Qiu:2013,
  author = {Xipeng Qiu and Qi Zhang and Xuanjing Huang},
  title = {FudanNLP: A Toolkit for Chinese Natural Language Processing},
  booktitle = {Proceedings of Annual Meeting of the Association for Computational Linguistics},
  year = {2013},
}
```

在[这里](http://jkx.fudan.edu.cn/~xpqiu/) 或  [Google Scholar](http://scholar.google.com/citations?sortby=pubdate&hl=en&user=Pq4Yp_kAAAAJ&view_op=list_works) 或 [DBLP](http://www.informatik.uni-trier.de/~ley/pers/hd/q/Qiu:Xipeng.html) 可以找到更多的相关论文。


We used [JProfiler](http://www.ej-technologies.com/products/jprofiler/overview.html) to help optimize the code.
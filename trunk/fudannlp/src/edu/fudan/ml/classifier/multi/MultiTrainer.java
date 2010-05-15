package edu.fudan.ml.classifier.multi;
import edu.fudan.ml.types.InstanceSet;
public interface MultiTrainer {
	MultiClassifier getClassifier();
	MultiClassifier train(InstanceSet instances);
}

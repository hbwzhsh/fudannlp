package edu.fudan.ml.classifier;
import edu.fudan.ml.types.InstanceSet;
public interface Trainer {
	Classifier getClassifier();
	Classifier train(InstanceSet instances);
}

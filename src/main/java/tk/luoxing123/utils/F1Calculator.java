package tk.luoxing123.utils;

import java.util.Set;

import com.google.common.collect.Sets;

/*
 *no totally implement  
 */

public class F1Calculator<T> {
	private int corrects = 0;
	private int answers = 0;
	private int golds = 0;

	public void addInstance(T gold, T answer) {
		if (gold != null && gold.equals(answer)) {
			corrects++;
		}
		if (gold != null)
			golds++;
		if (answer != null)
			answers++;
	}

	public void addInstances(Set<T> gold, Set<T> answer) {
		golds += gold.size();
		answers += answer.size();
		corrects += Sets.intersection(gold, answer).size();
	}

	public void addUnasnweredGold() {
		golds++;

	}

	public void addWrongAnswer() {
		answers++;
	}

	public double getF1() {
		double r = corrects / (double) answers;
		double p = corrects / (double) golds;
		return (2 * p * r) / (p + r);
	}
}

package connectives;

import concepts.AtomicConcept;
import concepts.BottomConcept;
import concepts.TopConcept;
import formula.Formula;


import java.util.List;

/**
 *
 * @author Yizheng
 */
public class And extends Formula {

	public And() {
		super();
	}
	
	public And(List<Formula> list) {
		super(list.size());
		this.setSubFormulas(list);
	}

	@Override
	public String toString() {
		if (this.getSubFormulas().size() == 1) {
			return this.getSubFormulas().get(0).toString();
		}
		String str = "";
		for (int i = 0; i < this.getSubFormulas().size(); i++) {
			if (i == 0) {
				if (this.getSubFormulas().get(i) instanceof AtomicConcept
						|| this.getSubFormulas().get(i) == TopConcept.getInstance()
						|| this.getSubFormulas().get(i) == BottomConcept.getInstance()
						|| this.getSubFormulas().get(i) instanceof Negation
						|| this.getSubFormulas().get(i) instanceof GreaterThan
						|| this.getSubFormulas().get(i) instanceof LessThan) {
					str = str + this.getSubFormulas().get(i);
					continue;
				}
				str = str + "(" + this.getSubFormulas().get(i) + ")";
				continue;
			}
			if (this.getSubFormulas().get(i) instanceof AtomicConcept
					|| this.getSubFormulas().get(i) == TopConcept.getInstance()
					|| this.getSubFormulas().get(i) == BottomConcept.getInstance()
					|| this.getSubFormulas().get(i) instanceof Negation
					|| this.getSubFormulas().get(i) instanceof GreaterThan
					|| this.getSubFormulas().get(i) instanceof LessThan) {
				str = str + " \u2293 " + this.getSubFormulas().get(i);
				continue;
			}
			str = str + " \u2293 " + "(" + this.getSubFormulas().get(i) + ")";
		}
		return str + "";
	}
}

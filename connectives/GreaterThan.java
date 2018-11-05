/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectives;

import concepts.ConceptExpression;
import formula.Formula;

public class GreaterThan extends ConceptExpression {

	public GreaterThan() {
		super();
	}

	public GreaterThan(Integer number, Formula role, Formula filler) {
		super(number, role, filler);
	}

	@Override
	public String toString() {
		Integer number = this.getNumber();
		Formula role = this.getSubFormulas().get(0);
		Formula filler = this.getSubFormulas().get(1);

		if (filler instanceof And || filler instanceof Or) {
			return "\u2265" + number + role + ".(" + filler + ")";
		} else {
			return "\u2265" + number + role + "." + filler;
		}
	}
}

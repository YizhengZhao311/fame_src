/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectives;

import formula.Formula;
import roles.RoleExpression;

/**
 *
 * @author Yizheng
 */
public class Inverse extends RoleExpression {

	public Inverse() {
		super();
	}

	public Inverse(RoleExpression re) {
		super(re);
	}
	
	public Inverse(Formula formula) {
		super(formula);
	}

	@Override
	public String toString() {
		return this.getSubFormulas().get(0) + "\u05BF";
	}
}

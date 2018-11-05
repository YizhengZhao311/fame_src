/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checkexistence;

import java.util.List;

import concepts.AtomicConcept;
import connectives.And;
import connectives.Equivalence;
import connectives.GreaterThan;
import connectives.Inclusion;
import connectives.Inverse;
import connectives.LessThan;
import connectives.Negation;
import connectives.Or;
import formula.Formula;
import roles.AtomicRole;
import roles.BottomRole;
import roles.TopRole;

/**
 *
 * @author Yizheng311
 */
public class EChecker {

	public EChecker() {

	}

	public boolean isPresent(AtomicRole role, Formula formula) {

		if (formula instanceof AtomicRole) {
			return formula.equals(role);
		} else if (formula instanceof Negation || formula instanceof Inverse) {
			return isPresent(role, formula.getSubFormulas().get(0));
		} else if (formula instanceof GreaterThan || formula instanceof LessThan || formula instanceof Inclusion
				|| formula instanceof Equivalence) {
			return isPresent(role, formula.getSubFormulas().get(0)) || isPresent(role, formula.getSubFormulas().get(1));
		} else if (formula instanceof And || formula instanceof Or) {
			List<Formula> operand_list = formula.getSubFormulas();
			for (Formula operand : operand_list) {
				if (isPresent(role, operand)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isPresent(AtomicConcept concept, Formula formula) {

		if (formula instanceof AtomicConcept) {
			return formula.equals(concept);
		} else if (formula instanceof Negation) {
			return isPresent(concept, formula.getSubFormulas().get(0));
		} else if (formula instanceof GreaterThan || formula instanceof LessThan) {
			return isPresent(concept, formula.getSubFormulas().get(1));
		} else if (formula instanceof Inclusion || formula instanceof Equivalence) {
			return isPresent(concept, formula.getSubFormulas().get(0))
					|| isPresent(concept, formula.getSubFormulas().get(1));
		} else if (formula instanceof And || formula instanceof Or) {
			List<Formula> operand_list = formula.getSubFormulas();
			for (Formula operand : operand_list) {
				if (isPresent(concept, operand)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isNegationInOr(Formula formula) {

		if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();

			for (Formula disjunct : disjunct_list) {
				if (disjunct instanceof Negation) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isAndInOr(Formula formula) {

		if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();

			for (Formula disjunct : disjunct_list) {
				if (disjunct instanceof And) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isAndInAnd(Formula formula) {

		if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();

			for (Formula conjunct : conjunct_list) {
				if (conjunct instanceof And) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isOrInOr(Formula formula) {

		if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();

			for (Formula disjunct : disjunct_list) {
				if (disjunct instanceof Or) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isOrInAnd(Formula formula) {

		if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();

			for (Formula conjunct : conjunct_list) {
				if (conjunct instanceof Or) {
					return true;
				}
			}
		}

		return false;
	}
	
	public boolean allNegationsInside(Formula formula) {

		List<Formula> conjunct_list = formula.getSubFormulas();

		for (Formula conjunct : conjunct_list) {
			if (!(conjunct instanceof Negation)) {
				return false;
			}
		}

		return true;
	}
	
	public boolean hasRole(Formula formula) {

		if (formula instanceof AtomicRole || formula == TopRole.getInstance() || formula == BottomRole.getInstance()) {
			return true;
		} else if (formula instanceof Negation || formula instanceof Inverse) {
			return hasRole(formula.getSubFormulas().get(0));
		} else if (formula instanceof GreaterThan || formula instanceof LessThan || formula instanceof Inclusion
				|| formula instanceof Equivalence) {
			return hasRole(formula.getSubFormulas().get(0)) || hasRole(formula.getSubFormulas().get(1));
		} else if (formula instanceof And || formula instanceof Or) {
			List<Formula> operand_list = formula.getSubFormulas();
			for (Formula operand : operand_list) {
				if (hasRole(operand)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasRoleRestriction(Formula formula) {

		if (formula instanceof Negation) {
			return hasRoleRestriction(formula.getSubFormulas().get(0));
		} else if (formula instanceof GreaterThan || formula instanceof LessThan) {
			return true;
		} else if (formula instanceof Inclusion || formula instanceof Equivalence) {
			return hasRoleRestriction(formula.getSubFormulas().get(0))
					|| hasRoleRestriction(formula.getSubFormulas().get(1));
		} else if (formula instanceof And || formula instanceof Or) {
			List<Formula> operand_list = formula.getSubFormulas();
			for (Formula operand : operand_list) {
				if (hasRoleRestriction(operand)) {
					return true;
				}
			}
		}

		return false;
	}

}

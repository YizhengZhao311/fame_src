package preprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.google.common.collect.Lists;

import checkexistence.EChecker;
import concepts.BottomConcept;
import concepts.TopConcept;
import connectives.And;
import connectives.GreaterThan;
import connectives.LessThan;
import connectives.Negation;
import connectives.Or;
import formula.Formula;
import roles.BottomRole;
import roles.TopRole;

public class PreProcessor {

	public PreProcessor() {

	}
			
	public List<Formula> getSimplifiedForm(List<Formula> input_list) throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();

		for (Formula unsimplified : input_list) {
			Formula simplified = getSimplifiedForm(unsimplified);

			if (simplified == BottomConcept.getInstance() || simplified == BottomRole.getInstance()) {
				return Collections.singletonList(BottomConcept.getInstance());
				
			} else if (simplified == TopConcept.getInstance() || simplified == TopRole.getInstance()) {

				
			} else if (simplified instanceof And) {
				output_list.addAll(simplified.getSubFormulas());
				
			} else {
				output_list.add(simplified);
			}
			
		}

		return output_list;
	}
	
	public Formula getSimplifiedForm(Formula formula) throws CloneNotSupportedException {

		while (!(formula.equals(simplifiedForm(formula)))) {
			formula = simplifiedForm(formula);
		}
		
		return formula;
	}
	
	public Formula simplifiedForm(final Formula input) throws CloneNotSupportedException {

		Formula formula = input.clone();

		formula = getNNF(formula);
		formula = simplified_1(formula);
		formula = simplified_2(formula);
		formula = simplified_3(formula);

		return formula;
	}
	
	private Formula simplified_1(Formula formula) {

		if (formula instanceof Negation) {
			formula.getSubFormulas().set(0, simplified_1(formula.getSubFormulas().get(0)));
			return formula;

		} else if (formula instanceof GreaterThan) {
			if (formula.getSubFormulas().get(0) == BottomRole.getInstance()
					|| formula.getSubFormulas().get(1) == BottomConcept.getInstance()) {
				return BottomConcept.getInstance();
				
			} else {
				formula.getSubFormulas().set(1, simplified_1(formula.getSubFormulas().get(1)));
				return formula;
			}

		} else if (formula instanceof LessThan) {
			if (formula.getSubFormulas().get(0) == BottomRole.getInstance()
					|| formula.getSubFormulas().get(1) == BottomConcept.getInstance()) {
				return TopConcept.getInstance();

			} else {
				formula.getSubFormulas().set(1, simplified_1(formula.getSubFormulas().get(1)));
				return formula;
			}

		} else if (formula instanceof And) {

			EChecker ec = new EChecker();

			if (formula.getSubFormulas().size() == 1) {
				return simplified_1(formula.getSubFormulas().get(0));

			} else if (ec.isAndInAnd(formula)) {
				List<Formula> conjunct_list = formula.getSubFormulas();
				List<Formula> new_conjunct_list = new ArrayList<>();

				for (Formula conjunct : conjunct_list) {
					if (conjunct instanceof And) {
						new_conjunct_list.addAll(conjunct.getSubFormulas());
					} else {
						new_conjunct_list.add(conjunct);
					}
				}
				formula.getSubFormulas().clear();
				formula.getSubFormulas().addAll(new_conjunct_list);
				return simplified_1(formula);

			} else {
				for (int i = 0; i < formula.getSubFormulas().size(); i++) {
					formula.getSubFormulas().set(i, simplified_1(formula.getSubFormulas().get(i)));
				}
				return formula;
			}

		} else if (formula instanceof Or) {

			EChecker ec = new EChecker();

			if (formula.getSubFormulas().size() == 1) {
				return simplified_1(formula.getSubFormulas().get(0));

			} else if (ec.isOrInOr(formula)) {
				List<Formula> disjunct_list = formula.getSubFormulas();
				List<Formula> new_disjunct_list = new ArrayList<>();

				for (Formula disjunct : disjunct_list) {
					if (disjunct instanceof Or) {
						new_disjunct_list.addAll(disjunct.getSubFormulas());
					} else {
						new_disjunct_list.add(disjunct);
					}
				}
				formula.getSubFormulas().clear();
				formula.getSubFormulas().addAll(new_disjunct_list);
				return simplified_1(formula);

			} else {
				for (int i = 0; i < formula.getSubFormulas().size(); i++) {
					formula.getSubFormulas().set(i, simplified_1(formula.getSubFormulas().get(i)));
				}
				return formula;
			}
		}

		return formula;
	}
	
	/*private Formula simplifiedOne(Formula formula) {

		if (formula instanceof Negation) {
			formula.getSubFormulas().set(0, simplifiedOne(formula.getSubFormulas().get(0)));
			return formula;

		} else if (formula instanceof GreaterThan) {
			if (formula.getSubFormulas().get(0) == BottomRole.getInstance()
					|| formula.getSubFormulas().get(1) == BottomConcept.getInstance()) {
				return BottomConcept.getInstance();
				
			} else {
				formula.getSubFormulas().set(0, simplifiedOne(formula.getSubFormulas().get(0)));
				formula.getSubFormulas().set(1, simplifiedOne(formula.getSubFormulas().get(1)));
				return formula;
			}

		} else if (formula instanceof LessThan) {
			if (formula.getSubFormulas().get(0) == BottomRole.getInstance()
					|| formula.getSubFormulas().get(1) == BottomConcept.getInstance()) {
				return TopConcept.getInstance();

			} else {
				formula.getSubFormulas().set(0, simplifiedOne(formula.getSubFormulas().get(0)));
				formula.getSubFormulas().set(1, simplifiedOne(formula.getSubFormulas().get(1)));
				return formula;
			}

		} else if (formula instanceof And) {

			EChecker ec = new EChecker();

			if (formula.getSubFormulas().size() == 1) {
				return simplifiedOne(formula.getSubFormulas().get(0));

			} else if (ec.isAndInAnd(formula)) {
				List<Formula> conjunct_list = formula.getSubFormulas();
				List<Formula> new_conjunct_list = new ArrayList<>();

				for (Formula conjunct : conjunct_list) {
					if (conjunct instanceof And) {
						new_conjunct_list.addAll(conjunct.getSubFormulas());
					} else {
						new_conjunct_list.add(conjunct);
					}
				}
				formula.getSubFormulas().clear();
				formula.getSubFormulas().addAll(new_conjunct_list);
				return simplifiedOne(formula);

			} else {
				for (int i = 0; i < formula.getSubFormulas().size(); i++) {
					formula.getSubFormulas().set(i, simplifiedOne(formula.getSubFormulas().get(i)));
				}
				return formula;
			}

		} else if (formula instanceof Or) {

			EChecker ec = new EChecker();

			if (formula.getSubFormulas().size() == 1) {
				return simplifiedOne(formula.getSubFormulas().get(0));

			} else if (ec.isOrInOr(formula)) {
				List<Formula> disjunct_list = formula.getSubFormulas();
				List<Formula> new_disjunct_list = new ArrayList<>();

				for (Formula disjunct : disjunct_list) {
					if (disjunct instanceof Or) {
						new_disjunct_list.addAll(disjunct.getSubFormulas());
					} else {
						new_disjunct_list.add(disjunct);
					}
				}
				formula.getSubFormulas().clear();
				formula.getSubFormulas().addAll(new_disjunct_list);
				return simplifiedOne(formula);

			} else {
				for (int i = 0; i < formula.getSubFormulas().size(); i++) {
					formula.getSubFormulas().set(i, simplifiedOne(formula.getSubFormulas().get(i)));
				}
				return formula;
			}
		}

		return formula;
	}*/
	
	private Formula simplified_2(Formula formula) {

		if (formula instanceof Negation) {
			formula.getSubFormulas().set(0, simplified_2(formula.getSubFormulas().get(0)));
			return formula;

		} else if (formula instanceof GreaterThan || formula instanceof LessThan) {
			formula.getSubFormulas().set(1, simplified_2(formula.getSubFormulas().get(1)));
			return formula;

		} else if (formula instanceof And || formula instanceof Or) {
			List<Formula> operand_list = formula.getSubFormulas();
			List<Formula> new_operand_list = new ArrayList<>();
			for (Formula operand : operand_list) {
				Formula new_operand = simplified_2(operand);
				if (!new_operand_list.contains(new_operand)) {
					new_operand_list.add(new_operand);
				}
			}
			if (new_operand_list.size() == 1) {
				return new_operand_list.get(0);
			} else {
				formula.getSubFormulas().clear();
				formula.getSubFormulas().addAll(new_operand_list);
				return formula;
			}		
		}

		return formula;
	}
	
	// And(A,B,A)=And(A,B)
	/*private Formula simplifiedFour(Formula formula) {

		if (formula instanceof Negation) {
			formula.getSubFormulas().set(0, simplifiedFour(formula.getSubFormulas().get(0)));
			return formula;

		} else if (formula instanceof GreaterThan || formula instanceof LessThan) {
			formula.getSubFormulas().set(0, simplifiedFour(formula.getSubFormulas().get(0)));
			formula.getSubFormulas().set(1, simplifiedFour(formula.getSubFormulas().get(1)));
			return formula;

		} else if (formula instanceof And || formula instanceof Or) {
			List<Formula> operand_list = formula.getSubFormulas();
			List<Formula> new_operand_list = new ArrayList<>();
			for (Formula operand : operand_list) {
				Formula new_operand = simplifiedFour(operand);
				if (!new_operand_list.contains(new_operand)) {
					new_operand_list.add(new_operand);
				}
			}
			if (new_operand_list.size() == 1) {
				return new_operand_list.get(0);
			} else {
				formula.getSubFormulas().clear();
				formula.getSubFormulas().addAll(new_operand_list);
				return formula;
			}		
		}

		return formula;
	}*/
	
	private Formula simplified_3(Formula formula) {

		if (formula instanceof Negation) {
			formula.getSubFormulas().set(0, simplified_3(formula.getSubFormulas().get(0)));
			return formula;

		} else if (formula instanceof GreaterThan || formula instanceof LessThan) {
			formula.getSubFormulas().set(1, simplified_3(formula.getSubFormulas().get(1)));
			return formula;

		} else if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			
			for (int i = 0; i < conjunct_list.size(); i++) {
				if (conjunct_list.get(i) == BottomConcept.getInstance()) {
					return BottomConcept.getInstance();
				}
				if (conjunct_list.get(i) == BottomRole.getInstance()) {
					return BottomRole.getInstance();
				}
				for (int j = i + 1; j < conjunct_list.size(); j++) {
					if (conjunct_list.get(j).negationComplement(conjunct_list.get(i))) {
						return BottomConcept.getInstance();
					}
				}
				if (conjunct_list.get(i) != TopConcept.getInstance() && conjunct_list.get(i) != TopRole.getInstance()) {
					new_conjunct_list.add(simplified_3(conjunct_list.get(i)));
				}
			}

			if (new_conjunct_list.isEmpty()) {
				return TopConcept.getInstance();
				
			} else if (new_conjunct_list.size() == 1) {
				return new_conjunct_list.get(0);
				
			} else {
				formula.getSubFormulas().clear();
				formula.getSubFormulas().addAll(new_conjunct_list);
				return formula;
			}

		} else if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();			
			
			for (int i = 0; i < disjunct_list.size(); i++) {
				if (disjunct_list.get(i) == TopConcept.getInstance()) {
					return TopConcept.getInstance();
				}
				if (disjunct_list.get(i) == TopRole.getInstance()) {
					return TopRole.getInstance();
				}
				for (int j = i + 1; j < disjunct_list.size(); j++) {
					if (disjunct_list.get(j).negationComplement(disjunct_list.get(i))) {
						return TopConcept.getInstance();
					}
				}
				if (disjunct_list.get(i) != BottomConcept.getInstance() && disjunct_list.get(i) != BottomRole.getInstance()) {
					new_disjunct_list.add(simplified_3(disjunct_list.get(i)));
				}
			}

			if (new_disjunct_list.isEmpty()) {
				return BottomConcept.getInstance();
				
			} else if (new_disjunct_list.size() == 1) {
				return new_disjunct_list.get(0);
				
			} else {
				formula.getSubFormulas().clear();
				formula.getSubFormulas().addAll(new_disjunct_list);
				return formula;				
			}
		}

		return formula;
	}

	// And(A,B,~A)=false, Or(A,B,~A)=true
	/*private Formula simplifiedFive(Formula formula) {

		if (formula instanceof Negation) {
			formula.getSubFormulas().set(0, simplifiedFive(formula.getSubFormulas().get(0)));
			return formula;

		} else if (formula instanceof GreaterThan || formula instanceof LessThan) {
			formula.getSubFormulas().set(0, simplifiedFive(formula.getSubFormulas().get(0)));
			formula.getSubFormulas().set(1, simplifiedFive(formula.getSubFormulas().get(1)));
			return formula;

		} else if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			
			for (int i = 0; i < conjunct_list.size(); i++) {
				if (conjunct_list.get(i) == BottomConcept.getInstance()) {
					return BottomConcept.getInstance();
				}
				if (conjunct_list.get(i) == BottomRole.getInstance()) {
					return BottomRole.getInstance();
				}
				for (int j = i + 1; j < conjunct_list.size(); j++) {
					if (conjunct_list.get(j).negationComplement(conjunct_list.get(i))) {
						return BottomConcept.getInstance();
					}
				}
				if (conjunct_list.get(i) != TopConcept.getInstance() && conjunct_list.get(i) != TopRole.getInstance()) {
					new_conjunct_list.add(simplifiedFive(conjunct_list.get(i)));
				}
			}

			if (new_conjunct_list.isEmpty()) {
				return TopConcept.getInstance();
				
			} else if (new_conjunct_list.size() == 1) {
				return new_conjunct_list.get(0);
				
			} else {
				formula.getSubFormulas().clear();
				formula.getSubFormulas().addAll(new_conjunct_list);
				return formula;
			}

		} else if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();			
			
			for (int i = 0; i < disjunct_list.size(); i++) {
				if (disjunct_list.get(i) == TopConcept.getInstance()) {
					return TopConcept.getInstance();
				}
				if (disjunct_list.get(i) == TopRole.getInstance()) {
					return TopRole.getInstance();
				}
				for (int j = i + 1; j < disjunct_list.size(); j++) {
					if (disjunct_list.get(j).negationComplement(disjunct_list.get(i))) {
						return TopConcept.getInstance();
					}
				}
				if (disjunct_list.get(i) != BottomConcept.getInstance() && disjunct_list.get(i) != BottomRole.getInstance()) {
					new_disjunct_list.add(simplifiedFive(disjunct_list.get(i)));
				}
			}

			if (new_disjunct_list.isEmpty()) {
				return BottomConcept.getInstance();
				
			} else if (new_disjunct_list.size() == 1) {
				return new_disjunct_list.get(0);
				
			} else {
				formula.getSubFormulas().clear();
				formula.getSubFormulas().addAll(new_disjunct_list);
				return formula;				
			}
		}

		return formula;
	} */

	public List<Formula> getNNF(List<Formula> input_list) {

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			output_list.add(getNNF(formula));
		}

		return output_list;
	}
	
	public Formula removeDoubleNegations(Formula formula) {

		if (formula instanceof Negation) {
			Formula operand = formula.getSubFormulas().get(0);

			if (operand == TopConcept.getInstance()) {
				return BottomConcept.getInstance();
				
			} else if (operand == BottomConcept.getInstance()) {
				return TopConcept.getInstance();
				
			} else if (operand == TopRole.getInstance()) {
				return BottomRole.getInstance();
				
			} else if (operand == BottomRole.getInstance()) {
				return TopRole.getInstance();
				
			} else if (operand instanceof Negation) {
				return removeDoubleNegations(operand.getSubFormulas().get(0));

			} else {
				formula.getSubFormulas().set(0, removeDoubleNegations(operand));
				return formula;
			}

		} else if (formula instanceof GreaterThan || formula instanceof LessThan) {
			formula.getSubFormulas().set(0, removeDoubleNegations(formula.getSubFormulas().get(0)));
			formula.getSubFormulas().set(1, removeDoubleNegations(formula.getSubFormulas().get(1)));
			return formula;

		} else if (formula instanceof And || formula instanceof Or) {
			for (int i = 0; i < formula.getSubFormulas().size(); i++) {
				formula.getSubFormulas().set(i, removeDoubleNegations(formula.getSubFormulas().get(i)));
			}
			return formula;
		}

		return formula;
	}

	
	public Formula getNNF(Formula formula) {

		if (formula instanceof Negation) {
			Formula operand = formula.getSubFormulas().get(0);

			if (operand == TopConcept.getInstance()) {
				return BottomConcept.getInstance();
				
			} else if (operand == BottomConcept.getInstance()) {
				return TopConcept.getInstance();
				
			} else if (operand == TopRole.getInstance()) {
				return BottomRole.getInstance();
				
			} else if (operand == BottomRole.getInstance()) {
				return TopRole.getInstance();
				
			} else if (operand instanceof Negation) {
				return getNNF(operand.getSubFormulas().get(0));
				
			} else if (operand instanceof GreaterThan) {
				return new LessThan(operand.getNumber() - 1, getNNF(operand.getSubFormulas().get(0)),
						getNNF(operand.getSubFormulas().get(1)));
				
			} else if (operand instanceof LessThan) {
				return new GreaterThan(operand.getNumber() + 1, getNNF(operand.getSubFormulas().get(0)),
						getNNF(operand.getSubFormulas().get(1)));
				
			} else if (operand instanceof And) {
				List<Formula> conjunct_list = operand.getSubFormulas();
				List<Formula> new_conjunct_list = new ArrayList<>();
				for (Formula conjunct : conjunct_list) {
					new_conjunct_list.add(getNNF(new Negation(conjunct)));
				}
				return new Or(new_conjunct_list);
				
			} else if (operand instanceof Or) {
				List<Formula> disjunct_list = operand.getSubFormulas();
				List<Formula> new_disjunct_list = new ArrayList<>();
				for (Formula disjunct : disjunct_list) {
					new_disjunct_list.add(getNNF(new Negation(disjunct)));
				}
				return new And(new_disjunct_list);
				
			} else {
				return formula;
			}

		} else if (formula instanceof GreaterThan || formula instanceof LessThan) {
			formula.getSubFormulas().set(0, getNNF(formula.getSubFormulas().get(0)));
			formula.getSubFormulas().set(1, getNNF(formula.getSubFormulas().get(1)));
			return formula;

		} else if (formula instanceof And || formula instanceof Or) {
			for (int i = 0; i < formula.getSubFormulas().size(); i++) {
				formula.getSubFormulas().set(i, getNNF(formula.getSubFormulas().get(i)));
			}
			return formula;
		}

		return formula;
	}	
	
	public List<Formula> getCNF(List<Formula> input_list) {

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			output_list.addAll(getCNF(formula));
		}

		return output_list;
	}

	public List<Formula> getCNF(Formula formula) {

		EChecker ec = new EChecker();
		
		if (ec.isAndInOr(formula)) {
			List<List<Formula>> list_list = new ArrayList<>();
			List<Formula> disjunct_list = formula.getSubFormulas();
			for (int i = 0; i < disjunct_list.size(); i++) {
				list_list.add(i, new ArrayList<>());
				if (disjunct_list.get(i) instanceof And) {
					list_list.get(i).addAll(disjunct_list.get(i).getSubFormulas());
				} else {
					list_list.get(i).add(disjunct_list.get(i));
				}
			}

			List<Formula> output_list = new ArrayList<>();
			List<List<Formula>> cp_list = Lists.cartesianProduct(list_list);
			
			for (List<Formula> list : cp_list) {
				output_list.add(new Or(list));
			}

			return output_list;		
		}		
		
		return Collections.singletonList(formula);
	}
			

	public List<Formula> getClauses(List<Formula> input_list) {

		List<Formula> output_list = new ArrayList<>();

		for (Formula axiom : input_list) {
			output_list.add(getClause(axiom));
		}
		return output_list;
	}

	private Formula getClause(Formula formula) {

		List<Formula> disjunct_list = new ArrayList<>();
		
		Formula subsumee = formula.getSubFormulas().get(0);
		Formula subsumer = formula.getSubFormulas().get(1);
		
		disjunct_list.add(new Negation(subsumee));
		disjunct_list.add(subsumer);

		Formula clause = new Or(disjunct_list);
		
		return clause;
	}
	
}

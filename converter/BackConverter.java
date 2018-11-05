package converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import checkexistence.EChecker;
import concepts.AtomicConcept;
import concepts.BottomConcept;
import concepts.TopConcept;
import connectives.And;
import connectives.GreaterThan;
import connectives.Inclusion;
import connectives.Inverse;
import connectives.LessThan;
import connectives.Negation;
import connectives.Or;
import formula.Formula;
import individual.Individual;
import inferencing.Inferencer;
import roles.AtomicRole;
import roles.BottomRole;
import roles.TopRole;

public class BackConverter {

	public BackConverter() {

	}

	private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private OWLDataFactory factory = manager.getOWLDataFactory();
	
	public List<Formula> toAxioms(List<Formula> input_list) throws CloneNotSupportedException {
				
		List<Formula> output_list = new ArrayList<>();
		for (Formula clause : input_list) {
			if (clause == BottomConcept.getInstance() || clause == BottomRole.getInstance()) {						
				return Collections.singletonList(toAxiom(BottomConcept.getInstance()));	
				
			} else if (clause != TopConcept.getInstance() && clause != TopRole.getInstance()) {
				Formula axiom = toAxiom(clause);
				output_list.add(axiom);
			}
		}
		
		if (output_list.isEmpty()) {			
			output_list.add(toAxiom(TopConcept.getInstance()));
		}
				
		Set<Formula> output_set = new HashSet<>(output_list);
		output_list.clear();
		output_list.addAll(output_set);
		
		return output_list;
	}
	
	public List<Formula> toAxiomsEnd(List<Formula> input_list) throws CloneNotSupportedException {
		
		List<Formula> output_list = new ArrayList<>();
		for (Formula clause : input_list) {
			if (clause == BottomConcept.getInstance() || clause == BottomRole.getInstance()) {			
				System.out.println("===================================================");
				System.out.println("Forgetting Successful!");
				System.out.println("===================================================");							
				return Collections.singletonList(toAxiom(BottomConcept.getInstance()));
				
			}
			if (clause != TopConcept.getInstance() && clause != TopRole.getInstance()) {
				Formula axiom = toAxiom(clause);
				output_list.add(axiom);
			}
		}
		
		if (output_list.isEmpty()) {			
			output_list.add(toAxiom(TopConcept.getInstance()));
		}
		
		if (Inferencer.definer_set.isEmpty() || output_list.isEmpty()) {
			System.out.println("===================================================");
			System.out.println("Forgetting Successful!");
			System.out.println("===================================================");
		} else {
			System.out.println("===================================================");
			System.out.println("Forgetting Unsuccessful!");
			System.out.println("===================================================");
			System.out.println("The remaining definer names are: " + Inferencer.definer_set);
			System.out.println("No. of remaining definer names: " + Inferencer.definer_set.size());
			System.out.println("===================================================");
		}
		
		Set<Formula> output_set = new HashSet<>(output_list);
		output_list.clear();
		output_list.addAll(output_set);
		
		return output_list;
	}
	
	public Formula toPreAxioms(Formula formula) throws CloneNotSupportedException {

		while (!(formula.equals(toPreAxiom(formula)))) {
			formula = toPreAxiom(formula);
		}
		
		return formula;
	}
	
	private Formula toPreAxiom(final Formula input) throws CloneNotSupportedException {
		
		Formula formula = input.clone();
		
		EChecker ec = new EChecker();

		if (formula instanceof Negation) {
			formula.getSubFormulas().set(0, toPreAxiom(formula.getSubFormulas().get(0)));
			return formula;

		} else if (formula instanceof GreaterThan) {
			if (formula.getSubFormulas().get(1) instanceof Negation) {
				return new Negation(new LessThan(formula.getNumber() - 1, formula.getSubFormulas().get(0),
						toPreAxiom(formula.getSubFormulas().get(1).getSubFormulas().get(0))));
			} else {
				formula.getSubFormulas().set(1, toPreAxiom(formula.getSubFormulas().get(1)));
				return formula;
			}

		} else if (formula instanceof LessThan) {
			if (formula.getSubFormulas().get(1) instanceof Negation) {
				return new Negation(new GreaterThan(formula.getNumber() + 1, formula.getSubFormulas().get(0),
						toPreAxiom(formula.getSubFormulas().get(1).getSubFormulas().get(0))));
			} else {
				formula.getSubFormulas().set(1, toPreAxiom(formula.getSubFormulas().get(1)));
				return formula;
			}

		} else if (formula instanceof And) {
						
			if (ec.allNegationsInside(formula)) {
				
				List<Formula> conjunct_list = formula.getSubFormulas();
				List<Formula> new_conjunct_list = new ArrayList<>();
				for (Formula conjunct : conjunct_list) {
					new_conjunct_list.add(conjunct.getSubFormulas().get(0));
				}				
				return new Negation(toPreAxiom(new Or(new_conjunct_list)));
				
			} else {
				
				List<Formula> conjunct_list = formula.getSubFormulas();
				List<Formula> new_conjunct_list = new ArrayList<>();
				for (Formula conjunct : conjunct_list) {
					new_conjunct_list.add(toPreAxiom(conjunct));
				}
				return new And(new_conjunct_list);				
			}
			
		} else if (formula instanceof Or) {
			
			if (ec.allNegationsInside(formula)) {
				
				List<Formula> disjunct_list = formula.getSubFormulas();
				List<Formula> new_disjunct_list = new ArrayList<>();
				for (Formula disjunct : disjunct_list) {
					new_disjunct_list.add(disjunct.getSubFormulas().get(0));
				}				
				return new Negation(toPreAxiom(new And(new_disjunct_list)));
				
			} else {
				
				List<Formula> disjunct_list = formula.getSubFormulas();
				List<Formula> new_disjunct_list = new ArrayList<>();
				for (Formula disjunct : disjunct_list) {
					new_disjunct_list.add(toPreAxiom(disjunct));
				}
				return new Or(new_disjunct_list);				
			}
		}

		return formula;
	}
	
	private Formula toAxiom(Formula formula) {
		
		if (formula instanceof Inclusion) {
			return formula;
			
		} else if (formula instanceof Or) {

			EChecker ec = new EChecker();
			if (ec.hasRole(formula) && !ec.hasRoleRestriction(formula)) {

				List<Formula> negative_list = new ArrayList<>();
				List<Formula> positive_list = new ArrayList<>();
				List<Formula> disjunct_list = formula.getSubFormulas();
				for (Formula disjunct : disjunct_list) {
					if (disjunct instanceof Negation) {
						negative_list.add(disjunct.getSubFormulas().get(0));
					} else {
						positive_list.add(disjunct);
					}
				}

				Formula lefthand = null;
				if (negative_list.isEmpty()) {
					lefthand = TopRole.getInstance();
				} else if (negative_list.size() == 1) {
					lefthand = negative_list.get(0);
				} else {
					lefthand = new And(negative_list);
				}

				Formula righthand = null;
				if (positive_list.isEmpty()) {
					righthand = BottomRole.getInstance();
				} else if (positive_list.size() == 1) {
					righthand = positive_list.get(0);
				} else {
					righthand = new Or(positive_list);
				}
				Formula axiom = new Inclusion(lefthand, righthand);
				return axiom;

			} else {

				List<Formula> negative_list = new ArrayList<>();
				List<Formula> positive_list = new ArrayList<>();
				List<Formula> disjunct_list = formula.getSubFormulas();
				for (Formula disjunct : disjunct_list) {
					if (disjunct instanceof Negation) {
						negative_list.add(disjunct.getSubFormulas().get(0));
					} else {
						positive_list.add(disjunct);
					}
				}

				Formula lefthand = null;
				if (negative_list.isEmpty()) {
					lefthand = TopConcept.getInstance();
				} else if (negative_list.size() == 1) {
					lefthand = negative_list.get(0);
				} else {
					lefthand = new And(negative_list);
				}

				Formula righthand = null;
				if (positive_list.isEmpty()) {
					righthand = BottomConcept.getInstance();
				} else if (positive_list.size() == 1) {
					righthand = positive_list.get(0);
				} else {
					righthand = new Or(positive_list);
				}
				Formula axiom = new Inclusion(lefthand, righthand);
				return axiom;
			}

		} else if (formula instanceof Negation) {

			if (formula.getSubFormulas().get(0) instanceof AtomicRole) {
				Formula axiom = new Inclusion(formula.getSubFormulas().get(0), BottomRole.getInstance());
				return axiom;
			} else {
				Formula axiom = new Inclusion(formula.getSubFormulas().get(0), BottomConcept.getInstance());
				return axiom;
			}

		} else if (formula instanceof AtomicRole) {
			Formula axiom = new Inclusion(TopRole.getInstance(), formula);
			return axiom;
			
		} else {
			Formula axiom = new Inclusion(TopConcept.getInstance(), formula);
			return axiom;
		}

	}	

	public OWLOntology toOWLOntology(List<Formula> formula_list) throws OWLOntologyCreationException {

		OWLOntology ontology = manager.createOntology();

		for (Formula formula : formula_list) {
			manager.addAxiom(ontology, toOWLAxiom(formula));
		}

		return ontology;
	}
	
	public Set<OWLAxiom> toOWLAxioms(List<Formula> formula_list) {

		Set<OWLAxiom> output_set = new HashSet<>();
		
		for (Formula formula : formula_list) {
			output_set.add(toOWLAxiom(formula));
		}

		return output_set;
	}

	public OWLAxiom toOWLAxiom(Formula formula) {
		
		EChecker ec = new EChecker();
		
		if (ec.hasRole(formula) && !ec.hasRoleRestriction(formula)) {
			return factory.getOWLSubObjectPropertyOfAxiom(toOWLObjectPropertyExpression(formula.getSubFormulas().get(0)),
					toOWLObjectPropertyExpression(formula.getSubFormulas().get(1)));
		} else {
			return factory.getOWLSubClassOfAxiom(toOWLClassExpression(formula.getSubFormulas().get(0)),
					toOWLClassExpression(formula.getSubFormulas().get(1)));
		}
		
	}
	
	public OWLClassExpression toOWLClassExpression(Formula formula) {

		if (formula == TopConcept.getInstance()) {
			return factory.getOWLThing();
		} else if (formula == BottomConcept.getInstance()) {
			return factory.getOWLNothing();
		} else if (formula instanceof AtomicConcept) {
			OWLClass owlClass = factory.getOWLClass(IRI.create(formula.getText()));
			return owlClass;
		} else if (formula instanceof Negation) {
			return factory.getOWLObjectComplementOf(toOWLClassExpression(formula.getSubFormulas().get(0)));
		} else if (formula instanceof GreaterThan) {
			return factory.getOWLObjectMinCardinality(formula.getNumber(),
					toOWLObjectPropertyExpression(formula.getSubFormulas().get(0)),
					toOWLClassExpression(formula.getSubFormulas().get(1)));
		} else if (formula instanceof LessThan) {
			return factory.getOWLObjectMaxCardinality(formula.getNumber(),
					toOWLObjectPropertyExpression(formula.getSubFormulas().get(0)),
					toOWLClassExpression(formula.getSubFormulas().get(1)));
		} else if (formula instanceof And) {
			Set<OWLClassExpression> conjunct_set = new HashSet<>();
			List<Formula> conjunct_list = formula.getSubFormulas();
			for (Formula conjunct : conjunct_list) {
				conjunct_set.add(toOWLClassExpression(conjunct));
			}
			return factory.getOWLObjectIntersectionOf(conjunct_set);
		} else if (formula instanceof Or) {
			Set<OWLClassExpression> disjunct_set = new HashSet<>();
			List<Formula> disjunct_list = formula.getSubFormulas();
			for (Formula disjunct : disjunct_list) {
				disjunct_set.add(toOWLClassExpression(disjunct));
			}
			return factory.getOWLObjectUnionOf(disjunct_set);
		}

		assert false : "Unsupported ClassExpression: " + formula;
		return null;
	}

	public OWLObjectPropertyExpression toOWLObjectPropertyExpression(Formula role) {

		if (role == TopRole.getInstance()) {
			return factory.getOWLTopObjectProperty();
		} else if (role == BottomRole.getInstance()) {
			return factory.getOWLBottomObjectProperty();
		} else if (role instanceof AtomicRole) {
		    return factory.getOWLObjectProperty(IRI.create(role.getText()));
		} else if (role instanceof Inverse) {
			return factory.getOWLObjectInverseOf(
					toOWLObjectPropertyExpression(role.getSubFormulas().get(0)));
		}

		assert false : "Unsupported ObjectPropertyExpression: " + role;
		return null;
	}
	
	public OWLNamedIndividual toOWLNamedIndividual(Formula Indi) {

		if (Indi instanceof Individual) {
			return factory.getOWLNamedIndividual(IRI.create(Indi.getText()));
		}

		assert false : "Unsupported ObjectPropertyExpression: " + Indi;
		return null;
	}

}

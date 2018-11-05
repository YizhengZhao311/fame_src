/*
1 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import concepts.AtomicConcept;
import concepts.BottomConcept;
import concepts.TopConcept;
import connectives.And;
import connectives.GreaterThan;
import connectives.Inclusion;
import connectives.LessThan;
import connectives.Negation;
import connectives.Or;
import formula.Formula;
import roles.AtomicRole;
import roles.BottomRole;
import roles.RoleExpression;
import roles.TopRole;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

/**
 *
 * @author Yizheng
 */
public class Converter {
		
	public AtomicConcept getConceptfromClass(OWLClass owlClass) {		
		return new AtomicConcept(owlClass.getIRI().toString());
	}
	
	public AtomicRole getRoleFromObjectProperty(OWLObjectProperty owlObjectProperty) {		
		return new AtomicRole(owlObjectProperty.getIRI().toString());
	}
	
	public Set<AtomicConcept> getConceptsfromClasses(Set<OWLClass> class_set) {

		Set<AtomicConcept> concept_set = new HashSet<>();

		for (OWLClass owlClass : class_set) {
			concept_set.add(getConceptfromClass(owlClass));
		}

		return concept_set;
	}
		
	public Set<AtomicRole> getRolesfromObjectProperties(Set<OWLObjectProperty> op_set) {

		Set<AtomicRole> role_set = new HashSet<>();

		for (OWLObjectProperty owlObjectProperty : op_set) {
			role_set.add(getRoleFromObjectProperty(owlObjectProperty));
		}

		return role_set;
	}
			
	/*
	public List<AtomicConcept> getConceptsfromClasses(Set<OWLClass> class_set) {

		List<AtomicConcept> concept_list = new ArrayList<>();

		for (OWLClass owlClass : class_set) {
			concept_list.add(getConceptfromClass(owlClass));
		}

		return concept_list;
	}
		
	public List<AtomicRole> getRolesfromObjectProperties(Set<OWLObjectProperty> op_set) {

		List<AtomicRole> role_list = new ArrayList<>();

		for (OWLObjectProperty owlRole : op_set) {
			role_list.add(getRoleFromObjectProperty(owlRole));
		}

		return role_list;
	}*/
					
	public List<AtomicConcept> getConceptsInSignature(OWLOntology ontology) {

		List<AtomicConcept> concept_list = new ArrayList<>();
		Set<OWLClass> class_set = ontology.getClassesInSignature();

		for (OWLClass owlClass : class_set) {
			concept_list.add(getConceptfromClass(owlClass));
		}

		return concept_list;
	}
	
	public List<AtomicRole> getRolesInSignature(OWLOntology ontology) {

		List<AtomicRole> role_list = new ArrayList<>();
		Set<OWLObjectProperty> op_set = ontology.getObjectPropertiesInSignature();

		for (OWLObjectProperty owlRole : op_set) {
			role_list.add(getRoleFromObjectProperty(owlRole));
		}

		return role_list;
	}
	
	public List<Formula> OntologyConverter(OWLOntology ontology) {

		List<Formula> formula_list = new ArrayList<>();		
		Set<OWLLogicalAxiom> owlAxiom_set = ontology.getLogicalAxioms();

		for (OWLLogicalAxiom owlAxiom : owlAxiom_set) {
			formula_list.addAll(AxiomConverter(owlAxiom));
		}

		return formula_list;
	}
	
	public List<Formula> AxiomsConverter(Set<OWLAxiom> owlAxiom_set) {

		List<Formula> formula_list = new ArrayList<>();

		for (OWLAxiom owlAxiom : owlAxiom_set) {
			formula_list.addAll(AxiomConverter(owlAxiom));
		}

		return formula_list;
	}
		
	public List<Formula> AxiomConverter(OWLAxiom axiom) {
		
		
		if (axiom instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom owlSCOA = (OWLSubClassOfAxiom) axiom;
			Formula converted = new Inclusion(ClassExpressionConverter(owlSCOA.getSubClass()),
					ClassExpressionConverter(owlSCOA.getSuperClass()));
			return Collections.singletonList(converted);

		} else if (axiom instanceof OWLEquivalentClassesAxiom) {
			OWLEquivalentClassesAxiom owlECA = (OWLEquivalentClassesAxiom) axiom;
			Set<OWLSubClassOfAxiom> owlSubClassOfAxioms = owlECA.asOWLSubClassOfAxioms();
			List<Formula> converted = new ArrayList<>();
			for (OWLSubClassOfAxiom owlSCOA : owlSubClassOfAxioms) {
				converted.addAll(AxiomConverter(owlSCOA));
			}
			return converted;

		} else if (axiom instanceof OWLDisjointClassesAxiom) {
			OWLDisjointClassesAxiom owlDCA = (OWLDisjointClassesAxiom) axiom;
			Set<OWLSubClassOfAxiom> owlSubClassOfAxioms = owlDCA.asOWLSubClassOfAxioms();
			List<Formula> converted = new ArrayList<>();
			for (OWLSubClassOfAxiom owlSCOA : owlSubClassOfAxioms) {
				converted.addAll(AxiomConverter(owlSCOA));
			}
			return converted;

		} else if (axiom instanceof OWLDisjointUnionAxiom) {
			OWLDisjointUnionAxiom owlDUA = (OWLDisjointUnionAxiom) axiom;
			OWLEquivalentClassesAxiom owlECA = owlDUA.getOWLEquivalentClassesAxiom();
			OWLDisjointClassesAxiom owlDCA = owlDUA.getOWLDisjointClassesAxiom();
			List<Formula> converted = new ArrayList<>();
			converted.addAll(AxiomConverter(owlECA));
			converted.addAll(AxiomConverter(owlDCA));
			return converted;

		} else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
			OWLObjectPropertyDomainAxiom owlOPDA = (OWLObjectPropertyDomainAxiom) axiom;
			OWLSubClassOfAxiom owlSCOA = owlOPDA.asOWLSubClassOfAxiom();
			return AxiomConverter(owlSCOA);

		} else if (axiom instanceof OWLObjectPropertyRangeAxiom) {
			OWLObjectPropertyRangeAxiom owlOPRA = (OWLObjectPropertyRangeAxiom) axiom;
			OWLSubClassOfAxiom owlSCOA = owlOPRA.asOWLSubClassOfAxiom();
			return AxiomConverter(owlSCOA);

		} 
		
		/*if (axiom instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom owlSCOA = (OWLSubClassOfAxiom) axiom;
			Formula converted = new Inclusion(ClassExpressionConverter(owlSCOA.getSubClass()),
					ClassExpressionConverter(owlSCOA.getSuperClass()));
			return Collections.singletonList(converted);

		} else if (axiom instanceof OWLEquivalentClassesAxiom) {
			OWLEquivalentClassesAxiom owlECA = (OWLEquivalentClassesAxiom) axiom;
			Set<OWLSubClassOfAxiom> owlSubClassOfAxioms = owlECA.asOWLSubClassOfAxioms();
			List<Formula> converted = new ArrayList<>();
			for (OWLSubClassOfAxiom owlSCOA : owlSubClassOfAxioms) {
				converted.addAll(AxiomConverter(owlSCOA));
			}
			return converted;

		} else if (axiom instanceof OWLDisjointClassesAxiom) {
			OWLDisjointClassesAxiom owlDCA = (OWLDisjointClassesAxiom) axiom;
			Set<OWLSubClassOfAxiom> owlSubClassOfAxioms = owlDCA.asOWLSubClassOfAxioms();
			List<Formula> converted = new ArrayList<>();
			for (OWLSubClassOfAxiom owlSCOA : owlSubClassOfAxioms) {
				converted.addAll(AxiomConverter(owlSCOA));
			}
			return converted;

		} else if (axiom instanceof OWLDisjointUnionAxiom) {
			OWLDisjointUnionAxiom owlDUA = (OWLDisjointUnionAxiom) axiom;
			OWLEquivalentClassesAxiom owlECA = owlDUA.getOWLEquivalentClassesAxiom();
			OWLDisjointClassesAxiom owlDCA = owlDUA.getOWLDisjointClassesAxiom();
			List<Formula> converted = new ArrayList<>();
			converted.addAll(AxiomConverter(owlECA));
			converted.addAll(AxiomConverter(owlDCA));
			return converted;

		} else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
			OWLObjectPropertyDomainAxiom owlOPDA = (OWLObjectPropertyDomainAxiom) axiom;
			OWLSubClassOfAxiom owlSCOA = owlOPDA.asOWLSubClassOfAxiom();
			return AxiomConverter(owlSCOA);

		} else if (axiom instanceof OWLObjectPropertyRangeAxiom) {
			OWLObjectPropertyRangeAxiom owlOPRA = (OWLObjectPropertyRangeAxiom) axiom;
			OWLSubClassOfAxiom owlSCOA = owlOPRA.asOWLSubClassOfAxiom();
			return AxiomConverter(owlSCOA);

		} else if (axiom instanceof OWLSubObjectPropertyOfAxiom) {
			OWLSubObjectPropertyOfAxiom owlSOPOA = (OWLSubObjectPropertyOfAxiom) axiom;
			Formula converted = new Inclusion(RoleExpressionConverter(owlSOPOA.getSubProperty()),
					RoleExpressionConverter(owlSOPOA.getSuperProperty()));
			return Collections.singletonList(converted);

		} else if (axiom instanceof OWLEquivalentObjectPropertiesAxiom) {
			OWLEquivalentObjectPropertiesAxiom owlEOPA = (OWLEquivalentObjectPropertiesAxiom) axiom;
			Set<OWLSubObjectPropertyOfAxiom> owlSOPOAs = owlEOPA.asSubObjectPropertyOfAxioms();
			List<Formula> converted = new ArrayList<>();
			for (OWLSubObjectPropertyOfAxiom owlSOPOA : owlSOPOAs) {
				converted.addAll(AxiomConverter(owlSOPOA));
			}
			return converted;
			
		} else if (axiom instanceof OWLFunctionalObjectPropertyAxiom) {
			OWLFunctionalObjectPropertyAxiom owlFOPA = (OWLFunctionalObjectPropertyAxiom) axiom;
			OWLSubClassOfAxiom owlSCOA = owlFOPA.asOWLSubClassOfAxiom();
			return AxiomConverter(owlSCOA);
			
		} else if (axiom instanceof OWLClassAssertionAxiom) {
			OWLClassAssertionAxiom owlCAA = (OWLClassAssertionAxiom) axiom;
			OWLSubClassOfAxiom owlSCOA = owlCAA.asOWLSubClassOfAxiom();
			return AxiomConverter(owlSCOA);
			
		} else if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
			OWLObjectPropertyAssertionAxiom owlOPAA = (OWLObjectPropertyAssertionAxiom) axiom;
			OWLSubClassOfAxiom owlSCOA = owlOPAA.asOWLSubClassOfAxiom();
			return AxiomConverter(owlSCOA);	
			
		} else if (axiom instanceof OWLClassAssertionAxiom) {
			OWLClassAssertionAxiom owlCAA = (OWLClassAssertionAxiom) axiom;
			Formula converted = new Inclusion(IndividualConverter(owlCAA.getIndividual()),
					ClassExpressionConverter(owlCAA.getClassExpression()));
			return Collections.singletonList(converted);

		} else if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
			OWLObjectPropertyAssertionAxiom owlOPAA = (OWLObjectPropertyAssertionAxiom) axiom;
			Formula converted = new Inclusion(IndividualConverter(owlOPAA.getSubject()),
					new GreaterThan(1, RoleExpressionConverter(owlOPAA.getProperty()),
							IndividualConverter(owlOPAA.getObject())));
			return Collections.singletonList(converted);
		}*/

		return Collections.emptyList();
	}

	private Formula ClassExpressionConverter(OWLClassExpression concept) {

		if (concept.isTopEntity()) {
			return TopConcept.getInstance();

		} else if (concept.isBottomEntity()) {
			return BottomConcept.getInstance();

		} else if (concept instanceof OWLClass) {
			OWLClass owlClass = (OWLClass) concept;
			return new AtomicConcept(owlClass.getIRI().toString());

		} else if (concept instanceof OWLObjectComplementOf) {
			OWLObjectComplementOf owlOCO = (OWLObjectComplementOf) concept;
			return new Negation(ClassExpressionConverter(owlOCO.getOperand()));

		} else if (concept instanceof OWLObjectSomeValuesFrom) {
			OWLObjectSomeValuesFrom owlOSVF = (OWLObjectSomeValuesFrom) concept;
			return new GreaterThan(1, RoleExpressionConverter(owlOSVF.getProperty()),
					ClassExpressionConverter(owlOSVF.getFiller()));

		} else if (concept instanceof OWLObjectAllValuesFrom) {
			OWLObjectAllValuesFrom owlOAVF = (OWLObjectAllValuesFrom) concept;
			return new LessThan(0, RoleExpressionConverter(owlOAVF.getProperty()),
					new Negation(ClassExpressionConverter(owlOAVF.getFiller())));

		} else if (concept instanceof OWLObjectMaxCardinality) {
			OWLObjectMaxCardinality owlOMaxC = (OWLObjectMaxCardinality) concept;
			return new GreaterThan(owlOMaxC.getCardinality(), RoleExpressionConverter(owlOMaxC.getProperty()),
					ClassExpressionConverter(owlOMaxC.getFiller()));
			
		} else if (concept instanceof OWLObjectMinCardinality) {
			OWLObjectMinCardinality owlOMinC = (OWLObjectMinCardinality) concept;
			return new LessThan(owlOMinC.getCardinality(), RoleExpressionConverter(owlOMinC.getProperty()),
					ClassExpressionConverter(owlOMinC.getFiller()));
			
		} else if (concept instanceof OWLObjectExactCardinality) {
			OWLObjectExactCardinality owlOExactC = (OWLObjectExactCardinality) concept;
			OWLClassExpression owlCE = owlOExactC.asIntersectionOfMinMax();
			return ClassExpressionConverter(owlCE);
			
		} else if (concept instanceof OWLObjectIntersectionOf) {
			OWLObjectIntersectionOf owlOIO = (OWLObjectIntersectionOf) concept;
			List<Formula> conjunctList = new ArrayList<>();
			for (OWLClassExpression conjunct : owlOIO.getOperands()) {
				conjunctList.add(ClassExpressionConverter(conjunct));
			}
			return new And(conjunctList);

		} else if (concept instanceof OWLObjectUnionOf) {
			OWLObjectUnionOf owlOUO = (OWLObjectUnionOf) concept;
			List<Formula> disjunctList = new ArrayList<>();
			for (OWLClassExpression disjunct : owlOUO.getOperands()) {
				disjunctList.add(ClassExpressionConverter(disjunct));
			}
			return new Or(disjunctList);
		}

		return TopConcept.getInstance();
	}
	
	private RoleExpression RoleExpressionConverter(OWLObjectPropertyExpression role) {

		if (role instanceof OWLObjectProperty) {
			OWLObjectProperty owlOP = (OWLObjectProperty) role;
			return new AtomicRole(owlOP.getIRI().toString());
			
		} else if (role.isOWLTopObjectProperty()) {
			return TopRole.getInstance();
			
		} else if (role.isOWLBottomObjectProperty()) {
			return BottomRole.getInstance();
			
		}

		return TopRole.getInstance();
	}
	
	/*
	private ConceptExpression IndividualConverter(OWLIndividual indi) {

		if (indi instanceof OWLNamedIndividual) {
			OWLNamedIndividual owlIndi = (OWLNamedIndividual) indi;
			return new Individual(owlIndi.getIRI().getShortForm());
			
		}

		return TopConcept.getInstance();
	}*/

}

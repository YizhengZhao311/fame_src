package forgetting;

import formula.Formula;
import preprocessing.PreProcessor;
import roles.AtomicRole;
import concepts.AtomicConcept;
import converter.BackConverter;
import converter.Converter;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;


import java.util.List;
import java.util.Set;


/**
 *
 * @author Yizheng
 */
public class Fame {

	public Fame() {

	}
	
	public OWLOntology FameRC(Set<OWLObjectProperty> op_set, Set<OWLClass> c_set, OWLOntology onto)
			throws OWLOntologyCreationException, CloneNotSupportedException {
		
		if (op_set.isEmpty() && c_set.isEmpty()) {
			return onto;
		}
		
		Converter ct = new Converter();
		PreProcessor pp = new PreProcessor();
		Set<AtomicRole> r_sig = ct.getRolesfromObjectProperties(op_set);
		Set<AtomicConcept> c_sig = ct.getConceptsfromClasses(c_set);
		List<Formula> formula_list = pp.getCNF(pp.getSimplifiedForm(pp.getClauses(ct.OntologyConverter(onto))));
		
		Forgetter ft = new Forgetter();
		List<Formula> uniform_interpolant = ft.Forgetting(r_sig, c_sig, formula_list);
		
		BackConverter bc = new BackConverter();
		OWLOntology result_onto = bc.toOWLOntology(uniform_interpolant);
				
		return result_onto;
	}
		
	public List<Formula> FameRC(Set<AtomicRole> r_sig, Set<AtomicConcept> c_sig, List<Formula> formula_list) throws OWLOntologyCreationException, CloneNotSupportedException {

		if (r_sig.isEmpty() && c_sig.isEmpty()) {
			return formula_list;
		}
		
		Forgetter ft = new Forgetter();
		List<Formula> uniform_interpolant = ft.Forgetting(r_sig, c_sig, formula_list);
		
		return uniform_interpolant;
	}
	
}

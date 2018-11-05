package forgetting;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import checkfrequency.FChecker;
import checkreducedform.RFChecker;
import concepts.AtomicConcept;
import extractor.SubsetExtractor;
import formula.Formula;
import inferencing.Inferencer;
import preprocessing.PreProcessor;
import roles.AtomicRole;

public class Forgetter {
	
	public List<Formula> Forgetting(Set<AtomicRole> r_sig, Set<AtomicConcept> c_sig,
			List<Formula> formula_list_normalised) throws CloneNotSupportedException {

		System.out.println("The Forgetting Starts:");

		PreProcessor pp = new PreProcessor();
		SubsetExtractor se = new SubsetExtractor();
		Inferencer inf = new Inferencer();
		FChecker fc = new FChecker();

		if (!r_sig.isEmpty()) {
			//se.getRoleSubset(r_sig, formula_list_normalised);
			List<Formula> r_sig_list_normalised = se.getRoleSubset(r_sig, formula_list_normalised);
			List<Formula> pivot_list_normalised = null;
			int i = 1;
			for (AtomicRole role : r_sig) {
				System.out.println("Eliminating role [" + i + "] = " + role);
				i++;
				pivot_list_normalised = pp
						.getCNF(pp.getSimplifiedForm(se.getRoleSubset(role, r_sig_list_normalised)));
				if (pivot_list_normalised.isEmpty()) {

				} else {
					pivot_list_normalised = inf.introduceDefiners(role, pivot_list_normalised);
					pivot_list_normalised = pp
							.getCNF(pp.getSimplifiedForm(inf.Ackermann_R(role, pivot_list_normalised)));
					r_sig_list_normalised.addAll(pivot_list_normalised);
				}
			}

			formula_list_normalised.addAll(r_sig_list_normalised);
		}

		if (!c_sig.isEmpty()) {
			List<Formula> c_sig_list_normalised = se.getConceptSubset(c_sig, formula_list_normalised);
			List<Formula> pivot_list_normalised = null;
			RFChecker rfc = new RFChecker();
			/*int case_1 = 0;
			int case_2 = 0;
			int case_3 = 0;
			int case_4 = 0;
			int case_5 = 0;
			int case_6 = 0;
			int case_1_d = 0;
			int case_2_d = 0;
			int case_3_d = 0;
			int case_4_d = 0;
			int case_5_d = 0;
			int case_6_d = 0;*/
			int j = 1;
			for (AtomicConcept concept : c_sig) {
				System.out.println("Eliminating concept [" + j + "] = " + concept);
				j++;
				pivot_list_normalised = pp
						.getCNF(pp.getSimplifiedForm(se.getConceptSubset(concept, c_sig_list_normalised)));

				if (pivot_list_normalised.isEmpty()) {
					//case_1++;
				} else if (fc.negative(concept, pivot_list_normalised) == 0) {
					c_sig_list_normalised.addAll(
							pp.getCNF(pp.getSimplifiedForm(inf.PurifyPositive(concept, pivot_list_normalised))));
					//case_2++;

				} else if (fc.positive(concept, pivot_list_normalised) == 0) {
					c_sig_list_normalised.addAll(
							pp.getCNF(pp.getSimplifiedForm(inf.PurifyNegative(concept, pivot_list_normalised))));
					//case_3++;

				} else if (rfc.isAReducedFormPositive(concept, pivot_list_normalised)) {
					c_sig_list_normalised.addAll(
							pp.getCNF(pp.getSimplifiedForm(inf.AckermannPositive(concept, pivot_list_normalised))));
					//case_4++;

				} else if (rfc.isAReducedFormNegative(concept, pivot_list_normalised)) {
					c_sig_list_normalised.addAll(
							pp.getCNF(pp.getSimplifiedForm(inf.AckermannNegative(concept, pivot_list_normalised))));
					//case_5++;

				} else {
					pivot_list_normalised = pp
							.getCNF(pp.getSimplifiedForm(inf.introduceDefiners(concept, pivot_list_normalised)));
					pivot_list_normalised = pp
							.getCNF(pp.getSimplifiedForm(inf.Ackermann_A(concept, pivot_list_normalised)));
					c_sig_list_normalised.addAll(pivot_list_normalised);
					//case_6++;
				}
			}
			
			if (!Inferencer.definer_set.isEmpty()) {
				Set<AtomicConcept> definer_set = null;
				
				int k = 1;
				do {
					if (Inferencer.definer_set.isEmpty()) {
						System.out.println("Forgetting Successful!");
						/*System.out.println("===================================================");
						System.out.println("case_1 = " + case_1);
						System.out.println("case_2 = " + case_2);
						System.out.println("case_3 = " + case_3);
						System.out.println("case_4 = " + case_4);
						System.out.println("case_5 = " + case_5);
						System.out.println("case_6 = " + case_6);
						System.out.println("case_1_d = " + case_1_d);
						System.out.println("case_2_d = " + case_2_d);
						System.out.println("case_3_d = " + case_3_d);
						System.out.println("case_4_d = " + case_4_d);
						System.out.println("case_5_d = " + case_5_d);
						System.out.println("case_6_d = " + case_6_d);*/
						formula_list_normalised.addAll(c_sig_list_normalised);
						return formula_list_normalised;
					}

					definer_set = new HashSet<>(Inferencer.definer_set);
					Inferencer.definer_set.clear();
	
					for (AtomicConcept concept : definer_set) {
						System.out.println("Eliminating definer [" + k + "] = " + concept);
						k++;
						pivot_list_normalised = pp
								.getCNF(pp.getSimplifiedForm(se.getConceptSubset(concept, c_sig_list_normalised)));
						if (pivot_list_normalised.isEmpty()) {
							//case_1_d++;
							//Inferencer.definer_set.remove(concept);

						} else if (fc.negative(concept, pivot_list_normalised) == 0) {
							c_sig_list_normalised.addAll(pp
									.getCNF(pp.getSimplifiedForm(inf.PurifyPositive(concept, pivot_list_normalised))));
							//Inferencer.definer_set.remove(concept);
							//case_2_d++;

						} else if (fc.positive(concept, pivot_list_normalised) == 0) {
							c_sig_list_normalised.addAll(pp
									.getCNF(pp.getSimplifiedForm(inf.PurifyNegative(concept, pivot_list_normalised))));
							//Inferencer.definer_set.remove(concept);
							//case_3_d++;

						} else if (rfc.isAReducedFormPositive(concept, pivot_list_normalised)) {
							c_sig_list_normalised.addAll(pp.getCNF(
									pp.getSimplifiedForm(inf.AckermannPositive(concept, pivot_list_normalised))));
							//Inferencer.definer_set.remove(concept);
							//case_4_d++;

						} else if (rfc.isAReducedFormNegative(concept, pivot_list_normalised)) {
							c_sig_list_normalised.addAll(pp.getCNF(
									pp.getSimplifiedForm(inf.AckermannNegative(concept, pivot_list_normalised))));
							//Inferencer.definer_set.remove(concept);
							//case_5_d++;

						} else {
							pivot_list_normalised = inf.introduceDefiners(concept, pivot_list_normalised);
							pivot_list_normalised = pp
									.getCNF(pp.getSimplifiedForm(inf.Ackermann_A(concept, pivot_list_normalised)));
							c_sig_list_normalised.addAll(pivot_list_normalised);
							//case_6_d++;
						}
					}

				} while (definer_set.size() > Inferencer.definer_set.size());
				
				do {
					if (Inferencer.definer_set.isEmpty()) {
						System.out.println("Forgetting Successful!");
						System.out.println("===================================================");
						/*System.out.println("case_1 = " + case_1);
						System.out.println("case_2 = " + case_2);
						System.out.println("case_3 = " + case_3);
						System.out.println("case_4 = " + case_4);
						System.out.println("case_5 = " + case_5);
						System.out.println("case_6 = " + case_6);
						System.out.println("case_1_d = " + case_1_d);
						System.out.println("case_2_d = " + case_2_d);
						System.out.println("case_3_d = " + case_3_d);
						System.out.println("case_4_d = " + case_4_d);
						System.out.println("case_5_d = " + case_5_d);
						System.out.println("case_6_d = " + case_6_d);*/
						formula_list_normalised.addAll(c_sig_list_normalised);
						return formula_list_normalised;
					}

					definer_set = new HashSet<>(Inferencer.definer_set);
	
					for (AtomicConcept concept : definer_set) {
						System.out.println("Eliminating definer [" + k + "] = " + concept);
						k++;
						pivot_list_normalised = pp
								.getCNF(pp.getSimplifiedForm(se.getConceptSubset(concept, c_sig_list_normalised)));
						if (pivot_list_normalised.isEmpty()) {
							Inferencer.definer_set.remove(concept);
							//case_1_d++;

						} else if (fc.negative(concept, pivot_list_normalised) == 0) {
							c_sig_list_normalised.addAll(pp
									.getCNF(pp.getSimplifiedForm(inf.PurifyPositive(concept, pivot_list_normalised))));
							Inferencer.definer_set.remove(concept);
							//case_2_d++;

						} else if (fc.positive(concept, pivot_list_normalised) == 0) {
							c_sig_list_normalised.addAll(pp
									.getCNF(pp.getSimplifiedForm(inf.PurifyNegative(concept, pivot_list_normalised))));
							Inferencer.definer_set.remove(concept);
							//case_3_d++;

						} else if (rfc.isAReducedFormPositive(concept, pivot_list_normalised)) {
							c_sig_list_normalised.addAll(pp.getCNF(
									pp.getSimplifiedForm(inf.AckermannPositive(concept, pivot_list_normalised))));
							Inferencer.definer_set.remove(concept);
							//case_4_d++;

						} else if (rfc.isAReducedFormNegative(concept, pivot_list_normalised)) {
							c_sig_list_normalised.addAll(pp.getCNF(
									pp.getSimplifiedForm(inf.AckermannNegative(concept, pivot_list_normalised))));
							Inferencer.definer_set.remove(concept);
							//case_5_d++;

						}
					}

				} while (definer_set.size() > Inferencer.definer_set.size());

			}
			
			/*System.out.println("case_1 = " + case_1);
			System.out.println("case_2 = " + case_2);
			System.out.println("case_3 = " + case_3);
			System.out.println("case_4 = " + case_4);
			System.out.println("case_5 = " + case_5);
			System.out.println("case_6 = " + case_6);
			System.out.println("case_1_d = " + case_1_d);
			System.out.println("case_2_d = " + case_2_d);
			System.out.println("case_3_d = " + case_3_d);
			System.out.println("case_4_d = " + case_4_d);
			System.out.println("case_5_d = " + case_5_d);
			System.out.println("case_6_d = " + case_6_d);*/

			formula_list_normalised.addAll(c_sig_list_normalised);
		}
		
		return formula_list_normalised;
	}
	
	/*
	public List<Formula> Forgetting(List<AtomicRole> r_sig, List<AtomicConcept> c_sig,
			List<Formula> formula_list_normalised) throws CloneNotSupportedException {

		System.out.println("Forgetting Starts:");
		
		PreProcessor pp = new PreProcessor();
		BackConverter bc = new BackConverter();
		SubsetExtractor se = new SubsetExtractor();
		Inferencer inf = new Inferencer();
		FChecker fc = new FChecker();

		if (!r_sig.isEmpty()) {
			List<Formula> r_sig_list_normalised = se.getRoleSubset(r_sig, formula_list_normalised);
			List<Formula> pivot_list_normalised = null;
			int i = 1;
			for (AtomicRole role : r_sig) {
				System.out.println("Eliminating role[" + i + "] = " + role);
				i++;
				pivot_list_normalised = se.getRoleSubset(role, r_sig_list_normalised);
				if (pivot_list_normalised.isEmpty()) {

				} else {
					pivot_list_normalised = inf.introduceDefiners(role, pivot_list_normalised);
					pivot_list_normalised = pp
							.getCNF(pp.getSimplifiedForm(inf.Ackermann_R(role, pivot_list_normalised)));
					r_sig_list_normalised.addAll(pivot_list_normalised);
				}
			}
			
			formula_list_normalised.addAll(r_sig_list_normalised);
		}
				
		if (!c_sig.isEmpty()) {			
			List<Formula> c_sig_list_normalised = se.getConceptSubset(c_sig, formula_list_normalised);
			List<Formula> pivot_list_normalised = null;
			RFChecker rfc = new RFChecker();
			int j = 1;
			for (AtomicConcept concept : c_sig) {
				System.out.println("Eliminating concept[" + j + "] = " + concept);
				j++;
				pivot_list_normalised = se.getConceptSubset(concept, c_sig_list_normalised);
				if (pivot_list_normalised.isEmpty()) {
					
				} else if (fc.negative(concept, pivot_list_normalised) == 0) {
					for (Formula formula : pivot_list_normalised) {
						c_sig_list_normalised
								.addAll(pp.getCNF(pp.getSimplifiedForm(inf.PurifyPositive(concept, formula))));
					}
				} else if (fc.positive(concept, pivot_list_normalised) == 0) {
					for (Formula formula : pivot_list_normalised) {
						c_sig_list_normalised
								.addAll(pp.getCNF(pp.getSimplifiedForm(inf.PurifyNegative(concept, formula))));
					}
				} else if (rfc.isAReducedFormPositive(concept, pivot_list_normalised)) {
					c_sig_list_normalised.addAll(
							pp.getCNF(pp.getSimplifiedForm(inf.AckermannPositive(concept, pivot_list_normalised))));

				} else if (rfc.isAReducedFormNegative(concept, pivot_list_normalised)) {
					c_sig_list_normalised.addAll(
							pp.getCNF(pp.getSimplifiedForm(inf.AckermannNegative(concept, pivot_list_normalised))));

				} else {
					pivot_list_normalised = inf.introduceDefiners(concept, pivot_list_normalised);
					pivot_list_normalised = pp
							.getCNF(pp.getSimplifiedForm(inf.Ackermann_A(concept, pivot_list_normalised)));
					c_sig_list_normalised.addAll(pivot_list_normalised);
				}
			}			

			if (!Inferencer.definer_list.isEmpty()) {
				
				Set<AtomicConcept> definer_set = null;

				// Ackermann's Lemma
				do {
					if (Inferencer.definer_list.isEmpty()) {
						System.out.println("Forgetting Successful!");
						System.out.println("===================================================");
						formula_list_normalised.addAll(c_sig_list_normalised);
						return bc.toAxiomsEnd(formula_list_normalised);
					}

					definer_set = new HashSet<>(Inferencer.definer_list);
					
					int k = 1;
					for (AtomicConcept concept : definer_set) {
						System.out.println("Eliminating definer[" + k + "] = " + concept);
						k++;
						pivot_list_normalised = se.getConceptSubset(concept, c_sig_list_normalised);
						if (pivot_list_normalised.isEmpty()) {
							Inferencer.definer_list.remove(concept);
							
						} else if (fc.negative(concept, pivot_list_normalised) == 0) {
							c_sig_list_normalised.addAll(pp.getCNF(pp.getSimplifiedForm(inf.PurifyPositive(concept, pivot_list_normalised))));
							Inferencer.definer_list.remove(concept);
							
						} else if (fc.positive(concept, pivot_list_normalised) == 0) {
							c_sig_list_normalised.addAll(pp.getCNF(pp.getSimplifiedForm(inf.PurifyNegative(concept, pivot_list_normalised))));
							Inferencer.definer_list.remove(concept);
							
						} else if (rfc.isAReducedFormPositive(concept, pivot_list_normalised)) {
							c_sig_list_normalised
									.addAll(pp.getCNF(pp.getSimplifiedForm(inf.AckermannPositive(concept, pivot_list_normalised))));
							Inferencer.definer_list.remove(concept);
							
						} else if (rfc.isAReducedFormNegative(concept, pivot_list_normalised)) {
							c_sig_list_normalised
									.addAll(pp.getCNF(pp.getSimplifiedForm(inf.AckermannNegative(concept, pivot_list_normalised))));
							Inferencer.definer_list.remove(concept);
							
						} else {						
							c_sig_list_normalised.addAll(pivot_list_normalised);
						}
					}

				} while (definer_set.size() > Inferencer.definer_list.size());

			}
			
			formula_list_normalised.addAll(c_sig_list_normalised);
		}

		return bc.toAxiomsEnd(formula_list_normalised);
	}*/
}

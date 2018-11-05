package inferencing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import checkexistence.EChecker;
import checkfrequency.FChecker;
import concepts.AtomicConcept;
import concepts.BottomConcept;
import concepts.TopConcept;
import roles.AtomicRole;
import roles.BottomRole;
import roles.TopRole;
import connectives.And;
import connectives.GreaterThan;
import connectives.Inverse;
import connectives.LessThan;
import connectives.Negation;
import connectives.Or;
import formula.Formula;
import individual.Individual;
import preprocessing.PreProcessor;

public class Inferencer {

	public static Map<Formula, AtomicConcept> pos_definer_map = new HashMap<>();
	public static Map<Formula, AtomicConcept> neg_definer_map = new HashMap<>();
	public static Set<AtomicConcept> definer_set = new HashSet<>();

	public Inferencer() {

	}

	public List<Formula> introduceDefiners(AtomicConcept concept, List<Formula> input_list)
			throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			output_list.addAll(introduceDefiners(concept, formula));
		}

		return output_list;
	}

	private List<Formula> introduceDefiners(AtomicConcept concept, Formula formula) throws CloneNotSupportedException {

		EChecker ec = new EChecker();
		FChecker fc = new FChecker();
		PreProcessor pp = new PreProcessor();

		List<Formula> output_list = new ArrayList<>();

		if (ec.isPresent(concept, formula)) {

			if (formula instanceof GreaterThan) {
				Formula filler = formula.getSubFormulas().get(1);

				if (filler.equals(concept) || filler.equals(new Negation(concept))) {
					output_list.add(formula);

				} else {
					AtomicConcept definer = null;
					if (neg_definer_map.get(filler) == null) {
						definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
						AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
						definer_set.add(definer);
						neg_definer_map.put(filler, definer);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
						List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler)));
						for (Formula conjunct : conjunct_list) {
							List<Formula> disjunct_list = new ArrayList<>();
							disjunct_list.add(new Negation(definer));
							if (conjunct instanceof Or) {
								disjunct_list.addAll(conjunct.getSubFormulas());
							} else {
								disjunct_list.add(conjunct);	
							}
							output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
						}

					} else {
						definer = neg_definer_map.get(filler);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
					}
				}

			} else if (formula instanceof LessThan) {
				Formula filler = formula.getSubFormulas().get(1);

				if (filler.equals(concept) || filler.equals(new Negation(concept))) {
					output_list.add(formula);

				} else {
					AtomicConcept definer = null;
					if (pos_definer_map.get(filler) == null) {
						definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
						AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
						definer_set.add(definer);
						pos_definer_map.put(filler, definer);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
						List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(new Negation(filler))));
						for (Formula conjunct : conjunct_list) {
							List<Formula> disjunct_list = new ArrayList<>();
							disjunct_list.add(definer);
							if (conjunct instanceof Or) {
								disjunct_list.addAll(conjunct.getSubFormulas());
							} else {
								disjunct_list.add(conjunct);	
							}
							output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
						}

					} else {
						definer = pos_definer_map.get(filler);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
					}
				}

			} else if (formula instanceof Or) {

				List<Formula> disjuncts = formula.getSubFormulas();

				if (fc.positive(concept, formula) + fc.negative(concept, formula) == 1) {
					if (disjuncts.contains(concept) || disjuncts.contains(new Negation(concept))) {
						output_list.add(formula);

					} else {

						for (Formula disjunct : disjuncts) {
							if (ec.isPresent(concept, disjunct)) {

								if (disjunct instanceof GreaterThan) {

									Formula filler = disjunct.getSubFormulas().get(1);

									if (filler.equals(concept) || filler.equals(new Negation(concept))) {
										output_list.add(formula);
										break;

									} else {
										AtomicConcept definer = null;
										if (neg_definer_map.get(filler) == null) {
											definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
											AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
											definer_set.add(definer);
											neg_definer_map.put(filler, definer);
											disjunct.getSubFormulas().set(1, definer);
											output_list.add(formula);
											List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler)));
											for (Formula conjunct : conjunct_list) {
												List<Formula> disjunct_list = new ArrayList<>();
												disjunct_list.add(new Negation(definer));
												if (conjunct instanceof Or) {
													disjunct_list.addAll(conjunct.getSubFormulas());
												} else {
													disjunct_list.add(conjunct);	
												}
												output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
											}
											break;

										} else {
											definer = neg_definer_map.get(filler);
											disjunct.getSubFormulas().set(1, definer);
											output_list.add(formula);
											break;
										}
									}

								} else if (disjunct instanceof LessThan) {

									Formula filler = disjunct.getSubFormulas().get(1);

									if (filler.equals(concept) || filler.equals(new Negation(concept))) {
										output_list.add(formula);
										break;

									} else {
										AtomicConcept definer = null;
										if (pos_definer_map.get(filler) == null) {
											definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
											AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
											definer_set.add(definer);
											pos_definer_map.put(filler, definer);
											disjunct.getSubFormulas().set(1, definer);
											output_list.add(formula);
											List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(new Negation(filler))));
											for (Formula conjunct : conjunct_list) {
												List<Formula> disjunct_list = new ArrayList<>();
												disjunct_list.add(definer);
												if (conjunct instanceof Or) {
													disjunct_list.addAll(conjunct.getSubFormulas());
												} else {
													disjunct_list.add(conjunct);	
												}
												output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
											}
											break;

										} else {
											definer = pos_definer_map.get(filler);
											disjunct.getSubFormulas().set(1, definer);
											output_list.add(formula);
											break;
										}
									}
								}
							}
						}
					}

					// pivot occurs >=2
				} else {

					for (int i = 0; i < disjuncts.size(); i++) {
						Formula disjunct = disjuncts.get(i);

						if (ec.isPresent(concept, disjunct)) {

							if (disjunct instanceof GreaterThan) {

								if ((fc.positive(concept, formula) + fc.negative(concept, formula))
										- (fc.positive(concept, disjunct) + fc.negative(concept, disjunct)) > 0) {

									AtomicConcept definer = null;
									if (neg_definer_map.get(disjunct) == null) {
										definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										neg_definer_map.put(disjunct, definer);
										disjuncts.set(i, definer);
										List<Formula> disjunct_list = new ArrayList<>();
										disjunct_list.add(new Negation(definer));
										disjunct_list.add(disjunct);
										output_list.addAll(introduceDefiners(concept, formula));
										output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
										break;

									} else {
										definer = neg_definer_map.get(disjunct);
										disjuncts.set(i, definer);
										output_list.addAll(introduceDefiners(concept, formula));
										break;
									}

								} else {

									Formula filler = disjunct.getSubFormulas().get(1);

									AtomicConcept definer = null;
									if (neg_definer_map.get(filler) == null) {
										definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										neg_definer_map.put(filler, definer);
										disjuncts.get(i).getSubFormulas().set(1, definer);
										output_list.add(formula);
										List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler)));
										for (Formula conjunct : conjunct_list) {
											List<Formula> disjunct_list = new ArrayList<>();
											disjunct_list.add(new Negation(definer));
											if (conjunct instanceof Or) {
												disjunct_list.addAll(conjunct.getSubFormulas());
											} else {
												disjunct_list.add(conjunct);	
											}
											output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
										}
										break;

									} else {
										definer = neg_definer_map.get(filler);
										disjuncts.get(i).getSubFormulas().set(1, definer);
										output_list.add(formula);
										break;
									}
								}

							} else if (disjunct instanceof LessThan) {

								if ((fc.positive(concept, formula) + fc.negative(concept, formula))
										- (fc.positive(concept, disjunct) + fc.negative(concept, disjunct)) > 0) {

									AtomicConcept definer = null;
									if (neg_definer_map.get(disjunct) == null) {
										definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										neg_definer_map.put(disjunct, definer);
										disjuncts.set(i, definer);
										List<Formula> disjunct_list = new ArrayList<>();
										disjunct_list.add(definer);
										disjunct_list.add(disjunct);
										output_list.addAll(introduceDefiners(concept, formula));
										output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
										break;

									} else {
										definer = neg_definer_map.get(disjunct);
										disjuncts.set(i, definer);
										output_list.addAll(introduceDefiners(concept, formula));
										break;
									}

								} else {

									Formula filler = disjunct.getSubFormulas().get(1);

									AtomicConcept definer = null;
									if (pos_definer_map.get(filler) == null) {
										definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										pos_definer_map.put(filler, definer);
										disjuncts.get(i).getSubFormulas().set(1, definer);
										output_list.add(formula);
										List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(new Negation(filler))));
										for (Formula conjunct : conjunct_list) {
											List<Formula> disjunct_list = new ArrayList<>();
											disjunct_list.add(definer);
											if (conjunct instanceof Or) {
												disjunct_list.addAll(conjunct.getSubFormulas());
											} else {
												disjunct_list.add(conjunct);	
											}
											output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
										}
										break;

									} else {
										definer = pos_definer_map.get(filler);
										disjuncts.get(i).getSubFormulas().set(1, definer);
										output_list.add(formula);
										break;
									}
								}
							}
						}
					}
				}
				// formula not instanceof Exists, Forall, Or
			} else {
				output_list.add(formula);
			}
			// concept not present in formula
		} else {
			output_list.add(formula);
		}

		return output_list;
	}

	public List<Formula> introduceDefiners(AtomicRole role, List<Formula> input_list)
			throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			output_list.addAll(introduceDefiners(role, formula));
		}

		return output_list;
	}

	private List<Formula> introduceDefiners(AtomicRole role, Formula formula) throws CloneNotSupportedException {

		EChecker ec = new EChecker();
		FChecker fc = new FChecker();
		PreProcessor pp = new PreProcessor();

		List<Formula> output_list = new ArrayList<>();

		if (ec.isPresent(role, formula) && ec.hasRoleRestriction(formula)) {

			if (formula instanceof GreaterThan) {
				Formula filler = formula.getSubFormulas().get(1);
				if (ec.isPresent(role, filler)) {
					AtomicConcept definer = null;
					if (neg_definer_map.get(filler) == null) {
						definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
						AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
						definer_set.add(definer);
						neg_definer_map.put(filler, definer);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
						List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(filler));
						for (Formula conjunct : conjunct_list) {
							List<Formula> disjunct_list = new ArrayList<>();
							disjunct_list.add(new Negation(definer));
							if (conjunct instanceof Or) {
								disjunct_list.addAll(conjunct.getSubFormulas());
							} else {
								disjunct_list.add(conjunct);	
							}
							output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
						}
						
					} else {
						definer = neg_definer_map.get(filler);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
					}

				} else {
					output_list.add(formula);
				}

			} else if (formula instanceof LessThan) {
				Formula filler = formula.getSubFormulas().get(1);
				if (ec.isPresent(role, filler)) {
					AtomicConcept definer = null;
					if (pos_definer_map.get(filler) == null) {
						definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
						AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
						definer_set.add(definer);
						pos_definer_map.put(filler, definer);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
						List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(new Negation(filler)));
						for (Formula conjunct : conjunct_list) {
							List<Formula> disjunct_list = new ArrayList<>();
							disjunct_list.add(definer);
							if (conjunct instanceof Or) {
								disjunct_list.addAll(conjunct.getSubFormulas());
							} else {
								disjunct_list.add(conjunct);	
							}
							output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
						}

					} else {
						definer = pos_definer_map.get(filler);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
					}

				} else {
					output_list.add(formula);
				}

			} else if (formula instanceof Or) {

				List<Formula> disjuncts = formula.getSubFormulas();

				if (fc.positive(role, formula) + fc.negative(role, formula) == 1) {
					for (Formula disjunct : disjuncts) {
						if (ec.isPresent(role, disjunct)) {
							if (disjunct instanceof GreaterThan) {
								Formula filler = disjunct.getSubFormulas().get(1);
								if (ec.isPresent(role, filler)) {
									AtomicConcept definer = null;
									if (neg_definer_map.get(filler) == null) {
										definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										neg_definer_map.put(filler, definer);
										disjunct.getSubFormulas().set(1, definer);
										output_list.add(formula);
										List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(filler));
										for (Formula conjunct : conjunct_list) {
											List<Formula> disjunct_list = new ArrayList<>();
											disjunct_list.add(new Negation(definer));
											if (conjunct instanceof Or) {
												disjunct_list.addAll(conjunct.getSubFormulas());
											} else {
												disjunct_list.add(conjunct);	
											}
											output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
										}
										break;

									} else {
										definer = neg_definer_map.get(filler);
										disjunct.getSubFormulas().set(1, definer);
										output_list.add(formula);
										break;
									}

								} else {
									output_list.add(formula);
								}

							} else if (disjunct instanceof LessThan) {
								Formula filler = disjunct.getSubFormulas().get(1);
								if (ec.isPresent(role, filler)) {
									AtomicConcept definer = null;
									if (pos_definer_map.get(filler) == null) {
										definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										pos_definer_map.put(filler, definer);
										disjunct.getSubFormulas().set(1, definer);
										output_list.add(formula);
										List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(new Negation(filler)));
										for (Formula conjunct : conjunct_list) {
											List<Formula> disjunct_list = new ArrayList<>();
											disjunct_list.add(definer);
											if (conjunct instanceof Or) {
												disjunct_list.addAll(conjunct.getSubFormulas());
											} else {
												disjunct_list.add(conjunct);	
											}
											output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
										}
										break;

									} else {
										definer = pos_definer_map.get(filler);
										disjunct.getSubFormulas().set(1, definer);
										output_list.add(formula);
										break;
									}

								} else {
									output_list.add(formula);
								}
							}
						}
					}
					// Case: >= 2
				} else {

					for (int i = 0; i < disjuncts.size(); i++) {
						Formula disjunct = disjuncts.get(i);
						if (ec.isPresent(role, disjunct)) {
							if (disjunct instanceof GreaterThan) {
								if ((fc.positive(role, formula) + fc.negative(role, formula))
										- (fc.positive(role, disjunct) + fc.negative(role, disjunct)) > 0) {

									AtomicConcept definer = null;
									if (neg_definer_map.get(disjunct) == null) {
										definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										neg_definer_map.put(disjunct, definer);
										disjuncts.set(i, definer);
										List<Formula> disjunct_list = new ArrayList<>();
										disjunct_list.add(new Negation(definer));
										disjunct_list.add(disjunct);
										output_list.addAll(introduceDefiners(role, formula));
										output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
										break;

									} else {
										definer = neg_definer_map.get(disjunct);
										disjuncts.set(i, definer);
										output_list.addAll(introduceDefiners(role, formula));
										break;
									}

								} else {

									Formula filler = disjunct.getSubFormulas().get(1);

									AtomicConcept definer = null;
									if (neg_definer_map.get(filler) == null) {
										definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										neg_definer_map.put(filler, definer);
										disjuncts.get(i).getSubFormulas().set(1, definer);
										output_list.add(formula);								
										List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(filler));
										for (Formula conjunct : conjunct_list) {
											List<Formula> disjunct_list = new ArrayList<>();
											disjunct_list.add(new Negation(definer));
											if (conjunct instanceof Or) {
												disjunct_list.addAll(conjunct.getSubFormulas());
											} else {
												disjunct_list.add(conjunct);	
											}
											output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
										}
										break;

									} else {
										definer = neg_definer_map.get(filler);
										disjuncts.get(i).getSubFormulas().set(1, definer);
										output_list.add(formula);
										break;
									}
								}

							} else if (disjunct instanceof LessThan) {

								if ((fc.positive(role, formula) + fc.negative(role, formula))
										- (fc.positive(role, disjunct) + fc.negative(role, disjunct)) > 0) {

									AtomicConcept definer = null;
									if (neg_definer_map.get(disjunct) == null) {
										definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										neg_definer_map.put(disjunct, definer);
										disjuncts.set(i, definer);
										List<Formula> disjunct_list = new ArrayList<>();
										disjunct_list.add(definer);
										disjunct_list.add(disjunct);
										output_list.addAll(introduceDefiners(role, formula));
										output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
										break;

									} else {
										definer = neg_definer_map.get(disjunct);
										disjuncts.set(i, definer);
										output_list.addAll(introduceDefiners(role, formula));
										break;
									}

								} else {

									Formula filler = disjunct.getSubFormulas().get(1);

									AtomicConcept definer = null;
									if (pos_definer_map.get(filler) == null) {
										definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										pos_definer_map.put(filler, definer);
										disjuncts.get(i).getSubFormulas().set(1, definer);
										output_list.add(formula);										
										List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(new Negation(filler)));
										for (Formula conjunct : conjunct_list) {
											List<Formula> disjunct_list = new ArrayList<>();
											disjunct_list.add(definer);
											if (conjunct instanceof Or) {
												disjunct_list.addAll(conjunct.getSubFormulas());
											} else {
												disjunct_list.add(conjunct);	
											}
											output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
										}
										break;

									} else {
										definer = pos_definer_map.get(filler);
										disjuncts.get(i).getSubFormulas().set(1, definer);
										output_list.add(formula);
										break;
									}
								}
							}
						}
					}
				}

			} else {
				output_list.add(formula);
			}

		} else {
			output_list.add(formula);
		}

		return output_list;
	}

	public List<List<Formula>> getCombinations(List<Formula> input_list) {

		List<List<Formula>> output_list = new ArrayList<>();

		int nCnt = input_list.size();

		int nBit = (0xFFFFFFFF >>> (32 - nCnt));

		for (int i = 1; i <= nBit; i++) {
			output_list.add(new ArrayList<>());
			for (int j = 0; j < nCnt; j++) {
				if ((i << (31 - j)) >> 31 == -1) {
					output_list.get(i - 1).add(input_list.get(j));
				}
			}
		}

		return output_list;
	}

	public List<Formula> Ackermann_A(AtomicConcept concept, List<Formula> formula_list)
			throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();

		List<Formula> positive_star_premises = new ArrayList<>();
		List<Formula> positive_gthan_premises = new ArrayList<>();
		List<Formula> positive_lthan_premises = new ArrayList<>();
		List<Formula> negative_star_premises = new ArrayList<>();
		List<Formula> negative_gthan_premises = new ArrayList<>();
		List<Formula> negative_lthan_premises = new ArrayList<>();

		EChecker ec = new EChecker();

		for (Formula formula : formula_list) {

			if (!ec.isPresent(concept, formula)) {
				output_list.add(formula);

			} else if (formula.equals(concept)) {
				positive_star_premises.add(formula);

			} else if (formula.equals(new Negation(concept))) {
				negative_star_premises.add(formula);

			} else if (formula instanceof GreaterThan && formula.getSubFormulas().get(1).equals(concept)) {
				positive_gthan_premises.add(formula);

			} else if (formula instanceof GreaterThan
					&& formula.getSubFormulas().get(1).equals(new Negation(concept))) {
				negative_gthan_premises.add(formula);

			} else if (formula instanceof LessThan && formula.getSubFormulas().get(1).equals(concept)) {
				negative_lthan_premises.add(formula);

			} else if (formula instanceof LessThan && formula.getSubFormulas().get(1).equals(new Negation(concept))) {
				positive_lthan_premises.add(formula);

			} else if (formula instanceof Or) {

				List<Formula> disjunct_list = formula.getSubFormulas();

				if (disjunct_list.contains(concept)) {
					positive_star_premises.add(formula);

				} else if (disjunct_list.contains(new Negation(concept))) {
					negative_star_premises.add(formula);

				} else {
					for (Formula disjunct : disjunct_list) {
						if (disjunct instanceof GreaterThan && disjunct.getSubFormulas().get(1).equals(concept)) {
							positive_gthan_premises.add(formula);
							break;
						} else if (disjunct instanceof GreaterThan
								&& disjunct.getSubFormulas().get(1).equals(new Negation(concept))) {
							negative_gthan_premises.add(formula);
							break;
						} else if (disjunct instanceof LessThan && disjunct.getSubFormulas().get(1).equals(concept)) {
							negative_lthan_premises.add(formula);
							break;
						} else if (disjunct instanceof LessThan
								&& disjunct.getSubFormulas().get(1).equals(new Negation(concept))) {
							positive_lthan_premises.add(formula);
							break;
						}
					}
				}

			} else {
				output_list.add(formula);
			}
		}
		// System.out.println("=====================================================");
		/*
		 * System.out.println("positive_star_premises = " +
		 * positive_star_premises.size());
		 * System.out.println("positive_exists_premises = " +
		 * positive_exists_premises.size());
		 * System.out.println("positive_forall_premises = " +
		 * positive_forall_premises.size());
		 * System.out.println("negative_star_premises = " +
		 * negative_star_premises.size());
		 * System.out.println("negative_exists_premises = " +
		 * negative_exists_premises.size());
		 * System.out.println("negative_forall_premises = " +
		 * negative_forall_premises.size());
		 */
		//Block 1
		if (!negative_star_premises.isEmpty()) {

			if (negative_star_premises.contains(new Negation(concept))) {				
				Formula ns_def = BottomConcept.getInstance();
				
				if (!positive_star_premises.isEmpty()) {
					for (Formula ps_premise : positive_star_premises) {
						output_list.add(AckermannReplace(concept, ps_premise, ns_def));
					}
				}
				if (!positive_gthan_premises.isEmpty()) {
					for (Formula pg_premise : positive_gthan_premises) {
						output_list.add(AckermannReplace(concept, pg_premise, ns_def));
					}
				}
				if (!positive_lthan_premises.isEmpty()) {
					for (Formula pl_premise : positive_lthan_premises) {
						output_list.add(AckermannReplace(concept, pl_premise, ns_def));
					}
				}

			} else {

				List<Formula> and_list = new ArrayList<>();
				for (Formula ns_premise : negative_star_premises) {
					Formula ns_def = null;

					List<Formula> def_disjunct_list = new ArrayList<>(ns_premise.getSubFormulas());
					def_disjunct_list.remove(new Negation(concept));

					if (def_disjunct_list.size() == 1) {
						ns_def = def_disjunct_list.get(0);
					} else {
						ns_def = new Or(def_disjunct_list);
					}

					and_list.add(ns_def);

					if (!positive_star_premises.isEmpty()) {
						for (Formula ps_premise : positive_star_premises) {
							output_list.add(AckermannReplace(concept, ps_premise, ns_def));
						}
					}
				}

				if (!positive_gthan_premises.isEmpty() || !positive_lthan_premises.isEmpty()) {

					Formula ns_def_and = null;

					if (and_list.size() == 1) {
						ns_def_and = and_list.get(0);
					} else {
						ns_def_and = new And(and_list);
					}
					for (Formula pg_premise : positive_gthan_premises) {
						output_list.add(AckermannReplace(concept, pg_premise, ns_def_and));
					}
					for (Formula pl_premise : positive_lthan_premises) {
						output_list.add(AckermannReplace(concept, pl_premise, ns_def_and));
					}
				}
			}
		}

		//Block 2
		if (!positive_star_premises.isEmpty()) {

			if (positive_star_premises.contains(concept)) {
				
				Formula ps_def = TopConcept.getInstance();

				if (!negative_gthan_premises.isEmpty()) {
					for (Formula ng_premise : negative_gthan_premises) {
						output_list.add(AckermannReplace(concept, ng_premise, ps_def));
					}
				}
				if (!negative_lthan_premises.isEmpty()) {
					for (Formula nf_premise : negative_lthan_premises) {
						output_list.add(AckermannReplace(concept, nf_premise, ps_def));
					}
				}

			} else {

				List<Formula> or_list = new ArrayList<>();

				for (Formula ps_premise : positive_star_premises) {

					Formula ps_def = null;

					List<Formula> def_disjunct_list = new ArrayList<>(ps_premise.getSubFormulas());
					def_disjunct_list.remove(concept);

					if (def_disjunct_list.size() == 1) {
						ps_def = new Negation(def_disjunct_list.get(0));
					} else {
						ps_def = new Negation(new Or(def_disjunct_list));
					}
					or_list.add(ps_def);
				}

				if (!negative_gthan_premises.isEmpty() || !negative_lthan_premises.isEmpty()) {

					Formula ps_def_or = null;

					if (or_list.size() == 1) {
						ps_def_or = or_list.get(0);
					} else {
						ps_def_or = new Or(or_list);
					}
					for (Formula ng_premise : negative_gthan_premises) {
						output_list.add(AckermannReplace(concept, ng_premise, ps_def_or));
					}
					for (Formula nl_premise : negative_lthan_premises) {
						output_list.add(AckermannReplace(concept, nl_premise, ps_def_or));
					}
				}
			}
		}

		if (!negative_gthan_premises.isEmpty()) {

			if (!positive_gthan_premises.isEmpty()) {
				if (negative_star_premises.isEmpty()) {
					for (Formula pe_premise : positive_gthan_premises) {
						output_list.add(PurifyPositive(concept, pe_premise));
					}
				}
				if (positive_star_premises.isEmpty()) {
					for (Formula ne_premise : negative_gthan_premises) {
						output_list.add(PurifyNegative(concept, ne_premise));
					}
				}
				// outer loop
				for (Formula pg_premise : positive_gthan_premises) {
					List<Formula> pg_disjunct_list = null;
					Integer pg_number = null;
					Formula pg_role = null;
					if (pg_premise instanceof GreaterThan) {
						pg_disjunct_list = new ArrayList<>();
						pg_number = pg_premise.getNumber();
						pg_role = pg_premise.getSubFormulas().get(0);
					} else {
						pg_disjunct_list = new ArrayList<>(pg_premise.getSubFormulas());
						for (Formula pg_disjunct : pg_disjunct_list) {
							if (ec.isPresent(concept, pg_disjunct)) {
								pg_number = pg_disjunct.getNumber();
								pg_role = pg_disjunct.getSubFormulas().get(0);
								pg_disjunct_list.remove(pg_disjunct);
								break;
							}
						}
					}
					// 3
					List<Formula> role_disjunct_list = new ArrayList<>();
					if (pg_role instanceof Or) {
						role_disjunct_list.addAll(pg_role.getSubFormulas());
					} else {
						role_disjunct_list.add(pg_role);
					}
					// 4 inner loop
					for (Formula ng_premise : negative_gthan_premises) {
						if (ng_premise instanceof GreaterThan) {
							Integer ng_number = ng_premise.getNumber();
							Formula ng_role = ng_premise.getSubFormulas().get(0);
							List<Formula> E_list = new ArrayList<>();
							E_list.addAll(pg_disjunct_list);
							if (ng_role instanceof Or) {
								role_disjunct_list.addAll(ng_role.getSubFormulas());
							} else {
								role_disjunct_list.add(ng_role);
							}
							E_list.add(new GreaterThan(pg_number + ng_number, new Or(role_disjunct_list),
									TopConcept.getInstance()));
							if (E_list.size() == 1) {
								output_list.add(E_list.get(0));
							} else {
								output_list.add(new Or(E_list));
							}
							// 5
						} else {
							List<Formula> ng_disjunct_list = new ArrayList<>(ng_premise.getSubFormulas());
							for (Formula ng_disjunct : ng_disjunct_list) {
								if (ec.isPresent(concept, ng_disjunct)) {
									Integer ng_number = ng_disjunct.getNumber();
									Formula ng_role = ng_disjunct.getSubFormulas().get(0);
									List<Formula> E_list = new ArrayList<>();
									E_list.addAll(pg_disjunct_list);
									ng_disjunct_list.remove(ng_disjunct);
									E_list.addAll(ng_disjunct_list);
									if (ng_role instanceof Or) {
										for (Formula role : ng_role.getSubFormulas()) {
											role_disjunct_list.add(role);
										}
									} else {
										role_disjunct_list.add(ng_role);
									}
									E_list.add(new GreaterThan(pg_number + ng_number, new Or(role_disjunct_list),
											TopConcept.getInstance()));
									output_list.add(new Or(E_list));
									break;
								}
							}
						}
					}
				}
			}

			if (!positive_lthan_premises.isEmpty()) {
				// 1
				if (positive_star_premises.isEmpty() && positive_gthan_premises.isEmpty()) {
					for (Formula ne_premise : negative_gthan_premises) {
						output_list.add(PurifyNegative(concept, ne_premise));
					}
				}
				// 2
				for (Formula pl_premise : positive_lthan_premises) {
					List<Formula> pl_disjunct_list = null;
					Integer pl_number = null;
					Formula pl_role = null;
					if (pl_premise instanceof LessThan) {
						pl_disjunct_list = new ArrayList<>();
						pl_number = pl_premise.getNumber();
						pl_role = pl_premise.getSubFormulas().get(0);
					} else {
						pl_disjunct_list = new ArrayList<>(pl_premise.getSubFormulas());
						for (Formula pl_disjunct : pl_disjunct_list) {
							if (ec.isPresent(concept, pl_disjunct)) {
								pl_number = pl_disjunct.getNumber();
								pl_role = pl_disjunct.getSubFormulas().get(0);
								pl_disjunct_list.remove(pl_disjunct);
								break;
							}
						}
					}
					// 3
					List<Formula> role_conjunct_list = new ArrayList<>();
					if (pl_role instanceof Or) {
						for (Formula role : pl_role.getSubFormulas()) {
							role_conjunct_list.add(new Negation(role));
						}
					} else {
						role_conjunct_list.add(new Negation(pl_role));
					}
					// 4
					for (Formula ng_premise : negative_gthan_premises) {
						if (ng_premise instanceof GreaterThan) {
							Integer ng_number = ng_premise.getNumber();
							Formula ng_role = ng_premise.getSubFormulas().get(0);
							if (ng_number > pl_number) {
								List<Formula> E_list = new ArrayList<>();
								E_list.addAll(pl_disjunct_list);
								if (ng_role instanceof And) {
									role_conjunct_list.addAll(ng_role.getSubFormulas());
								} else {
									role_conjunct_list.add(ng_role);
								}
								E_list.add(new GreaterThan(ng_number - pl_number, new And(role_conjunct_list),
										TopConcept.getInstance()));
								if (E_list.size() == 1) {
									output_list.add(E_list.get(0));
								} else {
									output_list.add(new Or(E_list));
								}
							}
							// 5
						} else {
							List<Formula> ng_disjunct_list = new ArrayList<>(ng_premise.getSubFormulas());
							for (Formula ng_disjunct : ng_disjunct_list) {
								if (ec.isPresent(concept, ng_disjunct)) {
									Integer ng_number = ng_disjunct.getNumber();
									Formula ng_role = ng_disjunct.getSubFormulas().get(0);
									if (ng_number > pl_number) {
										List<Formula> E_list = new ArrayList<>();
										E_list.addAll(pl_disjunct_list);
										ng_disjunct_list.remove(ng_disjunct);
										E_list.addAll(ng_disjunct_list);
										if (ng_role instanceof Or) {
											for (Formula role : ng_role.getSubFormulas()) {
												role_conjunct_list.add(new Negation(role));
											}
										} else {
											role_conjunct_list.add(new Negation(ng_role));
										}
										E_list.add(new GreaterThan(ng_number - pl_number, new And(role_conjunct_list),
												TopConcept.getInstance()));
										output_list.add(new Or(E_list));
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		//
		if (!negative_lthan_premises.isEmpty()) {

			if (!positive_gthan_premises.isEmpty()) {
				// 1
				if (negative_star_premises.isEmpty() && negative_gthan_premises.isEmpty()) {
					for (Formula pg_premise : positive_gthan_premises) {
						output_list.add(PurifyPositive(concept, pg_premise));
					}
				}

				for (Formula pg_premise : positive_gthan_premises) {
					List<Formula> pg_disjunct_list = null;
					Integer pg_number = null;
					Formula pg_role = null;
					if (pg_premise instanceof GreaterThan) {
						pg_disjunct_list = new ArrayList<>();
						pg_number = pg_premise.getNumber();
						pg_role = pg_premise.getSubFormulas().get(0);
					} else {
						pg_disjunct_list = new ArrayList<>(pg_premise.getSubFormulas());
						for (Formula pg_disjunct : pg_disjunct_list) {
							if (ec.isPresent(concept, pg_disjunct)) {
								pg_number = pg_disjunct.getNumber();
								pg_role = pg_disjunct.getSubFormulas().get(0);
								pg_disjunct_list.remove(pg_disjunct);
								break;
							}
						}
					}
					// 3
					List<Formula> role_conjunct_list = new ArrayList<>();
					if (pg_role instanceof And) {
						role_conjunct_list.addAll(pg_role.getSubFormulas());
					} else {
						role_conjunct_list.add(pg_role);
					}
					// 4
					for (Formula nl_premise : negative_lthan_premises) {
						if (nl_premise instanceof LessThan) {
							Integer nl_number = nl_premise.getNumber();
							Formula nl_role = nl_premise.getSubFormulas().get(0);
							if (pg_number > nl_number) {
								List<Formula> E_list = new ArrayList<>();
								E_list.addAll(pg_disjunct_list);
								if (nl_role instanceof Or) {
									for (Formula role : nl_role.getSubFormulas()) {
										role_conjunct_list.add(new Negation(role));
									}
								} else {
									role_conjunct_list.add(new Negation(nl_role));
								}
								E_list.add(new GreaterThan(pg_number - nl_number, new And(role_conjunct_list),
										TopConcept.getInstance()));
								if (E_list.size() == 1) {
									output_list.add(E_list.get(0));
								} else {
									output_list.add(new Or(E_list));
								}
							}
							// 5
						} else {

							List<Formula> nl_disjunct_list = new ArrayList<>(nl_premise.getSubFormulas());
							for (Formula nl_disjunct : nl_disjunct_list) {
								if (ec.isPresent(concept, nl_disjunct)) {
									Integer nl_number = nl_disjunct.getNumber();
									Formula nl_role = nl_disjunct.getSubFormulas().get(0);
									if (pg_number > nl_number) {
										List<Formula> E_list = new ArrayList<>();
										E_list.addAll(pg_disjunct_list);
										nl_disjunct_list.remove(nl_disjunct);
										E_list.addAll(nl_disjunct_list);
										if (nl_role instanceof Or) {
											for (Formula role : nl_role.getSubFormulas()) {
												role_conjunct_list.add(new Negation(role));
											}
										} else {
											role_conjunct_list.add(new Negation(nl_role));
										}
										E_list.add(new GreaterThan(pg_number - nl_number, new And(role_conjunct_list),
												TopConcept.getInstance()));
										output_list.add(new Or(E_list));
										break;
									}
								}
							}
						}
					}
				}
			}
			// Case IV
			if (!positive_lthan_premises.isEmpty()) {
				// 1
				for (Formula pl_premise : positive_lthan_premises) {

					List<Formula> pl_disjunct_list = null;
					Integer pl_number = null;
					Formula pl_role = null;
					if (pl_premise instanceof LessThan) {
						pl_disjunct_list = new ArrayList<>();
						pl_number = pl_premise.getNumber();
						pl_role = pl_premise.getSubFormulas().get(0);

					} else {
						pl_disjunct_list = new ArrayList<>(pl_premise.getSubFormulas());
						for (Formula pl_disjunct : pl_disjunct_list) {
							if (ec.isPresent(concept, pl_disjunct)) {
								pl_number = pl_disjunct.getNumber();
								pl_role = pl_disjunct.getSubFormulas().get(0);
								pl_disjunct_list.remove(pl_disjunct);
								break;
							}
						}
					}

					// 2
					List<Formula> role_conjunct_list = new ArrayList<>();
					if (pl_role instanceof And) {
						role_conjunct_list.addAll(pl_role.getSubFormulas());
					} else {
						role_conjunct_list.add(pl_role);
					}

					for (Formula nl_premise : negative_lthan_premises) {

						if (nl_premise instanceof LessThan) {
							Integer nl_number = nl_premise.getNumber();
							Formula nl_role = nl_premise.getSubFormulas().get(0);
							List<Formula> E_list = new ArrayList<>();
							E_list.addAll(pl_disjunct_list);
							if (nl_role instanceof And) {
								role_conjunct_list.addAll(nl_role.getSubFormulas());
							} else {
								role_conjunct_list.add(nl_role);
							}
							E_list.add(new LessThan(pl_number + nl_number, new And(role_conjunct_list),
									TopConcept.getInstance()));
							if (E_list.size() == 1) {
								output_list.add(E_list.get(0));
							} else {
								output_list.add(new Or(E_list));
							}

						} else {

							List<Formula> nl_disjunct_list = new ArrayList<>(nl_premise.getSubFormulas());
							for (Formula nl_disjunct : nl_disjunct_list) {
								if (ec.isPresent(concept, nl_disjunct)) {
									Integer nl_number = nl_disjunct.getNumber();
									Formula nl_role = nl_disjunct.getSubFormulas().get(0);
									List<Formula> E_list = new ArrayList<>();
									E_list.addAll(pl_disjunct_list);
									nl_disjunct_list.remove(nl_disjunct);
									E_list.addAll(nl_disjunct_list);
									if (nl_role instanceof And) {
										role_conjunct_list.addAll(nl_role.getSubFormulas());
									} else {
										role_conjunct_list.add(nl_role);
									}
									E_list.add(new LessThan(pl_number + nl_number, new And(role_conjunct_list),
											TopConcept.getInstance()));
									output_list.add(new Or(E_list));
									break;
								}
							}
						}
					}
				}
			}
		}

		return output_list;
	}

	public List<Formula> Ackermann_R(AtomicRole role, List<Formula> formula_list) throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();

		List<Formula> positive_RBox_premises = new ArrayList<>();
		List<Formula> negative_RBox_premises = new ArrayList<>();
		List<Formula> positive_TBox_premises = new ArrayList<>();
		List<Formula> negative_TBox_premises = new ArrayList<>();

		EChecker ec = new EChecker();

		for (Formula formula : formula_list) {
			if (!ec.isPresent(role, formula)) {
				output_list.add(formula);

			} else if (formula.equals(role)) {
				positive_RBox_premises.add(formula);

			} else if (formula.equals(new Negation(role))) {
				negative_RBox_premises.add(formula);

			} else if (formula instanceof GreaterThan && formula.getSubFormulas().get(0).equals(role)) {
				positive_TBox_premises.add(formula);

			} else if (formula instanceof LessThan && formula.getSubFormulas().get(0).equals(role)) {
				negative_TBox_premises.add(formula);

			} else if (formula instanceof Or) {
				List<Formula> disjunct_list = formula.getSubFormulas();

				if (disjunct_list.contains(role)) {
					positive_RBox_premises.add(formula);

				} else if (disjunct_list.contains(new Negation(role))) {
					negative_RBox_premises.add(formula);

				} else {
					for (Formula disjunct : disjunct_list) {
						if (disjunct instanceof GreaterThan && disjunct.getSubFormulas().get(0).equals(role)) {
							positive_TBox_premises.add(formula);
							break;
						} else if (disjunct instanceof LessThan && disjunct.getSubFormulas().get(0).equals(role)) {
							negative_TBox_premises.add(formula);
							break;
						}
					}
				}
			}
		}
		// System.out.println("===================================================");
		/*
		 * System.out.println("positive_TBox_premises = " +
		 * positive_TBox_premises.size());
		 * System.out.println("positive_RBox_premises = " +
		 * positive_RBox_premises.size());
		 * System.out.println("negative_TBox_premises = " +
		 * negative_TBox_premises.size());
		 * System.out.println("negative_RBox_premises = " +
		 * negative_RBox_premises.size());
		 */

		// purify positive
		if (negative_TBox_premises.isEmpty() && negative_RBox_premises.isEmpty()) {
			if (!positive_TBox_premises.isEmpty()) {
				for (Formula pt_premise : positive_TBox_premises) {
					output_list.add(PurifyPositive(role, pt_premise));
				}
			}
			if (!positive_RBox_premises.isEmpty()) {
				for (Formula pr_premise : positive_RBox_premises) {
					output_list.add(PurifyPositive(role, pr_premise));
				}
			}
			return output_list;
		}
		// purify negative
		if (positive_TBox_premises.isEmpty() && positive_RBox_premises.isEmpty()) {
			if (!negative_TBox_premises.isEmpty()) {
				for (Formula pt_premise : negative_TBox_premises) {
					output_list.add(PurifyPositive(role, pt_premise));
				}
			}
			if (!negative_RBox_premises.isEmpty()) {
				for (Formula pr_premise : negative_RBox_premises) {
					output_list.add(PurifyPositive(role, pr_premise));
				}
			}
			return output_list;
		}
		// Case
		if (!positive_RBox_premises.isEmpty()) {
			if (positive_RBox_premises.contains(role)) {
				if (!negative_RBox_premises.isEmpty()) {
					for (Formula nr_premise : negative_RBox_premises) {
						output_list.add(AckermannReplace(role, nr_premise, TopRole.getInstance()));
					}
				}
				if (!negative_TBox_premises.isEmpty()) {
					for (Formula nt_premise : negative_TBox_premises) {
						output_list.add(AckermannReplace(role, nt_premise, TopRole.getInstance()));
					}
				}

			} else {

				List<Formula> or_list = new ArrayList<>();
				for (Formula pr_premise : positive_RBox_premises) {
					Formula pr_def = null;

					List<Formula> pr_def_list = new ArrayList<>(pr_premise.getSubFormulas());
					pr_def_list.remove(role);
					if (pr_def_list.size() == 1) {
						pr_def = new Negation(pr_def_list.get(0));
					} else {
						pr_def = new Negation(new Or(pr_def_list));
					}

					or_list.add(pr_def);

					if (!negative_RBox_premises.isEmpty()) {
						for (Formula nr_premise : negative_RBox_premises) {
							output_list.add(AckermannReplace(role, nr_premise, pr_def));
						}
					}
				}

				if (!negative_TBox_premises.isEmpty()) {

					Formula pr_def_or = null;

					if (or_list.size() == 1) {
						pr_def_or = or_list.get(0);
					} else {
						pr_def_or = new Or(or_list);
					}
					for (Formula nt_premise : negative_TBox_premises) {
						output_list.add(AckermannReplace(role, nt_premise, pr_def_or));
					}
				}
			}
		}

		//
		if (negative_RBox_premises.isEmpty()) {

			if (!positive_TBox_premises.isEmpty()) {

				for (Formula pt_premise : positive_TBox_premises) {
					List<Formula> pt_disjunct_list = new ArrayList<>();
					List<Formula> pt_conjunct_list = new ArrayList<>();
					Integer pt_number = null;
					if (pt_premise instanceof GreaterThan) {
						if (pt_premise.getSubFormulas().get(1) instanceof And) {
							pt_conjunct_list.addAll(pt_premise.getSubFormulas().get(1).getSubFormulas());
						} else {
							pt_conjunct_list.add(pt_premise.getSubFormulas().get(1));
						}
						pt_number = pt_premise.getNumber();

					} else {
						pt_disjunct_list.addAll(pt_premise.getSubFormulas());
						for (Formula pt_disjunct : pt_disjunct_list) {
							if (ec.isPresent(role, pt_disjunct)) {
								pt_number = pt_disjunct.getNumber();
								pt_disjunct_list.remove(pt_disjunct);
								if (pt_disjunct.getSubFormulas().get(1) instanceof And) {
									pt_conjunct_list.addAll(pt_disjunct.getSubFormulas().get(1).getSubFormulas());
								} else {
									pt_conjunct_list.add(pt_disjunct.getSubFormulas().get(1));
								}
								break;
							}
						}
					}

					// 4 inner loop
					for (Formula nt_premise : negative_TBox_premises) {
						if (nt_premise instanceof LessThan) {
							Integer nt_number = nt_premise.getNumber();
							if (pt_number > nt_number) {
								List<Formula> E_list = new ArrayList<>();
								E_list.addAll(pt_disjunct_list);
								List<Formula> F_list = new ArrayList<>();
								F_list.addAll(pt_conjunct_list);
								if (nt_premise.getSubFormulas().get(1) instanceof Or) {
									List<Formula> nt_disjunct_filler = nt_premise.getSubFormulas().get(1)
											.getSubFormulas();
									for (Formula disjunct : nt_disjunct_filler) {
										F_list.add(new Negation(disjunct));
									}
								} else {
									F_list.add(new Negation(nt_premise.getSubFormulas().get(1)));
								}
								E_list.add(
										new GreaterThan(pt_number - nt_number, TopRole.getInstance(), new And(F_list)));
								if (E_list.size() == 1) {
									output_list.add(E_list.get(0));
								} else {
									output_list.add(new Or(E_list));
								}
							}
							// 5
						} else {
							List<Formula> nt_disjunct_list = new ArrayList<>(nt_premise.getSubFormulas());
							for (Formula nt_disjunct : nt_disjunct_list) {
								if (ec.isPresent(role, nt_disjunct)) {
									Integer nt_number = nt_disjunct.getNumber();
									if (pt_number > nt_number) {
										List<Formula> E_list = new ArrayList<>();
										E_list.addAll(pt_disjunct_list);
										nt_disjunct_list.remove(nt_disjunct);
										E_list.addAll(nt_disjunct_list);
										List<Formula> F_list = new ArrayList<>();
										F_list.addAll(pt_conjunct_list);
										if (nt_disjunct.getSubFormulas().get(1) instanceof Or) {
											List<Formula> nt_disjunct_filler = nt_disjunct.getSubFormulas().get(1)
													.getSubFormulas();
											for (Formula disjunct : nt_disjunct_filler) {
												F_list.add(new Negation(disjunct));
											}
										} else {
											F_list.add(new Negation(nt_disjunct.getSubFormulas().get(1)));
										}
										E_list.add(new GreaterThan(pt_number - nt_number, TopRole.getInstance(),
												new And(F_list)));
										output_list.add(new Or(E_list));
										break;
									}
								}
							}
						}
					}
				}
			}

		} else {

			if (negative_RBox_premises.contains(new Negation(role))) {

				if (!positive_TBox_premises.isEmpty()) {
					for (Formula pt_premise : positive_TBox_premises) {
						output_list.add(AckermannReplace(role, pt_premise, BottomRole.getInstance()));
					}

					if (!negative_TBox_premises.isEmpty()) {

						for (Formula pt_premise : positive_TBox_premises) {
							List<Formula> pt_disjunct_list = new ArrayList<>();
							List<Formula> pt_conjunct_list = new ArrayList<>();
							Integer pt_number = null;
							if (pt_premise instanceof GreaterThan) {
								if (pt_premise.getSubFormulas().get(1) instanceof And) {
									pt_conjunct_list.addAll(pt_premise.getSubFormulas().get(1).getSubFormulas());
								} else {
									pt_conjunct_list.add(pt_premise.getSubFormulas().get(1));
								}
								pt_number = pt_premise.getNumber();

							} else {
								pt_disjunct_list.addAll(pt_premise.getSubFormulas());
								for (Formula pt_disjunct : pt_disjunct_list) {
									if (ec.isPresent(role, pt_disjunct)) {
										pt_number = pt_disjunct.getNumber();
										pt_disjunct_list.remove(pt_disjunct);
										if (pt_disjunct.getSubFormulas().get(1) instanceof And) {
											pt_conjunct_list
													.addAll(pt_disjunct.getSubFormulas().get(1).getSubFormulas());
										} else {
											pt_conjunct_list.add(pt_disjunct.getSubFormulas().get(1));
										}
										break;
									}
								}
							}

							// 4 inner loop
							for (Formula nt_premise : negative_TBox_premises) {
								if (nt_premise instanceof LessThan) {
									Integer nt_number = nt_premise.getNumber();
									if (pt_number > nt_number) {
										List<Formula> E_list = new ArrayList<>();
										E_list.addAll(pt_disjunct_list);
										List<Formula> F_list = new ArrayList<>();
										F_list.addAll(pt_conjunct_list);
										if (nt_premise.getSubFormulas().get(1) instanceof Or) {
											List<Formula> nt_disjunct_filler = nt_premise.getSubFormulas().get(1)
													.getSubFormulas();
											for (Formula disjunct : nt_disjunct_filler) {
												F_list.add(new Negation(disjunct));
											}
										} else {
											F_list.add(new Negation(nt_premise.getSubFormulas().get(1)));
										}
										E_list.add(new GreaterThan(pt_number - nt_number, BottomRole.getInstance(),
												new And(F_list)));
										if (E_list.size() == 1) {
											output_list.add(E_list.get(0));
										} else {
											output_list.add(new Or(E_list));
										}
									}
									// 5
								} else {
									List<Formula> nt_disjunct_list = new ArrayList<>(nt_premise.getSubFormulas());
									for (Formula nt_disjunct : nt_disjunct_list) {
										if (ec.isPresent(role, nt_disjunct)) {
											Integer nt_number = nt_disjunct.getNumber();
											if (pt_number > nt_number) {
												List<Formula> E_list = new ArrayList<>();
												E_list.addAll(pt_disjunct_list);
												nt_disjunct_list.remove(nt_disjunct);
												E_list.addAll(nt_disjunct_list);
												List<Formula> F_list = new ArrayList<>();
												F_list.addAll(pt_conjunct_list);
												if (nt_disjunct.getSubFormulas().get(1) instanceof Or) {
													List<Formula> nt_disjunct_filler = nt_disjunct.getSubFormulas()
															.get(1).getSubFormulas();
													for (Formula disjunct : nt_disjunct_filler) {
														F_list.add(new Negation(disjunct));
													}
												} else {
													F_list.add(new Negation(nt_disjunct.getSubFormulas().get(1)));
												}
												E_list.add(new GreaterThan(pt_number - nt_number,
														BottomRole.getInstance(), new And(F_list)));
												output_list.add(new Or(E_list));
												break;
											}
										}
									}
								}
							}
						}
					}
				}

			} else {

				List<Formula> and_list = new ArrayList<>();
				for (Formula nr_premise : negative_RBox_premises) {

					Formula nr_def = null;

					List<Formula> nr_def_list = new ArrayList<>(nr_premise.getSubFormulas());
					nr_def_list.remove(new Negation(role));
					if (nr_def_list.size() == 1) {
						nr_def = nr_def_list.get(0);
					} else {
						nr_def = new Or(nr_def_list);
					}

					and_list.add(nr_def);
				}

				if (!positive_TBox_premises.isEmpty()) {
					Formula nr_def_and = null;

					if (and_list.size() == 1) {
						nr_def_and = and_list.get(0);
					} else {
						nr_def_and = new And(and_list);
					}
					for (Formula pt_premise : positive_TBox_premises) {
						output_list.add(AckermannReplace(role, pt_premise, nr_def_and));
					}

					if (!negative_TBox_premises.isEmpty()) {

						for (Formula pt_premise : positive_TBox_premises) {
							List<Formula> pt_disjunct_list = new ArrayList<>();
							List<Formula> pt_conjunct_list = new ArrayList<>();
							Integer pt_number = null;
							if (pt_premise instanceof GreaterThan) {
								if (pt_premise.getSubFormulas().get(1) instanceof And) {
									pt_conjunct_list.addAll(pt_premise.getSubFormulas().get(1).getSubFormulas());
								} else {
									pt_conjunct_list.add(pt_premise.getSubFormulas().get(1));
								}
								pt_number = pt_premise.getNumber();

							} else {
								pt_disjunct_list.addAll(pt_premise.getSubFormulas());
								for (Formula pt_disjunct : pt_disjunct_list) {
									if (ec.isPresent(role, pt_disjunct)) {
										pt_number = pt_disjunct.getNumber();
										pt_disjunct_list.remove(pt_disjunct);
										if (pt_disjunct.getSubFormulas().get(1) instanceof And) {
											pt_conjunct_list
													.addAll(pt_disjunct.getSubFormulas().get(1).getSubFormulas());
										} else {
											pt_conjunct_list.add(pt_disjunct.getSubFormulas().get(1));
										}
										break;
									}
								}
							}

							// 4 inner loop
							for (Formula nt_premise : negative_TBox_premises) {
								if (nt_premise instanceof LessThan) {
									Integer nt_number = nt_premise.getNumber();
									if (pt_number > nt_number) {
										List<Formula> E_list = new ArrayList<>();
										E_list.addAll(pt_disjunct_list);
										List<Formula> F_list = new ArrayList<>();
										F_list.addAll(pt_conjunct_list);
										if (nt_premise.getSubFormulas().get(1) instanceof Or) {
											List<Formula> nt_disjunct_filler = nt_premise.getSubFormulas().get(1)
													.getSubFormulas();
											for (Formula disjunct : nt_disjunct_filler) {
												F_list.add(new Negation(disjunct));
											}
										} else {
											F_list.add(new Negation(nt_premise.getSubFormulas().get(1)));
										}
										E_list.add(new GreaterThan(pt_number - nt_number, nr_def_and, new And(F_list)));
										if (E_list.size() == 1) {
											output_list.add(E_list.get(0));
										} else {
											output_list.add(new Or(E_list));
										}
									}
									// 5
								} else {
									List<Formula> nt_disjunct_list = new ArrayList<>(nt_premise.getSubFormulas());
									for (Formula nt_disjunct : nt_disjunct_list) {
										if (ec.isPresent(role, nt_disjunct)) {
											Integer nt_number = nt_disjunct.getNumber();
											if (pt_number > nt_number) {
												List<Formula> E_list = new ArrayList<>();
												E_list.addAll(pt_disjunct_list);
												nt_disjunct_list.remove(nt_disjunct);
												E_list.addAll(nt_disjunct_list);
												List<Formula> F_list = new ArrayList<>();
												F_list.addAll(pt_conjunct_list);
												if (nt_disjunct.getSubFormulas().get(1) instanceof Or) {
													List<Formula> nt_disjunct_filler = nt_disjunct.getSubFormulas()
															.get(1).getSubFormulas();
													for (Formula disjunct : nt_disjunct_filler) {
														F_list.add(new Negation(disjunct));
													}
												} else {
													F_list.add(new Negation(nt_disjunct.getSubFormulas().get(1)));
												}
												E_list.add(new GreaterThan(pt_number - nt_number, nr_def_and,
														new And(F_list)));
												output_list.add(new Or(E_list));
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return output_list;
	}

	public List<Formula> AckermannPositive(AtomicConcept concept, List<Formula> input_list)
			throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();
		List<Formula> toBeReplaced_list = new ArrayList<>();
		List<Formula> toReplace_list = new ArrayList<>();

		FChecker cf = new FChecker();

		for (Formula formula : input_list) {
			if (cf.positive(concept, formula) == 0) {
				toBeReplaced_list.add(formula);

			} else {
				toReplace_list.add(formula);
			}
		}

		Formula definition = null;
		List<Formula> disjunct_list = new ArrayList<>();

		for (Formula toReplace : toReplace_list) {
			if (toReplace.equals(concept)) {
				definition = TopConcept.getInstance();
				break;

			} else {
				List<Formula> other_list = new ArrayList<>(toReplace.getSubFormulas());
				other_list.remove(concept);
				if (other_list.size() == 1) {
					disjunct_list.add(new Negation(other_list.get(0)));
					continue;
				} else {
					disjunct_list.add(new Negation(new Or(other_list)));
					continue;
				}
			}
		}

		if (definition != TopConcept.getInstance()) {
			if (disjunct_list.size() == 1) {
				definition = disjunct_list.get(0);
			} else {
				definition = new Or(disjunct_list);
			}
		}

		for (Formula toBeReplaced : toBeReplaced_list) {
			output_list.add(AckermannReplace(concept, toBeReplaced, definition));
		}

		return output_list;
	}

	public List<Formula> AckermannNegative(AtomicConcept concept, List<Formula> input_list)
			throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();
		List<Formula> toBeReplaced_list = new ArrayList<>();
		List<Formula> toReplace_list = new ArrayList<>();

		FChecker cf = new FChecker();

		for (Formula formula : input_list) {
			if (cf.negative(concept, formula) == 0) {
				toBeReplaced_list.add(formula);

			} else {
				toReplace_list.add(formula);
			}
		}

		Formula definition = null;
		List<Formula> disjunct_list = new ArrayList<>();

		for (Formula toReplace : toReplace_list) {
			if (toReplace.equals(new Negation(concept))) {
				definition = BottomConcept.getInstance();
				break;

			} else {
				List<Formula> other_list = new ArrayList<>(toReplace.getSubFormulas());
				other_list.remove(new Negation(concept));
				if (other_list.size() == 1) {
					disjunct_list.add(other_list.get(0));
					continue;
				} else {
					disjunct_list.add(new Or(other_list));
					continue;
				}
			}
		}

		if (definition != BottomConcept.getInstance()) {
			if (disjunct_list.size() == 1) {
				definition = disjunct_list.get(0);
			} else {
				definition = new And(disjunct_list);
			}
		}

		for (Formula toBeReplaced : toBeReplaced_list) {
			output_list.add(AckermannReplace(concept, toBeReplaced, definition));
		}

		return output_list;
	}

	public List<Formula> PurifyPositive(AtomicRole role, List<Formula> input_list) throws CloneNotSupportedException {

		FChecker cf = new FChecker();

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			if (cf.positive(role, formula) == 0) {
				output_list.add(formula);
			} else {
				output_list.add(PurifyPositive(role, formula));
			}
		}

		return output_list;
	}

	public List<Formula> PurifyPositive(AtomicConcept concept, List<Formula> input_list)
			throws CloneNotSupportedException {

		FChecker cf = new FChecker();

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			if (cf.positive(concept, formula) == 0) {
				output_list.add(formula);
			} else {
				output_list.add(PurifyPositive(concept, formula));
			}
		}

		return output_list;
	}

	public List<Formula> PurifyNegative(AtomicRole role, List<Formula> input_list) throws CloneNotSupportedException {

		FChecker cf = new FChecker();

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			if (cf.negative(role, formula) == 0) {
				output_list.add(formula);
			} else {
				output_list.add(PurifyNegative(role, formula));
			}
		}

		return output_list;
	}

	public List<Formula> PurifyNegative(AtomicConcept concept, List<Formula> inputList)
			throws CloneNotSupportedException {

		FChecker cf = new FChecker();

		List<Formula> outputList = new ArrayList<>();

		for (Formula formula : inputList) {
			if (cf.negative(concept, formula) == 0) {
				outputList.add(formula);
			} else {
				outputList.add(PurifyNegative(concept, formula));
			}
		}

		return outputList;
	}

	public Formula AckermannReplace(AtomicRole role, Formula toBeReplaced, Formula definition) {

		if (toBeReplaced instanceof AtomicConcept) {
			return new AtomicConcept(toBeReplaced.getText());

		} else if (toBeReplaced instanceof AtomicRole) {
			return toBeReplaced.equals(role) ? definition : new AtomicRole(toBeReplaced.getText());

		} else if (toBeReplaced instanceof Negation) {
			return new Negation(AckermannReplace(role, toBeReplaced.getSubFormulas().get(0), definition));

		} else if (toBeReplaced instanceof GreaterThan) {
			return new GreaterThan(toBeReplaced.getNumber(),
					AckermannReplace(role, toBeReplaced.getSubFormulas().get(0), definition),
					AckermannReplace(role, toBeReplaced.getSubFormulas().get(1), definition));

		} else if (toBeReplaced instanceof LessThan) {
			return new LessThan(toBeReplaced.getNumber(),
					AckermannReplace(role, toBeReplaced.getSubFormulas().get(0), definition),
					AckermannReplace(role, toBeReplaced.getSubFormulas().get(1), definition));

		} else if (toBeReplaced instanceof And) {
			List<Formula> conjunct_list = toBeReplaced.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(AckermannReplace(role, conjunct, definition));
			}
			return new And(new_conjunct_list);

		} else if (toBeReplaced instanceof Or) {
			List<Formula> disjunct_list = toBeReplaced.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(AckermannReplace(role, disjunct, definition));
			}
			return new Or(new_disjunct_list);

		}

		return toBeReplaced;
	}

	public Formula AckermannReplace(AtomicConcept concept, Formula toBeReplaced, Formula definition) {

		if (toBeReplaced instanceof AtomicConcept) {
			return toBeReplaced.equals(concept) ? definition : new AtomicConcept(toBeReplaced.getText());

		} else if (toBeReplaced instanceof AtomicRole) {
			return new AtomicRole(toBeReplaced.getText());

		} else if (toBeReplaced instanceof Negation) {
			return new Negation(AckermannReplace(concept, toBeReplaced.getSubFormulas().get(0), definition));

		} else if (toBeReplaced instanceof GreaterThan) {
			return new GreaterThan(toBeReplaced.getNumber(),
					AckermannReplace(concept, toBeReplaced.getSubFormulas().get(0), definition),
					AckermannReplace(concept, toBeReplaced.getSubFormulas().get(1), definition));

		} else if (toBeReplaced instanceof LessThan) {
			return new LessThan(toBeReplaced.getNumber(),
					AckermannReplace(concept, toBeReplaced.getSubFormulas().get(0), definition),
					AckermannReplace(concept, toBeReplaced.getSubFormulas().get(1), definition));

		} else if (toBeReplaced instanceof And) {
			List<Formula> conjunct_list = toBeReplaced.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(AckermannReplace(concept, conjunct, definition));
			}
			return new And(new_conjunct_list);

		} else if (toBeReplaced instanceof Or) {
			List<Formula> disjunct_list = toBeReplaced.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(AckermannReplace(concept, disjunct, definition));
			}
			return new Or(new_disjunct_list);

		}

		return toBeReplaced;
	}

	public Formula PurifyPositive(AtomicRole role, Formula formula) {

		if (formula instanceof AtomicConcept) {
			return new AtomicConcept(formula.getText());

		} else if (formula instanceof AtomicRole) {
			return formula.equals(role) ? TopRole.getInstance() : new AtomicRole(formula.getText());

		} else if (formula instanceof Individual) {
			return new Individual(formula.getText());

		} else if (formula instanceof Negation) {
			return new Negation(PurifyPositive(role, formula.getSubFormulas().get(0)));

		} else if (formula instanceof Inverse) {
			return new Inverse(PurifyPositive(role, formula.getSubFormulas().get(0)));

		} else if (formula instanceof GreaterThan) {
			return new GreaterThan(formula.getNumber(), PurifyPositive(role, formula.getSubFormulas().get(0)),
					PurifyPositive(role, formula.getSubFormulas().get(1)));

		} else if (formula instanceof LessThan) {
			return new LessThan(formula.getNumber(), PurifyPositive(role, formula.getSubFormulas().get(0)),
					PurifyPositive(role, formula.getSubFormulas().get(1)));

		} else if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(PurifyPositive(role, conjunct));
			}
			return new And(new_conjunct_list);

		} else if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(PurifyPositive(role, disjunct));
			}
			return new Or(new_disjunct_list);
		}

		return formula;
	}

	public Formula PurifyNegative(AtomicRole role, Formula formula) {

		if (formula instanceof AtomicConcept) {
			return new AtomicConcept(formula.getText());

		} else if (formula instanceof AtomicRole) {
			return formula.equals(role) ? BottomRole.getInstance() : new AtomicRole(formula.getText());

		} else if (formula instanceof Individual) {
			return new Individual(formula.getText());

		} else if (formula instanceof Negation) {
			return new Negation(PurifyNegative(role, formula.getSubFormulas().get(0)));

		} else if (formula instanceof Inverse) {
			return new Inverse(PurifyNegative(role, formula.getSubFormulas().get(0)));

		} else if (formula instanceof GreaterThan) {
			return new GreaterThan(formula.getNumber(), PurifyNegative(role, formula.getSubFormulas().get(0)),
					PurifyNegative(role, formula.getSubFormulas().get(1)));

		} else if (formula instanceof LessThan) {
			return new LessThan(formula.getNumber(), PurifyNegative(role, formula.getSubFormulas().get(0)),
					PurifyNegative(role, formula.getSubFormulas().get(1)));

		} else if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(PurifyNegative(role, conjunct));
			}
			return new And(new_conjunct_list);

		} else if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(PurifyNegative(role, disjunct));
			}
			return new Or(new_disjunct_list);
		}

		return formula;
	}

	public Formula PurifyPositive(AtomicConcept concept, Formula formula) {

		if (formula instanceof AtomicConcept) {
			return formula.equals(concept) ? TopConcept.getInstance() : new AtomicConcept(formula.getText());

		} else if (formula instanceof AtomicRole) {
			return new AtomicRole(formula.getText());

		} else if (formula instanceof Individual) {
			return new Individual(formula.getText());

		} else if (formula instanceof Negation) {
			return new Negation(PurifyPositive(concept, formula.getSubFormulas().get(0)));

		} else if (formula instanceof Inverse) {
			return new Inverse(PurifyPositive(concept, formula.getSubFormulas().get(0)));

		} else if (formula instanceof GreaterThan) {
			return new GreaterThan(formula.getNumber(), PurifyPositive(concept, formula.getSubFormulas().get(0)),
					PurifyPositive(concept, formula.getSubFormulas().get(1)));

		} else if (formula instanceof LessThan) {
			return new LessThan(formula.getNumber(), PurifyPositive(concept, formula.getSubFormulas().get(0)),
					PurifyPositive(concept, formula.getSubFormulas().get(1)));

		} else if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(PurifyPositive(concept, conjunct));
			}
			return new And(new_conjunct_list);

		} else if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(PurifyPositive(concept, disjunct));
			}
			return new Or(new_disjunct_list);
		}

		return formula;
	}

	public Formula PurifyNegative(AtomicConcept concept, Formula formula) {

		if (formula instanceof AtomicConcept) {
			return formula.equals(concept) ? BottomConcept.getInstance() : new AtomicConcept(formula.getText());

		} else if (formula instanceof AtomicRole) {
			return new AtomicRole(formula.getText());

		} else if (formula instanceof Individual) {
			return new Individual(formula.getText());

		} else if (formula instanceof Negation) {
			return new Negation(PurifyNegative(concept, formula.getSubFormulas().get(0)));

		} else if (formula instanceof Inverse) {
			return new Inverse(PurifyNegative(concept, formula.getSubFormulas().get(0)));

		} else if (formula instanceof GreaterThan) {
			return new GreaterThan(formula.getNumber(), PurifyNegative(concept, formula.getSubFormulas().get(0)),
					PurifyNegative(concept, formula.getSubFormulas().get(1)));

		} else if (formula instanceof LessThan) {
			return new LessThan(formula.getNumber(), PurifyNegative(concept, formula.getSubFormulas().get(0)),
					PurifyNegative(concept, formula.getSubFormulas().get(1)));

		} else if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(PurifyNegative(concept, conjunct));
			}
			return new And(new_conjunct_list);

		} else if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(PurifyNegative(concept, disjunct));
			}
			return new Or(new_disjunct_list);
		}

		return formula;
	}

}

package org.variantsync.diffdetective.experiments.views_es;

import static org.variantsync.functjonal.Functjonal.intercalate;

import org.variantsync.diffdetective.util.CSV;

public record UnparseEvaluation(
    int[] dataTest,
    int[] diffTest,
    int[] diffSemEqTest,
    int[] treeBeforeTest,
    int[] treeAfterTest
) implements CSV {

  @Override
  public String toCSV(String delimiter){
    return intercalate(delimiter,
        dataTest[0],
        dataTest[1],
        dataTest[2],
        dataTest[3],
        dataTest[4],
        dataTest[5],
        dataTest[6],
        dataTest[7],
        diffTest[0],
        diffTest[1],
        diffTest[2],
        diffTest[3],
        diffTest[4],
        diffTest[5],
        diffTest[6],
        diffTest[7],
        diffSemEqTest[0],
        diffSemEqTest[1],
        diffSemEqTest[2],
        diffSemEqTest[3],
        treeBeforeTest[0],
        treeBeforeTest[1],
        treeBeforeTest[2],
        treeBeforeTest[3],
        treeBeforeTest[4],
        treeBeforeTest[5],
        treeBeforeTest[6],
        treeBeforeTest[7],
        treeAfterTest[0],
        treeAfterTest[1],
        treeAfterTest[2],
        treeAfterTest[3],
        treeAfterTest[4],
        treeAfterTest[5],
        treeAfterTest[6],
        treeAfterTest[7]
    );
  }

  public static String makeHeader(String delimiter) {
    return intercalate(delimiter,
        "diffFromTreesEqTextDiffMye",
        "diffFromTreesEqTextDiffMyeWhite",
        "diffFromTreesEqTextDiffHis",
        "diffFromTreesEqTextDiffHisWhite",
        "treeBefEqTreeBefDiff",
        "treeBefEqTreeBefDiffWhite",
        "treeAftEqTreeAftDiff",
        "treeAftEqTreeAftDiffWhite",
        "diffEqTestMultiL0EmptyL0",
        "diffEqTestMultiL1EmptyL0",
        "diffEqTestMultiL0EmptyL1",
        "diffEqTestMultiL1EmptyL1",
        "diffEqTestMultiL0EmptyL0White",
        "diffEqTestMultiL1EmptyL0White",
        "diffEqTestMultiL0EmptyL1White",
        "diffEqTestMultiL1EmptyL1White",
        "diffSemEqTestMultiL0EmptyL0",
        "diffSemEqTestMultiL1EmptyL0",
        "diffSemEqTestMultiL0EmptyL1",
        "diffSemEqTestMultiL1EmptyL1",
        "treeBeforeEqTestMultiL0EmptyL0",
        "treeBeforeEqTestMultiL1EmptyL0",
        "treeBeforeEqTestMultiL0EmptyL1",
        "treeBeforeEqTestMultiL1EmptyL1",
        "treeBeforeEqTestMultiL0EmptyL0White",
        "treeBeforeEqTestMultiL1EmptyL0White",
        "treeBeforeEqTestMultiL0EmptyL1White",
        "treeBeforeEqTestMultiL1EmptyL1White",
        "treeAfterEqTestMultiL0EmptyL0",
        "treeAfterEqTestMultiL1EmptyL0",
        "treeAfterEqTestMultiL0EmptyL1",
        "treeAfterEqTestMultiL1EmptyL1",
        "treeAfterEqTestMultiL0EmptyL0White",
        "treeAfterEqTestMultiL1EmptyL0White",
        "treeAfterEqTestMultiL0EmptyL1White",
        "treeAfterEqTestMultiL1EmptyL1White"
    );
  }




}

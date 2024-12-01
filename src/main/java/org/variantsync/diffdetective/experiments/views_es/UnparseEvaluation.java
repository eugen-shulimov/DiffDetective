package org.variantsync.diffdetective.experiments.views_es;

import static org.variantsync.functjonal.Functjonal.intercalate;

import org.variantsync.diffdetective.util.CSV;

public record UnparseEvaluation(
    int[] dataTest,
    int[] diffTest,
    int[] diffBeforeTreeTest,
    int[] diffAfterTreeTest,
    int[] treeBeforeTest,
    int[] treeAfterTest,
    int error,
    String[] errorSave
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
        diffBeforeTreeTest[0],
        diffBeforeTreeTest[1],
        diffBeforeTreeTest[2],
        diffBeforeTreeTest[3],
        diffBeforeTreeTest[4],
        diffBeforeTreeTest[5],
        diffBeforeTreeTest[6],
        diffBeforeTreeTest[7],
        diffAfterTreeTest[0],
        diffAfterTreeTest[1],
        diffAfterTreeTest[2],
        diffAfterTreeTest[3],
        diffAfterTreeTest[4],
        diffAfterTreeTest[5],
        diffAfterTreeTest[6],
        diffAfterTreeTest[7],
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
        treeAfterTest[7],
        error,
        errorSave[0],
        errorSave[1],
        errorSave[2]
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
        "diffEqTestBefTreeMultiL0EmptyL0",
        "diffEqTestBefTreeMultiL1EmptyL0",
        "diffEqTestBefTreeMultiL0EmptyL1",
        "diffEqTestBefTreeMultiL1EmptyL1",
        "diffEqTestBefTreeMultiL0EmptyL0White",
        "diffEqTestBefTreeMultiL1EmptyL0White",
        "diffEqTestBefTreeMultiL0EmptyL1White",
        "diffEqTestBefTreeMultiL1EmptyL1White",
        "diffEqTestAftTreeMultiL0EmptyL0",
        "diffEqTestAftTreeMultiL1EmptyL0",
        "diffEqTestAftTreeMultiL0EmptyL1",
        "diffEqTestAftTreeMultiL1EmptyL1",
        "diffEqTestAftTreeMultiL0EmptyL0White",
        "diffEqTestAftTreeMultiL1EmptyL0White",
        "diffEqTestAftTreeMultiL0EmptyL1White",
        "diffEqTestAftTreeMultiL1EmptyL1White",
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
        "treeAfterEqTestMultiL1EmptyL1White",
        "errorTyp",
        "errorData1",
        "errorData2",
        "errorData3"
    );
  }




}

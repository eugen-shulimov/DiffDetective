package org.variantsync.diffdetective.variation;

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm;
import org.variantsync.diffdetective.util.FileUtils;
import org.variantsync.diffdetective.variation.diff.Time;
import org.variantsync.diffdetective.variation.diff.VariationDiff;
import org.variantsync.diffdetective.variation.diff.construction.JGitDiff;
import org.variantsync.diffdetective.variation.tree.VariationTree;
import org.variantsync.diffdetective.variation.tree.VariationTreeNode;

public class VariationUnparser {


  /**
   * Unparst VariationTrees to Text/String
   * @param tree VariationTree, that be unparsed
   * @param linesToLabel Function, that return list of String and has a Class T
   * @return String, the result of unparsing
   * @param <T> that implements Label
   */
  public static <T extends Label>  String variationTreeUnparser(VariationTree<T> tree, Function<List<String>,T> linesToLabel){
    if(!tree.root().getChildren().isEmpty()) {
      StringBuilder result = new StringBuilder();
      Stack<VariationTreeNode<T>> stack = new Stack<>();
      for (int i = tree.root().getChildren().size() - 1; i >= 0; i--) {
        stack.push(tree.root().getChildren().get(i));
      }
      while (!stack.empty()) {
        VariationTreeNode<T> node = stack.pop();
        if (node.isIf()) {
          stack.push(new VariationTreeNode<>(NodeType.ARTIFACT, null, null,
              linesToLabel.apply(node.getEndIf())));
        }
        for (String line : node.getLabel().getLines()) {
          result.append(line);
          result.append("\n");
        }
        for (int i = node.getChildren().size() - 1; i >= 0; i--) {
          stack.push(node.getChildren().get(i));
        }
      }
      return result.substring(0, result.length() - 1);
    }else{
      return "";
    }
  }

  /**
   * Unparst VariationTrees to Text/String
   * @param tree VariationTree, that be unparsed
   * @return String, the result of unparsing
   */
  public static String variationTreeUnparser(VariationTree<DiffLinesLabel> tree){
    return variationTreeUnparser(tree,DiffLinesLabel::withInvalidLineNumbers);
  }

  /**
   * Unparst VariationDiffs to Text/String
   * @param diff VariationDiff, that be unparsed
   * @param linesToLabel Function, that return list of String and has a Class T
   * @return String, the result of unparsing
   * @param <T> that implements Label
   * @throws IOException
   */
  public static <T extends Label> String variationDiffUnparser(VariationDiff<T> diff,Function<List<String>,T> linesToLabel) throws IOException {
    String tree1 = variationTreeUnparser(diff.project(Time.BEFORE),linesToLabel);
    String tree2 = variationTreeUnparser(diff.project(Time.AFTER),linesToLabel);
    return JGitDiff.textDiff(tree1,tree2, SupportedAlgorithm.MYERS);
  }

  /**
   * Unparst VariationDiffs to Text/String
   * @param diff VariationDiff, that be unparsed
   * @return String, the result of unparsing
   * @throws IOException
   */
  public static String variationDiffUnparser(VariationDiff<DiffLinesLabel> diff) throws IOException {
    return variationDiffUnparser(diff,DiffLinesLabel::withInvalidLineNumbers);
  }

  public static String undiff(String text,Time time){
    if(text.isEmpty()){
      return "";
    }
    else {
      StringBuilder result = new StringBuilder();
      String[] textSplit = text.split("\n");
      char zeichen;
      if (Time.AFTER == time) {
        zeichen = '-';
      } else {
        zeichen = '+';
      }
      for (String line : textSplit) {
        if (line.isEmpty() ) {
          result.append(line);
          result.append("\n");
        }
        else if(line.charAt(0) != zeichen){
          result.append(line.substring(1));
          result.append("\n");
        }
      }
      if(result.isEmpty()){
        return "";
      }
      return result.toString();
    }
  }

}

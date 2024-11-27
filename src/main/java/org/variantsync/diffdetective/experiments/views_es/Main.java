package org.variantsync.diffdetective.experiments.views_es;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import org.variantsync.diffdetective.AnalysisRunner;
import org.variantsync.diffdetective.analysis.Analysis;
import org.variantsync.diffdetective.analysis.FilterAnalysis;
import org.variantsync.diffdetective.analysis.StatisticsAnalysis;
import org.variantsync.diffdetective.datasets.PatchDiffParseOptions;
import org.variantsync.diffdetective.datasets.Repository;
import org.variantsync.diffdetective.diff.git.DiffFilter;
import org.variantsync.diffdetective.variation.diff.filter.VariationDiffFilter;
import org.variantsync.diffdetective.variation.diff.parse.VariationDiffParseOptions;

public class Main {

  public static String dataSetPath = "";

  public static void main(String[] args) throws IOException {
    startAnalysis();


  }


  private static Analysis AnalysisFactory1(Repository repo, Path repoOutputDir) {
    return new Analysis(
        "Unparse Analysis",
        new ArrayList<>(List.of(
            new FilterAnalysis( // filters unwanted trees
                VariationDiffFilter.notEmpty()
            ),
            new UnparseAnalysis(),
            new StatisticsAnalysis()
        )),
        repo,
        repoOutputDir
    );
  }

  private static void startAnalysis() throws IOException{
    final AnalysisRunner.Options analysisOptions = new AnalysisRunner.Options(
        Paths.get("..", "DiffDetectiveReplicationDatasets"),
        Paths.get("results","views_es" ),
        Path.of("/mnt/c/Users/eshul/IdeaProjects/DiffDetective/docs/datasets","eugen-bachelor-thesis.md"),
        repo -> new PatchDiffParseOptions(
            PatchDiffParseOptions.DiffStoragePolicy.DO_NOT_REMEMBER,
            VariationDiffParseOptions.Default
        ),
        repo -> new DiffFilter.Builder().allowMerge(true)
            .allowedFileExtensions("c", "cpp").build(),
        true,
        false
    );

    /*
    AnalysisRunner.run(analysisOptions, (repository, path) -> {
      Analysis.forEachCommit(() -> AnalysisFactory(repository, path)
      );
    });
    */
    AnalysisRunner.run(analysisOptions,extractionRunner());
  }

  protected static BiConsumer<Repository, Path> extractionRunner() {
    return (repo, repoOutputDir) -> {


      final BiFunction<Repository, Path, Analysis> AnalysisFactory =
          (r, out) -> new Analysis("PCAnalysis", List.of(new UnparseAnalysis()), r, out);

      Analysis.forEachCommit(() -> AnalysisFactory.apply(repo, repoOutputDir));

    };
  }


}

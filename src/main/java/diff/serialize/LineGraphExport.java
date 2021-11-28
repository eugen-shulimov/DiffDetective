package diff.serialize;

import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.util.Pair;
import diff.CommitDiff;
import diff.PatchDiff;
import diff.difftree.DiffTree;
import diff.difftree.render.DiffTreeRenderer;
import diff.difftree.render.ErrorRendering;
import diff.difftree.serialize.DiffTreeLineGraphExporter;
import diff.difftree.serialize.DiffTreeNodeLabelFormat;
import diff.difftree.transform.DiffTreeTransformer;
import org.pmw.tinylog.Logger;
import util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class LineGraphExport {
    public static final String TREE_NAME_SEPARATOR = "$$$";

    public static record Options(
            DiffTreeNodeLabelFormat.NodePrintStyle nodePrintStyle,
            boolean skipEmptyTrees,
            List<DiffTreeTransformer> treePreProcessing,
            BiConsumer<PatchDiff, Exception> onError) {
        public Options(DiffTreeNodeLabelFormat.NodePrintStyle nodePrintStyle) {
            this(nodePrintStyle, false, new ArrayList<>(), LogError());
        }

        public static BiConsumer<PatchDiff, Exception> LogError() {
            return (p, e) -> Logger.error(e);
        }

        public static BiConsumer<PatchDiff, Exception> RenderError() {
            final ErrorRendering errorRenderer = new ErrorRendering(DiffTreeRenderer.WithinDiffDetective());
            return (p, e) -> {
                Logger.error(e);
                Logger.error("Rendering patch");
                errorRenderer.onError(p);
            };
        }

        public static BiConsumer<PatchDiff, Exception> SysExitOnError() {
            return (p, e) -> System.exit(0);
        }
    }

    public static Pair<DiffTreeSerializeDebugData, String> toLineGraphFormat(final DiffTree diffTree, final Options options) {
        DiffTreeTransformer.apply(options.treePreProcessing, diffTree);
        diffTree.assertConsistency();

        if (options.skipEmptyTrees && diffTree.isEmpty()) {
            return new Pair<>(new DiffTreeSerializeDebugData(), "");
        }

        final DiffTreeLineGraphExporter exporter = new DiffTreeLineGraphExporter(diffTree);
        final String result = exporter.export(options);
        return new Pair<>(exporter.getDebugData(), result);
    }

    /**
     * Writes the given commitDiff in line graph format to the given StringBuilder.
     * @param commitDiff The diff to convert to line graph format.
     * @param lineGraph The string builder to write the result to.
     * @param treeCounter The number of the first diff tree to export.
     * @return The number of the next diff tree to export (updated value of treeCounter).
     */
    public static Pair<DiffTreeSerializeDebugData, Integer> toLineGraphFormat(final CommitDiff commitDiff, final StringBuilder lineGraph, int treeCounter, final Options options) {
        final DiffTreeSerializeDebugData debugData = new DiffTreeSerializeDebugData();

        final String hash = commitDiff.getCommitHash();
        for (final PatchDiff patchDiff : commitDiff.getPatchDiffs()) {
            if (patchDiff.isValid()) {
                //Logger.info("  Exporting DiffTree #" + treeCounter);
                final Pair<DiffTreeSerializeDebugData, String> patchDiffLg;
                try {
                    patchDiffLg = toLineGraphFormat(patchDiff.getDiffTree(), options);
                } catch (Exception e) {
                    options.onError.accept(patchDiff, e);
                    break;
                }

                debugData.mappend(patchDiffLg.getKey());

                if (!patchDiffLg.getValue().isEmpty()) {
                    lineGraph
//                        .append("t # ").append(treeCounter)
                            .append("t # ").append(patchDiff.getFileName()).append(TREE_NAME_SEPARATOR).append(hash)
                            .append(StringUtils.LINEBREAK)
                            .append(patchDiffLg.getValue())
                            .append(StringUtils.LINEBREAK)
                            .append(StringUtils.LINEBREAK);

                    ++treeCounter;
                }
            } else {
                Logger.info("  Skipping invalid patch for file " + patchDiff.getFileName() + " at commit " + hash);
            }
        }

        return new Pair<>(debugData, treeCounter);
    }
}

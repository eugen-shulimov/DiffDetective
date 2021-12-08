package diff.difftree.serialize;

import diff.PatchDiff;
import diff.difftree.DiffTree;
import diff.difftree.render.DiffTreeRenderer;
import diff.difftree.render.PatchDiffRenderer;
import diff.difftree.serialize.nodeformat.DiffTreeNodeLabelFormat;
import diff.difftree.serialize.treeformat.DiffTreeLabelFormat;
import diff.difftree.transform.DiffTreeTransformer;
import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Options necessary for exporting a line graph.
 * This records contains information for exporting a {@link DiffTree} into a line graph, such as the graph format and tree and node layouts.
 */
public record DiffTreeLineGraphExportOptions(GraphFormat format, 
		DiffTreeLabelFormat treeParser, 
		DiffTreeNodeLabelFormat nodeParser,
		boolean skipEmptyTrees,
        List<DiffTreeTransformer> treePreProcessing,
        BiConsumer<PatchDiff, Exception> onError) {
	
    public DiffTreeLineGraphExportOptions(GraphFormat graphFormat, DiffTreeLabelFormat treeParser, DiffTreeNodeLabelFormat nodeParser) {
        this(graphFormat, treeParser, nodeParser, false, new ArrayList<>(), LogError());
    }

    public static BiConsumer<PatchDiff, Exception> LogError() {
        return (p, e) -> Logger.error(e);
    }

    public static BiConsumer<PatchDiff, Exception> RenderError() {
        final PatchDiffRenderer errorRenderer = PatchDiffRenderer.ErrorRendering(DiffTreeRenderer.WithinDiffDetective());
        return (p, e) -> {
            Logger.error(e);
            Logger.error("Rendering patch");
            errorRenderer.render(p);
        };
    }

    public static BiConsumer<PatchDiff, Exception> SysExitOnError() {
        return (p, e) -> System.exit(0);
    }
    
}
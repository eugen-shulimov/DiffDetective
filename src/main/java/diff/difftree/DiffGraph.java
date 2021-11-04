package diff.difftree;

import java.util.Collection;

public final class DiffGraph {
    private DiffGraph() {}

    private static boolean hasNoParents(final DiffNode node) {
        return node.getBeforeParent() == null && node.getAfterParent() == null;
    }

    /**
     * Invokes {@link DiffGraph::fromNodes(Collection<DiffNode>)} with an unknown DiffTreeSource.
     */
    public static DiffTree fromNodes(final Collection<DiffNode> nodes) {
        return fromNodes(nodes, DiffTreeSource.Unknown);
    }

    /**
     * Takes a set of DiffNodes that forms a DiffGraph (i.e., similar to a DiffTree but with no explicit root)
     * and converts it to a DiffTree by equipping it with a synthetic root node.
     * @param nodes a DiffGraph
     * @param source the source where the DiffGraph came from.
     * @return A DiffTree representing the DiffGraph with a synthetic root node.
     * see DiffGraph.fromNodes(Collection<DiffNode>)
     */
    public static DiffTree fromNodes(final Collection<DiffNode> nodes, final DiffTreeSource source) {
        final DiffNode newRoot = DiffNode.createRoot();
        nodes.stream()
                .filter(DiffGraph::hasNoParents)
                .forEach(n ->
                        n.diffType.matchBeforeAfter(
                                () -> newRoot.addBeforeChild(n),
                                () -> newRoot.addAfterChild(n)
                        ));
        return new DiffTree(newRoot, source);
    }
}

package org.variantsync.diffdetective.variation.diff.transform;

import org.variantsync.diffdetective.diff.text.DiffLineNumber;
import org.variantsync.diffdetective.variation.DiffLinesLabel;
import org.variantsync.diffdetective.variation.diff.DiffNode;
import org.variantsync.diffdetective.variation.diff.VariationDiff;
import org.variantsync.diffdetective.variation.diff.DiffType;
import org.variantsync.diffdetective.variation.diff.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Starfold reduces redundancy in edited leaves.
 * It identifies stars and simplifies its arms.
 * A star is a non-edited annotation node together with all its children
 * that are artifact nodes and leaves.
 * The Starfold collapses all these children to a single child for each time.
 * This means, all inserted star-children will be merged into a single child, and for deletions respectively.
 * @author Paul Bittner, Benjamin Moosherr
 */
public class Starfold implements VariationDiffTransformer<DiffLinesLabel> {
    private final boolean respectNodeOrder;

    private Starfold(boolean respectNodeOrder) {
        this.respectNodeOrder = respectNodeOrder;
    }

    /**
     * Create a new Starfold that respects node order.
     * It will not fold a star such that the order of children is mixed up after the fold.
     * @return A new order-respecting Starfold.
     */
    public static Starfold RespectNodeOrder() {
        return new Starfold(true);
    }

    /**
     * Create a new Starfold that ignores node order.
     * It will fold stars aggressively and might shuffle the order of children below a star-root.
     * @return A new order-ignoring Starfold.
     */
    public static Starfold IgnoreNodeOrder() {
        return new Starfold(false);
    }

    @Override
    public void transform(VariationDiff<DiffLinesLabel> variationDiff) {
        // All non-artifact nodes are potential roots of stars.
        final List<DiffNode<DiffLinesLabel>> annotations = variationDiff.computeAllNodesThat(Starfold::isStarRoot);
//        System.out.println("Inspecting " + annotations.size() + " star roots.");
        for (DiffNode<DiffLinesLabel> annotation : annotations) {
//            System.out.println("Found star root " + annotation);
            foldStar(annotation);
        }
    }

    private void foldStar(final DiffNode<DiffLinesLabel> starRoot) {
        // We fold the stars for each time respectively.
        Time.forAll(t -> foldStarAtTime(starRoot, t));
    }

    private void foldStarAtTime(final DiffNode<DiffLinesLabel> starRoot, Time time) {
//        System.out.println("Fold " + starRoot + " at time " + time);
        final DiffType targetDiffType = DiffType.thatExistsOnlyAt(time);
        final List<DiffNode<DiffLinesLabel>> starArms = new ArrayList<>();

        for (DiffNode<DiffLinesLabel> child : starRoot.getAllChildren()) {
            if (!starRoot.isChild(child, time)) {
                continue;
            }

            if (isStarArm(child) && child.diffType == targetDiffType) {
//                System.out.println("  Found arm " + child);
                starArms.add(child);
            } else if (respectNodeOrder && !starArms.isEmpty() && child.isNon()) {
                // If we find a non-edited node, we cannot continue grouping arm nodes without invalidating the order
                // of the nodes. We thus have to merge and restart after the non-edited node.
//                System.out.println("  Found blocker " + child);
                mergeArms(starRoot, time, targetDiffType, starArms);
                starArms.clear();
            }
        }

        mergeArms(starRoot, time, targetDiffType, starArms);
    }

    private void mergeArms(final DiffNode<DiffLinesLabel> starRoot, Time time, final DiffType targetDiffType, final List<DiffNode<DiffLinesLabel>> starArms) {
        // If there is more than one arm, merge.
        if (starArms.size() > 1) {
            final int targetIndex = starRoot.indexOfChild(starArms.get(0), time);
            starRoot.removeChildren(starArms);
            starRoot.insertChild(
                    DiffNode.createArtifact(
                            targetDiffType,
                            DiffLineNumber.Invalid(),
                            DiffLineNumber.Invalid(),
                            new DiffLinesLabel(starArms.stream().flatMap(node -> node.getLabel().getDiffLines().stream()).toList())
                    ),
                    targetIndex,
                    time
            );
        }
    }

    private static boolean isStarRoot(final DiffNode<?> node) {
        return !node.isArtifact() && node.isNon();
    }

    private static boolean isStarArm(final DiffNode<?> node) {
        return node.isLeaf() && node.isArtifact();
    }
}

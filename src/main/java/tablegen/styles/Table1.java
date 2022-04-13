package tablegen.styles;

import pattern.atomic.AtomicPattern;
import pattern.atomic.proposed.ProposedAtomicPatterns;
import tablegen.Row;
import tablegen.TableDefinition;
import tablegen.TableGenerator;
import tablegen.rows.ContentRow;
import tablegen.rows.HLine;

import java.util.ArrayList;
import java.util.List;

import static tablegen.Alignment.*;

public class Table1 extends TableDefinition {
    public Table1() {
        super(new ArrayList<>());

        this.columnDefinitions.addAll(List.of(
                col("Name", LEFT, row -> row.dataset().name()),
                col("Domain", LEFT, row -> row.dataset().domain()),
                col("\\#total commits", RIGHT_DASH, row -> makeReadable(row.results().totalCommits)),
                col("\\#processed commits", RIGHT, row -> makeReadable(row.results().exportedCommits)),
                col("\\#diffs", RIGHT, row -> makeReadable(row.results().exportedTrees))
        ));

        for (final AtomicPattern a : ProposedAtomicPatterns.Instance.all()) {
            this.columnDefinitions.add(col(a.getName(), RIGHT, row ->  makeReadable(row.results().atomicPatternCounts.getOccurences().get(a).getTotalAmount())));
        }

        this.columnDefinitions.add(col("runtime (s)", RIGHT, row -> makeReadable(row.results().runtimeInSeconds)));
    }

    @Override
    public List<? extends Row> sortAndFilter(final List<ContentRow> rows, final ContentRow ultimateResult) {
        final List<Row> res = new ArrayList<>(TableGenerator.alphabeticallySorted(rows));

        res.add(new HLine());
        res.add(new HLine());
        res.add(ultimateResult);

        return res;
    }
}
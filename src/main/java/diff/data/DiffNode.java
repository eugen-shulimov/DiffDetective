package diff.data;

import org.apache.commons.lang3.builder.Diff;
import org.pmw.tinylog.Logger;
import org.prop4j.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of a node of the diff tree.
 *
 * Includes methods for creating a node by getting its code type and diff type and for getting the feature mapping of the node.
 */
public class DiffNode {

    final static String ifRegex = "^[+-]?\\s*#\\s*if.*$";
    final static String endifRegex = "^[+-]?\\s*#\\s*endif.*$";
    final static String elseRegex = "^[+-]?\\s*#\\s*else.*$";
    final static String elifRegex = "^[+-]?\\s*#\\s*elif.*$";

    final static String addCharacter = "+";
    final static String remCharacter = "-";

    public static final String EQUAL_PLACEHOLDER = "__eq__";
    public static final String TRUE_LITERAL_NAME = "__true__";
    public static final String INVALID_ANNOTATION = "__INVALID_ANNOTATION__";

    public DiffType diffType;
    public CodeType codeType;

    private int fromLine = -1; // including
    private int toLine = -1; // excluding

    private Node featureMapping;

    private DiffNode beforeParent;
    private DiffNode afterParent;

    private final List<DiffNode> children;

    public DiffNode(DiffType diffType, CodeType codeType, int fromLine, int toLine,
                    Node featureMapping, DiffNode beforeParent, DiffNode afterParent) {
        this.diffType = diffType;
        this.codeType = codeType;
        this.fromLine = fromLine;
        this.toLine = toLine;
        this.featureMapping = featureMapping;
        this.beforeParent = beforeParent;
        this.afterParent = afterParent;
        this.children = new ArrayList<>();
    }

    public void addChild(DiffNode child){
        this.children.add(child);
    }

    public void setCorrespondingLines(int from, int to) {
        this.fromLine = from;
        this.toLine = to;
    }

    private DiffNode() {
        this.children = new ArrayList<>();
    }

    /**
     * Creates a DiffNode from a line and two parents
     *
     * @param line The line which the new node node corresponds to
     * @param beforeParent The before parent of the new node
     * @param afterParent The after parent of the new noe
     * @return A DiffNode with a code type, diff type, feature mapping and parents
     */
    public static DiffNode fromLine(String line, DiffNode beforeParent, DiffNode afterParent) {
        DiffNode diffNode = new DiffNode();
        diffNode.diffType = getDiffType(line);
        diffNode.codeType = getCodeType(line);

        if (diffNode.isCode() || diffNode.isEndif() || diffNode.isElse()) {
            diffNode.featureMapping = null;
        } else {
            diffNode.featureMapping = parseFeatureMapping(line);
        }

        if(!diffNode.isAdd()) {
            diffNode.beforeParent = beforeParent;
        }
        if(!diffNode.isRem()) {
            diffNode.afterParent = afterParent;
        }
        return diffNode;
    }

    /**
     * Gets a feature mapping from an annotation line using a NodeReader
     * @param line The line of which to get the feature mapping
     * @return The feature mapping of the given line
     */
    private static Node parseFeatureMapping(String line) {
        String fmString = getFMString(line);

        Node node = null;
        if (fmString != null) {
            NodeReader nodeReader = new NodeReader();
            nodeReader.activateJavaSymbols();
            node = nodeReader.stringToNode(fmString);
        } else {
            fmString = INVALID_ANNOTATION;
        }

        if (node == null) {
            Logger.warn("Could not parse feature mapping of line \"{}\"", line);
            node = new Literal(fmString);
        }

        // negate for ifndef
        if (line.contains("ifndef")) {
            node = new Not(node);
        }

        return node;

    }

    /**
     * Gets the feature mapping as a String from an annotation line
     * @param line The line of which to get the feature mapping
     * @return The feature mapping as a String of the given line
     */
    private static String getFMString(String line) {
        // ^[+-]?\s*#\s*(if|ifdef|ifndef|elif)(\s+(.*)|\((.*)\))$
        String regex = "^[+-]?\\s*#\\s*(if|ifdef|ifndef|elif)(\\s+(.*)|\\((.*)\\))$";
        Pattern regexPattern = Pattern.compile(regex);
        Matcher matcher = regexPattern.matcher(line);

        String fm;
        if (matcher.find()) {
            if (matcher.group(3) != null) {
                fm = matcher.group(3);
            } else {
                fm = matcher.group(4);
            }
        } else {
            return null;
        }

        // remove comments
        fm = fm.split("//")[0];
        fm = fm.replaceAll("/\\*.*\\*/", "");

        // remove whitespace
        fm = fm.trim();

        // remove defined(), ENABLED() and DISABLED()
        fm = fm.replaceAll("defined\\s*\\(([^)]*)\\)", "$1");
        fm = fm.replaceAll("defined ", " ");
        fm = fm.replaceAll("ENABLED\\s*\\(([^)]*)\\)", "$1");
        fm = fm.replaceAll("DISABLED\\s*\\(([^)]*)\\)", "!($1)");

        // remove whitespace

        fm = fm.replaceAll("\\s", "");

        // remove parentheses from custom cpp functions such as MB() or PIN_EXISTS()
        fm = fm.replaceAll("(\\w+)\\((\\w*)\\)", "$1__$2");

        // replace all "=="'s with a placeholder because NodeReader parses these
        fm = fm.replaceAll("==", EQUAL_PLACEHOLDER);

        return fm;
    }

    private static CodeType getCodeType(String line) {
        if (line.matches(ifRegex)) {
            return CodeType.IF;

        } else if (line.matches(endifRegex)) {
            return CodeType.ENDIF;

        } else if (line.matches(elseRegex)) {
            return CodeType.ELSE;

        } else if (line.matches(elifRegex)) {
            return CodeType.ELIF;

        } else {
            return CodeType.CODE;
        }
    }

    private static DiffType getDiffType(String line) {
        if (line.startsWith(addCharacter)) {
            return DiffType.ADD;
        } else if (line.startsWith(remCharacter)) {
            return DiffType.REM;
        } else {
            return DiffType.NON;
        }
    }

    /**
     * Creates a (new) root node
     * @return A (new) root node
     */
    public static DiffNode createRoot() {
        return new DiffNode(
                DiffType.NON,
                CodeType.ROOT,
                -1, -1,
                // new True() sadly does not work
                new Literal(TRUE_LITERAL_NAME),
                null,
                null
        );
    }

    /**
     * Gets the first if node in the path following the before parent
     * @return The first if node in the path following the before parent
     */
    public DiffNode getBeforeIfNode() {
        if (isIf()) {
            return this;
        }
        if (isRoot()) {
            return null;
        }
        return beforeParent.getBeforeIfNode();
    }

    /**
     * Gets the first if node in the path following the after parent
     * @return The first if node in the path following the after parent
     */
    public DiffNode getAfterIfNode() {
        if (isIf()) {
            return this;
        }
        if (isRoot()) {
            return null;
        }
        return afterParent.getAfterIfNode();
    }

    /**
     * Gets the depth of the diff tree following the before parent
     * @return the depth of the diff tree following the before parent
     */
    public int getBeforeDepth(){
        if (isRoot()) {
            return 0;
        }

        if (isIf()) {
            return beforeParent.getBeforeDepth() + 1;
        }

        return beforeParent.getBeforeDepth();
    }

    /**
     * Gets the depth of the diff tree following the after parent
     * @return the depth of the diff tree following the after parent
     */
    public int getAfterDepth(){
        if (isRoot()) {
            return 0;
        }

        if (isIf()) {
            return afterParent.getAfterDepth() + 1;
        }

        return afterParent.getAfterDepth();
    }

    /**
     * Gets the amount of nodes with diff type REM in the path following the before parent
     * @return the amount of nodes with diff type REM in the path following the before parent
     */
    public int getRemAmount() {
        if (isRoot()) {
            return 0;
        }

        if (isIf() && diffType.equals(DiffType.REM)) {
            return beforeParent.getRemAmount() + 1;
        }

        if (isElif() || isElse() && diffType.equals(DiffType.REM)) {
            // if this is a removed elif or else we do not want to count the other branches of
            // this annotation
            // we thus go up the tree until we get the next if and continue with the parent of it
            return beforeParent.getBeforeIfNode().beforeParent.getRemAmount() + 1;
        }

        return beforeParent.getRemAmount();
    }

    /**
     * Gets the amount of nodes with diff type ADD in the path following the after parent
     * @return the amount of nodes with diff type ADD in the path following the after parent
     */
    public int getAddAmount() {

        if (isRoot()) {
            return 0;
        }

        if (isIf() && diffType.equals(DiffType.ADD)) {
            return afterParent.getAddAmount() + 1;
        }

        if (isElif() || isElse() && diffType.equals(DiffType.ADD)) {
            // if this is an added elif or else we do not want to count the other branches of
            // this annotation
            // we thus go up the tree until we get the next if and continue with the parent of it
            return afterParent.getAfterIfNode().afterParent.getAddAmount() + 1;
        }

        return afterParent.getAddAmount();
    }

    public DiffNode getBeforeParent() {
        return beforeParent;
    }

    public DiffNode getAfterParent() {
        return afterParent;
    }

    public void setToLine(int toLine) {
        this.toLine = toLine;
    }

    public void setFromLine(int fromLine) {
        this.fromLine = fromLine;
    }

    public int getFromLine() {
        return fromLine;
    }

    public int getToLine() {
        return toLine;
    }

    public Node getFeatureMapping() {
        return featureMapping;
    }

    public List<DiffNode> getChildren() {
        return children;
    }

    public void dropChildren() {
        for (final DiffNode child : children) {
            if (child.beforeParent == this) {
                child.beforeParent = null;
            }
            if (child.afterParent == this) {
                child.afterParent = null;
            }
        }

        children.clear();
    }

    /**
     * Gets the feature mapping of the node after the patch
     * @return the feature mapping of the node after the patch
     */
    public Node getAfterFeatureMapping() {
        if (isElse()) {
            return new Not(getAfterParent().getAfterFeatureMapping());
        } else if (isElif()) {
            return new And(featureMapping, new Not(getAfterParent().getAfterFeatureMapping()));
        } else if(isCode()) {
            return afterParent.getAfterFeatureMapping();
        }
        return featureMapping;
    }

    /**
     * Gets the feature mapping of the node before the patch
     * @return the feature mapping of the node before the patch
     */
    public Node getBeforeFeatureMapping() {
        if (isElse()) {
            return new Not(getBeforeParent().getBeforeFeatureMapping());
        } else if (isElif()) {
            return new And(featureMapping, new Not(getBeforeParent().getBeforeFeatureMapping()));
        } else if(isCode()) {
            return beforeParent.getBeforeFeatureMapping();
        }
        return featureMapping;
    }

    public enum DiffType {
        ADD("+"),
        REM("-"),
        NON(" ");

        public final String name;

        DiffType(String name) {
            this.name = name;
        }
    }

    public enum CodeType {
        IF("if"),
        ENDIF("endif"),
        ELSE("else"),
        ELIF("elif"),
        CODE("code"),
        ROOT("ROOT");
        public final String name;

        CodeType(String name) {
            this.name = name;
        }
    }

    public boolean isRem() {
        return this.diffType.equals(DiffType.REM);
    }

    public boolean isNon() {
        return this.diffType.equals(DiffType.NON);
    }

    public boolean isAdd() {
        return this.diffType.equals(DiffType.ADD);
    }

    public boolean isElif() {
        return this.codeType.equals(CodeType.ELIF);
    }

    public boolean isIf() {
        return this.codeType.equals(CodeType.IF);
    }

    public boolean isCode() {
        return this.codeType.equals(CodeType.CODE);
    }

    public boolean isEndif() {
        return this.codeType.equals(CodeType.ENDIF);
    }

    public boolean isElse() {
        return this.codeType.equals(CodeType.ELSE);
    }

    public boolean isRoot() {
        return this.codeType.equals(CodeType.ROOT);
    }

    /**
     * @return An integer that uniquely identifiers this DiffNode within its patch.
     */
    public int getID() {
        return 1 + fromLine;
    }

    public String toLineGraphFormat() {
        return "v " + getID() + " " + diffType + "_" + codeType;
    }

    @Override
    public String toString() {
        String s;
        if (isCode()) {
            s = String.format("%s_%s: (%d-%d)", diffType, codeType, fromLine, toLine);
        } else if (isRoot()) {
            s = "ROOT";
        } else {
            s = String.format("%s_%s: (%d-%d), fm: %s", diffType, codeType,
                    fromLine, toLine, featureMapping);
        }
        return s;
    }
}

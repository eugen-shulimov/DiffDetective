package org.variantsync.diffdetective.feature;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.variantsync.diffdetective.feature.antlr.CExpressionLexer;
import org.variantsync.diffdetective.feature.antlr.CExpressionParser;

/**
 * Visitor that controls how subtrees are evaluated further.
 */
@SuppressWarnings("CheckReturnValue")
public class ControllingCExpressionVisitor extends BasicCExpressionVisitor {
	private final AbstractingCExpressionVisitor abstractingVisitor = new AbstractingCExpressionVisitor();

	public ControllingCExpressionVisitor() {}

	public String simplify(String formula) {
		CExpressionLexer lexer = new CExpressionLexer(CharStreams.fromString(formula));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CExpressionParser parser = new CExpressionParser(tokens);
		ParseTree tree = parser.conditionalExpression();
		return tree.accept(this).toString();
	}

	// conditionalExpression
	//    :   logicalOrExpression ('?' conditionalExpression ':' conditionalExpression)?
	//    ;
	@Override public StringBuilder visitConditionalExpression(CExpressionParser.ConditionalExpressionContext ctx) {
		if (!ctx.conditionalExpression().isEmpty()) {
			// logicalOrExpression '?' conditionalExpression ':' conditionalExpression
			// We have to abstract the expression if it is a ternary expression
			return ctx.accept(abstractingVisitor);
		} else {
			// logicalOrExpression
			return ctx.logicalOrExpression().accept(this);
		}
	}

	// multiplicativeExpression
	//    :   primaryExpression (('*'|'/'|'%') primaryExpression)*
	//    ;
	@Override public StringBuilder visitMultiplicativeExpression(CExpressionParser.MultiplicativeExpressionContext ctx) {
		if (ctx.primaryExpression().size() > 1) {
			// primaryExpression (('*'|'/'|'%') primaryExpression)+
			// We have to abstract the arithmetic expression if there is more than one operand
			return ctx.accept(abstractingVisitor);
		} else {
			// primaryExpression
			// There is exactly one child expression
			return ctx.primaryExpression(0).accept(this);
		}
	}

	// additiveExpression
	//    :   multiplicativeExpression (('+'|'-') multiplicativeExpression)*
	//    ;
	@Override public StringBuilder visitAdditiveExpression(CExpressionParser.AdditiveExpressionContext ctx) {
		if (ctx.multiplicativeExpression().size() > 1) {
			// multiplicativeExpression (('+'|'-') multiplicativeExpression)+
			// We have to abstract the arithmetic expression if there is more than one operand
			return ctx.accept(abstractingVisitor);
		} else {
			// multiplicativeExpression
			// There is exactly one child expression
			return ctx.multiplicativeExpression(0).accept(this);
		}
	}

	// shiftExpression
	//    :   additiveExpression (('<<'|'>>') additiveExpression)*
	//    ;
	@Override public StringBuilder visitShiftExpression(CExpressionParser.ShiftExpressionContext ctx) {
		if (ctx.additiveExpression().size() > 1) {
			// additiveExpression (('<<'|'>>') additiveExpression)+
			// We have to abstract the shift expression if there is more than one operand
			return ctx.accept(abstractingVisitor);
		} else {
			// additiveExpression
			// There is exactly one child expression
			return ctx.additiveExpression(0).accept(this);
		}
	}

	// relationalExpression
	//    :   shiftExpression (('<'|'>'|'<='|'>=') shiftExpression)*
	//    ;
	@Override public StringBuilder visitRelationalExpression(CExpressionParser.RelationalExpressionContext ctx) {
		if (ctx.shiftExpression().size() > 1) {
			// shiftExpression (('<'|'>'|'<='|'>=') shiftExpression)+
			// We have to abstract the relational expression if there is more than one operand
			return ctx.accept(abstractingVisitor);
		} else {
			// shiftExpression
			// There is exactly one child expression
			return ctx.shiftExpression(0).accept(this);
		}
	}

	// equalityExpression
	//    :   relationalExpression (('=='| '!=') relationalExpression)*
	//    ;
	@Override public StringBuilder visitEqualityExpression(CExpressionParser.EqualityExpressionContext ctx) {
		if (ctx.relationalExpression().size() > 1) {
			// relationalExpression (('=='| '!=') relationalExpression)+
			// We have to abstract the equality expression if there is more than one operand
			return ctx.accept(abstractingVisitor);
		} else {
			// relationalExpression
			// There is exactly one child expression
			return ctx.relationalExpression(0).accept(this);
		}
	}

	// andExpression
	//    :   equalityExpression ( '&' equalityExpression)*
	//    ;
	@Override public StringBuilder visitAndExpression(CExpressionParser.AndExpressionContext ctx) {
		if (ctx.equalityExpression().size() > 1) {
			// equalityExpression ( '&' equalityExpression)+
			// We have to abstract the 'and' expression if there is more than one operand
			return ctx.accept(abstractingVisitor);
		} else {
			// equalityExpression
			// There is exactly one child expression
			return ctx.equalityExpression(0).accept(this);
		}
	}

	// exclusiveOrExpression
	//    :   andExpression ('^' andExpression)*
	//    ;
	@Override public StringBuilder visitExclusiveOrExpression(CExpressionParser.ExclusiveOrExpressionContext ctx) {
		if (ctx.andExpression().size() > 1) {
			// andExpression ('^' andExpression)+
			// We have to abstract the xor expression if there is more than one operand
			return ctx.accept(abstractingVisitor);
		} else {
			// andExpression
			// There is exactly one child expression
			return ctx.andExpression(0).accept(this);
		}
	}

	// inclusiveOrExpression
	//    :   exclusiveOrExpression ('|' exclusiveOrExpression)*
	//    ;
	@Override public StringBuilder visitInclusiveOrExpression(CExpressionParser.InclusiveOrExpressionContext ctx) {
		if (ctx.exclusiveOrExpression().size() > 1) {
			// exclusiveOrExpression ('|' exclusiveOrExpression)+
			// We have to abstract the 'or' expression if there is more than one operand
			return ctx.accept(abstractingVisitor);
		} else {
			// exclusiveOrExpression
			// There is exactly one child expression
			return ctx.exclusiveOrExpression(0).accept(this);
		}
	}
}
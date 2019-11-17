package org.didelphis.language.automata.parsing;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.expressions.ParallelNode;
import org.didelphis.language.automata.expressions.ParentNode;
import org.didelphis.language.automata.expressions.TerminalNode;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults (level = AccessLevel.PRIVATE)
public final class ParserBuffer {

	boolean negative;
	boolean parallel;
	boolean capturing;

	String quantifier = "";
	String terminal   = "";

	List<Expression> nodes = new ArrayList<>();

	public boolean isEmpty() {
		return nodes.isEmpty() && terminal.isEmpty();
	}

	public @Nullable Expression toExpression() {
		if (nodes.isEmpty()) {
			return new TerminalNode(terminal, quantifier, negative);
		} else if (parallel) {
			return new ParallelNode(nodes, quantifier, negative);
		} else if (terminal == null || terminal.isEmpty()) {
			return new ParentNode(nodes, quantifier, negative, capturing);
		} else {
			return null;
		}
	}
}

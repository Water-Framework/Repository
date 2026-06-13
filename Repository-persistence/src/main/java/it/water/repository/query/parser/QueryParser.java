
/*
 * Copyright 2024 Aristide Cittadino
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.water.repository.query.parser;

import it.water.core.api.repository.query.Query;
import it.water.core.api.repository.query.QueryFilterOperation;
import it.water.core.api.repository.query.QueryFilterOperator;
import it.water.core.api.repository.query.operands.FieldNameOperand;
import it.water.core.api.repository.query.operands.FieldValueListOperand;
import it.water.core.api.repository.query.operands.FieldValueOperand;
import it.water.core.api.repository.query.operands.ParenthesisNode;
import it.water.core.api.repository.query.operations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author Aristide Cittadino.
 * Query Parser
 */
public class QueryParser {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(QueryParser.class.getName());

    /**
     * Generic, non-revealing parse error message. Internal token details are
     * intentionally NOT exposed to the caller to avoid leaking parser internals.
     */
    private static final String PARSE_ERROR_MSG = "Invalid query filter";

    /**
     * Maximum allowed length of a raw filter string. Filters longer than this are
     * rejected before tokenization as a defensive cap against CPU-DoS via oversized
     * input. Documented constant: QueryParser is instantiated directly (not as a
     * framework component) and has no access to ApplicationProperties, so an
     * Options-based property would require an invasive refactor of DefaultQueryBuilder.
     */
    private static final int MAX_FILTER_LENGTH = 4096;

    /**
     * Absolute backstop on the number of tokenizer advancements. Guards against any
     * residual non-advancing loop even if the per-iteration guard is bypassed.
     */
    private static final int MAX_ITERATIONS = 10000;

    private StreamTokenizer tokenizer;
    /**
     * Counts every tokenizer.nextToken() call. Used as an absolute backstop guard
     * counter to break any residual infinite loop, and as a cheap "did the tokenizer
     * advance?" heuristic for the parse() non-advancement detector.
     */
    private int tokenCount;
    /**
     * Running parenthesis-balance counter: incremented on '(', decremented on ')'.
     * A negative value (a ')' without a matching '(') or a non-zero value at EOF is a
     * parse error.
     */
    private int parenthesisDepth;
    private static List<QueryFilterOperation> availableOperations;

    static {
        availableOperations = new ArrayList<>();
        availableOperations.add(new AndOperation());
        availableOperations.add(new EqualTo());
        availableOperations.add(new GreaterOrEqualThan());
        availableOperations.add(new GreaterThan());
        availableOperations.add(new In());
        availableOperations.add(new Like());
        availableOperations.add(new LowerOrEqualThan());
        availableOperations.add(new LowerThan());
        availableOperations.add(new NotEqualTo());
        availableOperations.add(new NotOperation());
        availableOperations.add(new OrOperation());
    }

    public QueryParser(String filter) {
        // Defensive length cap: reject oversized filters before tokenizing to bound
        // CPU work and protect against DoS via huge input strings.
        if (filter != null && filter.length() > MAX_FILTER_LENGTH) {
            throw new IllegalArgumentException(PARSE_ERROR_MSG);
        }
        this.tokenizer = new StreamTokenizer(new StringReader(filter));
        tokenizer.resetSyntax();
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars(128 + 32, 255);
        tokenizer.whitespaceChars(0, ' ');
        tokenizer.quoteChar('"');
        tokenizer.quoteChar('\'');
        //enable @ as word char
        tokenizer.wordChars('@','@');
        //enable / as word char
        tokenizer.wordChars('/','/');
        tokenizer.wordChars('0','9');
        tokenizer.wordChars('.','.');
        tokenizer.wordChars('-','-');
        tokenizer.slashSlashComments(false);
        tokenizer.slashStarComments(false);
    }

    /**
     * Single choke point for advancing the tokenizer. Centralizes the absolute
     * backstop guard counter (MAX_ITERATIONS) and the parenthesis-balance tracking
     * so they hold no matter which parsing method advances the stream.
     */
    private int advance() throws IOException {
        if (++tokenCount > MAX_ITERATIONS) {
            // Absolute backstop: too many token advancements => malformed/abusive input.
            throw new IllegalArgumentException(PARSE_ERROR_MSG);
        }
        int type = tokenizer.nextToken();
        if (type == '(') {
            parenthesisDepth++;
        } else if (type == ')') {
            parenthesisDepth--;
            if (parenthesisDepth < 0) {
                // ')' without a matching '(' => unbalanced parentheses.
                throw new IllegalArgumentException(PARSE_ERROR_MSG);
            }
        }
        return type;
    }

    public Query parse() throws InstantiationException, IllegalAccessException, IOException, InvocationTargetException, NoSuchMethodException {
        Query result = null;
        advance();
        while (tokenizer.ttype != StreamTokenizer.TT_EOF) {
            // Snapshot tokenizer state before the call. If parseExpression returns
            // without consuming a token (same advancement count, still not EOF),
            // we would otherwise spin forever (e.g. an unbalanced trailing ')').
            int countBefore = tokenCount;
            result = this.parseExpression(result);
            if (tokenizer.ttype != StreamTokenizer.TT_EOF && tokenCount == countBefore) {
                // No progress on a non-EOF token => unparseable / offending token left behind.
                throw new IllegalArgumentException(PARSE_ERROR_MSG);
            }
        }
        // At end of input every '(' must have been matched by a ')'.
        if (parenthesisDepth != 0) {
            throw new IllegalArgumentException(PARSE_ERROR_MSG);
        }
        return result;
    }

    private Query parsePrimary(boolean parseField) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Query result = null;
        if (tokenizer.ttype == '(') {
            result = parseParenthesisNode(parseField);
        } else if (tokenizer.ttype == '"' || tokenizer.ttype == '\'' || tokenizer.ttype == StreamTokenizer.TT_WORD) {
            result = checkParseOperator(tokenizer.sval);
            if (result == null) {
                if (parseField) {
                    String fieldName = tokenizer.sval;
                    result = new FieldNameOperand(fieldName);
                } else result = new FieldValueOperand(tokenizer.sval);
            } else
                // returning directly in order to continue parsing
                return result;
        } else if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
            result = new FieldValueOperand(String.valueOf(tokenizer.nval));
        } else if (tokenizer.ttype != -4 /* TT_NOTHING */) {
            throw new IllegalArgumentException(PARSE_ERROR_MSG);
        }
        advance();
        return result;
    }

    private Query parseParenthesisNode(boolean parseField) throws IOException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Query innerFilter = null;
        advance();
        while (tokenizer.ttype != ')' && tokenizer.ttype != StreamTokenizer.TT_EOF) {
            // Guard against a non-advancing inner loop (e.g. an offending token that
            // neither matches a value/expression nor terminates the parenthesis).
            int countBefore = tokenCount;
            if(!parseField)
                innerFilter = parseValueList();
            else
                innerFilter = parseExpression(innerFilter);
            if (tokenizer.ttype != ')' && tokenizer.ttype != StreamTokenizer.TT_EOF && tokenCount == countBefore) {
                throw new IllegalArgumentException(PARSE_ERROR_MSG);
            }
        }
        if (tokenizer.ttype != ')') {
            // Unclosed '(' (reached EOF before ')').
            throw new IllegalArgumentException(PARSE_ERROR_MSG);
        }
        Query parenthesisNode = new ParenthesisNode();
        parenthesisNode.defineOperands(innerFilter);
        return parenthesisNode;
    }

    private Query parseValueList() throws IOException {
        List<Object> values = new ArrayList<>();

        while (tokenizer.ttype != ')' && tokenizer.ttype != StreamTokenizer.TT_EOF) {
            // Skip comma separator
            if (tokenizer.ttype == ',') {
                advance();
                continue;
            }

            // Parse the value based on token type
            Object value = null;
            if (tokenizer.ttype == '"' || tokenizer.ttype == '\'') {
                value = tokenizer.sval;
            } else if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                value = tokenizer.sval;
            } else if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                value = tokenizer.nval;
            }

            if (value != null) {
                values.add(value);
            }

            advance();
        }

        return new FieldValueListOperand(values);
    }

    private Query parseExpression(Query left) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (left == null) {
            left = parsePrimary(true);
        }

        if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
            return parseOperation(left, tokenizer.sval);
        } else if (tokenizer.ttype != StreamTokenizer.TT_NUMBER && tokenizer.ttype != StreamTokenizer.TT_EOF && tokenizer.ttype != ')') {
            StringBuilder sb = new StringBuilder();
            // Accumulate a symbolic operator (e.g. '=', '>=', '<'). Must test TT_EOF
            // so that an operator with no right-hand operand (e.g. "name=") does NOT
            // spin forever calling nextToken() past end of input.
            while (tokenizer.ttype != StreamTokenizer.TT_WORD && tokenizer.ttype != StreamTokenizer.TT_NUMBER
                    && tokenizer.ttype != '"' && tokenizer.ttype != '\'' && tokenizer.ttype != StreamTokenizer.TT_EOF) {
                char ch = (char) tokenizer.ttype;
                sb.append(ch);
                advance();
            }
            if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
                // Reached EOF while expecting the operator's right-hand operand => invalid.
                throw new IllegalArgumentException(PARSE_ERROR_MSG);
            }
            return parseOperation(left, sb.toString());
        }
        return left;
    }

    private Query parseOperation(Query firstOperand, String val) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for (int i = 0; i < availableOperations.size(); i++) {
            if (availableOperations.get(i).operator().equals(val)) {
                QueryFilterOperation operation = availableOperations.get(i).getClass().getConstructor().newInstance();
                Query nr = (Query) operation;
                if (operation.needsExpr()) {
                    advance();
                    nr.defineOperands(firstOperand, parseExpression(null));
                } else {
                    Query[] operands = new Query[operation.numOperands()];
                    parseOperands(operands, firstOperand, operation);
                    nr.defineOperands(operands);
                }
                return nr;
            }
        }
        throw new IllegalArgumentException(PARSE_ERROR_MSG);
    }

    private void parseOperands(Query[] operands, Query firstOperand, QueryFilterOperation operation) throws IOException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        operands[0] = firstOperand;
        //if is unary, then go ahead
        if (operation.numOperands() == 1) {
            advance();
        } else {
            for (int j = 1; j < operation.numOperands(); j++) {
                //go ahead if the current operator is a string not symbol
                if (tokenizer.ttype == StreamTokenizer.TT_WORD && tokenizer.sval.equalsIgnoreCase(operation.operator()))
                    advance();
                operands[j] = parsePrimary(false);
            }
        }
    }

    private Query checkParseOperator(String val) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (int i = 0; i < availableOperations.size(); i++) {
            if (availableOperations.get(i).operator().equals(val)) {
                QueryFilterOperation operation = availableOperations.get(i);
                if (operation instanceof QueryFilterOperator) {
                    Query nr = (Query) operation;
                    advance();
                    nr.defineOperands(parseExpression(null));
                    return nr;
                }
            }
        }
        return null;
    }
}

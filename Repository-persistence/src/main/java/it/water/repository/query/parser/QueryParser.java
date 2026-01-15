
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
    private StreamTokenizer tokenizer;
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

    public Query parse() throws InstantiationException, IllegalAccessException, IOException, InvocationTargetException, NoSuchMethodException {
        Query result = null;
        tokenizer.nextToken();
        while (tokenizer.ttype != StreamTokenizer.TT_EOF) {
            result = this.parseExpression(result);
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
            throw new IllegalArgumentException("Unrecognized token: " + tokenizer.ttype + "/" + tokenizer.sval);
        }
        tokenizer.nextToken();
        return result;
    }

    private Query parseParenthesisNode(boolean parseField) throws IOException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Query innerFilter = null;
        tokenizer.nextToken();
        while (tokenizer.ttype != ')' && tokenizer.ttype != StreamTokenizer.TT_EOF) {
            if(!parseField)
                innerFilter = parseValueList();
            else
                innerFilter = parseExpression(innerFilter);
        }
        if (tokenizer.ttype != ')') {
            throw new IllegalAccessException(") expected, got " + tokenizer.ttype + "/" + tokenizer.sval);
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
                tokenizer.nextToken();
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

            tokenizer.nextToken();
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
            while (tokenizer.ttype != StreamTokenizer.TT_WORD && tokenizer.ttype != StreamTokenizer.TT_NUMBER && tokenizer.ttype != '"' && tokenizer.ttype != '\'') {
                char ch = (char) tokenizer.ttype;
                sb.append(ch);
                tokenizer.nextToken();
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
                    tokenizer.nextToken();
                    nr.defineOperands(firstOperand, parseExpression(null));
                } else {
                    Query[] operands = new Query[operation.numOperands()];
                    parseOperands(operands, firstOperand, operation);
                    nr.defineOperands(operands);
                }
                return nr;
            }
        }
        throw new IllegalArgumentException("Impossibile to parse data...");
    }

    private void parseOperands(Query[] operands, Query firstOperand, QueryFilterOperation operation) throws IOException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        operands[0] = firstOperand;
        //if is unary, then go ahead
        if (operation.numOperands() == 1) {
            tokenizer.nextToken();
        } else {
            for (int j = 1; j < operation.numOperands(); j++) {
                //go ahead if the current operator is a string not symbol
                if (tokenizer.ttype == StreamTokenizer.TT_WORD && tokenizer.sval.equalsIgnoreCase(operation.operator()))
                    tokenizer.nextToken();
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
                    tokenizer.nextToken();
                    nr.defineOperands(parseExpression(null));
                    return nr;
                }
            }
        }
        return null;
    }
}

package com.teger.flscript;

import com.teger.flscript.tokenizer.Token;
import com.teger.flscript.exception.*;
import com.teger.flscript.tokenizer.TokenType;

import java.util.*;

public class FLInterpreter {
    public Map<String, Token> variable = new HashMap<>();
    public int ignoreBrace = 0;
    public Stack<int[]> statementRun = new Stack<>();
    public int nowNumber;

    private final FLScript plugin;

    public FLInterpreter(FLScript instance){
        this.plugin = instance;
    }

    public int run(String line, int lineNumber) throws VariableNotDefined, IllegalOperation, SyntaxErrorException, IllegalFunctionArgumentException, InvalidStatementException {
        this.nowNumber = lineNumber;
        Token[] tokens = Token.parse(line);
        return run(tokens);
    }

    public int run(Token[] tokens) throws VariableNotDefined, IllegalOperation, SyntaxErrorException, IllegalFunctionArgumentException, InvalidStatementException {
        if(containsRBrace(tokens)) {
            if(tokens.length > 1) {
                throw new InvalidStatementException("Braces cannot be used with other expressions on one line.", nowNumber);
            }
            if(statementRun.isEmpty()) {
                throw new InvalidStatementException("Braces is not matched.", nowNumber);
            }
            if(ignoreBrace != 0) {
                ignoreBrace --;
            } else {
                int[] statement = statementRun.pop();
                if(statement[1] == 2)
                    return statement[0];
            }
        } else {
            if(statementRun.isEmpty() || statementRun.peek()[1] != 1) {
                if(containsControlStatement(tokens)) {
                    runStatement(tokens);
                } else {
                    functionCalculate(tokens);
                    calculate(tokens);
                }
            } else {
                if(containsLBrace(tokens)) {
                    ignoreBrace ++;
                }
            }
        }

        return nowNumber+1;
    }

    public Token runNoneStatement(Token[] tokens) throws IllegalFunctionArgumentException, VariableNotDefined, IllegalOperation, SyntaxErrorException, InvalidStatementException {
        tokens = functionCalculate(tokens);
        tokens = calculate(tokens);
        return tokens[0];
    }

    private void runStatement(Token[] tokens) throws InvalidStatementException, VariableNotDefined, IllegalOperation, SyntaxErrorException, IllegalFunctionArgumentException {
        if(tokens.length < 5) {
            throw new InvalidStatementException("Statement start format must be <statement><bracket><condition><bracket><brace>", nowNumber);
        }
        Token statement = tokens[0];
        if(!(statement.getType().getCode() >= 0x50 && statement.getType().getCode() < 0x60)) {
            throw new InvalidStatementException("Statement start format must be <statement><bracket><condition><bracket><brace>", nowNumber);
        }
        Token start = tokens[1];
        if(!start.getType().equals(TokenType.lbracket)) {
            throw new InvalidStatementException("Statement start format must be <statement><bracket><condition><bracket><brace>", nowNumber);
        }
        int bracketCnt = 1;
        List<Token> innerToken = new ArrayList<>();
        for(int i = 2; i < tokens.length; i ++) {
            Token token = tokens[i];
            if(token.getType().equals(TokenType.rbracket)) {
                bracketCnt --;
            } else if(token.getType().equals(TokenType.lbracket)) {
                bracketCnt ++;
            }
            if(bracketCnt == 0) break;
            innerToken.add(token);
        }
        if(bracketCnt != 0) {
            throw new InvalidStatementException("Statement start format must be <statement><bracket><condition><bracket><brace>", nowNumber);
        }
        Token innerResult = runNoneStatement(innerToken.toArray(Token[]::new));
        if(innerResult.getType().equals(TokenType.identifier)) {
            if(innerResult.getName().toString().startsWith("$")) {
                Token tmp = plugin.globalVariable.get(innerResult.getName().toString());
                if(tmp == null) {
                    throw new VariableNotDefined(innerResult.getName().toString() + " is not defined.", nowNumber);
                }
                innerResult = tmp;
            } else {
                Token tmp = variable.get(innerResult.getName().toString());
                if(tmp == null) {
                    throw new VariableNotDefined(innerResult.getName().toString() + " is not defined.", nowNumber);
                }
                innerResult = tmp;
            }
        }
        if(innerResult.getType().getCode() != 0x12) {
            throw new InvalidStatementException("Condition is not bool.", nowNumber);
        }
        if(!tokens[tokens.length-1].getType().equals(TokenType.lbrace)) {
            throw new InvalidStatementException("Statement start format must be <statement><bracket><condition><bracket><brace>", nowNumber);
        }
        if(statement.getType().equals(TokenType._if)) {
            if(innerResult.getType().equals(TokenType._true)) {
                statementRun.push(new int[]{nowNumber, 0});
            } else {
                statementRun.push(new int[] {nowNumber, 1});
            }
        } else if(statement.getType().equals(TokenType._while)) {
            if(innerResult.getType().equals(TokenType._true)) {
                statementRun.push(new int[]{nowNumber, 2});
            } else {
                statementRun.push(new int[] {nowNumber, 1});
            }
        }
    }

    private boolean containsLBrace(Token[] tokens) {
        for(Token token : tokens)
            if(token.getType().equals(TokenType.lbrace))
                return true;
        return false;
    }

    private boolean containsRBrace(Token[] tokens) {
        for(Token token : tokens)
            if(token.getType().equals(TokenType.rbrace))
                return true;
        return false;
    }

    private boolean containsControlStatement(Token[] tokens) {
        for(Token token : tokens)
            if(token.getType().getCode() >= 0x50 && token.getType().getCode() < 0x60) {
                return true;
            }
        return false;
    }

    private Token[] functionCalculate(Token[] tokens) throws IllegalFunctionArgumentException, VariableNotDefined, IllegalOperation, SyntaxErrorException, InvalidStatementException {

        List<Token> compressed = new ArrayList<>();

        for(int i = 0; i < tokens.length; i ++) {
            Token token = tokens[i];
            if(token.getType().getCode() >= 0x30 && token.getType().getCode() < 0x40) {
                //getInnerToken
                List<Token> innerToken = new ArrayList<>();
                if(i+1 >= tokens.length) {
                    throw new IllegalFunctionArgumentException("There is no argument when calling a function.", nowNumber);
                }
                Token next = tokens[++i];
                if(!next.getType().equals(TokenType.lbracket)) {
                    throw new IllegalFunctionArgumentException("There is no argument when calling a function.", nowNumber);
                }
                int bracketCnt = 1;
                while(bracketCnt != 0) {
                    next = tokens[++i];
                    if(next.getType().equals(TokenType.rbracket)) {
                        bracketCnt --;
                    } else if(next.getType().equals(TokenType.lbracket)) {
                        bracketCnt ++;
                    }
                    if(bracketCnt != 0) {
                        innerToken.add(next);
                    }
                }
                //InnerToken Calculate
                Token innerResult = runNoneStatement(innerToken.toArray(Token[]::new));
                if(innerResult.getType().equals(TokenType.identifier)) {
                    if(innerResult.getName().toString().startsWith("$")) {
                        Token tmp = plugin.globalVariable.get(innerResult.getName().toString());
                        if(tmp == null) {
                            throw new VariableNotDefined(innerResult.getName().toString() + " is not defined.", nowNumber);
                        }
                        innerResult = tmp;
                    } else {
                        Token tmp = variable.get(innerResult.getName().toString());
                        if(tmp == null) {
                            throw new VariableNotDefined(innerResult.getName().toString() + " is not defined.", nowNumber);
                        }
                        innerResult = tmp;
                    }
                }
                Token result = Token.builder().type(TokenType.none).build();
                switch(token.getType()) {
                    case println -> {
                        System.out.println(innerResult.getName().toString());
                        result = Token.builder().type(TokenType.none).build();
                    }
                    case print -> {
                        System.out.print(innerResult.getName().toString());
                        result = Token.builder().type(TokenType.none).build();
                    }
                    case string_casting -> {
                        if(innerResult.getType().equals(TokenType._true))
                            result = Token.builder().type(TokenType.string).name("true").build();
                        else if(innerResult.getType().equals(TokenType._false))
                            result = Token.builder().type(TokenType.string).name("false").build();
                        else if(innerResult.getType().equals(TokenType.number))
                            result = Token.builder().type(TokenType.string).name(innerResult.getName().toString()).build();
                        else if(innerResult.getType().equals(TokenType.string))
                            result = innerResult;
                    }
                    case number_casting -> {
                        if(innerResult.getType().equals(TokenType._true))
                            result = Token.builder().type(TokenType.number).name("1").build();
                        else if(innerResult.getType().equals(TokenType._false))
                            result = Token.builder().type(TokenType.number).name("0").build();
                        else if(innerResult.getType().equals(TokenType.number))
                            result = innerResult;
                        else if(innerResult.getType().equals(TokenType.string)) {
                            try {
                                double x = Double.parseDouble(innerResult.getName().toString());
                                result = Token.builder()
                                        .type(TokenType.number)
                                        .name(Double.toString(x))
                                        .build();
                            } catch(NumberFormatException e) {
                                throw new NumberFormatException("Type Casting Error.");
                            }
                        }
                    }
                    case bool_casting -> {
                        if(innerResult.getType().equals(TokenType._true))
                            result = innerResult;
                        else if(innerResult.getType().equals(TokenType._false))
                            result = innerResult;
                        else if(innerResult.getType().equals(TokenType.number))
                            result = Token.builder()
                                    .type(Double.parseDouble(innerResult.getName().toString()) == 0 ? TokenType._false : TokenType._true)
                                    .build();
                        else if(innerResult.getType().equals(TokenType.string)) {
                            throw new NumberFormatException("Type Casting Error.");
                        }
                    }
                    case sin -> {
                        if(innerResult.getType().equals(TokenType.number)) {
                            result = Token.builder()
                                    .type(TokenType.number)
                                    .name(Double.toString(Math.sin(Double.parseDouble(innerResult.getName().toString()))))
                                    .build();
                        } else {
                            throw new IllegalFunctionArgumentException("Sin function need number argument.", nowNumber);
                        }
                    }
                    case cos -> {
                        if(innerResult.getType().equals(TokenType.number)) {
                            result = Token.builder()
                                    .type(TokenType.number)
                                    .name(Double.toString(Math.cos(Double.parseDouble(innerResult.getName().toString()))))
                                    .build();
                        } else {
                            throw new IllegalFunctionArgumentException("Sin function need number argument.", nowNumber);
                        }
                    }
                    case tan -> {
                        if(innerResult.getType().equals(TokenType.number)) {
                            result = Token.builder()
                                    .type(TokenType.number)
                                    .name(Double.toString(Math.tan(Double.parseDouble(innerResult.getName().toString()))))
                                    .build();
                        } else {
                            throw new IllegalFunctionArgumentException("Sin function need number argument.", nowNumber);
                        }
                    }
                    case to_radian -> {
                        if(innerResult.getType().equals(TokenType.number)) {
                            result = Token.builder()
                                    .type(TokenType.number)
                                    .name(Double.toString(Math.toRadians(Double.parseDouble(innerResult.getName().toString()))))
                                    .build();
                        } else {
                            throw new IllegalFunctionArgumentException("Sin function need number argument.", nowNumber);
                        }
                    }
                    case to_degree -> {
                        if(innerResult.getType().equals(TokenType.number)) {
                            result = Token.builder()
                                    .type(TokenType.number)
                                    .name(Double.toString(Math.toDegrees(Double.parseDouble(innerResult.getName().toString()))))
                                    .build();
                        } else {
                            throw new IllegalFunctionArgumentException("Sin function need number argument.", nowNumber);
                        }
                    }
                }
                if(!token.getType().equals(TokenType.print) && !token.getType().equals(TokenType.println) && result.getType().equals(TokenType.none)) {
                    throw new IllegalFunctionArgumentException("Not supported function.", nowNumber);
                }
                compressed.add(result);
            } else {
                compressed.add(token);
            }
        }
        return compressed.toArray(Token[]::new);
    }

    private Token[] calculate(Token[] tokens) throws VariableNotDefined, IllegalOperation, SyntaxErrorException {
        try {
            Stack<Token> result = new Stack<>();
            Stack<Token> operator = new Stack<>();

            for(Token token : tokens) {
                if(token.getType().getCode() >= 0x20 && token.getType().getCode() < 0x30) {
                    if(operator.isEmpty()) {
                        operator.push(token);
                    } else {
                        Token top = operator.peek();
                        if(!top.getType().equals(TokenType.lbracket)) {
                            if(token.getType().equals(TokenType.assign) || token.getType().equals(TokenType.pow)) {
                                if(top.getType().getCode() < token.getType().getCode()) {
                                    Token op = operator.pop();
                                    if(op.getType().equals(TokenType.not)) {
                                        result.push(calculateNot(result.pop()));
                                    } else {
                                        Token num1 = result.pop();
                                        Token num2 = result.pop();
                                        result.push(calculateNoneNot(op, num2, num1));
                                    }

                                }
                            } else {
                                if(top.getType().getCode() <= token.getType().getCode()) {
                                    Token op = operator.pop();
                                    if(op.getType().equals(TokenType.not)) {
                                        result.push(calculateNot(result.pop()));
                                    } else {
                                        Token num1 = result.pop();
                                        Token num2 = result.pop();
                                        result.push(calculateNoneNot(op, num2, num1));
                                    }

                                }
                            }
                        }
                        operator.push(token);
                    }
                } else if(token.getType().equals(TokenType.lbracket)) {
                    operator.push(token);
                } else if(token.getType().equals(TokenType.rbracket)) {
                    while(!operator.isEmpty() && !operator.peek().getType().equals(TokenType.lbracket)) {
                        Token op = operator.pop();
                        if(op.getType().equals(TokenType.not)) {
                            result.push(calculateNot(result.pop()));
                        } else {
                            Token num1 = result.pop();
                            Token num2 = result.pop();
                            result.push(calculateNoneNot(op, num2, num1));
                        }
                    }
                    operator.pop();
                } else {
                    result.add(token);
                }
            }
            while(!operator.isEmpty()) {
                Token op = operator.pop();
                if(op.getType().equals(TokenType.not)) {
                    result.push(calculateNot(result.pop()));
                } else {
                    Token num1 = result.pop();
                    Token num2 = result.pop();
                    result.push(calculateNoneNot(op, num2, num1));
                }

            }
            if(result.size() > 1)
                throw new SyntaxErrorException("The number of operands does not match.", nowNumber);
            return result.toArray(Token[]::new);
        } catch(EmptyStackException e) {
            throw new SyntaxErrorException("The number of operands does not match.", nowNumber);
        }
    }

    private Token calculateNot(Token num) throws IllegalOperation, VariableNotDefined {
        if(num.getType().equals(TokenType.identifier)) {
            if(num.getName().toString().startsWith("$")) {
                Token tmp = plugin.globalVariable.get(num.getName().toString());
                if(tmp == null) {
                    throw new VariableNotDefined(num.getName().toString() + " is not defined.", nowNumber);
                }
                num = tmp;
            } else {
                Token tmp = variable.get(num.getName().toString());
                if(tmp == null) {
                    throw new VariableNotDefined(num.getName().toString() + " is not defined.", nowNumber);
                }
                num = tmp;
            }

        }
        if(num.getType().equals(TokenType._true)
                || num.getType().equals(TokenType._false)) {
            return Token.builder()
                    .type(num.getType().equals(TokenType._true) ? TokenType._false : TokenType._true)
                    .build();
        } else {
            throw new IllegalOperation("Not operation can only operate bool values.", nowNumber);
        }
    }

    private Token calculateNoneNot(Token operator, Token num1, Token num2) throws VariableNotDefined, IllegalOperation {
        if(!operator.getType().equals(TokenType.assign)) {
            if(num1.getType().equals(TokenType.identifier)) {
                if(num1.getName().toString().startsWith("$")) {
                    Token tmp = plugin.globalVariable.get(num1.getName().toString());
                    if(tmp == null) {
                        throw new VariableNotDefined(num1.getName().toString() + " is not defined.", nowNumber);
                    }
                    num1 = tmp;
                } else {
                    Token tmp = variable.get(num1.getName().toString());
                    if(tmp == null) {
                        throw new VariableNotDefined(num1.getName().toString() + " is not defined.", nowNumber);
                    }
                    num1 = tmp;
                }
            }
        }
        if(num2.getType().equals(TokenType.identifier)) {
            if(num2.getName().toString().startsWith("$")) {
                Token tmp = plugin.globalVariable.get(num2.getName().toString());
                if(tmp == null) {
                    throw new VariableNotDefined(num2.getName().toString() + " is not defined.", nowNumber);
                }
                num2 = tmp;
            } else {
                Token tmp = variable.get(num2.getName().toString());
                if(tmp == null) {
                    throw new VariableNotDefined(num2.getName().toString() + " is not defined.", nowNumber);
                }
                num2 = tmp;
            }
        }
        if(operator.getType().equals(TokenType.plus)) {

            if(num1.getType().equals(TokenType.string)
                    || num2.getType().equals(TokenType.string)) {
                return Token.builder()
                        .type(TokenType.string)
                        .name(num1.getName().toString() + num2.getName().toString())
                        .build();
            } else if(num1.getType().equals(TokenType.number) && num2.getType().equals(TokenType.number)){
                return Token.builder()
                        .type(TokenType.number)
                        .name(Double.toString(Double.parseDouble(num1.getName().toString()) + Double.parseDouble(num2.getName().toString())))
                        .build();
            } else {
                throw new IllegalOperation("Plus operation can only operate string or number values.", nowNumber);
            }
        } else if(operator.getType().equals(TokenType.pow)
                || operator.getType().equals(TokenType.asterisk)
                || operator.getType().equals(TokenType.divide)
                || operator.getType().equals(TokenType.quotient)
                || operator.getType().equals(TokenType.mod)
                || operator.getType().equals(TokenType.minus)) {
            if(num1.getType().equals(TokenType.number) && num2.getType().equals(TokenType.number)){
                double x = Double.parseDouble(num1.getName().toString());
                double y = Double.parseDouble(num2.getName().toString());
                double result = 0.0;
                switch(operator.getType()) {
                    case pow -> result = Math.pow(x, y);
                    case asterisk -> result = x * y;
                    case divide -> result = x / y;
                    case quotient -> result = (int) x / (int) y;
                    case mod -> result = (int) x % (int) y;
                    case minus -> result = x - y;
                }
                return Token.builder()
                        .type(TokenType.number)
                        .name(Double.toString(result))
                        .build();
            } else {
                throw new IllegalOperation(operator.getType().toString().substring(0, 1).toUpperCase()
                        + operator.getType().toString().substring(1)
                        + " operation can only operate number values.", nowNumber);
            }
        } else if(operator.getType().equals(TokenType.equals)) {
            if(num1.getType() != num2.getType()) {
                return Token.builder()
                        .type(TokenType._false)
                        .build();
            }
            if(num1.getType().equals(TokenType._true) || num1.getType().equals(TokenType._false)) {
                if(num1.getType().equals(num2.getType())) {
                    return Token.builder()
                            .type(TokenType._true)
                            .build();
                } else {
                    return Token.builder()
                            .type(TokenType._false)
                            .build();
                }
            } else {
                if(num1.getName().equals(num2.getName())) {
                    return Token.builder()
                            .type(TokenType._true)
                            .build();
                } else {
                    return Token.builder()
                            .type(TokenType._false)
                            .build();
                }
            }
        } else if(operator.getType().equals(TokenType.greater)
                || operator.getType().equals(TokenType.less)) {
            if(num1.getType().equals(TokenType.number) && num2.getType().equals(TokenType.number)){
                double x = Double.parseDouble(num1.getName().toString());
                double y = Double.parseDouble(num2.getName().toString());
                boolean result = false;
                switch(operator.getType()) {
                    case greater -> result = x > y;
                    case less -> result = x < y;
                }
                return Token.builder()
                        .type(result ? TokenType._true : TokenType._false)
                        .build();
            } else {
                throw new IllegalOperation(operator.getType().toString().substring(0, 1).toUpperCase()
                        + operator.getType().toString().substring(1)
                        + " operation can only operate number values.", nowNumber);
            }
        } else if(operator.getType().equals(TokenType.xor)
                || operator.getType().equals(TokenType.and)
                || operator.getType().equals(TokenType.or)) {
            if(num1.getType().getCode() == 0x12 && num2.getType().getCode() == 0x12){
                boolean x = num1.getType().equals(TokenType._true);
                boolean y = num2.getType().equals(TokenType._true);
                boolean result = false;
                switch(operator.getType()) {
                    case xor -> result = x ^ y;
                    case and -> result = x && y;
                    case or -> result = x || y;
                }
                return Token.builder()
                        .type(result ? TokenType._true : TokenType._false)
                        .build();
            } else {
                throw new IllegalOperation(operator.getType().toString().substring(0, 1).toUpperCase()
                        + operator.getType().toString().substring(1)
                        + " operation can only operate bool values.", nowNumber);
            }
        } else if(operator.getType().equals(TokenType.assign)){
            if(num1.getType().equals(TokenType.identifier)) {
                if(num2.getType().getCode() >= 0x10 && num2.getType().getCode() < 0x20) {
                    if(num1.getName().toString().startsWith("$"))
                        plugin.globalVariable.put(num1.getName().toString(), num2);
                    else variable.put(num1.getName().toString(), num2);
                    return num2;
                } else {
                    throw new IllegalOperation("Cannot assign non-value.", nowNumber);
                }
            } else {
                throw new IllegalOperation("Cannot assign to a non-variable identifier.", nowNumber);
            }
        }
        throw new IllegalOperation("Unknown Operator.", nowNumber);
    }
}

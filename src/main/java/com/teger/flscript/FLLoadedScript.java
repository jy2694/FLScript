package com.teger.flscript;

import com.teger.flscript.tokenizer.Token;
import com.teger.flscript.exception.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FLLoadedScript {
    private final FLEventType type;
    private final List<String> scriptLines;
    private final FLScript plugin;

    public FLLoadedScript(FLScript instance, FLEventType type, String...strings) {
        plugin = instance;
        this.type = type;
        scriptLines = Arrays.asList(strings);
    }

    public FLLoadedScript(FLScript instance, FLEventType type, File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        List<String> lines = new ArrayList<>();
        String line;
        while((line = br.readLine()) != null) {
            lines.add(line);
        }
        this.plugin = instance;
        this.type = type;
        this.scriptLines = lines;
    }

    public FLEventType getType() {
        return type;
    }

    public void run(Map<String, Token> defaultVariable) throws VariableNotDefined, IllegalOperation, SyntaxErrorException, IllegalFunctionArgumentException, InvalidStatementException {
        FLInterpreter event = new FLInterpreter(plugin);
        for(String key : defaultVariable.keySet()) {
            event.variable.put(key, defaultVariable.get(key));
        }
        for(int i = 0; i < scriptLines.size(); ) {
            i = event.run(scriptLines.get(i), i);
        }
    }
}

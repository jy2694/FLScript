package com.teger.flscript;

import com.teger.flscript.exception.*;
import com.teger.flscript.tokenizer.Token;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class FLScript extends JavaPlugin {

    public Map<String, Token> globalVariable = new ConcurrentHashMap<>();
    public List<FLLoadedScript> loadedScriptList = new LinkedList<>();

    private final File folder = new File("plugins/FLScript");

    @Override
    public void onEnable() {
        loadScripts();
    }

    public void loadScripts() {
        loadedScriptList.clear();
        if(!folder.exists()) folder.mkdir();
        for(FLEventType type : FLEventType.values()){
            File eventFolder = new File("plugins/FLScript/" + type.toString());
            if(!eventFolder.exists()) eventFolder.mkdir();
            for(File scriptfile : Objects.requireNonNull(eventFolder.listFiles())){
                if(scriptfile.getName().endsWith(".flscript")){
                    try {
                        loadedScriptList.add(new FLLoadedScript(this, type, scriptfile));
                    } catch (IOException ignore) {}
                }
            }
        }
        Bukkit.getLogger().info("[FLScript] " + loadedScriptList.size() + " script's are loaded.");
    }

    public void runLoadedScriptByType(FLEventType type, Map<String, Token> variable){
        for(FLLoadedScript script : getLoadedScriptListByType(type))
            try {
                script.run(variable);
            } catch (VariableNotDefined | IllegalOperation | SyntaxErrorException | IllegalFunctionArgumentException |
                     InvalidStatementException ex) {
                Bukkit.getLogger().warning("[FLScript] " + ex.getMessage());
            }
    }
    private List<FLLoadedScript> getLoadedScriptListByType(FLEventType type){
        List<FLLoadedScript> scripts = new ArrayList<>();
        for(FLLoadedScript loadedScript : loadedScriptList)
            if(loadedScript.getType().equals(type))
                scripts.add(loadedScript);
        return scripts;
    }
}

package org.jenkinsci.plugins.scriptcolumn;

import groovy.lang.Binding;
import hudson.Extension;
import hudson.model.Item;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.SecureGroovyScript;
import org.kohsuke.stapler.DataBoundConstructor;

public class ScriptColumn extends ListViewColumn {

    private String columnCaption;

    private SecureGroovyScript script;

    @DataBoundConstructor
    public ScriptColumn(String columnCaption, SecureGroovyScript script) {
        this.columnCaption = columnCaption;
        this.script = script.configuringWithNonKeyItem();
    }

    @Override
    public String getColumnCaption() {
        return columnCaption;
    }

    public String getContent(Item item) {
        Binding b = new Binding();
        b.setVariable("item", item);

        try {
            return script.evaluate(Jenkins.getInstance().getPluginManager().uberClassLoader, b).toString();
        } catch (Exception e) {
            return "Script Error: " + e.getMessage();
        }
    }

    public SecureGroovyScript getScript() {
        return script;
    }

    @Extension
    public static class ScriptColumnDescriptor extends ListViewColumnDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.DisplayName();
        }

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getHelpFile() {
            return "/plugin/script-column/help.html";
        }
    }
}

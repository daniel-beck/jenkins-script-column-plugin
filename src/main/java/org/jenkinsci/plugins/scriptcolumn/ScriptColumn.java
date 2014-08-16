package org.jenkinsci.plugins.scriptcolumn;

import groovy.lang.Binding;
import hudson.Extension;
import hudson.model.Item;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.SecureGroovyScript;
import org.jenkinsci.plugins.scriptsecurity.scripts.ApprovalContext;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.swing.text.html.ListView;

public class ScriptColumn extends ListViewColumn {

    private String columnCaption;

    private SecureGroovyScript script;

    private boolean sandbox;

    @DataBoundConstructor
    public ScriptColumn(String columnCaption, SecureGroovyScript script, boolean sandbox) {
        this.columnCaption = columnCaption;
        this.script = script.configuringWithNonKeyItem();
        this.sandbox = sandbox;
    }

    @Override
    public String getColumnCaption() {
        return columnCaption    ;
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

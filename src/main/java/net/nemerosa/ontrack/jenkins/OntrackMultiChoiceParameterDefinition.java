package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OntrackMultiChoiceParameterDefinition extends AbstractOntrackMultipleParameterDefinition {

    @DataBoundConstructor
    public OntrackMultiChoiceParameterDefinition(String name, String description, String dsl, boolean sandbox, String valueProperty, String injectProperties) {
        super(name, description, dsl, sandbox, valueProperty, injectProperties);
    }

    public OntrackMultiChoiceParameterDefinition(String name, String description, String dsl, boolean sandbox, String valueProperty, Map<String, Object> bindings) {
        super(name, description, dsl, sandbox, valueProperty, bindings);
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        Object object = jo.get("value");
        List<String> choices = getChoices();
        List<String> selectionList = new ArrayList<>();
        if (object instanceof Boolean && !choices.isEmpty()) {
            Boolean selected = (Boolean) object;
            String firstOption = choices.get(0);
            if (selected) {
                selectionList.add(firstOption);
            }
        } else if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            for (int i = 0; i < choices.size(); i++) {
                String choice = choices.get(i);
                if (i < jsonArray.size() && jsonArray.get(i) instanceof Boolean && (Boolean) jsonArray.get(i)) {
                    selectionList.add(choice);
                }
            }
        }
        String selections = StringUtils.join(selectionList, ',');
        StringParameterValue value = new StringParameterValue(getName(), selections, getDescription());
        return value;
    }

    @Override
//    @Deprecated ???
    public ParameterValue createValue(StaplerRequest req) {
        String[] value = req.getParameterValues(getName());
        String selections = StringUtils.join(value, ',');
        return new StringParameterValue(getName(), selections, getDescription());
    }

    @Override
    public StringParameterValue getDefaultParameterValue() {
        return new StringParameterValue(getName(), "", getDescription());
    }

    @Extension
    @Symbol("ontrackMultiChoiceParam")
    public static class DescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return "Ontrack: Multi Parameter choice";
        }

        @Override
        public String getHelpFile() {
            return "/help/ontrack/parameter-choice.html";
        }
    }
}

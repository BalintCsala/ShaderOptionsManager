package me.balintcsala.data.options;

import java.util.List;

public class ConstOption extends Option {
    public ConstOption(Type type, String name, String defaultValue, String[] values, String comment) {
        super(type, name, defaultValue, values, comment);
    }

    @Override
    protected void changeLine(List<String> lines, int line) {
        if (type == Type.BOOLEAN) {
            lines.set(line, "const " + name + " = " + (getCurrentValue().equals("ON") ? "true" : "false") + "; // " + comment);
        } else {
            lines.set(line, "const " + name + " =  " + getCurrentValue() + "; // " + comment);
        }
    }
}

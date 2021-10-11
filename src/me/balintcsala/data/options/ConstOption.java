package me.balintcsala.data.options;

import java.util.HashMap;
import java.util.List;

public class ConstOption extends Option {

    private static HashMap<String, String> TYPES = new HashMap<>();

    static {
        TYPES.put("shadowMapResolution", "int");
        TYPES.put("shadowDistance", "float");
        TYPES.put("shadowDistanceRenderMul", "float");
        TYPES.put("shadowIntervalSize", "float");
        TYPES.put("generateShadowMipmap", "bool");
        TYPES.put("generateShadowColorMipmap", "bool");
        TYPES.put("shadowHardwareFiltering", "bool");
        TYPES.put("shadowHardwareFiltering0", "bool");
        TYPES.put("shadowHardwareFiltering1", "bool");
        TYPES.put("shadowtex0Mipmap", "bool");
        TYPES.put("shadowtexMipmap", "bool");
        TYPES.put("shadowtex1Mipmap", "bool");
        TYPES.put("shadowcolor0Mipmap", "bool");
        TYPES.put("shadowColor0Mipmap", "bool");
        TYPES.put("shadowcolor1Mipmap", "bool");
        TYPES.put("shadowColor1Mipmap", "bool");
        TYPES.put("shadowtex0Nearest", "bool");
        TYPES.put("shadowtexNearest", "bool");
        TYPES.put("shadow0MinMagNearest", "bool");
        TYPES.put("shadowtex1Nearest", "bool");
        TYPES.put("shadow1MinMagNearest", "bool");
        TYPES.put("shadowcolor0Nearest", "bool");
        TYPES.put("shadowColor0Nearest", "bool");
        TYPES.put("shadowColor0MinMagNearest", "bool");
        TYPES.put("shadowcolor1Nearest", "bool");
        TYPES.put("shadowColor1Nearest", "bool");
        TYPES.put("shadowColor1MinMagNearest", "bool");
        TYPES.put("wetnessHalflife", "float");
        TYPES.put("drynessHalflife", "float");
        TYPES.put("eyeBrightnessHalflife", "float");
        TYPES.put("centerDepthHalflife", "float");
        TYPES.put("sunPathRotation", "float");
        TYPES.put("ambientOcclusionLevel", "float");
        TYPES.put("superSamplingLevel", "int");
        TYPES.put("noiseTextureResolution", "int");
    }

    public ConstOption(Type type, String name, String defaultValue, String[] values, String comment) {
        super(type, name, defaultValue, values, comment);
    }

    @Override
    protected void changeLine(List<String> lines, int line) {
        String typeString = TYPES.get(name);
        if (type == Type.BOOLEAN) {
            lines.set(line, "const " + typeString + " "  + name + " = " + (getCurrentValue().equals("ON") ? "true" : "false") + "; // " + comment);
        } else {
            lines.set(line, "const " + typeString + " "  + name + " =  " + getCurrentValue() + "; // " + comment);
        }
    }
}

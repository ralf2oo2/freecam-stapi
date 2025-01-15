package ralf2oo2.freecam;

import net.glasslauncher.mods.gcapi3.api.*;

public class FreecamConfig {
    @ConfigRoot(value = "config", visibleName = "Freecam Config")
    public static ConfigFields config = new ConfigFields();

    public static class ConfigFields {
        @ConfigEntry(
                name = "Freecam Speed",
                maxLength = 1000
        )
        public Float speed = 10f;
        @ConfigEntry(
                name = "Enable Freecam Collisions"
        )
        public Boolean collision = true;
        @ConfigEntry(
                name = "Show Freecam"
        )
        public Boolean showCamera = true;
    }
}

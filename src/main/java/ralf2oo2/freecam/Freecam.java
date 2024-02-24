package ralf2oo2.freecam;

import net.glasslauncher.mods.api.gcapi.api.GConfig;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import ralf2oo2.freecam.client.FreecamController;

public class Freecam {
    @Entrypoint.Namespace
    public static final Namespace MODID = Null.get();
    @GConfig(value = "config", visibleName = "Freecam Config")
    public static final FreecamConfig config = new FreecamConfig();
    public static FreecamController freecamController = new FreecamController();
}

package meteordevelopment.meteorclient.utils.services;

import meteordevelopment.meteorclient.Reaper;
import meteordevelopment.meteorclient.util.services.ResourceLoaderService;
import meteordevelopment.meteorclient.systems.modules.misc.RPC;
import meteordevelopment.meteorclient.utils.misc.MathUtil;
import meteordevelopment.meteorclient.utils.misc.MessageUtil;
import meteordevelopment.meteorclient.utils.os.OSUtil;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class SL { // Service loader


    public static void load() {
        long start = MathUtil.now();
        OSUtil.init(); // setup current os for stuff like spotify
        ResourceLoaderService.init(); // download assets
        //GlobalManager.init();
        MessageUtil.init();
        NotificationManager.init();
        SpotifyService.init();
        //WellbeingService.init(); useless
        Runtime.getRuntime().addShutdownHook(new Thread(TL::shutdown));
        Reaper.log("Started services (" + MathUtil.msPassed(start) + "ms)");

    }

    public static void setupRPC() {
        TL.cached.execute(() -> {
            try { Thread.sleep(5000); } catch (Exception ignored) {}
            RPC rpc = Modules.get().get(RPC.class);
            if (rpc == null) return;
            if (!rpc.runInMainMenu) rpc.runInMainMenu = true;
            rpc.checkMeteorRPC();
            if (!rpc.isActive()) rpc.toggle();
        });
    }



}

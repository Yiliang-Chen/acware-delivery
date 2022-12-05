package top.acware.delivery;

import top.acware.delivery.config.global.Constants;
import top.acware.delivery.config.module.ModuleConfig;
import top.acware.delivery.config.server.ServerConfig;
import top.acware.delivery.utils.ThreadPool;
import top.acware.delivery.worker.receive.Receive;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class DeliveryLauncher {

    public static void main(String[] args) {
        printBanner();
        start();
    }

    public static void start() {
        for (ModuleConfig module : ServerConfig.modules) {
            ThreadPool.executor(module.websocket);
            for (Receive receive : module.receives) {
                receive.start();
            }
        }
    }

    public static void printBanner() {
        System.out.println("             __          __            ");
        System.out.println("      /\\     \\ \\        / /            ");
        System.out.println("     /  \\   __\\ \\  /\\  / /_ _ _ __ ___ ");
        System.out.println("    / /\\ \\ / __\\ \\/  \\/ / _` | '__/ _ \\");
        System.out.println("   / ____ \\ (__ \\  /\\  / (_| | | |  __/");
        System.out.println("  /_/    \\_\\___| \\/  \\/ \\__,_|_|  \\___|");
        System.out.println("=========================================");
        System.out.println("");
        System.out.println(" :: " + Constants.NAME + " (V" + Constants.VERSION + ") ::  " + Constants.WEB_SITE );
        System.out.println("");
    }


}

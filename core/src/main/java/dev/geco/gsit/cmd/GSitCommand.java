package dev.geco.gsit.cmd;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import dev.geco.gsit.GSitMain;
import dev.geco.gsit.objects.*;

public class GSitCommand implements CommandExecutor {
    
    private final GSitMain GPM;
    
    public GSitCommand(GSitMain GPluginMain) { GPM = GPluginMain; }
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(s instanceof Player) {
            Player p = (Player) s;
            if(a.length == 0) {
                if(GPM.getPManager().hasNormalPermission(s, "Sit")) {
                    if(GPM.getSitManager().isSitting(p)) {
                        GPM.getSitManager().removeSeat(GPM.getSitManager().getSeat(p), GetUpReason.GET_UP);
                    } else {
                        if(p.isValid() && !p.isSneaking() && p.isOnGround()) {
                            if(!p.isInsideVehicle() && !p.isSleeping()) {
                                if(!GPM.getCManager().WORLDBLACKLIST.contains(p.getWorld().getName())) {
                                    Location pl = p.getLocation();
                                    Block b = pl.getBlock().isPassable() ? pl.getBlock().getRelative(BlockFace.DOWN) : pl.getBlock();
                                    if(!GPM.getCManager().MATERIALBLACKLIST.contains(b.getType())) {
                                        Material u = b.getRelative(BlockFace.UP).getType();
                                        if(GPM.getCManager().S_ALLOW_UNSAFE || (u.isTransparent() || u == Material.WATER || u.name().equalsIgnoreCase("LIGHT"))) {
                                            if(GPM.getPlotSquared() == null || GPM.getPlotSquared().canCreateSeat(b.getLocation(), p)) {
                                                if(GPM.getWorldGuard() == null || GPM.getWorldGuard().checkFlag(b.getLocation(), GPM.getWorldGuard().SIT_FLAG)) {
                                                    if(GPM.getCManager().REST_SAME_BLOCK || GPM.getSitManager().kickSeat(b, p)) {
                                                        GPM.getSitManager().createSeat(b, p);
                                                    } else GPM.getMManager().sendMessage(s, "Messages.action-sit-kick-error");
                                                } else GPM.getMManager().sendMessage(s, "Messages.action-sit-region-error");
                                            } else GPM.getMManager().sendMessage(s, "Messages.action-sit-region-error");
                                        } else GPM.getMManager().sendMessage(s, "Messages.action-sit-location-error");
                                    } else GPM.getMManager().sendMessage(s, "Messages.action-sit-location-error");
                                } else GPM.getMManager().sendMessage(s, "Messages.action-sit-world-error");
                            } else GPM.getMManager().sendMessage(s, "Messages.action-sit-already-error");
                        } else GPM.getMManager().sendMessage(s, "Messages.action-sit-ground-error");
                    }
                } else GPM.getMManager().sendMessage(s, "Messages.command-permission-error");
            } else {
                switch(a[0]) {
                case "toggle":
                    if(GPM.getPManager().hasNormalPermission(s, "SitToggle")) {
                        if(GPM.getToggleManager().canSit(p.getUniqueId())) {
                            GPM.getToggleManager().setCanSit(p.getUniqueId(), false);
                            GPM.getMManager().sendMessage(s, "Messages.command-gsit-toggle-off");
                        } else {
                            GPM.getToggleManager().setCanSit(p.getUniqueId(), true);
                            GPM.getMManager().sendMessage(s, "Messages.command-gsit-toggle-on");
                        }
                        break;
                    }
                case "playertoggle":
                    if(GPM.getPManager().hasNormalPermission(s, "PlayerSitToggle")) {
                        if(GPM.getToggleManager().canPlayerSit(p.getUniqueId())) {
                            GPM.getToggleManager().setCanPlayerSit(p.getUniqueId(), false);
                            GPM.getMManager().sendMessage(s, "Messages.command-gsit-playertoggle-off");
                        } else {
                            GPM.getToggleManager().setCanPlayerSit(p.getUniqueId(), true);
                            GPM.getMManager().sendMessage(s, "Messages.command-gsit-playertoggle-on");
                        }
                        break;
                    }
                default:
                    Bukkit.dispatchCommand(s, l);
                    break;
                }
            }
        } else GPM.getMManager().sendMessage(s, "Messages.command-sender-error");
        return true;
    }
    
}
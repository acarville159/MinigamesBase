package com.tree.minigames;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class JoinListener implements Listener {
	
	
	private Minigame main;
	
	public JoinListener(Minigame m) {
		this.main = m;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		main.mmapi.changeName(ChatColor.AQUA + main.mmapi.GetNickname(player), player);
		player.setHealth(20);
		player.setFoodLevel(20);
		main.bossBar.addPlayer(player);
		player.teleport(main.fallBackSpawn);
		//player.teleport(main.BlueTeamSpawn);
		if(main.getState() == GameState.WAITING || main.getState() == GameState.COUNTDOWN) {
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			main.addPlayer(player);
		}else {
			//player has joined in progress so add them as a spectator!
			e.getPlayer().sendMessage("You have joined a game in progress and so you have been made a " + ChatColor.LIGHT_PURPLE + " SPECTATOR");
			e.getPlayer().setGameMode(GameMode.SPECTATOR);
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		System.out.println("Player did leave");
		Player player = e.getPlayer();
		main.HandleLeave(player);
	}

}
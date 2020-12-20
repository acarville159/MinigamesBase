package com.tree.minigames;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.scheduler.BukkitRunnable;


public class Countdown extends BukkitRunnable {
	
	Minigame main;
	private int seconds;
	public boolean isStarted;

	
	public Countdown(Minigame main) {
		this.main = main;
		isStarted = false;
	}
	
	public void begin() {
		isStarted = true;
		seconds = main.getGameSettings().getStartCountdownTime();

		main.setState(GameState.COUNTDOWN);
		main.bossBar.setColor(BarColor.GREEN);
		this.runTaskTimer(main,0,20);
	}
	
	public String FormatTime(int seconds) {
		int minutes = (int) (seconds / 60);
		int seconds2 = seconds % 60;
		return (minutes + "m " + seconds2 + "s");
	}
	
	
	@Override
	public void run() {
		if(seconds == 0) {
			cancel();
			main.sendSoundToPlayatAllPlayers(Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
			main.sendTitleToAllPlayers(ChatColor.GREEN+"GO!", "", 5, 10, 5);
			main.startGame();
		}else {
			main.bossBar.setTitle(ChatColor.WHITE + "Game Starting in: " + ChatColor.YELLOW + FormatTime(seconds));		
			if(seconds == 3) {
				main.sendTitleToAllPlayers(ChatColor.RED+"3", "", 5, 10, 5);
				main.sendSoundToPlayatAllPlayers(Sound.BLOCK_ANVIL_HIT, 1f, 1f);
			}
			if(seconds == 2) {
				main.sendTitleToAllPlayers(ChatColor.GREEN+"2", "", 5, 10, 5);
				main.sendSoundToPlayatAllPlayers(Sound.BLOCK_ANVIL_HIT, 1f, 1f);
			}
			if(seconds == 1) {
				main.sendTitleToAllPlayers(ChatColor.RED+"1", "", 5, 10, 5);
				main.sendSoundToPlayatAllPlayers(Sound.BLOCK_ANVIL_HIT, 1f, 1f);
			}
		}
		if(seconds % 60 == 0 || seconds <=10) {
			if(seconds == 1) {
				main.sendMessageToPlayers(ChatColor.YELLOW + "Game will start in 1 Second!");
			}else {
				if(seconds != 0) {
					main.sendMessageToPlayers(ChatColor.GRAY + "Game will start in "+ ChatColor.YELLOW + FormatTime(seconds) );
				}
			}
		}
		
		if(main.getPlayers().size() < main.getGameSettings().getRequiredPlayers()) {
			//we do not have enough players stop the countdown
			cancel();
			main.setState(GameState.WAITING);
			main.sendMessageToPlayers("There are too few players, countdown has been stopped.");
			
		}
		
		seconds --;
	}
}

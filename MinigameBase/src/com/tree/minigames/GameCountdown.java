package com.tree.minigames;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.scheduler.BukkitRunnable;


public class GameCountdown extends BukkitRunnable {
	
	Minigame main;
	private int seconds;
	
	private boolean running = false;
	
	private int maxTime = 10;

	
	public GameCountdown(Minigame main) {
		this.main = main;
		maxTime = this.main.minigameSettings.getRoundTime();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void begin() {
		seconds = 0;
		main.setState(GameState.LIVE);
		main.bossBar.setColor(BarColor.GREEN);
		this.runTaskTimer(main,0,20);
		running = true;
	}
	
	
	@Override
	public void run() {
		if(seconds >= maxTime) {
			if(isRunning()) {
				running = false;
				cancel();
			}
			if(main.getState() == GameState.LIVE) {
				main.endRound();
			}
		}else {
			//main.bossBar.setProgress((float)seconds/(float)maxTime);
			//main.bossBar.setTitle(ChatColor.WHITE + " Time Left: " + FormatTime(seconds));			
			main.gameTick(seconds);
			}
		
		seconds = seconds + 1;
		}

}

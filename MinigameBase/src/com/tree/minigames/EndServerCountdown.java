package com.tree.minigames;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;


public class EndServerCountdown extends BukkitRunnable {
	
	private int seconds;
	private Minigame main;
	public boolean isRunning;
	
	public EndServerCountdown(Minigame main) {
		this.main = main;
		this.seconds = main.getGameSettings().getPostGameTime();
		this.isRunning = false;
	}
	
	public void begin() {
		main.setState(GameState.LIVE);
		main.sendSoundToPlayatAllPlayers(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
		this.isRunning = true;
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
			main.endServer();

			}
		main.bossBar.setTitle(ChatColor.GREEN + "Game Over! " + ChatColor.GREEN + " Server Restarting in: " + FormatTime(seconds));
		if(seconds <=10 && seconds > 0) {
			if(seconds == 1) {
				//main.sendMessageToPlayers(ChatColor.GRAY + "Server will close in 1 Second");
			}else {
				if(seconds != 0) {
					//main.sendMessageToPlayers(ChatColor.GRAY + "Server will close in " + seconds + " seconds");
				}
			}
		}
		
		seconds --;
	}
}

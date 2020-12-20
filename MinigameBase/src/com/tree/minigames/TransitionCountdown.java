package com.tree.minigames;

import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class TransitionCountdown extends BukkitRunnable  {
	
	private int seconds;
	private Minigame main;
	public boolean isStarted = false;
	
	
	private int startingSeconds;
	
	public TransitionCountdown (Minigame main) {
		this.main = main;
		this.isStarted = false;
		this.seconds = this.main.minigameSettings.getTransitionTime();
		this.startingSeconds = this.main.minigameSettings.getTransitionTime();
	}
	
	public void begin() {
		//main.setState(GameState.LIVE);
		startingSeconds = seconds;
		main.bossBar.setColor(BarColor.GREEN);
		//main.sendMessageToPlayers("Round Started!");
		this.isStarted = true;
		this.runTaskTimer(main,0,20);
		//main.setBorderSize(0, startingSeconds-10);
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
			if(main.getState() == GameState.TRANSITION) {
				main.startNextRound();
			}else {
				System.out.println("Tried to startNext wrong but incorrect state " + main.getState().name());
			}
		}else {
			
			main.bossBar.setTitle(ChatColor.YELLOW + " Round "+main.getCurrentGame()+"/"+main.getGameSettings().getMaxRounds()+" starting in "+ChatColor.WHITE+FormatTime(seconds));
			main.bossBar.setColor(BarColor.YELLOW);
			main.bossBar.setProgress((float)seconds / (float)startingSeconds);
			if(seconds < 5) {
				main.sendTitleToAllPlayers(ChatColor.GOLD+""+seconds, "", 0, 20, 0);
				main.sendSoundToPlayatAllPlayers(Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1f, 1f);
			}
		}
		
		seconds --;
	}

}

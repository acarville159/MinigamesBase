package com.tree.minigames;

public class MinigameSettings {
	
	private int requiredPlayers;
	private int maxRounds;
	private int startCountdownTime;
	private int roundTime;
	private int postGameTime;
	private int transitionTime;
	
	public MinigameSettings() {
		this.requiredPlayers = 1;
		this.maxRounds = 5;
		startCountdownTime = 30;
		roundTime = 120;
		transitionTime = 10;
		postGameTime = 10;
		
	}
	
	public void setRequiredPlayers(int i) {
		requiredPlayers = i;
	}
	
	public int getRequiredPlayers() {
		return requiredPlayers;
	}
	
	public void setMaxRounds(int i) {
		maxRounds = i;
	}
	
	public int getMaxRounds() {
		return maxRounds;
	}

	public int getStartCountdownTime() {
		return startCountdownTime;
	}

	public void setStartCountdownTime(int startCountdownTime) {
		this.startCountdownTime = startCountdownTime;
	}

	public int getRoundTime() {
		return roundTime;
	}

	public void setRoundTime(int roundTime) {
		this.roundTime = roundTime;
	}

	public int getPostGameTime() {
		return postGameTime;
	}

	public void setPostGameTime(int postGameTime) {
		this.postGameTime = postGameTime;
	}

	public int getTransitionTime() {
		return transitionTime;
	}

	public void setTransitionTime(int transitionTime) {
		this.transitionTime = transitionTime;
	}
	
	

}

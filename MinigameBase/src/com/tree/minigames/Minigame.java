package com.tree.minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.tree.mmapi.*;

public class Minigame extends JavaPlugin {
	
	
	protected BossBar bossBar;

	protected ArrayList<UUID> players;
	protected ArrayList<UUID> alivePlayers;

	public Location fallBackSpawn;

	private TransitionCountdown transCountdown;

	public String WinString;

	private HashMap<Integer, UUID> playerNumbers;

	private GameState state;

	private int currentGame = 1;
	private int maxGames = 5;
	
	private HashMap<UUID, Integer> gameScores = new HashMap<UUID, Integer>();

	protected Countdown countdown;
	protected GameCountdown gameCountdown;
	protected EndServerCountdown endServerCountdown;
	
	public MinigameSettings minigameSettings;
	
	public World world;
	


	protected com.tree.mmapi.Main mmapi;
	
	public void Setup() {
		setState(GameState.WAITING);

		players = new ArrayList<UUID>();
		alivePlayers = new ArrayList<UUID>();

		playerNumbers = new HashMap<Integer, UUID>();
		
		mmapi = (com.tree.mmapi.Main) Bukkit.getPluginManager().getPlugin("MMAPI");

		world = Bukkit.getWorld("world");
		setupGameRules();
		setupGameSettings();
		setupCountdowns();
		setupListeners();
		setupBossbar();
		setupDefaultSpawn();
		world.setTime(0);

	}
	
    public void removeItemFromPlayersInventory(Player player, Material type, int amount) {
    	if(player == null) {
    		return;
    	}
    	Inventory inventory = player.getInventory();
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }
	
	public void removeAllPotionEffectsFromPlayer(Player p) {
		for(PotionEffect e : p.getActivePotionEffects()) {
			p.removePotionEffect(e.getType());
		}
	}
	
	public void removePotionEffect(Player player, PotionEffectType type) {
		if(player.hasPotionEffect(type)) {
			player.removePotionEffect(type);
		}
	}
	
	public MinigameSettings getGameSettings() {
		return minigameSettings;
	}
	
	public void setupGameSettings() {
		minigameSettings = new MinigameSettings();
	}
	
	public void setupDefaultSpawn() {
		fallBackSpawn = new Location(Bukkit.getWorld("world"), 24, 64, -22, 0, 0);
	}
	
	public void setupBossbar() {
		bossBar = Bukkit.createBossBar(ChatColor.GRAY + "Minigame Name " + ChatColor.YELLOW + "v1"
				+ ChatColor.WHITE + " Waiting for Players", BarColor.WHITE, BarStyle.SOLID);	
	}
	
	public void setupListeners() {
		Bukkit.getPluginManager().registerEvents(setupJoinListener(this), this);
		Bukkit.getPluginManager().registerEvents(setupGameListener(this), this);
	}
	
	public GameListener setupGameListener(Minigame main) {
		System.out.println("[Game Listener] Default");
		return new GameListener(main);
	}
	
	public JoinListener setupJoinListener(Minigame main) {
		return new JoinListener(main);
	}
	
	public Countdown setupCountdown(Minigame main) {
		return new Countdown(main);
	}
	
	public void gameTick(int seconds) {
		setBossBar(seconds);
	}
	
	public void setBossBar(int seconds) {
		bossBar.setTitle("Time Left: " + (getGameSettings().getRoundTime() - seconds));
		bossBar.setProgress((float)seconds/(float)getGameSettings().getRoundTime());
	}
	
	public GameCountdown setupGameCountdown(Minigame main) {
		return new GameCountdown(main);
	}
	
	public EndServerCountdown setupEndServerCountdown(Minigame main) {
		return new EndServerCountdown(main);
	}
	
	public void setupCountdowns() {
		countdown = setupCountdown(this);
		gameCountdown = setupGameCountdown(this);
		endServerCountdown = setupEndServerCountdown(this);
	}
	
	@Override
	public void onEnable() {	
		Setup();	
	}
	
	public void endRound() {
		if(gameCountdown.isRunning()) {
			
			gameCountdown.cancel();
		}
		System.out.println("endRound");
		setState(GameState.TRANSITION);
		setCurrentGame(getCurrentGame() + 1);
		for (UUID id : players) {
			Player p = Bukkit.getPlayer(id);
			if (p != null) {
				p.sendTitle(ChatColor.RED + "ROUND OVER!", "", 5, 40, 5);
				p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
			}
		}
		if (getCurrentGame() <= getGameSettings().getMaxRounds()) {
			// start a new round
			System.out.println("startRoundCountdown1");
			startRoundCountdown();
		} else {
			// this is the end of the whole minigame
			endGame();
		}
	}
	
	public TransitionCountdown setupTransitionCountdown(Minigame main) {
		return new TransitionCountdown(main);
	}
	
	public void startRoundCountdown() {
		System.out.println("startRoundCountdown2");
		transCountdown = setupTransitionCountdown(this);
		transCountdown.begin();
	}
	
	public void setCurrentGame(int i) {
		currentGame = i;
	}
	
	public int getCurrentGame() {
		return currentGame;
	}
	
	public void endGame() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p != null) {
				p.sendTitle(WinString, "", 10, 40, 10);
			}
		}
		DistributeMMScore();
		if (endServerCountdown.isRunning == false) {
			endServerCountdown.begin();
		}

	}
	
	public int getGameScore(UUID id) {
		try {
			return gameScores.get(id);
		}catch(Exception e){
			return 0;
		}
	}

	public void DistributeMMScore() {
		// work out the top game scores in order and distribute points to them
		ArrayList<UUID> orderedScores = GetGameScoresInOrder();
		int scoreToGive = 100;
		int currentPos = 1;
		for (UUID id : orderedScores) {
			sendMessageToPlayers(currentPos +". " + mmapi.GetNickname(id) + "("+getGameScore(id)+")");
			if (scoreToGive > 0) {
				mmapi.AddScoreToPlayer(Bukkit.getPlayer(id), "for placement!", scoreToGive);
				scoreToGive -= 10;
			}
			currentPos ++;

		}
	}

	public void AddGameScore(UUID uuid, String reason, int amt) {
		int currentScore = 0;
		if (gameScores.containsKey(uuid)) {
			currentScore = gameScores.get(uuid);
		}
		gameScores.put(uuid, currentScore + amt);
		Player p = Bukkit.getPlayer(uuid);
		if (p != null) {
			p.sendMessage(ChatColor.GREEN + "+" + amt + " points " + reason);
		}
	}

	public ArrayList<UUID> GetGameScoresInOrder() {
		ArrayList<UUID> orderedPlayers = new ArrayList<UUID>();
		// just all players in any order
		for (Entry<UUID, Integer> entry : gameScores.entrySet()) {
			orderedPlayers.add(entry.getKey());
		}
	    boolean sorted = false;
	    while(!sorted) {
	        sorted = true;
	        for (int i = 0; i < orderedPlayers.size() - 1; i++) {
	            if (getGameScore(orderedPlayers.get(i)) < getGameScore(orderedPlayers.get(i+1))) {
	                UUID tempID = orderedPlayers.get(i);
	                orderedPlayers.set(i, orderedPlayers.get(i+1));
	                orderedPlayers.set(i+1, tempID);
	                sorted = false;
	            }
	        }
	    }
		return orderedPlayers;
	}
	
	public void setupGameRules() {
		world.setTime(1200);
		world.setClearWeatherDuration(9999);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
	}
	
	public void startNextRound() {
		
		gameCountdown = setupGameCountdown(this);
		
		alivePlayers.clear();

		// tp all players to start
		for (UUID id : getPlayers()) {
			Player p = Bukkit.getPlayer(id);
			if (p != null) {
				//p.teleport(fallBackSpawn);
				addPlayer(p);
			}
		}
		gameCountdown.begin();
		
		teleportAllPlayersToRoundSpawn();
		
		
		setState(GameState.LIVE);
	}
	
	public void playSoundAtAllPlayers(Sound sound) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p != null) {
				p.playSound(p.getLocation(), sound, 10, 10);
			}

		}
	}
	
	public void giveStartingRoundItems(Player p) {
		p.getInventory().clear();
	}

	public void teleportPlayerToRoundSpawn(Player p) {
		
	}
	
	public void teleportAllPlayersToRoundSpawn() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			teleportPlayerToRoundSpawn(player);
		}
	}
	
	public void addPlayer(Player player) {
		int numb = getPlayerNumber(player.getUniqueId());
		if (numb == -1) {
			// this is a new player
			if (players.contains(player.getUniqueId())) {
				player.sendMessage("Already a player");
			} else {
				players.add(player.getUniqueId());
			}
			if(alivePlayers.contains(player.getUniqueId())) {
				
			}else {
				alivePlayers.add(player.getUniqueId());
			}
			setPlayerNumber(getNextPlayerNumber(), player.getUniqueId());
			player.setGameMode(GameMode.SURVIVAL);
			player.sendMessage(ChatColor.YELLOW + " Added to game as player " + ChatColor.AQUA
					+ getPlayerNumber(player.getUniqueId()));
			giveStartingRoundItems(player);
			teleportPlayerToRoundSpawn(player);
		} else {
			if(alivePlayers.contains(player.getUniqueId())) {
				
			}else {
				alivePlayers.add(player.getUniqueId());
			}
			player.setGameMode(GameMode.SURVIVAL);
			player.sendMessage(ChatColor.YELLOW + " Readded to game as player " + ChatColor.AQUA + numb);
			giveStartingRoundItems(player);
			teleportPlayerToRoundSpawn(player);
		}
		if (players.size() >= this.getConfig().getInt("requiredPlayers") && getState() == GameState.WAITING) {
			countdown.begin();
		}
	}

	public int getPlayerNumber(UUID id) {
		System.out.println("getPlayerNumber size: " + playerNumbers.size());
		for (int i : playerNumbers.keySet()) {
			if (playerNumbers.get(i).equals(id)) {
				return i;
			} else {
				System.out.println("1." + playerNumbers.get(i) + " does not match 2." + id);
			}
		}
		return -1;
	}

	public int getNextPlayerNumber() {
		return playerNumbers.keySet().size();
	}

	public void setPlayerNumber(int num, UUID id) {
		playerNumbers.put(num, id);
	}

	public void startGame() {
		startNextRound();
	}

	public void sendSoundToPlayatAllPlayers(Sound sound, float f1, float f2) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player != null) {
				player.playSound(player.getLocation(), sound, f1, f2);
			}
		}
	}

	public void sendMessageToPlayers(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p != null) {
				p.sendMessage(msg);
			}
		}
	}
	
	public ArrayList<UUID> getPlayers() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(players.contains(p.getUniqueId())) {
				
			}else {
				players.add(p.getUniqueId());
			}
		}
		return players;
	}

	
	public void sendTitleToAllPlayers(String s1, String s2, int t1, int t2, int t3) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player != null) {
				player.sendTitle(s1, s2, t1, t2, t3);
			}
		}
	}

	public void HandleLeave(Player player) {
		UUID id = player.getUniqueId();
		if (players.contains(id)) {
			players.remove(id);
		}

	}

	public void setState(GameState state) {
		this.state = state;
	}

	public GameState getState() {
		return this.state;
	}

	public void endServer() {
		mmapi.sendAllPlayersToServer("lobby");
		getServer().shutdown();
	}
	
	

}

package com.pigsar.szmj.library;

import java.util.ArrayList;

import android.util.Log;
import android.view.MotionEvent;

import com.pigsar.szmj.graphic.AnimationEventListener;
import com.pigsar.szmj.graphic.RenderManager;

public class GameController implements AnimationEventListener {
	
	public enum State {
		Invalid,
		StartGame,
		StartHand,
		InitBeforeStartTurn,
		DrawNewTile,
		SelectActionTile,
		ShowActionTile,
		ShowKongLabel,
		ShowKongTile,
		SelectSpecialMove,
		ShowSpecialMoveLabel,
		ShowSpecialMoveTile,
		WithDrawSpecialMove,
		StartHandResult,
		ShowWinHand,
		PayWinHand,
		FinishedHand,
		FinishedGame,
	}
	
	final public static int PLAYER_NUM = 4;
	final public static int PLAYER_TILE_NUM = 13;
	
	private State _state;
	
	private TilePool _tilePool;
	private int _handCount;
	private ArrayList<AbstractPlayer> _players = new ArrayList<AbstractPlayer>();
	private int _userPlayerIndex;
	private int _currentPlayerIndex;
	//private int _dealerPlayerIndex;
	private RenderManager _renderMgr;
	
	
	public GameController() {
		_state = State.Invalid;
		_tilePool = new TilePool();
		
		_players.add(new UserPlayer(this));
		for (int i = 1; i < PLAYER_NUM; ++i) {
			_players.add(new ComputerPlayer(this));
		}
		_userPlayerIndex = 0;
		_currentPlayerIndex = 0;
		
		// TEST
		
	}
	
	public TilePool tilePool() {
		return _tilePool;
	}
	
	public RenderManager renderManager() {
		return _renderMgr;
	}
	
	public void setRenderManager(RenderManager manager) {
		_renderMgr = manager;
	}
	
	public ArrayList<AbstractPlayer> players() {
		return _players;			
	}
	
	public UserPlayer userPlayer() {
		return (UserPlayer)_players.get(_userPlayerIndex);
	}
	
	public AbstractPlayer currentPlayer() {
		return _players.get(_currentPlayerIndex);
	}
	
	public AbstractPlayer dealerPlayer() {
		return _players.get(dealerPlayerIndex());
	}
	
	public int dealerPlayerIndex() {
		return _handCount % PLAYER_NUM;
	}
	
	public State state() {
		return _state;
	}
	
	/**
	 * State Flow:
	 * 
	 * 	START
	 * 	Invalid > StartGame > (StartHand) > InitBeforeStartTurn >
	 *	DrawNewTile
	 *	IF POOL NOT EMPTY
	 * 		SelectActionTile > ShowActionTile
	 * 		[Next player]
	 * 		IF KONG: ShowKongLabel > ShowKongTile
	 * 		IF SPECIAL MOVE: SelectSpecialMove > ShowSpecialMoveLabel >
	 * 						 ShowSpecialMoveTile > WithDrawSpecialMove
	 *		GOTO DrawNewTile
	 *	ELSE
	 *		StartHandResult > ShowWinHand > PayWinHand > FinishedHand > FinishedGame
	 */
	
	public void transitState(State next) {
		boolean ok = false;
		switch (_state) {
		case Invalid:				ok = (next == State.StartGame);					break;
		case StartGame:				ok = (next == State.InitBeforeStartTurn);		break;
		case InitBeforeStartTurn:	ok = (next == State.DrawNewTile);				break;
		case StartHand:				ok = (next == State.DrawNewTile);				break;
		case DrawNewTile:			ok = (next == State.SelectActionTile);			break;
		case SelectActionTile:		ok = (next == State.ShowActionTile);			break;
		case ShowActionTile:		ok = (next == State.DrawNewTile);				break;
		default:	Log.e("MJ", String.format("Unhandled State: {1}", _state.toString()));
		}
		
		if (ok) {
			Log.d("MJ", String.format("[State] %s > %s", _state.toString(), next.toString()));
			_state = next;
		} else {
			return;
		}
		
		switch (next) {
		case StartGame:				startGame();									break;
		case InitBeforeStartTurn:	initBeforeStartTurn();							break;
		case DrawNewTile:			drawNewTile();									break;
		case SelectActionTile:		selectActionTile();								break;
		case ShowActionTile:		showActionTile();								break;
		default:	Log.e("MJ", String.format("State %s no corresponding function", next.toString()));
		}
	}
	
	public void onAnimationFinished(Object sender) {
		switch (state()) {
		case InitBeforeStartTurn: 	transitState(State.DrawNewTile);				break;
		case DrawNewTile: 			transitState(State.SelectActionTile);			break;
		case ShowActionTile:		nextTurn();										break;
		}
	}
	
	private void nextTurn() {
		
		// Check kong
		
		// Check special move
		
		
		if (tilePool().isValid()) {
			_currentPlayerIndex = (_currentPlayerIndex + 1) % PLAYER_NUM;
		
			transitState(State.DrawNewTile);
		}
	}
	
	private void startGame() {
		for (AbstractPlayer player : _players) {
			player.resetTiles();
		}
		
		transitState(State.InitBeforeStartTurn);
	}
	
	private void initBeforeStartTurn() {
		for (AbstractPlayer player : _players) {
			player.sortSelectableTiles(player instanceof UserPlayer);
		}
		// PS: Transit state after animation finished.
	}
	
	private void drawNewTile() {
		currentPlayer().drawTile();
		// PS: Transit state after animation finished.
	}
	
	private void selectActionTile() {
		currentPlayer().selectActionTile();
	}
	
	private void showActionTile() {
		SoundManager.instance().playDiscard();
	}
	
	//============== Process event =================
	
	public void processTouchEvent(MotionEvent event) {
		if (currentPlayer() == userPlayer()) {
			userPlayer().processEvent(event);
		}
	}
	
}

package com.pigsar.szmj.library;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
	
	private RenderManager _renderMgr;
	//private Evaluator _evaluator;
	private TilePool _tilePool;
	private int _handCount;
	private LinkedList<Player> _players = new LinkedList<Player>();
	private int _userPlayerIndex;
	//private int _currentPlayerIndex;
	//private int _dealerPlayerIndex;
	private Tile _actionTile;
	
	
	public GameController() {
		_state = State.Invalid;
		//_evaluator = new Evaluator();
		_tilePool = new TilePool();
		
		_players.add(new UserPlayer(this));
		for (int i = 1; i < PLAYER_NUM; ++i) {
			_players.add(new ComputerPlayer(this));
		}
		_userPlayerIndex = 0;
		//_currentPlayerIndex = 0;
		
	}

//	public Evaluator evaluator() {
//		return _evaluator;
//	}
	
	public TilePool tilePool() {
		return _tilePool;
	}
	
	public RenderManager renderManager() {
		return _renderMgr;
	}
	
	public void setRenderManager(RenderManager manager) {
		_renderMgr = manager;
	}
	
	/**
	 * The player list is always in order.
	 * @return
	 */
	public List<Player> players() {
		return _players;			
	}
	
	public UserPlayer userPlayer() {
		return (UserPlayer)_players.get(_userPlayerIndex);
	}
	
	public Player currentPlayer() {
		return _players.getFirst();
	}
	
	public Player nextPlayer() {
		return _players.get(1);
	}
	
	public Player dealerPlayer() {
		return _players.get(dealerPlayerIndex());
	}
	
	public int dealerPlayerIndex() {
		return _handCount % PLAYER_NUM;
	}
	
	private boolean passToNextPlayer() {
		if (tilePool().isValid()) {
			//_currentPlayerIndex = (_currentPlayerIndex + 1) % PLAYER_NUM;
			_players.add(_players.poll());
			return true;
		}
		return false;
	}

	public void setActionTile(Tile tile) {
		_actionTile = tile;
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
		case ShowActionTile:		ok = (next == State.DrawNewTile ||
										  next == State.SelectSpecialMove);			break;
		
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
		
		case SelectSpecialMove:		selectSpecialMove();							break;
		
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
	
	private void startGame() {
		for (Player player : _players) {
			player.resetTiles();
		}
		
		transitState(State.InitBeforeStartTurn);
	}
	
	private void initBeforeStartTurn() {
		for (Player player : _players) {
			player.sortSelectableTiles(player instanceof UserPlayer);
		}
		// PS: Transit state after animation finished.
	}
	
	private void nextTurn() {
		
		if (claimDiscardedTile()) {
			// Claim success
			transitState(State.SelectSpecialMove);
		}

		
		if (passToNextPlayer()) {
			// Next turn
			//List<SpecialMove> specialMoves = _evaluator.checkSpecialMoves(currentPlayer());
			List<SpecialMove> specialMoves = currentPlayer().evaluator().checkSpecialMoves();
		
			transitState(State.DrawNewTile);
		} else {
			// End game
		}
	}
	
	private void selectSpecialMove() {
		
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
	
	//=========================================================================
	
	private List<SpecialMove> getAvailableSpecialMoves(Player player) {
		List<SpecialMove> availableSpecialMoves = _evaluator.checkSpecialMoves(player);
		return availableSpecialMoves;
	}
	
	private List<SpecialMove> getAvailableSpecialMoves(Player player, boolean canChow) {
		List<SpecialMove> availableSpecialMoves = _evaluator.checkSpecialMoves(player, _actionTile,
													canChow, false /*canWin*/);
		if (!availableSpecialMoves.isEmpty()) {
			// TODO: available special move - give up special move
			
			if (!availableSpecialMoves.isEmpty()) {
				for (SpecialMove sm : availableSpecialMoves) {
					sm.setPlayers(currentPlayer(), player);
				}
				
				SpecialMove giveUpSpecialMove = new SpecialMove(SpecialMove.Type.GiveUp, _actionTile);
				availableSpecialMoves.add(giveUpSpecialMove);
			}
		}
		
		return availableSpecialMoves;
	}

	/***
	 * 
	 * Refer: MahjongController.checkAvailableSpecialMove
	 */
	private boolean claimDiscardedTile() {
		boolean available = false;
		for (Player player : players()) {
			//player.clearAvailableSpecialMove();
			
			if (player == currentPlayer()) {
	            // The current player that selected action tile must give up
				player.setSelectedSpecialMove(new SpecialMove(SpecialMove.Type.GiveUp, _actionTile));
			} else {
				boolean canChow = (player == nextPlayer());
				List<SpecialMove> availableSpecialMoves = getAvailableSpecialMoves(player, canChow);
				
				if (availableSpecialMoves.isEmpty()) {
					player.setSelectedSpecialMove(new SpecialMove(SpecialMove.Type.GiveUp, _actionTile));
				} else {
					// TEMP: always select the first special move
					player.setSelectedSpecialMove(availableSpecialMoves.get(0));
					
					// TODO: Save the selection
					
					available = true;
				}
			}
		}
		
		//if (available) {
			// TODO: change state
		//}
		
		return available;
	}
	
}

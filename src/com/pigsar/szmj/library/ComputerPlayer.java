package com.pigsar.szmj.library;

import com.pigsar.szmj.library.GameController.State;

public class ComputerPlayer extends Player {

	public ComputerPlayer(GameController controller) {
		super(controller);
	}

	@Override
	public void selectActionTile() {
		assert(!selectableTiles().isEmpty());
		
		discardTile(selectableTiles().get(0));								// TODO: AI
		_gameCtrl.transitState(State.ShowActionTile);
	}

	@Override
	public void selectSpecialMove() {
		assert(!_evaluator.planningSpecialMoves().isEmpty());
		
		_plannedSpecialMove = _evaluator.planningSpecialMoves().get(0);		// TODO: AI
		_gameCtrl.transitState(State.ShowSpecialMoveLabel);
	}

}

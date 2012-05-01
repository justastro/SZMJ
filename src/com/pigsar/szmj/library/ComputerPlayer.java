package com.pigsar.szmj.library;

import com.pigsar.szmj.library.GameController.State;

public class ComputerPlayer extends AbstractPlayer {

	public ComputerPlayer(GameController controller) {
		super(controller);
	}

	@Override
	public void selectActionTile() {
		if (selectableTiles().size() > 0) {
			_gameCtrl.transitState(State.ShowActionTile);
			discardTile(selectableTiles().get(0));
		}
	}

}

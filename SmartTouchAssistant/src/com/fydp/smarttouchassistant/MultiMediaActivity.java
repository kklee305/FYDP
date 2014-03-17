package com.fydp.smarttouchassistant;

import android.os.Bundle;
import android.view.View;

public class MultiMediaActivity extends BaseBluetoothActivity {
	private static final String MULTIMEDIA_HEADER = "multimedia#";
	private static final String VOLUME_HEADER = "volume#";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_media);
	}

	public void inputCommand(View view) {
		String message = null;
		if (!isBound) {
			return;
		}

		switch (view.getId()) {
		case R.id.rewind:
			message = MULTIMEDIA_HEADER + "previoustrack";
			break;
		case R.id.play:
			message = MULTIMEDIA_HEADER + "playpause";
			break;
		case R.id.forward:
			message = MULTIMEDIA_HEADER + "nexttrack";
			break;
		case R.id.volumeUp:
			message = VOLUME_HEADER + "volumeup";
			break;
		case R.id.volumeDown:
			message = VOLUME_HEADER + "volumedown";
			break;
		case R.id.stop:
			message = MULTIMEDIA_HEADER + "stop";
			break;
		case R.id.mute:
			message = VOLUME_HEADER + "mute";
			break;
		}
		btService.sendMessage(message + "#");

	}

	@Override
	protected void bluetoothBounded() {
	}

}

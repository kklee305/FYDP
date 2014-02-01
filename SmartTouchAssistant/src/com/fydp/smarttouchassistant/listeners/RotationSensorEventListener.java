package com.fydp.smarttouchassistant.listeners;

import com.fydp.smarttouchassistant.R;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RotationSensorEventListener implements SensorEventListener {

	private float[] rotation_vector = new float[9];
	private float[] orientation = new float[3];
	private int[] origin_orientation = new int[3];
	private int x, y;
	private RelativeLayout parentLayout;
	private MarginLayoutParams marginLayoutParams;
	private ImageView imageMoveMe;
	private TextView textView;

	public RotationSensorEventListener(RelativeLayout layout) {
		this.parentLayout = layout;

		textView = (TextView) parentLayout.findViewById(R.id.textView1);
		imageMoveMe = (ImageView) parentLayout.findViewById(R.id.imageMoveMe);
		marginLayoutParams = (MarginLayoutParams) imageMoveMe.getLayoutParams();
		x = marginLayoutParams.leftMargin;
		y = marginLayoutParams.bottomMargin;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			rotation_vector[0] = 1;
			rotation_vector[4] = 1;
			rotation_vector[8] = 1;
			SensorManager.getRotationMatrixFromVector(rotation_vector, event.values);
			SensorManager.getOrientation(rotation_vector, orientation);
			updateJoyStick();
		}
	}

	private void updateJoyStick() {

		int x_angle = (int) (((orientation[1] * 180) / Math.PI));
		TextView currentPitchView = (TextView) parentLayout.findViewById(R.id.currentPitch);
		currentPitchView.setText("Pitch: " + String.valueOf(x_angle));
		TextView pitchView = (TextView) parentLayout.findViewById(R.id.textPitch);
		int x_diff = origin_orientation[0] - x_angle;
		pitchView.setText("Pitch: " + String.valueOf(x_diff));

		int y_angle = (int) (((orientation[2] * 180) / Math.PI));
		TextView currentRollView = (TextView) parentLayout.findViewById(R.id.currentRoll);
		currentRollView.setText("Roll: " + String.valueOf(y_angle));
		TextView rollView = (TextView) parentLayout.findViewById(R.id.textRoll);
		int y_diff = origin_orientation[1] - y_angle;
		rollView.setText("Roll: " + String.valueOf(y_diff));

		int x_offset = x_diff;
		int y_offset = y_diff;

		marginLayoutParams.leftMargin = (int) (marginLayoutParams.leftMargin - y_offset);
		marginLayoutParams.bottomMargin = (int) (marginLayoutParams.bottomMargin - x_offset);
		imageMoveMe.requestLayout();

		int z_angle = (int) (((orientation[0] * 180) / Math.PI));
		TextView currentAzimuthView = (TextView) parentLayout.findViewById(R.id.currentAzimuth);
		currentAzimuthView.setText("Azimuth: " + String.valueOf(z_angle));
		TextView azimuthView = (TextView) parentLayout.findViewById(R.id.textAzimuth);
		azimuthView.setText("Azimuth: " + String.valueOf(origin_orientation[2] - z_angle));
		return;
	}

	public void calibrate() {
		textView.setText("CALIBRATE!");
		marginLayoutParams.leftMargin = x;
		marginLayoutParams.bottomMargin = y;
		origin_orientation[0] = (int) (((orientation[1] * 180) / Math.PI));
		origin_orientation[1] = (int) (((orientation[2] * 180) / Math.PI));
		origin_orientation[2] = (int) (((orientation[0] * 180) / Math.PI));
	}
}

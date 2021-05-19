package hu.bme.mit.train.controller;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import hu.bme.mit.train.interfaces.TrainController;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TrainControllerImpl implements TrainController {

	private int step = 0;
	private int referenceSpeed = 0;
	private int speedLimit = 0;

	private Tachograph tacho = new Tachograph();

	public TrainControllerImpl() {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				TrainControllerImpl.this.followSpeed();
			}
		};

		Timer timer = new Timer("Ticket");
		timer.scheduleAtFixedRate(timerTask, 30, 250);
	}

	@Override
	public void followSpeed() {
		if (referenceSpeed < 0) {
			referenceSpeed = 0;
		} else {
		    if(referenceSpeed+step > 0) {
                referenceSpeed += step;
            } else {
		        referenceSpeed = 0;
            }
		}

		enforceSpeedLimit();

		tacho.record(this.step, this.referenceSpeed);
	}

	@Override
	public int getReferenceSpeed() {
		return referenceSpeed;
	}

	@Override
	public void setSpeedLimit(int speedLimit) {
		this.speedLimit = speedLimit;
		enforceSpeedLimit();
		
	}

	private void enforceSpeedLimit() {
		if (referenceSpeed > speedLimit) {
			referenceSpeed = speedLimit;
		}
	}

	@Override
	public void setJoystickPosition(int joystickPosition) {
		this.step = joystickPosition;
	}


	class Tachograph {
		private Table<Long, Integer, Integer> records = HashBasedTable.create();

		public void record(int joystick, int refSpeed){
			long now = System.currentTimeMillis();

			System.out.println(String.format("Record made: %d - %d - %d", now, joystick, refSpeed));

			records.put(now, joystick, refSpeed);
		}
	}
}
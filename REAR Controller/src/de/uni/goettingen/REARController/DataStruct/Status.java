package de.uni.goettingen.REARController.DataStruct;

import java.io.Serializable;

public class Status implements Serializable {
	private static final long serialVersionUID = 3536320114748358072L;
	private StatusEnum	status;
	
	public Status(StatusEnum s) {
		status = s;
	}
	
	public Status() {
		status = StatusEnum.UNINITIALIZED;
	}
	
	public StatusEnum get() {
		return status;
	}
	
	public void set(StatusEnum s) {
		status = s;
	}
	
	public Boolean isUninitialized() {
		if(status == StatusEnum.UNINITIALIZED)
			return true;
		return false;
	}
	
	public Boolean isStopped() {
		if(status == StatusEnum.STOPPED)
			return true;
		return false;
	}

	public Boolean isRecording() {
		if(status == StatusEnum.RECORDING)
			return true;
		return false;
	}

	public Boolean isUploading() {
		if(status == StatusEnum.UPLOADING)
			return true;
		return false;
	}

	public Boolean isDone() {
		if(status == StatusEnum.DONE)
			return true;
		return false;
	}
	
	public void setUninitialized() {
		status = StatusEnum.UNINITIALIZED;
	}

	public void setStopped() {
		status = StatusEnum.STOPPED;
	}

	public void setRecording() {
		status = StatusEnum.RECORDING;
	}

	public void setUploading() {
		status = StatusEnum.UPLOADING;
	}

	public void setDone() {
		status = StatusEnum.DONE;
	}
}
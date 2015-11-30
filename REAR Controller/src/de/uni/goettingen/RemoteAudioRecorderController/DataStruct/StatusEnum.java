package de.uni.goettingen.RemoteAudioRecorderController.DataStruct;

import java.io.Serializable;

public enum StatusEnum implements Serializable {
	UNINITIALIZED,
	STOPPED,
	RECORDING,
	UPLOADING,
	DONE
}

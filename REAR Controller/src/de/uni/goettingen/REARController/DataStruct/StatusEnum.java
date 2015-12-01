package de.uni.goettingen.REARController.DataStruct;

import java.io.Serializable;

public enum StatusEnum implements Serializable {
	UNINITIALIZED,
	STOPPED,
	RECORDING,
	UPLOADING,
	DONE
}

package de.uni.goettingen.REARController.DataStruct;

import java.io.Serializable;

public enum StatusEnum implements Serializable {
	NOTCONNECTED,
	UNINITIALIZED,
	NOMIC,
	STOPPED,
	RECORDING,
	UPLOADING,
	DONE,
	MULTI_STATUS
}

package de.uni.goettingen.REARController.DataStruct;

import java.io.Serializable;

public class ClientStatus implements Serializable {
	private static final long serialVersionUID = 6835731377460284019L;

	private Boolean none;
	private Boolean noMic;
	private Boolean init;
	private Boolean rec;
	private Boolean upload;
	private Boolean done;
	private Boolean connected;

	public ClientStatus() {
		none		= false;
		noMic		= false;
		init		= false;
		rec			= false;
		upload		= false;
		done		= false;
		connected	= false;
	}

	public ClientStatus(ClientStatus cs) {
		if(cs != null) {
			none		= cs.none;
			noMic		= cs.noMic;
			init		= cs.init;
			rec			= cs.rec;
			upload		= cs.upload;
			done		= cs.done;
			connected	= cs.connected;
		}
		else
		{
			none		= false;
			noMic		= false;
			init		= false;
			rec			= false;
			upload		= false;
			done		= false;
			connected	= false;
		}
	}

	public ClientStatus(Boolean n, Boolean nmic, Boolean i, Boolean r, Boolean u, Boolean d) {
		none		= n;
		noMic		= nmic;
		init		= i;
		rec			= r;
		upload		= u;
		done		= d;
		connected	= true;
	}

	public ClientStatus(StatusEnum s) {
		none		= false;
		noMic		= false;
		init		= false;
		rec			= false;
		upload		= false;
		done		= false;
		connected	= true;
		if(s == StatusEnum.NOTCONNECTED)
			connected = false;
		else if(s == StatusEnum.STOPPED)
			init	= true;
		else if(s == StatusEnum.NOMIC)
			noMic	= true;
		else if(s == StatusEnum.RECORDING)
			rec		= true;
		else if(s == StatusEnum.UPLOADING)
			upload	= true;
		else if(s == StatusEnum.DONE)
			done	= true;
		else if(s == StatusEnum.UNINITIALIZED)
			none	= true;
	}

	public StatusEnum getStatus() {
		Boolean a = init 	&& ( rec	|| upload	|| done);
		Boolean b = rec  	&& ( upload	|| done		);
		Boolean c = upload	&& done;
		if(!connected)
			return StatusEnum.NOTCONNECTED;
		if(noMic)
			return StatusEnum.NOMIC;
		if(a || b || c) 
			return StatusEnum.MULTI_STATUS;
		if(init)
			return StatusEnum.STOPPED;
		if(rec)
			return StatusEnum.RECORDING;
		if(upload)
			return StatusEnum.UPLOADING;
		if(done)
			return StatusEnum.DONE;
		return StatusEnum.UNINITIALIZED;
	}

	public void or(ClientStatus cs) {
		none		|= cs.none;
		connected	|= cs.connected;
		noMic		|= cs.noMic;
		init		|= cs.init;
		rec			|= cs.rec;
		upload		|= cs.upload;
		done		|= cs.done;
	}

	public void and(ClientStatus cs) {
		none		&= cs.none;
		connected	&= cs.connected;
		noMic		&= cs.noMic;
		init		&= cs.init;
		rec			&= cs.rec;
		upload		&= cs.upload;
		done		&= cs.done;
	}

	public void not() {
		none		= !none;
		connected	= !connected;
		noMic		= !noMic;
		init		= !init;
		rec			= !rec;
		upload		= !upload;
		done		= !done;
	}

	public void setNone(Boolean n) {
		none = n;
		connected 	= true;
	}

	public void setNone() {
		none		= true;
		connected 	= true;
		noMic		= false;
		init		= false;
		rec			= false;
		upload		= false;
		done		= false;
	}
	
	public void setConnected(Boolean n) {
		connected = n;
	}

	public void setConnected() {
		connected 	= true;
	}
	
	public void setDisconnected() {
		connected 	= false;
	}
	
	public void setInit(Boolean i) {
		init = i;
	}

	public void setInit() {
		none	= false;
		noMic	= false;
		init	= true;
		rec		= false;
		upload	= false;
		done	= false;
	}
	
	public void setNoMic(Boolean i) {
		noMic = i;
	}

	public void setNoMic() {
		none	= false;
		noMic	= true;
		init	= false;
		rec		= false;
		upload	= false;
		done	= false;
	}

	public void setRec(Boolean r) {
		rec = r;
	}

	public void setRec() {
		none	= false;
		noMic	= false;
		init	= false;
		rec		= true;
		upload	= false;
		done	= false;
	}

	public void setUpload(Boolean u) {
		upload = u;
	}

	public void setUpload() {
		none	= false;
		noMic	= false;
		init	= false;
		rec		= false;
		upload	= true;
		done	= false;
	}

	public void setDone(Boolean d) {
		done = d;
	}

	public void setDone() {
		none	= false;
		noMic	= false;
		init	= false;
		rec		= false;
		upload	= false;
		done	= true;
	}

	public Boolean getNone() {
		return none;
	}
	
	public Boolean getNoMic() {
		return noMic;
	}

	public Boolean getInit() {
		return init;
	}

	public Boolean getRec() {
		return rec;
	}

	public Boolean getUpload() {
		return upload;
	}

	public Boolean getDone() {
		return done;
	}
	
	public boolean isDisconnected() {
		return !connected;
	}

	public boolean isUninitialized() {
		return none && !init && !rec && !upload && !done;

	}
	
	public boolean isNoMic() {
		return noMic && !init && !rec && !upload && !done;
	}

	public boolean isInitialized() {
		return !none && !noMic && init && !rec && !upload && !done;
	}

	public boolean isRec() {
		return !none && !noMic &&!init && rec && !upload && !done;
	}

	public boolean isUpload() {
		return !none && !noMic &&!init && !rec && upload && !done;
	}

	public boolean isDone() {
		return !none && !noMic &&!init && !rec && !upload && done;
	}

	public String toString() {
		String out = "[" + String.valueOf(none);
		out += ", " + String.valueOf(init);
		out += ", " + String.valueOf(rec);
		out += ", " + String.valueOf(upload);
		out += ", " + String.valueOf(done) + "]";
		return out;
	}
}

package de.uni.goettingen.REARController.DataStruct;

import java.io.Serializable;

public class ClientStatus implements Serializable {
	private static final long serialVersionUID = 6835731377460284019L;
	
	private Boolean none;
	private Boolean init;
	private Boolean rec;
	private Boolean upload;
	private Boolean done;
	
	public ClientStatus() {
		none	= true;
		init	= false;
		rec		= false;
		upload	= false;
		done	= false;
	}
	
	public ClientStatus(ClientStatus cs) {
		none	= cs.none;
		init	= cs.init;
		rec		= cs.rec;
		upload	= cs.upload;
		done	= cs.done;
	}
	
	public ClientStatus(Boolean n, Boolean i, Boolean r, Boolean u, Boolean d) {
		none	= n;
		init	= i;
		rec		= r;
		upload	= u;
		done	= d;
	}
	
	public ClientStatus(StatusEnum s) {
		none	= false;
		init	= false;
		rec		= false;
		upload	= false;
		done	= false;
		if(s == StatusEnum.STOPPED)
			init	= true;
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
		none	|= cs.none;
		init	|= cs.init;
		rec		|= cs.rec;
		upload	|= cs.upload;
		done	|= cs.done;
	}
	
	public void and(ClientStatus cs) {
		none	&= cs.none;
		init	&= cs.init;
		rec		&= cs.rec;
		upload	&= cs.upload;
		done	&= cs.done;
	}
	
	public void not() {
		none	= !none;
		init	= !init;
		rec		= !rec;
		upload	= !upload;
		done	= !done;
	}
	
	public void setNone(Boolean n) {
		none = n;
	}
	
	public void setNone() {
		none	= true;
		init	= false;
		rec		= false;
		upload	= false;
		done	= false;
	}
	
	public void setInit(Boolean i) {
		init = i;
	}
	
	public void setInit() {
		none	= false;
		init	= true;
		rec		= false;
		upload	= false;
		done	= false;
	}
	
	public void setRec(Boolean r) {
		rec = r;
	}
	
	public void setRec() {
		none	= false;
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
		init	= false;
		rec		= false;
		upload	= false;
		done	= true;
	}
	
	public Boolean getNone() {
		return none;
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

	public boolean isUninitialized() {
		if(none && !init && !rec && !upload && !done)
			return true;
		return false;
	}
	
	public boolean isInitialized() {
		if(!none && init && !rec && !upload && !done)
			return true;
		return false;
	}
	
	public boolean isRec() {
		if(!none && !init && rec && !upload && !done)
			return true;
		return false;
	}
	
	public boolean isUpload() {
		if(!none && !init && !rec && upload && !done)
			return true;
		return false;
	}
	
	public boolean isDone() {
		if(!none && !init && !rec && !upload && done)
			return true;
		return false;
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

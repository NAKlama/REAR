package de.uni.goettingen.REARController.Net;

import java.io.Serializable;

import de.uni.goettingen.REARController.DataStruct.StatusEnum;

public class ClientStatus implements Serializable {
	private static final long serialVersionUID = -43648760049411008L;
	private Boolean init;
	private Boolean rec;
	private Boolean upload;
	private Boolean done;
	
	public ClientStatus() {
		init	= false;
		rec		= false;
		upload	= false;
		done	= false;
	}
	
	public ClientStatus(ClientStatus cs) {
		init	= cs.init;
		rec		= cs.rec;
		upload	= cs.upload;
		done	= cs.done;
	}
	
	public ClientStatus(Boolean i, Boolean r, Boolean u, Boolean d) {
		init	= i;
		rec		= r;
		upload	= u;
		done	= d;
	}
	
	public ClientStatus(StatusEnum s) {
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
		init	|= cs.init;
		rec		|= cs.rec;
		upload	|= cs.upload;
		done	|= cs.done;
	}
	
	public void and(ClientStatus cs) {
		init	&= cs.init;
		rec		&= cs.rec;
		upload	&= cs.upload;
		done	&= cs.done;
	}
	
	public void not() {
		init	= !init;
		rec		= !rec;
		upload	= !upload;
		done	= !done;
	}
	
	public void setInit(Boolean i) {
		init = i;
	}
	
	public void setRec(Boolean r) {
		rec = r;
	}
	
	public void setUpload(Boolean u) {
		upload = u;
	}
	
	public void setDone(Boolean d) {
		done = d;
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
		if(!init && !rec && !upload && !done)
			return true;
		return false;
	}
}

package de.uni.goettingen.REARController.DataStruct;

import java.io.Serializable;

public class ClientStatus implements Serializable {
	private static final long serialVersionUID = 6835731377460284019L;

	private Boolean none			= false;
	private Boolean noMic			= false;
	private Boolean init			= false;
	private Boolean rec				= false;
	private Boolean upload			= false;
	private Boolean done			= false;
	private Boolean connected		= false;
	private Boolean rec_test		= false;
	private Boolean rec_test_done	= false;

	public ClientStatus() {
		none			= false;
		noMic			= false;
		init			= false;
		rec				= false;
		upload			= false;
		done			= false;
		connected		= false;
		rec_test		= false;
		rec_test_done	= false;
	}

	public ClientStatus(ClientStatus cs) {
		if(cs != null) {
			none			= cs.none;
			noMic			= cs.noMic;
			init			= cs.init;
			rec				= cs.rec;
			upload			= cs.upload;
			done			= cs.done;
			connected		= cs.connected;
			rec_test		= cs.rec_test;
			rec_test_done	= cs.rec_test_done;
		}
		else
		{
			none			= false;
			noMic			= false;
			init			= false;
			rec				= false;
			upload			= false;
			done			= false;
			connected		= false;
			rec_test		= false;
			rec_test_done	= false;
		}
	}

	public ClientStatus(Boolean n, Boolean nmic, Boolean i, Boolean r, Boolean u, Boolean d, Boolean rt, Boolean rtd) {
		none			= n;
		noMic			= nmic;
		init			= i;
		rec				= r;
		upload			= u;
		done			= d;
		connected		= true;
		rec_test		= rt;
		rec_test_done	= rtd;
	}

	public ClientStatus(StatusEnum s) {
		none			= false;
		noMic			= false;
		init			= false;
		rec				= false;
		upload			= false;
		done			= false;
		rec_test		= false;
		rec_test_done	= false;
		connected		= true;
		if(s == StatusEnum.NOTCONNECTED)
			connected = false;
		else if(s == StatusEnum.STOPPED)
			init			= true;
		else if(s == StatusEnum.NOMIC)
			noMic			= true;
		else if(s == StatusEnum.RECORDING)
			rec				= true;
		else if(s == StatusEnum.UPLOADING)
			upload			= true;
		else if(s == StatusEnum.DONE)
			done			= true;
		else if(s == StatusEnum.UNINITIALIZED)
			none			= true;
		else if(s == StatusEnum.REC_TEST_INIT)
			rec_test		= true;
		else if(s == StatusEnum.REC_TEST_DONE)
			rec_test_done	= true;
	}

	public StatusEnum getStatus() {
		Boolean a = init 	 && ( rec	  || upload	|| done     || rec_test || rec_test_done);
		Boolean b = rec  	 && ( upload	  || done		|| rec_test || rec_test_done);
		Boolean c = upload	 && ( done     || rec_test || rec_test_done);
		Boolean d = done	 && ( rec_test || rec_test_done);
		Boolean e = rec_test && rec_test_done;
		if(!connected)
			return StatusEnum.NOTCONNECTED;
		if(noMic)
			return StatusEnum.NOMIC;
		if(a || b || c || d || e) 
			return StatusEnum.MULTI_STATUS;
		if(init)
			return StatusEnum.STOPPED;
		if(rec)
			return StatusEnum.RECORDING;
		if(upload)
			return StatusEnum.UPLOADING;
		if(done)
			return StatusEnum.DONE;
		if(rec_test)
			return StatusEnum.REC_TEST_INIT;
		if(rec_test_done)
			return StatusEnum.REC_TEST_DONE;
		return StatusEnum.UNINITIALIZED;
	}

	public void or(ClientStatus cs) {
		if(cs != null) {
			none			|= cs.none;
			connected		|= cs.connected;
			noMic			|= cs.noMic;
			init			|= cs.init;
			rec				|= cs.rec;
			upload			|= cs.upload;
			done			|= cs.done;
			rec_test		|= cs.rec_test;
			rec_test_done	|= cs.rec_test_done;
		}
	}

	public void and(ClientStatus cs) {
		if(cs != null) {
			none			&= cs.none;
			connected		&= cs.connected;
			noMic			&= cs.noMic;
			init			&= cs.init;
			rec				&= cs.rec;
			upload			&= cs.upload;
			done			&= cs.done;
			rec_test		&= cs.rec_test;
			rec_test_done	&= cs.rec_test_done;
		}
	}

	public void not() {
		none			= !none;
		connected		= !connected;
		noMic			= !noMic;
		init			= !init;
		rec				= !rec;
		upload			= !upload;
		done			= !done;
		rec_test		= !rec_test;
		rec_test_done	= !rec_test_done;
	}

	public void setNone(Boolean n) {
		none = n;
		connected 	= true;
	}

	public void setNone() {
		none			= true;
		connected 		= true;
		noMic			= false;
		init			= false;
		rec				= false;
		upload			= false;
		done			= false;
		rec_test		= false;
		rec_test_done	= false;
	}

	public void setConnected(Boolean n) {
		if(n != null)
			connected = n;
	}

	public void setConnected() {
		connected 	= true;
	}

	public void setDisconnected() {
		connected 	= false;
	}

	public void setInit(Boolean i) {
		if(i != null)
			init = i;
	}

	public void setInit() {
		none			= false;
		noMic			= false;
		init			= true;
		rec				= false;
		upload			= false;
		done			= false;
		connected 		= true;
		rec_test		= false;
		rec_test_done	= false;
	}

	public void setNoMic(Boolean i) {
		if(i != null)
			noMic = i;
	}

	public void setNoMic() {
		none	= false;
		noMic	= true;
		init	= false;
		rec		= false;
		upload	= false;
		done	= false;
		connected 	= true;
		rec_test		= false;
		rec_test_done	= false;
	}

	public void setRec(Boolean r) {
		if(r != null)
			rec = r;
	}

	public void setRec() {
		none	= false;
		noMic	= false;
		init	= false;
		rec		= true;
		upload	= false;
		done	= false;
		connected 	= true;
		rec_test		= false;
		rec_test_done	= false;
	}

	public void setUpload(Boolean u) {
		if(u != null)
			upload = u;
	}

	public void setUpload() {
		none	= false;
		noMic	= false;
		init	= false;
		rec		= false;
		upload	= true;
		done	= false;
		connected 	= true;
		rec_test		= false;
		rec_test_done	= false;
	}

	public void setDone(Boolean d) {
		if(d != null)
			done = d;
	}

	public void setDone() {
		none	= false;
		noMic	= false;
		init	= false;
		rec		= false;
		upload	= false;
		done	= true;
		connected 	= true;
		rec_test		= false;
		rec_test_done	= false;
	}

	public void setRecTest(Boolean rt) {
		if(rt != null)
			rec_test = rt;
	}

	public void setrecTest() {
		none	= false;
		noMic	= false;
		init	= false;
		rec		= false;
		upload	= false;
		done	= false;
		connected 	= true;
		rec_test		= true;
		rec_test_done	= false;
	}
	
	public void setRecTestDone(Boolean rtd) {
		if(rtd != null)
			rec_test_done = rtd;
	}

	public void setrecTestDone() {
		none	= false;
		noMic	= false;
		init	= false;
		rec		= false;
		upload	= false;
		done	= false;
		connected 	= true;
		rec_test		= false;
		rec_test_done	= true;
	}
	
	public Boolean getNone() {
		if(none == null)
			return false;
		return none;
	}

	public Boolean getNoMic() {
		if(noMic == null)
			return false;
		return noMic;
	}

	public Boolean getInit() {
		if(init == null)
			return false;
		return init;
	}

	public Boolean getRec() {
		if(rec == null)
			return false;
		return rec;
	}

	public Boolean getUpload() {
		if(upload == null)
			return false;
		return upload;
	}

	public Boolean getDone() {
		if(done == null)
			return false;
		return done;
	}

	public Boolean getRecTest() {
		if(rec_test == null)
			return false;
		return rec_test;
	}

	public Boolean getRecTestDone() {
		if(rec_test_done == null)
			return false;
		return rec_test_done;
	}

	public boolean isDisconnected() {
		if(connected == null)
			return false;
		return !connected;
	}

	public boolean isUninitialized() {
		return none && !init && !rec && !upload && !done && !rec_test && !rec_test_done;

	}

	public boolean isNoMic() {
		return noMic && !init && !rec && !upload && !done&& !rec_test && !rec_test_done;
	}

	public boolean isInitialized() {
		return !none && !noMic && init && !rec && !upload && !done&& !rec_test && !rec_test_done;
	}

	public boolean isRec() {
		return !none && !noMic &&!init && rec && !upload && !done&& !rec_test && !rec_test_done;
	}

	public boolean isUpload() {
		return !none && !noMic &&!init && !rec && upload && !done&& !rec_test && !rec_test_done;
	}

	public boolean isDone() {
		return !none && !noMic &&!init && !rec && !upload && done&& !rec_test && !rec_test_done;
	}
	
	public boolean isRecTest() {
		return !none && !noMic &&!init && !rec && !upload && !done&& rec_test && !rec_test_done;
	}
	
	public boolean isRecTestDone() {
		return !none && !noMic &&!init && !rec && !upload && !done&& !rec_test && rec_test_done;
	}
	
	public String toString() {
		String out = "[" + String.valueOf(none);
		out += ", " + String.valueOf(init);
		out += ", " + String.valueOf(rec);
		out += ", " + String.valueOf(upload);
		out += ", " + String.valueOf(done);
		out += ", " + String.valueOf(rec_test);
		out += ", " + String.valueOf(rec_test_done);
		out += "]";
		return out;
	}
}

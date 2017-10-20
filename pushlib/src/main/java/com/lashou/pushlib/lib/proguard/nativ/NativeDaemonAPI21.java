package com.lashou.pushlib.lib.proguard.nativ;


import android.content.Context;

import com.lashou.pushlib.lib.proguard.NativeDaemonBase;

/**
 * native code to watch each other when api over 21 (contains 21)
 * @author Mars
 *
 */
public class NativeDaemonAPI21 extends NativeDaemonBase {

	public NativeDaemonAPI21(Context context) {
		super(context);
	}

	static{
		try {
			System.loadLibrary("daemon_api21");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public native void doDaemon(String indicatorSelfPath, String indicatorDaemonPath, String observerSelfPath, String observerDaemonPath);
}

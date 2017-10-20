package com.lashou.pushlib.lib.proguard.nativ;


import android.content.Context;

import com.lashou.pushlib.lib.proguard.NativeDaemonBase;

/**
 * native code to watch each other when api under 20 (contains 20)
 * @author Mars
 *
 */
public class NativeDaemonAPI20 extends NativeDaemonBase {
	
	public NativeDaemonAPI20(Context context) {
		super(context);
	}

	static{
		try {
			System.loadLibrary("daemon_api20");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public native void doDaemon(String pkgName, String svcName, String daemonPath);
	
}

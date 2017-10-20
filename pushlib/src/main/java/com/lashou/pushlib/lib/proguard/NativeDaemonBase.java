package com.lashou.pushlib.lib.proguard;

import android.content.Context;

/**
 * native base class
 * 
 * @author Mars
 *
 */
public class NativeDaemonBase {
	/**
	 * used for native
	 */
	protected 	Context			mContext;
	
    public NativeDaemonBase(Context context){
        this.mContext = context;
    }

    /**
     * native call back
     */
	protected void onDaemonDead(){
		IDaemonStrategy.Fetcher.fetchStrategy().onDaemonDead();
    }
    
}

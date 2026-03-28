
package ${YYAndroidPackageName};

import com.yoyogames.runner.RunnerJNILib;

import android.util.Log;

import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.KeyEvent;

public class AndroidInputs extends RunnerSocial {
    private static final int EVENT_OTHER_SOCIAL = 70;	

    private boolean currentMainInputIsTouch = false;
    private boolean currentMainKeyboardInputIsHardware = false;

    @Override
    public boolean onTouchEvent(final MotionEvent event){
        if (currentMainInputIsTouch != true) {
            currentMainInputIsTouch = true;
            Log.i("yoyo", "Switched to touch input");
            int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
            RunnerJNILib.DsMapAddString(dsMapIndex, "event_type", "android_input_detected");
            RunnerJNILib.DsMapAddString(dsMapIndex, "input_type", "touch");
            RunnerJNILib.CreateAsynEventWithDSMap( dsMapIndex, EVENT_OTHER_SOCIAL);  
        }
        return true;       
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event){
        if (currentMainInputIsTouch == true) {
            currentMainInputIsTouch = false;
            Log.i("yoyo", "Switched to mouse input");
            int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
            RunnerJNILib.DsMapAddString(dsMapIndex, "event_type", "android_input_detected");
            RunnerJNILib.DsMapAddString(dsMapIndex, "input_type", "mouse");
            RunnerJNILib.CreateAsynEventWithDSMap( dsMapIndex, EVENT_OTHER_SOCIAL);  
        }
        return true;            
    }
    

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		int eventSource = event.getSource();
		boolean eventFromPhysicalDevice = (event.getDeviceId() > 0);
		boolean eventFromKeyboard = ((eventSource & InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD);

        if (eventFromKeyboard) {
            if (currentMainKeyboardInputIsHardware && !eventFromPhysicalDevice) {
                currentMainKeyboardInputIsHardware = false;
                Log.i("yoyo", "Switched to software keyboard input");
                int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
                RunnerJNILib.DsMapAddString(dsMapIndex, "event_type", "android_input_detected");
                RunnerJNILib.DsMapAddString(dsMapIndex, "input_type", "software_keyboard");
                RunnerJNILib.CreateAsynEventWithDSMap( dsMapIndex, EVENT_OTHER_SOCIAL);                 
            }
            else if (!currentMainKeyboardInputIsHardware && eventFromPhysicalDevice) {
                currentMainKeyboardInputIsHardware = true;
                Log.i("yoyo", "Switched to hardware keyboard input");
                int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
                RunnerJNILib.DsMapAddString(dsMapIndex, "event_type", "android_input_detected");
                RunnerJNILib.DsMapAddString(dsMapIndex, "input_type", "hardware_keyboard");
                RunnerJNILib.CreateAsynEventWithDSMap( dsMapIndex, EVENT_OTHER_SOCIAL);                 
            }
        }
        return true;
    }
}
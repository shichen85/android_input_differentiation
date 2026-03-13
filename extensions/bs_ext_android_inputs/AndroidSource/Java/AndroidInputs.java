
package ${YYAndroidPackageName};

import ${YYAndroidPackageName}.R;
import ${YYAndroidPackageName}.RunnerActivity;

import android.util.Log;

import android.content.Context;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.KeyEvent;
import android.content.pm.PackageManager;

public class AndroidInputs extends RunnerSocial {

    private enum ToolType {
        FINGER, MOUSE, STYLUS, ERASER, UNKNOWN
    }

    private enum HoverAction {
        ENTER, MOVE, EXIT
    }

    private interface InputListener {
        void onTouchInput(ToolType toolType, float x, float y, MotionEvent event);
        void onHoverInput(HoverAction action, float x, float y, MotionEvent event);
    }

    private InputListener listener;

    // Attach to any View
    private void attachToView(View view) {
        Log.i("yoyo", "Attaching input listeners to view");
        view.setOnTouchListener((v, event) -> handleTouchEvent(event));
        view.setOnGenericMotionListener((v, event) -> handleGenericMotionEvent(event));
    }

    // --- Touch Events (finger, mouse click, stylus tap) ---

    private boolean handleTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) return false;

        ToolType toolType = getToolType(event.getToolType(0));
        float x = event.getX();
        float y = event.getY();

        if (listener != null) {
            listener.onTouchInput(toolType, x, y, event);
        }
        return true;
    }

    // --- Generic Motion Events (mouse hover) ---

    private boolean handleGenericMotionEvent(MotionEvent event) {
        if (!event.isFromSource(InputDevice.SOURCE_CLASS_POINTER)) return false;

        float x = event.getX();
        float y = event.getY();
        HoverAction action;

        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
                action = HoverAction.ENTER;
                break;
            case MotionEvent.ACTION_HOVER_MOVE:
                action = HoverAction.MOVE;
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                action = HoverAction.EXIT;
                break;
            default:
                return false;
        }

        if (listener != null) {
            listener.onHoverInput(action, x, y, event);
        }
        return true;
    }

    // --- Map MotionEvent tool type int to our enum ---

    private ToolType getToolType(int motionEventToolType) {
        switch (motionEventToolType) {
            case MotionEvent.TOOL_TYPE_FINGER: return ToolType.FINGER;
            case MotionEvent.TOOL_TYPE_MOUSE:  return ToolType.MOUSE;
            case MotionEvent.TOOL_TYPE_STYLUS: return ToolType.STYLUS;
            case MotionEvent.TOOL_TYPE_ERASER: return ToolType.ERASER;
            default:                           return ToolType.UNKNOWN;
        }
    }

    public void _android_setup_input_listener() {
        Log.i("yoyo", "Setting up input listener");
        this.listener = new InputListener() {
            @Override
            public void onTouchInput(ToolType toolType, float x, float y, MotionEvent event) {
                switch (toolType) {
                    case FINGER:
                        Log.i("yoyo", "Finger tap at " + x + ", " + y);
                        // Show touch UI, hide cursor highlights, etc.
                        break;
                    case MOUSE:
                        Log.i("yoyo", "Mouse click at " + x + ", " + y);
                        // Handle right-click: event.getButtonState() == MotionEvent.BUTTON_SECONDARY
                        break;
                    case STYLUS:
                        Log.i("yoyo", "Stylus tap — pressure: " + event.getPressure());
                        break;
                    case ERASER:
                        Log.i("yoyo", "Stylus eraser end");
                        break;
                    default:
                        Log.i("yoyo", "Unknown input at " + x + ", " + y);
                }
            }

            @Override
            public void onHoverInput(HoverAction action, float x, float y, MotionEvent event) {
                switch (action) {
                    case ENTER:
                        Log.i("yoyo", "Mouse entered view");
                        break;
                    case MOVE:
                        Log.i("yoyo", "Mouse hovering at " + x + ", " + y);
                        break;
                    case EXIT:
                        Log.i("yoyo", "Mouse left view");
                        break;
                }
            }
        };

        View mainView = RunnerActivity.CurrentActivity.findViewById(R.id.demogl);
        Log.i("yoyo", "The main view is: " + mainView.toString());
        this.attachToView(mainView);
    }


    private boolean isHardwareKeyboard(InputDevice device) {
        return (device.getSources() & InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD
            && device.getKeyboardType() == InputDevice.KEYBOARD_TYPE_ALPHABETIC;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        InputDevice device = InputDevice.getDevice(event.getDeviceId());
        if (device != null) {
            if (isHardwareKeyboard(device)) {
                Log.i("yoyo",  "Hardware keyboard: " + device.getName());
            } else {
                Log.i("yoyo",  "Virtual keyboard (IME)");
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private static final String DEVICE_CHROMEOS = "ChromeOS";
    private static final String DEVICE_PC = "PC/Emulator";
    private static final String DEVICE_ANDROID = "Android Device";

    public String _android_get_device_type() {
        PackageManager pm = RunnerActivity.CurrentActivity.getPackageManager();

        // ChromeOS check (most reliable)
        if (isChromeOS(pm)) {
            return DEVICE_CHROMEOS;
        }

        // PC/Emulator check
        if (isPC(pm)) {
            return DEVICE_PC;
        }

        return DEVICE_ANDROID;
    }

    private static boolean isChromeOS(PackageManager pm) {
        return pm.hasSystemFeature("org.chromium.arc") ||
               pm.hasSystemFeature("org.chromium.arc.device_management");
    }

    private static boolean isPC(PackageManager pm) {
        String arch = System.getProperty("os.arch", "");
        boolean isX86 = arch.contains("x86") || arch.contains("amd64");
        boolean isPC = pm.hasSystemFeature(PackageManager.FEATURE_PC);
        boolean noTouchscreen = !pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);

        return isX86 && (isPC || noTouchscreen);
    }

    private static boolean isAndroidDevice(PackageManager pm) {
        return !isChromeOS(pm) && !isPC(pm);
    }    
}
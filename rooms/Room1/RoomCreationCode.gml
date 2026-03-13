if os_type == os_android{
	_android_setup_input_listener();	
	keyboard_virtual_show(kbv_type_numbers, kbv_returnkey_default, kbv_autocapitalize_none, false);
	show_debug_message("device type:");
	show_debug_message(_android_get_device_type());
}
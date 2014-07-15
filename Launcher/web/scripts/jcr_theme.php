<?php
	define("IMPASS_CHECK", true);
	
	include("../jcr_settings.php");
	include("jcr_security.php");
	
	$action		=	$_GET["action"];
	$verApp		=	$_GET["version"];
	$request	=	$_GET["request"];
	
	if ($action == "theme") // Онлайн-тема
	{
		if ($version == $verApp)
		for ($i = 0; $i < count($online_elements); $i++)
		{
			if ($request == $online_elements[$i])
			header("Location: " . "../files/".$themeFolder."/".$online_elements[$i]);
		}
		
		if ($request == "color") echo $themeColorDisable.":s:".$themeColorStatic;
	} else if ($action == "settings") // Онлайн-настройки
	{
		if ($online_theme) $use_online_theme = "true"; else $use_online_theme = "false";
		echo $use_online_theme;
		
		echo "<::>";
		
		if ($online_theme && $request == "elements" && $version == $verApp)
		{
			for ($i = 0; $i < count($online_elements); $i++) echo $online_elements[$i]."<:i:>";
		}
		
		echo "<::>";
		
		for ($i = 0; $i < count($blocked_processes); $i++) echo $blocked_processes[$i]."<:i:>";
		
		echo "<::>";
		
		echo Guard::encrypt(JGuard::stir_string($HideAESKey), $AESKey);
		
		echo "<::>";
		
		echo sha1(handle_md5(md5($debugKey)));
		
		echo "<::>";
		
		echo
			parse_boolean($use_mods_delete)."<:g:>".			// [0]
			parse_boolean($use_send_report)."<:g:>".			// [1]
			parse_boolean($use_jar_check)."<:g:>".				// [2]
			parse_boolean($use_mod_check)."<:g:>".				// [3]
			parse_boolean($stop_dirty_drogram)."<:g:>".			// [4]
			parse_boolean($use_mod_check_timer)."<:g:>".		// [5]
			$time_for_mods_check."<:g:>".						// [6]
			parse_boolean($use_process_check)."<:g:>".			// [7]
			parse_boolean($show_all_processes)."<:g:>".			// [8]
			parse_boolean($use_process_check_timer)."<:g:>".	// [9]
			$time_for_process_check;							// [10]
	}
	
	/* POST-операции */
	
	$action		=	$_POST["action"];
	$client		=	$_POST["client"];
	$upd_files	=	$_POST["updateFiles"];
	
	if ($action == "updateSize") // Суммарный размер предстоящего обновления
	{
		if ($client == null || $upd_files == null) die (0);
		
		$filesize = 0; $elements = explode("<:f:>", $upd_files);
		for ($i = 0; $i < count($elements); $i++)
		{
			$file_path = "../files/clients/".$client."/".$elements[$i];
			if (file_exists($file_path)) $filesize += filesize($file_path);
		}
		
		echo "=:=".$filesize;
	}
	
	/* *** FUNCTIONS *** */
	
	// Удаляет 0 в начале строки, т.к. Java не включает 0 в начало строки при генерации хеш-суммы
	function handle_md5($string)
	{
		if (strcasecmp(substr($string, 0, 1), "0") == 0)
			$string = substr($string, 1);
			
		return $string;
	}
	
	function parse_boolean($boolean)
	{
		if ($boolean) return "true";
		else return "false";
	}
?>
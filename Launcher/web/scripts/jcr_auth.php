<?php
	define("IMPASS_CHECK", true);
	
	include("../jcr_connect.php");
	include("../jcr_settings.php");
	include("../jcr_uuid.php");
	include("jcr_security.php");
	
	@$action	= mysql_escape_string($_POST["action"]);
	@$getLogin	= mysql_escape_string($_POST["login"]);
	@$getPass	= mysql_escape_string($_POST["password"]);
	@$appHash	= mysql_escape_string($_POST["hash"]);
	@$appForm	= mysql_escape_string($_POST["format"]);
	@$client	= mysql_escape_string($_POST["client"]);
	@$cl_vers	= mysql_escape_string($_POST["version"]);
	@$forge		= mysql_escape_string($_POST["forge"]);
	@$liteloader= mysql_escape_string($_POST["liteloader"]);
	@$hwid		= mysql_escape_string($_POST["mac"]);
	@$secCode	= mysql_escape_string($_POST["code"]);
	@$files		= mysql_escape_string($_POST["files"]);
	@$message	= mysql_escape_string($_POST["message"]);
	@$authSes	= mysql_escape_string($_POST["session"]);
	if (!($secCode == sha1($protectKey))) die("BadCode");
	
	if (!preg_match("/^[a-zA-Z0-9_]+$/", $getLogin) || !preg_match("/^[a-zA-Z0-9_]+$/", $getPass)) die("BadData");
	
	$hwid		= $hwid;
	$getLogin	= $getLogin;
	$injLogin	= $getLogin;
	
	if ($crypt == 'hash_md5' || $crypt == 'hash_authme' || $crypt == 'hash_xauth' || $crypt == 'hash_cauth' || $crypt == 'hash_joomla' || $crypt == 'hash_wordpress' || $crypt == 'hash_dle' || $crypt == 'hash_drupal' || $crypt == 'hash_webmcr')
	{
		$query    = $db -> query("SELECT $db_colUser, $db_colPass, $db_colUserStat FROM $db_table WHERE $db_colUser='$getLogin'") or die($db -> error);
		$row      = $query -> fetch_assoc();
		$getLogin = $row[$db_colUser];
		$realPass = $row[$db_colPass];
		$userStat = $row[$db_colUserStat];
	} else if ($crypt == 'hash_ipb' || $crypt == 'hash_vbulletin')
	{
		$query    = $db -> query("SELECT $db_colId, $db_colUser, $db_colPass, $db_colSalt, $db_colUserStat FROM $db_table WHERE $db_colUser='$getLogin'") or die($db -> error);
		$row      = $query -> fetch_assoc();
		$getLogin = $row[$db_colUser];
		$realPass = $row[$db_colPass];
		$salt     = $row[$db_colSalt];
		$userStat = $row[$db_colUserStat];
	} else if ($crypt == 'hash_xenforo')
	{
		$query    = $db -> query("SELECT $db_table.$db_colId, $db_table.$db_colUser, $db_table.$db_colUserStat, $db_tableOther.$db_colId, $db_tableOther.$db_colPass FROM $db_table, $db_tableOther WHERE $db_table.$db_colId = $db_tableOther.$db_colId AND $db_table.$db_colUser='$getLogin'") or die($db -> error);
		$row      = $query -> fetch_assoc();
		$getLogin = $row[$db_colUser];
		$realPass = substr($row[$db_colPass], 22, 64);
		$salt     = substr($row[$db_colPass], 105, 64);
		$userStat = $row[$db_colUserStat];
	} else die ("Error: Unknown encryption format"); $hashPass = $crypt();
	
	if ($injLogin == null && $getPass == null) die ("BadParams");
	else if (!(strcasecmp($injLogin, $getLogin) == 0)) die("BadData");
	else if (!($realPass == $hashPass)) die("BadData");
	
	if ($action == "auth")
	{
		if ($client == "null") die ("BadParams");
		
		$first_hwid_auth = "false";
		if ($use_hwid_search) $first_hwid_auth = check_user_hwid($hwid, $getLogin);
		
		$access_to_upload_cloak = check_access_to_cloak($userStat);
		
		$version_compare = version_compare($cl_vers, "1.6");
		if ($version_compare == (0 | 1))
		{
			if (!file_exists("../files/clients/$client/bin/minecraft.jar"))		$not_exists_elements[] = "'bin/minecraft.jar'";
			if (!file_exists("../files/clients/$client/assets.zip"))			$not_exists_elements[] = "'assets.zip'";
			if (!file_exists("../files/clients/$client/bin/libraries.jar"))		$not_exists_elements[] = "'bin/libraries.jar'";
			if (!file_exists("../files/clients/$client/extra.zip"))				$not_exists_elements[] = "'extra.zip'";
			if (!file_exists("../files/clients/$client/bin/natives.zip"))		$not_exists_elements[] = "'bin/natives.zip'";
			if (@$not_exists_elements != null) die ("Error: Some elements of client '".$client."' not detected: ".implode(", ", $not_exists_elements));
			
			if (strcasecmp($forge, "true") == 0 && !file_exists("../files/clients/$client/bin/forge.jar"))
				die ("Error: Some elements of client '".$client."' not detected: 'bin/forge.jar'");
				
			if (strcasecmp($liteloader, "true") == 0 && !file_exists("../files/clients/$client/bin/liteloader.jar"))
				die ("Error: Some elements of client '".$client."' not detected: 'bin/liteloader.jar'");
		} else
		{
			if (!file_exists("../files/clients/$client/bin/minecraft.jar"))		$not_exists_elements[] = "'bin/minecraft.jar'";
			if (!file_exists("../files/clients/$client/bin/lwjgl.jar"))			$not_exists_elements[] = "'bin/lwjgl.jar'";
			if (!file_exists("../files/clients/$client/bin/lwjgl_util.jar"))	$not_exists_elements[] = "'bin/lwjgl_util.jar'";
			if (!file_exists("../files/clients/$client/bin/jinput.jar"))		$not_exists_elements[] = "'bin/jinput.jar'";
			if (!file_exists("../files/clients/$client/extra.zip"))				$not_exists_elements[] = "'extra.zip'";
			if (!file_exists("../files/clients/$client/bin/natives.zip"))		$not_exists_elements[] = "'bin/natives.zip'";
			if (@$not_exists_elements != null) die ("Error: Some elements of client '".$client."' not detected: ".implode(", ", $not_exists_elements));
		}
		
		if (!file_exists("../files/program/".$programName.$appForm)) die ("Error: Program update file is not detected in 'files/program/".$programName.$appForm."'");
		
		$md5_zip		= md5_file("../files/clients/$client/extra.zip");
		$md5_jar		= md5_file("../files/clients/$client/bin/minecraft.jar");
		$md5_natives	= md5_file("../files/clients/$client/bin/natives.zip");
		$md5_program 	= md5_file("../files/program/".$programName.$appForm);
		if ($version_compare == (0 | 1))
		{
			$md5_ass	= md5_file("../files/clients/$client/assets.zip");
			$md5_lib	= md5_file("../files/clients/$client/bin/libraries.jar");
			
			if (strcasecmp($forge, "true") == 0)
				$sha1_md5forge = sha1(md5_file("../files/clients/$client/bin/forge.jar"));
			else $sha1_md5forge = "null";
			
			if (strcasecmp($liteloader, "true") == 0)
				$sha1_md5lloader = sha1(md5_file("../files/clients/$client/bin/liteloader.jar"));
			else $sha1_md5lloader = "null";
			
			$md5_lwjql	= $md5_lwjql_util = $md5_jinput = "null";
		} else
		{
			$md5_lwjql		= md5_file("../files/clients/$client/bin/lwjgl.jar");
			$md5_lwjql_util	= md5_file("../files/clients/$client/bin/lwjgl_util.jar");
			$md5_jinput		= md5_file("../files/clients/$client/bin/jinput.jar");
			
			$md5_ass = $md5_lib	= $sha1_md5forge = $sha1_md5lloader = "null";
		}
		
		$uuid			= uuidConvert($getLogin); // md5($getLogin);
		$sha1_md5zip	= sha1($md5_zip);
		$sha1_pass		= sha1($realPass);
		$sha1_version	= sha1($version);
		$seskey			= generate_session_id();
		$last_version	= "false";
		
		if(strtolower($appHash) == strtolower($md5_program)) $last_version = "true";
		
		echo $sha1_md5zip."<::>".$md5_jar."<::>".$md5_lwjql."<::>".$md5_lwjql_util."<::>".$md5_jinput."<::>"."<br>".
			$sha1_version."<::>".$sha1_pass."<::>".Guard::encrypt(JGuard::stir_string($seskey), $HideAESKey)."<::>".$last_version."<::>"."<br>";
		
		$mods = array(); get_mods_list(null, $mods); if (count($mods) == 0) $mods[] = "nomods";
		
		echo implode("<:f:>", $mods)."<::>null<::>".get_image_url("skin")."<::>".get_image_url("cloak")."<::>";
		
		if ($version_compare != (0 | 1))
		{
			$coremods = array(); get_coremods_list(null, $coremods); if (count($coremods) == 0) $coremods[] = "nocoremods";
			echo implode("<:f:>", $coremods);
		} else echo "nocoremods";
		
		$configs = array(); get_config_list(null, $configs); if (count($configs) == 0) $configs[] = "noconfigs";
		$check_f = array(); get_checked_files_list(null, $check_f); if (count($check_f) == 0) $check_f[] = "nocheckfs";
		
		if ($upload_images && $skins_url == null && $cloaks_url == null) $use_upload_images = 'true'; else $use_upload_images = 'false';
		if ($access_to_upload_cloak) $can_upload_cloak = 'true'; else $can_upload_cloak = 'false';
		
		$db -> query("UPDATE $db_table SET $db_colSesId='$seskey', $db_colAuthId='$seskey', $db_colUUID='$uuid' WHERE $db_colUser='$injLogin'") or die ("Error2");
		
		echo "<br>"."<::>".$programName.$appForm."<::>".$md5_natives."<::>".$md5_ass."<::>".$md5_lib."<::>".$sha1_md5forge."<::>".
		$sha1_md5lloader."<::>".handle_md5(md5($md5_program))."<::>".$first_hwid_auth."<::>". "<br>" .implode("<:f:>", $configs)."<::>".$getLogin."<br>".
		"<::>".implode("<:f:>", $check_f)."<::>".$use_upload_images."<::>".$can_upload_cloak."<::>".Guard::encrypt(JGuard::stir_string($uuid), $HideAESKey);
		
	} else if ($action == "report")
	{
		if ($getLogin == null || $files == null || $authSes == null) die ("BadParams");
		
		$mq = $db -> query("SELECT $db_colUser FROM $db_table WHERE $db_colUser='$getLogin' AND $db_colAuthId='$authSes'") or die ("Error3");
		
		if ($mq -> num_rows == 1)
		{
			$file = "../files/admin/".$report_file;
			
			$find_files_array = explode("<:f:>", $files);
			for ($i = 0; $i < count($find_files_array); $i++)
			{
				$find_files = $find_files." * ".$find_files_array[$i]."\n";
			}
			
			if ($message != null) $note = "Message: ".$message."\n"; else $note = "";
			
			$text = "Date: ".date("d.m.Y H:i")."\nLogin: ".$getLogin."\nFound files (".count($find_files_array)."):\n".$find_files.$note."*************************\n";
			
			$fp = fopen($file, 'a') or die ("Error1");
			fwrite($fp, $text);
			fclose($fp);
			echo "Done";
		} else die ("Error2");
	} else if ($action == "upload_skin")
	{
		if (!$upload_images) die ("NoAccess");
		if (!is_uploaded_file($_FILES['userfile']['tmp_name'])) die ("Error_1");
		
		$img_info = getimagesize($_FILES['userfile']['tmp_name']); 
		if ($img_info['mime'] != 'image/png' || $img_info["0"] != '64' || $img_info["1"] != '32') die("Error_2");
		
		$login = $lowerSkinsCase ? strtolower($getLogin) : $getLogin;
		$move_to_file = "../files/skins/".$login.".png";
		if (move_uploaded_file($_FILES['userfile']['tmp_name'], $move_to_file)) echo "Success"; else echo "Error_3";
	} else if ($action == "upload_cloak")
	{
		if (!$upload_images && !$upload_cloaks && $userStat != "1") die ("NoAccess");
		if (!is_uploaded_file($_FILES['userfile']['tmp_name'])) die ("Error_1");
		
		$img_info = getimagesize($_FILES['userfile']['tmp_name']); 
		if ($img_info['mime'] == 'image/png')
		{
			if (($img_info["0"] == '64' && $img_info["1"] == '32') || ($img_info["0"] == '22' && $img_info["1"] == '17'));
			else die ("Error_2");
		} else die ("Error_2");
		
		$login = $lowerSkinsCase ? strtolower($getLogin) : $getLogin;
		$move_to_file = "../files/cloaks/".$login.".png";
		if (move_uploaded_file($_FILES['userfile']['tmp_name'], $move_to_file)) echo "Success"; else die ("Error_3");
	}
	
	/* *** FUNCTIONS *** */
	
	// Удаляет 0 в начале строки, т.к. Java не включает 0 в начало строки при генерации хеш-суммы
	function handle_md5($string)
	{
		if (strcasecmp(substr($string, 0, 1), "0") == 0)
			$string = substr($string, 1);
			
		return $string;
	}
	
	function generate_session_id()
	{
		$chars = array('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z');
		
		for ($i = 0; $i < 26; $i++)
		{
			$char = $chars[rand(0, 25)];
			if (rand(0, 1) == 0) $char = strtoupper($char);
			@$randStr = $randStr.$char;
		}
		
		return $randStr;
	}
	
	// Проверяет наличие прав на загрузку плаща
	function can_upload_cloak($user_status)
	{
		global $getLogin, $cloaks_url, $upload_cloaks, $use_permEx;
		
		if ($cloaks_url != null || $upload_cloaks || $user_status == "1")
		{
			return true;
			
		} else if ($use_permEx)
		{
			global $server_db_enable, $permEx_table, $permEx_colLog, $permEx_colStat, $permEx_groups;
			
			if ($server_db_enable)
			{
				include("jcr_server_connect.php");
				
				$query = $db_server -> query("SELECT $permEx_colStat FROM $permEx_table WHERE $permEx_colLog='$getLogin'") or die($db_server -> error);
				
				if ($query -> num_rows == 1)
				{
					$row		= $query -> fetch_assoc();
					$status		= $row[$permEx_colStat];
					
					for ($i = 0; $i < count($permEx_groups); $i++)
					{
						if (strcasecmp($permEx_groups[$i], $status) == 0) return true;
					}
				}
				
				return false;
			} else
			{
				global $db;
				
				$query = $db -> query("SELECT $permEx_colStat FROM $permEx_table WHERE $permEx_colLog='$getLogin'") or die($db -> error);
				
				if ($query -> num_rows == 1)
				{
					$row		= $query -> fetch_assoc();
					$status		= $row[$permEx_colStat];
					
					for ($i = 0; $i < count($permEx_groups); $i++)
					{
						if (strcasecmp($permEx_groups[$i], $status) == 0) return true;
					}
				}
				
				return false;
			}
		} else
		{
			return false;
		}
	}
	
	// Удаляет плащ, если изменился статус пользователя в меньшую сторону
	function check_access_to_cloak($user_status)
	{
		global $getLogin, $cloaks_url, $upload_cloaks;
		
		if (can_upload_cloak($user_status)) return true;
		
		$login = $lowerSkinsCase ? strtolower($getLogin) : $getLogin;
		if (file_exists("../files/cloaks/".$login.".png"))
			unlink("../files/cloaks/".$login.".png");
			
		return false;
	}
	
	function get_image_url($type)
	{
		global $getLogin, $lowerSkinsCase, $skins_url, $cloaks_url;
		
		if ($type == "skin")
		{
			$login = $lowerSkinsCase ? strtolower($getLogin) : $getLogin;
			if ($skins_url != null)
			{
				$url = $skins_url."/".$login.".png";
				$Headers = @get_headers($url);
				if (preg_match("|200|", $Headers[0]))
					return $url;
				else
					return $skins_url."/default.png";
			}
			
			if (file_exists("../files/skins/".$login.".png")) return $login.".png";
			else return "default.png";
		} else if ($type == "cloak")
		{
			$login = $lowerSkinsCase ? strtolower($getLogin) : $getLogin;
			if ($cloaks_url != null)
			{
				$url = $cloaks_url."/".$login.".png";
				$Headers = @get_headers($url);
				if (preg_match("|200|", $Headers[0]))
					return $url;
				else
					return $cloaks_url."/default.png";
			}
			
			if (file_exists("../files/cloaks/".$login.".png")) return $login.".png";
			else return "default.png";
		}
	}
	
	function check_user_hwid($hwid, $user)
	{
		if (strcasecmp($hwid, "null") == 0) die ("BadHWID");
		
		global $db, $db_table, $db_colUser, $db_colHWID, $db_colBlHWIDs;
		
		$sha1_hwid = sha1($hwid);
		
		$macvb = array( "00-50-56", "00-0C-29", "00-05-69", "00-03-FF", "00-1C-42", "00-0F-4B", "00-16-3E", "08-00-27" ); // Список HWID возможных виртуальных машин
		
		if (array_search(substr($hwid, 0, 8), $macvb) === false)
		{
			$query	= $db -> query("SELECT $db_colHWID FROM $db_table WHERE $db_colUser='$user'") or die ($db -> error);
			$row	= $query -> fetch_assoc();
			$bdHWID = $row[$db_colHWID];
			
			$query	= $db -> query("SELECT $db_colBlHWIDs FROM $db_table WHERE $db_colBlHWIDs='$sha1_hwid'") or die ($db -> error);
			if ($query -> num_rows > 0) die ("Banned");
			
			if (strcasecmp($sha1_hwid, $bdHWID) != 0)
			{
				if ($bdHWID == null)
				{
					$db -> query("UPDATE $db_table SET $db_colHWID='$sha1_hwid' WHERE $db_colUser='$user'") or die ($db -> error);
					return "true";
				} else die ("BadUserHWID");
			}
		} else die ("BadHWID");
		
		return "false";
	}
	
	function get_config_list($folder, $configs)
	{
		global $configs, $client;
		
		$start_folder = "../files/clients/$client/config/";
		if (!file_exists($start_folder)) return;
		
		$fp = opendir($start_folder.$folder);
		
		while ($cv_file = readdir($fp))
		{
			if (is_file($start_folder.$folder."/".$cv_file))
			{
				if (substr($cv_file, -4) == ".cfg" || substr($cv_file, -4) == ".txt" || substr($cv_file, -5) == ".conf")
					$configs[] = substr($folder."/".$cv_file, 1). "<:h:>" .md5_file($start_folder.$folder."/".$cv_file);
			} else if ($cv_file != "." && $cv_file != ".." && is_dir($start_folder.$folder."/".$cv_file))
			{
				get_config_list($folder."/".$cv_file, $configs);
			}
		}
		
		closedir($fp);
	}
	
	function get_checked_files_list($folder, $check_f)
	{
		global $check_f, $client;
		
		$start_folder = "../files/clients/$client/check/";
		if (!file_exists($start_folder)) return;
		
		$fp = opendir($start_folder.$folder);
		
		while ($cv_file = readdir($fp))
		{
			if (is_file($start_folder.$folder."/".$cv_file))
			{
				$check_f[] = substr($folder."/".$cv_file, 1). "<:h:>" .md5_file($start_folder.$folder."/".$cv_file);
			} else if ($cv_file != "." && $cv_file != ".." && is_dir($start_folder.$folder."/".$cv_file))
			{
				//if (strcasecmp($cv_file, "mods") != 0 && strcasecmp($cv_file, "coremods") != 0)
					get_checked_files_list($folder."/".$cv_file, $check_f);
			}
		}
		
		closedir($fp);
	}
	
	function get_mods_list($folder, $mods)
	{
		global $mods, $client;
		
		$start_folder = "../files/clients/$client/mods/";
		if (!file_exists($start_folder)) return;
		
		$fp = opendir($start_folder.$folder);
		
		while ($cv_file = readdir($fp))
		{
			if (is_file($start_folder.$folder."/".$cv_file))
			{
				if (substr($cv_file, -4) == ".zip" || substr($cv_file, -4) == ".jar" || substr($cv_file, -8) == ".litemod")
					$mods[] = substr($folder."/".$cv_file, 1). "<:h:>" .md5_file($start_folder.$folder."/".$cv_file);
			} else if ($cv_file != "." && $cv_file != ".." && is_dir($start_folder.$folder."/".$cv_file))
			{
				get_mods_list($folder."/".$cv_file, $mods);
			}
		}
		
		closedir($fp);
	}
	
	function get_coremods_list($folder, $coremods)
	{
		global $coremods, $client;
		
		$start_folder = "../files/clients/$client/coremods/";
		if (!file_exists($start_folder)) return;
		
		$fp = opendir($start_folder.$folder);
		
		while ($cv_file = readdir($fp))
		{
			if (is_file($start_folder.$folder."/".$cv_file))
			{
				if (substr($cv_file, -4) == ".zip" || substr($cv_file, -4) == ".jar" || substr($cv_file, -8) == ".litemod")
					$coremods[] = substr($folder."/".$cv_file, 1). "<:h:>" .md5_file($start_folder.$folder."/".$cv_file);
			} else if ($cv_file != "." && $cv_file != ".." && is_dir($start_folder.$folder."/".$cv_file))
			{
				get_coremods_list($folder."/".$cv_file, $coremods);
			}
		}
		
		closedir($fp);
	}
	
	// hashing

	function hash_md5()
	{
		global $getPass;
		$cryptPass = false;
		$cryptPass = md5($getPass);
		return $cryptPass;
	}

	function hash_webmcr()
	{
		global $getPass;
		$cryptPass = false;
		$cryptPass = md5($getPass);
		return $cryptPass;
	}
	
	function hash_authme()
	{
		global $realPass, $getPass;
		
		$cryptPass = false;
		$ar = preg_split("/\\$/", $realPass);
		$salt = $ar[2];
		$cryptPass = '$SHA$'.$salt.'$'.hash('sha256', hash('sha256', $getPass).$salt);
		
		return $cryptPass;
	}

	function hash_cauth()
	{
		global $realPass, $getPass;
		
		$cryptPass = false;
		if (strlen($realPass) == 32)
		{
			$cryptPass = md5($getPass);
		}
		else
		{
			$pass = md5($getPass);
			$cryptPass = substr($pass, 0, 8) . substr($pass, -23);
		}
		
		return $cryptPass;
	}

	function hash_xauth()
	{
		global $realPass, $getPass;
		
		$cryptPass = false;
		$saltPos = (strlen($getPass) >= strlen($realPass) ? strlen($realPass) : strlen($getPass));
		$salt = substr($realPass, $saltPos, 12);
		$hash = hash('whirlpool', $salt . $getPass);
		$cryptPass = substr($hash, 0, $saltPos) . $salt . substr($hash, $saltPos);
		
		return $cryptPass;
	}

	function hash_dle()
	{
		global $getPass;
		$cryptPass = false;
		$cryptPass = md5(md5($getPass));
		return $cryptPass;
	}

	function hash_joomla()
	{
		global $realPass, $getPass;
		$cryptPass = false;
		$parts = explode(':', $realPass);
		$salt = $parts[1];
		$cryptPass = md5($getPass . $salt) . ":" . $salt;
		return $cryptPass;
	}

	function hash_ipb()
	{
		global $getPass, $salt;
		$cryptPass = false;
		$cryptPass = md5(md5($salt).md5($getPass));
		return $cryptPass;
	}

	function hash_xenforo()
	{
		global $getPass, $salt;
		$cryptPass = false;
		$cryptPass = hash('sha256', hash('sha256', $getPass) . $salt);
		return $cryptPass;
	}

	function hash_wordpress()
	{
		global $realPass, $getPass;
		$cryptPass = false;
		$itoa64 = './0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
		$count_log2 = strpos($itoa64, $realPass[3]);
		$count = 1 << $count_log2;
		$salt = substr($realPass, 4, 8);
		$input = md5($salt . $getPass, TRUE);
		do $input = md5($input . $getPass, TRUE);
		while (--$count);        
		$output = substr($realPass, 0, 12);
		$count = 16;
		$i = 0;
		do 
		{
			$value = ord($input[$i++]);
			$cryptPass .= $itoa64[$value & 0x3f];
			if ($i < $count) $value |= ord($input[$i]) << 8;
			$cryptPass .= $itoa64[($value >> 6) & 0x3f];
			if ($i++ >= $count) break;
			if ($i < $count) $value |= ord($input[$i]) << 16;
			$cryptPass .= $itoa64[($value >> 12) & 0x3f];
			if ($i++ >= $count) break;
			$cryptPass .= $itoa64[($value >> 18) & 0x3f];
		} while ($i < $count);
		$cryptPass = $output . $cryptPass;
		return $cryptPass;
	}

	function hash_vbulletin()
	{
		global $getPass, $salt;
		$cryptPass = false;
		$cryptPass = md5(md5($getPass) . $salt);
		return $cryptPass;
	}

	function hash_drupal()
	{
		global $getPass, $realPass;
		$cryptPass = false;
		$setting = substr($realPass, 0, 12);
		$itoa64 = './0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
		$count_log2 = strpos($itoa64, $setting[3]);
		$salt = substr($setting, 4, 8);
		$count = 1 << $count_log2;
		$input = hash('sha512', $salt . $getPass, TRUE);
		do $input = hash('sha512', $input . $getPass, TRUE);
		while (--$count);

		$count = strlen($input);
		$i = 0;
	  
		do
		{
			$value = ord($input[$i++]);
			$cryptPass .= $itoa64[$value & 0x3f];
			if ($i < $count) $value |= ord($input[$i]) << 8;
			$cryptPass .= $itoa64[($value >> 6) & 0x3f];
			if ($i++ >= $count) break;
			if ($i < $count) $value |= ord($input[$i]) << 16;
			$cryptPass .= $itoa64[($value >> 12) & 0x3f];
			if ($i++ >= $count) break;
			$cryptPass .= $itoa64[($value >> 18) & 0x3f];
		} while ($i < $count);
		$cryptPass =  $setting . $cryptPass;
		$cryptPass =  substr($cryptPass, 0, 55);
		return $cryptPass;
	}
?>
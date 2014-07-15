<?php

	define ('IMPASS_CHECK', true);
	include ("../jcr_connect.php");
	include ("../jcr_settings.php");
	
	/* ‚ыполнение кода версий 1.7+ */
	
	if (($_SERVER['REQUEST_METHOD'] == 'POST') && (stripos($_SERVER["CONTENT_TYPE"], "application/json") === 0))
	{
		$json = json_decode($HTTP_RAW_POST_DATA);
		
		$uuid		= sql_param($json -> selectedProfile);
		$sessionid	= sql_param($json -> accessToken);
		$serverid	= sql_param($json -> serverId);
		
		if ($uuid == null || $sessionid == null || $serverid == null) die (json_error("Bad data"));
		
		$checkUUID = $db -> query("SELECT $db_colUser FROM $db_table WHERE $db_colUUID='$uuid'") or die (json_error("Error #1"));
		if ($checkUUID -> num_rows == 1) { $row = $checkUUID -> fetch_assoc(); $user = $row[$db_colUser]; } else die ("Bad UUID");
		
		$ok = array('id' => md5($sessionKey.$user), 'name' => $user);
		
		$updateSesId = sha1($sessionKey.$sessionid);
		$checkSession = $db -> query("SELECT $db_colUUID FROM $db_table WHERE $db_colUUID='$uuid' AND $db_colSesId='$sessionid'") or die (json_error("Error #2"));
		
		if ($checkSession -> num_rows == 1)
		{
			$db -> query("UPDATE $db_table SET $db_colSesId='$updateSesId' WHERE $db_colUUID='$uuid'") or die (json_error("Error #3"));
			
			$result = $db -> query("SELECT $db_colUUID FROM $db_table WHERE $db_colUUID='$uuid' AND $db_colServer='$serverid'") or die (json_error("Error #4"));
			
			if ($result -> num_rows == 1)
			{
				echo json_encode($ok);
			} else
			{
				$result = $db -> query("UPDATE $db_table SET $db_colServer='$serverid' WHERE $db_colUUID='$uuid'") or die (json_error("Error #5"));
				
				if ($db -> affected_rows == 1)
				{
					echo json_encode($ok);
				} else
					echo json_error("Bad login #1");
			}
		} else
		{
			$checkUpdateSession = $db -> query("SELECT $db_colUUID FROM $db_table WHERE $db_colUUID='$uuid' AND $db_colSesId='$updateSesId'") or die (json_error("Error #6"));
			
			if ($checkUpdateSession -> num_rows == 1)
			{
				if ($use_one_entrance)
				{
					echo json_error("You must be authorized again to play on the server.");
				} else
				{
					$result = $db -> query("SELECT $db_colUUID FROM $db_table WHERE $db_colUUID='$uuid' AND $db_colServer='$serverid'") or die (json_error("Error #7"));
					
					if ($result -> num_rows == 1)
					{
						echo json_encode($ok);
					} else
					{
						$result = $db -> query("UPDATE $db_table SET $db_colServer='$serverid' WHERE $db_colUUID='$uuid'") or die (json_error("Error #8"));
						
						if ($db -> affected_rows == 1)
						{
							echo json_encode($ok);
						} else
							echo json_error("Bad login #2");
					}
				}
			} else
			{
				echo json_error("Bad session");
			}
		}
		
		exit();
	}
	
	/* ‚ыполнение кода версий 1.6- */
	
	$user			= sql_param($_GET['user']);
	$sessionid		= sql_param($_GET['sessionId']);
	$serverid		= sql_param($_GET['serverId']);
	$hwid			= sql_param($_GET['mac']);
	$hash			= $db -> real_escape_string($_GET['hash']);
	$client			= $db -> real_escape_string($_GET['client']);
	$action			= $db -> real_escape_string($_GET['action']);
	$get_mods		= $_GET['mods'];
	
	if ($user == null || $sessionid == null || $serverid == null || $client == null || $get_mods == null || $action == null) die ("BadParams #1");
	
	if ($use_checkhash)
	{
		if ($hash == null) die ("BadParams #2");
		if (!file_exists("../files/clients/$client/bin/minecraft.jar")) die ("Internal error");
		if (!(strtolower($hash) == strtolower(md5_file("../files/clients/$client/bin/minecraft.jar")))) die ("Bad hash");
	}
	
	if ($action == "setServerId")
	{
		if ($use_hwid_search) { if ($hwid != null) check_user_hwid($hwid, $user); else die ("BadParams #3"); }
		
		$updateSesId = sha1($sessionKey.$sessionid);
		$checkSession = $db -> query("SELECT $db_colUser FROM $db_table WHERE $db_colUser='$user' AND $db_colSesId='$sessionid'") or die ("Error #1");
		
		if ($checkSession -> num_rows == 1)
		{
			check_user_mods();
			
			$db -> query("UPDATE $db_table SET $db_colSesId='$updateSesId' WHERE $db_colUser='$user'") or die ("Error #2");
			
			$result = $db -> query("SELECT $db_colUser FROM $db_table WHERE $db_colUser='$user' AND $db_colServer='$serverid'") or die ("Error #3");
			
			if ($result -> num_rows == 1)
			{
				echo "OK";
			} else
			{
				$result = $db -> query("UPDATE $db_table SET $db_colServer='$serverid' WHERE $db_colUser='$user'") or die ("Error #3");
				
				if ($db -> affected_rows == 1)
				{
					echo "OK";
				} else
					echo "Bad login #1";
			}
		} else
		{
			$checkUpdateSession = $db -> query("SELECT $db_colUser FROM $db_table WHERE $db_colUser='$user' AND $db_colSesId='$updateSesId'") or die ("Error #4");
			
			if ($checkUpdateSession -> num_rows == 1)
			{
				check_user_mods();
				
				if ($use_one_entrance)
				{
					echo "You must be authorized again to play on the server.";
				} else
				{
					$result = $db -> query("SELECT $db_colUser FROM $db_table WHERE $db_colUser='$user' AND $db_colServer='$serverid'") or die ("Error #5");
					
					if ($result -> num_rows == 1)
					{
						echo "OK";
					} else
					{
						$result = $db -> query("UPDATE $db_table SET $db_colServer='$serverid' WHERE $db_colUser='$user'") or die ("Error #6");
						
						if ($db -> affected_rows == 1)
						{
							echo "OK";
						} else
							echo "Bad login #2";
					}
				}
			} else
			{
				echo "Bad session";
			}
		}
	}
	
	function sql_param($string)
	{
		global $db;
		(string) $string = $string;
		$string = PREG_REPLACE("/[^\w- ]|INSERT|DELETE|UPDATE|UNION|SET|SELECT|TRUNCATE|DROP|TABLE/i", "", $string);
		$string = TRIM($string);
		$db -> real_escape_string($string);
		return $string;
	}
	
	function check_user_mods()
	{
		global $use_modscheck, $mods, $get_mods, $sessionid;
		
		if (!$use_modscheck) return;
		
		$mods = array(); get_mods_list(null, $mods); if (count($mods) == 0) $mods[] = "nomods"; sort($mods);
		$mods_string = str_replace("/", "<:s:>", implode("<:f:>", $mods));
		$md5_mods_string = handle_md5(md5($mods_string.$sessionid));
		
		if (strCaseCmp($md5_mods_string, $get_mods) != 0) die ("Bad mods");
	}
	
	function check_user_hwid($hwid, $user)
	{
		if (strCaseCmp($hwid, "null") == 0) die ("Access Error #1");
		
		global $db, $db_table, $db_colUser, $db_colHWID, $db_colBlHWIDs;
		
		$sha1_hwid = sha1($hwid);
		
		$macvb = array( "00-50-56", "00-0C-29", "00-05-69", "00-03-FF", "00-1C-42", "00-0F-4B", "00-16-3E", "08-00-27" ); // список HWID возможных виртуальных машин
		
		if (array_search(substr($hwid, 0, 8), $macvb) === false)
		{
			$query	= $db -> query("SELECT $db_colHWID FROM $db_table WHERE $db_colUser='$user'") or die ($db -> error);
			$row	= $query -> fetch_assoc();
			$bdHWID = $row[$db_colHWID];
			
			$query	= $db -> query("SELECT $db_colBlHWIDs FROM $db_table WHERE $db_colBlHWIDs='$sha1_hwid'") or die ($db -> error);
			if ($query -> num_rows > 0) die ("You are blocked");
			
			if (strCaseCmp($sha1_hwid, $bdHWID) != 0)
			{
				if ($bdHWID == null)
				{
					die ("Use launcher to entry");
				} else
				{
					die ("Enter through the launcher using your computer");
				}
			}
		} else die ("Access Error #2");
	}
	
	/* *** FUNCTIONS *** */
	
	function json_error($text)
	{
		return json_encode(array('error' => $text, 'errorMessage' => $text));
	}
	
	// удалЯет 0 в начале строки, т.к. Java не включает 0 в начало строки при генерации хеш-суммы
	function handle_md5($string)
	{
		if (strcasecmp(substr($string, 0, 1), "0") == 0)
			$string = substr($string, 1);
			
		return $string;
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
	
?>
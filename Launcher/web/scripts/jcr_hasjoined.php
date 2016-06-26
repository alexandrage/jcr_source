<?php

	define ('IMPASS_CHECK', true);
	include ("../jcr_connect.php");
	include ("../jcr_uuid.php");
	
	$user		= sql_param($_GET['username']);
	$serverid	= sql_param($_GET['serverId']);
	
	$result		= $db -> query("SELECT $db_colUser FROM $db_table WHERE $db_colUser='$user' AND $db_colServer='$serverid'") or die ("Error");
	$row		= $result -> fetch_assoc();
	$realUser	= $row[$db_colUser];
	if ($user != $realUser) die(json_error("Bad login"));
	
	if ($result -> num_rows == 1)
	{
		$time = time(); $id = uuidConvert($realUser);
		$base64 = '{"timestamp": '.$time.'", "profileId": "'.$id.'", "profileName": "'.$realUser.'", "isPublic": true, "textures": {"SKIN": {"url": "'.get_skins_url($realUser).'"}}}';
		echo '{"id": "'.$id.'", "name": "'.$realUser.'", "properties": [{"name": "textures", "value": "'.base64_encode($base64).'", "signature": ""}]}';
	} else
	{
		die (json_error("Bad login"));
	}
	
	function json_error($text)
	{
		return json_encode(array('error' => $text, 'errorMessage' => $text));
	}
	
	// Возвращает URL адрес к скину игрока
	function get_skins_url($username)
	{
		global $lowerSkinsCase, $skins_url, $cloaks_url;
		
		$login = $lowerSkinsCase ? strtolower($username) : $username;
		if ($skins_url != null)
		{
			$url = $skins_url."/".$login.".png";
			$Headers = @get_headers($url);
			if (preg_match("|200|", $Headers[0]))
				return $url;
			else
				return $skins_url."/default.png";
		}
		
		if (!file_exists("../files/skins/".$login.".png")) $login = "default";
		
		return "http://".$_SERVER["HTTP_HOST"].dirname(dirname($_SERVER["REQUEST_URI"]))."/files/skins/".$login.".png";
	}
	
	function sql_param($string)
	{
		return mysql_real_escape_string($string);
	}
	
?>
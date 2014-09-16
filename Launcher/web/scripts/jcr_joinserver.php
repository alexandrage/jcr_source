<?php

	define ('IMPASS_CHECK', true);
	include ("../jcr_connect.php");
	include ("../jcr_settings.php");
	
	/* Исполнение кода версий 1.7+ */
	
	if (($_SERVER['REQUEST_METHOD'] == 'POST') && (stripos($_SERVER["CONTENT_TYPE"], "application/json") === 0))
	{
		$json = json_decode($HTTP_RAW_POST_DATA);
		
		$uuid		= sql_param($json -> selectedProfile);
		$sessionid	= sql_param($json -> accessToken);
		$serverid	= sql_param($json -> serverId);
		
		//if ($uuid == null || $sessionid == null || $serverid == null) die (json_error("Bad data"));
		
		$checkUUID = $db -> query("SELECT $db_colUser FROM $db_table WHERE $db_colUUID='$uuid'") or die (json_error("Error #1"));
		if ($checkUUID -> num_rows == 1) { $row = $checkUUID -> fetch_assoc(); $user = $row[$db_colUser]; } else die ("Bad UUID");
		
		$ok = array('id' => md5($sessionKey.$user), 'name' => $user);
		
		$updateSesId = $sessionid;
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
	} else {
	
	/* Выполнение кода версий 1.6- */
	
	$user			= sql_param($_GET['user']);
	$sessionid		= sql_param($_GET['sessionId']);
	$serverid		= sql_param($_GET['serverId']);

	{
		$checkSession = $db -> query("SELECT $db_colUser FROM $db_table WHERE $db_colUser='$user' AND $db_colSesId='$sessionid'") or die ("Error #1");
		
		if ($checkSession -> num_rows == 1)
		{			
			$db -> query("UPDATE $db_table SET $db_colServer='$serverid' WHERE $db_colUser='$user'") or die ("Error #2");
			echo "ok";
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
	
	/* *** FUNCTIONS *** */
	
	function json_error($text)
	{
		return json_encode(array('error' => $text, 'errorMessage' => $text));
	}
	
?>
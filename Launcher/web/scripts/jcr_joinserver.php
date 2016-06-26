<?php

	define ('IMPASS_CHECK', true);
	include ("../jcr_connect.php");
	include ("../jcr_settings.php");
	include ("../jcr_uuid.php");
	
	/* Исполнение кода версий 1.7+ */
	
	if (($_SERVER['REQUEST_METHOD'] == 'POST') && (stripos($_SERVER["CONTENT_TYPE"], "application/json") === 0))
	{
		$json = json_decode($HTTP_RAW_POST_DATA);
		
		$uuid		= sql_param($json -> selectedProfile);
		$sessionid	= sql_param($json -> accessToken);
		$serverid	= sql_param($json -> serverId);
        
        $checkSession = $db -> query("SELECT $db_colUUID FROM $db_table WHERE $db_colUUID='$uuid' AND $db_colSesId='$sessionid'") or die ("Error #1");
		
		if ($checkSession -> num_rows == 1)
		{			
			$db -> query("UPDATE $db_table SET $db_colServer='$serverid' WHERE $db_colUser='$user'") or die ("Error #2");
			echo json_encode($ok);
		} else {
          exit;
		}

	} else {
	
	/* Выполнение кода версий 1.6- */
	
	@$user			= sql_param($_GET['user']);
	@$sessionid		= sql_param($_GET['sessionId']);
	@$serverid		= sql_param($_GET['serverId']);

	$checkSession = $db -> query("SELECT $db_colUser FROM $db_table WHERE $db_colUser='$user' AND $db_colSesId='$sessionid'") or die ("Error #1");
		
	if ($checkSession -> num_rows == 1)
	{			
		$db -> query("UPDATE $db_table SET $db_colServer='$serverid' WHERE $db_colUser='$user'") or die ("Error #2");
		echo "ok";
	}

}
	
	function sql_param($string)
	{
		return mysql_real_escape_string($string);
	}
	
	/* *** FUNCTIONS *** */
	
	function json_error($text)
	{
		return json_encode(array('error' => $text, 'errorMessage' => $text));
	}
	
?>
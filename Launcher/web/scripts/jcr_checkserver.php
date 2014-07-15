<?php

	define ('IMPASS_CHECK', true);
	include ("../jcr_connect.php");
	
	$user		= sql_param($_GET['user']);
	$serverid	= sql_param($_GET['serverId']);
	
	$result		= $db -> query("SELECT $db_colUser FROM $db_table WHERE $db_colUser='$user' AND $db_colServer='$serverid'") or die ("Error");
	$row		= $result -> fetch_assoc();
	$realUser	= $row[$db_colUser];
	if ($user != $realUser) exit ("NO");
	
	if ($result -> num_rows == 1) echo "YES";
	else echo "NO";
	
	function sql_param($string)
	{
		global $db;
		(string) $string = $string;
		$string = PREG_REPLACE("/[^\w- ]|INSERT|DELETE|UPDATE|UNION|SET|SELECT|TRUNCATE|DROP|TABLE/i", "", $string);
		$string = TRIM($string);
		$db -> real_escape_string($string);
		return $string;
	}
	
?>
<?php
	if(!defined('IMPASS_CHECK')) die("You don't have permissions to run this");
	
	include("../jcr_settings.php");
	
 /** **************************** Connect to the server - DO NOT TOUCH! **************************** **/
 /** *********************************************************************************************** **/
 
	$db_server = new mysqli($db_server_host, $db_server_user, $db_server_pass, $db_server_database, $db_server_port);
	if ($db_server -> connect_error) die('Connection Error (' . $db_server -> connect_errno . ') ' . $db_server -> connect_error);
	
	$db_server -> query("SET names ".$server_encoding." COLLATE cp1251_general_ci");
?>
<?php
	if(!defined('IMPASS_CHECK')) die("You don't have permissions to run this");

/*
	Лист методов хеширования пароля для интеграции с различними плагинами, cms, форумами... :
	
	'hash_md5' 			- md5 хеширование
	'hash_authme'   	- интеграция с плагином AuthMe
	'hash_cauth' 		- интеграция с плагином Cauth
	'hash_xauth' 		- интеграция с плагином xAuth
	'hash_joomla' 		- интеграция с Joomla (v1.6- v1.7)
	'hash_ipb' 			- интеграция с IPB
	'hash_xenforo' 		- интеграция с XenForo (до v1.2)
	'hash_wordpress' 	- интеграция с WordPress
	'hash_vbulletin' 	- интеграция с vBulletin
	'hash_dle' 			- интеграция с DLE
	'hash_drupal'     	- интеграция с Drupal (v7)
	'hash_webmcr'     	- интеграция с webMCR (v2.35)
*/
	$crypt				= 'hash_md5'; 			// Интеграция (таблица выше)
	
	$db_host			= 'localhost';					// Ip-адрес базы данных
	$db_port			= '3306';						// Порт базы данных
	$db_user			= 'root';						// Пользователь базы данных
	$db_pass			= 'root';					    // Пароль базы данных
	
/*
	$db_database - имя базы данных, значение по умолчанию:
	AuthMe = 'authme'
	xAuth = отсутствует (указывается вручную)
	CAuth = 'cauth'
	Joomla, IPB, XenForo, WordPress, vBulletin, DLE, Drupal, webMCR - отсутствует (указывается вручную)
*/
	$db_database		= 'fix';					// База данных
	$encoding			= 'UTF8';					// Кодировка базы данных: cp1251 or UTF8 ...
	
/*
	$db_table - таблица базы данных, значение по умолчанию:
	AuthMe = 'authme'
	xAuth = 'accounts'
	CAuth = 'users'
	Joomla = 'префикс_users' - пример 'y3wbm_users', где "y3wbm_" - префикс. Примечание префикс может отсутствовать - пример 'users'
	IPB = 'members'
	XenForo = 'префикс_user' - пример 'xf_user', где "xf_" - префикс. Примечание префикс может отсутствовать - пример 'user'
	vBulletin = 'префикс_user' - пример 'bb_user', где "bb_" - префикс. Примечание префикс может отсутствовать - пример 'user'
	WordPress = 'префикс_users' - пример 'wp_users', где "wp_" - префикс. Примечание префикс может отсутствовать - пример 'users'
	DLE = 'префикс_users' - пример 'dle_users', где "dle_" - префикс. Примечание префикс может отсутствовать - пример 'users'
	Drupal = 'префикс_users' - пример 'drupal_users', где "drupal_" - префикс. Примечание префикс может отсутствовать - пример 'users'
	webMCR = 'accounts'
*/
	$db_table			= 'accounts';					// Таблица с пользователями
	
/*
	$db_colId - уникальный идентификатор, значение по умолчанию
	AuthMe = 'id'
	xAuth = 'id'
	CAuth = 'id'
	Joomla = 'id'
	IPB = 'member_id'
	XenForo = 'user_id'
	vBulletin = 'userid'
	WordPress = 'id'
	DLE = 'user_id'
	Drupal = 'uid'
	webMCR = 'id'
*/
	$db_colId			= 'id';							// Колонка с ID пользователей
	  
/*
	$db_colUser - колонка логина, значение по умолчанию:
	AuthMe = 'username'
	xAuth = 'playername'
	CAuth = 'login'
	Joomla = 'name'
	IPB = 'name'
	XenForo = 'username'
	vBulletin = 'username'
	WordPress = 'user_login'
	DLE = 'name'
	Drupal = 'name'
	webMCR = 'login'
*/
	$db_colUser		= 'login';						// Колонка с именами пользователей
	  
/*
	$db_colPass - колонка пароля, значение по умолчанию:
	AuthMe = 'password'
	xAuth = 'password'
	CAuth = 'password'
	Joomla = 'password'
	IPB = 'members_pass_hash'
	XenForo = 'data'
	vBulletin = 'password'
	WordPress = 'user_pass'
	DLE = 'password'
	Drupal = 'pass'
	webMCR = 'password'
*/
	$db_colPass		= 'password';						// Колонка с паролями пользователей
	
	$db_colSalt		= 'members_pass_salt';				// Настройка для IPB (members_pass_salt) и vBulletin (salt)
	$db_tableOther	= 'xf_user_authenticate';			// Доп. таблица для интеграции с XenForo
	
	
	$db_colSesId	= 'session';							// Не трогать, колонка с сессиями пользователей
	$db_colServer	= 'server';						// Не трогать, колонка с серверами пользователей
	$db_colHWID		= 'HWID';							// Не трогать, колонка с HWID пользователя
	$db_colBlHWIDs	= 'blockedHWIDs';					// Не трогать, колонка с заблокированными HWID
	$db_colAuthId	= 'authSesId';						// Не трогать, колонка с сессиями при авторизации
	$db_colUUID		= 'UUID';							// Не трогать, колонка с UUID пользователя
	$db_colUserStat	= 'userStatus';						// Не трогать, колонка со статусом пользователя
	
	
 /** **************************** Connect to the server - DO NOT TOUCH! **************************** **/
 /** *********************************************************************************************** **/
 
	$db = new mysqli($db_host, $db_user, $db_pass, $db_database, $db_port);
	if ($db -> connect_error) die('Connection Error (' . $db -> connect_errno . ') ' . $db -> connect_error);
	
	$db -> query("SET names ".$encoding." COLLATE cp1251_general_ci");
?>
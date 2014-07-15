
	/* Добавление значений sesId и serverId в таблицу */
	
	ALTER TABLE table_name
	ADD sesId varchar(255) DEFAULT '0',
	ADD serverId varchar(255) DEFAULT '0',
	ADD HWID varchar(255) DEFAULT '0',
	ADD blockedHWIDs varchar(255) DEFAULT '0',
	ADD authSesId varchar(255) DEFAULT '0',
	ADD UUID varchar(255) DEFAULT '0',
	ADD userStatus int(9) NOT NULL DEFAULT '0';
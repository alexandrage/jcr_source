package ru.er_log;

public class Style {
    
    /* *** Настройки стиля JCR Launcher *** */
    
    /* Основные настройки цветовой схемы */
    public static final String fields_static_color		= "#1d7e83"; 		// Основной цвет текста текстовых полей (в HTML формате)
        public static final String linux_fields_static_color	    = "#1d7e83"; 	// Основной цвет текста текстовых полей на ОС Linux (в HTML формате)
    public static final String fields_inactive_color		= "#a5b0b1"; 		// Цвет текста неактивных текстовых полей (в HTML формате)
        public static final String linux_fields_inactive_color	    = "#a5b0b1"; 	// Цвет текста неактивных текстовых полей на ОС Linux (в HTML формате)
    public static final String settings_color			= "#f5f5f5"; 		// Цвет текста окна настроек (в HTML формате)
    public static final String alert_color			= "#f5f5f5"; 		// Цвет оповещаний (статус сервера..) (в HTML формате)
    public static final String panel_title_color		= "#f5f5f5"; 		// Цвет заголовка на панели (заголовок окна chooser, окно обновления, окно оповещания...) (в HTML формате)
    public static final String panel_text_color			= "#f5f5f5"; 		// Цвет текста на панели (статус окна splash, окно обновления...) (в HTML формате)
    public static final String elements_text_color		= "#f5f5f5"; 		// Цвет текста кнопок и комбобокс'а (в HTML формате)
    public static final String program_combobox_sel_color	= "#a5a5a5"; 		// Панель выбора сервера: цвет названия выбранного сервера в основной программе (в HTML формате)
    
    /* Окно "Chooser" (первоначальный выбор сервера) */
    public static final int chooser_title_font_num		= 2; 			// Главная надпись окна Chooser: номер шрифта (в файле настроек темы)
    public static final int chooser_title_font_size		= 18; 			// Главная надпись окна Chooser: размер шрифта
    public static final String chooser_title_text_color		= "#f5f5f5"; 		// Главная надпись окна Chooser: цвет текста (в HTML формате)
    public static final int chooser_title_Y			= 36; 			// Главная надпись окна Chooser: положение относительно оси Y
    
    public static final int chooser_alert_font_num		= 1; 			// Надпись-оповещание окна Chooser: номер шрифта (в файле настроек темы)
    public static final int chooser_alert_font_size		= 10; 			// Надпись-оповещание окна Chooser: размер шрифта
    public static final int chooser_alert_text_opacity		= 20; 			// Надпись-оповещание окна Chooser: непрозрачность текста (в процентах)
    public static final String chooser_alert_text_color		= "#f5f5f5"; 		// Надпись-оповещание окна Chooser: цвет текста (в HTML формате)
    public static final int chooser_alert_Y			= 88; 			// Надпись-оповещание окна Chooser: положение относительно оси Y
    
    public static final int chooser_status_font_num		= 1; 			// Статус сервера в окне Chooser: номер шрифта (в файле настроек темы)
    public static final int chooser_status_font_size		= 13; 			// Статус сервера в окне Chooser: размер шрифта
    public static final String chooser_status_text_color	= "#7f8c8d"; 		// Статус сервера в окне Chooser: цвет текста (в HTML формате)
    public static final int chooser_status_Y			= 21; 			// Статус сервера в окне Chooser: положение относительно оси Y (отсчет с панели)
    
    public static final int chooser_checkbox_width		= 120; 			// Элемент CheckBox окна Chooser: ширина
    public static final int chooser_checkbox_height		= 16; 			// Элемент CheckBox окна Chooser: высота
    public static final String chooser_checkbox_color		= "#7f8c8d"; 		// Элемент CheckBox окна Chooser: цвет текста (в HTML формате)
    
    
    /* *** Настройки расположения элементов *** */
    
    
    /* *** Кнопки *** */
    
    /* Кнопка "Свернуть" */
    public static final int button_turn_X			= 300; 			// Положение относительно оси X (отсчет с панели)
    public static final int button_turn_Y			= 12; 			// Положение относительно оси Y (отсчет с панели)
    public static final int button_turn_width			= 13; 			// Ширина элемента
    public static final int button_turn_height			= 10; 			// Высота элемента
    
    public static final int button_turn_def_X			= 0; 			// Обычное состояние: положение относительно оси X на картинке
    public static final int button_turn_def_Y			= 0; 			// Обычное состояние: положение относительно оси Y на картинке
    public static final int button_turn_rol_X			= 0; 			// Наведенное состояние: положение относительно оси X на картинке
    public static final int button_turn_rol_Y			= 10; 			// Наведенное состояние: положение относительно оси Y на картинке
    public static final int button_turn_pre_X			= 0; 			// Нажатое состояние: положение относительно оси X на картинке
    public static final int button_turn_pre_Y			= 20; 			// Нажатое состояние: положение относительно оси Y на картинке
    
    /* Кнопка "Закрыть" */
    public static final int button_close_X			= 324; 			// Положение относительно оси X (отсчет с панели)
    public static final int button_close_Y			= 12; 			// Положение относительно оси Y (отсчет с панели)
    public static final int button_close_width			= 10; 			// Ширина элемента
    public static final int button_close_height			= 10; 			// Высота элемента
    
    public static final int button_close_def_X			= 13; 			// Обычное состояние: положение относительно оси X на картинке
    public static final int button_close_def_Y			= 0; 			// Обычное состояние: положение относительно оси Y на картинке
    public static final int button_close_rol_X			= 13; 			// Наведенное состояние: положение относительно оси X на картинке
    public static final int button_close_rol_Y			= 10; 			// Наведенное состояние: положение относительно оси Y на картинке
    public static final int button_close_pre_X			= 13; 			// Нажатое состояние: положение относительно оси X на картинке
    public static final int button_close_pre_Y			= 20; 			// Нажатое состояние: положение относительно оси Y на картинке
    
    /* Кнопка "Настройки" (окно авторизации) */
    public static final String button_settings_title		= "Настройки"; 		// Название элемента
    public static final int button_settings_X			= 57; 			// Положение относительно оси X (отсчет с панели)
    public static final int button_settings_Y			= 269; 			// Положение относительно оси Y (отсчет с панели)
    public static final int button_settings_width		= 112; 			// Ширина элемента
    public static final int button_settings_height		= 36; 			// Высота элемента
    
    /* Кнопка "Войти" (окно авторизации) */
    public static final String button_auth_title		= "Войти"; 		// Название элемента
    public static final int button_auth_X			= 177; 			// Положение относительно оси X (отсчет с панели)
    public static final int button_auth_Y			= 269; 			// Положение относительно оси Y (отсчет с панели)
    public static final int button_auth_width			= 112; 			// Ширина элемента
    public static final int button_auth_height			= 36; 			// Высота элемента
    
    /* Кнопка "Отмена" (окно настроек) */
    public static final String button_cancelSettings_title	= "Отмена"; 		// Название элемента
    public static final int button_cancelSettings_X		= 57; 			// Положение относительно оси X (отсчет с панели)
    public static final int button_cancelSettings_Y		= 326; 			// Положение относительно оси Y (отсчет с панели)
    public static final int button_cancelSettings_width		= 112; 			// Ширина элемента
    public static final int button_cancelSettings_height	= 36; 			// Высота элемента
    
    /* Кнопка "Принять" (окно настроек) */
    public static final String button_takeSettings_title	= "Принять"; 		// Название элемента
    public static final int button_takeSettings_X		= 177; 			// Положение относительно оси X (отсчет с панели)
    public static final int button_takeSettings_Y		= 326; 			// Положение относительно оси Y (отсчет с панели)
    public static final int button_takeSettings_width		= 112; 			// Ширина элемента
    public static final int button_takeSettings_height		= 36; 			// Высота элемента
    
    /* Кнопка "Выход" (окна: самообновления, оповещания, личного кабинета) */
    public static final String button_skip_title		= "Выход"; 		// Название элемента
    public static final int button_skip_X			= 57; 			// Положение относительно оси X (отсчет с панели)
    public static final int button_skip_Y			= 326; 			// Положение относительно оси Y (отсчет с панели)
    public static final int button_skip_width			= 112; 			// Ширина элемента
    public static final int button_skip_height			= 36; 			// Высота элемента
    
    /* Кнопка "Обновить" (окно самообновления) */
    public static final String button_takeSelfRrenewal_title	= "Обновить"; 		// Название элемента
    public static final int button_takeSelfRrenewal_X		= 177; 			// Положение относительно оси X (отсчет с панели)
    public static final int button_takeSelfRrenewal_Y		= 326; 			// Положение относительно оси Y (отсчет с панели)
    public static final int button_takeSelfRrenewal_width	= 112; 			// Ширина элемента
    public static final int button_takeSelfRrenewal_height	= 36; 			// Высота элемента
    
    /* Кнопка "Продолжить" (окно оповещания) */
    public static final String button_takeAlert_title		= "Продолжить"; 	// Название элемента
    public static final int button_takeAlert_X			= 177; 			// Положение относительно оси X (отсчет с панели)
    public static final int button_takeAlert_Y			= 326; 			// Положение относительно оси Y (отсчет с панели)
    public static final int button_takeAlert_width		= 112; 			// Ширина элемента
    public static final int button_takeAlert_height		= 36; 			// Высота элемента
    
    /* Кнопка "В игру" (окно личного кабинета) */
    public static final String button_toGame_title		= "В игру"; 		// Название элемента
    public static final int button_toGame_X			= 177; 			// Положение относительно оси X (отсчет с панели)
    public static final int button_toGame_Y			= 326; 			// Положение относительно оси Y (отсчет с панели)
    public static final int button_toGame_width			= 112; 			// Ширина элемента
    public static final int button_toGame_height		= 36; 			// Высота элемента
    
    
    /* *** Поля ввода *** */
    
    /* Поле "Логин" (окно авторизации) */
    public static final String field_login_title		= "Логин"; 		// Значение по умолчанию для данного элемента
    public static final int field_login_X			= 57; 			// Положение относительно оси X (отсчет с панели)
    public static final int field_login_Y			= 179; 			// Положение относительно оси Y (отсчет с панели)
    public static final int field_login_width			= 232; 			// Ширина элемента
    public static final int field_login_height			= 36; 			// Высота элемента
    
    /* Поле "Пароль" (окно авторизации) */
    public static final String field_password_title		= "Пароль"; 		// Значение по умолчанию для данного элемента
    public static final int field_password_X			= 57; 			// Положение относительно оси X (отсчет с панели)
    public static final int field_password_Y			= 223; 			// Положение относительно оси Y (отсчет с панели)
    public static final int field_password_width		= 232; 			// Ширина элемента
    public static final int field_password_height		= 36; 			// Высота элемента
    
    /* Поле "Выделенная память" (окно настроек) */
    public static final int field_memory_X			= 72; 			// Положение относительно оси X (отсчет с панели)
    public static final int field_memory_Y			= 292; 			// Положение относительно оси Y (отсчет с панели)
    public static final int field_memory_width			= 48; 			// Ширина элемента
    public static final int field_memory_height			= 18; 			// Высота элемента
    
    
    /* *** Выпадающий список (ComboBox) *** */
    
    /* Список серверов (окно первоначального выбора сервера ("Chooser")) */
    public static final int combobox_chooser_servers_X		= 24; 			// Положение относительно оси X (отсчет с панели)
    public static final int combobox_chooser_servers_Y		= 52; 			// Положение относительно оси Y (отсчет с панели)
    
    /* Список серверов (окно авторизации) */
    public static final int combobox_auth_servers_X		= 57; 			// Положение относительно оси X (отсчет с панели)
    public static final int combobox_auth_servers_Y		= 315; 			// Положение относительно оси Y (отсчет с панели)
    
    /* Настройка целого элемента */
    public static final int combobox_img_def_X			= 0; 			// Обычное состояние: положение относительно оси X на картинке
    public static final int combobox_img_def_Y			= 0; 			// Обычное состояние: положение относительно оси Y на картинке
    public static final int combobox_img_def_width		= 232; 			// Обычное состояние: ширина элемента
    public static final int combobox_img_def_height		= 19; 			// Обычное состояние: высота элемента
    
    public static final int combobox_img_rol_X			= 0; 			// Наведенное состояние: положение относительно оси X на картинке
    public static final int combobox_img_rol_Y			= 25; 			// Наведенное состояние: положение относительно оси Y на картинке
    public static final int combobox_img_rol_width		= 232; 			// Наведенное состояние: ширина элемента
    public static final int combobox_img_rol_height		= 19; 			// Наведенное состояние: высота элемента
    
    public static final int combobox_img_pre_X			= 0; 			// Нажатое состояние: положение относительно оси X на картинке
    public static final int combobox_img_pre_Y			= 50; 			// Нажатое состояние: положение относительно оси Y на картинке
    public static final int combobox_img_pre_width		= 232; 			// Нажатое состояние: ширина элемента
    public static final int combobox_img_pre_height		= 19; 			// Нажатое состояние: высота элемента
    
    public static final int combobox_img_loc_X			= 0; 			// Заблокированное состояние: положение относительно оси X на картинке
    public static final int combobox_img_loc_Y			= 75; 			// Заблокированное состояние: положение относительно оси Y на картинке
    public static final int combobox_img_loc_width		= 232; 			// Заблокированное состояние: ширина элемента
    public static final int combobox_img_loc_height		= 19; 			// Заблокированное состояние: высота элемента
    
    public static final int combobox_img_pane_X			= 0; 			// Панель со списком: положение относительно оси X на картинке
    public static final int combobox_img_pane_Y			= 100; 			// Панель со списком: положение относительно оси Y на картинке
    public static final int combobox_img_pane_width		= 232; 			// Панель со списком: ширина элемента
    public static final int combobox_img_pane_height		= 70; 			// Панель со списком: высота элемента
    
    
    /* *** Флажки (CheckBox) *** */
    
    /* Флажок "Запомнить мои данные" (окно настроек) */
    public static final int checkbox_remember_X			= 68; 			// Положение относительно оси X (отсчет с панели)
    public static final int checkbox_remember_Y			= 188; 			// Положение относительно оси Y (отсчет с панели)
    public static final int checkbox_remember_width		= 210; 			// Ширина элемента
    public static final int checkbox_remember_height		= 16; 			// Высота элемента
    
    /* Флажок "Перекачать клиент" (окно настроек) */
    public static final int checkbox_redownload_X		= 68; 			// Положение относительно оси X (отсчет с панели)
    public static final int checkbox_redownload_Y		= 214; 			// Положение относительно оси Y (отсчет с панели)
    public static final int checkbox_redownload_width		= 210; 			// Ширина элемента
    public static final int checkbox_redownload_height		= 16; 			// Высота элемента
    
    /* Флажок "Полноэкранный режим" (окно настроек) */
    public static final int checkbox_fullScreen_X		= 68; 			// Положение относительно оси X (отсчет с панели)
    public static final int checkbox_fullScreen_Y		= 240; 			// Положение относительно оси Y (отсчет с панели)
    public static final int checkbox_fullScreen_width		= 210; 			// Ширина элемента
    public static final int checkbox_fullScreen_height		= 16; 			// Высота элемента
    
    /* Флажок "Режим оффлайн" (окно настроек) */
    public static final int checkbox_offline_X			= 68; 			// Положение относительно оси X (отсчет с панели)
    public static final int checkbox_offline_Y			= 266; 			// Положение относительно оси Y (отсчет с панели)
    public static final int checkbox_offline_width		= 210; 			// Ширина элемента
    public static final int checkbox_offline_height		= 16; 			// Высота элемента
}

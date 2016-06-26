package ru.er_log;

import ru.er_log.components.UI_Theme;
import ru.er_log.themes.*;


public class Settings {
    
    /**
     * * * На заметку: ...
     * 
     * * ГЛАВНЫЙ КЛАСС: ru.er_log.Starter
     * * Инструкция: www.er-log.ru/?p=128
     * 
     * * Обновление программы.
     * При том, как вы обновите лаунчер и загрузите его на сервер,
     * старый лаунчер обязательно предолжит обновление игрокам.
     * Начиная с предыдущих версий, обновление всегда будет принудительным
     * в целях укрепления защиты. Однако не забудтье указать версию
     * нового лаунчера в файле "jcr_settings.php" на веб-сервере.
     * 
     * * Online тема.
     * Online тема можеть быть загружена с сервера только в том случае,
     * если версия текущей программы совпадает с версией программы на сервере.
     * 
     * * Возможности и особенности программы:
     * - Авторизация
     * - Поддержка Minecraft 1.6+
     * - Умный режим "Офлайн"
     * - Бан по Hardware ID
     * - Защита сессии (скрывает сессию от WireShark и других подобных программ)
     * - Мощный античит (проверка модов и jar'ников, их хеш-сумм)
     * - Загрузка и проверка папки "coremods", которую создает Forge
     * - Поддержка Forge
     * - Мониторинг серверов (работает и со Spigot серверами)
     * - Отладчик (отображение всех действий программы в консоли)
     * - Мультиклиентность
     * - Мультисерверность
     * - Загрузка новостей
     * - Выделение памяти
     * - Самообновление
     * - Автоматический патчинг клиента (во избежании использования стандартной папки ".minecraft")
     * - Загрузка online темы (хорошо продумана, вы можете использовать её без опаски)
     * - Личный кабинет (skins and cloaks, revision...)
     * - Загрузка и распаковка архива с файлами в директорию с клиентом (extra.zip)
     * - Окно заставки перед запуском (оптимально для online темы)
     * - Анимация переходов между окнами (затемнение и осветление)
     * - Надежное шифрование паролей
     * - Online настройки (веб часть - очень удобно, без необходимости обновлять программу)
     * - Шрифты, читаемые из файла (по умолчанию поддержка 3-х шрифтов)
     * - При запуске программы в формате ".exe" функция самообновления будет скачивать и запускать программу именно в этом формате, а не в формате ".jar" (аналогично с форматом ".jar")
     * - Аккуратное, небольшое окно, которое позволяет вписать в себя все необходимое
     * - Возможность полного легкого изменения дизайна (изображения)
     * - Отображение скинов и плащей любых размеров с соблюдением пропорций (объемный шлем, как и в игре)
     * - И множество других возможностей и приятных вещей, которые станут известны вам после покупки на собственном опыте...
     * 
     */
    
    /* Основные настройки программы */
    public static final String[] debugKey			= { "debugKey" }; // ВНИМАНИЕ! Ключ для отладки программы (пропускать запрос об обновлении программы); ОСТАВИТЬ СТРОКУ ПУСТОЙ при окончательной сборке программы! При настройке программы, скопируйте схожий параметр из файла настроек веб-части
    
    public static final String[] title				= { "JCR Launcher" }; // Название окна программы
    public static final String[] title_in_game			= { "Minecraft" }; // Название окна программы после запуска игры
    public static final String[] version			= { "v6.0.3_full_SRC" }; // Версия программы (не используйте символ пробела)
    
    public static final String[] par_directory			= { "AppData" }; // Родительская папка для Minecraft (только для Windows) [ProgramFiles, AppData]
    public static final String[] game_directory			= { "jcr" }; // Папка с Minecraft (.minecraft)
    
    /* Настройки подключения */
    public static final String[] domain				= { "localhost" }; // Домен сайта в формате: example.com
    public static final String[] site_dir			= { "web" }; // Директория к папке программы от корня сайта
    public static final String[] protect_key			= {"protectKey"}; // Ключ защиты доступа к веб-части (равен ключу в веб-части). Пример: 17@Ee'45x_Fq;04
    public static final String[] aes_key			= { "123456789abcdefj" }; // Ключ для шифрования данных (равен ключу в веб-части, не более 16 символов)
    
     // Надпись под логотипом, Размер шрифта, Номер шрифта (в файле настроек темы), Цвет (HTML формат)
    public static final String[] string_under_logo =
    {
        "Java CRaFT4ik Launcher " + version[0], "14", "#3", "#f5f5f5"
    };
    
    public static final String[] auth_but_auth_text		= { "Войти" }; 		// Текст кнопки авторизации для входа в онлайн-игру
    public static final String[] auth_but_offline_text		= { "В игру" }; 	// Текст кнопки авторизации для входа в оффлайн-игру
    
    public static final String[] off_user			= { "Player" }; 	// Имя пользователя одиночной игры
    public static final String[] off_sess			= { "123456" };		// Сессия пользователя для одиночной игры
    
    /* Глобальные параметры программы */
    public static final boolean[] use_splash_screen		= { true }; 		// Использовать заставку пред запуском (оптимально для online темы)
    public static final boolean[] use_animation			= { true }; 		// Использовать анимацию переходов между окнами в программе
        public static final int[] animation_speed		    = { 15 }; 		    // Скорость анимации переходов между окнами (в разумных пределах от 10 до 30)
    
    public static final boolean[] use_logo_as_url		= { true }; 		// Использовать логотип как ссылку на сайт
        public static final String[] logo_url			    = { "" }; 		    // Ссылка (в формате: example.com), которая откроется при клике на логотип. Оставтье пустым, если хотите, чтобы открывался ваш сайт указанный выше
    public static final int[] logo_indent			= { 67 }; 		// Положение логотипа относительно оси Y
    
    public static final UI_Theme[] current_theme		= { new JCRTheme() }; 	// Название темы программы (имя файла темы)
    
    /* Основные параметры программы */
    public static final boolean[] use_personal			= { true }; 		// Использовать личный кабинет пользователя
        public static final int[] max_attempts_num		    = { 4 }; 		    // Максимальное число попыток загрузки изображений из личного кабинета (для скинов и плащей)
    public static final boolean[] show_server_is_not_selected	= { true }; 		// Отображать "Сервер не выбран" при первом использовании программы
    public static final boolean[] allow_to_change_server	= { true }; 		// Позволить изменять выбранный сервер, после совершения его выбора в окне Chooser
    public static final boolean[] show_client_version		= { true }; 		// Отображать версию сервера в списке серверов
    public static final boolean[] use_monitoring		= { true }; 		// Использовать мониторинг при выборе сервера в списке серверов
        public static final boolean[] use_update_mon		    = { false }; 	    // Автоматически загружать статус текущего сервера при запуске программы
    public static final boolean[] use_multi_client		= { true }; 		// Мультиклиентность (своя подпапка для каждого сервера)
        public static final boolean[] use_auto_entrance		    = { true }; 	    // Использовать автоматический вход на выбранный сервер после авторизации
    public static final boolean[] use_pass_remember		= { true }; 		// Использовать функцию запоминания пароля (помимо логина)
    public static final boolean[] use_loading_news		= { false }; 		// Загружать новости и отображать их в личном кабинете игрока
        // Скорость анимации выезда новостей
        public static final int[] news_drawing_time		= { 50 }; 		// Таймер прорисовки. Каждые [n] милисекунд прорисовывать окно (чем больше значение смещения (ниже), тем меньше должно быть значение таймера прорисовки)
        public static final int[] news_offset_panel		= { 5 }; 		// Смещение. Каждые [n] милисекунд (выше) смещать окно на [x] пикселей (Внимание! Должно быть кратно ширине новостной панели (300))
    
    public static final boolean[] path_client			= { true }; 		// Использовать автоматическую замену директории в клиенте (во избежании использования стандартной папки .minecraft) (до версии 1.6)
    public static final String[] old_mine_class		= { "net.minecraft.client.Minecraft" };		// Главный класс Minecraft (до версии 1.6, используется при патчинге клиента)
    public static final String[] mine_class		= { "net.minecraft.client.main.Main" };		// Главный класс Minecraft (версия 1.6+)
    public static final String[] lwrap_mine_class	= { "net.minecraft.launchwrapper.Launch" };	// Главный класс загрузчика (версия 1.6+)
    
    /* Настройки отладки */
    public static final boolean[] use_debugging			= { true }; 		// Режим отладки (вывод сообщений в консоль), рекомендуется отключить после окончательной настройки программы (как и отладку после запуска игры)
    public static final boolean[] use_game_debug_mode		= { true }; 		// Производить отладку после запуска игры (при версии 1.6+ лаунчер будет закрыт при значении параметра "false")
    public static final boolean[] draw_borders			= { false }; 		// Отрисовка границы элементов программы
    
    /* Настройки окна заставки (для опытных) */
    public static final int[] chooser_width			= { 280 }; 		// Ширина окна со списком серверов
    public static final int[] chooser_height			= { 99 }; 		// Высота окна со списком серверов
    
    public static final int[] splash_width			= { 280 }; 		// Ширина окна заставки
    public static final int[] splash_height			= { 200 }; 		// Высота окна заставки
    public static final int[] splash_X_align			= { 30 }; 		// Положение статуса на заставке относительно оси X
    public static final int[] splash_Y_align			= { 172 }; 		// Положение статуса на заставке относительно оси Y
    
    /* Блокированные настройки */
    public static final int[] frame_width			= { 346 }; 		// Ширина фрейма [346]
    public static final int[] frame_height			= { 450 }; 		// Высота фрейма [450]
    public static final int[] linux_frame_w                     = { 2 }; 		// Ширина, занимаемая системным фреймом на ОС Linux
    public static final int[] linux_frame_h                     = { 29 }; 		// Высота, занимаемая системным фреймом на ОС Linux
    
    public static final String[] libraryForPath = { "1.2.5::aj", "1.3.x::am", "1.4.x::an", "1.5.x::an" }; // Библиотека зашифрованных полей Minecraft.class для подмены директории. Поле соответсвтует версии Minecraft.
}

<?php
	
	class JGuard
	{
		// Перемешивает строку "n" раз; возвращает перемешанную строку и ключи к ней (строка<:k:>ключи)
		public static function stir_string($string)
		{
			$processing_num = 15; // повторить цикл 'n' раз
			$max_str_len = strlen($string); // длина шифруемой строки
			
			for ($i = 0; $i < $processing_num; $i++)
			{
				$rand_1	= rand(0, ($max_str_len - 1)); // позиция ножниц для обрезки
				$rand_2	= rand(1, $max_str_len - $rand_1); // длина будущего "обрезка"
				$cut	= substr($string, $rand_1, $rand_2); // обрезанная строка
				$string	= substr_replace($string, "", $rand_1, $rand_2); // удалям из исходной строки "обрезок"
				
				$rand	= rand(0, strlen($string)); // новая позиция "обрезка" в строке
				$string	= substr($string, 0, $rand).$cut.substr($string, $rand, $max_str_len); // возвращение "обрезка" в строку на новую позицию
				
			    @$coodrs = $rand.":".$rand_2.":".$rand_1 . $coodrs;
				if ($i + 1 < $processing_num) $coodrs = "::" . $coodrs;
			}
			
			return $string."<:k:>".$coodrs;
		}
	}
	
	/* Выражаю благодарность @alexandrage за этот общеоткрытый код ниже. Его значимость в стоимость программы не включена. */
	
	class Guard
	{
		public static function encrypt($input, $key)
		{
			$size = mcrypt_get_block_size(MCRYPT_RIJNDAEL_128, MCRYPT_MODE_ECB); 
			$input = Guard::pkcs5_pad($input, $size); 
			$td = mcrypt_module_open(MCRYPT_RIJNDAEL_128, '', MCRYPT_MODE_ECB, ''); 
			$iv = mcrypt_create_iv (mcrypt_enc_get_iv_size($td), MCRYPT_RAND); 
			mcrypt_generic_init($td, $key, $iv); 
			$data = mcrypt_generic($td, $input); 
			mcrypt_generic_deinit($td); 
			mcrypt_module_close($td); 
			$data = base64_encode($data); 
			return $data; 
		} 
		
		private static function pkcs5_pad($text, $blocksize)
		{ 
			$pad = $blocksize - (strlen($text) % $blocksize); 
			return $text.str_repeat(chr($pad), $pad); 
		} 
		
		public static function decrypt($sStr, $sKey)
		{
			$decrypted = mcrypt_decrypt(MCRYPT_RIJNDAEL_128, $sKey, base64_decode($sStr), MCRYPT_MODE_ECB);
			$dec_s = strlen($decrypted); 
			$padding = ord($decrypted[$dec_s - 1]); 
			$decrypted = substr($decrypted, 0, -$padding);
			return $decrypted;
		}
	}
?>
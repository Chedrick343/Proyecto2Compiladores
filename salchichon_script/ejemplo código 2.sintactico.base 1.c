
 let string _s1_$
 init void principal є э  ¿ | semantico tipo int, sintactico?
	| _arr_[67] = -0.01$  | semantico
	let bool _b1_$ |semantico
	let string _s1_$ |semantico
	input є _s1_ э $ |semantico
	output є _b1_ э $ |semantico
	output є true э $ |semantico
	output є -6.7 э $
	return$  |sintactico
	return 1+_f_ є  э $
 ? 

float _mi_  є int _dif_, char _otra_ э  ¿  |sintactico  э 
	let int _otra_$
	let string _str_="Hola $~&|?$& э  mundo"$
	for  _i_=20 step -2 downto 0 do  | puede dar sintactico
	 ¿ 
		loop
		  	_var_2 = _var_2 - 1$ |semantico
			_var_ = _var_ - 1$ |semantico
		exit when є _var2_>12.2 @ Σ є12> є 34+35 ээ  э $ |semantico
		end loop$


		decide   of
		є _var_==0 э  ->
		 ¿ 
			output є "entra al if" э $
		 ?
		end decide$
		decide   of 
		є true э ->  
		 ¿ 
			output є _var_ э $
		 ? 
		else ->
		 ¿ 
			decide   of  |semantico _otra_
				є _otra_ == 1 э-> ¿ 
					output є "!|" э $ ?
				є _otra_ == 2 э-> ¿
						_otra_ = 10$
						break$ ?
				else -> ¿ | sintactico
					_otra_ = -10.5 * -1.9$ ?
			end  decide  $

			decide   of  |semantico _otra2_
				є _otra2_ == 10 э-> ¿ 
					output є "un case" э $ ?
			end  decide  $ 
		 ?
		 end  decide  $
	 ? 
	return 1$ |semantico
  ? 

 bool _miOtraFun_  є  э   ¿ 
	return -5.6$ |semantico
  ? 
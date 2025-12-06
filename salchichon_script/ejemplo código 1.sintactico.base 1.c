 
let string _s1_$ | sintactico
let bool _b1_$
let int w = 23 $
let float f = 2.4 $

 float func1 є char x22,char x23 э  ¿ |semantico borrar parametro
 	
 	let int arregloChar¿20? = ¿1,2,3,4?$
	let int suma = 7 + arregloChar¿2?$
	let int numero$
	let char letra$
	numero = 10$
	letra = 'a'$
	output є numero э $
	output є letra э $
	let int numS = numero - 1 * numero + numero $
	output є numS э $
	let char arreglo¿55?$
	let float flotante =-0.01$ |error sintactico
	let char x25='a'$ ¡semantico x22!
	let char _miChar_='!'$  |error sintactico
	let char _miChar2_='!'$ |sintactico-semantico
	let int _x30_=-1$
	let bool _x40_=false$
	let char _x50_¿1000? = ¿4,5?$
	let string _x50_="Hola a todos los que est[a] haciendo un compilador nuevo¿n"$
	decide of
	є _x30_<=45 @ flotante>5.6 э ->  ¿  |semantico x22, var
		let int y$
		|x22=10$
		let char ch33='a'$
	 ? 
	є flotante>=2.5 э ->  ¿
		let int y$
		|x22=10$
		let char ch33='a'$
		|let char ch33='a'$
	? 
	else -> ¿ 
		let int y$ |no error duplicado en if-else
		let string str2="sdff"$
	 ?
	end decide$
	let int _i_$
	for   _i_=0 step 1 to 10 do  ¿ output є _i_ э $ ?  |semantico i y j puede dar error sintactico
	output є "Hola mundo" э $
	input є _i_ э $
	return -5.6$|cambio en retorno genera semantico
 ?  

 bool _func2_  є bool _b1_, int _i1_ э   ¿ 
	let int entero $ |sintactico
	return false$ |generar error con -5.6 y con i1
  ?  

string _func3_  є  э   ¿  |semantico string
	let string _b1_$
	return _b1_$ 
  ? |No permitir reetorno string ...fixed... Se valido semanticamente 

init void principal є  э  ¿ 
¡
Comentario 1
!
 	let int arr¿67?$|semantico
	arr¿12?$
	let char arreglo2¿20?$
	arreglo2¿12?$
	let char miChar='!'$
	let char miChar2='!'$ |sintactico
	let string str1="Mi string 1"$
	let float fl1 = 4.0$
	let float fl2=56.6$ |semantico fl1
	let int in1=w- -14%w + 7 // 15$ |semantico fl1, in1, semantico division
	let float fl3=3.7 / fl1+ є 45.6 * 76.4 э $ |semantico literal 76
	
|comentario 2
	|arr¿22? = 10 - 67 $ |semantico func1, retorno func1
	fl1 = 4.5*3.4^-0.005$ |semantico miChar
	_func2_ є _func2_ є true, 1 э ,7 э $ |semantico miFunc, hola
	let bool bl0 = 6.7 != 8.9$ |ok
	bl0 = true != false$ |ok
	let bool bl1 = in1 >= w ~ false @ є func1 є 'A', 'd'э  > 1.6 э $ |semantico in1 >= fl1, func2
	output є true э $ |semantico
	loop
		let int _var_2 = w - 1$ |semantico
		let int _var_ = w - 1$ |semantico
		let int _i_$
		for   _i_=0 step 1 to 10 do  ¿ output є _i_ э $ ?  |semantico i y j puede dar error sintactico
		exit when є f>12.2 @ Σ є12 >  є 34+35 ээ  э $ |semantico
	end loop$
	return$ |semantico
 ? 
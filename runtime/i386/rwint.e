int readInt2(){
	int[11] buf;
	int tmp;
	int a;
	int b;
	int begin;
	int count;
	int result;
	int resultTmp;
	resultTmp := 0;
	count := 0;
	begin := 0;
	a := 1;
	b := 1;
	while(a = b){
		tmp := readChar();
		if(begin = 0 && (tmp = 45 || (tmp >= 48 && tmp <= 57))){
			begin :=1;
			buf[count] := tmp;
			count := count + 1;
		}else{
			if(begin = 1 && (tmp >=48 && tmp <= 57)){
				buf[count] := tmp;
				count := count+1;
			}else{
				if(begin = 1){
					b := 0;
				}
			}
		}
	}
	if(buf[0] = 45){
		result :=0-1;
		a := 1;
	}else{
		result := 1;
		a := 0;
	}
	while(a < count){
		int dec;
		int tmp2;
		int asci2int;
		tmp2 := 1;
		dec := count-a-1;
		while(dec > 0){
			tmp2 := tmp2*10;
			dec := dec-1;
		}
		asci2int := buf[a];
		if(asci2int = 48){
			b := 0;
		}
		if(asci2int = 49){
			b := 1;
		}
		if(asci2int = 50){
			b := 2;
		}
		if(asci2int = 51){
			b := 3;
		}
		if(asci2int = 52){
			b := 4;
		}
		if(asci2int = 53){
			b := 5;
		}
		if(asci2int = 54){
			b := 6;
		}
		if(asci2int = 55){
			b := 7;
		}
		if(asci2int = 56){
			b := 8;
		}
		if(asci2int = 57){
			b := 9;
		}		
		resultTmp := resultTmp+(tmp2*b);
		a := a+1;
	}
	result := resultTmp*result;
	return result;
}

int writeInt2(int x){

}

int main(){
	int a;
	a := readInt2();
	return a;
}


extern int readChar();
extern int writeChar(int);

int readInt();
int writeInt();

int readInt(){
	char tmp[1];
	char buf[11];
	int flag1 = 0;
	int flag2 = 0;
	int flag3 = 0;
	int count = 0;
	while(1) {
		tmp[0] = readChar();
		if(tmp[0] == 10) {
			int result = 0;
			int sign;
			int a;
			if(buf[0] == 45){
				sign = -1;
				a = 1;
			}else{
				sign = 1;
				a = 0;
			}
			while(a<count){
				int dec = count-a-1;
				int ten = 1;
				while(dec>0){
					ten = ten*10;
					dec--;
				}
				result = result+((buf[a]-48)*ten);
				a++;
			}
			return result*sign;	
		}
		if((tmp[0] == 45 && !flag1)) {
		buf[0] = tmp[0];
		count++;
		flag1 = 1;
		flag3 = 1;
		}else {
			if(((tmp[0] >= 48 &&tmp[0] <= 57)) && !flag2) {
				buf[count] = tmp[0];
				count++;
				flag3 = 1;
			}else {
				if(flag3 == 1) {
					flag2 = 1;
				}
			}	
		}
	}
}


int writeInt(int x) {
	int buf[11];
	int count = 10;
	int neg = 0;
	int tmp;
	if(x<0){
		neg = 1;
		x*=-1;
	}
	tmp = x;
	while(tmp>0){
		buf[count] = (tmp % 10)+48;
		tmp = tmp/10;
		count--;		 		
	}
	if(neg){
		buf[count] = 45;
		count--;
	}
	int res = 10-count;
	for(int i = count+1;i<11;i++){
		writeChar(buf[i]);
	}
	return res;	
}

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>
#include <string.h>


int readInt();
int writeInt();

int readInt(){
	char tmp[1];
	char buf[11];
	int flag1 = 0;
	int flag2 = 0;
	char *ptr;
	ptr = buf;
	while(1) {
		if(tmp[0] = fgetc(stdin)){
			if(tmp[0] == '\n') {
				if(ferror(stdin)){
		          		perror("fgetc");
		          		exit(EXIT_FAILURE);
		      		} else{
					return atoi(buf);
				}
			}
			if((tmp[0] == '-' || tmp[0] == '0'|| tmp[0] == '1' || tmp[0] == '2' || tmp[0] == '3' || tmp[0] == '4' || tmp[0] == '5' || tmp[0] == '6' || tmp[0] == '7' || tmp[0] == '8' || tmp[0] == '9')&& !flag1) {
			*ptr = tmp[0];
			 ptr++;
			 flag2 = 1;
			}else {
				if(flag2 == 1) {
					flag1 = 1;
				}
			}
		}
	}
}


int writeInt(int x) {
	fprintf(stdout, "%i\n", x);
	char digits[11];
	return sprintf(digits, "%i", x);	
}

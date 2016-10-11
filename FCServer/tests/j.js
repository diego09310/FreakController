// Forma 1: Math.floor(key/Math.pow(2, i))%2
// Forma 2: (key/(Math.pow(2, i))%2)>=1?1:0

var test = [5, 28394, 48385958, 24323, 32423423, 43423, 2147483647, 0, 43242342]
var bin1 = []
var bin2 = []
var bin3 = []
var bin4 = []
var bin5 = []
var bin6 = []
var bin7 = []
var bin8 = []

console.time('test1');
for (k = 0; k < test.length; k++) {
	for (i = 0; i < 31; i++) {
		bin1[i] = Math.floor(test[k]/Math.pow(2, i))%2;
	}
	//console.log(test[k] + ": " + bin1);
}
console.timeEnd('test1');

console.time('test2');
for (k = 0; k < test.length; k++) {
    for (i = 0; i < 31; i++) {
        bin2[i] =(test[k]/(Math.pow(2, i))%2)>=1?1:0; 
    }
    //console.log(test[k] + ": " + bin2);
}
console.timeEnd('test2');

console.time('test3');
for (k = 0; k < test.length; k++) {
	bin3 = test[k].toString(2);
    //console.log(test[k] + ": " + bin3);
}   
console.timeEnd('test3');

console.time('test4');
for (k = 0; k < test.length; k++) {
    bin4 = createBinaryString(test[k]);
    //console.log(test[k] + ": " + bin4);
}   
console.timeEnd('test4');

console.time('test5');
for (k = 0; k < test.length; k++) {
	for (i = 0; i < 31; i++) {
		bin5[31-i] = Math.floor(test[k]/Math.pow(2, i))%2;
	}
	//console.log(test[k] + ": " + bin5);
}
console.timeEnd('test5');

console.time('test6');
for (k = 0; k < test.length; k++) {
    for (i = 0; i < 31; i++) {
        bin6[31-i] =(test[k]/(Math.pow(2, i))%2)>=1?1:0; 
    }
    //console.log(test[k] + ": " + bin6);
}
console.timeEnd('test6');

console.time('test7');
for (k = 0; k < test.length; k++) {
	for (i = 0; i < 31; i++) {
		bin7[i] = Math.floor(test[k]/Math.pow(2, i))%2;
	}
}
console.timeEnd('test7');

console.time('test8');
for (k = 0; k < test.length; k++) {
    for (i = 0; i < 31; i++) {
        bin8[i] =(test[k]/(Math.pow(2, i))%2)>=1?1:0; 
    }
}
console.timeEnd('test8');

function createBinaryString (nMask) {
  // nMask must be between -2147483648 and 2147483647
	for (var nFlag = 0, nShifted = nMask, sMask = ""; nFlag < 32;
		nFlag++, sMask += String(nShifted >>> 31), nShifted <<= 1);
	return sMask;
}

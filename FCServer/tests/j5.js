// Forma 1: Math.floor(key/Math.pow(2, i))%2
// Forma 2: (key/(Math.pow(2, i))%2)>=1?1:0

var test = [5, 28394, 48385958, 24323, 32423423, 43423, 2147483647, 0, 43242342, 42343435, 532543653, 435345345, 234234332, 324234243, 54353543, 45353434, 534543, 534534543, 4354534, 543543, 192934, 93294, 4239432, 3243, 3, 432423, 435465, 12132]
var bin5 = []

console.time('test5');
for (k = 0; k < test.length; k++) {
	for (i = 0; i < 31; i++) {
		bin5[31-i] = Math.floor(test[k]/Math.pow(2, i))%2;
	}
	//console.log(test[k] + ": " + bin5);
}
console.timeEnd('test5');

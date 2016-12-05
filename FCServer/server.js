//var net=require('net');
var dgram = require('dgram');
var robot=require('robotjs');
var exec=require('child_process').exec;

var port = 9000;
var states = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
var new_states = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
var changes = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
var bit = {select:0, start:1, up:2, right:3, down:4, left:5, l2:6, r2:7, l1:8, r1: 9, triangle:10, circle:11, x: 12, square:13};
//var commands = {select:"space", start:"enter", up:"up", right:"right", down:"down", left:"left", l2:null, r2:null, l1:null, r1: null, triangle:"y", circle:"backspace", x: "Z", square:"X"};
var commands_sets = {epsxe : {select:"c", start:"j", up:"up", right:"right", down:"down", left:"left", l2:"e", r2:"p", l1:"w", r1: "l", triangle:"y", circle:"backspace", x: "x", square:"z"},
                	 kodi  : {select:"c", start:"enter", up:"up", right:"right", down:"down", left:"left", l2:"audio_vol_down", r2:"pagedown", l1:"audio_vol_up", r1: "pageup", triangle:"escape", circle:"backspace", x: "space", square:null}};

// var server = net.createServer(finc
net.createServer(function (socket) {
	socket.on('data', function(data) {
		var set_key = data.toString().slice(0, -1).split(":");
		var set = set_key[0];
		var key = parseInt(set_key[1]);
		var commands;
		switch (set) {
			case "epsxe":
				commands = commands_sets.epsxe;
				break;
			case "kodi":
				commands = commands_sets.kodi;
				break;
			default:
				commands = commands_sets.kodi;

		}
		//console.log("key: '" + key + "', set: '" + set);
		//console.log("states: " + states);
		//console.log("new_states: " + new_states);
		//console.log("changes: " + changes);*/
		for (i = 0; i < states.length; i++) {
			new_states[i] = Math.floor(key/Math.pow(2, i))%2;
			//console.log("i: " + i + ", new_states[i]: " + new_states[i]);
			changes[i] = new_states[i]-states[i];
			states[i] = new_states[i];
		}
		//console.log("states (updated): " + states);
		//console.log("new_states (updated): " + new_states);
		//console.log("changes (updated): " + changes);
		
		try {
			if (changes[bit.select] == 1) {
				robot.keyToggle(commands.select, "down");
			} else if (changes[bit.select] == -1) {
				robot.keyToggle(commands.select, "up");
			}
			if (changes[bit.start] == 1) {
				robot.keyToggle(commands.start, "down");
			} else if (changes[bit.start] == -1) {
				robot.keyToggle(commands.start, "up");
			}
			if (changes[bit.up] == 1) {
				robot.keyToggle(commands.up, "down");
			} else if (changes[bit.up] == -1) {
				robot.keyToggle(commands.up, "up");
			}
			if (changes[bit.right] == 1) {
				robot.keyToggle(commands.right, "down");
			} else if (changes[bit.right] == -1) {
				robot.keyToggle(commands.right, "up");
			}
			if (changes[bit.down] == 1) {
				robot.keyToggle(commands.down, "down");
			} else if (changes[bit.down] == -1) {
				robot.keyToggle(commands.down, "up");
			}
			if (changes[bit.left] == 1) {
				robot.keyToggle(commands.left, "down");
			} else if (changes[bit.left] == -1) {
				robot.keyToggle(commands.left, "up");
			}
			if (changes[bit.l2] == 1) {
				robot.keyToggle(commands.l2, "down");
			} else if (changes[bit.l2] == -1) {
				robot.keyToggle(commands.l2, "up");
			}
			if (changes[bit.r2] == 1) {
				robot.keyToggle(commands.r2, "down");
			} else if (changes[bit.r2] == -1) {
				robot.keyToggle(commands.r2, "up");
			}
			if (changes[bit.l1] == 1) {
				robot.keyToggle(commands.l1, "down");
			} else if (changes[bit.l1] == -1) {
				robot.keyToggle(commands.l1, "up");
			}
			if (changes[bit.r1] == 1) {
				robot.keyToggle(commands.r1, "down");
			} else if (changes[bit.r1] == -1) {
				robot.keyToggle(commands.r1, "up");
			}
			if (changes[bit.triangle] == 1) {
				robot.keyToggle(commands.triangle, "down");
			} else if (changes[bit.triangle] == -1) {
				robot.keyToggle(commands.triangle, "up");
			}
			if (changes[bit.circle] == 1) {
				robot.keyToggle(commands.circle, "down");
			} else if (changes[bit.circle] == -1) {
				robot.keyToggle(commands.circle, "up");
			}
			if (changes[bit.x] == 1) {
				robot.keyToggle(commands.x, "down");
			} else if (changes[bit.x] == -1) {
				robot.keyToggle(commands.x, "up");
			}
			if (changes[bit.square] == 1) {
				robot.keyToggle(commands.square, "down");
			} else if (changes[bit.square] == -1) {
				robot.keyToggle(commands.square, "up");
			}
		} catch (err) {
			console.log("key: '" + key + "'");
			console.log(err);
			console.log("Tecla no reconocida");
		}
	});
}).listen(port);

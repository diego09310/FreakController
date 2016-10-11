var net=require('net');
var robot=require('robotjs');
var exec=require('child_process').exec;

var port = 9000;
// var server = net.createServer(finc
net.createServer(function (socket) {
	socket.on('data', function(data) {
		 console.log("data: '" + data.toString() + "'");
		var key = data.toString().slice(0, -1);
		if (key == "suspend") {
			exec('sudo systemctl suspend', function (error, stdout, stderr) {
				if (error !== null) {
					console.log('Error while sleeping (sudo?)');
				} else {
					console.log('Sleeping...');
				}
			});
		} else {
			try {
				if (key.charAt(0) == "_") {
					if (key.charAt(key.length-1) == p)
						robot.keyToggle(key.slice(1, -1), "down");
					else
						robot.keyToggle(key.slice(1, -1), "up");
				} else {
					robot.keyTap(key);
				}
			} catch (err) {
				console.log("key: '" + key + "'");
				console.log("Tecla no reconocida");
			}
		}
	});
}).listen(port);

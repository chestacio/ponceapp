var app = require('express')();
var http = require('http').Server(app)
var io = require('socket.io')(http);
var bodyparser = require('body-parser');
var connection = require('./connection');
var routes = require('./routes');

var users = {};

app.use(bodyparser.urlencoded({extended: true}));
app.use(bodyparser.json());

connection.init();
routes.configure(app);

io.on('connection', function(socket){
	console.log('a user connected: ' + socket.id);

	socket.on('chat message', function(data){
		console.log(data.username + ' (' + data.email + '): ' + data.msg);

		var msg = data.msg.trim();

		// Los chat privados se manejarán mediante un mensaje tipo:
		// data.msg = '/w destino mensaje'
		if (msg.substr(0, 3) == '/w '){
			msg = msg.substr(3);
			var spaceIndex = msg.indexOf(' ');
			var to = msg.substr(0, spaceIndex);
			var msg = msg.substr(spaceIndex + 1).trim();

			if (to in users) {
				data = { username: data.username, msg: msg, email: data.email };
				users[to].emit('chat message', data);
			}
		}

		//io.emit('chat message', data);
	});

	socket.on('new user', function(data, callback){
		if (data in users){
			// holi
		}
		// Si el usuario no está 'logueado'
		else {
			socket.nickname = data;
			users[socket.nickname] = socket;
			updateUsers();	
		}
	});

	socket.on('disconnect', function(data){
		if(!socket.nickname)
			return
		delete users[socket.nickname];
		updateUsers();	
	});

	function updateUsers(){
		io.emit('active_users', Object.keys(users));
		console.log(Object.keys(users));
	}
});

http.listen(3000, function(){
	console.log('listening on *:3000')
});
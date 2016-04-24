var app = require('express')();
var http = require('http').Server(app)
var io = require('socket.io')(http);
var bodyparser = require('body-parser');
var connection = require('./connection');
var routes = require('./routes');

var active_users = [];

app.use(bodyparser.urlencoded({extended: true}));
app.use(bodyparser.json());

connection.init();
routes.configure(app);

io.on('connection', function(socket){
	console.log('a user connected: ' + socket.id);
	socket.on('chat message', function(msg){
		console.log(msg.username + ': ' + msg.msg);
		io.emit('chat message', msg);
	});

	socket.on('new user', function(data, callback){
		// Si el usuario no está 'logueado'
		if (active_users.indexOf(data) == -1){
			socket.nickname = data;
			active_users.push(socket.nickname);
			io.emit('active_users', active_users);	
		}
	});

	socket.on('disconnect', function(data){
		if(!socket.nickname)
			return
		active_users.splice(active_users.indexOf(socket.nickname), 1);
		io.emit('active_users', active_users);	
	});
});

http.listen(3000, function(){
	console.log('listening on *:3000')
});
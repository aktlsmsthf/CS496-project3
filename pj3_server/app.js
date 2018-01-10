var app = require('express')();
var server = require('http').createServer(app);
var io = require('socket.io')(server);
var fs = require('fs');
var mongoose = require('mongoose');

var db =  mongoose.connection;

db.on('error', console.error);
db.once('open', function(){
	console.log("Connected to mongod server");
});

mongoose.connect('mongodb://localhost:27017/user');

var User = require('./models/user');

var router = require('./routes')(app, User);

var chat = io.of('/chat').on('connection', function(socket){
	socket.on('chat message', function(data){
		console.log('message from client: ', data);

		var name = socket.name = data.name;
		var room = socket.room = data.room;

		socket.join(room);

		chat.to(room).emit('chat message', {name:data.name, msg: data.msg});
	});
});

var login = io.of('/login').on('connection', function(socket){
	socket.on('login info', function(data){
		console.log('login');

		var userid = data.userid;
		var password = data.password;
		var exist = false;

		/*User.find({userid: userid}, function(err, user){
			if(user){
				console.log("no such user");
				if(user.password=password){
					exist = true;	
				}	
			}
			if(exist)*/
				login.emit('login result', {result:1});
			/*else
				login.emit('login result', {result:0});
		});*/
	});
});

var dir = io.of('/move').on('connection', function(socket){
	socket.on('direction', function(data){
		var direction = data.direction;
		console.log(direction);
	});
});

var appsocket = io.of('/appsocket').on('connection', function(asocket){
	console.log('appsocket');
	var websocket = io.of('/websocket').on('connection', function(wsocket){
		console.log('websocket');
		wsocket.on('direction', function(data){
			var direction = data.direction;
			console.log(direction);
			asocket.emit('direction', data);
		});
	});
});


server.listen(3000, function(){
	console.log('Socket IO server listening on port 3000');
});

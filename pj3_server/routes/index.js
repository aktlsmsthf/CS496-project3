var fs = require('fs');
var mime = require('mime');
module.exports = function(app, User){
	app.get('/', function(req, res){
		console.log('login');
		res.sendFile(__dirname+'/login.html');
	});
	
	app.get('/main', function(req, res){
		console.log('main');
		res.sendFile(__dirname+'/index.html');
	});

	app.get('/chatting', function(req, res){
		console.log('chatting');
		res.sendFile(__dirname+'/chat.html');
	});

	app.get('/cam', function(req, res){
		console.log('cam');
		res.sendFile(__dirname+'/cam.html');
	});

	app.get('/cam/download', function(req, res){
		console.log('cam');
		var file = __dirname+'/image/monitor.zip';
		res.setHeader('Content-disposition', 'attachment; filename=monitor.zip');
		res.setHeader('Content-type', 'application/zip');
		var filestream = fs.createReadStream(file);
		filestream.pipe(res);
	});

	app.get('/login2', function(req, res){
		console.log('login2');
		console.log(req.query);
		var userid = req.query.userid;
		var password = req.query.password;
		findLoginUser(userid, password, res);
	});

	app.get('/imgs', function(req, res){
		console.log('imgs');
		fs.readFile(__dirname+'/../image/img.jpg', function(err, data){
			if(err) console.log('error');
			res.writeHead(200, {'Content-Type' : 'text/html'});
			res.end(data);
		});
	});

	app.get('/register', function(req, res){
		console.log('register');
		var userid = req.query.userid;
		var password = req.query.password;
		addNewUser(userid, password, res);
	});

	app.get('/move', function(req, res){
		console.log('move');
		var direction = req.query.direction;
		console.log(direction);
	});

	function findLoginUser(userid, password, res){
		console.log(userid, password);
		User.findOne({userid:userid}, function(err, user){
			var exist = 0;
			if(user){
				if(user.password==password){
					console.log('find');
					exist = 1;
				}
			}
			res.json({result:exist});
			res.end();
		});
	}

	function addNewUser(userid, password, res){
		console.log(userid, password);
		User.findOne({userid:userid}, function(err, user){
			
			if(!user){
				var usr = new User({userid:userid, password:password});
				usr.save(function(err){
					if(err){
						console.error(err);
						res.json({result:1});
						res.end();
					}
					else{
						res.json({result:0});
						res.end();
					}
				});
			}
			else{
				res.json({result:1});
				res.end();
			}
		});
	}
}

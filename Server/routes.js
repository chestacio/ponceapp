var user = require('./models/user');

module.exports = {
	configure: function(app) {
		app.get('/', function(req, res) {
			res.sendFile(__dirname + '/index.html');
		});
		app.get('/media/:name', function(req, res) {
			res.sendFile(__dirname + '/media/' + req.params.name);
		});
		app.post('/login', function(req, res) {
			user.login(req.body, res);
		});
		app.get('/user/', function(req, res) {
			user.getAll(res);
		});
		app.post('/user/', function(req, res) {
	    	user.create(req.body, res);
	    });
		app.get('/user/:id', function(req, res) {
			user.get(req.params.id, res);
		});
		app.get('/user/:id/friends/', function(req, res) {
			user.getFriends(req.params.id, res);
		});
		app.post('/user/:id/friends/', function(req, res) {
			user.addFriend(req.params.id, req.body, res);
		});
	    app.put('/user/:id/nickname', function(req, res) {
	    	user.updateNickname(req.params.id, req.body, res);
	    });
	    app.put('/user/:id/email', function(req, res) {
	    	user.updateEmail(req.params.id, req.body, res);
	    });
	    app.put('/user/:id/password', function(req, res) {
	    	user.updatePassword(req.params.id, req.body, res);
	    });
	    app.put('/user/:id/image', function(req, res) {
	    	user.updateAvatar(req.params.id, req.body, res);
	    });
	    app.put('/user/:id/subnick', function(req, res) {
	    	user.updateSubnick(req.params.id, req.body, res);
	    });
	    app.put('/user/:id/nickname_style', function(req, res) {
	    	user.updateNicknameStyle(req.params.id, req.body, res);
	    });
	    app.put('/user/:id/msg_style', function(req, res) {
	    	user.updateMsgStyle(req.params.id, req.body, res);
	    });
	    app.put('/user/:id/state', function(req, res) {
	    	user.updateState(req.params.id, req.body, res);
	    });
		
		
	}
};
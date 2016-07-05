var connection = require('../connection');

function User() {

	this.login = function(user, res) {
		connection.acquire(function(err, con) {
			query = 'SELECT u.id, u.nickname, u.subnick, u.email, u.nickname_style, u.msg_style, u.avatar, e.state ';
			query += 'FROM users as u, states as e WHERE u.state=e.id AND u.email=? AND u.password=?';
			con.query(query, [user.email, user.password], function(err, response) {
				con.release();
				if (response.length > 0)
					res.send(response[0]);
				else
					res.send({ status: 1, message: 'Email y/o contraseña inválida'});

			});
		});
	};

	this.get = function(id, res) {
		connection.acquire(function(err, con) {
			query = 'SELECT u.id, u.nickname, u.subnick, u.email, u.nickname_style, u.msg_style, u.avatar, e.state ';
			query += 'FROM users as u, states as e WHERE u.state=e.id AND u.id=?';
			con.query(query, id, function(err, response) {
				con.release();
				res.send(response);
			});
		});
	};

	this.getAll = function(res) {
		connection.acquire(function(err, con) {
			query = 'SELECT u.id, u.nickname, u.subnick, u.email, u.nickname_style, u.msg_style, u.avatar, e.state ';
			query += 'FROM users as u, states as e WHERE u.state=e.id';
			con.query(query, function(err, response) {
				con.release();
				res.send(response);
			});
		});
	};

	this.create = function(user, res) {
		console.log(user);
		connection.acquire(function(err, con) {
			query = 'INSERT INTO users (id, nickname, email, password, avatar, subnick, nickname_style, state, created_at) ';
			query += 'VALUES (NULL, ?, ?, ?, ?, NULL, NULL, "1", CURRENT_TIMESTAMP);'
			con.query(query, [user.nickname, user.email, user.password, user.avatar], function(err, response) {
				con.release();
				if (err)
					res.send({ status: 1, message: 'Error al ingresar usuario'});
				else	
					res.send({ status: 0, message: 'Usuario ingresado con éxito'});	
			});
		});
	};

	this.addFriend = function(id, data, res) {
		console.log(data);
		connection.acquire(function(err, con) {
			query = 'INSERT INTO friends (user1, user2) ';
			query += 'SELECT id,? FROM users WHERE email = ?;'
			con.query(query, [id, data.email], function(err, response) {
				con.release();
				if (err)
					res.send({ status: 1, message: 'Error al ingresar usuario'});
				else	
					res.send({ status: 0, message: 'Amigo agregado con éxito'});	
			});
		});
	};

	this.getFriends = function(id, res) {
		connection.acquire(function(err, con) {
			query = 'SELECT u.id, u.nickname, u.subnick, u.email, u.nickname_style, u.msg_style, u.avatar, e.state ';
			query += 'FROM friends AS a, users AS u, states AS e ';
			query += 'WHERE ((a.user1=? AND a.user2=u.id) OR (a.user2=? AND a.user1=u.id)) AND u.state=e.id';
			con.query(query, [id, id], function(err, response) {
				con.release();
				res.send(response);
			});
		});
	};	

	this.updateNickname = function(id, user, res) {
		connection.acquire(function(err, con) {
			query = 'UPDATE users SET nickname=? WHERE id=?';
			con.query(query, [user.nickname, id], function(err, response) {
				con.release();
				if (err)
					res.send({ status: 1, message: 'Error al modificar usuario'});
				else	
					res.send({ status: 0, message: 'Usuario modificado con éxito'});	
			});
		});	
	}

	this.updateEmail = function(id, user, res) {
		connection.acquire(function(err, con) {
			query = 'UPDATE users SET email=? WHERE id=?';
			con.query(query, [user.email, id], function(err, response) {
				con.release();
				if (err)
					res.send({ status: 1, message: 'Error al modificar usuario'});
				else	
					res.send({ status: 0, message: 'Usuario modificado con éxito'});	
			});
		});	
	}

	this.updatePassword = function(id, user, res) {
		connection.acquire(function(err, con) {
			query = 'UPDATE users SET password=? WHERE id=?';
			con.query(query, [user.password, id], function(err, response) {
				con.release();
				if (err)
					res.send({ status: 1, message: 'Error al modificar usuario'});
				else	
					res.send({ status: 0, message: 'Usuario modificado con éxito'});	
			});
		});	
	}

	this.updateAvatar = function(id, user, res) {
		connection.acquire(function(err, con) {
			query = 'UPDATE users SET avatar=? WHERE id=?';
			con.query(query, [user.avatar, id], function(err, response) {
				con.release();
				if (err)
					res.send({ status: 1, message: 'Error al modificar usuario'});
				else	
					res.send({ status: 0, message: 'Usuario modificado con éxito'});	
			});
		});	
	}

	this.updateSubnick = function(id, user, res) {
		connection.acquire(function(err, con) {
			query = 'UPDATE users SET subnick=? WHERE id=?';
			con.query(query, [user.subnick, id], function(err, response) {
				con.release();
				if (err)
					res.send({ status: 1, message: 'Error al modificar usuario'});
				else	
					res.send({ status: 0, message: 'Usuario modificado con éxito'});	
			});
		});	
	}

	this.updateNicknameStyle = function(id, user, res) {
		connection.acquire(function(err, con) {
			query = 'UPDATE users SET nickname_style=? WHERE id=?';
			con.query(query, [user.nickname_style, id], function(err, response) {
				con.release();
				if (err)
					res.send({ status: 1, message: 'Error al modificar usuario'});
				else	
					res.send({ status: 0, message: 'Usuario modificado con éxito'});	
			});
		});	
	}

	this.updateMsgStyle = function(id, user, res) {
		connection.acquire(function(err, con) {
			query = 'UPDATE users SET msg_style=? WHERE id=?';
			con.query(query, [user.msg_style, id], function(err, response) {
				con.release();
				if (err)
					res.send({ status: 1, message: 'Error al modificar usuario'});
				else	
					res.send({ status: 0, message: 'Usuario modificado con éxito'});	
			});
		});	
	}

	this.updateState = function(id, user, res) {
		connection.acquire(function(err, con) {
			query = 'UPDATE users INNER JOIN states SET users.state=states.id WHERE users.id=? and states.state=?';
			con.query(query, [id, user.state], function(err, response) {
				con.release();
				if (err)
					res.send({ status: 1, message: 'Error al modificar usuario'});
				else	
					res.send({ status: 0, message: 'Usuario modificado con éxito'});	
			});
		});	
	}

}
module.exports = new User();

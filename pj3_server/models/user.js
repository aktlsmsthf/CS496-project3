var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var UserSchema = new Schema({
	userid: String,
	password: String
});

module.exports = mongoose.model('user', UserSchema);
